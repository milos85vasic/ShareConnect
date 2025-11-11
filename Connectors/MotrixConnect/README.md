# MotrixConnect - Motrix/Aria2 Android Client

MotrixConnect is an Android application that provides native client functionality for Motrix download manager and Aria2 servers. Built with Kotlin and Jetpack Compose, it offers complete JSON-RPC protocol support for managing downloads.

## 🎯 Overview

MotrixConnect enables Android apps to interact with Motrix/Aria2 servers for:
- **Download Management**: Add, pause, resume, remove downloads
- **Transfer Control**: Set speed limits, manage connections
- **Queue Management**: Handle waiting and active downloads
- **Batch Operations**: Pause/resume all downloads at once
- **Session Management**: Save/restore download sessions

## 📱 Features

### Download Operations
- Add downloads via HTTP, HTTPS, FTP, BitTorrent
- Monitor download progress and speed
- Pause/resume individual or all downloads
- Remove downloads (normal or force)
- Purge completed downloads

### Transfer Management
- Set download/upload speed limits
- Configure connection counts
- Manage global and per-download options
- View real-time statistics

### Queue Control
- List active downloads
- List waiting downloads
- List stopped downloads (paused, complete, error)
- Pagination support for large lists

## 🔧 API Reference

### MotrixApiClient

#### Initialization
```kotlin
val apiClient = MotrixApiClient(
    serverUrl = "http://localhost:16800",
    secret = "your-rpc-secret"  // Optional
)
```

#### Download Management
```kotlin
// Add single download
val gidResult = apiClient.addUri("https://example.com/file.zip")
val gid = gidResult.getOrThrow()

// Add with options
val options = MotrixDownloadOptions(
    directory = "/downloads",
    outputFileName = "myfile.zip",
    connections = 16,
    maxDownloadSpeed = "5M"
)
apiClient.addUri("https://example.com/file.zip", options)

// Add with multiple mirrors
apiClient.addUris(
    listOf(
        "https://mirror1.com/file.zip",
        "https://mirror2.com/file.zip"
    )
)

// Get download status
val statusResult = apiClient.tellStatus(gid)
val download = statusResult.getOrThrow()
println("Status: ${download.status}, Speed: ${download.downloadSpeed}")
```

#### Download Control
```kotlin
// Pause download
apiClient.pause(gid)

// Resume download
apiClient.unpause(gid)

// Remove download
apiClient.remove(gid)

// Force remove (even if active)
apiClient.forceRemove(gid)

// Delete from list
apiClient.removeDownloadResult(gid)

// Batch operations
apiClient.pauseAll()
apiClient.unpauseAll()
```

#### Queue Management
```kotlin
// Get active downloads
val activeDownloads = apiClient.tellActive().getOrThrow()

// Get waiting downloads
val waitingDownloads = apiClient.tellWaiting(offset = 0, limit = 10).getOrThrow()

// Get stopped downloads
val stoppedDownloads = apiClient.tellStopped(offset = 0, limit = 10).getOrThrow()
```

#### Statistics & Options
```kotlin
// Get global statistics
val stat = apiClient.getGlobalStat().getOrThrow()
println("Download Speed: ${stat.downloadSpeed}")
println("Active: ${stat.numActive}, Waiting: ${stat.numWaiting}")

// Get global options
val globalOptions = apiClient.getGlobalOption().getOrThrow()

// Change global options
apiClient.changeGlobalOption(mapOf(
    "max-download-limit" to "10M",
    "max-concurrent-downloads" to "5"
))

// Get download-specific options
val downloadOptions = apiClient.getOption(gid).getOrThrow()

// Change download options
apiClient.changeOption(gid, mapOf("max-download-limit" to "2M"))
```

#### Session Management
```kotlin
// Get version
val version = apiClient.getVersion().getOrThrow()
println("Aria2 Version: ${version.version}")

// Save session
apiClient.saveSession()

// Purge completed downloads
apiClient.purgeDownloadResult()

// Shutdown
apiClient.shutdown()        // Graceful
apiClient.forceShutdown()   // Force
```

## 🧪 Stub Mode for Testing and Development

MotrixConnect includes a **stub mode** that allows development and testing without requiring a live Motrix/Aria2 server.

### Enabling Stub Mode

```kotlin
val apiClient = MotrixApiClient(
    serverUrl = "http://localhost:16800",
    secret = null,
    isStubMode = true  // Enable stub mode
)
```

### Stub Mode Features

**Realistic Test Data**:
- 7 pre-configured downloads (active, waiting, paused, complete, error, removed)
- Active downloads with HTTP and BitTorrent
- Realistic file sizes and download speeds
- Complete version and statistics data

**Complete API Coverage**:
- All JSON-RPC methods implemented
- Stateful download management
- Session and queue operations
- Error simulation for invalid GIDs

