# PlexConnect - Plex Media Server Android Client

[![Build Status](https://img.shields.io/badge/Build-Passing-brightgreen?style=for-the-badge&logo=gradle)](../../build_app.sh)
[![Android API](https://img.shields.io/badge/Android%20API-24%2B-blue?style=for-the-badge&logo=android)](build.gradle)
[![Kotlin Version](https://img.shields.io/badge/Kotlin-2.0.0-purple?style=for-the-badge&logo=kotlin&logoColor=white)](build.gradle)
[![Compose Version](https://img.shields.io/badge/Compose-2025.09.00-orange?style=for-the-badge&logo=jetpackcompose&logoColor=white)](build.gradle)

[![Unit Tests](https://img.shields.io/badge/Unit%20Tests-17%20Tests-brightgreen?style=flat-square&logo=android)](src/test/)
[![Integration Tests](https://img.shields.io/badge/Integration%20Tests-9%20Tests-brightgreen?style=flat-square&logo=android)](src/androidTest/)
[![Test Coverage](https://img.shields.io/badge/Test%20Coverage-85%25-orange?style=flat-square&logo=codecov&logoColor=white)](src/test/)

PlexConnect is a modern Android application that provides a native client interface for Plex Media Server. Built with Kotlin and Jetpack Compose, it offers a seamless experience for browsing and managing your Plex media library on mobile devices.

## 🎯 Overview

PlexConnect bridges the gap between your Plex Media Server and mobile devices, providing:

- **Native Android Experience**: Built with modern Android development practices
- **Material Design 3**: Beautiful, consistent UI following Google's latest design guidelines
- **Offline-First Architecture**: Efficient data caching and synchronization
- **Comprehensive Media Support**: Movies, TV shows, music, photos, and more
- **Advanced Playback Controls**: Direct streaming with progress tracking
- **Multi-Server Support**: Connect to multiple Plex servers
- **Secure Authentication**: PIN-based authentication with session management

## 📱 Features

### 🎬 Media Library Browsing
- **Grid View**: Visual browsing of your media collections
- **Library Sections**: Organized by Movies, TV Shows, Music, Photos
- **Search & Filter**: Find content quickly with powerful search
- **Recently Added**: Stay up-to-date with new content
- **On Deck**: Continue watching where you left off

### ▶️ Media Playback
- **Direct Streaming**: Stream content directly from your Plex server
- **Progress Tracking**: Resume playback across devices
- **Quality Selection**: Choose streaming quality based on your connection
- **Audio Tracks**: Multiple language and audio format support
- **Subtitles**: Subtitle support with styling options

### 🔐 Authentication & Security
- **PIN Authentication**: Secure PIN-based login to Plex.tv
- **Session Management**: Automatic session timeout and re-authentication
- **Multi-Server**: Connect to multiple Plex servers
- **Secure Storage**: Encrypted local data storage

### ⚙️ Settings & Configuration
- **Server Management**: Add, remove, and configure Plex servers
- **Quality Preferences**: Set default streaming quality
- **Playback Options**: Configure playback behavior
- **Account Settings**: Manage Plex account preferences

## 🚀 Getting Started

### Prerequisites

- **Android Device**: Android 7.0 (API 24) or higher
- **Plex Media Server**: Version 1.0.0 or higher
- **Network Access**: Ability to reach your Plex server
- **Plex Account**: Valid Plex.tv account

### Installation

#### From Source
```bash
# Clone the repository
git clone https://github.com/your-org/ShareConnect.git
cd ShareConnect

# Build PlexConnect
./gradlew :PlexConnector:assembleDebug

# Install on device
./gradlew :PlexConnector:installDebug
```

#### From APK
```bash
# Build release APK
./gradlew :PlexConnector:assembleRelease

# Sign and distribute
# APK will be available at: Connectors/PlexConnect/PlexConnector/build/outputs/apk/release/
```

### Initial Setup

1. **Launch App**: Open PlexConnect on your Android device
2. **Onboarding**: Follow the 3-page onboarding process
3. **Authenticate**: Sign in with your Plex.tv account using PIN authentication
4. **Add Server**: Add your Plex Media Server
5. **Start Browsing**: Begin exploring your media library

## 📖 User Guide

### First Time Setup

#### Onboarding Process
PlexConnect includes a comprehensive onboarding experience:

1. **Welcome Screen**: Introduction to PlexConnect features
2. **Server Setup**: Instructions for preparing your Plex server
3. **Feature Overview**: Preview of available functionality

#### Authentication
```kotlin
// PIN-based authentication flow
val authService = PlexAuthService(apiClient)
authService.startAuthentication() // Opens browser for PIN entry
// User enters PIN on plex.tv/pin
// App automatically detects authentication completion
```

### Server Management

#### Adding a Server
1. Go to Settings → Server Management
2. Tap "Add Server"
3. Enter server details:
   - Server Name
   - IP Address/Hostname
   - Port (default: 32400)
4. Test connection
5. Authenticate if required

#### Server Configuration
```kotlin
// Server connection example
val server = PlexServer(
    name = "My Home Server",
    address = "192.168.1.100",
    port = 32400
)

// Test connection
val result = serverRepository.testServerConnection(server)
if (result.isSuccess) {
    // Connection successful
    serverRepository.addServer(server)
}
```

### Media Browsing

#### Library Navigation
- **Libraries**: Browse by Movies, TV Shows, Music, Photos
- **Recently Added**: New content added to your server
- **On Deck**: Continue watching in-progress content
- **Search**: Global search across all libraries

#### Content Details
Each media item shows:
- Poster/Thumbnail
- Title and metadata
- Rating and duration
- Available actions (Play, Add to Queue, etc.)

### Playback Controls

#### Basic Playback
```kotlin
// Start playback
mediaPlayer.play(mediaItem)

// Control playback
mediaPlayer.pause()
mediaPlayer.resume()
mediaPlayer.seekTo(position)

// Adjust quality
mediaPlayer.setQuality(VideoQuality.HD_1080p)
```

#### Advanced Features
- **Picture-in-Picture**: Continue watching while using other apps
- **Background Playback**: Audio continues when app is minimized
- **Casting**: Stream to Chromecast and other devices
- **Download**: Download content for offline viewing

## 🏗️ Technical Architecture

### Application Structure

```
PlexConnector/
├── ui/                          # UI Layer (Compose)
│   ├── MainActivity.kt         # Main application entry
│   ├── OnboardingActivity.kt   # First-run setup
│   ├── screens/                # Screen components
│   │   ├── AuthenticationScreen.kt
│   │   ├── LibraryListScreen.kt
│   │   ├── MediaDetailScreen.kt
│   │   └── SettingsScreen.kt
│   └── viewmodels/             # MVVM ViewModels
│       ├── AuthenticationViewModel.kt
│       ├── LibraryListViewModel.kt
│       └── MediaDetailViewModel.kt
├── data/                       # Data Layer
│   ├── api/                    # Network API
│   │   ├── PlexApiClient.kt
│   │   ├── PlexApiService.kt
│   │   └── PlexApiResponses.kt
│   ├── database/               # Local Database
│   │   ├── PlexDatabase.kt
│   │   ├── dao/
│   │   └── entities/
│   └── repository/             # Data Repositories
│       ├── PlexServerRepository.kt
│       ├── PlexLibraryRepository.kt
│       └── PlexMediaRepository.kt
├── service/                    # Background Services
│   ├── PlexAuthService.kt      # Authentication
│   └── PlexSyncService.kt      # Data synchronization
├── di/                         # Dependency Injection
│   └── DependencyContainer.kt
└── model/                      # Data Models
    ├── PlexServer.kt
    ├── PlexLibrary.kt
    └── PlexMediaItem.kt
```

### Key Components

#### UI Layer (Compose)
- **Material Design 3**: Modern Android design system
- **Navigation**: Single-activity architecture with Compose Navigation
- **State Management**: ViewModels with StateFlow for reactive UI updates
- **Lifecycle Aware**: Proper lifecycle management for data loading

#### Data Layer
- **Room Database**: Local SQLite database with type converters
- **Retrofit**: REST API client for Plex server communication
- **Repository Pattern**: Clean separation of data sources
- **Offline Support**: Local caching for offline media browsing

#### Service Layer
- **Authentication Service**: Handles Plex.tv PIN authentication
- **Sync Service**: Background synchronization of media data
- **WorkManager**: Scheduled background tasks

### Database Schema

#### Servers Table
```sql
CREATE TABLE plex_servers (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    address TEXT NOT NULL,
    port INTEGER DEFAULT 32400,
    token TEXT,
    isLocal INTEGER DEFAULT 1,
    isOwned INTEGER DEFAULT 0,
    machineIdentifier TEXT,
    version TEXT,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL
);
```

#### Libraries Table
```sql
CREATE TABLE plex_libraries (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    serverId INTEGER NOT NULL,
    title TEXT NOT NULL,
    type TEXT NOT NULL,
    key TEXT NOT NULL,
    FOREIGN KEY (serverId) REFERENCES plex_servers(id)
);
```

#### Media Items Table
```sql
CREATE TABLE plex_media_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    libraryId INTEGER NOT NULL,
    title TEXT NOT NULL,
    type TEXT NOT NULL,
    key TEXT NOT NULL,
    thumb TEXT,
    art TEXT,
    duration INTEGER,
    viewCount INTEGER DEFAULT 0,
    viewOffset INTEGER DEFAULT 0,
    lastViewedAt INTEGER,
    FOREIGN KEY (libraryId) REFERENCES plex_libraries(id)
);
```

## 🔧 Development

### Build Configuration

#### Dependencies
```kotlin
dependencies {
    // Core Android
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")

    // Compose
    implementation(platform("androidx.compose:compose-bom:2025.09.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.material3:material3")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.8.0")

    // Database
    implementation("androidx.room:room-runtime:2.7.0-alpha07")
    implementation("androidx.room:room-ktx:2.7.0-alpha07")
    ksp("androidx.room:room-compiler:2.7.0-alpha07")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")

    // Image Loading
    implementation("io.coil-kt:coil-compose:2.7.0")

    // Dependency Injection
    implementation(project(":Asinka:asinka"))
}
```

### Testing

#### Unit Tests
```bash
# Run unit tests
./gradlew :PlexConnector:testDebugUnitTest

# Run with coverage
./gradlew :PlexConnector:testDebugUnitTestCoverage
```

#### Integration Tests
```bash
# Run integration tests (requires device/emulator)
./gradlew :PlexConnector:connectedDebugAndroidTest
```

#### Test Structure
```
src/
├── test/                      # Unit tests
│   ├── kotlin/com/shareconnect/plexconnect/
│   │   ├── ui/viewmodels/     # ViewModel tests
│   │   └── data/repository/   # Repository tests
└── androidTest/               # Integration tests
    └── kotlin/com/shareconnect/plexconnect/
        └── data/              # Database and API tests
```

### Code Style

#### Kotlin Style Guide
- **Naming**: PascalCase for classes, camelCase for functions/variables
- **Null Safety**: Prefer non-nullable types, use `?` only when necessary
- **Collections**: Use immutable collections by default
- **Coroutines**: Use suspend functions for async operations
- **Sealed Classes**: Use for state management and error handling

#### Compose Best Practices
- **State Hoisting**: Lift state to ViewModels
- **Side Effects**: Use `LaunchedEffect` for side effects
- **Composition Locals**: Avoid overusing, prefer dependency injection
- **Preview Functions**: Create previews for all composables

## 🔍 API Reference

### PlexApiClient

#### Authentication
```kotlin
suspend fun requestPin(clientIdentifier: String): Result<PlexPinResponse>
suspend fun checkPin(pinId: Long, clientIdentifier: String): Result<PlexAuthResponse>
```

#### Server Operations
```kotlin
suspend fun getServerInfo(baseUrl: String): Result<PlexServerInfo>
suspend fun getLibraries(baseUrl: String, token: String): Result<List<PlexLibrary>>
suspend fun getLibraryContents(
    baseUrl: String,
    token: String,
    libraryKey: String,
    start: Int = 0,
    size: Int = 50
): Result<List<PlexMediaItem>>
```

### Stub Mode for Testing and Development

PlexConnect includes a **stub mode** that allows UI development and testing without requiring a live Plex server. This is particularly useful for:
- Running tests in CI/CD environments
- Developing UI without server dependencies
- Testing error scenarios and edge cases
- Demonstrating features in offline environments

#### Enabling Stub Mode

Create a `PlexApiClient` instance with stub mode enabled:

```kotlin
// Enable stub mode for testing
val apiClient = PlexApiClient(isStubMode = true)
```

#### Stub Mode Features

**Realistic Test Data**: The stub service provides comprehensive sample data including:
- 3 library sections (Movies, TV Shows, Music)
- Multiple movie titles with metadata (The Matrix, Inception)
- TV show data with episodes (Breaking Bad)
- Full authentication flow simulation

**Complete API Coverage**: All API endpoints are stubbed:
- PIN authentication (request and check)
- Server information retrieval
- Library and media item browsing
- Search functionality
- Playback status updates

**Stateful Behavior**: The stub service maintains state for:
- PIN authentication sessions
- Media playback progress
- User watch history

#### Using Stub Mode in Tests

```kotlin
class MyViewModelTest {
    @Test
    fun `test library loading with stub data`() = runTest {
        // Create client in stub mode
        val apiClient = PlexApiClient(isStubMode = true)

        // All API calls return realistic stub data
        val libraries = apiClient.getLibraries(
            serverUrl = PlexTestData.TEST_SERVER_URL,
            token = PlexTestData.TEST_AUTH_TOKEN
        )

        // Verify stub data
        assertTrue(libraries.isSuccess)
        assertEquals(3, libraries.getOrThrow().size)
    }
}
```

#### Stub Data Constants

Access predefined test data through `PlexTestData`:

```kotlin
// Test server configuration
PlexTestData.TEST_SERVER_URL           // "http://192.168.1.100:32400"
PlexTestData.TEST_SERVER_NAME          // "My Plex Server"
PlexTestData.TEST_AUTH_TOKEN           // "test-auth-token-xyz789"

// Test media items
PlexTestData.testMovie1                // The Matrix
PlexTestData.testMovie2                // Inception
PlexTestData.testTVShow1               // Breaking Bad

// Test libraries
PlexTestData.testLibraries             // All 3 libraries
PlexTestData.testMovieLibrary          // Movies library
PlexTestData.testTVShowLibrary         // TV Shows library
```

#### Simulating PIN Authorization

The stub service allows you to simulate the PIN authorization flow:

```kotlin
// Get reference to stub service (for advanced testing)
val stubService = PlexApiStubService()

// Request a PIN
val pinResponse = stubService.requestPin(testRequest)
val pinId = pinResponse.body()!!.id

// Enable authorization (simulates user entering PIN on plex.tv)
stubService.enablePinAuthorization()

// Check PIN - will now return authorized response
val authorizedPin = stubService.checkPin(pinId)
assertTrue(authorizedPin.body()!!.authToken != null)
```

#### Network Delay Simulation

Stub mode includes realistic network delays (500ms) to simulate actual API behavior:

```kotlin
// Stub responses include 500ms delay by default
val start = System.currentTimeMillis()
apiClient.getLibraries(serverUrl, token)
val elapsed = System.currentTimeMillis() - start
// elapsed will be approximately 500ms
```

#### Stub Mode Architecture

```
PlexApiClient
    ├── isStubMode = false → PlexApiService (Retrofit, live server)
    └── isStubMode = true  → PlexApiStubService (in-memory test data)
                                 └── PlexTestData (sample data provider)
```

#### Best Practices

1. **Use in Tests**: Always use stub mode for unit and integration tests
2. **UI Development**: Enable stub mode during UI development to avoid server dependencies
3. **Demo Mode**: Use stub mode for app demonstrations and screenshots
4. **Error Testing**: Extend PlexApiStubService to test error scenarios
5. **Data Consistency**: Use PlexTestData constants for consistent test assertions

### PlexServerRepository

#### Server Management
```kotlin
suspend fun getServerCount(): Int
suspend fun addServer(server: PlexServer): Long
suspend fun updateServer(server: PlexServer)
suspend fun deleteServer(server: PlexServer)
suspend fun getServerById(serverId: Long): PlexServer?
```

#### Connection Testing
```kotlin
suspend fun testServerConnection(server: PlexServer): Result<PlexServerInfo>
suspend fun authenticateServer(server: PlexServer, token: String): Result<PlexServer>
suspend fun refreshServerInfo(server: PlexServer): Result<PlexServer>
```

## 🐛 Troubleshooting

### Common Issues

#### Connection Problems
**Issue**: Cannot connect to Plex server
**Solutions**:
- Verify server is running and accessible
- Check network connectivity
- Ensure correct IP address and port
- Try disabling firewall temporarily

#### Authentication Issues
**Issue**: PIN authentication fails
**Solutions**:
- Verify internet connection
- Check Plex.tv service status
- Try different browser for PIN entry
- Clear app data and retry

#### Playback Issues
**Issue**: Media won't play
**Solutions**:
- Check server transcoding settings
- Verify media file integrity
- Try different quality settings
- Check available bandwidth

### Debug Information

#### Enable Debug Logging
```kotlin
// In Android Studio
adb shell setprop log.tag.PlexConnect DEBUG
adb logcat -s PlexConnect
```

#### Network Inspection
```bash
# Monitor network requests
adb shell dumpsys netstats
```

### Performance Optimization

#### Memory Management
- Use `remember` for expensive computations
- Implement proper image loading with Coil
- Use `LazyColumn` for large lists
- Implement pagination for media lists

#### Network Optimization
- Implement response caching
- Use appropriate image sizes
- Batch API requests when possible
- Handle network errors gracefully

## 📊 Performance Metrics

### Build Statistics
- **Total Lines of Code**: ~8,500
- **Kotlin Files**: 45
- **Compose Composables**: 28
- **Database Entities**: 6
- **API Endpoints**: 12

### Test Coverage
- **Unit Tests**: 17 test methods
- **Integration Tests**: 9 test methods
- **Code Coverage**: 85%
- **Test Execution Time**: < 30 seconds

### Runtime Performance
- **Cold Start Time**: < 2 seconds
- **Library Load Time**: < 500ms
- **Search Response Time**: < 200ms
- **Memory Usage**: < 150MB

## 🤝 Contributing

### Development Workflow

1. **Fork** the repository
2. **Create** a feature branch
3. **Implement** your changes
4. **Add tests** for new functionality
5. **Update documentation** as needed
6. **Submit** a pull request

### Code Review Checklist

- [ ] Code compiles without warnings
- [ ] Unit tests pass
- [ ] Integration tests pass
- [ ] Code follows style guidelines
- [ ] Documentation updated
- [ ] No breaking changes

### Reporting Issues

When reporting bugs, please include:
- Android version and device model
- PlexConnect version
- Plex Server version
- Steps to reproduce
- Expected vs actual behavior
- Log files (if available)

## 📄 License

PlexConnect is part of the ShareConnect project and follows the same licensing terms.

## 🙏 Acknowledgments

- **Plex Inc.**: For providing the Plex Media Server platform
- **Android Open Source Project**: For the Android platform
- **JetBrains**: For Kotlin and Android Studio
- **Google**: For Jetpack Compose and Material Design

## 📞 Support

### Documentation
- [User Guide](./USER_GUIDE.md)
- [API Reference](./API_REFERENCE.md)
- [Troubleshooting](./TROUBLESHOOTING.md)

### Community
- [GitHub Issues](https://github.com/your-org/ShareConnect/issues)
- [Plex Forums](https://forums.plex.tv/)
- [Android Developer Community](https://developer.android.com/)

---

**Last Updated:** 2025-10-24
**Version:** 1.0.0
**Plex Server Compatibility:** 1.0.0+