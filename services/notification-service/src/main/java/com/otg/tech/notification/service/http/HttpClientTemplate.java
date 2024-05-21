package com.otg.tech.notification.service.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import com.otg.tech.notification.service.http.exceptions.HttpClientException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.temporal.ChronoUnit;

import static com.otg.tech.notification.constant.NotificationConstants.API_KEY;
import static com.otg.tech.notification.constant.NotificationConstants.API_URL;
import static com.otg.tech.notification.constant.NotificationConstants.API_USER;
import static com.otg.tech.notification.constant.NotificationConstants.USER_ID;
import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@AllArgsConstructor
abstract class HttpClientTemplate implements ProviderHttpClient {

    @Getter
    private final ProviderCacheResponse provider;
    @Getter
    private final ObjectMapper objectMapper;
    private final HttpClient httpClient;

    @Override
    public final void execute(Message message, NotificationEvent event, ProviderCacheResponse provider) {
        try {
            String body = createBody(message, event, provider);
            HttpRequest httpRequest = createHttpRequest(provider, body);
            log.info("Request for event id: {} , url : {}, headers : {}, body : {}", event.getId(), httpRequest.uri(),
                    httpRequest.headers().toString(), body);
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            int statusCode = response.statusCode();
            log.info("Received response with HTTP status code {}", statusCode);
            String jsonResponse = response.body();
            log.info("Received response body {} for event id {}", jsonResponse, event.getId());
            if (!HttpStatus.valueOf(statusCode).is2xxSuccessful()) {
                throw new HttpClientException(String.format("Received response code %s when sending %s for event %s",
                        statusCode, getCommunicationType(), event.getId()));
            }
            handleResponse(jsonResponse);
        } catch (InterruptedException e) {
            log.error("Exception while sending {} to user {}", getCommunicationType(),
                    event.getCustomerData().get(USER_ID), e);
            Thread.currentThread().interrupt(); // for SonarQube
            throw new HttpClientException(String.format("Exception while sending %s to user %s", getCommunicationType(),
                    event.getCustomerData().get(USER_ID)), e);
        } catch (Exception e) {
            var userId = event.getCustomerData().get(USER_ID);
            log.error("Exception while sending {} to user {}", getCommunicationType(), userId, e);
            if (e instanceof HttpClientException hce) {
                throw hce;
            }
            throw new HttpClientException(String.format("Exception while sending %s to user %s", getCommunicationType(),
                    userId), e);
        }
    }

    abstract String createBody(Message message, NotificationEvent event, ProviderCacheResponse provider)
            throws JsonProcessingException;

    abstract String getCommunicationType();

    abstract void handleResponse(String jsonResponse) throws HttpClientException;

    HttpRequest createHttpRequest(ProviderCacheResponse provider, String body) {
        final String apiUrl = provider.getConfig(API_URL);
        final String apiUser = provider.getConfig(API_USER);
        final String apiKey = provider.getConfig(API_KEY);
        return HttpRequest
                .newBuilder()
                .uri(URI.create(apiUrl))
                .timeout(Duration.of(5, ChronoUnit.SECONDS))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(API_USER, apiUser)
                .header(API_KEY, apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
    }
}
