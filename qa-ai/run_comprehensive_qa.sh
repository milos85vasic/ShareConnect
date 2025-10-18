#!/bin/bash

# ShareConnect Comprehensive AI QA Test Runner
# This script runs the complete automated QA test suite with mock services

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORTS_DIR="$SCRIPT_DIR/reports/$(date +%Y%m%d_%H%M%S)"
LOGS_DIR="$REPORTS_DIR/logs"
SCREENSHOTS_DIR="$REPORTS_DIR/screenshots"

# Test scenarios to run
TEST_SCENARIOS=(
    "single_app_scenarios"
    "dual_app_scenarios"
    "triple_app_scenarios"
    "all_apps_scenarios"
    "lifecycle_scenarios"
    "sharing_scenarios"
)

# Parse command line arguments
RUN_ALL=true
SPECIFIC_SCENARIO=""
PARALLEL_EXECUTION=false
SKIP_CLEANUP=false
MAX_RETRIES=2

while [[ $# -gt 0 ]]; do
    case $1 in
        --scenario)
            RUN_ALL=false
            SPECIFIC_SCENARIO="$2"
            shift 2
            ;;
        --parallel)
            PARALLEL_EXECUTION=true
            shift
            ;;
        --skip-cleanup)
            SKIP_CLEANUP=true
            shift
            ;;
        --max-retries)
            MAX_RETRIES="$2"
            shift 2
            ;;
        --help)
            echo "ShareConnect Comprehensive AI QA Test Runner"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --scenario SCENARIO    Run specific test scenario"
            echo "  --parallel             Run tests in parallel where possible"
            echo "  --skip-cleanup         Skip cleanup after tests"
            echo "  --max-retries COUNT    Maximum retry attempts per test (default: 2)"
            echo "  --help                 Show this help message"
            echo ""
            echo "Available scenarios:"
            echo "  single_app_scenarios   - Tests for individual apps"
            echo "  dual_app_scenarios     - Tests for app pairs"
            echo "  triple_app_scenarios   - Tests for three apps"
            echo "  all_apps_scenarios     - Tests for all four apps"
            echo "  lifecycle_scenarios    - App lifecycle and state tests"
            echo "  sharing_scenarios      - URL sharing and integration tests"
            echo ""
            echo "Examples:"
            echo "  $0                          # Run all scenarios"
            echo "  $0 --scenario single_app_scenarios"
            echo "  $0 --parallel --max-retries 3"
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
echo "╔════════════════════════════════════════════════════════════════╗"
echo "║         ShareConnect Comprehensive AI QA Test Suite           ║"
echo "║         Powered by Claude AI with Mock Services               ║"
echo "╚════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

# Create report directories
mkdir -p "$REPORTS_DIR"
mkdir -p "$LOGS_DIR"
mkdir -p "$SCREENSHOTS_DIR"

echo -e "${YELLOW}Report Directory: $REPORTS_DIR${NC}"
echo -e "${YELLOW}Parallel Execution: $PARALLEL_EXECUTION${NC}"
echo -e "${YELLOW}Max Retries: $MAX_RETRIES${NC}"
echo ""

# Prerequisites check
echo -e "${YELLOW}[1/6] Checking prerequisites...${NC}"

# Check for required tools
for tool in adb emulator java gradle; do
    if ! command -v "$tool" >/dev/null 2>&1; then
        echo -e "${RED}ERROR: $tool is not installed or not in PATH${NC}"
        exit 1
    fi
done

# Check for API key
if [ -z "$ANTHROPIC_API_KEY" ]; then
    echo -e "${RED}ERROR: ANTHROPIC_API_KEY environment variable is not set${NC}"
    exit 1
fi

echo -e "${GREEN}✓ All prerequisites met${NC}"

# Build the project
echo -e "${YELLOW}[2/6] Building project...${NC}"

cd "$PROJECT_ROOT"

# Build all apps
./gradlew assembleDebug >> "$LOGS_DIR/build.log" 2>&1
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: Project build failed${NC}"
    echo "Check build log: $LOGS_DIR/build.log"
    exit 1
fi

# Build QA module
./gradlew :qa-ai:assembleDebug >> "$LOGS_DIR/qa_build.log" 2>&1
if [ $? -ne 0 ]; then
    echo -e "${RED}ERROR: QA module build failed${NC}"
    echo "Check QA build log: $LOGS_DIR/qa_build.log"
    exit 1
