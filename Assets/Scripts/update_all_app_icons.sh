#!/bin/bash

# ShareConnect Icon Update Script
# This script helps update icons across all ShareConnect applications

set -e

# Color definitions for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Base project directory
PROJECT_ROOT="$(pwd)"

# List of all applications that need icon updates
APPS=(
    "ShareConnector"
    "Connectors/TransmissionConnect/TransmissionConnector"
    "Connectors/uTorrentConnect/uTorrentConnector"
    "Connectors/qBitConnect/qBitConnector"
    "Connectors/JDownloaderConnect/JDownloaderConnector"
    "Connectors/PlexConnect/PlexConnector"
    "Connectors/HomeAssistantConnect/HomeAssistantConnector"
    "Connectors/PortainerConnect/PortainerConnector"
    "Connectors/JellyfinConnect/JellyfinConnector"
    "Connectors/NetdataConnect/NetdataConnector"
    "Connectors/WireGuardConnect/WireGuardConnector"
    "Connectors/MatrixConnect/MatrixConnector"
    "Connectors/GiteaConnect/GiteaConnector"
    "Connectors/OnlyOfficeConnect/OnlyOfficeConnector"
    "Connectors/MinecraftServerConnect/MinecraftServerConnector"
    "Connectors/SeafileConnect/SeafileConnector"
    "Connectors/SyncthingConnect/SyncthingConnector"
    "Connectors/DuplicatiConnect/DuplicatiConnector"
    "Connectors/PaperlessNGConnect/PaperlessNGConnector"
    "Connectors/MotrixConnect/MotrixConnector"
)

echo -e "${BLUE}ShareConnect Icon Update Tool${NC}"
echo -e "${BLUE}==============================${NC}"
echo ""

# Function to check if directory exists
check_app_exists() {
    local app_path="$1"
    if [ ! -d "$app_path" ]; then
        echo -e "${YELLOW}Warning: $app_path not found${NC}"
        return 1
    fi
    return 0
}

# Function to check if res directory exists
check_res_exists() {
    local app_path="$1"
    if [ ! -d "$app_path/src/main/res" ]; then
        echo -e "${YELLOW}Warning: $app_path/src/main/res not found${NC}"
        return 1
    fi
    return 0
}

# Function to generate Android icon sizes
generate_android_icons() {
    local app_name="$1"
    local app_path="$2"
    local source_icon="Assets/Logos/$app_name/icon_${app_name,,}_4k.png"
    
    echo -e "${GREEN}Generating Android icons for $app_name${NC}"
    
    # Check if source icon exists
    if [ ! -f "$source_icon" ]; then
        echo -e "${YELLOW}Source icon not found: $source_icon${NC}"
        echo -e "${YELLOW}Using placeholder instead${NC}"
        source_icon="Assets/Logos/$app_name/icon_${app_name,,}_placeholder.png"
    fi
    
    # Create res directories if they don't exist
    mkdir -p "$app_path/src/main/res/mipmap-mdpi"
    mkdir -p "$app_path/src/main/res/mipmap-hdpi"
    mkdir -p "$app_path/src/main/res/mipmap-xhdpi"
    mkdir -p "$app_path/src/main/res/mipmap-xxhdpi"
    mkdir -p "$app_path/src/main/res/mipmap-xxxhdpi"
    mkdir -p "$app_path/src/main/res/mipmap-anydpi-v26"
    mkdir -p "$app_path/src/main/res/drawable"
    
    # Generate standard icons
    if command -v convert &> /dev/null; then
        convert "$source_icon" -resize 48x48 "$app_path/src/main/res/mipmap-mdpi/ic_launcher.png"
        convert "$source_icon" -resize 72x72 "$app_path/src/main/res/mipmap-hdpi/ic_launcher.png"
        convert "$source_icon" -resize 96x96 "$app_path/src/main/res/mipmap-xhdpi/ic_launcher.png"
        convert "$source_icon" -resize 144x144 "$app_path/src/main/res/mipmap-xxhdpi/ic_launcher.png"
        convert "$source_icon" -resize 192x192 "$app_path/src/main/res/mipmap-xxxhdpi/ic_launcher.png"
        
        # Generate round icons
        convert "$source_icon" -resize 48x48 -alpha set -background none -vignette 0x0 "$app_path/src/main/res/mipmap-mdpi/ic_launcher_round.png"
        convert "$source_icon" -resize 72x72 -alpha set -background none -vignette 0x0 "$app_path/src/main/res/mipmap-hdpi/ic_launcher_round.png"
        convert "$source_icon" -resize 96x96 -alpha set -background none -vignette 0x0 "$app_path/src/main/res/mipmap-xhdpi/ic_launcher_round.png"
        convert "$source_icon" -resize 144x144 -alpha set -background none -vignette 0x0 "$app_path/src/main/res/mipmap-xxhdpi/ic_launcher_round.png"
        convert "$source_icon" -resize 192x192 -alpha set -background none -vignette 0x0 "$app_path/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png"
        
        echo -e "${GREEN}✓ Generated Android icons for $app_name${NC}"
    else
        echo -e "${YELLOW}ImageMagick not available. Please install to generate icons.${NC}"
        echo -e "${YELLOW}Run: sudo apt install imagemagick (Ubuntu/Debian)${NC}"
        echo -e "${YELLOW}Or: brew install imagemagick (macOS)${NC}"
    fi
}

