package com.onevour.core.applications.rest.exceptions;

public class HttpMethodNotFoundException extends RuntimeException {

    public HttpMethodNotFoundException(String message) {
        super(message);
    }

    public HttpMethodNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
