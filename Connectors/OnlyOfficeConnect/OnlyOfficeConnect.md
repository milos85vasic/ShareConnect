# OnlyOfficeConnect

**Version:** 1.0.0
**Package:** com.shareconnect.onlyofficeconnect
**Type:** Document Editor Connector
**Target SDK:** 36
**Min SDK:** 28

## Overview

OnlyOfficeConnect is a ShareConnect connector application that integrates with ONLYOFFICE Document Server, enabling seamless document editing, collaboration, and file management within the ShareConnect ecosystem.

### Key Features

- **Document Editing**: Edit Word documents, Excel spreadsheets, and PowerPoint presentations
- **Real-time Collaboration**: Multiple users can edit documents simultaneously
- **WebView Integration**: Native Android WebView with ONLYOFFICE Document Server JavaScript API
- **File Management**: Upload, download, move, copy, and delete files and folders
- **JWT Authentication**: Secure token-based authentication
- **Full API Support**: Complete ONLYOFFICE REST API implementation
- **Sync Integration**: All 8 ShareConnect sync managers for cross-app data sharing
- **Security Access**: PIN/biometric authentication support
- **Multi-format Support**: DOCX, XLSX, PPTX, ODT, ODS, ODP, and more

## Architecture

### Application Structure

```
OnlyOfficeConnect/
├── OnlyOfficeConnector/
│   ├── src/
│   │   ├── main/
│   │   │   ├── kotlin/com/shareconnect/onlyofficeconnect/
│   │   │   │   ├── OnlyOfficeConnectApplication.kt    # Application class with sync managers
│   │   │   │   ├── data/
│   │   │   │   │   ├── api/
│   │   │   │   │   │   ├── OnlyOfficeApiService.kt     # Retrofit service interface
│   │   │   │   │   │   └── OnlyOfficeApiClient.kt      # API client implementation
│   │   │   │   │   └── models/
│   │   │   │   │       └── OnlyOfficeModels.kt          # Data models
│   │   │   │   ├── ui/
│   │   │   │   │   ├── MainActivity.kt                  # Main activity
│   │   │   │   │   ├── OnboardingActivity.kt           # Onboarding flow
│   │   │   │   │   └── OnlyOfficeEditorManager.kt      # WebView editor manager
│   │   │   │   └── service/
│   │   │   │       └── OnlyOfficeSyncService.kt        # Background sync
│   │   │   ├── res/                                    # Resources
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                                       # Unit tests
│   │   └── androidTest/                                # Integration & automation tests
│   └── build.gradle
└── Documentation/
    ├── OnlyOfficeConnect.md                            # This file
    └── OnlyOfficeConnect_User_Manual.md                # User guide
```

### Sync Managers

OnlyOfficeConnect integrates with all 8 ShareConnect sync managers:

1. **ThemeSync** (Port 8890) - Theme preferences and custom themes
2. **ProfileSync** (Port 8900) - Document server profiles
3. **HistorySync** (Port 8910) - Document sharing history
4. **RSSSync** (Port 8920) - RSS feed subscriptions
5. **BookmarkSync** (Port 8930) - Document bookmarks
6. **PreferencesSync** (Port 8940) - App preferences
7. **LanguageSync** (Port 8950) - Language settings
8. **TorrentSharingSync** (Port 8960) - Torrent sharing data

Each sync manager:
- Uses unique gRPC port for communication
- Extends AsinkaClient for real-time synchronization
- Manages encrypted Room database with SQLCipher
- Broadcasts changes to all connected ShareConnect apps

### Data Models

#### Authentication Models

- **OnlyOfficeAuthRequest**: Username and password authentication
- **OnlyOfficeAuthResponse**: Authentication token and user details
- **OnlyOfficeAuthUser**: User profile information

#### File Models

- **OnlyOfficeFile**: File metadata including ID, title, size, version, permissions
- **OnlyOfficeFilesResponse**: List of files and folders with pagination
- **OnlyOfficeFilesData**: Container for files, folders, and metadata

#### Folder Models

- **OnlyOfficeFolder**: Folder metadata with file/folder counts
- **OnlyOfficeFolderResponse**: Folder creation/update response

#### Document Editor Models

- **OnlyOfficeDocument**: Document configuration for editor
- **OnlyOfficeEditorConfig**: Editor settings and customization
- **OnlyOfficePermissions**: Document access permissions
- **OnlyOfficeEditorConfiguration**: Complete editor configuration

