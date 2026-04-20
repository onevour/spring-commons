package com.onevour.core.repositories.repository;


import com.onevour.core.repositories.entities.Company;
import com.onevour.core.repositories.entities.CompanyFeatureUser;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.entities.User;
import com.onevour.core.repositories.keys.CompanyFeatureUserId;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CompanyFeatureUserRepository extends JpaRepository<CompanyFeatureUser, CompanyFeatureUserId> {

    Optional<CompanyFeatureUser> findByFeature(Feature feature);

    Optional<CompanyFeatureUser> findByUserAndFeature(User user, Feature feature);

    @Query(value = "select sum(quota) from company_feature_users where company_id=?1 and feature_id=?2 and username=?3", nativeQuery = true)
    Long sumQuotaByCompanyFeatureUser(Long companyId, Long featureId, String username);

    List<CompanyFeatureUser> findByCompanyIdAndUserAndDeletedFalseOrDeletedIsNull( Long companyId, User user, Sort sort );

    Optional<CompanyFeatureUser> findByCompanyAndFeatureAndUserAndDeletedFalse(Company company, Feature feature, User user);

    Optional<CompanyFeatureUser> findFirstByUserAndFeatureAndDeletedFalse(User user, Feature feature);

    List<CompanyFeatureUser> findAllByCompany(Company company);
}