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

        String localDocumentPath = "/home/dinesh.krishna@zucisystems.com/Pictures/NINE.pdf";

        String s3DocumentKey = uploadDocumentToS3(s3Client, s3Bucket, localDocumentPath);

        analyzeDocument(textractClient, s3Bucket, s3DocumentKey);
    }

    private static String uploadDocumentToS3(S3Client s3Client, String s3Bucket, String localDocumentPath) {
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
        System.out.println(jobId);
        waitForJobCompletion(textractClient, jobId);

        GetDocumentAnalysisResponse result = textractClient.getDocumentAnalysis(GetDocumentAnalysisRequest.builder()
                .jobId(jobId)
                .build());

        System.out.println(result);
    }

    private static void waitForJobCompletion(TextractClient textractClient, String jobId) {
        boolean finished = false;
        while (!finished) {
        GetDocumentTextDetectionRequest getDocumentTextDetectionRequest = GetDocumentTextDetectionRequest.builder()
                .jobId(jobId)
                .maxResults(5000)
                .build();
        GetDocumentTextDetectionResponse response = textractClient.getDocumentTextDetection(getDocumentTextDetectionRequest);
        String status = response.jobStatus().toString();
        System.out.println(status);
        if (status.equals("SUCCEEDED")) {
            finished = true;
        } else {
            System.out.println("Job status: " + status);
//            Thread.sleep(1000);
        }
        }
    }
}
