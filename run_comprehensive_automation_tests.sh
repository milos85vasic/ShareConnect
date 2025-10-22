#!/bin/bash

# ShareConnect - Comprehensive Automation Tests Execution Script
# This script runs automation tests for ALL applications

set -e

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
NC='\033[0m' # No Color

# Get current timestamp for directory naming
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
TEST_TYPE="automation_tests"
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND/${TEST_TYPE}"

echo -e "${BLUE}ShareConnect Comprehensive Automation Tests Execution${NC}"
echo -e "${BLUE}====================================================${NC}"
echo ""

# Check if device/emulator is connected
echo -e "${YELLOW}Checking for connected devices...${NC}"
adb devices -l

DEVICE_COUNT=$(adb devices | grep -v "List of devices" | grep -c "device$" || true)
if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo -e "${YELLOW}No devices found. Starting emulator using emulator manager...${NC}"

    # Use our polished emulator manager
    if ! ./emulator_manager.sh start; then
        echo -e "${RED}✗ Failed to start emulator using emulator manager!${NC}"
        exit 1
    fi

    # Source the environment variables set by emulator manager
    eval "$(./emulator_manager.sh start)"

    # Verify device is connected
    DEVICE_COUNT=$(adb devices | grep -v "List of devices" | grep -c "device$" || true)
fi

if [ "$DEVICE_COUNT" -eq 0 ]; then
    echo -e "${RED}✗ No Android devices/emulators available after startup attempt!${NC}"
    exit 1
fi

echo -e "${GREEN}✓ Found $DEVICE_COUNT connected device(s)${NC}"

# Get device information
DEVICE_INFO=$(adb shell getprop ro.product.model)
API_LEVEL=$(adb shell getprop ro.build.version.sdk)
echo -e "${BLUE}Device: $DEVICE_INFO (API $API_LEVEL)${NC}"
echo ""

# Create report directory
mkdir -p "$REPORT_DIR"

echo -e "${YELLOW}Starting Comprehensive Automation Tests...${NC}"
echo "Report will be saved to: $REPORT_DIR"
echo ""

# Track test results for each module
declare -A MODULE_RESULTS
MODULES=(
    "ShareConnector"
    "JDownloaderConnector"
    "TransmissionConnector"
    "uTorrentConnector"
    "qBitConnector"
)

# Build all applications first
echo -e "${BLUE}Building all applications...${NC}"
for module in "${MODULES[@]}"; do
    echo -e "${BLUE}Building $module...${NC}"
    if timeout 600 ./gradlew ":$module:assembleDebug" ":$module:assembleDebugAndroidTest" --quiet; then
        echo -e "${GREEN}✓ $module built successfully${NC}"
    else
        echo -e "${RED}✗ Failed to build $module${NC}"
        MODULE_RESULTS[$module]="BUILD_FAILED"
    fi
done

# Clean up any previous test data to ensure test isolation
echo -e "${BLUE}Cleaning up previous test data...${NC}"
for module in "${MODULES[@]}"; do
    case $module in
        "ShareConnector")
            adb shell pm clear com.shareconnect.debug 2>/dev/null || true
            ;;
        "JDownloaderConnector")
            adb shell pm clear com.shareconnect.jdownloaderconnect.debug 2>/dev/null || true
            ;;
        "TransmissionConnector")
            adb shell pm clear com.shareconnect.transmissionconnect 2>/dev/null || true
            ;;
        "uTorrentConnector")
            adb shell pm clear com.shareconnect.utorrentconnect 2>/dev/null || true
            ;;
        "qBitConnector")
            adb shell pm clear com.shareconnect.qbitconnect.debug 2>/dev/null || true
            ;;
    esac
done

# Install applications to ensure fresh state
echo -e "${BLUE}Installing applications...${NC}"
for module in "${MODULES[@]}"; do
    echo -e "${BLUE}Installing $module...${NC}"
    if timeout 300 ./gradlew ":$module:installDebug" --quiet; then
        echo -e "${GREEN}✓ $module installed successfully${NC}"
    else
        echo -e "${RED}✗ Failed to install $module${NC}"
        MODULE_RESULTS[$module]="INSTALL_FAILED"
    fi
done

# Ensure emulator is in a clean state
echo -e "${BLUE}Preparing emulator for testing...${NC}"
adb shell input keyevent KEYCODE_HOME
sleep 2

