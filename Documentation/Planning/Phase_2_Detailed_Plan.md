# ShareConnect Phase 2 - Detailed Implementation Plan

## Document Information
- **Version**: 1.0.0
- **Date**: 2025-10-25
- **Status**: Planning - Ready for Implementation
- **Phase**: Phase 2 - Ecosystem Growth
- **Timeline**: 3-6 months
- **Connectors**: JellyfinConnect, PortainerConnect, NetdataConnect, HomeAssistantConnect

---

## Executive Summary

Phase 2 expands the ShareConnect ecosystem with **four additional connectors** focusing on media servers, container management, system monitoring, and home automation. Building on Phase 1's success (4 connectors, 215 tests, 98% complete), Phase 2 targets similar quality standards while introducing more complex integrations.

### Phase 2 Goals

1. **Expand Media Ecosystem** - Add Jellyfin as Plex alternative
2. **Add DevOps Tools** - Portainer for Docker management
3. **Enable Monitoring** - Netdata for system health tracking
4. **Smart Home Integration** - Home Assistant connectivity
5. **Maintain Quality** - 80%+ test coverage, comprehensive docs
6. **Accelerate Development** - Leverage Phase 1 patterns for 30% faster development

### Success Criteria

- ✅ 4 connectors delivered and production-ready
- ✅ 200+ tests (80%+ coverage average)
- ✅ Complete technical documentation
- ✅ Complete user manuals
- ✅ Security audit passed
- ✅ Performance targets met
- ✅ Production approval granted

---

## Phase 2 Connectors Overview

### 2.1 JellyfinConnect - Jellyfin Media Server Integration

**Priority**: HIGH
**Complexity**: MEDIUM
**Estimated Effort**: 4 weeks
**Similar to**: PlexConnect (Phase 1)

#### Purpose
Provide native Android access to Jellyfin media servers, offering an open-source alternative to PlexConnect with similar functionality for media library management and sharing.

#### Key Features
- Library browsing (Movies, TV Shows, Music, Photos)
- Media playback integration
- User authentication (username/password or API key)
- Media metadata viewing
- Watch status tracking
- Direct play and transcoding support
- ShareConnect integration

#### Technical Approach
- **API**: Jellyfin REST API (OpenAPI documented)
- **Auth**: Username/password or API key
- **Protocol**: REST over HTTP/HTTPS
- **Similarities**: 70% code reuse from PlexConnect architecture

#### Unique Challenges
- Multiple authentication methods
- Transcoding quality selection
- Plugin support detection
- DLNA server integration

---

### 2.2 PortainerConnect - Docker Container Management

**Priority**: HIGH
**Complexity**: HIGH
**Estimated Effort**: 5 weeks
**New Integration**: Docker/Container management

#### Purpose
Enable remote Docker container management from Android, allowing users to monitor, start, stop, and configure containers across multiple Docker hosts via Portainer.

#### Key Features
- Container listing and status monitoring
- Start/stop/restart containers
- View container logs in real-time
- Image management (pull, remove)
- Stack deployment from Compose files
- Volume and network management
- Multi-endpoint support (multiple Docker hosts)
- ShareConnect integration for sharing configs

#### Technical Approach
- **API**: Portainer REST API v2
- **Auth**: API token or JWT
- **Protocol**: REST over HTTP/HTTPS
- **Real-time**: WebSocket for log streaming

#### Unique Challenges
- Real-time log streaming (WebSocket)
- Complex JSON for stack deployment
- Multi-endpoint management
- Docker Compose file parsing
- Container resource monitoring

---

### 2.3 NetdataConnect - System Monitoring Integration

**Priority**: MEDIUM
**Complexity**: MEDIUM
**Estimated Effort**: 3 weeks
**New Integration**: Monitoring/Metrics

#### Purpose
Provide mobile access to Netdata system monitoring dashboards, enabling users to track server health, performance metrics, and alerts from their Android devices.

#### Key Features
- Real-time metrics visualization
- Alert monitoring and management
- Historical data viewing
- Multi-server monitoring
- Custom dashboard creation
- Metric alerts and thresholds
- ShareConnect integration for sharing metrics

