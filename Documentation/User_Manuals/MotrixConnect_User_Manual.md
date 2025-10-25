# MotrixConnect User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Server Connection](#server-connection)
5. [Download Management](#download-management)
6. [Advanced Features](#advanced-features)
7. [Settings](#settings)
8. [Troubleshooting](#troubleshooting)
9. [FAQ](#faq)

## Introduction

MotrixConnect is a native Android client for Motrix download manager, providing powerful download capabilities with multi-connection support, torrent management, and advanced scheduling. Control your Motrix server remotely from your Android device.

### Key Features
- **Multi-Connection Downloads**: Up to 16 connections per file
- **Protocol Support**: HTTP, HTTPS, FTP, BitTorrent, Magnet links
- **Download Queuing**: Manage multiple downloads efficiently
- **Bandwidth Control**: Limit download and upload speeds
- **Torrent Support**: Full BitTorrent protocol support
- **Remote Control**: Manage downloads from anywhere
- **ShareConnect Integration**: Add downloads from other apps

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "MotrixConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "MotrixConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnect/motrixconnect/releases)
2. Enable "Install from unknown sources" in Settings
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open MotrixConnect after installation
2. Complete the onboarding process:
   - Introduction to features
   - Server setup instructions
   - Permission requests
3. Add your Motrix server

### Required Permissions
- **Internet Access**: For connecting to Motrix server
- **Network State**: To check connectivity
- **Storage Access**: For managing downloads
- **Notification Access**: For download progress notifications

## Server Connection

### Motrix Server Setup

Before using MotrixConnect, ensure Motrix is configured:

**On Your Computer:**
1. Open Motrix application
2. Go to Preferences → Advanced
3. Enable "RPC Listen to All IP"
4. Note the RPC Port (default: 6800)
5. Set RPC Secret for security (recommended)
6. Apply settings

**Finding Your Server Address:**
- **Local Network**: Use local IP (e.g., 192.168.1.100)
- **Windows**: Run `ipconfig` in Command Prompt
- **Mac/Linux**: Run `ifconfig` in Terminal
- **Router**: Check connected devices

### Adding Motrix Server

**Quick Setup:**
1. Tap "Add Server"
2. Enter server details:
   - **Server URL**: `http://192.168.1.100:6800`
   - **RPC Secret**: Your security token (if set)
3. Tap "Test Connection"
4. If successful, tap "Save"

**Server Configuration:**
- **Server Name**: Friendly name for identification
- **Host**: IP address or hostname
- **Port**: RPC port (default: 6800)
- **RPC Secret**: Security token (optional but recommended)
- **Use HTTPS**: For encrypted connection
- **Auto-Connect**: Connect automatically on app launch

### Connection Types

**Local Network:**
- Fastest connection
- No internet required
- Use local IP address
- Example: `http://192.168.1.100:6800`

**Remote Access:**
- Requires port forwarding
- Use public IP or domain
- Security risk without RPC secret
- Example: `http://your-ip:6800`

**VPN/Tunnel:**
- Most secure for remote access
- Use VPN server IP
- Slower than local
- Example: `http://10.8.0.1:6800`

### Security Setup

**Setting RPC Secret:**
1. In Motrix: Preferences → Advanced → RPC Secret
2. Enter a strong random string
3. Save and restart Motrix
4. Use same secret in MotrixConnect

**Best Practices:**
- Always use RPC secret
- Use HTTPS when possible
- Don't expose port to internet without security
- Use VPN for remote access
- Keep Motrix updated

## Download Management

### Adding Downloads

**From URL:**
1. Tap "+" button
2. Select "Add URL"
3. Paste or enter URL
4. Configure options (optional):
   - Download directory
   - File name
   - Connections per server
   - Speed limits
5. Tap "Add"

**From Share Menu:**
1. Find link in browser or app
2. Long-press and select "Share"
3. Choose "MotrixConnect"
4. Configure options
5. Confirm addition

**From Clipboard:**
1. Copy download URL
2. Open MotrixConnect
3. App detects clipboard URL
4. Tap "Add from Clipboard"
5. Configure and add

**Batch URLs:**
1. Tap "+" → "Add URLs"
2. Enter or paste multiple URLs (one per line)
3. All URLs added to queue
4. Downloads start automatically

### Download Options

**Basic Settings:**
- **Directory**: Where to save file
- **File Name**: Custom filename (optional)
- **Start Immediately**: Begin download or queue

**Advanced Settings:**
- **Connections**: Number of connections (1-16)
- **Max Download Speed**: Per-file speed limit
- **Max Upload Speed**: For torrents
- **Headers**: Custom HTTP headers
- **Referer**: Set HTTP referer
- **User Agent**: Custom user agent string

### Monitoring Downloads

**Active Downloads:**
- Real-time speed display
- Progress bar and percentage
- ETA (estimated time remaining)
- Connected servers count
- Downloaded/total size

**Download States:**
- **Active**: Currently downloading
- **Waiting**: Queued for download
- **Paused**: Manually paused
- **Complete**: Successfully finished
- **Error**: Failed with error message
- **Removed**: Cancelled by user

**Download Details:**
1. Tap on any download
2. View detailed information:
   - File list (for multi-file downloads)
   - URIs used
   - Peers (for torrents)
   - Download log
   - Options

### Controlling Downloads

**Pause Download:**
1. Tap download
2. Select "Pause"
3. Resume anytime

**Resume Download:**
1. Tap paused download
2. Select "Resume"
3. Download continues

**Cancel Download:**
1. Long-press download
2. Select "Remove"
3. Choose to keep or delete partial file
4. Confirm

**Pause All:**
- Tap menu → "Pause All"
- All active downloads pause
- Resume individually or all at once

**Priority Management:**
1. Long-press download
2. Select "Priority"
3. Choose: High, Normal, Low
4. Higher priority downloads start first

### Queue Management

**Managing Queue:**
- View all waiting downloads
- Reorder by drag and drop
- Move downloads to top/bottom
- Set maximum concurrent downloads

**Global Settings:**
1. Go to Settings → Downloads
2. Set max concurrent downloads
3. Set max connections per server
4. Configure global speed limits

## Advanced Features

### Torrent Support

**Adding Torrents:**

**From Torrent File:**
1. Tap "+" → "Add Torrent"
2. Browse and select .torrent file
3. Review file list
4. Select files to download
5. Start download

**From Magnet Link:**
1. Copy magnet link
2. MotrixConnect detects automatically
3. Tap "Add Magnet"
4. Select files
5. Start download

**Torrent Options:**
- **Seed Ratio**: Auto-stop after ratio reached
- **Seed Time**: Auto-stop after time elapsed
- **Upload Limit**: Bandwidth for seeding
- **Peer Connections**: Max peer count
- **DHT**: Enable/disable DHT

**Viewing Peers:**
1. Tap torrent download
2. Select "Peers"
3. View connected peers:
   - IP address
   - Client type
   - Download/upload speed
   - Completion percentage

### Bandwidth Control

**Global Limits:**
1. Go to Settings → Bandwidth
2. Set "Max Download Speed"
3. Set "Max Upload Speed"
4. Apply to all downloads

**Per-Download Limits:**
1. Tap download → Options
2. Set custom limits
3. Override global settings

**Speed Scheduling:**
1. Go to Settings → Scheduler
2. Add time-based rules:
   - Weekdays: Normal speed
   - Nights: Full speed
   - Weekends: Limited speed
3. Enable scheduler

### Multiple File Downloads

**Metalink Support:**
1. Obtain metalink file (.meta4 or .metalink)
2. Add via "Add Torrent" option
3. Select files from metalink
4. Download with multiple mirrors

**Mirror Handling:**
- Automatically uses multiple mirrors
- Switches if mirror fails
- Optimizes for fastest source
- Shows active mirrors in details

### Download History

**Completed Downloads:**
1. Go to "Completed" tab
2. View finished downloads
3. Options:
   - Open file location
   - Re-download
   - Clear from history
   - Share download

**Failed Downloads:**
1. Go to "Failed" tab
2. View error details
3. Retry download
4. Remove from list

**Clearing History:**
1. Settings → Downloads → History
2. "Clear Completed"
3. "Clear Failed"
4. "Clear All"

## Settings

### App Settings

**Appearance:**
- Theme: Light, Dark, Auto
- Language: Select app language
- Compact View: Dense download list
- Show Speed Graph: Visual speed indicator

**Notifications:**
- Download Started: Notify on add
- Download Complete: Notify on finish
- Download Failed: Notify on error
- Progress Updates: Show ongoing progress
- Sound: Enable notification sound

**Downloads:**
- Default Directory: Save location
- Max Concurrent: Simultaneous downloads (default: 5)
- Max Connections: Per server (default: 16)
- Auto-Clear Completed: After X hours
- Continue on Cellular: Allow mobile data

**Bandwidth:**
- Global Download Limit: Maximum speed
- Global Upload Limit: For torrents
- Dynamic Allocation: Distribute bandwidth
- Cellular Limit: Different limit on mobile

### Server Settings

1. Go to Settings → Servers
2. Select server
3. Configure:
   - Connection timeout
   - Retry attempts
   - RPC secret
   - Auto-connect

### Advanced Settings

**File Allocation:**
- Pre-allocate: Reserve disk space (faster)
- Falloc: Linux file allocation
- None: Allocate as downloaded (slower)

**Disk Cache:**
- Cache Size: RAM used for buffering
- Larger cache = better performance
- Default: 16 MB

**Network:**
- IPv6: Enable IPv6 support
- Proxy: Configure HTTP/SOCKS proxy
- DNS Servers: Custom DNS

## Troubleshooting

### Connection Issues

**Cannot Connect to Server:**
1. Verify server is running
2. Check IP address is correct
3. Ensure port is accessible
4. Test with `curl http://IP:6800/jsonrpc`
5. Check firewall settings
6. Verify RPC secret

**Connection Timeout:**
1. Increase timeout in settings
2. Check network stability
3. Try ping server from terminal
4. Verify Motrix is responding

**RPC Secret Errors:**
1. Verify secret matches Motrix config
2. Check for typos or spaces
3. Try without secret first
4. Restart Motrix after changing secret

### Download Problems

**Download Won't Start:**
1. Check internet connection
2. Verify URL is valid
3. Check disk space
4. Look at error message
5. Try different server/mirror

**Slow Download Speed:**
1. Increase connections per server
2. Remove global speed limits
3. Check other network usage
4. Try different time of day
5. Verify server isn't limiting speed

**Download Keeps Failing:**
1. Check error message
2. Verify file still exists on server
3. Try fewer connections
4. Check disk permissions
5. Update Motrix to latest version

**Torrent Issues:**
1. Check DHT is enabled
2. Verify port forwarding
3. Look for seeders/peers
4. Try different tracker
5. Check firewall settings

### App Issues

**Crashes:**
1. Update to latest version
2. Clear app cache
3. Clear app data
4. Reinstall app
5. Report with crash log

**Slow Performance:**
1. Reduce max concurrent downloads
2. Clear completed downloads
3. Reduce connection count
4. Close other apps
5. Restart device

## FAQ

### General Questions

**Q: What is Motrix?**
A: Motrix is a full-featured download manager built on Aria2, supporting HTTP, FTP, BitTorrent, and Magnet links.

**Q: Do I need Motrix installed?**
A: Yes, MotrixConnect is a remote control app for Motrix. You need Motrix running on a computer.

**Q: Is MotrixConnect free?**
A: Yes, both MotrixConnect and Motrix are free and open-source.

**Q: Can I use this with Aria2 instead of Motrix?**
A: Yes, MotrixConnect works with any Aria2 RPC server.

### Connection

**Q: How do I find my server IP?**
A: On Windows: `ipconfig`, on Mac/Linux: `ifconfig`. Use the local IP address (192.168.x.x).

**Q: What is RPC secret?**
A: A password for securing the Aria2/Motrix RPC interface. Set it in Motrix settings.

**Q: Can I connect over the internet?**
A: Yes, but you need port forwarding and should use RPC secret for security.

**Q: What if I have multiple Motrix servers?**
A: MotrixConnect supports multiple server profiles. Switch between them in settings.

### Downloads

**Q: What's the maximum download speed?**
A: Limited only by your internet connection and server bandwidth.

**Q: How many connections should I use?**
A: 16 connections work well for most servers. Some servers limit connections.

**Q: Can I schedule downloads?**
A: Yes, use the bandwidth scheduler to set time-based rules.

**Q: What file types are supported?**
A: All file types. Motrix handles the actual downloading.

**Q: How do I pause all downloads at once?**
A: Tap menu → "Pause All" or use the pause all button.

### Torrents

**Q: Does this support torrents?**
A: Yes, full BitTorrent and Magnet link support.

**Q: Can I seed torrents?**
A: Yes, configure seeding ratio and time in settings.

**Q: Why is my torrent slow?**
A: Check number of seeders, enable DHT, verify port forwarding.

**Q: Can I select specific files from a torrent?**
A: Yes, after adding torrent, you can choose which files to download.

### Storage & Performance

**Q: Where are files downloaded?**
A: Files are saved on the computer running Motrix, not on your Android device.

**Q: How much RAM does MotrixConnect use?**
A: Minimal. MotrixConnect is just a remote control interface.

**Q: Can I download while phone is locked?**
A: Yes, downloads continue on the Motrix server regardless of phone state.

### Technical

**Q: What Android version is required?**
A: Android 9.0 (API 28) or higher.

**Q: What's the difference between Motrix and Aria2?**
A: Motrix is a user-friendly GUI application built on top of Aria2 engine.

**Q: Can I use this on tablet?**
A: Yes, MotrixConnect works on phones and tablets.

**Q: Does this work offline?**
A: You need network access to the Motrix server, but not necessarily internet.

### Troubleshooting

**Q: Why can't I connect to my server?**
A: Check IP address, port, firewall, and verify Motrix is running.

**Q: Downloads fail immediately. Why?**
A: Check disk space, permissions, and URL validity.

**Q: App says "RPC error". What does this mean?**
A: Communication issue with Motrix. Check connection and Motrix status.

### Getting Help

**Q: Where can I report bugs?**
A: GitHub: github.com/shareconnect/motrixconnect/issues

**Q: Is there a support forum?**
A: Yes, ShareConnect community forum at shareconnect.org/forum

**Q: How do I check app version?**
A: Settings → About → Version

## Additional Resources

### Official Links
- **Website**: [shareconnect.org/motrixconnect](https://shareconnect.org/motrixconnect)
- **GitHub**: [github.com/shareconnect/motrixconnect](https://github.com/shareconnect/motrixconnect)
- **Documentation**: [docs.shareconnect.org/motrixconnect](https://docs.shareconnect.org/motrixconnect)

### Motrix & Aria2 Resources
- **Motrix**: [motrix.app](https://motrix.app)
- **Motrix GitHub**: [github.com/agalwood/Motrix](https://github.com/agalwood/Motrix)
- **Aria2**: [aria2.github.io](https://aria2.github.io)
- **Aria2 Manual**: [aria2.github.io/manual](https://aria2.github.io/manual)

### Community
- **Discord**: Join the ShareConnect Discord server
- **Reddit**: r/ShareConnect, r/Motrix
- **Twitter**: @ShareConnectApp

---

**Version**: 1.0.0
**Last Updated**: 2025-10-25
**License**: Open Source (GPL-3.0)

For the latest updates and detailed documentation, visit [shareconnect.org/docs](https://shareconnect.org/docs)
