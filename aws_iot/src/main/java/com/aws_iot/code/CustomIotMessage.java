package com.aws_iot.code;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomIotMessage extends AWSIotMessage {

    public CustomIotMessage(String topic, AWSIotQos qos, String payload) {
        super(topic, qos, payload);
    }

    public CustomIotMessage(String topic, String payload) {
        super(topic, AWSIotQos.QOS0, payload);
    }

    @Override
    public void onSuccess() {
        log.info("[AWSIotMqttClient][Publish] send message to {} successfully", topic);
        
    }

    @Override
    public void onFailure() {
       log.error("[AWSIotMqttClient][Publish] send message to {} failed: {}", topic, errorMessage);
    }

    @Override
    public void onTimeout() {
         log.error("[AWSIotMqttClient][Publish] send message to {} timeout: {}", topic, errorMessage);
    }

}