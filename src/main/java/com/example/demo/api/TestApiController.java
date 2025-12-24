package com.example.demo.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestApiController {

	@GetMapping(value = "/sample/{id}", version = "1.0.0")
	public String getSample(@PathVariable("id") final String id) {
		return id;
	}

	@PostMapping(value = "/demo")
	public String postDemo(@RequestParam("id") final String id) {
		return id;
	}

}
