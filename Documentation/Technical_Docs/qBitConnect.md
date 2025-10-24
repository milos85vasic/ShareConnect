# qBitConnect

## Overview and Purpose

qBitConnect is a specialized Android application that provides comprehensive integration with the qBittorrent client. It offers a modern, feature-rich mobile interface for managing torrent downloads, with advanced features like category management, search integration, and real-time synchronization. The app connects to qBittorrent WebUI instances running on local networks or remote servers.

## Architecture and Components

qBitConnect is built using Kotlin Multiplatform with shared business logic and platform-specific UI implementations:

### Core Components
- **App**: Main application class managing dependency injection and sync managers
- **DependencyContainer**: Handles dependency injection and service initialization
- **RequestManager**: Manages HTTP communication with qBittorrent WebUI
- **QBitConnectOnboardingActivity**: First-time setup and configuration

### Data Layer
- **ServerRepository**: Manages qBittorrent server connections and profiles
- **TorrentRepository**: Handles torrent data operations and state management
- **SettingsManager**: Manages application preferences and configuration

### UI Layer (Compose Multiplatform)
- **MainScreen**: Primary torrent list and management interface
- **AddServerScreen**: Server configuration and connection setup
- **SettingsScreen**: Application preferences and configuration
- **TorrentDetailsScreen**: Detailed torrent information and controls

### Sync Integration
- **ThemeSyncManager**: Theme synchronization with ShareConnect ecosystem
- **ProfileSyncManager**: Server profile management across devices
- **HistorySyncManager**: Torrent history synchronization
- **RSSSyncManager**: RSS feed integration for automated downloads
- **BookmarkSyncManager**: Bookmark management for torrent sites
- **PreferencesSyncManager**: User preference synchronization
- **LanguageSyncManager**: Language and localization sync
- **TorrentSharingSyncManager**: Cross-device torrent sharing

## API Reference

### RequestManager
```kotlin
class RequestManager {
    suspend fun login(username: String, password: String): Result<String>
    suspend fun getTorrents(): Result<List<Torrent>>
    suspend fun addTorrent(url: String, category: String? = null): Result<Unit>
    suspend fun pauseTorrent(hash: String): Result<Unit>
    suspend fun resumeTorrent(hash: String): Result<Unit>
    suspend fun deleteTorrent(hash: String, deleteFiles: Boolean = false): Result<Unit>
    suspend fun getCategories(): Result<Map<String, Category>>
    suspend fun setCategory(hash: String, category: String): Result<Unit>
}
```

### ServerRepository
```kotlin
class ServerRepository {
    suspend fun getServers(): List<Server>
    suspend fun addServer(server: Server): Result<Unit>
    suspend fun updateServer(server: Server): Result<Unit>
    suspend fun deleteServer(serverId: String): Result<Unit>
    suspend fun testConnection(server: Server): Result<Unit>
}
```

### TorrentRepository
```kotlin
class TorrentRepository {
    fun getTorrents(): Flow<List<Torrent>>
    suspend fun refreshTorrents(): Result<Unit>
    suspend fun addTorrent(url: String, serverId: String): Result<Unit>
    suspend fun pauseTorrent(hash: String): Result<Unit>
    suspend fun resumeTorrent(hash: String): Result<Unit>
    suspend fun deleteTorrent(hash: String, deleteFiles: Boolean): Result<Unit>
}
```

## Key Classes and Their Responsibilities

### App
- **Responsibilities**:
  - Application lifecycle management
  - Dependency injection container initialization
  - Sync manager initialization and coordination
  - Global coroutine scope management
  - Application-wide error handling

### RequestManager
- **Responsibilities**:
  - HTTP client management for qBittorrent WebUI API
  - Authentication handling (cookie-based sessions)
  - API request/response serialization
  - Error handling and retry logic
  - Connection pooling and timeout management

### ServerRepository
- **Responsibilities**:
  - qBittorrent server profile CRUD operations
  - Connection testing and validation
  - Server preference management
  - Migration between data storage formats

### TorrentRepository
- **Responsibilities**:
  - Torrent data caching and state management
  - Real-time torrent status updates
  - Offline data persistence
  - Data synchronization with remote servers

