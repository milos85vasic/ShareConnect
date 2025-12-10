#!/bin/bash

# Final Release Build Generation Script

# Color Codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Build Configuration
VERSION="1.0.0"
BUILD_TYPES=("debug" "release")
OUTPUT_DIR="./build/outputs/artifacts"

# Prepare Output Directory
mkdir -p "$OUTPUT_DIR"

# Logging Function
log_message() {
    local type="$1"
    local message="$2"
    local color="$3"
    
    echo -e "${color}[${type^^}] ${message}${NC}"
}

# Build Function
generate_build() {
    local build_type="$1"
    
    log_message "info" "Generating ${build_type} build" "$YELLOW"
    
    # Clean previous builds
    ./gradlew clean

    # Build specific type
    if [ "$build_type" == "release" ]; then
        ./gradlew assembleRelease
    else
        ./gradlew assembleDebug
    fi

    # Check build status
    if [ $? -eq 0 ]; then
        log_message "success" "${build_type^} build generated successfully" "$GREEN"
        
        # Copy artifacts
        find . -name "*.apk" -type f | while read -r apk; do
            cp "$apk" "$OUTPUT_DIR/MatrixConnector-${VERSION}-${build_type}.apk"
        done
        
        return 0
    else
        log_message "error" "${build_type^} build failed" "$RED"
        return 1
    fi
}

# Overall Build Status
BUILD_STATUS=0

# Generate Builds
for build_type in "${BUILD_TYPES[@]}"; do
    generate_build "$build_type"
    
    if [ $? -ne 0 ]; then
        BUILD_STATUS=1
    fi
done

# Generate Build Report
echo -e "\n${YELLOW}===== Build Generation Report =====${NC}"
ls -l "$OUTPUT_DIR"

# Final Status
if [ $BUILD_STATUS -eq 0 ]; then
    echo -e "\n${GREEN}===== BUILD GENERATION COMPLETE =====${NC}"
    exit 0
else
    echo -e "\n${RED}===== BUILD GENERATION FAILED =====${NC}"
    exit 1
fi