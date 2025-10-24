# PlexConnect API Reference

This document provides comprehensive API reference for PlexConnect's public interfaces, data models, and integration points.

## Table of Contents

1. [Core Components](#core-components)
2. [Data Models](#data-models)
3. [API Client](#api-client)
4. [Repositories](#repositories)
5. [ViewModels](#viewmodels)
6. [Services](#services)
7. [Dependency Injection](#dependency-injection)

## Core Components

### Application Entry Points

#### MainActivity
Main application activity handling navigation and lifecycle.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check onboarding completion
        if (isOnboardingNeeded()) {
            startActivity(Intent(this, OnboardingActivity::class.java))
            finish()
            return
        }

        setContent {
            App()
        }
    }
}
```

#### OnboardingActivity
First-run setup activity with 3-page onboarding flow.

```kotlin
class OnboardingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            OnboardingScreen()
        }
    }
}
```

### Navigation Structure

#### App Navigation
```kotlin
@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Screen.Home.route
) {
    NavHost(navController, startDestination) {
        composable(Screen.Home.route) { HomeScreen() }
        composable(Screen.Libraries.route) { LibraryListScreen() }
        composable(Screen.Search.route) { SearchScreen() }
        composable(Screen.Settings.route) { SettingsScreen() }
        // Additional routes...
    }
}
```

## Data Models

### PlexServer
Represents a Plex Media Server instance.

```kotlin
@Entity(tableName = "plex_servers")
data class PlexServer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val address: String,          // IP address or hostname
    val port: Int = 32400,
    val token: String? = null,
    val isLocal: Boolean = true,
    val isOwned: Boolean = false,
    val ownerId: Long? = null,
    val machineIdentifier: String? = null,
    val version: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val baseUrl: String
        get() = "http://$address:$port"

    val isAuthenticated: Boolean
        get() = !token.isNullOrBlank()
}
```

**Properties:**
- `id`: Unique database identifier
- `name`: User-friendly server name
- `address`: Server IP address or hostname
- `port`: Server port (default: 32400)
- `token`: Authentication token for server access
- `isLocal`: Whether server is on local network
- `isOwned`: Whether user owns this server
- `machineIdentifier`: Unique server identifier
- `version`: Server version string
- `createdAt`/`updatedAt`: Timestamps

### PlexLibrary
Represents a media library section.

```kotlin
@Entity(tableName = "plex_libraries")
data class PlexLibrary(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val serverId: Long,
    val title: String,
    val type: String,             // "movie", "show", "artist", "photo"
    val key: String,
    val art: String? = null,
    val thumb: String? = null,
    val composite: String? = null,
    val filters: Boolean = false,
    val refreshing: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
```

**Properties:**
- `id`: Unique database identifier
- `serverId`: Parent server ID
- `title`: Library display name
- `type`: Library type (movie, show, artist, photo)
- `key`: Plex API key for library
- `art`/`thumb`/`composite`: Image URLs
- `filters`: Whether advanced filters are available
- `refreshing`: Whether library is currently refreshing

### PlexMediaItem
Represents individual media content.

```kotlin
@Entity(tableName = "plex_media_items")
data class PlexMediaItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val libraryId: Long,
    val title: String,
    val type: String,             // "movie", "episode", "track", "photo"
    val key: String,
    val thumb: String? = null,
    val art: String? = null,
    val duration: Long? = null,
    val viewCount: Int = 0,
    val viewOffset: Long = 0,
    val lastViewedAt: Long? = null,
    val year: Int? = null,
    val rating: Float? = null,
    val contentRating: String? = null,
    val summary: String? = null,
    val tagline: String? = null,
    val studio: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val isWatched: Boolean
        get() = viewCount > 0

    val progress: Float
        get() = if (duration != null && duration > 0) {
            (viewOffset.toFloat() / duration.toFloat()).coerceIn(0f, 1f)
        } else 0f
}
```

**Properties:**
- `id`: Unique database identifier
- `libraryId`: Parent library ID
- `title`: Media title
- `type`: Media type
- `key`: Plex API key
- `duration`: Media duration in milliseconds
- `viewCount`: Number of times viewed
- `viewOffset`: Current playback position
- `year`/`rating`: Metadata
- `isWatched`: Computed property for watch status
- `progress`: Computed playback progress (0.0-1.0)

## API Client

### PlexApiClient
Handles all network communication with Plex servers.

```kotlin
class PlexApiClient {
    // Authentication
    suspend fun requestPin(clientIdentifier: String): Result<PlexPinResponse>
    suspend fun checkPin(pinId: Long, clientIdentifier: String): Result<PlexAuthResponse>

