#!/bin/bash

# Script to fix all test imports in uTorrentConnect
# This replaces old package references with new ones

TEST_DIR="/home/milosvasic/Projects/ShareConnect/Connectors/uTorrentConnect/uTorrentConnector/src/test"

echo "Fixing test imports in uTorrentConnect..."

# Fix Java files
find "$TEST_DIR" -name "*.java" -type f -exec sed -i \
  -e 's|net\.yupol\.transmissionremote\.app\.|com.shareconnect.utorrentconnect.|g' \
  {} +

# Fix Kotlin files
find "$TEST_DIR" -name "*.kt" -type f -exec sed -i \
  -e 's|net\.yupol\.transmissionremote\.app\.|com.shareconnect.utorrentconnect.|g' \
  {} +

echo "Test import fixes completed!"
echo "Run: ./gradlew :uTorrentConnector:test"
