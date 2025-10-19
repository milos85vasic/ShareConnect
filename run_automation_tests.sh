#!/bin/bash

# ShareConnect - Full Automation Tests Execution Script
# This script runs all automation UI/UX tests and generates reports

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

echo -e "${BLUE}ShareConnect Full Automation Tests Execution${NC}"
echo -e "${BLUE}===========================================${NC}"
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

echo -e "${YELLOW}Starting Full Automation Tests...${NC}"
echo "Report will be saved to: $REPORT_DIR"
echo ""

# Build the app first
echo -e "${BLUE}Building application...${NC}"
timeout 600 ./gradlew :ShareConnector:assembleDebug :ShareConnector:assembleDebugAndroidTest || {
    echo -e "${RED}✗ Build timed out or failed${NC}"
    exit 1
}

# Clean up any previous test data to ensure test isolation
echo -e "${BLUE}Cleaning up previous test data...${NC}"
timeout 30 adb shell pm clear com.shareconnect.debug 2>/dev/null || true
timeout 30 adb shell pm clear com.shareconnect.debug.test 2>/dev/null || true

# Install the app to ensure fresh state
echo -e "${BLUE}Installing application...${NC}"
timeout 300 ./gradlew :ShareConnector:installDebug || {
    echo -e "${RED}✗ Install timed out or failed${NC}"
    exit 1
}

# Ensure emulator is in a clean state
echo -e "${BLUE}Preparing emulator for testing...${NC}"
adb shell input keyevent KEYCODE_HOME
adb shell am force-stop com.shareconnect 2>/dev/null || true
sleep 2

# Run automation tests with detailed output (abort on first failure)
echo -e "${BLUE}Running Full Automation Test Suite...${NC}"
if timeout 1200 ./gradlew :ShareConnector:connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.package=com.shareconnect.automation \
    --info \
    --stacktrace \
    2>&1 | tee "${REPORT_DIR}/automation_test_execution.log"; then
    echo -e "${GREEN}✓ Automation tests completed successfully!${NC}"
    TEST_STATUS="PASSED"
else
    echo -e "${RED}✗ Automation tests failed!${NC}"
    TEST_STATUS="FAILED"
    exit 1
fi

# Copy test reports
echo -e "${BLUE}Copying test reports...${NC}"
if [ -d "ShareConnector/build/reports/androidTests/connected" ]; then
    cp -r ShareConnector/build/reports/androidTests/connected/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ HTML test reports copied${NC}"
fi

if [ -d "ShareConnector/build/outputs/androidTest-results/connected" ]; then
    cp -r ShareConnector/build/outputs/androidTest-results/connected/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ XML test results copied${NC}"
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

# Generate summary report
cat > "${REPORT_DIR}/test_summary.txt" << EOF
ShareConnect Full Automation Tests Execution Summary
===================================================

Execution Date: $(date)
Test Type: Full Automation UI/UX Tests
Test Suite: com.shareconnect.suites.FullAutomationTestSuite
Status: ${TEST_STATUS}

Device Information:
- Model: ${DEVICE_INFO}
- API Level: ${API_LEVEL}
- ADB Device: $(adb devices | grep -v "List of devices" | head -1)

Test Classes Executed:
- FullAppFlowAutomationTest
  * testCompleteFirstRunFlow
  * testCompleteThemeChangeFlow
  * testCompleteProfileManagementFlow
  * testCompleteHistoryFlow
  * testCompleteShareIntentFlow
  * testCompleteAppNavigationFlow
  * testAppStressTest

- AccessibilityAutomationTest
  * testAccessibilityLabels
  * testMinimumTouchTargetSize
  * testTextContrast
  * testKeyboardNavigation
  * testScreenReaderSupport
  * testContentLabeling
  * testStateDescriptions

Test Coverage:
- Complete user workflows end-to-end
- UI/UX interaction testing
- Accessibility compliance verification
- Application navigation flows
- Theme management functionality
- Profile management operations
- History viewing capabilities
- Share intent handling
- Stress testing with rapid navigation

Report Location: ${REPORT_DIR}

Files Generated:
- automation_test_execution.log: Full execution log
- test_summary.txt: This summary file
- index.html: HTML test report (if available)
- TEST-*.xml: JUnit XML results (if available)
- device_final_state.png: Final device screenshot

Command Used:
./gradlew connectedAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.shareconnect.suites.FullAutomationTestSuite --info --stacktrace

Requirements:
- Connected Android device or emulator
- USB debugging enabled
- Application installed and permissions granted
- Device unlocked during test execution

EOF

echo ""
echo -e "${BLUE}Full Automation Tests Execution Complete${NC}"
echo -e "${BLUE}=======================================${NC}"
echo "Status: ${TEST_STATUS}"
echo "Report Directory: ${REPORT_DIR}"
echo "Device: ${DEVICE_INFO} (API ${API_LEVEL})"
echo ""

if [ "$TEST_STATUS" = "PASSED" ]; then
    echo -e "${GREEN}All automation tests passed successfully!${NC}"
    echo -e "${GREEN}UI/UX flows and accessibility compliance verified.${NC}"
    exit 0
else
    echo -e "${RED}Some automation tests failed. Check the reports for details.${NC}"
    exit 1
fi