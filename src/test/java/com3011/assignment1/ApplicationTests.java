package com3011.assignment1;

import org.springframework.http.HttpHeaders;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import com3011.assignment1.service.VoskService;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApplicationTests {

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

}
