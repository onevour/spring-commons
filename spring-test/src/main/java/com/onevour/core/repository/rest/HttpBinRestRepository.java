package com.onevour.core.repository.rest;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.annotations.Post;
import com.onevour.core.applications.rest.repository.RestRepository;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;

public interface HttpBinRestRepository extends RestRepository {

    @Post(url = "https://httpbin.org/post",  contentType = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String urlEncoder(MultiValueMap form);

}
