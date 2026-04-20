package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "features")
public class Feature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "features_id_seq")
    @SequenceGenerator(name = "features_id_seq", sequenceName = "features_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_feature_id", referencedColumnName = "id")
    private CategoryFeature categoryFeature;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL)
    private List<CompanyFeature> companyFeatures;

    @OneToMany(mappedBy = "feature", cascade = CascadeType.ALL)
    private List<CompanyFeatureUser> companyFeatureUsers;

    @Column(name = "veripal_base_url", length = Integer.MAX_VALUE)
    private String veripalBaseUrl;

    @Column(name = "veripal_feature", length = Integer.MAX_VALUE)
    private String veripalFeature;

    @Column(name = "name")
    private String name;

    @Column(name = "sequence")
    private Long sequence;

    @Column(name = "feature_key")
    private String featureKey;
    
}