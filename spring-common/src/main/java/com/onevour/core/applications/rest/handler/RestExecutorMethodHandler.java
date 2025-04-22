package com.onevour.core.applications.rest.handler;


import com.onevour.core.applications.commons.ApiRequest;
import com.onevour.core.applications.commons.ReflectionCommons;
import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.rest.annotations.*;
import com.onevour.core.applications.rest.exceptions.HttpMethodNotFoundException;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import com.onevour.core.applications.rest.model.RestConfigBaseKey;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
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
import java.util.Objects;
import java.util.Optional;

public class RestExecutorMethodHandler implements MethodInterceptor {

    private final Logger log = LoggerFactory.getLogger(RestExecutorMethodHandler.class);

    private final ConfigurableListableBeanFactory beanFactory;

    public RestExecutorMethodHandler(ConfigurableListableBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
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
     * replace base url
     * if base url hardcode in url param annotation
     * if base url from properties
     * if base url from config database field
     */
    private String baseURL(String annotationKey, String annotationURL) {
        StringBuilder url = new StringBuilder();
        if (Objects.isNull(annotationKey) || annotationKey.trim().isEmpty()) {
            url.append(annotationURL);
        } else if (annotationKey.startsWith("${") && annotationKey.endsWith("}")) {
            Environment environment = beanFactory.getBean(Environment.class);
            String key = annotationKey.trim().replace("${", "").replace("}", "");
            String urlEnv = environment.getProperty(key);
            Assert.notNull(urlEnv, "base url cannot be null, key : " + key);
            url.setLength(0);
            url.append(urlEnv);
            url.append(annotationURL);
            log.trace("key {} value {}", key, urlEnv);
        } else {
            boolean exist = !beanFactory.getBeansOfType(RestConfigBaseKey.class).isEmpty();
            if (exist) {
                RestConfigBaseKey restConfigBaseKey = beanFactory.getBean(RestConfigBaseKey.class);
                url.append(restConfigBaseKey.getValue(annotationKey).trim());
                url.append(annotationURL);
            }
        }
        return url.toString();
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
                    String pathKey = "{" + (value.isEmpty() ? name : value) + "}";
                    url = StringUtils.replace(url, pathKey, String.valueOf(param));
                    hasPathVariable = true;
                    break;
                }
            }
            if (hasPathVariable) continue;

