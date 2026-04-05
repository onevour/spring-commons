package com.onevour.core;

import com.onevour.core.applications.commons.StopWatch;
import com.onevour.core.repository.rest.HttpBinRestRepository;
import com.onevour.core.repository.rest.PokemonRestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

@Slf4j
//@SpringBootApplication(exclude = {
//        DataSourceAutoConfiguration.class,
//        DataSourceTransactionManagerAutoConfiguration.class,
//        HibernateJpaAutoConfiguration.class
//})
@SpringBootApplication
public class CoreApplicationNoDatasource implements CommandLineRunner {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PokemonRestRepository pokemonRestRepository;

    @Autowired
    HttpBinRestRepository httpBinRestRepository;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplicationNoDatasource.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        StopWatch watch = new StopWatch("test running call api");
        String response = watch.start("call pokemon", () -> pokemonRestRepository.call());
        log.info("response {}", response);
        response = watch.start("call pokemon", () -> pokemonRestRepository.callWithPath("ditto"));
        log.info("response {}", response);
        double result = watch.start("calculate", () -> calculate(10, 20));
        log.info("result {}", result);
        watch.start("calculate void", () -> calculateVoid(10, 2));
        log.info("\n{}", watch.prettyPrintMillis());
        log.info("\n{}", watch.prettyPrintSecond());
        //
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.set("name", "john doe");
        body.add("date", "today");
        response = httpBinRestRepository.urlEncoder(body);
        log.info("result {}", response);
    }

    private double calculate(int x, int y) {
        return x / y;
    }

    private void calculateVoid(int x, int y) {
        double z = x / y;
        log.info("z {}", z);
    }

}
