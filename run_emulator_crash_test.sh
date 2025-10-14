#!/bin/bash

# ShareConnect Emulator Crash Test Script
# This script tests all 4 applications on an emulator to ensure no port binding crashes occur

echo "ShareConnect Emulator Crash Test"
echo "================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Configuration
EMULATOR_NAME="test_emulator"
ANDROID_API_LEVEL=30
ANDROID_ABI="x86_64"
EMULATOR_TIMEOUT=300  # 5 minutes timeout for each app test
APK_TIMEOUT=30        # 30 seconds for APK installation

# APK paths
SHARECONNECT_APK="./ShareConnector/build/outputs/apk/debug/ShareConnector-debug.apk"
TRANSMISSION_APK="./Connectors/TransmissionConnect/TransmissionConnector/build/outputs/apk/debug/TransmissionConnector-debug.apk"
UTORRENT_APK="./Connectors/uTorrentConnect/uTorrentConnector/build/outputs/apk/debug/uTorrentConnector-debug.apk"
QBIT_APK="./Connectors/qBitConnect/qBitConnector/build/outputs/apk/debug/qBitConnector-debug.apk"

# Package names
SHARECONNECT_PKG="com.shareconnect"
TRANSMISSION_PKG="com.shareconnect.transmissionconnect"
UTORRENT_PKG="com.shareconnect.utorrentconnect"
QBIT_PKG="com.shareconnect.qbitconnect"

# Test results
TEST_PASSED=true
CRASH_LOG=""

# Function to log messages
log() {
    echo -e "${GREEN}[$(date +'%Y-%m-%d %H:%M:%S')] $1${NC}"
}

error() {
    echo -e "${RED}[$(date +'%Y-%m-%d %H:%M:%S')] ERROR: $1${NC}"
    TEST_PASSED=false
    CRASH_LOG="${CRASH_LOG}$1\n"
}

warning() {
    echo -e "${YELLOW}[$(date +'%Y-%m-%d %H:%M:%S')] WARNING: $1${NC}"
}

# Function to check if APK exists
check_apk() {
    local apk_path=$1
    local app_name=$2

    if [ ! -f "$apk_path" ]; then
        error "APK not found: $apk_path for $app_name"
        return 1
    fi

    log "Found APK: $apk_path for $app_name"
    return 0
}

# Function to check if emulator is running
check_emulator() {
    if ! adb devices | grep -q "emulator"; then
        error "No emulator running"
        return 1
    fi

    log "Emulator is running"
    return 0
}

# Function to wait for device
wait_for_device() {
    log "Waiting for device to be ready..."
    timeout 60 adb wait-for-device || {
        error "Device not ready within 60 seconds"
        return 1
    }

    # Wait for boot completion
    log "Waiting for boot completion..."
    timeout 120 adb wait-for-device shell 'while [[ -z $(getprop sys.boot_completed) ]]; do sleep 1; done' || {
        error "Device boot not completed within 120 seconds"
        return 1
    }

    log "Device is ready"
    return 0
}

# Function to install APK
install_apk() {
    local apk_path=$1
    local app_name=$2

    log "Installing $app_name..."

    if adb install -r "$apk_path"; then
        log "Successfully installed $app_name"
        return 0
    else
        error "Failed to install $app_name"
        return 1
    fi
}

# Function to test app launch and monitor for crashes
test_app() {
    local package_name=$1
    local app_name=$2

    log "Testing $app_name ($package_name)..."

    # Clear logcat before test
    adb logcat -c

    # Launch app
    if ! timeout 30 adb shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1; then
        error "Failed to launch $app_name"
        return 1
    fi

    log "Launched $app_name, monitoring for crashes..."

    # Monitor logcat for crashes during the test period
    local start_time=$(date +%s)
    local crash_detected=false

    while [ $(($(date +%s) - start_time)) -lt $EMULATOR_TIMEOUT ]; do
        # Check for crash patterns in logcat
        if adb logcat -d | grep -i "fatal\|exception\|crash\|bind.*failed\|eaddrinuse" | grep -v "FA " | grep -q "$package_name"; then
            error "Crash detected in $app_name!"
            adb logcat -d | grep -A 10 -B 5 "$package_name" | tail -20 >> crash_details.log
            crash_detected=true
            break
        fi

        # Check if app is still running
        if ! adb shell pidof "$package_name" > /dev/null 2>&1; then
            log "$app_name process terminated (expected)"
            break
        fi

        sleep 2
    done

    # Force stop app
    timeout 30 adb shell am force-stop "$package_name"

    if [ "$crash_detected" = false ]; then
        log "✅ $app_name test PASSED - no crashes detected"
        return 0
    else
        return 1
    fi
}

# Function to cleanup
cleanup() {
    log "Cleaning up..."

    # Stop all apps
    adb shell am force-stop "$SHARECONNECT_PKG" 2>/dev/null || true
    adb shell am force-stop "$TRANSMISSION_PKG" 2>/dev/null || true
    adb shell am force-stop "$UTORRENT_PKG" 2>/dev/null || true
    adb shell am force-stop "$QBIT_PKG" 2>/dev/null || true

    # Uninstall apps
    adb uninstall "$SHARECONNECT_PKG" 2>/dev/null || true
    adb uninstall "$TRANSMISSION_PKG" 2>/dev/null || true
    adb uninstall "$UTORRENT_PKG" 2>/dev/null || true
    adb uninstall "$QBIT_PKG" 2>/dev/null || true

    log "Cleanup completed"
}

# Main test execution
main() {
    log "Starting ShareConnect crash test suite"

    # Check prerequisites
    if ! command -v adb &> /dev/null; then
        error "ADB not found. Please install Android SDK."
        exit 1
    fi

    # Check APK files
    check_apk "$SHARECONNECT_APK" "ShareConnect" || exit 1
    check_apk "$TRANSMISSION_APK" "TransmissionConnect" || exit 1
    check_apk "$UTORRENT_APK" "uTorrentConnect" || exit 1
    check_apk "$QBIT_APK" "qBitConnect" || exit 1

    # Check emulator
    check_emulator || exit 1
    wait_for_device || exit 1

    # Install APKs
    install_apk "$SHARECONNECT_APK" "ShareConnect" || exit 1
    install_apk "$TRANSMISSION_APK" "TransmissionConnect" || exit 1
    install_apk "$UTORRENT_APK" "uTorrentConnect" || exit 1
    install_apk "$QBIT_APK" "qBitConnect" || exit 1

    # Test each app
    test_app "$SHARECONNECT_PKG" "ShareConnect"
    test_app "$TRANSMISSION_PKG" "TransmissionConnect"
    test_app "$UTORRENT_PKG" "uTorrentConnect"
    test_app "$QBIT_PKG" "qBitConnect"

    # Cleanup
    cleanup

    # Report results
    echo ""
    echo "================================"
    if [ "$TEST_PASSED" = true ]; then
        log "🎉 ALL TESTS PASSED! No crashes detected in any application."
        echo ""
        echo "✅ Port binding fix is working correctly"
        echo "✅ All applications start without BindException crashes"
        exit 0
    else
        error "❌ TESTS FAILED! Crashes detected:"
        echo -e "$CRASH_LOG"
        echo ""
        echo "❌ Port binding issues still exist"
        exit 1
    fi
}

# Trap for cleanup on exit
trap cleanup EXIT

# Run main function
main "$@"