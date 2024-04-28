package com.otg.tech.redis.config;

import com.otg.tech.redis.config.prop.RedisProperty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.util.StringUtils;

@Configuration
@ConditionalOnProperty(name = "redis.cache-enable", havingValue = "true")
@SuppressWarnings("unused")
@EnableCaching
@RequiredArgsConstructor
@Slf4j
public class RedisConfiguration {
    private final RedisProperty redisProperty;

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        final RedisTemplate<String, Object> redisTemplate =
                new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new GenericToStringSerializer<>(String.class));
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
        redisTemplate.setValueSerializer(new Jackson2JsonRedisSerializer<>(Object.class));
        redisTemplate.setConnectionFactory(lettuceFactory());
        return redisTemplate;
    }

    private RedisStandaloneConfiguration redisConnectionProperty() {
        log.info("Initiating redis connection, host : {}, port : {}",
                this.redisProperty.getConnectorHost(),
                this.redisProperty.getConnectorPort()
        );

        RedisStandaloneConfiguration redisProperties = new RedisStandaloneConfiguration();
        redisProperties.setHostName(this.redisProperty.getConnectorHost());
        redisProperties.setPort(this.redisProperty.getConnectorPort());
        if (StringUtils.hasLength(this.redisProperty.getConnectorUsername()))
            redisProperties.setUsername(this.redisProperty.getConnectorUsername());
        if (StringUtils.hasLength(this.redisProperty.getConnectorPassword()))
            redisProperties.setPassword(this.redisProperty.getConnectorPassword());

        return redisProperties;
    }

    private LettuceConnectionFactory lettuceFactory() {
        RedisStandaloneConfiguration configuration = redisConnectionProperty();

        LettuceClientConfiguration clientConfiguration;
        if (redisProperty.isConnectorSSLFlag()) {
            clientConfiguration = LettuceClientConfiguration.builder()
                    .useSsl()
                    .build();
        } else {
            clientConfiguration = LettuceClientConfiguration.builder()
                    .build();
        }

        LettuceConnectionFactory factory = new LettuceConnectionFactory(configuration, clientConfiguration);
        factory.afterPropertiesSet();
        return factory;
    }
}
