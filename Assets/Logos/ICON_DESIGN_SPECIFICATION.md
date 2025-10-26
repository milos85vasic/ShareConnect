# ShareConnect Icon Family Design Specification

## Overview
This document outlines the design specifications for the ShareConnect application icon family. All icons follow a consistent design language while maintaining unique identities for each application.

## Design Principles

### Color Palette
- **Background**: Light blue circular background (#4FC3F7)
- **Foreground**: White/light gray icons with subtle gradients
- **Accent Colors**: Application-specific accent colors for visual distinction
- **Transparency**: Transparent background around the circular blue area

### Icon Style
- **Family Unity**: All icons use the same circular blue background
- **Distinctive Foreground**: Each app has a unique, context-appropriate icon
- **Modern & Clean**: Simple, recognizable symbols
- **4K Resolution**: 4096x4096 pixels
- **Format**: PNG with transparency

## Application Icons Design

### 1. ShareConnector
- **Concept**: Universal sharing and connectivity
- **Icon**: Network nodes connected in a star pattern
- **Accent**: Multi-color nodes representing different services

### 2. TransmissionConnect
- **Concept**: Torrent client with butterfly logo
- **Icon**: Stylized butterfly with transmission gears
- **Accent**: Orange/amber colors

### 3. uTorrentConnect
- **Concept**: uTorrent client integration
- **Icon**: µ symbol with download arrow integration
- **Accent**: Blue gradient

### 4. qBitConnect
- **Concept**: qBittorrent client
- **Icon**: Q letter with integrated torrent pieces
- **Accent**: Purple gradient

### 5. JDownloaderConnect
- **Concept**: Download management
- **Icon**: Download arrow with package/container symbols
- **Accent**: Green gradient

### 6. PlexConnect
- **Concept**: Media server and streaming
- **Icon**: Plex-style media waves with play symbol
- **Accent**: Orange/yellow gradient

### 7. HomeAssistantConnect
- **Concept**: Home automation
- **Icon**: Smart home with connected devices
- **Accent**: Blue gradient

### 8. PortainerConnect
- **Concept**: Container management
- **Icon**: Docker whale with container stack
- **Accent**: Blue/teal gradient

### 9. JellyfinConnect
- **Concept**: Media server
- **Icon**: Jellyfish with media symbols
- **Accent**: Purple gradient

### 10. NetdataConnect
- **Concept**: System monitoring
- **Icon**: Dashboard with real-time metrics
- **Accent**: Green gradient

### 11. WireGuardConnect
- **Concept**: VPN connectivity
- **Icon**: Shield with network waves
- **Accent**: Blue gradient

### 12. MatrixConnect
- **Concept**: Communication platform
- **Icon**: Matrix-style chat bubbles
- **Accent**: Green gradient

### 13. GiteaConnect
- **Concept**: Git repository hosting
- **Icon**: Git branch with code symbols
- **Accent**: Orange gradient

### 14. OnlyOfficeConnect
- **Concept**: Office suite
- **Icon**: Document with editing tools
- **Accent**: Blue gradient

### 15. MinecraftServerConnect
- **Concept**: Game server management
- **Icon**: Minecraft block with server gear
- **Accent**: Green gradient

### 16. SeafileConnect
- **Concept**: File synchronization
- **Icon**: Cloud with sync arrows
- **Accent**: Blue gradient

### 17. SyncthingConnect
- **Concept**: File synchronization
- **Icon**: Syncing folders with arrows
- **Accent**: Green gradient

### 18. DuplicatiConnect
- **Concept**: Backup solution
- **Icon**: Backup vault with data streams
- **Accent**: Blue gradient

### 19. PaperlessNGConnect
- **Concept**: Document management
- **Icon**: Document scanner with OCR symbols
- **Accent**: Orange gradient

### 20. MotrixConnect
- **Concept**: Download manager
- **Icon**: Download manager with speed indicators
- **Accent**: Purple gradient

## Technical Specifications

### File Structure
```
Assets/Logos/
├── ShareConnector/
│   └── icon_shareconnect_4k.png
├── TransmissionConnect/
│   └── icon_transmission_4k.png
├── uTorrentConnect/
│   └── icon_utorrent_4k.png
├── qBitConnect/
│   └── icon_qbit_4k.png
├── JDownloaderConnect/
│   └── icon_jdownloader_4k.png
├── PlexConnect/
│   └── icon_plex_4k.png
├── HomeAssistantConnect/
│   └── icon_homeassistant_4k.png
├── PortainerConnect/
│   └── icon_portainer_4k.png
├── JellyfinConnect/
│   └── icon_jellyfin_4k.png
├── NetdataConnect/
│   └── icon_netdata_4k.png
├── WireGuardConnect/
│   └── icon_wireguard_4k.png
├── MatrixConnect/
│   └── icon_matrix_4k.png
├── GiteaConnect/
│   └── icon_gitea_4k.png
├── OnlyOfficeConnect/
│   └── icon_onlyoffice_4k.png
├── MinecraftServerConnect/
│   └── icon_minecraft_4k.png
├── SeafileConnect/
│   └── icon_seafile_4k.png
├── SyncthingConnect/
│   └── icon_syncthing_4k.png
├── DuplicatiConnect/
│   └── icon_duplicati_4k.png
├── PaperlessNGConnect/
│   └── icon_paperless_4k.png
└── MotrixConnect/
    └── icon_motrix_4k.png
```

### File Format
- **Resolution**: 4096x4096 pixels
- **Format**: PNG with transparency
- **Color Mode**: RGB
- **Background**: Transparent with circular blue element
- **File Naming**: `icon_{appname}_4k.png`

### Visual Consistency
- All icons maintain the same circular blue background
- Foreground elements are centered and properly scaled
- Consistent stroke weights and visual hierarchy
- Clear visual distinction between applications

## Implementation Notes
- Icons should be easily recognizable at small sizes
- Maintain brand consistency across the entire family
- Ensure accessibility and color contrast
- Support both light and dark themes
- Optimize for Android launcher display