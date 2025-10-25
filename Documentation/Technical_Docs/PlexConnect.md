# PlexConnect

## Overview and Purpose

PlexConnect is a specialized Android application that provides comprehensive integration with Plex Media Server. It offers a modern, feature-rich mobile interface for browsing media libraries, managing playback, and accessing Plex content. The app connects to Plex Media Server instances running on local networks or remote servers through the Plex.tv cloud service.

PlexConnect extends the ShareConnect ecosystem by enabling sharing from personal media libraries to other connected services (torrents, download managers, cloud storage), closing the loop between content acquisition and organization.

## Architecture and Components

PlexConnect is built using Kotlin with Jetpack Compose for UI, following the MVVM architecture pattern and Repository pattern for data management.

### Core Components
- **PlexConnectApplication**: Main application class managing dependency injection and sync managers
- **DependencyContainer**: Handles dependency injection and service initialization
- **PlexApiClient**: HTTP client for Plex.tv and Plex Media Server REST API communication
- **PlexAuthService**: Manages PIN-based authentication with Plex.tv

### Data Layer
- **PlexServerRepository**: Manages Plex server connections and profiles
- **PlexLibraryRepository**: Handles library data operations and caching
- **PlexMediaRepository**: Manages media item data and metadata
- **PlexDatabase**: Room database with SQLCipher encryption for local data storage

### UI Layer (Jetpack Compose)
- **MainActivity**: Primary application entry point with navigation
- **OnboardingActivity**: First-time setup and configuration flow
- **AuthenticationScreen**: PIN-based authentication UI
- **LibraryListScreen**: Media library browsing interface
- **MediaDetailScreen**: Detailed media item view with playback controls
- **SettingsScreen**: Application preferences and server management

### Sync Integration
PlexConnect integrates with ShareConnect's sync ecosystem:
- **ThemeSyncManager**: Theme synchronization (port 8890)
- **ProfileSyncManager**: Server profile management (port 8900)
- **HistorySyncManager**: Media history synchronization (port 8910)
- **RSSSyncManager**: RSS feed integration (port 8920)
- **BookmarkSyncManager**: Media bookmark management (port 8930)
- **PreferencesSyncManager**: User preference synchronization (port 8940)
- **LanguageSyncManager**: Language settings sync (port 8950)

## API Reference

### PlexApiClient

#### Authentication Methods
```kotlin
/**
 * Request a new PIN for authentication
 * @param clientIdentifier Unique client identifier
 * @return Result containing PIN response with code and ID
 */
suspend fun requestPin(clientIdentifier: String): Result<PlexPinResponse>

/**
 * Check PIN authentication status
 * @param pinId PIN ID from requestPin
 * @return Result containing auth token if successful
 */
suspend fun checkPin(pinId: Long): Result<PlexPinResponse>
```

#### Server Operations
```kotlin
/**
 * Get server information and capabilities
 * @param serverUrl Plex server base URL
 * @return Result containing server info
 */
suspend fun getServerInfo(serverUrl: String): Result<PlexServerInfo>

/**
 * Get all libraries on a server
 * @param serverUrl Plex server base URL
 * @param token Authentication token
 * @return Result containing list of libraries
 */
suspend fun getLibraries(serverUrl: String, token: String): Result<List<PlexLibrary>>

/**
 * Get media items from a library
 * @param serverUrl Plex server base URL
 * @param sectionKey Library section identifier
 * @param token Authentication token
 * @param limit Maximum items to return (default 50)
 * @param offset Pagination offset (default 0)
 * @return Result containing list of media items
 */
suspend fun getLibraryItems(
    serverUrl: String,
    sectionKey: String,
    token: String,
    limit: Int = 50,
    offset: Int = 0
): Result<List<PlexMediaItem>>

/**
 * Get detailed media item information
 * @param serverUrl Plex server base URL
 * @param ratingKey Media item rating key
 * @param token Authentication token
 * @return Result containing media item or null
 */
suspend fun getMediaItem(serverUrl: String, ratingKey: String, token: String): Result<PlexMediaItem?>

/**
 * Get children of a media item (e.g., episodes of a season)
 * @param serverUrl Plex server base URL
 * @param ratingKey Parent media item rating key
 * @param token Authentication token
 * @return Result containing list of child items
 */
suspend fun getMediaChildren(serverUrl: String, ratingKey: String, token: String): Result<List<PlexMediaItem>>

/**
 * Mark media as watched
 * @param serverUrl Plex server base URL
 * @param ratingKey Media item rating key
 * @param token Authentication token
 * @return Result indicating success or failure
 */
suspend fun markWatched(serverUrl: String, ratingKey: String, token: String): Result<Unit>

/**
 * Mark media as unwatched
 * @param serverUrl Plex server base URL
 * @param ratingKey Media item rating key
 * @param token Authentication token
 * @return Result indicating success or failure
 */
suspend fun markUnwatched(serverUrl: String, ratingKey: String, token: String): Result<Unit>

/**
 * Update playback timeline position
 * @param serverUrl Plex server base URL
 * @param ratingKey Media item rating key
 * @param token Authentication token
 * @param position Current playback position in milliseconds
 * @return Result indicating success or failure
 */
suspend fun updateTimeline(
    serverUrl: String,
    ratingKey: String,
    token: String,
    position: Long
): Result<Unit>
```

