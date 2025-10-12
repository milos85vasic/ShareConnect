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

## Version Requirements
- **Android Gradle Plugin (AGP)**: 8.9.1
- **Kotlin**: 2.0.0
- **KSP**: 2.0.0-1.0.21
- **Gradle**: 8.14.3
- **Java**: 17
- **Compile SDK**: 36
- **Target SDK**: 36
- **Min SDK**: 26 (TransmissionConnect), 21 (qBitConnect), 28 (Application)

## Code Style Guidelines
- **Naming**: PascalCase for classes/types, camelCase for variables/functions, UPPER_CASE for constants.
- **Imports**: Group by standard library, third-party, then local; use wildcards sparingly.
- **Formatting**: 4 spaces indentation, max line length 120; follow Kotlin style guide.
- **Types**: Prefer explicit types for public APIs; use val over var; avoid nullable types unless necessary.
- **Error Handling**: Use try-catch for exceptions, Result/ sealed classes for operations; log errors appropriately.
- **Android/Kotlin**: Follow Material Design 3; use Compose for UI; handle lifecycle properly; encrypt sensitive data with SQLCipher.

## Recent Fixes
- **uTorrentConnect**: Applied fixes for gRPC port binding crashes and blurry splash screen logos (same as TransmissionConnect). Updated `generate_icons.sh` for uTorrentConnector paths and 1024x1024 resolution splash logos. Modified `splash_background.xml` to reference `@drawable/splash_logo`. Fixed clientTypeFilter to use `TORRENT_CLIENT_UTORRENT` instead of `TORRENT_CLIENT_TRANSMISSION`.
- **qBitConnector**: Fixed build issues by adding `sourceSets` configuration in `build.gradle` to exclude Java source directories. Made `setActiveServer()` function suspend in `ServerRepository.kt` for coroutine compatibility.
- **Port Binding**: Fixed gRPC port binding crashes by implementing unique port calculation per app ID with port availability checking across all sync managers (RSSSyncManager, ProfileSyncManager, BookmarkSyncManager, HistorySyncManager, ThemeSyncManager, PreferencesSyncManager, TorrentSharingSyncManager). Each app now calculates a preferred port using `basePort + Math.abs(appId.hashCode() % 100)` and then finds the next available port if the preferred one is in use. Added logging to show which port each app is using. This prevents BindException crashes when multiple apps try to use the same port.
- **Constructor Parameters**: Fixed compilation errors in sync managers by adding missing `appName` and `appVersion` constructor parameters to PreferencesSyncManager, RSSSyncManager, and TorrentSharingSyncManager. Updated getInstance methods to properly pass these parameters to constructors.
- **Build Stability**: All apps (ShareConnector, TransmissionConnector, uTorrentConnector, qBitConnector) now build successfully with `./assembleDebug`, producing APK files without errors. Kotlin compilation passes for all sync managers. Unit tests pass with 100% success rate.