#### Technical Approach
- **API**: Netdata REST API + Streaming API
- **Auth**: Basic Auth or API key
- **Protocol**: REST over HTTP/HTTPS
- **Real-time**: Server-Sent Events (SSE) for live metrics

#### Unique Challenges
- High-frequency data updates (1-second intervals)
- Chart rendering for time-series data
- Efficient data buffering
- Alert notification handling
- Metric aggregation across servers

---

### 2.4 HomeAssistantConnect - Home Automation Integration

**Priority**: MEDIUM
**Complexity**: HIGH
**Estimated Effort**: 5 weeks
**New Integration**: IoT/Smart Home

#### Purpose
Connect ShareConnect ecosystem to Home Assistant, enabling automation triggers, device control, and integration with the broader smart home ecosystem.

#### Key Features
- Entity browsing (lights, switches, sensors, etc.)
- Device control (on/off, dimming, temperature)
- Automation triggers
- Scene activation
- Service calls
- Event monitoring
- Multi-instance support
- ShareConnect integration for automation sharing

#### Technical Approach
- **API**: Home Assistant REST API + WebSocket API
- **Auth**: Long-lived access tokens
- **Protocol**: REST + WebSocket over HTTP/HTTPS
- **Real-time**: WebSocket for state changes

#### Unique Challenges
- WebSocket connection management
- Complex entity types (100+ types)
- State synchronization
- Service call parameter handling
- Event subscription management
- YAML automation parsing

---

## Phase 2 Architecture

### Shared Architecture Patterns (from Phase 1)

All Phase 2 connectors will follow established patterns:

```
┌─────────────────────────────────────────────────────────────┐
│                    Phase 2 Connector Architecture            │
├─────────────────────────────────────────────────────────────┤
│  ┌──────────────────────────────────────────────────────┐   │
│  │                   UI Layer (Compose)                  │   │
│  │  - MainActivity, OnboardingActivity                   │   │
│  │  - Screens: List, Detail, Settings, (Dashboard)      │   │
│  │  - ViewModels: State management with StateFlow       │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │                   Data Layer                          │   │
│  │  - API Client: Retrofit/OkHttp/(WebSocket)           │   │
│  │  - Repository: Data access abstraction               │   │
│  │  - Database: Room + SQLCipher (optional)              │   │
│  │  - Models: Data classes with Gson serialization      │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │         New: Real-Time Communication Layer            │   │
│  │  - WebSocket Manager (Portainer, HomeAssistant)      │   │
│  │  - SSE Handler (Netdata)                              │   │
│  │  - Connection state management                        │   │
│  └──────────────────────────────────────────────────────┘   │
│                           │                                   │
│  ┌──────────────────────────────────────────────────────┐   │
│  │              ShareConnect Integration                 │   │
│  │  - ThemeSync, ProfileSync, HistorySync               │   │
│  │  - SecurityAccess, DesignSystem, Onboarding          │   │
│  │  - Asinka: Real-time sync via gRPC                   │   │
│  └──────────────────────────────────────────────────────┘   │
└─────────────────────────────────────────────────────────────┘
```

### New Architectural Components

#### 1. WebSocket Manager
```kotlin
/**
 * Manages WebSocket connections for real-time communication
 * Used by: PortainerConnect, HomeAssistantConnect
 */
class WebSocketManager(
    private val url: String,
    private val authToken: String
) {
    private val client: OkHttpClient
    private var webSocket: WebSocket? = null

    fun connect(listener: WebSocketListener)
    fun disconnect()
    fun send(message: String): Boolean
    fun isConnected(): Boolean
}
```

#### 2. Server-Sent Events (SSE) Handler
```kotlin
/**
 * Handles SSE streams for real-time metrics
 * Used by: NetdataConnect
 */
class SSEHandler(
    private val url: String,
    private val authToken: String
) {
    suspend fun observe(callback: (String) -> Unit)
    fun stop()
}
```

#### 3. Real-Time Data Buffer
```kotlin
/**
 * Buffers high-frequency data updates
 * Used by: NetdataConnect (metrics), PortainerConnect (logs)
 */
class DataBuffer<T>(
    private val maxSize: Int = 1000,
    private val flushInterval: Long = 1000L
) {
    fun add(item: T)
    fun flush(): List<T>
    fun clear()
}
```

---

## Detailed Implementation Plans

