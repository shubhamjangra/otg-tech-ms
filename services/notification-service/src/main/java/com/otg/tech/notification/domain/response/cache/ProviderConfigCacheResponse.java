package com.otg.tech.notification.domain.response.cache;

import com.otg.tech.notification.domain.entity.Provider;
import lombok.Builder;
import lombok.NonNull;

import java.util.Collection;
import java.util.List;

@Builder
public record ProviderConfigCacheResponse(
        String key,
        String value) {

    @NonNull
    public static List<ProviderConfigCacheResponse> getProviderConfigCacheResponses(
            List<Provider> providers) {
        final var providerConfigs = providers.stream().map(
                Provider::getProviderConfigs).toList().stream().flatMap(
                Collection::stream).toList();

        return providerConfigs.stream().map(
                        providerConfig -> ProviderConfigCacheResponse.builder()
                                .key(providerConfig.getKey())
                                .value(providerConfig.getValue())
                                .build())
                .toList();
    }
}
