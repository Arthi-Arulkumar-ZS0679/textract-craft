package com.example.demo.service;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AwsServiceTest {

    private final AwsService awsService;

    private String filePath = "/home/dineshkumar.anandan@zucisystems.com/Downloads/download.png";

    AwsServiceTest(AwsService awsService, String filePath) {
        this.awsService = awsService;
        this.filePath = filePath;
    }

    @Test
    void signatureExtractor() {
        awsService.signatureExtractor(filePath);
    }

    @Test
    void tableExtractor() {
    }

    @Test
    void formExtractor() {
    }

    @Test
    void queryExtractor() {
    }
}