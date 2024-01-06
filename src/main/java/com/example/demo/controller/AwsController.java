package com.example.demo.controller;

import com.example.demo.service.AwsS3Service;
import com.example.demo.service.AwsTextractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.textract.model.Query;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AwsController {
    private final AwsTextractService awsService;

    private final AwsS3Service awsS3Service;

    @Autowired
    public AwsController(AwsTextractService awsService, AwsS3Service awsS3Service){
        this.awsService = awsService;
        this.awsS3Service = awsS3Service;
    }

    @GetMapping(value = "/awsSignatureExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> awsSignatureExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.signatureExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsTableExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> tableExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.tableExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsFormExtractor", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> formExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.formExtractor(filePath);
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
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsUpload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> upload(@RequestParam String filePath) {
        try {
            String response = awsS3Service.uploadDocument(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping(value = "/awsDownload", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<InputStreamResource> downloadDocument(@RequestParam String key, @RequestParam String downloadFormat) {
        try {
            return awsS3Service.downloadDocument(key, downloadFormat);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @PostMapping(value = "/awsCreateBucket", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<String> createAwsBucket(@RequestParam String bucketName) {
        System.out.println(bucketName);
        return null;
    }
}
