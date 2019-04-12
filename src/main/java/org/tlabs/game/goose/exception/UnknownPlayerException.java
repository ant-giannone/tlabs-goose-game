package org.tlabs.game.goose.exception;

public class UnknownPlayerException extends Exception {

    public UnknownPlayerException() {
        super();
    }

    public UnknownPlayerException(String message) {
        super(message);
    }

    public UnknownPlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownPlayerException(Throwable cause) {
        super(cause);
    }

    protected UnknownPlayerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
