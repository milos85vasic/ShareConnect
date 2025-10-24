# ThemeSync

## Overview and Purpose

ThemeSync is a synchronization module that enables real-time theme sharing and consistency across all ShareConnect applications. It allows users to create custom themes on one device and have them automatically sync to all other ShareConnect apps on connected devices. The module handles theme data persistence, conflict resolution, and cross-device synchronization.

## Architecture and Components

ThemeSync is built on top of the Asinka IPC framework and follows a repository pattern:

### Core Components
- **ThemeSyncManager**: Main synchronization orchestrator
- **ThemeRepository**: Local theme data persistence
- **ThemeApplier**: Applies themes to Android activities
- **SyncableTheme**: Theme data model for synchronization

### Data Layer
- **ThemeDao**: Room database access object
- **ThemeDatabase**: Local SQLite database
- **ThemeData**: Internal theme representation
- **SyncableTheme**: Network synchronization model

## API Reference

### ThemeSyncManager
```kotlin
class ThemeSyncManager private constructor(
    context: Context,
    appIdentifier: String,
    appName: String,
    appVersion: String,
    asinkaClient: AsinkaClient,
    repository: ThemeRepository
) {
    suspend fun start(): Unit
    suspend fun stop(): Unit
    fun getCurrentTheme(): ThemeData?
    suspend fun setTheme(theme: ThemeData): Boolean
    suspend fun createCustomTheme(theme: ThemeData): ThemeData?
    suspend fun deleteCustomTheme(themeId: String): Boolean
    val themeChangeFlow: Flow<ThemeData>
}
```

### ThemeRepository
```kotlin
class ThemeRepository(context: Context) {
    suspend fun initializeDefaultThemes(): Unit
    suspend fun getAllThemes(): List<ThemeData>
    suspend fun getThemeById(id: String): ThemeData?
    suspend fun saveTheme(theme: ThemeData): Boolean
    suspend fun deleteTheme(id: String): Boolean
    suspend fun getCustomThemes(): List<ThemeData>
    fun observeThemeChanges(): Flow<ThemeData>
}
```

### ThemeApplier
```kotlin
class ThemeApplier {
    fun applyTheme(activity: AppCompatActivity, theme: ThemeData): Unit
    fun applyThemeToContext(context: Context, theme: ThemeData): Context
    fun getThemeResourceId(theme: ThemeData): Int
}
```

## Key Classes and Their Responsibilities

### ThemeSyncManager
- **Responsibilities**:
  - Synchronization lifecycle management (start/stop)
  - Theme change event broadcasting
  - Conflict resolution for concurrent theme modifications
  - Integration with Asinka client for cross-device sync
  - Port conflict handling for gRPC server binding

### ThemeRepository
- **Responsibilities**:
  - Local theme data persistence using Room database
  - Default theme initialization
  - CRUD operations for theme data
  - Theme change observation and notification
  - Data migration and schema management

### ThemeApplier
- **Responsibilities**:
  - Runtime theme application to Android activities
  - Context theming for consistent UI appearance
  - Theme resource resolution and mapping
  - Dynamic theme switching without activity restart

### SyncableTheme
- **Responsibilities**:
  - Theme data serialization for network transmission
  - Version control and conflict resolution
  - Cross-platform theme compatibility
  - Data integrity validation

## Data Models

### ThemeData
```kotlin
data class ThemeData(
    val id: String,
    val name: String,
    val isCustom: Boolean = false,
    val isDark: Boolean = false,
    val colors: ThemeColors,
    val createdAt: Long = System.currentTimeMillis(),
    val modifiedAt: Long = System.currentTimeMillis()
)

data class ThemeColors(
    val primary: Long,
    val onPrimary: Long,
    val primaryContainer: Long,
    val onPrimaryContainer: Long,
    val secondary: Long,
    val onSecondary: Long,
    val secondaryContainer: Long,
    val onSecondaryContainer: Long,
    val tertiary: Long,
    val onTertiary: Long,
    val tertiaryContainer: Long,
    val onTertiaryContainer: Long,
    val error: Long,
    val onError: Long,
    val errorContainer: Long,
    val onErrorContainer: Long,
    val background: Long,
    val onBackground: Long,
    val surface: Long,
    val onSurface: Long,
    val surfaceVariant: Long,
    val onSurfaceVariant: Long,
    val outline: Long,
    val outlineVariant: Long,
    val scrim: Long,
    val inverseSurface: Long,
    val inverseOnSurface: Long,
    val inversePrimary: Long,
    val surfaceDim: Long,
    val surfaceBright: Long,
    val surfaceContainerLowest: Long,
    val surfaceContainerLow: Long,
    val surfaceContainer: Long,
    val surfaceContainerHigh: Long,
    val surfaceContainerHighest: Long
)
```

### SyncableTheme
```kotlin
data class SyncableTheme(
    override val id: String,
    val name: String,
    val isCustom: Boolean = false,
    val isDark: Boolean = false,
    val colorsJson: String,
    override val version: Long = 1,
    override val lastModified: Long = System.currentTimeMillis(),
    override val ownerId: String
) : SyncableObject() {

    fun toThemeData(): ThemeData
    companion object {
        fun fromThemeData(themeData: ThemeData, ownerId: String): SyncableTheme
    }
}
```

## Usage Examples

### Initializing ThemeSync
```kotlin
val themeSyncManager = ThemeSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect.main",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start synchronization
lifecycleScope.launch {
    themeSyncManager.start()
}
```

### Creating and Applying Custom Themes
```kotlin
val customColors = ThemeColors(
    primary = 0xFF2196F3,
    onPrimary = 0xFFFFFFFF,
    // ... other colors
)

val customTheme = ThemeData(
    id = "custom_theme_1",
    name = "My Custom Theme",
    isCustom = true,
    isDark = false,
    colors = customColors
)

// Save and apply the theme
lifecycleScope.launch {
    val savedTheme = themeSyncManager.createCustomTheme(customTheme)
    if (savedTheme != null) {
        themeSyncManager.setTheme(savedTheme)
    }
}
```

### Observing Theme Changes
```kotlin
// Observe theme changes from other devices
themeSyncManager.themeChangeFlow.collect { newTheme ->
    // Apply the new theme to current activity
    ThemeApplier().applyTheme(this@MainActivity, newTheme)
}
```

### Managing Themes
```kotlin
// Get all available themes
val allThemes = themeSyncManager.getAllThemes()

// Delete a custom theme
lifecycleScope.launch {
    themeSyncManager.deleteCustomTheme("custom_theme_1")
}
```

## Dependencies

### Asinka Framework
- `digital.vasic.asinka:asinka` - IPC and synchronization framework

### Android Architecture Components
- `androidx.room:room-runtime:2.6.1` - Database runtime
- `androidx.room:room-ktx:2.6.1` - Kotlin extensions for Room
- `androidx.room:room-compiler:2.6.1` - Room annotation processor

### Kotlin Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2` - Coroutine support
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2` - Android coroutine support

### JSON Serialization
- `com.google.code.gson:gson:2.10.1` - JSON parsing and serialization

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `org.mockito:mockito-core:5.8.0` - Mocking framework
- `androidx.arch.core:core-testing:2.2.0` - Architecture component testing
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2` - Coroutine testing

---

*For more information, visit [https://shareconnect.org/docs/themesync](https://shareconnect.org/docs/themesync)*