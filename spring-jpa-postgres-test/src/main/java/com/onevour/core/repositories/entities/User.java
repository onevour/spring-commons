package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
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
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "users")
public class User extends BaseEntity {


    @Id
    @Column(name = "username", nullable = false)
    String username;

    String name;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    Set<UserRole> userRoles = new HashSet<>();
//    Set<UserRole> userRoles=null;// = new HashSet<>();

}
