#!/bin/bash

################################################################################
# ShareConnect Screenshot Capture Script
#
# This script helps capture screenshots from all three apps for documentation
# purposes. It guides you through capturing each required screenshot.
#
# Usage: ./capture_screenshots.sh
#
# Requirements:
# - Android device or emulator connected via ADB
# - Apps installed on the device
################################################################################

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Directories
SCREENSHOT_DIR="Documentation/Screenshots"
TEMP_DIR="/sdcard/screenshots"

# Print colored message
print_msg() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# Print section header
print_header() {
    echo ""
    echo "========================================================================"
    print_msg "$BLUE" "  $1"
    echo "========================================================================"
    echo ""
}

# Check if ADB is available
check_adb() {
    if ! command -v adb &> /dev/null; then
        print_msg "$RED" "ERROR: adb not found. Please install Android SDK Platform Tools."
        exit 1
    fi
}

# Check if device is connected
check_device() {
    local devices=$(adb devices | grep -v "List" | grep "device$" | wc -l)
    if [ "$devices" -eq 0 ]; then
        print_msg "$RED" "ERROR: No Android device/emulator connected."
        print_msg "$YELLOW" "Please connect a device or start an emulator and try again."
        exit 1
    fi
    print_msg "$GREEN" "✓ Device connected"
}

# Create directories
create_directories() {
    print_msg "$BLUE" "Creating screenshot directories..."
    mkdir -p "${SCREENSHOT_DIR}/ShareConnect"
    mkdir -p "${SCREENSHOT_DIR}/qBitConnect"
    mkdir -p "${SCREENSHOT_DIR}/TransmissionConnect"
    mkdir -p "${SCREENSHOT_DIR}/SyncModules"
    print_msg "$GREEN" "✓ Directories created"
}

# Capture single screenshot
capture() {
    local app=$1
    local name=$2
    local description=$3

    print_msg "$YELLOW" "Screenshot: $name"
    if [ ! -z "$description" ]; then
        print_msg "$NC" "  $description"
    fi
    print_msg "$NC" "  Navigate to the desired screen and press ENTER (or 's' to skip)..."

    read -r input
    if [ "$input" = "s" ] || [ "$input" = "S" ]; then
        print_msg "$YELLOW" "  ⊘ Skipped"
        return
    fi

    print_msg "$NC" "  Capturing..."

    # Create temp directory on device if it doesn't exist
    adb shell mkdir -p "${TEMP_DIR}" 2>/dev/null || true

    # Capture screenshot
    adb shell screencap -p "${TEMP_DIR}/${name}.png"

    # Pull to computer
    adb pull "${TEMP_DIR}/${name}.png" "${SCREENSHOT_DIR}/${app}/${name}.png" 2>/dev/null

    # Clean up device
    adb shell rm "${TEMP_DIR}/${name}.png" 2>/dev/null || true

    print_msg "$GREEN" "  ✓ Saved to ${SCREENSHOT_DIR}/${app}/${name}.png"
}

# ShareConnect screenshots
capture_shareconnect() {
    print_header "ShareConnect Screenshots (9 total)"

    print_msg "$YELLOW" "Please launch ShareConnect app and navigate through the following screens:"
    echo ""

    capture "ShareConnect" "main_screen" "Home screen with profile list (show 2-3 profiles)"
    capture "ShareConnect" "profiles_list" "Full profile management screen"
    capture "ShareConnect" "add_profile" "Add/Edit profile dialog with fields filled"
    capture "ShareConnect" "history_view" "History list with recent downloads"
    capture "ShareConnect" "bookmarks_view" "Bookmarks screen with favorites"
    capture "ShareConnect" "rss_feeds" "RSS feeds list (enabled and disabled)"
    capture "ShareConnect" "settings_general" "General settings screen"
    capture "ShareConnect" "settings_sync" "Sync-specific settings"
    capture "ShareConnect" "dark_mode" "App in dark mode (same as main screen for comparison)"
}

# qBitConnect screenshots
capture_qbitconnect() {
    print_header "qBitConnect Screenshots (8 total)"

    print_msg "$YELLOW" "Please launch qBitConnect app and navigate through the following screens:"
    echo ""

    capture "qBitConnect" "server_list" "Server selection screen"
    capture "qBitConnect" "torrent_list" "Active torrents view (downloading, seeding, paused)"
    capture "qBitConnect" "torrent_details" "Detailed torrent information (files, trackers, peers)"
    capture "qBitConnect" "add_torrent" "Add torrent dialog (magnet URI or file)"
    capture "qBitConnect" "rss_feeds" "RSS automation screen with configured feeds"
    capture "qBitConnect" "search" "Torrent search interface"
    capture "qBitConnect" "settings" "App settings screen"
    capture "qBitConnect" "profiles_qbit" "Profile management showing synced profiles"
}

