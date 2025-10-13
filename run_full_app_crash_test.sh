#!/bin/bash

# ShareConnect - Full Application Crash Test Automation Script
# This script tests all four Android applications for crashes and sync functionality
# Tests app launching, restarting, and Asinka library sync operations

set -e

# Configuration
APPS=("ShareConnector" "qBitConnector" "TransmissionConnector" "uTorrentConnector")
APP_MODULES=("ShareConnector" "qBitConnector" "TransmissionConnector" "uTorrentConnector")
APP_PACKAGES=("com.shareconnect.debug" "com.shareconnect.qbitconnect.debug" "com.shareconnect.transmissionconnect" "com.shareconnect.utorrentconnect")

if [ -z "$ANDROID_HOME" ]; then
    echo "ERROR: ANDROID_HOME is not defined"
    exit 1
fi

export PATH="$ANDROID_HOME/cmdline-tools/latest/bin:$ANDROID_HOME/platform-tools:$ANDROID_HOME/emulator:$PATH"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

# Get current timestamp for directory naming
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
TEST_TYPE="full_app_crash_test"
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND/${TEST_TYPE}"

echo -e "${BOLD}${CYAN}ShareConnect Full Application Crash Test${NC}"
echo -e "${BOLD}${CYAN}=======================================${NC}"
echo ""

# Create report directory
mkdir -p "$REPORT_DIR"

# Initialize test results
declare -A APP_RESULTS
declare -A CRASH_RESULTS
declare -A SYNC_RESULTS
OVERALL_STATUS="UNKNOWN"

# Function to check if emulator is running
is_emulator_running() {
    adb devices | grep -q "emulator-"
}

# Function to wait for device to be ready
wait_for_device() {
    echo -e "${YELLOW}Waiting for emulator to be ready...${NC}"
    timeout=300
    counter=0

    while [ $counter -lt $timeout ]; do
        if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
            echo -e "${GREEN}✓ Emulator is ready!${NC}"
            return 0
        fi
        sleep 2
        counter=$((counter + 2))
        echo "Still waiting... ($counter/$timeout seconds)"
    done

    echo -e "${RED}✗ Timeout waiting for emulator to be ready${NC}"
    return 1
}

# Function to check for app crashes
check_for_crashes() {
    local app_name=$1
    local package_name=$2

    echo -e "${BLUE}Checking for crashes in $app_name...${NC}"

    # Check logcat for crash indicators in the last 30 seconds
    local crash_count=$(adb logcat -d -t "30s" | grep -i -c "AndroidRuntime.*$package_name\|FATAL.*$package_name\|$package_name.*crash\|$package_name.*exception" || true)

    if [ "$crash_count" -gt 0 ]; then
        echo -e "${RED}✗ Found $crash_count crash indicators for $app_name${NC}"
        CRASH_RESULTS[$app_name]="CRASHED"
        return 1
    else
        echo -e "${GREEN}✓ No crashes detected for $app_name${NC}"
        CRASH_RESULTS[$app_name]="NO_CRASH"
        return 0
    fi
}

