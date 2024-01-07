package com.example.demo.dto;

import lombok.Data;

@Data
public class S3ObjectDto {
    private String key;
    private String lastModified;
    private String eTag;
    private long size;
    private String storageClass;
}
