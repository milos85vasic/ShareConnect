#!/bin/bash

# ShareConnect Phase 1 Implementation Script
# Enables the 9 disabled connector modules

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

# Function to uncomment module in settings.gradle
enable_module() {
    local module_name=$1
    local start_line=$2
    local end_line=$3
    local module_dir=$4
    
    print_status "Enabling $module_name..."
    
    # Uncomment the lines in settings.gradle
    sed -i "${start_line},${end_line}s|^//||" settings.gradle
    
    # Try to build the module
    echo "Building $module_name..."
    if ./gradlew :$module_name:assembleDebug > "logs/${module_name}_build.log" 2>&1; then
        print_status "$module_name builds successfully"
    else
        print_error "$module_name build failed. Check logs/${module_name}_build.log"
        # Re-comment the lines
        sed -i "${start_line},${end_line}s|^|//|" settings.gradle
        return 1
    fi
    
    # Run unit tests
    echo "Running unit tests for $module_name..."
    if ./gradlew :$module_name:test > "logs/${module_name}_unit_test.log" 2>&1; then
        print_status "$module_name unit tests pass"
    else
        print_warning "$module_name unit tests have issues. Check logs/${module_name}_unit_test.log"
    fi
    
    # Run instrumentation tests if they exist
    if [ -f "Connectors/${module_dir}/${module_name}/src/androidTest" ]; then
        echo "Running instrumentation tests for $module_name..."
        if ./gradlew :$module_name:connectedAndroidTest > "logs/${module_name}_instrumentation_test.log" 2>&1; then
            print_status "$module_name instrumentation tests pass"
        else
            print_warning "$module_name instrumentation tests have issues. Check logs/${module_name}_instrumentation_test.log"
        fi
    fi
    
    return 0
}

# Create logs directory
mkdir -p logs

# Backup original settings.gradle
cp settings.gradle settings.gradle.backup.$(date +%Y%m%d_%H%M%S)
print_status "Created backup of settings.gradle"

# Week 1: Enable MatrixConnector, PortainerConnector, NetdataConnector
echo ""
echo "=== WEEK 1 MODULES ==="

# MatrixConnector (Lines 115-116)
if module_exists "MatrixConnect"; then
    enable_module "MatrixConnector" 115 116 "MatrixConnect/MatrixConnector"
else
    print_error "MatrixConnector directory not found"
fi

# PortainerConnector (Lines 100-101)
if module_exists "PortainerConnect"; then
    enable_module "PortainerConnector" 100 101 "PortainerConnect/PortainerConnector"
else
    print_error "PortainerConnector directory not found"
fi

# NetdataConnector (Lines 103-104)
if module_exists "NetdataConnect"; then
    enable_module "NetdataConnector" 103 104 "NetdataConnect/NetdataConnector"
else
    print_error "NetdataConnector directory not found"
fi

echo ""
echo "=== WEEK 1 COMPLETE ==="
echo "MatrixConnector, PortainerConnector, and NetdataConnector enabled"
echo ""

# Week 2: Enable HomeAssistantConnector, SyncthingConnector, PaperlessNGConnector
echo "=== WEEK 2 MODULES ==="

# HomeAssistantConnector (Lines 106-107)
if module_exists "HomeAssistantConnect"; then
    enable_module "HomeAssistantConnector" 106 107 "HomeAssistantConnect/HomeAssistantConnector"
else
    print_error "HomeAssistantConnector directory not found"
fi

# SyncthingConnector (Lines 112-113)
if module_exists "SyncthingConnect"; then
    enable_module "SyncthingConnector" 112 113 "SyncthingConnect/SyncthingConnector"
else
    print_error "SyncthingConnector directory not found"
fi

# PaperlessNGConnector (Lines 118-119)
if module_exists "PaperlessNGConnect"; then
    enable_module "PaperlessNGConnector" 118 119 "PaperlessNGConnect/PaperlessNGConnector"
else
    print_error "PaperlessNGConnector directory not found"
fi

echo ""
echo "=== WEEK 2 COMPLETE ==="
echo "HomeAssistantConnector, SyncthingConnector, and PaperlessNGConnector enabled"
echo ""

# Week 3: Enable WireGuardConnector, MinecraftServerConnector, OnlyOfficeConnector
echo "=== WEEK 3 MODULES ==="

# WireGuardConnector (Lines 124-125)
if module_exists "WireGuardConnect"; then
    enable_module "WireGuardConnector" 124 125 "WireGuardConnect/WireGuardConnector"
else
    print_error "WireGuardConnector directory not found"
fi

# MinecraftServerConnector (Lines 127-128)
if module_exists "MinecraftServerConnect"; then
    enable_module "MinecraftServerConnector" 127 128 "MinecraftServerConnect/MinecraftServerConnector"
else
    print_error "MinecraftServerConnector directory not found"
fi

# OnlyOfficeConnector (Lines 130-131)
if module_exists "OnlyOfficeConnect"; then
    enable_module "OnlyOfficeConnector" 130 131 "OnlyOfficeConnect/OnlyOfficeConnector"
else
    print_error "OnlyOfficeConnector directory not found"
fi

echo ""
echo "=== WEEK 3 COMPLETE ==="
echo "WireGuardConnector, MinecraftServerConnector, and OnlyOfficeConnector enabled"
echo ""

# Final verification
echo "=== FINAL VERIFICATION ==="
echo "Building all modules to verify no conflicts..."

if ./gradlew assembleDebug > logs/final_build.log 2>&1; then
    print_status "All modules build successfully together"
else
    print_error "Build conflicts detected. Check logs/final_build.log"
    exit 1
fi

# Run unit tests for all modules
echo "Running unit tests for all modules..."
if ./gradlew test > logs/final_unit_tests.log 2>&1; then
    print_status "All unit tests pass"
else
    print_warning "Some unit tests have issues. Check logs/final_unit_tests.log"
fi

echo ""
print_status "Phase 1 Implementation Complete!"
echo "All 9 disabled modules have been enabled."
echo ""
echo "Next steps:"
echo "1. Review build logs in the logs/ directory"
echo "2. Run Phase 2 test completion script"
echo "3. Update documentation for enabled modules"
echo ""
echo "To revert changes:"
echo "cp settings.gradle.backup.$(ls settings.gradle.backup.* | tail -1) settings.gradle"
echo ""
echo "To continue with Phase 2:"
echo "./phase_2_complete_tests.sh"