package com.onevour.core.applications.base;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Helper success/error di BaseService: kode, pesan default, dan result. */
class BaseServiceTest {

    /** subclass hanya untuk membuka method protected */
    static class SampleService extends BaseService {
        ServiceResolver s() { return success(); }
        ServiceResolver sMsg() { return success("done"); }
        ServiceResolver sStringPicksMessageOverload() { return success("payload"); }
        ServiceResolver<String> sResult() { return successNative("payload"); }
        ServiceResolver<Integer> sObject() { return success(7); }
        ServiceResolver c() { return created(); }
        ServiceResolver<Integer> sNative() { return successNative(7); }
        ServiceResolver e() { return error(); }
        ServiceResolver eMsg() { return error("boom"); }
        ServiceResolver eCode(int code) { return error(code); }
        ServiceResolver<String> eFull() { return error(409, "conflict", "detail"); }
        String p(String v) { return param(v); }
        String str() { return string("a", 1, "b"); }
    }

    private final SampleService service = new SampleService();

    @Test
    void successVariants() {
        assertEquals(200, service.s().getCode());
        assertEquals("success", service.s().getMessage());
        assertEquals("done", service.sMsg().getMessage());
        // JEBAKAN API: success(String) adalah overload "message", bukan result.
        // Untuk result bertipe String pakai successNative(...). Test ini mengunci perilaku itu.
        assertNull(service.sStringPicksMessageOverload().getResult());
        assertEquals("payload", service.sStringPicksMessageOverload().getMessage());
        assertEquals("payload", service.sResult().getResult());
        assertEquals("success", service.sResult().getMessage());
        assertTrue(service.sResult().isSuccess());
        assertEquals(7, service.sObject().getResult());
        assertEquals(201, service.c().getCode());
        assertEquals("created", service.c().getMessage());
        assertEquals(7, service.sNative().getResult());
    }

    @Test
    void errorVariants() {
        assertEquals(500, service.e().getCode());
        assertEquals("error", service.e().getMessage());
        assertTrue(service.e().isError());
        assertEquals("boom", service.eMsg().getMessage());
        assertEquals(500, service.eMsg().getCode());
        ServiceResolver<String> full = service.eFull();
        assertEquals(409, full.getCode());
        assertEquals("conflict", full.getMessage());
        assertEquals("detail", full.getResult());
    }

    @Test
    void errorByCodeUsesDefaultMessageTable() {
        assertEquals("Bad request", service.eCode(400).getMessage());
        assertEquals("Access denied", service.eCode(403).getMessage());
        assertEquals("Data not found", service.eCode(404).getMessage());
        assertEquals(404, service.eCode(404).getCode());
    }

    @Test
    void errorByUnknownCodeThrowsInsteadOfSilentlyMappingToError() {
        // perilaku saat ini: message().get(code) null -> Optional.of melempar NPE.
        // Test ini mengunci perilaku tersebut agar perubahan ke pesan default disengaja, bukan tak sengaja.
        assertThrows(NullPointerException.class, () -> service.eCode(418));
    }

    @Test
    void paramAndString() {
        assertEquals("", service.p(null));
        assertEquals("x", service.p("x"));
        assertEquals("a1b", service.str());
    }
}
