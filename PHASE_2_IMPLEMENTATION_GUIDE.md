# PHASE 2 IMPLEMENTATION GUIDE
## Test Coverage & Disabled Test Reactivation - Step-by-Step Instructions

---

## OVERVIEW

**Duration**: 2 weeks (10 working days)
**Goal**: Reactivate all disabled tests, achieve 100% test coverage, create comprehensive test suites
**Success Criteria**: Zero disabled tests, 100% code coverage, all test types passing

---

## WEEK 4: DISABLED TEST RESOLUTION

### DAY 16-18: QBITCONNECT TEST REACTIVATION

#### 16.1 Fix TorrentListViewModelTest Context Setup (Day 16)

**File**: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/ui/viewmodels/TorrentListViewModelTest.kt`

**Current Issue**: Test disabled "for release build" due to Robolectric context issues

**Step 1: Remove @Disabled Annotation**
```kotlin
// Remove this line:
@Disabled("Temporarily disabled for release build")

// Add proper annotations:
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = TestApplication::class)
class TorrentListViewModelTest {
```

**Step 2: Fix Context Setup**
```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

@get:Rule 
val instantExecutorRule = InstantTaskExecutorRule()

@MockK
private lateinit var mockTorrentRepository: TorrentRepository

@MockK
private lateinit var mockServerRepository: ServerRepository

@MockK
private lateinit var mockProfileSyncManager: ProfileSyncManager

private lateinit var viewModel: TorrentListViewModel

@Before
fun setup() {
    MockKAnnotations.init(this, relaxUnitFun = true)
    
    // Create ViewModel with proper dependencies
    viewModel = TorrentListViewModel(
        torrentRepository = mockTorrentRepository,
        serverRepository = mockServerRepository,
        profileSyncManager = mockSyncManager
    )
}
```

**Step 3: Implement Test Methods**
```kotlin
@Test
fun `load torrents should update ui state with success`() = runTest {
    // Given
    val torrents = listOf(
        Torrent(
            hash = "abc123",
            name = "Test Torrent",
            size = 1073741824L,
            progress = 50.0f,
            state = "downloading",
            speed = 1048576L
        )
    )
    
    coEvery { mockTorrentRepository.getAllTorrents() } returns flowOf(torrents)
    
    // When
    viewModel.loadTorrents()
    
    // Then
    val uiState = viewModel.uiState.value
    assertFalse(uiState.isLoading)
    assertTrue(uiState.torrents.isNotEmpty())
    assertEquals("Test Torrent", uiState.torrents.first().name)
    
    coVerify { mockTorrentRepository.getAllTorrents() }
}

@Test
fun `load torrents should handle error and update ui state`() = runTest {
    // Given
    val errorMessage = "Network error"
    coEvery { mockTorrentRepository.getAllTorrents() } throws Exception(errorMessage)
    
    // When
    viewModel.loadTorrents()
    
    // Then
    val uiState = viewModel.uiState.value
    assertFalse(uiState.isLoading)
    assertEquals(errorMessage, uiState.error)
    
    coVerify { mockTorrentRepository.getAllTorrents() }
}

@Test
fun `pause torrent should call repository and update state`() = runTest {
    // Given
    val torrentHash = "abc123"
    val torrents = listOf(Torrent(hash = torrentHash, name = "Test"))
    coEvery { mockTorrentRepository.getAllTorrents() } returns flowOf(torrents)
    coEvery { mockTorrentRepository.pauseTorrent(torrentHash) } returns true
    
    // Load torrents first
    viewModel.loadTorrents()
    
    // When
    viewModel.pauseTorrent(torrentHash)
    
    // Then
    coVerify { mockTorrentRepository.pauseTorrent(torrentHash) }
}

@Test
fun `resume torrent should call repository and update state`() = runTest {
    // Given
    val torrentHash = "abc123"
    val torrents = listOf(Torrent(hash = torrentHash, name = "Test"))
    coEvery { mockTorrentRepository.getAllTorrents() } returns flowOf(torrents)
    coEvery { mockTorrentRepository.resumeTorrent(torrentHash) } returns true
    
    // Load torrents first
    viewModel.loadTorrents()
    
    // When
    viewModel.resumeTorrent(torrentHash)
    
    // Then
    coVerify { mockTorrentRepository.resumeTorrent(torrentHash) }
}

@Test
fun `delete torrent should call repository and update state`() = runTest {
    // Given
    val torrentHash = "abc123"
    val torrents = listOf(Torrent(hash = torrentHash, name = "Test"))
    coEvery { mockTorrentRepository.getAllTorrents() } returns flowOf(torrents)
    coEvery { mockTorrentRepository.deleteTorrent(torrentHash, false) } returns true
    
    // Load torrents first
    viewModel.loadTorrents()
    
    // When
    viewModel.deleteTorrent(torrentHash, false)
    
    // Then
    coVerify { mockTorrentRepository.deleteTorrent(torrentHash, false) }
}

@Test
fun `refresh torrents should call repository and update state`() = runTest {
    // Given
    val torrents = listOf(
        Torrent(hash = "abc123", name = "Test", state = "downloading"),
        Torrent(hash = "def456", name = "Test 2", state = "completed")
    )
    coEvery { mockTorrentRepository.refreshTorrents() } returns Result.success(torrents)
    coEvery { mockTorrentRepository.getAllTorrents() } returns flowOf(torrents)
    
    // When
    viewModel.refreshTorrents()
    
    // Then
    coVerify { mockTorrentRepository.refreshTorrents() }
    coVerify { mockTorrentRepository.getAllTorrents() }
}
```

**Step 4: Create TestApplication**
```kotlin
// File: Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/TestApplication.kt

@HiltAndroidApp
class TestApplication : Application()
```

**Step 5: Update test/AndroidManifest.xml**
```xml
<!-- File: Connectors/qBitConnect/qBitConnector/src/test/AndroidManifest.xml -->
<manifest xmlns:android="http://schemas.android.com/apk/res/android">
    <application android:name=".TestApplication" />
</manifest>
```

**Step 6: Run Tests**
```bash
cd Connectors/qBitConnect
./gradlew test --tests "*TorrentListViewModelTest*"
```

#### 17.1 Complete TorrentRepositoryTest (Day 17)

**File**: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/repositories/TorrentRepositoryTest.kt`

**Step 1: Remove @Disabled Annotation**
```kotlin
// Remove: @Disabled("Temporarily disabled for release build")

// Add proper annotations:
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TorrentRepositoryTest {
```

**Step 2: Fix Flow Testing with Proper Coroutine Handling**
```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

@MockK
private lateinit var mockApi: QBittorrentApiClient

@MockK
private lateinit var mockDao: TorrentDao

private lateinit var repository: TorrentRepository

@Before
fun setup() {
    MockKAnnotations.init(this, relaxUnitFun = true)
    repository = TorrentRepository(mockApi, mockDao, StandardTestDispatcher)
}

@Test
fun `getAllTorrents should return flow from dao`() = runTest {
    // Given
    val torrents = listOf(
        Torrent(hash = "abc123", name = "Test 1", state = "downloading"),
        Torrent(hash = "def456", name = "Test 2", state = "completed")
    )
    every { mockDao.getAllTorrents() } returns flowOf(torrents)
    
    // When
    val result = repository.getAllTorrents().first()
    
    // Then
    assertEquals(2, result.size)
    assertEquals("Test 1", result[0].name)
    assertEquals("Test 2", result[1].name)
    
    verify { mockDao.getAllTorrents() }
}

@Test
fun `refreshTorrents should fetch from api and update dao`() = runTest {
    // Given
    val apiTorrents = listOf(
        ApiTorrent(hash = "abc123", name = "Test 1", state = "downloading"),
        ApiTorrent(hash = "def456", name = "Test 2", state = "completed")
    )
    val dbTorrents = apiTorrents.map { it.toTorrent() }
    
    coEvery { mockApi.getAllTorrents() } returns apiTorrents
    coEvery { mockDao.insertAll(dbTorrents) } just Runs
    
    // When
    val result = repository.refreshTorrents()
    
    // Then
    assertTrue(result.isSuccess)
    val torrents = result.getOrNull()
    assertEquals(2, torrents?.size)
    assertEquals("Test 1", torrents?.get(0)?.name)
    
    coVerify { mockApi.getAllTorrents() }
    coVerify { mockDao.insertAll(dbTorrents) }
}

@Test
fun `setTorrentCategory should call api and update dao`() = runTest {
    // Given
    val torrentHash = "abc123"
    val category = "movies"
    
    coEvery { mockApi.setTorrentCategory(torrentHash, category) } returns true
    coEvery { mockDao.updateTorrentCategory(torrentHash, category) } returns 1
    
    // When
    val result = repository.setTorrentCategory(torrentHash, category)
    
    // Then
    assertTrue(result.isSuccess)
    
    coVerify { mockApi.setTorrentCategory(torrentHash, category) }
    coVerify { mockDao.updateTorrentCategory(torrentHash, category) }
}

@Test
fun `pauseTorrent should call api`() = runTest {
    // Given
    val torrentHash = "abc123"
    
    coEvery { mockApi.pauseTorrents(listOf(torrentHash)) } returns true
    
    // When
    val result = repository.pauseTorrent(torrentHash)
    
    // Then
    assertTrue(result.isSuccess)
    
    coVerify { mockApi.pauseTorrents(listOf(torrentHash)) }
}

@Test
fun `resumeTorrent should call api`() = runTest {
    // Given
    val torrentHash = "abc123"
    
    coEvery { mockApi.resumeTorrents(listOf(torrentHash)) } returns true
    
    // When
    val result = repository.resumeTorrent(torrentHash)
    
    // Then
    assertTrue(result.isSuccess)
    
    coVerify { mockApi.resumeTorrents(listOf(torrentHash)) }
}

@Test
fun `deleteTorrent should call api`() = runTest {
    // Given
    val torrentHash = "abc123"
    val deleteFiles = false
    
    coEvery { mockApi.deleteTorrents(listOf(torrentHash), deleteFiles) } returns true
    coEvery { mockDao.deleteTorrent(torrentHash) } returns 1
    
    // When
    val result = repository.deleteTorrent(torrentHash, deleteFiles)
    
    // Then
    assertTrue(result.isSuccess)
    
    coVerify { mockApi.deleteTorrents(listOf(torrentHash), deleteFiles) }
    coVerify { mockDao.deleteTorrent(torrentHash) }
}
```

**Step 3: Fix MockK Dependencies**
```kotlin
// Add relaxUnitFun = true to MockKAnnotations.init
// This fixes issues with methods returning Unit
@Before
fun setup() {
    MockKAnnotations.init(this, relaxUnitFun = true)
    repository = TorrentRepository(mockApi, mockDao, StandardTestDispatcher)
}

// For coroutines, ensure proper MockK coroutine support
@Test
fun `async repository operation should work with MockK`() = runTest {
    // Given
    coEvery { mockApi.pauseTorrents(any()) } returns true
    
    // When
    val result = repository.pauseTorrent("abc123")
    
    // Then
    assertTrue(result.isSuccess)
    coVerify { mockApi.pauseTorrents(listOf("abc123")) }
}
```

#### 18.1 Fix Remaining qBitConnect Tests (Day 18)

**File**: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/SettingsManagerTest.kt`

**Step 1: Remove @Disabled Annotation**
```kotlin
// Remove: @Disabled("Temporarily disabled for release build")

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SettingsManagerTest {
```

**Step 2: Fix Context and Shared Preferences**
```kotlin
@get:Rule
val mainDispatcherRule = MainDispatcherRule()

private lateinit var context: Context
private lateinit var settingsManager: SettingsManager

@Before
fun setup() {
    context = ApplicationProvider.getApplicationContext<Context>()
    settingsManager = SettingsManager(context)
    
    // Clear preferences before each test
    context.getSharedPreferences("qbitconnect_prefs", Context.MODE_PRIVATE)
        .edit().clear().apply()
}

@Test
fun `save and get server url should work correctly`() {
    // Given
    val serverUrl = "http://localhost:8080"
    
    // When
    settingsManager.saveServerUrl(serverUrl)
    val result = settingsManager.getServerUrl()
    
    // Then
    assertEquals(serverUrl, result)
}

@Test
fun `save and get username should work correctly`() {
    // Given
    val username = "testuser"
    
    // When
    settingsManager.saveUsername(username)
    val result = settingsManager.getUsername()
    
    // Then
    assertEquals(username, result)
}
```

**File**: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/ui/viewmodels/SettingsViewModelTest.kt`

**Step 3: Fix SettingsViewModelTest**
```kotlin
// Remove @Disabled and fix dependencies
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class SettingsViewModelTest {
    
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()
    
    @MockK
    private lateinit var mockSettingsManager: SettingsManager
    
    private lateinit var viewModel: SettingsViewModel
    
    @Before
    fun setup() {
        MockKAnnotations.init(this)
        viewModel = SettingsViewModel(mockSettingsManager)
    }
    
    @Test
    fun `save settings should call settings manager`() = runTest {
        // Given
        val settings = ServerSettings(
            serverUrl = "http://localhost:8080",
            username = "testuser",
            password = "testpass"
        )
        
        every { mockSettingsManager.saveServerUrl(settings.serverUrl) } just Runs
        every { mockSettingsManager.saveUsername(settings.username) } just Runs
        every { mockSettingsManager.savePassword(settings.password) } just Runs
        
        // When
        viewModel.saveSettings(settings)
        
        // Then
        verify { mockSettingsManager.saveServerUrl(settings.serverUrl) }
        verify { mockSettingsManager.saveUsername(settings.username) }
        verify { mockSettingsManager.savePassword(settings.password) }
    }
}
```

**File**: `Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/models/TorrentTest.kt`

**Step 4: Fix TorrentTest**
```kotlin
// Remove @Disabled and fix domain model tests
class TorrentTest {
    
    @Test
    fun `torrent constructor should create valid torrent`() {
        // Given
        val hash = "abc123"
        val name = "Test Torrent"
        val size = 1073741824L
        val progress = 50.0f
        val state = "downloading"
        val speed = 1048576L
        
        // When
        val torrent = Torrent(
            hash = hash,
            name = name,
            size = size,
            progress = progress,
            state = state,
            speed = speed
        )
        
        // Then
        assertEquals(hash, torrent.hash)
        assertEquals(name, torrent.name)
        assertEquals(size, torrent.size)
        assertEquals(progress, torrent.progress)
        assertEquals(state, torrent.state)
        assertEquals(speed, torrent.speed)
    }
    
    @Test
    fun `torrent isDownloading should return true for downloading state`() {
        // Given
        val torrent = Torrent(hash = "abc123", state = "downloading")
        
        // When
        val result = torrent.isDownloading()
        
        // Then
        assertTrue(result)
    }
    
    @Test
    fun `torrent isDownloading should return false for completed state`() {
        // Given
        val torrent = Torrent(hash = "abc123", state = "completed")
        
        // When
        val result = torrent.isDownloading()
        
        // Then
        assertFalse(result)
    }
    
    @Test
    fun `torrent getProgressPercent should return formatted percentage`() {
        // Given
        val torrent = Torrent(hash = "abc123", progress = 75.5f)
        
        // When
        val result = torrent.getProgressPercent()
        
        // Then
        assertEquals("75.5%", result)
    }
}
```

### DAY 19-20: SHARECONNECTOR TEST FIXES

#### 19.1 Rewrite OnboardingIntegrationTest for Compose (Day 19)

**File**: `ShareConnector/src/androidTest/kotlin/com/shareconnect/OnboardingIntegrationTest.kt`

**Current Issue**: "Test needs to be rewritten for Compose UI - onboarding uses Compose, not XML layouts with view IDs"

**Step 1: Replace with Compose Testing Framework**
```kotlin
// Remove: @Disabled("Test needs to be rewritten for Compose UI - onboarding uses Compose, not XML layouts with view IDs")

// Replace with proper Compose test:
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class OnboardingIntegrationTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()
    
    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @Before
    fun setup() {
        hiltRule.inject()
        
        // Clear onboarding state
        val context = composeTestRule.activity.applicationContext
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit().clear().apply()
    }

    @Test
    fun `complete onboarding flow should save preferences and launch main app`() {
        // Launch app
        composeTestRule.setContent {
            ShareConnectTheme {
                OnboardingNavigation()
            }
        }

        // Step 1: Welcome screen
        composeTestRule
            .onNodeWithText("Welcome to ShareConnect")
            .assertIsDisplayed()
            
        composeTestRule
            .onNodeWithText("Get Started")
            .performClick()

        // Step 2: Onboarding screens
        // Profile creation screen
        composeTestRule
            .onNodeWithText("Add Your First Service")
            .assertIsDisplayed()

        // Fill in profile form
        composeTestRule
            .onNodeWithText("Service Name")
            .performTextInput("Test MeTube")

        composeTestRule
            .onNodeWithText("Server URL")
            .performTextInput("http://test-server.com")

        composeTestRule
            .onNodeWithText("Save Profile")
            .performClick()

        // Success confirmation
        composeTestRule
            .onNodeWithText("Profile Created")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Continue")
            .performClick()

        // Step 3: Complete onboarding
        composeTestRule
            .onNodeWithText("Setup Complete")
            .assertIsDisplayed()

        composeTestRule
            .onNodeWithText("Start Using ShareConnect")
            .performClick()

        // Verify main app is launched (onboarding screen is no longer displayed)
        composeTestRule
            .onNodeWithText("Welcome to ShareConnect")
            .assertDoesNotExist()

        // Verify onboarding preferences are saved
        val context = composeTestRule.activity.applicationContext
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("onboarding_completed", false))
        
        // Verify profile was created
        val profileManager = ProfileManager.getInstance(context)
        val profiles = profileManager.getAllProfiles()
        assertTrue(profiles.isNotEmpty())
        
        val testProfile = profiles.find { it.name == "Test MeTube" }
        assertNotNull(testProfile)
        assertEquals("http://test-server.com", testProfile?.serverUrl)
    }

    @Test
    fun `skip onboarding should work correctly`() {
        // Launch app
        composeTestRule.setContent {
            ShareConnectTheme {
                OnboardingNavigation()
            }
        }

        // Navigate to welcome screen
        composeTestRule
            .onNodeWithText("Welcome to ShareConnect")
            .assertIsDisplayed()

        // Look for skip option (if available)
        composeTestRule
            .onNodeWithText("Skip")
            .performClick()

        // Verify onboarding is marked as completed
        val context = composeTestRule.activity.applicationContext
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        assertTrue(prefs.getBoolean("onboarding_completed", false))
    }

    @Test
    fun `onboarding should not show if already completed`() {
        // Set onboarding as completed
        val context = composeTestRule.activity.applicationContext
        val prefs = context.getSharedPreferences("onboarding_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("onboarding_completed", true).apply()

        // Launch main activity
        composeTestRule.setContent {
            ShareConnectTheme {
                ShareConnectNavigation()
            }
        }

        // Verify onboarding screen is not displayed
        composeTestRule
            .onNodeWithText("Welcome to ShareConnect")
            .assertDoesNotExist()

        // Verify main screen is displayed
        composeTestRule
            .onNodeWithText("ShareConnect")
            .assertIsDisplayed()
    }
}
```

**Step 2: Update Onboarding Components for Testability**
```kotlin
// File: ShareConnector/src/main/kotlin/com/shareconnect/onboarding/components/OnboardingWelcome.kt

@Composable
fun OnboardingWelcome(
    onGetStarted: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Logo
        Icon(
            Icons.Share,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )
        
        Spacer(modifier = Modifier.height(32.dp))
        
        // Title
        Text(
            text = stringResource(R.string.welcome_title),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Subtitle
        Text(
            text = stringResource(R.string.welcome_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(48.dp))
        
        // Get Started button
        Button(
            onClick = onGetStarted,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.get_started))
        }
    }
}

// Add test tags for better testability
@Composable
fun TestTaggedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.testTag("button_$text")
    ) {
        Text(text)
    }
}
```

#### 20.1 Fix SecurityAccessManagerTest (Day 20)

**File**: `ShareConnector/src/test/kotlin/com/shareconnect/SecurityAccessManagerTest.kt`

**Current Issue**: "SecurityAccessManager requires proper Android context setup - tested via instrumentation tests"

**Step 1: Remove @Disabled and Fix Context Setup**
```kotlin
// Remove: @Disabled("SecurityAccessManager requires proper Android context setup - tested via instrumentation tests")

// Replace with proper unit test:
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = TestApplication::class)
class SecurityAccessManagerTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var context: Context
    private lateinit var securityManager: SecurityAccessManager

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext<Context>()
        securityManager = SecurityAccessManager.getInstance(context)
        
        // Clear any existing security data
        context.getSharedPreferences("security_access_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    @After
    fun cleanup() {
        context.getSharedPreferences("security_access_prefs", Context.MODE_PRIVATE)
            .edit().clear().apply()
    }

    @Test
    fun `set pin should save and validate correctly`() = runTest {
        // Given
        val pin = "1234"
        
        // When
        val setResult = securityManager.setPin(pin)
        val validationResult = securityManager.validatePin(pin)
        
        // Then
        assertTrue(setResult.isSuccess)
        assertTrue(validationResult)
    }

    @Test
    fun `validate pin should return false for incorrect pin`() = runTest {
        // Given
        val correctPin = "1234"
        val incorrectPin = "5678"
        
        // When
        securityManager.setPin(correctPin)
        val validationResult = securityManager.validatePin(incorrectPin)
        
        // Then
        assertFalse(validationResult)
    }

    @Test
    fun `is security enabled should return false initially`() {
        // When
        val result = securityManager.isSecurityEnabled()
        
        // Then
        assertFalse(result)
    }

    @Test
    fun `enable security should set enabled flag`() = runTest {
        // Given
        val pin = "1234"
        securityManager.setPin(pin)
        
        // When
        val enableResult = securityManager.enableSecurity()
        val isEnabled = securityManager.isSecurityEnabled()
        
        // Then
        assertTrue(enableResult.isSuccess)
        assertTrue(isEnabled)
    }

    @Test
    fun `session timeout should work correctly`() = runTest {
        // Given
        val pin = "1234"
        securityManager.setPin(pin)
        securityManager.enableSecurity()
        
        // When
        val sessionStart = System.currentTimeMillis()
        securityManager.startSession()
        
        // Simulate time passing
        val currentTime = sessionStart + (5 * 60 * 1000 + 1000) // 5 minutes + 1 second
        
        val isSessionValid = securityManager.isSessionValid(currentTime)
        
        // Then
        assertFalse(isSessionValid)
    }

    @Test
    fun `authenticate should work with correct pin`() = runTest {
        // Given
        val pin = "1234"
        securityManager.setPin(pin)
        securityManager.enableSecurity()
        
        // When
        val authResult = securityManager.authenticate(pin)
        
        // Then
        assertTrue(authResult.isSuccess)
        assertTrue(securityManager.isSessionValid())
    }

    @Test
    fun `authenticate should fail with incorrect pin`() = runTest {
        // Given
        val correctPin = "1234"
        val incorrectPin = "5678"
        securityManager.setPin(correctPin)
        securityManager.enableSecurity()
        
        // When
        val authResult = securityManager.authenticate(incorrectPin)
        
        // Then
        assertTrue(authResult.isFailure)
        assertFalse(securityManager.isSessionValid())
    }

    @Test
    fun `clear session should end current session`() = runTest {
        // Given
        val pin = "1234"
        securityManager.setPin(pin)
        securityManager.enableSecurity()
        securityManager.authenticate(pin)
        
        // When
        securityManager.clearSession()
        
        // Then
        assertFalse(securityManager.isSessionValid())
    }
}
```

**Step 2: Add Integration Test Component**
```kotlin
// File: ShareConnector/src/androidTest/kotlin/com/shareconnect/SecurityAccessIntegrationTest.kt

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@HiltAndroidTest
class SecurityAccessIntegrationTest {

    @get:Rule
    var hiltRule = HiltAndroidRule(this)

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Before
    fun setup() {
        hiltRule.inject()
    }

    @Test
    fun `security dialog should show and work correctly`() {
        // Set up security first
        val securityManager = SecurityAccessManager.getInstance(composeTestRule.activity)
        runBlocking {
            securityManager.setPin("1234")
            securityManager.enableSecurity()
            securityManager.clearSession() // Force re-authentication
        }

        // Launch main activity
        composeTestRule.setContent {
            ShareConnectTheme {
                MainActivityContent()
            }
        }

        // Verify PIN dialog appears
        composeTestRule
            .onNodeWithText("Enter PIN")
            .assertIsDisplayed()

        // Enter incorrect PIN
        composeTestRule
            .onNodeWithText("PIN")
            .performTextInput("5678")

        composeTestRule
            .onNodeWithText("Authenticate")
            .performClick()

        // Verify error message
        composeTestRule
            .onNodeWithText("Incorrect PIN")
            .assertIsDisplayed()

        // Enter correct PIN
        composeTestRule
            .onNodeWithText("PIN")
            .performTextClearance()
            .performTextInput("1234")

        composeTestRule
            .onNodeWithText("Authenticate")
            .performClick()

        // Verify main app content appears
        composeTestRule
            .onNodeWithText("ShareConnect")
            .assertIsDisplayed()

        // Verify PIN dialog is gone
        composeTestRule
            .onNodeWithText("Enter PIN")
            .assertDoesNotExist()
    }
}
```

---

## WEEK 5: COMPREHENSIVE TEST SUITE ENHANCEMENT

### DAY 21-23: MISSING TEST FILE CREATION

#### 21.1 Create Comprehensive Test Templates (Day 21)

**API Client Test Template**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/templates/ApiClientTestTemplate.kt

/**
 * Template for testing API clients with comprehensive coverage
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
abstract class ApiClientTestTemplate<T, R> {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected abstract val apiClient: T
    protected abstract val mockHttpClient: OkHttpClient
    protected abstract val mockGson: Gson
    
    // Template test methods
    protected open fun createSuccessResponse(body: String): MockResponse {
        return MockResponse()
            .setBody(body)
            .setResponseCode(200)
            .addHeader("Content-Type", "application/json")
    }

    protected open fun createErrorResponse(code: Int, message: String): MockResponse {
        return MockResponse()
            .setBody("""{"error": "$message"}""")
            .setResponseCode(code)
            .addHeader("Content-Type", "application/json")
    }

    protected open fun <R> enqueueResponse(response: R) {
        val json = Gson().toJson(response)
        mockHttpClient.enqueue(createSuccessResponse(json))
    }

    protected open fun enqueueError(code: Int, message: String) {
        mockHttpClient.enqueue(createErrorResponse(code, message))
    }

    // Common test patterns
    @Test
    open fun `api call should return success on 200 response`() = runTest {
        // Given
        val expectedResponse = createExpectedSuccessResponse()
        enqueueResponse(expectedResponse)
        
        // When
        val result = callApiMethod()
        
        // Then
        assertTrue(result.isSuccess)
        assertEquals(expectedResponse, result.getOrNull())
    }

    @Test
    open fun `api call should return failure on error response`() = runTest {
        // Given
        val errorMessage = "API Error"
        enqueueError(400, errorMessage)
        
        // When
        val result = callApiMethod()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    @Test
    open fun `api call should handle network error`() = runTest {
        // Given
        mockHttpClient.enqueue(MockResponse().setSocketException(Exception("Network error")))
        
        // When
        val result = callApiMethod()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull()?.message?.contains("Network error") == true)
    }

    // Abstract methods to be implemented by specific test classes
    protected abstract fun createExpectedSuccessResponse(): R
    protected abstract suspend fun callApiMethod(): Result<R>
}
```

**Repository Test Template**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/templates/RepositoryTestTemplate.kt

/**
 * Template for testing repositories with proper Flow testing
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
abstract class RepositoryTestTemplate<T, R> {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected abstract val repository: T
    protected abstract val mockDao: Any
    protected abstract val mockApiClient: Any

    // Flow testing utilities
    protected suspend fun <T> Flow<T>.testFlow(): List<T> {
        return this.toList()
    }

    protected suspend fun <T> Flow<T>.firstOrNull(): T? {
        return this.first()
    }

    // Common repository test patterns
    @Test
    open fun `get all should return flow from dao`() = runTest {
        // Given
        val expectedData = createExpectedDataList()
        whenever(mockGetDataFromDao()).thenReturn(flowOf(expectedData))
        
        // When
        val result = repository.getAll().testFlow()
        
        // Then
        assertEquals(expectedData, result)
        verify(mockGetDataFromDao())
    }

    @Test
    open fun `refresh should fetch from api and update dao`() = runTest {
        // Given
        val apiData = createApiData()
        val dbData = convertToDbData(apiData)
        
        whenever(mockGetDataFromApi()).thenReturn(apiData)
        coWhenever(mockInsertToDao()).just(Runs)
        
        // When
        val result = repository.refresh()
        
        // Then
        assertTrue(result.isSuccess)
        verify(mockGetDataFromApi())
        coVerify(mockInsertToDao())
    }

    @Test
    open fun `refresh should handle api error`() = runTest {
        // Given
        val errorMessage = "API Error"
        whenever(mockGetDataFromApi()).thenThrow(Exception(errorMessage))
        
        // When
        val result = repository.refresh()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(errorMessage, result.exceptionOrNull()?.message)
    }

    // Abstract methods to be implemented
    protected abstract fun createExpectedDataList(): List<R>
    protected abstract fun createApiData(): Any
    protected abstract fun convertToDbData(apiData: Any): List<R>
    protected abstract suspend fun repository.getAll(): Flow<List<R>>
    protected abstract suspend fun repository.refresh(): Result<List<R>>
    
    // Mock verification methods
    protected abstract fun mockGetDataFromDao(): Any
    protected abstract fun mockGetDataFromApi(): Any
    protected abstract fun mockInsertToDao(): Any
}
```

**ViewModel Test Template**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/templates/ViewModelTestTemplate.kt

/**
 * Template for testing ViewModels with proper state testing
 */
@ExperimentalCoroutinesApi
abstract class ViewModelTestTemplate<T : BaseViewModel> {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    protected abstract val viewModel: T

    // State testing utilities
    protected fun <T> StateFlow<T>.testState(): T {
        return this.value
    }

    protected fun <T> SharedFlow<T>.testEvents(): List<T> {
        return runBlocking { this.toList() }
    }

    // Common ViewModel test patterns
    @Test
    open fun `initial state should be correct`() {
        // When
        val initialState = viewModel.uiState.testState()
        
        // Then
        assertEquals(createExpectedInitialState(), initialState)
    }

    @Test
    open fun `loading state should be set during async operation`() = runTest {
        // Given
        setupAsyncOperation()
        
        // When
        viewModel.performAsyncOperation()
        
        // Then
        val loadingState = viewModel.uiState.testState()
        assertTrue(loadingState.isLoading)
    }

    @Test
    open fun `error state should be set on operation failure`() = runTest {
        // Given
        val errorMessage = "Test Error"
        setupOperationFailure(errorMessage)
        
        // When
        viewModel.performAsyncOperation()
        
        // Then
        val errorState = viewModel.uiState.testState()
        assertEquals(errorMessage, errorState.error)
    }

    @Test
    open fun `success state should be set on operation success`() = runTest {
        // Given
        setupOperationSuccess()
        
        // When
        viewModel.performAsyncOperation()
        
        // Then
        val successState = viewModel.uiState.testState()
        assertNotNull(successState.data)
        assertFalse(successState.isLoading)
        assertNull(successState.error)
    }

    // Abstract methods
    protected abstract fun createExpectedInitialState(): Any
    protected abstract suspend fun setupAsyncOperation()
    protected abstract suspend fun setupOperationFailure(errorMessage: String)
    protected abstract suspend fun setupOperationSuccess()
    protected abstract suspend fun T.performAsyncOperation()
}
```

#### 22.1 Implement Proper Flow Testing Patterns (Day 22)

**Flow Testing Utilities**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/utils/FlowTestUtils.kt

object FlowTestUtils {
    
    /**
     * Collect all emissions from a Flow within a timeout
     */
    suspend fun <T> Flow<T>.collectAll(timeoutMs: Long = 5000): List<T> {
        val result = mutableListOf<T>()
        val job = launch {
            collect { result.add(it) }
        }
        
        delay(timeoutMs)
        job.cancel()
        
        return result
    }
    
    /**
     * Get the first emission from a Flow with timeout
     */
    suspend fun <T> Flow<T>.firstWithTimeout(timeoutMs: Long = 5000): T? {
        return try {
            withTimeout(timeoutMs) { first() }
        } catch (e: TimeoutCancellationException) {
            null
        }
    }
    
    /**
     * Get emissions from a StateFlow over time
     */
    suspend fun <T> StateFlow<T>.collectStates(count: Int, intervalMs: Long = 100): List<T> {
        val states = mutableListOf<T>()
        repeat(count) { index ->
            states.add(value)
            if (index < count - 1) {
                delay(intervalMs)
            }
        }
        return states
    }
    
    /**
     * Test Flow transformations
     */
    suspend fun <T, R> Flow<T>.testTransformation(
        inputs: List<T>,
        transformation: Flow<T>.() -> Flow<R>
    ): List<R> {
        val inputFlow = flow {
            inputs.forEach { emit(it) }
        }
        
        return transformation(inputFlow).collectAll()
    }
    
    /**
     * Verify Flow emissions
     */
    suspend fun <T> Flow<T>.verifyEmissions(
        expectedEmissions: List<T>,
        timeoutMs: Long = 5000
    ) {
        val actualEmissions = collectAll(timeoutMs)
        assertEquals(expectedEmissions, actualEmissions)
    }
}

/**
 * Test dispatcher for consistent testing
 */
class TestDispatcherRule : TestWatcher() {
    val testDispatcher = StandardTestDispatcher()
    
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }
    
    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
    
    fun advanceTimeBy(delayMs: Long) {
        testDispatcher.scheduler.advanceTimeBy(delayMs)
    }
    
    fun advanceUntilIdle() {
        testDispatcher.scheduler.runCurrent()
    }
}
```

**Repository Flow Testing Example**
```kotlin
// File: Connectors/qBitConnect/qBitConnector/src/test/kotlin/com/shareconnect/qbitconnect/data/repositories/FlowTestingExample.kt

@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class TorrentRepositoryFlowTest {

    @get:Rule
    val flowTestRule = FlowTestUtils.TestDispatcherRule()

    @MockK
    private lateinit var mockApi: QBittorrentApiClient

    @MockK
    private lateinit var mockDao: TorrentDao

    private lateinit var repository: TorrentRepository

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        repository = TorrentRepository(mockApi, mockDao, flowTestRule.testDispatcher)
    }

    @Test
    fun `torrent state flow should emit updates correctly`() = runTest {
        // Given
        val initialTorrents = listOf(
            Torrent(hash = "abc123", name = "Test 1", state = "downloading", progress = 0.0f)
        )
        val updatedTorrents = listOf(
            Torrent(hash = "abc123", name = "Test 1", state = "downloading", progress = 50.0f)
        )
        
        every { mockDao.getAllTorrents() } returns flowOf(initialTorrents, updatedTorrents)
        
        // When
        val emissions = FlowTestUtils.collectStates(repository.getAllTorrents(), 2)
        
        // Then
        assertEquals(2, emissions.size)
        assertEquals("Test 1", emissions[0].first().name)
        assertEquals(0.0f, emissions[0].first().progress)
        assertEquals(50.0f, emissions[1].first().progress)
    }

    @Test
    fun `refresh torrents should update flow correctly`() = runTest {
        // Given
        val initialTorrents = emptyList<Torrent>()
        val apiTorrents = listOf(
            ApiTorrent(hash = "abc123", name = "Test 1", state = "downloading", progress = 25.0f)
        )
        val dbTorrents = apiTorrents.map { it.toTorrent() }
        
        every { mockDao.getAllTorrents() } returns flowOf(initialTorrents, dbTorrents)
        coEvery { mockApi.getAllTorrents() } returns apiTorrents
        coEvery { mockDao.insertAll(dbTorrents) } just Runs
        
        // When
        val emissions = FlowTestUtils.collectStates(repository.getAllTorrents(), 3)
        
        // Initially empty
        assertEquals(0, emissions[0].size)
        
        // Refresh torrents
        repository.refreshTorrents()
        flowTestRule.advanceUntilIdle()
        
        // Should get updated torrents
        assertEquals(1, emissions[2].size)
        assertEquals("Test 1", emissions[2].first().name)
        assertEquals(25.0f, emissions[2].first().progress)
    }

    @Test
    fun `torrent speed flow should emit periodic updates`() = runTest {
        // Given
        val torrents = listOf(
            Torrent(hash = "abc123", name = "Test 1", speed = 0L),
            Torrent(hash = "def456", name = "Test 2", speed = 1048576L)
        )
        
        every { mockDao.getAllTorrents() } returns flowOf(torrents)
        
        // When
        val totalSpeedFlow = repository.getTotalDownloadSpeed()
        val speedEmissions = FlowTestUtils.collectStates(totalSpeedFlow, 2)
        
        // Then
        assertEquals(1048576L, speedEmissions[1]) // 1MB/s
    }
}
```

#### 23.1 Add Edge Case and Error Handling Tests (Day 23)

**Network Error Handling Tests**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/NetworkErrorHandlingTests.kt

/**
 * Comprehensive network error handling test suite
 */
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class NetworkErrorHandlingTests {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @MockK
    private lateinit var mockHttpClient: OkHttpClient

    @Test
    fun `should handle connection timeout`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setSocketException(SocketTimeoutException("Connection timed out"))
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is SocketTimeoutException)
    }

