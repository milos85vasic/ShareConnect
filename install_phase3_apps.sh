#!/bin/bash

# Install all Phase 3 ShareConnect applications to connected device
# Usage: ./install_phase3_apps.sh

set -e

echo "=========================================="
echo "Installing Phase 3 Applications"
echo "=========================================="
echo ""

# Check for connected device
if ! adb devices | grep -q "device$"; then
    echo "ERROR: No Android device connected"
    echo "Please connect a device or start an emulator"
    exit 1
fi

PHASE3_APPS=(
    "SeafileConnector"
    "SyncthingConnector"
    "MatrixConnector"
    "PaperlessNGConnector"
    "DuplicatiConnector"
    "WireGuardConnector"
    "MinecraftServerConnector"
    "OnlyOfficeConnector"
)

GREEN='\033[0;32m'
RED='\033[0;31m'
NC='\033[0m'

success_count=0
failure_count=0

for app in "${PHASE3_APPS[@]}"; do
    echo "Installing ${app}..."
    
    if ./gradlew ":${app}:installDebug" --quiet; then
        echo -e "${GREEN}✓${NC} ${app} installed successfully"
        success_count=$((success_count + 1))
    else
        echo -e "${RED}✗${NC} ${app} installation failed"
        failure_count=$((failure_count + 1))
    fi
    echo ""
done

echo "=========================================="
echo "Installation Summary"
echo "=========================================="
echo -e "${GREEN}Installed: ${success_count}${NC}"
if [ $failure_count -gt 0 ]; then
    echo -e "${RED}Failed: ${failure_count}${NC}"
else
    echo -e "${GREEN}Failed: 0${NC}"
fi

if [ $failure_count -eq 0 ]; then
    echo -e "${GREEN}✅ All Phase 3 applications installed!${NC}"
    echo ""
    echo "Installed apps:"
    adb shell pm list packages | grep "com.shareconnect" | sort
    exit 0
else
    echo -e "${RED}⚠️  Some installations failed.${NC}"
    exit 1
fi
