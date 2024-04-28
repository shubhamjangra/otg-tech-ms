package com.otg.tech.events;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
public enum AppTopic {
    AUDIT_EVENTS(Constants.AUDIT_TOPIC),
    MQTT_NOTIFICATION_TOPIC(Constants.MQTT_NOTIFICATION_TOPIC);

    private final String topicName;

    AppTopic(String topicName) {
        this.topicName = topicName;
    }

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Constants {
        public static final String AUDIT_TOPIC = "audit-events";
        public static final String MQTT_NOTIFICATION_TOPIC = "mqtt-notification-event";
    }
}
