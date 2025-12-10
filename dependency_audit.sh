#!/bin/bash

# Dependency Compatibility Audit Script

# Color Codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

echo -e "${YELLOW}===== Dependency Compatibility Audit =====${NC}"

# Critical Dependency Versions
declare -A DEPENDENCIES=(
    ["AndroidGradlePlugin"]="8.13.0"
    ["Kotlin"]="2.0.0"
    ["KSP"]="2.0.0-1.0.21"
    ["Gradle"]="8.14.3"
    ["Java"]="17"
    ["CompileSDK"]="36"
    ["TargetSDK"]="36"
    ["MinSDK"]="21"
)

# Dependency Version Check Function
check_dependency_version() {
    local dep_name="$1"
    local expected_version="$2"
    local actual_version=""

    case "$dep_name" in
        "AndroidGradlePlugin")
            actual_version=$(grep "com.android.tools.build:gradle" build.gradle | cut -d: -f3 | tr -d "'" | tr -d ' ')
            ;;
        "Kotlin")
            actual_version=$(grep "org.jetbrains.kotlin" build.gradle | grep -oP "version '\K[^']+")
            ;;
        "KSP")
            actual_version=$(grep "com.google.devtools.ksp" build.gradle | grep -oP "version '\K[^']+")
            ;;
        "Gradle")
            actual_version=$(gradle --version | grep "Gradle" | awk '{print $2}')
            ;;
        "Java")
            actual_version=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d. -f1)
            ;;
        *)
            echo -e "${YELLOW}Skipping version check for $dep_name${NC}"
            return 0
            ;;
    esac

    if [[ "$actual_version" == "$expected_version" ]]; then
        echo -e "${GREEN}✅ $dep_name: $actual_version (Matches expected version)${NC}"
        return 0
    else
        echo -e "${RED}❌ $dep_name: Actual $actual_version ≠ Expected $expected_version${NC}"
        return 1
    fi
}

# Audit Results Tracking
AUDIT_STATUS=0

# Check Each Dependency
for dep in "${!DEPENDENCIES[@]}"; do
    check_dependency_version "$dep" "${DEPENDENCIES[$dep]}"
    if [ $? -ne 0 ]; then
        AUDIT_STATUS=1
    fi
done

# Final Audit Report
if [ $AUDIT_STATUS -eq 0 ]; then
    echo -e "\n${GREEN}===== DEPENDENCY COMPATIBILITY AUDIT PASSED =====${NC}"
    exit 0
else
    echo -e "\n${RED}===== DEPENDENCY COMPATIBILITY AUDIT FAILED =====${NC}"
    exit 1
fi