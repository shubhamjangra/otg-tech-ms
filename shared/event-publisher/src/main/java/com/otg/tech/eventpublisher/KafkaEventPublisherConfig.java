package com.otg.tech.eventpublisher;

import com.otg.tech.events.Event;
import org.apache.kafka.clients.CommonClientConfigs;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.config.SslConfigs;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

@Configuration
@PropertySource("classpath:/event-publisher.properties")
@EnableAsync
@ConditionalOnProperty(name = "kafka.event.publisher.enabled", havingValue = "true")
@SuppressWarnings("unused")
public class KafkaEventPublisherConfig {

    @Value("${spring.kafka.producer.bootstrap-servers}")
    protected String bootstrapServers;
    @Value("${spring.kafka.producer.properties.max.block.ms:10000}")
    protected int maxBlockMs;
    @Value("${spring.kafka.producer.security.protocol:SSL}")
    protected String protocol;
    @Value("${spring.kafka.ssl.trust-store-location:trust-store}")
    protected String trustStoreLocation;
    @Value("${spring.kafka.producer.security.enabled:true}")
    private boolean isSSLEnabled;
    @Value("${spring.kafka.producer.properties.use-trust:false}")
    private boolean useTrustFile;
    @Value("${spring.kafka.producer.idempotency-enabled:true}")
    private boolean producerIdempotency;
    @Value("${spring.kafka.producer.delivery.timeout.ms:120000}")
    private int deliveryTimeout;
    @Value("${spring.kafka.producer.max-in-flight-requests-per-connection:3}")
    private int maxInFlightConnection;
    @Value("${spring.kafka.producer.acks:all}")
    private String acks;
    @Value("${spring.kafka.producer.retries:3}")
    private int retryCount;
    @Value("${spring.kafka.producer.properties.retry.backoff.ms:100}")
    private int retryBackOff;

    @Bean(name = "eventPublisherThreadExecutor")
    public Executor threadPoolTaskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(1000);
        executor.setThreadNamePrefix("Event_Publisher_Executor@");
        executor.initialize();
        return executor;
    }

    @Bean
    public ProducerFactory<String, Event<?>> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerConfigs());
    }

    @Bean
    @Profile("!test")
    public Map<String, Object> producerConfigs() {
        Map<String, Object> props = new HashMap<>();
        props.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.MAX_BLOCK_MS_CONFIG, maxBlockMs);
        props.put(ProducerConfig.RETRY_BACKOFF_MS_CONFIG, retryBackOff);
        props.put(ProducerConfig.ENABLE_IDEMPOTENCE_CONFIG, producerIdempotency);
        props.put(ProducerConfig.DELIVERY_TIMEOUT_MS_CONFIG, deliveryTimeout);
        props.put(ProducerConfig.MAX_IN_FLIGHT_REQUESTS_PER_CONNECTION, maxInFlightConnection);
        props.put(ProducerConfig.ACKS_CONFIG, acks);
        props.put(ProducerConfig.RETRIES_CONFIG, retryCount);
        if (isSSLEnabled)
            props.put(CommonClientConfigs.SECURITY_PROTOCOL_CONFIG, protocol);
        if (useTrustFile)
            props.put(SslConfigs.SSL_TRUSTSTORE_LOCATION_CONFIG, trustStoreLocation);

        return props;
    }

    @Bean
    public KafkaTemplate<String, Event<?>> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
