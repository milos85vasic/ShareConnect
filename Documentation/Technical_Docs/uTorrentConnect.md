# uTorrentConnect

## Overview and Purpose

uTorrentConnect is a specialized Android application that provides seamless integration with the uTorrent BitTorrent client. It offers a mobile interface for managing torrent downloads with features like remote control, file prioritization, and download scheduling. The app connects to uTorrent WebUI instances running on Windows systems.

## Architecture and Components

uTorrentConnect is built on a hybrid architecture combining Java and Kotlin code with ShareConnect ecosystem integration:

### Core Components
- **MainActivity**: Main application interface for torrent management
- **uTorrentRemote**: Core uTorrent WebUI client functionality
- **SecurityAccessHelper**: Authentication and security management
- **HistoryActivity**: Torrent history and completed downloads display

### UI Components
- **Torrent List View**: Displays active downloads with progress indicators
- **Torrent Details Dialog**: Shows detailed torrent information and controls
- **Add Torrent Interface**: Support for magnet links and torrent files
- **Settings Panel**: Server configuration and application preferences

### Integration Components
- **Asinka Sync**: Cross-device torrent data synchronization
- **ThemeSync**: Theme consistency with ShareConnect apps
- **ProfileSync**: Server profile management
- **SecurityAccess**: Authentication integration

## API Reference

### uTorrentRemote
```kotlin
class uTorrentRemote {
    fun connect(host: String, port: Int, username: String?, password: String?): Boolean
    fun addTorrent(url: String): Boolean
    fun pauseTorrent(hash: String): Boolean
    fun resumeTorrent(hash: String): Boolean
    fun removeTorrent(hash: String, deleteData: Boolean): Boolean
    fun getTorrents(): List<Torrent>
    fun getTorrentDetails(hash: String): Torrent?
    fun setFilePriority(hash: String, fileIndex: Int, priority: Priority): Boolean
}
```

### SecurityAccessHelper
```kotlin
class SecurityAccessHelper {
    fun isAccessGranted(): Boolean
    fun requestAuthentication(callback: (Boolean) -> Unit)
    fun onAuthenticationSuccess()
    fun onAuthenticationFailure()
}
```

## Key Classes and Their Responsibilities

### MainActivity
- **Responsibilities**:
  - Main application entry point and UI orchestration
  - Torrent list display and real-time updates
  - Server connection management and status monitoring
  - Navigation between different app sections
  - Integration with ShareConnect security features

### uTorrentRemote
- **Responsibilities**:
  - WebUI API communication with uTorrent client
  - Torrent CRUD operations and state management
  - File prioritization and download control
  - Authentication handling and session management
  - Error recovery and connection resilience

### HistoryActivity
- **Responsibilities**:
  - Display historical torrent downloads and completions
  - Search and filtering of torrent history
  - Metadata display and management
  - Integration with ShareConnect history sync

### uTorrentConnectOnboardingActivity
- **Responsibilities**:
  - First-time setup wizard for new users
  - uTorrent server discovery and configuration
  - WebUI enablement guidance
  - Permission setup and initial preferences

## Data Models

### Torrent
```kotlin
data class Torrent(
    val hash: String,
    val name: String,
    val size: Long,
    val downloaded: Long,
    val uploadSpeed: Long,
    val downloadSpeed: Long,
    val progress: Float,
    val status: TorrentStatus,
    val eta: Long,
    val peers: Int,
    val seeds: Int,
    val ratio: Float,
    val label: String?,
    val files: List<TorrentFile>
)
```

### TorrentFile
```kotlin
data class TorrentFile(
    val name: String,
    val size: Long,
    val downloaded: Long,
    val priority: Priority,
    val index: Int
)
```

### ServerProfile
```kotlin
data class ServerProfile(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 8080,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false
)
```

### Priority
```kotlin
enum class Priority {
    SKIP,
    LOW,
    NORMAL,
    HIGH
}
```

## Usage Examples

### Connecting to uTorrent Server
```kotlin
val remote = uTorrentRemote()
val connected = remote.connect("192.168.1.100", 8080, "admin", "password")
if (connected) {
    // Connection successful
}
```

### Managing Torrents
```kotlin
val remote = uTorrentRemote()

// Add torrent via magnet link
remote.addTorrent("magnet:?xt=urn:btih:...")

// Get torrent list
val torrents = remote.getTorrents()

// Control torrent state
remote.pauseTorrent(torrentHash)
remote.resumeTorrent(torrentHash)
remote.removeTorrent(torrentHash, true) // Remove with data
```

### File Prioritization
```kotlin
val remote = uTorrentRemote()

// Set high priority for specific file
remote.setFilePriority(torrentHash, fileIndex, Priority.HIGH)

// Skip unwanted files
remote.setFilePriority(torrentHash, fileIndex, Priority.SKIP)
```

## Dependencies

### Core uTorrent Dependencies
- `com.squareup.okhttp3:okhttp:4.12.0` - HTTP client for WebUI API
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - Network request logging

### Android Framework Dependencies
- `androidx.appcompat:appcompat:1.6.1` - AppCompat support
- `androidx.core:core-ktx:1.12.0` - Core Kotlin extensions
- `com.google.android.material:material:1.11.0` - Material Design components
- `androidx.activity:activity-ktx:1.8.2` - Activity extensions
- `androidx.fragment:fragment-ktx:1.6.6` - Fragment extensions

### ShareConnect Integration
- Project modules: `:Asinka:asinka`, `:DesignSystem`, `:Onboarding`
- Toolkit modules: `:Toolkit:Main`, `:Toolkit:SecurityAccess`
- Sync modules: `:ThemeSync`, `:ProfileSync`

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing framework
- `androidx.test.ext:junit:1.3.0` - Android test runner
- `androidx.test.espresso:espresso-core:3.7.0` - UI testing
- `org.mockito:mockito-core:5.8.0` - Mocking framework

---

*For more information, visit [https://shareconnect.org/docs/utorrentconnect](https://shareconnect.org/docs/utorrentconnect)*