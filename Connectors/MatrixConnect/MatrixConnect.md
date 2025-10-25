# CLAUDE.md - MatrixConnect

This file provides comprehensive guidance for working with the MatrixConnect Android application.

## Project Overview

**MatrixConnect** - An Android application providing end-to-end encrypted messaging through the Matrix protocol. Full integration with ShareConnect ecosystem for synchronized profiles, themes, and preferences across all apps.

**Package**: `com.shareconnect.matrixconnect`
**Version**: 1.0.0
**Target**: Android API 28-36
**Protocol**: Matrix Client-Server API r0
**Encryption**: Olm/Megolm for E2EE

## Build Commands

### Building
```bash
# Build debug APK
./gradlew :MatrixConnector:assembleDebug

# Build release APK
./gradlew :MatrixConnector:assembleRelease

# Clean build
./gradlew :MatrixConnector:clean
```

APK location: `Connectors/MatrixConnect/MatrixConnector/build/outputs/apk/debug/MatrixConnector-debug.apk`

### Testing
```bash
# Run all tests
./gradlew :MatrixConnector:test :MatrixConnector:connectedAndroidTest

# Unit tests only (28 tests)
./gradlew :MatrixConnector:test

# Integration tests (33 tests)
./gradlew :MatrixConnector:connectedAndroidTest --tests "*.integration.*"

# Automation tests (11 tests)
./gradlew :MatrixConnector:connectedAndroidTest --tests "*.automation.*"

# E2EE tests
./gradlew :MatrixConnector:connectedAndroidTest --tests "*E2EE*"

# Single test
./gradlew :MatrixConnector:test --tests "MatrixApiClientTest.test login success returns access token"

# Test with coverage
./gradlew :MatrixConnector:testDebugUnitTestCoverage
```

## Architecture

### Core Components

#### Matrix Protocol Implementation
- **MatrixApiClient**: Complete Matrix Client-Server API r0 implementation
  - Authentication (login/logout)
  - Room operations (create, join, leave, invite)
  - Message sending and retrieval
  - Real-time sync with long polling
  - User profile management
  - Presence tracking
  - Device management
  - E2EE key upload/query

- **MatrixEncryptionManager**: End-to-end encryption with Olm/Megolm
  - Olm account management
  - Device identity keys (ed25519, curve25519)
  - One-time key generation and upload
  - Megolm outbound group sessions for rooms
  - Message encryption/decryption
  - Device verification support
  - Key backup and recovery

#### Data Models
Located in `models/MatrixModels.kt`:
- **MatrixLoginResponse**: Authentication response with access token
- **MatrixSyncResponse**: Real-time sync data with rooms and events
- **MatrixEvent**: Timeline events (messages, state changes)
- **MatrixRoom**: Room information with metadata
- **MatrixMessage**: Message content (text, formatted, media)
- **MatrixProfile**: User profile (display name, avatar)
- **MatrixPresence**: User presence status
- **DeviceKeys**: E2EE device identity keys
- **KeysUploadRequest/Response**: E2EE key management
- **MatrixResult<T>**: Result wrapper for error handling

#### UI Layer (Jetpack Compose)
- **MainActivity**: Entry point with SecurityAccess integration
- **MatrixConnectApp**: Main navigation and UI structure
- **RoomsScreen**: List of joined rooms
- **ContactsScreen**: User contacts and direct messages
- **DiscoverScreen**: Public room discovery
- **SettingsScreen**: App configuration
- **MatrixConnectOnboardingActivity**: First-run setup

#### Sync Layer
- **MatrixSyncService**: Foreground service for persistent sync
- **8 ShareConnect Sync Managers**:
  - ThemeSync (port 8890)
  - ProfileSync (port 8900)
  - HistorySync (port 8910)
  - RSSSync (port 8920)
  - BookmarkSync (port 8930)
  - PreferencesSync (port 8940)
  - LanguageSync (port 8950)
  - TorrentSharingSync (port 8960)

### Matrix Protocol Specifics

