package org.tlabs.game.goose.exception;

public class UnknownRequestFormatException extends Exception {

    public UnknownRequestFormatException() {
        super();
    }

    public UnknownRequestFormatException(String message) {
        super(message);
    }

    public UnknownRequestFormatException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownRequestFormatException(Throwable cause) {
        super(cause);
    }

    protected UnknownRequestFormatException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
