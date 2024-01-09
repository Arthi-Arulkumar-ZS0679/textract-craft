package com.example.textractcraft.awscore.utils;

import com.example.textractcraft.awscore.controller.AwsController;
import com.example.textractcraft.awscore.dto.S3GetListOfFilesAndObjectDto;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class JsonBuilder {
    private String jsonResponse = null;

    private final S3GetListOfFilesAndObjectDto s3ObjectDto = new S3GetListOfFilesAndObjectDto();

    private static final Logger logger = LoggerFactory.getLogger(AwsController.class);

    public String buildJson(List<Block> allBlocks, BlockType signature) {
        ObjectMapper objectMapper = new ObjectMapper();

        // Register custom serializer for Geometry class
        SimpleModule module = new SimpleModule();
        module.addSerializer(Geometry.class, new GeometrySerializer());
        objectMapper.registerModule(module);

        List<Object> jsonList = new ArrayList<>();
        List<Block> blockList = allBlocks.stream().filter(block -> block.blockType().equals(signature)).toList();

        for (Block block : blockList) {
            Map<String, Object> signatureJson = buildSignatureJson(block);
            if (block.blockType() == BlockType.SIGNATURE) {
                jsonList.add(signatureJson);
            } else if (block.blockType() == BlockType.TABLE) {
                jsonList.add(signatureJson);
            } else if (block.blockType() == BlockType.QUERY || block.blockType() == BlockType.QUERY_RESULT) {
                jsonList.add(signatureJson);
            } else {
                jsonList.add(signatureJson);
            }
        }

        Map<String, Object> mapJsonResponse = new HashMap<>();
        mapJsonResponse.put("Blocks", jsonList);

        try {
            jsonResponse = objectMapper.writeValueAsString(mapJsonResponse);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
        return jsonResponse;
    }

    private Map<String, Object> buildSignatureJson(Block block) {
        Map<String, Object> signatureJson = new HashMap<>();
        signatureJson.put("BlockType", block.blockType().toString());
        signatureJson.put("Confidence", block.confidence());
        signatureJson.put("Geometry", block.geometry());
        signatureJson.put("Answer", block.text());
        signatureJson.put("Id", block.id());
        return signatureJson;
    }

    private static class GeometrySerializer extends JsonSerializer<Geometry> {
        @Override
        public void serialize(Geometry geometry, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeStartObject();

            // Serialize BoundingBox
            jsonGenerator.writeObjectFieldStart("BoundingBox");
            BoundingBox boundingBox = geometry.boundingBox();
            jsonGenerator.writeNumberField("Width", boundingBox.width());
            jsonGenerator.writeNumberField("Height", boundingBox.height());
            jsonGenerator.writeNumberField("Left", boundingBox.left());
            jsonGenerator.writeNumberField("Top", boundingBox.top());
            jsonGenerator.writeEndObject();

            // Serialize Polygon
            jsonGenerator.writeArrayFieldStart("Polygon");
            for (Point point : geometry.polygon()) {
                jsonGenerator.writeStartObject();
                jsonGenerator.writeNumberField("X", point.x());
                jsonGenerator.writeNumberField("Y", point.y());
                jsonGenerator.writeEndObject();
            }
            jsonGenerator.writeEndArray();

            jsonGenerator.writeEndObject();
        }
    }

    public static String getBucketJsonResponse(List<Bucket> buckets) {
        try {
            List<Bucket> sortedBuckets = buckets.stream()
                    .sorted(Comparator.comparing(Bucket::creationDate))
                    .toList();

            List<Map<String, String>> jsonList = getMapList(sortedBuckets);
            ObjectMapper objectMapper = new ObjectMapper();
            logger.info("Bucket list fetching successfully {}", buckets);
            return objectMapper.writeValueAsString(jsonList);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private static List<Map<String, String>> getMapList(List<Bucket> sortedBuckets) {

        List<Map<String, String>> jsonList = new ArrayList<>();

        Map<String, String> totalBucketsMap = new LinkedHashMap<>();
        totalBucketsMap.put("totalBuckets", String.valueOf(sortedBuckets.size()));
        jsonList.add(totalBucketsMap);

        // mapping and ordering response name with date
        for (int i = 0; i < sortedBuckets.size(); i++) {
            Bucket bucket = sortedBuckets.get(i);
            String createdDate = bucket.creationDate().toString();
            Map<String, String> bucketMap = new LinkedHashMap<>();
            bucketMap.put("bucket " + (i + 1), bucket.name());
            bucketMap.put("createdDate", createdDate);
            jsonList.add(bucketMap);
        }
        return jsonList;
    }

    public ResponseEntity<String>  getListOfFilesOrObjects(List<S3Object> getListOfFiles) {
        List<S3GetListOfFilesAndObjectDto> dtoList = getListOfFiles.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
        Map<String, List<S3GetListOfFilesAndObjectDto>> responseMap = new HashMap<>();
        responseMap.put("files", dtoList);

        ObjectMapper objectMapper = new ObjectMapper();
        String json;
        try {
            json = objectMapper.writeValueAsString(responseMap);
        } catch (JsonProcessingException e) {
            return ResponseEntity.status(500).body("Error converting to JSON");
        }
        return ResponseEntity.ok(json);
    }

    private S3GetListOfFilesAndObjectDto convertToDto(S3Object s3Object) {
        s3ObjectDto.setKey(s3Object.key());
        s3ObjectDto.setLastModified(s3Object.lastModified().toString());
        s3ObjectDto.setETag(s3Object.eTag());
        s3ObjectDto.setSize(s3Object.size());
        s3ObjectDto.setStorageClass(String.valueOf(s3Object.storageClass()));
        return s3ObjectDto;
    }
}
