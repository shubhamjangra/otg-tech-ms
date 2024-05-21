package com.otg.tech.notification.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.otg.tech.notification.domain.enums.ChannelType;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.NotNull;

public record ChannelRequest(
        @Enumerated(EnumType.STRING) @NotNull @JsonProperty("channelType") ChannelType channelType) {
}