            // is request body
            builder.setRequest(param);

        }
        log.trace("url {}", url);
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

        boolean isAllowConfig = connectTimeout + requestTimeout + readTimeout > 3000;

        StringBuilder configString = new StringBuilder("restTemplate");
        configString.append("CO").append(connectTimeout);
        configString.append("RT").append(requestTimeout);
        configString.append("RD").append(readTimeout);
        String configName = configString.toString();

        try {
            RestTemplate restTemplate = restTemplateFactory(isAllowConfig, configName, connectTimeout, requestTimeout, readTimeout);
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
                throw new BadGatewayException();
            }
            if (isRestResultParameterize) return new ResponseWrapper<>(e);
            log.error("resource access exception {}", e.getMessage());
            throw e;
        }
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<?> exchange(RestTemplate restTemplate, ApiRequest request, ParameterizedTypeReference typeReference) {
        RequestEntity.BodyBuilder requestBuilder = RequestEntity.method(request.getMethod(), request.getUrl());
        HttpHeaders headers = request.getHeaders();

        if (HttpMethod.GET != request.getMethod()) {
            if (Objects.nonNull(request.getRequest())) {
                requestBuilder.body(request.getRequest());
                if (request.getRequest() instanceof byte[]) {
                    headers.setContentLength(((byte[]) request.getRequest()).length);
                }
            }
        }
        requestBuilder.headers(headers);
        if (Objects.isNull(request.getClassResponse())) {
            ParameterizedTypeReference<?> ref = Objects.nonNull(typeReference) ? typeReference : request.getParameterizedResponse();
            return restTemplate.exchange(requestBuilder.build(), ref);
        }
        return restTemplate.exchange(requestBuilder.build(), request.getClassResponse());
    }

    @SuppressWarnings("rawtypes")
    private ResponseEntity<?> getResponseEntity(RestTemplate restTemplate, ApiRequest request, ParameterizedTypeReference typeReference) {
        if (HttpMethod.GET == request.getMethod()) {
            if (Objects.isNull(request.getClassResponse())) {
                ParameterizedTypeReference<?> ref = Objects.nonNull(typeReference) ? typeReference : request.getParameterizedResponse();
                return restTemplate.exchange(request.getUrl(), HttpMethod.GET, new HttpEntity<>(request.getHeaders()), ref);
            }
            if (Objects.isNull(request.getParameterizedResponse())) {
                return restTemplate.exchange(request.getUrl(), HttpMethod.GET, new HttpEntity<>(request.getHeaders()), request.getClassResponse());
            }
        } else {
            if (Objects.isNull(request.getClassResponse())) {
                ParameterizedTypeReference<?> ref = Objects.nonNull(typeReference) ? typeReference : request.getParameterizedResponse();
                return restTemplate.exchange(request.getUrl(), request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), ref);

            }
            if (Objects.isNull(request.getParameterizedResponse())) {
                return restTemplate.exchange(request.getUrl(), request.getMethod(), new HttpEntity<>(request.getRequest(), request.getHeaders()), request.getClassResponse());
            }
        }
        return null;
    }

    private RestTemplate restTemplateFactory(boolean isAllowConfig, String configName, int connectTimeout, int requestTimeout, int readTimeout) {
        if (isAllowConfig) {
            if (checkBeanHasNameAndType(beanFactory, configName, RestTemplate.class)) {
                return beanFactory.getBean(configName, RestTemplate.class);
            }
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(RestTemplate.class);
            // Configure a SimpleClientHttpRequestFactory and set it on the RestTemplate
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
            requestFactory.setConnectionRequestTimeout(Integer.max(requestTimeout, 12000));     // Set read timeout
            requestFactory.setConnectTimeout(Integer.max(connectTimeout, 12000));  // Set connect timeout
            requestFactory.setReadTimeout(Integer.max(readTimeout, 12000));     // Set read timeout

            // Register the request factory bean definition
            BeanDefinitionBuilder factoryBuilder = BeanDefinitionBuilder.genericBeanDefinition(HttpComponentsClientHttpRequestFactory.class);
            factoryBuilder.addPropertyValue("connectionRequestTimeout", Integer.max(requestTimeout, 12000));
            factoryBuilder.addPropertyValue("connectTimeout", Integer.max(connectTimeout, 12000));
            factoryBuilder.addPropertyValue("readTimeout", Integer.max(readTimeout, 12000));
            registry.registerBeanDefinition("customRequestFactory", factoryBuilder.getBeanDefinition());

            // Add properties to RestTemplate BeanDefinition
            builder.addPropertyReference("requestFactory", "customRequestFactory");
            // Register the RestTemplate bean definition
            registry.registerBeanDefinition(configName, builder.getBeanDefinition());
            return beanFactory.getBean(configName, RestTemplate.class);
        }
        return beanFactory.getBean("restTemplate", RestTemplate.class);
    }

    private HttpComponentsClientHttpRequestFactory clientFactory(int connectTimeout, int requestTimeout, int readTimeout) {
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        if (connectTimeout > 0) factory.setConnectTimeout(connectTimeout);
        if (requestTimeout > 0) factory.setConnectionRequestTimeout(requestTimeout);
        if (readTimeout > 0) factory.setReadTimeout(readTimeout);
        return factory;
    }

    public boolean checkBeanHasNameAndType(BeanFactory beanFactory, String beanName, Class<?> beanType) {
        if (beanFactory instanceof ConfigurableListableBeanFactory) {
            ConfigurableListableBeanFactory configurableBeanFactory = (ConfigurableListableBeanFactory) beanFactory;

            // Check if the bean exists by name and if its type matches
            return configurableBeanFactory.containsBean(beanName) && configurableBeanFactory.isTypeMatch(beanName, beanType);
        }
        return false; // If the beanFactory is not a ConfigurableListableBeanFactory
    }

}