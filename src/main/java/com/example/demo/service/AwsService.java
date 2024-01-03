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

@Service
public class AwsService {

    public void textExtractor(String filePath) {
        Region region = Region.US_WEST_2;
        String accessKeyId = "AKIA2NMGUGDKLAB7CWP2";
        String secretAccessKey = "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";
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();

        try (InputStream inputStream = new FileInputStream(filePath)) {

            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.TABLES).build());

            // Process the extracted text and data
            System.out.println("Extracted text:");
            for (Block block : response.blocks()) {
                System.out.println(block.text());
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }
}
