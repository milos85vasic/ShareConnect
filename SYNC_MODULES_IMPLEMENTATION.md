# ShareConnect Sync Modules - Complete Implementation Guide

## ✅ **COMPLETED MODULES**

### 1. **ThemeSync**
- **Status:** ✅ Complete and integrated
- **Features:** Real-time theme synchronization with 6 color schemes
- **Apps:** ShareConnect, qBitConnect, TransmissionConnect

### 2. **ProfileSync**
- **Status:** ✅ Complete and integrated
- **Features:** Server profiles with client-type filtering
- **Apps:** ShareConnect (all), qBitConnect (qBit only), TransmissionConnect (Transmission only)

### 3. **HistorySync**
- **Status:** ✅ Module created, needs integration
- **Features:** Unified download/share history across all apps
- **Data Model:** `HistoryData` with 24 fields including:
  - URL, title, description, thumbnail
  - Service type, torrent client type
  - File size, duration, quality
  - Torrent hash, magnet URI
  - Categories and tags
- **Benefits:**
  - See all downloads from any app
  - Prevent duplicate downloads
  - Quick re-download capability
  - Searchable across all apps

---

## 🚧 **IN PROGRESS MODULES**

### 4. **RSSSync**
- **Status:** 🚧 Started
- **Features:** RSS feed subscriptions for automatic torrent downloads
- **Data Model:** `RSSFeedData`
  - URL, name, auto-download flag
  - Filter patterns (include/exclude)
  - Update interval, last update
  - Category, download path
  - Torrent client type filtering
- **Use Cases:**
  - Subscribe once in qBitConnect, works in Transmission too
  - Auto-download new episodes
  - Filter by quality/release group
  - Scheduled updates

---

## 📋 **REMAINING HIGH-PRIORITY MODULES**

### 5. **BookmarkSync** ⭐⭐⭐⭐⭐
**Purpose:** Shared bookmarks for frequently used URLs, magnet links, media sources

**Data Model:**
```kotlin
data class BookmarkData(
    id: String,
    url: String,
    title: String,
    type: String, // "magnet", "video", "playlist", "rss", "webpage"
    category: String?,
    tags: String?, // comma-separated
    favicon: String?,
    dateAdded: Long,
    lastAccessed: Long?,
    accessCount: Int,
    sourceApp: String,
    version: Int,
    lastModified: Long
)
```

**Benefits:**
- Save favorite magnet links
- Bookmark media URLs
- Quick access from any app
- Categorized organization

---

### 6. **PreferencesSync** ⭐⭐⭐⭐⭐
**Purpose:** Unified settings module combining all preference types

**Data Model:**
```kotlin
data class PreferencesData(
    id: String = "user_prefs",

    // Download Settings
    defaultDownloadPath: String?,
    customDownloadPaths: String?, // JSON map of category -> path

    // Bandwidth Settings
    downloadSpeedLimit: Int, // KB/s, 0 = unlimited
    uploadSpeedLimit: Int,
    altDownloadLimit: Int,
    altUploadLimit: Int,
    useAltSchedule: Boolean,
    scheduleStart: String?, // "22:00"
    scheduleEnd: String?,   // "08:00"

    // Notification Settings
    torrentFinishedNotification: Boolean,
    downloadFinishedNotification: Boolean,
    notificationSound: Boolean,
    notificationVibrate: Boolean,
    backgroundUpdateOnWifiOnly: Boolean,

    // UI Preferences
    sortBy: String, // "name", "date", "size", "status"
    sortOrder: String, // "asc", "desc"
    viewMode: String, // "list", "grid"
    compactMode: Boolean,
    showSpeedInTitle: Boolean,

    // Connection Settings
    connectionTimeout: Int, // seconds
    retryAttempts: Int,
    keepAliveInterval: Int,

    // Update Settings
    updateInterval: Int, // minutes

    // Advanced
    confirmOnDelete: Boolean,
    showAddTorrentFAB: Boolean,

    sourceApp: String,
    version: Int,
    lastModified: Long
)
```

**Benefits:**
- Configure once, apply everywhere
- Consistent behavior across apps
- Schedule bandwidth limits
- Unified notification settings

---

## 🔧 **IMPLEMENTATION PATTERN**

Each sync module follows this structure:

