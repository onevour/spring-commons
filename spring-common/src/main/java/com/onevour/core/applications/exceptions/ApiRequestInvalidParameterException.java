package com.onevour.core.applications.exceptions;

public class ApiRequestInvalidParameterException extends RuntimeException {

    public ApiRequestInvalidParameterException() {
        super("Invalid setup configuration");
    }

    public ApiRequestInvalidParameterException(String message) {
        super(message);
    }

    public ApiRequestInvalidParameterException(IllegalAccessException e) {
        super("Invalid setup configuration");
        addSuppressed(e);
    }
}
