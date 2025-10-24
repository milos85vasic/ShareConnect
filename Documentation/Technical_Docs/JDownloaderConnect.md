# JDownloaderConnect

## Overview and Purpose

JDownloaderConnect is a specialized Android application that provides seamless integration with the JDownloader download manager. It enables users to send download links directly to JDownloader for processing, supporting a wide range of file hosting services, streaming platforms, and direct download links. The app leverages JDownloader's powerful download capabilities and link recognition.

## Architecture and Components

JDownloaderConnect is designed as a lightweight connector application that integrates with the MyJDownloader service:

### Core Components
- **MainActivity**: Main application interface for link sharing
- **JDownloaderApiClient**: Handles communication with MyJDownloader API
- **LinkProcessor**: Processes and validates download links
- **DeviceManager**: Manages JDownloader device connections

### UI Components
- **Link Input Interface**: Manual link entry and clipboard detection
- **Device Selection**: Choose target JDownloader device
- **Download Queue**: View pending and active downloads
- **Settings Panel**: API configuration and preferences

### Integration Components
- **Asinka Sync**: Cross-device synchronization
- **ThemeSync**: Theme consistency with ShareConnect apps
- **ProfileSync**: API profile management
- **SecurityAccess**: Authentication integration

## API Reference

### JDownloaderApiClient
```kotlin
class JDownloaderApiClient {
    suspend fun connect(email: String, password: String): Result<String>
    suspend fun getDevices(): Result<List<Device>>
    suspend fun addLinks(deviceId: String, links: List<String>): Result<Unit>
    suspend fun getDownloads(deviceId: String): Result<List<Download>>
    suspend fun startDownload(deviceId: String, packageId: String): Result<Unit>
    suspend fun pauseDownload(deviceId: String, packageId: String): Result<Unit>
}
```

### LinkProcessor
```kotlin
class LinkProcessor {
    fun validateLink(url: String): LinkValidationResult
    fun extractLinks(text: String): List<String>
    fun categorizeLink(url: String): LinkCategory
    fun getSupportedHosts(): List<String>
}
```

### DeviceManager
```kotlin
class DeviceManager {
    suspend fun discoverDevices(): List<Device>
    suspend fun connectToDevice(deviceId: String): Result<DeviceConnection>
    suspend fun getDeviceStatus(deviceId: String): DeviceStatus
    fun setDefaultDevice(deviceId: String)
    fun getDefaultDevice(): Device?
}
```

## Key Classes and Their Responsibilities

### MainActivity
- **Responsibilities**:
  - Main application entry point and UI orchestration
  - Link input handling and validation
  - Device selection and connection management
  - Download status monitoring
  - Integration with ShareConnect sharing system

### JDownloaderApiClient
- **Responsibilities**:
  - MyJDownloader API authentication and session management
  - Device discovery and connection handling
  - Link submission and download management
  - Error handling and API rate limiting
  - Response parsing and data transformation

### LinkProcessor
- **Responsibilities**:
  - URL validation and format checking
  - Link extraction from text and clipboard
  - Content type detection and categorization
  - Support for JDownloader's extensive host list
  - Link preprocessing and optimization

### DeviceManager
- **Responsibilities**:
  - JDownloader device discovery and enumeration
  - Device connection state management
  - Default device preference handling
  - Device capability detection
  - Connection health monitoring

## Data Models

### Device
```kotlin
data class Device(
    val id: String,
    val name: String,
    val type: DeviceType,
    val status: DeviceStatus,
    val lastSeen: Long
)
```

### Download
```kotlin
data class Download(
    val id: String,
    val name: String,
    val url: String,
    val size: Long,
    val progress: Float,
    val status: DownloadStatus,
    val speed: Long,
    val eta: Long,
    val packageId: String
)
```

### LinkValidationResult
```kotlin
data class LinkValidationResult(
    val isValid: Boolean,
    val url: String,
    val category: LinkCategory,
    val supported: Boolean,
    val errorMessage: String? = null
)
```

### LinkCategory
```kotlin
enum class LinkCategory {
    STREAMING_VIDEO,
    STREAMING_AUDIO,
    FILE_HOSTING,
    PREMIUM_HOSTING,
    DIRECT_DOWNLOAD,
    TORRENT,
    ARCHIVE,
    UNKNOWN
}
```

## Usage Examples

### Connecting to MyJDownloader
```kotlin
val apiClient = JDownloaderApiClient()
val result = apiClient.connect("user@example.com", "password")
result.onSuccess { sessionToken ->
    // Connection successful
}.onFailure { error ->
    // Handle connection error
}
```

### Adding Download Links
```kotlin
val apiClient = JDownloaderApiClient()
val deviceManager = DeviceManager()

// Get available devices
val devices = apiClient.getDevices().getOrNull() ?: emptyList()

// Add links to default device
val defaultDevice = deviceManager.getDefaultDevice()
if (defaultDevice != null) {
    val links = listOf(
        "https://youtube.com/watch?v=...",
        "https://mega.nz/file/..."
    )
    apiClient.addLinks(defaultDevice.id, links)
}
```

### Processing Clipboard Content
```kotlin
val linkProcessor = LinkProcessor()
val clipboardText = getClipboardText()

// Extract and validate links
val links = linkProcessor.extractLinks(clipboardText)
val validLinks = links.filter { link ->
    linkProcessor.validateLink(link).isValid
}

// Categorize links
val categorizedLinks = validLinks.map { link ->
    link to linkProcessor.categorizeLink(link)
}
```

## Dependencies

### JDownloader API Dependencies
- `com.squareup.okhttp3:okhttp:4.12.0` - HTTP client for API calls
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - Network logging
- `org.jsoup:jsoup:1.18.1` - HTML parsing for link extraction

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

*For more information, visit [https://shareconnect.org/docs/jdownloaderconnect](https://shareconnect.org/docs/jdownloaderconnect)*