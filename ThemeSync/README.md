# ThemeSync Module

The ThemeSync module provides cross-application theme synchronization for ShareConnect applications, allowing users to maintain consistent visual themes across all connected services.

## Overview

ThemeSync enables seamless theme sharing between ShareConnect applications using the Asinka synchronization framework. It supports both predefined color schemes and custom themes with full Material Design 3 color palette customization.

## Features

- **Cross-App Theme Sync**: Synchronize themes between ShareConnect, qBitConnect, TransmissionConnect, and uTorrentConnect
- **Predefined Themes**: 7 built-in color schemes (Warm Orange, Crimson, Light Blue, Purple, Green, Material)
- **Custom Themes**: Full Material Design 3 color palette customization
- **Dark/Light Mode**: Automatic dark/light mode variants for all themes
- **Real-time Sync**: Instant theme updates across all connected applications

## Architecture

### Core Components

```
ThemeSync/
├── ThemeSyncManager.kt      # Main synchronization manager
├── models/
│   ├── ThemeData.kt         # Theme data model with 30+ color fields
│   └── SyncableTheme.kt     # Asinka synchronization wrapper
├── repository/
│   └── ThemeRepository.kt   # Local database operations
├── database/
│   ├── ThemeDao.kt          # Room DAO for theme operations
│   └── ThemeDatabase.kt     # Room database configuration
└── utils/
    └── ThemeApplier.kt      # Theme application utilities
```

### Data Model

The `ThemeData` model supports:
- **Basic Properties**: name, color scheme, dark mode flag
- **Material Design 3 Colors**: Primary, secondary, tertiary, surface, error colors
- **Variants**: Container and on-color variants for all color roles
- **Metadata**: Source app, version, timestamps, custom flags

## Usage

### Basic Setup

```kotlin
// Initialize ThemeSyncManager
val themeManager = ThemeSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start synchronization
CoroutineScope(Dispatchers.IO).launch {
    themeManager.start()
}
```

### Creating Custom Themes

```kotlin
val customTheme = ThemeData.createCustomTheme(
    name = "My Custom Theme",
    isDarkMode = false,
    sourceApp = "com.shareconnect",
    primary = 0xFF3A7BD5.toLong(),
    onPrimary = 0xFFFFFFFF.toLong(),
    // ... other color parameters
)

// Save and sync
themeManager.saveTheme(customTheme)
```

### Observing Theme Changes

```kotlin
// Observe theme changes from other apps
themeManager.themeChangeFlow.collect { themeData ->
    // Apply theme to current app
    ThemeApplier.applyTheme(context, themeData)
}
```

## Predefined Color Schemes

| Scheme | Light Mode | Dark Mode | Description |
|--------|------------|-----------|-------------|
| `COLOR_WARM_ORANGE` | ✅ | ✅ | Warm, inviting orange tones |
| `COLOR_CRIMSON` | ✅ | ✅ | Deep red/crimson palette |
| `COLOR_LIGHT_BLUE` | ✅ | ✅ | Soft blue color scheme |
| `COLOR_PURPLE` | ✅ | ✅ | Rich purple tones |
| `COLOR_GREEN` | ✅ | ✅ | Natural green palette |
| `COLOR_MATERIAL` | ✅ | ✅ | Google's Material Design colors |
| `COLOR_CUSTOM` | ✅ | ✅ | Fully customizable |

## Database Schema

```sql
CREATE TABLE synced_themes (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    colorScheme TEXT NOT NULL,
    isDarkMode INTEGER NOT NULL,
    isDefault INTEGER NOT NULL,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,
    isCustom INTEGER NOT NULL DEFAULT 0,
    -- 25 custom color fields (customPrimary, customOnPrimary, etc.)
    customPrimary INTEGER,
    customOnPrimary INTEGER,
    -- ... additional color fields
);
```

## Integration with Apps

### ShareConnect Integration

```kotlin
class SCApplication : BaseApplication() {
    lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate() {
        super.onCreate()

        // Initialize theme sync
        themeSyncManager = ThemeSyncManager.getInstance(
            context = this,
            appIdentifier = BuildConfig.APPLICATION_ID,
            appName = getString(R.string.app_name),
            appVersion = BuildConfig.VERSION_NAME
        )

        // Start in background
        CoroutineScope(Dispatchers.IO).launch {
            themeSyncManager.start()
        }
    }
}
```

### Theme Application

```kotlin
object ThemeApplier {
    fun applyTheme(context: Context, themeData: ThemeData) {
        // Apply colors to Material Design 3 theme
        // Update activity themes
        // Notify UI components of theme change
    }
}
```

## Testing

### Unit Tests

```bash
./gradlew :ThemeSync:testDebugUnitTest
```

**Test Coverage:**
- ThemeData model validation
- SyncableTheme conversion
- Repository operations
- Manager lifecycle

### Integration Tests

```bash
./gradlew :ThemeSync:connectedDebugAndroidTest
```

**Test Scenarios:**
- Cross-app theme synchronization
- Theme persistence and retrieval
- Real-time sync validation
- Theme application verification

## API Reference

### ThemeSyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `start()` | Start synchronization |
| `saveTheme()` | Save and sync theme |
| `getAllThemes()` | Get all available themes |
| `deleteTheme()` | Remove theme |
| `themeChangeFlow` | Flow of theme changes |

### ThemeData

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Unique theme identifier |
| `name` | String | Display name |
| `colorScheme` | String | Color scheme constant |
| `isDarkMode` | Boolean | Dark/light mode flag |
| `customPrimary` | Long? | Custom primary color |

## Troubleshooting

### Common Issues

1. **Themes not syncing between apps**
   - Ensure all apps are running and connected to same network
   - Check Asinka service discovery is working
   - Verify app identifiers match

2. **Custom colors not applying**
   - Ensure all required color fields are set
   - Check ThemeApplier implementation
   - Verify Material Design 3 theme setup

3. **Port conflicts**
   - ThemeSync uses base port 8890
   - Conflicts resolved automatically with port scanning

### Debug Logging

Enable verbose logging:
```kotlin
System.setProperty("kotlinx.coroutines.debug", "on")
```

## Dependencies

- **Asinka**: Synchronization framework
- **Room**: Local database persistence
- **Kotlin Coroutines**: Asynchronous operations
- **Material Design 3**: Theme system

## Contributing

When contributing to ThemeSync:

1. Maintain backward compatibility with existing themes
2. Add comprehensive tests for new features
3. Update documentation for API changes
4. Test cross-app synchronization thoroughly

## License

Licensed under the MIT License. See project LICENSE file for details.