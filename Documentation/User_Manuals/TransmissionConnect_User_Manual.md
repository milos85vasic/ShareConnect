# TransmissionConnect User Manual

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

TransmissionConnect is a native Android client for Transmission torrent daemon, providing comprehensive remote management capabilities. With TransmissionConnect, you can control your Transmission server from anywhere using your Android device.

### Key Features
- **Remote Torrent Management**: Full control over Transmission daemon
- **Real-time Updates**: Live torrent status and statistics
- **File Management**: Browse and manage downloaded files
- **Multiple Torrent Sources**: Support for torrents, magnets, and links
- **Security-First**: PIN/biometric protection
- **Cross-Platform Sync**: Settings synchronization

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "TransmissionConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "TransmissionConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnector/transmissionconnect/releases)
2. Enable "Install from unknown sources"
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open TransmissionConnect after installation
2. Complete the onboarding process:
   - Grant storage permissions
   - Set up security preferences
   - Configure basic settings
3. Add your Transmission server connection

### Required Permissions
- **Internet Access**: For connecting to Transmission
- **Network State**: To monitor connectivity
- **Storage Access**: For file operations
- **Notification Access**: For torrent alerts

## Connection Setup

### Adding Transmission Server
1. Tap the "+" button on the main screen
2. Enter server details:
   - **Name**: Descriptive server name
   - **Host/IP**: Transmission server address
   - **Port**: Usually 9091 (default)
   - **Username**: Transmission username
   - **Password**: Transmission password
   - **RPC Path**: Usually "/transmission/rpc"
3. Configure advanced options:
   - **SSL/TLS**: Enable for HTTPS connections
   - **Trust Certificate**: For self-signed certificates
   - **Connection Timeout**: Adjust for network conditions
4. Test the connection
5. Save the profile

### Transmission Server Setup
Before using TransmissionConnect, ensure your Transmission daemon is configured:

#### Enable Remote Access
1. Open Transmission settings file (`settings.json`)
2. Set `"rpc-enabled": true`
3. Set `"rpc-bind-address": "0.0.0.0"`
4. Set `"rpc-port": 9091` (or preferred port)
5. Set `"rpc-username": "your_username"`
6. Set `"rpc-password": "your_password"`
7. Restart Transmission daemon

#### Network Configuration
1. Configure firewall to allow RPC port
2. Test local access: `http://localhost:9091`
3. For remote access, configure port forwarding
4. Consider VPN for secure remote access

#### Security Recommendations
- Use a strong password
- Enable SSL if possible
- Limit access to trusted IPs
- Keep Transmission updated

### Connection Types
TransmissionConnect supports various connection scenarios:

#### Local Network
- Use local IP address (192.168.x.x, 10.x.x.x)
- Fastest and most reliable connection
- No internet required for server access

#### Remote Access
- Use public IP or domain name
- Requires port forwarding on router
- Additional security considerations

#### VPN/Tunnel Access
- Use VPN client IP addresses
- Secure remote connection method
- May affect performance

## Torrent Management

### Adding Torrents
TransmissionConnect supports multiple methods to add torrents:

#### From Share Menu
1. Find a torrent file or magnet link
2. Use Android "Share" function
3. Select "TransmissionConnect"
4. Configure download options:
   - **Download Directory**: Where to save files
   - **Priority**: High/Normal/Low
   - **Start Paused**: Add without starting
   - **Bandwidth Priority**: High/Normal/Low

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
   - Start all selected
   - Pause all selected
   - Remove all selected
   - Change priority for all

### Monitoring Torrents
The main screen displays all torrents with comprehensive information:

#### Torrent Information
- **Name**: Torrent title
- **Size**: Total and downloaded size
- **Progress**: Percentage and visual progress bar
- **Speed**: Download/upload rates
- **ETA**: Estimated completion time
- **Status**: Downloading, seeding, paused, error
- **Peers**: Connected seeders and leechers
- **Ratio**: Upload/download ratio

