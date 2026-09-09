package com3011.assignment1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com3011.assignment1.model.GlobalStatResponse;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatController {
	
	@GetMapping("/stats")
	public GlobalStatResponse getTime() {
		
		
		return new GlobalStatResponse(0L, 0L);
	}
	
}
