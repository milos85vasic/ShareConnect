# ShareConnect Restoration - Continuation Guide

**Last Updated**: 2025-11-11
**Current Status**: Phase 1 Complete - Ready for Phase 2

---

## Quick Start: "Please continue with the implementation"

When you say this command, the work should continue with **Phase 2: API Stub Implementations** as outlined below.

---

## Current State Summary

### ✅ Completed
- **Phase 1: Test Restoration** - All fixable tests restored
  - qBitConnect: 6 test classes passing (100%)
  - PlexConnect: MockK tests providing full coverage
  - ShareConnect: Architectural issues documented
  - See: `PHASE_1_TEST_RESTORATION_COMPLETE.md`

### 📍 Current Position
- Repository: Clean, on main branch
- All commits: Pushed to 6 remote repositories
- Environment: Properly configured (local Android SDK)
- Build Status: All modules compiling successfully

---

## Phase 2: API Stub Implementations (NEXT)

### Objective
Implement placeholder API responses for services under development to enable UI and integration testing without live services.

### Priority Order

#### 1. PlexConnect API Stubs (Highest Priority)
**Status**: 6% complete (per WORK_IN_PROGRESS.md)
**Location**: `Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/data/api/`

**Tasks**:
1. Create stub mode configuration in PlexApiClient
   - Add `isStubMode` parameter to constructor
   - Create `PlexApiStubService` implementing PlexApiService

2. Implement stub responses for:
   - Authentication (PIN request/check) ✅ Already tested via MockK
   - Server discovery
   - Library listing
   - Media items retrieval
   - Playback status updates
   - Search functionality

3. Create test data generators:
   - `PlexTestData.kt` with sample servers, libraries, media items
   - Realistic data matching Plex API structure

4. Update PlexApiClient to switch between real and stub services:
```kotlin
class PlexApiClient(
    plexApiService: PlexApiService? = null,
    private val isStubMode: Boolean = false
) {
    private val service: PlexApiService = when {
        plexApiService != null -> plexApiService
        isStubMode -> PlexApiStubService()
        else -> retrofit.create(PlexApiService::class.java)
    }
}
```

**Acceptance Criteria**:
- All API methods return realistic stub data when in stub mode
- Stub mode can be toggled via build variant or runtime flag
- Tests validate stub responses match real API structure
- UI can be developed and tested without Plex server

#### 2. NextcloudConnect API Stubs
**Status**: Not started
**Location**: `Connectors/NextcloudConnect/NextcloudConnector/src/main/kotlin/com/shareconnect/nextcloudconnect/data/api/`

**Tasks**:
1. Create NextcloudApiStubService
2. Implement WebDAV stub responses
3. Implement OCS API stub responses
4. Create test file/folder structures

#### 3. MotrixConnect API Stubs
**Status**: Not started
**Location**: `Connectors/MotrixConnect/MotrixConnector/src/main/kotlin/com/shareconnect/motrixconnect/data/api/`

**Tasks**:
1. Create MotrixApiStubService (Aria2 JSON-RPC)
2. Implement download management stubs
3. Create test download data

#### 4. GiteaConnect API Stubs
**Status**: Not started
**Location**: `Connectors/GiteaConnect/GiteaConnector/src/main/kotlin/com/shareconnect/giteaconnect/data/api/`

**Tasks**:
1. Create GiteaApiStubService
2. Implement repository API stubs
3. Implement issue/PR stubs
4. Create test repository data

---

## Alternative Phase 2 Tasks (If Requested)

### Option B: ShareConnect Test Refactoring

#### Task 1: Rewrite OnboardingIntegrationTest for Compose
**File**: `ShareConnector/src/androidTest/kotlin/com/shareconnect/OnboardingIntegrationTest.kt`

**Current Issue**: Tests written for XML views, onboarding migrated to Compose

**Steps**:
1. Remove old Espresso-based tests
2. Add Compose testing dependencies:
```kotlin
androidTestImplementation("androidx.compose.ui:ui-test-junit4")
debugImplementation("androidx.compose.ui:ui-test-manifest")
```

3. Rewrite 4 tests using Compose testing:
```kotlin
@get:Rule
val composeTestRule = createAndroidComposeRule<MainActivity>()

@Test
fun testOnboardingFlow() {
    composeTestRule.onNodeWithText("Welcome").assertIsDisplayed()
    composeTestRule.onNodeWithText("Next").performClick()
    // ... compose test assertions
}
```

4. Verify tests pass with new Compose UI
5. Remove @Ignore annotations

#### Task 2: Convert SecurityAccessManagerTest to Instrumentation Test
**File**: `ShareConnector/src/test/kotlin/com/shareconnect/SecurityAccessManagerTest.kt`

**Current Issue**: Requires real Android context

**Steps**:
1. Move file to `ShareConnector/src/androidTest/kotlin/com/shareconnect/`
2. Update test to use real Android context
3. Add required permissions to AndroidManifest.xml
4. Update test assertions for real environment
5. Remove @Ignore annotation
6. Verify test passes on emulator/device

---

## Environment Setup (For New Sessions)

### Required Environment Variables
```bash
export GRADLE_USER_HOME=/Users/milosvasic/.gradle
export ANDROID_HOME=/Users/milosvasic/android-sdk
```

