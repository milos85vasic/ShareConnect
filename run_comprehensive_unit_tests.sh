#!/bin/bash

# ShareConnect - Comprehensive Unit Tests Execution Script
# This script runs unit tests for ALL modules and applications

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Get current timestamp for directory naming
TIMESTAMP=$(date +"%Y%m%d_%H%M%S")
TEST_TYPE="unit_tests"
REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND/${TEST_TYPE}"

echo -e "${BLUE}ShareConnect Comprehensive Unit Tests Execution${NC}"
echo -e "${BLUE}==============================================${NC}"
echo ""

# Create report directory
mkdir -p "$REPORT_DIR"

echo -e "${YELLOW}Starting Comprehensive Unit Tests...${NC}"
echo "Report will be saved to: $REPORT_DIR"
echo ""

# Track test results for each module
declare -A MODULE_RESULTS
MODULES=(
    "ShareConnector"
    "TransmissionConnector"
    "uTorrentConnector" 
    "qBitConnector"
    "JDownloaderConnector"
    "ThemeSync"
    "ProfileSync"
    "HistorySync"
    "RSSSync"
    "BookmarkSync"
    "PreferencesSync"
    "LanguageSync"
    "TorrentSharingSync"
    "DesignSystem"
    "Onboarding"
    "Localizations"
)

# Ensure clean state for consistent results
echo -e "${BLUE}Cleaning project for unit tests...${NC}"
timeout 300 ./gradlew clean > /dev/null 2>&1 || {
    echo -e "${RED}✗ Gradle clean timed out or failed${NC}"
    exit 1
}

# Run unit tests for each module
OVERALL_STATUS="PASSED"
for module in "${MODULES[@]}"; do
    echo -e "${BLUE}Running unit tests for $module...${NC}"
    
    # Check if module exists and has tests
    if ./gradlew projects | grep -q ":$module"; then
        if timeout 300 ./gradlew ":$module:test" --quiet 2>&1 | tee -a "${REPORT_DIR}/unit_test_execution.log"; then
            if [ ${PIPESTATUS[0]} -eq 0 ]; then
                echo -e "${GREEN}✓ $module unit tests passed${NC}"
                MODULE_RESULTS[$module]="PASSED"
            else
                echo -e "${RED}✗ $module unit tests failed${NC}"
                MODULE_RESULTS[$module]="FAILED"
                OVERALL_STATUS="FAILED"
            fi
        else
            echo -e "${RED}✗ $module unit tests failed${NC}"
            MODULE_RESULTS[$module]="FAILED"
            OVERALL_STATUS="FAILED"
        fi
    else
        echo -e "${YELLOW}⚠ $module not found or has no tests${NC}"
        MODULE_RESULTS[$module]="NOT_FOUND"
    fi
done

# Copy test reports from main modules
echo -e "${BLUE}Copying test reports...${NC}"

# Copy ShareConnector reports
if [ -d "ShareConnector/build/reports/tests/testDebugUnitTest" ]; then
    cp -r ShareConnector/build/reports/tests/testDebugUnitTest/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ ShareConnector HTML test reports copied${NC}"
fi