    @Test
    fun `should handle connection refused`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setResponseCode(503)
                .setBody("""{"error": "Service Unavailable"}""")
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(503, (result.exceptionOrNull() as? HttpException)?.code)
    }

    @Test
    fun `should handle malformed json response`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setBody("{invalid json}")
                .setResponseCode(200)
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is JsonSyntaxException)
    }

    @Test
    fun `should handle empty response body`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setBody("")
                .setResponseCode(200)
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals("Empty response", result.exceptionOrNull()?.message)
    }

    @Test
    fun `should handle rate limiting`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setResponseCode(429)
                .addHeader("Retry-After", "60")
                .setBody("""{"error": "Too Many Requests"}""")
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(429, (result.exceptionOrNull() as? HttpException)?.code)
    }

    @Test
    fun `should handle unauthorized access`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setResponseCode(401)
                .setBody("""{"error": "Unauthorized"}""")
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(401, (result.exceptionOrNull() as? HttpException)?.code)
    }

    @Test
    fun `should handle server error`() = runTest {
        // Given
        mockHttpClient.enqueue(
            MockResponse()
                .setResponseCode(500)
                .setBody("""{"error": "Internal Server Error"}""")
        )
        
        val apiClient = TestApiClient(mockHttpClient, Gson())
        
        // When
        val result = apiClient.makeRequest()
        
        // Then
        assertTrue(result.isFailure)
        assertEquals(500, (result.exceptionOrNull() as? HttpException)?.code)
    }
}
```

**Data Validation Tests**
```kotlin
// File: Tests/src/main/kotlin/com/shareconnect/test/DataValidationTests.kt

