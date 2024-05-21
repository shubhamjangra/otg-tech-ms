package com.otg.tech.notification.domain.entity;

import com.otg.tech.notification.domain.enums.NotificationReadStatus;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import com.otg.tech.notification.domain.response.cache.RuleCacheResponse;
import com.otg.tech.notification.domain.response.cache.TemplateCacheResponse;
import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;

import static com.otg.tech.notification.constant.NotificationConstants.LANGUAGE;
import static com.otg.tech.notification.constant.NotificationConstants.USER_ID;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
public class Notification extends AuditAwareBaseEntity {

    @Column(name = "channel")
    private String channel;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "provider_id")
    private String providerId;

    @Column(name = "template_id")
    private String templateId;

    @Column(name = "language")
    private String language;

    @Column(name = "rule_id")
    private String ruleId;

    @Column(name = "event_type")
    private String eventType;

    @Column(name = "user_id")
    private String userId;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "notification_body")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String notificationBody;

    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name = "response_body")
    @JdbcTypeCode(Types.LONGVARCHAR)
    private String responseBody;

    @Column(name = "read_status")
    @Enumerated(EnumType.STRING)
    private NotificationReadStatus readStatus = NotificationReadStatus.UNREAD;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_event_id", referencedColumnName = "id")
    private NotificationEvent notificationEvent;

    public Notification(String channel, String channelId, String provider,
                        String providerId, String templateId, String language,
                        String ruleId) {
        this.channel = channel;
        this.channelId = channelId;
        this.provider = provider;
        this.providerId = providerId;
        this.templateId = templateId;
        this.language = language;
        this.ruleId = ruleId;
    }

    public Notification(NotificationEvent notificationEvent, TemplateCacheResponse templateCacheResponse,
                        ProviderCacheResponse providerCacheResponse, String hydratedTemplate, String responseBody,
                        RuleCacheResponse ruleCacheResponse) {
        this(ruleCacheResponse.channel().channelType(), ruleCacheResponse.channel().id(),
                providerCacheResponse.name(), providerCacheResponse.id(), templateCacheResponse.id(),
                (String) notificationEvent.getCustomerData().get(LANGUAGE), ruleCacheResponse.id());
        this.eventType = notificationEvent.getEventType();
        this.notificationEvent = notificationEvent;
        this.userId = (String) notificationEvent.getCustomerData().get(USER_ID);
        this.notificationBody = hydratedTemplate;
        this.responseBody = responseBody;
    }

    public void markRead() {
        this.readStatus = NotificationReadStatus.READ;
    }

    public void markUnRead() {
        this.readStatus = NotificationReadStatus.UNREAD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        Notification that = (Notification) o;
        return this.getId() != null && this.getId().equals(that.getId());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
