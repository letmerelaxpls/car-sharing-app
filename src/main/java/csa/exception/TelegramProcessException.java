package csa.exception;

public class TelegramProcessException extends RuntimeException {
    public TelegramProcessException(String message) {
        super(message);
    }

    public TelegramProcessException(String message, Throwable e) {
        super(message, e);
    }
}
