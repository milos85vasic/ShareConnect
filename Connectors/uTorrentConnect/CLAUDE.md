# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is an Android application called "uTorrentConnect" that allows remote control of uTorrent BitTorrent client servers. The app is adapted from the TransmissionConnect connector and integrates with the ShareConnect application.

- **Package**: `com.shareconnect.utorrentconnect`
- **Base Package**: `net.yupol.transmissionremote.app` (legacy, being migrated)
- **Language**: Primarily Java with some Kotlin components
- **Architecture**: MVVM with Dependency Injection (Hilt)

## Build Commands

### Standard Gradle Commands
```bash
# Build debug version
./gradlew assembleDebug

# Build release version
./gradlew assembleRelease

# Run tests
./gradlew test

# Run instrumentation tests
./gradlew connectedAndroidTest

# Clean build
./gradlew clean

# Lint check
./gradlew lint
```

### Key Build Configuration
- **Compile SDK**: 36
- **Min SDK**: 26
- **Target SDK**: 36
- **Kotlin Version**: 2.1.0
- **Java Version**: 11

## Application Architecture

### Core Components

1. **uTorrentRemote.kt** - Main application class managing:
   - Server configurations and active server state
   - Torrent filtering and sorting
   - Background notifications
   - Speed limit controls
   - Preference management

2. **MainActivity.java** - Primary entry point providing:
   - Torrent list management
   - Server switching interface
   - Torrent actions (start, stop, pause, remove)
   - File/magnet link handling
   - Search functionality

3. **Transport Layer** (`app/transport/`):
   - **BaseSpiceActivity.kt** - Base for network-enabled activities
   - **OkHttpTransportManager.kt** - HTTP communication with uTorrent WebUI
   - **SessionIdInterceptor.kt** - Handles authentication tokens
   - **BasicAuthenticator.kt** - Authentication handling

### Key Activities

- **MainActivity** - Main torrent management interface
- **TorrentDetailsActivity** - Detailed torrent information and management
- **AddServerActivity** - Server configuration
- **PreferencesActivity** - Application settings
- **ServersActivity** - Multiple server management

### Dependency Injection (Hilt)

The app uses Hilt for dependency injection with modules in `app/di/`:
- **DataStoreModule** - Preferences and data storage
- **FirebaseModule** - Analytics and crash reporting

### Data Layer

- **Server.java** - Server configuration model
- **Torrent.java** - Torrent data model (in `model/json/`)
- **PreferencesRepository.kt** - Centralized preferences management

## Key Features

1. **Multiple Server Support** - Connect to multiple uTorrent servers
2. **Torrent Management** - Full CRUD operations on torrents
3. **File Handling** - Import .torrent files and magnet links
4. **Background Updates** - Notification system for completed torrents
5. **Filtering & Sorting** - Comprehensive torrent organization
6. **Theme Support** - Night mode and theme customization

## API Implementation Status

### Current State ✅ IMPLEMENTED (as of 2025-10-10)

The transport layer has been **successfully adapted** from Transmission's JSON-RPC protocol to uTorrent's Web API. All core functionality is now implemented and building successfully.

- **Transmission (Legacy)**: JSON-RPC with methods like `torrent-get`, `torrent-add`, etc.
- **uTorrent (Current)**: Query parameter-based API with actions like `?list=1`, `?action=add-url`, etc.

### Completed Implementation

1. **Request Structure** (`transport/request/`) ✅:
   - ✅ **Base Request class** - Added `getQueryParameters()` method for all request classes
   - ✅ **TorrentGetRequest** - Maps to `/gui/?list=1`
   - ✅ **AddTorrentByUrlRequest** - Maps to `/gui/?action=add-url&s=URL`
   - ✅ **AddTorrentByFileRequest** - Maps to `/gui/?action=add-file` (multipart)
   - ✅ **TorrentRemoveRequest** - Maps to `/gui/?action=remove&hash=HASH` or `removedata`
   - ✅ **TorrentActionRequest** - Unified handler for start/stop/pause/recheck actions
   - ✅ **SessionGetRequest** - Maps to `/gui/?action=getsettings`
   - ✅ **SessionSetRequest** - Maps to `/gui/?action=setsetting` (basic implementation)

