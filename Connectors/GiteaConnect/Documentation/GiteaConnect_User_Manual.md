# GiteaConnect - User Manual

## What is GiteaConnect?

GiteaConnect is a ShareConnect application that connects your Android device to Gitea Git servers. It provides comprehensive repository management, issue tracking, and collaborative development features, allowing you to manage your code projects directly from your mobile device.

## Getting Started

### Installation

1. Install GiteaConnect APK on your Android device
2. Launch the app
3. Configure your Gitea server connection

### First-Time Setup

1. **Server Configuration**:
   - Open Settings tab
   - Enter your Gitea server URL (e.g., https://git.example.com)
   - Enter your Gitea username
   - Enter your Gitea password or access token
   - Test the connection

2. **Authentication Options**:
   - **Username/Password**: Traditional authentication
   - **Access Token**: More secure, recommended for automation
   - **SSH Keys**: For Git operations (configured separately)
   - **Two-Factor Authentication**: If enabled on your server

3. **Security (Optional)**:
   - Enable PIN or biometric authentication
   - Configure in Settings > Security

## Features

### Repository Management

Browse and manage your Git repositories:

- **Repository List**: View all accessible repositories
- **Repository Details**: See description, language, stars, forks
- **Repository Types**: Personal, organization, public, private
- **Repository Statistics**: Commits, contributors, activity
- **Repository Search**: Find repositories by name or description

### Issues and Bug Tracking

Manage project issues effectively:

- **Issue List**: View open, closed, and all issues
- **Issue Creation**: Create new issues with titles and descriptions
- **Issue Labels**: Categorize issues with labels
- **Issue Assignees**: Assign issues to team members
- **Issue Milestones**: Track issues by project milestones
- **Issue Comments**: Collaborate through issue comments

### Pull Request Management

Handle code reviews and merges:

- **Pull Request List**: View open, closed, and merged PRs
- **PR Creation**: Create pull requests between branches
- **PR Reviews**: Review code changes and provide feedback
- **PR Comments**: Discuss changes with team members
- **PR Approval**: Approve or request changes on PRs
- **Merge Operations**: Merge approved pull requests

### Code Exploration

Browse and understand code:

- **File Browser**: Navigate repository file structure
- **File Content**: View file contents with syntax highlighting
- **Commit History**: Browse commit history and changes
- **Branch Management**: View and switch between branches
- **Tag Management**: View repository tags and releases
- **Diff Views**: See changes between commits or branches

### User and Organization Management

Manage users and organizations:

- **User Profiles**: View user information and activity
- **Organization Management**: Browse organization details
- **Team Management**: View organization teams and members
- **Collaborator Management**: Manage repository collaborators
- **Permission Levels**: Control access levels and permissions

## Advanced Features

### Multi-Server Support

Connect to multiple Gitea instances:

1. **Add Servers**: Configure multiple Gitea servers
2. **Server Switching**: Quickly switch between servers
3. **Combined Views**: View activity across all servers
4. **Server-Specific Settings**: Customize per server

### Advanced Repository Operations

Perform sophisticated repository operations:

- **Repository Creation**: Create new repositories with templates
- **Repository Forking**: Fork existing repositories
- **Repository Mirroring**: Set up repository mirrors
- **Repository Transfer**: Transfer repositories between users/orgs
- **Repository Archiving**: Archive inactive repositories

### Git Operations

Basic Git operations support:

- **Branch Creation**: Create new branches
- **Branch Comparison**: Compare branches and see differences
- **Tag Creation**: Create tags for releases
- **Merge Conflict Detection**: Identify potential conflicts
- **Release Management**: Manage releases and changelogs

### Webhook Integration

Monitor repository activity:

- **Activity Feed**: Real-time repository activity
- **Webhook Events**: Receive notifications for events
- **Push Notifications**: Mobile notifications for important events
- **Event Filtering**: Filter activity by type or repository

## Settings and Configuration

### Connection Settings

- **Server URL**: Configure Gitea server address
- **Authentication**: Manage login credentials and tokens
- **Protocol**: Choose HTTP or HTTPS connections
- **Connection Test**: Verify server connectivity
- **SSL/TLS**: Configure secure connection options

### Display Options

- **Theme Selection**: Light, dark, or system theme
- **Repository View**: Customize repository display
- **Code Highlighting**: Syntax highlighting themes
- **Diff Settings**: Configure diff display options
- **Language**: Interface language selection

### Notification Settings

- **Push Notifications**: Configure mobile notifications
- **Email Notifications**: Email notification preferences
- **Event Types**: Choose which events to notify about
- **Notification Filters**: Filter notifications by repository
- **Quiet Hours**: Set do-not-disturb periods

### Privacy and Security

- **Authentication**: PIN, password, or biometric protection
- **Session Management**: Automatic timeout settings
- **Token Management**: Manage API access tokens
- **Activity Privacy**: Control activity visibility
- **Data Encryption**: Encrypt sensitive data

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to Gitea server
**Solutions**:
- Verify server URL (include https:// if SSL enabled)
- Check username and password/token
- Ensure server allows API access
- Verify firewall settings
- Check if server requires specific API version

**Problem**: Authentication fails
**Solutions**:
- Verify credentials are correct
- Check if access token has expired
- Ensure user has necessary permissions
- Try regenerating access token
- Check server authentication settings

### Repository Access Issues

**Problem**: Cannot see repositories
**Solutions**:
- Check user permissions on repositories
- Verify repository visibility settings
- Ensure proper organization membership
- Check if repositories are archived
- Verify server accessibility

**Problem**: Cannot create issues or PRs
**Solutions**:
- Check repository permissions
- Verify user has write access
- Ensure repository isn't archived
- Check if branch protection is enabled
- Verify issue/PR creation limits

### Performance Issues

**Problem**: Slow loading or timeouts
**Solutions**:
- Check server hardware resources
- Optimize network connection
- Reduce displayed items per page
- Clear app cache and data
- Check for server-side rate limiting

**Problem**: High data usage
**Solutions**:
- Enable data saver mode
- Reduce content preview quality
- Limit background sync
- Disable auto-refresh
- Use Wi-Fi when possible

## FAQ

### Q: Do I need a Gitea account to use GiteaConnect?
**A**: Yes, you need access to a Gitea server with a valid user account.

### Q: Can I use GiteaConnect with multiple servers?
**A**: Yes, GiteaConnect supports multiple Gitea server connections simultaneously.

### Q: Does GiteaConnect support Git operations like push/pull?
**A**: GiteaConnect focuses on repository management and collaboration. For Git operations, use dedicated Git clients.

### Q: How secure is my repository data?
**A**: Very secure - all communications are encrypted, and authentication uses secure tokens.

### Q: What repository permissions do I need?
**A**: Minimum read access for viewing, write access for creating issues/PRs, admin access for repository management.

### Q: Can I manage private repositories?
**A**: Yes, if you have appropriate permissions on the private repositories.

### Q: Does GiteaConnect support GitHub or GitLab?
**A**: No, GiteaConnect is specifically designed for Gitea servers. Other ShareConnect apps handle different platforms.

### Q: How often does GiteaConnect sync with the server?
**A**: Sync frequency can be configured in settings, with options for real-time, periodic, or manual sync.

### Q: Can I use GiteaConnect offline?
**A**: Some cached information is available offline, but full functionality requires server connectivity.

### Q: What Gitea versions are supported?
**A**: GiteaConnect supports Gitea 1.11+ with full API compatibility.

## Advanced Configuration

### API Integration

GiteaConnect leverages Gitea's comprehensive API:

- **REST API**: Full REST API v1 implementation
- **GraphQL**: GraphQL API support (where available)
- **Webhook API**: Webhook event handling
- **Extension Support**: Custom API extensions

### Repository Templates

Use repository templates for consistency:

- **Template Repositories**: Create from templates
- **License Templates**: Pre-defined license options
- **Gitignore Templates**: Language-specific gitignore files
- **Issue Templates**: Structured issue creation
- **PR Templates**: Standardized pull request formats

### Advanced Git Features

Access advanced Git functionality:

- **Commit Signing**: GPG-signed commits
- **Force Push Protection**: Prevent accidental force pushes
- **Branch Protection Rules**: Configure protection policies
- **Merge Strategies**: Choose merge methods
- **Squash and Rebase**: Advanced merge options

### Custom Integrations

Integrate with external tools:

- **CI/CD Integration**: Connect with continuous integration
- **Issue Tracker Integration**: Link to external issue trackers
- **Notification Services**: Integrate with notification platforms
- **Analytics Integration**: Connect with analytics services

### Enterprise Features

Support for enterprise Gitea deployments:

- **LDAP Integration**: Enterprise authentication
- **SAML Support**: Single sign-on capabilities
- **Audit Logging**: Comprehensive activity logs
- **Compliance Features**: Enterprise compliance tools
- **Role-Based Access**: Advanced permission systems

## Performance Optimization

### Repository Performance

Optimize repository access:

- **Caching Strategies**: Intelligent content caching
- **Pagination**: Efficient large dataset handling
- **Lazy Loading**: On-demand content loading
- **Background Sync**: Non-blocking synchronization

### Network Optimization

Maximize connection efficiency:

- **Connection Pooling**: Efficient connection reuse
- **Compression**: Data compression for faster transfers
- **CDN Support**: Content delivery network integration
- **Geographic Optimization**: Server proximity selection

### Mobile Optimization

Optimized for mobile experience:

- **Responsive Design**: Adaptive interface for all screen sizes
- **Touch Optimization**: Touch-friendly interactions
- **Battery Optimization**: Power-efficient operations
- **Data Usage**: Efficient data consumption

## Monitoring and Analytics

### Repository Analytics

Track repository performance:

- **Commit Activity**: Commit frequency and patterns
- **Contributor Statistics**: Contributor activity analysis
- **Issue Metrics**: Issue resolution time and trends
- **PR Analytics**: Pull request processing statistics

### User Activity

Monitor user engagement:

- **Activity Timeline**: User activity visualization
- **Contribution Graphs**: Contribution pattern displays
- **Engagement Metrics**: User interaction tracking
- **Performance Indicators**: Key performance metrics

### System Health

Monitor system status:

- **Server Status**: Server health monitoring
- **API Rate Limits**: Rate limit tracking and management
- **Error Tracking**: Error frequency and analysis
- **Performance Monitoring**: System performance metrics

## Integration with ShareConnect Ecosystem

### Cross-App Collaboration

Work seamlessly with other ShareConnect apps:

- **File Sharing**: Share repository files with other apps
- **Download Integration**: Download repository contents
- **Notification Sync**: Unified notification system
- **Activity Sharing**: Share development activity

### Unified Management

Manage development workflow:

- **Project Management**: Coordinate across multiple services
- **Resource Sharing**: Share development resources
- **Status Updates**: Unified status reporting
- **Collaboration Tools**: Cross-platform collaboration

### Ecosystem Benefits

Leverage ShareConnect advantages:

- **Profile Sync**: Consistent user profiles across apps
- **Theme Sync**: Unified appearance and branding
- **History Sync**: Shared activity history
- **Security Sync**: Consistent security policies

## Getting Help

### In-App Help

- Access help through Settings > Help & Support
- Browse FAQ section for common issues
- Check connection diagnostics
- View activity logs and statistics

### Online Resources

- **Documentation**: Visit the ShareConnect documentation site
- **Community Forums**: Join discussions with other users
- **Video Tutorials**: Watch setup and usage guides
- **Knowledge Base**: Search for solutions to common problems
- **Developer Resources**: API documentation and guides

### Technical Support

For technical issues:
1. Check troubleshooting section first
2. Gather relevant information (server logs, error messages)
3. Test connection using other API clients
4. Contact support through official channels
5. Provide detailed problem description and configuration

## Privacy and Data Usage

### Data Collection

GiteaConnect collects minimal data:
- Connection settings (server URLs, credentials)
- Repository preferences and settings
- Activity data and interaction history
- App usage statistics (optional)

### Data Storage

- **Local Storage**: Settings and cached data stored locally
- **Server Communication**: Direct connection to your Gitea server
- **No Third-Party Sharing**: Data not shared with external services
- **Encrypted Connections**: Secure communication protocols

### Security Measures

- **Encrypted Connections**: HTTPS support for secure communication
- **Local Authentication**: PIN/password protection available
- **Session Management**: Automatic timeout for security
- **Token-Based Authentication**: Secure API token usage
- **Data Encryption**: Sensitive data encrypted at rest

---

## Version Information

**GiteaConnect Version**: 1.0.0  
**Last Updated**: December 2025  
**Manual Version**: 1.0  

For the latest version of this manual, visit the ShareConnect documentation site.