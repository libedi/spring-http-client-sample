# Repository Guidelines

## Project Structure & Module Organization
- `src/main/java/com/example/demo`: Spring Boot application code, HTTP client services, and controllers.
- `src/main/resources/config/application.yml`: main configuration (HTTP service base URLs, virtual threads).
- `src/test/java/com/example/demo`: integration-style tests and web tests.
- `bin/`: compiled output from local builds; do not edit or commit.

## Architecture Overview
- HTTP interfaces (`SampleService`, `DemoService`) define external calls via Spring’s `@GetExchange`/`@PostExchange`.
- `HttpServiceConfig` wires RestClient defaults and registers service groups.
- `OrchestrationService` composes calls and applies resilience annotations like `@Retryable` and `@ConcurrencyLimit`.
- Controllers in `api/` and `httpclient/` expose endpoints for manual or test-driven verification.

## Build, Test, and Development Commands
- `./gradlew build`: compile and run tests.
- `./gradlew bootRun`: start the app locally.
- `./gradlew test`: run the full test suite.
- `./gradlew test --tests com.example.demo.OrchestrationServiceTest`: run a single test class.
- Windows equivalent: `gradlew.bat <task>`.

## Coding Style & Naming Conventions
- Language: Java 25 via Gradle toolchain.
- Indentation: follow existing files (tabs are used in current Java sources).
- Naming: `PascalCase` for classes, `camelCase` for methods, `*Test` suffix for tests.
- No formatter or linter is configured; avoid reformatting unrelated code.

## Testing Guidelines
- Frameworks: JUnit 5 (JUnit Platform), Spring Boot test starters, Mockito, AssertJ, AutoParams.
- Tests live under `src/test/java`; keep tests close to the package they validate.
- Prefer focused tests that exercise HTTP interface behavior and resilience annotations.

## Commit & Pull Request Guidelines
- Commit history uses short, imperative messages (e.g., "Add README"); follow that style.
- Pull requests should include: concise summary, linked issue (if any), and test results.
- Note any configuration changes under `src/main/resources/config/application.yml`.

## Configuration & Runtime Notes
- HTTP client base URLs are defined under `spring.http.serviceclient.*.base-url`.
- Virtual threads are enabled; avoid blocking calls in performance-critical paths.
