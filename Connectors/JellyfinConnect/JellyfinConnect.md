# JellyfinConnect Technical Documentation

## Table of Contents
1. [Overview](#overview)
2. [Architecture](#architecture)
3. [Installation](#installation)
4. [API Integration](#api-integration)
5. [Data Models](#data-models)
6. [Authentication](#authentication)
7. [Core Features](#core-features)
8. [Synchronization](#synchronization)
9. [Testing](#testing)
10. [Development](#development)
11. [Troubleshooting](#troubleshooting)
12. [API Reference](#api-reference)

---

## Overview

**JellyfinConnect** is an Android connector application for the ShareConnect ecosystem that enables seamless integration with Jellyfin Media Server. It provides comprehensive media server functionality including authentication, library browsing, media playback tracking, and search capabilities.

### Key Features
- **Authentication**: Username/password and API key support
- **Library Management**: Browse and organize media libraries
- **Playback Tracking**: Monitor and report playback progress
- **Search**: Comprehensive media search functionality
- **Real-time Sync**: Synchronizes with other ShareConnect apps via Asinka
- **Security**: Integrated SecurityAccess for app-level protection
- **Material Design 3**: Modern, adaptive UI

### Version Information
- **Version**: 1.0.0
- **Min SDK**: 28 (Android 9.0)
- **Target SDK**: 36
- **Jellyfin API**: Compatible with v10.8+

---

## Architecture

### Application Structure

JellyfinConnect follows ShareConnect's modular architecture pattern:

```
JellyfinConnect/
├── JellyfinConnector/               # Main application module
│   ├── src/main/
│   │   ├── kotlin/
│   │   │   └── com/shareconnect/jellyfinconnect/
│   │   │       ├── JellyfinConnectApplication.kt
│   │   │       ├── data/
│   │   │       │   ├── api/
│   │   │       │   │   ├── JellyfinApiClient.kt
│   │   │       │   │   └── JellyfinApiService.kt
│   │   │       │   ├── models/
│   │   │       │   │   └── JellyfinModels.kt
│   │   │       │   └── websocket/
│   │   │       │       ├── JellyfinWebSocketClient.kt
│   │   │       │       └── JellyfinWebSocketMessages.kt
│   │   │       ├── ui/
│   │   │       │   ├── MainActivity.kt
│   │   │       │   ├── dashboard/
│   │   │       │   └── settings/
│   │   │       └── widget/
│   │   │           └── JellyfinWidget.kt
│   │   ├── res/                     # Resources
│   │   └── AndroidManifest.xml
│   └── src/test/                    # Unit tests
│   └── src/androidTest/             # Instrumentation tests
└── build.gradle                     # Build configuration
```

### Component Diagram

```
┌─────────────────────────────────────────────┐
│          JellyfinConnectApplication         │
│  ┌─────────────────────────────────────┐   │
│  │     Sync Managers (8 modules)       │   │
│  │  - ThemeSync                        │   │
│  │  - ProfileSync                      │   │
│  │  - HistorySync                      │   │
│  │  - RSSSync                          │   │
│  │  - BookmarkSync                     │   │
│  │  - PreferencesSync                  │   │
│  │  - LanguageSync                     │   │
│  │  - TorrentSharingSync               │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│            MainActivity                      │
│  ┌─────────────────────────────────────┐   │
│  │     SecurityAccessManager           │   │
│  │  - PIN Authentication               │   │
│  │  - Biometric Support                │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│          JellyfinApiClient                   │
│  ┌─────────────────────────────────────┐   │
│  │     Retrofit + OkHttp               │   │
│  │  - REST API Integration             │   │
│  │  - Authentication Headers           │   │
│  │  - Error Handling                   │   │
│  └─────────────────────────────────────┘   │
└─────────────────────────────────────────────┘
                    ↓
┌─────────────────────────────────────────────┐
│         Jellyfin Media Server               │
│         (http://server:8096)                │
└─────────────────────────────────────────────┘
```

---

## Installation

### Prerequisites
- Android Studio Arctic Fox or later
- Android SDK 28+
- Java 17
- Gradle 8.13.0+

### Build from Source

1. **Clone the repository**:
```bash
git clone https://github.com/your-org/ShareConnect.git
cd ShareConnect/Connectors/JellyfinConnect
```

2. **Build the project**:
```bash
./gradlew :JellyfinConnector:assembleDebug
```

3. **Install on device**:
```bash
./gradlew :JellyfinConnector:installDebug
```

### Dependencies

The application depends on several ShareConnect modules:

- **Core Modules**:
  - `DesignSystem`: Material Design 3 components
  - `Toolkit`: Shared utilities
  - `SecurityAccess`: App security layer
  - `Asinka`: IPC synchronization library

- **Sync Modules**:
  - `ThemeSync`: Theme synchronization
  - `ProfileSync`: Server profile synchronization
  - `HistorySync`: Usage history synchronization
  - `RSSSync`: RSS feed synchronization
  - `BookmarkSync`: Bookmark synchronization
  - `PreferencesSync`: App preferences synchronization
  - `LanguageSync`: Language settings synchronization
  - `TorrentSharingSync`: Torrent data synchronization

---

## API Integration

### JellyfinApiClient

The `JellyfinApiClient` is the main interface for communicating with Jellyfin Media Server.

#### Initialization

```kotlin
val apiClient = JellyfinApiClient(
    serverUrl = "http://192.168.1.100:8096",
    deviceId = "unique-device-id",
    deviceName = "My Android Phone",
    appVersion = "1.0.0"
)
```

#### Authentication Header

Jellyfin uses the `X-Emby-Authorization` header for authentication:

```kotlin
// Without token (for public endpoints)
val header = apiClient.getAuthHeader()
// Result: MediaBrowser Client="JellyfinConnect", Device="Android",
//         DeviceId="device-123", Version="1.0.0"

// With token (for authenticated endpoints)
val header = apiClient.getAuthHeader("your-access-token")
// Result: MediaBrowser Client="JellyfinConnect", Device="Android",
//         DeviceId="device-123", Version="1.0.0", Token="your-access-token"
```

---

## Data Models

### Core Models

#### JellyfinAuthResponse
Authentication response containing user info and access token.

```kotlin
data class JellyfinAuthResponse(
    val user: JellyfinUser?,
    val sessionInfo: JellyfinSession?,
    val accessToken: String,
    val serverId: String
)
```

#### JellyfinUser
User account information.

```kotlin
data class JellyfinUser(
    val name: String,
    val serverId: String,
    val id: String,
    val hasPassword: Boolean,
    val hasConfiguredPassword: Boolean,
    val hasConfiguredEasyPassword: Boolean,
    val enableAutoLogin: Boolean? = null,
    val lastLoginDate: String? = null,
    val lastActivityDate: String? = null
)
```

#### JellyfinServerInfo
Server configuration and status.

```kotlin
data class JellyfinServerInfo(
    val localAddress: String? = null,
    val serverName: String,
    val version: String,
    val productName: String? = null,
    val operatingSystem: String? = null,
    val id: String,
    val startupWizardCompleted: Boolean? = null
)
```

#### JellyfinLibrary
Media library/collection information.

```kotlin
data class JellyfinLibrary(
    val name: String,
    val serverId: String,
    val id: String,
    val collectionType: String? = null, // "movies", "tvshows", "music", etc.
    val imageTags: Map<String, String>? = null,
    val primaryImageAspectRatio: Double? = null
)
```

#### JellyfinItem
Media item (movie, show, episode, song, etc.).

```kotlin
data class JellyfinItem(
    val name: String,
    val serverId: String,
    val id: String,
    val type: String, // "Movie", "Series", "Season", "Episode", "Audio"
    val collectionType: String? = null,
    val productionYear: Int? = null,
    val runTimeTicks: Long? = null, // Duration in ticks (1 tick = 100 nanoseconds)
    val premiereDate: String? = null,
    val officialRating: String? = null,
    val overview: String? = null,
    val imageTags: Map<String, String>? = null,
    val backdropImageTags: List<String>? = null,
    val userData: JellyfinUserData? = null,
    val mediaType: String? = null,
    val indexNumber: Int? = null, // Episode or track number
    val parentIndexNumber: Int? = null, // Season number
    val seriesId: String? = null,
    val seasonId: String? = null,
    val seriesName: String? = null
)
```

#### JellyfinUserData
User-specific data for a media item.

```kotlin
data class JellyfinUserData(
    val playbackPositionTicks: Long? = null,
    val playCount: Int? = null,
    val isFavorite: Boolean? = null,
    val played: Boolean? = null,
    val key: String? = null,
    val unplayedItemCount: Int? = null
)
```

---

## Authentication

### Authentication Flow

1. **Get Public Server Info** (optional, for server discovery):
```kotlin
val serverInfoResult = apiClient.getPublicServerInfo()
if (serverInfoResult.isSuccess) {
    val serverInfo = serverInfoResult.getOrNull()
    println("Server: ${serverInfo?.serverName} v${serverInfo?.version}")
}
```

2. **Authenticate with Credentials**:
```kotlin
val authResult = apiClient.authenticateByName(
    username = "myusername",
    password = "mypassword"
)

when {
    authResult.isSuccess -> {
        val auth = authResult.getOrNull()!!
        val accessToken = auth.accessToken
        val userId = auth.user?.id
        // Store token and userId for future requests
    }
    else -> {
        println("Authentication failed: ${authResult.exceptionOrNull()?.message}")
    }
}
```

3. **Use Access Token for Subsequent Requests**:
```kotlin
// All authenticated endpoints require userId and token
val librariesResult = apiClient.getUserViews(userId, accessToken)
```

### Security Best Practices

- **Token Storage**: Store access tokens securely using EncryptedSharedPreferences
- **Token Expiration**: Monitor for 401 responses and re-authenticate when necessary
- **HTTPS**: Always use HTTPS in production environments
- **Device ID**: Generate a unique, persistent device ID for each installation

---

## Core Features

### 1. Library Browsing

#### Get User Libraries/Views

```kotlin
val viewsResult = apiClient.getUserViews(userId, accessToken)

if (viewsResult.isSuccess) {
    val libraries = viewsResult.getOrNull()!!
    libraries.forEach { library ->
        println("Library: ${library.name} (${library.collectionType})")
    }
}
```

#### Get Items from Library

```kotlin
val itemsResult = apiClient.getItems(
    userId = userId,
    token = accessToken,
    parentId = libraryId,        // Optional: filter by library
    includeItemTypes = "Movie",  // Optional: "Movie", "Series", "Episode", etc.
    recursive = true,            // Include subdirectories
    limit = 50,                  // Items per page
    startIndex = 0               // Pagination offset
)

if (itemsResult.isSuccess) {
    val result = itemsResult.getOrNull()!!
    println("Total items: ${result.totalRecordCount}")
    result.items.forEach { item ->
        println("${item.name} (${item.productionYear})")
    }
}
```

#### Get Specific Item Details

```kotlin
val itemResult = apiClient.getItem(userId, itemId, accessToken)

if (itemResult.isSuccess) {
    val item = itemResult.getOrNull()!!
    println("Title: ${item.name}")
    println("Overview: ${item.overview}")
    println("Runtime: ${item.runTimeTicks?.let { it / 10000000 / 60 }} minutes")
}
```

### 2. Playback Tracking

#### Report Playback Start

```kotlin
val startResult = apiClient.updateProgress(
    itemId = itemId,
    positionTicks = 0,           // Starting from beginning
    isPaused = false,
    token = accessToken
)
```

#### Update Playback Progress

```kotlin
val progressResult = apiClient.updateProgress(
    itemId = itemId,
    positionTicks = 36000000000,  // 1 hour (in ticks)
    isPaused = false,
    token = accessToken
)
```

#### Mark as Played

```kotlin
val playedResult = apiClient.markPlayed(userId, itemId, accessToken)

if (playedResult.isSuccess) {
    val userData = playedResult.getOrNull()!!
    println("Play count: ${userData.playCount}")
}
```

#### Mark as Unplayed

```kotlin
val unplayedResult = apiClient.markUnplayed(userId, itemId, accessToken)
```

### 3. Search

#### Search for Media

```kotlin
val searchResult = apiClient.search(
    searchTerm = "matrix",
    userId = userId,
    token = accessToken,
    limit = 50
)

if (searchResult.isSuccess) {
    val result = searchResult.getOrNull()!!
    println("Found ${result.totalRecordCount} results")

    result.searchHints.forEach { hint ->
        println("${hint.name} (${hint.type}) - ${hint.productionYear}")
    }
}
```

### 4. Server Management

#### Get Authenticated Server Info

```kotlin
val serverInfoResult = apiClient.getServerInfo(accessToken)

if (serverInfoResult.isSuccess) {
    val info = serverInfoResult.getOrNull()!!
    println("Server: ${info.serverName}")
    println("Version: ${info.version}")
    println("OS: ${info.operatingSystem}")
}
```

#### Get Current User

```kotlin
val userResult = apiClient.getCurrentUser(accessToken)

if (userResult.isSuccess) {
    val user = userResult.getOrNull()!!
    println("User: ${user.name}")
    println("Last login: ${user.lastLoginDate}")
}
```

---

## Synchronization

### Sync Manager Initialization

JellyfinConnectApplication initializes all 8 sync managers on app start:

```kotlin
class JellyfinConnectApplication : Application() {

    lateinit var themeSyncManager: ThemeSyncManager
    lateinit var profileSyncManager: ProfileSyncManager
    lateinit var historySyncManager: HistorySyncManager
    lateinit var rssSyncManager: RSSSyncManager
    lateinit var bookmarkSyncManager: BookmarkSyncManager
    lateinit var preferencesSyncManager: PreferencesSyncManager
    lateinit var languageSyncManager: LanguageSyncManager
    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager

    override fun onCreate() {
        super.onCreate()

        // Initialize all sync managers with staggered delays
        // to prevent port binding conflicts
        initializeLanguageSync()      // delay: 700ms
        initializeTorrentSharingSync() // delay: 800ms
        initializeThemeSync()          // delay: 100ms
        initializeProfileSync()        // delay: 200ms
        initializeHistorySync()        // delay: 300ms
        initializeRSSSync()            // delay: 400ms
        initializeBookmarkSync()       // delay: 500ms
        initializePreferencesSync()    // delay: 600ms
    }
}
```

### Sync Features

- **Real-time Synchronization**: Changes propagate instantly to other ShareConnect apps
- **Conflict Resolution**: Automatic conflict resolution based on timestamps
- **Encrypted Transport**: All sync data uses TLS encryption
- **Client Type Filtering**: ProfileSync and RSSSync filter by "MEDIA_SERVER" type
- **Port Management**: Each sync manager uses a unique port to prevent conflicts

---

## Testing

### Test Coverage

JellyfinConnect includes comprehensive test suites:

#### Unit Tests (25+ tests)
- API client initialization
- Authentication flows
- Server information retrieval
- Library and item queries
- Playback tracking
- Search functionality
- Error handling
- Edge cases

**Location**: `src/test/kotlin/com/shareconnect/jellyfinconnect/data/api/`

**Run tests**:
```bash
./gradlew :JellyfinConnector:test
```

#### Integration Tests (20+ tests)
- Complete authentication workflow
- Library browsing workflow
- Playback tracking workflow
- Search workflow
- Error recovery scenarios
- Multi-step operations
- Data consistency
- Concurrent operations

**Location**: `src/test/kotlin/com/shareconnect/jellyfinconnect/data/api/JellyfinApiClientIntegrationTest.kt`

#### Automation Tests (12+ tests)
- App launch verification
- UI display validation
- Accessibility compliance
- Layout responsiveness
- Theme application
- User interactions

**Location**: `src/androidTest/kotlin/com/shareconnect/jellyfinconnect/automation/`

**Run automation tests**:
```bash
./gradlew :JellyfinConnector:connectedAndroidTest
```

### MockWebServer Usage

Tests use OkHttp's MockWebServer for reliable, fast testing:

```kotlin
@Before
fun setUp() {
    mockWebServer = MockWebServer()
    mockWebServer.start()

    val serverUrl = mockWebServer.url("/").toString().trimEnd('/')
    apiClient = JellyfinApiClient(serverUrl = serverUrl)
}

@Test
fun `test authentication`() = runBlocking {
    mockWebServer.enqueue(MockResponse()
        .setResponseCode(200)
        .setBody(authResponseJson))

    val result = apiClient.authenticateByName("user", "pass")
    assertTrue(result.isSuccess)
}
```

---

## Development

### Adding New API Endpoints

1. **Define in JellyfinApiService**:
```kotlin
@GET("endpoint/path")
suspend fun getNewEndpoint(
    @Url url: String,
    @Header("X-Emby-Authorization") authHeader: String,
    @Query("param") param: String
): Response<ResponseType>
```

2. **Implement in JellyfinApiClient**:
```kotlin
suspend fun getNewData(param: String, token: String): Result<ResponseType> =
    withContext(Dispatchers.IO) {
        try {
            val url = buildUrl("endpoint/path")
            val authHeader = getAuthHeader(token)
            val response = service.getNewEndpoint(url, authHeader, param)

            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Request failed: ${response.code()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error in getNewData", e)
            Result.failure(e)
        }
    }
```

3. **Add Tests**:
```kotlin
@Test
fun `test new endpoint`() = runBlocking {
    mockWebServer.enqueue(MockResponse()
        .setResponseCode(200)
        .setBody(mockJson))

    val result = apiClient.getNewData("test", token)
    assertTrue(result.isSuccess)
}
```

### Code Style Guidelines

- Follow Kotlin coding conventions
- Use meaningful variable names
- Add KDoc comments for public APIs
- Keep functions focused and small
- Use Result<T> for error handling
- Prefer coroutines over callbacks

---

## Troubleshooting

### Common Issues

#### 1. Authentication Failed (401)
**Cause**: Invalid credentials or expired token
**Solution**: Verify credentials, re-authenticate if token expired

#### 2. Connection Refused
**Cause**: Server URL incorrect or server not running
**Solution**: Verify server URL format: `http://ip:port` (default port: 8096)

#### 3. SSL/TLS Errors
**Cause**: HTTPS with self-signed certificate
**Solution**: Either use HTTP (local network) or add certificate to trust store

#### 4. Empty Responses
**Cause**: Server returned 204 No Content or malformed JSON
**Solution**: Check server logs, verify API endpoint compatibility

#### 5. Timeout Errors
**Cause**: Slow network or server overload
**Solution**: Increase timeout values in OkHttpClient configuration

---

## API Reference

### Quick Reference

| Endpoint | Method | Auth | Description |
|----------|--------|------|-------------|
| `/Users/AuthenticateByName` | POST | No | Authenticate user |
| `/System/Info/Public` | GET | No | Get public server info |
| `/System/Info` | GET | Yes | Get server info |
| `/Users/Me` | GET | Yes | Get current user |
| `/Users/{userId}/Views` | GET | Yes | Get user libraries |
| `/Users/{userId}/Items` | GET | Yes | Get library items |
| `/Users/{userId}/Items/{itemId}` | GET | Yes | Get item details |
| `/Users/{userId}/PlayedItems/{itemId}` | POST | Yes | Mark as played |
| `/Users/{userId}/PlayedItems/{itemId}` | DELETE | Yes | Mark as unplayed |
| `/Sessions/Playing/Progress` | POST | Yes | Update progress |
| `/Search/Hints` | GET | Yes | Search media |

### Time Units

Jellyfin uses "ticks" for time values:
- **1 tick** = 100 nanoseconds
- **1 second** = 10,000,000 ticks
- **1 minute** = 600,000,000 ticks
- **1 hour** = 36,000,000,000 ticks

**Conversion helpers**:
```kotlin
// Ticks to seconds
val seconds = ticks / 10_000_000

// Seconds to ticks
val ticks = seconds * 10_000_000L

// Ticks to human-readable
fun ticksToReadable(ticks: Long): String {
    val totalSeconds = ticks / 10_000_000
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return "%02d:%02d:%02d".format(hours, minutes, seconds)
}
```

---

## Performance Considerations

### Network Optimization

- **Connection Pooling**: OkHttpClient reuses connections automatically
- **Timeouts**: Configured at 30 seconds (adjustable)
- **Compression**: Automatic gzip compression for responses
- **Caching**: Consider implementing response caching for frequently accessed data

### Memory Management

- **Pagination**: Always use `limit` and `startIndex` for large libraries
- **Image Loading**: Use Coil for efficient image loading and caching
- **Coroutines**: All API calls use `Dispatchers.IO` to avoid blocking main thread

---

## License

JellyfinConnect is part of the ShareConnect ecosystem.
For licensing information, see the main ShareConnect repository.

---

## Support

For issues, feature requests, or contributions:
- GitHub Issues: [ShareConnect Issues](https://github.com/your-org/ShareConnect/issues)
- Wiki: [ShareConnect Wiki](https://deepwiki.com/vasic-digital/ShareConnect)

---

## Changelog

### Version 1.0.0 (2025-10-25)
- Initial release
- Full Jellyfin API integration
- Authentication support
- Library browsing
- Playback tracking
- Search functionality
- 8 sync manager integration
- SecurityAccess integration
- Comprehensive test suite (57+ tests)
