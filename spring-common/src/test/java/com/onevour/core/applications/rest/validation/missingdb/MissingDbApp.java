package com.onevour.core.applications.rest.validation.missingdb;

import com.onevour.core.applications.rest.model.RestConfigBaseKey;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.Map;
import java.util.NoSuchElementException;

@SpringBootApplication
public class MissingDbApp {

    /** meniru tabel konfigurasi: hanya kunci PRESENT yang ada */
    @Bean
    public RestConfigBaseKey restConfigBaseKey() {
        Map<String, String> table = Map.of("PRESENT", "http://localhost:1/base");
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                String value = table.get(key);
                if (value == null) throw new NoSuchElementException("config " + key + " not found");
                return value;
            }
        };
    }
}
