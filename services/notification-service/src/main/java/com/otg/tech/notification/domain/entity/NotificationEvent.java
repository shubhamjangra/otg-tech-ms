package com.otg.tech.notification.domain.entity;

import com.otg.tech.notification.domain.enums.NotificationEventStatus;
import com.otg.tech.util.commons.Utils;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.Hibernate;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Entity
@Table(name = "notification_events")
@Getter
@Setter
@NoArgsConstructor
public class NotificationEvent extends AuditAwareBaseEntity {

    @OneToMany(mappedBy = "notificationEvent", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Notification> notifications = new ArrayList<>();

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "idempotency_key")
    private String idempotencyKey;

    @Column(name = "notification_event_status")
    @Enumerated(EnumType.STRING)
    private NotificationEventStatus notificationEventStatus = NotificationEventStatus.ENQUEUED;

    @Column(name = "customer_data")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> customerData;

    @Column(name = "event_data")
    @JdbcTypeCode(SqlTypes.JSON)
    @Convert(converter = MapToStringConverter.class)
    private Map<String, Object> eventData;

    @Column(name = "retry_attempts")
    private Long retryAttempts;

    public NotificationEvent(String eventType,
                             String idempotencyKey,
                             NotificationEventStatus status,
                             Map<String, Object> customerData) {
        this(eventType, idempotencyKey, status, customerData, Map.of());
    }

    public NotificationEvent(String eventType,
                             String idempotencyKey,
                             Map<String, Object> customerData,
                             Map<String, Object> data) {
        this(eventType, idempotencyKey, NotificationEventStatus.ENQUEUED, customerData, data);
    }

    @Builder
    public NotificationEvent(String eventType,
                             String idempotencyKey,
                             NotificationEventStatus status,
                             Map<String, Object> customerData,
                             Map<String, Object> data) {
        this.eventType = eventType;
        this.idempotencyKey = idempotencyKey;
        this.notificationEventStatus = status;
        this.customerData = customerData;
        this.eventData = data;
        this.retryAttempts = 0L;
    }

    @SuppressWarnings("unused")
    public NotificationEvent addNotification(Notification notification) {
        this.notifications.add(notification);
        notification.setNotificationEvent(this);
        return this;
    }

    @SuppressWarnings("unused")
    public NotificationEvent removeNotification(Notification notification) {
        this.notifications.remove(notification);
        notification.setNotificationEvent(null);
        return this;
    }

    @SuppressWarnings("unused")
    public void changeStateToProcessing() {
        this.notificationEventStatus = NotificationEventStatus.PROCESSING;
    }

    public void markProcessed() {
        this.notificationEventStatus = NotificationEventStatus.SUCCESSFULLY_PROCESSED;
    }

    public void markFailed() {
        this.notificationEventStatus = NotificationEventStatus.FAILED;
    }

    public boolean isProcessing() {
        return this.notificationEventStatus == NotificationEventStatus.PROCESSING;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        NotificationEvent that = (NotificationEvent) o;
        return getId() != null && Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    static class MapToStringConverter implements AttributeConverter<Map<String, Object>, String> {

        @Override
        public String convertToDatabaseColumn(Map<String, Object> attribute) {
            if (attribute == null)
                return null;
            return Utils.toJsonString(attribute);
        }

        @Override
        @SuppressWarnings("unchecked")
        public Map<String, Object> convertToEntityAttribute(String dbData) {
            if (StringUtils.isEmpty(dbData))
                return Collections.emptyMap();
            return Utils.toJavaPojo(dbData, Map.class);
        }
    }
}
