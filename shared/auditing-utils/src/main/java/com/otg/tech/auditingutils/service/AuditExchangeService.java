package com.otg.tech.auditingutils.service;

import com.otg.tech.auditingutils.model.AuditingModel;
import com.otg.tech.eventpublisher.EventPublisher;
import com.otg.tech.events.AppTopic;
import com.otg.tech.events.Event;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import static com.otg.tech.auditingutils.constant.AuditingUtilsConstants.HYPHEN;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuditExchangeService {

    private final EventPublisher eventPublisher;
    @Value("${server.deployed-environment:DEV}")
    protected String serverEnv;

    public void sendAuditingEvent(Event<AuditingModel> event, String id) {
        try {
            eventPublisher.publishAsync(topic(), id, event);
        } catch (Exception e) {
            log.error("Exception occurred while kafka publish event for audit event : {}, exception message: {}", id,
                    e.getMessage());
        }
    }

    public String topic() {
        return (AppTopic.Constants.AUDIT_TOPIC + HYPHEN + serverEnv).toLowerCase();
    }
}
