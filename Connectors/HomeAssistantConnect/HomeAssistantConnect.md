# HomeAssistantConnect Technical Documentation

## Overview

HomeAssistantConnect is a comprehensive Android application that integrates with Home Assistant, providing full control over smart home automation, IoT devices, and home monitoring systems. It is part of the ShareConnect ecosystem and leverages the Asinka synchronization framework for seamless data sharing across ShareConnect applications.

**Package**: `com.shareconnect.homeassistantconnect`
**Version**: 1.0.0
**Minimum SDK**: 28 (Android 9.0)
**Target SDK**: 36
**Compile SDK**: 36

## Architecture

### Application Components

#### 1. HomeAssistantConnectApplication

The main application class that initializes all sync managers and coordinates the application lifecycle.

```kotlin
class HomeAssistantConnectApplication : Application() {
    lateinit var themeSyncManager: ThemeSyncManager
    lateinit var profileSyncManager: ProfileSyncManager
    lateinit var historySyncManager: HistorySyncManager
    lateinit var rssSyncManager: RSSSyncManager
    lateinit var bookmarkSyncManager: BookmarkSyncManager
    lateinit var preferencesSyncManager: PreferencesSyncManager
    lateinit var languageSyncManager: LanguageSyncManager
    lateinit var torrentSharingSyncManager: TorrentSharingSyncManager
}
```

**Key responsibilities**:
- Initialize all Asinka sync managers with staggered delays (100ms intervals) to prevent port conflicts
- Set gRPC system properties for Android compatibility
- Apply language preferences via LocaleHelper
- Observe language changes and persist them for app restarts

#### 2. MainActivity

The primary UI entry point using Jetpack Compose and Material Design 3.

```kotlin
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HomeAssistantConnectTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeAssistantConnectContent()
                }
            }
        }
    }
}
```

Features:
- Full Compose UI with Material 3 theming
- SecurityAccess integration for biometric authentication
- Navigation between Dashboard, Entities, Automations, Scenes, and Settings
- Real-time entity state updates via WebSocket

### Data Layer

#### Home Assistant API Client

The `HomeAssistantApiClient` class provides comprehensive REST API access to Home Assistant servers.

**Base URL Format**: `http(s)://{host}:{port}`
**Authentication**: Bearer token in Authorization header
**Default Port**: 8123

##### API Endpoints

###### Configuration & Status

```kotlin
// Get API status
suspend fun getApiStatus(): Result<HomeAssistantApiStatus>

// Get Home Assistant configuration
suspend fun getConfig(): Result<HomeAssistantConfig>

// Check configuration validity
suspend fun checkConfig(): Result<Map<String, Any>>
```

###### Entity State Management

```kotlin
// Get all entity states
suspend fun getStates(): Result<List<HomeAssistantState>>

// Get specific entity state
suspend fun getState(entityId: String): Result<HomeAssistantState>

// Update entity state
suspend fun setState(
    entityId: String,
    state: HomeAssistantState
): Result<HomeAssistantState>
```

###### Service Calls

```kotlin
// Call a Home Assistant service
suspend fun callService(
    domain: String,
    service: String,
    request: ServiceCallRequest
): Result<List<ServiceCallResponse>>

// Get available services
suspend fun getServices(): Result<List<HomeAssistantServiceDomain>>
```

**Example Service Call**:
```kotlin
val result = apiClient.callService(
    domain = "light",
    service = "turn_on",
    request = ServiceCallRequest(
        entityId = "light.living_room",
        data = mapOf(
            "brightness" to 255,
            "rgb_color" to listOf(255, 0, 0)
        )
    )
)
```

###### History & Logbook

```kotlin
// Get historical state changes
suspend fun getHistory(
    timestamp: String,
    endTime: String? = null,
    filterEntityId: String? = null,
    minimalResponse: Boolean = false
): Result<List<List<HomeAssistantState>>>

// Get logbook entries
suspend fun getLogbook(
    timestamp: String,
    endTime: String? = null,
    entity: String? = null
): Result<List<HomeAssistantLogbookEntry>>
```

