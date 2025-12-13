#!/bin/bash

# ShareConnect Video Quality Check Script
# Usage: ./quality-check.sh <video_file>

VIDEO_FILE="$1"

if [ -z "$VIDEO_FILE" ]; then
    echo "Usage: $0 <video_file>"
    exit 1
fi

if [ ! -f "$VIDEO_FILE" ]; then
    echo "❌ Error: Video file not found: $VIDEO_FILE"
    exit 1
fi

echo "🔍 Checking video quality for: $VIDEO_FILE"

# Check video resolution
echo "📺 Checking video resolution..."
RESOLUTION=$(ffprobe -v error -select_streams v:0 -show_entries stream=width,height -of csv=s=x:p=0 "$VIDEO_FILE" 2>/dev/null)
if [ $? -eq 0 ]; then
    echo "✅ Resolution: $RESOLUTION"
    if [[ "$RESOLUTION" == "1920x1080" ]]; then
        echo "✅ Resolution meets 1080p requirement"
    else
        echo "⚠️  Resolution is $RESOLUTION (recommended: 1920x1080)"
    fi
else
    echo "❌ Could not determine video resolution"
fi

# Check audio bitrate
echo "🎵 Checking audio quality..."
AUDIO_BITRATE=$(ffprobe -v error -select_streams a:0 -show_entries stream=bit_rate -of default=noprint_wrappers=1:nokey=1 "$VIDEO_FILE" 2>/dev/null)
if [ $? -eq 0 ] && [ ! -z "$AUDIO_BITRATE" ]; then
    AUDIO_KBPS=$((AUDIO_BITRATE / 1000))
    echo "✅ Audio bitrate: ${AUDIO_KBPS}kbps"
    if [ $AUDIO_KBPS -ge 320 ]; then
        echo "✅ Audio bitrate meets quality requirement"
    else
        echo "⚠️  Audio bitrate is ${AUDIO_KBPS}kbps (recommended: 320kbps+)"
    fi
else
    echo "❌ Could not determine audio bitrate"
fi

# Check video codec
echo "📹 Checking video codec..."
VIDEO_CODEC=$(ffprobe -v error -select_streams v:0 -show_entries stream=codec_name -of default=noprint_wrappers=1:nokey=1 "$VIDEO_FILE" 2>/dev/null)
if [ $? -eq 0 ] && [ ! -z "$VIDEO_CODEC" ]; then
    echo "✅ Video codec: $VIDEO_CODEC"
    if [[ "$VIDEO_CODEC" == "h264" ]]; then
        echo "✅ Video codec is H.264 (recommended)"
    else
        echo "⚠️  Video codec is $VIDEO_CODEC (recommended: h264)"
    fi
else
    echo "❌ Could not determine video codec"
fi

echo ""
echo "✅ Quality check complete!"
