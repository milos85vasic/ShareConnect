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
