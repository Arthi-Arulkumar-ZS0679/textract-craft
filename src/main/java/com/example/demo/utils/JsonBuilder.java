package com.example.demo.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.util.*;

public class JsonBuilder {
    private String jsonResponse = null;

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

    public static String getS3ObjectListJson(List<S3Object> s3Objects) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            StringBuilder jsonBuilder = new StringBuilder("[");
            for (int i = 0; i < s3Objects.size(); i++) {
                S3Object s3Object = s3Objects.get(i);
                String json = objectMapper.writeValueAsString(s3Object);

                jsonBuilder.append(json);

                // Add a comma if it's not the last element
                if (i < s3Objects.size() - 1) {
                    jsonBuilder.append(", ");
                }
            }
            jsonBuilder.append("]");

            return jsonBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
