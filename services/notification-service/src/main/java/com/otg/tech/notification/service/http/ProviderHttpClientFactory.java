package com.otg.tech.notification.service.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.net.http.HttpClient;

import static com.otg.tech.notification.constant.NotificationConstants.OTG_EMAIL;
import static com.otg.tech.notification.constant.NotificationConstants.OTG_PUSH;
import static com.otg.tech.notification.constant.NotificationConstants.OTG_SMS;

@Component
@RequiredArgsConstructor
public class ProviderHttpClientFactory {

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper;

    public ProviderHttpClient getProviderHttpClient(ProviderCacheResponse provider) {
        return switch (provider.name()) {
            case OTG_EMAIL -> new EmailHttpClient(provider, objectMapper, httpClient);
            case OTG_SMS -> new SmsHttpClient(provider, objectMapper, httpClient);
            case OTG_PUSH -> new FcmPushClient(provider);
            default -> throw new IllegalArgumentException(
                    String.format("No provider http client found with name %s", provider.name()));
        };
    }
}
