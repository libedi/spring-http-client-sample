# spring-http-client-sample

Spring Boot 4 기반의 HTTP Client 샘플 프로젝트입니다. `spring-boot-starter-restclient`와 Spring의 HTTP Interface(`@GetExchange`, `@PostExchange`)를 사용해 외부 서비스를 호출하고, `@Retryable`, `@ConcurrencyLimit` 같은 회복성(resilience) 애노테이션 적용 예시를 제공합니다.

## Features
- HTTP Interface 기반 클라이언트: `SampleService`, `DemoService`
- `@ImportHttpServices`로 그룹별 HTTP 서비스 등록 (`sample`, `demo`)
- 기본 HTTP 에러 처리 커스터마이징: `HttpServiceConfig#restClientCustomizer()`
- Virtual Threads 활성화(`application.yml`)
- Resilience 애노테이션 예시
  - `OrchestrationService#retry()` → `@Retryable`
  - `OrchestrationService#limit()` → `@ConcurrencyLimit(5)`

## Requirements
- Java 25 (Gradle Toolchain 사용: `build.gradle` 참고)

## Run
```bash
./gradlew bootRun
# Windows
gradlew.bat bootRun
```

## Test
```bash
./gradlew test
./gradlew test --tests com.example.demo.OrchestrationServiceTest
```

## Configuration
- 메인 설정: `src/main/resources/config/application.yml`
- HTTP 서비스 그룹 설정:
  - `spring.http.serviceclient.demo.base-url`
  - `spring.http.serviceclient.sample.base-url`
- Virtual Threads:
  - `spring.threads.virtual.enabled: true`

## Project Structure
- `src/main/java/com/example/demo`
  - `*Service.java`: HTTP Interface 및 오케스트레이션 서비스
  - `HttpServiceConfig.java`: HTTP 서비스/RestClient 커스터마이징
- `src/test/java/com/example/demo`
  - `OrchestrationServiceTest.java`: Retry/ConcurrencyLimit 통합 테스트
  - `TestControllerConfig.java`: 테스트용 컨트롤러 제공

## Notes
이 프로젝트의 `@ConcurrencyLimit`은 초과 호출을 거부하지 않고 대기(block)시키는 방식으로 동작합니다. 테스트에서는 가상 스레드로 동시 호출을 발생시켜 “동시 진입 최대치가 제한을 넘지 않는지”를 검증합니다.
