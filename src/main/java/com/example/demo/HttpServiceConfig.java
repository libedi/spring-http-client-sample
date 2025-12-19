package com.example.demo;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClientException;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "sample", types = SampleService.class)
@ImportHttpServices(group = "demo", types = DemoService.class)
@Configuration
class HttpServiceConfig {

	@Bean
	RestClientCustomizer restClientCustomizer() {
		return builder -> builder.defaultStatusHandler(HttpStatusCode::isError, (_, res) -> {
			switch (res.getStatusCode()) {
				case HttpStatus.NOT_FOUND -> throw new RestClientException("Resource Not Found.");
				case HttpStatus.UNAUTHORIZED -> throw new RestClientException("Unauthorized.");
				case HttpStatus.BAD_REQUEST  -> throw new RestClientException("Invalid request.");
				default -> throw new RestClientException("HTTP error: " + res.getStatusCode().toString());
			}
		});
	}

}
