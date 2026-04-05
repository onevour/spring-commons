package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.UserRole;
import com.onevour.core.repositories.keys.UserRoleId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRoleRepository extends JpaRepository<UserRole, UserRoleId> {

}
