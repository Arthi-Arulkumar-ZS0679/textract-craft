/*
package com.example.textractcraft.awscore.service;

import com.example.textractcraft.awscore.dto.IdentityDocumentDto;
import com.example.textractcraft.awscore.utils.JsonBuilder;
import com.example.textractcraft.config.ApplicationConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


@Slf4j
@Service
@AllArgsConstructor
public class AwsAnalyzeIdService {
    private final ApplicationConfig applicationConfig;
    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);
    private final JsonBuilder jsonBuilder = new JsonBuilder();


    public List<com.example.textractcraft.awscore.service.IdentityDocumentDto> analyzeId(String filePath) {

        String bucketName = applicationConfig.getAwsBucketName();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        Region region = applicationConfig.getAwsRegion();

        S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();
        TextractExampleWithS3 textractExampleWithS3 = new TextractExampleWithS3();
        String s3DocumentKey = textractExampleWithS3.uploadDocumentToS3(s3Client, bucketName, filePath);
        System.out.println(s3DocumentKey);

        TextractClient textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        S3Object s3Object = S3Object.builder()
                .bucket(bucketName)
                .name(s3DocumentKey)
                .build();
        Document myDoc = Document.builder()
                .s3Object(s3Object)
                .build();
        AnalyzeIdRequest analyzeIdRequest = AnalyzeIdRequest.builder()
                .documentPages(myDoc).build();

        AnalyzeIdResponse analyzeId = textractClient.analyzeID(analyzeIdRequest);
        List<IdentityDocument> docs = analyzeId.identityDocuments();
        List<IdentityDocumentDto> identityDocumentDto = new ArrayList<>();


        docs.forEach(identityDocument -> {
            identityDocumentDto.add(IdentityDocumentDto.builder()
                    .documentIndex(identityDocument.documentIndex())
                    .identityDocumentFields(identityDocument.identityDocumentFields().toString())
                    .blocks(identityDocument.blocks().toString())
                    .build());
        });
        return identityDocumentDto;
    }

}
*/