/**
 * Data validation edge case tests
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class DataValidationTests {

    @Test
    fun `should handle empty strings in model validation`() {
        // Given
        val torrent = Torrent(
            hash = "",  // Empty hash
            name = "Test",
            size = 0L,
            progress = 0.0f
        )
        
        // When
        val isValid = torrent.isValid()
        
        // Then
        assertFalse(isValid)
    }

    @Test
    fun `should handle null values in model validation`() {
        // Given
        val profile = ServerProfile(
            id = null,  // Null ID
            name = "Test",
            serverUrl = "http://test.com",
            type = "qBittorrent"
        )
        
        // When
        val isValid = profile.isValid()
        
        // Then
        assertFalse(isValid)
    }

    @Test
    fun `should handle extreme values`() {
        // Given
        val torrent = Torrent(
            hash = "abc123",
            name = "Test",
            size = Long.MAX_VALUE,  // Extreme size
            progress = 150.0f  // Invalid progress > 100%
        )
        
        // When
        val clampedProgress = torrent.clampProgress()
        
        // Then
        assertEquals(100.0f, clampedProgress)
    }

    @Test
    fun `should handle special characters in strings`() {
        // Given
        val specialChars = "!@#$%^&*()_+-=[]{}|;':\",./<>?"
        val profileName = "Test $specialChars"
        
        // When
        val sanitized = profileName.sanitize()
        
        // Then
        assertTrue(sanitized.contains(specialChars))  // Should preserve
        assertFalse(sanitized.contains("\n"))  // Should remove newlines
        assertFalse(sanitized.contains("\r"))  // Should remove carriage returns
        assertFalse(sanitized.contains("\t"))  // Should remove tabs
    }

    @Test
    fun `should handle unicode characters`() {
        // Given
        val unicodeText = "测试中文字符🚀🎉"
        
        // When
        val encoded = unicodeText.encodeToUtf8()
        val decoded = String(encoded, Charsets.UTF_8)
        
        // Then
        assertEquals(unicodeText, decoded)
    }

    @Test
    fun `should handle very long strings`() {
        // Given
        val longString = "a".repeat(10000)  // 10KB string
        
        // When
        val truncated = longString.truncate(100)  // Truncate to 100 chars
        
        // Then
        assertEquals(100, truncated.length)
        assertTrue(truncated.endsWith("..."))
    }
}
```

### DAY 24-25: TEST COVERAGE ANALYSIS & ENHANCEMENT

#### 24.1 Generate Jacoco Coverage Reports (Day 24)

**Configure Jacoco in Build Files**
```kotlin
// File: Connectors/qBitConnect/qBitConnector/build.gradle

android {
    // ...
    buildTypes {
        debug {
            testCoverageEnabled true
        }
        release {
            testCoverageEnabled false
        }
    }
}

// Add Jacoco plugin
apply plugin: 'jacoco'

jacoco {
    toolVersion = "0.8.8"
}

tasks.withType(Test) {
    jacoco {
        includeNoLocationClasses = true
        includes = ['com.shareconnect.qbitconnect.*']
        excludes = [
            '**/R.class',
            '**/R$*.class',
            '**/BuildConfig.*',
            '**/Manifest*.*',
            '**/*Test*.*',
            '**/android/**/*.*'
        ]
    }
}

