package csa.exception;

public class PaymentProcessException extends RuntimeException {
    public PaymentProcessException(String message) {
        super(message);
    }

    public PaymentProcessException(String message, Throwable e) {
        super(message, e);
    }
}
