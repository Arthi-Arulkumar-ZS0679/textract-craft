package com.example.textractcraft.awscore.dto;

import lombok.Data;

@Data
public class S3GetListOfFilesAndObjectDto {
    private String key;
    private String lastModified;
    private String eTag;
    private long size;
    private String storageClass;
}