task jacocoTestReport(type: JacocoReport, dependsOn: 'testDebugUnitTest') {
    group = 'Reporting'
    description = 'Generate Jacoco coverage reports'

    reports {
        xml.required = true
        html.required = true
        html.outputLocation = layout.buildDirectory.dir('reports/jacoco')
    }

    sourceDirectories.from = files(['src/main/kotlin'])
    classDirectories.from = files(
        fileTree(dir: "$buildDir/tmp/kotlin-classes/debug")
    )
    executionData.from = files(
        "$buildDir/jacoco/testDebugUnitTest.exec"
    )
}
```

**Coverage Analysis Script**
```bash
#!/bin/bash
# scripts/analyze_coverage.sh

echo "=== Analyzing Test Coverage ==="

# Generate coverage reports for all modules
echo "Generating coverage reports..."

# qBitConnect
echo "Analyzing qBitConnect coverage..."
cd Connectors/qBitConnect/qBitConnector
./gradlew jacocoTestReport

# ShareConnector
echo "Analyzing ShareConnector coverage..."
cd ../../ShareConnector
./gradlew jacocoTestReport

# Generate combined coverage analysis
cd ../..
python3 scripts/coverage_analyzer.py

echo "Coverage analysis complete. See reports in Documentation/Coverage/"
```

**Coverage Analyzer Script**
```python
#!/usr/bin/env python3
# scripts/coverage_analyzer.py

