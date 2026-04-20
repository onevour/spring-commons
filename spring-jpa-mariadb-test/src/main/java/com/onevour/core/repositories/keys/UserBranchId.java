package com.onevour.core.repositories.keys;

import lombok.Data;

import java.io.Serializable;
@Data
public class UserBranchId implements Serializable {

    Long id;

    Long branchId;
}
