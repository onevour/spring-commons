package com.onevour.core;


import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.repository.RestRepository;

public interface PokemonRestRepository extends RestRepository {

    @Get(url = "https://pokeapi.co/api/v2/pokemon/ditto")
    Object call();
}
