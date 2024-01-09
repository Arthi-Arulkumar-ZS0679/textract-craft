package com.example.textractcraft.preprocess.controller;

import com.example.textractcraft.preprocess.service.PreprocessService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/preProcess")
public class PreprocessController {

    private final PreprocessService preprocessService;

    @Autowired
    public PreprocessController(PreprocessService preprocessService) {
        this.preprocessService = preprocessService;
    }

    @PostMapping(value = "/pdfToImage", produces = MediaType.APPLICATION_JSON_VALUE)
    public String pdfToImage(@RequestParam final String pdfFilePath, @RequestParam final String imageExtension) {
        final List<String> resultList = Collections.singletonList(preprocessService.convertPdfToImage(pdfFilePath, imageExtension));
        return resultList.toString();
    }

}