#### Authentication Flow
1. User enters homeserver URL, username, password
2. Client sends POST to `/_matrix/client/r0/login`
3. Server returns access token, user ID, device ID
4. Client stores credentials securely
5. All subsequent requests include `Authorization: Bearer <token>` header

#### Sync Loop
1. Initial sync without `since` parameter
2. Server returns full state + `next_batch` token
3. Long-polling sync with `since=<next_batch>&timeout=30000`
4. Server holds connection until new events or timeout
5. Client processes events and updates UI
6. Repeat with new `next_batch` token

#### E2EE Flow (Olm/Megolm)
1. **Device Setup**:
   - Generate Olm account (creates ed25519 and curve25519 keys)
   - Generate 100 one-time keys
   - Upload device keys and one-time keys to server

2. **Room Encryption Setup**:
   - Create Megolm outbound group session for room
   - Share session keys with room members via Olm
   - Each device gets encrypted copy of room key

3. **Message Encryption**:
   - Encrypt plaintext with Megolm group session
   - Create event with `m.room.encrypted` type
   - Include algorithm, sender key, ciphertext, session ID
   - Send encrypted event to room

4. **Message Decryption**:
   - Receive `m.room.encrypted` event
   - Look up inbound group session by session ID
   - Decrypt ciphertext using Megolm
   - Display plaintext to user

#### Room Types
- **Private**: Default, invite-only rooms
- **Public**: Discoverable and joinable by anyone
- **Direct**: One-on-one conversations (marked with `is_direct: true`)
- **Encrypted**: Rooms with E2EE enabled (initial state includes `m.room.encryption`)

#### Event Types
- **m.room.message**: Regular text/media messages
- **m.room.encrypted**: Encrypted message content
- **m.room.member**: Membership changes (join, leave, invite, ban)
- **m.room.name**: Room name changes
- **m.room.topic**: Room topic changes
- **m.room.avatar**: Room avatar changes
- **m.room.create**: Room creation event
- **m.room.power_levels**: Permission configuration
- **m.typing**: Typing indicators (ephemeral)
- **m.receipt**: Read receipts (ephemeral)
- **m.presence**: User presence updates

## Matrix API Endpoints

### Authentication
```kotlin
// Login
POST /_matrix/client/r0/login
Body: {
  "type": "m.login.password",
  "identifier": { "type": "m.id.user", "user": "username" },
  "password": "password",
  "device_id": "DEVICE_ID"
}
Response: { "user_id", "access_token", "device_id" }

// Logout
POST /_matrix/client/r0/logout
Headers: Authorization: Bearer <token>
```

### Sync
```kotlin
// Long-polling sync
GET /_matrix/client/r0/sync?timeout=30000&since=<token>
Headers: Authorization: Bearer <token>
Response: { "next_batch", "rooms", "presence", "to_device" }
```

### Rooms
```kotlin
// Create room
POST /_matrix/client/r0/createRoom
Body: { "name", "topic", "is_direct", "invite", "initial_state" }
Response: { "room_id" }

// Join room
POST /_matrix/client/r0/join/:roomIdOrAlias
Response: { "room_id" }

// Leave room
POST /_matrix/client/r0/rooms/:roomId/leave

// Invite user
POST /_matrix/client/r0/rooms/:roomId/invite
Body: { "user_id" }

// Get joined rooms
GET /_matrix/client/r0/joined_rooms
Response: { "joined_rooms": ["!room1:server", ...] }
```

### Messages
```kotlin
// Send message
PUT /_matrix/client/r0/rooms/:roomId/send/m.room.message/:txnId
Body: { "msgtype": "m.text", "body": "message" }
Response: { "event_id" }

// Send encrypted message
PUT /_matrix/client/r0/rooms/:roomId/send/m.room.encrypted/:txnId
Body: { "algorithm", "sender_key", "ciphertext", "session_id", "device_id" }

// Get messages
GET /_matrix/client/r0/rooms/:roomId/messages?from=<token>&dir=b&limit=50
Response: { "start", "end", "chunk": [events...] }
```

