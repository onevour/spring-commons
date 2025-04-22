package com.onevour.core.applications.rest.annotations;

import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Put {

    String key() default "";

    String url();

    int connect() default -1;

    int request() default -1;

    int read() default -1;

    boolean throwException() default true;

    String contentType() default MediaType.APPLICATION_JSON_VALUE;

}
