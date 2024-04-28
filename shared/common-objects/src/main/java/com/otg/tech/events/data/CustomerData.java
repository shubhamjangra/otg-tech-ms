package com.otg.tech.events.data;

import lombok.Builder;

@Builder
public record CustomerData(String customerId,
                           String deviceId,
                           String device,
                           String mobileNo,
                           String persona,
                           String sessionId,
                           String eventId) {
}
