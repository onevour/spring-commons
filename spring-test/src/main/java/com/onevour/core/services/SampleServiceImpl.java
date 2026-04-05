package com.onevour.core.services;

import com.onevour.core.applications.base.BaseService;
import com.onevour.core.applications.base.ServiceResolver;
import com.onevour.core.repository.api.request.SampleRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SampleServiceImpl extends BaseService implements SampleService {

    @Override
    public ServiceResolver execute(String sessionCode) {
        String result = "OK";
        return success(result);
    }

    @Override
    public ServiceResolver execute(SampleRequest request) {
        String result = "OK";
        return success(result);
    }
}
