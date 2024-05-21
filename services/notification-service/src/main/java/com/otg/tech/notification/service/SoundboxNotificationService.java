package com.otg.tech.notification.service;

import com.amazonaws.services.iot.client.AWSIotConnectionStatus;
import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.otg.tech.events.data.MqttData;
import com.otg.tech.exception.ApplicationException;
import com.otg.tech.notification.util.NonBlockingPublishListener;
import com.otg.tech.util.commons.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.otg.tech.notification.domain.enums.ErrorExceptionCodes.NOTIFY2001;
import static com.otg.tech.notification.util.MqttKeyStoreUtil.KeyStorePasswordPair;
import static com.otg.tech.notification.util.MqttKeyStoreUtil.getKeyStorePasswordPair;

@Service
@Slf4j
@RequiredArgsConstructor
public class SoundboxNotificationService {

    private static final AWSIotQos TOPIC_QOS = AWSIotQos.QOS0;
    @Value("${mqtt.end.point.url:acr9w2x5aszgv-ats.iot.ap-south-1.amazonaws.com}")
    protected String clientEndpoint;
    @Value("${mqtt.client.id:QR_Msg_01}")
    protected String clientId;
    @Value("${mqtt.certificate.file.path}")
    protected String certificateFilePath;
    @Value("${mqtt.private.key.file.path}")
    protected String privateKeyFilePath;
    @Value("${mqtt.key.algorithm:RSA}")
    protected String keyAlgorithm;
    private AWSIotMqttClient awsIotClient;

    public void sendNotification(MqttData.CustomerRequest customerRequest, MqttData.Payload payload) {
        if (awsIotClient == null) {
            log.info("-----Connecting to AWS IOT Core-----");
            initClient();
        }
        try {
            if (awsIotClient.getConnectionStatus() != AWSIotConnectionStatus.CONNECTED) {
                awsIotClient.connect();
            }
            String payloadData = Utils.toJsonString(payload);
            List<String> topics = customerRequest.getDeviceIds();
            for (String topic : topics) {
                AWSIotMessage message = new NonBlockingPublishListener(topic, TOPIC_QOS, payloadData);

                awsIotClient.publish(message);
                log.info("message published to aws mqtt topic {}, payload {}", topic, payload);
            }
        } catch (AWSIotException e) {
            log.error("mqtt notification publish failed {}", e.getMessage());
            throw new ApplicationException(NOTIFY2001.getHttpStatus(), NOTIFY2001.getCode(),
                    NOTIFY2001.getMessage());
        }
    }

    private void initClient() {
        if (awsIotClient == null && certificateFilePath != null && privateKeyFilePath != null) {
            KeyStorePasswordPair pair = getKeyStorePasswordPair(certificateFilePath, privateKeyFilePath, keyAlgorithm);
            if (pair != null) {
                awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, pair.getKeyStore(),
                        pair.getKeyPassword());
            }
        }
        if (awsIotClient == null) {
            awsIotClient = new AWSIotMqttClient(clientEndpoint, clientId, null);
        }
    }
}
