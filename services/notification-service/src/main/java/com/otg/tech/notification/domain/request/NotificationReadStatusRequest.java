package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record NotificationReadStatusRequest(
        @NotBlank @JsonProperty("userId") String userId) {
}
