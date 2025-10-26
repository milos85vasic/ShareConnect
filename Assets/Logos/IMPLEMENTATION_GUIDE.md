# ShareConnect Icon Implementation Guide

## Overview
This guide explains how to integrate the new icon family into all ShareConnect Android applications.

## File Structure
```
Assets/Logos/
├── ICON_DESIGN_SPECIFICATION.md
├── IMPLEMENTATION_GUIDE.md
├── generate_icons.sh
├── ShareConnector/
│   ├── icon_shareconnector_template.svg
│   └── icon_shareconnector_placeholder.png
├── TransmissionConnect/
│   ├── icon_transmissionconnect_template.svg
│   └── icon_transmissionconnect_placeholder.png
└── [20+ other applications...]
```

## Android Icon Integration

### 1. Prepare Icon Files
For each application, you need multiple icon sizes:

**Required Android Icon Sizes:**
- `mipmap-mdpi` (48x48)
- `mipmap-hdpi` (72x72)  
- `mipmap-xhdpi` (96x96)
- `mipmap-xxhdpi` (144x144)
- `mipmap-xxxhdpi` (192x192)
- `mipmap-anydpi-v26` (adaptive icons)

### 2. Generate Icon Variants
Use the following script to generate all required sizes:

```bash
#!/bin/bash
# generate_android_icons.sh

APP_NAME="YourAppName"
SOURCE_ICON="Assets/Logos/${APP_NAME}/icon_${APP_NAME,,}_4k.png"

# Generate all Android icon sizes
convert "$SOURCE_ICON" -resize 48x48 "app/src/main/res/mipmap-mdpi/ic_launcher.png"
convert "$SOURCE_ICON" -resize 72x72 "app/src/main/res/mipmap-hdpi/ic_launcher.png"
convert "$SOURCE_ICON" -resize 96x96 "app/src/main/res/mipmap-xhdpi/ic_launcher.png"
convert "$SOURCE_ICON" -resize 144x144 "app/src/main/res/mipmap-xxhdpi/ic_launcher.png"
convert "$SOURCE_ICON" -resize 192x192 "app/src/main/res/mipmap-xxxhdpi/ic_launcher.png"

# Generate round icons
convert "$SOURCE_ICON" -resize 48x48 -alpha set -background none -vignette 0x0 "app/src/main/res/mipmap-mdpi/ic_launcher_round.png"
convert "$SOURCE_ICON" -resize 72x72 -alpha set -background none -vignette 0x0 "app/src/main/res/mipmap-hdpi/ic_launcher_round.png"
convert "$SOURCE_ICON" -resize 96x96 -alpha set -background none -vignette 0x0 "app/src/main/res/mipmap-xhdpi/ic_launcher_round.png"
convert "$SOURCE_ICON" -resize 144x144 -alpha set -background none -vignette 0x0 "app/src/main/res/mipmap-xxhdpi/ic_launcher_round.png"
convert "$SOURCE_ICON" -resize 192x192 -alpha set -background none -vignette 0x0 "app/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"

echo "Android icons generated for $APP_NAME"
```

### 3. Update AndroidManifest.xml
Update the application section in each app's `AndroidManifest.xml`:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    android:label="@string/app_name"
    ...>
</application>
```

### 4. Create Adaptive Icons (Android 8.0+)
For adaptive icons, create XML files in `mipmap-anydpi-v26/`:

**ic_launcher.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

**ic_launcher_round.xml:**
```xml
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
```

### 5. Update Colors
Add the light blue background color to `colors.xml`:

```xml
<color name="ic_launcher_background">#4FC3F7</color>
```

### 6. Create Foreground Drawables
Create foreground drawables in `drawable/`:

**ic_launcher_foreground.xml:**
```xml
<vector xmlns:android="http://schemas.android.com/apk/res/android"
    android:width="108dp"
    android:height="108dp"
    android:viewportWidth="108"
    android:viewportHeight="108">
    <!-- Your app-specific icon path data here -->
