package com.onevour.core.repositories.keys;

import lombok.Data;

import java.io.Serializable;

@Data
//@Embeddable
public class CompanyFeatureUserId implements Serializable {
    private Long company;

    private Long feature;

    private String user;

//    private static final long serialVersionUID = -6062772130585181947L;
//    @Column(name = "company_id", nullable = false)
//    private Long companyId;
//
//    @Column(name = "feature_id", nullable = false)
//    private Long featureId;
//
//    @Column(name = "username", nullable = false)
//    private String username;
//
//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
//        CompanyFeatureUserId entity = (CompanyFeatureUserId) o;
//        return Objects.equals(this.companyId, entity.companyId) &&
//                Objects.equals(this.featureId, entity.featureId) &&
//                Objects.equals(this.username, entity.username);
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(companyId, featureId, username);
//    }

}