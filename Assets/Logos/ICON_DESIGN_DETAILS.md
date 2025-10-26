# Detailed Icon Design Specifications

## Icon Family Design System

### Core Visual Elements

#### 1. Background Circle
- **Color**: #4FC3F7 (Light Blue)
- **Opacity**: 90% (slightly transparent for depth)
- **Size**: 3600px diameter within 4096px canvas
- **Gradient**: Subtle radial gradient for depth
- **Shadow**: Soft drop shadow for elevation

#### 2. Foreground Icons
- **Color**: #FFFFFF (White) with subtle gradients
- **Style**: Modern, clean, geometric
- **Stroke Weight**: Consistent 120px stroke
- **Padding**: 400px from circle edge
- **Visual Hierarchy**: Clear primary symbol

### Application-Specific Icon Designs

#### ShareConnector
**Concept**: Universal connectivity and sharing
**Elements**: 
- Central node with 6 connected satellites
- Interconnecting lines forming star pattern
- Multi-color nodes (red, green, blue, yellow, purple, orange)
- Dynamic flow lines

#### TransmissionConnect
**Concept**: Butterfly with mechanical elements
**Elements**:
- Stylized butterfly silhouette
- Gear teeth integrated into wings
- Download arrows as antennae
- Orange/amber accent colors

#### uTorrentConnect
**Concept**: µ symbol with torrent integration
**Elements**:
- Greek letter µ (mu) as primary shape
- Download arrow integrated into µ stem
- Torrent pieces orbiting the symbol
- Blue gradient accents

#### qBitConnect
**Concept**: Q letter with torrent technology
**Elements**:
- Stylized Q letter with cutout
- Torrent puzzle pieces inside Q
- Speed indicators as background elements
- Purple gradient accents

#### JDownloaderConnect
**Concept**: Download management
**Elements**:
- Downward arrow as primary shape
- Package/container symbols around arrow
- Progress indicators
- Green gradient accents

#### PlexConnect
**Concept**: Media streaming
**Elements**:
- Plex-style waveform
- Play triangle integrated
- Media symbols (film, music, photo)
- Orange/yellow gradient

#### HomeAssistantConnect
**Concept**: Smart home automation
**Elements**:
- House silhouette with connected devices
- Smart devices (lights, thermostat, security)
- Network connection lines
- Blue gradient accents

#### PortainerConnect
**Concept**: Container management
**Elements**:
- Docker whale silhouette
- Container stack on whale's back
- Networking waves
- Blue/teal gradient

#### JellyfinConnect
**Concept**: Media server
**Elements**:
- Jellyfish silhouette
- Media symbols in tentacles
- Streaming waves
- Purple gradient

#### NetdataConnect
**Concept**: System monitoring
**Elements**:
- Dashboard with metrics
- Real-time graphs and charts
- Alert indicators
- Green gradient

#### WireGuardConnect
**Concept**: VPN security
**Elements**:
- Shield shape with network waves
- Lock symbol integration
- Secure connection lines
- Blue gradient

#### MatrixConnect
**Concept**: Communication platform
**Elements**:
- Chat bubble matrix
- Message flow lines
- Encryption symbols
- Green gradient

#### GiteaConnect
**Concept**: Git repository hosting
**Elements**:
- Git branch symbol
- Code brackets and symbols
- Version control indicators
- Orange gradient

#### OnlyOfficeConnect
**Concept**: Office suite
**Elements**:
- Document with editing tools
- Text, spreadsheet, presentation symbols
- Collaboration indicators
- Blue gradient

#### MinecraftServerConnect
**Concept**: Game server management
**Elements**:
- Minecraft block (grass/dirt)
- Server gear integration
- Player indicators
- Green gradient

#### SeafileConnect
**Concept**: File synchronization
**Elements**:
- Cloud with sync arrows
- File folder symbols
- Version history indicators
- Blue gradient

#### SyncthingConnect
**Concept**: File synchronization
**Elements**:
- Two synchronized folders
- Bidirectional arrows
- Real-time sync indicators
- Green gradient

#### DuplicatiConnect
**Concept**: Backup solution
**Elements**:
- Backup vault/safe
- Data stream arrows
- Recovery symbols
- Blue gradient

#### PaperlessNGConnect
**Concept**: Document management
**Elements**:
- Document scanner
- OCR text recognition
- Filing system symbols
- Orange gradient

#### MotrixConnect
**Concept**: Download manager
**Elements**:
- Download manager interface
- Speed indicators
- Queue management symbols
- Purple gradient

### Design Principles

#### Visual Consistency
- **Stroke Weight**: 120px uniform
- **Corner Radius**: 80px rounded corners
- **Spacing**: 400px padding from edges
- **Scale**: Icons fill 70% of circle area

#### Color System
- **Primary**: #4FC3F7 (Background)
- **Foreground**: #FFFFFF (Icons)
- **Accents**: Application-specific gradients
- **Shadows**: 45° angle, 20px blur

#### Typography (for text elements)
- **Font**: Roboto Medium
- **Size**: Proportional to icon scale
- **Weight**: Medium (500)
- **Tracking**: -2% for better readability

### Technical Specifications

#### File Formats
- **Source**: SVG (vector)
- **Export**: PNG 4096x4096
- **Color Mode**: RGB
- **Bit Depth**: 32-bit with alpha channel

#### Export Settings
- **Resolution**: 4096x4096 pixels
- **DPI**: 300 (for print if needed)
- **Compression**: Lossless PNG
- **Metadata**: Include creation details

#### Android Requirements
- **mdpi**: 48x48 (1x)
- **hdpi**: 72x72 (1.5x)
- **xhdpi**: 96x96 (2x)
- **xxhdpi**: 144x144 (3x)
- **xxxhdpi**: 192x192 (4x)

### Design Tools Workflow

#### Inkscape (Recommended)
1. Open SVG template
2. Design foreground elements
3. Apply gradients and effects
4. Export to PNG

#### GIMP
1. Open PNG placeholder
2. Create new layers for design
3. Use vector tools for clean shapes
4. Export optimized PNG

#### Adobe Illustrator
1. Open SVG template
2. Use artboards for different sizes
3. Apply global color swatches
4. Export asset packages

### Quality Assurance Checklist

#### Design Quality
- [ ] Icon is recognizable at 48x48
- [ ] Consistent stroke weight
- [ ] Proper visual balance
- [ ] Clear application context

#### Technical Quality
- [ ] 4096x4096 resolution
- [ ] Transparent background
- [ ] Proper file naming
- [ ] Optimized file size

#### Brand Consistency
- [ ] Matches family design language
- [ ] Uses correct blue background
- [ ] Appropriate accent colors
- [ ] Consistent visual weight

### Version Control

#### File Naming Convention
- `icon_[appname]_v[major].[minor].svg`
- `icon_[appname]_v[major].[minor]_4k.png`
- `icon_[appname]_current.svg` (symlink)

#### Changelog Format
```
## Version 1.0.0 - 2025-01-01
- Initial icon design
- Added all 20 applications
- Established design system
```

This detailed specification ensures all icons maintain visual consistency while providing unique, meaningful representations for each application in the ShareConnect ecosystem.