# TransmissionConnect screenshots
capture_transmissionconnect() {
    print_header "TransmissionConnect Screenshots (7 total)"

    print_msg "$YELLOW" "Please launch TransmissionConnect app and navigate through the following screens:"
    echo ""

    capture "TransmissionConnect" "server_list" "Server management screen with multiple servers"
    capture "TransmissionConnect" "torrent_list" "Main torrent list view (various states)"
    capture "TransmissionConnect" "torrent_details" "Torrent detail screen (peers, trackers, files)"
    capture "TransmissionConnect" "add_server" "Add/Edit server dialog"
    capture "TransmissionConnect" "settings" "Application settings"
    capture "TransmissionConnect" "notifications" "Notification settings or example"
    capture "TransmissionConnect" "profiles_transmission" "Synced profiles view"
}

# Sync demonstration screenshots
capture_sync_demos() {
    print_header "Sync Module Demonstrations (5 total)"

    print_msg "$YELLOW" "For these screenshots, you'll need multiple apps running simultaneously."
    print_msg "$NC" "Consider using a screen recording tool and extracting frames, or"
    print_msg "$NC" "manually capture split-screen or sequential screenshots."
    echo ""

    capture "SyncModules" "cross_app_demo" "Three apps side by side showing synced data"
    capture "SyncModules" "real_time_update" "Split screen showing simultaneous updates"
    capture "SyncModules" "conflict_resolution" "Version-based conflict resolution example"

    print_msg "$YELLOW" ""
    print_msg "$YELLOW" "Note: For animated GIFs, you'll need to:"
    print_msg "$NC" "1. Record screen using: adb shell screenrecord /sdcard/sync_demo.mp4"
    print_msg "$NC" "2. Pull the video: adb pull /sdcard/sync_demo.mp4"
    print_msg "$NC" "3. Convert to GIF using ffmpeg or online tool"
    print_msg "$NC" "4. Save as: ${SCREENSHOT_DIR}/SyncModules/sync_in_action.gif"
    echo ""
}

# Generate summary
generate_summary() {
    print_header "Screenshot Capture Summary"

    local total_captured=0
    local total_required=29  # 9 + 8 + 7 + 5

    for app in "ShareConnect" "qBitConnect" "TransmissionConnect" "SyncModules"; do
        local count=$(find "${SCREENSHOT_DIR}/${app}" -type f \( -name "*.png" -o -name "*.gif" \) 2>/dev/null | wc -l)
        total_captured=$((total_captured + count))
        print_msg "$GREEN" "$app: $count screenshots"
    done

    echo ""
    print_msg "$BLUE" "Total: $total_captured / $total_required screenshots captured"
    echo ""

    if [ $total_captured -eq $total_required ]; then
        print_msg "$GREEN" "✓ All screenshots captured!"
    else
        local remaining=$((total_required - total_captured))
        print_msg "$YELLOW" "⚠ $remaining screenshots remaining"
    fi

    echo ""
    print_msg "$BLUE" "Screenshots saved in: ${SCREENSHOT_DIR}/"
    echo ""
}

# Main script
main() {
    clear
    print_header "ShareConnect Screenshot Capture Tool"

    print_msg "$NC" "This script will guide you through capturing screenshots for all three apps."
    print_msg "$NC" "You can skip any screenshot by typing 's' when prompted."
    echo ""

    # Checks
    check_adb
    check_device
    create_directories

    echo ""
    print_msg "$YELLOW" "Ready to begin screenshot capture."
    print_msg "$NC" "Press ENTER to continue or Ctrl+C to cancel..."
    read

    # Capture screenshots
    capture_shareconnect
    capture_qbitconnect
    capture_transmissionconnect
    capture_sync_demos

    # Summary
    generate_summary

    print_msg "$GREEN" "Screenshot capture complete!"
    print_msg "$NC" "Next steps:"
    print_msg "$NC" "1. Review screenshots in ${SCREENSHOT_DIR}/"
    print_msg "$NC" "2. Retake any that need improvement"
    print_msg "$NC" "3. Create sync demonstration GIFs"
    print_msg "$NC" "4. Update documentation with screenshot references"
    echo ""
}

# Run main function
main
