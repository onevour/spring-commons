package com.onevour.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevour.core.applications.rest.sample.AuthResponse;
import com.onevour.core.applications.rest.sample.BaseResponse;
import com.onevour.core.applications.rest.sample.BodyRequest;
import com.onevour.core.applications.rest.services.AuthRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class CoreApplication implements CommandLineRunner {

    @Autowired
    AuthRepository authRepository;

    @Autowired
    ObjectMapper mapper;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("app is running");
        BaseResponse<AuthResponse> response = authRepository.login(new BodyRequest("test", "password"));
        log.info("response {}", mapper.writeValueAsString(response));
    }
}
