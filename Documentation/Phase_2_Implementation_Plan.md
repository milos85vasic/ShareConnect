# Phase 2: Ecosystem Growth - IMPLEMENTATION PLAN

**Date:** October 25, 2025
**Status:** 🚀 **READY TO START**

---

## 📋 Overview

Phase 2 expands the ShareConnect ecosystem with **4 new connector applications** focused on system monitoring, container management, and home automation.

### Phase 2 Connectors

1. **JellyfinConnect** - Jellyfin Media Server integration
2. **PortainerConnect** - Docker container management
3. **NetdataConnect** - System monitoring and metrics
4. **HomeAssistantConnect** - Home automation platform

---

## 🎯 Objectives

### Primary Goals
- ✅ Implement 4 production-ready connector applications
- ✅ Complete API client implementations with comprehensive coverage
- ✅ Full integration with ShareConnect ecosystem (8 sync modules each)
- ✅ Unit test coverage (target: 15+ tests per connector)
- ✅ All connectors building successfully

### Success Criteria
- All 4 connectors build without errors
- API clients cover all major operations
- Integration with ThemeSync, ProfileSync, HistorySync, etc.
- SecurityAccess PIN protection integrated
- Compose UI with Material Design 3
- Minimum 50 total unit tests passing

---

## 📊 Connector Details

### 2.1 JellyfinConnect

**Priority:** HIGH
**Estimated Effort:** 3 weeks
**Complexity:** MEDIUM (similar to Plex)

**Technology Stack:**
- Jellyfin REST API v1
- Retrofit for HTTP client
- Result<T> error handling
- Coroutines for async operations

**API Coverage Required:**
- Authentication (API key, username/password)
- Server information retrieval
- Library browsing (movies, TV shows, music)
- Media item queries and metadata
- Playback status tracking
- User management
- Search functionality

**Integration:**
- 8 sync modules (Theme, Profile, History, RSS, Bookmark, Preferences, Language, TorrentSharing)
- SecurityAccess for PIN protection
- Room database for local storage
- Compose UI screens

**Test Target:** 18-20 unit tests

---

### 2.2 PortainerConnect

**Priority:** HIGH
**Estimated Effort:** 4 weeks
**Complexity:** HIGH (Docker API complexity)

**Technology Stack:**
- Portainer REST API v2
- Retrofit for HTTP client
- Result<T> error handling
- Coroutines for async operations

**API Coverage Required:**
- Authentication (API token)
- Container management (list, start, stop, restart, remove)
- Image management (list, pull, remove)
- Volume management
- Network management
- Stack/Compose deployment
- Resource monitoring (CPU, memory, network)

**Integration:**
- 8 sync modules
- SecurityAccess
- Room database
- Compose UI with real-time container status

**Test Target:** 20-25 unit tests

---

### 2.3 NetdataConnect

**Priority:** MEDIUM
**Estimated Effort:** 3 weeks
**Complexity:** MEDIUM (metrics visualization)

**Technology Stack:**
- Netdata REST API v1
- Retrofit for HTTP client
- Result<T> error handling
- Coroutines for async operations

**API Coverage Required:**
- Server information and status
- System metrics (CPU, RAM, disk, network)
- Service monitoring
- Alert retrieval
- Historical data queries
- Chart data for visualization

**Integration:**
- 8 sync modules
- SecurityAccess
- Room database for metric history
- Compose UI with charts (using Vico or MPAndroidChart)

**Test Target:** 15-18 unit tests

---

### 2.4 HomeAssistantConnect

**Priority:** MEDIUM
**Estimated Effort:** 4 weeks
**Complexity:** HIGH (WebSocket + REST API)

**Technology Stack:**
- Home Assistant REST API
- Home Assistant WebSocket API (for real-time updates)
- Retrofit for REST
- OkHttp for WebSocket
- Result<T> error handling
- Coroutines for async operations

**API Coverage Required:**
- Authentication (long-lived access token)
- Entity state retrieval (lights, switches, sensors, etc.)
- Service calls (turn on/off, set state)
- Automation triggering
- Scene activation
- Real-time state updates via WebSocket

**Integration:**
- 8 sync modules
- SecurityAccess
- Room database for entity caching
- Compose UI for entity control

