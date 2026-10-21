package com.onevour.core.applications.commons;

import com.onevour.core.applications.exceptions.ApiRequestInvalidParameterException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.util.UriUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@Getter
@SuppressWarnings({"ALL", "unchecked"})
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

    /**
     * URL yang sudah ter-encode sebagai {@link URI}. Pakai ini saat memanggil
     * {@code RestTemplate.exchange(URI, ...)} supaya nilai path/query yang sudah
     * di-encode tidak di-encode ulang oleh {@code UriTemplateHandler} milik RestTemplate
     * (mis. {@code %20} menjadi {@code %2520}).
     */
    public URI getUri() {
        return URI.create(url);
    }

    /**
     * Encode satu segmen path (UTF-8), mis. nilai {@code @PathVariable}.
     * Karakter yang punya arti khusus di path ({@code / ? # {}} dan spasi) ikut di-encode.
     */
    public static String encodePathSegment(Object value) {
        return UriUtils.encodePathSegment(String.valueOf(value), StandardCharsets.UTF_8);
    }

    /**
     * Encode nama atau nilai parameter query (UTF-8), mis. {@code &} dan {@code =}.
     */
    public static String encodeQueryParam(Object value) {
        return UriUtils.encodeQueryParam(String.valueOf(value), StandardCharsets.UTF_8);
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
            throw new ApiRequestInvalidParameterException("url cannot be null");
        }

        // validate expect response
        if (Objects.isNull(clazzResponse) && Objects.isNull(parameterizedResponse)) {
            throw new ApiRequestInvalidParameterException("cannot found response class");
        }
        if (Objects.nonNull(clazzResponse) && Objects.nonNull(parameterizedResponse)) {
            throw new ApiRequestInvalidParameterException("found more than one response class and parameterize");
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
            // is resolver
            Type type = method.getGenericReturnType();
            boolean isParameterized = ReflectionCommons.isParameterizedTypeReference(type);
            if (isParameterized) {
                this.parameterized = ReflectionCommons.parameterized(type);
            } else {
                this.clazzResponse = method.getReturnType();
                log.trace("response type class {}", this.clazzResponse);
            }
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

        /**
         * Header milik pemanggil disalin (termasuk list nilainya) supaya tidak ikut
         * termutasi saat builder menambah atau mengganti header (mis. Content-Type).
         */
        public Builder setHeaders(HttpHeaders headers) {
            HttpHeaders copy = new HttpHeaders();
            if (Objects.nonNull(headers)) {
                headers.forEach((key, values) -> copy.put(key, new ArrayList<>(values)));
            }
            this.headers = copy;
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
            this.method = HttpMethod.POST;
            cleanNonGet();
            return build();
        }

        public ApiRequest buildPatch() {
            this.method = HttpMethod.PATCH;
            cleanNonGet();
            return build();
        }

        public ApiRequest buildPut() {
            this.method = HttpMethod.PUT;
            cleanNonGet();
            return build();
        }

        public ApiRequest buildDelete() {
            this.method = HttpMethod.DELETE;
            cleanNonGet();
            return build();
        }

        /**
         * Untuk POST/PUT/PATCH/DELETE objek request dikirim sebagai body, bukan query.
         * Query string yang sudah ada di URL (mis. dari anotasi) tetap dipertahankan.
         */
        private void cleanNonGet() {
            this.requestObjectForGet = false;
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
                throw new ApiRequestInvalidParameterException("method get not allowed exist request body object and call param(key, value)");
            }
        }

        private void buildHeader() {
            header.forEach((k, v) -> {
                headers.remove(k);
                headers.add(k, v);
            });
        }

        private String camelToSnake(String str) {
            String regex = "([a-z])([A-Z]+)";
            String replacement = "$1_$2";
            str = str.replaceAll(regex, replacement).toLowerCase();
            return str;
        }

        // build url
        private void buildParamGetIfExist() {
            boolean isGet = HttpMethod.GET == method;
            boolean isNotNullRequest = Objects.nonNull(request);
            boolean isNotZero = 0 < requestGetTemp.size();
            if (isGet && isNotNullRequest && requestObjectForGet && isNotZero) {
                throw new ApiRequestInvalidParameterException("cannot combine setRequest(Object request, boolean isGet) for request get and param(String key, String value)");
            }
            // kalau URL sudah punya query string, parameter berikutnya disambung dengan '&'
            AtomicBoolean first = new AtomicBoolean(Objects.nonNull(url) && url.contains("?"));
            StringBuilder sb = new StringBuilder(Objects.isNull(url) ? "" : url);
            if (requestObjectForGet && Objects.nonNull(request)) {
                final List<Field> fields = FieldUtils.getAllFieldsList(request.getClass());
                for (Field o : fields) {
                    // field static dan synthetic (mis. this$0, $jacocoData) bukan parameter request
                    if (Modifier.isStatic(o.getModifiers()) || o.isSynthetic()) continue;
                    try {
                        Field field = FieldUtils.getField(request.getClass(), o.getName(), true);
                        Object value = field.get(request);
                        log.debug("field {} : {}", o.getName(), value);
                        // field null dilewati, jangan kirim literal "null"
                        if (Objects.isNull(value)) continue;
                        String name = snakeCaseField ? camelToSnake(o.getName()) : o.getName();
                        appendQueryParam(sb, first, name, value);
                    } catch (IllegalAccessException e) {
                        throw new ApiRequestInvalidParameterException(e);
                    }
                }
                url = sb.toString();
                return;
            }
            //
            requestGetTemp.forEach((k, v) -> {
                if (Objects.isNull(v)) return;
                appendQueryParam(sb, first, k, v);
            });
            url = sb.toString();
        }

        private void appendQueryParam(StringBuilder sb, AtomicBoolean first, String name, Object value) {
            sb.append(first.get() ? "&" : "?");
            sb.append(encodeQueryParam(name)).append("=").append(encodeQueryParam(value));
            first.set(true);
        }
    }

}