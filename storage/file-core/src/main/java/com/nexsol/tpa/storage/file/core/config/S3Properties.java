package com.nexsol.tpa.storage.file.core.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "s3.storage")
public class S3Properties {

    private String endpoint;

    private String accessKey;

    private String secretKey;

    private String bucketName;

    private String region;

}
