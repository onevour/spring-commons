package com.onevour.core.services;

public interface TransactionCompanyAndFeatureService {

    void createCompanyIfNotExist();

    void createFeatureIfNotExist();

    void createCategoryFeatureIfNotExist();

    void deleteCompany();

    void deleteFeature();
}
