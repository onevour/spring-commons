package com.onevour.core.applications.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevour.core.applications.commons.ValueOf;
import com.onevour.core.applications.session.ClientManifest;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import java.util.Objects;
import java.util.Set;

@Slf4j
@SuppressWarnings({"unchecked", "rawtypes", "unused"})
public class BaseRestController {

    private static final int SUCCESS_CODE = 200;

    private static final int ERROR_CODE = 500;

    private static final String SUCCESS = "success";

    private static final String ERROR = "error";

    private static final String ERROR_MANDATORY_FIELD = "mandatory field not complete";

    protected final static String view = "view";

    protected final static String update = "update";

    protected final static String delete = "delete";

    protected final static String process = "process";

    protected final static String create = "create";

    @Autowired
    protected Validator validator;

    @Autowired
    protected ObjectMapper mapper;


    protected boolean isNull(Object... values) {
        return ValueOf.isNull(values);
    }

    protected <T> void validateValue(T request) {
        Set<ConstraintViolation<T>> oks = validator.validate(request);
        if (oks.isEmpty()) return;
        throw new ConstraintViolationException(oks);
    }

    protected ResponseEntity response() {
        Response response = new Response();
        response.setCode(SUCCESS_CODE);
        response.setMessage(SUCCESS);
        return ResponseEntity.ok(response);
    }

    protected ResponseEntity responseMessage(String message) {
        Response response = new Response();
        response.setCode(SUCCESS_CODE);
        response.setMessage(message);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity response(ServiceResolver<T> resolver) {
        if (resolver.isError()) return responseError(resolver);
        return responseSuccess(resolver.getCode(), resolver.getMessage(), resolver.getResult());
    }

    protected <T> ResponseEntity response(ServiceResolver<T> resolver, ClientManifest manifest, String action, String description) {
        return response(resolver);
    }

    protected <T> ResponseEntity responseEmptyResult(ServiceResolver<T> resolver, ClientManifest sig, String action, Object... description) {
        return responseEmptyResult(resolver);
    }

    protected <T> ResponseEntity responseEmptyResult(ServiceResolver<T> resolver) {
        if (resolver.isError()) return responseError(resolver);
        return responseSuccess(resolver.getCode(), resolver.getMessage(), null);
    }

    protected <T, E extends ResponseConverter> ResponseEntity response(ServiceResolver<T> resolver, Class<E> converterClass) {
        if (resolver.isError()) return responseError(resolver);
        if (Objects.isNull(resolver.getResult())) return response(resolver.getResult());
        E converter = BeanUtils.instantiateClass(converterClass);
        return response(converter.convert(resolver.getResult()));
    }

    protected <T, E extends ResponseConverter> ResponseEntity response(ServiceResolver<T> resolver, E converter) {
        if (resolver.isError()) {
            return responseError(resolver);
        }
        if (Objects.isNull(resolver.getResult())) {
            return response(resolver.getResult());
        }
        return response(converter.convert(resolver.getResult()));
    }

    protected <T, E extends ResponseConverter> ResponseEntity response(ServiceResolver<T> resolver, E converter, ClientManifest manifest, String action, Object... description) {
        return response(resolver, converter);
    }

    protected <T> ResponseEntity response(T value) {
        Response<T> response = new Response<>();
        response.setCode(SUCCESS_CODE);
        response.setMessage(SUCCESS);
        response.setResult(value);
        return ResponseEntity.ok(response);
    }

    protected <T> ResponseEntity responseSuccess(int code, String message, T payload) {
        if (code < 200 || code >= 300) {
            return responseError(code, message, payload);
        }
        Response<T> response = new Response<>();
        response.setCode(code);
        response.setMessage(message);
        response.setResult(payload);
        return ResponseEntity.status(code).body(response);
    }

    protected <T> ResponseEntity responseError(ServiceResolver<T> resolver) {
        return responseError(resolver.getCode(), resolver.getMessage(), resolver.getResult());
    }

    protected <T> ResponseEntity responseError(int code, String message, T payload) {
        Response<T> response = new Response<>();
        // override status code if 200
        if (code == SUCCESS_CODE) {
            code = ERROR_CODE;
        }
        // override message if 200
        if (SUCCESS.equalsIgnoreCase(message)) {
            message = ERROR;
        }
        log.warn("response error client {}, {}", code, message);
        response.setCode(code);
        response.setMessage(message);
        response.setResult(payload);
        return ResponseEntity.status(code).body(response);
    }

    protected String string(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (Object part : parts) {
            sb.append(part);
            sb.append(" ");
        }
        return StringUtils.normalizeSpace(sb.toString().trim());
    }

}
