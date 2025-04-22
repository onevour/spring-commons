package com.onevour.core.applications.rest.model;

import lombok.*;
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
            this.code = response.getStatusCodeValue();
            this.message = "success";
            this.body = response.getBody();
        }
    }

    public ResponseWrapper(HttpStatusCodeException e) {
        this.code = e.getRawStatusCode();
        this.message = e.getStatusText();
        this.exception = e;
        this.exceptionBodyAsText = e.getResponseBodyAsString();
    }

    public ResponseWrapper(ResourceAccessException resourceAccessException) {
        this.resourceAccessException = resourceAccessException;
    }

    public boolean isSuccess() {
        return code >= 0 && code <= 300;
    }

    public boolean isError() {
        return code == 0 || code >= 300;
    }

    public <X extends Throwable> T bodyOrElseThrow() throws X {
        if (Objects.isNull(body)) throw exception;
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
        if (isError() && codeMatch) throw exception;
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
