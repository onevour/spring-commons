package com.onevour.core.applications.rest.handler;

import com.onevour.core.applications.exceptions.ApiRequestInvalidParameterException;
import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Post;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import com.onevour.core.applications.rest.repository.RestRepository;
import com.sun.net.httpserver.HttpServer;
import org.apache.http.client.config.RequestConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpHeaders;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test ujung ke ujung RestExecutorMethodHandler lewat proxy RestRepository
 * terhadap server HTTP lokal (com.sun.net.httpserver): timeout per method,
 * URL encoding, query string pada POST, header pemanggil, dan cause BadGatewayException.
 */
@SpringBootTest(classes = RestExecutorMethodHandlerTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RestExecutorMethodHandlerTest {

    @SpringBootApplication
    static class App {
    }

    public interface TimeoutRestRepository extends RestRepository {

        @Get(key = "${test.server.url}", url = "/echo/{id}", connect = 500, read = 2000)
        String firstTimeout(@PathVariable("id") String id);

        @Get(key = "${test.server.url}", url = "/echo/{id}", connect = 1000, request = 1000, read = 1500)
        String secondTimeout(@PathVariable("id") String id);

        @Get(key = "${test.server.url}", url = "/echo/{id}")
        String defaultTimeout(@PathVariable("id") String id);

        @Get(key = "${test.server.url}", url = "/echo/{id}", connect = 500)
        String withHeaders(HttpHeaders headers, @PathVariable("id") String id);

        @Get(key = "${test.server.url}", url = "/slow", read = 300)
        String slow();

        @Get(key = "${test.server.url}", url = "/slow", read = 300, throwException = false)
        ResponseWrapper<String> slowWrapped();

        @Post(key = "${test.server.url}", url = "/items?mode=bulk&v=2", connect = 500)
        String post(Map<String, Object> body);
    }

    /** request terakhir yang diterima server: "METHOD rawPath?rawQuery" dan header */
    static final List<String> REQUESTS = new CopyOnWriteArrayList<>();
    static volatile Map<String, List<String>> lastHeaders = Collections.emptyMap();
    static volatile String lastBody = "";

    static final HttpServer SERVER = startServer();

    private static HttpServer startServer() {
        try {
            HttpServer server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
            server.createContext("/", exchange -> {
                String line = exchange.getRequestMethod() + " " + exchange.getRequestURI().getRawPath()
                        + (exchange.getRequestURI().getRawQuery() == null ? "" : "?" + exchange.getRequestURI().getRawQuery());
                REQUESTS.add(line);
                lastHeaders = exchange.getRequestHeaders();
                try (InputStream in = exchange.getRequestBody()) {
                    lastBody = new String(in.readAllBytes(), StandardCharsets.UTF_8);
                }
                if (exchange.getRequestURI().getRawPath().startsWith("/slow")) {
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                }
                byte[] body = line.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "text/plain; charset=utf-8");
                exchange.sendResponseHeaders(200, body.length);
                exchange.getResponseBody().write(body);
                exchange.close();
            });
            // request /slow tidak boleh memblokir request berikutnya
            server.setExecutor(Executors.newCachedThreadPool());
            server.start();
            return server;
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    @DynamicPropertySource
    static void serverUrl(DynamicPropertyRegistry registry) {
        registry.add("test.server.url", () -> "http://127.0.0.1:" + SERVER.getAddress().getPort());
    }

    @AfterAll
    static void stopServer() {
        SERVER.stop(0);
    }

    @Autowired
    ConfigurableApplicationContext context;

    @Autowired
    TimeoutRestRepository repository;

    @BeforeEach
    void reset() {
        REQUESTS.clear();
        lastBody = "";
    }

    @Test
    void twoMethodsWithDifferentTimeoutsWithoutError() {
        assertEquals("GET /echo/one", repository.firstTimeout("one"));
        assertEquals("GET /echo/two", repository.secondTimeout("two"));
        assertEquals("GET /echo/three", repository.defaultTimeout("three"));
        // panggilan ulang memakai RestTemplate yang sama dari cache
        assertEquals("GET /echo/again", repository.firstTimeout("again"));
        // tidak ada bean RestTemplate/request factory tambahan yang didaftarkan ke context
        assertEquals(Collections.singleton("restTemplate"), context.getBeansOfType(RestTemplate.class).keySet());
        assertFalse(context.containsBean("customRequestFactory"));
    }

    @Test
    void restTemplateFactoryCachesPerTimeoutAndUsesValuesAsIs() {
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        RestExecutorMethodHandler handler = new RestExecutorMethodHandler(beanFactory);

        RestTemplate first = handler.restTemplateFactory(500, -1, 2000);
        assertSame(first, handler.restTemplateFactory(500, -1, 2000));
        RestTemplate second = handler.restTemplateFactory(1000, 1000, 1500);
        assertNotSame(first, second);
        // semua timeout <= 0: bean restTemplate
        assertSame(context.getBean("restTemplate", RestTemplate.class), handler.restTemplateFactory(-1, -1, -1));
        assertSame(context.getBean("restTemplate", RestTemplate.class), handler.restTemplateFactory(0, 0, 0));

        // nilai timeout dipakai apa adanya (tidak dipaksa minimal 12 detik), <= 0 dibiarkan default (-1)
        RequestConfig config = requestConfig(first);
        assertEquals(500, config.getConnectTimeout());
        assertEquals(-1, config.getConnectionRequestTimeout());
        assertEquals(2000, config.getSocketTimeout());
        RequestConfig configSecond = requestConfig(second);
        assertEquals(1000, configSecond.getConnectTimeout());
        assertEquals(1000, configSecond.getConnectionRequestTimeout());
        assertEquals(1500, configSecond.getSocketTimeout());
    }

    private static RequestConfig requestConfig(RestTemplate restTemplate) {
        assertTrue(restTemplate.getRequestFactory() instanceof HttpComponentsClientHttpRequestFactory);
        return (RequestConfig) ReflectionTestUtils.getField(restTemplate.getRequestFactory(), "requestConfig");
    }

    @Test
    void pathVariableEncodedOnce() {
        String response = repository.firstTimeout("a b/c?d#e{f}");
        assertEquals("GET /echo/a%20b%2Fc%3Fd%23e%7Bf%7D", response);
        assertEquals(Collections.singletonList("GET /echo/a%20b%2Fc%3Fd%23e%7Bf%7D"), REQUESTS);
    }

    @Test
    void nullPathVariableRejected() {
        assertThrows(ApiRequestInvalidParameterException.class, () -> repository.firstTimeout(null));
        assertTrue(REQUESTS.isEmpty());
    }

    @Test
    void postKeepsQueryStringAndSendsJsonBody() {
        String response = repository.post(Collections.singletonMap("name", "x"));
        assertEquals("POST /items?mode=bulk&v=2", response);
        assertEquals("{\"name\":\"x\"}", lastBody);
        assertEquals("application/json", lastHeaders.get("Content-type").get(0));
    }

    @Test
    void callerHeadersSentButNotMutated() {
        HttpHeaders caller = new HttpHeaders();
        caller.add("X-Trace", "trace-1");
        caller.add(HttpHeaders.CONTENT_TYPE, "text/plain");

        assertEquals("GET /echo/h", repository.withHeaders(caller, "h"));
        assertEquals("trace-1", lastHeaders.get("X-trace").get(0));
        assertEquals("application/json", lastHeaders.get("Content-type").get(0));
        // HttpHeaders milik pemanggil tetap utuh
        assertEquals(Collections.singletonList("text/plain"), caller.get(HttpHeaders.CONTENT_TYPE));
        assertEquals(Collections.singletonList("trace-1"), caller.get("X-Trace"));
        assertEquals(2, caller.size());
    }

    @Test
    void readTimeoutHonoredAndBadGatewayCarriesCause() {
        long start = System.currentTimeMillis();
        BadGatewayException e = assertThrows(BadGatewayException.class, repository::slow);
        long elapsed = System.currentTimeMillis() - start;
        assertTrue(e.getCause() instanceof ResourceAccessException, "cause: " + e.getCause());
        assertTrue(e.getCause().getCause() instanceof SocketTimeoutException, "root cause: " + e.getCause().getCause());
        // read timeout 300 ms dipakai apa adanya; server baru menjawab setelah 1500 ms
        assertTrue(elapsed < 1200, "elapsed " + elapsed + " ms, timeout tidak dipakai");
    }

    @Test
    void wrappedResourceAccessExceptionThrownByBodyOrElseThrow() {
        ResponseWrapper<String> wrapper = repository.slowWrapped();
        assertTrue(wrapper.isError());
        assertFalse(wrapper.isSuccess());
        assertEquals(502, wrapper.getCode());
        assertTrue(wrapper.isErrorResourceAccessException());
        assertThrows(ResourceAccessException.class, wrapper::bodyOrElseThrow);
    }
}
