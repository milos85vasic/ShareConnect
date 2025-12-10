#!/bin/bash

# Module Compatibility Verification Script

echo "===== Module Compatibility Verification ====="

# Define modules to check
MODULES=(
    "MatrixConnector"
    "ShareConnector"
    "SecurityAccess"
    "ProfileSync"
    "HistorySync"
)

# Compatibility check function
check_module_compatibility() {
    local module="$1"
    
    echo "Checking compatibility for module: $module"
    
    # Attempt to build the module
    ./gradlew ":$module:build"
    
    if [ $? -eq 0 ]; then
        echo -e "\e[32m✅ $module Build Successful\e[0m"
        
        # Run module-specific tests
        ./gradlew ":$module:test"
        
        if [ $? -eq 0 ]; then
            echo -e "\e[32m✅ $module Tests Passed\e[0m"
            return 0
        else
            echo -e "\e[31m❌ $module Tests Failed\e[0m"
            return 1
        fi
    else
        echo -e "\e[31m❌ $module Build Failed\e[0m"
        return 1
    fi
}

# Track overall compatibility status
COMPATIBILITY_STATUS=0

# Check each module
for module in "${MODULES[@]}"; do
    check_module_compatibility "$module"
    
    # Update overall status if any module fails
    if [ $? -ne 0 ]; then
        COMPATIBILITY_STATUS=1
    fi
done

# Final compatibility report
if [ $COMPATIBILITY_STATUS -eq 0 ]; then
    echo -e "\n\e[32m===== ALL MODULES COMPATIBLE ====\e[0m"
    exit 0
else
    echo -e "\n\e[31m===== MODULE COMPATIBILITY ISSUES DETECTED ====\e[0m"
    exit 1
fi