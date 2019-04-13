package org.tlabs.game.goose.exception;

public class UnknownStrategyException extends Exception {

    public UnknownStrategyException() {
        super();
    }

    public UnknownStrategyException(String message) {
        super(message);
    }

    public UnknownStrategyException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnknownStrategyException(Throwable cause) {
        super(cause);
    }

    protected UnknownStrategyException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
