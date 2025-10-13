#!/bin/bash

# ShareConnect - Full Test Suite Execution Script
# This script runs unit tests and full application crash tests

set -e

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
MASTER_REPORT_DIR="Documentation/Tests/${TIMESTAMP}_TEST_ROUND"

echo -e "${BOLD}${CYAN}ShareConnect Full Test Suite Execution${NC}"
echo -e "${BOLD}${CYAN}=====================================${NC}"
echo ""
echo -e "${BLUE}Starting comprehensive testing of ShareConnect applications${NC}"
echo -e "${BLUE}Test Round: ${TIMESTAMP}${NC}"
echo ""

# Create master report directory
mkdir -p "$MASTER_REPORT_DIR"

# Track test results
UNIT_TEST_STATUS="NOT_RUN"
CRASH_TEST_STATUS="NOT_RUN"

# Track execution times
START_TIME=$(date +%s)

echo -e "${BOLD}${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${YELLOW}                    UNIT TESTS                            ${NC}"
echo -e "${BOLD}${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo ""

# Run Unit Tests
echo -e "${BLUE}Executing unit tests...${NC}"
if ./run_unit_tests.sh; then
    UNIT_TEST_STATUS="PASSED"
    echo -e "${GREEN}✓ Unit tests completed successfully${NC}"
else
    UNIT_TEST_STATUS="FAILED"
    echo -e "${RED}✗ Unit tests failed${NC}"
fi

UNIT_END_TIME=$(date +%s)
UNIT_DURATION=$((UNIT_END_TIME - START_TIME))

echo -e "${BOLD}${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${YELLOW}                 CRASH TESTS                             ${NC}"
echo -e "${BOLD}${YELLOW}═══════════════════════════════════════════════════════════${NC}"
echo ""

# Run Crash Tests
CRASH_START_TIME=$(date +%s)
echo -e "${BLUE}Executing full application crash tests...${NC}"
if ./run_full_app_crash_test.sh; then
    CRASH_TEST_STATUS="PASSED"
    echo -e "${GREEN}✓ Crash tests completed successfully${NC}"
else
    CRASH_TEST_STATUS="FAILED"
    echo -e "${RED}✗ Crash tests failed${NC}"
fi

CRASH_END_TIME=$(date +%s)
CRASH_DURATION=$((CRASH_END_TIME - CRASH_START_TIME))
TOTAL_DURATION=$((CRASH_END_TIME - START_TIME))

# Determine overall status
OVERALL_STATUS="$UNIT_TEST_STATUS"
if [ "$CRASH_TEST_STATUS" != "PASSED" ]; then
    OVERALL_STATUS="FAILED"
fi

echo ""
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo -e "${BOLD}${CYAN}                    EXECUTION SUMMARY                      ${NC}"
echo -e "${BOLD}${CYAN}═══════════════════════════════════════════════════════════${NC}"
echo ""

# Generate master summary report
cat > "${MASTER_REPORT_DIR}/master_test_summary.txt" << EOF
ShareConnect Full Test Suite Execution Summary
==============================================

Execution Date: $(date)
Test Round ID: ${TIMESTAMP}
Overall Status: ${OVERALL_STATUS}

══════════════════════════════════════════════════════════

UNIT TESTS
Status: ${UNIT_TEST_STATUS}
Duration: ${UNIT_DURATION} seconds
Test Suite: com.shareconnect.suites.UnitTestSuite
Coverage: Core business logic and data models

Test Classes:
- MainActivityUnitTest: Main activity unit testing
- HistoryRepositoryUnitTest: History repository operations
- ThemeRepositoryUnitTest: Theme repository operations
- ProfileManagerUnitTest: Profile management operations

CRASH TESTS
Status: ${CRASH_TEST_STATUS}
Duration: ${CRASH_DURATION} seconds
Test Suite: Full Application Crash Test Suite
Coverage: All 4 Android applications crash testing

