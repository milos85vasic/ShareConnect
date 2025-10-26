#!/bin/bash

# ShareConnect Emulator Manager
# A polished, non-blocking emulator management script for testing
# Supports multiple emulators, proper cleanup, and integration with test scripts

set -e

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration - can be overridden by environment variables
EMULATOR_NAME="${EMULATOR_NAME:-ShareConnect_Test_Emulator}"
ANDROID_API_LEVEL="${ANDROID_API_LEVEL:-34}"
DEVICE_MODEL="${DEVICE_MODEL:-pixel_6}"
EMULATOR_PORT="${EMULATOR_PORT:-5554}"
TIMEOUT="${EMULATOR_TIMEOUT:-180}"  # 3 minutes default
HEADLESS="${HEADLESS:-true}"
GPU_MODE="${GPU_MODE:-swiftshader_indirect}"

# State file for tracking emulator instances
STATE_FILE="/tmp/shareconnect_emulator_${EMULATOR_PORT}.state"
PID_FILE="/tmp/shareconnect_emulator_${EMULATOR_PORT}.pid"

# Function to log messages
log() {
    echo -e "${BLUE}[$(date +'%H:%M:%S')]${NC} $1"
}

# Function to log errors
error() {
    echo -e "${RED}[$(date +'%H:%M:%S')] ERROR:${NC} $1" >&2
}

# Function to log warnings
warning() {
    echo -e "${YELLOW}[$(date +'%H:%M:%S')] WARNING:${NC} $1"
}

# Function to check if command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

