# MotrixConnect

## Overview and Purpose

MotrixConnect is a specialized Android application that provides comprehensive integration with Motrix download manager. Motrix is a full-featured download manager supporting HTTP, FTP, BitTorrent, and Magnet links, built on top of Aria2. The app connects to Motrix instances via JSON-RPC protocol, enabling remote control and monitoring of downloads.

MotrixConnect extends the ShareConnect ecosystem by enabling high-performance, multi-connection downloads with advanced features like bandwidth control, download scheduling, and comprehensive progress tracking.

## Architecture and Components

MotrixConnect is built using Kotlin with Jetpack Compose for UI, implementing JSON-RPC protocol for communication with Aria2/Motrix backend.

### Core Components
- **MotrixConnectApplication**: Main application class managing lifecycle and sync managers
- **DependencyContainer**: Handles dependency injection and service initialization
- **MotrixApiClient**: JSON-RPC client for Motrix/Aria2 communication
- **RPC Protocol**: Custom JSON-RPC 2.0 implementation for Aria2

### Data Layer
- **MotrixApiClient**: Manages all JSON-RPC communication
- **Authentication**: Token-based RPC secret authentication
- **Data Models**: Download, statistics, version, and option models

### UI Layer (Jetpack Compose)
- **MainActivity**: Primary application entry point with navigation
- **OnboardingActivity**: First-time setup and server configuration
- **DownloadListScreen**: Active downloads monitoring interface
- **DownloadDetailScreen**: Detailed download view with controls
- **SettingsScreen**: Application preferences and server management

### Sync Integration
MotrixConnect integrates with ShareConnect's sync ecosystem:
- **ThemeSyncManager**: Theme synchronization (port 8890)
- **ProfileSyncManager**: Server profile management (port 8900)
- **HistorySyncManager**: Download history (port 8910)
- **RSSSyncManager**: RSS feed integration (port 8920)
- **BookmarkSyncManager**: Download bookmark management (port 8930)
- **PreferencesSyncManager**: User preference synchronization (port 8940)
- **LanguageSyncManager**: Language settings sync (port 8950)

## API Reference

### MotrixApiClient

#### System Operations
```kotlin
/**
 * Get Aria2/Motrix version information
 * @return Result containing version and enabled features
 */
suspend fun getVersion(): Result<MotrixVersion>

/**
 * Get global download statistics
 * @return Result containing download/upload speeds and task counts
 */
suspend fun getGlobalStat(): Result<MotrixGlobalStat>

/**
 * Get global configuration options
 * @return Result containing all global options
 */
suspend fun getGlobalOption(): Result<Map<String, String>>

/**
 * Change global configuration options
 * @param options Map of option name to value
 * @return Result indicating success or failure
 */
suspend fun changeGlobalOption(options: Map<String, String>): Result<Unit>

/**
 * Shutdown Aria2
 * @return Result indicating success or failure
 */
suspend fun shutdown(): Result<Unit>
```

#### Download Management
```kotlin
/**
 * Add a new download by URI
 * @param uri Download URL
 * @param options Optional download configuration
 * @return Result containing GID (download identifier)
 */
suspend fun addUri(uri: String, options: MotrixDownloadOptions? = null): Result<String>

/**
 * Add multiple URIs for the same download (mirrors)
 * @param uris List of mirror URLs
 * @param options Optional download configuration
 * @return Result containing GID
 */
suspend fun addUris(uris: List<String>, options: MotrixDownloadOptions? = null): Result<String>

/**
 * Add a torrent file download
 * @param torrentData Base64-encoded torrent file data
 * @param options Optional download configuration
 * @return Result containing GID
 */
suspend fun addTorrent(torrentData: String, options: MotrixDownloadOptions? = null): Result<String>

/**
 * Add a metalink download
 * @param metalinkData Base64-encoded metalink file data
 * @param options Optional download configuration
 * @return Result containing GID
 */
suspend fun addMetalink(metalinkData: String, options: MotrixDownloadOptions? = null): Result<String>
```

