# Repository Guidelines

## Library Concepts
- No alias: avoid type or method aliases; expose clear, direct APIs.
- 100% Coverage: aim for full test coverage on all changes.

## Project Structure & Module Organization
- This is a multi-module Gradle project. The primary modules are `packages/core` and `packages/fp`.
- Standard layout is `packages/<module>/src/main/java` for source and `packages/<module>/src/test/java` for tests.
- Examples: `packages/core/src/main/java/babysteps/core`, `packages/fp/src/test/java/babysteps/fp`.

## Build, Test, and Development Commands
- `./gradlew build`: Builds and tests all modules and applies formatting (Spotless).
- `./gradlew test`: Runs unit tests (JUnit 5) only.
- `./gradlew spotlessApply`: Formats Java with Google Java Format.
- `./gradlew jacocoTestReport`: Generates coverage reports (XML/HTML).

## Coding Style & Naming Conventions
- Java toolchain is set to Java 21.
- Indentation is 2 spaces for Gradle files; Java is formatted via Google Java Format.
- Package names follow `babysteps.*`. Class names use PascalCase; methods use camelCase.

## Testing Guidelines
- Tests use JUnit 5 with AssertJ.
- Test classes should be named `<TargetClassName>Test` and live under `src/test/java`.
- Examples: `EitherTest`, `FunctionsTest`.
- CI enforces coverage diffs via Codecov, so add tests for even small changes.

## Commit & Pull Request Guidelines
- Recent history follows Conventional Commits (e.g., `test(core): add ...`). Please follow when possible.
- PRs should include a summary, linked issues, and test results (command + outcome).
- For API or behavior changes, document impact and migration notes.

## Configuration Tips
- Dependencies and quality tooling are centralized in the root `build.gradle`.
- `codecov.yml` enables CI pass requirements and coverage diff checks.
