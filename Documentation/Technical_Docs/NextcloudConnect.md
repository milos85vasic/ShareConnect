# NextcloudConnect

## Overview and Purpose

NextcloudConnect is a specialized Android application that provides comprehensive integration with Nextcloud file hosting and collaboration platform. It offers a modern mobile interface for managing files, folders, sharing, and accessing cloud storage. The app connects to Nextcloud instances via WebDAV protocol and Nextcloud OCS (Open Collaboration Services) API.

NextcloudConnect extends the ShareConnect ecosystem by enabling sharing from cloud storage to other connected services (download managers, torrent clients, media servers), transforming cloud storage from an endpoint into a sharing source.

## Architecture and Components

NextcloudConnect is built using Kotlin with Jetpack Compose for UI, following the MVVM architecture pattern with Repository pattern for data management and WebDAV/REST API integration.

### Core Components
- **NextcloudConnectApplication**: Main application class managing lifecycle and sync managers
- **DependencyContainer**: Handles dependency injection and service initialization
- **NextcloudApiClient**: HTTP client for Nextcloud WebDAV and OCS API communication
- **NextcloudApiService**: Retrofit service interface for Nextcloud REST endpoints

### Data Layer
- **NextcloudApiClient**: Manages all API communication with Nextcloud servers
- **Authentication**: HTTP Basic Authentication with Base64 encoding
- **Data Models**: File, folder, share, user, and server status models

### UI Layer (Jetpack Compose)
- **MainActivity**: Primary application entry point with navigation
- **OnboardingActivity**: First-time setup and server configuration flow
- **FileListScreen**: File and folder browsing interface
- **FileDetailScreen**: Detailed file view with download/upload/share actions
- **SettingsScreen**: Application preferences and server management

### Sync Integration
NextcloudConnect integrates with ShareConnect's sync ecosystem:
- **ThemeSyncManager**: Theme synchronization (port 8890)
- **ProfileSyncManager**: Server profile management (port 8900)
- **HistorySyncManager**: File sharing history (port 8910)
- **RSSSyncManager**: RSS feed integration (port 8920)
- **BookmarkSyncManager**: File/folder bookmark management (port 8930)
- **PreferencesSyncManager**: User preference synchronization (port 8940)
- **LanguageSyncManager**: Language settings sync (port 8950)

## API Reference

### NextcloudApiClient

#### Server Operations
```kotlin
/**
 * Get server status and version information
 * @return Result containing server status
 */
suspend fun getServerStatus(): Result<NextcloudStatus>

/**
 * Get current authenticated user information
 * @return Result containing user details including quota
 */
suspend fun getUserInfo(): Result<NextcloudUser>
```

#### File Operations (WebDAV)
```kotlin
/**
 * List files and folders in a directory
 * Note: Returns XML response that needs parsing
 * @param path Directory path (relative to user root)
 * @return Result containing XML response with file list
 */
suspend fun listFiles(path: String = ""): Result<String>

/**
 * Download a file from Nextcloud
 * @param path File path (relative to user root)
 * @return Result containing file data as byte array
 */
suspend fun downloadFile(path: String): Result<ByteArray>

/**
 * Upload a file to Nextcloud
 * @param path Destination path (relative to user root)
 * @param data File data as byte array
 * @param mimeType MIME type of the file (default: application/octet-stream)
 * @return Result indicating success or failure
 */
suspend fun uploadFile(
    path: String,
    data: ByteArray,
    mimeType: String = "application/octet-stream"
): Result<Unit>

/**
 * Create a new folder
 * @param path Folder path to create (relative to user root)
 * @return Result indicating success or failure
 */
suspend fun createFolder(path: String): Result<Unit>

/**
 * Delete a file or folder
 * @param path Path to delete (relative to user root)
 * @return Result indicating success or failure
 */
suspend fun deleteFile(path: String): Result<Unit>

/**
 * Move/rename a file or folder
 * @param sourcePath Source path (relative to user root)
 * @param destinationPath Destination path (relative to user root)
 * @return Result indicating success or failure
 */
suspend fun moveFile(sourcePath: String, destinationPath: String): Result<Unit>

/**
 * Copy a file or folder
 * @param sourcePath Source path (relative to user root)
 * @param destinationPath Destination path (relative to user root)
 * @return Result indicating success or failure
 */
suspend fun copyFile(sourcePath: String, destinationPath: String): Result<Unit>
```

