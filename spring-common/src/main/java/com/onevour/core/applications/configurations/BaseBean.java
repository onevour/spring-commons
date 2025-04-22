package com.onevour.core.applications.configurations;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevour.core.applications.base.BaseSpec;
import com.onevour.core.applications.resolver.ClientManifestResolver;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;

@Slf4j
@Configuration
@ComponentScan("com.onevour.core.applications.configurations")
public class BaseBean {

    @Bean
    public BaseSpec baseSpec() {
        return new BaseSpec();
    }

    @Bean
    @ConditionalOnMissingBean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        return mapper;
    }

    @Bean
    @ConditionalOnMissingBean(name = "restTemplate")
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectionRequestTimeout(12000);
        factory.setConnectTimeout(12000);
        factory.setReadTimeout(12000);
        restTemplate.setRequestFactory(factory);
        return restTemplate;
    }

    @Bean
    public HandlerMethodArgumentResolver clientManifestResolver() {
        return new ClientManifestResolver();
    }

}
