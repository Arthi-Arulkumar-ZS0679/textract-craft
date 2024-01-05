package com.example.demo.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;

@Data
@Configuration
public class ApplicationConfig {

    @Value("${aws.region}")
    private Region awsRegion;

    @Value("${aws.accessKeyId}")
    private String awsAccessKeyId;

    @Value("${aws.secretAccessKey}")
    private String awsSecretAccessKey;

    @Value("${aws.bucketName}")
    private String awsBucketName;

    @Value("${aws.s3Location}")
    private String awsS3uri;

}
