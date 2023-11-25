package com.onevour.core.applications.rest.annotations;

import org.springframework.http.MediaType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Delete {

    String key() default "";

    String url();

    String contentType() default MediaType.APPLICATION_JSON_VALUE;

}