### PlexServerRepository

#### Server Management
```kotlin
/**
 * Get count of configured servers
 * @return Number of servers
 */
suspend fun getServerCount(): Int

/**
 * Add a new Plex server
 * @param server Server configuration
 * @return Server ID
 */
suspend fun addServer(server: PlexServer): Long

/**
 * Update existing server configuration
 * @param server Updated server configuration
 */
suspend fun updateServer(server: PlexServer)

/**
 * Delete a server
 * @param server Server to delete
 */
suspend fun deleteServer(server: PlexServer)

/**
 * Get server by ID
 * @param serverId Server identifier
 * @return Server or null if not found
 */
suspend fun getServerById(serverId: Long): PlexServer?

/**
 * Get all configured servers
 * @return List of all servers
 */
suspend fun getAllServers(): List<PlexServer>

/**
 * Test server connection and retrieve info
 * @param server Server to test
 * @return Result containing server info if successful
 */
suspend fun testServerConnection(server: PlexServer): Result<PlexServerInfo>
```

### PlexLibraryRepository

#### Library Management
```kotlin
/**
 * Get libraries for a server
 * @param serverId Server identifier
 * @return Flow of libraries
 */
fun getLibrariesForServer(serverId: Long): Flow<List<PlexLibrary>>

/**
 * Refresh libraries from server
 * @param server Server to refresh from
 * @param token Authentication token
 * @return Result indicating success or failure
 */
suspend fun refreshLibraries(server: PlexServer, token: String): Result<Unit>

/**
 * Get library by ID
 * @param libraryId Library identifier
 * @return Library or null if not found
 */
suspend fun getLibraryById(libraryId: Long): PlexLibrary?
```

### PlexMediaRepository

#### Media Management
```kotlin
/**
 * Get media items for a library
 * @param libraryId Library identifier
 * @return Flow of media items
 */
fun getMediaItemsForLibrary(libraryId: Long): Flow<List<PlexMediaItem>>

/**
 * Refresh media items from server
 * @param library Library to refresh
 * @param server Server to connect to
 * @param token Authentication token
 * @param limit Maximum items to fetch
 * @param offset Pagination offset
 * @return Result indicating success or failure
 */
suspend fun refreshMediaItems(
    library: PlexLibrary,
    server: PlexServer,
    token: String,
    limit: Int = 50,
    offset: Int = 0
): Result<Unit>

/**
 * Get recently added media items
 * @param serverId Server identifier
 * @param limit Maximum items to return
 * @return List of recently added items
 */
suspend fun getRecentlyAddedItems(serverId: Long, limit: Int = 20): List<PlexMediaItem>
```

## Key Classes and Their Responsibilities

### PlexConnectApplication
- **Responsibilities**:
  - Application lifecycle management
  - Dependency injection container initialization
  - Sync manager initialization and coordination
  - Global coroutine scope management
  - Application-wide error handling

