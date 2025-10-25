# NetdataConnect - System Monitoring Connector

## Overview

**NetdataConnect** is a comprehensive Android connector application for the ShareConnect ecosystem that provides real-time system monitoring and metrics visualization through integration with Netdata servers.

Netdata is a powerful, distributed, real-time performance and health monitoring system for systems and applications. NetdataConnect brings this monitoring capability to Android devices with a modern, Material Design 3 interface.

**Package**: `com.shareconnect.netdataconnect`
**Version**: 1.0.0
**Min SDK**: 28
**Target SDK**: 36
**Netdata API**: v1 and v2

---

## Table of Contents

1. [Architecture](#architecture)
2. [Features](#features)
3. [Build Instructions](#build-instructions)
4. [API Integration](#api-integration)
5. [Data Models](#data-models)
6. [Testing](#testing)
7. [Security](#security)
8. [Development Guidelines](#development-guidelines)
9. [Troubleshooting](#troubleshooting)
10. [API Reference](#api-reference)

---

## Architecture

### Application Structure

NetdataConnect follows the ShareConnect multi-application architecture pattern with full Asinka synchronization support:

```
NetdataConnect/
├── NetdataConnector/          # Main application module
│   ├── src/main/
│   │   ├── kotlin/
│   │   │   ├── data/
│   │   │   │   ├── api/       # Netdata API client
│   │   │   │   ├── models/    # Data models
│   │   │   │   └── realtime/  # Real-time streaming
│   │   │   ├── ui/            # Compose UI
│   │   │   └── widget/        # App widgets
│   │   └── res/               # Resources
│   ├── src/test/              # Unit tests (22 tests)
│   └── src/androidTest/       # Integration (16) + Automation (7)
└── build.gradle               # Module configuration
```

### Key Components

#### 1. API Layer (`data/api/`)

**NetdataApiService.kt** - Retrofit interface defining all Netdata API endpoints:
- `/api/v1/info` - Server information
- `/api/v1/charts` - Available charts and metrics
- `/api/v1/data` - Historical and real-time chart data
- `/api/v1/alarms` - Active alarms and alerts
- `/api/v1/alarm_log` - Alarm history
- `/api/v1/allmetrics` - Complete metrics snapshot
- `/api/v1/badge.svg` - Badge generation
- `/api/v1/contexts` - Chart groupings
- `/api/v2/functions` - Available functions
- `/api/v1/weights` - Chart priorities

**NetdataApiClient.kt** - API client with `Result<T>` error handling:
```kotlin
class NetdataApiClient(
    private val serverUrl: String,
    netdataApiService: NetdataApiService? = null
) {
    suspend fun getInfo(): Result<NetdataInfo>
    suspend fun getCharts(): Result<NetdataCharts>
    suspend fun getData(
        chart: String,
        after: Long? = null,
        before: Long? = null,
        points: Int? = null,
        group: String? = "average",
        gtime: Int? = null,
        options: String? = null,
        dimensions: String? = null,
        format: String? = "json"
    ): Result<NetdataChartData>
    suspend fun getAlarms(all: Boolean? = null): Result<NetdataAlarms>
    suspend fun getAlarmLog(after: Long? = null, alarm: String? = null): Result<List<NetdataAlarm>>
    suspend fun getAllMetrics(...): Result<NetdataAllMetrics>
    suspend fun getBadge(...): Result<String>
    suspend fun getContexts(): Result<NetdataContexts>
    suspend fun getFunctions(chart: String): Result<NetdataFunctions>
    suspend fun getNode(): Result<NetdataNode>
    suspend fun getWeights(): Result<NetdataWeights>
}
```

#### 2. Data Models (`data/models/`)

**NetdataModels.kt** - Comprehensive data classes covering all Netdata API responses:

- **Server Information**: `NetdataInfo`, `NetdataNode`
- **Charts**: `NetdataCharts`, `NetdataChart`, `NetdataDimension`
- **Data**: `NetdataChartData`, `NetdataAllMetrics`, `NetdataMetricChart`
- **Alarms**: `NetdataAlarms`, `NetdataAlarm`, `NetdataHealth`
- **Metadata**: `NetdataContexts`, `NetdataContext`, `NetdataFunctions`, `NetdataWeights`

All models use Gson serialization with `@SerializedName` annotations for API compatibility.

#### 3. Real-time Streaming (`data/realtime/`)

**NetdataRealtimeClient.kt** - WebSocket-based real-time metric streaming:
- Server-Sent Events (SSE) support
- Automatic reconnection
- Metric update callbacks
- Connection state management

**NetdataRealtimeMessages.kt** - Real-time message types:
- Metric updates
- Alarm notifications
- Chart data streams

#### 4. UI Layer (`ui/`)

**MainActivity.kt** - Main entry point with SecurityAccess integration:
```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var securityViewModel: SecurityAccessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityViewModel = SecurityAccessViewModel(application)

        setContent {
            SecurityAccess(
                viewModel = securityViewModel,
                pinLength = PinLength.FOUR,
                onAuthenticationSuccess = { }
            ) {
                NetdataConnectContent()
            }
        }
    }
}
```

**NetdataDashboard.kt** - Main dashboard UI
**NetdataChartsScreen.kt** - Chart visualization

#### 5. Widgets (`widget/`)

**NetdataWidget.kt** - Glance-based app widget for home screen metrics
**WidgetUpdateWorker.kt** - Background metric updates using WorkManager

#### 6. Sync Integration

NetdataConnect integrates with all ShareConnect sync modules:
- **ThemeSync** (port 8890) - Theme preferences
- **ProfileSync** (port 8900) - Server profiles
- **HistorySync** (port 8910) - Monitoring history
- **RSSSync** (port 8920) - RSS feeds
- **BookmarkSync** (port 8930) - Bookmarks
- **PreferencesSync** (port 8940) - App preferences
- **LanguageSync** (port 8950) - Language settings
- **TorrentSharingSync** (port 8960) - Torrent sharing

All sync managers use Asinka for real-time synchronization across ShareConnect apps.

---

## Features

### Core Capabilities

1. **Real-time Metrics Monitoring**
   - Live system metrics (CPU, RAM, disk, network)
   - Chart updates with configurable intervals
   - Historical data visualization
   - Multi-dimensional metric support

2. **Comprehensive Chart Support**
   - 1000+ built-in charts from Netdata
   - System, application, and service metrics
   - Custom chart groupings (contexts)
   - Interactive chart visualization

3. **Alarm Management**
   - Active alarm monitoring
   - Alarm history tracking
   - Configurable alarm notifications
   - Severity levels (CLEAR, WARNING, CRITICAL)

4. **Server Health Monitoring**
   - Overall health status
   - Resource usage tracking
   - Performance metrics
   - System information

5. **Multi-Server Support**
   - Manage multiple Netdata instances
   - Server profiles synchronized via ProfileSync
   - Quick server switching
   - Per-server configurations

6. **Advanced Features**
   - Badge generation for simple metrics
   - Prometheus format export
   - Chart weight/priority management
   - Function execution (v2 API)

### UI Features

- **Material Design 3** with dynamic theming
- **SecurityAccess** PIN protection
- **Responsive layouts** for all screen sizes
- **Dark mode** support
- **Home screen widgets** for quick metrics

---

## Build Instructions

### Prerequisites

- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 36
- Kotlin 2.0.0

### Building

```bash
# Navigate to project directory
cd /path/to/ShareConnect/Connectors/NetdataConnect

# Build debug APK
./gradlew assembleDebug

# Build release APK
./gradlew assembleRelease

# Clean build
./gradlew clean
```

**Output**: `NetdataConnector/build/outputs/apk/debug/NetdataConnector-debug.apk`

### Build Configuration

**build.gradle** highlights:
```gradle
android {
    namespace 'com.shareconnect.netdataconnect'
    compileSdk 36

    defaultConfig {
        applicationId 'com.shareconnect.netdataconnect'
        minSdk 28
        targetSdk 36
        versionCode 1
        versionName '1.0.0'
    }

    compileOptions {
        sourceCompatibility JavaVersion.VERSION_17
        targetCompatibility JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = '17'
    }
}

dependencies {
    // ShareConnect modules
    implementation project(':Asinka:asinka')
    implementation project(':DesignSystem')
    implementation project(':Onboarding')
    implementation project(':Localizations')

    // Sync modules
    implementation project(':ThemeSync')
    implementation project(':ProfileSync')
    implementation project(':HistorySync')
    // ... all sync modules

    // Security
    implementation project(':Toolkit:SecurityAccess')

    // Networking
    implementation "com.squareup.retrofit2:retrofit:2.11.0"
    implementation "com.squareup.retrofit2:converter-gson:2.11.0"
    implementation "com.squareup.okhttp3:okhttp:4.12.0"

    // Compose
    implementation platform('androidx.compose:compose-bom:2025.09.00')
    implementation 'androidx.compose.material3:material3'
    implementation 'androidx.compose.ui:ui'

    // Widgets
    implementation "androidx.glance:glance-appwidget:1.1.1"
}
```

---

## API Integration

### Netdata API Overview

Netdata exposes a comprehensive REST API for accessing all monitoring data. NetdataConnect supports both v1 and v2 API endpoints.

**Base URL Format**: `http://[server-ip]:19999`

### API Client Usage

#### 1. Initialize Client

```kotlin
val apiClient = NetdataApiClient(
    serverUrl = "http://192.168.1.100:19999"
)
```

#### 2. Get Server Information

```kotlin
viewModelScope.launch {
    val result = apiClient.getInfo()
    result.onSuccess { info ->
        println("Netdata version: ${info.version}")
        println("OS: ${info.osName}")
        println("CPU cores: ${info.coresTotal}")
        println("RAM: ${info.ramTotal}")
    }.onFailure { error ->
        println("Error: ${error.message}")
    }
}
```

#### 3. Get Available Charts

```kotlin
val result = apiClient.getCharts()
result.onSuccess { charts ->
    charts.charts?.forEach { (chartId, chart) ->
        println("${chart.title}: ${chart.units}")
        chart.dimensions?.forEach { (dimId, dimension) ->
            println("  - ${dimension.name}")
        }
    }
}
```

#### 4. Get Chart Data

```kotlin
// Get last 60 seconds of CPU data
val result = apiClient.getData(
    chart = "system.cpu",
    after = -60,  // 60 seconds ago
    points = 60,
    group = "average"
)

result.onSuccess { data ->
    data.labels?.forEachIndexed { index, label ->
        val values = data.data?.map { it[index] }
        println("$label: $values")
    }
}
```

#### 5. Monitor Alarms

```kotlin
val result = apiClient.getAlarms()
result.onSuccess { alarms ->
    alarms.alarms?.forEach { (name, alarm) ->
        if (alarm.active == true) {
            println("ALARM: ${alarm.name} - ${alarm.status}")
            println("Value: ${alarm.value} ${alarm.units}")
            println("Info: ${alarm.info}")
        }
    }
}
```

#### 6. Get All Metrics Snapshot

```kotlin
val result = apiClient.getAllMetrics()
result.onSuccess { metrics ->
    println("Health: ${metrics.health?.status}")
    println("Critical: ${metrics.health?.critical}")
    println("Warning: ${metrics.health?.warning}")

    metrics.charts?.forEach { (chartId, chart) ->
        chart.dimensions?.forEach { (dimId, dimension) ->
            println("${chart.name}.${dimension.name} = ${dimension.value}")
        }
    }
}
```

### Chart Data Parameters

The `getData()` method supports extensive filtering and aggregation:

- **after**: Start time (unix timestamp or negative for relative seconds)
- **before**: End time (unix timestamp)
- **points**: Number of data points to return
- **group**: Aggregation method
  - `average` - Average values
  - `max` - Maximum values
  - `min` - Minimum values
  - `sum` - Sum of values
- **gtime**: Group time in seconds
- **options**: Additional options
  - `null2zero` - Convert null to zero
  - `percentage` - Show as percentage
  - `jsonwrap` - Wrap response in JSON
- **dimensions**: Comma-separated dimension filter
- **format**: Response format (`json`, `csv`, `datasource`)

### Error Handling

All API methods return `Result<T>` for consistent error handling:

```kotlin
result.onSuccess { data ->
    // Process successful response
}.onFailure { exception ->
    when (exception) {
        is IOException -> {
            // Network error
        }
        is HttpException -> {
            // HTTP error (4xx, 5xx)
        }
        else -> {
            // Other errors
        }
    }
}
```

---

## Data Models

### Server Information Models

**NetdataInfo** - Complete server information:
```kotlin
data class NetdataInfo(
    val version: String?,           // Netdata version (e.g., "v1.42.0")
    val uid: String?,              // Unique server ID
    val timezone: String?,         // Server timezone
    val osName: String?,           // OS name (e.g., "Ubuntu")
    val osId: String?,             // OS ID
    val osVersion: String?,        // OS version
    val coresTotal: Int?,          // CPU core count
    val totalDiskSpace: Long?,     // Total disk space in bytes
    val cpuFreq: String?,          // CPU frequency
    val ramTotal: Long?,           // Total RAM in bytes
    val updateEvery: Int?,         // Update interval in seconds
    val history: Int?,             // History retention in seconds
    val memoryMode: String?,       // Memory mode (ram, save, map, dbengine)
    val hostLabels: Map<String, String>?  // Custom host labels
)
```

### Chart Models

**NetdataChart** - Chart definition:
```kotlin
data class NetdataChart(
    val id: String?,               // Chart ID (e.g., "system.cpu")
    val name: String?,             // Chart name
    val type: String?,             // Chart type
    val family: String?,           // Chart family/group
    val context: String?,          // Chart context
    val title: String?,            // Human-readable title
    val priority: Int?,            // Display priority
    val plugin: String?,           // Data source plugin
    val module: String?,           // Plugin module
    val enabled: Boolean?,         // Chart enabled status
    val units: String?,            // Unit of measurement
    val chartType: String?,        // Visualization type (line, area, stacked)
    val updateEvery: Int?,         // Update frequency in seconds
    val dimensions: Map<String, NetdataDimension>?,  // Chart dimensions
    val green: Double?,            // Green threshold
    val red: Double?               // Red threshold
)
```

**NetdataDimension** - Chart dimension (metric):
```kotlin
data class NetdataDimension(
    val name: String?,             // Dimension name
    val algorithm: String?,        // Calculation algorithm
    val multiplier: Int?,          // Value multiplier
    val divisor: Int?,             // Value divisor
    val hidden: Boolean?           // Hidden from display
)
```

### Alarm Models

**NetdataAlarm** - Alarm/alert information:
```kotlin
data class NetdataAlarm(
    val id: Long?,                 // Alarm ID
    val status: String?,           // Status (CLEAR, WARNING, CRITICAL)
    val name: String?,             // Alarm name
    val chart: String?,            // Associated chart
    val family: String?,           // Chart family
    val active: Boolean?,          // Currently active
    val disabled: Boolean?,        // Alarm disabled
    val silenced: Boolean?,        // Alarm silenced
    val units: String?,            // Value units
    val info: String?,             // Alarm description
    val value: Double?,            // Current value
    val lastStatusChange: Long?,   // Last status change timestamp
    val lastUpdated: Long?,        // Last update timestamp
    val updateEvery: Int?          // Update frequency
)
```

### Health Status

**NetdataHealth** - Overall health summary:
```kotlin
data class NetdataHealth(
    val status: String?,           // Overall status
    val critical: Int?,            // Critical alarm count
    val warning: Int?,             // Warning alarm count
    val undefined: Int?,           // Undefined alarm count
    val uninitialized: Int?,       // Uninitialized alarm count
    val clear: Int?                // Clear alarm count
)
```

---

## Testing

NetdataConnect includes a comprehensive test suite with 45+ tests covering all functionality.

### Test Structure

```
src/
├── test/                          # Unit tests (22 tests)
│   └── kotlin/
│       └── com/shareconnect/netdataconnect/
│           ├── TestApplication.kt
│           └── data/api/
│               └── NetdataApiClientMockKTest.kt
└── androidTest/                   # Integration + Automation (23 tests)
    └── kotlin/
        └── com/shareconnect/netdataconnect/
            ├── integration/
            │   └── NetdataApiClientIntegrationTest.kt  # 16 tests
            └── automation/
                └── NetdataConnectAutomationTest.kt     # 7 tests
```

### Running Tests

#### All Tests
```bash
# Run all tests (unit + integration + automation)
./gradlew test connectedAndroidTest
```

#### Unit Tests Only
```bash
./gradlew test
```

#### Integration Tests
```bash
./gradlew connectedAndroidTest
```

#### Specific Test Class
```bash
./gradlew test --tests "NetdataApiClientMockKTest"
```

#### Specific Test Method
```bash
./gradlew test --tests "NetdataApiClientMockKTest.test getInfo success"
```

### Unit Test Coverage

**NetdataApiClientMockKTest.kt** (22 tests):
- Server information retrieval (2 tests)
- Charts retrieval (1 test)
- Chart data retrieval (2 tests)
- Alarms retrieval (3 tests)
- Alarm log retrieval (1 test)
- All metrics retrieval (2 tests)
- Badge generation (2 tests)
- Contexts retrieval (1 test)
- Functions retrieval (1 test)
- Node information (1 test)
- Weights retrieval (1 test)
- Error handling (4 tests)

### Integration Test Coverage

**NetdataApiClientIntegrationTest.kt** (16 tests):
- Real API communication with MockWebServer
- JSON serialization/deserialization
- HTTP error handling (401, 404, 500)
- Malformed response handling
- Complex data structures
- Parameter passing

### Automation Test Coverage

**NetdataConnectAutomationTest.kt** (7 tests):
- App launch verification
- UI element visibility
- SecurityAccess integration
- User interaction response
- Feature list display
- Accessibility compliance

### Test Best Practices

1. **Use MockK for unit tests** - Fast, reliable mocking
2. **Use MockWebServer for integration tests** - Test actual network behavior
3. **Use Compose Test for automation** - UI testing with Jetpack Compose
4. **Test error scenarios** - Network failures, malformed data
5. **Verify thread safety** - Coroutine testing with `runTest`
6. **Test edge cases** - Empty data, null values, large datasets

---

## Security

### SecurityAccess Integration

NetdataConnect uses the SecurityAccess library for PIN-based authentication:

```kotlin
SecurityAccess(
    viewModel = securityViewModel,
    pinLength = PinLength.FOUR,
    onAuthenticationSuccess = {
        // User authenticated successfully
    }
) {
    // Protected content
}
```

Features:
- 4-digit PIN protection
- Biometric authentication support
- Auto-lock after inactivity
- PIN change capability

### Data Protection

1. **Encrypted Sync** - All Asinka synchronization uses SQLCipher encryption
2. **Secure Storage** - Server credentials encrypted in Room database
3. **HTTPS Support** - SSL/TLS for API communication
4. **Network Security Config** - Custom certificate trust

### Permissions

Required permissions in AndroidManifest.xml:
```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.WAKE_LOCK" />
<uses-permission android:name="android.permission.FOREGROUND_SERVICE" />
```

---

## Development Guidelines

### Code Style

- **Kotlin first** - Use Kotlin for all new code
- **Coroutines** - Async operations with structured concurrency
- **Compose** - Jetpack Compose for UI
- **Result<T>** - Consistent error handling
- **Data classes** - Immutable data models

### Architecture Patterns

1. **Repository Pattern** - Centralized data access
2. **ViewModel Pattern** - UI state management
3. **Dependency Injection** - Manual DI with lazy initialization
4. **Clean Architecture** - Separation of concerns

### Adding New API Endpoints

1. Add method to `NetdataApiService` interface
2. Add corresponding method to `NetdataApiClient`
3. Create/update data models
4. Add unit tests
5. Add integration tests
6. Update documentation

Example:
```kotlin
// 1. NetdataApiService.kt
@GET("api/v1/custom-endpoint")
suspend fun getCustomData(): Response<CustomData>

// 2. NetdataApiClient.kt
suspend fun getCustomData(): Result<CustomData> {
    return try {
        val response = service.getCustomData()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(Exception("Failed: ${response.code()}"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

// 3. NetdataModels.kt
data class CustomData(
    @SerializedName("field") val field: String?
)
```

### Sync Manager Integration

All sync managers follow this pattern:
```kotlin
private fun initializeThemeSync() {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    themeSyncManager = ThemeSyncManager.getInstance(
        context = this,
        appId = packageName,
        appName = "NetdataConnect",
        appVersion = packageInfo.versionName ?: "1.0.0"
    )

    applicationScope.launch {
        delay(100) // Prevent port conflicts
        themeSyncManager.start()
    }
}
```

---

## Troubleshooting

### Common Issues

#### Build Failures

**Issue**: Kotlin version conflicts
```
Execution failed for task ':NetdataConnector:compileDebugKotlin'
```

**Solution**: Force Kotlin version in build.gradle:
```gradle
configurations.all {
    resolutionStrategy {
        force 'org.jetbrains.kotlin:kotlin-stdlib:2.0.0'
    }
}
```

#### Port Binding Errors

**Issue**: gRPC port already in use
```
java.net.BindException: Address already in use
```

**Solution**: Each sync manager uses unique port with automatic fallback. Increase delay between initializations:
```kotlin
delay(100) // Increase to 200-300ms if needed
```

#### Test Failures

**Issue**: Robolectric tests fail with AndroidKeyStore errors

**Solution**: Use TestApplication that skips sync initialization:
```kotlin
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = TestApplication::class)
class MyTest { ... }
```

### Network Issues

**Self-signed certificates**:
```xml
<!-- res/xml/network_security_config.xml -->
<network-security-config>
    <domain-config cleartextTrafficPermitted="true">
        <domain includeSubdomains="true">192.168.1.0/24</domain>
    </domain-config>
</network-security-config>
```

**Connection timeouts**:
```kotlin
val client = OkHttpClient.Builder()
    .connectTimeout(30, TimeUnit.SECONDS)
    .readTimeout(30, TimeUnit.SECONDS)
    .build()
```

---

## API Reference

### Complete Endpoint List

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/v1/info` | GET | Server information and version |
| `/api/v1/charts` | GET | All available charts |
| `/api/v1/data` | GET | Chart data with filtering |
| `/api/v1/alarms` | GET | Active alarms |
| `/api/v1/alarm_log` | GET | Alarm history |
| `/api/v1/allmetrics` | GET | All metrics snapshot |
| `/api/v1/badge.svg` | GET | Badge SVG for metric |
| `/api/v1/contexts` | GET | Chart contexts/groupings |
| `/api/v1/weights` | GET | Chart priorities |
| `/api/v2/functions` | GET | Available functions |

### Response Codes

- `200` - Success
- `400` - Bad request (invalid parameters)
- `401` - Unauthorized (if authentication enabled)
- `404` - Not found (invalid chart/endpoint)
- `500` - Internal server error

### Common Chart IDs

- `system.cpu` - Total CPU utilization
- `system.ram` - System RAM usage
- `system.swap` - Swap usage
- `disk.space` - Disk space usage
- `disk.io` - Disk I/O
- `net.eth0` - Network interface traffic
- `system.load` - System load average
- `system.uptime` - System uptime

---

## Additional Resources

- **Netdata Documentation**: https://learn.netdata.cloud/
- **Netdata API Reference**: https://learn.netdata.cloud/docs/netdata-agent/netdata-api
- **ShareConnect Wiki**: https://deepwiki.com/vasic-digital/ShareConnect
- **Source Code**: `/ShareConnect/Connectors/NetdataConnect/`

---

## License

NetdataConnect is part of the ShareConnect ecosystem.
Copyright © 2024 Vasic Digital. All rights reserved.
