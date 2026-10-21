package com.onevour.core.applications.rest.validation.ok;

import com.onevour.core.applications.rest.model.RestConfigBaseKey;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class OkApp {

    @Bean
    public RestConfigBaseKey restConfigBaseKey() {
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                return "BASE".equals(key) ? "http://localhost:1/db" : null;
            }
        };
    }
}
