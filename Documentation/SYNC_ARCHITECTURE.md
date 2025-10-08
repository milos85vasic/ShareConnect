# Synchronization Architecture Documentation

## Table of Contents

1. [Overview](#overview)
2. [System Architecture](#system-architecture)
3. [Module Architecture](#module-architecture)
4. [Data Flow](#data-flow)
5. [Sync Process](#sync-process)
6. [Database Schema](#database-schema)
7. [API Reference](#api-reference)

---

## Overview

The ShareConnect synchronization ecosystem enables real-time bidirectional data synchronization across three Android applications using the Asinka gRPC IPC library.

### Key Principles

- **Decentralized:** No central server required
- **Real-time:** Changes propagate immediately
- **Bidirectional:** All apps can initiate changes
- **Conflict-free:** Version-based resolution
- **Type-safe:** Compile-time safety with Kotlin
- **Reactive:** Flow-based UI updates

---

## System Architecture

### High-Level Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                        Application Layer                                │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────────────────┐     │
│  │ShareConnect  │  │ qBitConnect  │  │ TransmissionConnect      │     │
│  │              │  │              │  │                          │     │
│  │ • Activities │  │ • Composables│  │ • Activities/Fragments  │     │
│  │ • ViewModels │  │ • ViewModels │  │ • ViewModels            │     │
│  │ • UI Flows   │  │ • UI State   │  │ • Listeners             │     │
│  └──────┬───────┘  └──────┬───────┘  └──────────┬───────────────┘     │
│         │                 │                     │                      │
└─────────┼─────────────────┼─────────────────────┼──────────────────────┘
          │                 │                     │
┌─────────┼─────────────────┼─────────────────────┼──────────────────────┐
│         │      Sync Manager Layer              │                      │
│  ┌──────▼─────┐    ┌──────▼─────┐      ┌──────▼──────┐               │
│  │   Theme    │    │  Profile   │      │  History    │               │
│  │   Manager  │    │  Manager   │      │  Manager    │               │
│  └──────┬─────┘    └──────┬─────┘      └──────┬──────┘               │
│         │                 │                     │                      │
│  ┌──────▼─────┐    ┌──────▼─────┐      ┌──────▼──────┐               │
│  │    RSS     │    │ Bookmark   │      │ Preferences │               │
│  │  Manager   │    │  Manager   │      │  Manager    │               │
│  └──────┬─────┘    └──────┬─────┘      └──────┬──────┘               │
│         │                 │                     │                      │
└─────────┼─────────────────┼─────────────────────┼──────────────────────┘
          │                 │                     │
┌─────────┼─────────────────┼─────────────────────┼──────────────────────┐
│         │      Repository Layer                │                      │
│  ┌──────▼───────────────────────────────────────▼──────┐               │
│  │         Data Access Repositories                    │               │
│  │  • Type-safe data operations                        │               │
│  │  • Filtering and sorting logic                      │               │
│  │  • Flow-based reactive queries                      │               │
│  └──────┬───────────────────────────────────────┬──────┘               │
│         │                                       │                      │
└─────────┼───────────────────────────────────────┼──────────────────────┘
          │                                       │
┌─────────┼───────────────────────────────────────┼──────────────────────┐
│         │      Database Layer                   │                      │
│  ┌──────▼─────┐    Room 2.8.1 with KSP   ┌──────▼──────┐               │
│  │    DAO     │                           │  Database   │               │
│  │ Interface  │◄──────────────────────────┤   Instance  │               │
│  └──────┬─────┘                           └──────┬──────┘               │
│         │                                       │                      │
│  ┌──────▼────────────────────────────────────────▼──────┐               │
│  │            SQLite Database Files                     │               │
│  │    (synced_themes, synced_profiles, etc.)            │               │
│  └───────────────────────────────────────────────────────┘               │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
          │
┌─────────┼───────────────────────────────────────────────────────────────┐
│         │      Asinka IPC Layer                                         │
│  ┌──────▼───────────────────────────────────────────────┐               │
│  │              AsinkaClient                            │               │
│  │  ┌────────────────────────────────────────────────┐ │               │
│  │  │          SyncManager                           │ │               │
│  │  │  • registerObject()                            │ │               │
│  │  │  • updateObject()                              │ │               │
│  │  │  • deleteObject()                              │ │               │
│  │  │  • observeAllChanges()                         │ │               │
│  │  └────────────────────────────────────────────────┘ │               │
│  │                                                      │               │
│  │  ┌────────────────────────────────────────────────┐ │               │
│  │  │          gRPC Transport                        │ │               │
│  │  │  • Protocol Buffers serialization             │ │               │
│  │  │  • Local IPC (no network)                     │ │               │
│  │  │  • OkHttp transport for Android               │ │               │
│  │  └────────────────────────────────────────────────┘ │               │
│  └──────────────────────────────────────────────────────┘               │
│                                                                          │
└──────────────────────────────────────────────────────────────────────────┘
```

---

## Module Architecture

### Standard Module Structure

```
ModuleSync/
│
├── build.gradle.kts
│   └── Configuration
│       ├── Android SDK: min 23, compile 36
│       ├── Dependencies: Asinka, Room, Coroutines
│       └── KSP for annotation processing
│
├── src/main/
│   │
│   ├── AndroidManifest.xml
│   │
│   └── kotlin/com/shareconnect/{module}sync/
│       │
│       ├── models/
│       │   │
│       │   ├── {Module}Data.kt
│       │   │   ├── @Entity annotation (Room)
│       │   │   ├── @PrimaryKey field
│       │   │   ├── Data fields
│       │   │   ├── Companion object with constants
│       │   │   └── Helper methods
│       │   │
│       │   └── Syncable{Module}.kt
│       │       ├── implements SyncableObject (Asinka)
│       │       ├── objectId, objectType, version
│       │       ├── toFieldMap() → Map<String, Any?>
│       │       ├── fromFieldMap(Map<String, Any?>)
│       │       └── Type conversion handling
│       │
│       ├── database/
│       │   │
│       │   ├── {Module}Dao.kt
│       │   │   ├── @Dao annotation (Room)
│       │   │   ├── @Query methods (Flow & suspend)
│       │   │   ├── @Insert with conflict strategy
│       │   │   ├── @Update method
│       │   │   └── @Delete / @Query delete methods
│       │   │
│       │   └── {Module}Database.kt
│       │       ├── abstract class : RoomDatabase()
│       │       ├── @Database annotation
│       │       ├── abstract DAO getter
│       │       └── Singleton getInstance() method
│       │
│       ├── repository/
│       │   │
│       │   └── {Module}Repository.kt
│       │       ├── DAO wrapper
│       │       ├── Flow-based methods
│       │       ├── Suspend methods
│       │       ├── Filtering logic
│       │       └── Helper methods
│       │
│       └── {Module}SyncManager.kt
│           ├── Singleton pattern
│           ├── AsinkaClient integration
│           ├── Repository instance
│           ├── Flow for change events
│           ├── start() / stop() methods
│           ├── CRUD operations
│           ├── Sync registration
│           └── Change observation
```

---

## Data Flow

### 1. User Creates Item (e.g., Add Bookmark)

```
┌─────────────────────────────────────────────────────────────────────┐
│ Step 1: User Action in App A (ShareConnect)                         │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
                            User clicks "Save Bookmark"
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│ Step 2: ViewModel/Activity                                          │
│                                                                      │
│   val bookmark = BookmarkData(                                      │
│       id = UUID.randomUUID().toString(),                            │
│       url = userInput.url,                                          │
│       title = userInput.title,                                      │
│       ...                                                            │
│   )                                                                  │
│                                                                      │
│   lifecycleScope.launch {                                           │
│       bookmarkSyncManager.addBookmark(bookmark)                     │
│   }                                                                  │
└───────────────────────────────────┬─────────────────────────────────┘
                                    │
                                    ▼
┌─────────────────────────────────────────────────────────────────────┐
│ Step 3: BookmarkSyncManager.addBookmark()                           │
│                                                                      │
│   suspend fun addBookmark(bookmark: BookmarkData) {                 │
│       repository.insertBookmark(bookmark)        ─────┐             │
│       asinkaClient.syncManager.registerObject(  )     │             │
│       _bookmarkChangeFlow.emit(bookmark)              │             │
│   }                                                    │             │
└────────────────────────────────────────────────────────┼─────────────┘
                                                         │
                    ┌────────────────────────────────────┤
                    │                                    │
                    ▼                                    ▼
┌───────────────────────────────┐    ┌──────────────────────────────┐
│ Step 4a: Local Storage        │    │ Step 4b: Asinka Registration │
│                                │    │                              │
│ Repository.insertBookmark()    │    │ registerObject(              │
│         │                      │    │   SyncableBookmark           │
│         ▼                      │    │ )                            │
│ BookmarkDao.insert()           │    │         │                    │
│         │                      │    │         ▼                    │
│         ▼                      │    │ Convert to Protocol Buffer   │
│ Room inserts into SQLite       │    │         │                    │
│                                │    │         ▼                    │
│ Returns: Row ID                │    │ Broadcast via gRPC           │
└───────────────────────────────┘    └──────────┬───────────────────┘
                                                 │
                    ┌────────────────────────────┴─────────────────┐
                    │                                              │
                    ▼                                              ▼
┌──────────────────────────────────────┐  ┌─────────────────────────────┐
│ App B (qBitConnect) Receives         │  │ App C (TransmissionConnect) │
│                                       │  │ Receives                    │
│ observeAllChanges().collect {        │  │                             │
│   when (it) {                         │  │ observeAllChanges().collect │
│     is SyncChange.Updated -> {        │  │ Same process as App B       │
│       handleReceivedBookmark(...)     │  │                             │
│     }                                 │  │                             │
│   }                                   │  │                             │
│ }                                     │  │                             │
└───────────────┬───────────────────────┘  └──────────┬──────────────────┘
                │                                     │
                ▼                                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│ Step 5: handleReceivedBookmark()                                    │
│                                                                      │
│   suspend fun handleReceivedBookmark(                               │
│       syncableBookmark: SyncableBookmark                            │
│   ) {                                                                │
│       val bookmark = syncableBookmark.getBookmarkData()             │
│       val existing = repository.getBookmarkById(bookmark.id)        │
│                                                                      │
│       if (existing == null) {                                       │
│           repository.insertBookmark(bookmark)                       │
│           _bookmarkChangeFlow.emit(bookmark)                        │
│       } else if (bookmark.version > existing.version) {             │
│           repository.updateBookmark(bookmark)                       │
│           _bookmarkChangeFlow.emit(bookmark)                        │
│       }                                                              │
│       // else: ignore older/same version                            │
│   }                                                                  │
└───────────────────────────────┬─────────────────────────────────────┘
                                │
                                ▼
┌─────────────────────────────────────────────────────────────────────┐
│ Step 6: UI Update via Flow                                          │
│                                                                      │
│   lifecycleScope.launch {                                           │
│       bookmarkSyncManager.bookmarkChangeFlow.collect { bookmark ->  │
│           // Update RecyclerView                                    │
│           bookmarkAdapter.addOrUpdate(bookmark)                     │
│                                                                      │
│           // Or refresh entire list                                 │
│           refreshBookmarkList()                                     │
│       }                                                              │
│   }                                                                  │
└─────────────────────────────────────────────────────────────────────┘
```

---

## Sync Process

### Initialization Sequence

```
App Startup
    │
    ▼
onCreate()
    │
    ├─► initializeThemeSync()
    │       │
    │       ├─► ThemeSyncManager.getInstance()
    │       │       │
    │       │       ├─► Create AsinkaConfig
    │       │       ├─► Create AsinkaClient
    │       │       ├─► Get ThemeDatabase
    │       │       └─► Create ThemeRepository
    │       │
    │       └─► themeSyncManager.start()
    │               │
    │               ├─► asinkaClient.start()
    │               ├─► syncLocalThemesToAsinka()
    │               │       └─► registerObject() for all existing themes
    │               │
    │               └─► observeAllChanges()
    │                       └─► Launch coroutine to collect changes
    │
    ├─► initializeProfileSync()
    │       └─► (Same pattern with client filtering)
    │
    ├─► initializeHistorySync()
    ├─► initializeRSSSync()
    ├─► initializeBookmarkSync()
    └─► initializePreferencesSync()
```

### Sync Registration

```
syncLocalItemsToAsinka()
    │
    ▼
Get all items from local database
    │
    ├─► item1 → SyncableObject
    ├─► item2 → SyncableObject
    └─► item3 → SyncableObject
            │
            ▼
    asinkaClient.syncManager.registerObject(syncableItem)
            │
            ├─► Convert to Protocol Buffer
            ├─► Calculate version hash
            ├─► Store in Asinka registry
            └─► Broadcast to other apps
```

### Change Observation

```
observeAllChanges()
    │
    ▼
Flow<SyncChange>
    │
    ├─► SyncChange.Updated
    │       │
    │       ├─► Extract SyncableObject
    │       ├─► Convert to Data class
    │       ├─► Check version
    │       └─► Insert or Update local DB
    │
    └─► SyncChange.Deleted
            │
            ├─► Extract objectId
            └─► Delete from local DB
```

---

## Database Schema

### ThemeSync Database

```sql
CREATE TABLE synced_themes (
    id TEXT PRIMARY KEY NOT NULL,
    isDarkMode INTEGER NOT NULL,
    primaryColor INTEGER NOT NULL,
    accentColor INTEGER NOT NULL,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,
    -- Additional theme fields
);

CREATE INDEX idx_themes_version ON synced_themes(version);
CREATE INDEX idx_themes_lastModified ON synced_themes(lastModified);
```

### ProfileSync Database

```sql
CREATE TABLE synced_profiles (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    host TEXT NOT NULL,
    port INTEGER NOT NULL DEFAULT 8080,
    username TEXT,
    password TEXT,
    useSsl INTEGER NOT NULL DEFAULT 0,
    rpcUrl TEXT,
    torrentClientType TEXT,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,
    -- Additional profile fields
);

CREATE INDEX idx_profiles_clientType ON synced_profiles(torrentClientType);
CREATE INDEX idx_profiles_version ON synced_profiles(version);
```

### HistorySync Database

```sql
CREATE TABLE synced_history (
    id TEXT PRIMARY KEY NOT NULL,
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
    quality TEXT,
    downloadPath TEXT,
    torrentHash TEXT,
    magnetUri TEXT,
    category TEXT,
    tags TEXT
);

CREATE INDEX idx_history_timestamp ON synced_history(timestamp DESC);
CREATE INDEX idx_history_serviceType ON synced_history(serviceType);
CREATE INDEX idx_history_category ON synced_history(category);
```

### RSSSync Database

```sql
CREATE TABLE synced_rss_feeds (
    id TEXT PRIMARY KEY NOT NULL,
    url TEXT NOT NULL,
    name TEXT NOT NULL,
    autoDownload INTEGER NOT NULL DEFAULT 0,
    filters TEXT,
    excludeFilters TEXT,
    updateInterval INTEGER NOT NULL DEFAULT 30,
    lastUpdate INTEGER NOT NULL DEFAULT 0,
    isEnabled INTEGER NOT NULL DEFAULT 1,
    category TEXT,
    torrentClientType TEXT,
    downloadPath TEXT,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL
);

CREATE INDEX idx_rss_enabled ON synced_rss_feeds(isEnabled);
CREATE INDEX idx_rss_clientType ON synced_rss_feeds(torrentClientType);
CREATE INDEX idx_rss_category ON synced_rss_feeds(category);
```

### BookmarkSync Database

```sql
CREATE TABLE synced_bookmarks (
    id TEXT PRIMARY KEY NOT NULL,
    url TEXT NOT NULL,
    title TEXT,
    description TEXT,
    thumbnailUrl TEXT,
    type TEXT NOT NULL,
    category TEXT,
    tags TEXT,
    isFavorite INTEGER NOT NULL DEFAULT 0,
    notes TEXT,
    serviceProvider TEXT,
    torrentHash TEXT,
    magnetUri TEXT,
    createdAt INTEGER NOT NULL,
    lastAccessedAt INTEGER,
    accessCount INTEGER NOT NULL DEFAULT 0,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL
);

CREATE INDEX idx_bookmarks_favorite ON synced_bookmarks(isFavorite);
CREATE INDEX idx_bookmarks_type ON synced_bookmarks(type);
CREATE INDEX idx_bookmarks_category ON synced_bookmarks(category);
CREATE INDEX idx_bookmarks_accessCount ON synced_bookmarks(accessCount DESC);
```

### PreferencesSync Database

```sql
CREATE TABLE synced_preferences (
    id TEXT PRIMARY KEY NOT NULL,
    category TEXT NOT NULL,
    key TEXT NOT NULL,
    value TEXT,
    type TEXT NOT NULL,
    description TEXT,
    sourceApp TEXT NOT NULL,
    version INTEGER NOT NULL DEFAULT 1,
    lastModified INTEGER NOT NULL,

    UNIQUE(category, key)
);

CREATE INDEX idx_prefs_category ON synced_preferences(category);
CREATE INDEX idx_prefs_key ON synced_preferences(key);
```

---

## API Reference

### SyncManager Common Interface

All sync managers follow this pattern:

```kotlin
class {Module}SyncManager private constructor(...) {

    // Properties
    private val scope: CoroutineScope
    private val _changeFlow: MutableSharedFlow<{Module}Data>
    val changeFlow: SharedFlow<{Module}Data>
    private var isStarted: Boolean

    // Lifecycle
    suspend fun start()
    suspend fun stop()

    // Observers (Flow-based)
    fun getAll{Items}(): Flow<List<{Module}Data>>
    fun get{Items}By{Criteria}(param): Flow<List<{Module}Data>>

    // Queries (Suspend)
    suspend fun get{Item}ById(id: String): {Module}Data?
    suspend fun search{Items}(query: String): List<{Module}Data>

    // Mutations
    suspend fun add{Item}(item: {Module}Data)
    suspend fun update{Item}(item: {Module}Data)
    suspend fun delete{Item}(id: String)

    // Internal
    private suspend fun syncLocal{Items}ToAsinka()
    private suspend fun handleReceived{Item}(syncable: Syncable{Module})
    private suspend fun handleDeleted{Item}(id: String)

    // Singleton
    companion object {
        @Volatile
        private var INSTANCE: {Module}SyncManager? = null

        fun getInstance(
            context: Context,
            appId: String,
            appName: String,
            appVersion: String,
            ... // Module-specific params
        ): {Module}SyncManager

        fun resetInstance()
    }
}
```

### Example: BookmarkSyncManager API

```kotlin
// Get manager instance
val bookmarkSyncManager = BookmarkSyncManager.getInstance(
    context = this,
    appId = packageName,
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start sync
lifecycleScope.launch {
    bookmarkSyncManager.start()
}

// Observe all bookmarks
bookmarkSyncManager.getAllBookmarks()
    .collect { bookmarks ->
        updateUI(bookmarks)
    }

// Observe favorites only
bookmarkSyncManager.getFavoriteBookmarks()
    .collect { favorites ->
        updateFavorites(favorites)
    }

// Add bookmark
val bookmark = BookmarkData(...)
bookmarkSyncManager.addBookmark(bookmark)

// Update bookmark
val updated = bookmark.copy(title = "New Title")
bookmarkSyncManager.updateBookmark(updated)

// Delete bookmark
bookmarkSyncManager.deleteBookmark(bookmarkId)

// Toggle favorite
bookmarkSyncManager.toggleFavorite(bookmarkId)

// Record access
bookmarkSyncManager.recordAccess(bookmarkId)

// Search bookmarks
val results = bookmarkSyncManager.searchBookmarks("keyword")
```

---

## Performance Considerations

### Optimization Strategies

1. **Lazy Loading**
   - Load data only when needed
   - Use paging for large datasets

2. **Indexing**
   - Index frequently queried fields
   - Composite indexes for complex queries

3. **Batch Operations**
   - Use `insertAll()` for bulk inserts
   - Transaction wrapping for multiple operations

4. **Flow Efficiency**
   - Use `distinctUntilChanged()` to avoid duplicate emissions
   - `debounce()` for rapid changes

5. **Memory Management**
   - Limit Flow replay buffer
   - Clean up old data periodically

---

**Last Updated:** 2025-10-08
**Version:** 1.0.0
