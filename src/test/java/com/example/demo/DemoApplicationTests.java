package com.example.demo;

import com.example.demo.config.ApplicationConfig;
import com.example.demo.service.AwsService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.textract.TextractClient;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentRequest;
import software.amazon.awssdk.services.textract.model.AnalyzeDocumentResponse;
import software.amazon.awssdk.services.textract.model.Query;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@SpringBootTest
class DemoApplicationTests {

	@Mock
	private ApplicationConfig applicationConfig;

	@InjectMocks
	private AwsService awsService;

	@Test
	void testQueryExtractor() throws RuntimeException, IOException {
		// Mock configuration values
		when(applicationConfig.getAwsRegion()).thenReturn(Region.US_EAST_1);
		when(applicationConfig.getAwsAccessKeyId()).thenReturn("dummyAccessKey");
		when(applicationConfig.getAwsSecretAccessKey()).thenReturn("dummySecretKey");

		// Mock TextractClient
		TextractClient textractClient = Mockito.mock(TextractClient.class);
		when(textractClient.analyzeDocument(Mockito.any(AnalyzeDocumentRequest.class)))
				.thenReturn(AnalyzeDocumentResponse.builder().build());

		// Inject the mocked TextractClient into the awsService
		awsService = new AwsService(applicationConfig);

		// Mock InputStream
		byte[] dummyBytes = "dummy content".getBytes();
		try (InputStream inputStream = new ByteArrayInputStream(dummyBytes)) {
			// Mock analyzeDocument call
			AnalyzeDocumentResponse response = awsService.queryExtractor("dummyFilePath", Collections.singletonList(Query.builder().text("dummyQuery").build()));

			// Assertions or verifications can be added based on the expected behavior
			// For example, you might want to verify that textractClient.analyzeDocument was called with the expected parameters
			assertEquals(1, response.sdkHttpResponse().statusCode());
		}
	}
}
