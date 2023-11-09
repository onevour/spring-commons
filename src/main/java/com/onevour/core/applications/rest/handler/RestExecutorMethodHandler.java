package com.onevour.core.applications.rest.handler;

import com.onevour.core.applications.base.Response;
import com.onevour.core.applications.commons.ApiRequest;
import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.rest.annotations.*;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class RestExecutorMethodHandler implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(RestExecutorMethodHandler.class);

    public RestExecutorMethodHandler() {

    }

    @Nullable
    @Override
    public Object invoke(@Nonnull MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();

//        RestConfig config = invocation.getClass().getAnnotation(RestConfig.class);
//        log.info("method interceptor {} class {} | {}", method.getName(), config, invocation.getClass());
//        if(invocation.getThis().getClass().getAnnotation(RestConfig.class)) {
//            throw new BaseException("...");
//        }
        Get get = getTransactionalMethodGet(method).orElse(null);
        if (Objects.nonNull(get)) {
            return handleTransactionalMethod(get, method, invocation.getArguments());
        }
        Post post = getTransactionalMethodPost(method).orElse(null);
        if (Objects.nonNull(post)) {
            return handleTransactionalMethod(post, method, invocation.getArguments());
        }
        Patch patch = getTransactionalMethodPatch(method).orElse(null);
        if (Objects.nonNull(patch)) {
            return handleTransactionalMethod(patch, method, invocation.getArguments());
        }
        Put put = getTransactionalMethodPut(method).orElse(null);
        if (Objects.nonNull(put)) {
            return handleTransactionalMethod(put, method, invocation.getArguments());
        }
        Delete delete = getTransactionalMethodDelete(method).orElse(null);
        if (Objects.nonNull(delete)) {
            return handleTransactionalMethod(delete, method, invocation.getArguments());
        }
        Response response = new Response();
        response.setCode(200);
        response.setMessage("success");
        return response;
    }

    private Optional<Put> getTransactionalMethodPut(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Put.class));
    }

    private Optional<Delete> getTransactionalMethodDelete(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Delete.class));
    }

    private Optional<Patch> getTransactionalMethodPatch(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Patch.class));
    }

    private Optional<Get> getTransactionalMethodGet(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Get.class));
    }

    private Optional<Post> getTransactionalMethodPost(Method method) {
        return Optional.ofNullable(method.getDeclaredAnnotation(Post.class));
    }

    ExecutorService executor = Executors.newFixedThreadPool(300);

    private Object handleTransactionalMethod(Get annotation, Method method, Object[] args) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method.getReturnType());
        builder.setUrl(annotation.url());
        for (Object param : args) {
            builder.setRequest(param);
        }
        if(annotation.sync()){
            executor.submit(() -> {
                requestData(builder.buildGet());
            });
        } else  {
            return requestData(builder.buildGet());
        }
        return null;

    }

    private Object handleTransactionalMethod(Patch annotation, Method method, Object[] args) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method.getReturnType());
        builder.setUrl(annotation.url());
        for (Object param : args) {
            builder.setRequest(param);
        }
        return requestData(builder.buildGet());
    }

    private Object handleTransactionalMethod(Put annotation, Method method, Object[] args) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method.getReturnType());
        builder.setUrl(annotation.url());
        for (Object param : args) {
            builder.setRequest(param);
        }
        return requestData(builder.buildGet());
    }

    private Object handleTransactionalMethod(Delete annotation, Method method, Object[] args) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method.getReturnType());
        builder.setUrl(annotation.url());
        for (Object param : args) {
            builder.setRequest(param);
        }
        return requestData(builder.buildGet());
    }

    private Object handleTransactionalMethod(Post annotation, Method method, Object[] args) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method.getReturnType());
        builder.setUrl(annotation.url());
        for (Object param : args) {
            builder.setRequest(param);
        }
        return requestData(builder.buildPost());
    }

    protected <T> T requestData(ApiRequest request) {
        try {
            RestTemplate restTemplate = new RestTemplate();
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


}
