package com.onevour.core.repository.api.request;


import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SampleRequest {

    @NotBlank
    String username;
}
