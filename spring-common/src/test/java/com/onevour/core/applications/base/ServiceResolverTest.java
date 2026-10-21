package com.onevour.core.applications.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ServiceResolverTest {

    @Test
    void defaultIsSuccess200() {
        ServiceResolver<String> r = new ServiceResolver<>();
        assertEquals(200, r.getCode());
        assertTrue(r.isSuccess());
        assertFalse(r.isError());
        assertNull(r.getResult());
    }

    @Test
    void successOnlyFor2xx() {
        assertTrue(new ServiceResolver<>(200, "ok").isSuccess());
        assertTrue(new ServiceResolver<>(201, "created").isSuccess());
        assertTrue(new ServiceResolver<>(299, "x").isSuccess());
        assertFalse(new ServiceResolver<>(199, "x").isSuccess());
        assertFalse(new ServiceResolver<>(300, "x").isSuccess());
        assertFalse(new ServiceResolver<>(404, "x").isSuccess());
        assertFalse(new ServiceResolver<>(0, "x").isSuccess());
        assertTrue(new ServiceResolver<>(500, "x").isError());
    }

    @Test
    void settersAndCodeMessage() {
        ServiceResolver<Integer> r = new ServiceResolver<>(200, "ok", 1);
        r.setCodeMessage(404, "not found");
        assertEquals(404, r.getCode());
        assertEquals("not found", r.getMessage());
        assertEquals(1, r.getResult());
        r.setResult(2);
        assertEquals(2, r.getResult());
    }
}
