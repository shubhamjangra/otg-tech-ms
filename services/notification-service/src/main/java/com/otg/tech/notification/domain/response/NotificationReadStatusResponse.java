package com.otg.tech.notification.domain.response;

import com.otg.tech.notification.domain.entity.Notification;
import com.otg.tech.notification.domain.enums.NotificationReadStatus;
import lombok.Builder;

import java.time.OffsetDateTime;

@Builder
public record NotificationReadStatusResponse(
        NotificationReadStatus readStatus,
        String eventType,
        String subject,
        String body,
        OffsetDateTime createdDate) {

    public static NotificationReadStatusResponse toNotificationStatusResponse(Notification notification) {
        return NotificationReadStatusResponse.builder()
                .body(notification.getResponseBody())
                .eventType(notification.getEventType())
                .readStatus(notification.getReadStatus())
                .subject(notification.getEventType())
                .createdDate(notification.getNotificationEvent().getCreatedAt())
                .build();
    }
}
