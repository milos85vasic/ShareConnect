# uTorrent Connector Usage Guide

## Table of Contents
- [Introduction](#introduction)
- [Prerequisites](#prerequisites)
- [Setting Up uTorrent Web UI](#setting-up-utorrent-web-ui)
- [Configuring the Connector](#configuring-the-connector)
- [Basic Operations](#basic-operations)
- [Advanced Features](#advanced-features)
- [Code Examples](#code-examples)
- [Troubleshooting](#troubleshooting)
- [API Reference](#api-reference)

## Introduction

The uTorrent Connector enables remote control of uTorrent BitTorrent clients through the uTorrent Web UI API. This connector is part of the ShareConnect application ecosystem and provides seamless integration with uTorrent servers.

### Key Features
- **Token-based authentication** - Secure connection with automatic token refresh
- **Full torrent management** - List, add, remove, start, stop, pause torrents
- **Real-time monitoring** - Track download/upload speeds, progress, and peer information
- **Settings management** - Get and set uTorrent server settings
- **Multiple server support** - Connect to multiple uTorrent instances
- **Backward compatibility** - Transparent protocol conversion for existing code

## Prerequisites

### uTorrent Server Requirements
- uTorrent 2.0 or later (Windows, Mac, or Linux)
- uTorrent Web UI enabled
- Network access to uTorrent server (same network or port forwarding configured)

### Android Application Requirements
- Android 8.0 (API 26) or higher
- Network connectivity
- ShareConnect application installed

## Setting Up uTorrent Web UI

### 1. Enable Web UI in uTorrent

1. Open uTorrent client
2. Go to **Options** → **Preferences** → **Web UI**
3. Check **Enable Web UI**
4. Set authentication credentials:
   - **Username**: Your chosen username (e.g., `admin`)
   - **Password**: Your chosen password (strong password recommended)
5. Note the **Port** (default: 8080)
6. Optional: Check **Alternative listening port** to use a different port
7. Click **OK** to save

### 2. Configure Firewall (if needed)

If accessing uTorrent remotely:
```bash
# Linux (ufw)
sudo ufw allow 8080/tcp

# Windows Firewall
# Add inbound rule for TCP port 8080 in Windows Defender Firewall
```

### 3. Test Web UI Access

Open a web browser and navigate to:
```
http://localhost:8080/gui/
```

Or for remote access:
```
http://YOUR_SERVER_IP:8080/gui/
```

You should see the uTorrent Web UI login page.

## Configuring the Connector

### Adding a Server in ShareConnect

1. Open ShareConnect application
2. Navigate to **Settings** → **Servers**
3. Tap **Add Server** → **uTorrent**
4. Fill in the server details:
   - **Name**: A friendly name (e.g., "Home Server")
   - **Host**: IP address or hostname (e.g., `192.168.1.100` or `home.example.com`)
   - **Port**: Web UI port (default: `8080`)
   - **Username**: Your uTorrent Web UI username
   - **Password**: Your uTorrent Web UI password
   - **Use HTTPS**: Enable if using SSL/TLS (requires additional setup)
   - **Trust self-signed certificates**: Enable if using self-signed SSL certificate

### Server Configuration Object

```java
Server server = new Server();
server.setName("My uTorrent Server");
server.setHost("192.168.1.100");
server.setPort(8080);
server.setUserName("admin");
server.setPassword("mySecurePassword");
server.setUseHttps(false); // Set to true for HTTPS
server.setTrustSelfSignedSslCert(false); // Set to true for self-signed certs
```

## Basic Operations

### 1. Listing Torrents

```kotlin
import com.shareconnect.utorrentconnect.transport.request.TorrentGetRequest
import com.shareconnect.utorrentconnect.transport.okhttp.OkHttpTransportManager

val transportManager = OkHttpTransportManager(server)
val request = TorrentGetRequest()

transportManager.doRequest(request, object : RequestListener<TorrentList> {
    override fun onRequestSuccess(result: TorrentList?) {
        result?.torrents?.forEach { torrent ->
            println("Torrent: ${torrent.name}")
            println("  Hash: ${torrent.hash}")
            println("  Progress: ${torrent.percentDone * 100}%")
            println("  Status: ${torrent.status}")
            println("  Download: ${torrent.downloadRate} bytes/s")
            println("  Upload: ${torrent.uploadRate} bytes/s")
        }
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Error: ${error?.message}")
    }
})
```

### 2. Adding a Torrent

#### Add by Magnet Link or URL

```kotlin
import com.shareconnect.utorrentconnect.transport.request.AddTorrentByUrlRequest

val magnetLink = "magnet:?xt=urn:btih:..."
val request = AddTorrentByUrlRequest(magnetLink)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent added successfully")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to add torrent: ${error?.message}")
    }
})
```

#### Add by Torrent File

```kotlin
import com.shareconnect.utorrentconnect.transport.request.AddTorrentByFileRequest

val torrentFile = File("/path/to/file.torrent")
val request = AddTorrentByFileRequest(torrentFile)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent file uploaded and added")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to add torrent file: ${error?.message}")
    }
})
```

### 3. Starting a Torrent

```kotlin
import com.shareconnect.utorrentconnect.transport.request.StartTorrentRequest

// Using hash string
val torrentHash = "abc123def456..."
val request = StartTorrentRequest(torrentHash)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent started")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to start torrent: ${error?.message}")
    }
})
```

### 4. Stopping a Torrent

```kotlin
import com.shareconnect.utorrentconnect.transport.request.StopTorrentRequest

val request = StopTorrentRequest(torrentHash)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent stopped")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to stop torrent: ${error?.message}")
    }
})
```

### 5. Pausing a Torrent

```kotlin
import com.shareconnect.utorrentconnect.transport.request.PauseTorrentRequest

val request = PauseTorrentRequest(torrentHash)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent paused")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to pause torrent: ${error?.message}")
    }
})
```

### 6. Removing a Torrent

#### Remove Torrent Only (keep downloaded files)

```kotlin
import com.shareconnect.utorrentconnect.transport.request.TorrentRemoveRequest

val request = TorrentRemoveRequest(intArrayOf(torrentId), false)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent removed (files kept)")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to remove torrent: ${error?.message}")
    }
})
```

#### Remove Torrent and Data

```kotlin
val request = TorrentRemoveRequest(intArrayOf(torrentId), true)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent and data removed")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to remove torrent and data: ${error?.message}")
    }
})
```

## Advanced Features

### 1. Torrent Status and State

The connector maps uTorrent's bitfield status to Transmission-compatible enum values:

```kotlin
val torrent: Torrent = // ... from TorrentGetRequest

when (torrent.status) {
    Torrent.Status.STOPPED -> println("Stopped")
    Torrent.Status.CHECK_WAIT -> println("Waiting to check")
    Torrent.Status.CHECK -> println("Checking files")
    Torrent.Status.DOWNLOAD_WAIT -> println("Waiting to download")
    Torrent.Status.DOWNLOAD -> println("Downloading")
    Torrent.Status.SEED_WAIT -> println("Waiting to seed")
    Torrent.Status.SEED -> println("Seeding")
}

// Check specific states
if (torrent.isDownloading()) {
    println("Currently downloading at ${torrent.downloadRate} bytes/s")
}

if (torrent.isSeeding()) {
    println("Currently seeding at ${torrent.uploadRate} bytes/s")
}

if (torrent.isFinished()) {
    println("Download complete!")
}
```

### 2. Progress and Statistics

```kotlin
val torrent: Torrent = // ... from TorrentGetRequest

// Progress
val percentComplete = (torrent.percentDone * 100).toInt()
println("Progress: $percentComplete%")

// Sizes
val downloaded = torrent.sizeWhenDone - torrent.leftUntilDone
println("Downloaded: ${formatBytes(downloaded)} / ${formatBytes(torrent.totalSize)}")

// Speeds
println("Download speed: ${formatBytes(torrent.downloadRate)}/s")
println("Upload speed: ${formatBytes(torrent.uploadRate)}/s")

// Peers
println("Peers sending to us: ${torrent.peersSendingToUs}")
println("Peers getting from us: ${torrent.peersGettingFromUs}")

// ETA
if (torrent.eta > 0) {
    println("ETA: ${formatDuration(torrent.eta)}")
} else if (torrent.eta == -1L) {
    println("ETA: Unknown")
}

// Ratio
println("Upload ratio: ${String.format("%.2f", torrent.uploadRatio)}")
```

### 3. Server Settings

#### Get Server Settings

```kotlin
import com.shareconnect.utorrentconnect.transport.request.SessionGetRequest

val request = SessionGetRequest()

transportManager.doRequest(request, object : RequestListener<ServerSettings> {
    override fun onRequestSuccess(result: ServerSettings?) {
        result?.let { settings ->
            println("Download speed limit: ${settings.downloadSpeedLimit}")
            println("Upload speed limit: ${settings.uploadSpeedLimit}")
            println("Port: ${settings.peerPort}")
        }
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to get settings: ${error?.message}")
    }
})
```

#### Set Server Settings

```kotlin
import com.shareconnect.utorrentconnect.transport.request.SessionSetRequest
import org.json.JSONObject

val arguments = JSONObject().apply {
    put("speed-limit-down-enabled", true)
    put("speed-limit-down", 1024) // KB/s
    put("speed-limit-up-enabled", true)
    put("speed-limit-up", 512) // KB/s
}

val request = SessionSetRequest(arguments)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Settings updated")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to update settings: ${error?.message}")
    }
})
```

### 4. Verifying/Rechecking Torrents

```kotlin
import com.shareconnect.utorrentconnect.transport.request.VerifyTorrentRequest

val request = VerifyTorrentRequest(torrentHash)

transportManager.doRequest(request, object : RequestListener<Void> {
    override fun onRequestSuccess(result: Void?) {
        println("Torrent verification started")
    }

    override fun onRequestFailure(error: SpiceException?) {
        println("Failed to verify torrent: ${error?.message}")
    }
})
```

## Code Examples

### Complete Example: Monitor Torrent Progress

```kotlin
import android.os.Handler
import android.os.Looper
import com.shareconnect.utorrentconnect.server.Server
import com.shareconnect.utorrentconnect.transport.okhttp.OkHttpTransportManager
import com.shareconnect.utorrentconnect.transport.request.TorrentGetRequest

class TorrentMonitor(private val server: Server) {

    private val transportManager = OkHttpTransportManager(server)
    private val handler = Handler(Looper.getMainLooper())
    private val updateInterval = 2000L // 2 seconds

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateTorrents()
            handler.postDelayed(this, updateInterval)
        }
    }

    fun start() {
        handler.post(updateRunnable)
    }

    fun stop() {
        handler.removeCallbacks(updateRunnable)
    }

    private fun updateTorrents() {
        val request = TorrentGetRequest()

        transportManager.doRequest(request, object : RequestListener<TorrentList> {
            override fun onRequestSuccess(result: TorrentList?) {
                result?.torrents?.forEach { torrent ->
                    displayTorrentInfo(torrent)
                }
            }

            override fun onRequestFailure(error: SpiceException?) {
                println("Update failed: ${error?.message}")
            }
        })
    }

    private fun displayTorrentInfo(torrent: Torrent) {
        val progress = (torrent.percentDone * 100).toInt()
        val downSpeed = formatBytes(torrent.downloadRate)
        val upSpeed = formatBytes(torrent.uploadRate)

        println("[${torrent.hash.substring(0, 8)}] ${torrent.name}")
        println("  $progress% - ↓$downSpeed/s ↑$upSpeed/s - ${torrent.status.name}")
    }

    private fun formatBytes(bytes: Int): String {
        return when {
            bytes < 1024 -> "$bytes B"
            bytes < 1024 * 1024 -> "${bytes / 1024} KB"
            else -> "${bytes / (1024 * 1024)} MB"
        }
    }
}

// Usage
val server = Server().apply {
    host = "192.168.1.100"
    port = 8080
    userName = "admin"
    password = "password"
}

val monitor = TorrentMonitor(server)
monitor.start()

// Later, when done
monitor.stop()
```

### Example: Add Torrent with Download Path

```kotlin
import com.shareconnect.utorrentconnect.transport.request.AddTorrentByUrlRequest
import java.lang.reflect.Field

fun addTorrentWithPath(
    magnetLink: String,
    downloadPath: String,
    transportManager: OkHttpTransportManager
) {
    val request = AddTorrentByUrlRequest(magnetLink).apply {
        // Set download destination using reflection
        // Note: This is a workaround until proper setter is added
        try {
            val field: Field = AddTorrentRequest::class.java.getDeclaredField("destination")
            field.isAccessible = true
            field.set(this, downloadPath)
        } catch (e: Exception) {
            println("Could not set download path: ${e.message}")
        }
    }

    transportManager.doRequest(request, object : RequestListener<Void> {
        override fun onRequestSuccess(result: Void?) {
            println("Torrent added to: $downloadPath")
        }

        override fun onRequestFailure(error: SpiceException?) {
            println("Failed to add torrent: ${error?.message}")
        }
    })
}
```

## Troubleshooting

### Connection Issues

#### Problem: "Failed to connect to server"
**Solutions:**
1. Verify uTorrent Web UI is enabled
2. Check firewall settings
3. Ensure correct IP address and port
4. Test Web UI access in browser first

#### Problem: "401 Unauthorized"
**Solutions:**
1. Verify username and password are correct
2. Check that Web UI authentication is enabled
3. Ensure no special characters in password causing encoding issues

#### Problem: "Token fetch failed"
**Solutions:**
1. Check that `/gui/token.html` is accessible in browser
2. Verify Basic Auth credentials
3. Check uTorrent version supports token authentication (2.0+)

### Performance Issues

#### Problem: Slow response times
**Solutions:**
1. Reduce update frequency in monitoring loops
2. Use cache ID parameter for incremental updates: `?list=1&cid=CACHE_ID`
3. Limit number of concurrent requests
4. Check network latency between device and server

#### Problem: High memory usage
**Solutions:**
1. Limit torrent list size by filtering
2. Avoid storing large torrent lists in memory
3. Use pagination if available
4. Clear old request listeners

### Data Issues

#### Problem: Torrent hash not found
**Solutions:**
1. Verify torrent exists in uTorrent
2. Refresh torrent list before operations
3. Use correct hash format (40-character hex string)
4. Check for hash/ID confusion (use hash strings, not numeric IDs)

#### Problem: Progress not updating
**Solutions:**
1. Check that torrent is actually downloading/seeding
2. Verify sufficient polling interval (recommend 2-5 seconds)
3. Check for network issues
4. Restart uTorrent if stuck

## API Reference

### Request Classes

| Class | uTorrent Action | Description |
|-------|----------------|-------------|
| `TorrentGetRequest` | `?list=1` | Get list of all torrents |
| `AddTorrentByUrlRequest` | `?action=add-url&s=URL` | Add torrent by magnet/URL |
| `AddTorrentByFileRequest` | `?action=add-file` | Upload and add .torrent file |
| `TorrentRemoveRequest` | `?action=remove&hash=HASH` | Remove torrent (keep files) |
| `TorrentRemoveRequest` (with data) | `?action=removedata&hash=HASH` | Remove torrent and files |
| `StartTorrentRequest` | `?action=start&hash=HASH` | Start/resume torrent |
| `StopTorrentRequest` | `?action=stop&hash=HASH` | Stop torrent |
| `PauseTorrentRequest` | `?action=pause&hash=HASH` | Pause torrent |
| `VerifyTorrentRequest` | `?action=recheck&hash=HASH` | Verify/recheck torrent files |
| `SessionGetRequest` | `?action=getsettings` | Get server settings |
| `SessionSetRequest` | `?action=setsetting&s=X&v=Y` | Set server setting |

### Torrent Model Properties

| Property | Type | Description |
|----------|------|-------------|
| `id` | `int` | Numeric ID (derived from hash) |
| `hash` | `String` | 40-character SHA-1 hash (primary identifier) |
| `name` | `String` | Torrent name |
| `totalSize` | `long` | Total size in bytes |
| `percentDone` | `double` | Progress (0.0 - 1.0) |
| `status` | `Status` | Current status enum |
| `downloadRate` | `int` | Download speed (bytes/s) |
| `uploadRate` | `int` | Upload speed (bytes/s) |
| `eta` | `long` | Estimated time remaining (seconds, -1 if unknown) |
| `uploadedSize` | `long` | Total uploaded bytes |
| `uploadRatio` | `double` | Upload ratio |
| `peersGettingFromUs` | `int` | Number of peers downloading from us |
| `peersSendingToUs` | `int` | Number of peers uploading to us |
| `queuePosition` | `int` | Position in download queue |
| `leftUntilDone` | `long` | Remaining bytes to download |
| `sizeWhenDone` | `long` | Total size when complete |
| `isFinished` | `boolean` | Whether download is complete |

### Status Enum Values

| Status | Value | Description |
|--------|-------|-------------|
| `STOPPED` | 0 | Torrent is stopped |
| `CHECK_WAIT` | 1 | Waiting to verify files |
| `CHECK` | 2 | Verifying files |
| `DOWNLOAD_WAIT` | 3 | Queued for download |
| `DOWNLOAD` | 4 | Downloading |
| `SEED_WAIT` | 5 | Queued for seeding |
| `SEED` | 6 | Seeding |

### uTorrent Status Bitfield

uTorrent uses bitfield status flags (combined with OR):

| Flag | Value | Description |
|------|-------|-------------|
| Started | 1 | Torrent is started |
| Checking | 2 | Checking files |
| Start after check | 4 | Will start after check completes |
| Checked | 8 | Files checked |
| Error | 16 | Error occurred |
| Paused | 32 | Paused by user |
| Queued | 64 | In queue |
| Loaded | 128 | Torrent loaded |

Example: Status `201 = 1 + 8 + 64 + 128` = Started + Checked + Queued + Loaded

## Best Practices

1. **Always use hash strings** for torrent operations instead of numeric IDs
2. **Implement proper error handling** for all requests
3. **Use reasonable polling intervals** (2-5 seconds) to avoid server overload
4. **Cache torrent data** when possible to reduce API calls
5. **Handle 401 errors** - the TokenInterceptor automatically refreshes tokens
6. **Test with mock data** before connecting to real servers
7. **Use HTTPS** in production environments for security
8. **Implement timeout handling** for network requests
9. **Respect server resources** - limit concurrent requests
10. **Monitor memory usage** when dealing with large torrent lists

## Security Considerations

1. **Store credentials securely** using Android Keystore or encrypted SharedPreferences
2. **Use HTTPS** to encrypt traffic (requires SSL setup on uTorrent server)
3. **Validate SSL certificates** in production (disable self-signed cert trust)
4. **Don't log sensitive information** (passwords, tokens)
5. **Implement request rate limiting** to prevent abuse
6. **Use strong passwords** for uTorrent Web UI
7. **Restrict Web UI access** to trusted networks when possible
8. **Keep uTorrent updated** to latest version for security patches

## Further Resources

- [uTorrent Web UI Documentation](https://forum.utorrent.com/topic/49588-webui-api/)
- [ShareConnect GitHub Repository](https://github.com/milos85vasic/ShareConnect)
- [API Implementation Guide](./API_IMPLEMENTATION.md)
- [CLAUDE.md Developer Guide](./CLAUDE.md)

## Support

For issues, questions, or contributions:
- GitHub Issues: https://github.com/milos85vasic/ShareConnect/issues
- Email: support@shareconnect.com (if applicable)
- Community Forum: (link if applicable)

---

**Last Updated:** 2025-10-10
**Version:** 1.0
**Compatible with:** uTorrent 2.0+, ShareConnect 1.0+
