# ProfileSync

## Overview and Purpose

ProfileSync is a synchronization module that manages server profile data across all ShareConnect applications. It enables users to configure service connections (MeTube, YT-DLP, torrent clients, JDownloader) on one device and have those profiles automatically sync to all other ShareConnect apps. The module handles profile encryption, conflict resolution, and secure cross-device synchronization.

## Architecture and Components

ProfileSync is built on the Asinka IPC framework with encrypted local storage:

### Core Components
- **ProfileSyncManager**: Main synchronization orchestrator
- **ProfileRepository**: Encrypted profile data persistence
- **ProfileEncryption**: Profile data encryption/decryption
- **SyncableProfile**: Profile data model for synchronization

### Data Layer
- **ServerProfileDao**: Room database access for profiles
- **ServerProfileDatabase**: Encrypted SQLite database
- **ProfileData**: Internal profile representation
- **SyncableProfile**: Network synchronization model

## API Reference

### ProfileSyncManager
```kotlin
class ProfileSyncManager private constructor(
    context: Context,
    appIdentifier: String,
    appName: String,
    appVersion: String,
    asinkaClient: AsinkaClient,
    repository: ProfileRepository
) {
    suspend fun start(): Unit
    suspend fun stop(): Unit
    suspend fun getProfiles(): List<ServerProfile>
    suspend fun addProfile(profile: ServerProfile): Boolean
    suspend fun updateProfile(profile: ServerProfile): Boolean
    suspend fun deleteProfile(profileId: String): Boolean
    suspend fun setDefaultProfile(profileId: String): Boolean
    val profileChangeFlow: Flow<List<ServerProfile>>
}
```

### ProfileRepository
```kotlin
class ProfileRepository(context: Context) {
    suspend fun getAllProfiles(): List<ServerProfile>
    suspend fun getProfileById(id: String): ServerProfile?
    suspend fun saveProfile(profile: ServerProfile): Boolean
    suspend fun deleteProfile(id: String): Boolean
    suspend fun getDefaultProfile(): ServerProfile?
    suspend fun setDefaultProfile(profileId: String): Boolean
    fun observeProfileChanges(): Flow<List<ServerProfile>>
}
```

## Key Classes and Their Responsibilities

### ProfileSyncManager
- **Responsibilities**:
  - Profile synchronization lifecycle management
  - Cross-device profile conflict resolution
  - Encrypted profile data handling
  - Integration with Asinka for IPC communication
  - Profile change event broadcasting

### ProfileRepository
- **Responsibilities**:
  - Encrypted local storage of profile data
  - SQLCipher integration for database encryption
  - Profile CRUD operations with encryption
  - Data migration from unencrypted storage
  - Profile validation and integrity checks

### ProfileEncryption
- **Responsibilities**:
  - Profile data encryption/decryption
  - Secure key management
  - Credential protection
  - Encryption algorithm selection

## Data Models

### ServerProfile
```kotlin
data class ServerProfile(
    val id: String? = null,
    val name: String = "",
    val host: String = "",
    val port: Int = 80,
    val serviceType: ServiceType = ServiceType.METUBE,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false,
    val isDefault: Boolean = false,
    val clientType: TorrentClientType? = null
)

enum class ServiceType {
    METUBE, YTDLP, TORRENT_CLIENT, JDOWNLOADER
}

enum class TorrentClientType {
    TRANSMISSION, QBITORRENT, UTORRENT
}
```

### SyncableProfile
```kotlin
data class SyncableProfile(
    override val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val serviceType: String,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean,
    val isDefault: Boolean,
    val clientType: String? = null,
    override val version: Long = 1,
    override val lastModified: Long = System.currentTimeMillis(),
    override val ownerId: String
) : SyncableObject()
```

## Usage Examples

### Initializing ProfileSync
```kotlin
val profileSyncManager = ProfileSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect.main",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start synchronization
lifecycleScope.launch {
    profileSyncManager.start()
}
```

### Managing Server Profiles
```kotlin
// Add a new profile
val profile = ServerProfile(
    name = "My Transmission Server",
    host = "192.168.1.100",
    port = 9091,
    serviceType = ServiceType.TORRENT_CLIENT,
    clientType = TorrentClientType.TRANSMISSION,
    useHttps = false
)

lifecycleScope.launch {
    profileSyncManager.addProfile(profile)
}
```

### Observing Profile Changes
```kotlin
// Observe profile changes from other devices
profileSyncManager.profileChangeFlow.collect { profiles ->
    // Update UI with new profile list
    updateProfileList(profiles)
}
```

## Dependencies

### Asinka Framework
- `digital.vasic.asinka:asinka` - IPC and synchronization framework

### Database and Encryption
- `androidx.room:room-runtime:2.6.1` - Database runtime
- `androidx.room:room-ktx:2.6.1` - Kotlin extensions
- `net.zetetic:android-database-sqlcipher:4.5.4` - Encrypted database

### Security
- `androidx.security:security-crypto:1.1.0` - Android security crypto

### JSON Processing
- `com.google.code.gson:gson:2.10.1` - JSON serialization

---

*For more information, visit [https://shareconnect.org/docs/profilesync](https://shareconnect.org/docs/profilesync)*