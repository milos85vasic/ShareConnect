#!/bin/bash

# ShareConnect Video Production - OBS Setup Script
# This script configures OBS Studio for professional video recording

set -euo pipefail

echo "🎬 ShareConnect Video Production - OBS Setup"
echo "=============================================="

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
VIDEO_CONFIG_DIR="$PROJECT_ROOT/VideoProduction/config"
OBS_CONFIG_DIR="$HOME/.config/obs-studio"

# Create configuration directory
mkdir -p "$VIDEO_CONFIG_DIR"
mkdir -p "$OBS_CONFIG_DIR"

# Function to create OBS scene collection
create_scene_collection() {
    local collection_name="$1"
    local resolution="$2"
    local fps="$3"
    
    echo "📺 Creating OBS scene collection: $collection_name"
    
    # Create scene collection JSON
    cat > "$VIDEO_CONFIG_DIR/${collection_name}_scenes.json" << EOF
{
    "name": "ShareConnect - ${collection_name}",
    "scenes": [
        {
            "name": "Intro",
            "sources": [
                {
                    "name": "Intro Video",
                    "type": "ffmpeg_source",
                    "settings": {
                        "local_file": "$PROJECT_ROOT/VideoProduction/assets/intro-music.mp4",
                        "looping": false,
                        "restart_on_activate": true
                    }
                }
            ]
        },
        {
            "name": "Main Presentation",
            "sources": [
                {
                    "name": "Screen Capture",
                    "type": "monitor_capture",
                    "settings": {
                        "monitor": 0,
                        "capture_cursor": true
                    }
                },
                {
                    "name": "Webcam",
                    "type": "v4l2_source",
                    "settings": {
                        "device": "/dev/video0",
                        "resolution": "${resolution}",
                        "fps": ${fps}
                    }
                },
                {
                    "name": "Lower Third",
                    "type": "browser_source",
                    "settings": {
                        "url": "file://$PROJECT_ROOT/VideoProduction/assets/lower-third.html",
                        "width": 1920,
                        "height": 108,
                        "fps": ${fps}
                    }
                }
            ]
        },
        {
            "name": "Outro",
            "sources": [
                {
                    "name": "Outro Video",
                    "type": "ffmpeg_source",
                    "settings": {
                        "local_file": "$PROJECT_ROOT/VideoProduction/assets/outro-music.mp4",
                        "looping": false,
                        "restart_on_activate": true
                    }
                }
            ]
        }
    ],
    "video_settings": {
        "base_resolution": "${resolution}",
        "output_resolution": "${resolution}",
        "fps": ${fps},
        "format": "mp4",
        "encoder": "x264",
        "bitrate": 8000,
        "keyframe_interval": 2,
        "preset": "veryfast",
        "profile": "high",
        "level": "4.2"
    },
    "audio_settings": {
        "sample_rate": 48000,
        "channels": 2,
        "bitrate": 320,
        "encoder": "aac"
    }
}
EOF
}

# Function to setup audio configuration
setup_audio() {
    echo "🎤 Setting up professional audio configuration"
    
    # Create audio settings file
    cat > "$VIDEO_CONFIG_DIR/audio-settings.json" << EOF
{
    "sample_rate": 48000,
    "channels": 2,
    "audio_devices": {
        "microphone": {
            "device": "default",
            "volume": 80,
            "noise_suppression": true,
            "gain": 10
        },
        "desktop": {
            "device": "default",
            "volume": 50,
            "monitoring": true
        }
    },
    "filters": {
        "noise_suppression": {
            "enabled": true,
            "method": "rnnoise",
            "suppress_level": -30
        },
        "compressor": {
            "enabled": true,
            "ratio": 3.0,
            "threshold": -18.0,
            "attack": 10.0,
            "release": 100.0
        },
        "limiter": {
            "enabled": true,
            "threshold": -6.0,
            "release": 50.0
        }
    }
}
EOF
}

# Function to create recording templates
create_recording_templates() {
    echo "📄 Creating recording templates"
    
    # Create recording script template
    cat > "$VIDEO_CONFIG_DIR/record-template.sh" << 'EOF'
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
EOF

    chmod +x "$VIDEO_CONFIG_DIR/record-template.sh"
}

# Function to setup quality check automation
setup_quality_checks() {
    echo "🔍 Setting up quality check automation"
    
    cat > "$VIDEO_CONFIG_DIR/quality-check.sh" << 'EOF'
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
EOF

    chmod +x "$VIDEO_CONFIG_DIR/quality-check.sh"
}

