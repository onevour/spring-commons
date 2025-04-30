package com.onevour.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@Slf4j
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
public class CoreApplicationNoDatasource implements CommandLineRunner {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    PokemonRestRepository pokemonRestRepository;

    public static void main(String[] args) {
        SpringApplication.run(CoreApplicationNoDatasource.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        log.info("test running");
        Object response = pokemonRestRepository.call();
        log.info("response\n{}", mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
    }

}
