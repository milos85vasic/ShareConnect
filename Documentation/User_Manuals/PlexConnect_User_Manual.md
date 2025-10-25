# PlexConnect User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Authentication](#authentication)
5. [Server Management](#server-management)
6. [Browsing Media](#browsing-media)
7. [Playback](#playback)
8. [Settings](#settings)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

## Introduction

PlexConnect is a native Android client for Plex Media Server, providing seamless access to your personal media library. Browse movies, TV shows, music, and photos from your Plex server on your Android device.

### Key Features
- **Native Android Experience**: Built with modern Material Design 3
- **PIN Authentication**: Secure login with Plex.tv
- **Multi-Server Support**: Connect to multiple Plex servers
- **Offline Browsing**: Cached media metadata for quick access
- **Playback Controls**: Stream media directly from your server
- **Cross-Device Sync**: Continue watching across devices
- **ShareConnect Integration**: Share media to other services

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "PlexConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "PlexConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnect/plexconnect/releases)
2. Enable "Install from unknown sources" in Settings
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open PlexConnect after installation
2. Complete the onboarding process:
   - Introduction to PlexConnect features
   - Server setup instructions
   - Permission requests
3. Proceed to authentication

### Required Permissions
- **Internet Access**: For connecting to Plex.tv and servers
- **Network State**: To check connectivity
- **Storage Access**: For caching media metadata
- **Notification Access**: For playback notifications

## Authentication

PlexConnect uses Plex.tv PIN authentication for secure login.

### PIN Authentication Process
1. Tap "Sign In with Plex" on the welcome screen
2. PlexConnect will display a 4-character PIN code
3. **On your computer or phone:**
   - Open a web browser
   - Go to [plex.tv/link](https://app.plex.tv/link)
   - Sign in to your Plex account if not already logged in
   - Enter the PIN code shown in PlexConnect
   - Click "Link"
4. **In PlexConnect:**
   - The app will automatically detect authentication
   - You'll be signed in within a few seconds

### Authentication Tips
- PIN codes expire after 10 minutes
- If expired, tap "Generate New PIN"
- Ensure your device has internet connection
- Check Plex.tv service status if issues persist

### Sign Out
1. Go to Settings → Account
2. Tap "Sign Out"
3. Confirm sign out

## Server Management

### Adding Your First Server

#### Automatic Discovery (Local Network)
1. After authentication, PlexConnect will automatically discover local servers
2. Tap on your server from the list
3. Grant access if prompted
4. Server is now connected

#### Manual Server Addition
1. Tap "+" button or "Add Server"
2. Enter server details:
   - **Server Name**: Descriptive name
   - **Address**: IP address or hostname
   - **Port**: Usually 32400 (default)
3. Tap "Test Connection"
4. If successful, tap "Save"

### Managing Multiple Servers
1. Go to Settings → Servers
2. View all connected servers
3. Select a server to:
   - View details
   - Test connection
   - Set as default
   - Remove server

### Server Settings
- **Default Server**: Server used when opening app
- **Auto-Connect**: Connect automatically on app launch
- **Connection Type**: Prefer local or remote connection
- **Quality**: Default streaming quality for this server

## Browsing Media

### Home Screen
The home screen shows:
- **On Deck**: Continue watching in-progress content
- **Recently Added**: New content on your server
- **Libraries**: All your media libraries
- **Recommended**: Suggested content based on history

### Navigating Libraries
1. Tap on a library (Movies, TV Shows, Music, Photos)
2. Browse content in grid or list view
3. Use filters and sorting:
   - **Filter**: By genre, year, rating
   - **Sort**: Title, date added, release date

### Search
1. Tap the search icon
2. Enter your search query
3. Results show across all libraries
4. Filter results by library type

### Media Details
Tap on any media item to view:
- **Poster/Artwork**: High-quality images
- **Title and Metadata**: Year, duration, rating
- **Summary**: Description and synopsis
- **Cast & Crew**: Actors and directors
- **Related Content**: Similar media
- **Actions**: Play, add to playlist, share

## Playback

### Starting Playback
1. Open media details
2. Tap "Play" button
3. Choose quality if prompted
4. Video begins playing

### Playback Controls
- **Play/Pause**: Tap center of screen
- **Seek**: Drag progress bar
- **Volume**: Use device volume buttons
- **Brightness**: Swipe up/down on left side
- **Volume**: Swipe up/down on right side

### Playback Options
Access during playback by tapping the menu icon:
- **Quality**: Select streaming quality
  - Original (highest quality, requires bandwidth)
  - 1080p, 720p, 480p, 360p
- **Audio Track**: Select language or format
- **Subtitles**: Enable/disable and select language
- **Playback Speed**: Adjust speed (0.5x to 2x)

### Picture-in-Picture
1. While playing, press home button
2. Video continues in small window
3. Drag to reposition
4. Tap to expand or close

### Continue Watching
- Progress is automatically saved
- Resume from where you left off
- Sync across all Plex clients
- View watch history in Settings

## Settings

### App Settings
**Appearance**
- Theme: Light, Dark, Auto
- Grid Size: Compact, Normal, Comfortable
- Show Posters: Enable/disable artwork

**Playback**
- Default Quality: Automatic, 1080p, 720p, etc.
- Auto-Play Next: Continue to next episode
- Remember Audio: Remember audio/subtitle preferences
- Hardware Acceleration: Use GPU for decoding

**Network**
- Cellular Data: Allow streaming on cellular
- Download Quality: For offline content
- Bandwidth Limit: Limit streaming bandwidth

**Security**
- App Lock: PIN or biometric
- Clear Cache: Remove cached data
- Clear History: Remove watch history

### Server Settings
1. Go to Settings → Servers
2. Select a server
3. Configure:
   - Connection settings
   - Quality preferences
   - Library visibility
   - Remote access settings

### Sync Settings
PlexConnect syncs with ShareConnect ecosystem:
- Theme preferences
- Server profiles
- Watch history
- Bookmarks
- Language settings

## Troubleshooting

### Cannot Connect to Server

**Local Network Issues:**
1. Ensure device and server on same network
2. Check server IP address is correct
3. Verify port 32400 is accessible
4. Restart Plex Media Server
5. Try using server's local IP (192.168.x.x)

**Remote Access Issues:**
1. Ensure remote access enabled in Plex server
2. Check server is published to Plex.tv
3. Verify internet connection on both ends
4. Try reconnecting to Plex.tv

### Authentication Failed

1. Ensure correct PIN entered at plex.tv/link
2. Check internet connection
3. Verify Plex.tv service status
4. Clear app data and try again
5. Check device date/time is correct

### Playback Issues

**Video Won't Play:**
1. Check media format compatibility
2. Enable transcoding on server
3. Reduce quality setting
4. Check available bandwidth
5. Try different media file

**Buffering Problems:**
1. Reduce streaming quality
2. Check network speed
3. Enable bandwidth limiting on server
4. Use local connection instead of remote
5. Close other apps using bandwidth

**Audio/Video Out of Sync:**
1. Try different audio track
2. Disable hardware acceleration
3. Update PlexConnect to latest version
4. Restart playback

### App Crashes

1. Update to latest version
2. Clear app cache: Settings → Apps → PlexConnect → Clear Cache
3. Clear app data (will sign you out)
4. Reinstall the app
5. Report crash with logs to support

### Library Not Showing

1. Ensure library is enabled for this server
2. Refresh server libraries: Settings → Servers → Refresh
3. Check library permissions on server
4. Verify library contains media files

## FAQ

### General Questions

**Q: Is PlexConnect free?**
A: Yes, PlexConnect is free and open-source. You need a Plex Media Server (also free) to use it.

**Q: Do I need Plex Pass?**
A: No, PlexConnect works with free Plex accounts. Some advanced features may require Plex Pass on the server side.

**Q: Can I download media for offline viewing?**
A: Offline downloads are planned for a future release.

**Q: How many servers can I connect to?**
A: Unlimited. You can add and manage multiple Plex servers.

### Authentication

**Q: Why use PIN authentication instead of username/password?**
A: PIN authentication is more secure and doesn't require storing your Plex password in the app.

**Q: Can I stay signed in?**
A: Yes, authentication tokens are stored securely. You'll only need to re-authenticate if you sign out or after extended periods.

**Q: What if my PIN expires?**
A: Simply generate a new PIN and repeat the authentication process.

### Media Playback

**Q: What video formats are supported?**
A: PlexConnect supports all formats that your Plex server can transcode: MP4, MKV, AVI, MOV, and many others.

**Q: Can I play 4K content?**
A: Yes, if your device supports 4K playback and you have sufficient bandwidth.

**Q: Does PlexConnect support HDR?**
A: HDR support depends on your device capabilities and Plex server version.

**Q: Can I stream to Chromecast?**
A: Chromecast support is planned for a future release.

### Server Management

**Q: Can I connect to a friend's Plex server?**
A: Yes, if they've shared their server with your Plex account. Shared servers appear automatically after authentication.

**Q: My server doesn't appear in the list**
A: Try manual server addition with IP address and port. Ensure remote access is enabled on the server.

**Q: Can I use PlexConnect without internet?**
A: Yes, for local servers on the same network. Remote access requires internet.

### Privacy & Security

**Q: Is my data private?**
A: Yes. All communication is between your device and your Plex server. PlexConnect doesn't collect or store your media data.

**Q: Are my credentials stored securely?**
A: Yes. Authentication tokens are encrypted and stored using Android Keystore.

**Q: Can I lock the app?**
A: Yes. Enable App Lock in Settings and use PIN or biometric authentication.

### ShareConnect Integration

**Q: What is ShareConnect?**
A: ShareConnect is an ecosystem that allows sharing content between different apps and services.

**Q: How do I share from PlexConnect?**
A: Long-press any media item and select "Share" to send to other ShareConnect apps.

**Q: What can I share to?**
A: Download managers, cloud storage, torrent clients, and other connected services.

### Technical

**Q: What Android version is required?**
A: Android 9.0 (API 28) or higher.

**Q: How much storage does PlexConnect use?**
A: App size is approximately 15-20 MB. Cache size depends on usage.

**Q: Does PlexConnect work on tablets?**
A: Yes, PlexConnect has optimized layouts for both phones and tablets.

**Q: Can I use PlexConnect on Android TV?**
A: Android TV support is planned for a future release.

### Getting Help

**Q: Where can I report bugs?**
A: Report issues on [GitHub](https://github.com/shareconnect/plexconnect/issues)

**Q: How do I request features?**
A: Submit feature requests on GitHub or vote on existing requests.

**Q: Is there a support forum?**
A: Yes, visit the ShareConnect community forum at shareconnect.org/forum

**Q: How do I check my version?**
A: Go to Settings → About → Version

## Additional Resources

### Official Links
- **Website**: [shareconnect.org/plexconnect](https://shareconnect.org/plexconnect)
- **GitHub**: [github.com/shareconnect/plexconnect](https://github.com/shareconnect/plexconnect)
- **Documentation**: [docs.shareconnect.org/plexconnect](https://docs.shareconnect.org/plexconnect)

### Plex Resources
- **Plex Support**: [support.plex.tv](https://support.plex.tv)
- **Plex Forums**: [forums.plex.tv](https://forums.plex.tv)
- **Server Setup Guide**: [support.plex.tv/articles/200288586](https://support.plex.tv/articles/200288586)

### Community
- **Discord**: Join the ShareConnect Discord server
- **Reddit**: r/ShareConnect
- **Twitter**: @ShareConnectApp

---

**Version**: 1.0.0
**Last Updated**: 2025-10-25
**License**: Open Source (GPL-3.0)

For the latest updates and detailed documentation, visit [shareconnect.org/docs](https://shareconnect.org/docs)