### QBitConnectOnboardingActivity
- **Responsibilities**:
  - First-time user setup and configuration
  - Server discovery and connection setup
  - Permission requests and initial preferences
  - Integration with ShareConnect onboarding ecosystem

## Data Models

### Server
```kotlin
data class Server(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 8080,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false,
    val trustSelfSignedCerts: Boolean = false,
    val timeout: Int = 30
)
```

### Torrent
```kotlin
data class Torrent(
    val hash: String,
    val name: String,
    val size: Long,
    val progress: Float,
    val status: TorrentStatus,
    val downloadSpeed: Long,
    val uploadSpeed: Long,
    val eta: Long,
    val category: String? = null,
    val tags: List<String> = emptyList(),
    val addedOn: Long,
    val completionOn: Long,
    val ratio: Float
)
```

### Category
```kotlin
data class Category(
    val name: String,
    val savePath: String
)
```

### TorrentStatus
```kotlin
enum class TorrentStatus {
    DOWNLOADING,
    SEEDING,
    PAUSED,
    QUEUED,
    CHECKING,
    ERROR,
    UNKNOWN
}
```

## Usage Examples

### Connecting to qBittorrent Server
```kotlin
val server = Server(
    name = "My qBittorrent",
    host = "192.168.1.100",
    port = 8080,
    username = "admin",
    password = "password"
)

val repository = ServerRepository()
val result = repository.addServer(server)
```

### Managing Torrents
```kotlin
val torrentRepo = TorrentRepository()

// Add a torrent
torrentRepo.addTorrent("magnet:?xt=urn:btih:...", serverId)

// Get torrent list
torrentRepo.getTorrents().collect { torrents ->
    // Update UI with torrent list
}

// Control torrent state
torrentRepo.pauseTorrent(torrentHash)
torrentRepo.resumeTorrent(torrentHash)
torrentRepo.deleteTorrent(torrentHash, deleteFiles = true)
```

### Category Management
```kotlin
val requestManager = RequestManager()

// Get available categories
val categoriesResult = requestManager.getCategories()
categoriesResult.onSuccess { categories ->
    // Display categories in UI
}

// Set torrent category
requestManager.setCategory(torrentHash, "Movies")
```

## Dependencies

### Kotlin Multiplatform Dependencies
- `org.jetbrains.kotlin:kotlin-stdlib:2.0.0` - Kotlin standard library
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2` - Coroutines for async operations
- `org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3` - JSON serialization
- `org.jetbrains.kotlinx:kotlinx-datetime:0.6.1` - Date/time handling

### Networking Dependencies
- `io.ktor:ktor-client-core:2.3.12` - Multiplatform HTTP client
- `io.ktor:ktor-client-cio:2.3.12` - CIO engine for JVM
- `io.ktor:ktor-client-okhttp:2.3.12` - OkHttp engine for Android
- `io.ktor:ktor-client-ios:2.3.12` - iOS HTTP client

### Android-Specific Dependencies
- `androidx.activity:activity-compose:1.9.3` - Compose activity integration
- `androidx.compose.material3:material3:1.2.1` - Material Design 3 components
- `androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4` - ViewModel integration
- `androidx.datastore:datastore-preferences:1.1.1` - Preferences storage

### ShareConnect Integration
- Project modules: `:Asinka:asinka`, `:DesignSystem`, `:Onboarding`
- Toolkit modules: `:Toolkit:Main`, `:Toolkit:SecurityAccess`
- Sync modules: `:ThemeSync`, `:ProfileSync`, `:HistorySync`, `:RSSSync`, `:BookmarkSync`, `:PreferencesSync`, `:LanguageSync`, `:TorrentSharingSync`

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `org.jetbrains.kotlin:kotlin-test:2.0.0` - Kotlin test framework
- `io.mockk:mockk:1.13.11` - Mocking framework
- `app.cash.turbine:turbine:1.1.0` - Flow testing utilities

---

*For more information, visit [https://shareconnect.org/docs/qbitconnect](https://shareconnect.org/docs/qbitconnect)*