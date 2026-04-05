package com.onevour.core.repositories.keys;

import com.onevour.core.repositories.entities.Role;
import com.onevour.core.repositories.entities.User;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserRoleId implements Serializable {

    private String user;

    private String role;

    public UserRoleId() {
    }

    public UserRoleId(User user, Role role) {
        this.user = user.getUsername();
        this.role = role.getRole();
    }

}