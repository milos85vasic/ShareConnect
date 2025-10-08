# Screenshots Directory

This directory contains screenshots for all three applications in the ShareConnect ecosystem.

## Directory Structure

```
Screenshots/
├── ShareConnect/          # ShareConnect app screenshots
├── qBitConnect/          # qBitConnect app screenshots
├── TransmissionConnect/  # TransmissionConnect app screenshots
└── SyncModules/          # Sync demonstration screenshots/GIFs
```

## Current Status

📸 **Screenshots Pending**

Screenshots have not yet been captured. Please follow the [Screenshot Guide](../SCREENSHOT_GUIDE.md) to capture and organize app screenshots.

## Required Screenshots

### ShareConnect (9 screenshots)
- [ ] main_screen.png
- [ ] profiles_list.png
- [ ] add_profile.png
- [ ] history_view.png
- [ ] bookmarks_view.png
- [ ] rss_feeds.png
- [ ] settings_general.png
- [ ] settings_sync.png
- [ ] dark_mode.png

### qBitConnect (8 screenshots)
- [ ] server_list.png
- [ ] torrent_list.png
- [ ] torrent_details.png
- [ ] add_torrent.png
- [ ] rss_feeds.png
- [ ] search.png
- [ ] settings.png
- [ ] profiles_qbit.png

### TransmissionConnect (7 screenshots)
- [ ] server_list.png
- [ ] torrent_list.png
- [ ] torrent_details.png
- [ ] add_server.png
- [ ] settings.png
- [ ] notifications.png
- [ ] profiles_transmission.png

### SyncModules (5 screenshots/GIFs)
- [ ] sync_in_action.gif
- [ ] sync_flow_diagram.png
- [ ] conflict_resolution.png
- [ ] real_time_update.png
- [ ] cross_app_demo.png

## How to Capture

See [Screenshot Guide](../SCREENSHOT_GUIDE.md) for detailed instructions.

Quick capture:
```bash
# Connect device/emulator
adb devices

# Capture screenshot
adb shell screencap -p /sdcard/screenshot.png

# Pull to Screenshots directory
adb pull /sdcard/screenshot.png ./Documentation/Screenshots/{AppName}/{filename}.png

# Clean up
adb shell rm /sdcard/screenshot.png
```

## Automated Capture Script

Use the provided script:
```bash
./capture_screenshots.sh
```

## Notes

- Screenshots should be high resolution (1080p+)
- PNG format preferred
- No personal data visible
- Show actual data, not empty states
- Keep UI clean (no debug overlays)

---

**Last Updated:** 2025-10-08
