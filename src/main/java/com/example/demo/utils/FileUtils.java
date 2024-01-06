package com.example.demo.utils;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

public class FileUtils {
    public ResponseEntity<InputStreamResource> getImageResponse(S3Client s3Client, GetObjectRequest getObjectRequest, String key, String requestType) throws S3Exception {
        try {
            ResponseBytes<GetObjectResponse> responseBytes = s3Client.getObjectAsBytes(getObjectRequest);
            final String s3ContentType = responseBytes.response().contentType();

            String inputFileExtension = s3ContentType.split("/")[1].toLowerCase();
            if (!requestType.equalsIgnoreCase(inputFileExtension)) {
                inputFileExtension = requestType.toLowerCase();
            }
            final String updatedFileName = replaceFileExtension(key, inputFileExtension);

            final Path tempFile = Files.createTempFile("tempImage", "." + inputFileExtension);
            Files.copy(responseBytes.asInputStream(), tempFile, StandardCopyOption.REPLACE_EXISTING);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", updatedFileName);

            final InputStreamResource inputStreamResource = new InputStreamResource(Files.newInputStream(tempFile));

            // deleting temp file
            Files.deleteIfExists(tempFile);

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(inputStreamResource);

        } catch (S3Exception | IOException e) {
            throw new RuntimeException(e);
        }
    }

    // Method to replace file extension in the filename
    private static String replaceFileExtension(String originalFileName, String newExtension) {
        final int lastDotIndex = originalFileName.lastIndexOf('.');
        if (lastDotIndex != -1) {
            final String fileNameWithoutExtension = originalFileName.substring(0, lastDotIndex);
            return fileNameWithoutExtension + "." + newExtension;
        } else {
            return originalFileName + "." + newExtension;
        }
    }

}
