package com.otg.tech.commons;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import java.util.Objects;

@JsonSerialize
@JsonPropertyOrder
@JsonNaming(PropertyNamingStrategies.LowerCamelCaseStrategy.class)
@SuppressWarnings("unused")
public record ApiResponse<T>(ApiResponse.Status status, T data, ErrorResponse error) {

    @JsonCreator
    @JsonIgnoreProperties(ignoreUnknown = true)
    public ApiResponse(
            @JsonProperty("status") Status status,
            @JsonProperty("data") T data,
            @JsonProperty("error") ErrorResponse error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(Status.SUCCESS, data, null);
    }

    public static <T> ApiResponse<T> accepted(T data) {
        return new ApiResponse<>(Status.ACCEPTED, data, null);
    }

    public static <T> ApiResponse<T> error(ErrorResponse error) {
        return new ApiResponse<>(Status.ERROR, null, error);
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(Status.SUCCESS, null, null);
    }

    @SuppressWarnings("unused")
    public boolean isSuccessfulResponse() {
        return Objects.equals(this.status(), Status.SUCCESS) || Objects.equals(this.status(), Status.ACCEPTED);
    }

    public enum Status {
        ACCEPTED("accepted"), SUCCESS("success"), ERROR("error");

        private final String status;

        Status(String status) {
            this.status = status;
        }

        @JsonValue
        public String getStatus() {
            return this.status;
        }
    }
}
