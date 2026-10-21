package com.onevour.core.applications.rest.model;

import lombok.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseWrapper<T> {

    private HttpHeaders headers;

    private int code;

    private String message;

    @Setter(AccessLevel.NONE)
    private T body;

    @Setter(AccessLevel.NONE)
    private HttpStatusCodeException exception;

    @Setter(AccessLevel.NONE)
    private String exceptionBodyAsText;

    @Setter(AccessLevel.NONE)
    private ResourceAccessException resourceAccessException;

    public ResponseWrapper(ResponseEntity<T> response) {
        if (Objects.nonNull(response)) {
            this.headers = response.getHeaders();
            this.code = response.getStatusCodeValue();
            this.message = "success";
            this.body = response.getBody();
        }
    }

    public ResponseWrapper(HttpStatusCodeException e) {
        this.headers = e.getResponseHeaders();
        this.code = e.getRawStatusCode();
        this.message = e.getStatusText();
        this.exception = e;
        this.exceptionBodyAsText = e.getResponseBodyAsString();
    }

    /**
     * service unavailable case by<br/>
     * unknown host<br/>
     * read timeout<br/>
     * connect timeout<br/>
     * connection refused<br/>
     */
    public ResponseWrapper(ResourceAccessException resourceAccessException) {
        this.resourceAccessException = resourceAccessException;
        this.code = 502;
    }

    /**
     * sukses hanya untuk kode 2xx; kode 0 (tidak ada response) dan selain 2xx dianggap error,
     * sehingga {@link #isSuccess()} dan {@link #isError()} tidak pernah sama-sama true
     */
    public boolean isSuccess() {
        return code >= 200 && code < 300;
    }

    public boolean isError() {
        return !isSuccess();
    }

    /**
     * body, atau lempar exception yang tercatat: {@link HttpStatusCodeException},
     * lalu {@link ResourceAccessException}; bila tidak ada (mis. 204 tanpa body)
     * lempar {@link IllegalStateException} dengan pesan yang jelas, bukan {@code null}
     */
    public <X extends Throwable> T bodyOrElseThrow() throws X {
        if (Objects.isNull(body)) throw defaultException();
        return body;
    }

    public <X extends Throwable> T bodyOrElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return Optional.ofNullable(body).orElseThrow(exceptionSupplier);
    }

    public <X extends Throwable> T bodyOrElseThrow(Supplier<? extends X> exceptionSupplier, int... codes) throws X {
        boolean codeMatch = isCodeMatch(codes);
        if (isError() && codeMatch) throw exceptionSupplier.get();
        return Optional.ofNullable(body).orElseThrow(exceptionSupplier);
    }

    /**
     * default exception when error HttpStatusCodeException
     */
    public <X extends Throwable> void isErrorAndThrow(int... codes) throws X {
        boolean codeMatch = isCodeMatch(codes);
        if (isError() && codeMatch) throw defaultException();
    }

    public <X extends Throwable> void isErrorAndThrow(Supplier<? extends X> exceptionSupplier) throws X {
        if (isError()) throw exceptionSupplier.get();
    }

    public <X extends Throwable> void isErrorAndThrow(Supplier<? extends X> exceptionSupplier, int... codes) throws X {
        boolean codeMatch = isCodeMatch(codes);
        if (isError() && codeMatch) throw exceptionSupplier.get();
    }


    public <X extends Throwable> void isErrorResourceAccessExceptionThrow() throws X {
        if (Objects.nonNull(resourceAccessException)) throw resourceAccessException;
    }

    public boolean isErrorResourceAccessException() {
        return Objects.nonNull(resourceAccessException);
    }


    private RuntimeException defaultException() {
        if (Objects.nonNull(exception)) return exception;
        if (Objects.nonNull(resourceAccessException)) return resourceAccessException;
        return new IllegalStateException("response body is null and no exception recorded, http code " + code + (Objects.isNull(message) ? "" : " " + message));
    }

    private boolean isCodeMatch(int[] codes) {
        boolean codeMatch = false;
        for (int i : codes) {
            if (i == code) {
                codeMatch = true;
                break;
            }
        }
        return codeMatch;
    }
}