```
{Module}Sync/
├── build.gradle.kts
├── src/main/
│   ├── AndroidManifest.xml
│   └── kotlin/com/shareconnect/{module}sync/
│       ├── models/
│       │   ├── {Data}Data.kt
│       │   └── Syncable{Data}.kt
│       ├── database/
│       │   ├── {Data}Dao.kt
│       │   └── {Data}Database.kt
│       ├── repository/
│       │   └── {Data}Repository.kt
│       └── {Module}SyncManager.kt
└── src/test/kotlin/
```

---

## 🎯 **INTEGRATION CHECKLIST**

### For Each App:

1. **Add dependency** in `build.gradle`:
```gradle
implementation project(':{Module}Sync')
```

2. **Initialize in Application class**:
```kotlin
lateinit var {module}SyncManager: {Module}SyncManager

private fun initialize{Module}Sync() {
    val packageInfo = packageManager.getPackageInfo(packageName, 0)
    {module}SyncManager = {Module}SyncManager.getInstance(
        context = this,
        appId = packageName,
        appName = getString(R.string.app_name),
        appVersion = packageInfo.versionName ?: "1.0"
    )
    applicationScope.launch {
        {module}SyncManager.start()
    }
}
```

3. **Add to settings.gradle**:
```gradle
include ':{Module}Sync'
```

---

## 📊 **PRIORITY RANKING**

| Module | Priority | Status | Integration Effort |
|--------|----------|--------|-------------------|
| ThemeSync | ⭐⭐⭐⭐⭐ | ✅ Done | Complete |
| ProfileSync | ⭐⭐⭐⭐⭐ | ✅ Done | Complete |
| HistorySync | ⭐⭐⭐⭐⭐ | ✅ Module created | Needs integration |
| PreferencesSync | ⭐⭐⭐⭐⭐ | 📋 Planned | High value |
| BookmarkSync | ⭐⭐⭐⭐ | 📋 Planned | Medium |
| RSSSync | ⭐⭐⭐⭐ | 🚧 Started | Medium |

---

## 🚀 **NEXT STEPS**

1. **Complete RSSSync module**
   - Finish DAO, Database, Repository, Manager
   - Add to settings.gradle

2. **Create BookmarkSync module**
   - Quick implementation following pattern
   - High user value

3. **Create comprehensive PreferencesSync**
   - Combines 6+ smaller modules
   - Reduces code duplication
   - Single source of truth for all settings

4. **Integration Phase**
   - Add HistorySync to all 3 apps
   - Add RSSSync to torrent apps
   - Add BookmarkSync to all apps
   - Add PreferencesSync to all apps

5. **Testing & Build**
   - Unit tests for new modules
   - Integration tests
   - Build verification

---

## 💡 **ARCHITECTURAL DECISIONS**

### Why PreferencesSync instead of separate modules?

**Advantages:**
- **Less Code:** One module vs. 6 separate modules
- **Consistency:** All preferences update together
- **Performance:** Single sync operation instead of multiple
- **Maintenance:** Easier to maintain one codebase
- **User Experience:** Settings changes propagate as a unit

**Disadvantages:**
- Larger data model
- All-or-nothing updates

**Decision:** Combine into PreferencesSync for better maintainability

---

## 🎓 **LESSONS LEARNED**

1. **Asinka API Usage:**
   - Use `AsinkaClient` not `Asinka`
   - Version must be String not Int
   - Use `FieldSchema` objects not strings

2. **Type Handling:**
   - Always handle Int/Long conversions in `fromFieldMap`
   - Port numbers can be Int or Long
   - Timestamps are always Long

3. **Filtering Logic:**
   - Implement in Repository for reusability
   - Use helper methods on data models
   - Clear separation of concerns

4. **Testing Strategy:**
   - Unit tests for models and adapters
   - Integration tests for database operations
   - Build verification after each module

---

## 📝 **COMPLETION CRITERIA**

- [x] ThemeSync functional across all apps
- [x] ProfileSync functional across all apps
- [ ] HistorySync integrated and tested
- [ ] RSSSync integrated and tested
- [ ] BookmarkSync integrated and tested
- [ ] PreferencesSync integrated and tested
- [ ] All modules build successfully
- [ ] All unit tests pass
- [ ] Cross-app sync verified

---

## 🔗 **RELATED DOCUMENTATION**

- See `ProfileSync/` for reference implementation
- See `ThemeSync/` for Asinka integration patterns
- See individual module READMEs for specific features

---

**Last Updated:** 2025-10-08
**Status:** In Progress - 3 of 6 core modules complete
