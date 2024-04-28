package com.otg.tech.eventpublisher;

import com.otg.tech.events.Event;
import io.micrometer.common.KeyValues;
import io.micrometer.observation.annotation.Observed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.support.micrometer.KafkaRecordSenderContext;
import org.springframework.kafka.support.micrometer.KafkaTemplateObservationConvention;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Semaphore;

@Component
@Slf4j
@ConditionalOnProperty(name = "kafka.event.publisher.enabled", havingValue = "true")
@SuppressWarnings("unused")
@Observed
class KafkaBasedEventPublisher implements EventPublisher {
    private final Semaphore semaphore;
    private final KafkaTemplate<String, Event<?>> kafkaTemplate;

    public KafkaBasedEventPublisher(KafkaTemplate<String, Event<?>> kafkaTemplate) {
        this.semaphore = new Semaphore(1);
        this.kafkaTemplate = kafkaTemplate;
        log.info("Initializing KafkaBasedEventPublisher with bounded context..............");
    }

    @Override
    public <EventData> void publish(String topic, String key, Event<EventData> event) {
        try {
            String eventId = event.id();
            log.info("Sending event with id {} to topic {}, runner : {}",
                    eventId,
                    topic,
                    Thread.currentThread().getName()
            );
            this.semaphore.acquire();
            log.info("Acquired permit, publishing event : {} to topic : {}, runner : {}",
                    event,
                    topic,
                    Thread.currentThread().getName()
            );
            CompletableFuture<SendResult<String, Event<?>>> future;
            this.kafkaTemplate.setObservationEnabled(true);
            this.kafkaTemplate.setObservationConvention(new KafkaTemplateObservationConvention() {
                @Override
                public KeyValues getLowCardinalityKeyValues(KafkaRecordSenderContext context) {
                    return KeyValues.of("topic", context.getDestination(),
                            "id", String.valueOf(context.getRecord().key()));
                }
            });
            if (key == null) {
                future = this.kafkaTemplate.send(topic, event);
            } else {
                future = this.kafkaTemplate.send(topic, key, event);
            }
            future.whenComplete((result, ex) -> {
                if (ex == null) {
                    log.info("Received success response for event with id {} to topic {}. Response is {}",
                            eventId,
                            topic,
                            result
                    );
                } else {
                    log.error("Received failure when sending event with id {} to topic {}",
                            eventId,
                            topic,
                            ex
                    );
                }
            });
            log.info("Sent event with id {} to topic {}, runner : {}",
                    eventId,
                    topic,
                    Thread.currentThread().getName()
            );
        } catch (KafkaException | InterruptedException e) {
            log.error("Exception while writing event {} to Kafka", event.id(), e);
        } finally {
            log.info("Releasing permit acquired by runner : {}", Thread.currentThread().getName());
            this.semaphore.release();
        }
    }
}