#### Sharing Operations (OCS API)
```kotlin
/**
 * Create a public share link for a file or folder
 * @param path Path to share (relative to user root)
 * @param password Optional password protection
 * @param expireDate Optional expiration date (YYYY-MM-DD)
 * @param permissions Share permissions (1=read, 15=read/write)
 * @return Result containing share information with public link
 */
suspend fun createShare(
    path: String,
    password: String? = null,
    expireDate: String? = null,
    permissions: Int = 1
): Result<NextcloudShare>

/**
 * Get all shares for current user
 * @return Result containing list of shares
 */
suspend fun getShares(): Result<List<NextcloudShare>>

/**
 * Delete a share
 * @param shareId Share identifier
 * @return Result indicating success or failure
 */
suspend fun deleteShare(shareId: String): Result<Unit>

/**
 * Update share permissions or password
 * @param shareId Share identifier
 * @param password New password (null to remove)
 * @param permissions New permissions
 * @return Result containing updated share
 */
suspend fun updateShare(
    shareId: String,
    password: String? = null,
    permissions: Int? = null
): Result<NextcloudShare>
```

## Key Classes and Their Responsibilities

### NextcloudConnectApplication
- **Responsibilities**:
  - Application lifecycle management
  - Dependency injection container initialization
  - Sync manager initialization and coordination
  - Global coroutine scope management
  - Application-wide error handling

### NextcloudApiClient
- **Responsibilities**:
  - HTTP client management for Nextcloud API communication
  - HTTP Basic Authentication header generation
  - WebDAV protocol implementation for file operations
  - OCS API REST calls for sharing and user management
  - Error handling and retry logic
  - Connection pooling and timeout management

### NextcloudApiService
- **Responsibilities**:
  - Retrofit service interface definitions
  - HTTP method annotations for REST endpoints
  - Request/response type declarations
  - Path and query parameter handling

## Data Models

### Server Models

#### NextcloudStatus
```kotlin
data class NextcloudStatus(
    val installed: Boolean,              // Installation status
    val maintenance: Boolean,            // Maintenance mode flag
    val needsDbUpgrade: Boolean,         // Database upgrade required
    val version: String,                 // Version number (e.g., "28.0.0.1")
    val versionString: String,           // Version string (e.g., "28.0.0")
    val edition: String,                 // Edition (e.g., "Community")
    val productName: String              // Product name (e.g., "Nextcloud")
)
```

### User Models

#### NextcloudUser
```kotlin
data class NextcloudUser(
    val id: String,                      // User ID
    val displayName: String?,            // Display name
    val email: String?,                  // Email address
    val quota: Quota?                    // Storage quota information
) {
    data class Quota(
        val free: Long,                  // Free space in bytes
        val used: Long,                  // Used space in bytes
        val total: Long,                 // Total space in bytes
        val relative: Float              // Usage percentage (0-100)
    )
}
```

### File Models

#### NextcloudFile
```kotlin
data class NextcloudFile(
    val id: String,                      // File/folder ID
    val name: String,                    // File/folder name
    val path: String,                    // Full path
    val type: String,                    // "file" or "directory"
    val size: Long,                      // Size in bytes
    val modifiedTime: Long,              // Last modified timestamp (Unix time)
    val mimeType: String?,               // MIME type (null for directories)
    val etag: String?,                   // ETag for caching
    val permissions: String?             // Permission string (e.g., "RDNVW")
)
```

### Sharing Models

#### NextcloudShare
```kotlin
data class NextcloudShare(
    val id: String,                      // Share ID
    val shareType: Int,                  // Share type (0=user, 1=group, 3=public)
    val shareWith: String?,              // User/group shared with (null for public)
    val shareWithDisplayName: String?,   // Display name of share recipient
    val path: String,                    // Path of shared item
    val permissions: Int,                // Permissions (1=read, 15=read/write)
    val shareTime: Long,                 // Share creation timestamp
    val expiration: String?,             // Expiration date (YYYY-MM-DD)
    val token: String?,                  // Public share token
    val url: String?,                    // Public share URL
    val password: Boolean,               // Has password protection
    val sendPasswordByTalk: Boolean,     // Password sent via Talk
    val mailSend: Boolean                // Email notification sent
)
```

### API Response Wrappers

#### NextcloudResponse
```kotlin
data class NextcloudResponse<T>(
    val ocs: OcsData<T>                  // OCS API wrapper
) {
    data class OcsData<T>(
        val meta: OcsMeta,               // Response metadata
        val data: T                      // Actual response data
    ) {
        data class OcsMeta(
            val status: String,          // Status (ok, failure)
            val statusCode: Int,         // HTTP status code
            val message: String?         // Error message if any
        )
    }
}
```

## Usage Examples

### Initializing NextcloudConnect

