package com3011.assignment1.controller;

import java.time.Duration;
import java.time.Instant;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com3011.assignment1.model.UptimeResponse;

@RestController
@RequestMapping("/api/v1/admin")
public class UptimeController {
	private final Instant serverStartTime = Instant.now();
	
	@GetMapping("/uptime")
	public UptimeResponse getTime() {
		
		Instant now = Instant.now();
		
		double upTimeSeconds = Duration.between(serverStartTime, now).toMillis() / 1000.0;
		
		return new UptimeResponse(serverStartTime, now, upTimeSeconds);
		
	}
}
	