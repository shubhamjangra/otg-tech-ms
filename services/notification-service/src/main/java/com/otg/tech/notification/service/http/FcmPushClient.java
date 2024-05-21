package com.otg.tech.notification.service.http;

import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import lombok.extern.slf4j.Slf4j;

import static com.otg.tech.notification.constant.NotificationConstants.PUSH;

@Slf4j
public class FcmPushClient extends PushClientTemplate {

    public FcmPushClient(ProviderCacheResponse provider) {
        super(provider);
    }

    @Override
    String getCommunicationType() {
        return PUSH;
    }
}
