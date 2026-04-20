package com.onevour.core.repositories.keys;

import lombok.Data;

import java.io.Serializable;

@Data
public class CompanyFeatureId implements Serializable {

    private Long company;

    private Long feature;

}