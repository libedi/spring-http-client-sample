package com.example.demo.httpclient;

import java.util.concurrent.TimeUnit;

import org.springframework.resilience.annotation.ConcurrencyLimit;
import org.springframework.resilience.annotation.Retryable;
import org.springframework.stereotype.Service;

@Service
public class OrchestrationService {

	private final SampleService sampleService;
	private final DemoService demoService;
	private final MockService mockService;

	OrchestrationService(final SampleService sampleService, final DemoService demoService,
			final MockService mockService) {
		this.sampleService = sampleService;
		this.demoService = demoService;
		this.mockService = mockService;
	}

	public String getSample(final String id) {
		return sampleService.get(id);
	}

	public String createDemo(final String id) {
		return demoService.post(id);
	}

	@Retryable(includes = IllegalArgumentException.class, // retry 하기 위한 예외 설정
			maxRetries = 3, // 재시도 횟수 (총 시도횟수는 1 + maxRetries)
			delay = 10L, // 재시도 전 지연시간 (backoff 기능)
			multiplier = 1.5, // 재시도마다 delay 를 늘리기 위해 곱해지는 값 (backoff 기능)
			maxDelay = 50L, // 최대 재시도 지연시간 (backoff 기능)
			jitter = 10L, // dalay 에 랜덤으로 +/- jitter (backoff 기능)
			timeUnit = TimeUnit.MILLISECONDS // (backoff 기능)
	)
	public String retry(final String id) {
		mockService.something(id);
		return id;
	}

	@ConcurrencyLimit(5)
	public String limit(final String id) {
		mockService.something(id);
		return id;
	}
}
