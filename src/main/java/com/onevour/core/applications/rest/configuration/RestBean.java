package com.onevour.core.applications.rest.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RestBean {

    @Bean
    public RestBeanInit beanConfiguration() {
        return new RestBeanInit();
    }
}
