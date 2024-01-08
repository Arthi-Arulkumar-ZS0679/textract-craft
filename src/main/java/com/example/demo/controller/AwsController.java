package com.example.demo.controller;

import com.example.demo.service.AwsS3Service;
import com.example.demo.service.AwsTextractService;
import com.example.demo.utils.JsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.rekognition.model.FaceDetail;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.textract.model.Query;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AwsController {
    private final AwsTextractService awsService;

    private final AwsS3Service awsS3Service;

    private final JsonBuilder jsonBuilder = new JsonBuilder();

    private static final Logger logger = LoggerFactory.getLogger(AwsController.class);


    @Autowired
    public AwsController(AwsTextractService awsService, AwsS3Service awsS3Service) {
        this.awsService = awsService;
        this.awsS3Service = awsS3Service;
    }

    @GetMapping(value = "/awsSignatureExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> awsSignatureExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.signatureExtractor(filePath);
            logger.info("\n*********************Signature Extraction Successfully***********************\n {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsTableExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tableExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.tableExtractor(filePath);
            logger.info("\n*********************Table Extraction Successfully***********************\n {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsFormExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> formExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.formExtractor(filePath);
            logger.info("\n*********************Form Extraction Successfully***********************\n {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsQueryExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> queryExtractor(
            @RequestParam String filePath,
            @RequestParam List<String> queryTexts
    ) {
        try {
            List<Query> queries = queryTexts.stream()
                    .map(queryText -> Query.builder().text(queryText).build())
                    .collect(Collectors.toList());

            String response = awsService.queryExtractor(filePath, queries);
            logger.info("\n*********************Query Extraction Successfully***********************\n {}", response);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsFileUpload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upload(@RequestParam String filePath) {
        try {
            String response = awsS3Service.uploadDocument(filePath);
            logger.info("Document uploaded successfully {}", filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsFileDownload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> downloadDocument(@RequestParam String key, @RequestParam String downloadFormat) {
        try {
            logger.info("File download successfully file name is {} and download format is {}", key, downloadFormat);
            return awsS3Service.downloadDocument(key, downloadFormat);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsCreateS3Bucket", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAwsBucket(@RequestParam String bucketName) {
        try {
            awsS3Service.createS3Bucket(bucketName);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)));
            final String responseBody = "Bucket creation successfully...";
            logger.info("Bucket created successfully {}", bucketName);
            return new ResponseEntity<>(responseBody, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsGetBucketList", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getBucketList() {
        try {
            final List<Bucket> listBucketsResponse = awsS3Service.getBucketList().stream().sorted(Comparator.comparing(Bucket::creationDate))
                    .toList();
            return JsonBuilder.getBucketJsonResponse(listBucketsResponse);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsDeleteS3Bucket", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> deleteBucket(@RequestParam String bucketName) {
        try {
            awsS3Service.deleteBucketRequest(bucketName);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.setContentType(MediaType.valueOf(String.valueOf(MediaType.APPLICATION_JSON)));
            final String responseBody = "Bucket deletion successfully...";
            logger.info("Bucket deleted successfully {}", bucketName);
            return new ResponseEntity<>(responseBody, httpHeaders, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsGetAllFilesOrObjects", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> getListOfFilesOrObjects(@RequestParam String bucketName) throws S3Exception {
        try {
            List<S3Object> getListOfFiles = awsS3Service.getListOfFilesOrObjects(bucketName);
            System.out.println(getListOfFiles);
            logger.info("List of files fetching successfully from the given bucket {}", bucketName);
            return jsonBuilder.getListOfFilesOrObjects(getListOfFiles);
        } catch (S3Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsDetectFace", produces = MediaType.ALL_VALUE)
    public List<FaceDetail> recognizeFaceFromImage(@RequestParam String filePath) throws S3Exception {
        try {
            return awsS3Service.detectFacesRequest(filePath);
        } catch (S3Exception e) {
            throw new RuntimeException(e);
        }
    }
}

