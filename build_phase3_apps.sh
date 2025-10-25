#!/bin/bash

# Build all Phase 3 ShareConnect applications
# Usage: ./build_phase3_apps.sh [debug|release]

set -e

BUILD_TYPE="${1:-debug}"
BUILD_TYPE_CAPITALIZED="$(echo ${BUILD_TYPE:0:1} | tr '[:lower:]' '[:upper:]')${BUILD_TYPE:1}"

echo "=========================================="
echo "Building Phase 3 Applications"
echo "Build Type: $BUILD_TYPE_CAPITALIZED"
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

for app in "${PHASE3_APPS[@]}"; do
    echo "Building ${app}..."
    
    if ./gradlew ":${app}:assemble${BUILD_TYPE_CAPITALIZED}" --quiet; then
        echo -e "${GREEN}✓${NC} ${app} built successfully"
        success_count=$((success_count + 1))
    else
        echo -e "${RED}✗${NC} ${app} build failed"
        failure_count=$((failure_count + 1))
        failed_apps+=("$app")
    fi
    echo ""
done

echo "=========================================="
echo "Build Summary"
echo "=========================================="
echo -e "${GREEN}Successful: ${success_count}${NC}"
if [ $failure_count -gt 0 ]; then
    echo -e "${RED}Failed: ${failure_count}${NC}"
    echo ""
    echo "Failed applications:"
    for app in "${failed_apps[@]}"; do
        echo -e "  ${RED}✗${NC} $app"
    done
else
    echo -e "${GREEN}Failed: 0${NC}"
fi

echo ""
echo "APKs location: Connectors/*/build/outputs/apk/${BUILD_TYPE}/"

if [ $failure_count -eq 0 ]; then
    echo -e "${GREEN}✅ All Phase 3 applications built successfully!${NC}"
    exit 0
else
    echo -e "${RED}⚠️  Some builds failed. Check output above.${NC}"
    exit 1
fi
