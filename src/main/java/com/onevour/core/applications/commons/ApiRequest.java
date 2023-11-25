package com.onevour.core.applications.commons;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class ApiRequest {

    private HttpHeaders headers;

    private String url;

    private Set<Integer> allowForCode;

    private HttpMethod method;

    // body for post, put, patch, delete
    private Object request;

    // expect response
    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.NONE)
    private ParameterizedTypeReference parameterizedResponse;

    // expect response

    @SuppressWarnings("rawtypes")
    @Getter(AccessLevel.NONE)
    private Class clazzResponse;

    public <T> ParameterizedTypeReference<T> getParameterizedResponse() {
        return parameterizedResponse;
    }

    public <T> Class<T> getClassResponse() {
        return clazzResponse;
    }

    private ApiRequest(Builder builder) {
        this.headers = builder.headers;
        this.url = builder.url;
        this.method = builder.method;
        this.request = builder.request;
        this.allowForCode = builder.allowForCode;
        this.parameterizedResponse = builder.parameterized;
        this.clazzResponse = builder.clazzResponse;
    }

    public void validate() {
        if (Objects.isNull(url)) {
            throw new RuntimeException("url cannot be null");
        }

        // validate expect response
        if (Objects.isNull(clazzResponse) && Objects.isNull(parameterizedResponse)) {
            throw new RuntimeException("cannot found response class");
        }
        if (Objects.nonNull(clazzResponse) && Objects.nonNull(parameterizedResponse)) {
            throw new RuntimeException("found more than one response class and parameterize");
        }
    }

    @Slf4j
    public static class Builder {

        private HttpHeaders headers = new HttpHeaders();

        private final Map<String, String> header = new HashMap<>();

        private String url;

        private HttpMethod method;

        // body for post, put, patch, delete
        private Object request;

        private boolean snakeCaseField;

        private boolean requestObjectForGet;

        private final Set<Integer> allowForCode = new HashSet<>();

        // body for delete only
        private final Map<String, Object> requestGetTemp = new HashMap<>();

        // expect response
        private ParameterizedTypeReference<?> parameterized;

        // expect response
        private Class<?> clazzResponse;

        public Builder() {

        }

        public Builder(Class<?> clazzResponse) {
            this.clazzResponse = clazzResponse;
        }

        public Builder(ParameterizedTypeReference<?> parameterized) {
            this.parameterized = parameterized;
        }

        public Builder(Method method) {
            boolean isParameterized = isParameterizedTypeReference(method.getGenericReturnType());
            if (isParameterized) {
                this.parameterized = parameterized(method.getGenericReturnType());
            } else {
                this.clazzResponse = method.getReturnType();
            }
        }

        private <T> ParameterizedTypeReference<T> parameterized(Type type) {
            return new ParameterizedTypeReference<T>() {
                @SuppressWarnings("NullableProblems")
                @Override
                public Type getType() {
                    return type;
                }
            };
        }

        private boolean isParameterizedTypeReference(Type genericReturnType) {

            if (genericReturnType instanceof ParameterizedType) {
                ParameterizedType parameterizedType = (ParameterizedType) genericReturnType;
                Type[] typeArguments = parameterizedType.getActualTypeArguments();
                for (Type typeArgument : typeArguments) {
                    System.out.println("Type Argument: " + typeArgument);
                }
                return typeArguments.length > 0;
            }
            return false;
        }

        public Builder setUrl(String... segments) {
            StringBuilder sb = new StringBuilder();
            for (String part : segments) {
                sb.append(part);
            }
            this.url = sb.toString();
            log.trace("url {}", this.url);
            return this;
        }

        public Builder setUrl(Object request, String... url) {
            setUrl(url);
            this.request = request;
            return this;
        }

        public Builder setRequest(Object request) {
            this.request = request;
            return this;
        }

        public Builder setHeaders(HttpHeaders headers) {
            this.headers = headers;
            return this;
        }

        public Builder setRequest(Object request, boolean isGet) {
            this.request = request;
            this.requestObjectForGet = isGet;
            return this;
        }

        public Builder header(String key, String value) {
            this.header.put(key, value);
            return this;
        }


        public Builder param(String key, Object value) {
            this.requestGetTemp.put(key, value);
            return this;
        }

        public Builder allowForCode(Integer... codes) {
            if (Objects.isNull(codes)) return this;
            for (Integer code : codes) {
                if (Objects.isNull(code)) continue;
                allowForCode.add(code);
            }
            return this;
        }

        public Builder response(ParameterizedTypeReference<?> parameterized) {
            this.parameterized = parameterized;
            return this;
        }

        public Builder response(Class<?> clazzResponse) {
            this.clazzResponse = clazzResponse;
            return this;
        }

        public ApiRequest buildGet() {
            return buildGet(false);
        }

        public ApiRequest buildGet(boolean snakeCaseField) {
            this.method = HttpMethod.GET;
            this.requestObjectForGet = true;
            this.snakeCaseField = snakeCaseField;
            return build();
        }

        public ApiRequest buildPost() {
            return buildPost(true);
        }

        public ApiRequest buildPatch() {
            return buildPatch(true);
        }

        public ApiRequest buildPut() {
            return buildPut(true);
        }

        public ApiRequest buildDelete() {
            return buildDelete(true);
        }

        public ApiRequest buildPost(boolean cleanNonGet) {
            this.method = HttpMethod.POST;
            if (cleanNonGet) cleanNonGet();
            return build();
        }

        public ApiRequest buildPatch(boolean cleanNonGet) {
            this.method = HttpMethod.PATCH;
            cleanNonGet();
            return build();
        }

        public ApiRequest buildPut(boolean cleanNonGet) {
            this.method = HttpMethod.PUT;
            cleanNonGet();
            return build();
        }

        public ApiRequest buildDelete(boolean cleanNonGet) {
            this.method = HttpMethod.DELETE;
            cleanNonGet();
            return build();
        }

        private void cleanNonGet() {
            this.requestObjectForGet = false;
            this.url = StringUtils.split(url, "?")[0];
        }

        private ApiRequest build() {
            validateParam();
            buildHeader();
            buildParamGetIfExist();
            return new ApiRequest(this);
        }

        private void validateParam() {
            // validate parameter request
            if (HttpMethod.GET == method && Objects.nonNull(request) && requestObjectForGet && 0 < requestGetTemp.size()) {
                throw new RuntimeException("method get not allowed exist request body object and call param(key, value)");
            }
        }

        private void buildHeader() {
            header.forEach((k, v) -> headers.add(k, v));
        }

        private String camelToSnake(String str) {
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            str = str.replaceAll(regex, replacement).toLowerCase();
            return str;
        }

        // build url
        private void buildParamGetIfExist() {
            if (HttpMethod.GET == method && Objects.nonNull(request) && requestObjectForGet && 0 < requestGetTemp.size()) {
                throw new RuntimeException("cannot combine setRequest(Object request,boolean isGet) for request get and param(String key, String value)");
            }
            AtomicBoolean first = new AtomicBoolean(false);
            StringBuilder sb = new StringBuilder(url);
            if (requestObjectForGet && Objects.nonNull(request)) {
                final List<Field> fields = FieldUtils.getAllFieldsList(request.getClass());
                for (Field o : fields) {
                    try {
                        Object value = FieldUtils.readDeclaredField(request, o.getName(), true);
                        log.debug("field {} : {}", o.getName(), value);
                        sb.append(first.get() ? "&" : "?");
                        sb.append(snakeCaseField ? camelToSnake(o.getName()) : o.getName());
                        sb.append("=").append(value);
                        first.set(true);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                }
                url = sb.toString();
                return;
            }
            //
            requestGetTemp.forEach((k, v) -> {
                if (first.get()) {
                    sb.append("&").append(k).append("=").append(v);
                } else {
                    sb.append("?").append(k).append("=").append(v);
                }
                first.set(true);
            });
            url = sb.toString();
        }
    }

}