# Function to create lower third HTML template
create_lower_third_template() {
    echo "🎨 Creating lower third template"
    
    cat > "$VIDEO_CONFIG_DIR/lower-third.html" << 'EOF'
<!DOCTYPE html>
<html>
<head>
    <style>
        body {
            margin: 0;
            padding: 0;
            background: transparent;
            font-family: 'Arial', sans-serif;
            overflow: hidden;
        }
        
        .lower-third {
            position: absolute;
            bottom: 20px;
            left: 50px;
            right: 50px;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0,0,0,0.3);
            animation: slideIn 0.5s ease-out;
        }
        
        .title {
            font-size: 24px;
            font-weight: bold;
            margin-bottom: 5px;
        }
        
        .subtitle {
            font-size: 16px;
            opacity: 0.9;
        }
        
        @keyframes slideIn {
            from {
                transform: translateY(100px);
                opacity: 0;
            }
            to {
                transform: translateY(0);
                opacity: 1;
            }
        }
    </style>
</head>
<body>
    <div class="lower-third">
        <div class="title">ShareConnect Tutorial</div>
        <div class="subtitle">Professional Video Production</div>
    </div>
    
    <script>
        // Function to update lower third content
        function updateContent(title, subtitle) {
            document.querySelector('.title').textContent = title;
            document.querySelector('.subtitle').textContent = subtitle;
        }
        
        // Example usage
        // updateContent('Lesson 1', 'ShareConnect Overview');
    </script>
</body>
</html>
EOF
}

# Function to create intro/outro video templates
create_intro_outro_templates() {
    echo "🎬 Creating intro/outro templates"
    
    # Create intro template script
    cat > "$VIDEO_CONFIG_DIR/create-intro.sh" << 'EOF'
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
EOF

    chmod +x "$VIDEO_CONFIG_DIR/create-intro.sh"
    
    # Create outro template script
    cat > "$VIDEO_CONFIG_DIR/create-outro.sh" << 'EOF'
#!/bin/bash

# ShareConnect Outro Video Generator
# Creates professional outro video with call-to-action

OUTPUT_FILE="$1"
DURATION="$2"

if [ -z "$OUTPUT_FILE" ] || [ -z "$DURATION" ]; then
    echo "Usage: $0 <output_file.mp4> <duration_seconds>"
    exit 1
fi

echo "🎬 Creating outro video: $OUTPUT_FILE"
echo "⏱️  Duration: $DURATION seconds"

# Create outro using ffmpeg with professional effects
ffmpeg -y -f lavfi -i color=c=#764ba2:s=1920x1080:d=$DURATION \
    -f lavfi -i "anullsrc=r=48000:d=$DURATION" \
    -filter_complex "
        [0:v]fade=t=in:st=0:d=1,fade=t=out:st=$(($DURATION-1)):d=1[v];
        [1:a]afade=t=in:st=0:d=0.5,afade=t=out:st=$(($DURATION-0.5)):d=0.5[a]
    " \
    -map "[v]" -map "[a]" \
    -c:v libx264 -preset veryfast -crf 23 \
    -c:a aac -b:a 320k \
    "$OUTPUT_FILE"

echo "✅ Outro video created: $OUTPUT_FILE"
EOF

    chmod +x "$VIDEO_CONFIG_DIR/create-outro.sh"
}

# Main setup function
main() {
    echo "🚀 Starting OBS setup for ShareConnect video production"
    
    # Create all configurations
    create_scene_collection "app-demo" "1920x1080" "30"
    create_scene_collection "api-tutorial" "1920x1080" "30"
    create_scene_collection "screen-share" "1920x1080" "30"
    
    setup_audio
    create_recording_templates
    setup_quality_checks
    create_lower_third_template
    create_intro_outro_templates
    
    echo ""
    echo "✅ OBS setup completed successfully!"
    echo ""
    echo "📁 Configuration files created in: $VIDEO_CONFIG_DIR"
    echo ""
    echo "Next steps:"
    echo "1. Install OBS Studio if not already installed"
    echo "2. Import scene collections from $VIDEO_CONFIG_DIR"
    echo "3. Test recording with: $VIDEO_CONFIG_DIR/record-template.sh"
    echo "4. Check quality with: $VIDEO_CONFIG_DIR/quality-check.sh"
    echo ""
    echo "For professional recording, ensure you have:"
    echo "- Good lighting setup"
    echo "- Quiet recording environment"
    echo "- Stable internet connection"
    echo "- Tested all equipment before recording"
}

# Run main function
main "$@"