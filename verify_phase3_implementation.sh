#!/bin/bash

# ShareConnect Phase 3 Implementation Verification Script
# Verifies all 8 Phase 3 applications are properly implemented

set -e

echo "=========================================="
echo "ShareConnect Phase 3 Verification"
echo "=========================================="
echo ""

# Color codes
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

PHASE3_APPS=(
    "SeafileConnect"
    "SyncthingConnect"
    "MatrixConnect"
    "PaperlessNGConnect"
    "DuplicatiConnect"
    "WireGuardConnect"
    "MinecraftServerConnect"
    "OnlyOfficeConnect"
)

total_checks=0
passed_checks=0
failed_checks=0

check_file() {
    local file="$1"
    local description="$2"
    
    total_checks=$((total_checks + 1))
    
    if [ -f "$file" ]; then
        echo -e "${GREEN}✓${NC} $description"
        passed_checks=$((passed_checks + 1))
        return 0
    else
        echo -e "${RED}✗${NC} $description (NOT FOUND)"
        failed_checks=$((failed_checks + 1))
        return 1
    fi
}

check_directory() {
    local dir="$1"
    local description="$2"
    
    total_checks=$((total_checks + 1))
    
    if [ -d "$dir" ]; then
        echo -e "${GREEN}✓${NC} $description"
        passed_checks=$((passed_checks + 1))
        return 0
    else
        echo -e "${RED}✗${NC} $description (NOT FOUND)"
        failed_checks=$((failed_checks + 1))
        return 1
    fi
}

# Verify each Phase 3 application
for app in "${PHASE3_APPS[@]}"; do
    echo ""
    echo "=== Checking $app ==="
    
    base_dir="/home/milosvasic/Projects/ShareConnect/Connectors/${app}/${app}Connector"
    
    # Check directory structure
    check_directory "$base_dir" "Base directory"
    check_directory "$base_dir/src/main/kotlin" "Main Kotlin source"
    check_directory "$base_dir/src/test/kotlin" "Test source"
    check_directory "$base_dir/src/androidTest/kotlin" "Android test source"
    check_directory "$base_dir/src/main/res" "Resources directory"
    
    # Check core files
    check_file "$base_dir/build.gradle" "build.gradle"
    check_file "$base_dir/proguard-rules.pro" "ProGuard rules"
    check_file "$base_dir/src/main/AndroidManifest.xml" "AndroidManifest.xml"
    
    # Check source files
    app_lower=$(echo "$app" | tr '[:upper:]' '[:lower:]')
    check_file "$base_dir/src/main/kotlin/com/shareconnect/${app_lower}/${app}Application.kt" "Application class"
    check_file "$base_dir/src/main/kotlin/com/shareconnect/${app_lower}/ui/MainActivity.kt" "MainActivity"
    
    # Check resource files
    check_file "$base_dir/src/main/res/values/strings.xml" "strings.xml"
    check_file "$base_dir/src/main/res/xml/network_security_config.xml" "network_security_config.xml"
    
    # Check documentation
    check_directory "/home/milosvasic/Projects/ShareConnect/Connectors/${app}/Documentation" "Documentation directory"
    check_file "/home/milosvasic/Projects/ShareConnect/Connectors/${app}/Documentation/${app}.md" "Technical documentation"
done

echo ""
echo "=== Checking settings.gradle Registration ==="
for app in "${PHASE3_APPS[@]}"; do
    connector="${app}Connector"
    if grep -q "include ':${connector}'" /home/milosvasic/Projects/ShareConnect/settings.gradle; then
        echo -e "${GREEN}✓${NC} $connector registered in settings.gradle"
        passed_checks=$((passed_checks + 1))
    else
        echo -e "${RED}✗${NC} $connector NOT registered in settings.gradle"
        failed_checks=$((failed_checks + 1))
    fi
    total_checks=$((total_checks + 1))
done

echo ""
echo "=========================================="
echo "Verification Summary"
echo "=========================================="
echo "Total Checks: $total_checks"
echo -e "${GREEN}Passed: $passed_checks${NC}"
if [ $failed_checks -gt 0 ]; then
    echo -e "${RED}Failed: $failed_checks${NC}"
else
    echo -e "${GREEN}Failed: $failed_checks${NC}"
fi

success_rate=$((passed_checks * 100 / total_checks))
echo "Success Rate: ${success_rate}%"
echo ""

if [ $failed_checks -eq 0 ]; then
    echo -e "${GREEN}✅ All Phase 3 applications verified successfully!${NC}"
    echo ""
    echo "Next steps:"
    echo "1. Run: ./gradlew build"
    echo "2. Run tests: ./gradlew test"
    echo "3. Deploy applications"
    exit 0
else
    echo -e "${RED}⚠️  Some checks failed. Please review the output above.${NC}"
    exit 1
fi