# Function to create adaptive icon XML
create_adaptive_icons() {
    local app_path="$1"
    
    # Create ic_launcher.xml for adaptive icons
    cat > "$app_path/src/main/res/mipmap-anydpi-v26/ic_launcher.xml" << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
EOF

    # Create ic_launcher_round.xml for adaptive icons
    cat > "$app_path/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml" << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@color/ic_launcher_background"/>
    <foreground android:drawable="@mipmap/ic_launcher_foreground"/>
</adaptive-icon>
EOF

    echo -e "${GREEN}✓ Created adaptive icon XML files${NC}"
}

# Function to update colors.xml
update_colors_xml() {
    local app_path="$1"
    local colors_file="$app_path/src/main/res/values/colors.xml"
    
    # Create colors directory if it doesn't exist
    mkdir -p "$app_path/src/main/res/values"
    
    # Check if colors.xml exists
    if [ ! -f "$colors_file" ]; then
        # Create new colors.xml
        cat > "$colors_file" << EOF
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#4FC3F7</color>
</resources>
EOF
    else
        # Check if ic_launcher_background color exists
        if ! grep -q "ic_launcher_background" "$colors_file"; then
            # Add the color before </resources>
            sed -i '/<\/resources>/i\    <color name="ic_launcher_background">#4FC3F7</color>' "$colors_file"
        fi
    fi
    
    echo -e "${GREEN}✓ Updated colors.xml${NC}"
}

# Main update process
for app_info in "${APPS[@]}"; do
    app_path="$PROJECT_ROOT/$app_info"
    app_name=$(basename "$app_info")
    
    echo -e "${BLUE}Processing: $app_name${NC}"
    echo -e "${BLUE}Path: $app_path${NC}"
    
    # Check if app exists
    if ! check_app_exists "$app_path"; then
        continue
    fi
    
    # Check if res directory exists
    if ! check_res_exists "$app_path"; then
        continue
    fi
    
    # Generate Android icons
    generate_android_icons "$app_name" "$app_path"
    
    # Create adaptive icons
    create_adaptive_icons "$app_path"
    
    # Update colors.xml
    update_colors_xml "$app_path"
    
    echo -e "${GREEN}✓ Completed $app_name${NC}"
    echo ""
    
done

echo -e "${GREEN}Icon update process completed!${NC}"
echo ""
echo -e "${YELLOW}Next steps:${NC}"
echo "1. Design final icons in vector format"
echo "2. Replace placeholder PNG files with final designs"
echo "3. Run this script again to update all applications"
echo "4. Test icons on Android devices and emulators"
echo "5. Build and verify each application"