package com.onevour.core.applications.rest.validation.ok;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Patch;
import com.onevour.core.applications.rest.repository.RestRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface OkRestRepository extends RestRepository {

    @Get(url = "https://example.com/hardcode/{id}")
    String hardcode(@PathVariable("id") String id);

    @Get(key = "${test.present.url}", url = "/prop")
    String property();

    @Patch(key = "D{BASE}", url = "/db/{id}")
    String database(@PathVariable("id") String id, String body);

    @Get(key = "BASE", url = "/db-no-prefix")
    String databaseNoPrefix();
}