### PlexApiClient
- **Responsibilities**:
  - HTTP client management for Plex API communication
  - PIN-based authentication flow with Plex.tv
  - API request/response serialization
  - Error handling and retry logic
  - Connection pooling and timeout management
  - Token management for authenticated requests

### PlexServerRepository
- **Responsibilities**:
  - Plex server profile CRUD operations
  - Connection testing and validation
  - Server preference management
  - Local database synchronization
  - Server discovery and configuration

### PlexLibraryRepository
- **Responsibilities**:
  - Library metadata management
  - Library content refresh and caching
  - Library state tracking
  - Pagination handling for large libraries

### PlexMediaRepository
- **Responsibilities**:
  - Media item CRUD operations
  - Playback state tracking
  - Media metadata caching
  - Recently added/on deck queries
  - Watch status management

### PlexDatabase
- **Responsibilities**:
  - SQLCipher-encrypted local storage
  - Room database schema management
  - Data Access Object (DAO) provision
  - Database migrations
  - Type converters for complex data

## Data Models

### Authentication Models

#### PlexPinResponse
```kotlin
data class PlexPinResponse(
    val id: Long,                    // PIN identifier
    val code: String,                // 4-character PIN code
    val product: String,             // Product name
    val trusted: Boolean,            // Trust status
    val clientIdentifier: String,    // Client ID
    val location: PlexLocation?,     // Authentication location
    val expiresIn: Long,             // Expiration time in seconds
    val createdAt: String,           // Creation timestamp
    val expiresAt: String,           // Expiration timestamp
    val authToken: String?,          // Authentication token (after successful auth)
    val newRegistration: String?     // New registration flag
)
```

#### PlexUser
```kotlin
data class PlexUser(
    val id: Long?,                           // User ID
    val uuid: String?,                       // User UUID
    val email: String?,                      // Email address
    val username: String?,                   // Username
    val title: String?,                      // Display name
    val thumb: String?,                      // Avatar URL
    val hasPassword: Boolean,                // Password status
    val authToken: String?,                  // Authentication token
    val subscription: PlexSubscription?,     // Subscription details
    val roles: PlexRoles?,                   // User roles
    val entitlements: List<String>           // Feature entitlements
)
```

### Server Models

#### PlexServer
```kotlin
@Entity(tableName = "plex_servers")
data class PlexServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,                        // Server name
    val address: String,                     // Server IP/hostname
    val port: Int = 32400,                   // Server port
    val token: String?,                      // Authentication token
    val isLocal: Boolean = true,             // Local network flag
    val isOwned: Boolean = false,            // Ownership flag
    val machineIdentifier: String?,          // Unique machine ID
    val version: String?,                    // Plex version
    val createdAt: Long,                     // Creation timestamp
    val updatedAt: Long                      // Last update timestamp
)
```

#### PlexServerInfo
```kotlin
data class PlexServerInfo(
    val machineIdentifier: String,           // Unique server ID
    val version: String,                     // Plex version
    val myPlex: Boolean,                     // Plex.tv integration
    val friendlyName: String,                // Server display name
    val platform: String,                    // Platform (Windows, Linux, etc.)
    val platformVersion: String,             // Platform version
    val multiuser: Boolean,                  // Multi-user support
    val transcoderVideo: Boolean,            // Video transcoding support
    val transcoderAudio: Boolean             // Audio transcoding support
)
```

### Library Models

#### PlexLibrary
```kotlin
@Entity(
    tableName = "plex_libraries",
    foreignKeys = [
        ForeignKey(
            entity = PlexServer::class,
            parentColumns = ["id"],
            childColumns = ["serverId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlexLibrary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long,                      // Parent server ID
    val title: String,                       // Library name
    val type: String,                        // Type: movie, show, artist, photo
    val key: String,                         // Library key/section ID
    val agent: String?,                      // Metadata agent
    val scanner: String?,                    // Media scanner
    val language: String?,                   // Preferred language
    val refreshing: Boolean = false,         // Refresh status
    val createdAt: Long,                     // Creation timestamp
    val updatedAt: Long                      // Last update timestamp
)
```

### Media Models

