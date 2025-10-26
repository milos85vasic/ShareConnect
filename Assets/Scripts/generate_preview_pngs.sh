#!/bin/bash

# Generate preview PNG files from SVG templates
# Creates smaller PNG files (512x512) for design review comparison

set -e

echo "🔄 Generating preview PNG files from SVG templates..."
echo "===================================================="
echo ""

# Check if ImageMagick is available
if ! command -v convert &> /dev/null; then
    echo "❌ ImageMagick not found. Please install:"
    echo "   Ubuntu/Debian: sudo apt install imagemagick"
    echo "   macOS: brew install imagemagick"
    echo "   Windows: Download from https://imagemagick.org"
    exit 1
fi

# Get absolute project path
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"

# List of all applications
APPS=(
    "ShareConnector"
    "TransmissionConnect"
    "uTorrentConnect"
    "qBitConnect"
    "JDownloaderConnect"
    "PlexConnect"
    "JellyfinConnect"
    "HomeAssistantConnect"
    "PortainerConnect"
    "NetdataConnect"
    "WireGuardConnect"
    "MatrixConnect"
    "GiteaConnect"
    "OnlyOfficeConnect"
    "MinecraftServerConnect"
    "SeafileConnect"
    "SyncthingConnect"
    "DuplicatiConnect"
    "PaperlessNGConnect"
    "MotrixConnect"
)

for app_name in "${APPS[@]}"; do
    app_lower=$(echo "$app_name" | tr '[:upper:]' '[:lower:]')
    
    echo "Processing: $app_name"
    
    # Source SVG files with absolute paths
    advanced_svg="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_advanced.svg"
    clean_svg="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_clean.svg"
    
    # Output preview PNG files (512x512 for web display)
    advanced_preview="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_advanced_preview.png"
    clean_preview="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_clean_preview.png"
    
    # Generate preview PNG from advanced SVG (with design guides)
    if [ -f "$advanced_svg" ]; then
        echo "  📄 Generating advanced preview PNG (512x512)..."
        convert -background transparent -resize 512x512 "$advanced_svg" "$advanced_preview" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "  ✅ Created: $(basename "$advanced_preview")"
        else
            echo "  ❌ Failed to create: $(basename "$advanced_preview")"
        fi
    else
        echo "  ⚠️  Advanced SVG not found: $(basename "$advanced_svg")"
    fi
    
    # Generate preview PNG from clean SVG (without guides)
    if [ -f "$clean_svg" ]; then
        echo "  📄 Generating clean preview PNG (512x512)..."
        convert -background transparent -resize 512x512 "$clean_svg" "$clean_preview" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "  ✅ Created: $(basename "$clean_preview")"
        else
            echo "  ❌ Failed to create: $(basename "$clean_preview")"
        fi
    else
        echo "  ⚠️  Clean SVG not found: $(basename "$clean_svg")"
    fi
    
    echo ""
    
done

echo "🎉 Preview PNG generation complete!"
echo ""
echo "📊 Preview files generated per application:"
echo "- icon_[appname]_advanced_preview.png (512x512 with guides)"
echo "- icon_[appname]_clean_preview.png (512x512 without guides)"
echo ""
echo "🔍 You can now compare SVG and PNG versions visually"
echo "   in the updated design review pages."