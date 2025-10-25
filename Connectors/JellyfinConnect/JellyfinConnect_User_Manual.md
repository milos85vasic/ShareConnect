# JellyfinConnect User Manual

## Welcome to JellyfinConnect!

JellyfinConnect is your gateway to connecting your Jellyfin Media Server with the ShareConnect ecosystem. This manual will guide you through setup, configuration, and daily use of the application.

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [First-Time Setup](#first-time-setup)
3. [Adding a Jellyfin Server](#adding-a-jellyfin-server)
4. [Browsing Your Media](#browsing-your-media)
5. [Playing Media](#playing-media)
6. [Searching Content](#searching-content)
7. [Managing Profiles](#managing-profiles)
8. [App Settings](#app-settings)
9. [Synchronization](#synchronization)
10. [Security Features](#security-features)
11. [Troubleshooting](#troubleshooting)
12. [FAQs](#faqs)

---

## Getting Started

### What is JellyfinConnect?

JellyfinConnect is an Android application that connects to your Jellyfin Media Server, allowing you to:

- Browse your media libraries (Movies, TV Shows, Music, etc.)
- Track your playback progress
- Search across all your media
- Sync settings with other ShareConnect apps
- Manage multiple server profiles

### System Requirements

- **Android Version**: 9.0 (API 28) or higher
- **Network**: WiFi or mobile data connection
- **Jellyfin Server**: Version 10.8.0 or higher
- **Storage**: 50 MB free space

### Installation

1. Download JellyfinConnect from the app store or ShareConnect distribution
2. Tap the APK file to install
3. Grant necessary permissions when prompted
4. Launch the app from your app drawer

---

## First-Time Setup

### Initial Launch

When you first launch JellyfinConnect, you'll be guided through a quick setup:

1. **Welcome Screen**: Brief introduction to the app
2. **Permissions**: Grant network and storage permissions
3. **Language Selection**: Choose your preferred language
4. **Theme Selection**: Pick a visual theme (can be changed later)
5. **Server Setup**: Add your first Jellyfin server

### Language Configuration

JellyfinConnect supports multiple languages. The language setting automatically syncs across all ShareConnect apps:

**To change language:**
1. Go to Settings → Language
2. Select your preferred language
3. The app will restart with the new language
4. All ShareConnect apps will sync to this language

### Theme Selection

Choose from 6 built-in themes or create your own:

**Built-in Themes:**
- Material Purple
- Ocean Blue
- Forest Green
- Sunset Orange
- Rose Pink
- Dark Mode

**To change theme:**
1. Go to Settings → Appearance → Theme
2. Browse available themes
3. Tap to apply instantly
4. Themes sync across ShareConnect apps

---

## Adding a Jellyfin Server

### Automatic Discovery

JellyfinConnect can automatically discover Jellyfin servers on your local network:

1. Tap **Add Server** on the home screen
2. Select **Auto-discover**
3. Wait for servers to appear (usually 5-10 seconds)
4. Select your server from the list
5. Proceed to authentication

### Manual Configuration

If automatic discovery doesn't work, add a server manually:

1. Tap **Add Server** → **Manual Setup**
2. Enter the following details:
   - **Server Name**: A friendly name (e.g., "Home Media Server")
   - **Server URL**: Your server address
     - Local network: `http://192.168.1.100:8096`
     - Remote access: `https://jellyfin.yourdomain.com`
   - **Port**: Usually 8096 (default)
3. Tap **Test Connection**
4. If successful, proceed to authentication

### Authentication

#### Username/Password Authentication

1. Enter your Jellyfin username
2. Enter your password
3. Tap **Sign In**
4. Your credentials are securely stored
5. Access token is encrypted and saved

#### API Key Authentication (Advanced)

For advanced users with API keys:

1. Tap **Use API Key** instead of username/password
2. Enter your API key from Jellyfin Dashboard
3. Tap **Authenticate**

**To generate an API key in Jellyfin:**
1. Open Jellyfin web interface
2. Go to Dashboard → API Keys
3. Click **New Key**
4. Name it "JellyfinConnect"
5. Copy the generated key

---

## Browsing Your Media

### Library Overview

After authentication, you'll see your media libraries:

- **Movies**: Your movie collection
- **TV Shows**: Series and episodes
- **Music**: Audio library
- **Photos**: Photo collections
- **Collections**: Custom collections
- **Favorites**: Your favorite items

### Navigating Libraries

1. **Tap a library** to open it
2. **Scroll vertically** to browse items
3. **Tap an item** to see details
4. **Long-press** for quick actions

### Viewing Options

**Grid View** (default):
- Shows poster images
- 2-3 items per row
- Best for visual browsing

**List View**:
- Shows title and details
- One item per row
- Best for quick scanning

**To switch views:**
Tap the view toggle button in the top-right corner

### Sorting Options

Sort your media by:
- **Name** (A-Z or Z-A)
- **Date Added** (newest or oldest)
- **Release Date**
- **Rating**
- **Runtime**

**To change sort:**
Tap the sort button and select your preference

### Filtering

Filter your library by:
- **Genre**: Action, Comedy, Drama, etc.
- **Year**: Specific year or range
- **Rating**: Minimum rating threshold
- **Status**: Played/Unplayed

**To apply filters:**
1. Tap the filter button
2. Select filter criteria
3. Tap **Apply**
4. Tap **Clear Filters** to reset

---

## Playing Media

### Starting Playback

1. Navigate to an item (movie, episode, song)
2. Tap the **Play** button
3. Playback starts in your preferred media player
4. Progress is automatically tracked

### Playback Controls

While playing media:

- **Play/Pause**: Tap center of screen
- **Seek**: Drag the progress bar
- **Volume**: Use device volume buttons
- **Fullscreen**: Rotate device
- **Subtitles**: Tap subtitle button (if available)
- **Quality**: Tap settings → Video Quality

### Resume Playback

If you stop watching before finishing:

1. Return to the item
2. Tap **Resume** to continue from where you left off
3. Or tap **Play from Beginning** to restart

### Marking as Watched

**Automatically**:
- Items are marked as watched when you finish them
- Progress is saved continuously

**Manually**:
1. Long-press an item
2. Select **Mark as Played** or **Mark as Unplayed**
3. Confirmation appears

---

## Searching Content

### Quick Search

1. Tap the search icon (magnifying glass)
2. Type your search query
3. Results appear as you type
4. Tap a result to open it

### Search Filters

Refine your search:

- **Type**: Movies, Shows, Episodes, Music, All
- **Library**: Specific library or all libraries
- **Year**: Specific year or range

**To apply search filters:**
1. Enter search query
2. Tap **Filters**
3. Select criteria
4. Results update automatically

### Search Tips

- **Use partial names**: "aveng" finds "Avengers"
- **Include year**: "matrix 1999" for specific movie
- **Actor names**: "tom hanks" finds movies with Tom Hanks
- **Director names**: "spielberg" finds Spielberg films
- **Genre keywords**: "sci-fi" finds science fiction

---

## Managing Profiles

### What are Profiles?

Profiles store server configurations. You can have multiple profiles for:
- Different Jellyfin servers
- Different user accounts on the same server
- Home vs. remote access configurations

### Adding a New Profile

1. Go to Settings → Profiles
2. Tap **Add Profile**
3. Follow the server setup wizard
4. Give the profile a descriptive name

### Switching Profiles

1. Tap the profile icon in the top-left
2. Select the profile you want to use
3. The app switches instantly
4. Your position and settings are preserved

### Editing a Profile

1. Go to Settings → Profiles
2. Long-press the profile to edit
3. Select **Edit**
4. Update details and tap **Save**

### Deleting a Profile

1. Go to Settings → Profiles
2. Long-press the profile to delete
3. Select **Delete**
4. Confirm deletion

**Note**: You cannot delete the currently active profile. Switch to another profile first.

### Setting Default Profile

1. Go to Settings → Profiles
2. Long-press the desired profile
3. Select **Set as Default**
4. This profile will be used on app launch

---

## App Settings

### General Settings

**Language**:
- Choose from supported languages
- Syncs across ShareConnect apps

**Theme**:
- Select visual theme
- Create custom themes
- Enable dark mode

**Notifications**:
- Enable/disable notifications
- Configure notification types
- Set quiet hours

### Playback Settings

**Default Player**:
- Choose default media player
- Internal player or external apps

**Quality Settings**:
- Auto (recommended)
- High (1080p+)
- Medium (720p)
- Low (480p)

**Subtitle Settings**:
- Default subtitle language
- Subtitle size and style
- Subtitle encoding

### Network Settings

**Streaming**:
- WiFi only
- Allow mobile data
- Data saver mode

**Connection Timeout**:
- Adjust timeout values
- Retry attempts

**Cache Settings**:
- Cache size limit
- Clear cache

### Privacy Settings

**Security Access**:
- Enable PIN protection
- Enable biometric lock
- Lock timeout duration

**Usage Data**:
- Share anonymous usage statistics
- Crash reporting

---

## Synchronization

### What Syncs?

JellyfinConnect automatically syncs with other ShareConnect apps:

- **Themes**: Visual appearance
- **Profiles**: Server configurations
- **History**: What you've watched
- **Bookmarks**: Saved items
- **Preferences**: App settings
- **Language**: Language selection

### How Sync Works

Synchronization happens automatically:

1. **Real-time**: Changes sync within seconds
2. **Bidirectional**: All apps stay in sync
3. **Encrypted**: All sync data is encrypted
4. **Local**: No cloud servers involved
5. **Automatic**: No user action required

### Viewing Sync Status

1. Go to Settings → Sync Status
2. See connected ShareConnect apps
3. View last sync time for each module
4. Check for sync errors

### Troubleshooting Sync

If sync isn't working:

1. **Check network**: Both devices must be on same network
2. **Check permissions**: Grant network discovery permission
3. **Restart apps**: Close and reopen ShareConnect apps
4. **Check firewall**: Ensure ports aren't blocked

---

## Security Features

### PIN Protection

Protect your app with a 4-6 digit PIN:

1. Go to Settings → Security → PIN Protection
2. Tap **Enable PIN**
3. Enter your desired PIN (4-6 digits)
4. Confirm PIN
5. PIN is now required to open the app

**To change PIN:**
1. Settings → Security → Change PIN
2. Enter current PIN
3. Enter new PIN
4. Confirm new PIN

**To disable PIN:**
1. Settings → Security → Disable PIN
2. Enter current PIN to confirm

### Biometric Lock

Use fingerprint or face recognition:

1. Ensure biometrics are set up on your device
2. Go to Settings → Security → Biometric Lock
3. Enable biometric authentication
4. Test by locking and unlocking the app

### Auto-Lock

Automatically lock the app when idle:

1. Settings → Security → Auto-Lock
2. Choose timeout:
   - Immediately
   - 1 minute
   - 5 minutes
   - 15 minutes
   - 30 minutes
   - Never

### Credential Storage

Your credentials are securely stored:

- **Encryption**: All credentials are encrypted
- **Device-only**: Never leaves your device
- **Secure storage**: Uses Android Keystore
- **Protected**: Can't be accessed by other apps

---

## Troubleshooting

### Can't Connect to Server

**Problem**: "Connection failed" or "Server not found"

**Solutions:**
1. Verify server URL is correct
   - Check IP address
   - Check port number (usually 8096)
   - Include `http://` or `https://`
2. Test on same network
   - Ensure phone and server on same WiFi
3. Check server status
   - Open Jellyfin in web browser
   - Verify server is running
4. Check firewall
   - Ensure port 8096 is open
   - Disable firewall temporarily to test

### Authentication Failed

**Problem**: "Invalid username or password"

**Solutions:**
1. Verify credentials in Jellyfin web interface
2. Check for typos in username
3. Ensure password is correct (case-sensitive)
4. Try creating a new user in Jellyfin Dashboard

### Media Won't Play

**Problem**: Playback fails or media doesn't load

**Solutions:**
1. Check internet connection
2. Try different quality setting
3. Ensure media player is installed
4. Update media player app
5. Clear app cache
6. Check Jellyfin transcoding settings

### Slow Performance

**Problem**: App is laggy or slow

**Solutions:**
1. Close other apps running in background
2. Clear app cache
3. Reduce quality settings
4. Check network speed
5. Restart your device
6. Update the app

### Sync Not Working

**Problem**: Changes don't sync between devices

**Solutions:**
1. Ensure both devices on same network
2. Check sync status in settings
3. Restart both apps
4. Verify permissions granted
5. Check firewall/router settings

---

## FAQs

### General Questions

**Q: Is JellyfinConnect free?**
A: Yes, JellyfinConnect is free and open-source as part of ShareConnect.

**Q: Do I need a Jellyfin account?**
A: Yes, you need a Jellyfin server and user account. Jellyfin is free and self-hosted.

**Q: Can I use it without internet?**
A: Yes, on local network. For remote access, you need internet connection.

**Q: How much data does streaming use?**
A: Depends on quality settings:
- Low (480p): ~300 MB/hour
- Medium (720p): ~800 MB/hour
- High (1080p): ~2 GB/hour
- Auto: Adjusts based on connection

**Q: Can I download media for offline viewing?**
A: This feature is planned for a future update.

### Setup Questions

**Q: What's the default port for Jellyfin?**
A: Port 8096 for HTTP, 8920 for HTTPS.

**Q: Can I add multiple servers?**
A: Yes! Create separate profiles for each server.

**Q: How do I access my server remotely?**
A: Set up port forwarding or use a reverse proxy with HTTPS. See Jellyfin documentation.

**Q: Do I need to create a new Jellyfin account?**
A: No, use your existing Jellyfin account credentials.

### Playback Questions

**Q: What media formats are supported?**
A: All formats supported by Jellyfin (virtually all formats via transcoding).

**Q: Can I cast to my TV?**
A: Yes, using Google Cast if your player supports it.

**Q: Why is playback buffering?**
A: Either network is slow or server needs to transcode. Try lower quality setting.

**Q: Can I adjust playback speed?**
A: Depends on your media player. Most players support speed adjustment.

### Sync Questions

**Q: What data is synced?**
A: Themes, profiles, history, bookmarks, preferences, and language settings.

**Q: Is my data sent to the cloud?**
A: No! All sync happens locally between your devices.

**Q: Can I sync with PlexConnect?**
A: Profile sync works (both are media servers), but server-specific data doesn't sync.

**Q: How do I stop syncing?**
A: Go to Settings → Sync and disable specific sync modules.

### Security Questions

**Q: Are my credentials safe?**
A: Yes, stored encrypted using Android Keystore.

**Q: Can others see my watch history?**
A: Only if they have access to your Jellyfin account.

**Q: Should I use HTTP or HTTPS?**
A: Use HTTP on trusted local network, HTTPS for remote access.

**Q: What's the difference between PIN and biometric lock?**
A: Both lock the app. Biometric is faster but requires device support.

### Troubleshooting Questions

**Q: App crashes on launch. What should I do?**
A: Clear app data, reinstall, or check for updates.

**Q: Media library is empty. Why?**
A: Check Jellyfin Dashboard to ensure libraries are properly configured.

**Q: Search returns no results. What's wrong?**
A: Wait for Jellyfin to finish scanning your library.

**Q: Can't connect after changing WiFi. Why?**
A: Update server URL with new IP address, or use hostname instead of IP.

**Q: Lost my PIN. How do I reset it?**
A: Clear app data (Settings → Apps → JellyfinConnect → Clear Data) and set up again.

### Advanced Questions

**Q: Can I use API keys instead of passwords?**
A: Yes! Tap "Use API Key" during authentication.

**Q: How do I enable transcoding?**
A: Transcoding is configured in Jellyfin Dashboard, not the app.

**Q: Can I customize the theme colors?**
A: Yes, use the custom theme creator in Settings → Appearance.

**Q: How do I contribute to development?**
A: Visit the ShareConnect GitHub repository and check CONTRIBUTING.md.

**Q: Is there a desktop version?**
A: JellyfinConnect is Android-only. Use Jellyfin web interface or desktop apps.

---

## Getting Help

### Support Channels

- **GitHub Issues**: Report bugs and request features
- **Wiki**: Detailed documentation and guides
- **Community Forum**: Ask questions and share tips

### Before Requesting Help

Please gather this information:

1. App version (Settings → About)
2. Android version
3. Jellyfin server version
4. Error messages or screenshots
5. Steps to reproduce the issue

---

## Appendix

### Keyboard Shortcuts (Android TV)

If using JellyfinConnect on Android TV:

- **Back**: Return to previous screen
- **Home**: Return to home screen
- **Play/Pause**: Control playback
- **Left/Right arrows**: Seek backward/forward
- **Up/Down arrows**: Navigate menus

### Supported Languages

- English
- Spanish
- French
- German
- Italian
- Portuguese
- Japanese
- Chinese (Simplified)
- Chinese (Traditional)
- Korean
- Russian
- Dutch
- Polish
- Turkish

### Credits

JellyfinConnect is part of the ShareConnect ecosystem.

**Development**: ShareConnect Team
**Jellyfin**: Jellyfin Project
**Libraries**: Retrofit, OkHttp, Compose, Room, and many others

---

## Glossary

- **API**: Application Programming Interface - how apps communicate
- **Jellyfin**: Free, open-source media server software
- **Library**: Collection of media (movies, shows, music)
- **Profile**: Saved server connection configuration
- **Sync**: Automatic synchronization of data between apps
- **Transcoding**: Converting media to compatible format on-the-fly
- **Ticks**: Jellyfin's time unit (1 tick = 100 nanoseconds)

---

**Thank you for using JellyfinConnect!**

For the latest updates and news, visit:
- Wiki: https://deepwiki.com/vasic-digital/ShareConnect
- GitHub: https://github.com/your-org/ShareConnect

**Version**: 1.0.0
**Last Updated**: October 25, 2025
