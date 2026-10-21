package com.onevour.core.applications.rest.validation.missingprop;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Post;
import com.onevour.core.applications.rest.repository.RestRepository;

public interface MissingPropRestRepository extends RestRepository {

    @Get(key = "${test.present.url}", url = "/ok")
    String ok();

    @Get(key = "${test.missing.base}", url = "/a")
    String missingKey();

    @Post(key = "${test.present.url}", url = "${test.missing.path}")
    String missingUrl(String body);

    @Get(key = "${test.optional.url:http://localhost:1}", url = "/default")
    String withDefault();
}