import xml.etree.ElementTree as ET
import json
import os
from pathlib import Path

def parse_jacoco_xml(file_path):
    """Parse Jacoco XML report and return coverage metrics"""
    tree = ET.parse(file_path)
    root = tree.getroot()
    
    coverage_data = {
        'total_lines': 0,
        'covered_lines': 0,
        'total_branches': 0,
        'covered_branches': 0,
        'packages': {}
    }
    
    for package in root.findall('.//package'):
        package_name = package.get('name')
        package_data = {
            'total_lines': 0,
            'covered_lines': 0,
            'total_branches': 0,
            'covered_branches': 0,
            'classes': {}
        }
        
        for cls in package.findall('.//class'):
            class_name = cls.get('name')
            class_data = {
                'total_lines': 0,
                'covered_lines': 0,
                'total_branches': 0,
                'covered_branches': 0
            }
            
            # Process line counters
            for counter in cls.findall('.//counter'):
                counter_type = counter.get('type')
                missed = int(counter.get('missed'))
                covered = int(counter.get('covered'))
                total = missed + covered
                
                if counter_type == 'LINE':
                    class_data['total_lines'] = total
                    class_data['covered_lines'] = covered
                    package_data['total_lines'] += total
                    package_data['covered_lines'] += covered
                    coverage_data['total_lines'] += total
                    coverage_data['covered_lines'] += covered
                elif counter_type == 'BRANCH':
                    class_data['total_branches'] = total
                    class_data['covered_branches'] = covered
                    package_data['total_branches'] += total
                    package_data['covered_branches'] += covered
                    coverage_data['total_branches'] += total
                    coverage_data['covered_branches'] += covered
            
            package_data['classes'][class_name] = class_data
        
        coverage_data['packages'][package_name] = package_data
    
    return coverage_data

