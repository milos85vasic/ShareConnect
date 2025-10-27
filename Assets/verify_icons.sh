#!/bin/bash

# Verification script to check that all apps have the new icons and splash logos

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

echo "Verifying icons and splash logos for all applications..."

FAILED=0

for app in "${APPS[@]}"; do
    RES_DIR="../$app/src/main/res"

    echo "Checking $app..."

    # Check mipmap icons
    for density in mdpi hdpi xhdpi xxhdpi xxxhdpi; do
        if [ ! -f "$RES_DIR/mipmap-$density/ic_launcher.png" ]; then
            echo "ERROR: Missing ic_launcher.png in $RES_DIR/mipmap-$density"
            FAILED=1
        fi
        if [ ! -f "$RES_DIR/mipmap-$density/ic_launcher_round.png" ]; then
            echo "ERROR: Missing ic_launcher_round.png in $RES_DIR/mipmap-$density"
            FAILED=1
        fi
    done

    # Check adaptive icons
    if [ ! -d "$RES_DIR/mipmap-anydpi-v26" ]; then
        echo "ERROR: Missing mipmap-anydpi-v26 directory in $RES_DIR"
        FAILED=1
    else
        if [ ! -f "$RES_DIR/mipmap-anydpi-v26/ic_launcher.xml" ]; then
            echo "ERROR: Missing ic_launcher.xml in $RES_DIR/mipmap-anydpi-v26"
            FAILED=1
        fi
        if [ ! -f "$RES_DIR/mipmap-anydpi-v26/ic_launcher_round.xml" ]; then
            echo "ERROR: Missing ic_launcher_round.xml in $RES_DIR/mipmap-anydpi-v26"
            FAILED=1
        fi
    fi

    # Check drawable files
    if [ ! -f "$RES_DIR/drawable/ic_launcher_foreground.png" ]; then
        echo "ERROR: Missing ic_launcher_foreground.png in $RES_DIR/drawable"
        FAILED=1
    fi
    if [ ! -f "$RES_DIR/drawable/ic_launcher_background.png" ]; then
        echo "ERROR: Missing ic_launcher_background.png in $RES_DIR/drawable"
        FAILED=1
    fi
    if [ ! -f "$RES_DIR/drawable/splash_logo.png" ]; then
        echo "ERROR: Missing splash_logo.png in $RES_DIR/drawable"
        FAILED=1
    fi

    # Check file sizes (should not be empty)
    for file in "$RES_DIR/mipmap-mdpi/ic_launcher.png" "$RES_DIR/drawable/ic_launcher_foreground.png" "$RES_DIR/drawable/splash_logo.png"; do
        if [ -f "$file" ]; then
            size=$(stat -c%s "$file")
            if [ "$size" -lt 100 ]; then
                echo "ERROR: $file is too small ($size bytes), likely corrupted"
                FAILED=1
            fi
        fi
    done

    echo "$app checked."
done

if [ $FAILED -eq 0 ]; then
    echo "All icons and splash logos verified successfully!"
    exit 0
else
    echo "Some icons or splash logos are missing or corrupted!"
    exit 1
fi