#### Callback Models

- **OnlyOfficeCallback**: Document save event notifications
- **OnlyOfficeHistory**: Document version history
- **OnlyOfficeAction**: User collaboration actions

## API Client

### OnlyOfficeApiClient

Complete ONLYOFFICE REST API implementation with Result-based error handling.

#### Authentication

```kotlin
// Authenticate user
suspend fun authenticate(username: String, password: String): Result<OnlyOfficeAuthResponse>

// Get server capabilities
suspend fun getCapabilities(): Result<OnlyOfficeCapabilities>
```

#### File Operations

```kotlin
// List files
suspend fun getFiles(
    token: String,
    folderId: String? = null,
    filterType: Int? = null,
    searchText: String? = null,
    startIndex: Int = 0,
    count: Int = 100
): Result<OnlyOfficeFilesData>

// Get file information
suspend fun getFileInfo(token: String, fileId: String): Result<OnlyOfficeFile>

// Upload file
suspend fun uploadFile(
    token: String,
    folderId: String,
    file: File,
    createNewIfExist: Boolean = false
): Result<OnlyOfficeFile>

// Delete file
suspend fun deleteFile(
    token: String,
    fileId: String,
    deleteAfter: Boolean = true,
    immediately: Boolean = false
): Result<List<OnlyOfficeOperationProgress>>

// Download file
suspend fun downloadFile(token: String, fileId: String): Result<ByteArray>
```

#### Folder Operations

```kotlin
// Get folder contents
suspend fun getFolderContents(
    token: String,
    folderId: String,
    startIndex: Int = 0,
    count: Int = 100
): Result<OnlyOfficeFilesData>

// Create folder
suspend fun createFolder(
    token: String,
    title: String,
    parentId: String? = null
): Result<OnlyOfficeFolder>

// Delete folder
suspend fun deleteFolder(
    token: String,
    folderId: String,
    deleteAfter: Boolean = true,
    immediately: Boolean = false
): Result<List<OnlyOfficeOperationProgress>>
```

#### Move and Copy Operations

```kotlin
// Move file
suspend fun moveFile(
    token: String,
    fileId: String,
    destFolderId: String,
    conflictResolveType: Int = 0,
    deleteAfter: Boolean = true
): Result<List<OnlyOfficeOperationProgress>>

// Copy file
suspend fun copyFile(
    token: String,
    fileId: String,
    destFolderId: String,
    conflictResolveType: Int = 0
): Result<List<OnlyOfficeOperationProgress>>

// Move/copy folder operations also available
```

#### Document Editor Operations

```kotlin
// Get editor configuration
suspend fun getEditorConfig(token: String, fileId: String): Result<OnlyOfficeEditorConfiguration>

// Handle document save callback
suspend fun handleCallback(token: String, callback: OnlyOfficeCallback): Result<OnlyOfficeCallbackResponse>
```

#### Utility Methods

```kotlin
// Generate JWT token for document editor
fun generateJwtToken(payload: Map<String, Any>): String?

// Get document type from file extension
fun getDocumentType(fileExtension: String): String

// Check if file type is editable
fun isEditableFileType(fileExtension: String): Boolean
```

### Usage Example

```kotlin
// Initialize API client
val apiClient = OnlyOfficeApiClient(
    serverUrl = "https://office.example.com",
    jwtSecret = "your-jwt-secret"
)

// Authenticate
val authResult = apiClient.authenticate("username", "password")
if (authResult.isSuccess) {
    val token = authResult.getOrNull()?.token ?: return

    // Get files
    val filesResult = apiClient.getFiles(token, folderId = "my-folder")
    filesResult.onSuccess { filesData ->
        filesData.files.forEach { file ->
            println("File: ${file.title}")
        }
    }

    // Upload file
    val file = File("/path/to/document.docx")
    val uploadResult = apiClient.uploadFile(token, "folder-id", file)
    uploadResult.onSuccess { uploadedFile ->
        println("Uploaded: ${uploadedFile.title}")
    }
}
```

## WebView Editor Integration

### OnlyOfficeEditorManager

Manages WebView-based ONLYOFFICE Document Editor integration.

#### Features

- **JavaScript Bridge**: Bidirectional communication with editor
- **Event Handling**: Document ready, save, error, collaboration events
- **Customization**: Editor appearance and behavior configuration
- **Security**: JWT token-based authentication
- **Callbacks**: Document state changes, save requests, collaboration

#### Editor Callbacks Interface

