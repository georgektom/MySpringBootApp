package com.example.myapp.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;

import java.net.URI;

@Configuration
@Profile("local")
public class LocalAwsConfig {

    @Bean
    S3Client s3Client(
            @Value("${app.aws.localstack.endpoint:http://localhost:4566}") String endpoint,
            @Value("${AWS_REGION:us-east-1}") String region
    ) {
        return S3Client.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }

    @Bean
    SnsClient snsClient(
            @Value("${app.aws.localstack.endpoint:http://localhost:4566}") String endpoint,
            @Value("${AWS_REGION:us-east-1}") String region
    ) {
        return SnsClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }

    @Bean
    SqsAsyncClient sqsAsyncClient(
            @Value("${app.aws.localstack.endpoint:http://localhost:4566}") String endpoint,
            @Value("${AWS_REGION:us-east-1}") String region
    ) {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.of(region))
                .credentialsProvider(staticCredentialsProvider())
                .build();
    }

    private StaticCredentialsProvider staticCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test"));
    }
}

