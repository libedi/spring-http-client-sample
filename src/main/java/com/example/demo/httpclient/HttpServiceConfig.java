package com.example.demo.httpclient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.springframework.boot.restclient.RestClientCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ApiVersionInserter;
import org.springframework.web.client.RestClientException;
import org.springframework.web.service.registry.ImportHttpServices;

@ImportHttpServices(group = "sample", types = SampleService.class)
@ImportHttpServices(group = "demo", types = DemoService.class)
@Configuration
class HttpServiceConfig {

	@Bean
	ClientHttpRequestInterceptor loggingInterceptor() {
		return (request, body, execution) -> {
			System.out.println("Request: " + request.getMethod() + " " + request.getURI());
			System.out.println("Headers: " + request.getHeaders());
			System.out.println("Body: " + new String(body));

			final ClientHttpResponse response = execution.execute(request, body);

			final ByteArrayOutputStream baos = new ByteArrayOutputStream();
			response.getHeaders().forEach((_, value) -> {
				try {
					baos.write(value.toString().getBytes());
				} catch (final IOException e) {
					throw new RuntimeException(e);
				}
			});
			System.out.println("Response Headers: " + response.getHeaders());
			return response;
		};
	}

	@Bean
	RestClientCustomizer restClientCustomizer() {
		return builder -> builder.defaultStatusHandler(HttpStatusCode::isError, (_, res) -> {
			switch (res.getStatusCode()) {
				case HttpStatus.NOT_FOUND -> throw new RestClientException("Resource Not Found.");
				case HttpStatus.UNAUTHORIZED -> throw new RestClientException("Unauthorized.");
				case HttpStatus.BAD_REQUEST -> throw new RestClientException("Invalid request.");
				default -> throw new RestClientException("HTTP error: " + res.getStatusCode().toString());
			}
		}).requestInterceptor(loggingInterceptor())
//		  .apiVersionInserter(ApiVersionInserter.useHeader("X-VERSION"))
//		  .defaultApiVersion("1.0.0")
		  ;
	}

}
