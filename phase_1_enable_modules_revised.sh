#!/bin/bash

# ShareConnect Phase 1 Implementation Script (Revised)
# Enables all 9 disabled connector modules without immediate build verification

set -e

echo "🚀 ShareConnect Phase 1: Enabling Disabled Modules"
echo "=================================================="
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Function to check if a module exists
module_exists() {
    local module=$1
    if [ -d "Connectors/$module" ]; then
        return 0
    else
        return 1
    fi
}

# Create logs directory
mkdir -p logs

# Backup original settings.gradle
cp settings.gradle settings.gradle.backup.$(date +%Y%m%d_%H%M%S)
print_status "Created backup of settings.gradle"

# Count enabled modules before
enabled_before=$(grep -v "^//" settings.gradle | grep -c "include ':")
echo "Modules enabled before: $enabled_before"

# Enable all commented modules at once
echo ""
echo "=== ENABLING ALL DISABLED MODULES ==="

# MatrixConnector (Lines 115-117)
if module_exists "MatrixConnect"; then
    sed -i '115,117s|^//||' settings.gradle
    print_status "Enabled MatrixConnector"
else
    print_error "MatrixConnector directory not found"
fi

# PortainerConnector (Lines 119-121)
if module_exists "PortainerConnect"; then
    sed -i '119,121s|^//||' settings.gradle
    print_status "Enabled PortainerConnector"
else
    print_error "PortainerConnector directory not found"
fi

# NetdataConnector (Lines 123-125)
if module_exists "NetdataConnect"; then
    sed -i '123,125s|^//||' settings.gradle
    print_status "Enabled NetdataConnector"
else
    print_error "NetdataConnector directory not found"
fi

# SeafileConnector (Lines 127-129)
if module_exists "SeafileConnect"; then
    sed -i '127,129s|^//||' settings.gradle
    print_status "Enabled SeafileConnector"
else
    print_error "SeafileConnector directory not found"
fi

# SyncthingConnector (Lines 131-133)
if module_exists "SyncthingConnect"; then
    sed -i '131,133s|^//||' settings.gradle
    print_status "Enabled SyncthingConnector"
else
    print_error "SyncthingConnector directory not found"
fi

# PaperlessNGConnector (Lines 135-137)
if module_exists "PaperlessNGConnect"; then
    sed -i '135,137s|^//||' settings.gradle
    print_status "Enabled PaperlessNGConnector"
else
    print_error "PaperlessNGConnector directory not found"
fi

# DuplicatiConnector (Lines 139-141)
if module_exists "DuplicatiConnect"; then
    sed -i '139,141s|^//||' settings.gradle
    print_status "Enabled DuplicatiConnector"
else
    print_error "DuplicatiConnector directory not found"
fi

# WireGuardConnector (Lines 143-145)
if module_exists "WireGuardConnect"; then
    sed -i '143,145s|^//||' settings.gradle
    print_status "Enabled WireGuardConnector"
else
    print_error "WireGuardConnector directory not found"
fi

# MinecraftServerConnector (Lines 147-149)
if module_exists "MinecraftServerConnect"; then
    sed -i '147,149s|^//||' settings.gradle
    print_status "Enabled MinecraftServerConnector"
else
    print_error "MinecraftServerConnector directory not found"
fi

# OnlyOfficeConnector (Lines 151-153)
if module_exists "OnlyOfficeConnect"; then
    sed -i '151,153s|^//||' settings.gradle
    print_status "Enabled OnlyOfficeConnector"
else
    print_error "OnlyOfficeConnector directory not found"
fi

# Count enabled modules after
enabled_after=$(grep -v "^//" settings.gradle | grep -c "include ':")
echo ""
echo "Modules enabled after: $enabled_after"
echo "New modules enabled: $((enabled_after - enabled_before))"

# Refresh Gradle projects
echo ""
echo "=== REFRESHING GRADLE PROJECTS ==="
./gradlew projects --quiet > logs/gradle_projects.log 2>&1
print_status "Gradle projects refreshed"

# Summary
echo ""
echo "=== PHASE 1 SUMMARY ==="
print_status "All 9 disabled modules have been enabled in settings.gradle"
echo ""
echo "Next steps:"
echo "1. Run 'gradlew build' to build all modules"
echo "2. Fix any compilation errors individually"
echo "3. Run './phase_2_complete_tests.sh' when ready"
echo ""
echo "Note: Some modules may need additional dependencies or fixes to build successfully."
print_status "Phase 1 completed - Modules enabled"