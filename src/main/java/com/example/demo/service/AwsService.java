package com.example.demo.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AwsService {

    public void signatureExtractor(String filePath) {
        Region region = Region.US_WEST_2;
        String accessKeyId = "AKIA2NMGUGDKLAB7CWP2";
        String secretAccessKey = "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";
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.SIGNATURES).build());
            System.out.println(response);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void tableExtractor(String filePath) {
        Region region = Region.US_WEST_2;
        String accessKeyId = "AKIA2NMGUGDKLAB7CWP2";
        String secretAccessKey = "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";
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.TABLES).build());
            System.out.println(response);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void formExtractor(String filePath) {
        Region region = Region.US_WEST_2;
        String accessKeyId = "AKIA2NMGUGDKLAB7CWP2";
        String secretAccessKey = "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";
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.FORMS).build());
            System.out.println(response);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void queryExtractor(String filePath) {
        Region region = Region.US_WEST_2;
        String accessKeyId = "AKIA2NMGUGDKLAB7CWP2";
        String secretAccessKey = "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";
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(
                    AnalyzeDocumentRequest.builder()
                            .document(document)
                            .featureTypes(FeatureType.QUERIES)
                            .build());
            System.out.println(response);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
