# ShareConnect Icon Family

## Overview
A comprehensive icon family for all ShareConnect applications, featuring a unified design language with light blue circular backgrounds and application-specific foreground icons.

## Quick Start

### 1. Generate Placeholder Icons
```bash
./generate_icons.sh
```

### 2. Update All Applications (when final icons are ready)
```bash
./update_all_app_icons.sh
```

### 3. Design Final Icons
1. Use the SVG templates in each application directory
2. Design application-specific icons
3. Export to 4096x4096 PNG format
4. Replace placeholder files

## File Structure
```
Assets/Logos/
├── README.md                          # This file
├── ICON_DESIGN_SPECIFICATION.md       # Design guidelines
├── IMPLEMENTATION_GUIDE.md            # Technical integration guide
├── generate_icons.sh                  # Template generation script
├── update_all_app_icons.sh            # Batch update script
└── [ApplicationName]/                 # One directory per app
    ├── icon_[appname]_template.svg    # SVG template for design
    └── icon_[appname]_placeholder.png # Placeholder PNG
```

## Applications Covered

### Core Applications
- **ShareConnector** - Main sharing app
- **TransmissionConnect** - Torrent client
- **uTorrentConnect** - Torrent client  
- **qBitConnect** - Torrent client
- **JDownloaderConnect** - Download manager

### Media & Entertainment
- **PlexConnect** - Media server
- **JellyfinConnect** - Media server

### Home & Automation
- **HomeAssistantConnect** - Home automation
- **PortainerConnect** - Container management
- **NetdataConnect** - System monitoring
- **WireGuardConnect** - VPN

### Communication & Productivity
- **MatrixConnect** - Communication platform
- **GiteaConnect** - Git hosting
- **OnlyOfficeConnect** - Office suite

### File Management & Backup
- **MinecraftServerConnect** - Game server
- **SeafileConnect** - File synchronization
- **SyncthingConnect** - File synchronization
- **DuplicatiConnect** - Backup solution
- **PaperlessNGConnect** - Document management
- **MotrixConnect** - Download manager

## Design Principles

### Visual Identity
- **Background**: Light blue circle (#4FC3F7)
- **Foreground**: White/light gray application icons
- **Style**: Modern, clean, and recognizable
- **Family**: Consistent design language across all apps

### Technical Specifications
- **Resolution**: 4096x4096 pixels
- **Format**: PNG with transparency
- **Background**: Transparent with circular blue element
- **Android Support**: All standard icon sizes

## Implementation Workflow

### Phase 1: Design (Current)
- [x] Create directory structure
- [x] Generate SVG templates
- [x] Create placeholder icons
- [x] Document design specifications

### Phase 2: Final Design
- [ ] Design application-specific icons
- [ ] Export to 4K PNG format
- [ ] Replace placeholder files
- [ ] Test visual consistency

### Phase 3: Integration
- [ ] Generate Android icon variants
- [ ] Update all application resources
- [ ] Test on devices and emulators
- [ ] Verify adaptive icons work

### Phase 4: Maintenance
- [ ] Document design changes
- [ ] Update for new applications
- [ ] Maintain version control

## Tools Required

### Design Tools
- **Inkscape** (recommended) - Free vector graphics
- **GIMP** - Free raster graphics
- **Adobe Illustrator** - Professional vector graphics
- **Figma** - Web-based design tool

### Development Tools
- **ImageMagick** - Command-line image processing
- **Android Studio** - Android development
- **Git** - Version control

## Color Palette

### Primary Colors
- **Light Blue**: #4FC3F7 (Icon background)
- **White**: #FFFFFF (Icon foreground)
- **Transparent**: For outer background

### Application Accents
Each application can use subtle accent colors in the foreground:
- TransmissionConnect: Orange/amber
- qBitConnect: Purple
- JDownloaderConnect: Green
- PlexConnect: Orange/yellow
- etc.

## Testing Checklist

### Visual Testing
- [ ] Icons are recognizable at small sizes
- [ ] Consistent visual weight across family
- [ ] Proper contrast and visibility
- [ ] Works on light and dark backgrounds

### Technical Testing
- [ ] All Android icon sizes generated
- [ ] Adaptive icons work correctly
- [ ] Round icons render properly
- [ ] No performance issues

### Integration Testing
- [ ] Each app builds successfully
- [ ] Launcher icons appear correctly
- [ ] No resource conflicts
- [ ] Works on multiple Android versions

## Support & Maintenance

### Adding New Applications
1. Add directory in `Assets/Logos/`
2. Create SVG template and placeholder
3. Update scripts and documentation
4. Generate Android resources

### Design Updates
1. Update source files in `Assets/Logos/`
2. Run `update_all_app_icons.sh`
3. Test all applications
4. Update documentation

### Troubleshooting
- Check file paths and naming
- Verify Android resource structure
- Test on multiple devices
- Check logcat for errors

## License & Attribution
These icons are designed specifically for the ShareConnect project. Please refer to the main project LICENSE for usage terms.

## Contributing
When contributing new icons:
1. Follow the design specifications
2. Maintain visual consistency
3. Test on multiple platforms
4. Update documentation
5. Submit through proper channels