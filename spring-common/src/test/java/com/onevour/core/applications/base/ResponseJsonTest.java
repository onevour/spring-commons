package com.onevour.core.applications.base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/** Bentuk JSON envelope: snake_case, errors hanya muncul bila ada, result generik. */
class ResponseJsonTest {

    private final ObjectMapper mapper = new ObjectMapper();

    static class Payload {
        public String fullName = "John";
    }

    @Test
    void envelopeIsSnakeCaseAndOmitsNullErrors() throws Exception {
        Response<Payload> response = new Response<>();
        response.setCode(200);
        response.setMessage("success");
        response.setResult(new Payload());

        String json = mapper.writeValueAsString(response);
        assertEquals("{\"code\":200,\"message\":\"success\",\"result\":{\"fullName\":\"John\"}}", json);
    }

    @Test
    void errorsIncludedWhenSet() throws Exception {
        Response<Object> response = new Response<>();
        response.setCode(400);
        response.setMessage("invalid");
        response.setErrors(Collections.singletonMap("username", "must not be blank"));

        Map<?, ?> tree = mapper.readValue(mapper.writeValueAsString(response), Map.class);
        assertEquals(400, tree.get("code"));
        assertEquals("must not be blank", ((Map<?, ?>) tree.get("errors")).get("username"));
        assertTrue(tree.containsKey("result"));
        assertNull(tree.get("result"));
    }
}