**Test Target:** 20-25 unit tests

---

## 🛠️ Implementation Approach

### Step 1: Project Scaffolding (All Connectors)
For each connector:
1. Create directory structure (`Connectors/{Name}Connect/{Name}Connector/`)
2. Initialize Gradle project
3. Configure AndroidManifest.xml
4. Set up build.gradle with dependencies
5. Create TestApplication for unit testing
6. Add Robolectric configuration

### Step 2: API Client Implementation
1. Research API documentation
2. Define data models (Kotlin data classes with Gson/Kotlinx Serialization)
3. Create Retrofit service interface
4. Implement API client with dependency injection support
5. Add error handling and logging

### Step 3: Integration
1. Integrate 8 sync modules
2. Add SecurityAccess for PIN protection
3. Implement Room database entities and DAOs
4. Create repository layer

### Step 4: UI Implementation
1. Create MainActivity with Compose
2. Implement navigation
3. Create feature screens (using DesignSystem components)
4. Add Material Design 3 theming

### Step 5: Testing
1. Create unit tests for API client (MockK pattern)
2. Target minimum 15 tests per connector
3. Verify all tests pass
4. Ensure builds are successful

---

## 📅 Timeline

### Week 1-3: JellyfinConnect
- Week 1: Scaffolding + API Client
- Week 2: Integration + UI
- Week 3: Testing + Polish

### Week 4-7: PortainerConnect
- Week 4: Scaffolding + API Client (Docker complexity)
- Week 5: Integration
- Week 6: UI with real-time updates
- Week 7: Testing + Polish

### Week 8-10: NetdataConnect
- Week 8: Scaffolding + API Client
- Week 9: Integration + UI with charts
- Week 10: Testing + Polish

### Week 11-14: HomeAssistantConnect
- Week 11: Scaffolding + REST API Client
- Week 12: WebSocket integration
- Week 13: UI for entity control
- Week 14: Testing + Polish

**Total Estimated Time:** 14 weeks (~3.5 months)

---

## 🎓 Lessons from Phase 1

### Apply These Patterns:
1. **Dependency Injection for Testability**
   - Accept optional service parameter in API client constructor
   - Enables MockK testing without SSL/TLS issues

2. **TestApplication Pattern**
   - Create TestApplication that skips Asinka/Firebase initialization
   - Configure via @Config annotation in tests

3. **MockK Over MockWebServer**
   - Mock Retrofit service interfaces directly
   - Avoids HTTP layer complexity and SSL/TLS issues

4. **Relaxed Mocks for Complex Data Models**
   - Use `mockk<Type>(relaxed = true)` for complex nested models
   - Focus tests on core functionality, not data structure details

5. **Incremental Testing**
   - Start with basic tests (initialization, simple operations)
   - Add complexity gradually
   - Don't block on 100% coverage initially

---

## 📊 Success Metrics

### Minimum Viable Phase 2:
- ✅ All 4 connectors build successfully
- ✅ All 4 connectors have functional API clients
- ✅ All 4 connectors integrated with sync modules
- ✅ Minimum 50 total passing unit tests (average 12-13 per connector)

### Ideal Phase 2:
- ✅ All 4 connectors build successfully
- ✅ Comprehensive API coverage
- ✅ Full UI implementation
- ✅ 75+ total passing unit tests (average 18-19 per connector)
- ✅ User documentation for all connectors

---

## 🚀 Getting Started

### Priority Order:
1. **JellyfinConnect** (HIGH) - Similar to Plex, proven patterns
2. **PortainerConnect** (HIGH) - Critical for DevOps workflows
3. **NetdataConnect** (MEDIUM) - Monitoring use case
4. **HomeAssistantConnect** (MEDIUM) - IoT/automation use case

### Initial Focus:
Start with **JellyfinConnect** as it's most similar to PlexConnect (already completed in Phase 1). This allows us to:
- Reuse patterns and code structure
- Validate our connector development workflow
- Build confidence before tackling more complex connectors

---

**Plan Created:** October 25, 2025
**Next Action:** Begin JellyfinConnect implementation
**Expected Phase 2 Completion:** February 2026
