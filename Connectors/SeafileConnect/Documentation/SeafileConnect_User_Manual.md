# SeafileConnect - User Manual

## What is SeafileConnect?

SeafileConnect is a ShareConnect application that connects your Android device to Seafile cloud storage servers. It provides secure file access, library management, and support for encrypted libraries.

## Getting Started

### Installation

1. Install SeafileConnect APK on your Android device
2. Launch the app
3. Configure your Seafile server connection

### First-Time Setup

1. **Server Configuration**:
   - Open Settings tab
   - Enter your Seafile server URL (e.g., https://seafile.example.com)
   - Enter your username/email
   - Enter your password

2. **Test Connection**:
   - Tap "Test Connection" to verify settings
   - If successful, you'll see your account information

3. **Security (Optional)**:
   - Enable PIN or biometric authentication
   - Configure in Settings > Security

## Features

### Libraries Tab

Browse and manage your Seafile libraries (repositories):

- **View Libraries**: See all accessible libraries
- **Library Info**: Tap any library to view details
- **Encrypted Libraries**: Identified with lock icon
- **Storage Usage**: View library size and owner

### Files Tab

Navigate and manage files:

- **Browse Files**: Tap folders to navigate
- **File Operations**:
  - Long press for options menu
  - Download files to device
  - Upload new files
  - Create folders
  - Delete items
  - Move/rename files

- **File Types**: Supports all file types stored in Seafile

### Search Tab

Search across all libraries:

- **Quick Search**: Type query to search
- **Results**: Shows matching files and folders
- **Jump to Location**: Tap result to view file location

### Settings Tab

Configure SeafileConnect:

- **Server Settings**: URL, credentials
- **Security**: PIN/biometric authentication
- **Sync Preferences**: Auto-sync settings
- **Theme**: Choose app theme (synced across ShareConnect apps)
- **About**: App version and information

## Encrypted Libraries

### Accessing Encrypted Libraries

1. Navigate to Libraries tab
2. Tap encrypted library (lock icon)
3. Enter library password
4. Library will be decrypted for current session

### Security Notes

- Library passwords are NOT stored
- Re-enter password after app restart
- Encryption uses AES-256
- Only you can decrypt your libraries

## Tips & Tricks

### Quick Actions

- **Pull to Refresh**: Update library list
- **Swipe**: Quick actions on files
- **Long Press**: Additional options

### Offline Access

- Downloaded files available offline
- Synced automatically when online
- Check Downloads folder

### Sharing

- Share files from Files tab
- Generate share links (if enabled on server)
- Share to other ShareConnect apps

## Troubleshooting

### Cannot Connect to Server

- Check server URL is correct
- Verify internet connection
- Ensure server is accessible
- Check firewall settings

### Authentication Failed

- Verify username/email
- Check password
- Ensure account is active
- Try resetting password on server

### Cannot Decrypt Library

- Verify library password
- Ensure library is encrypted
- Check with library owner

### Upload/Download Fails

- Check network connection
- Verify sufficient storage space
- Check file permissions
- Try smaller files

### App Crashes

- Clear app cache
- Reinstall app
- Check for updates
- Report issue on GitHub

## Sync with Other ShareConnect Apps

SeafileConnect automatically syncs:

- **Themes**: Theme changes sync instantly
- **Profiles**: Server profiles shared
- **History**: File access history tracked
- **Bookmarks**: Favorite files synced
- **Preferences**: Settings synchronized

## Privacy & Security

- Credentials stored securely
- Database encrypted with SQLCipher
- HTTPS-only connections
- No data sent to third parties
- Local-only sync between your devices

## FAQ

**Q: What Seafile versions are supported?**  
A: Seafile 7.0 and later

**Q: Can I use multiple Seafile servers?**  
A: Yes, use ProfileSync to manage multiple profiles

**Q: Is offline mode supported?**  
A: Downloaded files available offline; upload queued until online

**Q: How do encrypted libraries work?**  
A: Client-side encryption with AES-256; server never sees your password

**Q: What file types are supported?**  
A: All file types; preview depends on file type

**Q: Can I share files?**  
A: Yes, if your server has sharing enabled

## Support & Feedback

- **GitHub**: https://github.com/vasic-digital/ShareConnect
- **Wiki**: https://deepwiki.com/vasic-digital/ShareConnect
- **Issues**: Report bugs on GitHub Issues

## Credits

SeafileConnect is part of the ShareConnect ecosystem.

- **Developer**: Milos Vasic
- **License**: See LICENSE file
- **Seafile**: https://www.seafile.com

---

**Version**: 1.0.0  
**Last Updated**: 2025-10-25
