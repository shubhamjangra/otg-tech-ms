/*
 * Copyright 2016 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.otg.tech.notification.util;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import lombok.extern.slf4j.Slf4j;

/**
 * This class extends {@link AWSIotMessage} to provide customized handlers for
 * non-blocking message publishing.
 */
@Slf4j
public class NonBlockingPublishListener extends AWSIotMessage {

    public NonBlockingPublishListener(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    @Override
    public void onSuccess() {
        log.info("{}: >>> {}", System.currentTimeMillis(), getStringPayload());
    }

    @Override
    public void onFailure() {
        log.error("{}: publish failed for {}", System.currentTimeMillis(), getStringPayload());
    }

    @Override
    public void onTimeout() {
        log.error("{}: publish timeout for {}", System.currentTimeMillis(), getStringPayload());
    }
}