fi

echo -e "${GREEN}✓ Project built successfully${NC}"

# Start mock services
echo -e "${YELLOW}[3/6] Starting mock services...${NC}"

"$SCRIPT_DIR/start_mock_services.sh" >> "$LOGS_DIR/mock_services.log" 2>&1 &
MOCK_SERVICES_PID=$!

# Wait for services to be ready
sleep 10

# Verify services are running
if ! curl -s "http://localhost:8081/health" >/dev/null 2>&1; then
    echo -e "${RED}ERROR: Mock services failed to start${NC}"
    kill $MOCK_SERVICES_PID 2>/dev/null || true
    exit 1
fi

echo -e "${GREEN}✓ Mock services started (PID: $MOCK_SERVICES_PID)${NC}"

# Determine which scenarios to run
if [[ "$RUN_ALL" == true ]]; then
    SCENARIOS_TO_RUN=("${TEST_SCENARIOS[@]}")
else
    SCENARIOS_TO_RUN=("$SPECIFIC_SCENARIO")
fi

# Initialize test results
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
TOTAL_DURATION=0

# Run test scenarios
echo -e "${YELLOW}[4/6] Executing test scenarios...${NC}"

for scenario in "${SCENARIOS_TO_RUN[@]}"; do
    echo -e "${BLUE}Running scenario: $scenario${NC}"

    SCENARIO_START_TIME=$(date +%s)

    # Run scenario tests
    if run_scenario_tests "$scenario"; then
        echo -e "${GREEN}✓ Scenario $scenario completed${NC}"
    else
        echo -e "${RED}✗ Scenario $scenario failed${NC}"
    fi

    SCENARIO_DURATION=$(( $(date +%s) - SCENARIO_START_TIME ))
    echo -e "${YELLOW}Scenario duration: ${SCENARIO_DURATION}s${NC}"
    echo ""
done

# Generate comprehensive report
echo -e "${YELLOW}[5/6] Generating comprehensive report...${NC}"

generate_comprehensive_report

echo -e "${GREEN}✓ Comprehensive report generated${NC}"

# Cleanup
echo -e "${YELLOW}[6/6] Cleaning up...${NC}"

if [[ "$SKIP_CLEANUP" == false ]]; then
    # Stop mock services
    "$SCRIPT_DIR/start_mock_services.sh" --stop >> "$LOGS_DIR/cleanup.log" 2>&1

    # Cleanup emulator
    "$SCRIPT_DIR/cleanup_emulator.sh" >> "$LOGS_DIR/cleanup.log" 2>&1

    echo -e "${GREEN}✓ Cleanup completed${NC}"
else
    echo -e "${YELLOW}✓ Cleanup skipped${NC}"
fi

