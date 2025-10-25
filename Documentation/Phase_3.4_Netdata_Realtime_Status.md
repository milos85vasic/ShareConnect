# Phase 3.4: NetdataConnect Real-time Implementation - Status Report

**Status**: ✅ COMPLETE
**Date**: October 25, 2025
**Implementation Time**: ~2 hours

## Overview

Phase 3.4 successfully implements real-time updates for Netdata monitoring server using HTTP polling wrapped in the WebSocket message framework. Since Netdata does not have native WebSocket support, this implementation uses periodic HTTP polling to provide real-time metric updates, alarm notifications, and health status monitoring.

## Technical Approach

**Key Design Decision**: Netdata uses HTTP polling rather than WebSocket protocol. This implementation creates a hybrid approach that:
- Uses HTTP polling to fetch data from Netdata REST API
- Wraps responses in WebSocket message format for consistency
- Provides the same callback-based subscription pattern as other connectors
- Maintains architectural consistency across Phase 3

## Components Implemented

### 1. Netdata Real-time Messages (`NetdataRealtimeMessages.kt`)

Comprehensive message types representing polling-based updates:

#### Message Types (9 total)

1. **MetricsUpdateMessage** - Current metric values from all charts
2. **AlarmUpdateMessage** - Alarm status with severity counts
3. **ChartDataUpdateMessage** - Time series data points
4. **ServerInfoUpdateMessage** - Server metadata and statistics
5. **HealthStatusUpdateMessage** - Overall system health summary
6. **BadgeUpdateMessage** - Simple metric snapshots
7. **ConnectionStatusMessage** - Polling connection state
8. **PollingIntervalMessage** - Update frequency configuration
9. **ErrorMessage** - Error reporting with codes

#### Key Features

- **Nested Data Structures**: ChartMetrics, AlarmStatus, detailed chart data
- **Gson-based Parsing**: JSON deserialization with null safety
- **Comprehensive Parser**: `NetdataMessageParser` handles all message types
- **Timestamp Tracking**: All messages include creation timestamps

### 2. Netdata Real-time Client (`NetdataRealtimeClient.kt`)

High-level polling client for Netdata monitoring:

#### Core Functionality

```kotlin
class NetdataRealtimeClient(
    serverUrl: String,
    scope: CoroutineScope
)
```

**Features**:
- Periodic HTTP polling (default: 2 seconds)
- Separate polling jobs for metrics and alarms
- Configurable poll interval (500ms - 60s)
- StateFlow for reactive state management
- Subscription-based callbacks

#### Public API

**Connection Management**:
- `suspend fun connect()` - Start polling
- `suspend fun disconnect()` - Stop polling
- `fun isConnected(): Boolean` - Polling status
- `fun setPollingInterval(intervalMs: Long)` - Adjust update frequency

**State Flows**:
- `val connected: StateFlow<Boolean>` - Polling state
- `val healthStatus: StateFlow<HealthStatusUpdateMessage?>` - System health
- `val alarms: StateFlow<List<AlarmStatus>>` - Active alarms
- `val serverInfo: StateFlow<ServerInfoUpdateMessage?>` - Server metadata

**Data Fetching**:
- `suspend fun fetchChartData(chartId: String, points: Int = 60)` - Get specific chart

**Event Subscriptions**:
- `fun subscribeToMetrics(callback: (MetricsUpdateMessage) -> Unit)`
- `fun subscribeToAlarms(callback: (AlarmUpdateMessage) -> Unit)`
- `fun subscribeToChartData(callback: (ChartDataUpdateMessage) -> Unit)`
- `fun subscribeToHealthStatus(callback: (HealthStatusUpdateMessage) -> Unit)`
- `fun subscribeToConnectionStatus(callback: (ConnectionStatusMessage) -> Unit)`

### 3. Unit Tests (`NetdataRealtimeMessagesTest.kt`)

**Test Coverage**: 24 tests, 100% passing

#### Test Categories

