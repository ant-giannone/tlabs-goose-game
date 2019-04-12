package org.tlabs.game.goose.exception;

public class PlayerValidationRuntimeException extends RuntimeException {

    public PlayerValidationRuntimeException() {
        super();
    }

    public PlayerValidationRuntimeException(String message) {
        super(message);
    }

    public PlayerValidationRuntimeException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerValidationRuntimeException(Throwable cause) {
        super(cause);
    }

    protected PlayerValidationRuntimeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