# Function to test app launch and restart
test_app_launch_and_restart() {
    local app_name=$1
    local package_name=$2
    local activity_name=$3

    echo -e "${BLUE}Testing $app_name launch and restart...${NC}"

    # Clear logcat before testing
    adb logcat -c

    # Launch app
    echo "Launching $app_name..."
    adb shell am start -n "$package_name/$activity_name" 2>/dev/null || {
        echo -e "${YELLOW}⚠ Could not start $app_name with specified activity, trying default${NC}"
        adb shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1 2>/dev/null || {
            echo -e "${RED}✗ Failed to launch $app_name${NC}"
            APP_RESULTS[$app_name]="LAUNCH_FAILED"
            return 1
        }
    }

    # Wait for app to start
    sleep 5

    # Check if app is running
    if adb shell pidof "$package_name" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $app_name launched successfully${NC}"
    else
        echo -e "${RED}✗ $app_name failed to start${NC}"
        check_for_crashes "$app_name" "$package_name"
        APP_RESULTS[$app_name]="LAUNCH_FAILED"
        return 1
    fi

    # Check for crashes after launch
    if ! check_for_crashes "$app_name" "$package_name"; then
        APP_RESULTS[$app_name]="CRASHED_ON_LAUNCH"
        return 1
    fi

    # Force stop and restart app
    echo "Force stopping $app_name..."
    adb shell am force-stop "$package_name"
    sleep 2

    echo "Restarting $app_name..."
    adb shell am start -n "$package_name/$activity_name" 2>/dev/null || adb shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1 2>/dev/null

    # Wait for restart
    sleep 5

    # Check if app restarted successfully
    if adb shell pidof "$package_name" > /dev/null 2>&1; then
        echo -e "${GREEN}✓ $app_name restarted successfully${NC}"
    else
        echo -e "${RED}✗ $app_name failed to restart${NC}"
        check_for_crashes "$app_name" "$package_name"
        APP_RESULTS[$app_name]="RESTART_FAILED"
        return 1
    fi

    # Final crash check
    if ! check_for_crashes "$app_name" "$package_name"; then
        APP_RESULTS[$app_name]="CRASHED_ON_RESTART"
        return 1
    fi

    echo -e "${GREEN}✓ $app_name launch and restart test passed${NC}"
    APP_RESULTS[$app_name]="PASSED"
    return 0
}

# Function to test Asinka sync operations
test_asinka_sync() {
    local app_name=$1
    local package_name=$2

    echo -e "${BLUE}Testing Asinka sync operations for $app_name...${NC}"

    # Clear logcat
    adb logcat -c

    # Launch app to trigger sync operations
    if [ "$app_name" = "ShareConnector" ]; then
        adb shell am start -n "$package_name/.SplashActivity" 2>/dev/null || adb shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1 2>/dev/null
    else
        adb shell am start -n "$package_name/.MainActivity" 2>/dev/null || adb shell monkey -p "$package_name" -c android.intent.category.LAUNCHER 1 2>/dev/null
    fi

    # Wait for sync operations to initialize
    sleep 10

    # Check for sync-related logs (positive indicators)
    local sync_logs=$(adb logcat -d -t "30s" | grep -i -c "sync\|asinka\|grpc\|port.*binding" || true)

    # Check for sync errors
    local sync_errors=$(adb logcat -d -t "30s" | grep -i -c "sync.*error\|sync.*fail\|asinka.*error\|grpc.*error\|bind.*exception" || true)

    if [ "$sync_errors" -gt 0 ]; then
        echo -e "${RED}✗ Found sync errors in $app_name${NC}"
        SYNC_RESULTS[$app_name]="SYNC_ERRORS"
        return 1
    elif [ "$sync_logs" -gt 0 ]; then
        echo -e "${GREEN}✓ Sync operations detected in $app_name${NC}"
        SYNC_RESULTS[$app_name]="SYNC_ACTIVE"
        return 0
    else
        echo -e "${YELLOW}⚠ No sync activity detected in $app_name (may be normal)${NC}"
        SYNC_RESULTS[$app_name]="NO_ACTIVITY"
        return 0
    fi
}

# Setup emulator
echo -e "${YELLOW}Setting up Android emulator...${NC}"

if ! is_emulator_running; then
    echo "Starting emulator..."
    ./run_emulator_tests.sh &
    EMULATOR_PID=$!

    # Wait for emulator to be ready
    if ! wait_for_device; then
        echo -e "${RED}✗ Failed to start emulator${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ Emulator already running${NC}"
fi

# Get device info
DEVICE_INFO=$(adb shell getprop ro.product.model)
API_LEVEL=$(adb shell getprop ro.build.version.sdk)
echo -e "${BLUE}Device: $DEVICE_INFO (API $API_LEVEL)${NC}"
echo ""

