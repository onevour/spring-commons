package com.onevour.core.controller;

import com.onevour.core.applications.annotations.ConverterResolver;
import com.onevour.core.applications.base.BaseRestController;
import com.onevour.core.applications.base.ServiceResolver;
import com.onevour.core.repository.api.converter.SampleConverter;
import com.onevour.core.repository.api.request.SampleRequest;
import com.onevour.core.services.SampleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RestController
public class SampleController extends BaseRestController {

    @Autowired
    SampleService sampleService;

    @GetMapping("/validate")
    public ResponseEntity registerValidateUsername(@ConverterResolver SampleConverter converter, @Valid SampleRequest request) {
        ServiceResolver resolver = sampleService.execute(request);
        return response(resolver, converter);
    }

    @GetMapping("/validate/{sessionCode}")
    public ResponseEntity registerValidate(@ConverterResolver SampleConverter converter, @PathVariable("sessionCode") String sessionCode) {
        ServiceResolver resolver = sampleService.execute(sessionCode);
        log.info("receive parameter sessionCode {}", sessionCode);
        return response(resolver, converter);
    }

    @GetMapping("/validate/{sessionCode}/input")
    public ResponseEntity registerValidate(@ConverterResolver SampleConverter converter, @PathVariable("sessionCode") String sessionCode, @Valid SampleRequest request) {
        ServiceResolver resolver = sampleService.execute(sessionCode);
        log.info("receive parameter sessionCode {}", sessionCode);
        return response(resolver, converter);
    }

}