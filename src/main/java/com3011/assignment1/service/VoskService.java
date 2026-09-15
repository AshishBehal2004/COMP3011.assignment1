package com3011.assignment1.service;


import java.io.IOException;
import java.io.InputStream;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class VoskService {
	
	private final Model model; 
	
	private final AtomicLong totalInputSamples = new AtomicLong(0);
	private final AtomicLong totalOutputWords = new AtomicLong(0);
	
	public VoskService(@Value("${vosk.model.path}") String modelPath ) throws IOException  {
		String extractedPath = extractModelToTemp(modelPath);
		model = new Model(extractedPath);
		
	}
	
	public long getTotalInputSamples() {
		return totalInputSamples.get();
	}
	
	
	public long getTotalOutputWords() {
		return totalOutputWords.get();
	}
	
	private String extractModelToTemp(String resourcePath) throws IOException{
		
		Path tempDir = Files.createTempDirectory("vosk-model");
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath:"+resourcePath+"/**");
		System.out.println("Found resources: " + resources.length);
		
		for(Resource resource: resources) {
			
			if(resource.isReadable() && resource.contentLength() > 0) {
				String url = resource.getURL().toString();
				String relativePath = url.substring(url.indexOf(resourcePath)+ resourcePath.length());
				
				Path targetPath = tempDir.resolve(relativePath.replaceFirst("^/", ""));
				
				Files.createDirectories(targetPath.getParent());
				
				try(InputStream in = resource.getInputStream()){
					Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
				}
			}
		}
		return tempDir.toString();
//		return null;
	}

	public String transcribe(byte[] audioBytes) throws IOException  {
		
		Recognizer recognizer = new Recognizer(model, 16000);
		
		recognizer.acceptWaveForm(audioBytes, audioBytes.length);
		
		totalInputSamples.addAndGet(audioBytes.length / 2);
		
		String finalResult = recognizer.getFinalResult();
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode result = mapper.readTree(finalResult); //basically telling java that its in a JSON format
		String transcript = result.get("text").asString();
		
		if (!transcript.isBlank()) {
			totalOutputWords.addAndGet(transcript.trim().split("\\s+").length);
			
		}
		return transcript;
	
	}

	
	
}