### Verify Environment
```bash
# Check Android SDK
ls -la ~/android-sdk/platforms/
# Should show: android-28, android-33, android-36

# Check Gradle
ls -la ~/.gradle/
# Should show: caches, wrapper directories

# Check repository
git status
# Should show: clean working tree, on branch main
```

### Run Tests to Verify Setup
```bash
# qBitConnect (should pass)
./gradlew :qBitConnector:test --no-daemon

# PlexConnect (should pass)
./gradlew :PlexConnector:test --no-daemon
```

---

## Key Files Reference

### Documentation
- `PHASE_1_TEST_RESTORATION_COMPLETE.md` - Detailed Phase 1 completion report
- `WORK_IN_PROGRESS.md` - Overall restoration roadmap (12+ connectors)
- `CLAUDE.md` - Project build/test commands and architecture
- `AGENTS.md` - Recent fixes and patterns

### Test Files
- qBitConnect tests: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/`
- PlexConnect tests: `Connectors/PlexConnect/PlexConnector/src/test/kotlin/`
- ShareConnect tests: `ShareConnector/src/test/kotlin/` and `ShareConnector/src/androidTest/kotlin/`

### API Clients
- PlexApiClient: `Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/data/api/PlexApiClient.kt`
- PlexApiService: `Connectors/PlexConnect/PlexConnector/src/main/kotlin/com/shareconnect/plexconnect/data/api/PlexApiService.kt`

---

## Decision Points for Continuation

### When "please continue with the implementation" is issued:

**Default Path**: Start Phase 2 with PlexConnect API stubs

**User Can Specify**:
- "continue with Phase 2" → API stub implementations
- "continue with test refactoring" → ShareConnect test rewrites
- "continue with next connector" → Move to NextcloudConnect setup
- "show me options" → Present decision points

---

## Success Criteria for Phase 2 Completion

### PlexConnect Stubs
- [ ] PlexApiStubService fully implements PlexApiService interface
- [ ] All API methods return realistic stub data
- [ ] PlexTestData.kt provides sample servers, libraries, media
- [ ] Stub mode toggle works in PlexApiClient
- [ ] UI can be developed without live Plex server
- [ ] Tests validate stub data structure

### Overall Phase 2
- [ ] At least 2 connectors have working API stubs
- [ ] Stub mode documented in each connector's README
- [ ] Tests demonstrate stub functionality
- [ ] UI development can proceed independently of live services

---

## Common Commands

### Build
```bash
# Build all
./gradlew build

# Build specific connector
./gradlew :PlexConnector:assembleDebug
```

### Test
```bash
# Run all tests
./run_all_tests.sh

# Run specific module tests
./gradlew :PlexConnector:test --no-daemon

# Run with coverage
./gradlew :PlexConnector:testDebugUnitTestCoverage
```

### Git
```bash
# Check status
git status

# Create branch for Phase 2
git checkout -b feature/phase-2-api-stubs

# Commit work
git add .
git commit -m "Phase 2: Implement PlexConnect API stubs"

# Push to all remotes
git push --all
```

---

## Troubleshooting

### If tests fail after environment reload:
```bash
# Reset Gradle daemon
./gradlew --stop

# Clean build
./gradlew clean

# Reinstall dependencies
./gradlew --refresh-dependencies

# Re-run tests
./gradlew :qBitConnector:test --no-daemon
```

### If Android SDK issues:
```bash
# Verify SDK location
cat local.properties
# Should show: sdk.dir=/Users/milosvasic/android-sdk

# Check SDK components
$ANDROID_HOME/cmdline-tools/latest/bin/sdkmanager --list_installed
```

### If Gradle user home issues:
```bash
# Verify Gradle home
echo $GRADLE_USER_HOME
# Should show: /Users/milosvasic/.gradle

# Reset if needed
export GRADLE_USER_HOME=/Users/milosvasic/.gradle
mkdir -p ~/.gradle
chmod 755 ~/.gradle
```

---

## Notes for Claude

When user says "please continue with the implementation":

1. **Read this file first** to understand current state
2. **Check PHASE_1_TEST_RESTORATION_COMPLETE.md** for detailed context
3. **Verify environment** with the commands above
4. **Start Phase 2** with PlexConnect API stubs (unless user specifies otherwise)
5. **Use TodoWrite** to track Phase 2 tasks
6. **Create tests first** (TDD approach) for stub implementations
7. **Document decisions** in this file or create Phase 2 completion doc
8. **Commit regularly** with clear messages
9. **Update this file** with progress as you work

---

## Progress Tracking Template

Copy this to track Phase 2 progress:

```markdown
### Phase 2 Progress (Started: YYYY-MM-DD)

#### PlexConnect API Stubs
- [ ] PlexApiStubService created
- [ ] Authentication stubs implemented
- [ ] Server discovery stubs implemented
- [ ] Library stubs implemented
- [ ] Media stubs implemented
- [ ] Search stubs implemented
- [ ] PlexTestData.kt created
- [ ] Stub mode toggle working
- [ ] Tests passing
- [ ] Documentation updated

#### Current Blockers
(List any issues encountered)

#### Next Session TODO
(What to do when continuing next time)
```

---

**Ready to continue! Just say: "please continue with the implementation"**
