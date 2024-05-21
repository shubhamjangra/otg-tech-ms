package com.otg.tech.notification.domain.response.cache;

import com.otg.tech.notification.domain.entity.Provider;
import lombok.Builder;
import lombok.NonNull;

import java.util.List;
import java.util.Objects;

import static com.otg.tech.notification.domain.response.cache.ProviderConfigCacheResponse.getProviderConfigCacheResponses;

@Builder
public record ProviderCacheResponse(
        String id,
        String name,
        boolean isActive,
        List<ProviderConfigCacheResponse> providerConfigs) {

    @NonNull
    public static List<ProviderCacheResponse> getProviderCacheResponses(List<Provider> providers) {
        return providers.stream().map(provider ->
                        ProviderCacheResponse.builder()
                                .id(provider.getId())
                                .name(provider.getName())
                                .isActive(provider.isActive())
                                .providerConfigs(getProviderConfigCacheResponses(providers))
                                .build())
                .toList();
    }

    public String getConfig(String property) {
        final var config = this.providerConfigs()
                .stream()
                .filter(c -> Objects.equals(c.key(), property))
                .findFirst();
        return config.map(ProviderConfigCacheResponse::value)
                .orElse(null);
    }
}
