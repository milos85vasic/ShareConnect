# PreferencesSync Module

The PreferencesSync module provides cross-application user preferences synchronization, ensuring consistent settings and configurations across all ShareConnect applications.

## Overview

PreferencesSync manages user preferences across 7 categories with type-safe storage and real-time synchronization, allowing users to configure once and apply settings universally.

## Features

- **7 Preference Categories**: Download, bandwidth, notifications, UI, connection, updates, advanced
- **Type-Safe Storage**: String, int, long, boolean, and JSON value types
- **Cross-App Sync**: Unified preferences across all applications
- **Real-time Updates**: Instant preference synchronization
- **Version Control**: Preference versioning and conflict resolution

## Architecture

### Core Components

```
PreferencesSync/
├── PreferencesSyncManager.kt  # Main synchronization manager
├── models/
│   ├── PreferencesData.kt     # Preference data model
│   └── SyncablePreferences.kt # Asinka synchronization wrapper
├── repository/
│   └── PreferencesRepository.kt # Local database operations
└── database/
    ├── PreferencesDao.kt      # Room DAO for preference operations
    └── PreferencesDatabase.kt # Room database configuration
```

### Data Model

The `PreferencesData` model includes:
- **Categorization**: 7 predefined categories
- **Type Safety**: Multiple data types with validation
- **Metadata**: Descriptions and versioning
- **Source Tracking**: App-specific preference attribution

## Usage

```kotlin
// Initialize PreferencesSyncManager
val prefsManager = PreferencesSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Save preference
val downloadLimit = PreferencesData(
    id = "download_speed_limit",
    category = PreferencesData.CATEGORY_BANDWIDTH,
    key = "speed_limit_download",
    value = "1048576", // 1 MB/s in bytes
    type = "long",
    description = "Maximum download speed limit"
)

// Save and sync
prefsManager.savePreference(downloadLimit)
```

## Preference Categories

| Category | Description | Example Keys |
|----------|-------------|--------------|
| `download` | Download settings | `path_default`, `create_subfolder` |
| `bandwidth` | Speed and limits | `speed_limit_upload`, `alt_speed_enabled` |
| `notification` | Alert preferences | `notify_complete`, `notify_failed` |
| `ui` | Interface settings | `theme_dark`, `language_code` |
| `connection` | Network config | `listen_port`, `max_connections` |
| `update` | Update behavior | `auto_update`, `update_interval` |
| `advanced` | Expert settings | `debug_logging`, `experimental_features` |

## Database Schema

```sql
CREATE TABLE synced_preferences (
    id TEXT PRIMARY KEY,
    category TEXT NOT NULL,
    key TEXT NOT NULL,
    value TEXT,
    type TEXT NOT NULL,
    description TEXT,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL
);
```

## API Reference

### PreferencesSyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `savePreference()` | Save preference |
| `getPreference()` | Get preference by key |
| `getPreferencesByCategory()` | Get all prefs in category |
| `deletePreference()` | Remove preference |
| `getAllPreferences()` | Get all preferences |

### PreferencesData

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Unique preference identifier |
| `category` | String | Preference category |
| `key` | String | Preference key |
| `value` | String? | Preference value (JSON for complex) |
| `type` | String | Data type |

## License

Licensed under the MIT License. See project LICENSE file for details.