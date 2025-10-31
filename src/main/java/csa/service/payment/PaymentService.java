package csa.service.payment;

import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentService {
    Page<PaymentSummaryDto> findByUserId(Long userId, Pageable pageable);

    PaymentDetailsDto findById(Long paymentId);

    PaymentResponseDto createSession(Long userId,
                                     PaymentRequestDto requestDto);

    void paymentSuccess(String sessionId);

    void paymentCancel(String sessionId);
}
