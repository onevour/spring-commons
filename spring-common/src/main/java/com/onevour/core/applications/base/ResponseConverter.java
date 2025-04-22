package com.onevour.core.applications.base;

/**
 * convert object from E (source) to T (result)
 * @param <E> source
 * @param <T> result
 */
public abstract class ResponseConverter<E, T> {
    public abstract T convert(E param);

}
