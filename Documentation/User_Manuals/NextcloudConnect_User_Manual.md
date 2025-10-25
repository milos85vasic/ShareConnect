# NextcloudConnect User Manual

## Table of Contents
1. [Introduction](#introduction)
2. [Installation](#installation)
3. [Getting Started](#getting-started)
4. [Server Connection](#server-connection)
5. [File Management](#file-management)
6. [Sharing Files](#sharing-files)
7. [Settings](#settings)
8. [Troubleshooting](#troubleshooting)
9. [FAQ](#faq)

## Introduction

NextcloudConnect is a native Android client for Nextcloud, providing seamless access to your cloud files and folders. Manage files, upload photos, create shares, and sync your data from your Android device.

### Key Features
- **File Management**: Browse, upload, download, and organize files
- **Automatic Upload**: Auto-upload photos and videos
- **Share Files**: Create public share links with password protection
- **Offline Access**: Mark files for offline availability
- **Multi-Account**: Connect to multiple Nextcloud servers
- **Security**: End-to-end encrypted file storage
- **ShareConnect Integration**: Share files to other services

## Installation

### From Google Play Store
1. Open Google Play Store
2. Search for "NextcloudConnect"
3. Tap "Install"
4. Wait for installation to complete
5. Open the app

### From F-Droid
1. Open F-Droid app store
2. Search for "NextcloudConnect"
3. Tap "Install"
4. Follow installation prompts

### From APK
1. Download the latest APK from [GitHub Releases](https://github.com/shareconnect/nextcloudconnect/releases)
2. Enable "Install from unknown sources" in Settings
3. Install the APK file
4. Grant necessary permissions

## Getting Started

### First Launch
1. Open NextcloudConnect after installation
2. Complete the onboarding process:
   - Introduction to features
   - Permission requests
   - Server setup
3. Add your first Nextcloud server

### Required Permissions
- **Internet Access**: For connecting to Nextcloud server
- **Network State**: To check connectivity
- **Storage Access**: For uploading and downloading files
- **Camera Access**: For photo uploads (optional)
- **Notification Access**: For upload/download progress

## Server Connection

### Adding Nextcloud Server

#### Method 1: Server Discovery
1. Tap "Add Server"
2. Enter your Nextcloud server URL:
   - Format: `https://cloud.example.com`
   - Or: `https://example.com/nextcloud`
3. Tap "Continue"
4. NextcloudConnect will verify the server

#### Method 2: Manual Configuration
1. Tap "Add Server" → "Manual Setup"
2. Enter server details:
   - **Server URL**: Full address including protocol
   - **Username**: Your Nextcloud username
   - **Password**: App password (recommended) or account password
3. Tap "Connect"

### Creating App Password (Recommended)
For better security, use app-specific passwords:

1. Log in to Nextcloud web interface
2. Go to Settings → Personal → Security
3. Scroll to "Devices & sessions"
4. Enter app name: "NextcloudConnect Android"
5. Click "Create new app password"
6. Copy the generated password
7. Use this password in NextcloudConnect

### Server Configuration

**Server Settings:**
- **Server Name**: Friendly name for this connection
- **Trust Certificate**: For self-signed SSL certificates
- **WebDAV Path**: Usually `/remote.php/webdav` (auto-detected)

**Connection Settings:**
- **Timeout**: Connection timeout (default: 30s)
- **Retry**: Number of connection retries
- **Chunk Size**: Upload chunk size for large files

### Managing Multiple Servers
1. Go to Settings → Accounts
2. View all connected servers
3. Tap a server to:
   - Switch to this account
   - Edit connection settings
   - Remove account
4. Long-press to set as default

## File Management

### Browsing Files

**File List View:**
- Files and folders listed with icons
- Swipe right to see file details
- Long-press for context menu
- Pull down to refresh

**File Details:**
- File name and type
- File size
- Last modified date
- Share status
- Offline availability

### Navigating Folders
1. Tap a folder to open it
2. Use back button to go up one level
3. Tap breadcrumb path to jump to any parent folder
4. Home button returns to root folder

### Searching Files
1. Tap search icon
2. Enter filename or partial text
3. Results update as you type
4. Tap result to navigate to file

### Downloading Files

**Single File:**
1. Tap the file
2. Select "Download"
3. Choose save location
4. Wait for download to complete
5. File saved to Downloads folder

**Multiple Files:**
1. Long-press to select first file
2. Tap additional files to select
3. Tap download icon
4. All selected files downloaded

**Automatic Downloads:**
- PDF and text files can open directly
- Images preview without downloading
- Videos can stream without downloading

### Uploading Files

**From Storage:**
1. Tap "+" button or upload icon
2. Select "Upload from device"
3. Choose files using file picker
4. Multiple files can be selected
5. Confirm upload

**From Camera:**
1. Tap "+" button
2. Select "Take photo" or "Record video"
3. Capture media
4. Confirm upload

**From Share Menu:**
1. Open any app with files (Gallery, File Manager)
2. Select files to share
3. Tap "Share" → "NextcloudConnect"
4. Choose destination folder
5. Confirm upload

### Auto-Upload

**Enable Auto-Upload:**
1. Go to Settings → Auto-Upload
2. Enable "Auto-upload photos"
3. Configure options:
   - Upload folder (default: /Photos)
   - WiFi only
   - Charging only
   - Original quality
4. Grant permission to access media

**Auto-Upload Options:**
- **Photos**: Automatically upload new photos
- **Videos**: Automatically upload new videos
- **Screenshots**: Upload screenshots separately
- **Delete After Upload**: Remove local copy after upload

### Creating Folders
1. Navigate to desired location
2. Tap "+" button
3. Select "New folder"
4. Enter folder name
5. Tap "Create"

### Renaming Files/Folders
1. Long-press file or folder
2. Select "Rename"
3. Enter new name
4. Tap "OK"

### Moving Files
1. Long-press file or folder
2. Select "Move"
3. Navigate to destination folder
4. Tap "Move here"

### Copying Files
1. Long-press file or folder
2. Select "Copy"
3. Navigate to destination folder
4. Tap "Paste"

### Deleting Files
1. Long-press file or folder
2. Select "Delete"
3. Confirm deletion
4. File moved to trash (if enabled on server)

**Bulk Delete:**
1. Long-press to select first item
2. Tap additional items
3. Tap delete icon
4. Confirm deletion

### Offline Files

**Mark for Offline:**
1. Long-press file
2. Select "Make available offline"
3. File downloads and keeps updated
4. Star icon indicates offline status

**Managing Offline Files:**
1. Go to Settings → Offline Files
2. View all offline files
3. Remove offline availability as needed
4. Clear all offline files

## Sharing Files

### Creating Share Links

**Basic Share:**
1. Long-press file or folder
2. Select "Share"
3. Toggle "Create public link"
4. Link is generated and copied
5. Share link via any app

**Advanced Share Options:**
- **Password Protection**: Set password for link access
- **Expiration Date**: Link expires automatically
- **Read-Only**: View-only access
- **Allow Upload**: Others can upload to folder
- **Allow Editing**: Others can edit shared files
- **Hide Download**: Prevent file downloads

### Share Link Settings

**Setting Password:**
1. Create or edit share link
2. Tap "Password"
3. Enter password
4. Confirm
5. Password required for access

**Setting Expiration:**
1. Create or edit share link
2. Tap "Expiration date"
3. Choose date
4. Link becomes invalid after this date

**Share Permissions:**
- **Read**: View and download only
- **Read & Write**: View, download, and upload
- **Create**: Can create new files
- **Update**: Can modify existing files
- **Delete**: Can delete files

### Managing Shares
1. Go to Settings → Shares
2. View all active shares
3. Tap a share to:
   - Copy link again
   - Edit settings
   - View stats (views, downloads)
   - Delete share

### Share with Users

**Internal Sharing:**
1. Long-press file
2. Select "Share" → "Share with users"
3. Enter username or email
4. Set permissions
5. Tap "Share"

**Group Sharing:**
1. Share file
2. Select "Share with groups"
3. Choose group
4. Set permissions

## Settings

### App Settings

**Appearance:**
- Theme: Light, Dark, Auto
- Sort Order: Name, Date, Size, Type
- Show Hidden Files: Display files starting with "."
- Grid View: Switch between list and grid

**Upload/Download:**
- WiFi Only: Use WiFi for transfers
- Cellular Limit: Max size on cellular
- Parallel Uploads: Number of simultaneous uploads
- Notification: Show transfer progress

**Security:**
- App Lock: PIN or biometric
- Certificate Trust: Manage SSL certificates
- Clear Cache: Remove cached data

**Storage:**
- Cache Location: Where to store cache
- Max Cache Size: Limit cache storage
- Clear Downloads: Remove downloaded files

### Account Settings
1. Go to Settings → Accounts
2. Select account
3. Configure:
   - Display name
   - Storage quota
   - Auto-upload settings
   - Sync preferences

### Auto-Upload Settings
- Photo upload folder
- Video upload folder
- Only on WiFi
- Only when charging
- Delete after upload
- File naming: Keep original or timestamp

### Sync Settings
NextcloudConnect syncs with ShareConnect:
- Theme preferences
- Server profiles
- File access history
- Bookmarks

## Troubleshooting

### Connection Issues

**Cannot Connect to Server:**
1. Verify server URL is correct
2. Check internet connection
3. Ensure server is online
4. Try accessing via web browser
5. Check firewall settings
6. Verify SSL certificate is valid

**SSL Certificate Errors:**
1. If using self-signed certificate:
   - Go to server settings
   - Enable "Trust certificate"
   - Reconnect
2. For persistent issues:
   - Check certificate expiration
   - Verify server hostname matches certificate
   - Contact server administrator

### Authentication Failed

1. Verify username is correct
2. Check password (try logging in via web)
3. If using app password:
   - Ensure it hasn't been revoked
   - Generate new app password
4. Clear app data and re-authenticate

### Upload/Download Failures

**Upload Issues:**
1. Check storage quota on server
2. Verify file permissions
3. Check file size limits
4. Ensure stable connection
5. Try smaller files first

**Download Issues:**
1. Check local storage space
2. Verify file permissions on server
3. Try different network
4. Clear app cache
5. Reduce chunk size in settings

### Sync Problems

**Files Not Syncing:**
1. Force sync: Pull down to refresh
2. Check auto-sync settings
3. Verify WiFi/cellular settings
4. Check file conflicts
5. Review sync log in Settings

**Slow Sync:**
1. Check network speed
2. Reduce parallel uploads
3. Adjust chunk size
4. Use WiFi instead of cellular

### App Crashes

1. Update to latest version
2. Clear app cache
3. Clear app data (will sign you out)
4. Reinstall app
5. Report crash with logs

## FAQ

### General Questions

**Q: Is NextcloudConnect free?**
A: Yes, NextcloudConnect is free and open-source. You need a Nextcloud server to use it.

**Q: Can I use NextcloudConnect without my own server?**
A: You need access to a Nextcloud server. You can use a provider's hosting or self-host.

**Q: How much storage do I get?**
A: Storage depends on your Nextcloud server configuration. Check with your administrator.

**Q: Can I connect to multiple Nextcloud servers?**
A: Yes, you can add and manage multiple accounts.

### Authentication

**Q: Should I use my account password or app password?**
A: App passwords are recommended for better security. They can be revoked without changing your main password.

**Q: What if I forget my password?**
A: Use password reset on the Nextcloud web interface, or contact your administrator.

**Q: How do I change my password?**
A: Change it in Nextcloud web interface, then update in NextcloudConnect account settings.

### File Management

**Q: What file types are supported?**
A: All file types are supported for upload/download. Preview is available for common types (images, PDF, text).

**Q: Is there a file size limit?**
A: Limits depend on server configuration. Large files are uploaded in chunks.

**Q: Can I edit files directly in the app?**
A: Direct editing is planned for a future release. Currently, download, edit externally, and re-upload.

**Q: Where are downloaded files stored?**
A: Files are saved to your device's Downloads folder by default. You can choose a different location.

### Sharing

**Q: How long do share links last?**
A: By default, share links don't expire unless you set an expiration date.

**Q: Can I see who accessed my share?**
A: Access logs depend on your Nextcloud server version and configuration.

**Q: How do I stop sharing a file?**
A: Go to Settings → Shares, find the share, and delete it.

**Q: Can shared users see my other files?**
A: No, shares only provide access to the specific files/folders you share.

### Privacy & Security

**Q: Is my data encrypted?**
A: Data is encrypted in transit (HTTPS). Server-side encryption depends on your Nextcloud configuration.

**Q: Who can see my files?**
A: Only you and users you explicitly share with. Server administrators may have access depending on server policies.

**Q: How secure are app passwords?**
A: App passwords are cryptographically secure and can only access specific app functions.

### Technical

**Q: What Android version is required?**
A: Android 9.0 (API 28) or higher.

**Q: Does NextcloudConnect work offline?**
A: Yes, files marked for offline access are available without internet.

**Q: How much data does auto-upload use?**
A: Configure "WiFi only" to avoid cellular data usage. File sizes depend on your content.

**Q: Can I use NextcloudConnect on tablets?**
A: Yes, with optimized layouts for larger screens.

### Getting Help

**Q: Where can I get help?**
A: Check Nextcloud community forum or ShareConnect support.

**Q: How do I report bugs?**
A: Report on GitHub: github.com/shareconnect/nextcloudconnect/issues

**Q: Is there a user community?**
A: Join the ShareConnect Discord or Nextcloud forums.

## Additional Resources

### Official Links
- **Website**: [shareconnect.org/nextcloudconnect](https://shareconnect.org/nextcloudconnect)
- **GitHub**: [github.com/shareconnect/nextcloudconnect](https://github.com/shareconnect/nextcloudconnect)
- **Documentation**: [docs.shareconnect.org/nextcloudconnect](https://docs.shareconnect.org/nextcloudconnect)

### Nextcloud Resources
- **Nextcloud**: [nextcloud.com](https://nextcloud.com)
- **Documentation**: [docs.nextcloud.com](https://docs.nextcloud.com)
- **Community Forum**: [help.nextcloud.com](https://help.nextcloud.com)
- **Provider List**: [nextcloud.com/providers](https://nextcloud.com/providers)

### Community
- **Discord**: Join the ShareConnect Discord server
- **Reddit**: r/ShareConnect, r/Nextcloud
- **Twitter**: @ShareConnectApp, @Nextclouders

---

**Version**: 1.0.0
**Last Updated**: 2025-10-25
**License**: Open Source (GPL-3.0)

For the latest updates and detailed documentation, visit [shareconnect.org/docs](https://shareconnect.org/docs)
