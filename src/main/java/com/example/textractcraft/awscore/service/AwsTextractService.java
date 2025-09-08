package com.example.textractcraft.awscore.service;

import com.example.textractcraft.config.ApplicationConfig;
import com.example.textractcraft.awscore.utils.JsonBuilder;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Map;

@Slf4j
@Service
@AllArgsConstructor
public class AwsTextractService {

    private final ApplicationConfig applicationConfig;
    private final JsonBuilder jsonBuilder = new JsonBuilder();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public String signatureExtractor(String filePath) throws TextractException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(
                    AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.SIGNATURES).build());
            return jsonBuilder.buildJson(response.blocks(), BlockType.SIGNATURE);
        } catch (TextractException | IOException e) {
            logger.error("Runtime exception occurred in signature extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public String tableExtractor(String filePath) throws TextractException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.TABLES).build());
            return jsonBuilder.buildJson(response.blocks(), BlockType.TABLE);
        } catch (TextractException | IOException e) {
            logger.error("Runtime exception occurred in table extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String formExtractor(String filePath, Map<String, Object> jsonMap) throws TextractException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        TextractClient textractClient = TextractClient.builder()
                .region(region)
                .credentialsProvider(
                        StaticCredentialsProvider.create(
                                AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build();

        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder()
                    .bytes(SdkBytes.fromInputStream(inputStream))
                    .build();

            AnalyzeDocumentResponse response = textractClient.analyzeDocument(
                    AnalyzeDocumentRequest.builder()
                            .document(document)
                            .featureTypes(FeatureType.FORMS)
                            .build());

            return jsonBuilder.buildJson(response.blocks(),jsonMap);

        } catch (TextractException | IOException e) {
            logger.error("Runtime exception occurred in form extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String queryExtractor(String filePath, List<Query> queries) throws TextractException {
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
            return jsonBuilder.buildJson(response.blocks(), BlockType.QUERY_RESULT);
        } catch (TextractException | IOException e) {
            logger.error("Runtime exception occurred in query extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

}
