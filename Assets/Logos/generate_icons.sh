#!/bin/bash

# ShareConnect Icon Generation Script
# This script generates placeholder icons for all ShareConnect applications
# Replace with actual icon generation tools like Inkscape, GIMP, or custom scripts

# Color definitions
BLUE_BACKGROUND="#4FC3F7"
WHITE_FOREGROUND="#FFFFFF"

# Application directories and descriptions
APPS=(
    "ShareConnector:Network nodes connected in a star pattern"
    "TransmissionConnect:Stylized butterfly with transmission gears"
    "uTorrentConnect:µ symbol with download arrow integration"
    "qBitConnect:Q letter with integrated torrent pieces"
    "JDownloaderConnect:Download arrow with package/container symbols"
    "PlexConnect:Plex-style media waves with play symbol"
    "HomeAssistantConnect:Smart home with connected devices"
    "PortainerConnect:Docker whale with container stack"
    "JellyfinConnect:Jellyfish with media symbols"
    "NetdataConnect:Dashboard with real-time metrics"
    "WireGuardConnect:Shield with network waves"
    "MatrixConnect:Matrix-style chat bubbles"
    "GiteaConnect:Git branch with code symbols"
    "OnlyOfficeConnect:Document with editing tools"
    "MinecraftServerConnect:Minecraft block with server gear"
    "SeafileConnect:Cloud with sync arrows"
    "SyncthingConnect:Syncing folders with arrows"
    "DuplicatiConnect:Backup vault with data streams"
    "PaperlessNGConnect:Document scanner with OCR symbols"
    "MotrixConnect:Download manager with speed indicators"
)

echo "ShareConnect Icon Generation"
echo "============================"
echo ""

# Create SVG templates for each application
for app_info in "${APPS[@]}"; do
    IFS=':' read -r app_name description <<< "$app_info"
    
    echo "Creating template for: $app_name"
    echo "Description: $description"
    
    # Create SVG template
    cat > "Assets/Logos/$app_name/icon_${app_name,,}_template.svg" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<svg width="4096" height="4096" viewBox="0 0 4096 4096" xmlns="http://www.w3.org/2000/svg">
  <!-- ShareConnect Icon Template for $app_name -->
  <!-- Description: $description -->
  
  <!-- Background: Light blue circle -->
  <circle cx="2048" cy="2048" r="1800" fill="$BLUE_BACKGROUND" fill-opacity="0.9"/>
  
  <!-- Foreground: Application-specific icon -->
  <!-- TODO: Replace with actual $app_name icon design -->
  <text x="2048" y="2048" text-anchor="middle" 
        font-family="Arial, sans-serif" font-size="800" 
        fill="$WHITE_FOREGROUND" font-weight="bold">
    ${app_name:0:2}
  </text>
  
  <!-- Application name for reference -->
  <text x="2048" y="3500" text-anchor="middle" 
        font-family="Arial, sans-serif" font-size="200" 
        fill="#666666" font-weight="normal">
    $app_name
  </text>
</svg>
EOF
    
    echo "Created: Assets/Logos/$app_name/icon_${app_name,,}_template.svg"
    echo ""
    
    # Create placeholder PNG (using convert command if available)
    if command -v convert &> /dev/null; then
        convert -size 4096x4096 xc:transparent \
                -fill "$BLUE_BACKGROUND" -draw "circle 2048,2048 2048,3848" \
                -fill "$WHITE_FOREGROUND" -pointsize 800 -gravity center \
                -annotate +0+0 "${app_name:0:2}" \
                "Assets/Logos/$app_name/icon_${app_name,,}_placeholder.png"
        echo "Created placeholder PNG: Assets/Logos/$app_name/icon_${app_name,,}_placeholder.png"
    else
        echo "ImageMagick not available. Please install to generate placeholder PNGs."
        echo "Run: sudo apt install imagemagick (Ubuntu/Debian) or brew install imagemagick (macOS)"
    fi
    
    echo "---"
    echo ""
    
done

echo "Icon generation complete!"
echo ""
echo "Next steps:"
echo "1. Design actual icons in vector format (SVG recommended)"
echo "2. Export to 4096x4096 PNG with transparency"
echo "3. Replace placeholder files with final designs"
echo "4. Test icons at various sizes (from 48x48 to 4096x4096)"
echo ""
echo "Tools for icon creation:"
echo "- Inkscape (free, vector graphics)"
echo "- GIMP (free, raster graphics)"
echo "- Adobe Illustrator (professional)"
echo "- Figma (web-based, collaborative)"