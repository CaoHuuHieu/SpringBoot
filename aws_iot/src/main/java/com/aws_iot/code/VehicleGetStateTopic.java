package com.aws_iot.code;

import com.amazonaws.services.iot.client.AWSIotMessage;
import com.amazonaws.services.iot.client.AWSIotQos;
import com.amazonaws.services.iot.client.AWSIotTopic;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VehicleGetStateTopic extends AWSIotTopic{

    public static final String VEHICLE_STATE_TOPIC_NAME = "*/*/*/state";

    public VehicleGetStateTopic() {
        super(VEHICLE_STATE_TOPIC_NAME);
    }

    public VehicleGetStateTopic(AWSIotQos qos) {
        super(VEHICLE_STATE_TOPIC_NAME, qos);
    }


    @Override
    public void onMessage(AWSIotMessage message) {
        log.info("[VehicleGetStateTopic][ReceiveMessage] {}", topic);
    }
}
