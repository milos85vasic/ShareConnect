#!/bin/bash

# Script to run Android emulator for testing purposes
# This script creates and starts an Android emulator if none is connected

set -e

# Configuration
EMULATOR_NAME="test_emulator"
ANDROID_API_LEVEL=34
ANDROID_ABI="x86_64"
EMULATOR_PACKAGE="system-images;android-$ANDROID_API_LEVEL;google_apis;$ANDROID_ABI"

echo "=== Android Emulator Test Runner ==="

# Check if adb is available
if ! command -v adb &> /dev/null; then
    echo "Error: adb command not found. Please install Android SDK platform tools."
    exit 1
fi

# Check if emulator is available
if ! command -v emulator &> /dev/null; then
    echo "Error: emulator command not found. Please install Android SDK emulator."
    exit 1
fi

# Check if avdmanager is available
if ! command -v avdmanager &> /dev/null; then
    echo "Error: avdmanager command not found. Please install Android SDK tools."
    exit 1
fi

# Check if sdkmanager is available
if ! command -v sdkmanager &> /dev/null; then
    echo "Error: sdkmanager command not found. Please install Android SDK command line tools."
    exit 1
fi

# Function to check if emulator is running
is_emulator_running() {
    adb devices | grep -q "emulator-"
}

# Function to wait for device to be ready
wait_for_device() {
    echo "Waiting for emulator to be ready..."
    timeout=300
    counter=0

    while [ $counter -lt $timeout ]; do
        if adb shell getprop sys.boot_completed 2>/dev/null | grep -q "1"; then
            echo "Emulator is ready!"
            return 0
        fi
        sleep 2
        counter=$((counter + 2))
        echo "Still waiting... ($counter/$timeout seconds)"
    done

    echo "Timeout waiting for emulator to be ready"
    return 1
}

# Check if any device is already connected
if adb devices | grep -q "device$"; then
    echo "Android device/emulator already connected. Skipping emulator setup."
    adb devices
else
    echo "No Android device connected. Setting up emulator..."

    # Check if AVD already exists
    if avdmanager list avd | grep -q "$EMULATOR_NAME"; then
        echo "AVD '$EMULATOR_NAME' already exists."
    else
        echo "Creating AVD '$EMULATOR_NAME'..."

        # Check if system image is installed
        if ! sdkmanager --list_installed | grep -q "$EMULATOR_PACKAGE"; then
            echo "Installing system image..."
            echo "y" | sdkmanager "$EMULATOR_PACKAGE"
        fi

        # Create AVD
        echo "no" | avdmanager create avd \
            --name "$EMULATOR_NAME" \
            --package "$EMULATOR_PACKAGE" \
            --device "pixel" \
            --force
    fi

    # Start emulator
    echo "Starting emulator '$EMULATOR_NAME'..."
    emulator -avd "$EMULATOR_NAME" -no-audio -no-window -gpu swiftshader_indirect &

    # Wait for emulator to be ready
    if wait_for_device; then
        echo "Emulator started successfully!"
        adb devices
    else
        echo "Failed to start emulator"
        exit 1
    fi
fi

# Unlock device
echo "Unlocking device..."
adb shell input keyevent 82  # Unlock
adb shell input swipe 300 1000 300 500  # Swipe to unlock

# Disable animations for faster testing
echo "Disabling animations for testing..."
adb shell settings put global window_animation_scale 0
adb shell settings put global transition_animation_scale 0
adb shell settings put global animator_duration_scale 0

echo "Emulator setup complete. Ready for testing!"

# If script is called with arguments, execute them as test commands
if [ $# -gt 0 ]; then
    echo "Running: $@"
    exec "$@"
fi