package com.onevour.core.applications.rest.configuration;

import com.onevour.core.applications.rest.exceptions.RestConfigValidationException;
import org.springframework.boot.diagnostics.AbstractFailureAnalyzer;
import org.springframework.boot.diagnostics.FailureAnalysis;

/**
 * Menampilkan masalah konfigurasi RestRepository dalam blok
 * "APPLICATION FAILED TO START" milik Spring Boot, bukan sekadar stack trace.
 */
public class RestConfigValidationFailureAnalyzer extends AbstractFailureAnalyzer<RestConfigValidationException> {

    @Override
    protected FailureAnalysis analyze(Throwable rootFailure, RestConfigValidationException cause) {
        StringBuilder description = new StringBuilder("Konfigurasi RestRepository belum lengkap, ")
                .append(cause.getProblems().size()).append(" masalah:");
        for (String problem : cause.getProblems()) {
            description.append(System.lineSeparator()).append("  - ").append(problem);
        }
        String action = "Lengkapi properti di application.properties/yml atau environment variable untuk ${...}, "
                + "dan baris di tabel konfigurasi (bean RestConfigBaseKey) untuk D{...} atau key tanpa prefix. "
                + "Untuk menonaktifkan sementara: spring-common.rest.validate-on-startup=false";
        return new FailureAnalysis(description.toString(), action, cause);
    }

}
