# PortainerConnect User Manual

**Version 1.0.0**

Welcome to PortainerConnect - your mobile gateway to Docker container management through Portainer!

---

## Table of Contents

1. [Introduction](#introduction)
2. [Getting Started](#getting-started)
3. [Initial Setup](#initial-setup)
4. [Managing Containers](#managing-containers)
5. [Working with Images](#working-with-images)
6. [Volume Management](#volume-management)
7. [Network Management](#network-management)
8. [Multi-Endpoint Support](#multi-endpoint-support)
9. [Settings and Preferences](#settings-and-preferences)
10. [Troubleshooting](#troubleshooting)
11. [FAQ](#faq)
12. [Tips and Best Practices](#tips-and-best-practices)

---

## Introduction

### What is PortainerConnect?

PortainerConnect is an Android application that connects to your Portainer server, allowing you to manage Docker containers, images, volumes, and networks directly from your mobile device. It's part of the ShareConnect ecosystem, which means your settings, profiles, and preferences automatically sync across all ShareConnect apps.

### What is Portainer?

Portainer is a lightweight management UI for Docker, Docker Swarm, and Kubernetes. It simplifies container management with an intuitive web interface. PortainerConnect brings this power to your Android device.

### Key Features

- **Container Management**: Start, stop, restart, pause, and remove containers
- **Real-Time Monitoring**: View container stats, logs, and status
- **Resource Management**: Manage Docker images, volumes, and networks
- **Multi-Endpoint**: Connect to multiple Portainer instances
- **Sync Integration**: Settings sync across all ShareConnect apps
- **Secure**: Encrypted credentials and database storage
- **Offline Access**: View cached data when offline

### System Requirements

- **Android Version**: 9.0 (Pie) or higher
- **Storage**: Minimum 50MB free space
- **Network**: Wi-Fi or cellular data connection
- **Portainer**: Version 2.x running on accessible server

---

## Getting Started

### Installation

1. **Download the App**:
   - Install from Google Play Store (when available)
   - Or build from source (see technical documentation)

2. **Launch the App**:
   - Tap the PortainerConnect icon
   - Grant required permissions when prompted

3. **Complete Onboarding**:
   - Follow the welcome screens
   - Learn about key features
   - Set up your first Portainer server

### Understanding the Interface

When you first open PortainerConnect, you'll see a simple interface with:

- **App Title**: "PortainerConnect" prominently displayed
- **Description**: Brief explanation of the app's purpose
- **Status Message**: Current phase information (Phase 2.2)

**Note**: The full UI dashboard is coming in Phase 3. Currently, the app provides API connectivity and basic functionality.

---

## Initial Setup

### Adding Your First Portainer Server

#### Prerequisites

Before adding a server, ensure you have:
- Portainer installed and running
- Network access to Portainer server
- Valid username and password
- Portainer server URL (e.g., `http://192.168.1.100:9000`)

#### Step-by-Step Setup

1. **Prepare Portainer Server**:
   ```
   If Portainer is not installed, run:
   docker run -d -p 9000:9000 --name portainer \
     -v /var/run/docker.sock:/var/run/docker.sock \
     -v portainer_data:/data \
     portainer/portainer-ce:latest
   ```

2. **Find Your Server URL**:
   - Local network: `http://192.168.x.x:9000`
   - Remote server: `https://portainer.example.com`
   - With custom port: `http://server:9443`

3. **Create Portainer Account** (if needed):
   - Open Portainer in web browser
   - Navigate to your server URL
   - Create admin account
   - Remember username and password

4. **Add Server in PortainerConnect**:
   - Launch PortainerConnect
   - Navigate to Settings (when available)
   - Tap "Add Server"
   - Enter server details:
     - **Server Name**: e.g., "Home Server"
     - **Server URL**: e.g., `http://192.168.1.100:9000`
     - **Username**: Your Portainer username
     - **Password**: Your Portainer password
   - Tap "Test Connection"
   - If successful, tap "Save"

### Server Configuration Options

**Basic Settings:**
- **Server Name**: Friendly name for identification
- **Server URL**: Complete URL including protocol and port
- **Username**: Portainer account username
- **Password**: Portainer account password

**Advanced Settings:**
- **Connection Timeout**: Default 30 seconds
- **Auto-Refresh**: Enable automatic data refresh
- **Refresh Interval**: How often to refresh (30-300 seconds)
- **Enable Notifications**: Container event notifications
- **Default Endpoint**: Which endpoint to show by default

### Understanding Endpoints

Portainer can manage multiple Docker environments called "endpoints":

- **Type 1 - Docker**: Local or remote Docker daemon
- **Type 2 - Agent**: Portainer agent on remote host
- **Type 3 - Azure**: Azure Container Instances
- **Type 4 - Edge Agent**: Edge compute environments
- **Type 5 - Kubernetes Local**: Local Kubernetes cluster
- **Type 6 - Kubernetes Agent**: Remote Kubernetes via agent

PortainerConnect supports all endpoint types, though the current phase focuses on Docker endpoints.

---

## Managing Containers

### Viewing Containers

**Container List View:**
- Shows all containers (running and stopped)
- Each container displays:
  - Container name
  - Image name
  - Current state (running, exited, paused)
  - Status message
  - Port mappings
  - Created date

**Container States:**
- 🟢 **Running**: Container is active
- 🔴 **Exited**: Container has stopped
- 🟡 **Paused**: Container is paused
- 🔵 **Restarting**: Container is restarting
- ⚪ **Created**: Container exists but not started

### Starting a Container

1. **Locate Container**:
   - Browse container list
   - Find stopped container (exited state)

2. **Start Action**:
   - Tap on container
   - Select "Start" action
   - Confirm if prompted

3. **Verify**:
   - Wait for state to change to "running"
   - Check status message updates
   - View port mappings if applicable

**What Happens:**
- Container processes start
- Network connections established
- Ports are published
- Volumes are mounted

### Stopping a Container

1. **Select Container**:
   - Find running container
   - Tap to view details

2. **Stop Action**:
   - Select "Stop" option
   - Container receives SIGTERM signal
   - After timeout, receives SIGKILL

3. **Graceful Shutdown**:
   - Container has time to clean up
   - Saves state if configured
   - Releases resources

**Best Practice**: Always stop containers gracefully unless emergency requires force stop.

### Restarting a Container

**When to Restart:**
- After configuration changes
- To clear temporary issues
- After image updates
- Memory leak cleanup

**How to Restart:**
1. Select container
2. Choose "Restart" action
3. Wait for complete cycle
4. Verify container is running

**Process:**
- Container stops (graceful)
- Resources released
- Container starts fresh
- New process ID assigned

### Pausing and Resuming

**Pause a Container:**
- Freezes all container processes
- Maintains state in memory
- No CPU usage while paused
- Quick resume capability

**Use Cases:**
- Temporary resource saving
- Debugging purposes
- Waiting for external dependency
- Testing scenarios

**Resume (Unpause):**
- Restores exact state
- Processes continue where paused
- No data loss
- Fast operation

### Removing Containers

**Caution**: Removing a container deletes it permanently!

**Safe Removal:**
1. Stop container first
2. Verify no important data inside
3. Select "Remove" action
4. Confirm deletion

**Force Removal:**
- Removes running container
- No graceful shutdown
- Use only when necessary
- May lose unsaved data

**What Gets Deleted:**
- Container and its processes
- Container-specific data
- Log files
- Network connections

**What's Preserved:**
- Named volumes
- Images
- Networks
- Host-mounted data

### Viewing Container Details

**Container Information:**
- **ID**: Unique container identifier
- **Image**: Source image and tag
- **Command**: Entry point command
- **Created**: When container was created
- **State**: Current operational state
- **Status**: Human-readable status message

**Port Mappings:**
- **Private Port**: Container internal port
- **Public Port**: Host exposed port
- **Type**: TCP or UDP
- **IP**: Bound IP address

**Volume Mounts:**
- **Type**: bind, volume, or tmpfs
- **Source**: Host path or volume name
- **Destination**: Container path
- **Mode**: Read-only (ro) or read-write (rw)

**Network Settings:**
- **Networks**: Connected networks
- **IP Address**: Container IP
- **Gateway**: Network gateway
- **MAC Address**: Network interface MAC

### Container Logs

**Viewing Logs:**
1. Select container
2. Choose "View Logs"
3. Scroll through output
4. Use filters if needed

**Log Features:**
- Real-time streaming (future)
- Timestamp display
- Search and filter
- Export capability
- Color coding for errors

**Log Levels:**
- ERROR: Red
- WARN: Yellow
- INFO: White
- DEBUG: Gray

### Container Statistics

**Available Metrics:**

**CPU Usage:**
- Current CPU percentage
- Per-CPU breakdown
- User vs kernel time
- System CPU total

**Memory Usage:**
- Current memory used
- Memory limit
- Usage percentage
- Cache and buffers

**Network I/O:**
- Bytes received
- Bytes transmitted
- Packets in/out
- Errors and drops

**Refresh Rate:**
- Manual refresh
- Auto-refresh (configurable)
- Real-time streaming (future)

---

## Working with Images

### Viewing Images

**Image List:**
- All downloaded images
- Multiple tags per image
- Size information
- Creation date

**Image Details:**
- **ID**: Unique image identifier
- **Repository Tags**: All image tags
- **Size**: Compressed size
- **Created**: Build timestamp
- **Parent**: Base image
- **Labels**: Metadata labels

### Image Information

**Key Fields:**
- **RepoTags**: Like `nginx:latest`, `nginx:1.25`
- **Size**: Disk space used
- **Virtual Size**: Including parent layers
- **Shared Size**: Shared with other images
- **Containers**: Containers using this image

### Managing Images

**Future Features:**
- Pull new images
- Remove unused images
- Tag/untag images
- Search Docker Hub
- Build from Dockerfile
- Export/import images

---

## Volume Management

### Understanding Volumes

Volumes persist data outside containers:

**Volume Types:**
- **Named Volumes**: Created by Docker
- **Bind Mounts**: Host directory mounted
- **Tmpfs Mounts**: Temporary memory storage

**Volume Drivers:**
- **local**: Default driver
- **nfs**: Network file system
- **Custom**: Third-party drivers

### Viewing Volumes

**Volume List Shows:**
- Volume name
- Driver type
- Mountpoint path
- Creation date
- Scope (local/global)

**Volume Details:**
- Labels and metadata
- Driver options
- Connected containers
- Size (if available)

### Volume Best Practices

**Do:**
- Use named volumes for important data
- Regular backups of volume data
- Document volume purposes
- Clean up unused volumes

**Don't:**
- Store secrets in volumes
- Ignore volume growth
- Delete volumes without verification
- Use root-only permissions unnecessarily

---

## Network Management

### Docker Networks

**Network Types:**
- **bridge**: Default network type
- **host**: Use host networking
- **overlay**: Swarm multi-host networking
- **macvlan**: Assign MAC addresses
- **none**: No networking

### Viewing Networks

**Network Information:**
- Network name
- Network ID
- Scope (local/global/swarm)
- Driver type
- Subnet and gateway
- Connected containers

### Network Configuration

**IPAM (IP Address Management):**
- Subnet configuration
- IP range allocation
- Gateway settings
- Auxiliary addresses

**Network Options:**
- Enable IPv6
- Internal network
- Attachable (for standalone containers)
- Ingress (Swarm routing mesh)

---

## Multi-Endpoint Support

### Managing Multiple Endpoints

PortainerConnect supports multiple Portainer endpoints:

**Endpoint Types Supported:**
1. **Local Docker**: Socket connection
2. **Remote Docker**: TCP connection
3. **Docker Agent**: Portainer agent
4. **Kubernetes**: K8s clusters
5. **Azure**: ACI endpoints
6. **Edge**: Edge deployments

### Switching Between Endpoints

1. View endpoint list
2. Check endpoint status (up/down)
3. Select desired endpoint
4. View endpoint-specific resources

### Endpoint Status

**Status Indicators:**
- **Up (1)**: Endpoint is accessible
- **Down (2)**: Endpoint unreachable

**Health Checks:**
- Automatic ping
- Connection verification
- Resource availability
- Version compatibility

---

## Settings and Preferences

### App Settings

**General Settings:**
- **Theme**: Light, dark, or system
- **Language**: App display language
- **Refresh Interval**: Auto-refresh timing
- **Notifications**: Enable/disable alerts

**Advanced Settings:**
- **Connection Timeout**: Network timeout
- **Cache Duration**: Offline data retention
- **Log Level**: Debugging verbosity
- **Analytics**: Usage statistics (opt-in)

### Profile Management

PortainerConnect uses **ProfileSync** for server profiles:

**Profile Features:**
- **Auto-Sync**: Profiles sync across ShareConnect apps
- **Encryption**: Credentials stored securely
- **Default Profile**: Set preferred server
- **Multiple Profiles**: Manage many servers

**Managing Profiles:**
1. View all profiles
2. Edit profile details
3. Set default profile
4. Delete old profiles
5. Test connections

### ShareConnect Integration

**Sync Modules:**

1. **ThemeSync**: Shared app themes
2. **ProfileSync**: Server profiles
3. **HistorySync**: Action history
4. **BookmarkSync**: Favorite containers
5. **PreferencesSync**: App settings
6. **LanguageSync**: Language preferences
7. **RSSSync**: Container notifications
8. **TorrentSharingSync**: Ecosystem integration

**Benefits:**
- Consistent experience
- One-time configuration
- Automatic updates
- Cross-app features

---

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to Portainer server

**Solutions:**
1. **Verify Server URL**:
   - Correct protocol (http/https)
   - Correct IP address
   - Correct port number
   - Format: `http://192.168.1.100:9000`

2. **Check Network**:
   - Device on same network
   - Firewall not blocking
   - Portainer service running
   - Port 9000/9443 accessible

3. **Test Connectivity**:
   - Ping server IP
   - Access Portainer in browser
   - Check server logs
   - Verify Docker is running

### Authentication Problems

**Problem**: Login fails with 401 Unauthorized

**Solutions:**
1. **Verify Credentials**:
   - Correct username
   - Correct password
   - No extra spaces
   - Case sensitivity

2. **Check Portainer**:
   - Account exists
   - Account not locked
   - Password not expired
   - Permissions granted

3. **Token Issues**:
   - Clear app cache
   - Re-authenticate
   - Check token expiry (8 hours)

### Sync Not Working

**Problem**: Settings don't sync between apps

**Solutions:**
1. **Verify Installation**:
   - All ShareConnect apps installed
   - Apps up to date
   - Permissions granted

2. **Check Sync Managers**:
   - View logcat for errors
   - Check port conflicts
   - Restart all apps
   - Clear app data if needed

3. **Network Issues**:
   - Device has connectivity
   - No firewall blocking
   - Apps on same network (for local sync)

### Container Operations Fail

**Problem**: Cannot start/stop containers

**Solutions:**
1. **Check Permissions**:
   - User has endpoint access
   - Correct role assigned
   - Not restricted user

2. **Verify Container State**:
   - Container in valid state
   - No conflicting operations
   - Sufficient resources

3. **Docker Issues**:
   - Docker daemon running
   - No Docker errors
   - Endpoint accessible
   - Network functioning

### App Crashes

**Problem**: App crashes or freezes

**Solutions:**
1. **Basic Troubleshooting**:
   - Restart app
   - Clear app cache
   - Update to latest version
   - Restart device

2. **Resource Issues**:
   - Free up storage
   - Close other apps
   - Check available RAM
   - Reduce cache size

3. **Report Bug**:
   - Collect crash logs
   - Note reproduction steps
   - Share device info
   - Submit bug report

---

## FAQ

### General Questions

**Q: Is PortainerConnect free?**
A: Yes, PortainerConnect is free and open-source.

**Q: Do I need Portainer Pro?**
A: No, works with Portainer Community Edition (CE) and Pro.

**Q: Can I manage multiple servers?**
A: Yes, add unlimited Portainer servers.

**Q: Does it work offline?**
A: Limited. Cached data viewable, but operations require connection.

**Q: Is my data secure?**
A: Yes. Credentials encrypted, database encrypted, secure connections.

### Technical Questions

**Q: What ports does PortainerConnect use?**
A: Connects to Portainer on 9000 (HTTP) or 9443 (HTTPS). Uses ports 8890-8960 for ShareConnect sync.

**Q: Can I use HTTPS?**
A: Yes, use `https://` in server URL. Ensure valid SSL certificate.

**Q: How often does it sync?**
A: Real-time via Asinka. Profile changes sync immediately.

**Q: Can I use with Docker Swarm?**
A: Yes, through Portainer's Swarm support.

**Q: Does it support Kubernetes?**
A: Basic support. Full features in Phase 3.

### Container Management

**Q: Can I create new containers?**
A: Not yet. Feature planned for Phase 3.

**Q: How do I view container logs?**
A: Select container, choose "View Logs" (Phase 3).

**Q: Can I access container terminal?**
A: Not yet. Terminal access in Phase 3.

**Q: What about Docker Compose?**
A: View compose stacks. Deployment in Phase 3.

---

## Tips and Best Practices

### Performance Tips

1. **Optimize Refresh**:
   - Increase refresh interval
   - Disable auto-refresh when not needed
   - Use manual refresh for updates

2. **Reduce Data**:
   - Filter container lists
   - Archive old containers
   - Clean unused images
   - Limit history retention

3. **Network Efficiency**:
   - Use Wi-Fi when available
   - Enable request caching
   - Batch operations
   - Avoid frequent polling

### Security Best Practices

1. **Protect Credentials**:
   - Use strong passwords
   - Enable 2FA on Portainer (when available)
   - Don't share passwords
   - Rotate credentials regularly

2. **Secure Network**:
   - Use HTTPS when possible
   - VPN for remote access
   - Firewall rules
   - Private networks

3. **App Security**:
   - Keep app updated
   - Use device lock screen
   - Enable app lock (when available)
   - Review permissions

### Container Best Practices

1. **Naming Convention**:
   - Use descriptive names
   - Include version/purpose
   - Consistent format
   - Avoid special characters

2. **Resource Management**:
   - Set resource limits
   - Monitor usage
   - Clean up stopped containers
   - Remove unused images

3. **Data Management**:
   - Use volumes for persistence
   - Regular backups
   - Document mount points
   - Test restore procedures

### Maintenance

1. **Regular Tasks**:
   - Update app weekly
   - Review container states daily
   - Clean up monthly
   - Backup configurations

2. **Monitoring**:
   - Check resource usage
   - Review logs periodically
   - Monitor endpoint health
   - Track container restarts

3. **Troubleshooting**:
   - Keep logs enabled
   - Document issues
   - Test after changes
   - Maintain changelog

---

## Getting Help

### Support Resources

**Documentation:**
- This User Manual
- Technical Documentation (`PortainerConnect.md`)
- ShareConnect Wiki: https://deepwiki.com/vasic-digital/ShareConnect
- Portainer Docs: https://docs.portainer.io

**Community:**
- GitHub Issues (when available)
- ShareConnect Forums
- Portainer Community

**Logs and Debugging:**
```bash
# View app logs
adb logcat -s PortainerConnect:V

# Export logs
adb logcat -d > portainer-logs.txt
```

---

## Conclusion

Thank you for using PortainerConnect! This Phase 2.2 release provides the foundation for powerful mobile Docker management. Stay tuned for Phase 3 enhancements including:

- Complete dashboard UI
- Container creation and deployment
- Docker Compose support
- Real-time log streaming
- Terminal access
- Advanced monitoring

We appreciate your feedback and support!

---

**Manual Version**: 1.0.0
**Last Updated**: October 25, 2025
**Total Pages**: 15+
**Total Lines**: 650+

For technical support, visit the ShareConnect wiki or submit an issue on GitHub.

**Happy Container Managing! 🐳**
