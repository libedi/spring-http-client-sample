package com.example.demo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.assertj.core.api.SoftAssertions.assertSoftly;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willAnswer;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.times;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import autoparams.AutoParams;

@SpringBootTest(webEnvironment = WebEnvironment.DEFINED_PORT)
@ActiveProfiles("test")
@Import(TestControllerConfig.class)
class OrchestrationServiceTest {

	@Autowired
	OrchestrationService orchestrationService;

	@Autowired
	SampleService sampleService;

	@Autowired
	DemoService demoService;

	@MockitoBean
	MockService mockService;

	@BeforeEach
	void init() {
		assertSoftly(softly -> {
			softly.assertThat(orchestrationService).isNotNull();
			softly.assertThat(sampleService).isNotNull();
			softly.assertThat(demoService).isNotNull();
		});
	}

	@Test
	@AutoParams
	void test(final String id) {
		// when
		final String actual = sampleService.get(id);
		final String post = demoService.post(id);

		// then
		assertSoftly(softly -> {
			softly.assertThat(actual).isEqualTo(id);
			softly.assertThat(post).isEqualTo(id);
		});
	}

	@Test
	@AutoParams
	void retry(final String id) {
		// given
		willThrow(IllegalArgumentException.class).given(mockService).something(id);

		// when
		assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(() -> orchestrationService.retry(id));

		// then
		then(mockService).should(times(4)).something(id);
	}

	@Test
	@Timeout(20)
	void limit() throws Exception {
		// @ConcurrencyLimit(5) 검증: limit() 메소드 본문(= mockService.something 호출 구간)에
		// 동시에 진입 가능한 호출 수가 5를 넘지 않아야 한다.
		// 주의: 애노테이션은 Spring AOP 프록시에서 동작하므로 @SpringBootTest 통합 테스트로 검증한다.
		final int limit = 5;
		final int tasks = 30;

		// 모든 작업이 최대한 동시에 limit()을 호출하도록 출발 시점을 맞춘다.
		final CyclicBarrier startTogether = new CyclicBarrier(tasks);
		// virtual thread 기반으로 많은 동시 호출을 저비용으로 생성한다.
		final ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

		// inFlight/maxInFlight: 현재 동시 진입 수 / 관측된 최대 동시 진입 수
		final AtomicInteger inFlight = new AtomicInteger();
		final AtomicInteger maxInFlight = new AtomicInteger();
		// firstBatchEntered: 최소 limit(=5)개의 호출이 실제로 진입했는지 확인(동시성 상황이 성립했는지)
		final CountDownLatch firstBatchEntered = new CountDownLatch(limit);
		// mockService 내부에서 의도적으로 블로킹시켜 “동시 진입 수”를 안정적으로 관측한다.
		// @ConcurrencyLimit의 기본 동작은 초과 호출을 거부하지 않고 대기(block)시키는 것이므로,
		// release 전에는 최대 동시 진입 수가 limit를 넘지 않아야 한다.
		final CountDownLatch release = new CountDownLatch(1);

		// limit()이 호출하는 mockService.something()을 “진입 카운팅 + 블로킹”으로 구성한다.
		willAnswer(_ -> {
			// limit() -> mockService.something()으로 진입한 호출이 동시에 몇 개까지 들어오는지 측정한다.
			final int now = inFlight.incrementAndGet();
			maxInFlight.accumulateAndGet(now, Math::max);
			// 현재 들어온 호출이 limit 범위(<=5)라면 “첫 5개 진입 완료” 신호를 보낸다.
			if (now <= limit) {
				firstBatchEntered.countDown();
			}
			try {
				// 테스트가 release할 때까지 대기(초과 호출은 여기 진입 전에 대기해야 함)
				if (!release.await(5, TimeUnit.SECONDS)) {
					throw new TimeoutException("Timed out waiting for test to release mock");
				}
			} finally {
				// 진입 카운트 정리
				inFlight.decrementAndGet();
			}
			return null;
		}).given(mockService).something(anyString());

		try {
			// tasks 개의 동시 호출을 만들어 limit()에 몰아넣는다.
			final List<Future<?>> futures = new ArrayList<>(tasks);
			for (int i = 0; i < tasks; i++) {
				final String id = "id-" + i;
				futures.add(executor.submit(() -> {
					// startTogether에서 대기했다가 동시에 limit() 호출을 시작한다.
					startTogether.await(5, TimeUnit.SECONDS);
					orchestrationService.limit(id);
					return null;
				}));
			}

			// 최소 5개는 실제로 본문까지 진입해야 한다(동시 호출이 제대로 걸렸는지 확인).
			assertThat(firstBatchEntered.await(5, TimeUnit.SECONDS)).as("first 5 calls entered").isTrue();
			// release 전 시점에서 최대 동시 진입 수가 limit(5)를 넘지 않아야 한다.
			assertThat(maxInFlight.get()).as("max in-flight calls").isLessThanOrEqualTo(limit);

			// 블로킹을 풀어 나머지 호출도 완료시키고, 전체 완료를 기다린다.
			release.countDown();

			for (final Future<?> future : futures) {
				future.get(10, TimeUnit.SECONDS);
			}

			assertSoftly(softly -> {
				// 전체 완료 후에도 최대 동시 진입 수는 limit를 넘지 않아야 한다.
				softly.assertThat(maxInFlight.get()).as("max in-flight calls").isLessThanOrEqualTo(limit);
				// 테스트 종료 시점에는 블로킹된 호출이 남아있으면 안 된다.
				softly.assertThat(inFlight.get()).as("in-flight after completion").isZero();
			});

			// 결과적으로 모든 호출은 성공적으로 mockService.something()까지 도달해야 한다(대기 후 순차 진입).
			then(mockService).should(times(tasks)).something(anyString());
		} finally {
			// 테스트가 중간 실패하더라도 대기 중인 호출이 남지 않도록 release 및 executor 종료
			release.countDown();
			executor.shutdownNow();
		}
	}

}