2. **Session Management** ✅:
   - ✅ **TokenInterceptor.kt** - Implemented with JSoup HTML parsing
   - ✅ Fetches token from `/gui/token.html`
   - ✅ Automatically refreshes token on 401 responses
   - ✅ Adds token as query parameter to all requests

3. **Transport Layer** (`transport/okhttp/`) ✅:
   - ✅ **OkHttpTransportManager.kt** - Completely rewritten for uTorrent protocol:
     - Changed from POST to GET requests
     - URL building with query parameters
     - Token management via TokenInterceptor
     - Response parsing with UTorrentResponseParser
   - ✅ **BasicAuthenticator.kt** - HTTP Basic Auth for initial token fetch

4. **Response Parsing** ✅:
   - ✅ **UTorrentResponseParser.kt** - Comprehensive array-to-object parser:
     - Parses uTorrent's efficient array-based format
     - Maps 18+ array indices to Torrent properties
     - Converts status bitfields to Transmission enum values
     - Wraps responses in Transmission-compatible format for backward compatibility
   - ✅ **Torrent Model** - Enhanced with:
     - Hash string field (uTorrent's primary identifier)
     - Complete Builder pattern for all fields
     - Public getters for all properties

5. **API Endpoints Implemented**:
   - ✅ List torrents: `/gui/?list=1`
   - ✅ Add torrent: `/gui/?action=add-url&s=<url>`
   - ✅ Remove torrent: `/gui/?action=remove&hash=<hash>`
   - ✅ Remove with data: `/gui/?action=removedata&hash=<hash>`
   - ✅ Start torrent: `/gui/?action=start&hash=<hash>`
   - ✅ Stop torrent: `/gui/?action=stop&hash=<hash>`
   - ✅ Pause torrent: `/gui/?action=pause&hash=<hash>`
   - ✅ Recheck torrent: `/gui/?action=recheck&hash=<hash>`
   - ✅ Get settings: `/gui/?action=getsettings`
   - ✅ Set setting: `/gui/?action=setsetting&s=<setting>&v=<value>`

### Implementation Details

#### Token Authentication Flow
```kotlin
// 1. TokenInterceptor fetches token on first request
GET /gui/token.html
Response: <html><div id='token'>TOKEN_VALUE</div></html>

// 2. Token added to all subsequent requests
GET /gui/?token=TOKEN_VALUE&list=1

// 3. On 401 error, token is automatically refreshed
```

#### Response Format Handling
```json
// uTorrent Response (array format)
{
  "torrents": [
    ["HASH", 201, "Name", 1073741824, 750, 102400, ...],
    ...
  ],
  "torrentc": "CACHE_ID"
}

// Converted to Transmission format for backward compatibility
{
  "result": "success",
  "arguments": {
    "torrents": [
      {
        "id": 12345,
        "hashString": "HASH",
        "name": "Name",
        "totalSize": 1073741824,
        ...
      }
    ]
  }
}
```

### Pending Enhancements

- [ ] **TorrentSetRequest** - Full property setting implementation (currently basic)
- [ ] **Multiple torrent operations** - Support repeated `&hash=` parameters
- [ ] **File operations** - Implement `getfiles` and `setprio` actions
- [ ] **Mock server** - Create uTorrent API simulator for testing
- [ ] **Unit tests** - Test individual components
- [ ] **Integration tests** - Test full request/response cycles
- [ ] **E2E tests** - Test with real uTorrent instance

## Development Notes

- The codebase mixes Java and Kotlin - maintain consistency with existing file types
- Uses RoboSpice for background network operations (consider migrating to Kotlin Coroutines)
- Firebase integration for analytics and crash reporting
- Material Design components throughout the UI
- Supports Android 8.0+ (API 26+)

## Test Configuration

- **Unit Tests**: JUnit 4 with Mockito and Truth assertions
- **Instrumentation Tests**: Espresso with AndroidX Test orchestrator
- **Mock Server**: Custom mock server module for testing (needs adaptation for uTorrent API)

## Networking

- **Primary**: OkHttp with custom interceptors
- **Legacy**: RoboSpice with Google HTTP client
- **Authentication**: Basic Auth and token management
- **SSL**: Network security config for HTTPS connections

## TODO: High Priority

1. Implement uTorrent Web API protocol in transport layer
2. Update mock server to simulate uTorrent API responses
3. Adapt unit tests for new API format
4. Update instrumentation tests for uTorrent-specific features
5. Add comprehensive API documentation
