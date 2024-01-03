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

    @GetMapping("/awsTextExtractor")
    public void awsTextractor (@RequestParam String filePath) {
        awsService.textExtractor(filePath);
//        return new ResponseEntity<>(awsService.textExtractor(filePath), HttpStatus.OK);
    }
}
