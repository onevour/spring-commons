package com.onevour.core.repository.rest;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.repository.RestRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface PokemonRestRepository extends RestRepository {

    @Get(key = "", url = "https://pokeapi.co/api/v2/pokemon/ditto")
    String call();

    @Get(key = "", url = "https://pokeapi.co/api/v2/pokemon/{name}")
    String callWithPath(@PathVariable("name") String name);
}
