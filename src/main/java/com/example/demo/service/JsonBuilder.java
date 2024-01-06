package com.example.demo.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import software.amazon.awssdk.services.textract.model.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
}
