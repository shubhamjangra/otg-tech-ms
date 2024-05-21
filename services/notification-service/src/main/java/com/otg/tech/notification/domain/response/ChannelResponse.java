package com.otg.tech.notification.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.enums.ChannelType;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record ChannelResponse(
        @NotBlank @JsonProperty("id") String id,
        @NotBlank @JsonProperty("channelType") ChannelType channelType) {

    public static ChannelResponse toChannel(Channel channel) {
        return ChannelResponse.builder()
                .id(channel.getId())
                .channelType(channel.getChannelType())
                .build();
    }
}