#### PlexMediaItem
```kotlin
@Entity(
    tableName = "plex_media_items",
    foreignKeys = [
        ForeignKey(
            entity = PlexLibrary::class,
            parentColumns = ["id"],
            childColumns = ["libraryId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class PlexMediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryId: Long,                     // Parent library ID
    val ratingKey: String,                   // Plex rating key
    val key: String,                         // Media key
    val title: String,                       // Media title
    val type: String,                        // Type: movie, episode, track, photo
    val thumb: String?,                      // Thumbnail URL
    val art: String?,                        // Background art URL
    val duration: Long?,                     // Duration in milliseconds
    val year: Int?,                          // Release year
    val summary: String?,                    // Description/synopsis
    val rating: Float?,                      // Content rating
    val contentRating: String?,              // Age rating (PG, R, etc.)
    val viewCount: Int = 0,                  // Number of views
    val viewOffset: Long = 0,                // Playback position in milliseconds
    val lastViewedAt: Long?,                 // Last viewed timestamp
    val addedAt: Long?,                      // Date added timestamp
    val updatedAt: Long?                     // Last updated timestamp
)
```

## Usage Examples

### Initializing PlexConnect

```kotlin
class PlexConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize dependency container
        val container = DependencyContainer(this)

        // Initialize sync managers
        val themeSyncManager = ThemeSyncManager.getInstance(
            this,
            "com.shareconnect.plexconnect",
            "PlexConnect",
            "1.0.0"
        )
        themeSyncManager.startSync()

        val profileSyncManager = ProfileSyncManager.getInstance(
            this,
            "com.shareconnect.plexconnect",
            "PlexConnect",
            "1.0.0"
        )
        profileSyncManager.startSync()

        // Additional sync managers...
    }
}
```

### PIN-Based Authentication Flow

```kotlin
// 1. Request a PIN
val clientId = UUID.randomUUID().toString()
val pinResult = plexApiClient.requestPin(clientId)

if (pinResult.isSuccess) {
    val pin = pinResult.getOrNull()!!

    // 2. Display PIN code to user
    println("Enter this PIN at plex.tv/link: ${pin.code}")

    // 3. Poll for authentication completion
    var authenticated = false
    while (!authenticated) {
        delay(2000) // Wait 2 seconds

        val checkResult = plexApiClient.checkPin(pin.id)
        if (checkResult.isSuccess) {
            val updatedPin = checkResult.getOrNull()!!
            if (updatedPin.authToken != null) {
                // Authentication successful!
                authenticated = true
                val authToken = updatedPin.authToken

                // Save token and proceed
                saveAuthToken(authToken)
            }
        }
    }
}
```

### Adding and Testing a Server

```kotlin
// Create server configuration
val server = PlexServer(
    name = "My Home Server",
    address = "192.168.1.100",
    port = 32400,
    token = authToken,
    isLocal = true,
    isOwned = true,
    createdAt = System.currentTimeMillis(),
    updatedAt = System.currentTimeMillis()
)

// Test connection
val testResult = serverRepository.testServerConnection(server)
if (testResult.isSuccess) {
    val serverInfo = testResult.getOrNull()!!

    // Update server with retrieved info
    val updatedServer = server.copy(
        machineIdentifier = serverInfo.machineIdentifier,
        version = serverInfo.version,
        name = serverInfo.friendlyName
    )

    // Save to database
    val serverId = serverRepository.addServer(updatedServer)
    println("Server added with ID: $serverId")
} else {
    println("Connection failed: ${testResult.exceptionOrNull()?.message}")
}
```

### Browsing Media Libraries

```kotlin
// Get all servers
val servers = serverRepository.getAllServers()

for (server in servers) {
    // Refresh libraries from server
    val refreshResult = libraryRepository.refreshLibraries(server, server.token!!)

    if (refreshResult.isSuccess) {
        // Observe libraries
        libraryRepository.getLibrariesForServer(server.id)
            .collect { libraries ->
                println("${server.name} has ${libraries.size} libraries:")

                libraries.forEach { library ->
                    println("  - ${library.title} (${library.type})")

                    // Get media items for each library
                    mediaRepository.getMediaItemsForLibrary(library.id)
                        .collect { items ->
                            println("    ${items.size} items")
                        }
                }
            }
    }
}
```

