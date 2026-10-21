package com.onevour.core.applications.rest.handler;


import com.onevour.core.applications.commons.ApiRequest;
import com.onevour.core.applications.commons.ReflectionCommons;
import com.onevour.core.applications.exceptions.ApiRequestInvalidParameterException;
import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.rest.annotations.*;
import com.onevour.core.applications.rest.configuration.RestConfigValueResolver;
import com.onevour.core.applications.rest.exceptions.HttpMethodNotFoundException;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class RestExecutorMethodHandler implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(RestExecutorMethodHandler.class);

    private final ConfigurableListableBeanFactory beanFactory;

    private final RestConfigValueResolver resolver;

    /**
     * RestTemplate per kombinasi timeout (connect/request/read). Dibuat sekali lalu dipakai ulang,
     * tidak lagi didaftarkan sebagai bean (bean "customRequestFactory" yang namanya tetap memicu
     * BeanDefinitionOverrideException di Boot 2 saat ada kombinasi timeout kedua).
     */
    private final Map<String, RestTemplate> restTemplateCache = new ConcurrentHashMap<>();

    public RestExecutorMethodHandler(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
        this.resolver = new RestConfigValueResolver(beanFactory);
    }

    /**
     * - filter method<br/>
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        Method method = invocation.getMethod();
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
        throw new HttpMethodNotFoundException("Http method not found");
    }

    // --------------------------------------------------------------------------------------------------

    // CHECK METHOD annotation

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

    // END CHECK METHOD annotation

    // --------------------------------------------------------------------------------------------------

    // HANDLE METHOD by annotation

    /**
     * - handle method GET<br/>
     */
    @SuppressWarnings("unchecked")
    private Object handleTransactionalMethod(Get annotation, Method method, Object[] args) {
        String url = baseURL(annotation.key(), annotation.url());
        String contentType = annotation.contentType();
        ApiRequest.Builder builder = builderHttpMethod(method, args, url, contentType);
        return requestData(method, builder.buildGet(), annotation.connect(), annotation.request(), annotation.read(), annotation.throwException());
    }

    private Object handleTransactionalMethod(Patch annotation, Method method, Object[] args) {
        String url = baseURL(annotation.key(), annotation.url());
        String contentType = annotation.contentType();
        ApiRequest.Builder builder = builderHttpMethod(method, args, url, contentType);
        return requestData(method, builder.buildPatch(), annotation.connect(), annotation.request(), annotation.read(), annotation.throwException());
    }

    private Object handleTransactionalMethod(Put annotation, Method method, Object[] args) {
        String url = baseURL(annotation.key(), annotation.url());
        String contentType = annotation.contentType();
        ApiRequest.Builder builder = builderHttpMethod(method, args, url, contentType);
        return requestData(method, builder.buildPut(), annotation.connect(), annotation.request(), annotation.read(), annotation.throwException());
    }

    private Object handleTransactionalMethod(Delete annotation, Method method, Object[] args) {
        String url = baseURL(annotation.key(), annotation.url());
        String contentType = annotation.contentType();
        ApiRequest.Builder builder = builderHttpMethod(method, args, url, contentType);
        return requestData(method, builder.buildDelete(), annotation.connect(), annotation.request(), annotation.read(), annotation.throwException());
    }

    private Object handleTransactionalMethod(Post annotation, Method method, Object[] args) {
        String url = baseURL(annotation.key(), annotation.url());
        String contentType = annotation.contentType();
        ApiRequest.Builder builder = builderHttpMethod(method, args, url, contentType);
        return requestData(method, builder.buildPost(), annotation.connect(), annotation.request(), annotation.read(), annotation.throwException());
    }

    // END HANDLE METHOD by annotation

    // --------------------------------------------------------------------------------------------------

    // UTIL METHOD

    private ApiRequest.Builder builderHttpMethod(Method method, Object[] args, String url, String contentType) {
        ApiRequest.Builder builder = new ApiRequest.Builder(method);
        prepareBuilder(builder, method, url, args);
        builder.header(HttpHeaders.CONTENT_TYPE, contentType);
        return builder;
    }

    /**
     * base url = key + url, di-resolve oleh {@link RestConfigValueResolver} (aturan yang sama
     * dipakai {@code RestRepositoryConfigValidator} saat startup).
     */
    private String baseURL(String annotationKey, String annotationURL) {
        return resolver.baseUrl(annotationKey, annotationURL);
    }

    public boolean isValidUrl(String url) {
        if (url == null || url.isBlank()) {
            return false;
        }
        try {
            URI uri = new URI(url);
            return uri.getScheme() != null && uri.getHost() != null;
        } catch (URISyntaxException e) {
            return false;
        }
    }


    /**
     * check header <br/>
     * check path variable value <br/>
     * check body request value
     */
    private void prepareBuilder(ApiRequest.Builder builder, Method method, String url, Object[] args) {
        int countParam = method.getParameterCount();
        int countPathVariable = StringUtils.countMatches(url, '{');
        log.debug("count param {} count path variable {}", countParam, countPathVariable);
        Annotation[][] annotations = method.getParameterAnnotations();

        for (int i = 0; i < countParam; i++) {
            Object param = args[i];
            if (param instanceof HttpHeaders) {
                builder.setHeaders((HttpHeaders) param);
                continue;
            }
            // has annotation path variable
            boolean hasPathVariable = false;
            Annotation[] annotationChild = annotations[i];
            for (Annotation annotation : annotationChild) {
                log.trace("annotationsChild {}", annotation);
                if (annotation instanceof PathVariable) {
                    PathVariable pathVariable = (PathVariable) annotation;
                    String name = string(pathVariable.name());
                    String value = string(pathVariable.value());
                    if (name.isEmpty() && value.isEmpty()) {
                        throw new NullPointerException("path variable name or value cannot be null");
                    }
                    String variable = value.isEmpty() ? name : value;
                    if (Objects.isNull(param)) {
                        throw new ApiRequestInvalidParameterException("path variable " + variable + " cannot be null");
                    }
                    // nilai di-encode sebagai segmen path (UTF-8): '/', '?', '#', '{', '}' dan spasi aman
                    url = StringUtils.replace(url, "{" + variable + "}", ApiRequest.encodePathSegment(param));
                    hasPathVariable = true;
                    break;
                }
            }
            if (hasPathVariable) continue;

            // is request body
            builder.setRequest(param);

        }
        Assert.isTrue(isValidUrl(url), "invalid url scheme " + url);
        builder.setUrl(url);
    }

    private String string(String value) {
        if (Objects.isNull(value)) return value;
        return value.trim();
    }


    // END UTIL METHOD

    // --------------------------------------------------------------------------------------------------

    @SuppressWarnings("rawtypes")
    protected Object requestData(Method method, ApiRequest request, int connectTimeout, int requestTimeout, int readTimeout, boolean isThrowResourceAccessException) {
        //
        Type type = ReflectionCommons.typeOf(request.getParameterizedResponse());
        ParameterizedTypeReference typeReference = null;
        boolean isParameterize = Objects.nonNull(type);
        boolean isRestResultParameterize = false;
        if (isParameterize) {
            log.trace("type name {} and class is {} return type {}", type.getTypeName(), type.getClass(), method.getReturnType());
            Class<?> clazz = method.getReturnType();
            if (clazz.equals(ResponseWrapper.class)) {
                log.trace("response type parameterized equals RestResult {}", type);
                // get type parameterize index 1
                Type typeChild = ReflectionCommons.getGenericParameterType(type, 0);
                log.trace("index 0 {}", typeChild);
                typeReference = new ParameterizedTypeReference<Class>() {
                    @Override
                    public Type getType() {
                        return typeChild;
                    }
                };
                isRestResultParameterize = true;
            }
        }

        try {

            RestTemplate restTemplate = restTemplateFactory(connectTimeout, requestTimeout, readTimeout);
            log.trace("http request {} {}", request.getMethod(), request.getUrl());
            request.validate();
            ResponseEntity<?> response = getResponseEntity(restTemplate, request, typeReference);

            if (isRestResultParameterize) {
                log.trace("response with ResponseWrapper");
                return new ResponseWrapper<>(response);
            }
            // if (Objects.isNull(response)) return null;
            return response.getBody();


        } catch (HttpStatusCodeException e) {
            String bodyErrorString = e.getResponseBodyAsString();
            if (isRestResultParameterize) {
                return new ResponseWrapper<>(e);
            }
            log.error("error code {}, {}, body: {}", e.getStatusCode(), e.getStatusText(), bodyErrorString);
            throw e;
        } catch (ResourceAccessException e) {
            if (isThrowResourceAccessException) {
                throw new BadGatewayException("bad gateway: " + e.getMessage(), e);
            }
            if (isRestResultParameterize) return new ResponseWrapper<>(e);
            log.error("resource access exception {}", e.getMessage());
            throw e;
        }
    }

    /**
     * URL dikirim sebagai {@link URI} (bukan String) supaya nilai path/query yang sudah
     * di-encode di {@link ApiRequest} tidak di-encode ulang oleh RestTemplate.
     */
    @SuppressWarnings("rawtypes")
    private ResponseEntity<?> getResponseEntity(RestTemplate restTemplate, ApiRequest request, ParameterizedTypeReference typeReference) {
        URI uri = request.getUri();
        if (HttpMethod.GET == request.getMethod()) {
            if (Objects.isNull(request.getClassResponse())) {
                ParameterizedTypeReference<?> ref = Objects.nonNull(typeReference) ? typeReference : request.getParameterizedResponse();
                return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(request.getHeaders()), ref);
            }
            if (Objects.isNull(request.getParameterizedResponse())) {
                return restTemplate.exchange(uri, HttpMethod.GET, new HttpEntity<>(request.getHeaders()), request.getClassResponse());
            }
        } else {
            if (Objects.isNull(request.getClassResponse())) {
                ParameterizedTypeReference<?> ref = Objects.nonNull(typeReference) ? typeReference : request.getParameterizedResponse();
                return restTemplate.exchange(uri, request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), ref);

            }
            if (Objects.isNull(request.getParameterizedResponse())) {
                return restTemplate.exchange(uri, request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), request.getClassResponse());
            }
        }
        return null;
    }

    /**
     * Pilih RestTemplate berdasarkan timeout anotasi (milidetik).
     * <ul>
     *   <li>semua timeout &lt;= 0 (default anotasi -1): bean {@code restTemplate};</li>
     *   <li>selain itu: RestTemplate dari cache per kombinasi timeout; nilai dipakai apa adanya
     *       (tidak ada minimum 12 detik), timeout &lt;= 0 dibiarkan default HttpClient.</li>
     * </ul>
     * Converter, interceptor, dan error handler diambil dari bean {@code restTemplate} (bila ada)
     * supaya perilakunya sama, hanya timeout yang berbeda.
     */
    RestTemplate restTemplateFactory(int connectTimeout, int requestTimeout, int readTimeout) {
        if (connectTimeout <= 0 && requestTimeout <= 0 && readTimeout <= 0) {
            return baseRestTemplate();
        }
        String key = "CO" + connectTimeout + "RT" + requestTimeout + "RD" + readTimeout;
        return restTemplateCache.computeIfAbsent(key, k -> {
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            if (connectTimeout > 0) requestFactory.setConnectTimeout(connectTimeout);
            if (requestTimeout > 0) requestFactory.setConnectionRequestTimeout(requestTimeout);
            if (readTimeout > 0) requestFactory.setReadTimeout(readTimeout);
            RestTemplate restTemplate = new RestTemplate(requestFactory);
            if (beanFactory.containsBean("restTemplate")) {
                RestTemplate base = baseRestTemplate();
                restTemplate.setMessageConverters(base.getMessageConverters());
                restTemplate.setInterceptors(base.getInterceptors());
                restTemplate.setErrorHandler(base.getErrorHandler());
            }
            log.debug("create RestTemplate {} connect {} request {} read {}", k, connectTimeout, requestTimeout, readTimeout);
            return restTemplate;
        });
    }

    private RestTemplate baseRestTemplate() {
        return beanFactory.getBean("restTemplate", RestTemplate.class);
    }

}