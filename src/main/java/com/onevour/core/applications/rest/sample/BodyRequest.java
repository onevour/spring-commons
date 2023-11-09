package com.onevour.core.applications.rest.sample;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BodyRequest {

    //String type;

    String username;

    String password;
}
