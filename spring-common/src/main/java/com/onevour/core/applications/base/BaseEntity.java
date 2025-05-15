package com.onevour.core.applications.base;

import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public class BaseEntity extends BaseEntityCreated {

    protected String modifiedBy;

    @Temporal(TemporalType.TIMESTAMP)
    protected Date modifiedDate;

}
