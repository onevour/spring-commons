package com.onevour.core.repositories.entities;

import com.onevour.core.applications.base.BaseEntity;
import com.onevour.core.repositories.keys.UserBranchId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@IdClass(UserBranchId.class)
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_branch")
public class UserBranch extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_id_seq")
    @SequenceGenerator(name = "user_id_seq", sequenceName = "user_id_seq", allocationSize = 1)
    Long id;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_branch_id_seq")
    @SequenceGenerator(name = "user_branch_id_seq", sequenceName = "user_branch_id_seq", allocationSize = 1)
    Long branchId;

    String name;

}