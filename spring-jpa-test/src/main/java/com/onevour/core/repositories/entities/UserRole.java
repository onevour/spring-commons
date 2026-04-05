package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import com.onevour.core.repositories.keys.UserRoleId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@IdClass(UserRoleId.class)
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "user_roles", schema = "public")
public class UserRole extends BaseEntity {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "username")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role")
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Role role;


}