# Function to check prerequisites
check_prerequisites() {
    log "Checking prerequisites..."

    local missing_tools=()

    for tool in adb emulator avdmanager sdkmanager; do
        if ! command_exists "$tool"; then
            missing_tools+=("$tool")
        fi
    done

    if [ ${#missing_tools[@]} -gt 0 ]; then
        error "Missing required tools: ${missing_tools[*]}"
        error "Please install Android SDK command line tools and platform tools"
        return 1
    fi

    log "✓ All prerequisites found"
    return 0
}

# Function to check if emulator is running
is_emulator_running() {
    # Check if we have a recorded PID and it's still running
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if kill -0 "$pid" 2>/dev/null; then
            return 0
        fi
    fi

    # Check if emulator process is running
    if pgrep -f "emulator.*$EMULATOR_NAME" >/dev/null 2>&1; then
        return 0
    fi

    # Check ADB devices
    if adb devices 2>/dev/null | grep -q "emulator-$EMULATOR_PORT.*device"; then
        return 0
    fi

    return 1
}

# Function to wait for emulator to be ready (non-blocking)
wait_for_emulator_ready() {
    local max_attempts=${1:-30}  # 30 attempts = ~5 minutes
    local attempt=1

    log "Waiting for emulator to be ready (max ${max_attempts} attempts)..."

    while [ $attempt -le $max_attempts ]; do
        if adb devices 2>/dev/null | grep -q "emulator-$EMULATOR_PORT.*device"; then
            # Check if boot is completed
            if adb -s "emulator-$EMULATOR_PORT" shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
                log "✓ Emulator is ready!"
                return 0
            fi
        fi

        log "Attempt $attempt/$max_attempts: Emulator not ready yet..."
        sleep 10
        attempt=$((attempt + 1))
    done

    error "Emulator failed to become ready within timeout"
    return 1
}

# Function to start emulator
start_emulator() {
    log "Starting emulator: $EMULATOR_NAME"

    # Clean up any existing state
    cleanup_emulator_state

    # Build emulator arguments
    local emulator_args="-avd $EMULATOR_NAME -port $EMULATOR_PORT -gpu $GPU_MODE"

    if [ "$HEADLESS" = true ]; then
        emulator_args="$emulator_args -no-window -no-audio"
    fi

    # Start emulator in background
    log "Launching emulator with args: $emulator_args"
    emulator $emulator_args &
    local emulator_pid=$!

    # Save PID
    echo $emulator_pid > "$PID_FILE"

    # Save state
    echo "STARTED:$(date +%s)" > "$STATE_FILE"

    log "Emulator started with PID: $emulator_pid"

    # Wait a moment for emulator to initialize
    sleep 5

    # Check if process is still running
    if ! kill -0 $emulator_pid 2>/dev/null; then
        error "Emulator process died immediately after starting"
        cleanup_emulator_state
        return 1
    fi

    return 0
}

# Function to create AVD if needed
create_avd_if_needed() {
    log "Checking if AVD exists: $EMULATOR_NAME"

    if avdmanager list avd | grep -q "$EMULATOR_NAME"; then
        log "✓ AVD '$EMULATOR_NAME' already exists"
        return 0
    fi

    log "Creating AVD: $EMULATOR_NAME"

    # Detect available system images (try multiple variants)
    local arch="x86_64"
    local variants=("google_apis_playstore" "google_apis" "default")
    local system_image=""

    for variant in "${variants[@]}"; do
        local test_image="system-images;android-$ANDROID_API_LEVEL;$variant;$arch"
        if sdkmanager --list_installed 2>/dev/null | grep -q "$test_image"; then
            system_image="$test_image"
            break
        fi
    done

    # If no x86_64 found, try x86
    if [ -z "$system_image" ]; then
        arch="x86"
        for variant in "${variants[@]}"; do
            local test_image="system-images;android-$ANDROID_API_LEVEL;$variant;$arch"
            if sdkmanager --list_installed 2>/dev/null | grep -q "$test_image"; then
                system_image="$test_image"
                break
            fi
        done
    fi

    if [ -z "$system_image" ]; then
        error "No compatible system image found for API $ANDROID_API_LEVEL"
        return 1
    fi

    log "Using system image: $system_image"

    # Create AVD
    echo "no" | avdmanager create avd \
        --name "$EMULATOR_NAME" \
        --package "$system_image" \
        --device "$DEVICE_MODEL" \
        --force

    if [ $? -eq 0 ]; then
        log "✓ AVD created successfully"
        return 0
    else
        error "Failed to create AVD"
        return 1
    fi
}

# Function to setup emulator for testing
setup_emulator_for_testing() {
    log "Setting up emulator for testing..."

    # Disable animations
    adb -s "emulator-$EMULATOR_PORT" shell settings put global window_animation_scale 0.0 2>/dev/null || true
    adb -s "emulator-$EMULATOR_PORT" shell settings put global transition_animation_scale 0.0 2>/dev/null || true
    adb -s "emulator-$EMULATOR_PORT" shell settings put global animator_duration_scale 0.0 2>/dev/null || true

    # Disable notifications
    adb -s "emulator-$EMULATOR_PORT" shell settings put global heads_up_notifications_enabled 0 2>/dev/null || true

    # Set screen timeout to maximum
    adb -s "emulator-$EMULATOR_PORT" shell settings put system screen_off_timeout 2147483647 2>/dev/null || true

    # Wake up and unlock screen
    adb -s "emulator-$EMULATOR_PORT" shell input keyevent 26 2>/dev/null || true  # Power
    adb -s "emulator-$EMULATOR_PORT" shell input keyevent 82 2>/dev/null || true  # Unlock

    log "✓ Emulator setup completed"
}

# Function to cleanup emulator state files
cleanup_emulator_state() {
    rm -f "$STATE_FILE" "$PID_FILE"
}

# Function to stop emulator
stop_emulator() {
    log "Stopping emulator: $EMULATOR_NAME"

    # Kill by PID if we have it
    if [ -f "$PID_FILE" ]; then
        local pid=$(cat "$PID_FILE")
        if kill -0 "$pid" 2>/dev/null; then
            kill "$pid" 2>/dev/null || true
            sleep 2
            if kill -0 "$pid" 2>/dev/null; then
                kill -9 "$pid" 2>/dev/null || true
            fi
        fi
    fi

    # Kill any remaining processes
    pkill -f "emulator.*$EMULATOR_NAME" 2>/dev/null || true
    pkill -f "qemu.*$EMULATOR_NAME" 2>/dev/null || true

    # Disconnect ADB
    adb disconnect "emulator-$EMULATOR_PORT" 2>/dev/null || true

    # Clean up state
    cleanup_emulator_state

    log "✓ Emulator stopped"
}

# Function to get emulator status
get_emulator_status() {
    if is_emulator_running; then
        echo "RUNNING"
    else
        echo "STOPPED"
    fi
}

# Function to ensure emulator is ready
ensure_emulator_ready() {
    if is_emulator_running; then
        log "Emulator is already running"
        if wait_for_emulator_ready 10; then
            setup_emulator_for_testing
            return 0
        else
            warning "Existing emulator not responding, restarting..."
            stop_emulator
        fi
    fi

    # Create AVD if needed
    create_avd_if_needed || return 1

    # Start emulator
    start_emulator || return 1

    # Wait for it to be ready
    wait_for_emulator_ready || return 1

    # Setup for testing
    setup_emulator_for_testing || return 1

    log "✓ Emulator is ready for testing"
    return 0
}

# Main command handling
case "${1:-status}" in
    "start")
        check_prerequisites || exit 1
        ensure_emulator_ready || exit 1
        echo "ANDROID_SERIAL=emulator-$EMULATOR_PORT"
        echo "EMULATOR_PID=$(cat "$PID_FILE" 2>/dev/null || echo "")"
        ;;
    "stop")
        stop_emulator
        ;;
    "status")
        echo "$(get_emulator_status)"
        ;;
    "cleanup")
        stop_emulator
        # Optionally remove AVD
        if [ "$2" = "--remove-avd" ]; then
            log "Removing AVD: $EMULATOR_NAME"
            avdmanager delete avd -n "$EMULATOR_NAME" 2>/dev/null || true
        fi
        ;;
    "info")
        echo "Emulator Name: $EMULATOR_NAME"
        echo "Port: $EMULATOR_PORT"
        echo "API Level: $ANDROID_API_LEVEL"
        echo "Device: $DEVICE_MODEL"
        echo "Status: $(get_emulator_status)"
        if [ -f "$PID_FILE" ]; then
            echo "PID: $(cat "$PID_FILE")"
        fi
        ;;
    "setup")
        check_prerequisites || exit 1
        create_avd_if_needed || exit 1
        log "✓ AVD setup completed"
        ;;
    *)
        echo "Usage: $0 [COMMAND]"
        echo ""
        echo "Commands:"
        echo "  start     Start emulator and ensure it's ready"
        echo "  stop      Stop emulator"
        echo "  status    Show emulator status"
        echo "  cleanup   Stop emulator and cleanup"
        echo "  cleanup --remove-avd  Stop emulator and remove AVD"
        echo "  info      Show emulator information"
        echo "  setup     Create AVD without starting emulator"
        echo ""
        echo "Environment variables:"
        echo "  EMULATOR_NAME       Emulator name (default: ShareConnect_Test_Emulator)"
        echo "  EMULATOR_PORT       Emulator port (default: 5554)"
        echo "  ANDROID_API_LEVEL   Android API level (default: 34)"
        echo "  DEVICE_MODEL        Device model (default: pixel_6)"
        echo "  HEADLESS           Run headless (default: true)"
        echo "  GPU_MODE           GPU mode (default: swiftshader_indirect)"
        echo "  EMULATOR_TIMEOUT   Timeout in seconds (default: 180)"
        exit 1
        ;;
esac