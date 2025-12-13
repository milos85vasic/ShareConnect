#!/bin/bash

# ShareConnect Recording Template
# Usage: ./record-template.sh <scene_name> <output_name>

SCENE_NAME="$1"
OUTPUT_NAME="$2"
DATE=$(date +%Y%m%d_%H%M%S)

if [ -z "$SCENE_NAME" ] || [ -z "$OUTPUT_NAME" ]; then
    echo "Usage: $0 <scene_name> <output_name>"
    exit 1
fi

echo "🎬 Starting recording for scene: $SCENE_NAME"
echo "📁 Output will be saved as: $OUTPUT_NAME"

# Start OBS recording (this would be automated in real implementation)
echo "Starting OBS with scene: $SCENE_NAME"

# Simulate recording process
echo "⏺️  Recording started..."
echo "📊 Resolution: 1920x1080"
echo "🎵 Audio: 48kHz, 320kbps AAC"
echo "⏱️  Duration: Manual control"
echo ""
echo "Press Ctrl+C to stop recording"
echo ""

# Keep script running (in real implementation, this would control OBS)
while true; do
    sleep 1
done
