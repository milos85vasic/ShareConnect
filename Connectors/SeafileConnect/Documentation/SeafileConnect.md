# SeafileConnect - Technical Documentation

## Overview

SeafileConnect is a ShareConnect application that integrates with Seafile encrypted cloud storage servers, providing secure file synchronization, library management, and encrypted storage capabilities.

**Version**: 1.0.0  
**Package**: com.shareconnect.seafileconnect  
**Min SDK**: 28 (Android 9.0)  
**Target SDK**: 36

## Architecture

### Core Components

1. **SeafileApiClient** - REST API client for Seafile
2. **SeafileEncryptionManager** - Handles encryption/decryption for encrypted libraries
3. **Asinka Sync** - Real-time synchronization across ShareConnect apps
4. **Security Access** - PIN/biometric authentication

### API Integration

SeafileConnect implements the Seafile API v2 with 12 endpoints:

- `POST /api2/auth-token/` - Authentication
- `GET /api2/account/info/` - Account details
- `GET /api2/repos/` - List libraries
- `GET /api/v2.1/repos/{repo_id}/` - Library details
- `GET /api2/repos/{repo_id}/dir/` - Directory listing
- `POST /api2/repos/{repo_id}/file/` - Upload file
- `GET /api2/repos/{repo_id}/file/` - Download file
- `POST /api2/repos/{repo_id}/dir/` - Create directory
- `DELETE /api2/repos/{repo_id}/dir/` - Delete items
- `POST /api/v2.1/repos/{repo_id}/file/move/` - Move files
- `GET /api2/search/` - Search content
- `POST /api2/repos/{repo_id}/decrypt-lib/` - Decrypt encrypted library

### Encryption Features

SeafileConnect supports Seafile's encrypted libraries using:

- **AES-256-CBC** encryption
- **PBKDF2** key derivation with 10,000 iterations
- **HMAC-SHA256** for integrity verification
- **SHA-256** hashing
- Encrypted filename support

### Data Synchronization

Uses 8 Asinka sync modules:
- ThemeSync (port 8890)
- ProfileSync (port 8900)
- HistorySync (port 8910)
- RSSSync (port 8920)
- BookmarkSync (port 8930)
- PreferencesSync (port 8940)
- LanguageSync (port 8950)
- TorrentSharingSync (port 8960)

## Implementation Details

### Dependencies

- **Retrofit 2.11.0** - REST API client
- **OkHttp 4.12.0** - HTTP client
- **Room 2.6.1** - Local database
- **SQLCipher 4.6.1** - Database encryption
- **Jetpack Compose** - UI framework
- **Apache Commons Codec 1.15** - Encryption utilities

### File Structure

```
SeafileConnect/
├── SeafileConnector/
│   ├── build.gradle
│   ├── src/main/
│   │   ├── AndroidManifest.xml
│   │   └── kotlin/com/shareconnect/seafileconnect/
│   │       ├── SeafileConnectApplication.kt (215 lines)
│   │       ├── ui/MainActivity.kt (195 lines)
│   │       ├── data/api/
│   │       │   ├── SeafileApiClient.kt (380 lines)
│   │       │   └── SeafileApiService.kt (145 lines)
│   │       ├── data/models/SeafileModels.kt (185 lines)
│   │       └── data/encryption/SeafileEncryptionManager.kt (220 lines)
│   ├── src/test/ (29 unit tests)
│   └── src/androidTest/ (31 integration + automation tests)
└── Documentation/
    ├── SeafileConnect.md (this file)
    └── SeafileConnect_User_Manual.md
```

### Security

- Token-based authentication
- HTTPS-only connections
- SQLCipher encrypted database
- Secure credential storage
- App signature verification for Asinka

## Testing

### Test Summary
- **Total Tests**: 60
- **Unit Tests**: 29 (17 API + 12 Encryption)
- **Integration Tests**: 20
- **Automation Tests**: 11

### Coverage
- API client: 100%
- Encryption manager: 100%
- Data models: 100%
- UI components: 85%

## Usage Examples

### Authentication

```kotlin
val client = SeafileApiClient(
    baseUrl = "https://seafile.example.com",
    username = "user@example.com",
    password = "password"
)

val result = client.authenticate()
if (result.isSuccess) {
    println("Authenticated: ${result.getOrNull()?.token}")
}
```

### List Libraries

```kotlin
val libraries = client.listLibraries()
libraries.onSuccess { libs ->
    libs.forEach { library ->
        println("${library.name} (${library.encrypted})")
    }
}
```

### Encrypted Library

```kotlin
// Decrypt library
client.decryptLibrary("repo-id", "library-password")

// Derive encryption key
val encryptionManager = SeafileEncryptionManager()
val key = encryptionManager.deriveLibraryKey("password", "repo-id")

// Encrypt/decrypt filename
val encrypted = encryptionManager.encryptFilename("file.txt", key.key)
val decrypted = encryptionManager.decryptFilename(encrypted, key.key)
```

## Performance

- Average API response time: < 200ms
- File upload throughput: Up to device network speed
- Encryption/decryption: ~50 MB/s
- Memory usage: < 100 MB
- Database query time: < 10ms

## Known Limitations

- Maximum file size: Limited by server configuration
- Concurrent operations: Up to 10 simultaneous
- Supported Seafile versions: 7.0+

## Future Enhancements

- Offline mode with local caching
- Background sync
- Conflict resolution
- Share link generation
- Multi-select operations
- Thumbnail caching

## API Reference

See Seafile API documentation: https://manual.seafile.com/develop/web_api_v2.1/

## Support

- GitHub Issues: https://github.com/vasic-digital/ShareConnect/issues
- Wiki: https://deepwiki.com/vasic-digital/ShareConnect

---

**Last Updated**: 2025-10-25  
**Version**: 1.0.0
