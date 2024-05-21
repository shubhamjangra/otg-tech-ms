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

import java.net.http.HttpClient;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import static com.otg.tech.notification.constant.NotificationConstants.ATTACHMENT;
import static com.otg.tech.notification.constant.NotificationConstants.BCC;
import static com.otg.tech.notification.constant.NotificationConstants.CHANNEL;
import static com.otg.tech.notification.constant.NotificationConstants.CHANNEL_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.DATA_CONTENT_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.EMAIL;
import static com.otg.tech.notification.constant.NotificationConstants.EMAIL_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.FAILED_CODE;
import static com.otg.tech.notification.constant.NotificationConstants.FAILED_DESC;
import static com.otg.tech.notification.constant.NotificationConstants.FILE_DATA_CONTENT;
import static com.otg.tech.notification.constant.NotificationConstants.FILE_MIME_TYPE;
import static com.otg.tech.notification.constant.NotificationConstants.FILE_NAME;
import static com.otg.tech.notification.constant.NotificationConstants.FILE_NAME_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.MIME_TYPE_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.PRIORITY;
import static com.otg.tech.notification.constant.NotificationConstants.REQUEST_ID;
import static com.otg.tech.notification.constant.NotificationConstants.SUBJECT;
import static com.otg.tech.notification.constant.NotificationConstants.TEXT;
import static com.otg.tech.notification.constant.NotificationConstants.TO;
import static com.otg.tech.util.commons.ValidationUtils.isStringEmptyOrNull;


public class EmailHttpClient extends HttpClientTemplate {

    public EmailHttpClient(ProviderCacheResponse provider, ObjectMapper objectMapper, HttpClient httpClient) {
        super(provider, objectMapper, httpClient);
    }

    String createBody(Message message, NotificationEvent event, ProviderCacheResponse provider)
            throws JsonProcessingException {
        final String customerEmail = (String) event.getCustomerData().get(EMAIL_KEY);
        final String channel = provider.getConfig(CHANNEL_KEY);
        final String fileName = (String) event.getEventData().get(FILE_NAME_KEY);
        final String mimeType = (String) event.getEventData().get(MIME_TYPE_KEY);
        final String dataContent = (String) event.getEventData().get(DATA_CONTENT_KEY);

        return getObjectMapper().writeValueAsString(Map.of(
                ATTACHMENT, Map.of(
                        FILE_NAME, isStringEmptyOrNull(fileName) ? "" : fileName,
                        FILE_MIME_TYPE, isStringEmptyOrNull(mimeType) ? "" : mimeType,
                        FILE_DATA_CONTENT, isStringEmptyOrNull(dataContent) ? "" : dataContent),
                REQUEST_ID, String.valueOf(UUID.randomUUID()).replace("-", ""),
                CHANNEL, channel,
                TO, customerEmail,
                BCC, "",
                SUBJECT, message.subject(),
                TEXT, message.body(),
                PRIORITY, "")
        );
    }

    @Override
    String getCommunicationType() {
        return EMAIL;
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
