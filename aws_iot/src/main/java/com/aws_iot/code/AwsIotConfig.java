package com.aws_iot.code;

import com.amazonaws.services.iot.client.AWSIotException;
import com.amazonaws.services.iot.client.AWSIotMqttClient;
import com.amazonaws.services.iot.client.auth.Credentials;
import com.amazonaws.services.iot.client.auth.StaticCredentialsProvider;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class AwsIotConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access-key}")
    private String accessKey;
    
    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.iot.client-endpoint}")
    private String clientEndpoint;

     @Value("${aws.iot.client-id}")
    private String clientId;

    @Bean
    public AWSIotMqttClient awsIotMqttClient() {
        try {
            Credentials awsCreds = new Credentials(accessKey, secretKey);
            AWSIotMqttClient awsIotMqttClient = new AWSIotMqttClient(clientEndpoint, clientId, new StaticCredentialsProvider(awsCreds), region);
            awsIotMqttClient.subscribe(new VehicleGetStateTopic());
            awsIotMqttClient.connect();
            return awsIotMqttClient;
        } catch (AWSIotException ex) {
            log.error("[AwsIotConfig][awsIotMqttClient] can not initialize awsIotMqttClient. {}", ex.getMessage());
            throw new RuntimeException(ex);
        }
    }

}