    // Server operations
    suspend fun getServerInfo(baseUrl: String): Result<PlexServerInfo>
    suspend fun getLibraries(baseUrl: String, token: String): Result<List<PlexLibrary>>
    suspend fun getLibraryContents(
        baseUrl: String,
        token: String,
        libraryKey: String,
        start: Int = 0,
        size: Int = 50
    ): Result<List<PlexMediaItem>>

    // Media operations
    suspend fun getMediaDetails(
        baseUrl: String,
        token: String,
        mediaKey: String
    ): Result<PlexMediaItem>

    suspend fun searchMedia(
        baseUrl: String,
        token: String,
        query: String,
        type: String? = null
    ): Result<List<PlexMediaItem>>
}
```

### API Response Models

#### PlexPinResponse
```kotlin
data class PlexPinResponse(
    val id: Long,
    val code: String,
    val product: String = "PlexConnect",
    val trusted: Boolean = true,
    val clientIdentifier: String,
    val location: PlexLocation? = null,
    val expiresAt: Long? = null
)
```

#### PlexAuthResponse
```kotlin
data class PlexAuthResponse(
    val authToken: String,
    val user: PlexUser? = null
)
```

#### PlexServerInfo
```kotlin
data class PlexServerInfo(
    val name: String? = null,
    val machineIdentifier: String? = null,
    val version: String? = null,
    val owned: Int? = null,
    val accessToken: String? = null
)
```

## Repositories

### PlexServerRepository
Manages Plex server data and operations.

```kotlin
class PlexServerRepository(
    private val serverDao: PlexServerDao,
    private val apiClient: PlexApiClient
) {
    // Server queries
    fun getAllServers(): Flow<List<PlexServer>>
    fun getLocalServers(): Flow<List<PlexServer>>
    fun getOwnedServers(): Flow<List<PlexServer>>
    suspend fun getServerById(serverId: Long): PlexServer?
    suspend fun getServerByMachineIdentifier(machineIdentifier: String): PlexServer?

    // Server management
    suspend fun addServer(server: PlexServer): Long
    suspend fun updateServer(server: PlexServer)
    suspend fun deleteServer(server: PlexServer)
    suspend fun deleteServerById(serverId: Long)

    // Connection testing
    suspend fun testServerConnection(server: PlexServer): Result<PlexServerInfo>
    suspend fun authenticateServer(server: PlexServer, token: String): Result<PlexServer>
    suspend fun refreshServerInfo(server: PlexServer): Result<PlexServer>

    // Statistics
    suspend fun getServerCount(): Int
    suspend fun getAuthenticatedServerCount(): Int
}
```

### PlexLibraryRepository
Manages media library data.

```kotlin
class PlexLibraryRepository(
    private val libraryDao: PlexLibraryDao,
    private val apiClient: PlexApiClient
) {
    // Library queries
    fun getLibrariesForServer(serverId: Long): Flow<List<PlexLibrary>>
    suspend fun getLibraryById(libraryId: Long): PlexLibrary?
    suspend fun getLibraryByKey(serverId: Long, key: String): PlexLibrary?

    // Library management
    suspend fun addLibrary(library: PlexLibrary): Long
    suspend fun updateLibrary(library: PlexLibrary)
    suspend fun deleteLibrary(library: PlexLibrary)

    // Sync operations
    suspend fun syncLibraries(server: PlexServer): Result<Unit>
    suspend fun refreshLibrary(library: PlexLibrary): Result<PlexLibrary>
}
```

### PlexMediaRepository
Manages media content data.

```kotlin
class PlexMediaRepository(
    private val mediaDao: PlexMediaItemDao,
    private val apiClient: PlexApiClient
) {
    // Media queries
    fun getMediaForLibrary(libraryId: Long): Flow<List<PlexMediaItem>>
    fun getRecentlyAdded(serverId: Long, limit: Int = 50): Flow<List<PlexMediaItem>>
    fun getOnDeck(serverId: Long, limit: Int = 50): Flow<List<PlexMediaItem>>
    suspend fun getMediaById(mediaId: Long): PlexMediaItem?
    suspend fun searchMedia(query: String, serverId: Long? = null): List<PlexMediaItem>

    // Media management
    suspend fun addMediaItem(item: PlexMediaItem): Long
    suspend fun updateMediaItem(item: PlexMediaItem)
    suspend fun deleteMediaItem(item: PlexMediaItem)

    // Playback tracking
    suspend fun updatePlaybackProgress(mediaId: Long, position: Long)
    suspend fun markAsWatched(mediaId: Long)

    // Sync operations
    suspend fun syncMediaForLibrary(library: PlexLibrary): Result<Unit>
    suspend fun refreshMediaItem(item: PlexMediaItem): Result<PlexMediaItem>
}
```

## ViewModels

### AuthenticationViewModel
Manages authentication state and operations.

```kotlin
class AuthenticationViewModel(
    private val authService: PlexAuthService
) : ViewModel() {

    val authState: StateFlow<PlexAuthService.AuthState> = authService.authState

    private val _isAuthenticating = MutableStateFlow(false)
    val isAuthenticating: StateFlow<Boolean> = _isAuthenticating.asStateFlow()

    fun startAuthentication()
    fun cancelAuthentication()
    fun resetAuthentication()

    override fun onCleared() {
        super.onCleared()
        cancelAuthentication()
    }
}
```

**State Management:**
- `authState`: Current authentication state
- `isAuthenticating`: Whether authentication is in progress

**Actions:**
- `startAuthentication()`: Begin PIN authentication flow
- `cancelAuthentication()`: Cancel ongoing authentication
- `resetAuthentication()`: Reset to idle state

### LibraryListViewModel
Manages library browsing and selection.

```kotlin
class LibraryListViewModel(
    private val libraryRepository: PlexLibraryRepository,
    private val serverRepository: PlexServerRepository
) : ViewModel() {

    private val _libraries = MutableStateFlow<List<PlexLibrary>>(emptyList())
    val libraries: StateFlow<List<PlexLibrary>> = _libraries.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadLibraries(serverId: Long)
    fun refreshLibraries(serverId: Long)
    fun selectLibrary(library: PlexLibrary)
}
```

**State Management:**
- `libraries`: Available libraries for current server
- `isLoading`: Loading state indicator
- `error`: Error message if loading fails

### MediaListViewModel
Manages media content browsing.

```kotlin
class MediaListViewModel(
    private val mediaRepository: PlexMediaRepository,
    private val libraryRepository: PlexLibraryRepository
) : ViewModel() {

    private val _mediaItems = MutableStateFlow<List<PlexMediaItem>>(emptyList())
    val mediaItems: StateFlow<List<PlexMediaItem>> = _mediaItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    fun loadMediaForLibrary(libraryId: Long)
    fun loadRecentlyAdded(serverId: Long)
    fun loadOnDeck(serverId: Long)
    fun searchMedia(query: String)
    fun refreshMedia()
}
```

### MediaDetailViewModel
Manages individual media item details and playback.

```kotlin
class MediaDetailViewModel(
    private val mediaRepository: PlexMediaRepository,
    private val playbackService: PlexPlaybackService
) : ViewModel() {

    private val _mediaItem = MutableStateFlow<PlexMediaItem?>(null)
    val mediaItem: StateFlow<PlexMediaItem?> = _mediaItem.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _playbackState = MutableStateFlow<PlaybackState>(PlaybackState.Idle)
    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun loadMediaDetails(mediaId: Long)
    fun startPlayback(mediaItem: PlexMediaItem)
    fun pausePlayback()
    fun resumePlayback()
    fun seekTo(position: Long)
    fun updateProgress(position: Long)
}
```

### OnboardingViewModel
Manages onboarding flow state.

```kotlin
class OnboardingViewModel(
    private val serverRepository: PlexServerRepository
) : ViewModel() {

    private val _hasServers = MutableStateFlow(false)
    val hasServers: StateFlow<Boolean> = _hasServers.asStateFlow()

    init {
        checkExistingServers()
    }

    private fun checkExistingServers() {
        viewModelScope.launch {
            val serverCount = serverRepository.getServerCount()
            _hasServers.value = serverCount > 0
        }
    }
}
```

## Services

### PlexAuthService
Handles Plex.tv authentication flow.

```kotlin
class PlexAuthService(
    private val apiClient: PlexApiClient
) {
    sealed class AuthState {
        object Idle : AuthState()
        data class RequestingPin(val clientId: String) : AuthState()
        data class PinReceived(val pin: PlexPinResponse) : AuthState()
        object CheckingPin : AuthState()
        data class Authenticated(val token: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun startAuthentication(clientIdentifier: String = UUID.randomUUID().toString())
    fun cancelAuthentication()
    fun reset()
    fun isAuthenticating(): Boolean
}
```

**Authentication Flow:**
1. `Idle` → `RequestingPin` → `PinReceived` → `CheckingPin` → `Authenticated` or `Error`

### PlexPlaybackService
Manages media playback operations.

```kotlin
class PlexPlaybackService(
    private val apiClient: PlexApiClient
) {
    sealed class PlaybackState {
        object Idle : PlaybackState()
        object Loading : PlaybackState()
        object Playing : PlaybackState()
        object Paused : PlaybackState()
        object Buffering : PlaybackState()
        data class Error(val message: String) : PlaybackState()
    }

    val playbackState: StateFlow<PlaybackState> = _playbackState.asStateFlow()

    fun preparePlayback(mediaItem: PlexMediaItem, server: PlexServer)
    fun startPlayback()
    fun pausePlayback()
    fun resumePlayback()
    fun stopPlayback()
    fun seekTo(position: Long)
    fun setQuality(quality: VideoQuality)
}
```

### PlexSyncService
Handles background data synchronization.

```kotlin
class PlexSyncService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_SYNC_LIBRARIES -> syncLibraries()
            ACTION_SYNC_MEDIA -> syncMedia()
            ACTION_REFRESH_SERVER -> refreshServer()
        }
        return START_STICKY
    }

    private fun syncLibraries()
    private fun syncMedia()
    private fun refreshServer()

    companion object {
        const val ACTION_SYNC_LIBRARIES = "com.shareconnect.plexconnect.SYNC_LIBRARIES"
        const val ACTION_SYNC_MEDIA = "com.shareconnect.plexconnect.SYNC_MEDIA"
        const val ACTION_REFRESH_SERVER = "com.shareconnect.plexconnect.REFRESH_SERVER"
    }
}
```

## Dependency Injection

### DependencyContainer
Central dependency injection container.

```kotlin
object DependencyContainer {