```kotlin
interface EditorCallbacks {
    fun onDocumentReady()
    fun onDocumentStateChange(isSaved: Boolean)
    fun onError(error: String)
    fun onInfo(info: String)
    fun onWarning(warning: String)
    fun onRequestSaveAs(saveAsEvent: SaveAsEvent)
    fun onRequestInsertImage(insertImageEvent: InsertImageEvent)
    fun onRequestMailMergeRecipients()
    fun onRequestCompareFile()
    fun onRequestRestore(version: Int)
    fun onMetaChange(metaData: Map<String, Any>)
    fun onRequestClose()
}
```

#### Usage Example

```kotlin
class EditorActivity : ComponentActivity() {
    private lateinit var editorManager: OnlyOfficeEditorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val webView = WebView(this)
        setContentView(webView)

        editorManager = OnlyOfficeEditorManager(
            context = this,
            webView = webView,
            callbacks = object : OnlyOfficeEditorManager.EditorCallbacks {
                override fun onDocumentReady() {
                    Log.d(TAG, "Document loaded")
                }

                override fun onDocumentStateChange(isSaved: Boolean) {
                    Log.d(TAG, "Document saved: $isSaved")
                }

                override fun onError(error: String) {
                    Log.e(TAG, "Editor error: $error")
                }

                // Implement other callbacks...
            }
        )

        // Load editor
        val config = OnlyOfficeEditorConfiguration(
            document = OnlyOfficeDocument(
                fileType = "docx",
                key = "unique-document-key",
                title = "My Document.docx",
                url = "https://office.example.com/files/download/123"
            ),
            documentType = "word",
            editorConfig = OnlyOfficeEditorConfig(
                mode = "edit",
                lang = "en",
                user = OnlyOfficeEditorUser(
                    id = "user1",
                    name = "John Doe"
                )
            )
        )

        editorManager.loadEditor(
            documentServerUrl = "https://office.example.com",
            editorConfig = config
        )
    }

    override fun onDestroy() {
        super.onDestroy()
        editorManager.destroy()
    }
}
```

## Testing

### Test Coverage

OnlyOfficeConnect includes comprehensive test coverage:

- **Unit Tests**: 17 tests in `OnlyOfficeApiClientTest.kt`
- **Integration Tests**: 24 tests in `OnlyOfficeApiClientIntegrationTest.kt`
- **Automation Tests**: 11 tests in `OnlyOfficeConnectAutomationTest.kt`
- **Total**: 52+ tests

### Running Tests

```bash
# Run all unit tests
./gradlew :OnlyOfficeConnector:test

# Run specific test class
./gradlew :OnlyOfficeConnector:test --tests "OnlyOfficeApiClientTest"

# Run integration tests (requires MockWebServer)
./gradlew :OnlyOfficeConnector:test --tests "OnlyOfficeApiClientIntegrationTest"

# Run automation tests (requires emulator/device)
./gradlew :OnlyOfficeConnector:connectedDebugAndroidTest
```

### Test Categories

#### Unit Tests
- Authentication operations
- File CRUD operations
- Folder management
- Move/copy operations
- Document type detection
- JWT token generation
- Error handling

#### Integration Tests
- HTTP communication with mock server
- Response parsing
- Error scenarios
- Network timeouts
- Empty responses
- Various status codes

#### Automation Tests
- App launch stability
- UI rendering
- User interactions
- Accessibility
- Theme consistency
- Screen orientation

## Security

### Authentication

- **JWT Tokens**: Secure token generation using HMAC-SHA256
- **Bearer Token**: Standard HTTP Authorization header
- **Session Management**: Token expiration and renewal

### Data Protection

- **SQLCipher Encryption**: All local databases encrypted
- **HTTPS**: Secure communication with ONLYOFFICE server
- **Secure Storage**: Credentials stored encrypted
- **App Signature Verification**: During Asinka discovery

### Access Control

- **SecurityAccessManager**: PIN/biometric authentication
- **Permission-based**: Document edit/view/share permissions
- **User Roles**: Admin, editor, viewer roles supported

## Configuration

### build.gradle Dependencies

