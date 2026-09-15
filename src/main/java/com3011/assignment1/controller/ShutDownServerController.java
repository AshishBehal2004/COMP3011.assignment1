package com3011.assignment1.controller;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicBoolean;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com3011.assignment1.model.ErrorResponse;
import com3011.assignment1.model.ShutDownServerResponse;

@RestController
@RequestMapping("/api/v1/admin")
public class ShutDownServerController {
	
	private final ConfigurableApplicationContext context;
	private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
	
	public ShutDownServerController(ConfigurableApplicationContext currentContext) {
		this.context = currentContext;
	}
	
	@PostMapping("/shutdown")
	public ResponseEntity<?> shutDown() {
		if (!shuttingDown.compareAndSet(false, true)) {
			return ResponseEntity.status(409).body(new ErrorResponse(
					Instant.now().toString(), 409, "Conflict",
					"/api/v1/admin/shutdown", "Graceful shutdown is already in progress."
					));
		}
		
		new Thread(() -> {
			try {
				Thread.sleep(500);
			} catch(InterruptedException e){}
			context.close();
			System.exit(0);
		}).start();
		
		return ResponseEntity.status(202).body(new ShutDownServerResponse("Graceful shutdown requested."));
	}
}
