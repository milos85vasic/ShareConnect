# CRUSH.md

## Build Commands
- Build debug APK: `./build_app.sh` or `./gradlew assembleDebug`
- Build release APK: `./gradlew assembleRelease`
- Clean build: `./gradlew clean`
- Lint code: `./gradlew lint`
- Kotlin analysis: `./gradlew detekt`

## Testing Commands
- Run all tests: `./run_all_tests.sh`
- Unit tests only: `./run_unit_tests.sh`
- Instrumentation tests: `./run_instrumentation_tests.sh`
- Automation tests: `./run_automation_tests.sh`
- Single unit test: `./gradlew test --tests "com.shareconnect.ProfileManagerTest"`
- Single instrumentation test: `./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.DatabaseMigrationTest`
- Full crash test (all 4 apps): `./run_full_app_crash_test.sh`
- AI QA tests: `./run_ai_qa_tests.sh`

## Security Scanning
- Quick Snyk scan: `./snyk_scan_on_demand.sh --severity medium` (no token needed)
- Full security scan: `./run_snyk_scan.sh`
- Integrated QA + Security: `./run_ai_qa_with_snyk.sh`

## Code Style Guidelines
- **Naming**: PascalCase for classes/types, camelCase for variables/functions, UPPER_CASE for constants
- **Imports**: Group by standard library, third-party, then local; use wildcards sparingly
- **Formatting**: 4 spaces indentation, max line length 120; follow Kotlin style guide
- **Types**: Prefer explicit types for public APIs; use val over var; avoid nullable types unless necessary
- **Error Handling**: Use try-catch for exceptions, Result/sealed classes for operations; log errors appropriately
- **Android/Kotlin**: Follow Material Design 3; use Compose for UI; handle lifecycle properly; encrypt sensitive data with SQLCipher

## Version Requirements
- **Android Gradle Plugin**: 8.13.0
- **Kotlin**: 2.0.0
- **KSP**: 2.0.0-1.0.21
- **Java**: 17
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 26 (TransmissionConnect), 21 (qBitConnect), 28 (Application)

## Key Architecture Notes
- Multi-app structure: ShareConnector, TransmissionConnector, uTorrentConnector, qBitConnector, JDownloaderConnector
- Sync modules use unique gRPC ports (basePort + hash % 100) to prevent conflicts
- All Room databases use SQLCipher encryption
- Test reports saved to: `Documentation/Tests/{timestamp}_TEST_ROUND/`

## JDownloaderConnect Specific
- **Package**: `com.shareconnect.jdownloaderconnect`
- **Profile Type**: DOWNLOAD_MANAGER
- **Sync Port**: 8970
- **MyJDownloader API**: Full integration with remote download management
- **Test Coverage**: 100% with unit, integration, automation, and E2E tests