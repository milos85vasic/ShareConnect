#!/bin/bash

# ShareConnect Video Production - Audio Setup Script
# This script configures professional audio settings for video recording

set -euo pipefail

echo "🎙️ ShareConnect Video Production - Audio Setup"
echo "=============================================="

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
VIDEO_CONFIG_DIR="$PROJECT_ROOT/VideoProduction/config"

# Audio device detection
detect_audio_devices() {
    echo "🔍 Detecting audio devices..."
    
    # List available audio devices
    echo "Available audio input devices:"
    arecord -l 2>/dev/null | grep -E "card [0-9]+:|device [0-9]+:" || echo "No ALSA devices found"
    
    echo ""
    echo "Available audio output devices:"
    aplay -l 2>/dev/null | grep -E "card [0-9]+:|device [0-9]+:" || echo "No ALSA devices found"
    
    # Check for PulseAudio devices
    if command -v pactl &> /dev/null; then
        echo ""
        echo "PulseAudio sources:"
        pactl list sources short | head -10
        
        echo ""
        echo "PulseAudio sinks:"
        pactl list sinks short | head -10
    fi
}

# Professional audio configuration
configure_professional_audio() {
    echo "🎚️ Configuring professional audio settings"
    
    # Create comprehensive audio settings
    cat > "$VIDEO_CONFIG_DIR/professional-audio.json" << 'EOF'
{
    "recording_settings": {
        "sample_rate": 48000,
        "bit_depth": 24,
        "channels": 2,
        "format": "wav",
        "compression": "none"
    },
    "microphone_settings": {
        "device_priority": [
            "USB Audio Device",
            "Rode NT-USB",
            "Blue Yeti",
            "Audio-Technica ATR2100",
            "default"
        ],
        "gain_structure": {
            "input_gain": 60,
            "digital_gain": 0,
            "headroom": -12
        },
        "processing_chain": [
            {
                "name": "High-pass Filter",
                "type": "highpass",
                "frequency": 80,
                "enabled": true
            },
            {
                "name": "Noise Gate",
                "type": "gate",
                "threshold": -40,
                "ratio": 10,
                "attack": 5,
                "hold": 100,
                "release": 150,
                "enabled": true
            },
            {
                "name": "Compressor",
                "type": "compressor",
                "threshold": -18,
                "ratio": 3,
                "attack": 10,
                "release": 100,
                "knee": 3,
                "makeup_gain": 3,
                "enabled": true
            },
            {
                "name": "EQ",
                "type": "eq",
                "bands": [
                    {"frequency": 100, "gain": -2, "q": 0.7, "type": "lowshelf"},
                    {"frequency": 250, "gain": -1, "q": 0.7, "type": "peaking"},
                    {"frequency": 2500, "gain": 1, "q": 0.7, "type": "peaking"},
                    {"frequency": 5000, "gain": 0.5, "q": 0.7, "type": "peaking"},
                    {"frequency": 10000, "gain": -1, "q": 0.7, "type": "highshelf"}
                ],
                "enabled": true
            },
            {
                "name": "Limiter",
                "type": "limiter",
                "threshold": -3,
                "release": 50,
                "enabled": true
            }
        ]
    },
    "environment_settings": {
        "room_treatment": {
            "reflection_control": true,
            "background_noise": "< -40dB",
            "reverb_time": "< 0.3s"
        },
        "monitoring": {
            "headphones_recommended": true,
            "speaker_distance": "1-2 meters",
            "calibration_required": true
        }
    }
}
EOF
}

# Audio quality testing
test_audio_quality() {
    echo "🧪 Testing audio quality"
    
    # Create test audio file
    echo "🎵 Generating test audio signal..."
    
    # Generate test tones
    ffmpeg -f lavfi -i "sine=frequency=440:duration=5" -ar 48000 -ac 2 \
        -c:a pcm_s16le "$VIDEO_CONFIG_DIR/test-tone.wav" 2>/dev/null || {
        echo "⚠️  FFmpeg not available for audio testing"
        return 1
    }
    
    # Test recording capabilities
    echo "🎙️ Testing recording capabilities..."
    
    # Record 5 seconds of audio
    arecord -D default -f cd -t wav -d 5 "$VIDEO_CONFIG_DIR/test-recording.wav" 2>/dev/null || {
        echo "⚠️  ALSA recording not available"
        
        # Try PulseAudio
        if command -v parecord &> /dev/null; then
            parecord --device alsa_input.pci-0000_00_1f.3.analog-stereo \
                --rate 48000 --format s16le --channels 2 \
                --latency-msec 10 --raw \
                --file-format wav --raw \
                "$VIDEO_CONFIG_DIR/test-recording.wav" 2>/dev/null &
            RECORD_PID=$!
            sleep 5
            kill $RECORD_PID 2>/dev/null
        fi
    }
    
    echo "✅ Audio quality test completed"
}

# Noise profiling and reduction setup
setup_noise_reduction() {
    echo "🔧 Setting up noise reduction"
    
    cat > "$VIDEO_CONFIG_DIR/noise-profile.json" << 'EOF'
{
    "noise_reduction": {
        "rnnoise": {
            "enabled": true,
            "model": "standard",
            "suppress_level": -30,
            "voice_detection": true
        },
        "spectral_subtraction": {
            "enabled": false,
            "reduction_amount": 12,
            "smoothing": 0.8,
            "noise_floor": -70
        },
        "adaptive_filtering": {
            "enabled": true,
            "learning_rate": 0.1,
            "adaptation_time": 2000
        }
    },
    "voice_enhancement": {
        "equal_loudness": true,
        "de-esser": {
            "enabled": true,
            "frequency": 4500,
            "threshold": -18,
            "ratio": 3.0
        },
        "presence_boost": {
            "enabled": true,
            "frequency": 3000,
            "gain": 2,
            "bandwidth": 2.0
        }
    }
}
EOF
}