**Stateful Behavior**:
- In-memory download storage
- State transitions (active → paused → removed, etc.)
- Pause/resume affects download speeds
- Add/remove operations update state

**Error Simulation**:
- GID not found errors
- Authentication failures (when requireAuth = true)
- Invalid parameter handling

### Using Stub Mode in Tests

```kotlin
class MyViewModelTest {
    @Test
    fun `test download management with stub data`() = runTest {
        val apiClient = MotrixApiClient(
            serverUrl = MotrixTestData.TEST_SERVER_URL,
            secret = null,
            isStubMode = true
        )

        // Add download
        val gidResult = apiClient.addUri("https://example.com/file.zip")
        assertTrue(gidResult.isSuccess)

        val gid = gidResult.getOrThrow()

        // Check status
        val statusResult = apiClient.tellStatus(gid)
        assertTrue(statusResult.isSuccess)

        val download = statusResult.getOrThrow()
        assertEquals("waiting", download.status)
    }
}
```

### Stub Data Constants

Access predefined test data through `MotrixTestData`:

```kotlin
// Server configuration
MotrixTestData.TEST_SERVER_URL     // "http://localhost:16800"
MotrixTestData.TEST_RPC_SECRET     // "test-secret-token"

// Test downloads
MotrixTestData.testDownloadActiveHttp       // Active HTTP download
MotrixTestData.testDownloadActiveTorrent    // Active BitTorrent
MotrixTestData.testDownloadWaiting          // Waiting download
MotrixTestData.testDownloadPaused           // Paused download
MotrixTestData.testDownloadComplete         // Completed download

// Collections
MotrixTestData.testAllDownloads       // All test downloads
MotrixTestData.testActiveDownloads    // Only active
MotrixTestData.testWaitingDownloads   // Only waiting
MotrixTestData.testStoppedDownloads   // Paused/complete/error/removed

// Version & Statistics
MotrixTestData.testVersion           // Version info
MotrixTestData.testGlobalStatActive  // Statistics with active downloads
```

### Testing Download Operations

```kotlin
@Test
fun `test complete download workflow`() = runTest {
    val apiClient = MotrixApiClient(
        serverUrl = MotrixTestData.TEST_SERVER_URL,
        secret = null,
        isStubMode = true
    )

    // 1. Add download
    val gid = apiClient.addUri(
        "https://example.com/movie.mp4",
        MotrixTestData.testDownloadOptions
    ).getOrThrow()

    // 2. Check it's waiting
    var download = apiClient.tellStatus(gid).getOrThrow()
    assertEquals("waiting", download.status)

    // 3. Start (unpause makes it active)
    apiClient.unpause(gid)
    download = apiClient.tellStatus(gid).getOrThrow()
    assertEquals("active", download.status)
    assertTrue(download.downloadSpeed.toLong() > 0)

    // 4. Pause
    apiClient.pause(gid)
    download = apiClient.tellStatus(gid).getOrThrow()
    assertEquals("paused", download.status)
    assertEquals("0", download.downloadSpeed)

    // 5. Resume
    apiClient.unpause(gid)
    download = apiClient.tellStatus(gid).getOrThrow()
    assertEquals("active", download.status)

    // 6. Remove
    apiClient.remove(gid)
    download = apiClient.tellStatus(gid).getOrThrow()
    assertEquals("removed", download.status)

    // 7. Delete
    apiClient.removeDownloadResult(gid)
    val result = apiClient.tellStatus(gid)
    assertTrue(result.isFailure)
}
```

### Testing Batch Operations

```kotlin
@Test
fun `test pause and resume all`() = runTest {
    val apiClient = MotrixApiClient(
        serverUrl = MotrixTestData.TEST_SERVER_URL,
        secret = null,
        isStubMode = true
    )

    // Get initial active count
    val initialActive = apiClient.tellActive().getOrThrow().size

    // Pause all
    apiClient.pauseAll()
    var active = apiClient.tellActive().getOrThrow()
    assertEquals(0, active.size)

    // Unpause all
    apiClient.unpauseAll()
    active = apiClient.tellActive().getOrThrow()
    assertEquals(initialActive, active.size)
}
```

### Testing Error Scenarios

```kotlin
@Test
fun `test invalid GID handling`() = runTest {
    val apiClient = MotrixApiClient(
        serverUrl = MotrixTestData.TEST_SERVER_URL,
        secret = null,
        isStubMode = true
    )

    val invalidGid = "ffffffffffffffff"

    // All operations should fail
    assertTrue(apiClient.tellStatus(invalidGid).isFailure)
    assertTrue(apiClient.pause(invalidGid).isFailure)
    assertTrue(apiClient.remove(invalidGid).isFailure)
}

@Test
fun `test authentication requirement`() = runTest {
    val apiClient = MotrixApiClient(
        serverUrl = MotrixTestData.TEST_SERVER_URL,
        secret = "test-secret",
        isStubMode = true
    )

    // Operations will fail without proper authentication in stub
    // (when requireAuth = true in MotrixApiStubService)
}
```

