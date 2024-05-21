package com.otg.tech.notification.controller;

import com.otg.tech.annotation.ActivityAudit;
import com.otg.tech.commons.ApiResponse;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.request.NotificationReadStatusRequest;
import com.otg.tech.notification.domain.request.NotificationRequest;
import com.otg.tech.notification.domain.response.NotificationReadStatusResponse;
import com.otg.tech.notification.domain.response.NotificationResponse;
import com.otg.tech.notification.service.NotificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/notifications")
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("unused")
public class NotificationController {

    private final NotificationService notificationService;

    @Value(("${notification.display.duration.in.hrs:48}"))
    protected int duration;

    @PostMapping(path = "/send")
    @ActivityAudit(auditType = "Send notification",
            message = "Send notification from notification service.")
    public ApiResponse<NotificationResponse> send(@RequestBody @Valid NotificationRequest request) {
        Optional<NotificationEvent> notificationEventOptional = this.notificationService.deduplicate(
                request.idempotencyKey());
        if (notificationEventOptional.isPresent()) {
            log.info("Found the notification event with the given idempotencyKey {}", request.idempotencyKey());
            NotificationEvent notificationEvent = notificationEventOptional.get();
            return ApiResponse.success(
                    new NotificationResponse(notificationEvent.getIdempotencyKey(),
                            notificationEvent.getId(),
                            notificationEvent.getEventType(),
                            notificationEvent.getNotificationEventStatus().name(),
                            notificationEvent.getCreatedAt())
            );
        }
        log.info("No notification event found with the given idempotencyKey {}", request.idempotencyKey());
        NotificationEvent saved = this.notificationService.createEventAndSave(request);
        return ApiResponse.accepted(
                new NotificationResponse(saved.getIdempotencyKey(),
                        saved.getId(),
                        saved.getEventType(),
                        saved.getNotificationEventStatus().name(),
                        saved.getCreatedAt())
        );
    }

    @PostMapping(path = "/new")
    @ActivityAudit(auditType = "Unread notification list",
            message = "Return unread notification list from notification service.")
    public ApiResponse<List<NotificationReadStatusResponse>> unreadNotificationList(
            @RequestBody NotificationReadStatusRequest request) {
        OffsetDateTime currentTimeMinusDuration = OffsetDateTime.now().minusHours(duration);
        return ApiResponse.accepted(notificationService.getAllUnreadNotifications(request.userId(),
                currentTimeMinusDuration));
    }

    @PostMapping(path = "/new-all")
    @ActivityAudit(auditType = "Unread all notification list",
            message = "Return all unread notification list from notification service.")
    public ApiResponse<List<NotificationReadStatusResponse>> allUnreadNotificationList(
            @RequestBody NotificationReadStatusRequest request) {
        return ApiResponse.accepted(notificationService.getAllUnreadNotifications(request.userId(), null));
    }

    @PostMapping(path = "/list")
    @ActivityAudit(auditType = "user notification list",
            message = "Return user notification list from notification service.")
    public ApiResponse<List<NotificationReadStatusResponse>> userNotifications(
            @RequestBody NotificationReadStatusRequest request) {
        OffsetDateTime currentTimeMinusDuration = OffsetDateTime.now().minusHours(duration);
        return ApiResponse.accepted(notificationService.getAllUserNotifications(request.userId(),
                currentTimeMinusDuration));
    }

    @PostMapping(path = "/list-all")
    @ActivityAudit(auditType = "All user notification list",
            message = "Return all user notification list from notification service.")
    public ApiResponse<List<NotificationReadStatusResponse>> allUserNotifications(
            @RequestBody NotificationReadStatusRequest request) {
        return ApiResponse.accepted(notificationService.getAllUserNotifications(request.userId(), null));
    }
}