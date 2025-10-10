#!/bin/bash

# ShareConnect AI QA Test Runner
# This script executes AI-powered QA tests for the ShareConnect application

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
REPORT_DIR="qa-ai/reports/$(date +%Y%m%d_%H%M%S)"
SCREENSHOT_DIR="qa-ai/screenshots/$(date +%Y%m%d_%H%M%S)"
LOG_FILE="$REPORT_DIR/execution.log"

# Parse command line arguments
SUITE=""
CATEGORY=""
TEST_ID=""
PRIORITY=""
TAG=""

while [[ $# -gt 0 ]]; do
    case $1 in
        --suite)
            SUITE="$2"
            shift 2
            ;;
        --category)
            CATEGORY="$2"
            shift 2
            ;;
        --test)
            TEST_ID="$2"
            shift 2
            ;;
        --priority)
            PRIORITY="$2"
            shift 2
            ;;
        --tag)
            TAG="$2"
            shift 2
            ;;
        --help)
            echo "ShareConnect AI QA Test Runner"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --suite SUITE_ID       Run specific test suite"
            echo "  --category CATEGORY    Run tests from specific category"
            echo "  --test TEST_ID         Run specific test case"
            echo "  --priority PRIORITY    Run tests with specific priority (CRITICAL, HIGH, MEDIUM, LOW)"
            echo "  --tag TAG              Run tests with specific tag"
            echo "  --help                 Show this help message"
            echo ""
            echo "Examples:"
            echo "  $0                                    # Run all tests"
            echo "  $0 --suite smoke_test_suite          # Run smoke tests"
            echo "  $0 --category PROFILE_MANAGEMENT     # Run profile tests"
            echo "  $0 --test TC_PROF_001                # Run specific test"
            echo "  $0 --priority HIGH                   # Run high priority tests"
            echo "  $0 --tag smoke                       # Run tests tagged with 'smoke'"
            exit 0
            ;;
        *)
            echo -e "${RED}Unknown option: $1${NC}"
            echo "Use --help for usage information"
            exit 1
            ;;
    esac
done

# Banner
echo -e "${BLUE}"
echo "╔═══════════════════════════════════════════════════╗"
echo "║      ShareConnect AI QA Test Runner v1.0.0       ║"
echo "║         Powered by Claude AI (Anthropic)         ║"
echo "╚═══════════════════════════════════════════════════╝"
echo -e "${NC}"

# Check prerequisites
echo -e "${YELLOW}[1/7] Checking prerequisites...${NC}"

# Check for ANTHROPIC_API_KEY
if [ -z "$ANTHROPIC_API_KEY" ]; then
    echo -e "${RED}ERROR: ANTHROPIC_API_KEY environment variable is not set${NC}"
    echo "Please set your Claude API key: export ANTHROPIC_API_KEY=your_key_here"
    exit 1
fi
echo -e "${GREEN}✓ API key configured${NC}"

# Check for connected devices
DEVICE_COUNT=$(adb devices | grep -v "List" | grep "device$" | wc -l)
if [ $DEVICE_COUNT -eq 0 ]; then
    echo -e "${RED}ERROR: No Android devices/emulators found${NC}"
    echo "Please connect a device or start an emulator"
    echo "Check with: adb devices"
    exit 1
fi
echo -e "${GREEN}✓ Found $DEVICE_COUNT device(s)${NC}"

# Create report directories
echo -e "${YELLOW}[2/7] Preparing test environment...${NC}"
mkdir -p "$REPORT_DIR"
mkdir -p "$SCREENSHOT_DIR"
echo -e "${GREEN}✓ Report directory: $REPORT_DIR${NC}"
echo -e "${GREEN}✓ Screenshot directory: $SCREENSHOT_DIR${NC}"

# Build the qa-ai module
echo -e "${YELLOW}[3/7] Building AI QA module...${NC}"
./gradlew :qa-ai:assembleDebug >> "$LOG_FILE" 2>&1
if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ Build successful${NC}"
else
    echo -e "${RED}✗ Build failed${NC}"
    echo "Check log file: $LOG_FILE"
    exit 1
fi

# Generate test data if needed
echo -e "${YELLOW}[4/7] Generating test data...${NC}"
./gradlew :qa-ai:generateTestData >> "$LOG_FILE" 2>&1
echo -e "${GREEN}✓ Test data generated${NC}"

# Prepare test arguments
TEST_ARGS=""
if [ -n "$SUITE" ]; then
    TEST_ARGS="$TEST_ARGS -e suite $SUITE"
    echo -e "${BLUE}Test Suite: $SUITE${NC}"
fi
if [ -n "$CATEGORY" ]; then
    TEST_ARGS="$TEST_ARGS -e category $CATEGORY"
    echo -e "${BLUE}Test Category: $CATEGORY${NC}"
fi
if [ -n "$TEST_ID" ]; then
    TEST_ARGS="$TEST_ARGS -e test $TEST_ID"
    echo -e "${BLUE}Test ID: $TEST_ID${NC}"
fi
if [ -n "$PRIORITY" ]; then
    TEST_ARGS="$TEST_ARGS -e priority $PRIORITY"
    echo -e "${BLUE}Priority: $PRIORITY${NC}"
fi
if [ -n "$TAG" ]; then
    TEST_ARGS="$TEST_ARGS -e tag $TAG"
    echo -e "${BLUE}Tag: $TAG${NC}"
fi

# Install the test APK
echo -e "${YELLOW}[5/7] Installing test application...${NC}"
./gradlew :ShareConnector:installDebug >> "$LOG_FILE" 2>&1
./gradlew :qa-ai:installDebugAndroidTest >> "$LOG_FILE" 2>&1
echo -e "${GREEN}✓ Application installed${NC}"

# Run the AI QA tests
echo -e "${YELLOW}[6/7] Executing AI-powered tests...${NC}"
echo -e "${BLUE}This may take several minutes. Please wait...${NC}"
echo ""

START_TIME=$(date +%s)

# Execute tests with instrumentation runner
adb shell am instrument -w -r \
    -e debug false \
    -e reportDir "$REPORT_DIR" \
    -e screenshotDir "$SCREENSHOT_DIR" \
    $TEST_ARGS \
    com.shareconnect.qa.ai.test/androidx.test.runner.AndroidJUnitRunner \
    2>&1 | tee -a "$LOG_FILE"

TEST_EXIT_CODE=${PIPESTATUS[0]}
END_TIME=$(date +%s)
DURATION=$((END_TIME - START_TIME))

# Generate reports
echo ""
echo -e "${YELLOW}[7/7] Generating reports...${NC}"
./gradlew :qa-ai:analyzeResults >> "$LOG_FILE" 2>&1
echo -e "${GREEN}✓ Reports generated${NC}"

# Print summary
echo ""
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                  TEST SUMMARY                     ${NC}"
echo -e "${BLUE}═══════════════════════════════════════════════════${NC}"
echo -e "Duration: ${DURATION}s"
echo -e "Report Directory: $REPORT_DIR"
echo -e "Screenshot Directory: $SCREENSHOT_DIR"
echo -e "Log File: $LOG_FILE"
echo ""

if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo -e "${GREEN}╔═══════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║              ALL TESTS PASSED ✓                   ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "View HTML report: file://$PWD/$REPORT_DIR/html/index.html"
    exit 0
else
    echo -e "${RED}╔═══════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║            SOME TESTS FAILED ✗                    ║${NC}"
    echo -e "${RED}╚═══════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "View failure details: file://$PWD/$REPORT_DIR/html/index.html"
    echo -e "Check log file: $LOG_FILE"
    exit 1
fi
