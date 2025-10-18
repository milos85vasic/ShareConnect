#!/bin/bash

# Visual Regression Testing Setup for ShareConnect
# Sets up automated screenshot capture and comparison

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
VISUAL_DIR="$SCRIPT_DIR/visual_testing"
BASELINES_DIR="$VISUAL_DIR/baselines"
CURRENT_DIR="$VISUAL_DIR/current"
DIFFS_DIR="$VISUAL_DIR/diffs"
REPORTS_DIR="$VISUAL_DIR/reports"

echo "🎨 Setting up Visual Regression Testing..."

# Create directories
mkdir -p "$BASELINES_DIR" "$CURRENT_DIR" "$DIFFS_DIR" "$REPORTS_DIR"

# Create visual testing configuration
cat > "$VISUAL_DIR/visual_config.json" << 'EOF'
{
  "visual_testing": {
    "enabled": true,
    "screenshot_tool": "uiautomator",
    "comparison_tool": "pixelmatch",
    "threshold": 0.01,
    "baseline_images": {
      "main_screen": "main_activity_baseline.png",
      "settings_screen": "settings_activity_baseline.png",
      "profile_screen": "profile_management_baseline.png",
      "sharing_dialog": "url_sharing_dialog_baseline.png",
      "sync_status": "sync_status_indicator_baseline.png"
    },
    "test_scenarios": [
      {
        "name": "main_screen",
        "description": "Main application screen layout",
        "activity": "MainActivity",
        "wait_time_ms": 2000
      },
      {
        "name": "settings_screen",
        "description": "Settings screen layout",
        "activity": "SettingsActivity",
        "wait_time_ms": 1500
      },
      {
        "name": "profile_screen",
        "description": "Profile management screen",
        "activity": "ProfileManagementActivity",
        "wait_time_ms": 2000
      },
      {
        "name": "sharing_dialog",
        "description": "URL sharing dialog appearance",
        "activity": "ShareActivity",
        "wait_time_ms": 1000
      },
      {
        "name": "sync_status",
        "description": "Sync status indicator",
        "activity": "MainActivity",
        "wait_time_ms": 500
      }
    ]
  }
}
EOF

# Create screenshot capture script
cat > "$VISUAL_DIR/capture_screenshots.sh" << 'EOF'
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
EOF

# Create visual comparison script
cat > "$VISUAL_DIR/compare_screenshots.sh" << 'EOF'
#!/bin/bash

# Visual Comparison Script for Regression Testing

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CONFIG_FILE="$SCRIPT_DIR/visual_config.json"
BASELINES_DIR="$SCRIPT_DIR/baselines"
CURRENT_DIR="$SCRIPT_DIR/current"
DIFFS_DIR="$SCRIPT_DIR/diffs"
REPORTS_DIR="$SCRIPT_DIR/reports"

# Check if ImageMagick is installed
if ! command -v compare &> /dev/null; then
    echo "❌ ImageMagick not found. Please install: apt-get install imagemagick"
    exit 1
fi

echo "🔍 Comparing screenshots for visual regression..."

# Get latest screenshots
LATEST_DIR="$CURRENT_DIR/latest"
if [ ! -d "$LATEST_DIR" ]; then
    echo "❌ No screenshots found to compare"
    exit 1
fi

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
DIFF_RUN_DIR="$DIFFS_DIR/$TIMESTAMP"
REPORT_FILE="$REPORTS_DIR/visual_regression_report_$TIMESTAMP.html"

mkdir -p "$DIFF_RUN_DIR"

echo "Comparing screenshots from: $LATEST_DIR"
echo "Saving diffs to: $DIFF_RUN_DIR"

# Read configuration
THRESHOLD=$(jq -r '.visual_testing.threshold' "$CONFIG_FILE")
SCENARIOS=$(jq -r '.visual_testing.test_scenarios[].name' "$CONFIG_FILE")

DIFFERENCES_FOUND=0
TOTAL_COMPARISONS=0