    // Database
    val plexDatabase: PlexDatabase by lazy {
        Room.databaseBuilder(
            AndroidApp.context,
            PlexDatabase::class.java,
            "plex_database"
        ).build()
    }

    // DAOs
    val plexServerDao: PlexServerDao by lazy { plexDatabase.plexServerDao() }
    val plexLibraryDao: PlexLibraryDao by lazy { plexDatabase.plexLibraryDao() }
    val plexMediaDao: PlexMediaItemDao by lazy { plexDatabase.plexMediaItemDao() }

    // API Client
    val plexApiClient: PlexApiClient by lazy { PlexApiClient() }

    // Services
    val plexAuthService: PlexAuthService by lazy { PlexAuthService(plexApiClient) }
    val plexPlaybackService: PlexPlaybackService by lazy { PlexPlaybackService(plexApiClient) }

    // Repositories
    val plexServerRepository: PlexServerRepository by lazy {
        PlexServerRepository(plexServerDao, plexApiClient)
    }

    val plexLibraryRepository: PlexLibraryRepository by lazy {
        PlexLibraryRepository(plexLibraryDao, plexApiClient)
    }

    val plexMediaRepository: PlexMediaRepository by lazy {
        PlexMediaRepository(plexMediaDao, plexApiClient)
    }
}
```

### ViewModel Factories

```kotlin
class AuthenticationViewModelFactory(
    private val authService: PlexAuthService = DependencyContainer.plexAuthService
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return AuthenticationViewModel(authService) as T
    }
}