# Final summary
echo ""
echo -e "${BLUE}═════════════════════════════════════════════════════════════════${NC}"
echo -e "${BLUE}                     FINAL TEST SUMMARY                         ${NC}"
echo -e "${BLUE}═════════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "Total Test Scenarios: ${#SCENARIOS_TO_RUN[@]}"
echo -e "Total Tests Executed: $TOTAL_TESTS"
echo -e "Tests Passed: $PASSED_TESTS"
echo -e "Tests Failed: $FAILED_TESTS"

if [[ $TOTAL_TESTS -gt 0 ]]; then
    SUCCESS_RATE=$(( (PASSED_TESTS * 100) / TOTAL_TESTS ))
    AVERAGE_DURATION=$(( TOTAL_DURATION / TOTAL_TESTS ))
    echo -e "Success Rate: ${SUCCESS_RATE}%"
    echo -e "Average Test Duration: ${AVERAGE_DURATION}s"
fi

echo ""
echo -e "Reports Location: $REPORTS_DIR"
echo -e "HTML Report: file://$REPORTS_DIR/comprehensive_report.html"
echo -e "JSON Report: $REPORTS_DIR/comprehensive_report.json"
echo ""

if [[ $FAILED_TESTS -eq 0 ]]; then
    echo -e "${GREEN}╔═════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                   ALL TESTS PASSED ✓                           ║${NC}"
    echo -e "${GREEN}║              100% SUCCESS RATE ACHIEVED                       ║${NC}"
    echo -e "${GREEN}╚═════════════════════════════════════════════════════════════════╝${NC}"
    exit 0
else
    echo -e "${RED}╔═════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║                 SOME TESTS FAILED ✗                           ║${NC}"
    echo -e "${RED}║              REVIEW REPORTS FOR DETAILS                       ║${NC}"
    echo -e "${RED}╚═════════════════════════════════════════════════════════════════╝${NC}"
    exit 1
fi

# Function to run tests for a specific scenario
run_scenario_tests() {
    local scenario=$1
    local scenario_passed=true

    # Map scenario to app scenario
    local app_scenario=""
    case $scenario in
        "single_app_scenarios")
            app_scenario="single_shareconnector"
            ;;
        "dual_app_scenarios")
            app_scenario="dual_shareconnector_transmission"
            ;;
        "triple_app_scenarios")
            app_scenario="triple_shareconnector_transmission_utorrent"
            ;;
        "all_apps_scenarios"|"lifecycle_scenarios"|"sharing_scenarios")
            app_scenario="all_apps"
            ;;
    esac

    # Start clean emulator for this scenario
    "$SCRIPT_DIR/start_clean_emulator.sh" --scenario "$app_scenario" --no-clean >> "$LOGS_DIR/emulator_$scenario.log" 2>&1

    # Run the AI QA tests for this scenario
    if timeout 3600 ./gradlew :qa-ai:executeTestScenario -Pscenario="$scenario" -PmaxRetries="$MAX_RETRIES" >> "$LOGS_DIR/test_$scenario.log" 2>&1; then
        echo -e "${GREEN}Tests passed for scenario: $scenario${NC}"
    else
        echo -e "${RED}Tests failed for scenario: $scenario${NC}"
        scenario_passed=false
    fi

    # Collect results
    local scenario_tests=$(grep "Tests run:" "$LOGS_DIR/test_$scenario.log" | tail -1 | sed 's/.*Tests run: \([0-9]*\).*/\1/')
    local scenario_passed_count=$(grep "Tests run:" "$LOGS_DIR/test_$scenario.log" | tail -1 | sed 's/.*Tests run: \([0-9]*\), Failures: \([0-9]*\).*/\1-\2/')

    if [[ -n "$scenario_tests" ]]; then
        TOTAL_TESTS=$((TOTAL_TESTS + scenario_tests))
        PASSED_TESTS=$((PASSED_TESTS + scenario_passed_count))
        FAILED_TESTS=$((FAILED_TESTS + (scenario_tests - scenario_passed_count)))
    fi

    # Cleanup emulator for next scenario
    "$SCRIPT_DIR/cleanup_emulator.sh" >> "$LOGS_DIR/cleanup_$scenario.log" 2>&1

    return $([ "$scenario_passed" == "true" ])
}

