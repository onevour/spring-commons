package com.onevour.core.applications.configurations;

import com.onevour.core.applications.annotations.ConverterResolver;
import com.onevour.core.applications.base.ResponseConverter;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RestController;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Aspek MethodHandlerConverter: parameter @ConverterResolver pada method @RestController
 * diganti dengan bean converter dari context (yang punya dependency ter-inject),
 * bukan instance kosong yang dikirim pemanggil. Parameter tanpa anotasi dibiarkan.
 */
@SpringBootTest(classes = MethodHandlerConverterTest.App.class, webEnvironment = SpringBootTest.WebEnvironment.NONE,
        properties = "sample.prefix=bean:")
class MethodHandlerConverterTest {

    @SpringBootApplication
    static class App {
    }

    @Component
    public static class SampleConverter extends ResponseConverter<String, String> {
        @Value("${sample.prefix:}")
        String prefix;

        @Override
        public String convert(String param) {
            return prefix + param;
        }
    }

    @RestController
    public static class SampleController {
        public String withResolver(@ConverterResolver SampleConverter converter, String value) {
            return converter.convert(value);
        }

        public String withoutResolver(SampleConverter converter, String value) {
            return converter.convert(value);
        }
    }

    @Autowired
    SampleController controller;

    @Test
    void annotatedParameterReplacedByContextBean() {
        // instance baru tanpa prefix ter-inject; aspek harus menggantinya dengan bean
        String result = controller.withResolver(new SampleConverter(), "x");
        assertEquals("bean:x", result);
    }

    @Test
    void plainParameterLeftUntouched() {
        String result = controller.withoutResolver(new SampleConverter(), "x");
        assertEquals("nullx", result);
    }
}
