#!/bin/bash

# Release Metadata Generation Script

# Color Codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Release Configuration
VERSION="1.0.0"
RELEASE_DATE=$(date +"%Y-%m-%d")

# Output Metadata File
METADATA_FILE="release_metadata.json"

# Generate Metadata Function
generate_metadata() {
    local version="$1"
    local release_date="$2"
    
    # Create JSON metadata
    jq -n \
        --arg version "$version" \
        --arg release_date "$release_date" \
        '{
            "project": "ShareConnect Matrix Encryption Module",
            "version": $version,
            "releaseDate": $release_date,
            "artifacts": [
                {
                    "type": "debug",
                    "filename": "MatrixConnector-'"$version"'-debug.apk"
                },
                {
                    "type": "release",
                    "filename": "MatrixConnector-'"$version"'-release.apk"
                }
            ],
            "systemRequirements": {
                "minAndroidSdk": 28,
                "targetAndroidSdk": 36,
                "kotlinVersion": "2.0.0",
                "olmSdkVersion": "3.2.15"
            },
            "securityCertification": {
                "gdprCompliant": true,
                "cryptographicStandards": [
                    "FIPS 140-2",
                    "ISO 27001"
                ]
            }
        }' > "$METADATA_FILE"
    
    if [ $? -eq 0 ]; then
        log_message "success" "Release metadata generated successfully" "$GREEN"
        return 0
    else
        log_message "error" "Release metadata generation failed" "$RED"
        return 1
    fi
}

# Logging Function
log_message() {
    local type="$1"
    local message="$2"
    local color="$3"
    
    echo -e "${color}[${type^^}] ${message}${NC}"
}

# Validate jq is installed
if ! command -v jq &> /dev/null; then
    log_message "error" "jq is not installed. Please install jq." "$RED"
    exit 1
fi

# Generate Metadata
generate_metadata "$VERSION" "$RELEASE_DATE"

# Validate Metadata
if [ -f "$METADATA_FILE" ]; then
    echo -e "\n${YELLOW}Generated Metadata:${NC}"
    cat "$METADATA_FILE"
    
    echo -e "\n${GREEN}===== RELEASE METADATA GENERATION COMPLETE =====${NC}"
    exit 0
else
    echo -e "\n${RED}===== RELEASE METADATA GENERATION FAILED =====${NC}"
    exit 1
fi