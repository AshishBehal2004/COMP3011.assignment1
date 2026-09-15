package com3011.assignment1.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

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

	public VoskService(@Value("${vosk.model.path}") String modelPath) throws IOException  {
		String extractedPath = extractModelToTemp(modelPath);
		model = new Model(extractedPath);
	}
	
	private String extractModelToTemp(String resourcePath) throws IOException{
		
		Path tempDir = Files.createTempDirectory("vosk-model");
		
		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
		Resource[] resources = resolver.getResources("classpath:"+resourcePath+"/**");
		System.out.println("Found resources: " + resources.length);
		
		for(Resource resource: resources) {
			// Debug
//			System.out.println("Resource: " + resource.getFilename() + " readable=" + resource.isReadable() + " length=" + resource.contentLength());
			if(resource.isReadable() && resource.contentLength() > 0) {
				String url = resource.getURL().toString();
				String relativePath = url.substring(url.indexOf(resourcePath)+ resourcePath.length());
				
				Path targetPath = tempDir.resolve(relativePath.replaceFirst("^/", ""));
				
				Files.createDirectories(targetPath.getParent());
				
				try(InputStream in = resource.getInputStream()){
					Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
				}
				
//				System.out.println("Copied to: " + targetPath + " exists=" + Files.exists(targetPath));
			    
			}
		}
		return tempDir.toString();
//		return null;
	}

	public String transcribe(byte[] audioBytes) throws IOException, InterruptedException  {
		
		File tempInput = File.createTempFile("audio", ".webm");
		Files.write(tempInput.toPath(), audioBytes);
		File tempOutput = File.createTempFile("audio",".wav");
		
		ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-y", "-i", tempInput.getAbsolutePath(), "-ar", "16000", "-ac", "1", tempOutput.getAbsolutePath()); //conversion step
		
		processBuilder.inheritIO();
		
		Process process = processBuilder.start(); // begin converting
		
		
		process.waitFor(); // wait till the conversion finishes
		
		byte[] convertedBytes = Files.readAllBytes(tempOutput.toPath()); //read the file bytes into memory, so it can be used
		
		Recognizer recognizer = new Recognizer(model, 16000);
		
		recognizer.acceptWaveForm(convertedBytes, convertedBytes.length);
		
		String finalResult = recognizer.getFinalResult();
		ObjectMapper mapper = new ObjectMapper();
		
		JsonNode result = mapper.readTree(finalResult); //basically telling java that its in a JSON format
		String transcript = result.get("text").asString();
		
		return transcript;
	
	}
	
}