def generate_coverage_report(coverage_data, output_file):
    """Generate HTML coverage report"""
    total_line_coverage = (coverage_data['covered_lines'] / coverage_data['total_lines']) * 100 if coverage_data['total_lines'] > 0 else 0
    total_branch_coverage = (coverage_data['covered_branches'] / coverage_data['total_branches']) * 100 if coverage_data['total_branches'] > 0 else 0
    
    html_content = f"""
    <!DOCTYPE html>
    <html>
    <head>
        <title>ShareConnect Coverage Report</title>
        <style>
            body {{ font-family: Arial, sans-serif; margin: 20px; }}
            .coverage-high {{ color: green; font-weight: bold; }}
            .coverage-medium {{ color: orange; font-weight: bold; }}
            .coverage-low {{ color: red; font-weight: bold; }}
            table {{ border-collapse: collapse; width: 100%; }}
            th, td {{ border: 1px solid #ddd; padding: 8px; text-align: left; }}
            th {{ background-color: #f2f2f2; }}
        </style>
    </head>
    <body>
        <h1>ShareConnect Test Coverage Report</h1>
        
        <h2>Overall Coverage</h2>
        <p>Line Coverage: <span class="{'coverage-high' if total_line_coverage >= 80 else 'coverage-medium' if total_line_coverage >= 60 else 'coverage-low'}">{total_line_coverage:.1f}%</span></p>
        <p>Branch Coverage: <span class="{'coverage-high' if total_branch_coverage >= 80 else 'coverage-medium' if total_branch_coverage >= 60 else 'coverage-low'}">{total_branch_coverage:.1f}%</span></p>
        
        <h2>Package Coverage</h2>
        <table>
            <tr>
                <th>Package</th>
                <th>Line Coverage</th>
                <th>Branch Coverage</th>
                <th>Status</th>
            </tr>
    """
    
    for package_name, package_data in coverage_data['packages'].items():
        line_cov = (package_data['covered_lines'] / package_data['total_lines']) * 100 if package_data['total_lines'] > 0 else 0
        branch_cov = (package_data['covered_branches'] / package_data['total_branches']) * 100 if package_data['total_branches'] > 0 else 0
        status_class = 'coverage-high' if line_cov >= 80 else 'coverage-medium' if line_cov >= 60 else 'coverage-low'
        
        html_content += f"""
            <tr>
                <td>{package_name}</td>
                <td>{line_cov:.1f}%</td>
                <td>{branch_cov:.1f}%</td>
                <td class="{status_class}">{'PASS' if line_cov >= 80 else 'NEEDS WORK'}</td>
            </tr>
        """
    
    html_content += """
        </table>
    </body>
    </html>
    """
    
    with open(output_file, 'w') as f:
        f.write(html_content)

