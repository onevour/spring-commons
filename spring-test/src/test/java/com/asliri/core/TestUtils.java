package com.onevour.core;

import com.onevour.core.applications.commons.ValueOf;
import com.onevour.core.applications.rest.model.ResponseWrapper;
import com.onevour.core.repository.rest.PokemonRestRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;

import java.io.IOException;
import java.util.Map;

@Slf4j
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class TestUtils {

    @Autowired
    PokemonRestRepository pokemonRestRepository;

    @Test
    void valueOfNullTest() throws IOException {
        String stringNull = null;
        Assertions.assertTrue(ValueOf.isNull(stringNull));
        // at least 1 null, empty string
        Assertions.assertTrue(ValueOf.isNull(null, "", "lorem ipsum"));

        Assertions.assertFalse(ValueOf.isNull("null"));
        Assertions.assertFalse(ValueOf.isNull("lorem ipsum", "123", " un "));
    }

    @Test
    void pokemonApiHeader() {
        Object responseWrapper = pokemonRestRepository.call();
//        HttpHeaders headers = responseWrapper.getHeaders();
//        StringBuilder builder = new StringBuilder();
//        for (Map.Entry<String, java.util.List<String>> entry : headers.entrySet()) {
//            String headerName = entry.getKey();
//            for (String value : entry.getValue()) {
//                builder.append(headerName).append(": ").append(value).append("\n");
//            }
//        }
//        log.info("header is\n{}", builder);
//        String contentType = headers.getFirst("Content-Type");
//        log.info("header contentType: {}", contentType);
    }

}