# HomeAssistantConnect User Manual

## Welcome to HomeAssistantConnect

HomeAssistantConnect brings the power of Home Assistant to your Android device, allowing you to control your smart home, automate tasks, and monitor your home from anywhere. This comprehensive guide will help you get started and make the most of all features.

## Table of Contents

1. [Getting Started](#getting-started)
2. [Initial Setup](#initial-setup)
3. [Dashboard](#dashboard)
4. [Controlling Entities](#controlling-entities)
5. [Scenes and Automations](#scenes-and-automations)
6. [History and Monitoring](#history-and-monitoring)
7. [Widgets](#widgets)
8. [Settings](#settings)
9. [Advanced Features](#advanced-features)
10. [Troubleshooting](#troubleshooting)
11. [Tips and Tricks](#tips-and-tricks)
12. [FAQ](#faq)

## Getting Started

### What is HomeAssistantConnect?

HomeAssistantConnect is a native Android application that connects to your Home Assistant server, providing:

- Real-time control of all your smart home devices
- Beautiful, Material Design 3 interface
- Dashboard customization
- Scene activation and automation management
- History tracking and monitoring
- Home screen widgets
- Integration with other ShareConnect apps

### Requirements

- **Android Device**: Android 9.0 (API 28) or later
- **Home Assistant Server**: Version 2024.1.0 or later
- **Network**: Local network access or external URL configured in Home Assistant
- **Access Token**: Long-lived access token from Home Assistant

### Installation

1. Download HomeAssistantConnect from the Google Play Store or F-Droid
2. Open the app
3. Follow the setup wizard to connect to your Home Assistant server

## Initial Setup

### Step 1: Generate Access Token

Before setting up HomeAssistantConnect, you need to generate a long-lived access token in Home Assistant:

1. Open Home Assistant in your web browser
2. Click on your profile (bottom left)
3. Scroll down to "Long-Lived Access Tokens"
4. Click "Create Token"
5. Give it a name (e.g., "HomeAssistantConnect")
6. Copy the token (you won't be able to see it again!)

### Step 2: Add Server

1. Open HomeAssistantConnect
2. Tap "Add Server"
3. Enter your server details:
   - **Server URL**: Your Home Assistant URL (e.g., `http://homeassistant.local:8123` or `https://your-ha-domain.com`)
   - **Access Token**: Paste the token you generated
   - **Server Name** (optional): A friendly name for your server
4. Tap "Test Connection"
5. If successful, tap "Save"

**Common Server URLs**:
- Local network: `http://homeassistant.local:8123` or `http://192.168.1.100:8123`
- Remote access: `https://your-home-assistant-url.duckdns.org`
- Nabu Casa: `https://abcdefg.ui.nabu.casa`

### Step 3: Grant Permissions

HomeAssistantConnect may request the following permissions:

- **Internet**: Required to connect to your Home Assistant server
- **Notifications**: For automation triggers and state change alerts
- **Biometric Authentication** (optional): To secure access to the app

## Dashboard

### Overview

The Dashboard is your central hub for controlling your smart home. It displays customizable cards showing your most important entities and controls.

### Default Dashboard

When you first connect, HomeAssistantConnect creates a default dashboard showing:
- Lights
- Switches
- Climate controls
- Sensors
- Cameras

### Customizing Your Dashboard

#### Adding Cards

1. Tap the menu icon (☰) in the top left
2. Select "Dashboard"
3. Tap "Edit" in the top right
4. Tap "Add Card"
5. Choose a card type:
   - **Entity Card**: Display and control a single entity
   - **Entities Card**: Show multiple entities in a list
   - **Sensor Card**: Display sensor values with graphs
   - **Camera Card**: Show camera feed
   - **Weather Card**: Display weather information
   - **Media Card**: Control media players
   - **Thermostat Card**: Control climate devices
6. Configure the card:
   - Select entity/entities
   - Choose display options
   - Set card title
7. Tap "Add"

#### Rearranging Cards

1. Enter edit mode (tap "Edit")
2. Long-press on a card
3. Drag it to the desired position
4. Release to drop
5. Tap "Done" when finished

#### Editing Cards

1. Enter edit mode
2. Tap the edit icon on a card
3. Modify settings
4. Tap "Save"

#### Deleting Cards

1. Enter edit mode
2. Tap the delete icon (🗑️) on a card
3. Confirm deletion

### Card Types Explained

#### Entity Card
Shows a single entity with quick controls. Perfect for frequently used lights, switches, or sensors.

**Best for**: Living room light, front door lock, bedroom temperature

#### Entities Card
Displays multiple related entities in a compact list. Great for grouping entities by room or function.

**Best for**: All bedroom lights, all door sensors, all thermostats

#### Sensor Card
Displays sensor values with optional historical graphs. Useful for monitoring temperature, humidity, energy usage, etc.

**Best for**: Temperature trends, energy consumption, humidity levels

#### Camera Card
Shows live feed from cameras with snapshot refresh.

**Best for**: Front door camera, driveway camera, baby monitor

#### Weather Card
Displays current weather and forecast using your Home Assistant weather integration.

**Best for**: Local weather, outdoor temperature, rain forecast

#### Media Card
Controls media players with play/pause, volume, and source selection.

**Best for**: Spotify, TV, speakers, media centers

## Controlling Entities

### Entity List

Access the full entity list:
1. Tap the menu icon (☰)
2. Select "Entities"
3. Browse or search for entities

### Filtering Entities

Filter entities by domain:
1. In the Entities screen, tap "Filter"
2. Select one or more domains:
   - Lights
   - Switches
   - Sensors
   - Climate
   - Covers
   - Locks
   - Cameras
   - Media Players
   - Fans
   - Vacuum Cleaners
3. Tap "Apply"

### Searching Entities

1. In the Entities screen, tap the search icon (🔍)
2. Type entity name or ID
3. Results update in real-time

### Controlling Different Entity Types

#### Lights

**Toggle On/Off**:
- Tap the entity card
- Or use the toggle switch

**Adjust Brightness**:
1. Tap and hold the entity card
2. Use the slider to adjust brightness
3. Release when done

**Change Color**:
1. Open entity details (long-press)
2. Tap the color wheel
3. Select desired color
4. Tap "Apply"

**Set Color Temperature**:
1. Open entity details
2. Tap "Color Temperature"
3. Adjust slider from warm to cool
4. Tap "Apply"

**Apply Effects**:
1. Open entity details
2. Tap "Effects"
3. Choose an effect (e.g., "Flash", "Colorloop")
4. Tap "Activate"

#### Switches

**Toggle On/Off**:
- Tap the entity card or toggle switch

#### Climate (Thermostats)

**Set Temperature**:
1. Tap the climate entity
2. Use + / - buttons or slider
3. Changes apply immediately

**Change Mode**:
1. Tap the mode button
2. Select:
   - Off
   - Heat
   - Cool
   - Auto
   - Dry
   - Fan Only

**Set Preset**:
1. Tap "Preset"
2. Choose:
   - Home
   - Away
   - Sleep
   - Eco

#### Covers (Blinds, Garage Doors)

**Open/Close**:
- Tap to toggle

**Set Position**:
1. Tap and hold
2. Use slider to set position (0-100%)
3. Release when done

**Stop Movement**:
- Tap "Stop" button while cover is moving

#### Locks

**Lock/Unlock**:
1. Tap the lock
2. Confirm with biometric or PIN (if enabled)
3. Lock changes state

#### Media Players

**Play/Pause**:
- Tap the play/pause button

**Volume Control**:
- Use volume slider

**Select Source**:
1. Tap "Source"
2. Choose input source
3. Tap "Select"

**Browse Media**:
1. Tap "Browse"
2. Navigate media library
3. Tap item to play

#### Cameras

**View Live Feed**:
- Tap camera entity to view live feed

**Take Snapshot**:
1. Open camera entity
2. Tap "Snapshot"
3. Image saved to gallery

#### Vacuum Cleaners

**Start/Stop**:
- Tap to toggle cleaning

**Return to Dock**:
- Tap "Return to Dock"

**Set Fan Speed**:
1. Tap "Fan Speed"
2. Select speed level
3. Tap "Apply"

**Zone Cleaning**:
1. Tap "Zone Clean"
2. Select zone on map
3. Tap "Start"

#### Fans

**Toggle On/Off**:
- Tap to toggle

**Set Speed**:
1. Tap "Speed"
2. Choose speed level (Low, Medium, High)
3. Or use percentage slider

**Oscillate**:
- Toggle oscillation switch

**Set Direction**:
- Toggle between Forward and Reverse

### Entity Details

View detailed information about any entity:

1. Long-press an entity card
2. Or tap entity in list and select "Details"

**Entity Details Screen Shows**:
- Current state
- All attributes
- State history graph
- Related entities
- Last changed timestamp
- Last updated timestamp
- Available services
- Quick actions

## Scenes and Automations

### Scenes

Scenes allow you to save and recall specific states for multiple entities.

#### Activating a Scene

1. Tap menu (☰) → "Scenes"
2. Browse available scenes
3. Tap a scene to activate it
4. All entities in the scene change to their saved states

**Common Scenes**:
- "Good Morning": Turn on bedroom lights, open blinds, start coffee maker
- "Movie Time": Dim lights, close blinds, turn on TV
- "Good Night": Turn off all lights, lock doors, set thermostat
- "Away": Turn off lights, lock doors, arm security system

#### Creating Scenes (via Home Assistant)

Scenes are created in Home Assistant's UI or configuration files. HomeAssistantConnect can activate but not create scenes.

### Automations

Automations are rules that trigger actions based on conditions.

#### Viewing Automations

1. Tap menu (☰) → "Automations"
2. See all automations with:
   - Name
   - Current state (Enabled/Disabled)
   - Last triggered time
   - Trigger count

#### Enabling/Disabling Automations

1. In Automations list
2. Toggle the switch next to an automation
3. Automation is immediately enabled or disabled

#### Manually Triggering Automation

1. Tap an automation in the list
2. Tap "Trigger Now"
3. Automation runs immediately (regardless of trigger conditions)

**Use Cases**:
- Test new automations
- Run cleanup scripts
- Trigger complex sequences manually

#### Viewing Automation Details

1. Tap an automation
2. View:
   - Trigger conditions
   - Actions
   - Last run result
   - Execution history

### Scripts

Scripts are sequences of actions you can run manually or from automations.

#### Running Scripts

1. Tap menu (☰) → "Scripts"
2. Tap a script to run it
3. Script executes immediately

## History and Monitoring

### History

View the complete history of all entity state changes.

#### Accessing History

1. Tap menu (☰) → "History"
2. Select time range:
   - Last hour
   - Last 24 hours
   - Last 7 days
   - Last 30 days
   - Custom range

#### Filtering History

**By Entity**:
1. Tap "Filter"
2. Select entity or entities
3. Tap "Apply"

**By Domain**:
1. Tap "Filter"
2. Select domain (lights, switches, etc.)
3. Tap "Apply"

#### Viewing Details

1. Tap any history entry
2. View:
   - State change details
   - Timestamp
   - Changed attributes
   - Context (what triggered the change)

### Logbook

The Logbook shows a human-readable history of events.

#### Accessing Logbook

1. Tap menu (☰) → "Logbook"
2. See chronological list of events:
   - "Living Room Light turned on"
   - "Front door unlocked by John"
   - "Motion detected in hallway"
   - "Automation 'Good Morning' triggered"

#### Filtering Logbook

Same filtering options as History:
- Time range
- Entities
- Domains

### Energy Monitoring

If you have energy monitoring configured in Home Assistant:

1. Tap menu (☰) → "Energy"
2. View:
   - Current power usage
   - Daily/monthly consumption
   - Cost estimates
   - Solar production (if applicable)
   - Individual device consumption

#### Energy Dashboard

- **Consumption**: Energy used by your home
- **Individual Devices**: See which devices use the most energy
- **Solar Production**: If you have solar panels
- **Gas Consumption**: If configured
- **Water Consumption**: If configured

## Widgets

HomeAssistantConnect provides home screen widgets for quick access to your smart home.

### Adding a Widget

1. Long-press on your Android home screen
2. Tap "Widgets"
3. Find "HomeAssistantConnect"
4. Drag a widget to your home screen
5. Configure the widget:
   - Select entity to display
   - Choose widget size
   - Select quick actions
6. Tap "Add Widget"

### Widget Types

#### Entity Status Widget

**Sizes**: 2x1, 2x2, 4x2

**Shows**:
- Entity name
- Current state
- Last updated time
- Quick action button (if applicable)

**Best For**: Lights, switches, sensors, locks

#### Scene Widget

**Size**: 1x1

**Shows**:
- Scene name
- Icon

**Action**: Tap to activate scene

**Best For**: Quick scene activation from home screen

#### Automation Toggle Widget

**Size**: 1x1

**Shows**:
- Automation name
- Enabled/disabled state

**Action**: Tap to toggle automation

#### Dashboard Widget

**Size**: 4x4

**Shows**:
- Mini version of your dashboard
- Up to 6 entity cards
- Quick controls

**Best For**: Complete home screen smart home control

### Widget Customization

1. Long-press the widget
2. Tap "Configure"
3. Adjust:
   - Entity selection
   - Display options
   - Refresh interval
   - Theme
4. Tap "Save"

### Widget Refresh

Widgets automatically refresh:
- Every 15 minutes (to preserve battery)
- When you tap the refresh button
- When state changes occur (via WebSocket if app is running)

## Settings

Access settings by tapping menu (☰) → "Settings"

### Server Settings

#### Managing Servers

**Add Additional Server**:
1. Settings → "Servers"
2. Tap "Add Server"
3. Enter server details
4. Tap "Save"

**Switch Active Server**:
1. Settings → "Servers"
2. Tap the server you want to use
3. Tap "Set as Active"

**Edit Server**:
1. Settings → "Servers"
2. Tap the server
3. Tap "Edit"
4. Modify details
5. Tap "Save"

**Delete Server**:
1. Settings → "Servers"
2. Swipe left on server
3. Tap "Delete"
4. Confirm deletion

#### Connection Settings

**Update Interval**:
- 5 seconds (most responsive, higher battery usage)
- 10 seconds
- 30 seconds (recommended)
- 1 minute
- 5 minutes (battery saver)

**Timeout**:
- Connection timeout: 10-60 seconds
- Read timeout: 10-60 seconds

**WebSocket**:
- Enable/disable real-time updates
- Auto-reconnect on network change

### Appearance

#### Theme

Choose from 6 built-in themes:
1. **Default**: Blue and purple
2. **Green**: Nature-inspired green tones
3. **Red**: Bold red accents
4. **Orange**: Warm orange tones
5. **Purple**: Deep purple shades
6. **Custom**: Create your own theme

**Custom Theme**:
1. Tap "Custom Theme"
2. Choose colors:
   - Primary color
   - Secondary color
   - Background color
   - Surface color
   - Error color
3. Tap "Save"
4. Theme syncs to all ShareConnect apps

#### Dark Mode

- **System Default**: Follow system dark mode setting
- **Always Light**: Always use light theme
- **Always Dark**: Always use dark theme

#### Font Size

- Small
- Medium (default)
- Large
- Extra Large

### Notifications

Configure which events trigger notifications:

**State Changes**:
- ☑ Lights turned on/off
- ☑ Doors locked/unlocked
- ☑ Motion detected
- ☐ Temperature changes

**Automation Triggers**:
- ☑ Automation ran successfully
- ☑ Automation failed
- ☐ Automation manually triggered

**System Events**:
- ☑ Connection lost
- ☑ Connection restored
- ☐ Updates available

**Notification Channels**:
Each type of notification has its own Android notification channel, allowing you to customize:
- Sound
- Vibration
- LED color
- Importance level
- Do Not Disturb override

### Security

#### Biometric Authentication

Require fingerprint or face unlock to open app:

1. Settings → "Security"
2. Enable "Biometric Authentication"
3. Choose when to require:
   - Every time
   - After 1 minute
   - After 5 minutes
   - After 30 minutes

#### Screen Lock

Require PIN to open app:

1. Settings → "Security"
2. Enable "Screen Lock"
3. Set 4-digit PIN
4. Confirm PIN

#### Auto-Lock

Automatically lock app when minimized:
- Immediately
- After 1 minute
- After 5 minutes
- Never

### Language

HomeAssistantConnect supports multiple languages:

1. Settings → "Language"
2. Select your language
3. App restarts with new language
4. Language syncs to all ShareConnect apps

**Supported Languages**:
- English
- Spanish
- French
- German
- Italian
- Portuguese
- Dutch
- Polish
- Russian
- Japanese
- Chinese (Simplified)
- Chinese (Traditional)
- Korean

### Backup and Restore

#### Creating Backup

1. Settings → "Backup"
2. Tap "Create Backup"
3. Backup includes:
   - Server configurations
   - Dashboard layouts
   - Widget configurations
   - App preferences
4. Choose backup location:
   - Local storage
   - Google Drive
   - Other cloud storage

#### Restoring Backup

1. Settings → "Restore"
2. Choose backup file
3. Tap "Restore"
4. App restarts with restored configuration

### Advanced Settings

#### Network

**Prefer Local Connection**:
- Automatically use internal URL when on home network

**Use Mobile Data**:
- Allow connections over mobile data (may use significant data)

**Certificate Validation**:
- Enable to verify SSL certificates (recommended)
- Disable for self-signed certificates (not recommended)

#### Cache

**Clear Cache**:
- Clear cached images and data
- Frees up storage space

**Cache Size Limit**:
- Maximum cache size: 10MB - 500MB

#### Developer Options

**Enable Debug Logging**:
- Log detailed information for troubleshooting
- Logs saved to: `/Android/data/com.shareconnect.homeassistantconnect/files/logs/`

**Force WebSocket Reconnect**:
- Manually trigger WebSocket reconnection

**Clear All Data**:
- ⚠️ WARNING: This deletes all app data including server configurations

## Advanced Features

### Voice Commands

Control your smart home with voice:

1. Say "OK Google" or activate Google Assistant
2. Say your command:
   - "Turn on living room light"
   - "Set thermostat to 72 degrees"
   - "Lock the front door"
   - "Activate movie scene"
3. HomeAssistantConnect processes the command via Home Assistant

### Location-Based Automations

Use your phone's location to trigger automations:

1. Grant location permission
2. In Home Assistant, create automations using device_tracker.your_phone
3. Examples:
   - Unlock door when you arrive home
   - Turn off lights when you leave
   - Adjust thermostat based on proximity

### NFC Tags

Trigger scenes or scripts with NFC tags:

1. Write an NFC tag with Home Assistant URL
2. Tap your phone to the tag
3. HomeAssistantConnect performs the action

**Example NFC Tag**:
```
homeassistant://scene/activate/scene.good_morning
```

### Android Auto Integration

Control basic functions from your car:

1. Connect phone to Android Auto
2. HomeAssistantConnect appears in apps list
3. Voice control available:
   - "Open garage door"
   - "Turn on driveway light"

### Tasker Integration

Integrate with Tasker for advanced automation:

1. Install Tasker
2. Create new task
3. Add action → Plugin → HomeAssistantConnect
4. Configure action (service call, scene activation, etc.)
5. Trigger task based on any Tasker event

## Troubleshooting

### Connection Issues

#### "Unable to connect to server"

**Check**:
1. Is Home Assistant running?
2. Is your phone on the same network?
3. Is the URL correct?
4. Is port 8123 accessible?

**Try**:
- Use IP address instead of hostname
- Verify access token is valid
- Check Home Assistant logs for errors
- Disable any VPN that might be interfering

#### "Authentication failed"

**Cause**: Invalid or expired access token

**Solution**:
1. Generate new access token in Home Assistant
2. Update token in HomeAssistantConnect:
   - Settings → Servers → [Your Server] → Edit
   - Paste new token
   - Save

#### "Connection timeout"

**Cause**: Network too slow or unstable

**Solution**:
- Increase timeout: Settings → Connection → Timeout → 30 seconds
- Check Wi-Fi signal strength
- Try mobile data connection

### WebSocket Issues

#### "WebSocket disconnected"

**Cause**: Network interruption or Home Assistant restart

**Solution**:
- App automatically reconnects within 30 seconds
- If not, manually reconnect: Settings → Advanced → Force WebSocket Reconnect

#### Real-time updates not working

**Check**:
1. WebSocket enabled: Settings → Connection → WebSocket → Enabled
2. Network allows WebSocket connections
3. No firewall blocking WebSocket traffic

### Widget Issues

#### Widget not updating

**Try**:
1. Long-press widget → Configure → Save (to refresh configuration)
2. Remove and re-add widget
3. Check widget refresh interval
4. Restart phone

#### Widget shows "Error loading data"

**Cause**: Connection issue or entity not found

**Solution**:
1. Open HomeAssistantConnect app to test connection
2. Verify entity still exists in Home Assistant
3. Reconfigure widget with different entity

### Performance Issues

#### App is slow or laggy

**Try**:
1. Clear cache: Settings → Advanced → Clear Cache
2. Reduce update interval: Settings → Connection → Update Interval → 30 seconds
3. Disable unnecessary notifications
4. Reduce dashboard card count

#### High battery usage

**Optimize**:
1. Increase update interval to 1-5 minutes
2. Disable WebSocket real-time updates
3. Reduce widget refresh frequency
4. Enable battery optimization for app:
   - Android Settings → Battery → App battery usage → HomeAssistantConnect → Optimize

### Entity Issues

#### Entities not appearing

**Check**:
1. Are entities visible in Home Assistant UI?
2. Are entities disabled in Home Assistant?
3. Try refreshing: Pull down on entity list

**Solution**:
- Clear app cache
- Disconnect and reconnect server

#### Can't control entity

**Check**:
1. Entity supports the service you're trying to call
2. Entity is not disabled or unavailable
3. Home Assistant can control the entity

**Try**:
- Test control from Home Assistant UI
- Check Home Assistant logs
- Verify device is powered on and connected

### Sync Issues

#### Dashboard not syncing to other devices

**Check**:
1. All devices on same network
2. All ShareConnect apps updated to latest version
3. Sync managers running: Settings → Advanced → Sync Status

**Solution**:
- Restart all ShareConnect apps
- Check firewall isn't blocking ports 8890-8960

## Tips and Tricks

### Productivity Tips

1. **Create Morning Routine Scene**: Group all morning actions (open blinds, start coffee, set thermostat) into one scene for one-tap activation.

2. **Use Automation Geofencing**: Set up automations to trigger when your phone enters/exits home zone for hands-free control.

3. **Organize by Room**: Create separate dashboard tabs for each room for easier navigation.

4. **Quick Access Widget**: Place a 4x4 dashboard widget on your home screen for instant access without opening the app.

5. **Scene Shortcuts**: Create Android shortcuts for your most-used scenes:
   - Long-press app icon
   - Drag scene shortcut to home screen

### Energy Saving Tips

1. **Extend Update Interval**: Set to 1-5 minutes instead of 5-30 seconds.

2. **Disable WebSocket When Away**: Turn off real-time updates when you're not home.

3. **Limit Widgets**: Each widget consumes battery checking for updates.

4. **Use Dark Theme**: OLED screens use less power with dark pixels.

5. **Enable Battery Optimization**: Allow Android to manage app background activity.

### Customization Tips

1. **Custom Theme**: Create a theme matching your home decor colors.

2. **Entity Icons**: In Home Assistant, customize entity icons for better visual recognition.

3. **Entity Names**: Use clear, descriptive names ("Kitchen Ceiling Light" vs "Light 1").

4. **Dashboard Layouts**: Create multiple dashboards:
   - Main: Most used controls
   - Monitoring: All sensors
   - Media: All media players
   - Security: Cameras and locks

5. **Conditional Cards**: In Home Assistant, use conditional cards to show/hide based on state (e.g., show vacuum controls only when cleaning).

### Security Tips

1. **Use HTTPS**: Always use HTTPS for external connections.

2. **Strong Access Token**: Home Assistant access tokens should be kept secure like passwords.

3. **Enable Biometric Lock**: Prevent unauthorized access if phone is lost.

4. **Separate User**: Create a dedicated Home Assistant user for the app with limited permissions if sharing device.

5. **Regular Token Rotation**: Periodically generate new access tokens and update app.

### Advanced Tips

1. **Template Sensors**: Create template sensors in Home Assistant for complex calculations and display in app.

2. **Lovelace Raw Config**: For ultimate dashboard control, edit Lovelace YAML in Home Assistant then sync to app.

3. **Input Helpers**: Use input_boolean, input_number, and input_select for custom controls.

4. **RESTful Commands**: Create custom services in Home Assistant for advanced control.

5. **Markdown Cards**: Add rich formatted information to dashboard with markdown cards.

## FAQ

**Q: Does HomeAssistantConnect work without internet?**
A: Yes, as long as your phone and Home Assistant are on the same local network.

**Q: Can I control my home when away?**
A: Yes, if you have Home Assistant configured with external access (via Nabu Casa or manual setup).

**Q: How many servers can I add?**
A: Unlimited. Perfect for managing multiple homes or helping family/friends.

**Q: Does this replace the Home Assistant Companion App?**
A: HomeAssistantConnect focuses on control and monitoring. The official Companion App provides additional features like sensors, notifications, and location tracking. Both can be used together.

**Q: Is my data secure?**
A: Yes. All connections use HTTPS (if configured), access tokens are stored encrypted, and data never leaves your devices.

**Q: Can I use this without Home Assistant?**
A: No. HomeAssistantConnect requires a Home Assistant installation to function.

**Q: Will this drain my battery?**
A: Battery impact is minimal with default settings. Adjust update interval and disable real-time updates to reduce further.

**Q: Can I contribute to development?**
A: Yes! HomeAssistantConnect is part of the open-source ShareConnect project. See GitHub for contribution guidelines.

**Q: What's the difference between this and the web UI?**
A: HomeAssistantConnect is a native Android app with better performance, widgets, offline capability, and integration with other ShareConnect apps.

**Q: How do I report bugs or request features?**
A: Visit the ShareConnect GitHub repository to open an issue or discussion.

## Getting Help

### Support Resources

1. **In-App Help**: Settings → Help & Support
2. **User Manual**: This document
3. **Technical Documentation**: `HomeAssistantConnect.md`
4. **ShareConnect Wiki**: https://deepwiki.com/vasic-digital/ShareConnect
5. **GitHub Issues**: Report bugs and request features
6. **Community Forum**: Discuss with other users

### Diagnostic Information

When reporting issues, include:

1. **App Version**: Settings → About → Version
2. **Android Version**: Your Android version
3. **Home Assistant Version**: Your HA version
4. **Error Logs**: Settings → Advanced → Enable Debug Logging
5. **Steps to Reproduce**: Clear description of what you did

### Reset App

If all else fails, reset the app:

1. Settings → Advanced → Clear All Data
2. Confirm (⚠️ This deletes everything)
3. Reconfigure app from scratch

## Conclusion

HomeAssistantConnect brings the full power of Home Assistant to your Android device in a beautiful, native interface. Whether you're controlling lights, monitoring sensors, or activating complex automation routines, HomeAssistantConnect makes it fast and easy.

Explore all the features, customize your dashboard, and enjoy seamless control of your smart home!

**Happy Automating! 🏠🤖**

---

**Document Version**: 1.0.0
**Last Updated**: 2025-01-25
**For App Version**: 1.0.0 and later
