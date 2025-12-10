#!/bin/bash

# Comprehensive System Validation Script

echo "===== ShareConnect System Validation ====="

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Validation Stages
STAGES=(
    "Unit Tests"
    "Integration Tests"
    "Performance Tests"
    "Security Vulnerability Scan"
    "Code Quality Check"
    "Module Compatibility"
)

# Function to run validation stage
run_validation_stage() {
    local stage="$1"
    local command="$2"
    
    echo -e "\n${YELLOW}==== Validating: $stage ====${NC}"
    
    # Execute the command
    eval "$command"
    
    # Check command exit status
    if [ $? -eq 0 ]; then
        echo -e "${GREEN}✅ $stage Validation Passed${NC}"
    else
        echo -e "${RED}❌ $stage Validation Failed${NC}"
        exit 1
    fi
}

# Run Validation Stages
for stage in "${STAGES[@]}"; do
    case "$stage" in
        "Unit Tests")
            run_validation_stage "$stage" "./gradlew test"
            ;;
        "Integration Tests")
            run_validation_stage "$stage" "./gradlew integrationTest"
            ;;
        "Performance Tests")
            run_validation_stage "$stage" "./gradlew performanceTest"
            ;;
        "Security Vulnerability Scan")
            run_validation_stage "$stage" "./run_snyk_scan.sh"
            ;;
        "Code Quality Check")
            run_validation_stage "$stage" "./gradlew detekt"
            ;;
        "Module Compatibility")
            run_validation_stage "$stage" "./verify_module_compatibility.sh"
            ;;
    esac
done

# Final Validation Report
echo -e "\n${GREEN}===== SYSTEM VALIDATION COMPLETE =====${NC}"
echo -e "${GREEN}All validation stages passed successfully!${NC}"

exit 0