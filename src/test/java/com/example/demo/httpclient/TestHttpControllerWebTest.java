package com.example.demo.httpclient;

import static org.assertj.core.api.SoftAssertions.assertSoftly;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class TestHttpControllerWebTest {

	@Autowired
	MockMvcTester mockMvcTester;

	@Autowired
	OrchestrationService orchestrationService;

	@Autowired
	SampleService sampleService;

	@Autowired
	DemoService demoService;

	@Autowired
	MockService mockService;

	@BeforeEach
	void init() {
		assertSoftly(softly -> {
			softly.assertThat(mockMvcTester).isNotNull();
			softly.assertThat(orchestrationService).isNotNull();
			softly.assertThat(sampleService).isNotNull();
			softly.assertThat(demoService).isNotNull();
		});
	}

	@Test
	void test() {
		// TODO: mockMvcTest 를 통해 /http/sample/{id} 및 /http/demo/{id} 를 호출하여
		// /sample/{id} 및 /demo/{id} 호출시 api versioning 값이 제대로 전달되는지 확인할 것.

	}

}
