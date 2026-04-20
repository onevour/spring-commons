package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
