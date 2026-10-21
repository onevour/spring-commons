package com.onevour.core.applications.commons;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ApiRequestTest {

    /** objek request GET: ada field null, static, dan nilai dengan karakter khusus */
    @SuppressWarnings("unused")
    static class SearchRequest {
        static final String IGNORED_STATIC = "static";
        private String keyword = "a b&c=d";
        private String emptyFilter = null;
        private Integer pageSize = 10;
        private String cafe = "café";
    }

    @Test
    void getRequestObjectEncodedAndNullFieldSkipped() {
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/search")
                .setRequest(new SearchRequest(), true)
                .buildGet();

        String url = request.getUrl();
        assertEquals("http://localhost/search?keyword=a%20b%26c%3Dd&pageSize=10&cafe=caf%C3%A9", url);
        assertFalse(url.contains("null"), "field null tidak boleh dikirim sebagai literal null");
        assertFalse(url.contains("IGNORED_STATIC"), "field static bukan parameter");
        // URI harus bisa dibentuk tanpa encode ulang
        URI uri = request.getUri();
        assertEquals("/search", uri.getPath());
        assertEquals("keyword=a b&c=d&pageSize=10&cafe=café", uri.getQuery());
        assertEquals("keyword=a%20b%26c%3Dd&pageSize=10&cafe=caf%C3%A9", uri.getRawQuery());
    }

    @Test
    void getRequestObjectSnakeCase() {
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/search")
                .setRequest(new SearchRequest(), true)
                .buildGet(true);
        assertEquals("http://localhost/search?keyword=a%20b%26c%3Dd&page_size=10&cafe=caf%C3%A9", request.getUrl());
    }

    @Test
    void getRequestObjectAppendedToExistingQueryString() {
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/search?fixed=1")
                .setRequest(new SearchRequest(), true)
                .buildGet();
        assertTrue(request.getUrl().startsWith("http://localhost/search?fixed=1&keyword="), request.getUrl());
    }

    @Test
    void paramEncodedAndNullSkipped() {
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/items")
                .param("q", "x/y?z#w")
                .param("empty", null)
                .buildGet();
        assertEquals("http://localhost/items?q=x/y?z%23w", request.getUrl());
        assertEquals("q=x/y?z#w", request.getUri().getQuery());
    }

    @Test
    void postKeepsQueryStringFromUrl() {
        Object body = Arrays.asList(1, 2);
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/items?mode=bulk&v=2")
                .setRequest(body)
                .buildPost();
        assertEquals(HttpMethod.POST, request.getMethod());
        assertEquals("http://localhost/items?mode=bulk&v=2", request.getUrl());
        assertSame(body, request.getRequest());
        // objek request tidak dijadikan query untuk non-GET
        assertEquals("mode=bulk&v=2", request.getUri().getRawQuery());
    }

    @Test
    void putPatchDeleteKeepQueryString() {
        String url = "http://localhost/items/1?force=true";
        assertEquals(url, new ApiRequest.Builder(String.class).setUrl(url).buildPut().getUrl());
        assertEquals(url, new ApiRequest.Builder(String.class).setUrl(url).buildPatch().getUrl());
        assertEquals(url, new ApiRequest.Builder(String.class).setUrl(url).buildDelete().getUrl());
    }

    @Test
    void callerHeadersNotMutated() {
        HttpHeaders caller = new HttpHeaders();
        caller.add(HttpHeaders.CONTENT_TYPE, "text/plain");
        caller.add("X-Trace", "1");
        List<String> traceValues = caller.get("X-Trace");

        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/items")
                .setHeaders(caller)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .header("X-Trace", "2")
                .buildPost();

        assertEquals("application/json", request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("2", request.getHeaders().getFirst("X-Trace"));
        // header pemanggil tetap utuh, termasuk list nilainya
        assertEquals("text/plain", caller.getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals(Arrays.asList("1"), traceValues);
        assertNotSame(caller, request.getHeaders());
    }

    @Test
    void readOnlyCallerHeadersAccepted() {
        HttpHeaders caller = new HttpHeaders();
        caller.add("X-Trace", "1");
        HttpHeaders readOnly = HttpHeaders.readOnlyHttpHeaders(caller);
        ApiRequest request = new ApiRequest.Builder(String.class)
                .setUrl("http://localhost/items")
                .setHeaders(readOnly)
                .header(HttpHeaders.CONTENT_TYPE, "application/json")
                .buildGet();
        assertEquals("application/json", request.getHeaders().getFirst(HttpHeaders.CONTENT_TYPE));
        assertEquals("1", request.getHeaders().getFirst("X-Trace"));
    }

    @Test
    void encodeHelpers() {
        assertEquals("a%20b%2Fc%3Fd%23e%7Bf%7D", ApiRequest.encodePathSegment("a b/c?d#e{f}"));
        assertEquals("a%20b%26c%3Dd", ApiRequest.encodeQueryParam("a b&c=d"));
        assertEquals("42", ApiRequest.encodePathSegment(42));
    }
}
