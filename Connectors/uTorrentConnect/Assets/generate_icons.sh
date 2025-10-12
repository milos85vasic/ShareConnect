#!/bin/bash

# Create Android mipmap directories
mkdir -p ../uTorrentConnector/src/main/res/mipmap-mdpi
mkdir -p ../uTorrentConnector/src/main/res/mipmap-hdpi
mkdir -p ../uTorrentConnector/src/main/res/mipmap-xhdpi
mkdir -p ../uTorrentConnector/src/main/res/mipmap-xxhdpi
mkdir -p ../uTorrentConnector/src/main/res/mipmap-xxxhdpi

# Create adaptive icon directories
mkdir -p ../uTorrentConnector/src/main/res/mipmap-anydpi-v26

# Check if required tools are installed
if ! command -v convert &> /dev/null
then
    echo "ImageMagick is not installed. Please install ImageMagick to generate icons."
    echo "On macOS: brew install imagemagick"
    echo "On Ubuntu: sudo apt install imagemagick"
    exit 1
fi

# Crop Logo.jpeg to square
echo "Cropping Logo.jpeg to square..."
width=$(convert Logo.jpeg -format "%w" info:)
height=$(convert Logo.jpeg -format "%h" info:)
min_dim=$(( width < height ? width : height ))
convert Logo.jpeg -gravity center -crop ${min_dim}x${min_dim}+0+0 +repage Logo_square.png

# Remove white background to make transparent (keeping white letters/symbols)
echo "Removing white background..."
convert Logo_square.png -fuzz 10% -fill none -draw "matte 0,0 floodfill" Logo_square_transparent.png

# Generate adaptive icon foreground (PNG format)
echo "Generating adaptive icon foreground..."
convert Logo_square_transparent.png -resize 108x108 ../uTorrentConnector/src/main/res/drawable/ic_launcher_foreground.png

# Generate mipmap icons for different densities
echo "Generating mipmap icons..."

# mdpi (48x48)
convert Logo_square_transparent.png -resize 48x48 ../uTorrentConnector/src/main/res/mipmap-mdpi/ic_launcher.png
convert Logo_square_transparent.png -resize 48x48 ../uTorrentConnector/src/main/res/mipmap-mdpi/ic_launcher_round.png

# hdpi (72x72)
convert Logo_square_transparent.png -resize 72x72 ../uTorrentConnector/src/main/res/mipmap-hdpi/ic_launcher.png
convert Logo_square_transparent.png -resize 72x72 ../uTorrentConnector/src/main/res/mipmap-hdpi/ic_launcher_round.png

# xhdpi (96x96)
convert Logo_square_transparent.png -resize 96x96 ../uTorrentConnector/src/main/res/mipmap-xhdpi/ic_launcher.png
convert Logo_square_transparent.png -resize 96x96 ../uTorrentConnector/src/main/res/mipmap-xhdpi/ic_launcher_round.png

# xxhdpi (144x144)
convert Logo_square_transparent.png -resize 144x144 ../uTorrentConnector/src/main/res/mipmap-xxhdpi/ic_launcher.png
convert Logo_square_transparent.png -resize 144x144 ../uTorrentConnector/src/main/res/mipmap-xxhdpi/ic_launcher_round.png

# xxxhdpi (192x192)
convert Logo_square_transparent.png -resize 192x192 ../uTorrentConnector/src/main/res/mipmap-xxxhdpi/ic_launcher.png
convert Logo_square_transparent.png -resize 192x192 ../uTorrentConnector/src/main/res/mipmap-xxxhdpi/ic_launcher_round.png

# Generate splash screen logos
echo "Generating splash screen logos..."

# Splash logo (1024x1024 for high resolution)
convert Logo_square_transparent.png -resize 1024x1024 ../uTorrentConnector/src/main/res/drawable/splash_logo.png

# Generate adaptive icon background (solid color)
echo "Generating adaptive icon background..."
convert -size 108x108 xc:"#607D8B" ../uTorrentConnector/src/main/res/drawable/ic_background.png

# Create adaptive icon XML
echo "Creating adaptive icon XML..."
cat > ../uTorrentConnector/src/main/res/mipmap-anydpi-v26/ic_launcher.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

cat > ../uTorrentConnector/src/main/res/mipmap-anydpi-v26/ic_launcher_round.xml << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

# Clean up temporary files
rm Logo_square.png Logo_square_transparent.png

echo "Android mipmap directories and icons created successfully!"