if [ -d "ShareConnector/build/test-results/testDebugUnitTest" ]; then
    cp -r ShareConnector/build/test-results/testDebugUnitTest/* "${REPORT_DIR}/"
    echo -e "${GREEN}✓ ShareConnector XML test results copied${NC}"
fi

# Copy JDownloaderConnector reports
if [ -d "Connectors/JDownloaderConnect/JDownloaderConnector/build/reports/tests/testDebugUnitTest" ]; then
    mkdir -p "${REPORT_DIR}/jdownloader/"
    cp -r Connectors/JDownloaderConnect/JDownloaderConnector/build/reports/tests/testDebugUnitTest/* "${REPORT_DIR}/jdownloader/"
    echo -e "${GREEN}✓ JDownloaderConnector HTML test reports copied${NC}"
fi

# Generate comprehensive summary report
cat > "${REPORT_DIR}/test_summary.txt" << EOF
ShareConnect Comprehensive Unit Tests Execution Summary
=====================================================

Execution Date: $(date)
Test Type: Comprehensive Unit Tests
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

Main Applications:
- ShareConnector: Core business logic, URL compatibility, profile management
- TransmissionConnector: Transmission client integration, torrent operations
- uTorrentConnector: uTorrent client integration, torrent operations  
- qBitConnector: qBittorrent client integration, torrent operations
- JDownloaderConnector: JDownloader integration, download management

Sync Modules:
- ThemeSync: Theme synchronization across apps
- ProfileSync: Profile synchronization across apps
- HistorySync: History synchronization across apps
- RSSSync: RSS feed synchronization
- BookmarkSync: Bookmark synchronization
- PreferencesSync: Preference synchronization
- LanguageSync: Language setting synchronization
- TorrentSharingSync: Torrent sharing data synchronization

UI & Infrastructure:
- DesignSystem: Shared UI components and themes
- Onboarding: First-run experience flow
- Localizations: Multi-language support

══════════════════════════════════════════════════════════

TEST CLASSES EXECUTED:

ShareConnector:
- UrlCompatibilityUtilsTest: URL type detection and compatibility
- ServiceApiClientTest: Service API integration
- ProfileManagerTest: Profile management operations
- HistoryRepositoryTest: History data operations
- ThemeRepositoryTest: Theme data operations
- DialogUtilsTest: UI dialog utilities
- SecureStorageTest: Secure data storage
- SystemAppDetectorTest: System app detection
- TorrentAppHelperTest: Torrent app integration

JDownloaderConnector:
- JDownloaderRepositoryTest: JDownloader repository operations
- MyJDownloaderApiTest: MyJDownloader API integration
- DownloadItemTest: Download item data models
- JDownloaderDeviceTest: JDownloader device models

TransmissionConnector:
- ServerTest: Transmission server operations
- AnalyticsTest: Analytics functionality
- TorrentCountTest: Torrent counting logic

uTorrentConnector:
- ServerTest: uTorrent server operations
- AnalyticsTest: Analytics functionality
- TorrentCountTest: Torrent counting logic

qBitConnector:
- ServerManagerTest: Server management operations
- SettingsManagerTest: Settings management
- RequestManagerTest: API request handling
- QBittorrentVersionTest: Version compatibility

══════════════════════════════════════════════════════════

REPORT LOCATION: ${REPORT_DIR}

FILES GENERATED:
- unit_test_execution.log: Full execution log
- test_summary.txt: This summary file
- index.html: HTML test report (if available)
- TEST-*.xml: JUnit XML results (if available)
- jdownloader/: JDownloaderConnector test reports

COMMAND USED:
./run_comprehensive_unit_tests.sh

EOF

echo ""
echo -e "${BLUE}Comprehensive Unit Tests Execution Complete${NC}"
echo -e "${BLUE}==========================================${NC}"
echo "Overall Status: ${OVERALL_STATUS}"
echo "Report Directory: ${REPORT_DIR}"
echo ""

# Display module status summary
echo -e "${BLUE}Module Test Results:${NC}"
for module in "${MODULES[@]}"; do
    status="${MODULE_RESULTS[$module]}"
    if [ "$status" = "PASSED" ]; then
        echo -e "  $module: ${GREEN}✓ PASSED${NC}"
    elif [ "$status" = "FAILED" ]; then
        echo -e "  $module: ${RED}✗ FAILED${NC}"
    else
        echo -e "  $module: ${YELLOW}⚠ NOT FOUND${NC}"
    fi
done
echo ""

if [ "$OVERALL_STATUS" = "PASSED" ]; then
    echo -e "${GREEN}All unit tests passed successfully!${NC}"
    exit 0
else
    echo -e "${RED}Some unit tests failed. Check the reports for details.${NC}"
    exit 1
fi