package com.example.demo.service;

import com.example.demo.config.ApplicationConfig;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.*;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AwsService {

    private final ApplicationConfig applicationConfig;
    private final JsonBuilder jsonBuilder = new JsonBuilder();
    private static final Logger logger = LoggerFactory.getLogger(ApplicationConfig.class);

    public String signatureExtractor(String filePath) throws RuntimeException {
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
            logger.info("\n*********************Table Extraction***********************\n {}", response);
            return jsonBuilder.buildJson(response.blocks(), BlockType.SIGNATURE);
        } catch (IOException e) {
            logger.error("Runtime exception occurred in signature extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public String tableExtractor(String filePath) throws RuntimeException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.TABLES).build());
            logger.info("\n*********************Table Extraction***********************\n {}", response);
            return jsonBuilder.buildJson(response.blocks(), BlockType.TABLE);
        } catch (IOException e) {
            logger.error("Runtime exception occurred in table extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String formExtractor(String filePath) throws RuntimeException {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        TextractClient textractClient = TextractClient.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
        try (InputStream inputStream = new FileInputStream(filePath)) {
            Document document = Document.builder().bytes(SdkBytes.fromInputStream(inputStream)).build();
            AnalyzeDocumentResponse response = textractClient.analyzeDocument(AnalyzeDocumentRequest.builder().document(document).featureTypes(FeatureType.FORMS).build());
            logger.info("\n*********************Form Extraction***********************\n {}", response);
            return jsonBuilder.buildJson(response.blocks(), BlockType.LINE);
        } catch (IOException e) {
            logger.error("Runtime exception occurred in form extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String queryExtractor(String filePath, List<Query> queries) throws RuntimeException {
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
            logger.info("\n*********************Query Extraction***********************\n {}", response);
            return jsonBuilder.buildJson(response.blocks(), BlockType.QUERY_RESULT);
        } catch (IOException e) {
            logger.error("Runtime exception occurred in query extraction {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public String uploadDocument(String filePath) {
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();
        String bucketName = applicationConfig.getAwsBucketName();

        try (S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build()) {
            File documentFile = new File(filePath);
            String documentKeyName = documentFile.getName();
            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(documentKeyName)
                    .build();
            PutObjectResponse response = s3Client.putObject(putObjectRequest, documentFile.toPath());
            System.out.println(response);
            String jsonResponse = String.format(
                    "{\"status\": \"success\", \"message\": \"Upload successful!\", \"eTag\": %s, \"bucket\": \"%s\", \"key\": \"%s\"}",
                    response.eTag(), bucketName, documentKeyName);
            return jsonResponse;
        } catch (Exception e) {
            return "Upload failed: " + e.getMessage();
        }
    }
    public ResponseEntity<InputStreamResource> downloadDocument(String key) throws IOException {
        String bucketName = applicationConfig.getAwsBucketName();
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try (S3Client s3Client = S3Client.builder()
                .region(region)
                .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                .build()) {

            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            ResponseBytes<GetObjectResponse> responseInputStream = s3Client.getObjectAsBytes(getObjectRequest);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", key);

            InputStreamResource inputStreamResource = new InputStreamResource(responseInputStream.asInputStream());

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);

        }
        catch (S3Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(null);
        }
    }
}
