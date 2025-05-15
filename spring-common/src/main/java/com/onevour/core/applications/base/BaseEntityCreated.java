package com.onevour.core.applications.base;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.io.Serializable;
import java.util.Date;

@Data
@MappedSuperclass
public class BaseEntityCreated implements Serializable {

    @Column(name = "is_deleted")
    protected Boolean deleted = false;

    protected String createdBy;

    @Column(updatable = false)
    @Temporal(TemporalType.TIMESTAMP)
    protected Date createdDate;

}
