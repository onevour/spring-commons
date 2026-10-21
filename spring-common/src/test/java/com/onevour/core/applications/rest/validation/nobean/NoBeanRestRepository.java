package com.onevour.core.applications.rest.validation.nobean;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.repository.RestRepository;

public interface NoBeanRestRepository extends RestRepository {

    @Get(key = "D{BASE}", url = "/a")
    String database();
}
