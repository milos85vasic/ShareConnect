#!/bin/bash

# Test all Phase 3 ShareConnect applications
# Usage: ./test_phase3_apps.sh [unit|integration|all]

set -e

TEST_TYPE="${1:-all}"

echo "=========================================="
echo "Testing Phase 3 Applications"
echo "Test Type: $TEST_TYPE"
echo "=========================================="
echo ""

PHASE3_APPS=(
    "SeafileConnector"
    "SyncthingConnector"
    "MatrixConnector"
    "PaperlessNGConnector"
    "DuplicatiConnector"
    "WireGuardConnector"
    "MinecraftServerConnector"
    "OnlyOfficeConnector"
)

GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

success_count=0
failure_count=0
failed_apps=()
total_tests=0

run_unit_tests() {
    local app=$1
    echo "Running unit tests for ${app}..."
    
    if ./gradlew ":${app}:test" --quiet 2>&1 | tee /tmp/${app}_test.log; then
        # Count tests from output
        test_count=$(grep -c "Test.*PASSED" /tmp/${app}_test.log 2>/dev/null || echo "0")
        total_tests=$((total_tests + test_count))
        echo -e "${GREEN}✓${NC} ${app} unit tests passed"
        return 0
    else
        echo -e "${RED}✗${NC} ${app} unit tests failed"
        return 1
    fi
}

run_integration_tests() {
    local app=$1
    
    # Check if device is connected
    if ! adb devices | grep -q "device$"; then
        echo -e "${YELLOW}⚠${NC}  No device connected. Skipping integration tests for ${app}"
        return 0
    fi
    
    echo "Running integration tests for ${app}..."
    
    if ./gradlew ":${app}:connectedAndroidTest" --quiet 2>&1 | tee /tmp/${app}_integration.log; then
        test_count=$(grep -c "Test.*PASSED" /tmp/${app}_integration.log 2>/dev/null || echo "0")
        total_tests=$((total_tests + test_count))
        echo -e "${GREEN}✓${NC} ${app} integration tests passed"
        return 0
    else
        echo -e "${RED}✗${NC} ${app} integration tests failed"
        return 1
    fi
}

for app in "${PHASE3_APPS[@]}"; do
    app_failed=false
    
    if [ "$TEST_TYPE" = "unit" ] || [ "$TEST_TYPE" = "all" ]; then
        if ! run_unit_tests "$app"; then
            app_failed=true
        fi
    fi
    
    if [ "$TEST_TYPE" = "integration" ] || [ "$TEST_TYPE" = "all" ]; then
        if ! run_integration_tests "$app"; then
            app_failed=true
        fi
    fi
    
    if [ "$app_failed" = true ]; then
        failure_count=$((failure_count + 1))
        failed_apps+=("$app")
    else
        success_count=$((success_count + 1))
    fi
    
    echo ""
done

echo "=========================================="
echo "Test Summary"
echo "=========================================="
echo "Total Tests Run: $total_tests"
echo -e "${GREEN}Apps Passed: ${success_count}${NC}"
if [ $failure_count -gt 0 ]; then
    echo -e "${RED}Apps Failed: ${failure_count}${NC}"
    echo ""
    echo "Failed applications:"
    for app in "${failed_apps[@]}"; do
        echo -e "  ${RED}✗${NC} $app"
    done
else
    echo -e "${GREEN}Apps Failed: 0${NC}"
fi

echo ""
echo "Test reports: Connectors/*/build/reports/tests/"

if [ $failure_count -eq 0 ]; then
    echo -e "${GREEN}✅ All Phase 3 tests passed!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some tests failed. Check reports for details.${NC}"
    exit 1
fi
