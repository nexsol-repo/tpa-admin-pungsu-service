package com.nexsol.tpa.storage.file.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

import java.net.URI;

@Configuration
public class S3ClientConfig {

    @Bean
    public S3Client s3Client(S3Properties properties) {
        return S3Client.builder()
            .endpointOverride(URI.create(properties.getEndpoint()))
            .region(Region.of(properties.getRegion()))
            .credentialsProvider(StaticCredentialsProvider
                .create(AwsBasicCredentials.create(properties.getAccessKey(), properties.getSecretKey())))
            .forcePathStyle(true) // SeaweedFS 필수 설정
            .build();
    }

    @Bean
    public S3Presigner s3Presigner(S3Properties properties) {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(properties.getAccessKey(),
                properties.getSecretKey());

        return S3Presigner.builder()
            .endpointOverride(URI.create(properties.getEndpoint()))
            .region(Region.of(properties.getRegion()))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            // ⬅️ [핵심] S3Presigner에서 Path Style Access를 위한 설정
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .build();
    }

}
