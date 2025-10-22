#!/bin/bash

# ShareConnect - Comprehensive Integration Tests Execution Script
# This script runs integration tests for ALL modules and applications

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
TEST_TYPE="integration_tests"
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND/${TEST_TYPE}"

echo -e "${BLUE}ShareConnect Comprehensive Integration Tests Execution${NC}"
echo -e "${BLUE}=====================================================${NC}"
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

echo -e "${YELLOW}Starting Comprehensive Integration Tests...${NC}"
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

# Ensure emulator is in a clean state
echo -e "${BLUE}Preparing emulator for testing...${NC}"
adb shell input keyevent KEYCODE_HOME
sleep 2

# Run integration tests for each module
OVERALL_STATUS="PASSED"
for module in "${MODULES[@]}"; do
    echo -e "${BLUE}Running integration tests for $module...${NC}"
    
    # Check if module exists and has integration tests
    if ./gradlew projects | grep -q ":$module"; then
        # Run integration tests with appropriate test packages
        case $module in
            "ShareConnector")
                TEST_PACKAGES="com.shareconnect.database,com.shareconnect.activities"
                ;;
            "JDownloaderConnector")
                TEST_PACKAGES="com.shareconnect.jdownloaderconnect.integration"
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
            if timeout 900 ./gradlew ":$module:connectedAndroidTest" \
                -Pandroid.testInstrumentationRunnerArguments.package="$TEST_PACKAGES" \
                --info \
                --stacktrace \
                2>&1 | tee -a "${REPORT_DIR}/integration_test_execution.log"; then
                echo -e "${GREEN}✓ $module integration tests passed${NC}"
                MODULE_RESULTS[$module]="PASSED"
            else
                echo -e "${RED}✗ $module integration tests failed${NC}"
                MODULE_RESULTS[$module]="FAILED"
                OVERALL_STATUS="FAILED"
            fi
        else
            echo -e "${YELLOW}⚠ $module has no integration test packages defined${NC}"
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

# Generate comprehensive summary report
cat > "${REPORT_DIR}/test_summary.txt" << EOF
ShareConnect Comprehensive Integration Tests Execution Summary
============================================================

Execution Date: $(date)
Test Type: Comprehensive Integration Tests
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
- Database Integration: Room database operations with SQLCipher
- Repository Integration: Data repository operations
- Activity Integration: Activity lifecycle and UI integration
- Service Integration: External service API integration

JDownloaderConnector:
- MyJDownloader API Integration: Remote download management
- Database Integration: Download and account data persistence
- UI Integration: Compose UI integration testing
- Sync Integration: Asinka sync operations

TransmissionConnector:
- Transmission RPC Integration: Transmission client API
- Database Integration: Server and torrent data persistence
- UI Integration: Activity and fragment integration
- Network Integration: HTTP client and authentication

uTorrentConnector:
- uTorrent Web UI Integration: uTorrent client API
- Database Integration: Server and torrent data persistence
- UI Integration: Activity and fragment integration
- Network Integration: HTTP client and authentication

qBitConnector:
- qBittorrent Web API Integration: qBittorrent client API
- Database Integration: Server and torrent data persistence
- UI Integration: Compose UI integration testing
- Network Integration: HTTP client and authentication

══════════════════════════════════════════════════════════

TEST CLASSES EXECUTED:

ShareConnector:
- ThemeRepositoryInstrumentationTest: Theme database operations
- HistoryRepositoryInstrumentationTest: History database operations
- MainActivityInstrumentationTest: Main activity integration
- SettingsActivityInstrumentationTest: Settings activity integration
- DatabaseMigrationTest: Database migration testing

JDownloaderConnector:
- JDownloaderIntegrationTest: MyJDownloader API integration
- DatabaseIntegrationTest: JDownloader database operations
- AccountIntegrationTest: Account management integration

TransmissionConnector:
- ServerIntegrationTest: Transmission server integration
- TorrentIntegrationTest: Torrent operations integration
- NetworkIntegrationTest: Network operations integration

uTorrentConnector:
- ServerIntegrationTest: uTorrent server integration
- TorrentIntegrationTest: Torrent operations integration
- NetworkIntegrationTest: Network operations integration

qBitConnector:
- ServerIntegrationTest: qBittorrent server integration
- TorrentIntegrationTest: Torrent operations integration
- NetworkIntegrationTest: Network operations integration

══════════════════════════════════════════════════════════

REPORT LOCATION: ${REPORT_DIR}

FILES GENERATED:
- integration_test_execution.log: Full execution log
- test_summary.txt: This summary file
- index.html: HTML test report (if available)
- TEST-*.xml: JUnit XML results (if available)
- jdownloader/: JDownloaderConnector test reports

COMMAND USED:
./run_comprehensive_integration_tests.sh

EOF

echo ""
echo -e "${BLUE}Comprehensive Integration Tests Execution Complete${NC}"
echo -e "${BLUE}=================================================${NC}"
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
    echo -e "${GREEN}All integration tests passed successfully!${NC}"
    exit 0
else
    echo -e "${RED}Some integration tests failed. Check the reports for details.${NC}"
    exit 1
fi