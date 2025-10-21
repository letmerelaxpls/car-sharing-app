package csa.util;

import static csa.util.RentalTestUtil.createRental;

import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import csa.dto.payment.PaymentDetailsDto;
import csa.dto.payment.PaymentRequestDto;
import csa.dto.payment.PaymentResponseDto;
import csa.dto.payment.PaymentSummaryDto;
import csa.model.Payment;
import csa.model.enums.PaymentType;
import csa.model.enums.Status;
import java.math.BigDecimal;

public class PaymentTestUtil {
    private static final String CURRENCY = "usd";
    private static final String PRODUCT_NAME = "Car Rental";
    private static final String SESSION_ID = "session_id";
    private static final String SESSION_URL = "sessionurl.com";

    public static Payment createPayment() {
        Payment payment = new Payment();
        payment.setId(1L);
        payment.setStatus(Status.PENDING);
        payment.setType(PaymentType.PAYMENT);
        payment.setRental(createRental());
        payment.setSessionUrl(SESSION_URL);
        payment.setSessionId(SESSION_ID);
        payment.setAmountToPay(BigDecimal.valueOf(3100));
        return payment;
    }

    public static PaymentSummaryDto createPaymentSummaryDto() {
        PaymentSummaryDto summaryDto = new PaymentSummaryDto();
        summaryDto.setId(1L);
        summaryDto.setType(PaymentType.PAYMENT);
        summaryDto.setStatus(Status.PENDING);
        summaryDto.setAmountToPay(BigDecimal.valueOf(3100));
        return summaryDto;
    }

    public static PaymentDetailsDto createPaymentDetailsDto() {
        PaymentDetailsDto detailsDto = new PaymentDetailsDto();
        detailsDto.setId(1L);
        detailsDto.setType(PaymentType.PAYMENT);
        detailsDto.setStatus(Status.PENDING);
        detailsDto.setSessionUrl(SESSION_URL);
        detailsDto.setSessionId(SESSION_ID);
        detailsDto.setRentalId(1L);
        detailsDto.setAmountToPay(BigDecimal.valueOf(3100));
        return detailsDto;
    }

    public static PaymentRequestDto createPaymentRequestDto() {
        PaymentRequestDto requestDto = new PaymentRequestDto();
        requestDto.setType(PaymentType.PAYMENT);
        requestDto.setRentalId(1L);
        return requestDto;
    }

    public static PaymentResponseDto createPaymentResponseDto() {
        PaymentResponseDto responseDto = new PaymentResponseDto();
        responseDto.setSessionUrl(SESSION_URL);
        responseDto.setSessionId(SESSION_ID);
        return responseDto;
    }

    public static SessionCreateParams createSessionParams(BigDecimal amount) {
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .addLineItem(
                        SessionCreateParams.LineItem.builder()
                                .setQuantity(1L)
                                .setPriceData(
                                        SessionCreateParams.LineItem.PriceData.builder()
                                                .setCurrency(CURRENCY)
                                                .setUnitAmount(amount.multiply(
                                                        BigDecimal.valueOf(100)).longValue())
                                                .setProductData(
                                                        SessionCreateParams.LineItem.PriceData
                                                                .ProductData.builder()
                                                                .setName(PRODUCT_NAME)
                                                                .build()
                                                )
                                                .build()
                                )
                                .build()
                )
                .build();
    }

    public static Session createStripeSession() {
        Session session = new Session();
        session.setId(SESSION_ID);
        session.setUrl(SESSION_URL);
        return session;
    }
}
