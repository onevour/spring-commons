package com.onevour.core.applications.rest.exceptions;

/**
 * Nilai {@code key}/{@code url} anotasi RestRepository tidak bisa di-resolve:
 * properti {@code ${...}} tidak ada, kunci database {@code D{...}} tidak ada atau kosong,
 * atau bean {@code RestConfigBaseKey} tidak tersedia.
 */
public class RestConfigNotFoundException extends RuntimeException {

    public RestConfigNotFoundException(String message) {
        super(message);
    }

    public RestConfigNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
