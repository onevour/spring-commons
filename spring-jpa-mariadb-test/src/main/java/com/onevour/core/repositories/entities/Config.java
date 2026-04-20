package com.onevour.core.repositories.entities;

import com.onevour.core.applications.annotations.EntityHistory;
import com.onevour.core.applications.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;


@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@EntityHistory
@EntityListeners(AuditingEntityListener.class)
@Table(name = "config")
public class Config extends BaseEntity {

    @Id
    @Column(name = "`key`", nullable = false, length = 50)
    private String key;

    @Column(name = "`value`")
    private String value;

}