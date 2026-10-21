package com.onevour.core.applications.rest.validation;

import com.onevour.core.applications.rest.configuration.RestRepositoryConfigValidator;
import com.onevour.core.applications.rest.exceptions.RestConfigValidationException;
import com.onevour.core.applications.rest.validation.missingdb.MissingDbApp;
import com.onevour.core.applications.rest.validation.missingprop.MissingPropApp;
import com.onevour.core.applications.rest.validation.nobean.NoBeanApp;
import com.onevour.core.applications.rest.validation.ok.OkApp;
import org.junit.jupiter.api.Test;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Validasi konfigurasi RestRepository saat startup: aplikasi harus GAGAL up bila ada
 * ${properti} atau kunci database (D{...} maupun tanpa prefix) yang tidak ada, dan semua
 * masalah dilaporkan sekaligus. Tiap skenario punya @SpringBootApplication di sub-package
 * sendiri supaya scan RestRepository-nya tidak saling tercampur.
 */
class RestRepositoryConfigValidatorTest {

    private SpringApplicationBuilder app(Class<?> source, String... properties) {
        return new SpringApplicationBuilder(source)
                .web(WebApplicationType.NONE)
                .logStartupInfo(false)
                .properties(properties);
    }

    @Test
    void startupFailsWhenPropertyMissing_andReportsAllProblemsAtOnce() {
        RestConfigValidationException ex = assertThrows(RestConfigValidationException.class,
                () -> app(MissingPropApp.class, "test.present.url=http://localhost:1").run());

        assertEquals(2, ex.getProblems().size(), ex.getMessage());
        String message = ex.getMessage();
        assertTrue(message.contains("MissingPropRestRepository#missingKey()"), message);
        assertTrue(message.contains("${test.missing.base}"), message);
        assertTrue(message.contains("MissingPropRestRepository#missingUrl()"), message);
        assertTrue(message.contains("${test.missing.path}"), message);
        // yang ada dan yang punya default tidak dilaporkan
        assertFalse(message.contains("#ok()"), message);
        assertFalse(message.contains("#withDefault()"), message);
    }

    @Test
    void startupFailsWhenDatabaseKeyMissing_withAndWithoutPrefix() {
        RestConfigValidationException ex = assertThrows(RestConfigValidationException.class,
                () -> app(MissingDbApp.class).run());

        assertEquals(2, ex.getProblems().size(), ex.getMessage());
        String message = ex.getMessage();
        assertTrue(message.contains("MissingDbRestRepository#missingWithPrefix()"), message);
        assertTrue(message.contains("'ABSENT'"), message);
        assertTrue(message.contains("MissingDbRestRepository#missingNoPrefix()"), message);
        assertTrue(message.contains("'ABSENT_NO_PREFIX'"), message);
        assertFalse(message.contains("#ok()"), message);
        assertFalse(message.contains("#okNoPrefix()"), message);
    }

    @Test
    void startupFailsWhenDatabaseKeyUsedWithoutRestConfigBaseKeyBean() {
        RestConfigValidationException ex = assertThrows(RestConfigValidationException.class,
                () -> app(NoBeanApp.class).run());
        assertEquals(1, ex.getProblems().size(), ex.getMessage());
        assertTrue(ex.getMessage().contains("NoBeanRestRepository#database()"), ex.getMessage());
        assertTrue(ex.getMessage().contains("no RestConfigBaseKey bean"), ex.getMessage());
    }

    @Test
    void startupSucceedsWhenAllConfigPresent() {
        try (ConfigurableApplicationContext ctx = app(OkApp.class, "test.present.url=http://localhost:1").run()) {
            assertNotNull(ctx.getBean(RestRepositoryConfigValidator.class));
            assertTrue(ctx.containsBean("okRestRepository"));
        }
    }

    @Test
    void validationCanBeDisabledByProperty() {
        try (ConfigurableApplicationContext ctx = app(MissingPropApp.class,
                "test.present.url=http://localhost:1",
                "spring-common.rest.validate-on-startup=false").run()) {
            assertFalse(ctx.containsBean(RestRepositoryConfigValidator.class.getName()));
            assertEquals(0, ctx.getBeanNamesForType(RestRepositoryConfigValidator.class).length);
            assertTrue(ctx.containsBean("missingPropRestRepository"));
        }
    }

    @Test
    void invalidResolvedUrlIsReported() {
        RestConfigValidationException ex = assertThrows(RestConfigValidationException.class,
                () -> app(OkApp.class, "test.present.url=not-a-url").run());
        assertEquals(1, ex.getProblems().size(), ex.getMessage());
        assertTrue(ex.getMessage().contains("OkRestRepository#property()"), ex.getMessage());
        assertTrue(ex.getMessage().contains("not a valid http(s) url"), ex.getMessage());
    }
}
