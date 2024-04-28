package com.otg.tech.eventpublisher;

import com.otg.tech.events.AppTopic;
import com.otg.tech.events.Event;
import org.springframework.scheduling.annotation.Async;

public interface EventPublisher {

    @Async("eventPublisherThreadExecutor")
    default <EventData> void publishAsync(AppTopic topic, String key, Event<EventData> event) {
        this.publish(topic, key, event);
    }

    @Async("eventPublisherThreadExecutor")
    default <EventData> void publishAsync(String topic, String key, Event<EventData> event) {
        this.publish(topic, key, event);
    }

    default <EventData> void publish(AppTopic topic, String key, Event<EventData> event) {
        this.publish(topic.getTopicName(), key, event);
    }

    <EventData> void publish(String topic, String key, Event<EventData> event);

    default <EventData> void publish(String topic, Event<EventData> event) {
        this.publish(topic, null, event);
    }
}
