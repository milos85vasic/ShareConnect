# PlexConnect User Guide

## Welcome to PlexConnect

PlexConnect brings your Plex Media Server to your Android device with a native, modern interface designed for mobile media consumption.

## Table of Contents

1. [Getting Started](#getting-started)
2. [First-Time Setup](#first-time-setup)
3. [Navigating the App](#navigating-the-app)
4. [Media Playback](#media-playback)
5. [Settings & Configuration](#settings--configuration)
6. [Troubleshooting](#troubleshooting)

## Getting Started

### Requirements

Before using PlexConnect, ensure you have:

- **Android Device**: Android 7.0 (API 24) or higher
- **Plex Media Server**: Running and accessible on your network
- **Plex Account**: Valid Plex.tv account for authentication
- **Network Access**: Your device can reach your Plex server

### Installation

#### From Google Play Store (Future)
PlexConnect will be available on Google Play Store once released.

#### From APK File
1. Download the APK file from releases
2. Enable "Install from Unknown Sources" in Android settings
3. Install the APK file
4. Launch PlexConnect

#### From Source Code
```bash
git clone https://github.com/your-org/ShareConnect.git
cd ShareConnect
./gradlew :PlexConnector:installDebug
```

## First-Time Setup

### Onboarding Process

When you first launch PlexConnect, you'll go through a 3-step onboarding process:

#### Step 1: Welcome
- Introduction to PlexConnect features
- Overview of capabilities
- Skip option available

#### Step 2: Server Setup
- Instructions for preparing your Plex server
- Network requirements
- Server accessibility checklist

#### Step 3: Feature Overview
- Preview of available features
- Getting started tips
- Ready to begin using the app

### Authentication

PlexConnect uses Plex.tv authentication for security:

1. **Launch Authentication**: Tap "Sign In" in the authentication screen
2. **Visit plex.tv/pin**: Your device will open a browser or provide a PIN code
3. **Enter PIN**: On plex.tv/pin, enter the provided PIN code
4. **Automatic Detection**: PlexConnect will detect successful authentication

**Note**: Keep the app open during authentication - it monitors for completion.

## Navigating the App

### Main Navigation

PlexConnect uses a bottom navigation bar with four main sections:

#### 🏠 Home
- **Recently Added**: New content added to your server
- **On Deck**: Continue watching in-progress content
- **Continue Watching**: Resume playback from where you left off

#### 🎬 Libraries
- **Movies**: Browse your movie collection
- **TV Shows**: Browse TV series and episodes
- **Music**: Browse music albums and tracks
- **Photos**: Browse photo libraries

#### 🔍 Search
- **Global Search**: Search across all libraries
- **Filters**: Filter by media type, year, genre
- **Voice Search**: Use voice input for search (if supported)

#### ⚙️ Settings
- **Server Management**: Add/remove Plex servers
- **Account Settings**: Manage Plex account
- **Playback Settings**: Configure playback options
- **App Settings**: General application preferences

### Library Browsing

#### Grid View
- Visual browsing with poster thumbnails
- Sort by: Recently Added, Title, Year, Rating
- Filter by: Genre, Decade, Content Rating, Unwatched

#### List View
- Compact list with metadata
- Quick access to play buttons
- Episode progress indicators for TV shows

#### Content Details
Each media item displays:
- **Poster/Thumbnail**: High-quality artwork
- **Title & Year**: Content identification
- **Rating**: Star rating and content rating
- **Duration**: Runtime for movies/episodes
- **Progress**: Watch progress indicator
- **Actions**: Play, Queue, Favorite, More options

## Media Playback

### Basic Playback

#### Starting Playback
1. **Tap Media Item**: From any library or search result
2. **Auto-Play**: Begins playback immediately
3. **Quality Selection**: Automatic based on connection, or manual override

#### Playback Controls
- **Play/Pause**: Tap center of screen or play button
- **Seek**: Drag progress bar or use skip buttons (±10s, ±30s)
- **Volume**: Device volume controls or on-screen slider
- **Fullscreen**: Double-tap or fullscreen button

### Advanced Features

#### Picture-in-Picture (PiP)
- **Activate**: Press home button during playback
- **Controls**: Continue playback in small window
- **Close**: Tap close button or return to app

#### Background Audio
- **Continue Playing**: Audio plays when app minimized
- **Lock Screen**: Controls available on lock screen
- **Notifications**: Media controls in notification shade

#### Casting
- **Cast Button**: Available on supported devices
- **Device Selection**: Choose Chromecast or compatible device
- **Remote Control**: Control playback from casting device

### Quality & Performance

#### Quality Options
- **Auto**: Automatic quality based on connection
- **Original**: Highest available quality
- **1080p**: Full HD quality
- **720p**: HD quality
- **480p**: Standard definition
- **360p**: Low quality for slow connections

#### Network Optimization
- **Adaptive Streaming**: Adjusts quality based on bandwidth
- **Buffering**: Intelligent buffering for smooth playback
- **Resume on Network**: Automatically resume when connection restored

## Settings & Configuration

### Server Management

#### Adding a Server
1. **Navigate**: Settings → Server Management → Add Server
2. **Server Details**:
   - Name: Friendly name for the server
   - Address: IP address or hostname
   - Port: Usually 32400 (default)
   - Local: Check if on same network
3. **Test Connection**: Verify server is reachable
4. **Authenticate**: Provide server-specific authentication if required

#### Server Options
- **Set as Default**: Primary server for new content
- **Remove Server**: Disconnect and remove from app
- **Refresh Info**: Update server information
- **Test Connection**: Verify connectivity

### Account Settings

#### Plex Account
- **Sign Out**: Disconnect from Plex.tv
- **Account Info**: View account details
- **Subscription**: Manage Plex Pass features

#### Profile Management
- **Switch Profile**: Change active Plex profile
- **Profile Settings**: Customize profile preferences
- **Parental Controls**: Manage content restrictions

### Playback Settings

#### Video Playback
- **Default Quality**: Set preferred streaming quality
- **Auto-play**: Automatically start next episode
- **Skip Intros**: Automatically skip TV show intros
- **Subtitles**: Default subtitle preferences

#### Audio Playback
- **Audio Boost**: Enhance quiet audio
- **Normalize Volume**: Consistent volume levels
- **Audio Track**: Preferred language selection

### App Settings

#### General
- **Theme**: Light, Dark, or System default
- **Language**: App language selection
- **Notifications**: Playback and download notifications

#### Advanced
- **Debug Logging**: Enable detailed logging
- **Cache Management**: Clear cached data
- **Storage Location**: Choose download location

## Troubleshooting

### Connection Issues

#### Cannot Connect to Server
**Symptoms**: "Server Unreachable" or connection timeout

**Solutions**:
1. **Check Server Status**: Ensure Plex Media Server is running
2. **Network Connectivity**: Verify device and server are on same network
3. **Firewall Settings**: Check firewall allows Plex connections
4. **Port Forwarding**: Ensure port 32400 is accessible
5. **IP Address**: Verify correct server IP address
6. **Restart Services**: Restart Plex Media Server

#### Authentication Problems
**Symptoms**: PIN authentication fails or times out

**Solutions**:
1. **Internet Connection**: Ensure stable internet for plex.tv access
2. **Browser Issues**: Try different browser for PIN entry
3. **PIN Timeout**: Enter PIN within 5 minutes
4. **Account Issues**: Verify Plex account is active
5. **Clear Cache**: Clear app data and retry

### Playback Issues

#### Media Won't Play
**Symptoms**: Playback fails to start or buffers endlessly

**Solutions**:
1. **Server Transcoding**: Check server can transcode media
2. **File Corruption**: Verify media file integrity on server
3. **Network Speed**: Test connection speed and stability
4. **Quality Settings**: Try lower quality setting
5. **App Permissions**: Ensure storage and network permissions granted

#### Audio/Video Sync Issues
**Symptoms**: Audio and video out of synchronization

**Solutions**:
1. **Restart Playback**: Stop and restart the media
2. **Quality Change**: Switch to different quality setting
3. **Device Restart**: Restart Android device
4. **Server Restart**: Restart Plex Media Server

### Performance Issues

#### App Runs Slowly
**Symptoms**: Lag, freezing, or slow loading

**Solutions**:
1. **Clear Cache**: Clear app cache and data
2. **Restart App**: Force close and restart PlexConnect
3. **Update App**: Ensure latest version installed
4. **Device Storage**: Free up device storage space
5. **Background Apps**: Close other running applications

#### High Battery Usage
**Symptoms**: Battery drains quickly during use

**Solutions**:
1. **Quality Settings**: Use lower quality for mobile data
2. **Background Playback**: Disable if not needed
3. **Screen Brightness**: Reduce screen brightness
4. **Network Type**: Prefer Wi-Fi over mobile data

### Data & Sync Issues

#### Media Not Appearing
**Symptoms**: New media not showing in libraries

**Solutions**:
1. **Server Refresh**: Refresh library on Plex server
2. **App Refresh**: Pull down to refresh in app
3. **Server Restart**: Restart Plex Media Server
4. **Library Scan**: Force library scan on server

#### Progress Not Syncing
**Symptoms**: Watch progress not saving or syncing

**Solutions**:
1. **Account Sync**: Ensure signed into Plex account
2. **Network Issues**: Check internet connectivity
3. **Server Settings**: Verify server allows sync
4. **App Permissions**: Ensure background sync permissions

## Advanced Features

### Keyboard Shortcuts

When using external keyboard:
- **Space**: Play/Pause
- **←/→**: Seek backward/forward 10 seconds
- **↑/↓**: Volume up/down
- **F**: Toggle fullscreen
- **M**: Toggle mute

### Voice Commands

On supported devices:
- "Play [movie/show name]"
- "Pause playback"
- "Resume playing"
- "Skip forward/backward"
- "What's playing?"

### Download Management

For offline viewing:
1. **Select Content**: Choose movies/episodes to download
2. **Quality Selection**: Choose download quality
3. **Storage Location**: Select download destination
4. **Download Queue**: Manage multiple downloads
5. **Offline Playback**: Access downloaded content

## Support & Resources

### Getting Help

#### Documentation
- [Complete User Guide](./USER_GUIDE.md) - This document
- [API Reference](./API_REFERENCE.md) - Technical details
- [Troubleshooting Guide](./TROUBLESHOOTING.md) - Common issues

#### Community Support
- **GitHub Issues**: Report bugs and request features
- **Plex Forums**: General Plex community support
- **Reddit**: r/Plex and r/androidapps communities

#### Official Resources
- **Plex Support**: https://support.plex.tv/
- **Plex Status**: Check service status
- **Android Help**: Google Android support

### Feedback

We welcome your feedback to improve PlexConnect:

- **Feature Requests**: Suggest new functionality
- **Bug Reports**: Help us fix issues
- **User Experience**: Share your experience
- **Performance**: Report performance concerns

### Version Information

To check your version:
1. Go to Settings → About
2. View app version and build information
3. Check for updates in Google Play Store

---

**Document Version:** 1.0.0
**Last Updated:** 2025-10-24
**PlexConnect Version:** 1.0.0