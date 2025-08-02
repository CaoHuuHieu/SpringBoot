package com.athena.code;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.athena.AthenaClient;
import software.amazon.awssdk.services.athena.model.QueryExecutionContext;
import software.amazon.awssdk.services.athena.model.ResultConfiguration;

@Configuration
public class AwsAthenaConfig {

    @Value("${aws.region}")
    private String region;

    @Value("${aws.access-key}")
    private String accessKey;
    
    @Value("${aws.secret-key}")
    private String secretKey;

    @Value("${aws.athena.database}")
    private String athenaDatabase;

    @Value("${aws.athena.s3-output-location}")
    private String s3OutputLocation;

    @Bean
    public AthenaClient athenaClient() {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(accessKey, secretKey);
        return AthenaClient.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
    }

    @Bean
    public QueryExecutionContext queryExecutionContext() {
        return  QueryExecutionContext.builder().database(athenaDatabase).build();
    }

    @Bean
    public ResultConfiguration resultConfig(){
        return ResultConfiguration.builder()
                .outputLocation(s3OutputLocation)
                .build();
    }
}
