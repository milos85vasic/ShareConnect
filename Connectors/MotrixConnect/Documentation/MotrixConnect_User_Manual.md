# MotrixConnect - User Manual

## What is MotrixConnect?

MotrixConnect is a ShareConnect application that connects your Android device to Motrix download managers. It provides powerful download management capabilities with support for multiple protocols, allowing you to manage downloads efficiently from your mobile device.

## Getting Started

### Installation

1. Install MotrixConnect APK on your Android device
2. Launch the app
3. Configure connection to your Motrix server

### First-Time Setup

1. **Server Configuration**:
   - Open Settings tab
   - Enter your Motrix server details (host, port, token)
   - Configure connection protocol (HTTP/HTTPS)
   - Test the connection

2. **Authentication**:
   - Enter API token from Motrix settings
   - Configure access credentials
   - Enable secure connection if available

3. **Security (Optional)**:
   - Enable PIN or biometric authentication
   - Configure in Settings > Security

## Features

### Download Management

Control your downloads with powerful features:

- **Active Downloads**: Monitor and control current downloads
- **Download Queue**: Manage waiting downloads
- **Completed Downloads**: View finished downloads
- **Failed Downloads**: Retry or manage failed attempts
- **Download Priorities**: Set download priorities and speeds

### Protocol Support

MotrixConnect supports multiple download protocols:

- **HTTP/HTTPS**: Standard web downloads
- **FTP/SFTP**: File transfer protocol downloads
- **BitTorrent**: Torrent file and magnet link support
- **Metalink**: Multi-source download support

### Download Controls

Fine-tune your downloads:

- **Start/Pause**: Control individual downloads
- **Resume**: Continue interrupted downloads
- **Cancel**: Stop and remove downloads
- **Retry**: Attempt failed downloads again
- **Priority**: Set high/normal/low priority

### Speed Management

Optimize download speeds:

- **Global Speed Limits**: Set overall download/upload limits
- **Per-Download Limits**: Control individual download speeds
- **Connection Limits**: Manage number of connections
- **Bandwidth Scheduling**: Time-based speed controls

## Advanced Features

### Multi-Server Support

Manage multiple Motrix instances:

1. **Add Servers**: Configure multiple Motrix servers
2. **Server Switching**: Easily switch between servers
3. **Combined Views**: See all downloads in one interface
4. **Server-Specific Settings**: Customize per server

### Advanced Download Options

Configure sophisticated download settings:

- **Connection Settings**: Optimize connection parameters
- **Retry Logic**: Configure retry attempts and intervals
- **User-Agent**: Custom user-agent strings
- **Referrer**: Set referrer information
- **Cookies**: Manage cookie settings

### Batch Operations

Handle multiple downloads efficiently:

- **Bulk Actions**: Start, pause, or cancel multiple downloads
- **Batch Addition**: Add multiple URLs simultaneously
- **Template Downloads**: Use download templates
- **Queue Management**: Organize download queues

### Scheduling and Automation

Automate download management:

- **Download Scheduling**: Schedule downloads for specific times
- **Auto-Start**: Automatically start queued downloads
- **Completion Actions**: Actions after downloads finish
- **Bandwidth Scheduling**: Automatic speed adjustments

## Settings and Configuration

### Connection Settings

- **Server Host**: Configure Motrix server address
- **Port Configuration**: Set connection port (default: 16800)
- **Protocol**: Choose HTTP or HTTPS
- **Authentication**: Manage API tokens and credentials
- **Connection Timeout**: Configure timeout settings

### Download Settings

- **Default Download Path**: Set default save location
- **Max Connections**: Limit concurrent connections
- **Speed Limits**: Configure global speed restrictions
- **Retry Settings**: Configure retry attempts and delays
- **User-Agent**: Set custom user-agent strings

### Interface Settings