###### Events

```kotlin
// Get available events
suspend fun getEvents(): Result<List<HomeAssistantEvent>>

// Fire an event
suspend fun fireEvent(
    eventType: String,
    eventData: Map<String, Any>
): Result<Map<String, Any>>
```

###### Calendar Integration

```kotlin
// Get calendar events
suspend fun getCalendarEvents(
    entityId: String,
    start: String,
    end: String
): Result<List<CalendarEvent>>
```

###### Template Rendering

```kotlin
// Render Jinja2 template
suspend fun renderTemplate(
    template: String
): Result<TemplateResponse>
```

**Example Template**:
```kotlin
val result = apiClient.renderTemplate(
    "{{ states('sensor.temperature') }}°C"
)
// Returns: "22.5°C"
```

###### Intent Handling

```kotlin
// Handle conversation intent
suspend fun handleIntent(
    intent: IntentRequest
): Result<IntentResponse>
```

###### Error Logging

```kotlin
// Get error log
suspend fun getErrorLog(): Result<List<HomeAssistantErrorLog>>
```

#### WebSocket Client

Real-time updates via Home Assistant WebSocket API.

**Connection Flow**:
1. Connect to `ws://{host}:{port}/api/websocket`
2. Receive `auth_required` message
3. Send `auth` message with access token
4. Receive `auth_ok` or `auth_invalid`
5. Subscribe to entity state changes

**WebSocket Message Types**:

```kotlin
// Authentication
data class WebSocketAuthMessage(
    val type: String = "auth",
    val accessToken: String?
)

// Subscribe to state changes
data class WebSocketCommandMessage(
    val id: Int?,
    val type: String?, // "subscribe_events", "get_states", etc.
    val eventType: String? = null,
    val entityId: String? = null
)

// Result message
data class WebSocketResultMessage(
    val id: Int?,
    val type: String?, // "result", "event"
    val success: Boolean?,
    val result: Any?,
    val error: WebSocketError?
)
```

**Subscription Example**:
```kotlin
val wsClient = HomeAssistantWebSocketClient(serverUrl, token)
wsClient.connect()

// Subscribe to state changes
wsClient.subscribeToStateChanges { state ->
    println("Entity ${state.entityId} changed to ${state.state}")
}

// Subscribe to specific entity
wsClient.subscribeToEntity("light.living_room") { state ->
    updateUI(state)
}
```

### Data Models

#### Core Models

##### HomeAssistantConfig
```kotlin
data class HomeAssistantConfig(
    val latitude: Double?,
    val longitude: Double?,
    val elevation: Int?,
    val unitSystem: UnitSystem?,
    val locationName: String?,
    val timeZone: String?,
    val components: List<String>?,
    val version: String?,
    val state: String?, // "RUNNING", "STARTING", "STOPPING"
    val externalUrl: String?,
    val internalUrl: String?
)
```

##### HomeAssistantState
```kotlin
data class HomeAssistantState(
    val entityId: String?, // Format: domain.object_id (e.g., "light.living_room")
    val state: String?, // "on", "off", numeric value, or custom state
    val attributes: Map<String, Any>?, // Entity-specific attributes
    val lastChanged: String?, // ISO 8601 timestamp
    val lastUpdated: String?, // ISO 8601 timestamp
    val context: StateContext?
)
```

##### HomeAssistantService
```kotlin
data class HomeAssistantService(
    val name: String?,
    val description: String?,
    val fields: Map<String, ServiceField>?,
    val target: ServiceTarget?
)

data class ServiceField(
    val name: String?,
    val description: String?,
    val required: Boolean?,
    val example: Any?,
    val selector: Map<String, Any>? // UI selector type
)
```

##### ServiceCallRequest
```kotlin
data class ServiceCallRequest(
    val entityId: String? = null, // Target entity
    val areaId: String? = null, // Target area
    val deviceId: String? = null, // Target device
    val data: Map<String, Any>? = null // Service-specific data
)
```

