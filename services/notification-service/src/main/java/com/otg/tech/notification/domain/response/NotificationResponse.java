package com.otg.tech.notification.domain.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.OffsetDateTime;

public record NotificationResponse(
        @JsonProperty("idempotencyKey") String idempotencyKey,
        @JsonProperty("id") String id,
        @JsonProperty("eventType") String eventType,
        @JsonProperty("status") String status,
        @JsonProperty("submittedAt") OffsetDateTime submittedAt) {
}