### Profile
```kotlin
// Get profile
GET /_matrix/client/r0/profile/:userId
Response: { "displayname", "avatar_url" }

// Set display name
PUT /_matrix/client/r0/profile/:userId/displayname
Body: { "displayname": "name" }

// Set avatar
PUT /_matrix/client/r0/profile/:userId/avatar_url
Body: { "avatar_url": "mxc://server/media_id" }
```

### Presence
```kotlin
// Get presence
GET /_matrix/client/r0/presence/:userId/status
Response: { "presence": "online", "last_active_ago": 1000 }

// Set presence
PUT /_matrix/client/r0/presence/:userId/status
Body: { "presence": "online", "status_msg": "Working" }
```

### E2EE Keys
```kotlin
// Upload keys
POST /_matrix/client/r0/keys/upload
Body: { "device_keys": {...}, "one_time_keys": {...} }
Response: { "one_time_key_counts": { "signed_curve25519": 50 } }

// Query keys
POST /_matrix/client/r0/keys/query
Body: { "device_keys": { "@user:server": [] } }
Response: { "device_keys": { "@user:server": { "DEVICE_ID": {...} } } }
```

### Devices
```kotlin
// Get devices
GET /_matrix/client/r0/devices
Response: { "devices": [{ "device_id", "display_name", "last_seen_ts" }] }

// Get device
GET /_matrix/client/r0/devices/:deviceId
Response: { "device_id", "display_name", "last_seen_ip", "last_seen_ts" }

// Update device
PUT /_matrix/client/r0/devices/:deviceId
Body: { "display_name": "name" }
```

## Development Guidelines

### Code Style
- **Kotlin First**: All new code in Kotlin
- **Coroutines**: Use for all async operations
- **Sealed Classes**: For result types and state
- **Data Classes**: For models and DTOs
- **Immutability**: Prefer val over var

### Error Handling
```kotlin
sealed class MatrixResult<out T> {
    data class Success<T>(val data: T) : MatrixResult<T>()
    data class Error(val code: String, val message: String, val retryAfterMs: Long? = null) : MatrixResult<Nothing>()
    data class NetworkError(val exception: Throwable) : MatrixResult<Nothing>()
}

// Usage
when (val result = apiClient.login(username, password)) {
    is MatrixResult.Success -> {
        // Handle success
        val loginResponse = result.data
    }
    is MatrixResult.Error -> {
        // Handle API error
        if (result.code == "M_LIMIT_EXCEEDED") {
            delay(result.retryAfterMs ?: 5000)
            // Retry
        }
    }
    is MatrixResult.NetworkError -> {
        // Handle network error
        showNetworkErrorDialog()
    }
}
```

### Security Best Practices

#### Credential Storage
```kotlin
// Store credentials securely
val sharedPrefs = getSharedPreferences("matrix_prefs", MODE_PRIVATE)
sharedPrefs.edit()
    .putString("access_token", accessToken)
    .putString("user_id", userId)
    .putString("device_id", deviceId)
    .apply()
```

#### E2EE Key Management
```kotlin
// Initialize encryption on login
val encryptionManager = MatrixEncryptionManager(context, apiClient, userId, deviceId)
val initResult = encryptionManager.initialize()

if (initResult is MatrixResult.Success) {
    // Keys uploaded successfully
    // Start periodic key rotation
}

// Ensure one-time keys available
lifecycleScope.launch {
    while (isActive) {
        encryptionManager.ensureOneTimeKeys()
        delay(3600000) // Check hourly
    }
}
```

#### Secure Communication
- All HTTP communication over HTTPS
- Certificate pinning for production
- Access token in Authorization header
- No credentials in logs
- Clear credentials on logout

### Testing Strategy

#### Unit Tests (28 tests)
Located in `src/test/kotlin/`:
- **MatrixApiClientTest**: Tests all API endpoints with MockWebServer
  - Login/logout flows
  - Sync operations
  - Room CRUD
  - Message send/receive
  - Profile management
  - Presence updates
  - E2EE key operations
  - Device management
  - Error handling
  - Rate limiting