#### Entity Domains

Home Assistant organizes entities by domain. Common domains include:

- **light**: Lighting control (brightness, color, effects)
- **switch**: Binary switches
- **sensor**: Read-only sensors (temperature, humidity, etc.)
- **binary_sensor**: Binary sensors (on/off, open/closed)
- **climate**: HVAC control
- **cover**: Window coverings, garage doors
- **lock**: Smart locks
- **media_player**: Media playback devices
- **camera**: Security cameras
- **alarm_control_panel**: Security systems
- **vacuum**: Robot vacuums
- **fan**: Fan control
- **scene**: Predefined scenes
- **automation**: Automation rules
- **script**: Scripts
- **group**: Entity groups
- **person**: Person tracking
- **device_tracker**: Device presence detection
- **zone**: Geographic zones
- **sun**: Sun position
- **weather**: Weather information
- **calendar**: Calendar integration
- **notify**: Notification services

### Sync Integration

HomeAssistantConnect integrates with the ShareConnect ecosystem via Asinka sync managers:

#### Sync Managers

1. **ThemeSyncManager** (Port 8890)
   - Syncs theme preferences across all ShareConnect apps
   - Supports 6 built-in themes + custom themes

2. **ProfileSyncManager** (Port 8900)
   - Syncs Home Assistant server profiles
   - Stores server URLs, access tokens, and configurations

3. **HistorySyncManager** (Port 8910)
   - Syncs automation trigger history
   - Tracks service calls and state changes

4. **RSSSyncManager** (Port 8920)
   - Syncs RSS feed subscriptions for Home Assistant blogs/updates

5. **BookmarkSyncManager** (Port 8930)
   - Syncs bookmarked entities, scenes, and automations

6. **PreferencesSyncManager** (Port 8940)
   - Syncs app preferences (update intervals, notifications, etc.)

7. **LanguageSyncManager** (Port 8950)
   - Syncs language/locale preferences

8. **TorrentSharingSyncManager** (Port 8960)
   - Syncs torrent-related data (if applicable)

**Sync Initialization Pattern**:
```kotlin
private fun initializeThemeSync() {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    themeSyncManager = ThemeSyncManager.getInstance(
        context = this,
        appId = packageName,
        appName = "HomeAssistantConnect",
        appVersion = packageInfo.versionName ?: "1.0.0"
    )

    applicationScope.launch {
        delay(100) // Stagger to avoid port conflicts
        themeSyncManager.start()
    }
}
```

### Security Features

#### 1. Authentication
- **Access Tokens**: Long-lived access tokens generated in Home Assistant
- **Token Storage**: Encrypted via Room + SQLCipher
- **Biometric Auth**: Optional biometric authentication via SecurityAccess module

#### 2. Network Security
- **HTTPS Support**: Full SSL/TLS support for secure connections
- **Certificate Pinning**: Optional certificate pinning for added security
- **Local Network Detection**: Prioritizes local URLs over external URLs

#### 3. Data Encryption
- **Database Encryption**: All local data encrypted with SQLCipher
- **Secure Storage**: Credentials stored in Android KeyStore when available

### UI Components

#### Dashboard
- Grid layout with customizable cards
- Entity status cards (lights, sensors, etc.)
- Scene activation cards
- Automation status indicators
- Real-time updates via WebSocket

#### Entity Management
- Filterable entity list by domain
- Search functionality
- Quick actions (toggle, adjust, etc.)
- Detailed entity view with attributes and history

#### Automation Management
- List view of all automations
- Enable/disable toggle
- Trigger count and last triggered timestamp
- Manual trigger support

#### Scene Activation
- Grid view of available scenes
- One-tap activation
- Visual feedback on activation

#### Settings
- Server configuration
- Update interval (5s, 10s, 30s, 1m, 5m)
- Theme selection
- Language selection
- Notification preferences
- Backup and restore
- About and version info

### Widget Support

HomeAssistantConnect provides home screen widgets via Glance:

