package com.example.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.resilience.annotation.EnableResilientMethods;

@SpringBootApplication
@EnableResilientMethods
public class SpringHttpClientApplication {

	public static void main(final String[] args) {
		SpringApplication.run(SpringHttpClientApplication.class, args);
	}

}
