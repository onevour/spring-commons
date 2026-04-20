package com.onevour.core.repositories.entities;

import com.onevour.core.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

/**
 * webapps-service
 * Created at 5/6/25 15.38
 *
 * @author jhonskuy jhonsky008@gmail.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Entity
@Table(name = "category_features", schema = "public")
public class CategoryFeature extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "category_feature_id_seq")
    @SequenceGenerator(name = "category_feature_id_seq", sequenceName = "category_feature_id_seq", allocationSize = 1)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "key")
    private String key;

    @Column(name = "name")
    private String name;

}