**Message Creation Tests** (6 tests):
- MetricsUpdateMessage
- AlarmUpdateMessage
- ChartDataUpdateMessage
- ServerInfoUpdateMessage
- HealthStatusUpdateMessage
- BadgeUpdateMessage

**Serialization Tests** (4 tests):
- ConnectionStatusMessage with/without error
- PollingIntervalMessage
- ErrorMessage with/without error code

**Parsing Tests - Message Types** (11 tests):
- MetricsUpdateMessage
- AlarmUpdateMessage (with multiple alarms)
- ChartDataUpdateMessage
- ServerInfoUpdateMessage
- HealthStatusUpdateMessage
- BadgeUpdateMessage
- ConnectionStatusMessage
- PollingIntervalMessage
- ErrorMessage

**Error Handling Tests** (3 tests):
- Invalid JSON
- Unknown message type
- Message without type field

## Test Results

```
Test Suite: NetdataRealtimeMessagesTest
Tests: 24
Failures: 0
Errors: 0
Skipped: 0
Time: 0.463s
Success Rate: 100%
```

### Test Execution Details

```bash
./gradlew :NetdataConnector:testDebugUnitTest
BUILD SUCCESSFUL in 8s
302 actionable tasks: 4 executed, 298 up-to-date
```

## Technical Implementation

### Polling Architecture

**Dual Polling Strategy**:
1. **Metrics Polling**: Fetches all metrics every `pollIntervalMs`
2. **Alarm Polling**: Fetches alarms every `pollIntervalMs * 2` (less frequent)

**Flow**:
```
connect()
  → startMetricsPolling() [Job 1]
  → startAlarmPolling()   [Job 2]
  → fetchServerInfo()     [Initial]

Metrics Loop:
  → getAllMetrics() from API
  → Convert to MetricsUpdateMessage
  → Update healthStatus StateFlow
  → Notify callbacks

Alarm Loop:
  → getAlarms() from API
  → Convert to AlarmUpdateMessage
  → Update alarms StateFlow
  → Notify callbacks
```

### Data Conversion

The client converts Netdata REST API responses to message format:

**Metrics Conversion**:
```kotlin
NetdataAllMetrics (API) → MetricsUpdateMessage
{
  charts: Map<String, NetdataMetricChart>
  health: NetdataHealth
}
→
{
  charts: Map<String, ChartMetrics>
  healthStatus: HealthStatusUpdateMessage
}
```

**Alarms Conversion**:
```kotlin
NetdataAlarms (API) → AlarmUpdateMessage
{
  alarms: Map<String, NetdataAlarm>
}
→
{
  alarms: List<AlarmStatus>
  criticalCount: Int
  warningCount: Int
  clearCount: Int
}
```

### Message Structure Examples

**MetricsUpdateMessage**:
```json
{
  "type": "metrics_update",
  "timestamp": 1234567890,
  "charts": {
    "system.cpu": {
      "chartId": "system.cpu",
      "dimensions": {
        "user": 45.5,
        "system": 15.2,
        "idle": 39.3
      },
      "last_updated": 1234567890
    }
  }
}
```

**AlarmUpdateMessage**:
```json
{
  "type": "alarm_update",
  "timestamp": 1234567890,
  "critical_count": 1,
  "warning_count": 2,
  "clear_count": 5,
  "alarms": [
    {
      "id": 1,
      "name": "cpu_usage",
      "chart": "system.cpu",
      "status": "CRITICAL",
      "value": 95.5,
      "units": "%",
      "info": "CPU usage critical",
      "active": true,
      "silenced": false,
      "last_status_change": 1234567890
    }
  ]
}
```

**HealthStatusUpdateMessage**:
```json
{
  "type": "health_status_update",
  "timestamp": 1234567890,
  "status": "critical",
  "critical": 2,
  "warning": 5,
  "undefined": 0,
  "uninitialized": 0,
  "clear": 20
}
```

## Integration

### Dependencies

Added to `NetdataConnector/build.gradle`:
```gradle
// WebSocket
implementation project(':Toolkit:WebSocket')
```

### Usage Example

