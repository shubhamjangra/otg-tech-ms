package com.otg.tech.notification.service.http;

import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;

public interface ProviderHttpClient {

    void execute(Message message, NotificationEvent event, ProviderCacheResponse provider);
}
