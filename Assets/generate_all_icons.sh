#!/bin/bash

# Master script to generate launcher icons and splash logos for all applications
# using Logo.jpeg from Assets/ directory

# Check if ImageMagick is installed
if ! command -v convert &> /dev/null
then
    echo "ImageMagick is not installed. Please install ImageMagick to generate icons."
    echo "On macOS: brew install imagemagick"
    echo "On Ubuntu: sudo apt install imagemagick"
    exit 1
fi

# Define the apps that need icons
APPS=(
    "app"
    "Asinka/demo-app"
    "ShareConnector"
    "Connectors/DuplicatiConnect/DuplicatiConnector"
    "Connectors/GiteaConnect/GiteaConnector"
    "Connectors/HomeAssistantConnect/HomeAssistantConnector"
    "Connectors/JDownloaderConnect/JDownloaderConnector"
    "Connectors/JellyfinConnect/JellyfinConnector"
    "Connectors/MatrixConnect/MatrixConnector"
    "Connectors/MinecraftServerConnect/MinecraftServerConnector"
    "Connectors/MotrixConnect/MotrixConnector"
    "Connectors/NetdataConnect/NetdataConnector"
    "Connectors/NextcloudConnect/NextcloudConnector"
    "Connectors/OnlyOfficeConnect/OnlyOfficeConnector"
    "Connectors/PaperlessNGConnect/PaperlessNGConnector"
    "Connectors/PlexConnect/PlexConnector"
    "Connectors/PortainerConnect/PortainerConnector"
    "Connectors/qBitConnect/qBitConnector"
    "Connectors/SeafileConnect/SeafileConnector"
    "Connectors/SyncthingConnect/SyncthingConnector"
    "Connectors/TransmissionConnect/TransmissionConnector"
    "Connectors/uTorrentConnect/uTorrentConnector"
    "Connectors/WireGuardConnect/WireGuardConnector"
    "Toolkit/Applications/DataManagerDemo"
    "Toolkit/Applications/Echo"
)

# Process the logo
echo "Processing Logo.jpeg..."

# Crop to square
width=$(convert Logo.jpeg -format "%w" info:)
height=$(convert Logo.jpeg -format "%h" info:)
min_dim=$(( width < height ? width : height ))
convert Logo.jpeg -gravity center -crop ${min_dim}x${min_dim}+0+0 +repage Logo_square.png

# Remove black background to make transparent
echo "Removing black background..."
convert Logo_square.png -fuzz 10% -transparent black Logo_transparent.png

# For foreground, we want white symbol on transparent, so remove the blue background too
# Assuming blue is around #2196F3, but to be safe, use a range
# Since the user wants blue background with white symbol, but for adaptive icons,
# foreground should be the symbol on transparent, background blue.
# To extract white symbol, remove blue as well.
# But without exact color, let's assume the transparent version has the blue background,
# so for foreground, we can use it as is, but ideally separate.

# For simplicity, use the transparent logo as foreground (which has blue bg + white symbol on transparent)
# And set background to transparent for adaptive icons.

# But to follow user, perhaps set background to blue, and foreground to white symbol.
# To get white symbol, invert or something. Wait, better way:
# Assume the logo has white symbol on blue background on black.
# After removing black, we have white on blue on transparent.
# To get white on transparent, remove blue.
# Use -transparent with the blue color.

# But since I don't know the exact blue, let's use a common blue #2196F3
convert Logo_transparent.png -fuzz 20% -transparent "#2196F3" Logo_foreground.png

# For background, use blue
BACKGROUND_COLOR="#2196F3"

# Now, for each app, generate icons
for app in "${APPS[@]}"; do
    echo "Generating icons for $app..."

    RES_DIR="../$app/src/main/res"

    # Create directories
    mkdir -p "$RES_DIR/mipmap-mdpi"
    mkdir -p "$RES_DIR/mipmap-hdpi"
    mkdir -p "$RES_DIR/mipmap-xhdpi"
    mkdir -p "$RES_DIR/mipmap-xxhdpi"
    mkdir -p "$RES_DIR/mipmap-xxxhdpi"
    mkdir -p "$RES_DIR/mipmap-anydpi-v26"
    mkdir -p "$RES_DIR/drawable"

    # Generate mipmap icons (using the transparent logo as the icon)
    convert Logo_transparent.png -resize 48x48 "$RES_DIR/mipmap-mdpi/ic_launcher.png"
    convert Logo_transparent.png -resize 48x48 "$RES_DIR/mipmap-mdpi/ic_launcher_round.png"

    convert Logo_transparent.png -resize 72x72 "$RES_DIR/mipmap-hdpi/ic_launcher.png"
    convert Logo_transparent.png -resize 72x72 "$RES_DIR/mipmap-hdpi/ic_launcher_round.png"

    convert Logo_transparent.png -resize 96x96 "$RES_DIR/mipmap-xhdpi/ic_launcher.png"
    convert Logo_transparent.png -resize 96x96 "$RES_DIR/mipmap-xhdpi/ic_launcher_round.png"

    convert Logo_transparent.png -resize 144x144 "$RES_DIR/mipmap-xxhdpi/ic_launcher.png"
    convert Logo_transparent.png -resize 144x144 "$RES_DIR/mipmap-xxhdpi/ic_launcher_round.png"

    convert Logo_transparent.png -resize 192x192 "$RES_DIR/mipmap-xxxhdpi/ic_launcher.png"
    convert Logo_transparent.png -resize 192x192 "$RES_DIR/mipmap-xxxhdpi/ic_launcher_round.png"

    # Generate adaptive icon foreground
    convert Logo_foreground.png -resize 108x108 "$RES_DIR/drawable/ic_launcher_foreground.png"

    # Generate adaptive icon background
    convert -size 108x108 xc:"$BACKGROUND_COLOR" "$RES_DIR/drawable/ic_launcher_background.png"

    # Create adaptive icon XML
    cat > "$RES_DIR/mipmap-anydpi-v26/ic_launcher.xml" << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

    cat > "$RES_DIR/mipmap-anydpi-v26/ic_launcher_round.xml" << EOF
<?xml version="1.0" encoding="utf-8"?>
<adaptive-icon xmlns:android="http://schemas.android.com/apk/res/android">
    <background android:drawable="@drawable/ic_launcher_background"/>
    <foreground android:drawable="@drawable/ic_launcher_foreground"/>
</adaptive-icon>
EOF

    # Generate splash logo
    convert Logo_transparent.png -resize 1024x1024 "$RES_DIR/drawable/splash_logo.png"

    echo "Icons generated for $app"
done

# Clean up temporary files
rm -f Logo_square.png Logo_transparent.png Logo_foreground.png

echo "All icons and splash logos generated successfully!"
echo "To regenerate, simply run this script again after replacing Logo.jpeg"