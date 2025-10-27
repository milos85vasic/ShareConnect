# TorrentSharingSync Module

The TorrentSharingSync module manages cross-application torrent sharing preferences, controlling how torrents are shared between ShareConnect applications and torrent clients.

## Overview

TorrentSharingSync maintains user preferences for torrent sharing behavior, including direct sharing options and "don't ask again" settings for different torrent client applications.

## Features

- **Sharing Preferences**: Control torrent sharing behavior
- **Client-Specific Settings**: Per-client sharing preferences
- **Don't Ask Again**: Persistent dismissal of installation prompts
- **Cross-App Sync**: Unified sharing settings across all apps
- **Direct Sharing**: Enable/disable direct torrent client integration

## Architecture

### Core Components

```
TorrentSharingSync/
├── TorrentSharingSyncManager.kt # Main synchronization manager
├── models/
│   ├── TorrentSharingData.kt    # Sharing preferences model
│   └── SyncableTorrentSharing.kt # Asinka synchronization wrapper
├── repository/
│   └── TorrentSharingRepository.kt # Local database operations
└── database/
    ├── TorrentSharingDao.kt     # Room DAO for sharing operations
    └── TorrentSharingDatabase.kt # Room database configuration
```

### Data Model

The `TorrentSharingData` model includes:
- **Sharing Controls**: Enable/disable direct sharing
- **Client Preferences**: Per-client "don't ask again" settings
- **Singleton Pattern**: Single preference set per user

## Usage

```kotlin
// Initialize TorrentSharingSyncManager
val sharingManager = TorrentSharingSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Update sharing preferences
val sharingPrefs = TorrentSharingData(
    directSharingEnabled = true,
    dontAskQBitConnect = false,
    dontAskTransmissionConnect = true,
    dontAskUTorrentConnect = false
)

// Save and sync
sharingManager.saveSharingPreferences(sharingPrefs)
```

## Database Schema

```sql
CREATE TABLE synced_torrent_sharing_prefs (
    id TEXT PRIMARY KEY,
    directSharingEnabled INTEGER NOT NULL DEFAULT 1,
    dontAskQBitConnect INTEGER NOT NULL DEFAULT 0,
    dontAskTransmissionConnect INTEGER NOT NULL DEFAULT 0,
    dontAskUTorrentConnect INTEGER NOT NULL DEFAULT 0,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL
);
```

## API Reference

### TorrentSharingSyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `saveSharingPreferences()` | Save sharing preferences |
| `getSharingPreferences()` | Get current preferences |
| `isDirectSharingEnabled()` | Check if direct sharing enabled |
| `shouldAskForClient()` | Check if should prompt for client |

## License

Licensed under the MIT License. See project LICENSE file for details.