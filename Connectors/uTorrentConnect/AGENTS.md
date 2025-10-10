# AGENTS.md

This file provides guidance to agentic coding agents working in this Android Transmission Connect repository.

## Build, Lint, and Test Commands

- Build debug: `./gradlew assembleDebug`
- Build release: `./gradlew assembleRelease`
- Run all tests: `./gradlew test`
- Run single test: `./gradlew test --tests "com.example.TestClass.testMethod"`
- Run instrumentation tests: `./gradlew connectedAndroidTest`
- Lint check: `./gradlew lint`
- Clean build: `./gradlew clean`

## Code Style Guidelines

- **Language**: Primarily Java with Kotlin components; maintain file type consistency.
- **Imports**: Use explicit imports; avoid wildcards.
- **Formatting**: 4 spaces indentation; max line length 100-120 chars.
- **Naming**: CamelCase for classes/methods/variables; UPPER_CASE for constants.
- **Types**: Use explicit types where possible; prefer Kotlin data classes for models.
- **Error Handling**: Use try-catch with proper logging; avoid silent failures.
- **Architecture**: Follow MVVM with Hilt DI; use Material Design components.
- **Comments**: Add Javadoc for public methods; avoid unnecessary comments.