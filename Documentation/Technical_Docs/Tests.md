# Tests

## Overview and Purpose

Tests is a comprehensive testing framework module that provides shared testing utilities, base classes, and infrastructure for all ShareConnect applications. It includes test data factories, mock implementations, test rules, and utilities for consistent testing across the entire project.

## Architecture and Components

Tests follows a utility library pattern with shared testing infrastructure:

### Core Components
- **TestApplication**: Base application class for testing
- **TestDataFactory**: Factory for generating test data
- **MockImplementations**: Mock versions of key services
- **TestRules**: JUnit test rules for setup/teardown
- **TestUtils**: Common testing utilities

### Test Categories
- **Unit Tests**: Isolated component testing
- **Integration Tests**: Component interaction testing
- **Instrumentation Tests**: Device/emulator testing
- **Automation Tests**: UI automation testing

## API Reference

### TestDataFactory
```kotlin
object TestDataFactory {
    fun createServerProfile(
        id: String = UUID.randomUUID().toString(),
        name: String = "Test Server",
        serviceType: ServiceType = ServiceType.METUBE
    ): ServerProfile

    fun createHistoryItem(
        url: String = "https://example.com/video",
        title: String = "Test Video"
    ): HistoryItem

    fun createTheme(): ThemeData
    fun createUserProfile(): UserProfile
}
```

### TestApplication
```kotlin
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Test-specific initialization
        setupTestDependencies()
    }

    fun setupTestDependencies()
    fun resetTestState()
}
```

## Key Classes and Their Responsibilities

### TestDataFactory
- **Responsibilities**:
  - Consistent test data generation
  - Edge case data creation
  - Data validation for tests
  - Test data cleanup

### TestApplication
- **Responsibilities**:
  - Test environment setup
  - Dependency injection for tests
  - Test database management
  - Mock service initialization

### MockImplementations
- **Responsibilities**:
  - Service interface mocking
  - Deterministic behavior simulation
  - Error condition testing
  - Performance testing support

## Data Models

### TestData
```kotlin
data class TestData(
    val profiles: List<ServerProfile> = emptyList(),
    val historyItems: List<HistoryItem> = emptyList(),
    val themes: List<ThemeData> = emptyList(),
    val users: List<UserProfile> = emptyList()
)
```

## Usage Examples

### Using Test Data Factory
```kotlin
@Test
fun testProfileCreation() {
    val profile = TestDataFactory.createServerProfile(
        name = "Test MeTube Server",
        serviceType = ServiceType.METUBE
    )

    assertEquals("Test MeTube Server", profile.name)
    assertEquals(ServiceType.METUBE, profile.serviceType)
}
```

### Setting Up Test Application
```kotlin
class ProfileManagerTest {
    @get:Rule
    val testRule = TestRule()

    private lateinit var testApp: TestApplication

    @Before
    fun setup() {
        testApp = TestApplication()
        testApp.setupTestDependencies()
    }

    @After
    fun teardown() {
        testApp.resetTestState()
    }
}
```

## Dependencies

### Testing Frameworks
- `junit:junit:4.13.2` - Unit testing
- `org.mockito:mockito-core:5.8.0` - Mocking
- `androidx.test:core:1.7.0` - Android test core
- `androidx.test:runner:1.7.0` - Test runner

### Android Testing
- `androidx.test.ext:junit:1.3.0` - JUnit extensions
- `androidx.test.espresso:espresso-core:3.7.0` - UI testing
- `org.robolectric:robolectric:4.16` - Unit testing on JVM

---

*For more information, visit [https://shareconnect.org/docs/tests](https://shareconnect.org/docs/tests)*