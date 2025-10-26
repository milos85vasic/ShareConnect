#!/bin/bash

# Generate PNG files from SVG templates
# Creates proper 4096x4096 PNG files with transparency

set -e

echo "🔄 Generating PNG files from SVG templates..."
echo "============================================="
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
    
    # Output PNG files with absolute paths
    advanced_png="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_advanced.png"
    clean_png="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_clean.png"
    final_png="$PROJECT_ROOT/Assets/Logos/$app_name/icon_${app_lower}_4k.png"
    
    # Generate PNG from advanced SVG (with design guides)
    if [ -f "$advanced_svg" ]; then
        echo "  📄 Generating advanced PNG (with guides)..."
        convert -background transparent -resize 4096x4096 "$advanced_svg" "$advanced_png" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "  ✅ Created: $(basename "$advanced_png")"
        else
            echo "  ❌ Failed to create: $(basename "$advanced_png")"
        fi
    else
        echo "  ⚠️  Advanced SVG not found: $(basename "$advanced_svg")"
    fi
    
    # Generate PNG from clean SVG (without guides)
    if [ -f "$clean_svg" ]; then
        echo "  📄 Generating clean PNG (without guides)..."
        convert -background transparent -resize 4096x4096 "$clean_svg" "$clean_png" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "  ✅ Created: $(basename "$clean_png")"
        else
            echo "  ❌ Failed to create: $(basename "$clean_png")"
        fi
    else
        echo "  ⚠️  Clean SVG not found: $(basename "$clean_svg")"
    fi
    
    # Create final 4K PNG (use clean version as final)
    if [ -f "$clean_svg" ]; then
        echo "  📄 Generating final 4K PNG..."
        convert -background transparent -resize 4096x4096 "$clean_svg" "$final_png" 2>/dev/null
        if [ $? -eq 0 ]; then
            echo "  ✅ Created: $(basename "$final_png")"
        else
            echo "  ❌ Failed to create: $(basename "$final_png")"
        fi
    else
        echo "  ⚠️  Cannot create final PNG - clean SVG missing"
    fi
    
    echo ""
    
done

echo "🎉 PNG generation complete!"
echo ""
echo "📊 Files generated per application:"
echo "- icon_[appname]_advanced.png (with design guides)"
echo "- icon_[appname]_clean.png (without guides)"
echo "- icon_[appname]_4k.png (final version)"
echo ""
echo "🔍 You can now compare SVG and PNG versions visually"
echo "   in the updated design review pages."