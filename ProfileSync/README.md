# ProfileSync Module

The ProfileSync module manages cross-application profile synchronization for torrent client configurations, enabling seamless sharing of server profiles between ShareConnect applications.

## Overview

ProfileSync provides centralized management of torrent client server profiles, allowing users to configure qBittorrent, Transmission, and uTorrent servers once and use them across all ShareConnect applications.

## Features

- **Multi-Client Support**: qBittorrent, Transmission, uTorrent profiles
- **Secure Storage**: Encrypted profile data with SQLCipher
- **Profile Adapters**: Client-specific profile format conversion
- **Real-time Sync**: Instant profile updates across applications
- **Validation**: Profile connectivity and configuration validation

## Architecture

### Core Components

```
ProfileSync/
├── ProfileSyncManager.kt           # Main synchronization manager
├── models/
│   ├── ProfileData.kt              # Unified profile data model
│   └── SyncableProfile.kt          # Asinka synchronization wrapper
├── repository/
│   └── ProfileRepository.kt        # Local database operations
├── database/
│   ├── ProfileDao.kt               # Room DAO for profile operations
│   └── ProfileDatabase.kt          # Room database configuration
└── adapters/
    ├── QBitConnectProfileAdapter.kt    # qBittorrent profile conversion
    ├── ShareConnectProfileAdapter.kt   # ShareConnect profile conversion
    ├── TransmissionConnectProfileAdapter.kt # Transmission profile conversion
    └── UTorrentConnectProfileAdapter.kt     # uTorrent profile conversion
```

### Data Model

The `ProfileData` model includes:
- **Connection Details**: Host, port, authentication credentials
- **Client Type**: TORRENT_CLIENT_QBITTORRENT, TORRENT_CLIENT_TRANSMISSION, TORRENT_CLIENT_UTORRENT
- **Security**: Encrypted password storage
- **Metadata**: Profile name, description, validation status

## Usage

### Basic Setup

```kotlin
// Initialize ProfileSyncManager
val profileManager = ProfileSyncManager.getInstance(
    context = applicationContext,
    appIdentifier = "com.shareconnect",
    appName = "ShareConnect",
    appVersion = "1.0.0"
)

// Start synchronization
CoroutineScope(Dispatchers.IO).launch {
    profileManager.start()
}
```

### Creating Profiles

```kotlin
val qbitProfile = ProfileData(
    id = "qbit_main",
    name = "Main qBittorrent Server",
    clientType = ProfileData.TORRENT_CLIENT_QBITTORRENT,
    host = "192.168.1.100",
    port = 8080,
    username = "admin",
    password = "encrypted_password",
    isActive = true
)

// Save and sync
profileManager.saveProfile(qbitProfile)
```

### Profile Validation

```kotlin
// Validate profile connectivity
val isValid = profileManager.validateProfile(qbitProfile)
if (isValid) {
    // Profile is ready for use
    profileManager.setActiveProfile(qbitProfile.id)
}
```

## Supported Clients

### qBittorrent
- **Web UI**: Standard web interface authentication
- **API Version**: Supports v2.8.0+
- **Features**: Torrent management, category support

### Transmission
- **RPC API**: JSON-RPC over HTTP
- **Authentication**: Session ID based auth
- **Features**: Torrent operations, bandwidth limits

### uTorrent
- **Web UI**: Classic web interface
- **Authentication**: Basic HTTP auth
- **Features**: Label support, priority settings

## Database Schema

```sql
CREATE TABLE synced_profiles (
    id TEXT PRIMARY KEY,
    name TEXT NOT NULL,
    clientType TEXT NOT NULL,
    host TEXT NOT NULL,
    port INTEGER NOT NULL,
    username TEXT,
    password TEXT,  -- Encrypted
    isActive INTEGER NOT NULL DEFAULT 0,
    description TEXT,
    createdAt INTEGER NOT NULL,
    updatedAt INTEGER NOT NULL,
    validationStatus TEXT,
    lastValidated INTEGER
);
```

## Profile Adapters

### Purpose
Profile adapters convert between the unified `ProfileData` format and client-specific configurations used by each ShareConnect application.

### Adapter Interface

