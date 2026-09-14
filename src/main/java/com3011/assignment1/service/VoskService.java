package com3011.assignment1.service;

import java.io.IOException;

import org.springframework.stereotype.Service;
import org.vosk.Model;
import org.vosk.Recognizer;


@Service
public class VoskService {
	private final Model model; 

	public VoskService() throws IOException  {
		model = new Model("models/vosk-model-small-en-us-0.15");
	}
	
	public String transcribe(byte[] audioBytes) {
		Recognizer recognizer = new Recognizer(model, 16000);
		
		recognizer.acceptWaveForm(audioBytes, audioBytes.length);
		
		String finalResult = recognizer.getFinalResult();
		String partialResult = recognizer.getPartialResult();
		
		ObjectMapper mapper = new ObjectMapper();
		
		return "";
	}
	
}