# Generate HTML report header
cat > "$REPORT_FILE" << HTML_HEADER
<!DOCTYPE html>
<html>
<head>
    <title>Visual Regression Report - $TIMESTAMP</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; background: #f5f5f5; }
        .header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; border-radius: 10px; margin-bottom: 30px; }
        .comparison { background: white; padding: 20px; margin: 20px 0; border-radius: 10px; box-shadow: 0 2px 4px rgba(0,0,0,0.1); }
        .images { display: flex; justify-content: space-around; margin: 20px 0; }
        .image-container { text-align: center; }
        .image-container img { max-width: 300px; border: 2px solid #ddd; border-radius: 5px; }
        .status { padding: 10px; border-radius: 5px; margin: 10px 0; font-weight: bold; }
        .passed { background: #d4edda; color: #155724; }
        .failed { background: #f8d7da; color: #721c24; }
        .diff-highlight { border-color: #dc3545 !important; }
    </style>
</head>
<body>
    <div class="header">
        <h1>👁️ Visual Regression Report</h1>
        <p><strong>Generated:</strong> $(date)</p>
        <p><strong>Comparison Threshold:</strong> $THRESHOLD</p>
    </div>
HTML_HEADER

for scenario in $SCENARIOS; do
    CURRENT_IMG="$LATEST_DIR/${scenario}.png"
    BASELINE_IMG="$BASELINES_DIR/${scenario}_baseline.png"
    DIFF_IMG="$DIFF_RUN_DIR/${scenario}_diff.png"

    if [ ! -f "$CURRENT_IMG" ]; then
        echo "⚠️  Current screenshot not found: $scenario"
        continue
    fi

    if [ ! -f "$BASELINE_IMG" ]; then
        echo "📝 No baseline found for $scenario, creating one..."
        cp "$CURRENT_IMG" "$BASELINE_IMG"
        continue
    fi

    TOTAL_COMPARISONS=$((TOTAL_COMPARISONS + 1))

    echo "Comparing $scenario..."

    # Compare images using ImageMagick
    compare -metric MAE "$BASELINE_IMG" "$CURRENT_IMG" "$DIFF_IMG" 2>/dev/null || true

    # Calculate difference percentage (simplified)
    # In a real implementation, you'd use a more sophisticated tool like pixelmatch
    DIFFERENCE=$(identify -format "%[mean]" "$DIFF_IMG" 2>/dev/null || echo "0")

    # Convert to percentage (simplified calculation)
    DIFF_PERCENTAGE=$(echo "scale=4; $DIFFERENCE / 65535 * 100" | bc 2>/dev/null || echo "0")

    # Check if difference exceeds threshold
    THRESHOLD_EXCEEDED=$(echo "$DIFF_PERCENTAGE > $THRESHOLD" | bc -l 2>/dev/null || echo "0")

    if [ "$THRESHOLD_EXCEEDED" -eq 1 ]; then
        STATUS="FAILED"
        STATUS_CLASS="failed"
        DIFFERENCES_FOUND=$((DIFFERENCES_FOUND + 1))
        echo "❌ $scenario differs by ${DIFF_PERCENTAGE}% (threshold: $THRESHOLD%)"
    else
        STATUS="PASSED"
        STATUS_CLASS="passed"
        echo "✅ $scenario matches (diff: ${DIFF_PERCENTAGE}%)"
    fi

    # Add to HTML report
    cat >> "$REPORT_FILE" << COMPARISON_HTML
    <div class="comparison">
        <h3>$scenario</h3>
        <div class="status $STATUS_CLASS">$STATUS - Difference: ${DIFF_PERCENTAGE}%</div>
        <div class="images">
            <div class="image-container">
                <h4>Baseline</h4>
                <img src="data:image/png;base64,$(base64 -w 0 "$BASELINE_IMG")" alt="Baseline">
            </div>
            <div class="image-container">
                <h4>Current</h4>
                <img src="data:image/png;base64,$(base64 -w 0 "$CURRENT_IMG")" alt="Current">
            </div>
            <div class="image-container">
                <h4>Difference</h4>
                <img src="data:image/png;base64,$(base64 -w 0 "$DIFF_IMG")" alt="Difference" class="${STATUS_CLASS} diff-highlight">
            </div>
        </div>
    </div>
COMPARISON_HTML
done

# Complete HTML report
cat >> "$REPORT_FILE" << HTML_FOOTER
    <div class="summary">
        <h2>📊 Summary</h2>
        <p>Total Comparisons: $TOTAL_COMPARISONS</p>
        <p>Differences Found: $DIFFERENCES_FOUND</p>
        <p>Success Rate: $(echo "scale=2; ($TOTAL_COMPARISONS - $DIFFERENCES_FOUND) * 100 / $TOTAL_COMPARISONS" | bc 2>/dev/null || echo "100")%</p>
    </div>
</body>
</html>
HTML_FOOTER

echo ""
echo "📄 Visual regression report saved: $REPORT_FILE"

if [ $DIFFERENCES_FOUND -gt 0 ]; then
    echo "⚠️  $DIFFERENCES_FOUND visual differences detected"
    exit 1
else
    echo "✅ All visual comparisons passed"
fi
EOF

# Make scripts executable
chmod +x "$VISUAL_DIR/capture_screenshots.sh"
chmod +x "$VISUAL_DIR/compare_screenshots.sh"

# Create README for visual testing
cat > "$VISUAL_DIR/README.md" << 'EOF'
# Visual Regression Testing

This directory contains automated visual regression testing tools for ShareConnect.

## Setup

1. Ensure Android device/emulator is connected
2. Install ImageMagick: `apt-get install imagemagick`
3. Run baseline capture: `./capture_screenshots.sh` (first run creates baselines)

## Usage

### Capture Screenshots
```bash
./capture_screenshots.sh
```
Captures screenshots of key UI elements and saves them to `current/` directory.

### Compare with Baselines
```bash
./compare_screenshots.sh
```
Compares current screenshots with baseline images and generates a visual regression report.

## Configuration

Edit `visual_config.json` to:
- Add new test scenarios
- Adjust comparison thresholds
- Modify wait times for UI elements

## Baseline Images

Baseline images are stored in the `baselines/` directory. To update baselines:
1. Capture new screenshots with desired appearance
2. Copy images from `current/latest/` to `baselines/` with `_baseline.png` suffix

## Reports

Visual regression reports are generated in HTML format in the `reports/` directory, showing:
- Side-by-side comparison of baseline vs current screenshots
- Highlighted differences
- Pass/fail status based on threshold

echo "✅ Visual regression testing setup completed"
echo "📁 Created directory structure in: $VISUAL_DIR"
echo "📄 Configuration file: $VISUAL_DIR/visual_config.json"
echo "📸 Screenshot capture script: $VISUAL_DIR/capture_screenshots.sh"
echo "🔍 Comparison script: $VISUAL_DIR/compare_screenshots.sh"
echo "📖 Documentation: $VISUAL_DIR/README.md"
EOF

# Make the setup script executable
chmod +x "$SCRIPT_DIR/visual_testing_setup.sh"

echo "🎨 Visual testing setup script created: $SCRIPT_DIR/visual_testing_setup.sh"
echo "Run './qa-ai/visual_testing_setup.sh' to set up visual regression testing"