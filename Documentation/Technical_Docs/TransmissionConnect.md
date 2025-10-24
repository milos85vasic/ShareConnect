# TransmissionConnect

## Overview and Purpose

TransmissionConnect is a specialized Android application that provides seamless integration with the Transmission BitTorrent client. It serves as a mobile interface for managing torrents, allowing users to add, monitor, and control torrent downloads directly from their Android device. The app connects to Transmission daemon instances running on local networks or remote servers.

## Architecture and Components

TransmissionConnect is built on top of the Transmission Remote codebase with ShareConnect ecosystem integration:

### Core Components
- **MainActivity**: Main application interface for torrent management
- **TransmissionRemote**: Core Transmission RPC client functionality
- **SecurityAccessHelper**: Handles authentication and security features
- **HistoryActivity**: Displays torrent history and completed downloads

### UI Components
- **Torrent List**: Displays active and completed torrents
- **Torrent Details**: Shows detailed information about individual torrents
- **Add Torrent Dialog**: Interface for adding new torrents via URL or file
- **Settings Panel**: Configuration for server connection and preferences

### Integration Components
- **Asinka Sync**: Cross-device synchronization of torrent data
- **ThemeSync**: Theme synchronization with other ShareConnect apps
- **ProfileSync**: Server profile management
- **SecurityAccess**: Authentication integration

## API Reference

### TransmissionRemote
```java
public class TransmissionRemote {
    public void connect(String host, int port, String username, String password)
    public void addTorrent(String url)
    public void pauseTorrent(int torrentId)
    public void resumeTorrent(int torrentId)
    public void removeTorrent(int torrentId, boolean deleteData)
    public List<Torrent> getTorrents()
    public Torrent getTorrentDetails(int torrentId)
}
```

### SecurityAccessHelper
```kotlin
class SecurityAccessHelper {
    fun checkAccess(): Boolean
    fun requestAuthentication()
    fun onAuthenticationResult(success: Boolean)
}
```

## Key Classes and Their Responsibilities

### MainActivity
- **Responsibilities**:
  - Main application entry point and UI orchestration
  - Torrent list display and management
  - Server connection handling
  - Navigation between different app sections
  - Integration with ShareConnect security features

### TransmissionRemote
- **Responsibilities**:
  - RPC communication with Transmission daemon
  - Torrent CRUD operations (Create, Read, Update, Delete)
  - Session management and authentication
  - Real-time torrent status updates
  - Error handling and connection recovery

### HistoryActivity
- **Responsibilities**:
  - Display completed and historical torrent downloads
  - Torrent metadata management
  - Search and filtering functionality
  - Integration with ShareConnect history sync

### TransmissionConnectOnboardingActivity
- **Responsibilities**:
  - First-time setup wizard for new users
  - Server configuration and connection testing
  - Permission requests and initial setup
  - Integration with ShareConnect onboarding flow

## Data Models

### Torrent
```kotlin
data class Torrent(
    val id: Int,
    val name: String,
    val status: TorrentStatus,
    val progress: Float,
    val downloadSpeed: Long,
    val uploadSpeed: Long,
    val totalSize: Long,
    val downloadedSize: Long,
    val eta: Long,
    val peersConnected: Int,
    val peersTotal: Int,
    val files: List<TorrentFile>
)
```

### ServerProfile
```kotlin
data class ServerProfile(
    val id: String,
    val name: String,
    val host: String,
    val port: Int = 9091,
    val username: String? = null,
    val password: String? = null,
    val useHttps: Boolean = false,
    val rpcPath: String = "/transmission/rpc"
)
```

### TorrentFile
```kotlin
data class TorrentFile(
    val name: String,
    val size: Long,
    val downloaded: Long,
    val priority: FilePriority,
    val wanted: Boolean
)
```

## Usage Examples

### Connecting to Transmission Server
```java
TransmissionRemote remote = new TransmissionRemote();
remote.connect("192.168.1.100", 9091, "username", "password");
```

### Adding a Torrent
```java
TransmissionRemote remote = new TransmissionRemote();
// Add via magnet link
remote.addTorrent("magnet:?xt=urn:btih:...");
// Add via torrent file URL
remote.addTorrent("https://example.com/file.torrent");
```

### Managing Torrent State
```java
TransmissionRemote remote = new TransmissionRemote();
List<Torrent> torrents = remote.getTorrents();

// Pause a torrent
remote.pauseTorrent(torrentId);

// Resume a torrent
remote.resumeTorrent(torrentId);

// Remove torrent and data
remote.removeTorrent(torrentId, true);
```

## Dependencies

### Core Transmission Dependencies
- `net.yupol:transmissionremote-core:3.0.0` - Transmission RPC client core
- `net.yupol:transmissionremote-model:3.0.0` - Data models for Transmission RPC

### Android Framework Dependencies
- `androidx.appcompat:appcompat:1.6.1` - AppCompat support
- `androidx.core:core-ktx:1.12.0` - Core Kotlin extensions
- `com.google.android.material:material:1.11.0` - Material Design components
- `androidx.activity:activity-ktx:1.8.2` - Activity extensions
- `androidx.fragment:fragment-ktx:1.6.6` - Fragment extensions

### Networking Dependencies
- `com.squareup.okhttp3:okhttp:4.12.0` - HTTP client for RPC calls
- `com.squareup.okhttp3:logging-interceptor:4.12.0` - Network logging

### ShareConnect Integration
- Project modules: `:Asinka:asinka`, `:DesignSystem`, `:Onboarding`
- Toolkit modules: `:Toolkit:Main`, `:Toolkit:SecurityAccess`
- Sync modules: `:ThemeSync`, `:ProfileSync`

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `androidx.test.ext:junit:1.3.0` - Android test runner
- `androidx.test.espresso:espresso-core:3.7.0` - UI testing
- `org.mockito:mockito-core:5.8.0` - Mocking framework

---

*For more information, visit [https://shareconnect.org/docs/transmissionconnect](https://shareconnect.org/docs/transmissionconnect)*