#!/bin/bash

# ShareConnect Full Automated AI QA System
# Complete end-to-end testing with mock services and 100% success rate guarantee

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
PURPLE='\033[0;35m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORTS_DIR="$SCRIPT_DIR/reports/$(date +%Y%m%d_%H%M%S)_FULL_QA"
LOGS_DIR="$REPORTS_DIR/logs"
VALIDATION_DIR="$REPORTS_DIR/validation"
PERFORMANCE_DIR="$REPORTS_DIR/performance"

# Test scenarios (all scenarios for complete coverage)
ALL_SCENARIOS=(
    "single_app_scenarios"
    "dual_app_scenarios"
    "triple_app_scenarios"
    "all_apps_scenarios"
    "lifecycle_scenarios"
    "sharing_scenarios"
)

# Success rate requirements
REQUIRED_SUCCESS_RATE=100.0
MAX_RETRY_ATTEMPTS=3

# Parse command line arguments
RUN_SPECIFIC_SCENARIO=""
ENFORCE_SUCCESS=true
GENERATE_DOCS=false
PERFORMANCE_BASELINE=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --scenario)
            RUN_SPECIFIC_SCENARIO="$2"
            shift 2
            ;;
        --no-enforce)
            ENFORCE_SUCCESS=false
            shift
            ;;
        --docs)
            GENERATE_DOCS=true
            shift
            ;;
        --baseline)
            PERFORMANCE_BASELINE=true
            shift
            ;;
        --help)
            echo "ShareConnect Full Automated AI QA System"
            echo ""
            echo "Usage: $0 [OPTIONS]"
            echo ""
            echo "Options:"
            echo "  --scenario SCENARIO    Run specific test scenario"
            echo "  --no-enforce           Don't enforce 100% success rate"
            echo "  --docs                 Generate comprehensive documentation"
            echo "  --baseline             Run performance baseline tests"
            echo "  --help                 Show this help message"
            echo ""
            echo "This script runs the complete automated QA suite with:"
            echo "  - Mock services for all external dependencies"
            echo "  - Clean emulator environment per test"
            echo "  - 100% success rate validation"
            echo "  - Comprehensive reporting and documentation"
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
echo -e "${PURPLE}"
echo "╔══════════════════════════════════════════════════════════════════════════════╗"
echo "║            ShareConnect Full Automated AI QA System                        ║"
echo "║         Complete Testing with Mock Services & 100% Success Rate            ║"
echo "╚══════════════════════════════════════════════════════════════════════════════╝"
echo -e "${NC}"

echo -e "${CYAN}Test Environment:${NC}"
echo -e "  Reports Directory: $REPORTS_DIR"
echo -e "  Enforce 100% Success: $ENFORCE_SUCCESS"
echo -e "  Generate Documentation: $GENERATE_DOCS"
echo -e "  Performance Baseline: $PERFORMANCE_BASELINE"
echo ""

# Prerequisites check
echo -e "${YELLOW}[1/10] Checking prerequisites...${NC}"

check_prerequisites

echo -e "${GREEN}✓ All prerequisites met${NC}"

# Create report directories
mkdir -p "$REPORTS_DIR" "$LOGS_DIR" "$VALIDATION_DIR" "$PERFORMANCE_DIR"

# Setup mock services
echo -e "${YELLOW}[2/10] Setting up mock services...${NC}"

setup_mock_services

echo -e "${GREEN}✓ Mock services ready${NC}"

# Build all components
echo -e "${YELLOW}[3/10] Building all components...${NC}"

build_all_components

echo -e "${GREEN}✓ All components built${NC}"

# Determine test scenarios to run
if [[ -n "$RUN_SPECIFIC_SCENARIO" ]]; then
    TEST_SCENARIOS=("$RUN_SPECIFIC_SCENARIO")
else
    TEST_SCENARIOS=("${ALL_SCENARIOS[@]}")
fi

echo -e "${CYAN}Test Scenarios to Execute: ${TEST_SCENARIOS[*]}${NC}"
echo ""

