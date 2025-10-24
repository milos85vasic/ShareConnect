# ShareConnector User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Features](#features)
5. [Profile Management](#profile-management)
6. [Sharing Content](#sharing-content)
7. [Security](#security)
8. [Settings](#settings)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

## Introduction

ShareConnector is the central hub of the ShareConnect ecosystem, enabling seamless content sharing between various self-hosted services and torrent clients. With ShareConnector, you can easily share URLs, files, and media across multiple platforms with just a few taps.

### Key Features
- **Universal Content Sharing**: Share URLs from any app to supported services
- **Smart Profile Management**: Create and manage profiles for different services
- **Cross-Platform Sync**: Synchronize settings and profiles across devices
- **Security-First**: Built-in PIN/biometric protection
- **Extensive Compatibility**: Supports 1800+ streaming services and file hosts

## Installation

### From Google Play Store
1. Open Google Play Store on your Android device
2. Search for "ShareConnector"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From APK (Side Loading)
1. Download the latest APK from the [GitHub Releases](https://github.com/shareconnector/shareconnector/releases)
2. Enable "Install from unknown sources" in your device settings
3. Locate the downloaded APK file
4. Tap the file and follow installation prompts
5. Grant necessary permissions when prompted

## Getting Started

### First Launch
1. Open ShareConnector after installation
2. Complete the onboarding process:
   - Grant necessary permissions
   - Set up security preferences
   - Configure basic settings
3. You'll see the main screen with options to add profiles

### Granting Permissions
ShareConnector requires the following permissions:
- **Internet Access**: For connecting to services
- **Storage Access**: For handling file downloads
- **Notification Access**: For download notifications
- **Overlay Permission**: For floating action buttons (optional)

## Features

### Content Sharing
ShareConnector supports sharing various types of content:

#### Streaming Services (1800+ sites)
- YouTube, Vimeo, Twitch, TikTok
- Facebook, Instagram, Twitter/X
- Bilibili, Nicovideo, DailyMotion
- And many more...

#### File Hosting Services
- MediaFire, Mega.nz, Google Drive
- Dropbox, OneDrive, Box, pCloud
- Rapidgator, Uploaded.net, Nitroflare

#### Torrent Services
- Magnet links and .torrent files
- Integration with qBittorrent, Transmission, uTorrent
- Automatic client detection

#### Archive Formats
- RAR, 7Z, ZIP, TAR archives
- DLC, RSDF, CCF containers (JDownloader)

### Smart URL Detection
ShareConnector automatically detects URL types and suggests the most appropriate service for handling them.

## Profile Management

### Creating a New Profile
1. Tap the "+" button on the main screen
2. Select the service type:
   - **Torrent Clients**: qBittorrent, Transmission, uTorrent
   - **Download Managers**: JDownloader
   - **Media Servers**: Plex, Jellyfin
3. Enter connection details:
   - Server address/IP
   - Port number
   - Username/password (if required)
   - SSL/TLS settings
4. Test the connection
5. Save the profile

### Managing Existing Profiles
- **Edit**: Long-press on a profile and select "Edit"
- **Delete**: Long-press and select "Delete"
- **Duplicate**: Long-press and select "Duplicate"
- **Test Connection**: Tap the profile name to test

### Profile Sync
Profiles automatically sync across devices when:
- You're logged into the same Google account
- Sync is enabled in settings
- Internet connection is available

## Sharing Content

### Basic Sharing
1. Find content you want to share (URL, file, etc.)
2. Use the Android "Share" function
3. Select "ShareConnector" from the share menu
4. Choose the destination profile
5. Confirm the action

### Advanced Sharing Options
- **Add to Queue**: Add to download queue without starting
- **Start Immediately**: Begin download right away
- **Set Priority**: High/Normal/Low priority
- **Custom Location**: Specify download directory

### Batch Operations
1. Select multiple items from your share history
2. Choose batch action:
   - Share to same service
   - Add to playlist
   - Export links

## Security

### Setting Up Security
1. Go to Settings → Security
2. Choose authentication method:
   - **PIN Code**: 4-8 digit PIN
   - **Password**: Alphanumeric password
   - **Biometric**: Fingerprint/Face recognition
3. Configure session timeout (5-60 minutes)
4. Enable optional features:
   - Auto-lock on app close
   - Failed attempt lockout
   - Intruder detection

### Using Security
- **App Launch**: Authentication required on app start
- **Background Resume**: Authentication when returning to app
- **Sensitive Actions**: Additional verification for profile changes

### Security Best Practices
- Use a strong PIN/password
- Enable biometric authentication if available
- Set appropriate session timeout
- Regularly review connected devices

## Settings

### General Settings
- **Default Profile**: Set primary service for sharing
- **Auto-start**: Launch on device boot
- **Notifications**: Configure download notifications
- **Theme**: Light/Dark/Auto theme selection

### Network Settings
- **Connection Timeout**: Adjust timeout values
- **Retry Attempts**: Set retry behavior
- **Proxy Settings**: Configure HTTP/SOCKS proxy
- **SSL Verification**: Enable/disable certificate verification

### Sync Settings
- **Profile Sync**: Enable/disable cross-device sync
- **History Sync**: Share sharing history across devices
- **Settings Sync**: Synchronize app preferences
- **Backup**: Create local backups

### Advanced Settings
- **Debug Mode**: Enable detailed logging
- **API Configuration**: Custom API endpoints
- **Performance**: Memory and CPU optimization
- **Experimental Features**: Beta functionality

## Troubleshooting

### Connection Issues
**Problem**: Cannot connect to service
**Solutions**:
1. Check network connectivity
2. Verify server address and port
3. Confirm service is running
4. Check firewall settings
5. Try different connection protocol

### Authentication Failures
**Problem**: Login credentials not accepted
**Solutions**:
1. Verify username and password
2. Check for special characters
3. Try API token instead of password
4. Reset service credentials
5. Contact service administrator

### Download Failures
**Problem**: Downloads not starting
**Solutions**:
1. Check available disk space
2. Verify download directory permissions
3. Test with different content
4. Check service status
5. Restart the service

### Performance Issues
**Problem**: App running slowly
**Solutions**:
1. Clear app cache
2. Reduce sync frequency
3. Disable unused features
4. Check available memory
5. Restart the app

## FAQ

### General Questions
**Q: Is ShareConnector free?**
A: Yes, ShareConnector is completely free and open-source.

**Q: What Android version is required?**
A: Android 5.0 (API level 21) or higher.

**Q: Does ShareConnector collect personal data?**
A: No, all data is stored locally on your device.

### Technical Questions
**Q: How many profiles can I create?**
A: There's no limit to the number of profiles.

**Q: Can I use ShareConnector without internet?**
A: Basic features work offline, but sharing requires internet.

**Q: Does ShareConnector support VPN connections?**
A: Yes, VPN connections are fully supported.

### Privacy and Security
**Q: Are my credentials secure?**
A: Yes, all credentials are encrypted using Android Keystore.

**Q: Can ShareConnector access my files?**
A: Only files in designated download directories.

**Q: Is my sharing history private?**
A: Yes, history is stored locally and optionally synced.

## Support

### Getting Help
- **Documentation**: [Online Manual](https://shareconnector.org/docs)
- **Community**: [GitHub Discussions](https://github.com/shareconnector/shareconnector/discussions)
- **Issues**: [Bug Reports](https://github.com/shareconnector/shareconnector/issues)
- **Email**: support@shareconnector.org

### Contributing
We welcome contributions! See our [Contributing Guide](https://github.com/shareconnector/shareconnector/blob/main/CONTRIBUTING.md) for details.

### Version History
See the [CHANGELOG.md](https://github.com/shareconnector/shareconnector/blob/main/CHANGELOG.md) for detailed version information.

---

**Version**: 1.0.0  
**Last Updated**: October 24, 2025  
**License**: MIT License

For the most up-to-date information, visit our [website](https://shareconnector.org).