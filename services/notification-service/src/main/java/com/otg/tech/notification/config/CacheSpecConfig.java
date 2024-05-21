package com.otg.tech.notification.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Data
@Configuration
@ConfigurationProperties("caffeine.cache")
@Component
public class CacheSpecConfig {

    protected String notificationMaxSize;
    protected String notificationDuration;
}
