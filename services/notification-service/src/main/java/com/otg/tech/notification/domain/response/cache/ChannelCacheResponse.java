package com.otg.tech.notification.domain.response.cache;

import com.otg.tech.notification.domain.entity.Channel;
import lombok.Builder;

@Builder
public record ChannelCacheResponse(
        String id,
        String channelType) {

    public static ChannelCacheResponse getChannelCacheResponse(Channel channel) {
        return ChannelCacheResponse.builder()
                .id(channel.getId())
                .channelType(channel.getChannelType().name())
                .build();
    }
}
