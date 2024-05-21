package com.otg.tech.notification.listener;

import com.otg.tech.events.AppTopic;
import com.otg.tech.events.Event;
import com.otg.tech.events.data.MqttData;
import com.otg.tech.notification.service.SoundboxNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.otg.tech.constant.CommonConstant.HYPHEN;

@Component
@Slf4j
@RequiredArgsConstructor
@Transactional
@ConditionalOnProperty(name = "kafka.event.consumer.enabled", havingValue = "true")
@SuppressWarnings({"FileTabCharacter", "unused"})
public class MqttKafkaListener {

    private final SoundboxNotificationService soundboxNotificationService;

    @KafkaListener(topics = AppTopic.Constants.MQTT_NOTIFICATION_TOPIC + HYPHEN
            + "#{'${server.deployed-environment}'.toLowerCase()}",
            groupId = "${spring.kafka.consumer.group-id}",
            containerFactory = "notificationKafkaListenerFactory")
    public void consume(ConsumerRecord<String, Event<MqttData>> payload) {

        log.info("MqttData consumed from topic for SB notification : {}", payload.value());
        MqttData mqttData = payload.value().typedData(MqttData.class);

        soundboxNotificationService.sendNotification(mqttData.getCustomerRequest(), mqttData.getPayload());
    }
}