#### Download Status and Control
```kotlin
/**
 * Get download status by GID
 * @param gid Download identifier
 * @return Result containing download details
 */
suspend fun tellStatus(gid: String): Result<MotrixDownload>

/**
 * Get list of active downloads
 * @return Result containing active downloads
 */
suspend fun tellActive(): Result<List<MotrixDownload>>

/**
 * Get list of waiting downloads
 * @param offset Pagination offset (default 0)
 * @param limit Maximum items to return (default 100)
 * @return Result containing waiting downloads
 */
suspend fun tellWaiting(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>>

/**
 * Get list of stopped downloads
 * @param offset Pagination offset (default 0)
 * @param limit Maximum items to return (default 100)
 * @return Result containing stopped downloads
 */
suspend fun tellStopped(offset: Int = 0, limit: Int = 100): Result<List<MotrixDownload>>

/**
 * Pause a download
 * @param gid Download identifier
 * @return Result containing GID if successful
 */
suspend fun pause(gid: String): Result<String>

/**
 * Pause all active downloads
 * @return Result indicating success or failure
 */
suspend fun pauseAll(): Result<Unit>

/**
 * Unpause a download
 * @param gid Download identifier
 * @return Result containing GID if successful
 */
suspend fun unpause(gid: String): Result<String>

/**
 * Unpause all paused downloads
 * @return Result indicating success or failure
 */
suspend fun unpauseAll(): Result<Unit>

/**
 * Remove a download
 * @param gid Download identifier
 * @return Result containing GID if successful
 */
suspend fun remove(gid: String): Result<String>

/**
 * Force remove a download (even if active)
 * @param gid Download identifier
 * @return Result containing GID if successful
 */
suspend fun forceRemove(gid: String): Result<String>

/**
 * Remove download result (from completed/error/removed status)
 * @param gid Download identifier
 * @return Result indicating success or failure
 */
suspend fun removeDownloadResult(gid: String): Result<Unit>

/**
 * Purge all download results
 * @return Result indicating success or failure
 */
suspend fun purgeDownloadResult(): Result<Unit>
```

#### Advanced Operations
```kotlin
/**
 * Get URIs used for a download
 * @param gid Download identifier
 * @return Result containing list of URIs with status
 */
suspend fun getUris(gid: String): Result<List<MotrixUri>>

/**
 * Get files for a download
 * @param gid Download identifier
 * @return Result containing file information
 */
suspend fun getFiles(gid: String): Result<List<MotrixFile>>

/**
 * Get peers for BitTorrent download
 * @param gid Download identifier
 * @return Result containing peer information
 */
suspend fun getPeers(gid: String): Result<List<MotrixPeer>>

/**
 * Get options for a specific download
 * @param gid Download identifier
 * @return Result containing download options
 */
suspend fun getOption(gid: String): Result<Map<String, String>>

/**
 * Change options for a specific download
 * @param gid Download identifier
 * @param options Map of option name to value
 * @return Result indicating success or failure
 */
suspend fun changeOption(gid: String, options: Map<String, String>): Result<Unit>

/**
 * Change position in download queue
 * @param gid Download identifier
 * @param pos New position (0=top, negative=bottom)
 * @param how "POS_SET", "POS_CUR", or "POS_END"
 * @return Result containing new position
 */
suspend fun changePosition(gid: String, pos: Int, how: String): Result<Int>
```

## Key Classes and Their Responsibilities

### MotrixConnectApplication
- **Responsibilities**:
  - Application lifecycle management
  - Dependency injection container initialization
  - Sync manager initialization and coordination
  - Global coroutine scope management
  - Application-wide error handling

### MotrixApiClient
- **Responsibilities**:
  - JSON-RPC 2.0 protocol implementation
  - RPC secret token authentication
  - Request ID generation and tracking
  - Error response handling
  - Connection pooling and timeout management
  - Gson serialization/deserialization

## Data Models

### System Models

#### MotrixVersion
```kotlin
data class MotrixVersion(
    val version: String,                     // Aria2 version (e.g., "1.36.0")
    val enabledFeatures: List<String>?       // Enabled features (BitTorrent, HTTPS, etc.)
)
```

#### MotrixGlobalStat
```kotlin
data class MotrixGlobalStat(
    val downloadSpeed: String,               // Global download speed (bytes/sec as string)
    val uploadSpeed: String,                 // Global upload speed (bytes/sec as string)
    val numActive: String,                   // Number of active downloads
    val numWaiting: String,                  // Number of waiting downloads
    val numStopped: String,                  // Number of stopped downloads
    val numStoppedTotal: String              // Total stopped (completed + error + removed)
)
```

### Download Models

