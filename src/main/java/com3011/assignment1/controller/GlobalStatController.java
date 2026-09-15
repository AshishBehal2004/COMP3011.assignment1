package com3011.assignment1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com3011.assignment1.model.GlobalStatResponse;
import com3011.assignment1.service.VoskService;

@RestController
@RequestMapping("/api/v1/global")
public class GlobalStatController {
	
	private final VoskService voskService;
	
	public GlobalStatController(VoskService currentvoskService) {
		this.voskService = currentvoskService;
		
	}
	
	@GetMapping("/stats")
	public GlobalStatResponse getStats() {
		
		return new GlobalStatResponse(voskService.getTotalInputSamples(), voskService.getTotalOutputWords());
	}
	
}
