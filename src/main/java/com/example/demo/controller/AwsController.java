package com.example.demo.controller;

import com.example.demo.service.AwsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwsController {

    private  AwsService awsService;

    @Autowired
    public AwsController( AwsService awsService){
        this.awsService = awsService;
    }

    @GetMapping("/awsSignatureExtractor")
    public void awsTextractor (@RequestParam String filePath) {
        awsService.signatureExtractor(filePath);
    }

    @GetMapping("/awsTableExtractor")
    public void tableExtractor (@RequestParam String filePath) {
        awsService.tableExtractor(filePath);
    }

    @GetMapping("/awsFormExtractor")
    public void formExtractor (@RequestParam String filePath) {
        awsService.formExtractor(filePath);
    }

    @GetMapping("/awsQueryExtractor")
    public void QueryExtractor (@RequestParam String filePath) {
        awsService.queryExtractor(filePath);
    }
}
