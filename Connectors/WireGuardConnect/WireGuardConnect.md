# WireGuardConnect - Technical Documentation

## Overview

**WireGuardConnect** is a comprehensive Android application for managing WireGuard VPN configurations. It provides full support for creating, editing, importing, and exporting WireGuard configurations with QR code generation and scanning capabilities.

**Package**: `com.shareconnect.wireguardconnect`
**Version**: 1.0.0
**Target**: Android API 28-36
**License**: ShareConnect Ecosystem

## Table of Contents

1. [Architecture](#architecture)
2. [Features](#features)
3. [Build System](#build-system)
4. [Configuration Management](#configuration-management)
5. [QR Code Support](#qr-code-support)
6. [API Integration](#api-integration)
7. [Data Models](#data-models)
8. [Security](#security)
9. [Testing](#testing)
10. [Development Guidelines](#development-guidelines)

## Architecture

### Application Structure

WireGuardConnect follows the ShareConnect ecosystem architecture with:

- **UI Layer**: Jetpack Compose with Material Design 3
- **Business Logic Layer**: Configuration and QR code managers
- **Data Layer**: File-based configuration storage
- **Network Layer**: Optional API client for server management
- **Sync Layer**: 8 sync managers for cross-app data sharing

### Core Components

#### 1. WireGuardConfigManager

Central component for configuration management:

```kotlin
class WireGuardConfigManager(context: Context) {
    // Configuration parsing
    suspend fun parseConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig>
    suspend fun parseConfigFile(file: File): WireGuardResult<WireGuardConfig>

    // Configuration management
    suspend fun saveConfig(config: WireGuardConfig): WireGuardResult<File>
    suspend fun deleteConfig(configId: String): WireGuardResult<Boolean>
    suspend fun listConfigs(): WireGuardResult<List<WireGuardConfig>>

    // Configuration creation
    fun createConfig(...): WireGuardConfig
    fun generateKeyPair(): WireGuardKeyPair

    // Import/Export
    fun exportConfig(config: WireGuardConfig): String
    suspend fun importConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig>
}
```

**Responsibilities**:
- Parse WireGuard config files (INI format)
- Validate configuration integrity
- Generate Curve25519 key pairs
- Manage configuration persistence
- Import/export configurations

#### 2. QRCodeManager

Handles QR code generation and scanning:

```kotlin
class QRCodeManager {
    // QR code generation
    suspend fun generateQRCode(config: WireGuardConfig, size: Int): WireGuardResult<Bitmap>
    suspend fun generateQRCodeFromString(configContent: String, size: Int): WireGuardResult<Bitmap>

    // QR code parsing
    suspend fun parseQRCodeContent(name: String, content: String): WireGuardResult<WireGuardConfig>

    // Utility functions
    fun estimateQRCodeSize(config: WireGuardConfig): Int
    fun canEncodeAsQRCode(config: WireGuardConfig): Boolean
    fun getMaxQRCodeSize(): Int

    // Shareable QR codes
    suspend fun createShareableQRCode(config: WireGuardConfig, size: Int, includeLabel: Boolean): WireGuardResult<Bitmap>
}
```

**Features**:
- Generate QR codes from configurations
- Parse QR codes back to configurations
- Size validation (max 2953 bytes)
- Error correction level M
- Custom sizing support

#### 3. WireGuardApiClient

Optional API client for server management:

```kotlin
class WireGuardApiClient(baseUrl: String, username: String?, password: String?) {
    // Server status
    suspend fun getServerStatus(): WireGuardResult<ServerStatus>

    // Peer management
    suspend fun getPeers(): WireGuardResult<List<PeerInfo>>
    suspend fun getPeerStatistics(publicKey: String): WireGuardResult<WireGuardStatistics>
    suspend fun addPeer(publicKey: String, allowedIps: List<String>, presharedKey: String?): WireGuardResult<Boolean>
    suspend fun removePeer(publicKey: String): WireGuardResult<Boolean>

    // Configuration management
    suspend fun getServerConfig(): WireGuardResult<String>
    suspend fun updateServerConfig(config: String): WireGuardResult<Boolean>

    // Connection testing
    suspend fun testConnection(): WireGuardResult<Boolean>
}
```

**Capabilities**:
- Server status monitoring
- Peer management (add/remove)
- Configuration retrieval
- Statistics collection
- Connection testing

### Sync Managers

WireGuardConnect integrates 8 sync managers for ShareConnect ecosystem:

1. **ThemeSyncManager** (port 8890) - Theme preferences
2. **ProfileSyncManager** (port 8900) - VPN profiles
3. **HistorySyncManager** (port 8910) - Connection history
4. **RssSyncManager** (port 8920) - RSS feeds
5. **BookmarkSyncManager** (port 8930) - Bookmarks
6. **PreferencesSyncManager** (port 8940) - App preferences
7. **LanguageSyncManager** (port 8950) - Language settings
8. **TorrentSharingSyncManager** (port 8960) - Torrent data

All sync managers use Asinka for real-time gRPC-based synchronization.

## Features

### Core Features

#### 1. Configuration Management

**Create Configurations**:
```kotlin
val config = configManager.createConfig(
    name = "My VPN",
    address = listOf("10.0.0.2/24", "fd00::2/64"),
    dns = listOf("1.1.1.1", "1.0.0.1"),
    mtu = 1420,
    peerPublicKey = "base64-public-key",
    peerEndpoint = "vpn.example.com:51820",
    peerAllowedIps = listOf("0.0.0.0/0", "::/0"),
    persistentKeepalive = 25
)
```

**Parse Configurations**:
```kotlin
val result = configManager.parseConfig("MyVPN", configFileContent)
when (result) {
    is WireGuardResult.Success -> println("Config loaded: ${result.data.name}")
    is WireGuardResult.Error -> println("Error: ${result.message}")
}
```

**Save/Load Configurations**:
```kotlin
// Save
configManager.saveConfig(config)

// List all
val configs = configManager.listConfigs()

// Delete
configManager.deleteConfig(configId)
```

#### 2. Key Management

**Generate Key Pairs**:
```kotlin
val keyPair = configManager.generateKeyPair()
println("Private Key: ${keyPair.privateKey}")
println("Public Key: ${keyPair.publicKey}")
```

**Key Format**:
- Curve25519 keys
- Base64 encoded
- Properly clamped for WireGuard

#### 3. QR Code Support

**Generate QR Code**:
```kotlin
val qrResult = qrCodeManager.generateQRCode(config, size = 512)
when (qrResult) {
    is WireGuardResult.Success -> displayQRCode(qrResult.data)
    is WireGuardResult.Error -> showError(qrResult.message)
}
```

**Scan QR Code**:
```kotlin
// After scanning QR code and getting string content
val parseResult = qrCodeManager.parseQRCodeContent("Scanned Config", qrContent)
when (parseResult) {
    is WireGuardResult.Success -> saveConfig(parseResult.data)
    is WireGuardResult.Error -> showError(parseResult.message)
}
```

**Size Validation**:
```kotlin
if (qrCodeManager.canEncodeAsQRCode(config)) {
    // Generate QR code
} else {
    // Config too large, simplify or use file transfer
}
```

#### 4. Configuration Format Support

**WireGuard INI Format**:
```ini
[Interface]
PrivateKey = base64-encoded-private-key
Address = 10.0.0.2/24, fd00::2/64
DNS = 1.1.1.1, 1.0.0.1
MTU = 1420

[Peer]
PublicKey = base64-encoded-public-key
PresharedKey = base64-encoded-psk (optional)
AllowedIPs = 0.0.0.0/0, ::/0
Endpoint = vpn.example.com:51820
PersistentKeepalive = 25
```

**Supported Fields**:

Interface:
- PrivateKey (required)
- Address (required, multiple allowed)
- DNS (optional, multiple allowed)
- MTU (optional)
- ListenPort (optional)
- Table (optional)
- PreUp, PostUp, PreDown, PostDown (optional)

Peer:
- PublicKey (required)
- PresharedKey (optional)
- AllowedIPs (required, multiple allowed)
- Endpoint (optional)
- PersistentKeepalive (optional)

#### 5. Import/Export

**Export Configuration**:
```kotlin
val configString = configManager.exportConfig(config)
saveToFile(configString)
```

**Import Configuration**:
```kotlin
val configContent = readFromFile()
val importResult = configManager.importConfig("Imported VPN", configContent)
```

**Import from File**:
```kotlin
val file = File("/path/to/config.conf")
val parseResult = configManager.parseConfigFile(file)
```

## Build System

### Dependencies

```gradle
// ZXing for QR codes
implementation 'com.google.zxing:core:3.5.3'
implementation 'com.google.zxing:android-core:3.5.3'
implementation 'com.journeyapps:zxing-android-embedded:4.3.0'

// Networking
implementation 'io.ktor:ktor-client-core:3.2.3'
implementation 'io.ktor:ktor-client-okhttp:3.2.3'
implementation 'io.ktor:ktor-client-content-negotiation:3.2.3'

// ShareConnect modules
implementation project(':ThemeSync')
implementation project(':ProfileSync')
implementation project(':HistorySync')
// ... other sync managers
```

### Build Commands

```bash
# Build debug APK
./gradlew :WireGuardConnector:assembleDebug

# Run unit tests
./gradlew :WireGuardConnector:test

# Run integration tests
./gradlew :WireGuardConnector:connectedAndroidTest

# Run all tests
./gradlew :WireGuardConnector:test :WireGuardConnector:connectedAndroidTest
```

## Data Models

### WireGuardConfig

```kotlin
@Serializable
data class WireGuardConfig(
    val id: String,
    val name: String,
    val interface: WireGuardInterface,
    val peers: List<WireGuardPeer>,
    val createdAt: Long,
    val lastModified: Long,
    val isActive: Boolean
) {
    fun toConfigString(): String
    companion object {
        fun fromConfigString(name: String, configContent: String): WireGuardConfig
    }
}
```

### WireGuardInterface

```kotlin
@Serializable
data class WireGuardInterface(
    val privateKey: String,
    val address: List<String>,
    val dns: List<String>,
    val mtu: Int?,
    val listenPort: Int?,
    val table: String?,
    val preUp: String?,
    val postUp: String?,
    val preDown: String?,
    val postDown: String?
)
```

### WireGuardPeer

```kotlin
@Serializable
data class WireGuardPeer(
    val publicKey: String,
    val presharedKey: String?,
    val allowedIps: List<String>,
    val endpoint: String?,
    val persistentKeepalive: Int?
)
```

### WireGuardResult

```kotlin
sealed class WireGuardResult<out T> {
    data class Success<T>(val data: T) : WireGuardResult<T>()
    data class Error(val message: String, val exception: Throwable?) : WireGuardResult<Nothing>()
}
```

## Security

### SecurityAccess Integration

WireGuardConnect uses the SecurityAccess library for app-level protection:

```kotlin
class MainActivity : ComponentActivity() {
    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        securityAccessManager = SecurityAccessManager.getInstance(this)

        if (isSecurityAccessRequired()) {
            launchSecurityAccess()
            return
        }

        setupMainUI()
    }
}
```

### Configuration Security

**File Storage**:
- Configurations stored in app private directory
- Not accessible to other apps
- Backed up via Android Auto Backup (encrypted)

**Key Management**:
- Private keys never leave the device
- Secure key generation using SecureRandom
- Proper Curve25519 clamping

**Network Security**:
- HTTPS for API communications
- Certificate pinning support
- Custom SSL trust anchors

## Testing

### Test Coverage

**Total Tests**: 38+
- Unit Tests: 14
- Integration Tests: 17
- Automation Tests: 7

### Unit Tests (14)

Location: `src/test/kotlin/`

**WireGuardConfigManagerTest**:
1. Parse valid configuration
2. Parse configuration with multiple peers
3. Parse configuration with comments
4. Parse invalid configuration (missing interface)
5. Create configuration
6. Save and load configuration
7. Generate key pair
8. Export configuration
9. Import configuration
10. Delete configuration
11. Parse configuration file
12. Validate IPv4 addresses
13. Validate IPv6 addresses
14. List empty configurations

**QRCodeManagerTest**:
1. Generate QR code from config
2. Generate QR code with custom size
3. Generate QR code from string
4. Parse QR code content
5. Parse invalid QR code content
6. Estimate QR code size
7. Check if config can be encoded
8. Check large config cannot be encoded
9. Get max QR code size
10. Create shareable QR code
11. Create shareable QR code without label
12. Generate QR code from minimal config
13. Round trip QR code generation and parsing

### Integration Tests (17)

Location: `src/androidTest/kotlin/integration/`

**WireGuardConfigIntegrationTest**:
1. Create, save, and load configuration
2. Configuration parsing and export
3. QR code generation and parsing
4. Key pair generation uniqueness
5. Configuration with multiple addresses
6. Configuration without DNS
7. Configuration update flow
8. QR code size estimation
9. Shareable QR code creation
10. Configuration import/export round trip
11. Multiple configurations management
12. Configuration with IPv6 only
13. Configuration with custom MTU
14. Configuration without persistent keepalive
15. QR code with different sizes
16. Configuration with all optional fields
17. End-to-end configuration workflow

### Automation Tests (7)

Location: `src/androidTest/kotlin/automation/`

**WireGuardConnectAutomationTest**:
1. Launch app
2. Config manager initialization
3. QR code manager initialization
4. Create configuration flow
5. Save configuration flow
6. QR code generation flow
7. Key pair generation flow
8. App resume from background

### Running Tests

```bash
# Run all unit tests
./gradlew :WireGuardConnector:test

# Run specific test
./gradlew :WireGuardConnector:test --tests WireGuardConfigManagerTest

# Run integration tests
./gradlew :WireGuardConnector:connectedAndroidTest

# Run specific integration test
./gradlew :WireGuardConnector:connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.class=\
com.shareconnect.wireguardconnect.integration.WireGuardConfigIntegrationTest

# Run automation tests
./gradlew :WireGuardConnector:connectedAndroidTest \
  -Pandroid.testInstrumentationRunnerArguments.package=\
com.shareconnect.wireguardconnect.automation
```

## Development Guidelines

### Code Style

**Kotlin Conventions**:
- Use Kotlin idioms and best practices
- Prefer `val` over `var`
- Use data classes for models
- Leverage coroutines for async operations

**Naming**:
- Classes: PascalCase
- Functions: camelCase
- Constants: UPPER_SNAKE_CASE
- Private members: leading underscore optional

### Error Handling

**Result Pattern**:
```kotlin
sealed class WireGuardResult<out T> {
    data class Success<T>(val data: T) : WireGuardResult<T>()
    data class Error(val message: String, val exception: Throwable?) : WireGuardResult<Nothing>()
}
```

**Usage**:
```kotlin
when (val result = configManager.parseConfig(name, content)) {
    is WireGuardResult.Success -> {
        // Handle success
        val config = result.data
    }
    is WireGuardResult.Error -> {
        // Handle error
        Log.e(TAG, result.message, result.exception)
    }
}
```

### Adding New Features

1. **Update Data Models** if needed
2. **Implement Business Logic** in managers
3. **Add UI Components** in Compose
4. **Write Unit Tests** for logic
5. **Write Integration Tests** for flows
6. **Update Documentation**

### Performance Considerations

**Configuration Parsing**:
- Async operations with coroutines
- Efficient string parsing
- Minimal allocations

**QR Code Generation**:
- Default to Dispatchers.Default
- Configurable sizes for performance
- Size validation before generation

**File I/O**:
- All file operations on IO dispatcher
- Efficient file reading/writing
- Proper resource cleanup

## API Reference

### WireGuardConfigManager API

```kotlin
// Parsing
suspend fun parseConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig>
suspend fun parseConfigFile(file: File): WireGuardResult<WireGuardConfig>

// Persistence
suspend fun saveConfig(config: WireGuardConfig): WireGuardResult<File>
suspend fun deleteConfig(configId: String): WireGuardResult<Boolean>
suspend fun listConfigs(): WireGuardResult<List<WireGuardConfig>>

// Creation
fun generateKeyPair(): WireGuardKeyPair
fun createConfig(
    name: String,
    address: List<String>,
    privateKey: String? = null,
    dns: List<String> = listOf("1.1.1.1", "1.0.0.1"),
    mtu: Int? = 1420,
    peerPublicKey: String,
    peerEndpoint: String,
    peerAllowedIps: List<String> = listOf("0.0.0.0/0", "::/0"),
    persistentKeepalive: Int? = 25
): WireGuardConfig

// Import/Export
fun exportConfig(config: WireGuardConfig): String
suspend fun importConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig>
```

### QRCodeManager API

```kotlin
// Generation
suspend fun generateQRCode(config: WireGuardConfig, size: Int = 512): WireGuardResult<Bitmap>
suspend fun generateQRCodeFromString(configContent: String, size: Int = 512): WireGuardResult<Bitmap>

// Parsing
suspend fun parseQRCodeContent(name: String, content: String): WireGuardResult<WireGuardConfig>

// Utilities
fun estimateQRCodeSize(config: WireGuardConfig): Int
fun canEncodeAsQRCode(config: WireGuardConfig): Boolean
fun getMaxQRCodeSize(): Int = 2953

// Shareable
suspend fun createShareableQRCode(
    config: WireGuardConfig,
    size: Int = 512,
    includeLabel: Boolean = true
): WireGuardResult<Bitmap>
```

## Troubleshooting

### Common Issues

**QR Code Too Large**:
- Reduce configuration complexity
- Remove optional fields
- Use file transfer instead

**Configuration Parse Error**:
- Validate INI format
- Check for required fields
- Verify Base64 encoding

**Key Generation Issues**:
- Ensure sufficient entropy
- Check Android version compatibility

### Debug Logging

Enable debug logging:
```kotlin
Log.d("WireGuardConnect", "Message")
```

Tags:
- `WireGuardConfigManager`
- `QRCodeManager`
- `WireGuardApiClient`
- `MainActivity`

## Version History

### 1.0.0 (Current)
- Initial release
- Configuration management
- QR code support
- API client integration
- 8 sync managers
- Comprehensive testing (38+ tests)
- Full documentation

## License

Part of the ShareConnect ecosystem.
All rights reserved.

## Support

For issues, questions, or contributions:
- Check documentation
- Review test cases
- Consult ShareConnect main documentation
