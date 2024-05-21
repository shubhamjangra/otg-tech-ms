package com.otg.tech.notification.domain.response;

import com.otg.tech.notification.domain.entity.ProviderConfig;
import com.otg.tech.notification.domain.enums.ConfigDataType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record ProviderConfigResponse(
        @NotBlank String id,
        @NotBlank String key,
        @NotBlank String value,
        @Enumerated(EnumType.STRING)
        @NotNull
        ConfigDataType configDataType) {

    public static ProviderConfigResponse toProviderConfig(ProviderConfig providerConfig) {
        return ProviderConfigResponse.builder()
                .id(providerConfig.getId())
                .key(providerConfig.getKey())
                .value(providerConfig.getValue())
                .configDataType(providerConfig.getConfigDataType())
                .build();
    }
}
