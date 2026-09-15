package com3011.assignment1.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;


@Service
public class VoskService {
	
	private final Model model; 

	public VoskService(@Value("${vosk.model.path}") String modelPath) throws IOException  {
		model = new Model(modelPath);
	}
	
	public String transcribe(byte[] audioBytes) throws IOException, InterruptedException  {
		
		File tempInput = File.createTempFile("audio", ".webm");
		Files.write(tempInput.toPath(), audioBytes);
		File tempOutput = File.createTempFile("audio",".wav");
		
		ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-i", tempInput.getAbsolutePath(), "-ar", "16000", "-ac", "1", tempOutput.getAbsolutePath()); //conversion step
		
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