class LibraryListViewModelFactory(
    private val libraryRepository: PlexLibraryRepository = DependencyContainer.plexLibraryRepository,
    private val serverRepository: PlexServerRepository = DependencyContainer.plexServerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return LibraryListViewModel(libraryRepository, serverRepository) as T
    }
}
```

## Error Handling

### Result Types
PlexConnect uses Kotlin's `Result<T>` type for error handling:

```kotlin
sealed class PlexError {
    data class NetworkError(val message: String) : PlexError()
    data class AuthenticationError(val message: String) : PlexError()
    data class ServerError(val code: Int, val message: String) : PlexError()
    data class ParsingError(val message: String) : PlexError()
    data class UnknownError(val throwable: Throwable) : PlexError()
}

// Usage in repositories
suspend fun testServerConnection(server: PlexServer): Result<PlexServerInfo> {
    return try {
        val result = apiClient.getServerInfo(server.baseUrl)
        result
    } catch (e: IOException) {
        Result.failure(PlexError.NetworkError("Cannot connect to server: ${e.message}"))
    } catch (e: Exception) {
        Result.failure(PlexError.UnknownError(e))
    }
}
```

### Error Propagation
Errors flow from API client → Repository → ViewModel → UI:

```kotlin
// In ViewModel
private val _error = MutableStateFlow<String?>(null)
val error: StateFlow<String?> = _error.asStateFlow()

