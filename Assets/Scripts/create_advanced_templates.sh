#!/bin/bash

# Advanced Icon Template Generator
# Creates sophisticated SVG templates with design guidelines

set -e

# Color definitions
BLUE_BACKGROUND="#4FC3F7"
WHITE_FOREGROUND="#FFFFFF"
BLACK_TEXT="#333333"
GUIDE_COLOR="#FF6B6B"

# Application design concepts
declare -A APP_CONCEPTS=(
    ["ShareConnector"]="Network nodes connected in star pattern"
    ["TransmissionConnect"]="Stylized butterfly with transmission gears"
    ["uTorrentConnect"]="µ symbol with download arrow integration"
    ["qBitConnect"]="Q letter with integrated torrent pieces"
    ["JDownloaderConnect"]="Download arrow with package/container symbols"
    ["PlexConnect"]="Plex-style media waves with play symbol"
    ["HomeAssistantConnect"]="Smart home with connected devices"
    ["PortainerConnect"]="Docker whale with container stack"
    ["JellyfinConnect"]="Jellyfish with media symbols"
    ["NetdataConnect"]="Dashboard with real-time metrics"
    ["WireGuardConnect"]="Shield with network waves"
    ["MatrixConnect"]="Matrix-style chat bubbles"
    ["GiteaConnect"]="Git branch with code symbols"
    ["OnlyOfficeConnect"]="Document with editing tools"
    ["MinecraftServerConnect"]="Minecraft block with server gear"
    ["SeafileConnect"]="Cloud with sync arrows"
    ["SyncthingConnect"]="Syncing folders with arrows"
    ["DuplicatiConnect"]="Backup vault with data streams"
    ["PaperlessNGConnect"]="Document scanner with OCR symbols"
    ["MotrixConnect"]="Download manager with speed indicators"
)

echo "Generating Advanced Icon Templates..."
echo "====================================="
echo ""

for app_name in "${!APP_CONCEPTS[@]}"; do
    concept="${APP_CONCEPTS[$app_name]}"
    app_lower=$(echo "$app_name" | tr '[:upper:]' '[:lower:]')
    
    echo "Creating advanced template for: $app_name"
    
    # Create advanced SVG template with design guides
    cat > "Assets/Logos/$app_name/icon_${app_lower}_advanced.svg" << EOF