# Function to generate comprehensive report
generate_comprehensive_report() {
    local report_file="$REPORTS_DIR/comprehensive_report.html"
    local json_report_file="$REPORTS_DIR/comprehensive_report.json"

    # Generate HTML report
    cat > "$report_file" << EOF
<!DOCTYPE html>
<html>
<head>
    <title>ShareConnect Comprehensive AI QA Report</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .header { background: #2c3e50; color: white; padding: 20px; border-radius: 5px; }
        .summary { background: #ecf0f1; padding: 20px; margin: 20px 0; border-radius: 5px; }
        .scenario { background: #f8f9fa; padding: 15px; margin: 10px 0; border-left: 4px solid #3498db; }
        .passed { color: #27ae60; }
        .failed { color: #e74c3c; }
        .warning { color: #f39c12; }
        table { width: 100%; border-collapse: collapse; margin: 20px 0; }
        th, td { padding: 12px; text-align: left; border-bottom: 1px solid #ddd; }
        th { background-color: #f8f9fa; }
    </style>
</head>
<body>
    <div class="header">
        <h1>ShareConnect Comprehensive AI QA Report</h1>
        <p>Generated on: $(date)</p>
        <p>Test Environment: Android Emulator with Mock Services</p>
    </div>

    <div class="summary">
        <h2>Test Summary</h2>
        <table>
            <tr><th>Metric</th><th>Value</th></tr>
            <tr><td>Total Scenarios</td><td>${#SCENARIOS_TO_RUN[@]}</td></tr>
            <tr><td>Total Tests</td><td>$TOTAL_TESTS</td></tr>
            <tr><td>Tests Passed</td><td class="passed">$PASSED_TESTS</td></tr>
            <tr><td>Tests Failed</td><td class="failed">$FAILED_TESTS</td></tr>
            <tr><td>Success Rate</td><td>$([ $TOTAL_TESTS -gt 0 ] && echo "$(( (PASSED_TESTS * 100) / TOTAL_TESTS ))%" || echo "N/A")</td></tr>
            <tr><td>Average Duration</td><td>$([ $TOTAL_TESTS -gt 0 ] && echo "$(( TOTAL_DURATION / TOTAL_TESTS ))s" || echo "N/A")</td></tr>
        </table>
    </div>

    <h2>Test Scenarios Executed</h2>
EOF

    # Add scenario details
    for scenario in "${SCENARIOS_TO_RUN[@]}"; do
        cat >> "$report_file" << EOF
    <div class="scenario">
        <h3>$scenario</h3>
        <p><strong>Status:</strong> <span class="passed">Completed</span></p>
        <p><strong>Log:</strong> <a href="logs/test_$scenario.log">test_$scenario.log</a></p>
        <p><strong>Emulator Log:</strong> <a href="logs/emulator_$scenario.log">emulator_$scenario.log</a></p>
    </div>
EOF
    done

    # Close HTML
    cat >> "$report_file" << EOF
    <div class="summary">
        <h2>System Information</h2>
        <ul>
            <li><strong>Android Version:</strong> API 34 (Android 14)</li>
            <li><strong>Emulator:</strong> Pixel 6</li>
            <li><strong>Mock Services:</strong> All enabled</li>
            <li><strong>AI Provider:</strong> Claude (Anthropic)</li>
            <li><strong>Test Framework:</strong> AndroidX Test + UIAutomator</li>
        </ul>
    </div>

    <div class="summary">
        <h2>Recommendations</h2>
        <ul>
            <li>Review failed test logs for root cause analysis</li>
            <li>Check screenshots for UI validation failures</li>
            <li>Verify mock service logs for service-related issues</li>
            <li>Consider re-running failed tests individually</li>
        </ul>
    </div>
</body>
</html>
EOF

    # Generate JSON report
    cat > "$json_report_file" << EOF
{
    "report_type": "comprehensive_qa_report",
    "generated_at": "$(date -Iseconds)",
    "test_environment": {
        "android_version": "API 34",
        "emulator": "Pixel 6",
        "mock_services": true,
        "ai_provider": "Claude (Anthropic)"
    },
    "summary": {
        "total_scenarios": ${#SCENARIOS_TO_RUN[@]},
        "total_tests": $TOTAL_TESTS,
        "passed_tests": $PASSED_TESTS,
        "failed_tests": $FAILED_TESTS,
        "success_rate": $([ $TOTAL_TESTS -gt 0 ] && echo "$(( (PASSED_TESTS * 10000) / TOTAL_TESTS )) / 100" || echo "0"),
        "average_duration_seconds": $([ $TOTAL_TESTS -gt 0 ] && echo "$(( TOTAL_DURATION / TOTAL_TESTS ))" || echo "0")
    },
    "scenarios": [
EOF

    # Add scenario details to JSON
    for i in "${!SCENARIOS_TO_RUN[@]}"; do
        scenario="${SCENARIOS_TO_RUN[$i]}"
        comma=$([ $i -lt $((${#SCENARIOS_TO_RUN[@]} - 1)) ] && echo "," || echo "")

        cat >> "$json_report_file" << EOF
        {
            "name": "$scenario",
            "status": "completed",
            "logs": {
                "test_log": "logs/test_$scenario.log",
                "emulator_log": "logs/emulator_$scenario.log",
                "cleanup_log": "logs/cleanup_$scenario.log"
            }
        }$comma
EOF
    done

    # Close JSON
    cat >> "$json_report_file" << EOF
    ],
    "configuration": {
        "parallel_execution": $PARALLEL_EXECUTION,
        "max_retries": $MAX_RETRIES,
        "cleanup_skipped": $SKIP_CLEANUP
    }
}
EOF
}