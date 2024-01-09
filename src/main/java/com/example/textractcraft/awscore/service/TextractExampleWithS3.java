package com.example.demo.service;
import com.example.demo.config.ApplicationConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.File;

@Slf4j
@Service
@AllArgsConstructor
public class TextractExampleWithS3 {

    public static void main(String[] args) {
        String accessKeyId = "AKIA2NMGUGDKN4WVIAUW";
        String secretAccessKey = "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";

        S3Client s3Client = S3Client.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        TextractClient textractClient = TextractClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        String s3Bucket = "textract-console-us-east-1-d44828c7-0cff-4986-b6dd-f9e6970a4c0e";

        String localDocumentPath = "/home/dinesh.krishna@zucisystems.com/Workspace/DATA/Sample/INTICS_IH_SAMPLES 1/dl_final 1/dl/alaska.jpeg";

        String s3DocumentKey = uploadDocumentToS3(s3Client, s3Bucket, localDocumentPath);

        analyzeDocument(textractClient, s3Bucket, s3DocumentKey);
    }

    public static String uploadDocumentToS3(S3Client s3Client, String s3Bucket, String localDocumentPath) {
        File documentFile = new File(localDocumentPath);
        String s3Key = documentFile.getName();

        s3Client.putObject(PutObjectRequest.builder()
                .bucket(s3Bucket)
                .key(s3Key)
                .build(), documentFile.toPath());

        return s3Key;
    }

    private static void analyzeDocument(TextractClient textractClient, String s3Bucket, String s3DocumentKey) {
        StartDocumentAnalysisRequest startRequest = StartDocumentAnalysisRequest.builder()
                .documentLocation(DocumentLocation.builder()
                        .s3Object(S3Object.builder()
                                .bucket(s3Bucket)
                                .name(s3DocumentKey)
                                .build())
                        .build())
                .featureTypes(FeatureType.FORMS, FeatureType.TABLES)
                .build();

        StartDocumentAnalysisResponse startResponse = textractClient.startDocumentAnalysis(startRequest);
        String jobId = startResponse.jobId();
        System.out.println("Analysis Job ID: " + jobId);

        boolean analysisCompleted = false;
        while (!analysisCompleted) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                log.error("Error while waiting for analysis completion: " + e.getMessage());
            }
            GetDocumentAnalysisResponse result = textractClient.getDocumentAnalysis(
                    GetDocumentAnalysisRequest.builder().jobId(jobId).build());
            JobStatus jobStatus = result.jobStatus();
            System.out.println("Analysis Job Status: " + jobStatus);

            if (jobStatus == JobStatus.SUCCEEDED || jobStatus == JobStatus.FAILED || jobStatus == JobStatus.PARTIAL_SUCCESS) {
                analysisCompleted = true;
                System.out.println("Analysis Result: " + result);
            }
        }
    }

}