- **Theme Selection**: Light, dark, or system theme
- **View Options**: List, grid, or detailed views
- **Sorting**: Customize download sorting
- **Filters**: Filter downloads by status, type, or date
- **Notifications**: Configure download notifications

### Privacy and Security

- **Authentication**: PIN, password, or biometric protection
- **Session Management**: Automatic timeout settings
- **Secure Connections**: Enforce HTTPS connections
- **Data Encryption**: Encrypt sensitive download data
- **Privacy Controls**: Manage data sharing preferences

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to Motrix server
**Solutions**:
- Verify server host and port settings
- Check if Motrix is running and accessible
- Verify API token is correct and valid
- Ensure firewall allows connections
- Test connection with other tools

**Problem**: Authentication fails
**Solutions**:
- Verify API token is correctly configured
- Check if token has expired
- Ensure proper authentication headers
- Verify user permissions on server
- Regenerate API token if necessary

### Download Issues

**Problem**: Downloads won't start
**Solutions**:
- Check available disk space
- Verify download URL accessibility
- Check server download limits
- Review connection settings
- Check for server-side restrictions

**Problem**: Slow download speeds
**Solutions**:
- Increase connection limits
- Adjust speed limit settings
- Check network connectivity
- Verify server performance
- Optimize connection parameters

**Problem**: Downloads fail repeatedly
**Solutions**:
- Increase retry attempts
- Adjust retry intervals
- Check source availability
- Verify URL format and validity
- Review server error logs

### Performance Issues

**Problem**: App becomes unresponsive
**Solutions**:
- Reduce number of displayed downloads
- Clear completed download history
- Optimize connection settings
- Check server resource usage
- Restart app and server

**Problem**: High battery usage
**Solutions**:
- Reduce update frequency
- Enable battery optimization
- Limit background operations
- Adjust sync intervals
- Use dark theme

## FAQ

### Q: Do I need Motrix installed to use MotrixConnect?
**A**: Yes, MotrixConnect is a client app that connects to a running Motrix server.

### Q: What download protocols are supported?
**A**: HTTP/HTTPS, FTP/SFTP, BitTorrent, and Metalink protocols are supported.

### Q: Can I use MotrixConnect with multiple Motrix servers?
**A**: Yes, MotrixConnect supports multiple server connections simultaneously.

### Q: How secure is my download data?
**A**: Very secure - all communications are encrypted, and authentication is handled through secure API tokens.

### Q: What file types can I download?
**A**: MotrixConnect supports all file types that your Motrix server can handle.

### Q: Can I schedule downloads for specific times?
**A**: Yes, MotrixConnect supports advanced scheduling features for automated download management.

### Q: Does MotrixConnect work with BitTorrent?
**A**: Yes, full BitTorrent support including magnet links and torrent files is available.

### Q: How many concurrent downloads can I manage?
**A**: This depends on your Motrix server configuration, but typically 5-10 concurrent downloads are supported.

### Q: Can I control download speeds?
**A**: Yes, you can set global and per-download speed limits, plus schedule bandwidth usage.

### Q: What happens if my connection is interrupted?
**A**: Downloads can be resumed automatically when connection is restored, depending on server settings.

## Advanced Configuration

### Aria2 Integration

MotrixConnect leverages Aria2 for download management:

- **JSON-RPC API**: Full API access to Aria2 features
- **Event Notifications**: Real-time download status updates
- **Method Support**: Complete Aria2 method implementation
- **Extension Support**: Custom Aria2 extensions and plugins

### Protocol-Specific Settings

Fine-tune protocol behavior:

- **HTTP/HTTPS**: Custom headers, cookies, referrers
- **FTP/SFTP**: Passive/active mode, encryption settings
- **BitTorrent**: Tracker settings, DHT configuration
- **Metalink**: Mirror selection, checksum verification

### Advanced Server Configuration

Support for complex server setups:

- **Reverse Proxies**: Behind proxy configurations
- **Load Balancing**: Multiple server instances
- **Custom Ports**: Non-standard port configurations
- **Authentication**: Multiple authentication methods

