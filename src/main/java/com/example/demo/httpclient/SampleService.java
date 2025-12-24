package com.example.demo.httpclient;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.service.annotation.GetExchange;

public interface SampleService {

	@GetExchange("/sample/{id}")
	String get(@PathVariable("id") String id);
}
