package com3011.assignment1.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com3011.assignment1.model.ShutDownServerResponse;

@RestController
@RequestMapping("/api/v1/admin")
public class ShutDownServerController {
	
	public ShutDownServerResponse getTime() {
		return null ;
	}
}
