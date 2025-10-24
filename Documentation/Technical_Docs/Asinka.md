# Asinka

## Overview and Purpose

Asinka is a powerful Android Inter-Process Communication (IPC) library built on gRPC that enables real-time object synchronization between Android applications. It provides a robust framework for cross-app data synchronization, service discovery, and secure communication channels. Asinka serves as the backbone for the ShareConnect ecosystem's sync capabilities.

## Architecture and Components

Asinka follows a modular architecture with clear separation of concerns:

### Core Components
- **AsinkaClient**: Main entry point and client orchestration
- **SyncManager**: Handles object synchronization logic
- **GrpcTransport**: gRPC-based communication layer
- **ServiceDiscovery**: Automatic service discovery using Nearby API
- **HandshakeManager**: Secure connection establishment
- **SecurityManager**: Encryption and authentication
- **EventManager**: Event-driven communication system

### Data Layer
- **ObjectSchema**: Defines synchronization data structures
- **SyncableObject**: Base class for synchronizable objects
- **Database Integration**: Room-based local persistence

## API Reference

### AsinkaClient
```kotlin
class AsinkaClient(
    context: Context,
    appId: String,
    privateKey: String
) {
    fun start(): Flow<AsinkaEvent>
    fun stop()
    fun registerSyncableObject(obj: SyncableObject)
    fun unregisterSyncableObject(obj: SyncableObject)
    fun sendEvent(event: AsinkaEvent)
    fun getConnectedPeers(): List<PeerInfo>
}
```

### SyncManager
```kotlin
class SyncManager {
    fun <T : SyncableObject> syncObject(obj: T): Flow<SyncResult<T>>
    fun createSyncChannel(peerId: String): SyncChannel
    fun getSyncStatus(objectId: String): SyncStatus
    fun resolveConflicts(objects: List<SyncableObject>): SyncableObject
}
```

### ServiceDiscovery
```kotlin
class ServiceDiscovery(context: Context) {
    fun startDiscovery(serviceType: String): Flow<DiscoveredService>
    fun stopDiscovery()
    fun advertiseService(serviceInfo: ServiceInfo): Flow<ConnectionRequest>
    fun connectToService(serviceId: String): Flow<ConnectionResult>
}
```

## Key Classes and Their Responsibilities

### AsinkaClient
- **Responsibilities**:
  - Main client lifecycle management (start/stop)
  - Component initialization and coordination
  - Event routing and handling
  - Peer connection management
  - Error handling and recovery

### SyncManager
- **Responsibilities**:
  - Object synchronization orchestration
  - Conflict resolution strategies
  - Sync status tracking and reporting
  - Data consistency maintenance
  - Performance optimization

### GrpcTransport
- **Responsibilities**:
  - gRPC channel management
  - Protocol buffer serialization/deserialization
  - Bidirectional streaming setup
  - Connection pooling and reuse
  - Network error handling

### ServiceDiscovery
- **Responsibilities**:
  - Nearby API integration for service discovery
  - Service advertisement and discovery
  - Connection establishment coordination
  - Peer information management
  - Network topology awareness

### HandshakeManager
- **Responsibilities**:
  - Secure connection establishment protocol
  - Cryptographic key exchange
  - Peer authentication and verification
  - Session key generation
  - Connection security validation

### SecurityManager
- **Responsibilities**:
  - End-to-end encryption management
  - Authentication token handling
  - Data integrity verification
  - Secure key storage
  - Certificate management

## Data Models

### SyncableObject
```kotlin
abstract class SyncableObject {
    abstract val id: String
    abstract val version: Long
    abstract val lastModified: Long
    abstract val ownerId: String

    fun toProto(): SyncObjectProto
    fun fromProto(proto: SyncObjectProto): SyncableObject
    fun merge(other: SyncableObject): SyncableObject
}
```

### ObjectSchema
```kotlin
data class ObjectSchema(
    val typeName: String,
    val fields: List<FieldSchema>,
    val version: Int,
    val migrationRules: List<MigrationRule>
)

data class FieldSchema(
    val name: String,
    val type: FieldType,
    val required: Boolean,
    val defaultValue: Any?
)
```

