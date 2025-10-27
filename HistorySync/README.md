# HistorySync Module

The HistorySync module provides cross-application sharing history synchronization, maintaining a unified history of all shared content across ShareConnect applications.

## Overview

HistorySync tracks and synchronizes sharing activities between applications, providing users with a complete view of their content sharing history regardless of which app initiated the share.

## Features

- **Comprehensive History**: Tracks all sharing activities with 24+ data fields
- **Multi-Service Support**: YouTube, Vimeo, torrents, JDownloader, and more
- **Cross-App Sync**: Unified history across all ShareConnect applications
- **Rich Metadata**: Thumbnails, descriptions, file sizes, durations
- **Query Capabilities**: Advanced filtering and search functionality
- **Real-time Updates**: Instant history synchronization

## Architecture

### Core Components

```
HistorySync/
├── HistorySyncManager.kt       # Main synchronization manager
├── models/
│   ├── HistoryData.kt          # Comprehensive history data model
│   └── SyncableHistory.kt      # Asinka synchronization wrapper
├── repository/
│   └── HistoryRepository.kt    # Local database operations
├── database/
│   ├── HistoryDao.kt           # Room DAO for history operations
│   └── HistoryDatabase.kt      # Room database configuration
└── utils/
    └── HistoryQueryBuilder.kt  # Advanced query construction
```

### Data Model

The `HistoryData` model captures:
- **Content Info**: URL, title, description, thumbnail
- **Service Details**: Provider, type, service type
- **Sharing Context**: Profile used, success status, source app
- **Metadata**: File size, duration, timestamps
- **Client Info**: Torrent client type for torrent shares

## Usage

### Basic Setup

```kotlin
// Initialize HistorySyncManager
val historyManager = HistorySyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start synchronization
CoroutineScope(Dispatchers.IO).launch {
    historyManager.start()
}
```

### Recording Share History

```kotlin
val historyEntry = HistoryData(
    id = UUID.randomUUID().toString(),
    url = "https://youtube.com/watch?v=...",
    title = "Sample Video",
    description = "A sample video description",
    thumbnailUrl = "https://img.youtube.com/vi/.../default.jpg",
    serviceProvider = "YouTube",
    type = "video",
    timestamp = System.currentTimeMillis(),
    profileId = "qbittorrent_main",
    profileName = "Main Server",
    isSentSuccessfully = true,
    serviceType = "ytdl",
    sourceApp = "com.shareconnect",
    fileSize = 157286400, // 150MB
    duration = 3600 // 1 hour
)

// Save and sync
historyManager.saveHistoryEntry(historyEntry)
```

### Querying History

```kotlin
// Get recent shares
val recentShares = historyManager.getRecentHistory(limit = 50)

// Search by service
val youtubeShares = historyManager.searchHistory(
    query = HistoryQuery.Builder()
        .serviceProvider("YouTube")
        .type("video")
        .dateRange(startDate, endDate)
        .build()
)

// Get successful shares only
val successfulShares = historyManager.getHistoryByStatus(successful = true)
```

## Supported Service Types

| Service Type | Description | Supported Apps |
|--------------|-------------|----------------|
| `ytdl` | YouTube-DL compatible services | ShareConnect, qBitConnect |
| `torrent` | Torrent files and magnet links | qBitConnect, TransmissionConnect |
| `jdownloader` | JDownloader container formats | JDownloaderConnect |
| `metube` | MeTube sharing service | All apps |

## Content Types

- **Videos**: YouTube, Vimeo, Twitch, etc.
- **Playlists**: YouTube playlists, Spotify albums
- **Channels**: YouTube channels, Twitch channels
- **Torrents**: .torrent files, magnet links
- **Archives**: RAR, ZIP, 7Z files
- **Documents**: PDFs, documents
- **Direct Downloads**: Any HTTP/HTTPS link

## Database Schema

```sql
CREATE TABLE synced_history (
    id TEXT PRIMARY KEY,
    url TEXT NOT NULL,
    title TEXT,
    description TEXT,
    thumbnailUrl TEXT,
    serviceProvider TEXT,
    type TEXT NOT NULL,
    timestamp INTEGER NOT NULL,
    profileId TEXT,
    profileName TEXT,
    isSentSuccessfully INTEGER NOT NULL,
    serviceType TEXT NOT NULL,
    torrentClientType TEXT,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,
    fileSize INTEGER,
    duration INTEGER,
    downloadSpeed REAL,
    uploadSpeed REAL,
    eta INTEGER,
    progress REAL,
    status TEXT,
    category TEXT,
    tags TEXT,
    priority INTEGER,
    seeders INTEGER,
    leechers INTEGER
);
```

