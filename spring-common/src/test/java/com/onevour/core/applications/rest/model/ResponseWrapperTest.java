package com.onevour.core.applications.rest.model;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class ResponseWrapperTest {

    @Test
    void bodyOrElseThrowReturnsBody() {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>(ResponseEntity.ok("hello"));
        assertEquals("hello", wrapper.bodyOrElseThrow());
    }

    @Test
    void bodyOrElseThrowWithoutExceptionThrowsIllegalState() {
        // 204 No Content: body null, tidak ada exception -> jangan lempar null (NPE)
        ResponseWrapper<String> wrapper = new ResponseWrapper<>(ResponseEntity.noContent().build());
        IllegalStateException e = assertThrows(IllegalStateException.class, wrapper::bodyOrElseThrow);
        assertTrue(e.getMessage().contains("204"), e.getMessage());
    }

    @Test
    void bodyOrElseThrowNullResponseThrowsIllegalState() {
        ResponseWrapper<String> wrapper = new ResponseWrapper<>((ResponseEntity<String>) null);
        assertThrows(IllegalStateException.class, wrapper::bodyOrElseThrow);
    }

    @Test
    void bodyOrElseThrowWithHttpStatusCodeException() {
        HttpStatusCodeException cause = HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, "missing".getBytes(), null);
        ResponseWrapper<String> wrapper = new ResponseWrapper<>(cause);
        assertSame(cause, assertThrows(HttpStatusCodeException.class, wrapper::bodyOrElseThrow));
        assertEquals("missing", wrapper.getExceptionBodyAsText());
    }

    @Test
    void bodyOrElseThrowWithResourceAccessException() {
        ResourceAccessException cause = new ResourceAccessException("timeout", new IOException("read timed out"));
        ResponseWrapper<String> wrapper = new ResponseWrapper<>(cause);
        assertSame(cause, assertThrows(ResourceAccessException.class, wrapper::bodyOrElseThrow));
        assertTrue(wrapper.isErrorResourceAccessException());
    }

    @Test
    void isErrorAndThrowWithCodesUsesRecordedException() {
        ResourceAccessException cause = new ResourceAccessException("timeout");
        ResponseWrapper<String> wrapper = new ResponseWrapper<>(cause);
        assertSame(cause, assertThrows(ResourceAccessException.class, () -> wrapper.isErrorAndThrow(502)));
        // kode tidak cocok: tidak melempar
        assertDoesNotThrow(() -> wrapper.isErrorAndThrow(404));
    }

    @Test
    void successOnlyFor2xx() {
        assertSuccess(new ResponseWrapper<>(ResponseEntity.ok("x")));
        assertSuccess(new ResponseWrapper<>(ResponseEntity.status(201).body("x")));
        assertSuccess(new ResponseWrapper<>(ResponseEntity.noContent().build()));
    }

    @Test
    void errorForZeroRedirectClientServerAndResourceAccess() {
        assertError(new ResponseWrapper<>((ResponseEntity<String>) null)); // code 0
        assertError(new ResponseWrapper<>(ResponseEntity.status(300).build()));
        assertError(new ResponseWrapper<>(ResponseEntity.status(302).build()));
        assertError(new ResponseWrapper<>(HttpClientErrorException.create(HttpStatus.NOT_FOUND, "Not Found", null, null, null)));
        assertError(new ResponseWrapper<>(new ResourceAccessException("timeout"))); // 502
        ResponseWrapper<String> manual = new ResponseWrapper<>();
        manual.setCode(199);
        assertError(manual);
    }

    private static void assertSuccess(ResponseWrapper<?> wrapper) {
        assertTrue(wrapper.isSuccess(), "code " + wrapper.getCode() + " harus sukses");
        assertFalse(wrapper.isError(), "code " + wrapper.getCode() + " bukan error");
    }

    private static void assertError(ResponseWrapper<?> wrapper) {
        assertTrue(wrapper.isError(), "code " + wrapper.getCode() + " harus error");
        assertFalse(wrapper.isSuccess(), "code " + wrapper.getCode() + " bukan sukses");
    }
}
