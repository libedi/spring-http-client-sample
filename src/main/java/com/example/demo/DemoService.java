package com.example.demo;

import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.service.annotation.PostExchange;

public interface DemoService {

	@PostExchange("/demo")
	String post(@RequestParam("id") String id);

}