### Automation and Scripting

Advanced automation capabilities:

- **Download Rules**: Automatic categorization
- **Event Scripts**: Trigger actions on events
- **Batch Processing**: Mass operations
- **API Integration**: External system integration

## Performance Optimization

### Connection Optimization

Maximize download performance:

- **Connection Pooling**: Efficient connection reuse
- **DNS Optimization**: Fast DNS resolution
- **Keep-Alive**: Persistent connections
- **Compression**: Data compression support

### Download Optimization

Optimize download efficiency:

- **Multi-Connection Downloads**: Parallel chunk downloading
- **Intelligent Retry**: Adaptive retry strategies
- **Bandwidth Management**: Dynamic bandwidth allocation
- **Protocol Selection**: Optimal protocol choice

### Resource Management

Efficient resource usage:

- **Memory Management**: Optimized memory usage
- **CPU Optimization**: Efficient processing
- **Battery Optimization**: Power-efficient operations
- **Storage Management**: Intelligent caching

## Monitoring and Analytics

### Download Statistics

Track download performance:

- **Speed Metrics**: Download/upload speeds
- **Success Rates**: Download completion statistics
- **Time Analysis**: Download duration tracking
- **Usage Patterns**: Download behavior analysis

### Server Monitoring

Monitor server health:

- **Connection Status**: Server connectivity monitoring
- **Performance Metrics**: Server response times
- **Error Tracking**: Error frequency and types
- **Resource Usage**: Server resource utilization

### Network Analysis

Analyze network performance:

- **Connection Quality**: Network stability assessment
- **Speed Tests**: Network performance testing
- **Latency Measurement**: Response time analysis
- **Bandwidth Utilization**: Network usage optimization

## Integration with ShareConnect Ecosystem

### Cross-App Functionality

Seamless integration with other ShareConnect apps:

- **Download Sharing**: Share downloads with other apps
- **Profile Sync**: Settings synchronized across devices
- **History Sync**: Download history shared across apps
- **Theme Sync**: Consistent appearance with other ShareConnect apps

### Unified Management

Manage downloads from anywhere:

- **Remote Control**: Control downloads from other ShareConnect apps
- **Status Sharing**: Share download status across ecosystem
- **Resource Sharing**: Share bandwidth and connections
- **Notification Sync**: Unified notification system

## Getting Help

### In-App Help

- Access help through Settings > Help & Support
- Browse FAQ section for common issues
- Check connection diagnostics
- View download logs and statistics

### Online Resources

- **Documentation**: Visit the ShareConnect documentation site
- **Community Forums**: Join discussions with other users
- **Video Tutorials**: Watch setup and usage guides
- **Knowledge Base**: Search for solutions to common problems

### Technical Support

For technical issues:
1. Check troubleshooting section first
2. Gather relevant information (server logs, error messages)
3. Test connection using other tools
4. Contact support through official channels
5. Provide detailed problem description and configuration

## Privacy and Data Usage

### Data Collection

MotrixConnect collects minimal data:
- Connection settings (server details, credentials)
- Download preferences and settings
- Download statistics and history
- App usage statistics (optional)

### Data Storage

- **Local Storage**: Settings and cached data stored locally
- **Server Communication**: Direct connection to your Motrix server
- **No Third-Party Sharing**: Data not shared with external services
- **Encrypted Connections**: Secure communication protocols

### Security Measures

- **Encrypted Connections**: HTTPS support for secure communication
- **Local Authentication**: PIN/password protection available
- **Session Management**: Automatic timeout for security
- **Data Encryption**: Sensitive data encrypted at rest
- **Secure Authentication**: API token-based authentication

---

## Version Information

**MotrixConnect Version**: 1.0.0  
**Last Updated**: December 2025  
**Manual Version**: 1.0  

For the latest version of this manual, visit the ShareConnect documentation site.