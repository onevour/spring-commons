package com.onevour.core.applications.rest.exceptions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Dilempar saat startup oleh {@code RestRepositoryConfigValidator} bila ada method
 * RestRepository yang konfigurasinya belum lengkap. Semua masalah dikumpulkan dulu,
 * lalu dilaporkan sekaligus supaya bisa diperbaiki dalam satu kali jalan.
 */
public class RestConfigValidationException extends IllegalStateException {

    private final List<String> problems;

    public RestConfigValidationException(List<String> problems) {
        super(buildMessage(problems));
        this.problems = Collections.unmodifiableList(new ArrayList<>(problems));
    }

    public List<String> getProblems() {
        return problems;
    }

    private static String buildMessage(List<String> problems) {
        StringBuilder sb = new StringBuilder("RestRepository configuration incomplete, ")
                .append(problems.size()).append(" problem(s):");
        for (String problem : problems) {
            sb.append(System.lineSeparator()).append("  - ").append(problem);
        }
        return sb.toString();
    }

}
