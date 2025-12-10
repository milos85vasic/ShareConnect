# NextcloudConnect - Nextcloud Android Client

NextcloudConnect is an Android application that provides native client functionality for Nextcloud servers. Built with Kotlin and Jetpack Compose, it offers WebDAV file operations and OCS API integration.

## 🎯 Overview

NextcloudConnect enables Android apps to interact with Nextcloud servers for:
- **File Management**: WebDAV operations (list, upload, download, delete, move, copy)
- **Share Management**: Create and manage public share links
- **User Information**: Access user details and quota information
- **Server Status**: Monitor server health and version

## 📱 Features

### File Operations
- List files and folders with WebDAV
- Upload/download files
- Create folders
- Delete files and folders
- Move and rename files
- Copy files

### Sharing
- Create public share links
- List shares for files/folders
- Delete shares

### User & Server
- Get user information and quota
- Check server status and version
- Basic authentication support

## 🔧 API Reference

### NextcloudApiClient

#### Initialization
```kotlin
val apiClient = NextcloudApiClient(
    serverUrl = "https://cloud.example.com",
    username = "user",
    password = "password"
)
```

#### File Operations
```kotlin
// List files
val files = apiClient.listFiles("Documents")

// Upload file
apiClient.uploadFile("Documents/file.txt", byteArray, "text/plain")

// Download file
val content = apiClient.downloadFile("Documents/file.txt")

// Create folder
apiClient.createFolder("NewFolder")

// Delete
apiClient.delete("Documents/oldfile.txt")

// Move/Rename
apiClient.move("source.txt", "destination.txt")

// Copy
apiClient.copy("source.txt", "copy.txt")
```

#### Sharing
```kotlin
// Create share link
val share = apiClient.createShareLink("/Documents/file.pdf")
println("Share URL: ${share.url}")

// Get shares
val shares = apiClient.getShares("/Documents/file.pdf")

// Delete share
apiClient.deleteShare(shareId)
```

#### User & Server
```kotlin
// Get server status
val status = apiClient.getServerStatus()

// Get user info
val user = apiClient.getUserInfo()
println("Quota: ${user.quota?.used}/${user.quota?.total}")
```

## 🧪 Stub Mode for Testing and Development

NextcloudConnect includes a **stub mode** that allows development and testing without requiring a live Nextcloud server.

### Enabling Stub Mode

```kotlin
val apiClient = NextcloudApiClient(
    serverUrl = "https://cloud.example.com",
    username = "testuser",
    password = "testpassword",
    isStubMode = true  // Enable stub mode
)
```

### Stub Mode Features

**Realistic Test Data**:
- Complete file system with folders (Documents, Photos, Music)
- 15 test files (PDFs, images, spreadsheets, music)
- 2 pre-configured share links
- User information with quota

**Complete API Coverage**:
- All WebDAV operations (list, upload, download, delete, move, copy)
- OCS API v2 (user info, shares)
- Server status endpoint

**Stateful Behavior**:
- In-memory file system simulation
- Create/delete operations affect state
- Share management persistence

**Error Simulation**:
- 401 Unauthorized for invalid auth
- 404 Not Found for missing files
- 409 Conflict for missing parent directories
- 412 Precondition Failed for existing destinations

### Using Stub Mode in Tests

```kotlin
class MyViewModelTest {
    @Test
    fun `test file listing with stub data`() = runTest {
        val apiClient = NextcloudApiClient(
            serverUrl = NextcloudTestData.TEST_SERVER_URL,
            username = NextcloudTestData.TEST_USERNAME,
            password = NextcloudTestData.TEST_PASSWORD,
            isStubMode = true
        )

        val result = apiClient.listFiles("Documents")
        assertTrue(result.isSuccess)

        val xmlResponse = result.getOrThrow()
        assertTrue(xmlResponse.contains("Annual_Report_2024.pdf"))
    }
}
```

### Stub Data Constants

Access predefined test data through `NextcloudTestData`:

```kotlin
// Server configuration
NextcloudTestData.TEST_SERVER_URL     // "https://cloud.example.com"
NextcloudTestData.TEST_USERNAME       // "testuser"
NextcloudTestData.TEST_PASSWORD       // "testpassword123"

// Test files
NextcloudTestData.testReportPdf       // Annual Report PDF
NextcloudTestData.testSpreadsheet     // Budget spreadsheet
NextcloudTestData.testPhoto1          // Sample photo

// Test folders
NextcloudTestData.testDocumentsFolder // Documents folder
NextcloudTestData.testPhotosFolder    // Photos folder

// Collections
NextcloudTestData.testAllFiles        // All test files
NextcloudTestData.testRootFiles       // Root directory files
```

### Testing File Operations