```kotlin
class NextcloudConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize dependency container
        val container = DependencyContainer(this)

        // Initialize sync managers
        val themeSyncManager = ThemeSyncManager.getInstance(
            this,
            "com.shareconnect.nextcloudconnect",
            "NextcloudConnect",
            "1.0.0"
        )
        themeSyncManager.startSync()

        val profileSyncManager = ProfileSyncManager.getInstance(
            this,
            "com.shareconnect.nextcloudconnect",
            "NextcloudConnect",
            "1.0.0"
        )
        profileSyncManager.startSync()

        // Additional sync managers...
    }
}
```

### Connecting to Nextcloud Server

```kotlin
// Create API client with credentials
val apiClient = NextcloudApiClient(
    serverUrl = "https://cloud.example.com",
    username = "user@example.com",
    password = "app-password-token"
)

// Test connection and get server status
val statusResult = apiClient.getServerStatus()
if (statusResult.isSuccess) {
    val status = statusResult.getOrNull()!!
    println("Connected to ${status.productName} ${status.versionString}")
    println("Edition: ${status.edition}")
    println("Maintenance mode: ${status.maintenance}")
} else {
    println("Connection failed: ${statusResult.exceptionOrNull()?.message}")
}

// Get user information and quota
val userResult = apiClient.getUserInfo()
if (userResult.isSuccess) {
    val user = userResult.getOrNull()!!
    println("Logged in as: ${user.displayName} (${user.id})")

    user.quota?.let { quota ->
        val usedGB = quota.used / 1_000_000_000.0
        val totalGB = quota.total / 1_000_000_000.0
        println("Storage: ${String.format("%.2f", usedGB)} / ${String.format("%.2f", totalGB)} GB (${quota.relative}%)")
    }
}
```

### Browsing Files and Folders

```kotlin
// List files in root directory
val listResult = apiClient.listFiles(path = "")

if (listResult.isSuccess) {
    val xmlResponse = listResult.getOrNull()!!

    // Parse XML response (example using XML parser)
    val files = parseWebDAVResponse(xmlResponse)

    files.forEach { file ->
        if (file.type == "directory") {
            println("📁 ${file.name}")
        } else {
            val sizeKB = file.size / 1024
            println("📄 ${file.name} (${sizeKB} KB)")
        }
    }
}

// List files in specific folder
val documentsResult = apiClient.listFiles(path = "Documents")
```

### Downloading Files

```kotlin
// Download a file
val downloadResult = apiClient.downloadFile(path = "Documents/report.pdf")

if (downloadResult.isSuccess) {
    val fileData = downloadResult.getOrNull()!!

    // Save to local storage
    val outputFile = File(getExternalFilesDir(null), "report.pdf")
    outputFile.writeBytes(fileData)

    println("Downloaded ${fileData.size} bytes to ${outputFile.absolutePath}")
} else {
    println("Download failed: ${downloadResult.exceptionOrNull()?.message}")
}
```

### Uploading Files

```kotlin
// Read file from local storage
val localFile = File(getExternalFilesDir(null), "photo.jpg")
val fileData = localFile.readBytes()

// Upload to Nextcloud
val uploadResult = apiClient.uploadFile(
    path = "Photos/vacation/photo.jpg",
    data = fileData,
    mimeType = "image/jpeg"
)

if (uploadResult.isSuccess) {
    println("File uploaded successfully")
} else {
    println("Upload failed: ${uploadResult.exceptionOrNull()?.message}")
}
```

### Creating and Managing Folders

```kotlin
// Create a new folder
val createResult = apiClient.createFolder(path = "Projects/2025")

if (createResult.isSuccess) {
    println("Folder created")

    // Create nested folder
    apiClient.createFolder(path = "Projects/2025/Q1")
}

// Move a file
val moveResult = apiClient.moveFile(
    sourcePath = "Documents/old-report.pdf",
    destinationPath = "Archive/2024/old-report.pdf"
)

// Copy a file
val copyResult = apiClient.copyFile(
    sourcePath = "Templates/template.docx",
    destinationPath = "Documents/new-document.docx"
)

// Delete a file
val deleteResult = apiClient.deleteFile(path = "Temp/old-file.tmp")
```

### Creating Public Share Links

```kotlin
// Create a public share link with password
val shareResult = apiClient.createShare(
    path = "Documents/presentation.pdf",
    password = "SecurePass123",
    expireDate = "2025-12-31",
    permissions = 1  // Read-only
)

if (shareResult.isSuccess) {
    val share = shareResult.getOrNull()!!

    println("Share created!")
    println("Public URL: ${share.url}")
    println("Share token: ${share.token}")
    println("Expires: ${share.expiration}")
    println("Password protected: ${share.password}")

    // Share the URL with others
    shareUrl(share.url!!)
}
```

### Managing Existing Shares

