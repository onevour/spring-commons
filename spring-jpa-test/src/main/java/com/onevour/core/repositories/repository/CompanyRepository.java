package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.Company;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long>, JpaSpecificationExecutor<Company> {
    Optional<Company> findByName( String name);

    Optional<Company> findByNameAndDeletedFalseOrDeletedIsNull(String name);

    Optional<Company> findBySlug(String slug);
    Optional<Company> findFirstBySlug(String slug);
}