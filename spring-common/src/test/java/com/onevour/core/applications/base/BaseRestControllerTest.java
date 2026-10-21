package com.onevour.core.applications.base;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/** Semua variasi response(...) di BaseRestController dan aturan override kode/pesan error. */
class BaseRestControllerTest {

    static class Dto {
        final String value;
        Dto(String value) { this.value = value; }
    }

    static class DtoConverter extends ResponseConverter<String, Dto> {
        @Override
        public Dto convert(String param) { return new Dto("converted:" + param); }
    }

    /** subclass hanya untuk membuka method protected */
    static class Controller extends BaseRestController {
        ResponseEntity r() { return response(); }
        ResponseEntity rMsg() { return responseMessage("hello"); }
        <T> ResponseEntity rValue(T v) { return response(v); }
        <T> ResponseEntity rResolver(ServiceResolver<T> s) { return response(s); }
        <T> ResponseEntity rEmpty(ServiceResolver<T> s) { return responseEmptyResult(s); }
        ResponseEntity rConv(ServiceResolver<String> s, DtoConverter c) { return response(s, c); }
        ResponseEntity rConvClass(ServiceResolver<String> s) { return response(s, DtoConverter.class); }
        <T> ResponseEntity rSuccess(int code, String m, T p) { return responseSuccess(code, m, p); }
        <T> ResponseEntity rError(int code, String m, T p) { return responseError(code, m, p); }
        String str(Object... parts) { return string(parts); }
    }

    private final Controller controller = new Controller();

    @SuppressWarnings("unchecked")
    private static Response<Object> body(ResponseEntity entity) {
        return (Response<Object>) entity.getBody();
    }

    @Test
    void plainSuccessResponses() {
        ResponseEntity r = controller.r();
        assertEquals(200, r.getStatusCodeValue());
        assertEquals(200, body(r).getCode());
        assertEquals("success", body(r).getMessage());
        assertNull(body(r).getResult());

        assertEquals("hello", body(controller.rMsg()).getMessage());
        assertEquals("v", body(controller.rValue("v")).getResult());
    }

    @Test
    void resolverSuccessPassesThroughCodeMessageResult() {
        ResponseEntity r = controller.rResolver(new ServiceResolver<>(201, "created", "x"));
        assertEquals(201, r.getStatusCodeValue());
        assertEquals(201, body(r).getCode());
        assertEquals("created", body(r).getMessage());
        assertEquals("x", body(r).getResult());
    }

    @Test
    void resolverErrorBecomesErrorEnvelopeWithSameCode() {
        ResponseEntity r = controller.rResolver(new ServiceResolver<>(404, "not found"));
        assertEquals(404, r.getStatusCodeValue());
        assertEquals(404, body(r).getCode());
        assertEquals("not found", body(r).getMessage());
    }

    @Test
    void converterAppliedOnlyOnSuccessWithResult() {
        ResponseEntity ok = controller.rConv(new ServiceResolver<>(200, "success", "raw"), new DtoConverter());
        assertEquals("converted:raw", ((Dto) body(ok).getResult()).value);

        ResponseEntity byClass = controller.rConvClass(new ServiceResolver<>(200, "success", "raw"));
        assertEquals("converted:raw", ((Dto) body(byClass).getResult()).value);

        ResponseEntity err = controller.rConv(new ServiceResolver<>(400, "bad"), new DtoConverter());
        assertEquals(400, err.getStatusCodeValue());
        assertNull(body(err).getResult());

        ResponseEntity nullResult = controller.rConv(new ServiceResolver<>(200, "success", null), new DtoConverter());
        assertEquals(200, nullResult.getStatusCodeValue());
        assertNull(body(nullResult).getResult());
    }

    @Test
    void emptyResultDropsPayload() {
        ResponseEntity r = controller.rEmpty(new ServiceResolver<>(200, "success", "ignored"));
        assertEquals(200, r.getStatusCodeValue());
        assertNull(body(r).getResult());
    }

    @Test
    void responseSuccessWithNon2xxIsTreatedAsError() {
        ResponseEntity r = controller.rSuccess(503, "down", null);
        assertEquals(503, r.getStatusCodeValue());
        assertEquals("down", body(r).getMessage());
    }

    @Test
    void responseErrorNeverLooksLikeSuccess() {
        // code 200 dipaksa 500, pesan "success" dipaksa "error"
        ResponseEntity r = controller.rError(200, "success", null);
        assertEquals(500, r.getStatusCodeValue());
        assertEquals(500, body(r).getCode());
        assertEquals("error", body(r).getMessage());

        ResponseEntity keep = controller.rError(422, "invalid", "detail");
        assertEquals(422, keep.getStatusCodeValue());
        assertEquals("invalid", body(keep).getMessage());
        assertEquals("detail", body(keep).getResult());
    }

    @Test
    void stringJoinsWithSingleSpaces() {
        assertEquals("a 1 b", controller.str("a", 1, " b "));
    }
}
