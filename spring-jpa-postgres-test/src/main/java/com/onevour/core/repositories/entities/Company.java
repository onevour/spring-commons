package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "companies", schema = "public")
public class Company extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "companies_id_seq")
    @SequenceGenerator(name = "companies_id_seq", sequenceName = "companies_id_seq", allocationSize = 1)
    @Column(name = "id")
    private Long id;

    // CHILD (FK) yang menghalangi delete
    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CompanyFeature> companyFeatures;

    @Column(name = "name")
    private String name;

    @Column(name = "npwp", length = 50)
    private String npwp;

    @Column(name = "email")
    private String email;

    @Column(name = "phone", length = 50)
    private String phone;

    @Column(name = "address")
    private String address;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "district_id")
    private Long districtId;

    @Column(name = "regency_id")
    private Long regencyId;

    @Column(name = "zip_code", length = 10)
    private String zipCode;

    @Column(name = "photo_profile", length = Integer.MAX_VALUE)
    private String photoProfile;

    @Column(name = "slug")
    private String slug;

    @Column(name = "token")
    private String token;

    @Column(name = "flag_status")
    private Long flagStatus;

    @Column(name = "status_active")
    private Boolean statusActive;


}