package com3011.assignment1;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com3011.assignment1.service.VoskService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

	static final int  NUM_REQUESTS = 250;
	static final int SAMPLE_PER_REQUEST = 16000;
	@LocalServerPort private int port;
	
	@Autowired private VoskService voskService;
	
	private final TestRestTemplate restTemplate = new TestRestTemplate();
	
	private HttpEntity<MultiValueMap<String, Object>> buildMultipartRequest(byte[] audioBytes){
		ByteArrayResource audioResource = new ByteArrayResource(audioBytes) {
			@Override
			public String getFilename() {
				return "test-audio.raw";
			}
		};
		MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
		body.add("audio", audioResource);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		return new HttpEntity<>(body, headers);
	}
	
	@Test
	void contextLoads() {
	}

	// Java's default async HttpClient couldn't open 250 connections at once reliably,
	// so I switched to a simple blocking client just for this test.
	@Test
	void audioTest() throws Exception{
		byte[] audioArr = new byte[SAMPLE_PER_REQUEST * 2];
		long samples = voskService.getTotalInputSamples();
		
		String serverUrl = "http://localhost:" + port + "/api/v1/transcribe";
		
		List<Callable<ResponseEntity<String>>> tasks = new ArrayList<>();
			for(int i = 0; i < NUM_REQUESTS; i++){
				tasks.add(() -> restTemplate.postForEntity(serverUrl, buildMultipartRequest(audioArr), String.class));
		}
			
			ExecutorService executor = Executors.newFixedThreadPool(NUM_REQUESTS);
			Instant start = Instant.now();
		
			List<Future<ResponseEntity<String>>> futures = executor.invokeAll(tasks);
			Duration elapsed = Duration.between(start, Instant.now());
			
			executor.shutdown();
			
			for(Future<ResponseEntity<String>> future : futures) {
				ResponseEntity<String> response = future.get();
				assertEquals(HttpStatus.OK, response.getStatusCode());
			}
			assertTrue(elapsed.getSeconds() < 90);
			
			long expected = samples + ((long) NUM_REQUESTS * SAMPLE_PER_REQUEST);
			assertEquals(expected, voskService.getTotalInputSamples());
	}
	
	@BeforeEach
	void setUp() {
		restTemplate.getRestTemplate().setRequestFactory(new SimpleClientHttpRequestFactory());
	}
}
