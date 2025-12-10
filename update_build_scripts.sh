#!/bin/bash

# Modules to update
MODULES=(
    "ProfileSync"
    "HistorySync"
    "RSSSync"
    "TorrentSharingSync"
    "PreferencesSync"
    "LanguageSync"
)

for module in "${MODULES[@]}"; do
    MODULE_PATH="/home/milosvasic/Projects/ShareConnect/${module}"
    BUILD_FILE="${MODULE_PATH}/build.gradle.kts"
    
    if [ -f "$BUILD_FILE" ]; then
        cp "/home/milosvasic/Projects/ShareConnect/BookmarkSync/build.gradle.kts" "$BUILD_FILE"
        echo "Updated ${BUILD_FILE}"
    else
        echo "Build file not found for module ${module}"
    fi
done

echo "Build script updates complete"