### Network Delay Simulation

Stub mode includes realistic network delays (500ms):

```kotlin
val start = System.currentTimeMillis()
apiClient.getVersion()
val elapsed = System.currentTimeMillis() - start
// elapsed will be approximately 500ms
```

### Stub Mode Architecture

```
MotrixApiClient
    ├── isStubMode = false → MotrixApiLiveService (HTTP calls to server)
    └── isStubMode = true  → MotrixApiStubService (in-memory state)
                                 └── MotrixTestData (sample data provider)
```

### Best Practices

1. **Use in Tests**: Always use stub mode for unit and integration tests
2. **UI Development**: Enable stub mode during UI development to avoid server dependencies
3. **Demo Mode**: Use stub mode for app demonstrations
4. **State Management**: Call `MotrixApiStubService.resetState()` between tests
5. **Error Testing**: Stub service validates GIDs and simulates errors

### Complete Workflow Example

```kotlin
@Test
fun `test comprehensive download management`() = runTest {
    val apiClient = MotrixApiClient(
        serverUrl = MotrixTestData.TEST_SERVER_URL,
        secret = null,
        isStubMode = true
    )

    // 1. Check server version
    val version = apiClient.getVersion().getOrThrow()
    assertEquals("1.8.19", version.version)

    // 2. Check global stats
    val stat = apiClient.getGlobalStat().getOrThrow()
    assertTrue(stat.numActive.toInt() >= 0)

    // 3. Add new download
    val gid = apiClient.addUri(
        "https://example.com/ubuntu.iso",
        MotrixDownloadOptions(
            directory = "/downloads",
            connections = 16,
            maxDownloadSpeed = "10M"
        )
    ).getOrThrow()

    // 4. Manage download lifecycle
    apiClient.unpause(gid)  // Start
    apiClient.pause(gid)    // Pause
    apiClient.unpause(gid)  // Resume

    // 5. Check options
    val options = apiClient.getOption(gid).getOrThrow()
    assertNotNull(options)

    // 6. Change options
    apiClient.changeOption(gid, mapOf("max-download-limit" to "5M"))

    // 7. Clean up
    apiClient.remove(gid)
    apiClient.removeDownloadResult(gid)

    // 8. Verify removed
    val result = apiClient.tellStatus(gid)
    assertTrue(result.isFailure)
}
```

## 📊 Test Coverage

### Unit Tests
- **MotrixApiStubServiceTest**: 30+ test methods covering all stub service functionality

### Integration Tests
- **MotrixApiClientStubModeTest**: 30+ integration tests validating end-to-end stub mode

### Test Areas
- Server information (version, statistics)
- Download management (add, status, lists)
- Download control (pause, resume, remove)
- Batch operations (pauseAll, unpauseAll)
- Options management (get, change global/download options)
- Session management (save, purge, shutdown)
- Error scenarios (invalid GID, authentication)
- State management and transitions
- Pagination (waiting/stopped lists)

## 🔐 Authentication

MotrixConnect supports Aria2 RPC secret authentication:

```kotlin
// With authentication
val apiClient = MotrixApiClient(
    serverUrl = "http://localhost:16800",
    secret = "your-rpc-secret"
)

// Without authentication (for local/trusted servers)
val apiClient = MotrixApiClient(
    serverUrl = "http://localhost:16800",
    secret = null
)

// Secret is automatically prepended to all RPC calls
```

## 🏗️ Technical Architecture

### Dependencies
- **OkHttp**: HTTP client with logging
- **Gson**: JSON serialization
- **Kotlin Coroutines**: Async operations
- **Robolectric**: Android unit testing

### JSON-RPC Protocol
MotrixConnect implements the Aria2 JSON-RPC protocol:
- **Version**: JSON-RPC 2.0
- **Transport**: HTTP POST to `/jsonrpc`
- **Authentication**: Token-based secret (optional)
- **Response Format**: Standardized RPC response with result or error

### Request/Response Example
```kotlin
// Request
{
  "jsonrpc": "2.0",
  "id": "unique-id",
  "method": "aria2.tellStatus",
  "params": ["token:secret", "2089b05ecca3d829"]
}

// Response (Success)
{
  "jsonrpc": "2.0",
  "id": "unique-id",
  "result": {
    "gid": "2089b05ecca3d829",
    "status": "active",
    "downloadSpeed": "524288",
    ...
  }
}

// Response (Error)
{
  "jsonrpc": "2.0",
  "id": "unique-id",
  "error": {
    "code": 2,
    "message": "Active Download not found"
  }
}
```

## 📄 License

MotrixConnect is part of the ShareConnect project. See the main project LICENSE file for details.

---

**Last Updated:** 2025-11-11
**Version:** 1.0.0
**Aria2 Compatibility:** 1.8+