### Tracking Playback Progress

```kotlin
// Get media item
val mediaItem = mediaRepository.getMediaItemById(mediaId)

// Start playback
startPlayback(mediaItem)

// Update timeline periodically
var currentPosition = 0L
while (isPlaying) {
    delay(5000) // Update every 5 seconds

    currentPosition = getCurrentPlaybackPosition()

    plexApiClient.updateTimeline(
        serverUrl = server.address,
        ratingKey = mediaItem.ratingKey,
        token = server.token!!,
        position = currentPosition
    )
}

// When playback completes, mark as watched
if (currentPosition >= (mediaItem.duration ?: 0) * 0.9) {
    plexApiClient.markWatched(
        serverUrl = server.address,
        ratingKey = mediaItem.ratingKey,
        token = server.token!!
    )
}
```

### Recently Added Media

```kotlin
// Get recently added items across all libraries
val recentItems = mediaRepository.getRecentlyAddedItems(
    serverId = server.id,
    limit = 20
)

recentItems.forEach { item ->
    println("${item.title} - Added ${formatTimestamp(item.addedAt)}")
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

### Database
```gradle
implementation("androidx.room:room-runtime:2.7.0-alpha07")
implementation("androidx.room:room-ktx:2.7.0-alpha07")
ksp("androidx.room:room-compiler:2.7.0-alpha07")
implementation("net.zetetic:android-database-sqlcipher:4.5.4")
```

### Networking
```gradle
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### Serialization
```gradle
implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
implementation("com.google.code.gson:gson:2.10.1")
```

### Image Loading
```gradle
implementation("io.coil-kt:coil-compose:2.7.0")
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
implementation(project(":Toolkit:SecurityAccess"))
```

### Testing
```gradle
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.robolectric:robolectric:4.11.1")
testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
```

## Security Considerations

### Authentication
- **PIN-based auth**: Secure authentication flow through Plex.tv
- **Token storage**: Auth tokens encrypted in Room database with SQLCipher
- **Session management**: Automatic token refresh and expiration handling

### Data Protection
- **SQLCipher encryption**: All local data encrypted at rest
- **HTTPS only**: All API communication over TLS
- **No credential storage**: Never stores user passwords, only tokens

### Network Security
- **Certificate pinning**: Optional support for server certificate validation
- **Local network detection**: Identifies local vs remote connections
- **Timeout handling**: Proper timeout configuration to prevent hanging connections

## Performance Optimization

### Caching Strategy
- **Library caching**: Libraries cached locally with configurable TTL
- **Media metadata**: Media items cached to reduce server load
- **Image caching**: Coil handles thumbnail and artwork caching
- **Pagination**: Large libraries loaded in chunks (default 50 items)

### Background Operations
- **WorkManager**: Background sync of library updates
- **Coroutines**: Async operations on IO dispatcher
- **Flow**: Reactive data updates to UI

### Memory Management
- **Paging**: Large media lists paginated
- **Image sizing**: Thumbnails loaded at appropriate resolution
- **Database queries**: Indexed queries for performance
- **Resource cleanup**: Proper lifecycle management

## Testing

PlexConnect includes comprehensive test coverage:

### Unit Tests (17 tests)
- **PlexApiClientMockKTest**: API client tests using MockK
- **PlexModelsTest**: Data model serialization/deserialization tests

### Integration Tests (28 tests)
- **PlexApiClientIntegrationTest**: API integration with MockWebServer
- **PlexServerRepositoryTest**: Server repository database tests
- **PlexLibraryRepositoryTest**: Library repository tests
- **PlexMediaRepositoryTest**: Media repository tests

### Automation Tests (9 tests)
- **PlexConnectAutomationTest**: End-to-end app launch and lifecycle tests
- **PlexApiServiceIntegrationTest**: Full API service integration tests

**Total Coverage**: 54 tests with 85% code coverage

---

**Last Updated**: 2025-10-25
**Version**: 1.0.0
**Plex Server Compatibility**: 1.0.0+
**Android API**: 28-36
