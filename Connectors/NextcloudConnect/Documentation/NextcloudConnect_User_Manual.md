# NextcloudConnect - User Manual

## What is NextcloudConnect?

NextcloudConnect is a ShareConnect application that connects your Android device to Nextcloud servers. It provides secure access to your files, folders, and collaborative features, allowing you to manage your cloud storage directly from your mobile device.

## Getting Started

### Installation

1. Install NextcloudConnect APK on your Android device
2. Launch the app
3. Configure your Nextcloud server connection

### First-Time Setup

1. **Server Configuration**:
   - Open Settings tab
   - Enter your Nextcloud server URL (e.g., https://cloud.example.com)
   - Enter your Nextcloud username
   - Enter your Nextcloud password or app password
   - Test the connection

2. **Two-Factor Authentication**:
   - If enabled, generate an app password in Nextcloud
   - Use app password instead of regular password
   - App passwords can be created in Nextcloud Settings > Security

3. **Security (Optional)**:
   - Enable PIN or biometric authentication
   - Configure in Settings > Security

## Features

### Files Tab

Browse and manage your Nextcloud files:

- **File Browser**: Navigate folders with familiar file manager interface
- **File Operations**: Upload, download, move, copy, rename, delete
- **File Preview**: View documents, images, and videos directly
- **Search**: Find files quickly with search functionality
- **Sorting**: Sort by name, date, size, or type

### Upload Features

Upload content to your Nextcloud:

- **Camera Upload**: Automatically backup photos and videos
- **Manual Upload**: Select files from device storage
- **Share Integration**: Upload from any app using Share menu
- **Multiple Files**: Upload multiple files simultaneously
- **Resume Support**: Continue interrupted uploads

### Download Features

Download files from Nextcloud:

- **Individual Downloads**: Download specific files
- **Folder Downloads**: Download entire folders as ZIP
- **Background Downloads**: Continue downloads while using other apps
- **Download Manager**: Track and manage active downloads

### Sharing Capabilities

Share files and folders with others:

- **Public Links**: Create shareable links for files/folders
- **Link Options**: Set passwords, expiration dates, permissions
- **User Sharing**: Share directly with Nextcloud users
- **Group Sharing**: Share with Nextcloud groups
- **Email Sharing**: Send share links via email

### Offline Access

Access content when offline:

- **Favorite Files**: Mark files for offline access
- **Cached Content**: Recently accessed files available offline
- **Sync Status**: Visual indicators for synced content
- **Manual Sync**: Force synchronization when needed

## Advanced Features

### Multi-Account Support

Manage multiple Nextcloud accounts:

1. **Add Accounts**: Configure multiple Nextcloud servers
2. **Account Switching**: Quickly switch between accounts
3. **Account-Specific Settings**: Customize settings per account
4. **Unified View**: Browse all accounts in single interface

### File Versioning

Access file version history:

- **Version List**: View all versions of a file
- **Version Comparison**: Compare changes between versions
- **Restore Versions**: Revert to previous file versions
- **Version Details**: See modification dates and sizes

### Activity Feed

Stay updated with recent changes:

- **Recent Activity**: See what's been added, modified, or deleted
- **User Activity**: Track who made changes
- **File Activity**: Monitor specific file changes
- **Activity Filters**: Filter by type, user, or date

### Comments and Collaboration

Collaborate on files:

- **File Comments**: Add comments to files and folders
- **Comment Threads**: Reply to and discuss files
- **Mentions**: Tag other users in comments
- **Notifications**: Get notified of new comments

## Settings and Configuration

### Connection Settings

- **Server URL**: Configure Nextcloud server address
- **Authentication**: Manage login credentials
- **Connection Test**: Verify server connectivity
- **SSL/TLS**: Configure secure connection options
- **Proxy Settings**: Configure proxy if needed

### Sync Settings

- **Auto-Sync**: Automatically sync changes
- **Sync Frequency**: Control how often sync occurs
- **Sync on Wi-Fi Only**: Conserve mobile data
- **Selective Sync**: Choose which folders to sync
- **Conflict Resolution**: Handle sync conflicts

### Display Options

- **Theme Selection**: Light, dark, or system theme
- **File View**: Grid or list view options
- **Sorting**: Customize file sorting preferences
- **Thumbnails**: Show/hide file preview thumbnails
- **Hidden Files**: Show/hide hidden files and folders

### Privacy and Security

- **Authentication**: PIN, password, or biometric protection
- **Session Management**: Automatic timeout settings
- **Data Encryption**: End-to-end encryption support
- **Secure Connections**: Enforce HTTPS connections
- **Privacy Settings**: Control data sharing preferences

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to Nextcloud server
**Solutions**:
- Verify server URL (include https:// if SSL enabled)
- Check username and password
- Ensure server allows external connections
- Verify firewall settings
- Check if two-factor authentication is enabled

**Problem**: SSL certificate errors
**Solutions**:
- Install valid SSL certificate on server
- Add certificate to device trust store
- Temporarily allow self-signed certificates (not recommended)
- Contact server administrator

### File Sync Issues

**Problem**: Files not syncing
**Solutions**:
- Check available storage space
- Verify file permissions on server
- Ensure files aren't locked by other users
- Check network connectivity
- Force manual sync

**Problem**: Sync conflicts
**Solutions**:
- Review conflict resolution settings
- Manually resolve file conflicts
- Check file versions and timestamps
- Communicate with other users sharing files

### Performance Issues

**Problem**: Slow loading or timeouts
**Solutions**:
- Check server hardware resources
- Optimize network connection
- Reduce sync frequency
- Clear app cache and data
- Limit number of synced folders

**Problem**: High data usage
**Solutions**:
- Enable sync on Wi-Fi only
- Reduce thumbnail quality
- Limit background sync
- Disable auto-upload features
- Use selective sync

## FAQ

### Q: Do I need a Nextcloud account to use NextcloudConnect?
**A**: Yes, you need access to a Nextcloud server with a valid user account.

### Q: Can I use NextcloudConnect with multiple servers?
**A**: Yes, NextcloudConnect supports multiple Nextcloud accounts simultaneously.

### Q: Does NextcloudConnect support encrypted files?
**A**: Yes, NextcloudConnect supports server-side encryption if enabled on your Nextcloud server.

### Q: Can I edit documents directly in NextcloudConnect?
**A**: NextcloudConnect focuses on file management. For editing, it can open files in compatible apps.

### Q: How secure is my data with NextcloudConnect?
**A**: Very secure - all communications are encrypted, and authentication is handled through Nextcloud's secure system.

### Q: What file types can I upload?
**A**: NextcloudConnect supports all file types that your Nextcloud server can handle.

### Q: Can I share files with non-Nextcloud users?
**A**: Yes, you can create public share links that anyone can access without a Nextcloud account.

### Q: How much storage space do I need on my device?
**A**: This depends on your usage. You can control cache size and enable selective sync to manage storage.

### Q: Does NextcloudConnect work offline?
**A**: Favorited files and cached content are available offline. Full functionality requires connectivity.

### Q: Can I integrate NextcloudConnect with other apps?
**A**: Yes, through the Share menu and ShareConnect's sync ecosystem integration.

## Advanced Configuration

### WebDAV Integration

NextcloudConnect uses WebDAV for file operations:

- **Standard Compliance**: Full WebDAV protocol support
- **Custom Properties**: Extended metadata support
- **Batch Operations**: Efficient multi-file operations
- **Resume Support**: Interrupted transfer recovery

### OCS API Features

Access Nextcloud's OCS (Open Collaboration Services) API:

- **User Management**: Profile and user information
- **Sharing API**: Advanced sharing capabilities
- **Activity API**: Detailed activity tracking
- **Comments API**: Collaboration features

### Custom Server Configurations

Support for custom Nextcloud setups:

- **Subdirectory Installations**: Servers installed in subdirectories
- **Reverse Proxies**: Behind proxy configurations
- **Custom Ports**: Non-standard port configurations
- **Multi-domain Setups**: Complex server arrangements

## Getting Help

### In-App Help

- Access help through Settings > Help & Support
- Browse FAQ section for common issues
- Check connection diagnostics
- View sync status and logs

### Online Resources

- **Documentation**: Visit the ShareConnect documentation site
- **Community Forums**: Join discussions with other users
- **Video Tutorials**: Watch setup and usage guides
- **Knowledge Base**: Search for solutions to common problems

### Technical Support

For technical issues:
1. Check troubleshooting section first
2. Gather relevant information (server logs, error messages)
3. Test connection using other WebDAV clients
4. Contact support through official channels
5. Provide detailed problem description and server info

## Privacy and Data Usage

### Data Collection

NextcloudConnect collects minimal data:
- Connection settings (server URLs, credentials)
- Sync preferences and file metadata
- App usage statistics (optional)
- Local cache and favorites

### Data Storage

- **Local Storage**: Settings and cached data stored locally
- **Server Communication**: Direct connection to your Nextcloud server
- **No Third-Party Sharing**: Data not shared with external services
- **End-to-End Encryption**: Optional client-side encryption

### Security Measures

- **Encrypted Connections**: HTTPS support for secure communication
- **Local Authentication**: PIN/password protection available
- **Session Management**: Automatic timeout for security
- **Data Encryption**: Sensitive data encrypted at rest
- **Secure Authentication**: Modern authentication methods

## Advanced Topics

### Command Line Interface

For advanced users and automation:

- **ADB Commands**: Control app via Android Debug Bridge
- **Intent Actions**: Launch specific app functions
- **Automated Sync**: Script-based synchronization
- **Bulk Operations**: Mass file operations

### Developer Integration

For developers building on NextcloudConnect:

- **API Access**: Programmatic access to Nextcloud features
- **Intent Filters**: Deep linking and app integration
- **Content Providers**: Share data with other apps
- **Background Services**: Automated operations

---

## Version Information

**NextcloudConnect Version**: 1.0.0  
**Last Updated**: December 2025  
**Manual Version**: 1.0  

For the latest version of this manual, visit the ShareConnect documentation site.