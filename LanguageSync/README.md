# LanguageSync Module

The LanguageSync module provides cross-application language and localization synchronization, ensuring consistent language settings across all ShareConnect applications.

## Overview

LanguageSync manages language preferences and locale settings with automatic locale application and real-time synchronization across applications.

## Features

- **Locale Management**: System locale synchronization
- **Language Preferences**: User language choice persistence
- **Auto-Application**: Automatic locale updates on app restart
- **Cross-App Sync**: Unified language settings across all apps
- **Fallback Support**: Graceful fallback to system defaults

## Architecture

### Core Components

```
LanguageSync/
├── LanguageSyncManager.kt    # Main synchronization manager
├── models/
│   ├── LanguageData.kt       # Language preference model
│   └── SyncableLanguage.kt   # Asinka synchronization wrapper
├── repository/
│   └── LanguageRepository.kt # Local database operations
├── database/
│   ├── LanguageDao.kt        # Room DAO for language operations
│   └── LanguageDatabase.kt   # Room database configuration
└── utils/
    └── LocaleHelper.kt       # Locale application utilities
```

### Data Model

The `LanguageData` model includes:
- **Locale Info**: Language codes, country codes, display names
- **Preference Settings**: Primary and fallback languages
- **System Integration**: Android locale compatibility

## Usage

```kotlin
// Initialize LanguageSyncManager
val languageManager = LanguageSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Set language preference
val languagePref = LanguageData(
    id = "primary_language",
    languageCode = "es",
    countryCode = "ES",
    displayName = "Español (España)"
)

// Save and sync
languageManager.saveLanguagePreference(languagePref)
```

## Database Schema

```sql
CREATE TABLE synced_languages (
    id TEXT PRIMARY KEY,
    languageCode TEXT NOT NULL,
    countryCode TEXT,
    displayName TEXT,
    isPrimary INTEGER NOT NULL DEFAULT 0,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL
);
```

## API Reference

### LanguageSyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `saveLanguagePreference()` | Save language preference |
| `getCurrentLanguage()` | Get active language |
| `getAvailableLanguages()` | Get supported languages |
| `applyLanguage()` | Apply language to app |

## License

Licensed under the MIT License. See project LICENSE file for details.