### 2.1 JellyfinConnect Implementation

#### Week 1: Project Setup & API Client
**Tasks**:
- Create `Connectors/JellyfinConnect/` directory structure
- Set up Gradle project with dependencies
- Implement `JellyfinApiClient.kt` with Retrofit
- Create data models (JellyfinServer, JellyfinLibrary, JellyfinItem)
- Implement authentication (username/password + API key)
- Write unit tests for API client (target: 20 tests)

**Deliverables**:
- Project structure ✅
- API client with authentication ✅
- 20+ unit tests ✅

#### Week 2: Core Features
**Tasks**:
- Implement library browsing
- Add media item details
- Create repository pattern
- Implement Room database for server profiles
- Add playback integration (intent to Jellyfin app)
- Write integration tests (target: 15 tests)

**Deliverables**:
- Library browsing functional ✅
- Media details working ✅
- 15+ integration tests ✅

#### Week 3: UI Implementation
**Tasks**:
- Create MainActivity with SecurityAccess
- Implement library list screen (Compose)
- Add media detail screen
- Integrate with DesignSystem
- Add theme sync
- Create onboarding flow
- Write automation tests (target: 8 tests)

**Deliverables**:
- Complete UI with Material Design 3 ✅
- 8+ automation tests ✅

#### Week 4: ShareConnect Integration & Testing
**Tasks**:
- Integrate all sync modules (Theme, Profile, History, etc.)
- Add sharing functionality
- Comprehensive testing and bug fixes
- Performance optimization
- Write technical documentation
- Write user manual

**Deliverables**:
- APK builds successfully ✅
- 43+ total tests ✅
- Complete documentation ✅

**Target Metrics**:
- Tests: 43+ (20 unit, 15 integration, 8 automation)
- Coverage: 80%+
- Cold start: <2s
- Memory: <150MB active

---

### 2.2 PortainerConnect Implementation

#### Week 1: Project Setup & API Client
**Tasks**:
- Create `Connectors/PortainerConnect/` directory structure
- Set up Gradle with OkHttp WebSocket dependency
- Implement `PortainerApiClient.kt` with Retrofit
- Implement `PortainerWebSocketManager.kt`
- Create data models (Container, Stack, Image, Volume)
- Implement API token authentication
- Write unit tests (target: 25 tests)

**Deliverables**:
- Project structure ✅
- API client + WebSocket manager ✅
- 25+ unit tests ✅

#### Week 2: Container Management
**Tasks**:
- Implement container listing
- Add container control (start/stop/restart)
- Create log streaming with WebSocket
- Implement repository pattern
- Add Room database
- Write integration tests (target: 18 tests)

**Deliverables**:
- Container management functional ✅
- Real-time log streaming working ✅
- 18+ integration tests ✅

#### Week 3: Advanced Features
**Tasks**:
- Implement image management
- Add stack deployment
- Create volume/network management
- Multi-endpoint support
- Write integration tests (target: 10 tests)

**Deliverables**:
- Advanced features functional ✅
- 10+ integration tests ✅

#### Week 4: UI Implementation
**Tasks**:
- Create MainActivity and screens (Compose)
- Implement container list with status indicators
- Add log viewer with real-time updates
- Create stack deployment UI
- Integrate with DesignSystem
- Write automation tests (target: 10 tests)

**Deliverables**:
- Complete UI ✅
- 10+ automation tests ✅

#### Week 5: Integration & Polish
**Tasks**:
- Integrate all sync modules
- Add sharing functionality
- Comprehensive testing
- Performance optimization (WebSocket reconnection)
- Write documentation

**Deliverables**:
- APK builds successfully ✅
- 63+ total tests ✅
- Complete documentation ✅

**Target Metrics**:
- Tests: 63+ (25 unit, 28 integration, 10 automation)
- Coverage: 82%+
- Cold start: <2.5s (WebSocket overhead)
- Memory: <180MB active (log buffering)

---

### 2.3 NetdataConnect Implementation

#### Week 1: Project Setup & API Client
**Tasks**:
- Create `Connectors/NetdataConnect/` directory structure
- Set up Gradle project
- Implement `NetdataApiClient.kt` with Retrofit
- Implement `NetdataSSEHandler.kt` for streaming
- Create data models (NetdataChart, NetdataMetric, NetdataAlert)
- Implement authentication
- Write unit tests (target: 22 tests)

