#!/bin/bash

# ShareConnect AI QA - Emulator Cleanup Script
# This script properly cleans up the Android emulator environment after testing

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
EMULATOR_NAME="ShareConnect_QA_Emulator"
EMULATOR_PORT=${ANDROID_SERIAL#emulator-}
EMULATOR_PID=${EMULATOR_PID:-""}

echo -e "${BLUE}"
echo "╔══════════════════════════════════════════════════════════════╗"
echo "║         ShareConnect AI QA - Emulator Cleanup               ║"
echo "╚══════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Parse command line arguments
FORCE_CLEAN=false
REMOVE_AVD=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --force)
            FORCE_CLEAN=true
            shift
            ;;
        --remove-avd)
            REMOVE_AVD=true
            shift
            ;;
        --help)
            echo "ShareConnect AI QA - Emulator Cleanup"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --force       Force cleanup even if tests are running"
            echo "  --remove-avd  Remove the AVD after cleanup"
            echo "  --help        Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                    # Normal cleanup"
            echo "  $0 --force           # Force cleanup"
            echo "  $0 --remove-avd      # Remove AVD after cleanup"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Function to check if emulator is running
is_emulator_running() {
    if [[ -n "$EMULATOR_PID" ]] && kill -0 "$EMULATOR_PID" 2>/dev/null; then
        return 0
    fi

    # Check if any emulator process is running
    if pgrep -f "emulator.*$EMULATOR_NAME" >/dev/null 2>&1; then
        return 0
    fi

    return 1
}

# Function to check if tests are running
are_tests_running() {
    if adb devices 2>/dev/null | grep -q "emulator-$EMULATOR_PORT.*device"; then
        # Check if any test processes are running
        if adb -s "emulator-$EMULATOR_PORT" shell ps 2>/dev/null | grep -q -E "(qa\.ai|android\.test)"; then
            return 0
        fi
    fi
    return 1
}

echo -e "${YELLOW}Checking emulator status...${NC}"

if ! is_emulator_running; then
    echo -e "${GREEN}✓ No emulator running${NC}"
    exit 0
fi

echo -e "${YELLOW}Emulator is running (PID: $EMULATOR_PID)${NC}"

# Check if tests are running
if are_tests_running; then
    if [[ "$FORCE_CLEAN" == false ]]; then
        echo -e "${RED}WARNING: Tests appear to be running on the emulator${NC}"
        echo -e "${YELLOW}Use --force to cleanup anyway, or wait for tests to complete${NC}"
        exit 1
    else
        echo -e "${YELLOW}Force cleanup requested, proceeding...${NC}"
    fi
fi

# Stop all apps
echo -e "${YELLOW}[1/6] Stopping all apps...${NC}"

APPS_TO_STOP=(
    "com.shareconnect"
    "com.transmissionconnect"
    "com.utorrentconnect"
    "com.qbitconnect"
    "com.shareconnect.qa.ai"
)

for app in "${APPS_TO_STOP[@]}"; do
    adb -s "emulator-$EMULATOR_PORT" shell am force-stop "$app" 2>/dev/null || true
done

echo -e "${GREEN}✓ Apps stopped${NC}"

# Clear app data
echo -e "${YELLOW}[2/6] Clearing app data...${NC}"

for app in "${APPS_TO_STOP[@]}"; do
    adb -s "emulator-$EMULATOR_PORT" shell pm clear "$app" 2>/dev/null || true
done

echo -e "${GREEN}✓ App data cleared${NC}"

# Clear device storage
echo -e "${YELLOW}[3/6] Clearing device storage...${NC}"

# Clear downloads, cache, and temp files
adb -s "emulator-$EMULATOR_PORT" shell rm -rf /sdcard/Download/* 2>/dev/null || true
adb -s "emulator-$EMULATOR_PORT" shell rm -rf /sdcard/DCIM/* 2>/dev/null || true
adb -s "emulator-$EMULATOR_PORT" shell rm -rf /sdcard/Pictures/* 2>/dev/null || true
adb -s "emulator-$EMULATOR_PORT" shell rm -rf /sdcard/Movies/* 2>/dev/null || true

echo -e "${GREEN}✓ Device storage cleared${NC}"

# Kill emulator process
echo -e "${YELLOW}[4/6] Stopping emulator...${NC}"

if [[ -n "$EMULATOR_PID" ]]; then
    kill "$EMULATOR_PID" 2>/dev/null || true
    sleep 2

    # Force kill if still running
    if kill -0 "$EMULATOR_PID" 2>/dev/null; then
        kill -9 "$EMULATOR_PID" 2>/dev/null || true
    fi
fi

# Kill any remaining emulator processes
pkill -f "emulator.*$EMULATOR_NAME" 2>/dev/null || true
pkill -f "qemu.*$EMULATOR_NAME" 2>/dev/null || true

echo -e "${GREEN}✓ Emulator stopped${NC}"

# Clean up ADB connections
echo -e "${YELLOW}[5/6] Cleaning up ADB connections...${NC}"

adb disconnect "emulator-$EMULATOR_PORT" 2>/dev/null || true

# Kill ADB server if no other devices are connected
if ! adb devices 2>/dev/null | grep -q -v "List of devices attached"; then
    adb kill-server 2>/dev/null || true
fi

echo -e "${GREEN}✓ ADB connections cleaned${NC}"

# Remove AVD if requested
if [[ "$REMOVE_AVD" == true ]]; then
    echo -e "${YELLOW}[6/6] Removing AVD...${NC}"
    avdmanager delete avd -n "$EMULATOR_NAME" 2>/dev/null || true
    echo -e "${GREEN}✓ AVD removed${NC}"
else
    echo -e "${GREEN}✓ AVD preserved${NC}"
fi

# Final verification
echo -e "${YELLOW}Verifying cleanup...${NC}"

if is_emulator_running; then
    echo -e "${RED}WARNING: Emulator may still be running${NC}"
else
    echo -e "${GREEN}✓ Emulator successfully stopped${NC}"
fi

# Print summary
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                   CLEANUP COMPLETE                          ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${GREEN}Emulator environment has been cleaned up successfully!${NC}"
echo ""

# Unset environment variables
unset ANDROID_SERIAL
unset EMULATOR_PID
unset APP_SCENARIO