package com.example.demo.service;

import com.example.demo.config.ApplicationConfig;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
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
import java.util.List;

@Slf4j
@Service
public class AwsService {

    private final ApplicationConfig applicationConfig;

    private static Logger logger;

    @Autowired
    public AwsService(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public AnalyzeDocumentResponse signatureExtractor(String filePath) throws RuntimeException{
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.SIGNATURES).build());
            logger.info("Signature extraction completed {}", response);
            return response;
        } catch (IOException e) {
            logger.error("An runtime exception occurred in signature extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public AnalyzeDocumentResponse tableExtractor(String filePath) throws RuntimeException{
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.TABLES).build());
            logger.info("Table extraction completed {}", response);
            return response;
        } catch (IOException e) {
            logger.error("An runtime exception occurred in table extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public AnalyzeDocumentResponse formExtractor(String filePath) throws RuntimeException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.FORMS).build());
            logger.info("Form extraction completed {}", response);
            return response;
        } catch (IOException e) {
            logger.error("An runtime exception occurred in form extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public AnalyzeDocumentResponse queryExtractor(String filePath, List<Query> queries) throws RuntimeException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        QueriesConfig queryConfig = QueriesConfig.builder()
                .queries(queries)
                .build();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(
                    AnalyzeDocumentRequest.builder()
                            .document(document)
                            .featureTypes(FeatureType.QUERIES)
                            .queriesConfig(queryConfig)
                            .build());
            logger.info("Query extraction completed {}", response);
            return response;
        } catch (IOException e) {
            logger.error("An runtime exception occurred in query extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }
}