```kotlin
interface ProfileAdapter {
    fun convertToClientFormat(profile: ProfileData): ClientSpecificProfile
    fun convertFromClientFormat(clientProfile: ClientSpecificProfile): ProfileData
    fun validateProfile(profile: ProfileData): Boolean
}
```

### Client-Specific Implementations

Each adapter handles:
- **Authentication format conversion**
- **Connection parameter mapping**
- **Client-specific validation**
- **Error handling and logging**

## Integration with Apps

### ShareConnect Integration

```kotlin
class SCApplication : BaseApplication() {
    lateinit var profileSyncManager: ProfileSyncManager

    override fun onCreate() {
        super.onCreate()

        profileSyncManager = ProfileSyncManager.getInstance(
            context = this,
            appIdentifier = BuildConfig.APPLICATION_ID,
            appName = getString(R.string.app_name),
            appVersion = BuildConfig.VERSION_NAME
        )

        // Start in background
        CoroutineScope(Dispatchers.IO).launch {
            profileSyncManager.start()
        }
    }
}
```

### Profile Usage in Connectors

```kotlin
// In qBitConnect
val adapter = QBitConnectProfileAdapter()
val clientConfig = adapter.convertToClientFormat(profileData)
// Use clientConfig to initialize qBittorrent client
```

## Testing

### Unit Tests

```bash
./gradlew :ProfileSync:testDebugUnitTest
```

**Test Coverage:**
- ProfileData model validation
- Adapter conversions
- Repository operations
- Manager lifecycle

### Integration Tests

```bash
./gradlew :ProfileSync:connectedDebugAndroidTest
```

**Test Scenarios:**
- Cross-app profile synchronization
- Profile validation with real servers
- Adapter conversion accuracy
- Authentication flow testing

## API Reference

### ProfileSyncManager

| Method | Description |
|--------|-------------|
| `getInstance()` | Get singleton instance |
| `start()` | Start synchronization |
| `saveProfile()` | Save and sync profile |
| `getAllProfiles()` | Get all available profiles |
| `setActiveProfile()` | Set active profile |
| `validateProfile()` | Test profile connectivity |

### ProfileData

| Property | Type | Description |
|----------|------|-------------|
| `id` | String | Unique profile identifier |
| `name` | String | Display name |
| `clientType` | String | Client type constant |
| `host` | String | Server hostname/IP |
| `port` | Int | Server port |
| `username` | String? | Authentication username |
| `password` | String? | Encrypted password |

## Security Considerations

- **Password Encryption**: All passwords stored encrypted using SQLCipher
- **Network Security**: HTTPS recommended for remote connections
- **Access Control**: Profile data isolated per application
- **Validation**: Server connectivity verified before storage

## Troubleshooting

### Common Issues

1. **Connection failures**
   - Verify server is running and accessible
   - Check firewall settings
   - Validate credentials

2. **Sync not working**
   - Ensure apps are on same network
   - Check Asinka service discovery
   - Verify profile data integrity

3. **Adapter conversion errors**
   - Check client version compatibility
   - Validate profile data format
   - Review adapter logs

### Debug Mode

Enable detailed logging:
```kotlin
ProfileSyncManager.enableDebugLogging(true)
```

## Dependencies

- **Asinka**: Synchronization framework
- **Room**: Local database persistence
- **SQLCipher**: Encrypted database storage
- **Kotlin Coroutines**: Asynchronous operations

## Contributing

When contributing to ProfileSync:

1. Maintain compatibility with existing profile formats
2. Add validation for new client versions
3. Update adapters for new client features
4. Test with real torrent client instances

## Performance Metrics

| Metric | Value |
|--------|-------|
| Memory Usage | <50 KB |
| CPU Impact | <1% |
| Network Overhead | <2 KB/sync |
| Profile Validation Latency | <100ms |
| Max Supported Profiles | 50 |

## Version History

- **2.0.3**: Enhanced profile adapter logic
- **2.0.2**: Improved encryption and security
- **2.0.1**: Bug fixes and performance improvements
- **2.0.0**: Major refactoring with multi-client support

## License

Licensed under the MIT License. See project LICENSE file for details.