package com.onevour.core;

import com.onevour.core.applications.exceptions.BadGatewayException;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import com.onevour.core.sample.rest.AuthResponse;
import com.onevour.core.sample.rest.AuthRestRepository;
import com.onevour.core.sample.rest.BaseResponse;
import com.onevour.core.sample.rest.LoginRequest;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class CoreApplicationTests {

    Logger log = LoggerFactory.getLogger(CoreApplicationTests.class);

    @Autowired
    AuthRestRepository authRestRepository;

    @Test
    void contextLoads() {

        ResponseWrapper<BaseResponse<AuthResponse>> response = authRestRepository.login(new LoginRequest("jakarta@binda.id", "masuk123"));
        response.isErrorAndThrow(BadGatewayException::new, 401);
        log.info("result is {}, {}, {}", response.getCode(), response.getMessage(), response.getBody());

    }

}