**Deliverables**:
- Project structure ✅
- API client + SSE handler ✅
- 22+ unit tests ✅

#### Week 2: Metrics & Charting
**Tasks**:
- Implement metrics fetching
- Create chart data models
- Add alert monitoring
- Implement data buffering for high-frequency updates
- Create repository pattern
- Write integration tests (target: 16 tests)

**Deliverables**:
- Metrics system functional ✅
- 16+ integration tests ✅

#### Week 3: UI & Visualization
**Tasks**:
- Create MainActivity and screens (Compose)
- Implement metric dashboard with charts
- Add alert list and details
- Create custom chart composables
- Integrate with DesignSystem
- Write automation tests (target: 7 tests)

**Deliverables**:
- Complete UI with charts ✅
- 7+ automation tests ✅

**Target Metrics**:
- Tests: 45+ (22 unit, 16 integration, 7 automation)
- Coverage: 80%+
- Cold start: <2s
- Memory: <160MB active (chart rendering)

---

### 2.4 HomeAssistantConnect Implementation

#### Week 1: Project Setup & API Client
**Tasks**:
- Create `Connectors/HomeAssistantConnect/` directory structure
- Set up Gradle with WebSocket dependencies
- Implement `HomeAssistantApiClient.kt`
- Implement `HomeAssistantWebSocketClient.kt`
- Create data models (Entity, State, Service, Event)
- Implement long-lived token authentication
- Write unit tests (target: 28 tests)

**Deliverables**:
- Project structure ✅
- API client + WebSocket client ✅
- 28+ unit tests ✅

#### Week 2: Entity Management
**Tasks**:
- Implement entity browsing
- Add entity state fetching
- Create service call handling
- Implement state change subscriptions via WebSocket
- Write integration tests (target: 20 tests)

**Deliverables**:
- Entity system functional ✅
- 20+ integration tests ✅

#### Week 3: Device Control & Automation
**Tasks**:
- Implement device control UI
- Add scene activation
- Create automation triggers
- Add event monitoring
- Write integration tests (target: 12 tests)

**Deliverables**:
- Control features functional ✅
- 12+ integration tests ✅

#### Week 4: UI Implementation
**Tasks**:
- Create MainActivity and screens (Compose)
- Implement entity list grouped by domain
- Add device control cards
- Create automation builder UI
- Integrate with DesignSystem
- Write automation tests (target: 9 tests)

**Deliverables**:
- Complete UI ✅
- 9+ automation tests ✅

#### Week 5: Integration & Polish
**Tasks**:
- Integrate all sync modules
- Add sharing functionality
- WebSocket reconnection handling
- Comprehensive testing
- Write documentation

**Deliverables**:
- APK builds successfully ✅
- 69+ total tests ✅
- Complete documentation ✅

**Target Metrics**:
- Tests: 69+ (28 unit, 32 integration, 9 automation)
- Coverage: 83%+
- Cold start: <2.5s
- Memory: <170MB active

---

## Phase 2 Testing Strategy

### Test Coverage Targets

| Connector | Unit Tests | Integration Tests | Automation Tests | Total | Coverage |
|-----------|------------|-------------------|------------------|-------|----------|
| JellyfinConnect | 20 | 15 | 8 | 43 | 80% |
| PortainerConnect | 25 | 28 | 10 | 63 | 82% |
| NetdataConnect | 22 | 16 | 7 | 45 | 80% |
| HomeAssistantConnect | 28 | 32 | 9 | 69 | 83% |
| **TOTAL** | **95** | **91** | **34** | **220** | **81%** |

### Testing Approach

#### Unit Tests
- All API client methods
- All data model serialization
- Authentication flows
- WebSocket/SSE handlers
- Data buffering logic

#### Integration Tests
- End-to-end API workflows
- WebSocket connection lifecycle
- Real-time data streaming
- Database operations
- Repository patterns

#### Automation Tests
- App launch and onboarding
- Server connection
- Feature workflows
- ShareConnect integration

### Test Frameworks

**Phase 1 Frameworks** (continue using):
- JUnit 4.13.2
- MockK 1.13.8
- Robolectric 4.11.1
- MockWebServer 4.12.0
- Espresso 3.5.1

