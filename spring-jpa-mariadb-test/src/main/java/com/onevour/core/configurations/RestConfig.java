package com.onevour.core.configurations;

import com.onevour.core.applications.rest.model.RestConfigBaseKey;
import com.onevour.core.repositories.repository.ConfigRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestConfig {

    @Autowired
    ConfigRepository configRepository;

    @Bean
    public RestConfigBaseKey restConfigBaseKey() {
        return new RestConfigBaseKey() {
            @Override
            public String getValue(String key) {
                return configRepository.findById(key).orElseThrow().getValue();
            }
        };
    }
}
