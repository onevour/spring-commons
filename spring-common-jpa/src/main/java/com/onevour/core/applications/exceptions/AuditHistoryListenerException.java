package com.onevour.core.applications.exceptions;

public class AuditHistoryListenerException extends RuntimeException {

    public AuditHistoryListenerException() {
        super("audit history error");
    }

    public AuditHistoryListenerException(String message) {
        super(message);
    }
}