#### MotrixDownload
```kotlin
data class MotrixDownload(
    val gid: String,                         // Download GID (unique identifier)
    val status: String,                      // Status: active, waiting, paused, error, complete, removed
    val totalLength: String,                 // Total file size in bytes (as string)
    val completedLength: String,             // Downloaded bytes (as string)
    val uploadLength: String,                // Uploaded bytes for torrents (as string)
    val downloadSpeed: String,               // Current download speed (bytes/sec as string)
    val uploadSpeed: String,                 // Current upload speed (bytes/sec as string)
    val files: List<DownloadFile>?,          // List of files in download
    val directory: String?,                  // Download directory
    val errorCode: String?,                  // Error code if failed
    val errorMessage: String?,               // Error message if failed
    val connections: String?                 // Number of connections
) {
    data class DownloadFile(
        val index: String,                   // File index
        val path: String,                    // File path
        val length: String,                  // File size in bytes
        val completedLength: String,         // Downloaded bytes
        val selected: String,                // "true" if selected for download
        val uris: List<Uri>?                 // URIs for this file
    ) {
        data class Uri(
            val uri: String,                 // URI
            val status: String               // "used" or "waiting"
        )
    }
}
```

#### MotrixDownloadOptions
```kotlin
data class MotrixDownloadOptions(
    val directory: String? = null,           // Download directory
    val outputFileName: String? = null,      // Output file name
    val connections: Int? = null,            // Max connections per server (1-16)
    val maxDownloadSpeed: String? = null,    // Max download speed (e.g., "1M")
    val maxUploadSpeed: String? = null,      // Max upload speed (for torrents)
    val header: List<String>? = null,        // Custom HTTP headers
    val checkIntegrity: Boolean? = null,     // Verify file integrity
    val continue: Boolean? = null,           // Continue partial download
    val referer: String? = null,             // HTTP referer
    val userAgent: String? = null            // Custom user agent
)
```

### RPC Protocol Models

#### MotrixRpcRequest
```kotlin
data class MotrixRpcRequest(
    val jsonrpc: String = "2.0",             // JSON-RPC version
    val id: String,                          // Request ID (UUID)
    val method: String,                      // RPC method name (e.g., "aria2.addUri")
    val params: List<Any>                    // Method parameters
)
```

#### MotrixRpcResponse
```kotlin
data class MotrixRpcResponse<T>(
    val jsonrpc: String,                     // JSON-RPC version
    val id: String,                          // Request ID (matches request)
    val result: T?,                          // Result data
    val error: RpcError?                     // Error if request failed
) {
    data class RpcError(
        val code: Int,                       // Error code
        val message: String                  // Error message
    )
}
```

## Usage Examples

### Initializing MotrixConnect

```kotlin
class MotrixConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize dependency container
        val container = DependencyContainer(this)

        // Initialize sync managers
        val themeSyncManager = ThemeSyncManager.getInstance(
            this,
            "com.shareconnect.motrixconnect",
            "MotrixConnect",
            "1.0.0"
        )
        themeSyncManager.startSync()

        val profileSyncManager = ProfileSyncManager.getInstance(
            this,
            "com.shareconnect.motrixconnect",
            "MotrixConnect",
            "1.0.0"
        )
        profileSyncManager.startSync()

        // Additional sync managers...
    }
}
```

### Connecting to Motrix Server

```kotlin
// Create API client
val apiClient = MotrixApiClient(
    serverUrl = "http://192.168.1.100:6800",
    secret = "my-rpc-secret-token"  // Optional, if configured
)

// Test connection and get version
val versionResult = apiClient.getVersion()
if (versionResult.isSuccess) {
    val version = versionResult.getOrNull()!!
    println("Connected to Aria2 ${version.version}")
    println("Features: ${version.enabledFeatures?.joinToString()}")
} else {
    println("Connection failed: ${versionResult.exceptionOrNull()?.message}")
}

// Get global statistics
val statsResult = apiClient.getGlobalStat()
if (statsResult.isSuccess) {
    val stats = statsResult.getOrNull()!!
    println("Download: ${formatSpeed(stats.downloadSpeed)}")
    println("Upload: ${formatSpeed(stats.uploadSpeed)}")
    println("Active: ${stats.numActive} | Waiting: ${stats.numWaiting}")
}
```

### Adding Downloads

```kotlin
// Simple download
val downloadResult = apiClient.addUri("https://example.com/file.zip")

if (downloadResult.isSuccess) {
    val gid = downloadResult.getOrNull()!!
    println("Download added with GID: $gid")
}

// Download with options
val options = MotrixDownloadOptions(
    directory = "/downloads/videos",
    outputFileName = "my-video.mp4",
    connections = 16,  // Max connections
    maxDownloadSpeed = "5M",  // 5 MB/s limit
    userAgent = "Mozilla/5.0..."
)

val advancedResult = apiClient.addUri(
    uri = "https://example.com/large-file.iso",
    options = options
)

// Download with multiple mirrors
val mirrors = listOf(
    "https://mirror1.com/file.zip",
    "https://mirror2.com/file.zip",
    "https://mirror3.com/file.zip"
)

val mirrorResult = apiClient.addUris(uris = mirrors)
```

