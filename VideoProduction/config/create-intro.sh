#!/bin/bash

# ShareConnect Intro Video Generator
# Creates professional intro video with branding

OUTPUT_FILE="$1"
DURATION="$2"

if [ -z "$OUTPUT_FILE" ] || [ -z "$DURATION" ]; then
    echo "Usage: $0 <output_file.mp4> <duration_seconds>"
    exit 1
fi

echo "🎬 Creating intro video: $OUTPUT_FILE"
echo "⏱️  Duration: $DURATION seconds"

# Create intro using ffmpeg with professional effects
ffmpeg -y -f lavfi -i color=c=#667eea:s=1920x1080:d=$DURATION \
    -f lavfi -i "anullsrc=r=48000:d=$DURATION" \
    -filter_complex "
        [0:v]fade=t=in:st=0:d=1,fade=t=out:st=$(($DURATION-1)):d=1[v];
        [1:a]afade=t=in:st=0:d=0.5,afade=t=out:st=$(($DURATION-0.5)):d=0.5[a]
    " \
    -map "[v]" -map "[a]" \
    -c:v libx264 -preset veryfast -crf 23 \
    -c:a aac -b:a 320k \
    "$OUTPUT_FILE"

echo "✅ Intro video created: $OUTPUT_FILE"
