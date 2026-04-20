package com.onevour.core.repositories.entities;

import com.onevour.core.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "roles")
public class Role extends BaseEntity {

    @Id
    @Column(name = "role", nullable = false, length = 50)
    private String role;

    @OneToMany(mappedBy = "role", orphanRemoval = true)
    private Set<UserRole> userRoles = new HashSet<>();

}