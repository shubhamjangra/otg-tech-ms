package com.otg.tech.notification.config;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Ticker;
import lombok.AllArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.otg.tech.notification.constant.CacheKeys.FIND_RULE_CACHE;

@Configuration
@AllArgsConstructor
@SuppressWarnings("unused")
public class NotificationCaffeineConfig {

    private final CacheSpecConfig cacheConfig;

    @Bean
    public CacheManager cacheManagerTicker(Ticker ticker) {
        List<Cache> caches = new ArrayList<>();
        caches.add(
                this.buildCache(ticker,
                        Long.valueOf(cacheConfig.getNotificationMaxSize()),
                        Long.valueOf(cacheConfig.getNotificationDuration())));

        SimpleCacheManager cacheManager = new SimpleCacheManager();
        cacheManager.setCaches(caches);
        return cacheManager;
    }

    private CaffeineCache buildCache(Ticker ticker, Long maxSize, Long ttl) {

        Caffeine<Object, Object> cacheBuilder = Caffeine.newBuilder();
        if (ttl != null && ttl > 0) {
            cacheBuilder.expireAfterWrite(ttl, TimeUnit.MINUTES).expireAfterAccess(ttl,
                    TimeUnit.MINUTES);
        }
        if (maxSize != null && maxSize > 0) {
            cacheBuilder.maximumSize(maxSize);
        }
        cacheBuilder.weakKeys().softValues().ticker(ticker);

        return new CaffeineCache(FIND_RULE_CACHE, cacheBuilder.build());
    }

    @Bean
    public Ticker ticker() {
        return Ticker.systemTicker();
    }
}
