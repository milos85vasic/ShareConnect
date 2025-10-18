#!/bin/bash

# SonarQube Test Integration Script
# Non-blocking SonarQube testing for ShareConnect

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

SONARQUBE_URL="http://localhost:9001"

# Function to check if SonarQube is ready
check_sonarqube_ready() {
    if curl -s -f "$SONARQUBE_URL/api/system/status" | grep -q '"status":"UP"'; then
        return 0
    else
        return 1
    fi
}

# Function to wait for SonarQube with progress
wait_for_sonarqube_with_progress() {
    local max_attempts=60
    local attempt=1

    log_info "Waiting for SonarQube to be ready..."

    while [ $attempt -le $max_attempts ]; do
        if check_sonarqube_ready; then
            log_success "SonarQube is ready!"
            return 0
        fi

        echo -n "."
        sleep 5
        ((attempt++))
    done

    echo "" # New line after dots
    log_error "SonarQube failed to become ready within $(($max_attempts * 5)) seconds"
    return 1
}

log_info "ShareConnect SonarQube Test Integration"
log_info "====================================="

# Check command line arguments
if [ "$1" = "--async" ]; then
    log_info "Starting SonarQube containers asynchronously..."
    "$SCRIPT_DIR/run_sonarqube_scan.sh" --start
    log_info "Containers started. SonarQube will be available at $SONARQUBE_URL"
    log_info "Use this script without --async to run the actual scan when ready."
    exit 0
fi

# Check if containers are running
log_info "Checking SonarQube container status..."
if ! "$SCRIPT_DIR/run_sonarqube_scan.sh" --status >/dev/null 2>&1; then
    log_warning "SonarQube containers not running. Starting them..."
    "$SCRIPT_DIR/run_sonarqube_scan.sh" --start

    log_info "Waiting for SonarQube to initialize (this may take 2-3 minutes)..."
    if ! wait_for_sonarqube_with_progress; then
        log_error "Failed to start SonarQube. Check Docker and try again."
        exit 1
    fi
else
    log_info "SonarQube containers are running"
    if check_sonarqube_ready; then
        log_success "SonarQube is ready"
    else
        log_warning "SonarQube containers running but API not ready. Waiting..."
        if ! wait_for_sonarqube_with_progress; then
            exit 1
        fi
    fi
fi

# Run the actual scan
log_info "Running SonarQube code quality scan..."
if "$SCRIPT_DIR/run_sonarqube_scan.sh" --scan; then
    log_success "SonarQube scan completed successfully"
    exit 0
else
    log_error "SonarQube scan failed"
    exit 1
fi