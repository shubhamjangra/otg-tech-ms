package com.otg.tech.notification.service;

import com.otg.tech.notification.domain.entity.Notification;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.enums.ChannelType;
import com.otg.tech.notification.domain.enums.NotificationReadStatus;
import com.otg.tech.notification.domain.request.NotificationRequest;
import com.otg.tech.notification.domain.response.NotificationReadStatusResponse;
import com.otg.tech.notification.repository.NotificationEventRepository;
import com.otg.tech.notification.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationEventRepository notificationEventRepository;
    private final NotificationRepository notificationRepository;

    @Transactional(readOnly = true)
    public Optional<NotificationEvent> deduplicate(String idempotencyKey) {
        if (idempotencyKey == null || idempotencyKey.trim().isEmpty()) {
            return Optional.empty();
        }
        return this.notificationEventRepository.findByIdempotencyKey(idempotencyKey);
    }

    @Transactional
    public NotificationEvent createEventAndSave(NotificationRequest request) {
        String idempotencyKey = request.getExistingOrCreateIdempotencyKey();
        log.info("Creating new event for request {} with idempotency key {}", request, idempotencyKey);
        NotificationEvent notificationEvent = new NotificationEvent(
                String.valueOf(request.eventType()),
                idempotencyKey,
                request.getCustomerAsMap(),
                request.data()
        );
        NotificationEvent saved = this.notificationEventRepository.save(notificationEvent);
        log.info("Created new event with id {} and idempotency key {}", saved.getId(), idempotencyKey);
        return saved;
    }

    @Transactional
    public List<NotificationReadStatusResponse> getAllUnreadNotifications(String crnOrExplorerId,
                                                                          OffsetDateTime currentTimeMinusDuration) {
        List<Notification> unreadNotifications;
        if (Objects.isNull(currentTimeMinusDuration)) {
            unreadNotifications = notificationRepository
                    .findAllByUserIdAndChannelAndReadStatus(crnOrExplorerId,
                            ChannelType.PUSH_NOTIFICATION.toString(),
                            NotificationReadStatus.UNREAD);
        } else {
            unreadNotifications = notificationRepository
                    .findAllByUserIdAndChannelAndReadStatusAndCreatedAtAfter(crnOrExplorerId,
                            ChannelType.PUSH_NOTIFICATION.toString(),
                            NotificationReadStatus.UNREAD,
                            currentTimeMinusDuration);
        }
        if (CollectionUtils.isEmpty(unreadNotifications)) {
            log.info("No new notifications found for user {}", crnOrExplorerId);
            return Collections.emptyList();
        }
        List<NotificationReadStatusResponse> responseList = unreadNotifications.stream()
                .map(NotificationReadStatusResponse::toNotificationStatusResponse)
                .toList();

        log.info("Total {} unread notifications found for user {}", responseList.size(), crnOrExplorerId);
        return responseList;
    }

    @Transactional
    public List<NotificationReadStatusResponse> getAllUserNotifications(String crnOrExplorerId,
                                                                        OffsetDateTime currentTimeMinusDuration) {
        List<Notification> allNotifications;
        if (Objects.isNull(currentTimeMinusDuration)) {
            allNotifications = notificationRepository
                    .findAllByUserIdAndChannel(crnOrExplorerId,
                            ChannelType.PUSH_NOTIFICATION.toString());
        } else {
            allNotifications = notificationRepository
                    .findAllByUserIdAndChannelAndCreatedAtAfter(crnOrExplorerId,
                            ChannelType.PUSH_NOTIFICATION.toString(), currentTimeMinusDuration);
        }
        if (CollectionUtils.isEmpty(allNotifications)) {
            log.info("No notifications found for user {}", crnOrExplorerId);
            return Collections.emptyList();
        }

        List<NotificationReadStatusResponse> responseList = allNotifications.stream()
                .map(NotificationReadStatusResponse::toNotificationStatusResponse)
                .toList();

        log.info("marking all new notifications to read");
        for (Notification notification : allNotifications) {
            if (notification.getReadStatus() == NotificationReadStatus.UNREAD) {
                notification.markRead();
            }
        }
        log.info("Total {} notifications found for user {}", responseList.size(), crnOrExplorerId);
        return responseList;
    }
}
