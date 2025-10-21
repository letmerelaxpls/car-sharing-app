package csa.service.payment;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import csa.exception.EntityNotFoundException;
import csa.exception.PaymentProcessException;
import csa.mapper.PaymentMapper;
import csa.model.Payment;
import csa.model.Rental;
import csa.model.enums.PaymentType;
import csa.model.enums.Status;
import csa.repository.PaymentRepository;
import csa.repository.RentalRepository;
import csa.service.payment.calculators.CalculatorFactory;
import csa.service.stripe.StripePaymentService;
import csa.service.telegram.NotificationService;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private final RentalRepository rentalRepository;
    private final CalculatorFactory calculatorFactory;
    private final StripePaymentService stripePaymentService;
    private final PaymentMapper paymentMapper;
    private final PaymentRepository paymentRepository;
    private final NotificationService notificationService;

    @Override
    public Page<PaymentSummaryDto> findByUserId(Long userId, Pageable pageable) {
        return paymentRepository.findByRentalUserId(userId, pageable)
                .map(paymentMapper::toSummaryDto);

    }

    @Override
    public PaymentDetailsDto findById(Long paymentId) {
        return paymentMapper.toDetailsDto(paymentRepository.findById(paymentId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Payment "
                        + "with id: " + paymentId)));
    }

    @Override
    public PaymentResponseDto createSession(Long userId,
                                            PaymentRequestDto requestDto) {

        Rental rental = rentalRepository.findByIdAndUserId(requestDto.getRentalId(), userId)
                .orElseThrow(() -> new EntityNotFoundException("Could not find Rental by id: "
                        + requestDto.getRentalId() + " and User id: " + userId));
        PaymentType paymentType = determineType(rental, requestDto);
        Payment payment = paymentRepository
                .findByRentalIdAndRentalUserId(
                        requestDto.getRentalId(), userId)
                .map(p -> {
                    if (p.getStatus().equals(Status.PAID)) {
                        throw new PaymentProcessException("This Rental is already paid!");
                    }
                    return p;
                })
                .orElseGet(() -> paymentMapper.toModel(requestDto));
        BigDecimal amount = calculatorFactory.getCalculator(paymentType).calculate(rental);
        SessionCreateParams sessionCreateParams = stripePaymentService.createSessionParams(amount);
        Session session = stripePaymentService.makeSession(sessionCreateParams);

        payment.setSessionId(session.getId());
        payment.setSessionUrl(session.getUrl());
        payment.setAmountToPay(amount);
        return paymentMapper.toDto(paymentRepository.save(payment));
    }

    @Override
    public void paymentSuccess(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Payment with session id: "
                        + sessionId));
        if (!stripePaymentService.isSessionPaid(sessionId)) {
            notificationService.sendFailedPaymentNotification(payment);
            throw new PaymentProcessException("Payment for session id: "
                    + sessionId + " is not successful!");
        }
        payment.setStatus(Status.PAID);
        paymentRepository.save(payment);
        notificationService.sendSuccessfulPaymentNotification(payment);
    }

    @Override
    public void paymentCancel(String sessionId) {
        Payment payment = paymentRepository.findBySessionId(sessionId).orElseThrow(() ->
                new EntityNotFoundException("Could not find Payment with session id: "
                        + sessionId));
        if (Status.PENDING.equals(payment.getStatus())) {
            payment.setStatus(Status.CANCELED);
            paymentRepository.save(payment);
            notificationService.sendCanceledPaymentNotification(payment);
        }
    }

    private PaymentType determineType(Rental rental, PaymentRequestDto requestDto) {
        PaymentType paymentType;
        paymentType = rental.getActualReturnDate().isAfter(rental.getReturnDate())
                ? PaymentType.FINE
                : PaymentType.PAYMENT;
        if (!paymentType.equals(requestDto.getType())) {
            throw new PaymentProcessException("Requested PaymentType " + requestDto.getType()
                    + " is invalid! Expected: " + paymentType);
        }
        return paymentType;
    }

}
