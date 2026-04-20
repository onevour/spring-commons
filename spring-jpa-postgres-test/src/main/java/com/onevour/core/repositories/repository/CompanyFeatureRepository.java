package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.Company;
import com.onevour.core.repositories.entities.CompanyFeature;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.keys.CompanyFeatureId;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyFeatureRepository extends JpaRepository<CompanyFeature, CompanyFeatureId>, JpaSpecificationExecutor<CompanyFeature> {

    Optional<CompanyFeature> findByCompanyAndFeatureAndDeletedFalse(Company company, Feature feature);

    @Query(value = "select sum(quota) from company_features where feature_id=?1", nativeQuery = true)
    Long sumQuotaByFeature(Long feature);

    @Query(value = "select sum(quota) from company_features where company_id=?1 and feature_id=?2", nativeQuery = true)
    Long sumQuotaByCompanyAndFeature(Long companyId, Long featureId);

    List<CompanyFeature> findByCompanyIdAndDeletedFalseOrDeletedIsNull(Long companyId, Sort sort);

    CompanyFeature findByCompanyIdAndFeatureIdAndDeletedFalseOrDeletedIsNull(Long companyId, Long featureId);

    Optional<CompanyFeature> findByCompanyIdAndFeatureIdAndDeletedFalse(Long companyId, Long featureId);
    
    Optional<CompanyFeature> findAllByCompanyAndDeletedFalse(Company company);

    Optional<CompanyFeature> findOneByCompanyAndFeature(Company company, Feature feature);

    List<CompanyFeature> findAllByCompany(Company company);
}