def identify_coverage_gaps(coverage_data):
    """Identify packages and classes with low coverage"""
    gaps = []
    
    for package_name, package_data in coverage_data['packages'].items():
        line_cov = (package_data['covered_lines'] / package_data['total_lines']) * 100 if package_data['total_lines'] > 0 else 0
        
        if line_cov < 80:
            gaps.append({
                'package': package_name,
                'coverage': line_cov,
                'type': 'package'
            })
            
            for class_name, class_data in package_data['classes'].items():
                class_cov = (class_data['covered_lines'] / class_data['total_lines']) * 100 if class_data['total_lines'] > 0 else 0
                if class_cov < 80:
                    gaps.append({
                        'package': package_name,
                        'class': class_name,
                        'coverage': class_cov,
                        'type': 'class'
                    })
    
    return gaps

def main():
    """Main coverage analysis function"""
    coverage_reports = []
    
    # Find all Jacoco XML reports
    for root, dirs, files in os.walk('.'):
        for file in files:
            if file == 'jacocoTestReport.xml':
                coverage_reports.append(os.path.join(root, file))
    
    all_coverage_data = {}
    
    for report_file in coverage_reports:
        module_name = os.path.basename(os.path.dirname(os.path.dirname(report_file)))
        coverage_data = parse_jacoco_xml(report_file)
        all_coverage_data[module_name] = coverage_data
        
        # Generate individual module report
        output_dir = f'Documentation/Coverage/{module_name}'
        os.makedirs(output_dir, exist_ok=True)
        generate_coverage_report(coverage_data, f'{output_dir}/coverage_report.html')
    
    # Generate combined report
    combined_output_dir = 'Documentation/Coverage/combined'
    os.makedirs(combined_output_dir, exist_ok=True)
    
    # Identify coverage gaps across all modules
    all_gaps = []
    for module_name, coverage_data in all_coverage_data.items():
        gaps = identify_coverage_gaps(coverage_data)
        for gap in gaps:
            gap['module'] = module_name
            all_gaps.append(gap)
    
    # Generate gap report
    gap_report_file = f'{combined_output_dir}/coverage_gaps.json'
    with open(gap_report_file, 'w') as f:
        json.dump(all_gaps, f, indent=2)
    
    # Generate summary report
    summary_file = f'{combined_output_dir}/coverage_summary.md'
    with open(summary_file, 'w') as f:
        f.write('# ShareConnect Coverage Summary\n\n')
        f.write(f"**Modules Analyzed:** {len(all_coverage_data)}\n\n")
        f.write(f"**Coverage Gaps Found:** {len(all_gaps)}\n\n")
        f.write("## Gaps by Module\n\n")
        
        for module_name, coverage_data in all_coverage_data.items():
            total_cov = (coverage_data['covered_lines'] / coverage_data['total_lines']) * 100 if coverage_data['total_lines'] > 0 else 0
            status = "✅ PASS" if total_cov >= 80 else "❌ NEEDS WORK"
            f.write(f"### {module_name}: {total_cov:.1f}% {status}\n")
            
            module_gaps = [gap for gap in all_gaps if gap['module'] == module_name]
            for gap in module_gaps:
                if gap['type'] == 'class':
                    f.write(f"- {gap['class']}: {gap['coverage']:.1f}%\n")
            
            f.write("\n")
    
    print(f"Coverage analysis complete. Reports generated in {combined_output_dir}")