**New for Phase 2**:
- **MockWebServer** with WebSocket support
- **Turbine 1.0.0** for Flow testing (already used)
- **kotlinx-coroutines-test** for testing SSE

---

## Technical Challenges & Solutions

### Challenge 1: WebSocket Connection Management

**Challenge**: Maintaining persistent WebSocket connections with reconnection logic

**Solution**:
```kotlin
class WebSocketConnectionManager(
    private val url: String,
    private val authToken: String
) {
    private var reconnectAttempts = 0
    private val maxReconnectAttempts = 5
    private val reconnectDelayMs = 2000L

    fun connect() {
        // Exponential backoff reconnection
        if (reconnectAttempts < maxReconnectAttempts) {
            delay(reconnectDelayMs * (2.pow(reconnectAttempts)))
            // Attempt connection
        }
    }

    fun onConnectionLost() {
        reconnectAttempts++
        connect()
    }
}
```

### Challenge 2: High-Frequency Data Updates

**Challenge**: Netdata sends metrics every 1 second - could overwhelm UI

**Solution**:
```kotlin
class MetricsBuffer(
    private val updateIntervalMs: Long = 1000L
) {
    private val buffer = mutableListOf<Metric>()

    init {
        // Flush buffer to UI at controlled rate
        viewModelScope.launch {
            while (isActive) {
                delay(updateIntervalMs)
                val batch = buffer.toList()
                buffer.clear()
                _metrics.emit(batch)
            }
        }
    }
}
```

### Challenge 3: Complex Entity Types (Home Assistant)

**Challenge**: 100+ entity types with different attributes

**Solution**:
```kotlin
sealed class Entity {
    abstract val entityId: String
    abstract val state: String

    data class Light(
        override val entityId: String,
        override val state: String,
        val brightness: Int?,
        val colorTemp: Int?
    ) : Entity()

    data class Switch(
        override val entityId: String,
        override val state: String
    ) : Entity()

    // ... other types
}

// Deserializer based on domain prefix
class EntityDeserializer : JsonDeserializer<Entity> {
    override fun deserialize(...): Entity {
        val domain = entityId.split(".")[0]
        return when (domain) {
            "light" -> gson.fromJson(json, Light::class.java)
            "switch" -> gson.fromJson(json, Switch::class.java)
            // ...
        }
    }
}
```

---

## Documentation Requirements

### Technical Documentation (4 documents)

Each connector requires comprehensive technical documentation:

1. **JellyfinConnect.md** (~500 lines)
   - Jellyfin API integration
   - Authentication flows
   - Media library structure
   - Transcoding support

2. **PortainerConnect.md** (~600 lines)
   - Portainer REST API v2
   - WebSocket log streaming
   - Docker Compose deployment
   - Multi-endpoint management

3. **NetdataConnect.md** (~550 lines)
   - Netdata API + SSE streaming
   - Metrics buffering strategy
   - Chart rendering approach
   - Alert handling

4. **HomeAssistantConnect.md** (~650 lines)
   - Home Assistant REST + WebSocket API
   - Entity type handling
   - Service call patterns
   - Event subscription

**Total**: ~2,300 lines of technical documentation

### User Manuals (4 documents)

Each connector requires end-user documentation:

1. **JellyfinConnect_User_Manual.md** (10 sections, 35+ FAQs)
2. **PortainerConnect_User_Manual.md** (11 sections, 40+ FAQs)
3. **NetdataConnect_User_Manual.md** (9 sections, 30+ FAQs)
4. **HomeAssistantConnect_User_Manual.md** (12 sections, 45+ FAQs)

**Total**: 42 sections, 150+ FAQs, ~3,000 lines

---

## Phase 2 Timeline

### Month 1: JellyfinConnect + PortainerConnect

**Weeks 1-4**: JellyfinConnect (parallel)
**Weeks 1-5**: PortainerConnect (parallel)

**Deliverables**:
- 2 APKs building
- 106 tests (43 + 63)
- Technical docs started

### Month 2: NetdataConnect + HomeAssistantConnect

**Weeks 5-7**: NetdataConnect (parallel)
**Weeks 5-9**: HomeAssistantConnect (parallel)

