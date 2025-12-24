package com.example.demo.httpclient;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestHttpController {

	private final OrchestrationService service;

	TestHttpController(final OrchestrationService service) {
		this.service = service;
	}

	@GetMapping(value = "/http/sample/{id}", version = "1.0.0")
	public String getSample(@PathVariable("id") final String id) {
		return service.getSample(id);
	}

	@PostMapping(value = "/http/demo/{id}", version = "1.0.0")
	public String postDemo(final String id) {
		return service.createDemo(id);
	}

}
