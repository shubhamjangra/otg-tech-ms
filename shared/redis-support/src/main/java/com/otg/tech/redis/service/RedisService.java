package com.otg.tech.redis.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@ConditionalOnBean(RedisTemplate.class)
@SuppressWarnings("unused")
public class RedisService {

    private static final String NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE = "NUll/Empty key is not allowed in cache.";
    private static final String NULL_KEY_IS_NOT_ALLOWED = "Null key is not allowed!";
    private static final int DEFAULT_TIME = 48;
    private static final TimeUnit DEFAULT_TIMEUNIT = TimeUnit.HOURS;
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final String EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER =
            "There's some exception while connecting to redis server.";
    private final RedisTemplate<String, Object> redisTemplate;

    public void save(String key, Object val) {
        save(key, val, DEFAULT_TIME, DEFAULT_TIMEUNIT);
    }

    public Boolean saveIfAbsent(String key, Object val) {
        return saveIfAbsent(key, val, DEFAULT_TIME, DEFAULT_TIMEUNIT);
    }

    public void save(String key, Object val, long expiryTime, TimeUnit timeUnit) {
        log.info("Putting key : {} in redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        log.info("Putting key : {} with expiryTime : {}, and timeUnit : {}",
                key,
                expiryTime,
                timeUnit
        );
        try {
            redisTemplate.opsForValue().set(key, val);
            redisTemplate.expire(key, expiryTime, timeUnit);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
    }

    public void saveIfPresent(String key, Object val, long expiryTime, TimeUnit timeUnit) {
        log.info("Putting key : {} in redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        log.info("Putting key : {} with expiryTime : {}, and timeUnit : {}",
                key,
                expiryTime,
                timeUnit
        );
        try {
            redisTemplate.opsForValue().setIfPresent(key, val, expiryTime, timeUnit);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
    }

    public void saveIfPresent(String key, Object val) {
        log.info("Putting key : {} in redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        try {
            redisTemplate.opsForValue().setIfPresent(key, val);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
    }

    public Boolean saveIfAbsent(String key, Object val, int expiryTime, TimeUnit timeUnit) {
        log.info("Putting key : {} in redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        log.info("Putting key : {} with expiryTime : {}, and timeUnit : {}",
                key,
                expiryTime,
                timeUnit
        );
        try {
            return redisTemplate.opsForValue().setIfAbsent(
                    key,
                    val,
                    expiryTime,
                    timeUnit
            );
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
        return Boolean.TRUE;
    }

    public Boolean isKeyAvailable(String key) {
        log.info("Removing key : {} from redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
        return false;
    }

    public void update(String key, Object val) {
        update(key, val, DEFAULT_TIME, DEFAULT_TIMEUNIT);
    }

    public void update(String key, Object val, int expiryTime, TimeUnit timeUnit) {
        log.info("Updating key : {} in redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        log.info("Updating key : {} with expiryTime : {}, and timeUnit : {}",
                key,
                expiryTime,
                timeUnit
        );
        try {
            redisTemplate.opsForValue().setIfPresent(key, val);
            redisTemplate.expire(key, expiryTime, timeUnit);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
    }

    public <V> Optional<V> get(String key, Class<V> vClass) {
        log.info("Fetching value for key :  {}", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        try {
            Object val = redisTemplate.opsForValue().get(key);
            return readValue(val, vClass);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
        return Optional.empty();
    }

    public Boolean remove(String key) {
        log.info("Removing key : {} from redis", key);
        if (!StringUtils.hasLength(key)) {
            log.error(NULL_EMPTY_KEY_IS_NOT_ALLOWED_IN_CACHE);
            throw new IllegalArgumentException(NULL_KEY_IS_NOT_ALLOWED);
        }
        try {
            return redisTemplate.delete(key);
        } catch (Exception e) {
            log.error(EXCEPTION_WHILE_CONNECTING_TO_REDIS_SERVER, e);
        }
        return false;
    }

    private <V> Optional<V> readValue(Object val, Class<V> vClass) {
        try {
            final String strVal = OBJECT_MAPPER.writeValueAsString(val);
            return Optional.of(OBJECT_MAPPER.readValue(strVal, vClass));
        } catch (Exception e) {
            log.error("Exception in parsing json to value type : {} and error:", vClass.getSimpleName(), e);
            return Optional.empty();
        }
    }
}