# Run automation tests for each module
OVERALL_STATUS="PASSED"
for module in "${MODULES[@]}"; do
    echo -e "${BLUE}Running automation tests for $module...${NC}"
    
    # Check if module exists and has automation tests
    if ./gradlew projects | grep -q ":$module"; then
        # Run automation tests with appropriate test packages
        case $module in
            "ShareConnector")
                TEST_PACKAGES="com.shareconnect.automation"
                ;;
            "JDownloaderConnector")
                TEST_PACKAGES="com.shareconnect.jdownloaderconnect.automation"
                ;;
            "TransmissionConnector")
                TEST_PACKAGES="com.shareconnect.transmissionconnect"
                ;;
            "uTorrentConnector")
                TEST_PACKAGES="com.shareconnect.utorrentconnect"
                ;;
            "qBitConnector")
                TEST_PACKAGES="com.shareconnect.qbitconnect"
                ;;
            *)
                TEST_PACKAGES=""
                ;;
        esac
        
        if [ -n "$TEST_PACKAGES" ]; then
            if timeout 1200 ./gradlew ":$module:connectedAndroidTest" \
                -Pandroid.testInstrumentationRunnerArguments.package="$TEST_PACKAGES" \
                --info \
                --stacktrace \
                2>&1 | tee -a "${REPORT_DIR}/automation_test_execution.log"; then
                echo -e "${GREEN}✓ $module automation tests passed${NC}"
                MODULE_RESULTS[$module]="PASSED"
            else
                echo -e "${RED}✗ $module automation tests failed${NC}"
                MODULE_RESULTS[$module]="FAILED"
                OVERALL_STATUS="FAILED"
            fi
        else
            echo -e "${YELLOW}⚠ $module has no automation test packages defined${NC}"
            MODULE_RESULTS[$module]="NO_TESTS"
        fi
    else
        echo -e "${YELLOW}⚠ $module not found${NC}"
        MODULE_RESULTS[$module]="NOT_FOUND"
    fi
done

# Copy test reports
echo -e "${BLUE}Copying test reports...${NC}"

# Copy ShareConnector reports
if [ -d "ShareConnector/build/reports/androidTests/connected" ]; then
    cp -r ShareConnector/build/reports/androidTests/connected/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ ShareConnector HTML test reports copied${NC}"
fi

if [ -d "ShareConnector/build/outputs/androidTest-results/connected" ]; then
    cp -r ShareConnector/build/outputs/androidTest-results/connected/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ ShareConnector XML test results copied${NC}"
fi

