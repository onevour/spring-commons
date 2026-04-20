package com.onevour.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.onevour.core.repositories.entities.Config;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.repository.ConfigRepository;
import com.onevour.core.repositories.repository.FeatureRepository;
import com.onevour.core.repositories.rest.PokemonRestRepository;
import com.onevour.core.services.TransactionCompanyAndFeatureService;
import com.onevour.core.services.TransactionUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Slf4j
@SpringBootApplication
public class CoreJPAMariaDBApplication implements CommandLineRunner {

    @Autowired
    ObjectMapper mapper;

    @Autowired
    TransactionUserService transactionService;

    @Autowired
    ConfigRepository configRepository;

    @Autowired
    FeatureRepository featureRepository;

    @Autowired
    TransactionCompanyAndFeatureService transactionCompanyAndFeatureService;

    @Autowired
    PokemonRestRepository pokemonRestRepository;

    public static void main(String[] args) {
        SpringApplication.run(CoreJPAMariaDBApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // runningTransaction();
//        log.info("response hardcode {}", pokemonRestRepository.callHardcode());
//        log.info("response hardcode path variable {}", pokemonRestRepository.callHardcodeWithParam("ditto"));
//        log.info("response hardcode combine {}", pokemonRestRepository.callHardcodeCombine());
//        log.info("response environment{}", pokemonRestRepository.callEnvironment());
//        log.info("response environment combine{}", pokemonRestRepository.callEnvironmentCombine());
//        log.info("response database {}", pokemonRestRepository.callDatabase());
//        log.info("response database as key {}", pokemonRestRepository.callDatabaseAsKey());
//        log.info("response database combine {}", pokemonRestRepository.callDatabaseCombine());
        // runningTransactionCompanyAndFeature();
        Config config = configRepository.findById("POKEMON_DITTO").orElseThrow();
        config.setValue("https://youtube.com");
        configRepository.save(config); // update
//        Feature feature = featureRepository.findById(3L).orElseThrow();
//        featureRepository.delete(feature);
    }


    private void runningTransaction() {
        // transactionService.createRole();
        String username = "jon3";
        transactionService.createRole();
        transactionService.createUser(username); // not create his
        transactionService.updateUser(username, "name 1"); // create his UPDATE
        transactionService.updateUser(username, "name 2"); // create his UPDATE
//        transactionService.deleteUserRole(username, "CLIENT"); // create his DELETE
        transactionService.deleteUser(username); // create his DELETE
//        List<User> users = transactionService.transactionCreate();
//        Thread.sleep(5000);
//        users = transactionService.transactionUpdate(users); // add -number, create his
//        Thread.sleep(5000);
//        users = transactionService.transactionUpdate(users); // add -number, create his
//        Thread.sleep(5000);
//        transactionService.transactionDelete(users); // create his
    }

    private void runningTransactionCompanyAndFeature() {
        transactionCompanyAndFeatureService.createCompanyIfNotExist();
        transactionCompanyAndFeatureService.createFeatureIfNotExist();
        transactionCompanyAndFeatureService.createCategoryFeatureIfNotExist();
//        transactionCompanyAndFeatureService.deleteCompany();
        transactionCompanyAndFeatureService.deleteFeature();
    }

}