```kotlin
val client = NetdataRealtimeClient(
    serverUrl = "http://192.168.1.100:19999"
)

// Connect and start polling
client.connect()

// Subscribe to alarms
client.subscribeToAlarms { alarmUpdate ->
    Log.d("Netdata", "Alarms: ${alarmUpdate.criticalCount} critical, ${alarmUpdate.warningCount} warning")
    alarmUpdate.alarms.forEach { alarm ->
        if (alarm.status == "CRITICAL") {
            showNotification(alarm.name, alarm.info)
        }
    }
}

// Subscribe to health status
lifecycleScope.launch {
    client.healthStatus.collect { health ->
        health?.let {
            updateHealthBadge(it.status, it.critical, it.warning)
        }
    }
}

// Observe alarms
lifecycleScope.launch {
    client.alarms.collect { alarms ->
        updateAlarmList(alarms)
    }
}

// Adjust polling frequency
client.setPollingInterval(5000L) // 5 seconds

// Fetch specific chart data
client.fetchChartData("system.cpu", points = 120)

// Clean up
client.disconnect()
```

## Files Modified/Created

### New Files
1. `Connectors/NetdataConnect/NetdataConnector/src/main/kotlin/com/shareconnect/netdataconnect/data/realtime/NetdataRealtimeMessages.kt` (461 lines)
2. `Connectors/NetdataConnect/NetdataConnector/src/main/kotlin/com/shareconnect/netdataconnect/data/realtime/NetdataRealtimeClient.kt` (344 lines)
3. `Connectors/NetdataConnect/NetdataConnector/src/test/kotlin/com/shareconnect/netdataconnect/data/realtime/NetdataRealtimeMessagesTest.kt` (500 lines)

### Modified Files
1. `Connectors/NetdataConnect/NetdataConnector/build.gradle` (added WebSocket dependency)

**Total Lines Added**: ~1,305 lines

## Performance Characteristics

### Polling Performance
- **Default Interval**: 2 seconds
- **Minimum Interval**: 500ms (configurable)
- **Maximum Interval**: 60 seconds (configurable)
- **Metrics Bandwidth**: ~10-20 KB/s (Netdata standard)
- **Dual Polling**: Metrics at 1x, alarms at 0.5x frequency

### Resource Usage
- **Network**: Configurable via poll interval
- **Memory**: Minimal - only current state cached
- **CPU**: Low - coroutine-based async operations
- **Battery**: Optimized with adjustable intervals

## Differences from WebSocket Connectors

| Aspect | Netdata (Phase 3.4) | HomeAssistant/Jellyfin |
|--------|---------------------|------------------------|
| **Protocol** | HTTP Polling | Native WebSocket |
| **Connection** | Periodic fetch | Persistent connection |
| **Real-time** | Simulated (2s delay) | True real-time |
| **Bandwidth** | Higher (full responses) | Lower (deltas) |
| **Reliability** | Reconnect on each poll | Auto-reconnect |
| **Latency** | ~2 seconds | ~50-200ms |
| **Messages** | 9 types | 11-13 types |

### Why This Approach?

1. **Netdata Limitation**: No native WebSocket support as of 2025
2. **Architectural Consistency**: Maintains same patterns as other connectors
3. **Flexibility**: Easy to migrate to WebSocket if Netdata adds support
4. **Performance**: Acceptable for monitoring use case (not real-time control)

## Known Limitations

1. **No Native WebSocket**: Uses polling instead of persistent connection
   - Higher latency (2s vs real-time)
   - Higher bandwidth consumption
   - More server load than WebSocket

2. **Polling Overhead**: Each poll fetches full dataset
   - Cannot request only changed data
   - Bandwidth proportional to number of charts

3. **Limited Granularity**: Minimum practical interval is 500ms
   - Faster polling causes unnecessary server load
   - Battery drain on mobile devices

## Future Enhancements

### If Netdata Adds WebSocket Support

The architecture is designed for easy migration:

