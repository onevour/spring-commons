package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.Feature;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Optional;

public interface FeatureRepository extends JpaRepository<Feature, Long>, JpaSpecificationExecutor<Feature> {

    Optional<Feature> findFirstBySequenceAndDeletedFalseOrDeletedIsNull(Long seq);

    Optional<Feature> findByFeatureKey(String key);
}