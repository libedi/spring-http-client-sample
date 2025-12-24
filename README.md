# spring-http-client-sample

Spring Boot 4 기반의 HTTP Client 샘플 프로젝트입니다. `spring-boot-starter-restclient`와 Spring의 HTTP Interface(`@GetExchange`, `@PostExchange`)를 사용해 외부 서비스를 호출하고, `@Retryable`, `@ConcurrencyLimit` 같은 회복성(resilience) 애노테이션 적용 예시를 제공합니다.

## Features
- HTTP Interface 기반 클라이언트: `SampleService`, `DemoService`
- `@ImportHttpServices`로 그룹별 HTTP 서비스 등록 (`sample`, `demo`)
- 기본 HTTP 에러 처리 및 로깅 인터셉터 커스터마이징 (`HttpServiceConfig`)
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

## Endpoints
- GET `/sample/{id}`: API 엔드포인트 (id 반환, version=1.0.0)
- POST `/demo?id=...`: API 엔드포인트 (id 반환, version=1.0.0)

## API 버저닝
이 프로젝트는 Spring MVC API 버저닝을 사용합니다.
- 지원 버전: 1.0.0, 1.1.0, 1.2.0
- 입력 방식: `X-VERSION` 헤더, `version` 쿼리 파라미터, media-type 파라미터
컨트롤러는 `version = "1.0.0"`으로 명시되어 있습니다.
아웃바운드 클라이언트 버저닝:
- sample 클라이언트: `X-VERSION` 헤더로 버전 삽입
- demo 클라이언트: `version` 쿼리 파라미터로 버전 삽입

## HTTP 클라이언트 설정
- 기본 타임아웃: read/connect 10s
- sample 클라이언트 오버라이드: read/connect 5s
- base-url: `spring.http.serviceclient.sample.base-url`, `spring.http.serviceclient.demo.base-url`

## 로깅/관찰
`HttpServiceConfig`에서 요청/응답 로깅 인터셉터와 기본 에러 처리 핸들러를 등록합니다.
`org.springframework` 디버그 로그가 활성화되어 있습니다.

## 회복성(Resilience)
- `@Retryable`: `IllegalArgumentException` 대상, 최대 3회 재시도, backoff 적용
- `@ConcurrencyLimit(5)`: 초과 요청은 거부가 아닌 대기(block) 방식

## Project Structure
- `src/main/java/com/example/demo`
  - `SpringHttpClientApplication.java`: Spring Boot 시작점
- `src/main/java/com/example/demo/api`
  - `TestApiController.java`: 테스트용 API 엔드포인트 제공
- `src/main/java/com/example/demo/httpclient`
  - `SampleService.java`, `DemoService.java`: HTTP Interface 정의
  - `OrchestrationService.java`: 호출 오케스트레이션 및 Resilience 적용
  - `HttpServiceConfig.java`: RestClient 커스터마이징
  - `MockService.java`, `MockServiceImpl.java`: 테스트/예제용 Mock 서비스
- `src/test/java/com/example/demo`
  - `SpringHttpClientApplicationTests.java`: 컨텍스트 로드 테스트
- `src/test/java/com/example/demo/httpclient`
  - `OrchestrationServiceTest.java`: Retry/ConcurrencyLimit 통합 테스트

## Notes
이 프로젝트의 `@ConcurrencyLimit`은 초과 호출을 거부하지 않고 대기(block)시키는 방식으로 동작합니다. 테스트에서는 가상 스레드로 동시 호출을 발생시켜 “동시 진입 최대치가 제한을 넘지 않는지”를 검증합니다.