if __name__ == '__main__':
    main()
```

#### 25.1 Add Missing Test Cases for Low Coverage Areas (Day 25)

**Identify and Address Coverage Gaps**
```bash
#!/bin/bash
# scripts/fix_coverage_gaps.sh

echo "=== Fixing Coverage Gaps ==="

# Run coverage analysis first
./scripts/analyze_coverage.sh

# Read coverage gaps
gaps_file="Documentation/Coverage/combined/coverage_gaps.json"

if [ -f "$gaps_file" ]; then
    echo "Found coverage gaps. Generating test templates..."
    
    # Generate test templates for uncovered classes
    python3 scripts/generate_missing_tests.py "$gaps_file"
else
    echo "No coverage gaps found."
fi
```

**Test Template Generator**
```python
#!/usr/bin/env python3
# scripts/generate_missing_tests.py

import json
import os
from pathlib import Path

def generate_test_for_class(module_name, class_name, coverage_data):
    """Generate test file for a class with low coverage"""
    test_template = f"""
@ExperimentalCoroutinesApi
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class {class_name}Test {{

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    // TODO: Add mock dependencies
    @MockK
    private lateinit var mockDependency: SomeDependency

    private lateinit var {class_name.lower()}: {class_name}

    @Before
    fun setup() {{
        MockKAnnotations.init(this)
        // TODO: Initialize class under test
        {class_name.lower()} = {class_name}()
    }}

    @Test
    fun `main functionality should work correctly`() = runTest {{
        // Given
        // TODO: Set up test data
        
        // When
        // TODO: Call method under test
        
        // Then
        // TODO: Verify behavior
    }}

    @Test
    fun `should handle error conditions`() = runTest {{
        // Given
        // TODO: Set up error conditions
        
        // When
        // TODO: Call method under test
        
        // Then
        // TODO: Verify error handling
    }}

    @Test
    fun `should handle edge cases`() = runTest {{
        // Given
        // TODO: Set up edge case conditions
        
        // When
        // TODO: Call method under test
        
        // Then
        // TODO: Verify edge case handling
    }}
}}
"""
    
    return test_template

def main():
    """Generate missing test files"""
    gaps_file = sys.argv[1]
    
    with open(gaps_file, 'r') as f:
        gaps = json.load(f)
    
    generated_files = []
    
    for gap in gaps:
        if gap['type'] == 'class':
            module_name = gap['module']
            class_name = gap['class']
            
            # Determine test file path
            test_dir = f"{module_name}/src/test/kotlin/{gap['package'].replace('.', '/')}"
            test_file = f"{test_dir}/{class_name}Test.kt"
            
            # Create directory if needed
            os.makedirs(test_dir, exist_ok=True)
            
            # Generate test file
            test_content = generate_test_for_class(module_name, class_name, gap)
            
            with open(test_file, 'w') as f:
                f.write(test_content)
            
            generated_files.append(test_file)
            print(f"Generated test file: {test_file}")
    
    if generated_files:
        print(f"\nGenerated {len(generated_files)} test files")
        print("Please implement the TODO items in the generated tests.")
    else:
        print("No test files generated - all classes have sufficient coverage")

if __name__ == '__main__':
    main()
```

**Coverage Verification Script**
```bash
#!/bin/bash
# scripts/verify_coverage.sh

echo "=== Verifying 100% Test Coverage ==="

# Run all tests with coverage
./gradlew test jacocoTestReport

# Analyze coverage
python3 scripts/coverage_analyzer.py

# Check if coverage meets requirements
coverage_file="Documentation/Coverage/combined/coverage_summary.md"

if [ -f "$coverage_file" ]; then
    # Extract coverage percentages
    line_coverage=$(grep -o "Line Coverage: [0-9.]*%" "$coverage_file" | head -1)
    
    # Extract numeric value
    coverage_value=$(echo "$line_coverage" | grep -o "[0-9.]*")
    
    echo "Current Line Coverage: $coverage_value%"
    
    # Check if coverage is 100%
    if (( $(echo "$coverage_value >= 99.9" | bc -l) )); then
        echo "✅ COVERAGE REQUIREMENT MET: $coverage_value%"
        exit 0
    else
        echo "❌ COVERAGE REQUIREMENT NOT MET: $coverage_value% (require 100%)"
        exit 1
    fi
else
    echo "❌ Coverage report not found"
    exit 1
fi
```

---

## PHASE 2 COMPLETION CRITERIA

### Technical Requirements
- [ ] All 8 disabled tests reactivated and passing
- [ ] 100% test coverage across all modules
- [ ] All test templates created and documented
- [ ] Flow testing patterns properly implemented
- [ ] Edge cases and error handling fully tested

### Testing Infrastructure
- [ ] Jacoco coverage reports generated for all modules
- [ ] Coverage analysis automation working
- [ ] Test templates for API clients, repositories, ViewModels
- [ ] Proper coroutine testing with MockK support
- [ ] Compose UI testing framework integrated

### Quality Assurance
- [ ] Zero disabled tests remaining
- [ ] All new tests passing consistently
- [ ] Coverage gaps identified and addressed
- [ ] Error handling and edge cases covered
- [ ] Performance and integration tests updated

### Success Metrics
- [ ] **100%** test coverage (line and branch)
- [ ] **0** disabled tests
- [ ] **< 2 seconds** average test execution time
- [ ] **0** flaky tests
- [ ] **100%** of critical code paths tested

---

## NEXT STEPS

After Phase 2 completion:

1. **Proceed to Phase 3**: Complete Documentation
2. **Run full regression test**: Ensure Phase 2 changes don't break existing functionality  
3. **Update test documentation**: Document all test patterns and frameworks
4. **Prepare for Phase 3**: Set up documentation generation tools

**Phase 2 delivers** a comprehensive, 100% tested codebase with zero disabled tests and proper coverage across all modules. This provides the foundation for complete documentation creation in Phase 3.