</vector>
```

## Application-Specific Implementation

### ShareConnector
- **Icon**: Network connectivity nodes
- **Integration**: Update main app launcher
- **Files**: Replace existing icons in `ShareConnector/src/main/res/`

### TransmissionConnect
- **Icon**: Butterfly with gears
- **Integration**: Update torrent client app
- **Files**: Replace in `Connectors/TransmissionConnect/TransmissionConnector/src/main/res/`

### uTorrentConnect
- **Icon**: µ symbol with download arrow
- **Integration**: Update uTorrent client app
- **Files**: Replace in `Connectors/uTorrentConnect/uTorrentConnector/src/main/res/`

### qBitConnect
- **Icon**: Q with torrent pieces
- **Integration**: Update qBittorrent client app
- **Files**: Replace in `Connectors/qBitConnect/qBitConnector/src/main/res/`

### JDownloaderConnect
- **Icon**: Download arrow with packages
- **Integration**: Update download manager app
- **Files**: Replace in `Connectors/JDownloaderConnect/JDownloaderConnector/src/main/res/`

### PlexConnect
- **Icon**: Media waves with play symbol
- **Integration**: Update media server app
- **Files**: Replace in `Connectors/PlexConnect/PlexConnector/src/main/res/`

### HomeAssistantConnect
- **Icon**: Smart home with devices
- **Integration**: Update home automation app
- **Files**: Replace in `Connectors/HomeAssistantConnect/HomeAssistantConnector/src/main/res/`

### PortainerConnect
- **Icon**: Docker whale with containers
- **Integration**: Update container management app
- **Files**: Replace in `Connectors/PortainerConnect/PortainerConnector/src/main/res/`

### JellyfinConnect
- **Icon**: Jellyfish with media symbols
- **Integration**: Update media server app
- **Files**: Replace in `Connectors/JellyfinConnect/JellyfinConnector/src/main/res/`

### NetdataConnect
- **Icon**: Dashboard with metrics
- **Integration**: Update monitoring app
- **Files**: Replace in `Connectors/NetdataConnect/NetdataConnector/src/main/res/`

### WireGuardConnect
- **Icon**: Shield with network waves
- **Integration**: Update VPN app
- **Files**: Replace in `Connectors/WireGuardConnect/WireGuardConnector/src/main/res/`

### MatrixConnect
- **Icon**: Chat bubbles
- **Integration**: Update communication app
- **Files**: Replace in `Connectors/MatrixConnect/MatrixConnector/src/main/res/`

### GiteaConnect
- **Icon**: Git branch with code
- **Integration**: Update Git hosting app
- **Files**: Replace in `Connectors/GiteaConnect/GiteaConnector/src/main/res/`

### OnlyOfficeConnect
- **Icon**: Document with tools
- **Integration**: Update office suite app
- **Files**: Replace in `Connectors/OnlyOfficeConnect/OnlyOfficeConnector/src/main/res/`

### MinecraftServerConnect
- **Icon**: Minecraft block with gear
- **Integration**: Update game server app
- **Files**: Replace in `Connectors/MinecraftServerConnect/MinecraftServerConnector/src/main/res/`

### SeafileConnect
- **Icon**: Cloud with sync arrows
- **Integration**: Update file sync app
- **Files**: Replace in `Connectors/SeafileConnect/SeafileConnector/src/main/res/`

### SyncthingConnect
- **Icon**: Syncing folders
- **Integration**: Update file sync app
- **Files**: Replace in `Connectors/SyncthingConnect/SyncthingConnector/src/main/res/`

### DuplicatiConnect
- **Icon**: Backup vault
- **Integration**: Update backup app
- **Files**: Replace in `Connectors/DuplicatiConnect/DuplicatiConnector/src/main/res/`

### PaperlessNGConnect
- **Icon**: Document scanner
- **Integration**: Update document management app
- **Files**: Replace in `Connectors/PaperlessNGConnect/PaperlessNGConnector/src/main/res/`

### MotrixConnect
- **Icon**: Download manager
- **Integration**: Update download manager app
- **Files**: Replace in `Connectors/MotrixConnect/MotrixConnector/src/main/res/`

## Testing

### 1. Visual Testing
- Test icons on different Android versions
- Verify appearance on various launchers
- Check adaptive icon behavior

### 2. Size Testing
- Verify all icon sizes render correctly
- Test on different screen densities
- Check round icon variants

### 3. Integration Testing
- Build each application
- Verify launcher icons appear correctly
- Test on physical devices and emulators

## Tools for Icon Generation

### Recommended Tools
1. **Inkscape** - Vector graphics (free)
2. **GIMP** - Raster graphics (free)
3. **Adobe Illustrator** - Professional vector graphics
4. **Figma** - Web-based design tool
5. **Android Studio** - Built-in asset studio

### Android Asset Studio
Use Android Studio's Image Asset Studio:
1. Right-click `res` folder → New → Image Asset
2. Select "Launcher Icons (Adaptive and Legacy)"
3. Upload your 4K source icon
4. Configure background and foreground
5. Generate all required sizes

## Best Practices

### Design Guidelines
- Maintain consistent visual weight across all icons
- Ensure icons are recognizable at small sizes
- Use the same blue background color (#4FC3F7)
- Keep foreground elements simple and clear

### Technical Guidelines
- Use PNG format for raster icons
- Use SVG for vector assets when possible
- Optimize file sizes for performance
- Test on multiple Android versions
- Follow Material Design guidelines

### File Organization
- Keep source files in `Assets/Logos/`
- Generate Android resources for each app
- Maintain version control for all assets
- Document any design changes

## Troubleshooting

### Common Issues
1. **Icons appear blurry** - Ensure source is 4K resolution
2. **Adaptive icons not working** - Check XML configuration
3. **Wrong colors** - Verify color values in XML files
4. **Missing icons** - Check file paths and naming

### Debug Steps
1. Clean and rebuild project
2. Check AndroidManifest.xml configuration
3. Verify all icon sizes are present
4. Test on different devices
5. Check logcat for resource errors

## Maintenance

### Updates
- Update all icons when design changes
- Maintain consistency across applications
- Document version changes
- Test thoroughly after updates

### Version Control
- Keep source files in version control
- Tag releases with icon versions
- Maintain changelog for design changes