- **MatrixEncryptionManagerTest**: Tests E2EE operations
  - Group session creation
  - Identity key retrieval
  - Message encryption
  - Device key queries
  - Cleanup operations

#### Integration Tests (33 tests)
Located in `src/androidTest/kotlin/integration/`:
- **MatrixApiClientIntegrationTest**: Real API interactions
  - Complete login flow
  - Full message workflow
  - Continuous sync loop
  - Room invite flow
  - Join/leave operations
  - Profile management flow
  - Presence tracking
  - Device management flow
  - E2EE key management
  - All error conditions (401, 403, 404, 429)
  - Network timeout handling
  - Multi-room operations
  - Various message types
  - Different room types

- **MatrixE2EEIntegrationTest**: E2EE integration
  - Group session creation
  - Multiple sessions
  - Cleanup verification
  - Key query operations

#### Automation Tests (11 tests)
Located in `src/androidTest/kotlin/automation/`:
- **MatrixConnectAutomationTest**: UI workflow testing
  - Main screen loading
  - Navigation between tabs
  - Settings navigation
  - Screen content verification
  - Navigation bar selection
  - Top/bottom bar presence
  - Multiple navigation cycles
  - Settings back navigation

### Common Patterns

#### Initialization
```kotlin
class MatrixConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize all sync managers
        themeSyncManager = ThemeSyncManager.getInstance(...)
        profileSyncManager = ProfileSyncManager.getInstance(...)
        // ... other sync managers

        // Start sync in background
        applicationScope.launch {
            themeSyncManager.startSync()
            profileSyncManager.startSync()
            // ... other sync managers
        }
    }
}
```

#### API Client Usage
```kotlin
// Create client
val apiClient = MatrixApiClient(context, "https://matrix.org")

// Login
val loginResult = apiClient.login(username, password)
if (loginResult is MatrixResult.Success) {
    val loginResponse = loginResult.data
    // Credentials automatically set in client
}

// Sync loop
var nextBatch: String? = null
while (isActive) {
    val syncResult = apiClient.sync(since = nextBatch, timeout = 30000)
    if (syncResult is MatrixResult.Success) {
        val syncResponse = syncResult.data
        nextBatch = syncResponse.nextBatch

        // Process rooms
        syncResponse.rooms?.join?.forEach { (roomId, joinedRoom) ->
            joinedRoom.timeline.events.forEach { event ->
                if (event.type == "m.room.message") {
                    // Display message
                }
            }
        }
    }
}
```

#### Encryption Usage
```kotlin
// Initialize encryption manager
val encryptionManager = MatrixEncryptionManager(
    context, apiClient, userId, deviceId
)
encryptionManager.initialize()

// Encrypt message for room
val encryptResult = encryptionManager.encryptMessage(roomId, "Hello!")
if (encryptResult is MatrixResult.Success) {
    val encryptedMessage = encryptResult.data

    // Send encrypted message
    val sendResult = apiClient.sendEncryptedMessage(
        roomId, encryptedMessage, System.currentTimeMillis().toString()
    )
}

// Query device keys for users in room
val keyResult = encryptionManager.queryDeviceKeys(listOf("@user1:server", "@user2:server"))
if (keyResult is MatrixResult.Success) {
    val keys = keyResult.data.deviceKeys
    // Verify and trust keys
}
```

#### Room Creation
```kotlin
// Create private room
val createResult = apiClient.createRoom(
    CreateRoomRequest(
        name = "My Room",
        topic = "Discussion about stuff",
        invite = listOf("@friend:matrix.org")
    )
)

// Create direct message room
val dmResult = apiClient.createRoom(
    CreateRoomRequest(
        isDirect = true,
        invite = listOf("@friend:matrix.org")
    )
)

// Create encrypted room
val encryptedResult = apiClient.createRoom(
    CreateRoomRequest(
        name = "Encrypted Room",
        initialState = listOf(
            StateEvent(
                type = "m.room.encryption",
                content = mapOf("algorithm" to "m.megolm.v1.aes-sha2")
            )
        )
    )
)
```

