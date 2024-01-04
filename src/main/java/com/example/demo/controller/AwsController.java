package com.example.demo.controller;

import com.example.demo.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
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
    public ResponseEntity<AnalyzeDocumentResponse> awsSignatureExtractor(@RequestParam String filePath) {
        try {
            AnalyzeDocumentResponse response = awsService.signatureExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsTableExtractor")
    public ResponseEntity<AnalyzeDocumentResponse> tableExtractor(@RequestParam String filePath) {
        try {
            AnalyzeDocumentResponse response = awsService.tableExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsFormExtractor")
    public ResponseEntity<AnalyzeDocumentResponse> formExtractor(@RequestParam String filePath) {
        try {
            AnalyzeDocumentResponse response = awsService.formExtractor(filePath);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @GetMapping("/awsQueryExtractor")
    public ResponseEntity<AnalyzeDocumentResponse> queryExtractor(
            @RequestParam String filePath,
            @RequestParam List<String> queryTexts
    ) {
        try {
            List<Query> queries = queryTexts.stream()
                    .map(queryText -> Query.builder().text(queryText).build())
                    .collect(Collectors.toList());

            AnalyzeDocumentResponse response = awsService.queryExtractor(filePath, queries);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
