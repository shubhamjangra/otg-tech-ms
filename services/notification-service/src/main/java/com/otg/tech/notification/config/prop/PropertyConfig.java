package com.otg.tech.notification.config.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@Data
@PropertySource("classpath:/event-consumer.properties")
public class PropertyConfig {

    @Value("${spring.kafka.consumer.bootstrap-servers}")
    private String kafkaConsumerBootstrapServer;
    @Value("${spring.kafka.consumer.group-id}")
    private String kafkaConsumerGroupId;
    @Value("${spring.kafka.consumer.auto-offset-reset}")
    private String kafkaConsumerAutoOffset;
    @Value("${spring.kafka.consumer.security.protocol}")
    private String kafkaConsumerSecurityProtocol;
    @Value("${spring.kafka.consumer.client-id}")
    private String kafkaConsumerClientId;
    @Value("${spring.kafka.consumer.request.timeout.ms}")
    private int kafkaConsumerRequestTimeout;
    @Value("${spring.kafka.consumer.heartbeat.interval.ms}")
    private int kafkaConsumerHeartbeatInterval;
    @Value("${spring.kafka.consumer.max.poll.interval.ms}")
    private int kafkaConsumerMaxPollInterval;
    @Value("${spring.kafka.consumer.max.poll.records}")
    private int kafkaConsumerMaxPollRecord;
    @Value("${spring.kafka.consumer.session.timeout.ms}")
    private int kafkaConsumerSessionTimeout;
    @Value("${spring.kafka.consumer.back-off.ms:4000}")
    private int backOffMs;
    @Value("${spring.kafka.consumer.retry-count:5}")
    private int retryCount;
    @Value("${spring.kafka.consumer.max-retry-count:10}")
    private int maxRetryCount;

    @Value("${server.deployed-environment}")
    private String serverDeployedEnv;
}