#### Entity Status Widget
- Shows current state of selected entity
- Quick action button (toggle, etc.)
- Updates every 15 minutes or on manual refresh
- Configurable size (2x1, 2x2, 4x2)

**Implementation**:
```kotlin
@Composable
fun HomeAssistantWidget() {
    GlanceAppWidget {
        val entityState = currentState<HomeAssistantState>()

        Column(
            modifier = GlanceModifier.fillMaxSize().padding(16.dp)
        ) {
            Text(entityState.entityId ?: "Unknown")
            Text(entityState.state ?: "Unknown", style = TextStyle(fontSize = 24.sp))

            Button(
                text = "Toggle",
                onClick = actionStartActivity<MainActivity>()
            )
        }
    }
}
```

### Testing

#### Unit Tests (37 tests)

Located in `src/test/kotlin/`:
- **HomeAssistantApiClientMockKTest.kt** (20 tests)
  - API endpoint testing with MockK
  - Request/response validation
  - Error handling
  - Authentication flows

- **HomeAssistantWebSocketMessagesTest.kt** (17 tests)
  - WebSocket message serialization
  - Message type validation
  - Authentication flow
  - Event handling

**Running Unit Tests**:
```bash
./gradlew :HomeAssistantConnector:test
```

#### Integration Tests (29 tests)

Located in `src/androidTest/kotlin/`:
- **HomeAssistantIntegrationTest.kt** (29 tests)
  - Full API workflow testing with MockWebServer
  - Service call integration
  - State update workflows
  - History and logbook
  - Calendar integration
  - Template rendering
  - Error handling and recovery
  - Concurrent request handling
  - Network failure scenarios

**Running Integration Tests**:
```bash
./gradlew :HomeAssistantConnector:connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.homeassistantconnect.HomeAssistantIntegrationTest
```

#### Automation Tests (25 tests)

Located in `src/androidTest/kotlin/`:
- **HomeAssistantConnectAutomationTest.kt** (25 tests)
  - End-to-end UI flows
  - Server connection workflows
  - Entity management flows
  - Dashboard customization
  - Automation and scene activation
  - Settings configuration
  - Error handling and recovery
  - Multi-server support
  - Accessibility compliance
  - Data persistence
  - Complete user journey

**Running Automation Tests**:
```bash
./gradlew :HomeAssistantConnector:connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.homeassistantconnect.HomeAssistantConnectAutomationTest
```

#### Total Test Coverage: 91 Tests
- Unit: 37 tests
- Integration: 29 tests
- Automation: 25 tests

### Build Configuration

#### Dependencies

```gradle
dependencies {
    // ShareConnect modules
    implementation project(':DesignSystem')
    implementation project(':Onboarding')
    implementation project(':Localizations')
    implementation project(':Toolkit:SecurityAccess')
    implementation project(':Toolkit:WebSocket')

    // Sync modules
    implementation project(':ThemeSync')
    implementation project(':ProfileSync')
    implementation project(':HistorySync')
    implementation project(':RSSSync')
    implementation project(':BookmarkSync')
    implementation project(':PreferencesSync')
    implementation project(':LanguageSync')
    implementation project(':TorrentSharingSync')
    implementation project(':Asinka:asinka')

    // Compose
    implementation platform('androidx.compose:compose-bom:2025.09.00')
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.activity:activity-compose'
    implementation 'androidx.navigation:navigation-compose'

    // Networking
    implementation 'com.squareup.retrofit2:retrofit:2.11.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.11.0'
    implementation 'com.squareup.okhttp3:okhttp:4.12.0'
    implementation 'com.squareup.okhttp3:logging-interceptor:4.12.0'

    // Room Database
    implementation 'androidx.room:room-runtime:2.7.0-alpha07'
    implementation 'androidx.room:room-ktx:2.7.0-alpha07'
    ksp 'androidx.room:room-compiler:2.7.0-alpha07'

    // Glance widgets
    implementation 'androidx.glance:glance-appwidget:1.1.1'
    implementation 'androidx.glance:glance-material3:1.1.1'

    // Coroutines
    implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2'
    implementation 'org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3'
}
```