Test Coverage:
- ShareConnector: App launch, restart, and sync operations
- TransmissionConnector: App launch, restart, and sync operations
- uTorrentConnector: App launch, restart, and sync operations
- qBitConnector: App launch, restart, and sync operations
- Asinka Library: Sync operations and gRPC functionality
- Crash Detection: Logcat monitoring for crashes and exceptions

══════════════════════════════════════════════════════════

EXECUTION METRICS
Total Duration: ${TOTAL_DURATION} seconds ($(printf '%02d:%02d:%02d' $((TOTAL_DURATION/3600)) $((TOTAL_DURATION%3600/60)) $((TOTAL_DURATION%60))))
Unit Tests: ${UNIT_DURATION}s
Crash Tests: ${CRASH_DURATION}s

COVERAGE SUMMARY
✓ Business Logic: Unit tests verify core functionality
✓ Data Operations: Unit tests verify repository operations
✓ UI Logic: Unit tests verify activity behavior
✓ App Stability: Crash tests verify no crashes on launch/restart
✓ Sync Operations: Crash tests verify Asinka library functionality
✓ Cross-App Compatibility: All 4 apps tested simultaneously

REPORT STRUCTURE
${MASTER_REPORT_DIR}/
├── master_test_summary.txt (this file)
├── unit_tests/
│   ├── test_summary.txt
│   ├── unit_test_execution.log
│   └── [HTML/XML reports]
└── full_app_crash_test/
    ├── full_crash_test_report.txt
    ├── unit_tests.log
    ├── instrumentation_tests.log
    ├── final_device_state.png
    └── [additional test artifacts]

COMMAND EXECUTION
./run_all_tests.sh

This script executed:
1. ./run_unit_tests.sh
2. ./run_full_app_crash_test.sh

═════════════════════════════════════════════════════════

EOF

# Display summary
echo -e "${BLUE}Test Execution Summary:${NC}"
echo -e "${BLUE}=======================${NC}"
echo ""
echo -e "Unit Tests:          ${UNIT_TEST_STATUS} (${UNIT_DURATION}s)"
echo -e "Crash Tests:         ${CRASH_TEST_STATUS} (${CRASH_DURATION}s)"
echo ""
echo -e "Total Duration:      ${TOTAL_DURATION}s ($(printf '%02d:%02d:%02d' $((TOTAL_DURATION/3600)) $((TOTAL_DURATION%3600/60)) $((TOTAL_DURATION%60))))"
echo -e "Overall Status:      ${OVERALL_STATUS}"
echo ""
echo -e "${BLUE}Report Directory:    ${MASTER_REPORT_DIR}${NC}"
echo ""

# Update README badges with test results
echo -e "${CYAN}Updating README test badges...${NC}"
if [ -f "./update_badges.sh" ]; then
    ./update_badges.sh
    echo -e "${GREEN}✓ Badges updated${NC}"
else
    echo -e "${YELLOW}⚠ Badge update script not found${NC}"
fi
echo ""

if [ "$OVERALL_STATUS" = "PASSED" ]; then
    echo -e "${BOLD}${GREEN}🎉 ALL TESTS PASSED! 🎉${NC}"
    echo -e "${GREEN}ShareConnect application tests have been successfully executed.${NC}"
    echo -e "${GREEN}✓ Business logic is correct${NC}"
    echo -e "${GREEN}✓ Repository operations work properly${NC}"
    echo -e "${GREEN}✓ Activity logic functions as expected${NC}"
    echo -e "${GREEN}✓ All applications launch and restart without crashes${NC}"
    echo -e "${GREEN}✓ Asinka sync operations are functioning${NC}"
    exit 0
else
    echo -e "${BOLD}${RED}❌ TESTS FAILED ❌${NC}"
    echo -e "${RED}Please review the test reports for details.${NC}"

    if [ "$UNIT_TEST_STATUS" != "PASSED" ]; then
        echo -e "${RED}• Unit tests need attention${NC}"
    fi

    if [ "$CRASH_TEST_STATUS" != "PASSED" ]; then
        echo -e "${RED}• Crash tests need attention${NC}"
    fi

    exit 1
fi