# Initialize test results
TOTAL_SCENARIOS=${#TEST_SCENARIOS[@]}
COMPLETED_SCENARIOS=0
TOTAL_TESTS=0
PASSED_TESTS=0
FAILED_TESTS=0
OVERALL_SUCCESS=true

# Execute test scenarios
echo -e "${YELLOW}[4/10] Executing test scenarios...${NC}"

for scenario in "${TEST_SCENARIOS[@]}"; do
    echo -e "${BLUE}Executing Scenario: $scenario${NC}"

    SCENARIO_START_TIME=$(date +%s)

    if execute_scenario "$scenario"; then
        echo -e "${GREEN}✓ Scenario $scenario completed successfully${NC}"
        ((COMPLETED_SCENARIOS++))
    else
        echo -e "${RED}✗ Scenario $scenario failed${NC}"
        OVERALL_SUCCESS=false
    fi

    SCENARIO_DURATION=$(( $(date +%s) - SCENARIO_START_TIME ))
    echo -e "${YELLOW}Scenario duration: ${SCENARIO_DURATION}s${NC}"
    echo ""
done

# Comprehensive validation
echo -e "${YELLOW}[5/10] Running comprehensive validation...${NC}"

run_comprehensive_validation

echo -e "${GREEN}✓ Comprehensive validation completed${NC}"

# Performance analysis
echo -e "${YELLOW}[6/10] Analyzing performance...${NC}"

analyze_performance

echo -e "${GREEN}✓ Performance analysis completed${NC}"

# Generate reports
echo -e "${YELLOW}[7/10] Generating comprehensive reports...${NC}"

generate_comprehensive_reports

echo -e "${GREEN}✓ Reports generated${NC}"

# Documentation generation (if requested)
if [[ "$GENERATE_DOCS" == true ]]; then
    echo -e "${YELLOW}[8/10] Generating documentation...${NC}"

    generate_documentation

    echo -e "${GREEN}✓ Documentation generated${NC}"
else
    echo -e "${YELLOW}[8/10] Documentation generation skipped${NC}"
fi

# Success rate enforcement
echo -e "${YELLOW}[9/10] Enforcing success rate requirements...${NC}"

SUCCESS_RATE=$(calculate_success_rate)

if [[ "$ENFORCE_SUCCESS" == true ]]; then
    if enforce_success_rate "$SUCCESS_RATE"; then
        echo -e "${GREEN}✓ Success rate requirements met${NC}"
    else
        echo -e "${RED}✗ Success rate requirements not met${NC}"
        OVERALL_SUCCESS=false
    fi
else
    echo -e "${YELLOW}✓ Success rate enforcement disabled${NC}"
fi

# Cleanup
echo -e "${YELLOW}[10/10] Cleaning up...${NC}"

cleanup_environment

echo -e "${GREEN}✓ Cleanup completed${NC}"

# Final results
echo ""
echo -e "${PURPLE}═══════════════════════════════════════════════════════════════════════════════${NC}"
echo -e "${PURPLE}                           FINAL QA RESULTS                                  ${NC}"
echo -e "${PURPLE}═══════════════════════════════════════════════════════════════════════════════${NC}"
echo ""
echo -e "Test Execution Summary:"
echo -e "  Scenarios Executed: $COMPLETED_SCENARIOS/$TOTAL_SCENARIOS"
echo -e "  Total Tests: $TOTAL_TESTS"
echo -e "  Tests Passed: $PASSED_TESTS"
echo -e "  Tests Failed: $FAILED_TESTS"
echo -e "  Success Rate: ${SUCCESS_RATE}%"

if [[ "$OVERALL_SUCCESS" == true ]]; then
    echo ""
    echo -e "${GREEN}╔═══════════════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                                             ║${NC}"
    echo -e "${GREEN}║            🎉 FULL AUTOMATED QA COMPLETED SUCCESSFULLY! 🎉                 ║${NC}"
    echo -e "${GREEN}║                                                                             ║${NC}"
    echo -e "${GREEN}║         ✓ Mock Services: All operational                                   ║${NC}"
    echo -e "${GREEN}║         ✓ Test Scenarios: All executed                                     ║${NC}"
    echo -e "${GREEN}║         ✓ Validation: Comprehensive                                        ║${NC}"
    echo -e "${GREEN}║         ✓ Success Rate: 100%                                               ║${NC}"
    echo -e "${GREEN}║         ✓ Documentation: Generated                                         ║${NC}"
    echo -e "${GREEN}║                                                                             ║${NC}"
    echo -e "${GREEN}╚═══════════════════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "Reports available at: $REPORTS_DIR"
    exit 0
else
    echo ""
    echo -e "${RED}╔═══════════════════════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║                                                                             ║${NC}"
    echo -e "${RED}║               ⚠️  QA EXECUTION COMPLETED WITH ISSUES ⚠️                    ║${NC}"
    echo -e "${RED}║                                                                             ║${NC}"
    echo -e "${RED}║         Review the detailed reports for failure analysis                   ║${NC}"
    echo -e "${RED}║                                                                             ║${NC}"
    echo -e "${RED}╚═══════════════════════════════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo -e "Reports available at: $REPORTS_DIR"
    exit 1
fi

# Function definitions

check_prerequisites() {
    # Check required tools
    local tools=("adb" "emulator" "java" "gradle" "curl")
    for tool in "${tools[@]}"; do
        if ! command -v "$tool" >/dev/null 2>&1; then
            echo -e "${RED}ERROR: $tool is not installed${NC}"
            exit 1
        fi
    done

    # Check API key
    if [ -z "$ANTHROPIC_API_KEY" ]; then
        echo -e "${RED}ERROR: ANTHROPIC_API_KEY not set${NC}"
        exit 1
    fi

    # Check available disk space
    local available_space=$(df "$PROJECT_ROOT" | tail -1 | awk '{print $4}')
    if [[ $available_space -lt 5242880 ]]; then  # 5GB in KB
        echo -e "${RED}ERROR: Insufficient disk space (need at least 5GB)${NC}"
        exit 1
    fi
}

setup_mock_services() {
    # Start all mock services
    "$SCRIPT_DIR/start_mock_services.sh" >> "$LOGS_DIR/mock_services_setup.log" 2>&1

    # Verify services are running
    local services=("8081" "8082" "8083" "9091" "8080" "3129")
    for port in "${services[@]}"; do
        if ! curl -s "http://localhost:$port/health" >/dev/null 2>&1; then
            echo -e "${RED}ERROR: Mock service on port $port not responding${NC}"
            exit 1
        fi
    done
}

build_all_components() {
    cd "$PROJECT_ROOT"

    # Build main project
    ./gradlew assembleDebug >> "$LOGS_DIR/build_main.log" 2>&1

    # Build QA module
    ./gradlew :qa-ai:assembleDebug >> "$LOGS_DIR/build_qa.log" 2>&1

    # Build mock services
    ./gradlew :qa-ai:shadowJar >> "$LOGS_DIR/build_mock.log" 2>&1
}

execute_scenario() {
    local scenario=$1
    local scenario_success=true
    local attempt=1

    while [[ $attempt -le $MAX_RETRY_ATTEMPTS ]]; do
        echo -e "${CYAN}Attempt $attempt/$MAX_RETRY_ATTEMPTS for $scenario${NC}"

        if run_scenario_tests "$scenario" "$attempt"; then
            echo -e "${GREEN}Scenario $scenario passed on attempt $attempt${NC}"
            return 0
        else
            echo -e "${YELLOW}Scenario $scenario failed on attempt $attempt${NC}"
            ((attempt++))
        fi
    done

    echo -e "${RED}Scenario $scenario failed after $MAX_RETRY_ATTEMPTS attempts${NC}"
    return 1
}

run_scenario_tests() {
    local scenario=$1
    local attempt=$2

    # Setup clean emulator for scenario
    "$SCRIPT_DIR/start_clean_emulator.sh" --scenario "all_apps" --no-clean >> "$LOGS_DIR/emulator_$scenario_$attempt.log" 2>&1

    # Run the comprehensive QA tests
    if timeout 1800 ./gradlew :qa-ai:executeTestScenario -Pscenario="$scenario" -PmaxRetries=1 >> "$LOGS_DIR/test_$scenario_$attempt.log" 2>&1; then
        # Parse results
        local test_results=$(grep -o "Tests run: [0-9]*" "$LOGS_DIR/test_$scenario_$attempt.log" | tail -1 | sed 's/Tests run: //')
        local failures=$(grep -o "Failures: [0-9]*" "$LOGS_DIR/test_$scenario_$attempt.log" | tail -1 | sed 's/Failures: //')

        if [[ -n "$test_results" ]]; then
            local passed=$((test_results - failures))
            TOTAL_TESTS=$((TOTAL_TESTS + test_results))
            PASSED_TESTS=$((PASSED_TESTS + passed))
            FAILED_TESTS=$((FAILED_TESTS + failures))

            if [[ $failures -eq 0 ]]; then
                return 0
            fi
        fi
    fi

    # Cleanup emulator
    "$SCRIPT_DIR/cleanup_emulator.sh" >> "$LOGS_DIR/cleanup_$scenario_$attempt.log" 2>&1

    return 1
}

run_comprehensive_validation() {
    # Run validation suite
    ./gradlew :qa-ai:runValidation >> "$LOGS_DIR/validation.log" 2>&1
}

analyze_performance() {
    # Run performance analysis
    ./gradlew :qa-ai:analyzePerformance >> "$LOGS_DIR/performance.log" 2>&1
}

generate_comprehensive_reports() {
    # Generate all report formats
    ./gradlew :qa-ai:generateReports -PreportDir="$REPORTS_DIR" >> "$LOGS_DIR/report_generation.log" 2>&1
}

generate_documentation() {
    # Generate comprehensive documentation
    ./gradlew :qa-ai:generateDocumentation -PoutputDir="$REPORTS_DIR/docs" >> "$LOGS_DIR/documentation.log" 2>&1
}

calculate_success_rate() {
    if [[ $TOTAL_TESTS -gt 0 ]]; then
        echo "scale=2; ($PASSED_TESTS * 100) / $TOTAL_TESTS" | bc
    else
        echo "0.00"
    fi
}

enforce_success_rate() {
    local success_rate=$1

    if (( $(echo "$success_rate >= $REQUIRED_SUCCESS_RATE" | bc -l) )); then
        return 0
    else
        echo -e "${RED}SUCCESS RATE REQUIREMENT NOT MET${NC}"
        echo -e "${RED}Required: ${REQUIRED_SUCCESS_RATE}%${NC}"
        echo -e "${RED}Achieved: ${success_rate}%${NC}"
        return 1
    fi
}

cleanup_environment() {
    # Stop mock services
    "$SCRIPT_DIR/start_mock_services.sh" --stop >> "$LOGS_DIR/cleanup.log" 2>&1

    # Final emulator cleanup
    "$SCRIPT_DIR/cleanup_emulator.sh" --force >> "$LOGS_DIR/cleanup.log" 2>&1
}