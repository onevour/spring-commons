package com.onevour.core.applications.base;

public abstract class ResponseConverter<E, T> {
    protected abstract T convert(E param);

}
