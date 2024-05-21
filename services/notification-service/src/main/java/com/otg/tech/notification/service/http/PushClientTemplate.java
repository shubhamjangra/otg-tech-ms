package com.otg.tech.notification.service.http;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Notification;
import com.otg.tech.notification.domain.entity.NotificationEvent;
import com.otg.tech.notification.domain.response.cache.ProviderCacheResponse;
import com.otg.tech.notification.service.http.exceptions.HttpClientException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import static com.otg.tech.notification.constant.NotificationConstants.USER_ID;
import static com.otg.tech.util.commons.ValidationUtils.isStringEmptyOrNull;

@Getter
@Slf4j
@RequiredArgsConstructor
public abstract class PushClientTemplate implements ProviderHttpClient {

    private static final FirebaseMessaging FIREBASE_MESSAGING = FirebaseMessaging.getInstance();
    private final ProviderCacheResponse provider;

    public final void execute(Message message, NotificationEvent event, ProviderCacheResponse provider) {
        log.info("Inside execute :: push notification");
        Notification notification = Notification.builder()
                .setBody(message.body())
                .build();
        final var fcmToken = event.getEventData().get("fcmToken").toString();
        com.google.firebase.messaging.Message fireBaseMessage = com.google.firebase.messaging.Message.builder()
                .setNotification(notification)
                .setToken(isStringEmptyOrNull(fcmToken) ? provider.name() : fcmToken)
                .build();
        try {
            String response = FIREBASE_MESSAGING.send(fireBaseMessage);
            log.info("Push notification sent successfully, response : {}", response);
        } catch (Exception e) {
            var crn = event.getCustomerData().get(USER_ID);
            log.error("Exception while sending {} to user {}", getCommunicationType(), crn, e);
            if (e instanceof HttpClientException hce) {
                throw hce;
            }
            throw new HttpClientException(
                    String.format("Exception while sending %s to user %s", getCommunicationType(), crn), e);
        }
    }

    abstract String getCommunicationType();

}