```kotlin
@Test
fun `test upload and verify file`() = runTest {
    val apiClient = NextcloudApiClient(
        serverUrl = NextcloudTestData.TEST_SERVER_URL,
        username = NextcloudTestData.TEST_USERNAME,
        password = NextcloudTestData.TEST_PASSWORD,
        isStubMode = true
    )

    // Upload a file
    val uploadResult = apiClient.uploadFile(
        "Documents/NewFile.txt",
        "Test content".toByteArray(),
        "text/plain"
    )
    assertTrue(uploadResult.isSuccess)

    // Verify file exists
    val listResult = apiClient.listFiles("Documents")
    val xmlResponse = listResult.getOrThrow()
    assertTrue(xmlResponse.contains("NewFile.txt"))
}
```

### Testing Share Operations

```kotlin
@Test
fun `test share creation and retrieval`() = runTest {
    val apiClient = NextcloudApiClient(
        serverUrl = NextcloudTestData.TEST_SERVER_URL,
        username = NextcloudTestData.TEST_USERNAME,
        password = NextcloudTestData.TEST_PASSWORD,
        isStubMode = true
    )

    // Create share
    val createResult = apiClient.createShareLink("/Documents/Report.pdf")
    val share = createResult.getOrThrow()
    assertNotNull(share.url)

    // Get shares
    val getResult = apiClient.getShares("/Documents/Report.pdf")
    val shares = getResult.getOrThrow()
    assertTrue(shares.any { it.id == share.id })
}
```

### Network Delay Simulation

Stub mode includes realistic network delays (500ms):

```kotlin
val start = System.currentTimeMillis()
apiClient.listFiles("Documents")
val elapsed = System.currentTimeMillis() - start
// elapsed will be approximately 500ms
```

### Stub Mode Architecture

```
NextcloudApiClient
    ├── isStubMode = false → NextcloudApiService (Retrofit, live server)
    └── isStubMode = true  → NextcloudApiStubService (in-memory file system)
                                 └── NextcloudTestData (sample data provider)
```

### Best Practices

1. **Use in Tests**: Always use stub mode for unit and integration tests
2. **UI Development**: Enable stub mode during UI development to avoid server dependencies
3. **Demo Mode**: Use stub mode for app demonstrations
4. **State Management**: Call `NextcloudApiStubService.resetState()` between tests
5. **Error Testing**: Stub service validates auth and paths for error scenarios

### Complete Workflow Example

```kotlin
@Test
fun `test complete file management workflow`() = runTest {
    val apiClient = NextcloudApiClient(
        serverUrl = NextcloudTestData.TEST_SERVER_URL,
        username = NextcloudTestData.TEST_USERNAME,
        password = NextcloudTestData.TEST_PASSWORD,
        isStubMode = true
    )

    // 1. Create folder
    apiClient.createFolder("TestFolder")

    // 2. Upload file
    apiClient.uploadFile(
        "TestFolder/test.txt",
        "Test content".toByteArray(),
        "text/plain"
    )

    // 3. Create share
    val share = apiClient.createShareLink("/TestFolder/test.txt").getOrThrow()
    assertNotNull(share.url)

    // 4. Copy file
    apiClient.copy("TestFolder/test.txt", "TestFolder/test_copy.txt")

    // 5. Verify all operations
    val files = apiClient.listFiles("TestFolder").getOrThrow()
    assertTrue(files.contains("test.txt"))
    assertTrue(files.contains("test_copy.txt"))
}
```

## 📊 Test Coverage

### Unit Tests
- **NextcloudApiStubServiceTest**: 30+ test methods covering all stub service functionality
- **NextcloudApiClientStubModeTest**: 15+ integration tests validating end-to-end stub mode

### Test Areas
- Server status and user info
- WebDAV file listing
- File upload/download
- Folder creation
- File/folder deletion
- Move and copy operations
- Share creation and management
- Error scenarios (401, 404, 409, 412)
- State management and reset

## 🔐 Authentication

NextcloudConnect uses HTTP Basic Authentication:

```kotlin
// Client automatically generates auth header
val apiClient = NextcloudApiClient(
    serverUrl = "https://cloud.example.com",
    username = "user",
    password = "password"
)

// Auth header is included in all requests
```

## 🏗️ Technical Architecture

### Dependencies
- **Retrofit**: HTTP client for REST API calls
- **OkHttp**: Underlying HTTP client with logging
- **Gson**: JSON serialization
- **Kotlin Coroutines**: Async operations
- **Robolectric**: Android unit testing

### WebDAV Support
- PROPFIND for listing
- GET for download
- PUT for upload
- MKCOL for folder creation
- DELETE for removal
- MOVE for rename/move
- COPY for duplication

### OCS API v2
- User information endpoint
- Share management endpoints
- Standardized response wrapper

## 📄 License

NextcloudConnect is part of the ShareConnect project. See the main project LICENSE file for details.

---

**Last Updated:** 2025-11-11
**Version:** 1.0.0
**Nextcloud Compatibility:** 28.x+
