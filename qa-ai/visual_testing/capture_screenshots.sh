#!/bin/bash

# Screenshot Capture Script for Visual Testing

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/visual_config.json"
CURRENT_DIR="$SCRIPT_DIR/current"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)

echo "📸 Capturing screenshots for visual regression testing..."

# Check if device is connected
if ! adb devices | grep -q "device$"; then
    echo "❌ No Android device/emulator found"
    exit 1
fi

# Read configuration
if [ ! -f "$CONFIG_FILE" ]; then
    echo "❌ Configuration file not found: $CONFIG_FILE"
    exit 1
fi

# Create current screenshots directory
CURRENT_RUN_DIR="$CURRENT_DIR/$TIMESTAMP"
mkdir -p "$CURRENT_RUN_DIR"

echo "Capturing screenshots to: $CURRENT_RUN_DIR"

# Get test scenarios
SCENARIOS=$(jq -r '.visual_testing.test_scenarios[].name' "$CONFIG_FILE")

for scenario in $SCENARIOS; do
    echo "📱 Capturing $scenario..."

    # Get scenario details
    ACTIVITY=$(jq -r ".visual_testing.test_scenarios[] | select(.name==\"$scenario\") | .activity" "$CONFIG_FILE")
    WAIT_TIME=$(jq -r ".visual_testing.test_scenarios[] | select(.name==\"$scenario\") | .wait_time_ms" "$CONFIG_FILE")

    # Wait for activity to load
    sleep $(($WAIT_TIME / 1000))

    # Capture screenshot
    adb exec-out screencap -p > "$CURRENT_RUN_DIR/${scenario}.png"

    if [ $? -eq 0 ]; then
        echo "✅ Captured $scenario"
    else
        echo "❌ Failed to capture $scenario"
    fi
done

echo "📸 Screenshot capture completed"
echo "Screenshots saved to: $CURRENT_RUN_DIR"

# Create symlink to latest
ln -sf "$CURRENT_RUN_DIR" "$CURRENT_DIR/latest"