```kotlin
// Get all shares
val sharesResult = apiClient.getShares()

if (sharesResult.isSuccess) {
    val shares = sharesResult.getOrNull()!!

    shares.forEach { share ->
        println("Share ID: ${share.id}")
        println("  Path: ${share.path}")
        println("  Type: ${getShareTypeName(share.shareType)}")
        println("  URL: ${share.url ?: "N/A"}")
        println("  Permissions: ${getPermissionsString(share.permissions)}")
        println()
    }
}

// Update share (add password)
val updateResult = apiClient.updateShare(
    shareId = "12345",
    password = "NewPassword456"
)

// Delete a share
val deleteShareResult = apiClient.deleteShare(shareId = "12345")
```

### Complete File Management Workflow

```kotlin
suspend fun manageProjectFiles(apiClient: NextcloudApiClient) {
    // 1. Create project structure
    apiClient.createFolder("Projects/MyProject")
    apiClient.createFolder("Projects/MyProject/src")
    apiClient.createFolder("Projects/MyProject/docs")

    // 2. Upload project files
    val sourceFiles = listOf("Main.kt", "README.md", "build.gradle")
    sourceFiles.forEach { filename ->
        val localFile = File(localProjectDir, filename)
        if (localFile.exists()) {
            apiClient.uploadFile(
                path = "Projects/MyProject/$filename",
                data = localFile.readBytes(),
                mimeType = getMimeType(filename)
            )
        }
    }

    // 3. Create share link for documentation
    val docShareResult = apiClient.createShare(
        path = "Projects/MyProject/docs",
        permissions = 1,  // Read-only
        expireDate = getDateAfterDays(90)
    )

    if (docShareResult.isSuccess) {
        val docShare = docShareResult.getOrNull()!!
        // Send share link to team
        notifyTeam("Documentation available at: ${docShare.url}")
    }

    // 4. Backup entire project
    val backupResult = apiClient.copyFile(
        sourcePath = "Projects/MyProject",
        destinationPath = "Backups/MyProject_${getCurrentDate()}"
    )

    println("Project setup complete!")
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
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.retrofit2:converter-scalars:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### XML Parsing (for WebDAV)
```gradle
implementation("org.simpleframework:simple-xml:2.7.1")
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
- **HTTP Basic Auth**: Base64-encoded credentials in Authorization header
- **App passwords**: Supports Nextcloud app-specific passwords (recommended)
- **Token storage**: Credentials never stored, only in-memory during session

### Data Protection
- **HTTPS required**: All production instances must use HTTPS
- **Certificate validation**: Proper SSL/TLS certificate checking
- **No credential logging**: Credentials excluded from logs

### Network Security
- **Timeout handling**: Proper timeout configuration
- **Connection pooling**: Efficient connection reuse
- **Error handling**: Graceful failure without exposing sensitive info

## Performance Optimization

### File Transfer
- **Chunked uploads**: Large files uploaded in chunks
- **Streaming downloads**: Files downloaded without full buffering
- **Progress tracking**: Upload/download progress callbacks
- **Resumable transfers**: Support for resuming interrupted transfers

### Caching
- **File metadata caching**: Reduce repeated file list requests
- **ETag support**: Conditional requests using ETags
- **Thumbnail caching**: Local thumbnail cache

### Background Operations
- **WorkManager**: Background file sync
- **Coroutines**: Async operations on IO dispatcher
- **Flow**: Reactive updates for file changes

## Testing

NextcloudConnect includes comprehensive test coverage:

### Unit Tests (36 tests)
- **NextcloudApiClientTest**: API client tests with MockWebServer
- **NextcloudModelsTest**: Data model serialization tests

### Integration Tests (16 tests)
- **NextcloudApiClientIntegrationTest**: Full API integration tests
- File operations, sharing, user management

### Automation Tests (5 tests)
- **NextcloudConnectAutomationTest**: App launch and lifecycle tests

**Total Coverage**: 52 tests

## Known Limitations

### WebDAV Limitations
- **XML Parsing**: WebDAV responses in XML format (requires parsing)
- **Quota**: Quota information only available via OCS API
- **Versioning**: File versioning requires separate API calls

### API Version Support
- **Nextcloud 20+**: Recommended minimum version
- **OCS API v2**: Uses OCS API v2.php endpoint
- **WebDAV**: CalDAV/CardDAV not implemented

## Future Enhancements

- **Calendar/Contact sync**: CalDAV and CardDAV support
- **Activity feed**: Real-time activity notifications
- **Offline mode**: Local file caching and sync
- **File search**: Full-text search integration
- **Collaborative editing**: Nextcloud Office integration

---

**Last Updated**: 2025-10-25
**Version**: 1.0.0
**Nextcloud Compatibility**: 20.0+
**Android API**: 28-36
