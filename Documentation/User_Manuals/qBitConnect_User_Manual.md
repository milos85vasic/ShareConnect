# qBitConnect User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Connection Setup](#connection-setup)
5. [Torrent Management](#torrent-management)
6. [Remote Control](#remote-control)
7. [Security](#security)
8. [Settings](#settings)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

## Introduction

qBitConnect is a native Android client for qBittorrent, providing a seamless interface to manage your torrents remotely. With qBitConnect, you can add, monitor, and control torrents from anywhere using your Android device.

### Key Features
- **Remote Torrent Management**: Full control over qBittorrent server
- **Real-time Updates**: Live torrent status and progress
- **File Management**: Browse and manage downloaded files
- **Add Torrents**: Support for torrents, magnets, and links
- **Security-First**: PIN/biometric protection
- **Cross-Platform Sync**: Settings sync across devices

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "qBitConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "qBitConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnector/qbitconnect/releases)
2. Enable "Install from unknown sources"
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open qBitConnect after installation
2. Complete the onboarding process:
   - Grant storage permissions
   - Set up security preferences
   - Configure basic settings
3. Add your qBittorrent server connection

### Required Permissions
- **Internet Access**: For connecting to qBittorrent
- **Network State**: To check connectivity
- **Storage Access**: For file management
- **Notification Access**: For torrent notifications

## Connection Setup

### Adding qBittorrent Server
1. Tap the "+" button on the main screen
2. Enter server details:
   - **Name**: Descriptive name for the server
   - **Host/IP**: qBittorrent server address
   - **Port**: Usually 8080 (default)
   - **Username**: qBittorrent username
   - **Password**: qBittorrent password
   - **Path**: Web UI path (usually "/")
3. Configure advanced options:
   - **SSL/TLS**: Enable for HTTPS connections
   - **Trust Certificate**: For self-signed certificates
   - **Connection Timeout**: Adjust for slow networks
4. Test the connection
5. Save the profile

### qBittorrent Server Setup
Before using qBitConnect, ensure your qBittorrent server is configured:

#### Enable Web UI
1. Open qBittorrent on your computer
2. Go to Tools → Options → Web UI
3. Check "Enable Web User Interface"
4. Set port (default: 8080)
5. Set username and password
6. Click "Apply"

#### Network Configuration
1. In qBittorrent Options → Advanced
2. Set "Web UI address" to "0.0.0.0" (all interfaces)
3. Configure firewall to allow the port
4. Test local access first: `http://localhost:8080`

#### Security Recommendations
- Use a strong password
- Enable HTTPS if possible
- Consider VPN for remote access
- Keep qBittorrent updated

### Connection Types
qBitConnect supports various connection scenarios:

#### Local Network
- Use local IP address (192.168.x.x, 10.x.x.x)
- Fastest connection type
- No internet required for server access

#### Remote Access
- Use public IP or domain name
- Requires port forwarding on router
- Consider security implications

#### VPN/Tunnel
- Use VPN client IP addresses
- Secure remote access
- May affect performance

## Torrent Management

### Adding Torrents
qBitConnect supports multiple ways to add torrents:

#### From Share Menu
1. Find a torrent file or magnet link
2. Use Android "Share" function
3. Select "qBitConnect"
4. Configure download options:
   - **Save Path**: Download directory
   - **Category**: Organize by type
   - **Priority**: High/Normal/Low
   - **Start Torrent**: Begin immediately or paused

#### From Within App
1. Tap the "+" button
2. Choose input method:
   - **URL**: Paste magnet or HTTP link
   - **File**: Select .torrent file
   - **Search**: Built-in torrent search
3. Configure options and add

#### Batch Operations
1. Select multiple torrents from list
2. Choose batch action:
   - Start all
   - Pause all
   - Delete all
   - Change category

### Monitoring Torrents
The main screen shows all torrents with real-time information:

#### Torrent Information
- **Name**: Torrent title
- **Size**: Total and downloaded size
- **Progress**: Percentage and progress bar
- **Speed**: Download/upload rates
- **ETA**: Estimated completion time
- **Status**: Downloading, seeding, paused, etc.
- **Peers**: Connected seeders/leechers

#### Status Indicators
- **Green**: Active downloading
- **Blue**: Completed/seeding
- **Yellow**: Paused
- **Red**: Error or stopped

### File Management
Access and manage downloaded files:

#### Browse Files
1. Tap on a completed torrent
2. Select "Files" tab
3. Browse directory structure
4. Preview supported file types

#### File Operations
- **Open**: Open file with appropriate app
- **Share**: Share file to other apps
- **Delete**: Remove file from device
- **Rename**: Change file name
- **Move**: Change file location

#### Download to Device
1. Select files from torrent
2. Tap "Download" button
3. Choose local storage location
4. Monitor download progress

## Remote Control

### Torrent Control
Control torrents remotely with full functionality:

#### Basic Controls
- **Start/Resume**: Begin downloading
- **Pause**: Temporarily stop
- **Stop**: Completely stop torrent
- **Delete**: Remove torrent and files
- **Force Recheck**: Verify downloaded data

#### Advanced Controls
- **Set Priority**: Adjust file priorities
- **Sequential Download**: Download in order
- **Bandwidth Limits**: Set speed limits
- **Trackers**: Add/edit tracker URLs

### Queue Management
Organize your download queue:

#### Queue Settings
1. Go to Settings → Queue
2. Configure:
   - **Max Active Downloads**: Simultaneous torrents
   - **Max Active Uploads**: Simultaneous uploads
   - **Download Rate**: Global speed limit
   - **Upload Rate**: Global upload limit

#### Priority System
- **High Priority**: Downloads first
- **Normal Priority**: Standard queue order
- **Low Priority**: Downloads last
- **Don't Download**: Excluded from download

## Security

### Setting Up Security
1. Go to Settings → Security
2. Choose authentication method:
   - **PIN Code**: 4-8 digit PIN
   - **Password**: Alphanumeric password
   - **Biometric**: Fingerprint/Face ID
3. Configure session settings:
   - **Session Timeout**: Auto-lock duration
   - **Failed Attempts**: Lockout after X attempts
   - **Lock on Background**: Secure when app minimized

### Security Features
- **App Launch Protection**: Authentication on startup
- **Session Management**: Automatic timeout
- **Failed Attempt Lockout**: Prevents brute force
- **Biometric Support**: Quick secure access
- **Encrypted Storage**: Credentials encrypted locally

### Best Practices
- Use a unique PIN/password
- Enable biometric authentication
- Set appropriate session timeout
- Regularly update qBittorrent
- Use HTTPS for remote connections

## Settings

### Connection Settings
- **Server Profiles**: Manage multiple qBittorrent servers
- **Connection Timeout**: Adjust for network conditions
- **Retry Attempts**: Number of connection retries
- **SSL Verification**: Certificate validation options

### Interface Settings
- **Theme**: Light/Dark/Auto
- **Language**: App language selection
- **Refresh Rate**: Update frequency
- **Sort Order**: Torrent list organization

### Download Settings
- **Default Save Path**: Where torrents are saved
- **Category Management**: Organize downloads
- **Auto-Start**: Begin downloads immediately
- **Completion Action**: What to do when finished

### Notification Settings
- **Download Notifications**: Progress alerts
- **Completion Alerts**: Finished torrent notices
- **Error Notifications**: Problem alerts
- **Sound/Vibration**: Alert preferences

## Troubleshooting

### Connection Issues
**Problem**: Cannot connect to qBittorrent
**Solutions**:
1. Verify qBittorrent is running
2. Check IP address and port
3. Test with web browser
4. Check firewall settings
5. Verify username/password

**Problem**: Connection times out
**Solutions**:
1. Increase connection timeout
2. Check network stability
3. Try different network
4. Restart qBittorrent
5. Check server load

### Torrent Issues
**Problem**: Torrents not downloading
**Solutions**:
1. Check tracker status
2. Verify internet connectivity
3. Add more trackers
4. Check disk space
5. Restart torrent

**Problem**: Slow download speeds
**Solutions**:
1. Check number of seeders
2. Adjust bandwidth limits
3. Check network speed
4. Try different torrents
5. Configure port forwarding

### App Issues
**Problem**: App crashes or freezes
**Solutions**:
1. Clear app cache
2. Restart the app
3. Check available storage
4. Update to latest version
5. Reinstall the app

## FAQ

### General Questions
**Q: Is qBitConnect free?**
A: Yes, qBitConnect is completely free and open-source.

**Q: Does it work with all qBittorrent versions?**
A: Works with qBittorrent 4.1.0 and later.

**Q: Can I manage multiple servers?**
A: Yes, you can add unlimited server profiles.

### Technical Questions
**Q: What ports does qBittorrent use?**
A: Default Web UI port is 8080, torrent ports are configurable.

**Q: Does qBitConnect work without internet?**
A: Only if your qBittorrent server is on the same network.

**Q: Can I use qBitConnect with a VPN?**
A: Yes, VPN connections are fully supported.

### Security Questions
**Q: Are my credentials stored securely?**
A: Yes, all credentials are encrypted using Android Keystore.

**Q: Can someone access my torrents if they have my phone?**
A: Only if they can bypass the security authentication.

## Support

### Getting Help
- **Documentation**: [Online Manual](https://shareconnector.org/docs/qbitconnect)
- **Community**: [GitHub Discussions](https://github.com/shareconnector/qbitconnect/discussions)
- **Issues**: [Bug Reports](https://github.com/shareconnector/qbitconnect/issues)
- **qBittorrent**: [Official Forums](https://forum.qbittorrent.org/)

### Contributing
We welcome contributions! See our [Contributing Guide](https://github.com/shareconnector/qbitconnect/blob/main/CONTRIBUTING.md).

### Related Projects
- **ShareConnector**: Main sharing hub
- **TransmissionConnect**: For Transmission users
- **uTorrentConnect**: For uTorrent users

---

**Version**: 1.0.0  
**Last Updated**: October 24, 2025  
**License**: MIT License

For the most up-to-date information, visit our [website](https://shareconnector.org).