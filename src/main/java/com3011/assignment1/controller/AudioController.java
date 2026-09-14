package com3011.assignment1.controller;

import java.io.IOException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com3011.assignment1.service.VoskService;

@RestController
public class AudioController {

	@PostMapping("/api/v1/transcribe")
	public String transcribe(@RequestParam("audio") MultipartFile audioFile) throws IOException {
		String transcribedText = voskService.transcribe(audioFile.getBytes());
		
		return transcribedText;
	}
	
	private final VoskService voskService;
	public AudioController(VoskService voskService) {
		this.voskService = voskService;
	}
}
