package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.CategoryFeature;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * webapps-service
 * Created at 5/6/25 15.41
 *
 * @author jhonskuy jhonsky008@gmail.com
 */
public interface CategoryFeatureRepository extends JpaRepository<CategoryFeature, Long> {

    Optional<CategoryFeature> findByKey(String key);
}
