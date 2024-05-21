package com.otg.tech.notification.domain.response.cache;

import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.entity.Provider;
import com.otg.tech.notification.domain.entity.Rule;
import com.otg.tech.notification.domain.entity.Template;
import lombok.Builder;

import java.util.List;
import java.util.Optional;

import static com.otg.tech.notification.domain.response.cache.ChannelCacheResponse.getChannelCacheResponse;
import static com.otg.tech.notification.domain.response.cache.ProviderCacheResponse.getProviderCacheResponses;
import static com.otg.tech.notification.domain.response.cache.TemplateCacheResponse.getTemplateCacheResponse;

@Builder
public record RuleCacheResponse(
        String id,
        String eventType,
        List<ProviderCacheResponse> providers,
        ChannelCacheResponse channel,
        TemplateCacheResponse template,
        boolean isRetryEnabled) {

    public static RuleCacheResponse getRuleCacheResponse(Rule rule,
                                                         Channel channel,
                                                         List<Provider> providers,
                                                         Template template) {
        return RuleCacheResponse.builder()
                .id(rule.getId())
                .eventType(rule.getEventType())
                .providers(getProviderCacheResponses(providers))
                .channel(getChannelCacheResponse(channel))
                .template(getTemplateCacheResponse(template))
                .isRetryEnabled(rule.isRetryEnabled())
                .build();
    }

    public Optional<ProviderCacheResponse> getActiveProvider() {
        return this.providers.stream()
                .filter(ProviderCacheResponse::isActive)
                .findFirst();
    }
}
