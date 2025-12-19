# Repository Guidelines

## Project Structure & Module Organization
- Gradle-based Spring Boot sample in a single module.
- Source: `src/main/java/com/example/demo` (HTTP interfaces, configuration, services).
- Resources: `src/main/resources/config/application.yml` (HTTP client + app settings); `src/main/resources/static` and `src/main/resources/templates` are present for web assets/views.
- Tests: `src/test/java/com/example/demo` (Spring Boot integration tests and test-only controller config).

## Build, Test, and Development Commands
Use the Gradle wrapper so everyone builds consistently:
- Build + run all checks: `./gradlew build` (Windows: `gradlew.bat build`).
- Run tests only: `./gradlew test`.
- Run a single test class: `./gradlew test --tests com.example.demo.OrchestrationServiceTest`.
- Run the app locally: `./gradlew bootRun` (uses `application.yml`).

Notes:
- The project uses a Java toolchain (`JavaLanguageVersion.of(25)` in `build.gradle`). Ensure JDK 25 is available or allow Gradle toolchains to provision it.

## Coding Style & Naming Conventions
- Java package naming follows reverse-domain style (currently `com.example.demo`).
- Prefer `UpperCamelCase` for types and `lowerCamelCase` for members.
- Follow existing code style: tabs for indentation and `final` for method parameters where used.
- Keep Spring HTTP interface methods minimal and annotated (`@GetExchange`, `@PostExchange`) to match existing `SampleService`/`DemoService` patterns.

## Testing Guidelines
- Frameworks: JUnit 5 (`useJUnitPlatform()`), Spring Boot Test, AssertJ, Mockito, and AutoParams.
- Naming: `*Test`/`*Tests` under `src/test/java`.
- Favor Spring-focused tests when exercising HTTP interfaces; `TestControllerConfig` shows a lightweight in-test controller pattern.

## Commit & Pull Request Guidelines
- This workspace does not include Git history; use clear, imperative commit subjects (e.g., `Add retry test for OrchestrationService`).
- PRs should include: purpose, how to verify (commands + expected result), and any config changes (e.g., updates to `application.yml`).

## Configuration Tips
- HTTP client groups are imported via `@ImportHttpServices(group = "sample"|"demo", ...)` and configured under `spring.http.serviceclient.<group>` in `application.yml`. Keep group names in code and config in sync.
