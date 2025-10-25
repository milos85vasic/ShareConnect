# NetdataConnect User Manual

## Welcome to NetdataConnect

NetdataConnect brings powerful system monitoring to your Android device. Monitor your servers running Netdata in real-time with a modern, intuitive interface.

**Version**: 1.0.0
**Platform**: Android 9.0 (API 28) and above

---

## Table of Contents

1. [Getting Started](#getting-started)
2. [First Launch](#first-launch)
3. [Adding Servers](#adding-servers)
4. [Monitoring Metrics](#monitoring-metrics)
5. [Managing Alarms](#managing-alarms)
6. [Using Widgets](#using-widgets)
7. [Security Settings](#security-settings)
8. [Synchronization](#synchronization)
9. [Troubleshooting](#troubleshooting)
10. [FAQ](#faq)

---

## Getting Started

### What is Netdata?

Netdata is a powerful monitoring system that tracks your server's performance in real-time. It monitors:

- **CPU usage** - How busy your processor is
- **Memory usage** - RAM and swap utilization
- **Disk I/O** - Read/write speeds
- **Network traffic** - Bandwidth usage
- **Applications** - Running services and processes
- **And much more** - 1000+ metrics available

### What is NetdataConnect?

NetdataConnect is an Android app that lets you:
- Monitor multiple Netdata servers from your phone or tablet
- View real-time system metrics with beautiful charts
- Receive alerts when something goes wrong
- Access monitoring data on the go
- Share server profiles across ShareConnect apps

### Requirements

**Server Side:**
- Netdata installed and running (https://netdata.cloud)
- Network access to Netdata server (default port: 19999)
- Optional: Authentication configured for secure access

**Android Device:**
- Android 9.0 (Pie) or newer
- Internet/network connectivity
- Recommended: 4GB RAM or more for smooth performance

---

## First Launch

### Initial Setup

1. **Install NetdataConnect** from your app store or APK
2. **Launch the app** - Tap the NetdataConnect icon
3. **Set up security (optional)** - You'll be prompted to set a PIN
   - Choose a 4-digit PIN
   - Confirm the PIN
   - Enable biometric unlock (fingerprint/face) if desired

### Understanding the Interface

Upon first launch, you'll see the main screen with:

- **App Title**: "NetdataConnect" at the top
- **Tagline**: "Monitor your Netdata servers in real-time"
- **Features Card**: List of capabilities
  - Real-time system metrics monitoring
  - Comprehensive chart visualization
  - Alarm and alert tracking
  - Server health monitoring
  - Historical data analysis
  - Multi-server support

### Navigation

NetdataConnect uses a modern Material Design interface:

- **Bottom Navigation**: Switch between main sections
  - Dashboard - Overview of all servers
  - Charts - Detailed metric visualization
  - Alarms - Active alerts and history
  - Settings - App configuration

- **Floating Action Button (FAB)**: Quick actions
  - Add new server
  - Refresh data
  - Access favorites

---

## Adding Servers

### Adding Your First Server

1. **Tap the "+" button** in the bottom-right corner
2. **Enter server details**:
   - **Name**: Friendly name (e.g., "Home Server", "Production")
   - **URL**: Server address (e.g., `http://192.168.1.100:19999`)
   - **Description**: Optional notes
3. **Configure authentication** (if required):
   - Enable "Requires Authentication"
   - Enter username and password
   - Or use API key if configured
4. **Test connection**:
   - Tap "Test Connection"
   - Wait for verification
   - Green checkmark = success
   - Red X = connection failed
5. **Save the server**:
   - Tap "Save" to add the server
   - Server appears in your dashboard

### Server URL Format

**Local network:**
```
http://192.168.1.100:19999
```

**Remote server with domain:**
```
https://monitor.example.com
```

**Custom port:**
```
http://server.local:8080
```

**HTTPS (recommended):**
```
https://192.168.1.100:19999
```

### Multiple Servers

Add as many servers as needed:
1. Each server gets its own profile
2. Profiles sync across ShareConnect apps via Asinka
3. Switch between servers with a single tap
4. Set a default server for quick access

---

## Monitoring Metrics

### Dashboard Overview

The dashboard provides an at-a-glance view of all your servers:

**Server Cards** display:
- Server name and status (online/offline)
- Current CPU usage (percentage)
- Current RAM usage (percentage)
- Active alarms count (critical/warning)
- Last update timestamp

**Color coding:**
- **Green**: Healthy (< 75% usage)
- **Yellow**: Warning (75-90% usage)
- **Red**: Critical (> 90% usage)

### Viewing Charts

Access detailed charts for any metric:

1. **Select a server** from the dashboard
2. **Browse categories**:
   - System - CPU, RAM, disk, network
   - Applications - Services and processes
   - Containers - Docker, LXC
   - Custom - User-defined metrics

3. **View chart details**:
   - Chart title and description
   - Current values for all dimensions
   - Historical trend line
   - Min/max values
   - Time range selector

### Chart Interaction

**Time Ranges:**
- Last 1 minute
- Last 5 minutes
- Last 15 minutes
- Last 1 hour
- Last 6 hours
- Last 24 hours
- Custom range

**Chart Types:**
- Line chart - Show trends over time
- Stacked area - Compare multiple metrics
- Bar chart - Discrete data points
- Gauge - Current value display

**Actions:**
- Pinch to zoom in/out
- Swipe left/right to pan
- Tap dimension legend to show/hide
- Long press for exact values
- Share chart as image
- Export data as CSV

### Understanding Metrics

**CPU Metrics** (system.cpu):
- **User**: Application CPU usage
- **System**: Kernel CPU usage
- **Nice**: Low-priority processes
- **Idle**: Unused CPU capacity
- **IOWait**: Waiting for disk I/O

**Memory Metrics** (system.ram):
- **Used**: Active memory usage
- **Cached**: File cache memory
- **Buffers**: Kernel buffers
- **Free**: Available memory

**Disk Metrics** (disk.space):
- **Used**: Space in use
- **Available**: Free space
- **Reserved**: System reserved

**Network Metrics** (net.eth0):
- **Received**: Incoming traffic
- **Sent**: Outgoing traffic
- **Errors**: Network errors
- **Drops**: Dropped packets

---

## Managing Alarms

### Active Alarms

View all active alarms across your servers:

1. **Tap "Alarms" tab** in bottom navigation
2. **See alarm list** with:
   - Alarm name
   - Severity (CLEAR, WARNING, CRITICAL)
   - Current value and threshold
   - Time since alarm triggered
   - Affected chart

**Severity Levels:**

🟢 **CLEAR** - All is well
- Metric is within normal range
- No action required

🟡 **WARNING** - Attention needed
- Metric approaching threshold
- Monitor situation
- Plan corrective action

🔴 **CRITICAL** - Immediate action required
- Metric exceeded critical threshold
- System may be degraded
- Urgent intervention needed

### Alarm Details

Tap any alarm to see full details:

- **Chart**: Link to related metric chart
- **Current Value**: Latest metric value
- **Threshold**: Warning and critical thresholds
- **Duration**: How long alarm has been active
- **Repeat Interval**: Notification frequency
- **Execute**: Action script name
- **Recipient**: Notification recipient
- **Info**: Alarm description and guidance

### Alarm History

View past alarms to track server health trends:

1. **Tap "History" in Alarms screen**
2. **Filter by**:
   - Time range
   - Severity
   - Server
   - Alarm type
3. **See historical data**:
   - When alarm triggered
   - When it cleared
   - Duration
   - Peak value

---

## Using Widgets

### Home Screen Widgets

Add NetdataConnect widgets to your home screen for quick metrics:

1. **Long press** on home screen
2. **Tap "Widgets"**
3. **Find "NetdataConnect"**
4. **Drag widget** to home screen
5. **Configure widget**:
   - Select server
   - Select metric
   - Choose update interval
   - Pick theme

**Widget Sizes:**
- Small (2x2) - Single metric value
- Medium (4x2) - Metric with mini chart
- Large (4x4) - Multiple metrics grid

**Widget Updates:**
- Auto-update every 1/5/15/30/60 minutes
- Manual refresh by tapping
- Battery-efficient background updates

### Customizing Widgets

**Themes:**
- Light - White background
- Dark - Black background
- Auto - Follows system theme
- Custom - Match app theme

**Display Options:**
- Show/hide chart
- Show/hide trend arrow
- Show/hide last update time
- Text size adjustment

---

## Security Settings

### PIN Protection

Protect your monitoring data with a PIN:

1. **Open Settings**
2. **Tap "Security"**
3. **Enable "PIN Lock"**
4. **Set 4-digit PIN**
5. **Confirm PIN**
6. **Choose lock timeout**:
   - Immediate
   - 1 minute
   - 5 minutes
   - 15 minutes
   - Never

### Biometric Authentication

Use fingerprint or face unlock:

1. **Enable PIN Lock first** (required)
2. **Tap "Biometric Unlock"**
3. **Follow device prompts** to register
4. **Choose fallback**:
   - PIN required if biometric fails
   - Or allow retry

### Network Security

**HTTPS Configuration:**
1. Use HTTPS URLs for servers when possible
2. Accept self-signed certificates (if needed)
3. Configure custom CA certificates
4. Enable certificate pinning for production

**VPN Support:**
- NetdataConnect works over VPN
- Access servers on private networks
- Encrypted tunnel for all data

---

## Synchronization

### Asinka Sync

NetdataConnect synchronizes data with other ShareConnect apps via Asinka:

**What Syncs:**
- Server profiles
- Theme preferences
- App settings
- Bookmarked metrics
- Alarm configurations
- Language settings

**How It Works:**
1. **Automatic discovery** - Apps find each other on the network
2. **Secure connection** - Encrypted communication
3. **Real-time sync** - Changes propagate immediately
4. **Conflict resolution** - Latest change wins

**Sync Status:**
Check sync status in Settings:
- **Connected** - Active sync with other apps
- **Discovering** - Searching for apps
- **Offline** - No network connection

### Managing Sync

**Disable sync for specific data:**
1. **Open Settings > Sync**
2. **Toggle individual sync types**:
   - Profiles sync
   - Theme sync
   - History sync
   - Preferences sync
3. **Changes take effect immediately**

**Troubleshooting Sync:**
- Ensure all apps are on same network
- Check firewall allows required ports
- Verify all apps are latest version
- Restart sync managers if needed

---

## Troubleshooting

### Connection Issues

**Problem**: Cannot connect to server

**Solutions:**
1. **Verify server is running**:
   - Open browser on same network
   - Navigate to `http://[server-ip]:19999`
   - Should see Netdata dashboard

2. **Check network connectivity**:
   - Ensure device has network access
   - Try pinging server IP
   - Verify firewall rules

3. **Validate URL format**:
   - Must include `http://` or `https://`
   - Include port if not 19999
   - No trailing slash

4. **Test from browser**:
   - Open URL in mobile browser
   - If it works there, app should work

### Slow Performance

**Problem**: App is slow or laggy

**Solutions:**
1. **Reduce update frequency**:
   - Settings > General > Update Interval
   - Increase to 5 or 10 seconds

2. **Limit number of charts**:
   - Remove unused servers
   - Hide irrelevant charts
   - Use favorites for quick access

3. **Clear cache**:
   - Settings > Storage > Clear Cache
   - Does not delete servers or settings

4. **Check network speed**:
   - Slow connection = slow updates
   - Use WiFi instead of mobile data

### Alarms Not Updating

**Problem**: Alarms don't show or update

**Solutions:**
1. **Refresh manually**:
   - Pull down to refresh
   - Or tap refresh button

2. **Check server alarms**:
   - Verify alarms configured on server
   - Access `/api/v1/alarms` endpoint

3. **Enable notifications**:
   - Settings > Notifications
   - Allow alarm notifications

### Sync Not Working

**Problem**: Data doesn't sync across apps

**Solutions:**
1. **Verify network**:
   - All apps on same WiFi network
   - No VPN or proxy interference

2. **Check sync settings**:
   - Settings > Sync
   - Ensure sync types enabled

3. **Restart sync**:
   - Settings > Sync > Restart Sync Managers
   - Wait 10-30 seconds

4. **Update apps**:
   - Ensure all ShareConnect apps are latest version

---

## FAQ

### General Questions

**Q: Is NetdataConnect free?**
A: Yes, NetdataConnect is part of the ShareConnect ecosystem and is free to use.

**Q: Do I need a Netdata Cloud account?**
A: No, NetdataConnect connects directly to your Netdata server. No cloud account needed.

**Q: Can I monitor servers on the internet?**
A: Yes, if your Netdata server is accessible over the internet with a public IP or domain.

**Q: How many servers can I monitor?**
A: No limit. Add as many servers as you need.

**Q: Does it work offline?**
A: No, NetdataConnect requires network access to your Netdata servers.

### Technical Questions

**Q: What port does Netdata use?**
A: Default is 19999, but it can be configured to any port.

**Q: Does NetdataConnect support authentication?**
A: Yes, if your Netdata server has authentication enabled, you can configure credentials.

**Q: Can I export chart data?**
A: Yes, charts can be exported as CSV or shared as images.

**Q: What chart types are supported?**
A: Line, stacked area, bar, and gauge charts.

**Q: How often does data update?**
A: Default is 1 second, configurable from 1-60 seconds.

### Privacy & Security

**Q: Is my data encrypted?**
A: Yes, all sync data is encrypted via Asinka. Use HTTPS for server connections to encrypt metric data.

**Q: Where is data stored?**
A: Locally on your device in an encrypted database. No cloud storage.

**Q: Can someone access my monitoring data?**
A: Only if they have your device and PIN/biometric access.

**Q: What permissions does the app need?**
A: Internet access and network state. No camera, location, or contacts access.

---

## Getting Help

### Support Resources

**Documentation:**
- NetdataConnect.md - Technical documentation
- This user manual
- Netdata official docs: https://learn.netdata.cloud

**Community:**
- ShareConnect Wiki: https://deepwiki.com/vasic-digital/ShareConnect
- GitHub Issues (if applicable)
- User forums

### Reporting Issues

When reporting a problem, include:
1. NetdataConnect version
2. Android version and device model
3. Netdata server version
4. Steps to reproduce
5. Screenshots (if relevant)
6. Error messages

### Tips for Success

1. **Start simple** - Add one server first, then add more
2. **Use HTTPS** - For security when accessing over internet
3. **Set up alarms** - Let Netdata notify you of issues
4. **Create bookmarks** - For frequently viewed charts
5. **Use widgets** - Quick access to key metrics
6. **Regular updates** - Keep app and Netdata server updated

---

## Conclusion

NetdataConnect brings enterprise-grade monitoring to your Android device. With real-time metrics, comprehensive charts, and alarm management, you can keep an eye on your infrastructure from anywhere.

**Quick Wins:**
- Add your first server in under a minute
- View real-time CPU and RAM usage
- Get notified when problems occur
- Share server profiles across devices
- Access monitoring on the go

**Next Steps:**
1. Add your Netdata server
2. Explore the available charts
3. Set up widgets for your home screen
4. Configure alarms for critical metrics
5. Share with your team via ShareConnect

Thank you for choosing NetdataConnect!

---

**Version**: 1.0.0
**Last Updated**: 2024
**Copyright**: © 2024 Vasic Digital