```kotlin
// Current: HTTP Polling
class NetdataRealtimeClient(serverUrl, scope) {
    private val apiClient = NetdataApiClient(serverUrl)
    // Polling implementation...
}

// Future: Native WebSocket
class NetdataWebSocketClient(serverUrl, scope) {
    private val wsClient = OkHttpWebSocketClient(...)
    // WebSocket implementation...
    // Same public API, same messages!
}
```

### Phase 3.5+ Improvements

1. **Smart Polling**:
   - Only fetch changed charts
   - Differential updates
   - Adaptive intervals based on change frequency

2. **Server-Sent Events (SSE)**:
   - If Netdata adds SSE support
   - Lower overhead than polling
   - Better than polling, not as good as WebSocket

3. **UI Integration**:
   - Live metric charts
   - Alarm notification system
   - Health dashboard

4. **Optimization**:
   - Chart-specific subscriptions
   - Filtered polling (only subscribed metrics)
   - Compression for large datasets

## Comparison with Other Connectors

| Feature | Netdata | HomeAssistant | Jellyfin |
|---------|---------|---------------|----------|
| Protocol | HTTP Polling | WebSocket | WebSocket |
| Message Types | 9 | 11 | 13 |
| Connection State | StateFlow | StateFlow | StateFlow |
| Subscriptions | ✅ Yes | ✅ Yes | ✅ Yes |
| State Tracking | ✅ Alarms, Health | ✅ Entities | ✅ Sessions, NowPlaying |
| Auto-Reconnect | ✅ Per-poll | ✅ Exponential backoff | ✅ Exponential backoff |
| Tests | 24 | 17 | 22 |

## Compliance

### API Compliance
- ✅ Netdata REST API v1/v2
- ✅ JSON response format
- ✅ Charts, alarms, metrics endpoints
- ✅ Health status API

### Code Quality
- ✅ 100% test pass rate
- ✅ Null-safe Kotlin
- ✅ Coroutine-based async
- ✅ StateFlow reactive patterns
- ✅ Error handling with Result<T>

### Documentation
- ✅ Comprehensive KDoc comments
- ✅ Usage examples
- ✅ Message structure documentation
- ✅ API reference

## Lessons Learned

1. **WebSocket Not Universal**:
   - Not all services support WebSocket
   - Polling is a valid fallback strategy
   - Architecture should accommodate both patterns

2. **Polling vs WebSocket Trade-offs**:
   - Polling: Simple, stateless, works everywhere
   - WebSocket: Efficient, real-time, complex
   - Choose based on use case

3. **Message Framework Flexibility**:
   - WebSocket message patterns work for polling too
   - Consistent API across different transports
   - Easy to swap implementations

4. **Performance Considerations**:
   - 2-second polling is acceptable for monitoring
   - Dual-rate polling (metrics vs alarms) reduces load
   - StateFlow prevents unnecessary UI updates

## Conclusion

Phase 3.4 successfully implements real-time monitoring for Netdata using HTTP polling wrapped in the WebSocket message framework. While not true WebSocket, the implementation provides the same developer experience and maintains architectural consistency across all Phase 3 connectors.

**Key Achievements**:
- ✅ 9 message types implemented
- ✅ Dual-rate polling strategy
- ✅ Real-time alarm tracking
- ✅ Health status monitoring
- ✅ 24/24 tests passing
- ✅ Production-ready code
- ✅ Full documentation

**Architectural Benefits**:
- Same subscription pattern as other connectors
- Easy migration path if Netdata adds WebSocket
- Consistent message format across Phase 3
- Reusable polling infrastructure

**Next Steps**: Phase 3.5 - PortainerConnect WebSocket implementation (Docker events)

---

**Phase 3 Progress**:
- Phase 3.1: WebSocket Infrastructure ✅ (10 tests)
- Phase 3.2: HomeAssistant WebSocket ✅ (17 tests)
- Phase 3.3: Jellyfin WebSocket ✅ (22 tests)
- Phase 3.4: Netdata Real-time Polling ✅ (24 tests)
- **Total**: 73/73 tests passing (100%)