#### Status Indicators
- **Green**: Active downloading
- **Blue**: Completed and seeding
- **Yellow**: Paused or waiting
- **Red**: Error or stopped
- **Gray**: Stopped by user

### File Management
Access and manage downloaded files efficiently:

#### Browse Files
1. Tap on any torrent
2. Select "Files" tab
3. Navigate directory structure
4. Preview supported file types

#### File Operations
- **Download**: Save file to device storage
- **Open**: Open with appropriate application
- **Share**: Share file to other apps
- **Delete**: Remove file from server
- **Rename**: Change file name
- **Move**: Change file location

#### Selective Downloading
1. Select torrent from list
2. Go to "Files" tab
3. Uncheck files you don't want
4. Apply changes
5. Transmission will download only selected files

## Remote Control

### Torrent Control
Full remote control capabilities for all torrent operations:

#### Basic Controls
- **Start/Resume**: Begin downloading
- **Pause**: Temporarily stop download
- **Stop**: Completely stop torrent
- **Remove**: Delete torrent (keep files)
- **Delete**: Remove torrent and files
- **Verify**: Check downloaded data integrity

#### Advanced Controls
- **Set Location**: Change download directory
- **Bandwidth Limits**: Set individual speed limits
- **Queue Position**: Change download order
- **Tracker Management**: Add/edit/remove trackers
- **Peer Management**: View and manage connected peers

### Queue Management
Optimize your download queue:

#### Queue Settings
1. Go to Settings → Queue
2. Configure:
   - **Download Queue Size**: Max simultaneous downloads
   - **Seed Queue Size**: Max simultaneous seeds
   - **Idle Seeding Limit**: Stop seeding after X minutes
   - **Ratio Limit**: Stop at upload ratio

#### Priority System
- **High Priority**: Downloads first in queue
- **Normal Priority**: Standard queue position
- **Low Priority**: Downloads last
- **Bandwidth Priority**: Gets more bandwidth

### Statistics and Monitoring
Track your torrent activity:

#### Session Statistics
- **Total Downloaded**: Session download total
- **Total Uploaded**: Session upload total
- **Active Torrents**: Currently downloading
- **Total Torrents**: All torrents in list

#### Individual Statistics
- **Download Speed**: Current download rate
- **Upload Speed**: Current upload rate
- **Connected Peers**: Number of connections
- **Seeders/Peers**: Swarm information
- **Availability**: Complete copies in swarm

## Security

### Setting Up Security
1. Go to Settings → Security
2. Choose authentication method:
   - **PIN Code**: 4-8 digit PIN
   - **Password**: Alphanumeric password
   - **Biometric**: Fingerprint/Face recognition
3. Configure security options:
   - **Session Timeout**: Auto-lock duration
   - **Failed Attempts**: Lockout after X attempts
   - **Background Lock**: Secure when app minimized

### Security Features
- **App Authentication**: Required on app launch
- **Session Management**: Automatic timeout protection
- **Failed Attempt Protection**: Prevents brute force attacks
- **Biometric Support**: Quick and secure access
- **Encrypted Credentials**: Secure local storage

### Best Practices
- Use a strong, unique PIN/password
- Enable biometric authentication when available
- Set appropriate session timeout
- Keep Transmission daemon updated
- Use HTTPS for remote connections
- Regularly review access logs

## Settings

### Connection Settings
- **Server Profiles**: Manage multiple Transmission servers
- **Connection Timeout**: Adjust for network conditions
- **Retry Logic**: Number of connection retries
- **SSL Verification**: Certificate validation options
- **Keep Alive**: Maintain connection persistence

### Interface Settings
- **Theme Selection**: Light/Dark/Auto theme
- **Language**: App language preference
- **Refresh Interval**: Update frequency
- **Sort Options**: Torrent list organization
- **Compact View**: Show more torrents on screen

### Download Settings
- **Default Directory**: Where new torrents are saved
- **Incomplete Directory**: Location for partial downloads
- **Auto-Start**: Begin downloads immediately
- **Completion Action**: What to do when finished
- **Free Space Check**: Warn before downloading