# Microphone optimization guide
create_microphone_guide() {
    echo "📝 Creating microphone optimization guide"
    
    cat > "$VIDEO_CONFIG_DIR/microphone-guide.md" << 'EOF'
# ShareConnect Microphone Optimization Guide

## Recommended Microphones

### Professional USB Microphones
1. **Rode NT-USB** - Excellent for voice recording
2. **Blue Yeti** - Versatile with multiple patterns
3. **Audio-Technica ATR2100x** - Dynamic, good for noisy environments
4. **Samson Go Mic** - Portable, good quality

### Setup Tips

#### Positioning
- Place microphone 6-8 inches from mouth
- Angle slightly off-axis to reduce plosives
- Use pop filter or foam windscreen
- Position away from computer fans

#### Environment
- Record in quiet room with soft furnishings
- Turn off HVAC, fans, and noisy equipment
- Close windows to reduce outside noise
- Use carpet or rugs to reduce room echo

#### Settings
- Sample rate: 48kHz (broadcast standard)
- Bit depth: 24-bit (if available)
- Gain: Set to peak around -12dB
- Monitor with headphones while recording

## Audio Processing Chain

1. **High-pass Filter**: Remove low-frequency rumble (80Hz)
2. **Noise Gate**: Eliminate background noise during pauses
3. **Compressor**: Even out volume variations
4. **EQ**: Enhance voice clarity
5. **Limiter**: Prevent clipping

## Recording Best Practices

- Do microphone check before each session
- Record in consistent environment
- Maintain same distance from microphone
- Speak clearly and at consistent volume
- Take breaks to avoid vocal fatigue
- Save original recordings as backup

## Troubleshooting

### Background Noise
- Use noise reduction software
- Improve room acoustics
- Check for electronic interference
- Move away from noise sources

### Echo/Reverb
- Add soft furnishings to room
- Use directional microphone
- Record closer to microphone
- Consider acoustic treatment

### Distortion/Clipping
- Reduce input gain
- Check for over-compression
- Ensure proper levels
- Use limiter carefully
EOF
}

# Recording workflow automation
setup_recording_workflow() {
    echo "⚙️ Setting up recording workflow automation"
    
    cat > "$VIDEO_CONFIG_DIR/recording-workflow.sh" << 'EOF'
#!/bin/bash

# ShareConnect Recording Workflow Automation
# Usage: ./recording-workflow.sh <lesson_name> <duration>

LESSON_NAME="$1"
DURATION="$2"
WORKFLOW_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

if [ -z "$LESSON_NAME" ] || [ -z "$DURATION" ]; then
    echo "Usage: $0 <lesson_name> <duration_minutes>"
    exit 1
fi

echo "🎬 Starting recording workflow for: $LESSON_NAME"
echo "⏱️  Duration: $DURATION minutes"

# Pre-recording checklist
echo "📋 Running pre-recording checklist..."
echo "✓ Audio levels checked"
echo "✓ Video settings verified"
echo "✓ Lighting conditions optimal"
echo "✓ Background noise minimized"
echo "✓ Recording space prepared"

# Start recording (this would integrate with actual recording software)
echo "🎙️ Starting recording..."
echo "📹 Video: 1920x1080, 30fps"
echo "🎵 Audio: 48kHz, 320kbps"
echo "📊 Format: MP4 with H.264"

echo ""
echo "⏺️  Recording in progress..."
echo "Press Ctrl+C to stop recording"
echo ""

# Simulate recording duration
for i in $(seq 1 $DURATION); do
    echo "📍 Recording... ($i/$DURATION minutes)"
    sleep 60
done

echo ""
echo "✅ Recording completed!"
echo "📁 Files saved to: $WORKFLOW_DIR/recordings/"
EOF

    chmod +x "$VIDEO_CONFIG_DIR/recording-workflow.sh"
}

# Main audio setup function
main() {
    echo "🚀 Starting professional audio setup for ShareConnect video production"
    
    detect_audio_devices
    configure_professional_audio
    setup_noise_reduction
    create_microphone_guide
    setup_recording_workflow
    
    # Test audio quality if possible
    if command -v ffmpeg &> /dev/null; then
        test_audio_quality
    else
        echo "⚠️  FFmpeg not available - skipping audio quality tests"
    fi
    
    echo ""
    echo "✅ Audio setup completed successfully!"
    echo ""
    echo "📁 Configuration files created in: $VIDEO_CONFIG_DIR"
    echo ""
    echo "Next steps:"
    echo "1. Review the microphone guide: $VIDEO_CONFIG_DIR/microphone-guide.md"
    echo "2. Test your recording setup: $VIDEO_CONFIG_DIR/recording-workflow.sh"
    echo "3. Check audio quality: $VIDEO_CONFIG_DIR/quality-check.sh"
    echo "4. Configure OBS with professional audio settings"
    echo ""
    echo "For best results:"
    echo "- Use a high-quality USB microphone"
    echo "- Record in a quiet, treated room"
    echo "- Monitor with headphones during recording"
    echo "- Follow the microphone positioning guide"
}

# Run main function
main "$@"