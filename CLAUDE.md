# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**ShareConnect** - An Android application that allows sharing media links from streaming services and download sources directly to local services including MeTube, YT-DLP, torrent clients, and jDownloader.

**Package**: `com.shareconnect`
**Version**: 1.1.0
**Target**: Android API 28-36

## Build Commands

### Building
```bash
# Build debug APK (recommended)
./build_app.sh
# or
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

APK location: `ShareConnector/build/outputs/apk/debug/ShareConnector-debug.apk`

### Testing
```bash
# Run all tests (unit + crash tests)
./run_all_tests.sh

# Unit tests only
./run_unit_tests.sh

# Instrumentation tests (requires emulator/device)
./run_instrumentation_tests.sh

# Automation/UI tests (requires emulator/device)
./run_automation_tests.sh

# Full crash test suite (tests all 4 apps)
./run_full_app_crash_test.sh

# Single test
./gradlew test --tests "com.shareconnect.ProfileManagerTest"

# Single instrumentation test
./gradlew :ShareConnector:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.automation.ProfilesListScrollingAutomationTest
```

Test reports are saved to: `Documentation/Tests/{timestamp}_TEST_ROUND/`

## Architecture

### Multi-Application Structure

ShareConnect consists of **4 Android applications** that share components and sync data via Asinka:

1. **ShareConnector** (`ShareConnector/`) - Main app for sharing media links
2. **TransmissionConnector** (`Connectors/TransmissionConnect/`) - Torrent client integration
3. **uTorrentConnector** (`Connectors/uTorrentConnect/`) - uTorrent client integration
4. **qBitConnector** (`Connectors/qBitConnect/`) - qBittorrent client integration

All apps sync profiles, themes, history, RSS feeds, bookmarks, preferences, and torrent sharing data in real-time using Asinka.

### Core Modules

#### Synchronization Modules (all use Asinka)
- **ThemeSync** - Theme preferences and custom themes (port 8890)
- **ProfileSync** - Service profiles across apps (port 8900)
- **HistorySync** - Sharing history (port 8910)
- **RSSSync** - RSS feed subscriptions (port 8920)
- **BookmarkSync** - Media bookmarks (port 8930)
- **PreferencesSync** - App preferences (port 8940)
- **LanguageSync** - Language settings (port 8950)
- **TorrentSharingSync** - Torrent sharing data (port 8960)

Each sync module:
- Uses unique gRPC port (basePort + hash % 100)
- Extends AsinkaClient for real-time sync
- Manages Room database with SQLCipher encryption
- Broadcasts changes to all connected apps

#### UI & Design Modules
- **DesignSystem** - Shared Material Design 3 components, themes, design tokens
- **Onboarding** - First-run experience flow
- **Localizations** - Multi-language support

#### Infrastructure Modules
- **Asinka** (`Asinka/asinka/`) - gRPC-based IPC library for real-time object sync between apps
- **Toolkit** (`Toolkit/`) - Shared utilities, analytics, media handling, interprocess communication
- **Tests** - Shared test utilities and suites

#### Service Connectors
- **qBitConnector** - qBittorrent Web API client
- **TransmissionConnector** - Transmission RPC client
- **uTorrentConnector** - uTorrent Web UI client

### Data Layer

**Room Database with SQLCipher encryption** for:
- Themes (custom color schemes)
- Profiles (service configurations with credentials)
- History (shared links with metadata)
- RSS feeds
- Bookmarks
- Preferences

**Asinka Sync** enables real-time synchronization:
- Auto-discovery between installed apps
- Bi-directional sync via gRPC
- Encrypted transport (TLS)
- Conflict resolution
- Event broadcasting

### Port Allocation Strategy

Each sync manager uses a unique basePort to prevent gRPC binding conflicts:
- Calculate preferred port: `basePort + Math.abs(appId.hashCode() % 100)`
- Find next available port if preferred is in use
- Log which port each manager uses on startup

**CRITICAL**: Never reuse port ranges. Each new sync manager needs a unique basePort (increments of 10).

### UI Architecture

- **Material Design 3** with Jetpack Compose
- **6 built-in themes** + custom theme creator
- **Adaptive layouts** for different screen sizes
- **Visual profile management** with grid layout, icons, default indicators
- **Rich media metadata** extraction (titles, descriptions, thumbnails)
- **Comprehensive history** with filtering by service/type

### Security

- **SQLCipher encryption** for all Room databases
- **HTTPS** for service communications
- **Secure credential storage** for authenticated services
- **App signature verification** during Asinka discovery
- **Per-object access control** in sync operations

## Development Workflow

### Version Requirements
- **Android Gradle Plugin**: 8.13.0
- **Kotlin**: 2.0.0
- **KSP**: 2.0.0-1.0.21
- **Java**: 17
- **Compile SDK**: 36
- **Min SDK**: 28 (ShareConnector), 26 (Transmission), 21 (qBit)

### Code Organization

**Main application**: `ShareConnector/src/main/kotlin/com/shareconnect/`
- `activities/` - MainActivity, SettingsActivity, HistoryActivity, etc.
- `database/` - Room database and DAOs
- `utils/` - UrlCompatibilityUtils, DialogUtils, ServiceApiClient
- `automation/` - UI automation tests

**Sync modules**: `{ModuleName}Sync/src/main/kotlin/com/shareconnect/{modulename}sync/`
- `{ModuleName}SyncManager.kt` - Main sync coordinator extending AsinkaClient
- Database entities and DAOs
- AndroidManifest.xml with required permissions

### Adding New Sync Module

1. Create module in `settings.gradle`
2. Create `{Name}SyncManager` extending AsinkaClient
3. Assign unique basePort (next available in sequence, increment by 10)
4. Implement Room database with SQLCipher
5. Add to all 4 application dependencies
6. Initialize in each app's Application class

### Testing Strategy

**Unit Tests** (`src/test/`):
- Business logic validation
- Data model testing
- URL compatibility testing
- Profile management
- Theme operations

**Instrumentation Tests** (`src/androidTest/`):
- Database operations
- Room migrations
- Repository integration
- Activity lifecycle

**Automation Tests** (`src/androidTest/automation/`):
- End-to-end UI flows
- Profile management workflows
- Theme changes
- History operations
- Accessibility compliance

**Crash Tests**:
- App launch verification across all 4 apps
- Restart stability
- Asinka sync operation validation
- Port binding conflict detection

### Authentication Support

Supports multiple authentication methods:
- **qBittorrent**: Cookie-based auth with automatic login
- **Transmission**: Session ID handling with auto-retry
- **jDownloader**: My.JDownloader API
- **Basic Auth**: Standard HTTP authentication

Credentials stored encrypted in Room database.

### Service Type Detection

Uses `UrlCompatibilityUtils` to detect:
- Streaming services (1800+ sites via yt-dlp)
- File hosting (MediaFire, Mega, Google Drive, etc.)
- Premium links (RapidGator, Uploaded, etc.)
- Torrents (magnet links, .torrent files)
- Archives (RAR, 7Z, ZIP, TAR)
- Direct downloads

## Common Patterns

### Sync Manager Initialization
```kotlin
val syncManager = ThemeSyncManager.getInstance(context, appId, appName, appVersion)
syncManager.startSync()
```

### Profile Management
```kotlin
val profileManager = ProfileManager.getInstance(context)
profileManager.addProfile(serverProfile)
profileManager.setDefaultProfile(profileId)
```

### Theme Application
```kotlin
val themeSyncManager = ThemeSyncManager.getInstance(context)
themeSyncManager.setTheme(themeId, isDark)
```

## Known Issues & Solutions

### Port Binding Conflicts
If apps crash with `BindException`:
- Check each sync manager has unique basePort
- Ensure port calculation uses proper range (basePort + 0-99)
- Verify `findAvailablePort()` helper exists

### Onboarding Race Conditions
MainActivity must check for existing sync data synchronously before deciding to show onboarding. Never finish() immediately after splash without checking.

### Build Issues
- **Kotlin version conflicts**: Force Kotlin 2.0.0 in `configurations.all`
- **Room KSP**: Use KSP 2.0.0-1.0.21 with Room 2.8.1
- **gRPC conflicts**: Force consistent gRPC versions (1.57.1)

### Test Failures
- **Robolectric**: Configure `robolectric.properties` with SDK 28
- **Firebase**: Use TestApplication that doesn't initialize Firebase
- **Resources**: Enable `includeAndroidResources = true` in testOptions

## Key Files

- `build.gradle.kts` - Root build configuration with version definitions
- `settings.gradle` - Module includes and repository configuration
- `ShareConnector/build.gradle` - Main app dependencies and signing
- `run_*.sh` - Test execution scripts with comprehensive reporting
- `AGENTS.md` - Detailed recent fixes and test coverage documentation

## Environment Configuration

Create `env.properties` in project root (gitignored) for:
```
SHARECONNECT_DEV_KEYSTORE_PATH=Signing/dev.jks
SHARECONNECT_DEV_KEY_ALIAS=...
SHARECONNECT_DEV_KEY_PASSWORD=...
SHARECONNECT_DEV_STORE_PASSWORD=...
SHARECONNECT_CLOUD_KEYSTORE_PATH=Signing/cloud.jks
SHARECONNECT_CLOUD_KEY_ALIAS=...
SHARECONNECT_CLOUD_KEY_PASSWORD=...
SHARECONNECT_CLOUD_STORE_PASSWORD=...
FIREBASE_DISTRIBUTION_PROD_GROUP=...
FIREBASE_DISTRIBUTION_PROD_APP_ID=...
FIREBASE_DISTRIBUTION_PROD_ARTIFACT_TYPE=APK
FIREBASE_DISTRIBUTION_PROD_APP_CREDENTIALS_FILE=...
```

## Additional Resources

- **Asinka Documentation**: See `Asinka/CLAUDE.md` for IPC library details
- **Wiki**: https://deepwiki.com/vasic-digital/ShareConnect
- **Test Reports**: `Documentation/Tests/latest/`
