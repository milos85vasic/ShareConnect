#!/bin/bash

# ShareConnect - Instrumentation Tests Execution Script
# This script runs all instrumentation tests and generates reports

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
TEST_TYPE="instrumentation_tests"
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND/${TEST_TYPE}"

echo -e "${BLUE}ShareConnect Instrumentation Tests Execution${NC}"
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
echo ""

# Create report directory
mkdir -p "$REPORT_DIR"

echo -e "${YELLOW}Starting Instrumentation Tests...${NC}"
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
adb shell pm clear com.shareconnect 2>/dev/null || true
adb shell pm clear com.shareconnect.test 2>/dev/null || true

# Ensure emulator is in a clean state
echo -e "${BLUE}Preparing emulator for testing...${NC}"
adb shell input keyevent KEYCODE_HOME
adb shell am force-stop com.shareconnect 2>/dev/null || true
sleep 2

# Run instrumentation tests with detailed output (abort on first failure)
echo -e "${BLUE}Running Instrumentation Test Suite...${NC}"
if timeout 900 ./gradlew :ShareConnector:connectedAndroidTest \
    -Pandroid.testInstrumentationRunnerArguments.package=com.shareconnect.database,com.shareconnect.activities \
    --info \
    --stacktrace \
    2>&1 | tee "${REPORT_DIR}/instrumentation_test_execution.log"; then
    echo -e "${GREEN}✓ Instrumentation tests completed successfully!${NC}"
    TEST_STATUS="PASSED"
else
    echo -e "${RED}✗ Instrumentation tests failed!${NC}"
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
    echo -e "${GREEN}✓ Additional test artifacts copied${NC}"
fi

# Generate summary report
cat > "${REPORT_DIR}/test_summary.txt" << EOF
ShareConnect Instrumentation Tests Execution Summary
===================================================

Execution Date: $(date)
Test Type: Instrumentation Tests
Test Suite: com.shareconnect.suites.InstrumentationTestSuite
Status: ${TEST_STATUS}
Device Information: $(adb devices | grep -v "List of devices" | head -1)

Test Classes Executed:
- ThemeRepositoryInstrumentationTest
- HistoryRepositoryInstrumentationTest
- MainActivityInstrumentationTest
- SettingsActivityInstrumentationTest

Report Location: ${REPORT_DIR}

Files Generated:
- instrumentation_test_execution.log: Full execution log
- test_summary.txt: This summary file
- index.html: HTML test report (if available)
- TEST-*.xml: JUnit XML results (if available)

Command Used:
./gradlew :ShareConnector:connectedAndroidTest --info --stacktrace

Requirements:
- Connected Android device or emulator
- USB debugging enabled
- Application debug build

EOF

echo ""
echo -e "${BLUE}Instrumentation Tests Execution Complete${NC}"
echo -e "${BLUE}=======================================${NC}"
echo "Status: ${TEST_STATUS}"
echo "Report Directory: ${REPORT_DIR}"
echo ""

if [ "$TEST_STATUS" = "PASSED" ]; then
    echo -e "${GREEN}All instrumentation tests passed successfully!${NC}"
    exit 0
else
    echo -e "${RED}Some instrumentation tests failed. Check the reports for details.${NC}"
    exit 1
fi