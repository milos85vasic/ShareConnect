#!/bin/bash

# JDownloaderConnect Icon Generation Script
# Generates high-quality launcher icons and splash screen from Logo.jpeg

set -e  # Exit on any error

echo "JDownloaderConnect Icon Generation Script"
echo "========================================"

# Check if Logo.jpeg exists
if [ ! -f "Logo.jpeg" ]; then
    echo "Error: Logo.jpeg not found in Assets directory"
    exit 1
fi

# Check if required tools are installed
if ! command -v convert &> /dev/null; then
    echo "Error: ImageMagick is not installed. Please install ImageMagick to generate icons."
    echo "On macOS: brew install imagemagick"
    echo "On Ubuntu/Debian: sudo apt install imagemagick"
    echo "On CentOS/RHEL: sudo yum install ImageMagick"
    exit 1
fi

# Create Android resource directories
echo "Creating Android resource directories..."
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-mdpi
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-hdpi
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-xhdpi
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-xxhdpi
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-xxxhdpi
mkdir -p ../JDownloaderConnector/src/main/res/mipmap-anydpi-v26
mkdir -p ../JDownloaderConnector/src/main/res/drawable

# Get image dimensions and crop to square
echo "Processing Logo.jpeg..."
width=$(convert Logo.jpeg -format "%w" info:)
height=$(convert Logo.jpeg -format "%h" info:)
min_dim=$(( width < height ? width : height ))

echo "Original image: ${width}x${height}, cropping to ${min_dim}x${min_dim} square"
convert Logo.jpeg \
    -gravity center \
    -crop ${min_dim}x${min_dim}+0+0 \
    +repage \
    -quality 100 \
    Logo_square.png

# Remove white/transparent background to ensure clean alpha channel
echo "Processing background transparency..."
# First, try to remove white background
convert Logo_square.png \
    -fuzz 5% \
    -transparent white \
    -quality 100 \
    Logo_processed.png

# If the image has a transparent or pure white background, ensure proper alpha
# For logos with transparent/pure white backgrounds, this ensures clean edges
convert Logo_processed.png \
    -background none \
    -flatten \
    -quality 100 \
    Logo_final.png

# Generate adaptive icon foreground (108x108) - highest quality
echo "Generating adaptive icon foreground (108x108)..."
convert Logo_final.png \
    -resize 108x108 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/drawable/ic_launcher_foreground.png

# Generate mipmap icons for different densities with highest quality
echo "Generating mipmap launcher icons..."

# mdpi (48x48) - baseline density
convert Logo_final.png \
    -resize 48x48 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-mdpi/ic_launcher.png

convert Logo_final.png \
    -resize 48x48 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-mdpi/ic_launcher_round.png

# hdpi (72x72) - 1.5x mdpi
convert Logo_final.png \
    -resize 72x72 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-hdpi/ic_launcher.png

convert Logo_final.png \
    -resize 72x72 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-hdpi/ic_launcher_round.png

# xhdpi (96x96) - 2x mdpi
convert Logo_final.png \
    -resize 96x96 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xhdpi/ic_launcher.png

convert Logo_final.png \
    -resize 96x96 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xhdpi/ic_launcher_round.png

# xxhdpi (144x144) - 3x mdpi
convert Logo_final.png \
    -resize 144x144 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xxhdpi/ic_launcher.png

convert Logo_final.png \
    -resize 144x144 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xxhdpi/ic_launcher_round.png

# xxxhdpi (192x192) - 4x mdpi, highest density
convert Logo_final.png \
    -resize 192x192 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xxxhdpi/ic_launcher.png

convert Logo_final.png \
    -resize 192x192 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

# Generate high-resolution splash screen logo (2048x2048 for maximum quality)
echo "Generating high-resolution splash screen logo (2048x2048)..."
convert Logo_final.png \
    -resize 2048x2048 \
    -filter Lanczos \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/drawable/splash_logo.png

# Generate adaptive icon background (solid color based on theme)
echo "Generating adaptive icon background..."
# Using a professional blue-gray color that matches Material Design 3 theme
convert -size 108x108 \
    xc:"#4F378B" \
    -quality 100 \
    -define png:compression-level=9 \
    ../JDownloaderConnector/src/main/res/drawable/ic_launcher_background.png

# Create adaptive icon XML files
echo "Creating adaptive icon XML files..."
cat > ../JDownloaderConnector/src/main/res/mipmap-anydpi-v26/ic_launcher.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

cat > ../JDownloaderConnector/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml << 'EOF'
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# Clean up temporary files
echo "Cleaning up temporary files..."
rm -f Logo_square.png Logo_processed.png Logo_final.png

echo ""
echo "✅ JDownloaderConnect icon generation completed successfully!"
echo ""
echo "Generated assets:"
echo "  • Launcher icons: mdpi (48px) through xxxhdpi (192px)"
echo "  • Adaptive icons: 108x108 foreground + background"
echo "  • Splash screen: 2048x2048 high-resolution logo"
echo "  • All icons use Lanczos filtering for maximum quality"
echo ""
echo "Next steps:"
echo "  1. Review generated icons in ../JDownloaderConnector/src/main/res/"
echo "  2. Run './gradlew assembleDebug' to build and test"
echo "  3. Run icon verification tests"