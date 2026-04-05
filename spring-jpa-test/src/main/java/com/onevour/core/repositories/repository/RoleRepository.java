package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.Role;
import com.onevour.core.repositories.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

}
