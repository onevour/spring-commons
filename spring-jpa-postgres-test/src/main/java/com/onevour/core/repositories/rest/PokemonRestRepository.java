package com.onevour.core.repositories.rest;

import com.onevour.core.applications.rest.annotations.Get;
import com.onevour.core.applications.rest.repository.RestRepository;
import org.springframework.web.bind.annotation.PathVariable;

public interface PokemonRestRepository extends RestRepository {

    @Get(url = "https://pokeapi.co/api/v2/pokemon/ditto")
    String callHardcode();

    @Get(url = "https://pokeapi.co/api/v2/pokemon/{name}")
    String callHardcodeWithParam(@PathVariable("name") String name);

    @Get(key = "https://pokeapi.co/api/v2", url = "/pokemon/ditto")
    String callHardcodeCombine();

    @Get(key = "${pokemon.base}", url = "${pokemon.ditto}")
    String callEnvironment();

    @Get(key = "${pokemon.base}", url = "/pokemon/ditto")
    String callEnvironmentCombine();

    @Get(key = "D{POKEMON}", url = "D{POKEMON_DITTO}")
    String callDatabase();

    @Get(key = "D{POKEMON}", url = "/pokemon/ditto")
    String callDatabaseCombine();

    @Get(key = "POKEMON", url = "/pokemon/ditto")
    String callDatabaseAsKey();
}