#### ProGuard Rules

```proguard
# Home Assistant API models
-keep class com.shareconnect.homeassistantconnect.data.models.** { *; }

# Retrofit
-keepattributes Signature, InnerClasses, EnclosingMethod
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# OkHttp
-dontwarn okhttp3.**
-dontwarn okio.**

# WebSocket
-keep class com.shareconnect.homeassistantconnect.data.websocket.** { *; }

# Gson
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.google.gson.** { *; }
-keep class * implements com.google.gson.TypeAdapter
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer
```

### Performance Optimizations

1. **Lazy Loading**: Entities loaded on-demand with pagination
2. **Caching**: Local cache for frequently accessed states
3. **Debouncing**: Service calls debounced to prevent rapid-fire requests
4. **WebSocket Reconnection**: Exponential backoff for reconnection attempts
5. **Image Loading**: Coil for efficient image loading with caching
6. **Database Indexing**: Indexes on entity_id and last_updated for fast queries

### Known Limitations

1. **WebSocket Reconnection**: May take up to 30 seconds to reconnect after network changes
2. **Large Installations**: Performance may degrade with 500+ entities
3. **Image Caching**: Camera images are not cached due to privacy concerns
4. **Background Sync**: Limited by Android's background execution limits (Doze mode)

### API Compatibility

- **Home Assistant Version**: 2024.1.0 and later
- **REST API**: Stable API v1
- **WebSocket API**: Stable WebSocket API

### Troubleshooting

#### Connection Issues
- Verify Home Assistant is accessible on the network
- Check that the access token is valid and not expired
- Ensure firewall allows connections on port 8123
- Try using IP address instead of hostname

#### WebSocket Disconnects
- Check for network instability
- Verify Home Assistant hasn't restarted
- Check Android battery optimization settings

#### Sync Issues
- Verify all ShareConnect apps are on the same network
- Check that gRPC ports (8890-8960) are not blocked
- Restart all ShareConnect apps to reset sync state

### Development Guide

#### Adding New API Endpoints

1. Add model in `HomeAssistantModels.kt`
2. Add endpoint in `HomeAssistantApiService.kt`
3. Add method in `HomeAssistantApiClient.kt`
4. Add unit test in `HomeAssistantApiClientMockKTest.kt`
5. Add integration test in `HomeAssistantIntegrationTest.kt`

**Example**:
```kotlin
// 1. Model
data class HomeAssistantZone(
    val zoneId: String?,
    val name: String?,
    val latitude: Double?,
    val longitude: Double?,
    val radius: Int?
)

// 2. Service
@GET
suspend fun getZones(
    @Url url: String,
    @Header("Authorization") auth: String
): Response<List<HomeAssistantZone>>

// 3. Client method
suspend fun getZones(): Result<List<HomeAssistantZone>> = withContext(Dispatchers.IO) {
    try {
        val response = service.getZones(
            url = buildUrl("api/zones"),
            auth = authHeader
        )
        if (response.isSuccessful) {
            Result.success(response.body() ?: emptyList())
        } else {
            Result.failure(Exception("Get zones failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

### Contributing

1. Follow Kotlin coding standards
2. Add comprehensive unit tests for all new features
3. Update documentation for API changes
4. Ensure all tests pass before submitting PR
5. Follow ShareConnect architectural patterns

### License

Part of the ShareConnect project. See main project LICENSE for details.

### Support

For issues and questions:
- GitHub Issues: [ShareConnect Repository]
- Wiki: https://deepwiki.com/vasic-digital/ShareConnect
- Documentation: See `HomeAssistantConnect_User_Manual.md` for user-facing documentation

### Version History

#### 1.0.0 (2025-01-25)
- Initial release
- Full Home Assistant REST API support
- WebSocket real-time updates
- Dashboard customization
- Entity management
- Automation and scene support
- Widget support
- ShareConnect ecosystem integration
- 91 comprehensive tests
