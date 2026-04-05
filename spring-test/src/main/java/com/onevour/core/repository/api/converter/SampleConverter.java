package com.onevour.core.repository.api.converter;

import com.onevour.core.applications.base.ResponseConverter;
import com.onevour.core.repository.api.response.SampleResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SampleConverter extends ResponseConverter<String, SampleResponse> {
    @Override
    public SampleResponse convert(String param) {
        return new SampleResponse(param);
    }
}
