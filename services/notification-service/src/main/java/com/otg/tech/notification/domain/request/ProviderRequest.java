package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;

public record ProviderRequest(
        @NotBlank @JsonProperty("name") String name,
        @NotBlank @JsonProperty("channelId") String channelId) {
}
