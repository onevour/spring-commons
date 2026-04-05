package com.onevour.core.services;

import com.onevour.core.applications.base.ServiceResolver;
import com.onevour.core.repository.api.request.SampleRequest;

public interface SampleService {
    ServiceResolver execute(String sessionCode);

    ServiceResolver execute(SampleRequest request);
}
