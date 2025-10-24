# ShareConnector

## Overview and Purpose

ShareConnector is the main Android application in the ShareConnect ecosystem, serving as the primary interface for sharing media links from various streaming services and download sources to local services including MeTube, YT-DLP, torrent clients, and jDownloader. The app combines the concepts of "share" and "connect" to represent its core functionality of connecting content discovery with local download services.

## Architecture and Components

ShareConnector follows a modular architecture with the following key components:

### Core Components
- **MainActivity**: Main application entry point handling UI, profile management, and navigation
- **ProfileManager**: Manages service profiles and their configurations
- **ThemeManager**: Handles application theming and customization
- **SecurityAccessManager**: Provides authentication and security features

### Database Layer
- **HistoryDatabase**: Room database for storing sharing history and metadata
- **ServerProfileRepository**: Manages service profile persistence
- **ThemeRepository**: Handles theme data storage

### Sync Integration
- **ThemeSyncManager**: Synchronizes themes across ShareConnect apps
- **ProfileSyncManager**: Syncs service profiles across devices
- **LanguageSyncManager**: Manages language preferences
- **HistorySyncManager**: Syncs sharing history
- **RSSSyncManager**: Handles RSS feed synchronization
- **BookmarkSyncManager**: Manages bookmark synchronization
- **PreferencesSyncManager**: Syncs user preferences
- **TorrentSharingSyncManager**: Handles torrent sharing data

### UI Components
- **ProfileIconAdapter**: Displays service profiles in grid layout
- **SystemAppAdapter**: Shows compatible system applications
- **HistoryAdapter**: Manages sharing history display
- **ThemeAdapter**: Handles theme selection UI

## API Reference

### ProfileManager
```kotlin
class ProfileManager(context: Context) {
    fun getProfiles(): List<ServerProfile>
    fun addProfile(profile: ServerProfile): Boolean
    fun updateProfile(profile: ServerProfile): Boolean
    fun deleteProfile(profileId: String): Boolean
    fun getDefaultProfile(): ServerProfile?
    fun setDefaultProfile(profileId: String): Boolean
    fun hasProfiles(): Boolean
}
```

### ServiceApiClient
```kotlin
class ServiceApiClient {
    fun sendToService(url: String, profile: ServerProfile): ApiResponse
    fun testConnection(profile: ServerProfile): ConnectionResult
}
```

### ThemeManager
```kotlin
class ThemeManager {
    fun applyTheme(activity: AppCompatActivity)
    fun getCurrentTheme(): Theme
    fun setTheme(theme: Theme)
    fun getAvailableThemes(): List<Theme>
}
```

## Key Classes and Their Responsibilities

### MainActivity
- **Responsibilities**:
  - Application entry point and main UI orchestration
  - Handles security access checks on app launch/resume
  - Manages onboarding flow for new users
  - Coordinates profile display and management
  - Handles system app integration
  - Manages navigation between different app sections

### ProfileManager
- **Responsibilities**:
  - CRUD operations for service profiles
  - Migration from SharedPreferences to Room database
  - Profile validation and default profile management
  - Integration with sync managers for cross-device synchronization

### ServiceApiClient
- **Responsibilities**:
  - HTTP communication with target services (MeTube, YT-DLP, torrent clients, jDownloader)
  - Authentication handling (Basic Auth, cookie-based, session management)
  - Connection testing and validation
  - Error handling and retry logic

### MetadataFetcher
- **Responsibilities**:
  - Extracts rich metadata from shared URLs
  - Fetches titles, descriptions, and thumbnails
  - Supports multiple streaming platforms
  - Fallback extraction for unsupported sites

### UrlCompatibilityUtils
- **Responsibilities**:
  - Determines URL compatibility with different service types
  - Classifies URLs by content type (streaming, file hosting, torrent, etc.)
  - Validates URL formats and protocols

## Data Models

### ServerProfile
```kotlin
data class ServerProfile(
    var id: String? = null,
    var name: String = "",
    var host: String = "",
    var port: Int = 80,
    var serviceType: ServiceType = ServiceType.METUBE,
    var username: String? = null,
    var password: String? = null,
    var useHttps: Boolean = false,
    var isDefault: Boolean = false,
    var clientType: TorrentClientType? = null
)
```

### HistoryItem
```kotlin
data class HistoryItem(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val thumbnailUrl: String? = null,
    val serviceType: String,
    val profileName: String,
    val timestamp: Long = System.currentTimeMillis(),
    val success: Boolean = true
)
```

### Theme
```kotlin
data class Theme(
    val id: String,
    val name: String,
    val isCustom: Boolean = false,
    val colors: ThemeColors
)
```

## Usage Examples

### Adding a Service Profile
```kotlin
val profileManager = ProfileManager(context)
val profile = ServerProfile(
    name = "My MeTube Server",
    host = "192.168.1.100",
    port = 8080,
    serviceType = ServiceType.METUBE,
    useHttps = false
)
profileManager.addProfile(profile)
```

### Sharing a URL
```kotlin
val apiClient = ServiceApiClient()
val profile = profileManager.getDefaultProfile()
if (profile != null) {
    val result = apiClient.sendToService("https://youtube.com/watch?v=...", profile)
    if (result.success) {
        // Handle success
    }
}
```

### Applying a Theme
```kotlin
val themeManager = ThemeManager.getInstance(context)
val theme = themeManager.getAvailableThemes().first { it.name == "Material Dark" }
themeManager.setTheme(theme)
themeManager.applyTheme(activity)
```

## Dependencies

### Core Android Dependencies
- `androidx.appcompat:appcompat:1.6.1` - AppCompat support
- `androidx.core:core-ktx:1.12.0` - Core Kotlin extensions
- `androidx.activity:activity-ktx:1.8.2` - Activity Kotlin extensions
- `com.google.android.material:material:1.11.0` - Material Design components

### Database Dependencies
- `androidx.room:room-runtime:2.6.1` - Room database runtime
- `androidx.room:room-ktx:2.6.1` - Room Kotlin extensions
- `androidx.room:room-compiler:2.6.1` - Room annotation processor

### Networking Dependencies
- `com.squareup.okhttp3:okhttp:4.12.0` - HTTP client
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - HTTP logging
- `org.jsoup:jsoup:1.18.1` - HTML parsing

### Security Dependencies
- `androidx.biometric:biometric:1.1.0` - Biometric authentication
- `net.zetetic:android-database-sqlcipher:4.5.4` - Encrypted database

### Sync Dependencies
- `io.grpc:grpc-netty-shaded:1.57.1` - gRPC server for sync
- Project modules: `:ThemeSync`, `:ProfileSync`, `:HistorySync`, etc.

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `org.mockito:mockito-core:5.8.0` - Mocking framework
- `org.robolectric:robolectric:4.16` - Android unit testing
- `androidx.test.ext:junit:1.3.0` - Android test runner
- `androidx.test.espresso:espresso-core:3.7.0` - UI testing

---

*For more information, visit [https://shareconnect.org/docs/shareconnector](https://shareconnect.org/docs/shareconnector)*