**Deliverables**:
- 2 APKs building
- 114 tests (45 + 69)
- Technical docs started

### Month 3: Integration, Testing, Documentation

**Weeks 10-12**: Comprehensive QA and Documentation

**Tasks**:
- AI QA test plan creation
- Security audit
- Performance analysis
- Complete all documentation
- Code review
- Production readiness assessment

**Deliverables**:
- All 4 APKs production-ready
- 220 total tests
- 8 documentation files
- 5 QA reports
- Release notes
- Completion report

---

## Success Metrics

### Quality Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Test Coverage** | >80% | Code coverage tools |
| **Total Tests** | 200+ | Test execution reports |
| **Performance** | <2.5s cold start | Android Profiler |
| **Memory** | <180MB active | Android Profiler |
| **Security** | 0 critical issues | Security audit |
| **Documentation** | 100% complete | Deliverables checklist |

### User Experience Metrics

| Metric | Target | Measurement |
|--------|--------|-------------|
| **Crash-Free Rate** | >99% | Firebase Crashlytics |
| **Average Rating** | >4.2 stars | User feedback |
| **Onboarding Completion** | >85% | Analytics |
| **Feature Adoption** | >70% | Usage tracking |

---

## Risk Assessment

### High-Risk Items

1. **WebSocket Stability** (PortainerConnect, HomeAssistantConnect)
   - **Mitigation**: Comprehensive reconnection logic, extensive testing

2. **Real-Time Data Overhead** (NetdataConnect)
   - **Mitigation**: Data buffering, UI update throttling

3. **API Complexity** (HomeAssistantConnect)
   - **Mitigation**: Phased implementation, focus on common entity types

### Medium-Risk Items

1. **Timeline Pressure** (5-month timeline for 4 connectors)
   - **Mitigation**: Parallel development, reuse Phase 1 patterns

2. **Testing Real-Time Features**
   - **Mitigation**: Dedicated integration tests, mock WebSocket servers

---

## Dependencies

### New Dependencies for Phase 2

**WebSocket Support**:
```gradle
implementation("com.squareup.okhttp3:okhttp:4.12.0") // Already have
// WebSocket included in OkHttp
```

**Chart Rendering** (NetdataConnect):
```gradle
implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
// or
implementation("co.yml:ycharts:2.1.0") // Compose charts
```

**YAML Parsing** (HomeAssistantConnect - optional):
```gradle
implementation("org.yaml:snakeyaml:2.0")
```

All other dependencies same as Phase 1.

---

## Phase 2 Budget

### Development Effort

| Connector | Weeks | Developer Hours | Complexity |
|-----------|-------|----------------|------------|
| JellyfinConnect | 4 | 160 | Medium |
| PortainerConnect | 5 | 200 | High |
| NetdataConnect | 3 | 120 | Medium |
| HomeAssistantConnect | 5 | 200 | High |
| **Integration & QA** | 3 | 120 | Medium |
| **TOTAL** | **20** | **800** | - |

### Documentation Effort

| Activity | Weeks | Hours |
|----------|-------|-------|
| Technical Docs | 2 | 80 |
| User Manuals | 2 | 80 |
| QA Reports | 1 | 40 |
| **TOTAL** | **5** | **200** |

**Grand Total**: 25 weeks (~6 months), 1,000 developer hours

---

## Conclusion

Phase 2 builds on Phase 1's success by adding four strategically important connectors that expand ShareConnect into media server alternatives, DevOps tooling, system monitoring, and smart home automation.

**Key Differentiators**:
- Real-time communication (WebSocket, SSE)
- High-frequency data handling
- Complex entity type systems
- Multi-endpoint management

**Expected Outcomes**:
- 220+ tests (81% average coverage)
- 8 comprehensive documentation files
- 5 QA reports
- Production-ready connectors
- Expanded ShareConnect ecosystem to 12 applications

**Phase 2 Status**: ✅ **READY FOR IMPLEMENTATION**

---

**Document Version**: 1.0.0
**Last Updated**: 2025-10-25
**Prepared By**: ShareConnect Planning Team
**Next Steps**: Begin JellyfinConnect and PortainerConnect development

---

*This detailed plan provides a comprehensive roadmap for Phase 2 implementation, leveraging Phase 1 learnings and establishing patterns for future phases.*
