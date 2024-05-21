package com.otg.tech.notification.service;

import com.otg.tech.exception.ApplicationException;
import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.entity.Provider;
import com.otg.tech.notification.domain.entity.ProviderConfig;
import com.otg.tech.notification.domain.request.GetProviderRequest;
import com.otg.tech.notification.domain.request.ProviderConfigRequest;
import com.otg.tech.notification.domain.request.ProviderRequest;
import com.otg.tech.notification.domain.response.ProviderConfigResponse;
import com.otg.tech.notification.domain.response.ProviderListResponse;
import com.otg.tech.notification.domain.response.ProviderResponse;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import com.otg.tech.notification.repository.ChannelRepository;
import com.otg.tech.notification.repository.ProviderRepository;
import com.otg.tech.notification.service.http.Message;
import com.otg.tech.notification.service.http.ProviderHttpClient;
import com.otg.tech.notification.service.http.ProviderHttpClientFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1001;
import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY1002;

@Service
@RequiredArgsConstructor
public class ProviderService {

    private final ProviderHttpClientFactory providerHttpClientFactory;
    private final ChannelRepository channelRepository;
    private final ProviderRepository providerRepository;

    public void sendNotification(ProviderCacheResponse provider, NotificationEvent event, Message message) {
        ProviderHttpClient providerHttpClient = providerHttpClientFactory.getProviderHttpClient(provider);
        providerHttpClient.execute(message, event, provider);
    }

    public ProviderResponse createProvider(ProviderRequest providerRequest) {
        Channel channel = this.channelRepository.findById(providerRequest.channelId())
                .orElseThrow(() -> new ApplicationException(NOTIFY1001.getHttpStatus(),
                        NOTIFY1001.getCode(), NOTIFY1001.getMessage()));
        Provider response = providerRepository.save(new Provider(providerRequest.name(), channel));
        return ProviderResponse.toProvider(response, channel.getId());
    }

    public ProviderResponse getProvider(GetProviderRequest getProviderRequest) {
        Provider provider = this.providerRepository.findById(getProviderRequest.providerId())
                .orElseThrow(() -> new ApplicationException(NOTIFY1002.getHttpStatus(),
                        NOTIFY1002.getCode(), NOTIFY1002.getMessage()));
        return ProviderResponse.toProvider(provider, provider.getChannel().getId());
    }

    public List<ProviderListResponse> listProviders() {
        List<Provider> providerList = this.providerRepository.findAll();
        if (providerList.isEmpty())
            return Collections.emptyList();
        return providerList.stream().map(provider -> ProviderListResponse.toProviders(
                provider, provider.getChannel().getId())).toList();
    }

    public List<ProviderConfigResponse> addProviderConfig(ProviderConfigRequest providerConfigRequest) {
        Provider provider = this.providerRepository.findById(providerConfigRequest.providerId())
                .orElseThrow(() -> new ApplicationException(NOTIFY1002.getHttpStatus(),
                        NOTIFY1002.getCode(), NOTIFY1002.getMessage()));
        ProviderConfig providerConfig = new ProviderConfig(providerConfigRequest.key(), providerConfigRequest.value(),
                providerConfigRequest.configDataType());
        provider.addConfig(providerConfig);
        Provider response = providerRepository.save(provider);
        return response.getProviderConfigs().stream().map(ProviderConfigResponse::toProviderConfig)
                .toList();
    }
}