## Advanced Query Features

### HistoryQuery Builder

```kotlin
val query = HistoryQuery.Builder()
    .serviceProvider("YouTube")
    .type("video")
    .successfulOnly(true)
    .dateRange(startTime, endTime)
    .profileId("qbittorrent_main")
    .sortBy(SortField.TIMESTAMP, SortOrder.DESCENDING)
    .limit(100)
    .build()

val results = historyManager.searchHistory(query)
```

### Available Filters

- **Service Provider**: YouTube, Vimeo, SoundCloud, etc.
- **Content Type**: video, playlist, channel, torrent
- **Success Status**: successful vs failed shares
- **Time Range**: Custom date/time ranges
- **Profile**: Filter by torrent client profile
- **Source App**: Which app initiated the share

## Integration with Apps

### ShareConnect Integration

```kotlin
class SCApplication : BaseApplication() {
    lateinit var historySyncManager: HistorySyncManager

    override fun onCreate() {
        super.onCreate()

        historySyncManager = HistorySyncManager.getInstance(
            context = this,
            appIdentifier = BuildConfig.APPLICATION_ID,
            appName = getString(R.string.app_name),
            appVersion = BuildConfig.VERSION_NAME
        )

        // Start in background
        CoroutineScope(Dispatchers.IO).launch {
            historySyncManager.start()
        }
    }
}
```

### History Tracking in Connectors

```kotlin
// In qBitConnect - when sharing torrent
val historyEntry = HistoryData(
    id = generateId(),
    url = torrentUrl,
    title = torrentName,
    serviceProvider = "Torrent",
    type = "torrent",
    timestamp = System.currentTimeMillis(),
    profileId = activeProfile.id,
    profileName = activeProfile.name,
    isSentSuccessfully = true,
    serviceType = "torrent",
    torrentClientType = "qbittorrent",
    sourceApp = "com.shareconnect.qbitconnect"
)

historyManager.saveHistoryEntry(historyEntry)
```

## Testing

### Unit Tests

```bash
./gradlew :HistorySync:testDebugUnitTest
```

**Test Coverage:**
- HistoryData model validation
- Query builder functionality
- Repository operations
- Manager lifecycle

### Integration Tests

```bash
./gradlew :HistorySync:connectedDebugAndroidTest
```

**Test Scenarios:**
- Cross-app history synchronization
- Complex query execution
- Large dataset performance
- Real-time sync validation

## API Reference

### HistorySyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `start()` | Start synchronization |
| `saveHistoryEntry()` | Save history entry |
| `getRecentHistory()` | Get recent entries |
| `searchHistory()` | Advanced search with filters |
| `deleteHistoryEntry()` | Remove history entry |
| `clearHistory()` | Clear all history |

### HistoryData

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Unique history identifier |
| `url` | String | Shared content URL |
| `title` | String? | Content title |
| `serviceProvider` | String? | Service name (YouTube, etc.) |
| `type` | String | Content type |
| `timestamp` | Long | Share timestamp |
| `isSentSuccessfully` | Boolean | Success status |

## Performance Considerations

- **Database Indexing**: Optimized indexes on commonly queried fields
- **Pagination**: Large result sets paginated automatically
- **Background Sync**: Non-blocking synchronization operations
- **Memory Management**: Efficient object pooling for large histories

## Troubleshooting

### Common Issues

1. **History not syncing**
   - Check network connectivity between apps
   - Verify Asinka service discovery
   - Ensure sufficient storage space

2. **Query performance**
   - Add appropriate database indexes
   - Use pagination for large result sets
   - Optimize query filters

3. **Missing history entries**
   - Check app permissions for storage access
   - Verify database integrity
   - Review error logs for failed saves

### Debug Mode

Enable detailed logging:
```kotlin
HistorySyncManager.setDebugEnabled(true)
```

## Dependencies

- **Asinka**: Synchronization framework
- **Room**: Local database persistence
- **Kotlin Coroutines**: Asynchronous operations
- **Kotlinx.DateTime**: Date/time handling

## Contributing

When contributing to HistorySync:

1. Maintain backward compatibility with existing history data
2. Add proper indexing for new query fields
3. Update query builder for new filter options
4. Test with large datasets for performance

## License

Licensed under the MIT License. See project LICENSE file for details.