# Copy JDownloaderConnector reports
if [ -d "Connectors/JDownloaderConnect/JDownloaderConnector/build/reports/androidTests/connected" ]; then
    mkdir -p "${REPORT_DIR}/jdownloader/"
    cp -r Connectors/JDownloaderConnect/JDownloaderConnector/build/reports/androidTests/connected/* "${REPORT_DIR}/jdownloader/"
    echo -e "${GREEN}✓ JDownloaderConnector HTML test reports copied${NC}"
fi

# Copy any screenshots or additional artifacts
if [ -d "ShareConnector/build/outputs/connected_android_test_additional_output" ]; then
    cp -r ShareConnector/build/outputs/connected_android_test_additional_output/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ Screenshots and artifacts copied${NC}"
fi

# Take additional device screenshots for documentation
echo -e "${BLUE}Capturing device state...${NC}"
adb shell screencap -p /sdcard/final_state.png
adb pull /sdcard/final_state.png "${REPORT_DIR}/device_final_state.png" 2>/dev/null || true
adb shell rm /sdcard/final_state.png 2>/dev/null || true

# Generate comprehensive summary report
cat > "${REPORT_DIR}/test_summary.txt" << EOF
ShareConnect Comprehensive Automation Tests Execution Summary
===========================================================

Execution Date: $(date)
Test Type: Comprehensive Automation Tests
Device Information: $DEVICE_INFO (API $API_LEVEL)
Overall Status: ${OVERALL_STATUS}

══════════════════════════════════════════════════════════

MODULE TEST RESULTS:

EOF

for module in "${MODULES[@]}"; do
    cat >> "${REPORT_DIR}/test_summary.txt" << EOF
${module}: ${MODULE_RESULTS[$module]}
EOF
done

cat >> "${REPORT_DIR}/test_summary.txt" << EOF

══════════════════════════════════════════════════════════

TEST COVERAGE:

ShareConnector:
- Complete User Workflows: End-to-end user interaction flows
- UI/UX Interaction Testing: Touch, swipe, and navigation testing
- Accessibility Compliance: Screen reader, contrast, touch target testing
- Application Navigation: Bottom navigation, drawer navigation
- Theme Management: Theme switching and customization
- Profile Management: Profile creation, editing, deletion
- History Operations: History viewing, filtering, clearing
- Share Intent Handling: URL sharing and intent processing
- Stress Testing: Rapid navigation and app stress testing

JDownloaderConnector:
- Account Management: MyJDownloader account login and management
- Download Management: Download package creation and monitoring
- Link Grabber: URL analysis and package creation
- Settings Management: App settings and preferences
- UI Navigation: Compose UI navigation flows
- Accessibility: Compose accessibility testing

TransmissionConnector:
- Server Management: Server connection and configuration
- Torrent Operations: Add, remove, pause torrents
- File Management: Torrent file selection and management
- Settings Management: App preferences and settings
- UI Navigation: Activity and fragment navigation

qBitConnector:
- Server Management: qBittorrent server connection
- Torrent Operations: Add, remove, manage torrents
- RSS Management: RSS feed subscription and management
- Settings Management: App preferences and settings
- UI Navigation: Compose UI navigation flows

══════════════════════════════════════════════════════════

TEST CLASSES EXECUTED:

ShareConnector:
- FullAppFlowAutomationTest: Complete application workflows
- AccessibilityAutomationTest: Accessibility compliance testing
- ProfileManagementAutomationTest: Profile creation and management
- ThemeManagementAutomationTest: Theme switching and customization
- HistoryAutomationTest: History viewing and operations
- ShareIntentAutomationTest: URL sharing and intent handling

JDownloaderConnector:
- JDownloaderAutomationTest: Complete JDownloader workflows
- AccountAutomationTest: Account management automation
- DownloadAutomationTest: Download management automation
- LinkGrabberAutomationTest: Link grabber automation

TransmissionConnector:
- TransmissionAutomationTest: Transmission client automation
- ServerAutomationTest: Server management automation
- TorrentAutomationTest: Torrent operations automation

qBitConnector:
- QBitAutomationTest: qBittorrent client automation
- ServerAutomationTest: Server management automation
- TorrentAutomationTest: Torrent operations automation

══════════════════════════════════════════════════════════

REPORT LOCATION: ${REPORT_DIR}

FILES GENERATED:
- automation_test_execution.log: Full execution log
- test_summary.txt: This summary file
- index.html: HTML test report (if available)
- TEST-*.xml: JUnit XML results (if available)
- jdownloader/: JDownloaderConnector test reports
- device_final_state.png: Final device screenshot

COMMAND USED:
./run_comprehensive_automation_tests.sh

EOF

echo ""
echo -e "${BLUE}Comprehensive Automation Tests Execution Complete${NC}"
echo -e "${BLUE}================================================${NC}"
echo "Overall Status: ${OVERALL_STATUS}"
echo "Report Directory: ${REPORT_DIR}"
echo "Device: $DEVICE_INFO (API $API_LEVEL)"
echo ""

# Display module status summary
echo -e "${BLUE}Module Test Results:${NC}"
for module in "${MODULES[@]}"; do
    status="${MODULE_RESULTS[$module]}"
    if [ "$status" = "PASSED" ]; then
        echo -e "  $module: ${GREEN}✓ PASSED${NC}"
    elif [ "$status" = "FAILED" ]; then
        echo -e "  $module: ${RED}✗ FAILED${NC}"
    elif [ "$status" = "NO_TESTS" ]; then
        echo -e "  $module: ${YELLOW}⚠ NO TESTS${NC}"
    else
        echo -e "  $module: ${YELLOW}⚠ NOT FOUND${NC}"
    fi
done
echo ""

if [ "$OVERALL_STATUS" = "PASSED" ]; then
    echo -e "${GREEN}All automation tests passed successfully!${NC}"
    echo -e "${GREEN}UI/UX flows and accessibility compliance verified.${NC}"
    exit 0
else
    echo -e "${RED}Some automation tests failed. Check the reports for details.${NC}"
    exit 1
fi