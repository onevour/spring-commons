package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import com.onevour.core.repositories.entities.Company;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.entities.User;
import com.onevour.core.repositories.keys.CompanyFeatureUserId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@EqualsAndHashCode(callSuper = true)
@IdClass(CompanyFeatureUserId.class)
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "company_feature_users")
public class CompanyFeatureUser extends BaseEntity {

    @Id
    @ManyToOne
    @JoinColumn(name = "company_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Company company;

    @Id
    @ManyToOne
    @JoinColumn(name = "feature_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private Feature feature;

    @Id
    @ManyToOne
    @JoinColumn(name = "username", referencedColumnName = "username", insertable = false, updatable = false, nullable = false)
    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    private User user;

    @Column(name = "quota")
    private Long quota;


}