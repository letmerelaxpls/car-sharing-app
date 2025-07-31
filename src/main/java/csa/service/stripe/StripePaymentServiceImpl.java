package csa.service.stripe;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import csa.exception.StripeSessionException;
import java.math.BigDecimal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class StripePaymentServiceImpl implements StripePaymentService {
    private static final String CURRENCY = "usd";
    private static final String PRODUCT_NAME = "Car Rental";
    private static final String STATUS_PAID = "paid";
    @Value("${stripe.secret.key}")
    private String stripeSecretKey;
    @Value("${payment.success.url}")
    private String paymentSuccessUrl;
    @Value("${payment.cancel.url}")
    private String paymentCancelUrl;

    public SessionCreateParams createSessionParams(BigDecimal amount) {
        Stripe.apiKey = stripeSecretKey;
        return SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setSuccessUrl(paymentSuccessUrl)
                .setCancelUrl(paymentCancelUrl)
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

    @Override
    public Session makeSession(SessionCreateParams params) {
        Session session = null;
        try {
            session = Session.create(params);
        } catch (StripeException e) {
            throw new StripeSessionException("Could not create Stripe session", e);
        }
        return session;
    }

    @Override
    public boolean isSessionPaid(String sessionId) {
        try {
            Session session = Session.retrieve(sessionId);
            return STATUS_PAID.equals(session.getPaymentStatus());
        } catch (StripeException e) {
            throw new StripeSessionException("Could not check if session with id: "
                    + sessionId + " is paid");
        }
    }
}
