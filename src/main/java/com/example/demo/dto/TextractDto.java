package com.example.demo.dto;

import software.amazon.awssdk.services.textract.model.Block;

import java.util.List;

public class TextractDto {
    private List<Block> blockList;

    public List<Block> getBlockList() {
        return blockList;
    }

    public void setBlockList(List<Block> blocks) {
        this.blockList = blocks;
    }
}
