package com.example.demo.controller;

import com.example.demo.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.textract.model.Query;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class AwsController {
    private final AwsService awsService;

    @Autowired
    public AwsController( AwsService awsService){
        this.awsService = awsService;
    }

    @GetMapping("/awsSignatureExtractor")
    public ResponseEntity<String> awsSignatureExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.signatureExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsTableExtractor")
    public ResponseEntity<String> tableExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.tableExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsFormExtractor")
    public ResponseEntity<String> formExtractor(@RequestParam String filePath) {
        try {
            String response = awsService.formExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsQueryExtractor")
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

    @PostMapping("/awsUpload")
    public ResponseEntity<String> upload(@RequestParam String filePath) {
        try {
            String response = awsService.uploadDocument(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
    @GetMapping("/awsDownload")
    public ResponseEntity<InputStreamResource> downloadDocument(@RequestParam String key) {
        try {
            return awsService.downloadDocument(key);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
