package org.tlabs.game.goose.exception;

public class DuplicatePlayerException extends Exception {

    public DuplicatePlayerException() {
        super();
    }

    public DuplicatePlayerException(String message) {
        super(message);
    }

    public DuplicatePlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicatePlayerException(Throwable cause) {
        super(cause);
    }

    protected DuplicatePlayerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
