package csa.exception;

public class RentalException extends RuntimeException {
    public RentalException(String message) {
        super(message);
    }

    public RentalException(String message, Throwable e) {
        super(message, e);
    }
}
