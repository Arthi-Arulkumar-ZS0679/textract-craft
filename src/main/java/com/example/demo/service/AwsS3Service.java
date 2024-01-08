package com.example.demo.service;

import com.example.demo.config.ApplicationConfig;
import com.example.demo.utils.FileUtils;
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
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.util.List;
import java.io.IOException;


@Slf4j
@Service
@AllArgsConstructor
public class AwsS3Service {
    private final ApplicationConfig applicationConfig;
    private final FileUtils fileUtils = new FileUtils();
    private static final Logger logger = LoggerFactory.getLogger(AwsS3Service.class);


    public ResponseEntity<InputStreamResource> downloadDocument(String key, String downloadFormat) throws S3Exception {
        String bucketName = applicationConfig.getAwsBucketName();
        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try (S3Client s3Client = S3Client.builder().region(region).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build()) {
            GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(bucketName).key(key).build();
            return fileUtils.getImageResponse(s3Client, getObjectRequest, key, downloadFormat);
        } catch (S3Exception e) {
            logger.error("Runtime exception occurred while downloading file{}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    public void createS3Bucket(String bucketName) throws S3Exception {

        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try {
            S3Client s3Client = S3Client.builder().region(Region.of(String.valueOf(region))).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
            CreateBucketRequest createBucketRequest = CreateBucketRequest.builder().bucket(bucketName).build();
            s3Client.createBucket(createBucketRequest);
        } catch (S3Exception e) {
            logger.error("Runtime exception occurred in bucket creation {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<Bucket> getBucketList() throws S3Exception {

        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try {
            S3Client s3Client = S3Client.builder().region(Region.of(String.valueOf(region))).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
            ListBucketsResponse listBucketsResponse = s3Client.listBuckets();
            return listBucketsResponse.buckets().stream().toList();
        } catch (S3Exception e) {
            logger.error("Runtime exception occurred while getting bucket list {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }


    public void deleteBucketRequest(String bucketName) throws S3Exception {

        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try {
            S3Client s3Client = S3Client.builder().region(Region.of(String.valueOf(region))).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
            DeleteBucketRequest deleteBucketRequest = DeleteBucketRequest.builder()
                    .bucket(bucketName)
                    .build();
            s3Client.deleteBucket(deleteBucketRequest);
        } catch (S3Exception e) {
            logger.error("Runtime exception occurred while deleting a bucket {}", e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public List<S3Object> getListOfFilesOrObjects(String bucketName) throws S3Exception {

        Region region = applicationConfig.getAwsRegion();
        String accessKeyId = applicationConfig.getAwsAccessKeyId();
        String secretAccessKey = applicationConfig.getAwsSecretAccessKey();

        try {
            S3Client s3Client = S3Client.builder().region(Region.of(String.valueOf(region))).credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKeyId, secretAccessKey))).build();
            ListObjectsV2Request listObjectsV2Request = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();
            ListObjectsV2Response listObjectsV2Response = s3Client.listObjectsV2(listObjectsV2Request);
            return listObjectsV2Response.contents();
        } catch (S3Exception e) {
            logger.error("Runtime exception occurred while deleting a bucket {}", e.getMessage());
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
            return String.format(
                    "{\"status\": \"success\", \"message\": \"Upload successful!\", \"eTag\": %s, \"bucket\": \"%s\", \"key\": \"%s\"}",
                    response.eTag(), bucketName, documentKeyName);
        } catch (Exception e) {
            logger.error("Runtime exception occurred {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
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
            logger.error("Runtime exception occurred {}", e.getMessage());
            return ResponseEntity.status(500).body(null);
        }
    }
}