## Troubleshooting

### Common Issues

#### Authentication Errors
```
Error: M_FORBIDDEN - Invalid password
Solution: Verify username and password, check homeserver is accessible
```

```
Error: M_UNKNOWN_TOKEN - Access token expired
Solution: Re-authenticate user, implement token refresh
```

#### Sync Errors
```
Error: Connection timeout during sync
Solution: Reduce timeout parameter, implement retry with exponential backoff
```

```
Error: M_LIMIT_EXCEEDED - Too many sync requests
Solution: Respect retry_after_ms, implement rate limiting
```

#### E2EE Errors
```
Error: OLM_NOT_INITIALIZED
Solution: Call encryptionManager.initialize() before encryption operations
```

```
Error: No one-time keys available
Solution: Call encryptionManager.ensureOneTimeKeys() to generate more
```

#### Network Errors
```
Error: UnknownHostException
Solution: Check internet connectivity, verify homeserver URL
```

```
Error: SSLHandshakeException
Solution: Check certificate validity, implement certificate pinning
```

### Debugging

#### Enable Logging
```kotlin
// OkHttp logging
val loggingInterceptor = HttpLoggingInterceptor().apply {
    level = HttpLoggingInterceptor.Level.BODY
}

val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(loggingInterceptor)
    .build()
```

#### Olm Debugging
```kotlin
// Check Olm account state
val identityKeys = encryptionManager.getIdentityKeys()
Log.d("MatrixE2EE", "Identity keys: $identityKeys")

// Verify key upload
val uploadResult = encryptionManager.ensureOneTimeKeys()
Log.d("MatrixE2EE", "Key upload result: $uploadResult")
```

## Version Compatibility

### Dependencies
- **Kotlin**: 2.0.0
- **Android Gradle Plugin**: 8.9.1
- **Compile SDK**: 36
- **Min SDK**: 28
- **Target SDK**: 36
- **Compose BOM**: 2024.12.01
- **Olm SDK**: 3.2.15
- **Room**: 2.6.1
- **OkHttp**: 4.12.0
- **Retrofit**: 2.11.0

### Java Version
- **Source Compatibility**: Java 17
- **Target Compatibility**: Java 17

## Performance Optimization

### Network
- Use connection pooling in OkHttp
- Implement response caching
- Compress large payloads
- Batch API requests when possible
- Implement exponential backoff for retries

### E2EE
- Pre-generate one-time keys
- Cache device keys locally
- Reuse group sessions for rooms
- Lazy-load inbound sessions
- Clean up old sessions periodically

### Sync
- Use filter parameter to reduce response size
- Implement lazy loading for room members
- Paginate message history
- Cache sync state locally
- Resume sync from last token

### UI
- Use Compose lazy lists for rooms/messages
- Implement virtual scrolling
- Cache rendered messages
- Optimize recomposition
- Use derivedStateOf for computed state

## Additional Resources

### Matrix Protocol
- [Matrix Specification](https://spec.matrix.org/)
- [Client-Server API](https://spec.matrix.org/latest/client-server-api/)
- [End-to-End Encryption](https://spec.matrix.org/latest/client-server-api/#end-to-end-encryption)

### Libraries
- [Olm Library](https://gitlab.matrix.org/matrix-org/olm)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Room Database](https://developer.android.com/training/data-storage/room)
- [OkHttp](https://square.github.io/okhttp/)
- [Retrofit](https://square.github.io/retrofit/)

### Testing
- [Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [MockWebServer](https://github.com/square/okhttp/tree/master/mockwebserver)
- [Coroutines Testing](https://kotlinlang.org/api/kotlinx.coroutines/kotlinx-coroutines-test/)

### ShareConnect Integration
- [ShareConnect Documentation](../../../CLAUDE.md)
- [Asinka IPC Library](../../../Asinka/CLAUDE.md)
- [Sync Managers](../../../README.md#synchronization-modules)
