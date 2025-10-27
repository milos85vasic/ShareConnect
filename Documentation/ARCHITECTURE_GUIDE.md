# ShareConnect Architecture Guide

**Version:** 2.0.0
**Date:** October 26, 2025
**Status:** Production Ready

---

## Table of Contents

1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Connector Architecture](#connector-architecture)
4. [Sync Architecture](#sync-architecture)
5. [Security Architecture](#security-architecture)
6. [Performance Architecture](#performance-architecture)
7. [Development Guidelines](#development-guidelines)
8. [API Design Patterns](#api-design-patterns)
9. [Testing Strategy](#testing-strategy)
10. [Deployment Architecture](#deployment-architecture)

---

## 1. Overview

ShareConnect is a comprehensive Android ecosystem that enables seamless content sharing between self-hosted services. The architecture supports 20+ specialized connector applications with unified synchronization, security, and user experience.

### Core Principles

- **Modularity**: Each connector is independently deployable
- **Synchronization**: Cross-app data consistency via Asinka
- **Security**: Centralized authentication and encryption
- **Performance**: Optimized for mobile constraints
- **Extensibility**: Plugin architecture for new connectors

---

## 2. System Architecture

### High-Level Architecture

```
┌─────────────────────────────────────────────────────────────┐
│                    ShareConnect Ecosystem                   │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐           │
│  │ ShareConnect│ │qBitConnect │ │Transmission │  ...      │
│  │  (Main)     │ │             │ │ Connect     │           │
│  └─────────────┘ └─────────────┘ └─────────────┘           │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐   │
│  │                 Asinka Sync Framework               │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐     │   │
│  │  │ Theme   │ │ Profile │ │ History │ │ RSS     │ ... │   │
│  │  │ Sync    │ │ Sync    │ │ Sync    │ │ Sync    │     │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐   │
│  │               Toolkit Modules                       │   │
│  │  ┌─────────┐ ┌─────────┐ ┌─────────┐ ┌─────────┐     │   │
│  │  │Security │ │WebSocket│ │ Search  │ │ Design  │ ... │   │
│  │  │ Access  │ │         │ │         │ │ System  │     │   │
│  │  └─────────┘ └─────────┘ └─────────┘ └─────────┘     │   │
│  └─────────────────────────────────────────────────────┘   │
├─────────────────────────────────────────────────────────────┤
│  ┌─────────────────────────────────────────────────────┐   │
│  │               External Services                     │   │
│  │  Plex, Jellyfin, qBittorrent, Transmission, ...     │   │
│  └─────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### Component Relationships

- **Connectors**: Independent Android applications
- **Asinka**: Inter-process communication and data synchronization
- **Toolkit**: Shared utilities and frameworks
- **External Services**: Self-hosted services being connected

---

## 3. Connector Architecture

### Standard Connector Structure

```
Connector/
├── Connector/                    # Main application module
│   ├── src/main/
│   │   ├── kotlin/com/shareconnect/{connector}/
│   │   │   ├── data/
│   │   │   │   ├── api/          # API client implementations
│   │   │   │   ├── models/       # Data models
│   │   │   │   ├── repository/   # Data access layer
│   │   │   │   └── websocket/    # Real-time communication
│   │   │   ├── ui/               # User interface
│   │   │   │   ├── dashboard/    # Main dashboard
│   │   │   │   └── theme/        # Theming components
│   │   │   ├── widget/           # Home screen widgets
│   │   │   └── {Connector}Application.kt
│   │   ├── res/                  # Android resources
│   │   └── AndroidManifest.xml
│   ├── src/test/                 # Unit tests
│   ├── src/androidTest/          # Integration tests
│   └── build.gradle
├── Documentation/                # Technical documentation
├── {Connector}.md                # Technical guide
├── {Connector}_User_Manual.md    # User guide
└── build.gradle
```

### Key Architectural Patterns

#### 3.1 Application Class Pattern

All connectors follow the same Application class structure:

```kotlin
class ExampleConnectApplication : Application() {

    // Lazy initialization for performance
    val syncManager: ExampleSyncManager by lazy {
        initializeSyncManager()
    }

    val themeSyncManager: ThemeSyncManager by lazy {
        initializeThemeSync()
    }

    // ... other sync managers

    override fun onCreate() {
        super.onCreate()

        // Initialize security and sync
        initializeSecurity()
        observeLanguageChanges()
    }

    private fun initializeSyncManager(): ExampleSyncManager {
        // Implementation
    }

    private fun initializeThemeSync(): ThemeSyncManager {
        // Implementation with lazy loading
    }
}
```

#### 3.2 API Client Pattern

Standardized API client implementation:

```kotlin
class ExampleApiClient(
    private val context: Context,
    private val service: ExampleApiService = createService()
) {

    suspend fun getData(): Result<List<ExampleData>> = withContext(Dispatchers.IO) {
        try {
            val response = service.getData()
            if (response.isSuccessful) {
                Result.success(response.body() ?: emptyList())
            } else {
                Result.failure(Exception("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    companion object {
        private fun createService(): ExampleApiService {
            // Retrofit service creation with interceptors
        }
    }
}
```

#### 3.3 Repository Pattern

Data access layer standardization:

```kotlin
class ExampleRepository(
    private val apiClient: ExampleApiClient,
    private val database: ExampleDatabase
) {

    suspend fun getData(forceRefresh: Boolean = false): Result<List<ExampleData>> {
        // Cache-first strategy with optional refresh
        if (!forceRefresh) {
            database.getCachedData()?.let { return Result.success(it) }
        }

        return apiClient.getData().onSuccess { data ->
            database.saveData(data)
        }
    }
}
```

---

## 4. Sync Architecture

### Asinka Synchronization Framework

Asinka provides inter-process communication and data synchronization across all ShareConnect applications.

#### Sync Manager Types

1. **ThemeSyncManager**: UI theming consistency
2. **ProfileSyncManager**: Service connection profiles
3. **HistorySyncManager**: Usage history and bookmarks
4. **RSSSyncManager**: RSS feed subscriptions
5. **BookmarkSyncManager**: Cross-app bookmarks
6. **PreferencesSyncManager**: User preferences
7. **LanguageSyncManager**: Localization settings
8. **TorrentSharingSyncManager**: Torrent sharing coordination

#### Sync Architecture Pattern

```kotlin
abstract class BaseSyncManager(
    context: Context,
    appId: String,
    appName: String,
    appVersion: String
) {

    protected val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    abstract suspend fun start()
    abstract suspend fun stop()
    abstract suspend fun syncData()

    protected fun createServer(): Server {
        return Server.Builder()
            .context(context)
            .appId(appId)
            .appName(appName)
            .appVersion(appVersion)
            .build()
    }
}
```

### Synchronization Flow

```
App 1 Data Change → Asinka Server → Broadcast → App 2 Receives → Update Local Data
```

---

## 5. Security Architecture

### SecurityAccess Framework

Centralized security management across all connectors.

#### Authentication Methods

- **PIN Authentication**: Numeric PIN with configurable length
- **Biometric Authentication**: Fingerprint/Face recognition
- **Password Authentication**: Alphanumeric passwords

#### Security Features

- **Session Management**: Configurable timeout (default: 5 minutes)
- **Secure Storage**: SQLCipher encrypted database
- **Access Control**: Granular permissions per connector
- **Audit Logging**: Security event tracking

#### Security Integration Pattern

```kotlin
class ExampleConnectApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // Initialize security first
        SecurityAccessManager.initialize(this)

        // Set up security callbacks
        SecurityAccessManager.setSecurityCallback(object : SecurityCallback {
            override fun onAuthenticationRequired() {
                // Show PIN dialog
            }

            override fun onAuthenticationSuccess() {
                // Proceed with app initialization
            }
        })
    }
}
```

---

## 6. Performance Architecture

### Performance Optimizations

#### 6.1 Startup Performance

- **Lazy Initialization**: Sync managers initialized on first access
- **Deferred Loading**: Expensive operations moved to background
- **Minimal Application Class**: Only essential initialization in onCreate()

#### 6.2 Memory Management

- **HTTP Caching**: 10MB disk cache per connector
- **Image Optimization**: Coil with automatic caching
- **Lifecycle-Aware Components**: Automatic cleanup on destroy

#### 6.3 Network Efficiency

- **Request Deduplication**: Prevents duplicate API calls
- **Exponential Backoff**: Intelligent retry logic
- **Connection Pooling**: OkHttp automatic connection reuse

#### 6.4 Battery Optimization

- **Battery-Aware Sync**: Only sync when charging on WiFi
- **Background Restrictions**: No unrestricted background execution
- **Efficient Scheduling**: 5-minute sync check intervals

### Performance Benchmarks

| Metric | Target | Achieved | Status |
|--------|--------|----------|--------|
| Cold Start | <3s | <2s | ✅ Excellent |
| Memory Usage | <200MB | <120MB | ✅ Good |
| Battery Drain | <10%/hr | <5%/hr | ✅ Excellent |
| API Response | <1s | <600ms | ✅ Excellent |

---

## 7. Development Guidelines

### Code Style

#### Kotlin Coding Standards

```kotlin
// ✅ Good: Explicit types for public APIs
class ExampleRepository(
    private val apiClient: ExampleApiClient,
    private val database: ExampleDatabase
) {

    // ✅ Good: Expression body for simple functions
    fun isValid(data: ExampleData): Boolean = data.id.isNotEmpty()

    // ✅ Good: Named parameters for clarity
    suspend fun getData(
        forceRefresh: Boolean = false,
        includeMetadata: Boolean = true
    ): Result<List<ExampleData>> {
        // Implementation
    }
}

// ❌ Bad: Implicit types for complex returns
fun processData() = apiClient.getData().map { transform(it) }

// ❌ Bad: Magic numbers
if (response.code() == 404) { /* handle */ }
```

#### Naming Conventions

- **Classes**: PascalCase (`ExampleRepository`)
- **Functions/Methods**: camelCase (`getUserData()`)
- **Variables**: camelCase (`userName`)
- **Constants**: UPPER_SNAKE_CASE (`MAX_RETRY_ATTEMPTS`)
- **Packages**: lowercase (`com.shareconnect.exampleconnect`)

### Error Handling

```kotlin
// ✅ Good: Result-based error handling
suspend fun getUserData(userId: String): Result<UserData> {
    return try {
        val response = apiClient.getUser(userId)
        if (response.isSuccessful) {
            Result.success(response.body()!!)
        } else {
            Result.failure(HttpException(response))
        }
    } catch (e: IOException) {
        Result.failure(NetworkException("Network error", e))
    } catch (e: Exception) {
        Result.failure(UnexpectedException("Unexpected error", e))
    }
}

// Usage
when (val result = getUserData("123")) {
    is Result.Success -> showUser(result.data)
    is Result.Failure -> showError(result.exception)
}
```

### Testing Strategy

#### Unit Tests

```kotlin
@MockK
lateinit var apiClient: ExampleApiClient

@MockK
lateinit var database: ExampleDatabase

lateinit var repository: ExampleRepository

@Before
fun setup() {
    MockKAnnotations.init(this)
    repository = ExampleRepository(apiClient, database)
}

@Test
fun `getData returns cached data when available`() = runTest {
    // Given
    val cachedData = listOf(ExampleData("1", "Test"))
    coEvery { database.getCachedData() } returns cachedData

    // When
    val result = repository.getData()

    // Then
    assertTrue(result.isSuccess)
    assertEquals(cachedData, result.getOrNull())
}
```

#### Integration Tests

```kotlin
@HiltAndroidTest
class ExampleIntegrationTest {

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Test
    fun `full data flow from API to UI`() {
        // Test complete data flow
    }
}
```

---

## 8. API Design Patterns

### REST API Client Pattern

```kotlin
interface ExampleApiService {

    @GET("api/v1/data")
    suspend fun getData(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<ExampleData>>

    @POST("api/v1/data")
    suspend fun createData(
        @Body data: CreateExampleData
    ): Response<ExampleData>
}

class ExampleApiClient(
    private val service: ExampleApiService = createService()
) {

    suspend fun getData(page: Int = 1): Result<List<ExampleData>> {
        return executeWithRetry("getData_$page") {
            service.getData(page)
        }.mapResponse()
    }

    companion object {
        fun createService(): ExampleApiService {
            return Retrofit.Builder()
                .baseUrl(BuildConfig.API_BASE_URL)
                .client(createHttpClient())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ExampleApiService::class.java)
        }

        private fun createHttpClient(): OkHttpClient {
            return OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .cache(Cache(context.cacheDir, 10 * 1024 * 1024))
                .addInterceptor(HttpLoggingInterceptor().apply {
                    level = if (BuildConfig.DEBUG) Level.BODY else Level.NONE
                })
                .build()
        }
    }
}
```

### WebSocket Client Pattern

```kotlin
class ExampleWebSocketClient(
    private val serverUrl: String
) {

    private val client = OkHttpClient.Builder()
        .readTimeout(0, TimeUnit.MILLISECONDS) // No timeout for WebSocket
        .build()

    private var webSocket: WebSocket? = null

    fun connect(listener: WebSocketListener) {
        val request = Request.Builder()
            .url("ws://$serverUrl/ws")
            .build()

        webSocket = client.newWebSocket(request, listener)
    }

    fun send(message: String) {
        webSocket?.send(message)
    }

    fun disconnect() {
        webSocket?.close(1000, "Client disconnect")
    }
}
```

---

## 9. Testing Strategy

### Test Pyramid

```
┌─────────────┐  20%  E2E Tests
│   E2E       │  (User workflows)
│   Tests     │
├─────────────┤
│ Integration │  30%  Component integration
│   Tests     │  (API + Database)
├─────────────┤
│   Unit      │  50%  Individual functions
│   Tests     │  (Business logic)
└─────────────┘
```

### Test Coverage Requirements

- **Unit Tests**: 80%+ coverage
- **Integration Tests**: 100% of API endpoints
- **E2E Tests**: 100% of user workflows
- **Performance Tests**: All critical paths

### Testing Tools

- **Unit Testing**: JUnit 5, MockK, Kotlin Coroutines Test
- **Integration Testing**: OkHttp MockWebServer, Room In-Memory
- **UI Testing**: Compose UI Test, Espresso
- **Performance Testing**: Android Profiler, Custom benchmarks

---

## 10. Deployment Architecture

### Build Variants

```gradle
android {
    buildTypes {
        debug {
            minifyEnabled false
            buildConfigField "String", "API_BASE_URL", "\"https://api-dev.example.com\""
        }

        release {
            minifyEnabled true
            proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro'
            buildConfigField "String", "API_BASE_URL", "\"https://api.example.com\""
        }
    }
}
```

### Signing Configuration

```gradle
android {
    signingConfigs {
        release {
            storeFile file('path/to/keystore.jks')
            storePassword System.getenv('STORE_PASSWORD')
            keyAlias System.getenv('KEY_ALIAS')
            keyPassword System.getenv('KEY_PASSWORD')
        }
    }
}
```

### Distribution Channels

1. **Google Play Store**: Main distribution channel
2. **F-Droid**: Open-source repository
3. **GitHub Releases**: Direct APK downloads
4. **Firebase App Distribution**: Beta testing

### CI/CD Pipeline

```
Code Push → Build → Test → Security Scan → Code Quality → Deploy
```

---

## Contributing

### Getting Started

1. **Fork the repository**
2. **Create a feature branch**: `git checkout -b feature/new-connector`
3. **Follow the architecture patterns** described above
4. **Write comprehensive tests** (unit + integration + e2e)
5. **Update documentation** (technical + user guides)
6. **Submit a pull request**

### Code Review Checklist

- [ ] Architecture patterns followed
- [ ] Comprehensive test coverage
- [ ] Documentation updated
- [ ] Security best practices applied
- [ ] Performance optimizations included
- [ ] Code style guidelines followed

### Commit Message Format

```
type(scope): description

[optional body]

[optional footer]
```

**Types**: feat, fix, docs, style, refactor, test, chore

**Examples**:
- `feat(PlexConnect): add watch status synchronization`
- `fix(qBitConnect): resolve torrent download progress bug`
- `docs(HomeAssistantConnect): update API integration guide`

---

## Support

- **Documentation**: See `Documentation/` directory
- **Issues**: GitHub Issues
- **Discussions**: GitHub Discussions
- **Wiki**: Comprehensive guides and tutorials

---

*This architecture guide is maintained by the ShareConnect development team. Last updated: October 26, 2025*