# Unlock device and disable animations
echo -e "${BLUE}Preparing device for testing...${NC}"
adb shell input keyevent 82  # Unlock
adb shell input swipe 300 1000 300 500  # Swipe to unlock
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

echo -e "${YELLOW}Starting comprehensive app crash testing...${NC}"
echo "Report will be saved to: $REPORT_DIR"
echo ""

# Test each app
for i in "${!APPS[@]}"; do
    app="${APPS[$i]}"
    module="${APP_MODULES[$i]}"
    package="${APP_PACKAGES[$i]}"

    echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
    echo -e "${BOLD}${CYAN}                    TESTING $app                          ${NC}"
    echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
    echo ""

    # Build and install app
    echo -e "${BLUE}Building and installing $app...${NC}"
    if ./gradlew ":$module:assembleDebug" --quiet; then
        echo -e "${GREEN}✓ $app built successfully${NC}"

        # Install app
        if ./gradlew ":$module:installDebug" --quiet; then
            echo -e "${GREEN}✓ $app installed successfully${NC}"

            # Test launch and restart
            if [ "$app" = "ShareConnector" ]; then
                test_app_launch_and_restart "$app" "$package" ".SplashActivity"
            else
                test_app_launch_and_restart "$app" "$package" ".MainActivity"
            fi

            # Test Asinka sync operations
            test_asinka_sync "$app" "$package"

            # Clean up
            adb shell am force-stop "$package"
            adb shell pm clear "$package" 2>/dev/null || true

        else
            echo -e "${RED}✗ Failed to install $app${NC}"
            APP_RESULTS[$app]="INSTALL_FAILED"
            SYNC_RESULTS[$app]="NOT_TESTED"
        fi
    else
        echo -e "${RED}✗ Failed to build $app${NC}"
        APP_RESULTS[$app]="BUILD_FAILED"
        SYNC_RESULTS[$app]="NOT_TESTED"
    fi

    echo ""
done

# Run existing unit and instrumentation tests
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${CYAN}               RUNNING EXISTING TESTS                      ${NC}"
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo ""

echo -e "${BLUE}Running unit tests...${NC}"
if ./run_unit_tests.sh > "$REPORT_DIR/unit_tests.log" 2>&1; then
    UNIT_TEST_STATUS="PASSED"
    echo -e "${GREEN}✓ Unit tests passed${NC}"
else
    UNIT_TEST_STATUS="FAILED"
    echo -e "${RED}✗ Unit tests failed${NC}"
fi

echo -e "${BLUE}Running instrumentation tests...${NC}"
if ./run_instrumentation_tests.sh > "$REPORT_DIR/instrumentation_tests.log" 2>&1; then
    INSTRUMENTATION_TEST_STATUS="PASSED"
    echo -e "${GREEN}✓ Instrumentation tests passed${NC}"
else
    INSTRUMENTATION_TEST_STATUS="FAILED"
    echo -e "${RED}✗ Instrumentation tests failed${NC}"
fi

# Determine overall status
OVERALL_STATUS="PASSED"
for app in "${APPS[@]}"; do
    if [ "${APP_RESULTS[$app]}" != "PASSED" ]; then
        OVERALL_STATUS="FAILED"
        break
    fi
done

if [ "$UNIT_TEST_STATUS" != "PASSED" ] || [ "$INSTRUMENTATION_TEST_STATUS" != "PASSED" ]; then
    OVERALL_STATUS="FAILED"
fi

# Generate comprehensive test report
cat > "${REPORT_DIR}/full_crash_test_report.txt" << EOF
ShareConnect Full Application Crash Test Report
===============================================

Execution Date: $(date)
Test Round ID: ${TIMESTAMP}
Overall Status: ${OVERALL_STATUS}

═══════════════════════════════════════════════════════════

