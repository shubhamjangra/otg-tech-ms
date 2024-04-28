package com.otg.tech.redis.config.prop;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class RedisProperty {
    @Value("${redis.connector-port:6379}")
    private int connectorPort;
    @Value("${redis.connector-host:localhost}")
    private String connectorHost;
    @Value("${redis.connector-password:}")
    private String connectorPassword;
    @Value("${redis.connector-username:}")
    private String connectorUsername;
    @Value("${redis.connector-use-ssl:false}")
    private boolean connectorSSLFlag;
}