```gradle
dependencies {
    // ShareConnect modules
    implementation project(':DesignSystem')
    implementation project(':Toolkit:SecurityAccess')
    implementation project(':ThemeSync')
    implementation project(':ProfileSync')
    implementation project(':HistorySync')
    implementation project(':RSSSync')
    implementation project(':BookmarkSync')
    implementation project(':PreferencesSync')
    implementation project(':LanguageSync')
    implementation project(':TorrentSharingSync')
    implementation project(':Asinka:asinka')

    // WebView for document editing
    implementation "androidx.webkit:webkit:1.12.1"

    // JWT for authentication
    implementation "io.jsonwebtoken:jjwt-api:0.11.5"
    runtimeOnly "io.jsonwebtoken:jjwt-impl:0.11.5"
    runtimeOnly "io.jsonwebtoken:jjwt-gson:0.11.5"

    // Networking
    implementation "com.squareup.retrofit2:retrofit:2.11.0"
    implementation "com.squareup.retrofit2:converter-gson:2.11.0"
    implementation "com.squareup.okhttp3:okhttp:4.12.0"

    // Testing
    testImplementation "junit:junit:4.13.2"
    testImplementation "io.mockk:mockk:1.13.13"
    testImplementation "com.squareup.okhttp3:mockwebserver:4.12.0"
    androidTestImplementation "androidx.test.espresso:espresso-web:3.6.1"
}
```

### AndroidManifest Permissions

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

### Application Configuration

```kotlin
class OnlyOfficeConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize all 8 sync managers
        initializeLanguageSync()
        initializeTorrentSharingSync()
        initializeThemeSync()
        initializeProfileSync()
        initializeHistorySync()
        initializeRSSSync()
        initializeBookmarkSync()
        initializePreferencesSync()
    }
}
```

## Supported File Formats

### Editable Formats

**Word Processing:**
- DOCX, DOC, ODT, RTF, TXT, HTML, HTM, MHT

**Spreadsheets:**
- XLSX, XLS, ODS, CSV

**Presentations:**
- PPTX, PPT, ODP

### View-Only Formats

- PDF, DJVU, FB2, EPUB, XPS

## Known Issues and Solutions

### WebView Issues

**Issue**: WebView not loading editor
**Solution**: Ensure `usesCleartextTraffic="true"` in manifest for HTTP connections

**Issue**: JavaScript callbacks not working
**Solution**: Verify `javaScriptEnabled = true` in WebView settings

### Network Issues

**Issue**: Connection timeout
**Solution**: Increase timeout in OkHttpClient (default: 60 seconds)

**Issue**: SSL certificate errors
**Solution**: Add custom TrustManager or use self-signed certificate handling

## Development Guidelines

### Adding New API Endpoints

1. Add method to `OnlyOfficeApiService.kt`
2. Implement in `OnlyOfficeApiClient.kt` with Result wrapper
3. Add corresponding data models to `OnlyOfficeModels.kt`
4. Write unit tests in `OnlyOfficeApiClientTest.kt`
5. Add integration tests in `OnlyOfficeApiClientIntegrationTest.kt`

### Modifying Editor Integration

1. Update `OnlyOfficeEditorManager.kt`
2. Add JavaScript callbacks as needed
3. Update HTML template in `generateEditorHtml()`
4. Test in EditorActivity

## Troubleshooting

### Common Issues

**Q**: App crashes on launch
**A**: Check sync manager port conflicts, ensure unique ports for each manager

**Q**: Authentication fails
**A**: Verify server URL format, check network connectivity, validate credentials

**Q**: Documents don't load in editor
**A**: Check document URL accessibility, verify JWT token if required

**Q**: File upload fails
**A**: Check file size limits, verify folder permissions, ensure valid auth token

## Performance Optimization

- **Lazy Loading**: Files and folders loaded on demand
- **Pagination**: Large file lists paginated (default 100 items)
- **Caching**: OkHttp caching for network requests
- **Background Sync**: Sync managers run on background threads
- **WebView Optimization**: Hardware acceleration enabled

## Future Enhancements

- Offline document editing
- Advanced collaboration features
- Document templates
- Custom editor plugins
- Enhanced search capabilities
- Document versioning UI
- Comments and annotations

## Support and Resources

- **ONLYOFFICE API Documentation**: https://api.onlyoffice.com/
- **ShareConnect Wiki**: https://deepwiki.com/vasic-digital/ShareConnect
- **Issue Tracker**: GitHub repository issues
- **Community Forum**: ShareConnect discussions

## Version History

### 1.0.0 (Current)
- Initial release
- Full API implementation
- WebView editor integration
- All 8 sync managers
- Comprehensive test coverage
- Security Access integration

## License

Part of ShareConnect ecosystem
Copyright (c) 2024 Vasic Digital
