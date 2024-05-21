package com.otg.tech.notification.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import com.otg.tech.notification.service.http.exceptions.HttpClientException;
import lombok.extern.slf4j.Slf4j;

import java.net.http.HttpClient;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.otg.tech.notification.constant.NotificationConstants.BANK;
import static com.otg.tech.notification.constant.NotificationConstants.CHANNEL;
import static com.otg.tech.notification.constant.NotificationConstants.CHANNEL_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.CONTENT_TYPE;
import static com.otg.tech.notification.constant.NotificationConstants.CONTENT_TYPE_VALUE;
import static com.otg.tech.notification.constant.NotificationConstants.FAILED_CODE;
import static com.otg.tech.notification.constant.NotificationConstants.FAILED_DESC;
import static com.otg.tech.notification.constant.NotificationConstants.GROUP_ID;
import static com.otg.tech.notification.constant.NotificationConstants.IS_OTP_MESSAGE;
import static com.otg.tech.notification.constant.NotificationConstants.IS_OTP_MESSAGE_VALUE;
import static com.otg.tech.notification.constant.NotificationConstants.LANGUAGE_ID;
import static com.otg.tech.notification.constant.NotificationConstants.LANGUAGE_ID_VALUE;
import static com.otg.tech.notification.constant.NotificationConstants.MESSAGE;
import static com.otg.tech.notification.constant.NotificationConstants.MESSAGE_TEXT;
import static com.otg.tech.notification.constant.NotificationConstants.MESSAGE_TYPE;
import static com.otg.tech.notification.constant.NotificationConstants.MESSAGE_TYPE_VALUE;
import static com.otg.tech.notification.constant.NotificationConstants.MOBILE_NO;
import static com.otg.tech.notification.constant.NotificationConstants.MOBILE_NUMBER;
import static com.otg.tech.notification.constant.NotificationConstants.NATIONAL_OR_INTERNATIONAL;
import static com.otg.tech.notification.constant.NotificationConstants.N_OR_I_VALUE;
import static com.otg.tech.notification.constant.NotificationConstants.REQUEST_ID;
import static com.otg.tech.notification.constant.NotificationConstants.SMS;

@Slf4j
public class SmsHttpClient extends HttpClientTemplate {

    public SmsHttpClient(ProviderCacheResponse provider, ObjectMapper objectMapper, HttpClient httpClient) {
        super(provider, objectMapper, httpClient);
    }

    @Override
    String createBody(Message message, NotificationEvent event, ProviderCacheResponse provider)
            throws JsonProcessingException {
        final String mobileNo = (String) event.getCustomerData().get(MOBILE_NO);
        final String channel = provider.getConfig(CHANNEL_KEY);
        return getObjectMapper().writeValueAsString(Map.of(
                MESSAGE, Map.of(
                        MOBILE_NUMBER, mobileNo,
                        MESSAGE_TEXT, message.body()),
                REQUEST_ID, String.valueOf(UUID.randomUUID()).replace("-", ""),
                CHANNEL, channel,
                GROUP_ID, BANK,
                CONTENT_TYPE, CONTENT_TYPE_VALUE,
                NATIONAL_OR_INTERNATIONAL, N_OR_I_VALUE,
                MESSAGE_TYPE, MESSAGE_TYPE_VALUE,
                IS_OTP_MESSAGE, IS_OTP_MESSAGE_VALUE,
                LANGUAGE_ID, LANGUAGE_ID_VALUE)
        );
    }

    @Override
    String getCommunicationType() {
        return SMS;
    }

    @Override
    void handleResponse(String json) {
        Configuration configuration = Configuration.builder().options(Option.SUPPRESS_EXCEPTIONS).build();
        DocumentContext jsonDoc = JsonPath.parse(json, configuration);
        String statusCode = jsonDoc.read("$.TransactionStatus.ResponseCode", String.class);
        String statusMsg = jsonDoc.read("$.TransactionStatus.ResponseMessage", String.class);
        if (Objects.equals(statusCode, FAILED_CODE) || Objects.equals(statusMsg, FAILED_DESC)) {
            throw new HttpClientException(String.format("Failed to send email. Response received %s", statusCode));
        }
    }
}
