package com.emmydev.ecommerce.client.exception;

public class ComputationErrorException extends Exception{
    public ComputationErrorException() {
        super();
    }

    public ComputationErrorException(String message) {
        super(message);
    }

    public ComputationErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public ComputationErrorException(Throwable cause) {
        super(cause);
    }

    protected ComputationErrorException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