<?xml version="1.0" encoding="UTF-8"?>
<svg width="4096" height="4096" viewBox="0 0 4096 4096" xmlns="http://www.w3.org/2000/svg">
  <!-- ============================================== -->
  <!-- ShareConnect Advanced Icon Template -->
  <!-- Application: $app_name -->
  <!-- Concept: $concept -->
  <!-- Version: 1.0.0 -->
  <!-- ============================================== -->
  
  <!-- DESIGN GUIDELINES -->
  <!-- 
  - Background: Light blue circle (#4FC3F7) at 90% opacity
  - Foreground: White icons with subtle gradients
  - Stroke: 120px uniform stroke weight
  - Padding: 400px from circle edge
  - Scale: Fill 70% of circle area (2520px diameter)
  - Export: 4096x4096 PNG with transparency
  -->
  
  <!-- DESIGN GRID (Comment out before export) -->
  <defs>
    <!-- Safe area guides -->
    <circle id="safe-area" cx="2048" cy="2048" r="1800" fill="none" stroke="$GUIDE_COLOR" stroke-width="4" stroke-dasharray="20,10" opacity="0.3"/>
    <circle id="icon-area" cx="2048" cy="2048" r="1260" fill="none" stroke="$GUIDE_COLOR" stroke-width="2" stroke-dasharray="10,5" opacity="0.3"/>
    
    <!-- Center guides -->
    <line id="center-vertical" x1="2048" y1="248" x2="2048" y2="3848" stroke="$GUIDE_COLOR" stroke-width="2" stroke-dasharray="5,5" opacity="0.2"/>
    <line id="center-horizontal" x1="248" y1="2048" x2="3848" y2="2048" stroke="$GUIDE_COLOR" stroke-width="2" stroke-dasharray="5,5" opacity="0.2"/>
  </defs>
  
  <!-- BACKGROUND -->
  <g id="background">
    <!-- Light blue circular background with gradient -->
    <defs>
      <radialGradient id="bgGradient" cx="0.5" cy="0.5" r="0.8">
        <stop offset="0%" stop-color="#6FD4FF" stop-opacity="0.95"/>
        <stop offset="100%" stop-color="$BLUE_BACKGROUND" stop-opacity="0.9"/>
      </radialGradient>
    </defs>
    <circle cx="2048" cy="2048" r="1800" fill="url(#bgGradient)"/>
    
    <!-- Subtle shadow for depth -->
    <defs>
      <filter id="shadow" x="-20%" y="-20%" width="140%" height="140%">
        <feDropShadow dx="40" dy="40" stdDeviation="60" flood-color="#000000" flood-opacity="0.15"/>
      </filter>
    </defs>
    <circle cx="2048" cy="2048" r="1800" fill="$BLUE_BACKGROUND" filter="url(#shadow)" opacity="0.3"/>
  </g>
  
  <!-- FOREGROUND ICON AREA -->
  <g id="foreground" transform="translate(2048, 2048)">
    <!-- Application-specific icon goes here -->
    <!-- Replace this placeholder with your design -->
    
    <!-- Placeholder: App initials with design concept -->
    <g id="placeholder-icon" opacity="0.8">
      <!-- Background for icon area -->
      <circle cx="0" cy="0" r="1000" fill="none" stroke="$WHITE_FOREGROUND" stroke-width="120" stroke-opacity="0.3"/>
      
      <!-- App initials -->
      <text x="0" y="0" text-anchor="middle" dominant-baseline="central" 
            font-family="Arial, sans-serif" font-size="600" font-weight="bold"
            fill="$WHITE_FOREGROUND" fill-opacity="0.7">
        ${app_name:0:2}
      </text>
      
      <!-- Concept indicator -->
      <circle cx="-400" cy="-400" r="80" fill="$WHITE_FOREGROUND" fill-opacity="0.5"/>
      <circle cx="400" cy="-400" r="80" fill="$WHITE_FOREGROUND" fill-opacity="0.5"/>
      <circle cx="-400" cy="400" r="80" fill="$WHITE_FOREGROUND" fill-opacity="0.5"/>
      <circle cx="400" cy="400" r="80" fill="$WHITE_FOREGROUND" fill-opacity="0.5"/>
    </g>
    
    <!-- DESIGN NOTES: -->
    <!-- 
    1. Keep icon within 2520px diameter circle
    2. Use 120px stroke weight for consistency
    3. Apply subtle gradients for depth
    4. Ensure recognizability at small sizes
    5. Maintain visual balance and symmetry
    -->
  </g>
  
  <!-- DESIGN GUIDES (Comment out before final export) -->
  <g id="design-guides">
    <use href="#safe-area"/>
    <use href="#icon-area"/>
    <use href="#center-vertical"/>
    <use href="#center-horizontal"/>
    
    <!-- Design notes text -->
    <text x="2048" y="100" text-anchor="middle" 
          font-family="Arial, sans-serif" font-size="60" 
          fill="$GUIDE_COLOR" font-weight="bold">
      $app_name - DESIGN TEMPLATE
    </text>
    
    <text x="2048" y="180" text-anchor="middle" 
          font-family="Arial, sans-serif" font-size="40" 
          fill="$GUIDE_COLOR" font-style="italic">
      Concept: $concept
    </text>
    
    <text x="2048" y="3900" text-anchor="middle" 
          font-family="Arial, sans-serif" font-size="40" 
          fill="$BLACK_TEXT" fill-opacity="0.6">
      Remove design guides before final export
    </text>
  </g>
  
  <!-- EXPORT INSTRUCTIONS -->
  <!-- 
  FINAL EXPORT STEPS:
  1. Comment out or remove the <g id="design-guides"> section
  2. Remove opacity from placeholder icon or replace with final design
  3. Export as PNG 4096x4096 with transparency
  4. Save as: icon_${app_lower}_4k.png
  5. Verify icon looks good at 48x48 size
  -->
</svg>
EOF
    
    echo "✓ Created: Assets/Logos/$app_name/icon_${app_lower}_advanced.svg"
    
    # Create simplified version without guides
    sed '/DESIGN GUIDES/,+30d' "Assets/Logos/$app_name/icon_${app_lower}_advanced.svg" | \
    sed '/DESIGN GRID/,+10d' | \
    sed '/EXPORT INSTRUCTIONS/,+10d' > "Assets/Logos/$app_name/icon_${app_lower}_clean.svg"
    
    echo "✓ Created: Assets/Logos/$app_name/icon_${app_lower}_clean.svg"
    echo ""
    
done

echo "Advanced template generation complete!"
echo ""
echo "Generated files per application:"
echo "- icon_[appname]_advanced.svg (with design guides)"
echo "- icon_[appname]_clean.svg (clean version)"
echo ""
echo "Design workflow:"
echo "1. Design using advanced.svg (with guides)"
echo "2. Export using clean.svg (without guides)"
echo "3. Save as PNG 4096x4096 with transparency"
echo ""
echo "Design tools recommended:"
echo "- Inkscape: Best for SVG editing"
echo "- GIMP: For raster effects and final export"
echo "- Adobe Illustrator: Professional vector design"