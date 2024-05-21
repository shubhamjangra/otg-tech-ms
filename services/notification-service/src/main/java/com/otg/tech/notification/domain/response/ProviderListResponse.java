package com.otg.tech.notification.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.entity.Provider;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ProviderListResponse(
        @NotBlank @JsonProperty("id") String id,
        @NotBlank @JsonProperty("name") String name,
        @NotBlank @JsonProperty("channelId") String channelId) {

    public static ProviderListResponse toProviders(Provider provider, String channelId) {
        return ProviderListResponse.builder()
                .id(provider.getId())
                .name(provider.getName())
                .channelId(channelId)
                .build();
    }
}
