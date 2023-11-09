package com.onevour.core.applications.rest.sample;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {

    String accessToken;

    Long expiredIn;

    String refreshToken;

    Long refreshExpiredIn;

    AuthUserResponse user;

    @Data
    // @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class), response is camelcase
    @AllArgsConstructor
    @NoArgsConstructor
    public static class AuthUserResponse {

        String id;

        String username;

        Boolean enable;

        String email;

        String firstName;

        String lastName;
    }

}