### AsinkaEvent
```kotlin
sealed class AsinkaEvent {
    data class PeerConnected(val peerId: String, val peerInfo: PeerInfo) : AsinkaEvent()
    data class PeerDisconnected(val peerId: String) : AsinkaEvent()
    data class ObjectSynced(val objectId: String, val result: SyncResult) : AsinkaEvent()
    data class SyncConflict(val objectId: String, val conflicts: List<SyncableObject>) : AsinkaEvent()
    data class Error(val error: AsinkaError) : AsinkaEvent()
}
```

## Usage Examples

### Basic Client Setup
```kotlin
val asinkaClient = AsinkaClient(
    context = applicationContext,
    appId = "com.shareconnect.main",
    privateKey = getPrivateKey()
)

// Start the client
asinkaClient.start().collect { event ->
    when (event) {
        is AsinkaEvent.PeerConnected -> {
            // Handle peer connection
        }
        is AsinkaEvent.ObjectSynced -> {
            // Handle object synchronization
        }
    }
}
```

### Registering Syncable Objects
```kotlin
// Define a syncable object
data class UserProfile(
    override val id: String,
    val name: String,
    val email: String,
    override val version: Long = 1,
    override val lastModified: Long = System.currentTimeMillis(),
    override val ownerId: String = deviceId
) : SyncableObject()

// Register for synchronization
val userProfile = UserProfile("user123", "John Doe", "john@example.com")
asinkaClient.registerSyncableObject(userProfile)
```

### Service Discovery
```kotlin
val discovery = ServiceDiscovery(context)

// Start discovering services
discovery.startDiscovery("shareconnect-sync").collect { service ->
    // Connect to discovered service
    discovery.connectToService(service.id).collect { result ->
        when (result) {
            is ConnectionResult.Success -> {
                // Connection established
            }
            is ConnectionResult.Failure -> {
                // Handle connection failure
            }
        }
    }
}
```

### Handling Sync Conflicts
```kotlin
asinkaClient.start().collect { event ->
    when (event) {
        is AsinkaEvent.SyncConflict -> {
            val resolved = syncManager.resolveConflicts(event.conflicts)
            // Apply resolved object
        }
    }
}
```

## Dependencies

### Core gRPC Dependencies
- `io.grpc:grpc-core:1.57.1` - gRPC core functionality
- `io.grpc:grpc-kotlin-stub:1.4.0` - Kotlin gRPC stubs
- `io.grpc:grpc-netty-shaded:1.57.1` - Netty-based transport
- `io.grpc:grpc-protobuf:1.57.1` - Protocol buffer support
- `com.google.protobuf:protobuf-java:3.25.1` - Protocol buffers
- `com.google.protobuf:protobuf-kotlin:3.25.1` - Kotlin protocol buffers

### Kotlin Coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-android:1.10.2` - Android coroutines
- `org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.2` - Core coroutines

### Android Framework
- `androidx.core:core-ktx:1.17.0` - Core Android extensions
- `androidx.lifecycle:lifecycle-runtime-ktx:2.9.4` - Lifecycle components
- `androidx.lifecycle:lifecycle-process:2.9.4` - Process lifecycle

### Database
- `androidx.room:room-runtime:2.6.1` - Room database runtime
- `androidx.room:room-ktx:2.6.1` - Room Kotlin extensions
- `androidx.room:room-compiler:2.6.1` - Room annotation processor

### Security and Networking
- `net.zetetic:sqlcipher-android:4.10.0` - Encrypted SQLite database
- `com.google.android.gms:play-services-nearby:19.3.0` - Nearby service discovery
- `androidx.security:security-crypto:1.1.0` - Android security crypto

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.2` - Coroutine testing
- `org.mockito.kotlin:mockito-kotlin:5.4.0` - Mockito Kotlin extensions
- `io.mockk:mockk:1.13.13` - Mocking framework
- `org.robolectric:robolectric:4.13` - Android unit testing

---

*For more information, visit [https://shareconnect.org/docs/asinka](https://shareconnect.org/docs/asinka)*