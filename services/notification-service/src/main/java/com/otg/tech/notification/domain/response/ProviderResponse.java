package com.otg.tech.notification.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

import java.util.List;

@Builder
public record ProviderResponse(
        @NotBlank @JsonProperty("id") String id,
        @NotBlank @JsonProperty("name") String name,
        @NotBlank @JsonProperty("providerConfigsDetails")
        List<ProviderConfigResponse> providerConfigsDetails,
        @NotBlank @JsonProperty("channelId") String channelId) {

    public static ProviderResponse toProvider(Provider provider, String channelId) {
        return ProviderResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .providerConfigsDetails(toConfig(provider))
                .channelId(channelId)
                .build();
    }

    private static List<ProviderConfigResponse> toConfig(Provider provider) {
        return provider.getProviderConfigs().stream().map(ProviderConfigResponse::toProviderConfig).toList();
    }
}
