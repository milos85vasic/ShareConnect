# PlexConnect Troubleshooting Guide

This guide helps you resolve common issues with PlexConnect. If you can't find a solution here, check the [GitHub Issues](https://github.com/your-org/ShareConnect/issues) or community forums.

## Table of Contents

1. [Installation Issues](#installation-issues)
2. [Connection Problems](#connection-problems)
3. [Authentication Issues](#authentication-issues)
4. [Playback Problems](#playback-problems)
5. [Performance Issues](#performance-issues)
6. [Data Synchronization](#data-synchronization)
7. [Crashes & Errors](#crashes--errors)
8. [Advanced Diagnostics](#advanced-diagnostics)

## Installation Issues

### App Won't Install

**Error:** "App not installed" or "Installation failed"

**Solutions:**
1. **Storage Space**: Ensure sufficient storage space (at least 100MB free)
2. **Unknown Sources**: Enable "Install from Unknown Sources"
   - Android 7.0+: Settings → Apps → Special access → Install unknown apps
   - Android 8.0+: Settings → Apps & notifications → Special access → Install unknown apps
3. **Corrupted APK**: Re-download the APK file
4. **Device Compatibility**: Verify Android 7.0+ (API 24)
5. **Antivirus**: Temporarily disable antivirus apps

### App Crashes on Launch

**Symptoms:** App closes immediately after opening

**Solutions:**
1. **Clear Cache**: Settings → Apps → PlexConnect → Storage → Clear cache
2. **Clear Data**: Settings → Apps → PlexConnect → Storage → Clear storage
3. **Restart Device**: Perform a full device restart
4. **Update Android**: Ensure latest Android version
5. **Reinstall**: Uninstall and reinstall the app

### Onboarding Won't Complete

**Symptoms:** Stuck on onboarding screens

**Solutions:**
1. **Force Restart**: Close app completely and restart
2. **Clear Data**: Reset app data to restart onboarding
3. **Network Check**: Ensure internet connectivity
4. **Skip Onboarding**: Use "Skip" button if available

## Connection Problems

### Cannot Connect to Plex Server

**Error Messages:**
- "Server unreachable"
- "Connection timeout"
- "Network error"

**Diagnostic Steps:**

#### 1. Check Server Status
```bash
# On server machine, check if Plex is running
ps aux | grep plex
# Or check Plex Web interface at http://localhost:32400/web
```

#### 2. Network Connectivity
- **Same Network**: Ensure device and server are on same Wi-Fi network
- **IP Address**: Verify correct server IP address
- **Port**: Confirm port 32400 is not blocked
- **Firewall**: Check firewall allows Plex connections

#### 3. Server Configuration
- **Remote Access**: Enable in Plex settings if accessing remotely
- **Secure Connections**: Try disabling "Require secure connections"
- **Authentication**: Ensure server allows connections

#### 4. Device Network
```bash
# Test basic connectivity
ping [SERVER_IP]

# Test Plex port
telnet [SERVER_IP] 32400
```

#### 5. Advanced Troubleshooting
- **DNS Issues**: Try using IP address instead of hostname
- **VPN**: Disable VPN if using one
- **Mobile Data**: Test with Wi-Fi vs mobile data
- **Router Settings**: Check port forwarding if accessing remotely

### Server Appears But Won't Connect

**Symptoms:** Server shows in list but connection fails

**Solutions:**
1. **Server Restart**: Restart Plex Media Server
2. **Token Refresh**: Re-authenticate with server
3. **Server Update**: Update Plex server to latest version
4. **Database Issues**: Check Plex server logs for errors

### Multiple Server Issues

**Symptoms:** Problems when managing multiple servers

**Solutions:**
1. **Default Server**: Set one server as default
2. **Token Management**: Ensure each server has valid token
3. **Server Priority**: Check server response times
4. **Cleanup**: Remove unused servers from app

## Authentication Issues

### PIN Authentication Fails

**Error:** "Authentication failed" or PIN timeout

**Solutions:**
1. **Internet Connection**: Ensure stable internet for plex.tv access
2. **Browser Issues**: Try different browser for PIN entry
3. **PIN Timeout**: Enter PIN within 5 minutes of generation
4. **Account Status**: Verify Plex account is active and in good standing
5. **Region Issues**: Check if plex.tv is accessible in your region

### Authentication Loop

**Symptoms:** Repeatedly asked to authenticate

**Solutions:**
1. **Token Expiration**: Re-authenticate to get new token
2. **Account Changes**: Check for password changes or account issues
3. **Server Changes**: Re-add server if server token changed
4. **App Data**: Clear app data and re-authenticate

### Server-Specific Authentication

**Symptoms:** Works with one server but not another

**Solutions:**
1. **Server Ownership**: Ensure you have access to the server
2. **Shared Server**: Check if server allows shared users
3. **Token Scope**: Verify token has necessary permissions
4. **Server Settings**: Check server authentication requirements

## Playback Problems

### Media Won't Play

**Error Messages:**
- "Playback failed"
- "Transcoding error"
- "Format not supported"

**Solutions:**

#### 1. Server Transcoding
- **Enable Transcoding**: Ensure server can transcode media
- **Quality Settings**: Try lower quality setting
- **Codec Support**: Check if device supports media codecs

#### 2. Network Issues
- **Bandwidth**: Ensure sufficient bandwidth for selected quality
- **Connection Stability**: Test with stable Wi-Fi connection
- **Buffering**: Wait for sufficient buffer before playing

#### 3. Media File Issues
- **File Corruption**: Check media file integrity on server
- **Format Compatibility**: Verify format is supported
- **DRM Protection**: Ensure media is not DRM-protected

#### 4. Device-Specific
- **Permissions**: Grant necessary permissions (storage, network)
- **Battery Optimization**: Exclude from battery optimization
- **Background Apps**: Close other media apps

### Audio/Video Sync Issues

**Symptoms:** Audio and video out of synchronization

**Solutions:**
1. **Restart Playback**: Stop and restart the media
2. **Quality Change**: Switch to different quality setting
3. **Device Restart**: Restart Android device
4. **Server Restart**: Restart Plex Media Server
5. **Network Reset**: Reset network connection

### Quality Selection Problems

**Symptoms:** Cannot change playback quality

**Solutions:**
1. **Server Support**: Ensure server supports quality selection
2. **Network Speed**: Quality options depend on connection speed
3. **Media Format**: Some formats don't support quality selection
4. **App Settings**: Check quality preferences in settings

### Subtitle Issues

**Symptoms:** Subtitles not displaying or wrong language

**Solutions:**
1. **Subtitle Files**: Ensure subtitle files exist on server
2. **Language Settings**: Check subtitle language preferences
3. **Format Support**: Verify subtitle format is supported
4. **Timing**: Adjust subtitle timing if available

## Performance Issues

### App Runs Slowly

**Symptoms:** Lag, freezing, slow loading

**Diagnostic Steps:**

#### 1. Device Performance
```bash
# Check device memory
adb shell dumpsys meminfo com.shareconnect.plexconnect

# Check CPU usage
adb shell dumpsys cpuinfo | grep plexconnect
```

#### 2. App Optimization
- **Clear Cache**: Clear app cache regularly
- **Background Apps**: Close unnecessary apps
- **Device Storage**: Ensure adequate free storage
- **Memory Management**: Restart app periodically

#### 3. Network Optimization
- **Wi-Fi Priority**: Use Wi-Fi over mobile data
- **Quality Settings**: Lower quality for slower connections
- **Buffering**: Adjust buffer settings if available

#### 4. Server Performance
- **Server Load**: Check server CPU/memory usage
- **Concurrent Users**: Reduce number of simultaneous users
- **Database Optimization**: Optimize Plex database

### High Battery Usage

**Symptoms:** Battery drains quickly during use

**Solutions:**
1. **Quality Settings**: Use lower quality for mobile viewing
2. **Screen Brightness**: Reduce screen brightness
3. **Background Playback**: Disable if not needed
4. **Battery Optimization**: Check battery optimization settings
5. **Network Type**: Prefer Wi-Fi over mobile data

### Memory Issues

**Symptoms:** App crashes with "Out of memory" errors

**Solutions:**
1. **Clear Cache**: Regularly clear app cache
2. **Lower Quality**: Use lower quality settings
3. **Device RAM**: Ensure adequate device RAM
4. **Background Apps**: Close other memory-intensive apps
5. **App Updates**: Update to latest version

## Data Synchronization

### Media Not Appearing

**Symptoms:** New media not showing in libraries

**Solutions:**
1. **Library Refresh**: Refresh library on Plex server
2. **App Refresh**: Pull down to refresh in app
3. **Server Restart**: Restart Plex Media Server
4. **Library Scan**: Force library scan on server
5. **Permissions**: Check server file permissions

### Progress Not Syncing

**Symptoms:** Watch progress not saving across devices

**Solutions:**
1. **Account Sync**: Ensure signed into Plex account
2. **Network Issues**: Check internet connectivity
3. **Server Settings**: Verify server allows sync
4. **App Permissions**: Ensure background sync permissions
5. **Timing**: Allow time for sync to complete

### Library Updates Delayed

**Symptoms:** Library changes not appearing immediately

**Solutions:**
1. **Manual Refresh**: Use pull-to-refresh in app
2. **Server Settings**: Check library update frequency
3. **Network Issues**: Ensure stable connection
4. **Cache Issues**: Clear app cache

## Crashes & Errors

### Common Crash Scenarios

#### 1. Null Pointer Exceptions
**Symptoms:** App crashes with NPE errors

**Debug Steps:**
```bash
# Enable crash logging
adb logcat -s AndroidRuntime:E PlexConnect:E

# Check for null values in data models
# Verify API responses are not null
```

#### 2. Network Timeouts
**Symptoms:** Crashes during network operations

**Solutions:**
- Increase timeout values
- Implement retry logic
- Check network stability
- Handle network errors gracefully

#### 3. Database Corruption
**Symptoms:** Crashes related to database operations

**Solutions:**
- Clear app data to reset database
- Check database file integrity
- Update to latest app version
- Report database corruption issues

### Error Logging

#### Enable Debug Logging
```bash
# Enable verbose logging
adb shell setprop log.tag.PlexConnect DEBUG

# View logs
adb logcat -s PlexConnect
```

#### Log Analysis
Common error patterns:
- **Network errors**: Check connectivity and server status
- **Authentication errors**: Verify tokens and account status
- **Database errors**: Check data integrity
- **Memory errors**: Monitor device resources

### Crash Reporting

When reporting crashes, include:
- **Device Info**: Android version, device model
- **App Version**: PlexConnect version number
- **Crash Logs**: Logcat output around crash time
- **Steps to Reproduce**: How to trigger the crash
- **Frequency**: How often it occurs

## Advanced Diagnostics

### Network Diagnostics

#### Test Server Connectivity
```bash
# Test basic connectivity
curl -I http://[SERVER_IP]:32400

# Test with authentication
curl -H "X-Plex-Token: [TOKEN]" http://[SERVER_IP]:32400/library/sections
```

#### Network Tracing
```bash
# Enable network tracing
adb shell setprop log.tag.HttpTransport DEBUG
adb logcat -s HttpTransport
```

### Database Diagnostics

#### Check Database Integrity
```bash
# Access database via adb
adb shell
run-as com.shareconnect.plexconnect
cd databases
sqlite3 plex_database.db ".integrity_check"
```

#### Database Queries
```sql
-- Check server table
SELECT * FROM plex_servers;

-- Check library sync status
SELECT COUNT(*) FROM plex_libraries;

-- Check media items
SELECT COUNT(*) FROM plex_media_items;
```

### Performance Profiling

#### Memory Profiling
```bash
# Dump memory info
adb shell dumpsys meminfo com.shareconnect.plexconnect

# Heap dump
adb shell am dumpheap com.shareconnect.plexconnect /sdcard/heap.dump
```

#### CPU Profiling
```bash
# CPU usage
adb shell dumpsys cpuinfo | grep plexconnect

# Thread analysis
adb shell ps -t | grep plexconnect
```

### Server-Side Diagnostics

#### Plex Server Logs
```bash
# Linux server logs
tail -f /var/lib/plexmediaserver/Library/Application\ Support/Plex\ Media\ Server/Logs/Plex\ Media\ Server.log

# Check server status
curl http://localhost:32400/status/sessions
```

#### Server Performance
- Monitor CPU/memory usage during playback
- Check transcoding queue
- Verify network interface performance

## Getting Help

### Support Resources

#### Documentation
- [User Guide](./USER_GUIDE.md) - Complete user manual
- [API Reference](./API_REFERENCE.md) - Technical documentation
- [GitHub Issues](https://github.com/your-org/ShareConnect/issues) - Bug reports

#### Community Support
- **Plex Forums**: https://forums.plex.tv/
- **Reddit**: r/Plex, r/androidapps
- **Discord**: Plex community servers

#### Official Support
- **Plex Support**: https://support.plex.tv/
- **Android Issues**: Google Play Store reviews

### Reporting Issues

#### Bug Report Template
```
**Issue Summary:**
[Brief description of the problem]

**Environment:**
- PlexConnect Version: [version]
- Android Version: [version]
- Device Model: [model]
- Plex Server Version: [version]

**Steps to Reproduce:**
1. [Step 1]
2. [Step 2]
3. [Expected result]
4. [Actual result]

**Additional Information:**
- Logs: [attach logcat output]
- Screenshots: [attach relevant screenshots]
- Frequency: [how often it occurs]
```

#### Feature Requests
```
**Feature Summary:**
[Brief description of requested feature]

**Use Case:**
[Why this feature would be useful]

**Implementation Ideas:**
[Technical suggestions if any]

**Alternatives Considered:**
[Other solutions you've thought of]
```

### Version Compatibility

#### Supported Versions
- **Android**: 7.0+ (API 24+)
- **Plex Server**: 1.0.0+
- **Plex Account**: Active subscription required

#### Known Issues
- **Android 6.0**: Not supported (API 23 and below)
- **Old Plex Servers**: May have compatibility issues
- **Beta Versions**: May contain unstable features

---

**Document Version:** 1.0.0
**Last Updated:** 2025-10-24
**PlexConnect Version:** 1.0.0