package com.onevour.core.applications.exceptions;

public class BadGatewayException extends RuntimeException {

    public BadGatewayException() {
        super("bad gateway");
    }

    public BadGatewayException(String message) {
        super(message);
    }
}
