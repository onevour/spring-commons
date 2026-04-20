package com.onevour.core.repositories.repository;

import com.onevour.core.repositories.entities.UserBranch;
import com.onevour.core.repositories.keys.UserBranchId;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBranchRepository extends JpaRepository<UserBranch, UserBranchId> {

}
