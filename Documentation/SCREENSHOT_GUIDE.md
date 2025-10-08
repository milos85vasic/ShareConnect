# Screenshot Guide

## Purpose

This document describes where app screenshots should be placed and how to capture them for documentation purposes.

---

## Directory Structure

```
Documentation/Screenshots/
├── ShareConnect/
│   ├── main_screen.png
│   ├── profiles_list.png
│   ├── add_profile.png
│   ├── history_view.png
│   ├── bookmarks_view.png
│   ├── rss_feeds.png
│   ├── settings_general.png
│   ├── settings_sync.png
│   └── dark_mode.png
│
├── qBitConnect/
│   ├── server_list.png
│   ├── torrent_list.png
│   ├── torrent_details.png
│   ├── add_torrent.png
│   ├── rss_feeds.png
│   ├── search.png
│   ├── settings.png
│   └── profiles_qbit.png
│
├── TransmissionConnect/
│   ├── server_list.png
│   ├── torrent_list.png
│   ├── torrent_details.png
│   ├── add_server.png
│   ├── settings.png
│   ├── notifications.png
│   └── profiles_transmission.png
│
└── SyncModules/
    ├── sync_in_action.png
    ├── sync_flow_diagram.png
    ├── conflict_resolution.png
    ├── real_time_update.png
    └── cross_app_demo.png
```

---

## How to Capture Screenshots

### Method 1: Using ADB (Recommended)

```bash
# Ensure device/emulator is connected
adb devices

# Take screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull to computer
adb pull /sdcard/screenshot.png ./Documentation/Screenshots/ShareConnect/main_screen.png

# Clean up device
adb shell rm /sdcard/screenshot.png
```

### Method 2: Using Android Studio

1. Run the app on emulator or device
2. Click the camera icon in the Device toolbar
3. Save to `Documentation/Screenshots/{AppName}/`

### Method 3: Using Emulator Controls

1. Run app in Android Emulator
2. Press `Ctrl + S` (Windows/Linux) or `Cmd + S` (Mac)
3. Save to appropriate directory

---

## Screenshot Requirements

### General Requirements

- **Resolution:** 1080x1920 or higher (portrait)
- **Format:** PNG (preferred) or JPEG
- **Quality:** High quality, no compression artifacts
- **Content:** Show actual data, not empty states
- **UI:** Clean UI, no debug overlays

### Specific Screenshots Needed

#### ShareConnect

1. **main_screen.png**
   - Home screen with profile list
   - Show at least 2-3 profiles (one qBittorrent, one Transmission)

2. **profiles_list.png**
   - Full profile management screen
   - Display different profile types

3. **add_profile.png**
   - Add/Edit profile dialog
   - Show all fields filled

4. **history_view.png**
   - History list with recent downloads
   - Mix of successful and failed items

5. **bookmarks_view.png**
   - Bookmarks screen
   - Show favorites and regular bookmarks

6. **rss_feeds.png**
   - RSS feeds list
   - Show enabled and disabled feeds

7. **settings_general.png**
   - General settings screen

8. **settings_sync.png**
   - Sync-specific settings (if available)

9. **dark_mode.png**
   - App in dark mode
   - Same screen as main_screen for comparison

#### qBitConnect

1. **server_list.png**
   - Server selection screen
   - Show multiple servers if available

2. **torrent_list.png**
   - Active torrents view
   - Show downloading, seeding, paused torrents

3. **torrent_details.png**
   - Detailed torrent information
   - Files, trackers, peers tabs

4. **add_torrent.png**
   - Add torrent dialog
   - Show magnet URI or file selection

5. **rss_feeds.png**
   - RSS automation screen
   - Show configured feeds with filters

6. **search.png**
   - Torrent search interface

7. **settings.png**
   - App settings screen

8. **profiles_qbit.png**
   - Profile management (if different from server list)
   - Show synced profiles

#### TransmissionConnect

1. **server_list.png**
   - Server management screen
   - Multiple servers displayed

2. **torrent_list.png**
   - Main torrent list view
   - Various torrent states

3. **torrent_details.png**
   - Torrent detail screen
   - Show peers, trackers, files

4. **add_server.png**
   - Add/Edit server dialog

5. **settings.png**
   - Application settings

6. **notifications.png**
   - Notification settings or notification example

7. **profiles_transmission.png**
   - Synced profiles view

#### SyncModules

1. **sync_in_action.png**
   - Animated GIF or series showing:
     - Action in App A
     - Change appearing in App B
     - Change appearing in App C

2. **sync_flow_diagram.png**
   - Visual diagram of sync process
   - Can be created externally and imported

3. **conflict_resolution.png**
   - Example of version-based conflict resolution
   - Before/after comparison

4. **real_time_update.png**
   - Split screen showing simultaneous updates

5. **cross_app_demo.png**
   - Three apps side by side showing synced data

---

## Screenshot Capture Script

Save this as `capture_screenshots.sh`:

