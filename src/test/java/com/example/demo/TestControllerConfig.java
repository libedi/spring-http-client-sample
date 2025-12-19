package com.example.demo;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

class TestControllerConfig {

	@TestConfiguration
	static class TestConfig {
		@Bean
		TestController testController() {
			return new TestController();
		}
	}

	@RestController
	static class TestController {
		@GetMapping("/sample/{id}")
		public String getSample(@PathVariable("id") final String id) {
			return id;
		}

		@PostMapping("/demo")
		public String postDemo(@RequestParam("id") final String id) {
			return id;
		}
	}

}
