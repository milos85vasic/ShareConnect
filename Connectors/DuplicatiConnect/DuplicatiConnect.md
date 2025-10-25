# DuplicatiConnect Technical Documentation

## Table of Contents

1. [Overview](#overview)
2. [Architecture](#architecture)
3. [API Implementation](#api-implementation)
4. [Data Models](#data-models)
5. [Features](#features)
6. [Setup and Configuration](#setup-and-configuration)
7. [API Endpoints](#api-endpoints)
8. [Authentication](#authentication)
9. [Backup Operations](#backup-operations)
10. [Restore Operations](#restore-operations)
11. [Progress Monitoring](#progress-monitoring)
12. [Error Handling](#error-handling)
13. [Testing](#testing)
14. [Development Guidelines](#development-guidelines)
15. [Troubleshooting](#troubleshooting)

---

## Overview

**DuplicatiConnect** is an Android application that provides remote management and control of Duplicati backup servers. Duplicati is a free, open-source backup client that securely stores encrypted, incremental, compressed backups to cloud storage services and remote file servers.

### Key Capabilities

- **Backup Management**: Create, configure, update, and delete backup jobs
- **On-Demand Operations**: Run backups immediately without waiting for schedule
- **File Restoration**: Restore specific files or entire directories from backups
- **Real-Time Monitoring**: Track backup progress with detailed metrics
- **Log Analysis**: View comprehensive backup logs and error messages
- **Multi-Backend Support**: Works with any Duplicati-supported storage backend
- **Scheduling**: Configure automated backup schedules
- **Verification**: Verify backup integrity and repair corrupted backups

### Integration with ShareConnect

DuplicatiConnect is part of the ShareConnect ecosystem and integrates with:

- **ThemeSync**: Unified theme management across all ShareConnect apps
- **ProfileSync**: Shared server profile management
- **HistorySync**: Synchronized operation history
- **SecurityAccess**: PIN-based security layer
- **LanguageSync**: Multi-language support with synchronized preferences

---

## Architecture

### Application Structure

```
DuplicatiConnect/
├── DuplicatiConnector/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/shareconnect/duplicaticonnect/
│   │   │   │   ├── DuplicatiConnectApplication.kt
│   │   │   │   ├── ui/
│   │   │   │   │   └── MainActivity.kt
│   │   │   │   └── data/
│   │   │   │       ├── api/
│   │   │   │       │   └── DuplicatiApiClient.kt
│   │   │   │       └── models/
│   │   │   │           └── DuplicatiModels.kt
│   │   │   ├── res/
│   │   │   └── AndroidManifest.xml
│   │   ├── test/
│   │   │   └── kotlin/
│   │   │       └── DuplicatiApiClientTest.kt
│   │   └── androidTest/
│   │       └── kotlin/
│   │           ├── DuplicatiApiClientIntegrationTest.kt
│   │           └── automation/
│   │               └── DuplicatiConnectAutomationTest.kt
│   └── build.gradle
├── build.gradle
├── DuplicatiConnect.md
└── DuplicatiConnect_User_Manual.md
```

### Core Components

#### 1. DuplicatiConnectApplication

The main application class that initializes all sync managers:

- **ThemeSyncManager**: Port 8890 + hash offset
- **ProfileSyncManager**: Port 8900 + hash offset
- **HistorySyncManager**: Port 8910 + hash offset
- **RSSSyncManager**: Port 8920 + hash offset
- **BookmarkSyncManager**: Port 8930 + hash offset
- **PreferencesSyncManager**: Port 8940 + hash offset
- **LanguageSyncManager**: Port 8950 + hash offset
- **TorrentSharingSyncManager**: Port 8960 + hash offset

Each sync manager:
- Uses gRPC for inter-app communication
- Implements automatic port allocation with conflict resolution
- Starts with staggered delays (100ms apart) to prevent binding conflicts
- Broadcasts changes to all connected ShareConnect apps

#### 2. MainActivity

Compose-based UI with integrated SecurityAccess:

```kotlin
SecurityAccess(
    viewModel = securityViewModel,
    pinLength = PinLength.FOUR,
    onAuthenticationSuccess = {
        // Show main content
    }
) {
    DuplicatiConnectContent()
}
```

Features:
- PIN-based authentication (4-digit)
- Material Design 3 theme
- Responsive layout
- Feature showcase interface

#### 3. DuplicatiApiClient

Comprehensive REST API client implementation:

```kotlin
class DuplicatiApiClient(
    private val baseUrl: String,
    private val password: String? = null,
    private val timeoutSeconds: Long = 30
)
```

Features:
- OkHttp-based HTTP client
- Gson JSON serialization
- Automatic authentication header injection
- Result-based error handling
- Configurable timeouts
- Connection pooling

---

## API Implementation

### HTTP Client Configuration

```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(timeoutSeconds, TimeUnit.SECONDS)
    .readTimeout(timeoutSeconds, TimeUnit.SECONDS)
    .writeTimeout(timeoutSeconds, TimeUnit.SECONDS)
    .followRedirects(true)
    .followSslRedirects(true)
    .addInterceptor { chain ->
        val requestBuilder = chain.request().newBuilder()
        authToken?.let { requestBuilder.header("X-Auth-Token", it) }
        chain.proceed(requestBuilder.build())
    }
    .build()
```

### Request Execution Pattern

All API methods follow this pattern:

```kotlin
suspend fun apiMethod(): Result<ResponseType> = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("$baseUrl/api/v1/endpoint")
        .method(body)
        .build()

    executeRequest(request)
}
```

The `executeRequest` method handles:
- Network execution
- HTTP status code checking
- Response body parsing
- Error wrapping in Result type
- Resource cleanup

---

## Data Models

### DuplicatiServerState

Represents the current state of the Duplicati server:

```kotlin
data class DuplicatiServerState(
    val programState: String,           // "Running", "Paused", "Stopped"
    val lastEventId: Long,
    val lastDataUpdateId: Long,
    val activeTask: ActiveTask?,        // Currently running operation
    val pausedUntil: String?,
    val suggestedStatusIcon: String,
    val hasWarning: Boolean,
    val hasError: Boolean
)
```

### DuplicatiBackup

Complete backup job configuration:

```kotlin
data class DuplicatiBackup(
    val id: String,
    val name: String,
    val description: String?,
    val tags: List<String>?,
    val targetUrl: String,              // Backup destination
    val dbPath: String?,                // Local database path
    val sources: List<String>,          // Paths to backup
    val settings: List<BackupSetting>?, // Configuration options
    val filters: List<BackupFilter>?,   // Include/exclude rules
    val schedule: BackupSchedule?,      // Automated schedule
    val metadata: BackupMetadata?       // Runtime statistics
)
```

### BackupSetting

Individual configuration setting:

```kotlin
data class BackupSetting(
    val name: String,    // "encryption-module", "compression-module", etc.
    val value: String,   // Setting value
    val filter: String?  // Optional filter expression
)
```

Common settings:
- `encryption-module`: "aes", "gpg"
- `passphrase`: Encryption password
- `compression-module`: "zip", "7z"
- `dblock-size`: Block size in bytes
- `retention-policy`: Backup retention rules

### BackupFilter

Include/exclude pattern for backup sources:

```kotlin
data class BackupFilter(
    val order: Int,         // Filter priority
    val include: Boolean,   // true = include, false = exclude
    val expression: String  // Pattern (regex or glob)
)
```

Example filters:
```kotlin
BackupFilter(order = 0, include = false, expression = "*.tmp")
BackupFilter(order = 1, include = false, expression = "*.cache")
BackupFilter(order = 2, include = true, expression = "/important/*")
```

### BackupSchedule

Automated backup scheduling:

```kotlin
data class BackupSchedule(
    val id: Long,
    val tags: List<String>?,
    val time: String,              // ISO 8601 time
    val repeat: String,            // "1D", "1W", "1M"
    val lastRun: String?,
    val rule: String,              // Cron-like expression
    val allowedDays: List<String>? // ["Monday", "Wednesday", "Friday"]
)
```

### BackupMetadata

Runtime statistics and metrics:

```kotlin
data class BackupMetadata(
    val lastBackupDate: String?,
    val lastBackupStarted: String?,
    val lastBackupFinished: String?,
    val lastBackupDuration: String?,
    val lastBackupFileCount: Long?,
    val lastBackupFileSize: Long?,
    val lastBackupAddedFiles: Long?,
    val lastBackupModifiedFiles: Long?,
    val lastBackupDeletedFiles: Long?,
    val sourceFilesCount: Long?,
    val sourceFilesSize: Long?
)
```

### DuplicatiProgress

Real-time backup/restore progress:

```kotlin
data class DuplicatiProgress(
    val phase: String,                  // Current operation phase
    val overall: ProgressMetrics,       // Total progress
    val current: ProgressMetrics,       // Current file progress
    val processedFileCount: Long,
    val processedFileSize: Long,
    val totalFileCount: Long,
    val totalFileSize: Long,
    val stillCounting: Boolean          // Still scanning files
)

data class ProgressMetrics(
    val speed: Double,   // Bytes per second
    val count: Long,     // File count
    val size: Long       // Total bytes
)
```

### DuplicatiRestoreOptions

Configuration for restore operations:

```kotlin
data class DuplicatiRestoreOptions(
    val time: String? = null,           // Point-in-time restore
    val restorePath: String? = null,    // Destination path
    val overwrite: Boolean = false,     // Overwrite existing files
    val permissions: Boolean = true,     // Restore permissions
    val skipMetadata: Boolean = false,  // Skip metadata restore
    val paths: List<String> = emptyList() // Files to restore
)
```

---

## Features

### 1. Backup Job Management

**Create Backup Job**:
```kotlin
val newBackup = DuplicatiBackup(
    id = "",
    name = "Documents Backup",
    description = "Daily backup of important documents",
    tags = listOf("important", "daily"),
    targetUrl = "file:///mnt/backups/documents",
    sources = listOf("/home/user/documents"),
    settings = listOf(
        BackupSetting("encryption-module", "aes", null),
        BackupSetting("passphrase", "securePassword", null),
        BackupSetting("compression-module", "zip", null)
    ),
    filters = listOf(
        BackupFilter(0, false, "*.tmp"),
        BackupFilter(1, false, "*.cache")
    ),
    schedule = null,
    metadata = null,
    isTemporary = false
)

val result = apiClient.createBackup(newBackup)
```

**List All Backups**:
```kotlin
val result = apiClient.getBackups()
if (result.isSuccess) {
    val backups = result.getOrNull()
    backups?.forEach { backup ->
        println("${backup.name}: ${backup.sources.size} sources")
    }
}
```

**Update Backup**:
```kotlin
val updatedBackup = existingBackup.copy(
    description = "Updated description"
)
val result = apiClient.updateBackup(backupId, updatedBackup)
```

**Delete Backup**:
```kotlin
// Delete configuration only
val result = apiClient.deleteBackup(backupId, deleteRemoteFiles = false)

// Delete configuration and remote backup files
val result = apiClient.deleteBackup(backupId, deleteRemoteFiles = true)
```

### 2. Backup Operations

**Run Backup Immediately**:
```kotlin
val result = apiClient.runBackup(backupId)
if (result.isSuccess) {
    val run = result.getOrNull()
    println("Backup started with task ID: ${run?.id}")
}
```

**Monitor Progress**:
```kotlin
val progressResult = apiClient.getBackupsProgress()
if (progressResult.isSuccess) {
    val progressList = progressResult.getOrNull()
    progressList?.forEach { progress ->
        val percent = (progress.processedFileCount * 100.0) / progress.totalFileCount
        println("Progress: $percent% (${progress.phase})")
        println("Speed: ${progress.overall.speed / 1024 / 1024} MB/s")
    }
}
```

**Stop/Abort Backup**:
```kotlin
// Graceful stop (finishes current file)
apiClient.stopBackup(backupId)

// Immediate abort
apiClient.abortBackup(backupId)
```

**Pause/Resume All Backups**:
```kotlin
// Pause for 2 hours
apiClient.pauseBackups(duration = "2h")

// Resume immediately
apiClient.resumeBackups()
```

### 3. Restore Operations

**Restore Specific Files**:
```kotlin
val options = DuplicatiRestoreOptions(
    time = "2025-10-25T00:00:00Z",  // Point-in-time
    restorePath = "/tmp/restore",    // Destination
    overwrite = false,               // Don't overwrite existing
    permissions = true,              // Restore file permissions
    paths = listOf(
        "/home/user/documents/report.pdf",
        "/home/user/documents/presentation.pptx"
    )
)

val result = apiClient.restoreFiles(backupId, options)
```

**Restore Entire Backup**:
```kotlin
val options = DuplicatiRestoreOptions(
    restorePath = "/restore/full",
    overwrite = true,
    paths = emptyList()  // Empty = restore everything
)

val result = apiClient.restoreFiles(backupId, options)
```

### 4. Backup Verification and Repair

**Verify Backup Integrity**:
```kotlin
val result = apiClient.verifyBackup(backupId)
if (result.isSuccess) {
    println("Verification started")
}
```

**Repair Corrupted Backup**:
```kotlin
val result = apiClient.repairBackup(backupId)
if (result.isSuccess) {
    println("Repair operation started")
}
```

**Test Connection**:
```kotlin
val result = apiClient.testConnection(backupId)
if (result.isSuccess) {
    println("Connection to backup target successful")
} else {
    println("Connection failed: ${result.exceptionOrNull()?.message}")
}
```

### 5. Log Analysis

**Get Backup Logs**:
```kotlin
val result = apiClient.getBackupLog(
    backupId = backupId,
    offset = 0,
    pageSize = 100
)

if (result.isSuccess) {
    val logs = result.getOrNull()
    logs?.forEach { log ->
        println("[${log.type}] ${log.timestamp}: ${log.message}")
        log.exception?.let { println("Exception: $it") }
    }
}
```

**Get Remote Logs**:
```kotlin
val result = apiClient.getBackupRemoteLog(backupId)
```

### 6. System Information

**Get Server State**:
```kotlin
val result = apiClient.getServerState()
if (result.isSuccess) {
    val state = result.getOrNull()
    println("Server state: ${state?.programState}")
    println("Active task: ${state?.activeTask?.taskType}")
    println("Has warnings: ${state?.hasWarning}")
    println("Has errors: ${state?.hasError}")
}
```

**Get System Info**:
```kotlin
val result = apiClient.getSystemInfo()
if (result.isSuccess) {
    val info = result.getOrNull()
    println("Duplicati version: ${info?.serverVersion}")
    println("OS: ${info?.osType} ${info?.osVersion}")
    println("Machine: ${info?.machineName}")
}
```

### 7. Filesystem Browsing

**Browse Directories**:
```kotlin
val result = apiClient.browseFilesystem("/home/user")
if (result.isSuccess) {
    val entries = result.getOrNull()
    entries?.forEach { entry ->
        val type = if (entry.isFolder) "DIR" else "FILE"
        println("$type: ${entry.path}")
    }
}
```

### 8. Notifications

**Get Recent Notifications**:
```kotlin
val result = apiClient.getNotifications()
if (result.isSuccess) {
    val notifications = result.getOrNull()
    notifications?.forEach { notification ->
        println("[${notification.type}] ${notification.title}")
        println("  ${notification.message}")
        notification.backupId?.let { println("  Backup: $it") }
    }
}
```

---

## Setup and Configuration

### 1. Add to settings.gradle

```groovy
include ':Connectors:DuplicatiConnect:DuplicatiConnector'
```

### 2. Dependencies

All dependencies are configured in `build.gradle`:

```groovy
dependencies {
    // ShareConnect modules
    implementation project(':Asinka:asinka')
    implementation project(':DesignSystem')
    implementation project(':Onboarding')
    implementation project(':Localizations')

    // Sync managers
    implementation project(':ThemeSync')
    implementation project(':ProfileSync')
    // ... (all 8 sync managers)

    // Retrofit for REST API
    implementation "com.squareup.retrofit2:retrofit:2.11.0"
    implementation "com.squareup.retrofit2:converter-gson:2.11.0"

    // OkHttp
    implementation "com.squareup.okhttp3:okhttp:4.12.0"
}
```

### 3. Permissions

Required in AndroidManifest.xml:

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

### 4. Network Security

Configure `network_security_config.xml` for HTTPS/HTTP:

```xml
<network-security-config>
    <base-config cleartextTrafficPermitted="true">
        <trust-anchors>
            <certificates src="system" />
            <certificates src="user" />
        </trust-anchors>
    </base-config>
</network-security-config>
```

---

## Authentication

Duplicati supports password-based authentication:

### Authentication Flow

1. **Create Client with Password**:
```kotlin
val apiClient = DuplicatiApiClient(
    baseUrl = "http://192.168.1.100:8200",
    password = "mySecurePassword"
)
```

2. **Login**:
```kotlin
val result = apiClient.login()
if (result.isSuccess) {
    // Authentication successful
    // Auth token stored automatically
}
```

3. **Subsequent Requests**:
All subsequent API calls automatically include the auth token in headers.

### No Authentication

For servers without password protection:

```kotlin
val apiClient = DuplicatiApiClient(
    baseUrl = "http://localhost:8200"
    // No password parameter
)
```

---

## Error Handling

All API methods return `Result<T>` for consistent error handling:

### Success Handling

```kotlin
val result = apiClient.getBackups()
if (result.isSuccess) {
    val backups = result.getOrNull()
    // Process backups
}
```

### Error Handling

```kotlin
val result = apiClient.runBackup(backupId)
if (result.isFailure) {
    val exception = result.exceptionOrNull()
    when {
        exception?.message?.contains("404") == true ->
            println("Backup not found")
        exception?.message?.contains("500") == true ->
            println("Server error")
        exception is IOException ->
            println("Network error: ${exception.message}")
        else ->
            println("Unknown error: ${exception?.message}")
    }
}
```

### Common Error Types

- **HTTP 401**: Authentication required
- **HTTP 403**: Forbidden (invalid password)
- **HTTP 404**: Resource not found (backup ID invalid)
- **HTTP 500**: Server internal error
- **IOException**: Network connectivity issues
- **Parse Error**: Invalid JSON response

---

## Testing

### Test Coverage

Total: **42 tests** across 3 test suites

#### Unit Tests (21 tests)

File: `DuplicatiApiClientTest.kt`

- Login authentication
- Server state retrieval
- Backup list operations
- Individual backup operations
- Run backup
- Restore files
- Delete backup
- Log retrieval
- Progress monitoring
- Filesystem browsing
- Notifications
- Backup creation
- Pause/resume
- Stop/abort
- System info
- Verification
- Repair
- Error handling (HTTP errors, network errors)

#### Integration Tests (19 tests)

File: `DuplicatiApiClientIntegrationTest.kt`

- Full authentication flow
- Server state with active tasks
- Backup list with metadata
- Backup execution and monitoring
- File restore operations
- Log retrieval with pagination
- Filesystem browsing
- Notifications with errors
- Backup creation and updates
- Backup deletion with remote files
- Pause/resume flow
- Stop/abort operations
- System information
- Connection testing
- Backup verification
- Backup repair
- Error handling with invalid JSON
- Server error handling
- Timeout configuration

#### Automation Tests (10 tests)

File: `DuplicatiConnectAutomationTest.kt`

- App launch verification
- Main content display
- Feature list display
- UI responsiveness
- Card container display
- Theme application
- Accessibility labels
- Screen layout structure
- Startup error checking
- Content scrollability

### Running Tests

**All tests**:
```bash
./gradlew :Connectors:DuplicatiConnect:DuplicatiConnector:test
./gradlew :Connectors:DuplicatiConnect:DuplicatiConnector:connectedAndroidTest
```

**Unit tests only**:
```bash
./gradlew :Connectors:DuplicatiConnect:DuplicatiConnector:testDebugUnitTest
```

**Integration tests only**:
```bash
./gradlew :Connectors:DuplicatiConnect:DuplicatiConnector:connectedDebugAndroidTest
```

**Specific test**:
```bash
./gradlew :Connectors:DuplicatiConnect:DuplicatiConnector:test --tests "DuplicatiApiClientTest.test login success"
```

---

## Development Guidelines

### Code Style

- Follow Kotlin coding conventions
- Use meaningful variable names
- Document public APIs with KDoc
- Keep functions focused and small
- Use data classes for models
- Prefer immutability

### API Client Development

When adding new endpoints:

1. Add data models to `DuplicatiModels.kt`
2. Add method to `DuplicatiApiClient.kt`
3. Use `Result<T>` return type
4. Add unit tests
5. Add integration tests
6. Update documentation

### Example New Endpoint

```kotlin
// 1. Add model
data class BackupStatistics(
    val totalBackups: Int,
    val totalSize: Long,
    val lastBackupTime: String
)

// 2. Add method
suspend fun getBackupStatistics(): Result<BackupStatistics> =
    withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("$baseUrl/api/v1/statistics")
            .get()
            .build()

        executeRequest(request)
    }

// 3. Add test
@Test
fun `test get backup statistics`() = runBlocking {
    val response = """{"totalBackups": 5, "totalSize": 1073741824}"""
    mockServer.enqueue(MockResponse().setBody(response))

    val result = apiClient.getBackupStatistics()

    assertTrue(result.isSuccess)
    assertEquals(5, result.getOrNull()?.totalBackups)
}
```

---

## Troubleshooting

### Common Issues

**1. Connection Refused**

```
Error: Connection refused
```

**Solution**:
- Verify Duplicati server is running
- Check server URL and port
- Verify network connectivity
- Check firewall settings

**2. Authentication Failed**

```
Error: HTTP 403 Forbidden
```

**Solution**:
- Verify password is correct
- Check server authentication settings
- Ensure login() was called before other operations

**3. Port Binding Conflicts**

```
Error: BindException: Address already in use
```

**Solution**:
- Each sync manager uses unique port range
- Staggered initialization (100ms delays) prevents conflicts
- Check `findAvailablePort()` implementation

**4. Parse Errors**

```
Error: Failed to parse response
```

**Solution**:
- Verify Duplicati server version compatibility
- Check API version (should be v1)
- Enable logging to see raw responses

### Debug Logging

Enable OkHttp logging:

```kotlin
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val client = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

---

## Performance Considerations

### Timeouts

Default timeout: 30 seconds

Adjust for slow networks:
```kotlin
val apiClient = DuplicatiApiClient(
    baseUrl = baseUrl,
    timeoutSeconds = 60 // 1 minute
)
```

### Connection Pooling

OkHttp automatically manages connection pooling for optimal performance.

### Memory Management

Always close the API client when done:

```kotlin
apiClient.close()
```

This releases:
- Thread pool resources
- Connection pool
- Socket connections

---

## API Reference Summary

### Server Operations
- `getServerState()` - Get server status
- `pauseBackups(duration)` - Pause all backups
- `resumeBackups()` - Resume all backups
- `getSystemInfo()` - Get system information

### Backup Management
- `getBackups()` - List all backups
- `getBackup(id)` - Get backup details
- `createBackup(backup)` - Create new backup
- `updateBackup(id, backup)` - Update backup
- `deleteBackup(id, deleteRemote)` - Delete backup

### Backup Operations
- `runBackup(id)` - Run backup now
- `stopBackup(id)` - Stop backup gracefully
- `abortBackup(id)` - Abort backup immediately
- `verifyBackup(id)` - Verify backup integrity
- `repairBackup(id)` - Repair backup database
- `testConnection(id)` - Test backup target connection

### Restore Operations
- `restoreFiles(id, options)` - Restore files

### Monitoring
- `getBackupsProgress()` - Get progress for active operations
- `getBackupLog(id, offset, pageSize)` - Get backup logs
- `getBackupRemoteLog(id)` - Get remote backup logs
- `getNotifications()` - Get system notifications

### Filesystem
- `browseFilesystem(path)` - Browse filesystem

### Authentication
- `login()` - Authenticate with server

---

## License

Part of the ShareConnect project.
