package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import com.onevour.core.repositories.entities.Company;
import com.onevour.core.repositories.entities.Feature;
import com.onevour.core.repositories.keys.CompanyFeatureId;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@IdClass(CompanyFeatureId.class)
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "company_features", schema = "public")
public class CompanyFeature extends BaseEntity {

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

    @Column(name = "quota")
    private Long quota;

//    @Column(name = "flag_status")
    transient Integer flagStatus;

}