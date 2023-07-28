package com.onevour.core.applications.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevour.core.applications.commons.ApiRequest;
import com.onevour.core.applications.commons.ValueOf;
import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.session.ClientManifest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.*;

@Slf4j
@SuppressWarnings({"rawtypes"})
public abstract class BaseService {

    protected int SUCCESS = 200;

    protected int SUCCESS_CREATED = 201;

    protected int ERROR_UNAUTHORIZED = 401;

    protected int ERROR_INVALID_PARAM = 400;

    protected int ERROR_FORBIDDEN = 403;

    protected int ERROR_NOT_FOUND = 404;

    protected int ERROR_INTERNAL_SERVER = 500;

    protected int ERROR_BAD_GATEWAY = 502;

    protected String SUCCESS_MESSAGE = "success";

    protected String SUCCESS_CREATED_MESSAGE = "created";

    protected String ERROR_MESSAGE = "error";

    protected Map<Integer, String> message() {
        Map<Integer, String> map = new HashMap<>();
        map.put(400, "Bad request");
        map.put(403, "Access denied");
        map.put(404, "Data not found");
        return map;
    }

    @Autowired
    protected Validator validator;

    @Autowired
    protected RestTemplate restTemplate;

    @Autowired
    protected ObjectMapper mapper;

    @Autowired
    protected BaseSpec baseSpec;

    protected ServiceResolver success() {
        return success(SUCCESS, SUCCESS_MESSAGE);
    }

    protected ServiceResolver created() {
        return success(SUCCESS_CREATED, SUCCESS_CREATED_MESSAGE);
    }

    protected ServiceResolver success(String message) {
        return success(SUCCESS, message);
    }

    protected <T> ServiceResolver<T> success(T result) {
        return success(SUCCESS, SUCCESS_MESSAGE, result);
    }

    protected <T> ServiceResolver success(int code, String message) {
        return new ServiceResolver(code, message);
    }

    protected <T> ServiceResolver<T> success(int code, String message, T result) {
        return new ServiceResolver<T>(code, message, result);
    }

    /**
     * response with value native<br/>
     * String<br/>
     * int or Integer<br/>
     * long or Long<br/>
     * ect native variable java
     */
    protected <T> ServiceResolver<T> successNative(T result) {
        return new ServiceResolver<T>(SUCCESS, SUCCESS_MESSAGE, result);
    }

    protected ServiceResolver error() {
        return error(ERROR_INTERNAL_SERVER, ERROR_MESSAGE);
    }

    protected ServiceResolver error(String message) {
        return error(ERROR_INTERNAL_SERVER, message);
    }

    protected ServiceResolver error(int code) {
        return error(code, Optional.of(message().get(code)).orElse(ERROR_MESSAGE));
    }

    protected <T> ServiceResolver<T> error(int code, String message) {
        return new ServiceResolver<T>(code, message);
    }

    protected <T> ServiceResolver<T> error(int code, String message, T result) {
        return new ServiceResolver<T>(code, message, result);
    }

    protected <T extends BaseEntity> T sig(T value) {
        return sig(value, null);
    }

    protected <T extends BaseEntity> T sig(T value, String user) {
        if (Objects.isNull(value)) return value;
        if (ValueOf.isNull(value.getCreatedDate())) {
            value.setCreatedDate(new Date());
        } else value.setModifiedDate(new Date());
        if (ValueOf.nonNull(user)) {
            if (ValueOf.isNull(value.getCreatedBy())) {
                value.setCreatedBy(user);
            }
            value.setModifiedBy(user);
        }
        return value;
    }

    protected void errorData(String message) {
        throw new DataIntegrityViolationException(message);
    }

    protected <T extends BaseEntity> void wrap(T value, String username) {
        if (Objects.isNull(value.getCreatedDate())) {
            value.setCreatedDate(new Date());
            value.setModifiedDate(new Date());
            value.setCreatedBy(username);
            value.setModifiedBy(username);
        } else {
            value.setModifiedDate(new Date());
            value.setModifiedBy(username);
        }
    }

    protected <T extends BaseEntity> T sign(T value, ClientManifest sign) {
        return sign(value, sign.getUsername());
    }

    protected <T extends BaseEntity> T sign(T value) {
        return sign(value, "system");
    }

    protected <T extends BaseEntity> T sign(T value, String username) {
        if (Objects.isNull(value.getCreatedDate())) {
            value.setCreatedDate(new Date());
            value.setModifiedDate(new Date());
            value.setCreatedBy(username);
            value.setModifiedBy(username);
        } else {
            value.setModifiedDate(new Date());
            value.setModifiedBy(username);
        }
        return value;
    }

    protected <T> T requestData(ApiRequest request) {
        try {
            log.debug("http request {} {}", request.getMethod(), request.getUrl());
            request.validate();
            ResponseEntity<T> response = null;
            if (HttpMethod.GET == request.getMethod()) {
                if (Objects.isNull(request.getClassResponse())) {
                    response = restTemplate.exchange(request.getUrl(), HttpMethod.GET, new HttpEntity<>(request.getHeaders()), request.getParameterizedResponse());
                }
                if (Objects.isNull(request.getParameterizedResponse())) {
                    response = restTemplate.exchange(request.getUrl(), HttpMethod.GET, new HttpEntity<>(request.getHeaders()), request.getClassResponse());
                }
            } else {
                if (Objects.isNull(request.getClassResponse())) {
                    response = restTemplate.exchange(request.getUrl(), request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), request.getParameterizedResponse());
                }
                if (Objects.isNull(request.getParameterizedResponse())) {
                    response = restTemplate.exchange(request.getUrl(), request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), request.getClassResponse());
                }
            }
            if (Objects.isNull(response)) return null;
            return response.getBody();
        } catch (HttpStatusCodeException e) {
            log.error("error response http {}", request.getUrl());
            log.error("error response http code", e);
            int code = e.getStatusCode().value();
            if (request.getAllowForCode().contains(code)) {
                log.error("allow for code {}", code);
                return null;
            }
            //log.error("error response http code {}", code);
            throw new BadGatewayException();
        } catch (ResourceAccessException e) {
            log.error("error access external", e);
            throw new BadGatewayException();
        }
    }

    protected <T> void validateValue(T request) {
        Set<ConstraintViolation<T>> oks = validator.validate(request);
        if (oks.isEmpty()) return;
        throw new ConstraintViolationException(oks);
    }

    protected String param(String value) {
        if (Objects.isNull(value)) return "";
        return value;
    }

    protected String string(Object... parts) {
        StringBuilder sb = new StringBuilder();
        for (Object part : parts) {
            sb.append(part);
        }
        return sb.toString();
    }

}
