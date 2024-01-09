package com.example.textractcraft.preprocess.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@AllArgsConstructor
public class PreprocessService {

    public String convertPdfToImage(String filePath, String extension) {
        final List<String> imageFileNames = new ArrayList<>();
        final ObjectMapper objectMapper = new ObjectMapper();
        try {
            PDDocument document = PDDocument.load(new File(filePath));
            PDFRenderer pdfRenderer = new PDFRenderer(document);
            String outputFolder = generateOutputFolder(filePath);
            for (int page = 0; page < document.getNumberOfPages(); ++page) {
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, 300, ImageType.RGB);
                String outputFilename = generateOutputFilename(outputFolder, page, extension);
                ImageIOUtil.writeImage(bim, outputFilename, 300);
                imageFileNames.add(outputFilename);
            }
            document.close();
            return objectMapper.writeValueAsString(Map.of("convertedFilePaths", imageFileNames));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String generateOutputFolder(String filePath) {
        File inputFile = new File(filePath);
        String parentFolder = inputFile.getParent();
        return parentFolder + File.separator + "pdf_to_image";
    }

    private String generateOutputFilename(String outputFolder, int page, String extension) {
        File createFolderName = new File(outputFolder);
        if (!createFolderName.exists()) {
            boolean success = createFolderName.mkdirs();
            if (!success) {
                throw new RuntimeException("Failed to create the output folder: " + createFolderName);
            }
        }
        return String.format("%s/page_%03d.%s", outputFolder, page + 1, extension);
    }
}
