package com.aws_iot.code;

import org.springframework.stereotype.Service;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AwsIotServiceImpl implements AwsIotService{

    private final AWSIotMqttClient awsIotMqttClient;

    @Override
    public void publish(String topic, String strMessage) {
        CustomIotMessage message = new CustomIotMessage(topic, strMessage);
        try {
            awsIotMqttClient.publish(message);
        } catch (AWSIotException ex) {
            log.error("[AwsIotService][publish] ERROR {}", ex.getMessage());
        }
    }

}
