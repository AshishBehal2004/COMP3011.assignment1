package com3011.assignment1.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class AudioController {

	@PostMapping("/api/v1/transcribe")
	public String transcribe(@RequestParam("audio") MultipartFile audioFile) {
		return "Received file: " + audioFile.getOriginalFilename();
	}
}
