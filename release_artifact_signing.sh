#!/bin/bash

# Release Artifact Signing Script

# Color Codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m'

# Signing Configuration
KEYSTORE_PATH="./signing/release.keystore"
KEYSTORE_ALIAS="shareconnect"
ARTIFACTS_DIR="./build/outputs/artifacts"

# Logging Function
log_message() {
    local type="$1"
    local message="$2"
    local color="$3"
    
    echo -e "${color}[${type^^}] ${message}${NC}"
}

# Verify Keystore Exists
if [ ! -f "$KEYSTORE_PATH" ]; then
    log_message "error" "Keystore not found at $KEYSTORE_PATH" "$RED"
    exit 1
fi

# Sign Artifacts Function
sign_artifact() {
    local artifact="$1"
    local signed_artifact="${artifact%.apk}-signed.apk"
    
    log_message "info" "Signing: $(basename "$artifact")" "$YELLOW"
    
    # Sign APK
    jarsigner -verbose -sigalg SHA256withRSA -digestalg SHA-256 \
        -keystore "$KEYSTORE_PATH" \
        -storepass "$KEYSTORE_PASSWORD" \
        "$artifact" "$KEYSTORE_ALIAS"
    
    if [ $? -eq 0 ]; then
        # Verify signature
        jarsigner -verify -verbose -certs "$artifact"
        
        if [ $? -eq 0 ]; then
            log_message "success" "Artifact signed successfully: $(basename "$artifact")" "$GREEN"
            return 0
        else
            log_message "error" "Signature verification failed for $(basename "$artifact")" "$RED"
            return 1
        fi
    else
        log_message "error" "Signing failed for $(basename "$artifact")" "$RED"
        return 1
    fi
}

# Validate Environment
if [ -z "$KEYSTORE_PASSWORD" ]; then
    log_message "error" "KEYSTORE_PASSWORD environment variable not set" "$RED"
    exit 1
fi

# Find and Sign Artifacts
SIGNING_STATUS=0
find "$ARTIFACTS_DIR" -name "*.apk" | while read -r artifact; do
    sign_artifact "$artifact"
    
    if [ $? -ne 0 ]; then
        SIGNING_STATUS=1
    fi
done

# Final Status
if [ $SIGNING_STATUS -eq 0 ]; then
    echo -e "\n${GREEN}===== ARTIFACT SIGNING COMPLETE =====${NC}"
    exit 0
else
    echo -e "\n${RED}===== ARTIFACT SIGNING FAILED =====${NC}"
    exit 1
fi