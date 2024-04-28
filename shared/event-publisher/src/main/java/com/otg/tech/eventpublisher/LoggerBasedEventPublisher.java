package com.otg.tech.eventpublisher;

import com.otg.tech.events.Event;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(name = "kafka.event.publisher.enabled", havingValue = "false", matchIfMissing = true)
@SuppressWarnings("unused")
class LoggerBasedEventPublisher implements EventPublisher {

    @Override
    public <EventData> void publish(String topic, String key, Event<EventData> event) {
        log.info("Publishing event {} with key {} to topic {}", event, key, topic);
    }
}
