package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.Config;
import com.onevour.core.repositories.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigRepository extends JpaRepository<Config, String> {

}
