package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.enums.ConfigDataType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ProviderConfigRequest(
        @NotBlank @JsonProperty("providerId") String providerId,
        @NotBlank @JsonProperty("key") String key,
        @NotBlank @JsonProperty("value") String value,
        @Enumerated(EnumType.STRING)
        @NotNull @JsonProperty("configDataType")
        ConfigDataType configDataType) {
}