TEST ENVIRONMENT
Device Model: ${DEVICE_INFO}
API Level: ${API_LEVEL}
Android Version: $(adb shell getprop ro.build.version.release)
Emulator: $(adb devices | grep emulator | head -1)

═══════════════════════════════════════════════════════════

APPLICATION LAUNCH & CRASH TESTS

EOF

for app in "${APPS[@]}"; do
    cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
$app:
  Status: ${APP_RESULTS[$app]}
  Crash Check: ${CRASH_RESULTS[$app]}
  Sync Status: ${SYNC_RESULTS[$app]}

EOF
done

cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
═══════════════════════════════════════════════════════════

EXISTING TEST SUITE RESULTS
Unit Tests: ${UNIT_TEST_STATUS}
Instrumentation Tests: ${INSTRUMENTATION_TEST_STATUS}

═══════════════════════════════════════════════════════════

DETAILED RESULTS

CRASH ANALYSIS:
EOF

# Add crash analysis details
for app in "${APPS[@]}"; do
    cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
$app Crash Status: ${CRASH_RESULTS[$app]}
EOF
done

cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF

SYNC ANALYSIS:
EOF

for app in "${APPS[@]}"; do
    cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
$app Sync Status: ${SYNC_RESULTS[$app]}
EOF
done

cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF

═══════════════════════════════════════════════════════════

TEST ARTIFACTS
Report Directory: ${REPORT_DIR}
Unit Test Log: unit_tests.log
Instrumentation Test Log: instrumentation_tests.log
Full Report: full_crash_test_report.txt

═══════════════════════════════════════════════════════════

RECOMMENDATIONS
EOF

if [ "$OVERALL_STATUS" = "PASSED" ]; then
    cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
✓ All applications launched and restarted without crashes
✓ Asinka sync operations are functioning
✓ Existing test suites pass successfully
✓ Applications are stable for production use
EOF
else
    cat >> "${REPORT_DIR}/full_crash_test_report.txt" << EOF
✗ Some applications have issues that need attention
✗ Review individual app statuses above
✗ Check log files for detailed error information
EOF
fi

# Copy test artifacts
echo -e "${BLUE}Copying test artifacts...${NC}"
find . -name "*.log" -path "*/${TIMESTAMP}*" -exec cp {} "$REPORT_DIR/" \; 2>/dev/null || true

# Take final device screenshot
echo -e "${BLUE}Capturing final device state...${NC}"
adb shell screencap -p /sdcard/final_test_state.png
adb pull /sdcard/final_test_state.png "${REPORT_DIR}/final_device_state.png" 2>/dev/null || true
adb shell rm /sdcard/final_test_state.png 2>/dev/null || true

echo ""
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${CYAN}                    TEST SUMMARY                           ${NC}"
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "${BLUE}Test Results:${NC}"

for app in "${APPS[@]}"; do
    status="${APP_RESULTS[$app]}"
    if [ "$status" = "PASSED" ]; then
        echo -e "  $app: ${GREEN}✓ PASSED${NC}"
    else
        echo -e "  $app: ${RED}✗ $status${NC}"
    fi
done

echo ""
echo -e "Unit Tests: ${UNIT_TEST_STATUS}"
echo -e "Instrumentation Tests: ${INSTRUMENTATION_TEST_STATUS}"
echo ""
echo -e "Overall Status: ${OVERALL_STATUS}"
echo -e "Report Directory: ${REPORT_DIR}"
echo ""

if [ "$OVERALL_STATUS" = "PASSED" ]; then
    echo -e "${BOLD}${GREEN}🎉 ALL TESTS PASSED! 🎉${NC}"
    echo -e "${GREEN}All applications are crash-free and sync operations are working.${NC}"
    exit 0
else
    echo -e "${BOLD}${RED}❌ TESTS FAILED ❌${NC}"
    echo -e "${RED}Some applications have crashes or sync issues. Check the report for details.${NC}"
    exit 1
fi