### Monitoring Downloads

```kotlin
// Get all active downloads
val activeResult = apiClient.tellActive()

if (activeResult.isSuccess) {
    val downloads = activeResult.getOrNull()!!

    downloads.forEach { download ->
        val totalMB = download.totalLength.toLong() / 1_000_000
        val completedMB = download.completedLength.toLong() / 1_000_000
        val progress = (completedMB.toFloat() / totalMB * 100).toInt()
        val speedMBps = download.downloadSpeed.toLong() / 1_000_000

        println("GID: ${download.gid}")
        println("  Status: ${download.status}")
        println("  Progress: $completedMB / $totalMB MB ($progress%)")
        println("  Speed: $speedMBps MB/s")
        println("  Connections: ${download.connections}")
        println()
    }
}

// Get specific download status
val statusResult = apiClient.tellStatus(gid = "abc123")
if (statusResult.isSuccess) {
    val download = statusResult.getOrNull()!!

    // Check if completed
    if (download.status == "complete") {
        println("Download complete!")
        download.files?.forEach { file ->
            println("  File: ${file.path}")
        }
    } else if (download.status == "error") {
        println("Download failed: ${download.errorMessage}")
    }
}
```

### Controlling Downloads

```kotlin
// Pause a download
val pauseResult = apiClient.pause(gid = "abc123")

if (pauseResult.isSuccess) {
    println("Download paused")
}

// Resume a download
val unpauseResult = apiClient.unpause(gid = "abc123")

// Pause all downloads
apiClient.pauseAll()

// Resume all downloads
apiClient.unpauseAll()

// Remove a download
val removeResult = apiClient.remove(gid = "abc123")

// Force remove (even if downloading)
val forceRemoveResult = apiClient.forceRemove(gid = "abc123")

// Clean up completed downloads
apiClient.purgeDownloadResult()
```

### Advanced Download Management

```kotlin
// Get download files
val filesResult = apiClient.getFiles(gid = "abc123")
if (filesResult.isSuccess) {
    val files = filesResult.getOrNull()!!
    files.forEach { file ->
        println("${file.path} - ${file.completedLength}/${file.length} bytes")
    }
}

// Change download options on-the-fly
val newOptions = mapOf(
    "max-download-limit" to "10M",  // Change speed limit
    "max-connection-per-server" to "8"  // Change connections
)

apiClient.changeOption(gid = "abc123", options = newOptions)

// Move download to top of queue
apiClient.changePosition(
    gid = "abc123",
    pos = 0,
    how = "POS_SET"  // Set absolute position
)

// Get download URIs and status
val urisResult = apiClient.getUris(gid = "abc123")
if (urisResult.isSuccess) {
    val uris = urisResult.getOrNull()!!
    uris.forEach { uri ->
        println("${uri.uri} - Status: ${uri.status}")
    }
}
```

### Global Configuration

```kotlin
// Get all global options
val globalOptsResult = apiClient.getGlobalOption()
if (globalOptsResult.isSuccess) {
    val options = globalOptsResult.getOrNull()!!
    println("Download dir: ${options["dir"]}")
    println("Max concurrent: ${options["max-concurrent-downloads"]}")
}

// Change global settings
val newGlobalOpts = mapOf(
    "max-concurrent-downloads" to "5",
    "max-connection-per-server" to "16",
    "max-overall-download-limit" to "10M",
    "max-overall-upload-limit" to "1M"
)

apiClient.changeGlobalOption(newGlobalOpts)
```

### Complete Download Management Workflow

