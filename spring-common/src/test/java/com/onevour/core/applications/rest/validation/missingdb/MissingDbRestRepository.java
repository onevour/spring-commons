package com.onevour.core.applications.rest.validation.missingdb;

import com.onevour.core.applications.rest.annotations.Delete;
import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Put;
import com.onevour.core.applications.rest.repository.RestRepository;

public interface MissingDbRestRepository extends RestRepository {

    @Get(key = "D{PRESENT}", url = "/ok")
    String ok();

    @Get(key = "PRESENT", url = "/ok-no-prefix")
    String okNoPrefix();

    @Put(key = "D{ABSENT}", url = "/a")
    String missingWithPrefix(String body);

    @Delete(key = "ABSENT_NO_PREFIX", url = "/b")
    String missingNoPrefix();
}
