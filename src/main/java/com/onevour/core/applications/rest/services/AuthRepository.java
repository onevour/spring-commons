package com.onevour.core.applications.rest.services;

import com.onevour.core.applications.rest.annotations.BasePathConfig;
import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Post;
import com.onevour.core.applications.rest.repository.RestRepository;
import com.onevour.core.applications.rest.sample.AuthResponse;
import com.onevour.core.applications.rest.sample.BaseResponse;
import com.onevour.core.applications.rest.sample.BodyRequest;

import java.util.List;

public interface AuthRepository extends RestRepository {

    @BasePathConfig
    @Get(url = "https://baconipsum.com/api") //
    List<String> auth(BodyRequest request);

    @Post(url = "http://10.111.33.67:8004/auth/login") //
    BaseResponse<AuthResponse> login(BodyRequest request);

}