```kotlin
suspend fun downloadWithProgress(url: String, outputPath: String) {
    val apiClient = MotrixApiClient(
        serverUrl = "http://localhost:6800",
        secret = null
    )

    // 1. Add download
    val options = MotrixDownloadOptions(
        directory = File(outputPath).parent,
        outputFileName = File(outputPath).name,
        connections = 16
    )

    val addResult = apiClient.addUri(url, options)
    if (addResult.isFailure) {
        println("Failed to add download: ${addResult.exceptionOrNull()?.message}")
        return
    }

    val gid = addResult.getOrNull()!!
    println("Download started: $gid")

    // 2. Monitor progress
    while (true) {
        delay(1000)  // Check every second

        val statusResult = apiClient.tellStatus(gid)
        if (statusResult.isFailure) break

        val download = statusResult.getOrNull()!!

        when (download.status) {
            "active" -> {
                val progress = calculateProgress(download)
                val speed = download.downloadSpeed.toLong() / 1_000_000
                println("Progress: $progress% - Speed: $speed MB/s")
            }
            "complete" -> {
                println("Download completed!")
                break
            }
            "error" -> {
                println("Download failed: ${download.errorMessage}")
                break
            }
            "removed" -> {
                println("Download was cancelled")
                break
            }
        }
    }

    // 3. Clean up
    apiClient.removeDownloadResult(gid)
}

fun calculateProgress(download: MotrixDownload): Int {
    val total = download.totalLength.toLongOrNull() ?: 0
    val completed = download.completedLength.toLongOrNull() ?: 0
    return if (total > 0) ((completed.toFloat() / total) * 100).toInt() else 0
}
```

## Dependencies

### Core Android
```gradle
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
implementation("androidx.activity:activity-compose:1.9.0")
```

### Jetpack Compose
```gradle
implementation(platform("androidx.compose:compose-bom:2025.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.8.0")
```

### Networking
```gradle
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### Serialization
```gradle
implementation("com.google.code.gson:gson:2.10.1")
```

### ShareConnect Modules
```gradle
implementation(project(":Asinka:asinka"))
implementation(project(":ThemeSync"))
implementation(project(":ProfileSync"))
implementation(project(":HistorySync"))
implementation(project(":RSSSync"))
implementation(project(":BookmarkSync"))
implementation(project(":PreferencesSync"))
implementation(project(":LanguageSync"))
implementation(project(":DesignSystem"))
implementation(project(":Onboarding"))
implementation(project(":Toolkit:Main"))
```

### Testing
```gradle
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("org.robolectric:robolectric:4.11.1")
testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
```

## Security Considerations

### Authentication
- **RPC Secret**: Token-based authentication for Aria2 RPC interface
- **Local access**: Consider firewall rules for remote access
- **HTTPS**: Supports HTTPS for encrypted communication

### Network Security
- **JSON-RPC**: Standard JSON-RPC 2.0 protocol
- **Request validation**: All requests validated before sending
- **Error handling**: Proper error codes and messages

## Performance Optimization

### Download Performance
- **Multi-connection**: Up to 16 connections per server
- **Segmented downloading**: Files split into segments
- **Mirror support**: Automatic failover to mirrors
- **Bandwidth control**: Global and per-download limits

### API Efficiency
- **Connection pooling**: Reuses HTTP connections
- **Batch operations**: Multiple downloads managed efficiently
- **Async operations**: All API calls async with coroutines

## Testing

MotrixConnect includes comprehensive test coverage:

### Unit Tests (39 tests)
- **MotrixApiClientTest**: API client JSON-RPC tests
- **MotrixModelsTest**: Data model serialization tests

### Integration Tests (15 tests)
- **MotrixApiClientIntegrationTest**: Full API integration tests

### Automation Tests (6 tests)
- **MotrixConnectAutomationTest**: App launch and lifecycle tests

**Total Coverage**: 60 tests

## Aria2 Configuration

### Required Aria2 Settings
```conf
# Enable RPC
enable-rpc=true
rpc-listen-all=true
rpc-listen-port=6800

# RPC secret (recommended)
rpc-secret=your-secret-token

# Download settings
max-concurrent-downloads=5
max-connection-per-server=16
min-split-size=10M
split=16

# File allocation
file-allocation=falloc
```

## Known Limitations

### Protocol Limitations
- **String types**: Aria2 returns numbers as strings in JSON
- **Batch requests**: Not all methods support batch calls
- **WebSocket**: Only HTTP JSON-RPC supported (not WebSocket)

### Download Limitations
- **Max connections**: 16 connections per server (Aria2 limit)
- **Protocols**: Depends on Aria2 compilation features
- **Metalink**: Full metalink support requires XML parsing

## Future Enhancements

- **WebSocket support**: Real-time download notifications
- **Bandwidth scheduling**: Time-based bandwidth limits
- **Download categories**: Organize downloads by category
- **Batch operations**: Add multiple downloads from list
- **Download templates**: Predefined download configurations

---

**Last Updated**: 2025-10-25
**Version**: 1.0.0
**Aria2 Compatibility**: 1.35.0+
**Motrix Compatibility**: 1.6.0+
**Android API**: 28-36
