package com.otg.tech.notification.repository;

import com.otg.tech.notification.domain.entity.Notification;
import com.otg.tech.notification.domain.enums.NotificationReadStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, String> {

    List<Notification> findAllByUserIdAndChannelAndReadStatus(String userId,
                                                              String channel,
                                                              NotificationReadStatus readStatus);

    List<Notification> findAllByUserIdAndChannelAndReadStatusAndCreatedAtAfter(String userId,
                                                                               String channel,
                                                                               NotificationReadStatus readStatus,
                                                                               OffsetDateTime dateTime);

    List<Notification> findAllByUserIdAndChannel(String userId, String channel);

    List<Notification> findAllByUserIdAndChannelAndCreatedAtAfter(String userId, String channel,
                                                                  OffsetDateTime dateTime);
}
