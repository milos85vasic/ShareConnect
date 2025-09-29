# AGENTS.md

## Build/Lint/Test Commands
- Build debug APK: `./build_app.sh` or `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease`
- Clean build: `./gradlew clean`
- Run all tests: `./run_all_tests.sh`
- Run unit tests: `./run_unit_tests.sh`
- Run instrumentation tests: `./run_instrumentation_tests.sh`
- Run automation tests: `./run_automation_tests.sh`
- Run single test: `./gradlew test --tests "com.shareconnect.ProfileManagerTest"`
- Run single instrumentation test: `./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.DatabaseMigrationTest"`
- Lint code: `./gradlew lint`
- Kotlin analysis: `./gradlew detekt`

## Code Style Guidelines
- **Naming**: PascalCase for classes/types, camelCase for variables/functions, UPPER_CASE for constants.
- **Imports**: Group by standard library, third-party, then local; use wildcards sparingly.
- **Formatting**: 4 spaces indentation, max line length 120; follow Kotlin style guide.
- **Types**: Prefer explicit types for public APIs; use val over var; avoid nullable types unless necessary.
- **Error Handling**: Use try-catch for exceptions, Result/ sealed classes for operations; log errors appropriately.
- **Android/Kotlin**: Follow Material Design 3; use Compose for UI; handle lifecycle properly; encrypt sensitive data with SQLCipher.