package com.otg.tech.notification.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.otg.tech.events.Event;
import com.otg.tech.events.data.MqttData;
import com.otg.tech.notification.config.prop.PropertyConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.util.backoff.FixedBackOff;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "kafka.event.consumer.enabled", havingValue = "true")
@PropertySource("classpath:/event-consumer.properties")
@SuppressWarnings("unused")
@RequiredArgsConstructor
@EnableKafka
public class KafkaConsumerConfig {
    private final PropertyConfig kafkaConsumerProp;

    @Bean
    public ConsumerFactory<String, Event<MqttData>> mqttConsumerFactory() {
        Map<String, Object> config = new HashMap<>();
        log.info("Connecting Kafka consumer....................");
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaConsumerProp.getKafkaConsumerBootstrapServer());
        config.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaConsumerProp.getKafkaConsumerGroupId());
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);
        config.put(ConsumerConfig.CLIENT_ID_CONFIG, kafkaConsumerProp.getKafkaConsumerClientId());
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, kafkaConsumerProp.getKafkaConsumerAutoOffset());
        config.put(ConsumerConfig.REQUEST_TIMEOUT_MS_CONFIG, kafkaConsumerProp.getKafkaConsumerRequestTimeout());
        config.put(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, kafkaConsumerProp.getKafkaConsumerHeartbeatInterval());
        config.put(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, kafkaConsumerProp.getKafkaConsumerMaxPollInterval());
        config.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, kafkaConsumerProp.getKafkaConsumerMaxPollRecord());
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, kafkaConsumerProp.getKafkaConsumerSessionTimeout());
        config.put(ConsumerConfig.RETRY_BACKOFF_MS_CONFIG, kafkaConsumerProp.getBackOffMs());
        config.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG,
                kafkaConsumerProp.getKafkaConsumerSecurityProtocol());

        return new DefaultKafkaConsumerFactory<>(config, new StringDeserializer(),
                new JsonDeserializer<>(new TypeReference<>() {
                }));
    }

    @Autowired
    @Bean("notificationKafkaListenerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Event<MqttData>> mqttKafkaListenerFactory(
            ConsumerFactory<String, Event<MqttData>> consumerFactory
    ) {
        final DefaultErrorHandler defaultErrorHandler = defaultErrorHandler();
        ConcurrentKafkaListenerContainerFactory<String, Event<MqttData>> factory
                = new ConcurrentKafkaListenerContainerFactory<>();
        factory.getContainerProperties().setObservationEnabled(true);
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(defaultErrorHandler);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.RECORD);
        return factory;
    }

    private DefaultErrorHandler defaultErrorHandler() {
        return new DefaultErrorHandler(
                new ConsumerRecordErrorRecoverer(),
                new FixedBackOff(
                        kafkaConsumerProp.getBackOffMs(),
                        kafkaConsumerProp.getMaxRetryCount()
                )
        );
    }

    /**
     * Handles Kafka failures
     */
    @SuppressWarnings("checkstyle:FinalClass")
    private static class ConsumerRecordErrorRecoverer implements ConsumerRecordRecoverer {
        @Override
        public void accept(ConsumerRecord<?, ?> consumerRecord, Exception e) {
            final String topic = consumerRecord.topic();
            log.error("Exception occurred while consuming payload from topic : {}, partition : {}",
                    topic,
                    consumerRecord.partition(),
                    e
            );
            log.info("Retrying on topic : {}", topic);
        }
    }
}