```bash
#!/bin/bash

# Screenshot capture script for ShareConnect ecosystem

SCREENSHOT_DIR="Documentation/Screenshots"
TEMP_DIR="/sdcard/screenshots"

# Create directories
mkdir -p "${SCREENSHOT_DIR}/ShareConnect"
mkdir -p "${SCREENSHOT_DIR}/qBitConnect"
mkdir -p "${SCREENSHOT_DIR}/TransmissionConnect"
mkdir -p "${SCREENSHOT_DIR}/SyncModules"

# Function to capture and pull screenshot
capture() {
    local app=$1
    local name=$2

    echo "Capturing $app/$name..."
    echo "Please navigate to the desired screen and press Enter"
    read

    adb shell screencap -p "${TEMP_DIR}/${name}.png"
    adb pull "${TEMP_DIR}/${name}.png" "${SCREENSHOT_DIR}/${app}/${name}.png"
    adb shell rm "${TEMP_DIR}/${name}.png"

    echo "Saved to ${SCREENSHOT_DIR}/${app}/${name}.png"
}

# ShareConnect screenshots
echo "=== ShareConnect Screenshots ==="
capture "ShareConnect" "main_screen"
capture "ShareConnect" "profiles_list"
capture "ShareConnect" "add_profile"
capture "ShareConnect" "history_view"
capture "ShareConnect" "bookmarks_view"
capture "ShareConnect" "rss_feeds"
capture "ShareConnect" "settings_general"
capture "ShareConnect" "dark_mode"

# qBitConnect screenshots
echo "=== qBitConnect Screenshots ==="
capture "qBitConnect" "server_list"
capture "qBitConnect" "torrent_list"
capture "qBitConnect" "torrent_details"
capture "qBitConnect" "add_torrent"
capture "qBitConnect" "rss_feeds"
capture "qBitConnect" "search"
capture "qBitConnect" "settings"

# TransmissionConnect screenshots
echo "=== TransmissionConnect Screenshots ==="
capture "TransmissionConnect" "server_list"
capture "TransmissionConnect" "torrent_list"
capture "TransmissionConnect" "torrent_details"
capture "TransmissionConnect" "add_server"
capture "TransmissionConnect" "settings"
capture "TransmissionConnect" "notifications"

echo "Screenshot capture complete!"
echo "Screenshots saved in: ${SCREENSHOT_DIR}/"
```

Make executable:
```bash
chmod +x capture_screenshots.sh
```

Run:
```bash
./capture_screenshots.sh
```

---

## Embedding Screenshots in Documentation

### Markdown Syntax

```markdown
## ShareConnect Main Screen

![ShareConnect Main Screen](./Documentation/Screenshots/ShareConnect/main_screen.png)

*The main screen showing synced profiles from all apps*
```

### HTML with Size Control

```html
<p align="center">
  <img src="./Documentation/Screenshots/ShareConnect/main_screen.png" width="300" alt="ShareConnect Main Screen">
  <br>
  <em>ShareConnect main screen with synced profiles</em>
</p>
```

### Side-by-Side Comparison

```html
<p align="center">
  <img src="./Documentation/Screenshots/ShareConnect/main_screen.png" width="250" alt="ShareConnect">
  <img src="./Documentation/Screenshots/qBitConnect/torrent_list.png" width="250" alt="qBitConnect">
  <img src="./Documentation/Screenshots/TransmissionConnect/torrent_list.png" width="250" alt="TransmissionConnect">
  <br>
  <em>All three apps showing synced data</em>
</p>
```

---

## Animated Screenshots (GIFs)

For demonstrating sync in action:

### Using Android Studio

1. Record screen: Run → Record Video
2. Stop recording
3. Convert to GIF using online tools or:

```bash
ffmpeg -i screen_recording.mp4 -vf "fps=10,scale=360:-1:flags=lanczos" \
       -c:v gif Documentation/Screenshots/SyncModules/sync_in_action.gif
```

### Using ADB Screenrecord

```bash
# Record (max 3 minutes)
adb shell screenrecord /sdcard/sync_demo.mp4

# Stop with Ctrl+C

# Pull
adb pull /sdcard/sync_demo.mp4

# Convert to GIF
ffmpeg -i sync_demo.mp4 -vf "fps=10,scale=360:-1" sync_in_action.gif
```

---

## Screenshot Checklist

Before submitting screenshots:

- [ ] All screenshots are high resolution (1080p+)
- [ ] No personal or sensitive data visible
- [ ] No debug information or developer tools visible
- [ ] Screenshots show actual data, not placeholders
- [ ] Consistent UI theme across screenshots (unless showing theme sync)
- [ ] All required screenshots captured for each app
- [ ] Screenshots are properly named and organized
- [ ] README files updated with screenshot references
- [ ] Screenshots demonstrate sync functionality
- [ ] Dark mode variants captured where applicable

---

## Notes

- Keep original high-resolution screenshots
- Create web-optimized versions for documentation (max 800px width)
- Store originals separately in case re-editing is needed
- Consider creating video demonstrations for complex features
- Update screenshots when UI changes significantly

---

**Last Updated:** 2025-10-08
**Maintainer:** Project Documentation Team