### Bandwidth Settings
- **Global Limits**: Overall speed restrictions
- **Alternative Limits**: Time-based restrictions
- **Scheduled Limits**: Different limits for different times
- **Peer Limits**: Maximum connected peers

### Notification Settings
- **Progress Notifications**: Real-time download updates
- **Completion Alerts**: Finished torrent notifications
- **Error Notifications**: Problem alerts
- **Quiet Hours**: Disable notifications during certain times
- **Sound/Vibration**: Alert customization

## Troubleshooting

### Connection Issues
**Problem**: Cannot connect to Transmission
**Solutions**:
1. Verify Transmission daemon is running
2. Check IP address and port number
3. Test with web browser: `http://server:9091`
4. Verify username and password
5. Check firewall settings
6. Confirm RPC is enabled in settings

**Problem**: Connection refused
**Solutions**:
1. Check if Transmission is listening on correct port
2. Verify RPC is enabled in settings.json
3. Check for conflicting services
4. Restart Transmission daemon
5. Check system logs for errors

### Torrent Issues
**Problem**: Torrents not downloading
**Solutions**:
1. Check tracker status and connectivity
2. Verify internet connection
3. Add additional trackers
4. Check available disk space
5. Verify port forwarding for incoming connections
6. Restart individual torrents

**Problem**: Slow download speeds
**Solutions**:
1. Check number of connected peers
2. Adjust bandwidth limits
3. Test internet connection speed
4. Try different torrents with more seeders
5. Configure port forwarding
6. Check for ISP throttling

### App Issues
**Problem**: App crashes or freezes
**Solutions**:
1. Clear app cache and data
2. Restart the application
3. Check available device storage
4. Update to latest app version
5. Reinstall the application
6. Check Android system updates

### Performance Issues
**Problem**: App running slowly
**Solutions**:
1. Reduce refresh interval
2. Limit number of displayed torrents
3. Disable animations in settings
4. Close other running applications
5. Restart device
6. Check available memory

## FAQ

### General Questions
**Q: Is TransmissionConnect free?**
A: Yes, TransmissionConnect is completely free and open-source.

**Q: What Transmission versions are supported?**
A: Works with Transmission 2.80 and later.

**Q: Can I connect to multiple Transmission servers?**
A: Yes, you can add unlimited server profiles.

### Technical Questions
**Q: What is the default RPC port?**
A: The default RPC port is 9091.

**Q: Does TransmissionConnect work without internet?**
A: Only if your Transmission server is on the same local network.

**Q: Can I use TransmissionConnect with a VPN?**
A: Yes, VPN connections are fully supported.

### Configuration Questions
**Q: How do I enable SSL?**
A: Configure SSL in Transmission and enable it in the app connection settings.

**Q: What is the RPC path?**
A: Default is "/transmission/rpc", but can be customized in Transmission.

### Security Questions
**Q: Are my login credentials secure?**
A: Yes, all credentials are encrypted using Android Keystore system.

**Q: Can someone access my torrents if they steal my phone?**
A: Only if they can bypass the security authentication (PIN/biometrics).

## Support

### Getting Help
- **Documentation**: [Online Manual](https://shareconnector.org/docs/transmissionconnect)
- **Community**: [GitHub Discussions](https://github.com/shareconnector/transmissionconnect/discussions)
- **Issues**: [Bug Reports](https://github.com/shareconnector/transmissionconnect/issues)
- **Transmission**: [Official Website](https://transmissionbt.com/)

### Contributing
We welcome contributions! See our [Contributing Guide](https://github.com/shareconnector/transmissionconnect/blob/main/CONTRIBUTING.md).

### Related Projects
- **ShareConnector**: Main sharing hub application
- **qBitConnect**: For qBittorrent users
- **uTorrentConnect**: For uTorrent users

---

**Version**: 1.0.0  
**Last Updated**: October 24, 2025  
**License**: MIT License

For the most up-to-date information, visit our [website](https://shareconnector.org).