fun loadLibraries(serverId: Long) {
    viewModelScope.launch {
        _isLoading.value = true
        libraryRepository.getLibrariesForServer(serverId)
            .catch { error ->
                _error.value = "Failed to load libraries: ${error.message}"
            }
            .collect { libraries ->
                _libraries.value = libraries
                _error.value = null
            }
        _isLoading.value = false
    }
}
```

## Testing

### Unit Test Example
```kotlin
@Test
fun `addServer adds server with timestamp and returns id`() = runTest {
    // Given
    val server = PlexServer(name = "Test Server", address = "192.168.1.100")
    coEvery { mockDao.insertServer(any()) } returns 123L

    // When
    val result = repository.addServer(server)

    // Then
    assertEquals(123L, result)
    coVerify {
        mockDao.insertServer(withArg { insertedServer ->
            assertEquals(server.name, insertedServer.name)
            assertTrue(insertedServer.updatedAt > 0)
        })
    }
}
```

### Integration Test Example
```kotlin
@Test
fun `getServerInfo returns server info on successful response`() = runTest {
    // Given
    val mockResponse = MockResponse()
        .setResponseCode(200)
        .setBody("""{"MediaContainer":{"friendlyName":"Test Server"}}""")
    mockWebServer.enqueue(mockResponse)

    // When
    val result = apiClient.getServerInfo("http://test.com")

    // Then
    assertTrue(result.isSuccess)
    assertEquals("Test Server", result.getOrNull()?.name)
}
```

---

**Document Version:** 1.0.0
**Last Updated:** 2025-10-24
**API Version:** 1.0.0