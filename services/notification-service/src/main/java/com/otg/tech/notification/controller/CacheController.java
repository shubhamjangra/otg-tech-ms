package com.otg.tech.notification.controller;

import com.otg.tech.commons.ApiResponse;
import com.otg.tech.notification.domain.response.cache.RuleCacheResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

import static com.otg.tech.notification.constant.CacheKeys.FIND_RULE_CACHE;
import static com.otg.tech.notification.constant.CacheKeys.FIND_RULE_KEY;

@RestController
@RequestMapping(path = "/cache")
@SuppressWarnings("unused")
public class CacheController {

    protected final CacheManager cacheManager;

    @Autowired
    public CacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @SuppressWarnings("unchecked")
    private static <T> Class<T> generifyClass() {
        return (Class<T>) List.class;
    }

    /**
     * @return success and invalidate rule cache list
     * @deprecated to be removed for production
     */
    @PostMapping(path = "/invalidate")
    @Deprecated(forRemoval = true)
    public ApiResponse<Void> invalidateAll() {
        Objects.requireNonNull(cacheManager.getCache(FIND_RULE_CACHE)).clear();
        return ApiResponse.success();
    }

    /**
     * @return success and fetch rule cache list
     * @deprecated to be removed for production
     */
    @PostMapping(path = "/get")
    @Deprecated(forRemoval = true)
    public ApiResponse<List<RuleCacheResponse>> getByCacheNameAndKey() {
        return ApiResponse.success(Objects.requireNonNull(cacheManager.getCache(FIND_RULE_CACHE))
                .get(FIND_RULE_KEY, generifyClass()));
    }
}