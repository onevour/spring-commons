package com.onevour.core.services;

import com.onevour.core.repositories.entities.Company;
import com.onevour.core.repositories.entities.CompanyFeature;
import com.onevour.core.repositories.entities.CompanyFeatureUser;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class TransactionCompanyAndFeatureServiceImpl implements TransactionCompanyAndFeatureService {

    @Autowired
    CategoryFeatureRepository categoryFeatureRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    CompanyFeatureRepository companyFeatureRepository;

    @Autowired
    CompanyFeatureUserRepository companyFeatureUserRepository;

    @Autowired
    FeatureRepository featureRepository;

    @Override
    public void createCompanyIfNotExist() {

    }

    @Override
    public void createFeatureIfNotExist() {

    }

    @Override
    public void createCategoryFeatureIfNotExist() {

    }

    @Transactional
    @Override
    public void deleteCompany() {
        Object p = null;
        Optional.ofNullable(p).orElseThrow();
        Company company = companyRepository.findById(2L).orElseThrow();
        log.info("delete company {}", company.getName());
        List<CompanyFeature> companyFeatureList = companyFeatureRepository.findAllByCompany(company);
        companyFeatureRepository.deleteAll(companyFeatureList);
        List<CompanyFeatureUser> companyFeatureUsers = companyFeatureUserRepository.findAllByCompany(company);
        companyFeatureUserRepository.deleteAll(companyFeatureUsers);
        companyRepository.delete(company);
        log.info("delete company success");
    }

    @Transactional
    @Override
    public void deleteFeature() {
        Feature feature = featureRepository.findById(2L).orElseThrow();
        log.info("delete feature {}", feature.getName());
        featureRepository.delete(feature);
        log.info("delete feature success");
    }
}
