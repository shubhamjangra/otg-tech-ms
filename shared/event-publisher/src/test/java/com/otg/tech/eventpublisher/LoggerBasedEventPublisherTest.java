package com.otg.tech.eventpublisher;

import com.otg.tech.events.AppTopic;
import com.otg.tech.events.Event;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {EventPublisherConfig.class})
@TestPropertySource(properties = "kafka.event.publisher.enabled=false", locations = "classpath:/application-test" +
        ".properties")
class LoggerBasedEventPublisherTest {

    @Autowired
    protected EventPublisher eventPublisher;

    @Test
    void event_publisher_is_of_logging_type() {
        assertThat(eventPublisher).isInstanceOf(LoggerBasedEventPublisher.class);
    }

    @Test
    void send_event() {
        Event<Map<String, String>> event = Event.<Map<String, String>>builder()
                .source("/customers")
                .type("com.otg.tech.customer.registered")
                .data(Map.of("name", "test", "age", "100", "customerId", "c1"))
                .build();
        eventPublisher.publishAsync(AppTopic.AUDIT_EVENTS, "c1", event);
    }
}
