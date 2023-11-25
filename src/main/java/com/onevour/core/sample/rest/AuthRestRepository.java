package com.onevour.core.sample.rest;

import com.onevour.core.applications.rest.annotations.Post;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import com.onevour.core.applications.rest.repository.RestRepository;

public interface AuthRestRepository extends RestRepository {

    @Post(url = "http://10.111.33.67:8004/auth/login")
    ResponseWrapper<BaseResponse<AuthResponse>> login(LoginRequest request);
//    BaseResponse<AuthResponse> login(LoginRequest request);

}
