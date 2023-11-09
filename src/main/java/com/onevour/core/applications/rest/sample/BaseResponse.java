package com.onevour.core.applications.rest.sample;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
@AllArgsConstructor
@NoArgsConstructor
public class BaseResponse<T> {

    // @JsonProperty("http_code")
    private int httpCode;

    // @JsonIgnore
    private Boolean isSuccess = true;

    // @JsonProperty("status_code")
    private String statusCode;

    // @JsonProperty("status_message")
    private String statusMessage;

    // @JsonIgnore()
    private Map<String, String> errorValidation;

    private T data;

    private Object pagging;

    public void sendSuccess(T aData, String message) {
        this.httpCode = 200;
        this.statusCode = "1";
        this.statusMessage = message == null ? "Success" : message;
        this.data = aData;
    }
}
