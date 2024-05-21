package com.otg.tech.notification.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.otg.tech.notification.AbstractIntegrationTest;
import com.otg.tech.notification.domain.entity.Channel;
import com.otg.tech.notification.domain.entity.Notification;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.entity.Provider;
import com.otg.tech.notification.domain.entity.ProviderConfig;
import com.otg.tech.notification.domain.entity.Rule;
import com.otg.tech.notification.domain.entity.Template;
import com.otg.tech.notification.domain.enums.ChannelType;
import com.otg.tech.notification.domain.enums.ConfigDataType;
import com.otg.tech.notification.domain.enums.NotificationReadStatus;
import com.otg.tech.notification.domain.request.CustomerRequest;
import com.otg.tech.notification.domain.request.NotificationReadStatusRequest;
import com.otg.tech.notification.domain.request.NotificationRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

import static com.otg.tech.commons.ApiResponse.Status.ACCEPTED;
import static com.otg.tech.commons.ApiResponse.Status.SUCCESS;
import static com.otg.tech.notification.domain.enums.NotificationEventStatus.ENQUEUED;
import static com.otg.tech.notification.domain.enums.NotificationEventStatus.SUCCESSFULLY_PROCESSED;
import static com.otg.tech.notification.domain.enums.NotificationReadStatus.UNREAD;
import static com.otg.tech.notification.domain.response.cache.ProviderCacheResponse.getProviderCacheResponses;
import static com.otg.tech.notification.domain.response.cache.RuleCacheResponse.getRuleCacheResponse;
import static com.otg.tech.notification.domain.response.cache.TemplateCacheResponse.getTemplateCacheResponse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class NotificationControllerTests extends AbstractIntegrationTest {

    @BeforeEach
    void tearDown() {
        this.notificationRepository.deleteAll();
        this.notificationEventRepository.deleteAll();
        this.templateRepository.deleteAll();
    }

    @Test
    void return_the_same_event_if_exists_with_idempotency_key() throws Exception {
        var idempotencyKey = UUID.randomUUID().toString();
        var existingEvent = this.notificationEventRepository.save(
                newNotificationEvent(idempotencyKey,
                        Map.of(), Map.of()));
        String requestJson = toJson(newNotificationRequest(idempotencyKey));

        this.mockMvc.perform(post("/api/notification-service/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(SUCCESS.getStatus()))
                .andExpect(jsonPath("$.data.id").value(existingEvent.getId()))
                .andExpect(jsonPath("$.data.idempotencyKey").value(idempotencyKey))
                .andExpect(jsonPath("$.data.status").value(existingEvent.getNotificationEventStatus().name()));
    }

    @Test
    void create_a_new_event_and_return_accepted_response_created_idempotency_key() throws Exception {
        String requestJson = toJson(newNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.idempotencyKey").isNotEmpty())
                .andExpect(jsonPath("$.data.status").value(ENQUEUED.name()))
                .andExpect(jsonPath("$.data.submittedAt").isNotEmpty());
    }

    @Test
    void create_a_new_event_with_specified_idempotency_key_and_return_accepted_response() throws Exception {
        var idempotencyKey = UUID.randomUUID().toString();
        String requestJson = toJson(newNotificationRequest(idempotencyKey));
        this.mockMvc.perform(post("/api/notification-service/notifications/send")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data.id").isNotEmpty())
                .andExpect(jsonPath("$.data.idempotencyKey").value(idempotencyKey))
                .andExpect(jsonPath("$.data.status").value(ENQUEUED.name()))
                .andExpect(jsonPath("$.data.submittedAt").isNotEmpty());
    }

    @Test
    void should_return_list_of_user_notifications() throws Exception {
        this.templateRepository.save(createAppOtpTemplate());
        this.templateRepository.save(createWebOtpTemplate());
        this.notificationRepository.save(createNotification());

        String requestJson = toJson(createGetNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data[0].readStatus").value(UNREAD.toString()))
                .andExpect(jsonPath("$.data[0].eventType").isNotEmpty())
                .andExpect(jsonPath("$.data[0].subject").isNotEmpty())
                .andExpect(jsonPath("$.data[0].body").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdDate").isNotEmpty());
    }

    @Test
    void should_return_list_of_all_user_notifications() throws Exception {
        this.templateRepository.save(createAppOtpTemplate());
        this.templateRepository.save(createWebOtpTemplate());
        this.notificationRepository.save(createNotification());

        String requestJson = toJson(createGetNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/new-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data[0].readStatus").value(UNREAD.toString()))
                .andExpect(jsonPath("$.data[0].eventType").isNotEmpty())
                .andExpect(jsonPath("$.data[0].subject").isNotEmpty())
                .andExpect(jsonPath("$.data[0].body").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdDate").isNotEmpty());
    }

    @Test
    void should_return_list_of_unread_notifications() throws Exception {
        this.templateRepository.save(createAppOtpTemplate());
        this.templateRepository.save(createWebOtpTemplate());
        this.notificationRepository.save(createNotification());

        String requestJson = toJson(createGetNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data[0].readStatus").value(NotificationReadStatus.UNREAD.toString()))
                .andExpect(jsonPath("$.data[0].eventType").isNotEmpty())
                .andExpect(jsonPath("$.data[0].subject").isNotEmpty())
                .andExpect(jsonPath("$.data[0].body").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdDate").isNotEmpty());
    }

    @Test
    void should_return_list_of_all_unread_notifications() throws Exception {
        this.templateRepository.save(createAppOtpTemplate());
        this.templateRepository.save(createWebOtpTemplate());
        this.notificationRepository.save(createNotification());

        String requestJson = toJson(createGetNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/list-all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data[0].readStatus").value(NotificationReadStatus.UNREAD.toString()))
                .andExpect(jsonPath("$.data[0].eventType").isNotEmpty())
                .andExpect(jsonPath("$.data[0].subject").isNotEmpty())
                .andExpect(jsonPath("$.data[0].body").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdDate").isNotEmpty());
    }

    @Test
    void should_mark_unread_notifications_as_read_when_returning_user_notifications_list() throws Exception {
        this.templateRepository.save(createAppOtpTemplate());
        this.templateRepository.save(createWebOtpTemplate());
        this.notificationRepository.save(createNotification());

        String requestJson = toJson(createGetNotificationRequest());
        this.mockMvc.perform(post("/api/notification-service/notifications/list")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data[0].readStatus").value(NotificationReadStatus.UNREAD.toString()))
                .andExpect(jsonPath("$.data[0].eventType").isNotEmpty())
                .andExpect(jsonPath("$.data[0].subject").isNotEmpty())
                .andExpect(jsonPath("$.data[0].body").isNotEmpty())
                .andExpect(jsonPath("$.data[0].createdDate").isNotEmpty());

        this.mockMvc.perform(post("/api/notification-service/notifications/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.error").isEmpty())
                .andExpect(jsonPath("$.status").value(ACCEPTED.getStatus()))
                .andExpect(jsonPath("$.data").isEmpty());
    }

    private NotificationReadStatusRequest createGetNotificationRequest() {
        return new NotificationReadStatusRequest("12345098765");
    }

    private NotificationEvent newNotificationEvent(String idempotencyKey,
                                                   Map<String, Object> customerData,
                                                   Map<String, Object> eventData) {
        return new NotificationEvent("test-event",
                idempotencyKey,
                SUCCESSFULLY_PROCESSED,
                customerData,
                eventData);
    }

    private NotificationRequest newNotificationRequest() {
        return newNotificationRequest(null);
    }

    private NotificationRequest newNotificationRequest(String idempotencyKey) {
        return new NotificationRequest("test-event",
                idempotencyKey,
                new CustomerRequest("u123", "9898989891", "test@test.com", "en", "9999-01-05 08:04:11"),
                Map.of("otpValue", "123456"));
    }

    private Notification createNotification() {
        var idempotencyKey = UUID.randomUUID().toString();
        var existingEvent = this.notificationEventRepository.save(
                newNotificationEvent(idempotencyKey,
                        Map.of("userId", "12345098765",
                                "language", "en", "email", "abc@test.com"),
                        Map.of("otpValue", "123456")));

        Channel channel = createChannel();
        Template template = createTemplate();
        final var rule = createRule(channel, template);
        return new Notification(existingEvent, getTemplateCacheResponse(template),
                getProviderCacheResponses(Collections.singletonList(createEmailProvider())).get(0),
                "Test Template", "Test Response",
                getRuleCacheResponse(rule, rule.getChannel(), channel.getProviders(), template));
    }

    private Provider createEmailProvider() {
        Provider provider = Provider.builder().name("OTG_EMAIL").build();
        provider.addConfig(new ProviderConfig("apiUrl",
                "/Email",
                ConfigDataType.STRING));
        provider.addConfig(new ProviderConfig("source", "TEST", ConfigDataType.STRING));
        provider.addConfig(new ProviderConfig("channel", "AU", ConfigDataType.STRING));
        return provider;
    }

    private Template createTemplate() {
        return Template.builder()
                .bodyTemplate("Test Template".getBytes())
                .templateCode("T001")
                .language("en")
                .subject("Sending notification")
                .build();
    }

    private Channel createChannel() {
        return Channel.builder()
                .channelType(ChannelType.PUSH_NOTIFICATION)
                .build();
    }

    private Rule createRule(Channel channel, Template template) {
        return Rule.builder()
                .channel(channel)
                .template(template)
                .language("en")
                .eventType("APP_OTP_EMAIL")
                .build();
    }

    private Template createAppOtpTemplate() {
        String templateBody =
                "A secret code only between us. Use OTP 123456 for Login to Merchant Banking App. Valid till 00:02:58. - Bank";
        return Template.builder()
                .templateCode("t2")
                .language("en")
                .bodyTemplate(templateBody.getBytes())
                .subject("")
                .build();
    }

    private Template createWebOtpTemplate() {
        String templateBody =
                "A secret code only between us. Use OTP 123456 for Login to Merchant Banking Web. Valid till 00:02:58. - Bank";
        return Template.builder()
                .templateCode("t2")
                .language("en")
                .bodyTemplate(templateBody.getBytes())
                .subject("")
                .build();
    }

    private String toJson(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to write as JSON", e);
        }
    }
}
