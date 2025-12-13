#!/bin/bash

# ShareConnect Video Production - Content Creation Pipeline
# This script converts markdown scripts to teleprompter format and manages content

set -euo pipefail

echo "📄 ShareConnect Video Production - Content Creation Pipeline"
echo "=============================================================="

# Configuration
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/../.." && pwd)"
COURSES_DIR="$PROJECT_ROOT/VideoProduction/courses"
CONTENT_DIR="$PROJECT_ROOT/VideoProduction/content"

# Create content directory
mkdir -p "$CONTENT_DIR"/{teleprompter,audio-scripts,visual-notes}

# Function to convert markdown to teleprompter format
convert_to_teleprompter() {
    local input_file="$1"
    local output_file="$2"
    local lesson_title="$3"
    
    echo "📝 Converting to teleprompter format: $(basename "$input_file")"
    
    # Create teleprompter-friendly format
    cat > "$output_file" << EOF
# ShareConnect Video Course
## $lesson_title

$(date '+%B %d, %Y')

---

# TELEPROMPTER SCRIPT

$(sed 's/^## \(.*\)/\n## \1\n/' "$input_file" | \
  sed 's/^### \[/\n### [/' | \
  sed 's/\*\*VISUAL\*\*:/\n**VISUAL**:/' | \
  sed 's/\*\*NARRATOR\*\*:/\n**NARRATOR**:/' | \
  sed 's/\*\*ON-SCREEN TEXT\*\*:/\n**ON-SCREEN TEXT**:/')

---

# RECORDING NOTES

## Timing Guidelines:
- Speak at 150-160 words per minute
- Pause briefly at section breaks
- Allow time for visual transitions

## Audio Quality:
- Maintain consistent volume
- Avoid background noise
- Speak clearly and confidently

## Visual Cues:
- Wait for animations to complete
- Sync narration with on-screen text
- Allow time for viewer comprehension

## Technical Specs:
- Resolution: 1920x1080
- Frame Rate: 30fps
- Audio: 48kHz, 320kbps AAC
- Format: MP4 (H.264)

---

# POST-PRODUCTION NOTES

## Editing Requirements:
- [ ] Audio levels normalized
- [ ] Background noise removed
- [ ] Visual transitions smooth
- [ ] Text overlays readable
- [ ] Color grading applied

## Quality Check:
- [ ] Video resolution verified
- [ ] Audio quality confirmed
- [ ] Content accuracy reviewed
- [ ] Brand consistency maintained

EOF
}

# Function to extract audio script
extract_audio_script() {
    local input_file="$1"
    local output_file="$2"
    
    echo "🎙️ Extracting audio script: $(basename "$input_file")"
    
    # Extract narrator text and timing information
    grep -A 100 "## Video Script" "$input_file" | \
    sed -n '/\*\*NARRATOR\*\*:/,/^$/p' | \
    sed 's/\*\*NARRATOR\*\*: //' | \
    sed '/^$/d' > "$output_file"
    
    # Add timing estimates
    local word_count=$(wc -w < "$output_file")
    local estimated_time=$((word_count / 150))
    
    cat >> "$output_file" << EOF

---

# AUDIO RECORDING SPECIFICATIONS

Word Count: $word_count words
Estimated Recording Time: $estimated_time minutes
Target Speaking Rate: 150-160 words per minute

Recording Notes:
- Maintain consistent energy level
- Pause appropriately at punctuation
- Emphasize key technical terms
- Speak clearly for international audience

Audio Processing Chain:
1. High-pass filter (80Hz)
2. Noise gate (-40dB threshold)
3. Compressor (3:1 ratio)
4. EQ enhancement
5. Limiter (-3dB threshold)

EOF
}

# Function to create visual notes
create_visual_notes() {
    local input_file="$1"
    local output_file="$2"
    
    echo "🎨 Creating visual notes: $(basename "$input_file")"
    
    # Extract visual cues and timing
    grep -A 100 "## Video Script" "$input_file" | \
    grep "\*\*VISUAL\*\*:" | \
    sed 's/\*\*VISUAL\*\*: //' > "$output_file"
    
    # Add visual production notes
    cat >> "$output_file" << EOF

---

# VISUAL PRODUCTION NOTES

## Screen Recording Setup:
- Resolution: 1920x1080
- Frame Rate: 30fps
- Cursor: Highlighted and smoothed
- Transitions: Professional fade effects

## Animation Requirements:
- Smooth CSS animations
- Consistent timing (0.3s-0.5s)
- Professional easing functions
- Brand-consistent colors

## Screenshot Guidelines:
- Clean, uncluttered interfaces
- Highlighted important elements
- Consistent window sizes
- Professional cropping

## Text Overlay Specifications:
- Font: Sans-serif, readable size
- Color: High contrast with background
- Position: Safe title area
- Duration: Allow reading time

## Brand Consistency:
- ShareConnect color scheme
- Professional typography
- Consistent iconography
- Unified visual style

EOF
}

# Function to create lesson structure
create_lesson_structure() {
    local course_name="$1"
    local lesson_name="$2"
    local input_script="$3"
    
    echo "📚 Creating lesson structure: $course_name - $lesson_name"
    
    local lesson_dir="$CONTENT_DIR/lessons/$course_name/$lesson_name"
    mkdir -p "$lesson_dir"
    
    # Convert to teleprompter format
    convert_to_teleprompter \
        "$input_script" \
        "$lesson_dir/teleprompter.txt" \
        "$lesson_name"
    
    # Extract audio script
    extract_audio_script \
        "$input_script" \
        "$lesson_dir/audio-script.txt"
    
    # Create visual notes
    create_visual_notes \
        "$input_script" \
        "$lesson_dir/visual-notes.txt"
    
    # Create lesson metadata
    cat > "$lesson_dir/metadata.json" << EOF
{
    "course": "$course_name",
    "lesson": "$lesson_name",
    "created_date": "$(date -Iseconds)",
    "duration_estimate": "$(wc -w < "$input_script" | awk '{print int($1/150)}') minutes",
    "word_count": $(wc -w < "$input_script"),
    "difficulty": "beginner",
    "tags": ["shareconnect", "tutorial", "installation"],
    "status": "script_ready",
    "dependencies": [],
    "assets_required": [
        "app screenshots",
        "logo animations",
        "workflow diagrams"
    ]
}
EOF
}

# Function to generate recording checklist
generate_recording_checklist() {
    local lesson_dir="$1"
    
    echo "✅ Generating recording checklist"
    
    cat > "$lesson_dir/recording-checklist.md" << 'EOF'
# Recording Checklist

## Pre-Recording Setup
- [ ] Audio equipment tested and configured
- [ ] Video settings verified (resolution, frame rate)
- [ ] Screen recording software ready
- [ ] Teleprompter script loaded
- [ ] Visual assets prepared
- [ ] Recording environment quiet

## Technical Checks
- [ ] Microphone levels optimal (-12dB peaks)
- [ ] No background noise detected
- [ ] Screen resolution set correctly
- [ ] Cursor highlighting enabled
- [ ] Recording space has adequate lighting

## Content Preparation
- [ ] Script reviewed and timed
- [ ] Key terms pronunciation verified
- [ ] Transitions planned and rehearsed
- [ ] Demo steps practiced
- [ ] Visual cues synchronized

## During Recording
- [ ] Speak clearly and at consistent pace
- [ ] Maintain energy and engagement
- [ ] Follow script while sounding natural
- [ ] Allow adequate pauses
- [ ] Monitor audio levels continuously

## Post-Recording
- [ ] Backup original recording
- [ ] Check audio quality
- [ ] Verify video resolution
- [ ] Review for any technical issues
- [ ] Document any retakes needed

## Quality Assurance
- [ ] Audio levels normalized
- [ ] Background noise removed
- [ ] Visual transitions smooth
- [ ] Text overlays readable
- [ ] Color grading applied
EOF
}

# Main content creation function
process_course_content() {
    local course_name="$1"
    local course_dir="$COURSES_DIR/$course_name"
    
    echo "🚀 Processing course content: $course_name"
    
    if [ ! -d "$course_dir" ]; then
        echo "❌ Course directory not found: $course_dir"
        return 1
    fi
    
    # Process each lesson
    for lesson_script in "$course_dir"/scripts/*.md; do
        if [ -f "$lesson_script" ]; then
            local lesson_name=$(basename "$lesson_script" .md)
            echo "📖 Processing lesson: $lesson_name"
            
            create_lesson_structure "$course_name" "$lesson_name" "$lesson_script"
            generate_recording_checklist "$CONTENT_DIR/lessons/$course_name/$lesson_name"
            
            echo "✅ Completed: $lesson_name"
        fi
    done
    
    # Create course summary
    create_course_summary "$course_name"
}

# Function to create course summary
create_course_summary() {
    local course_name="$1"
    local course_dir="$COURSES_DIR/$course_name"
    
    echo "📊 Creating course summary: $course_name"
    
    local total_lessons=$(ls -1 "$course_dir"/scripts/*.md 2>/dev/null | wc -l)
    local total_words=$(cat "$course_dir"/scripts/*.md | wc -w)
    local total_duration=$((total_words / 150))
    
    cat > "$CONTENT_DIR/course-summaries/$course_name-summary.md" << EOF
# Course Summary: $course_name

## Overview
- **Total Lessons**: $total_lessons
- **Total Word Count**: $total_words words
- **Estimated Total Duration**: $total_duration minutes
- **Average Lesson Duration**: $((total_duration / total_lessons)) minutes

## Content Breakdown
$(for script in "$course_dir"/scripts/*.md; do
    lesson_name=$(basename "$script" .md)
    word_count=$(wc -w < "$script")
    duration=$((word_count / 150))
    echo "- **$lesson_name**: $word_count words (~$duration minutes)"
done)

## Production Status
- [ ] Scripts converted to teleprompter format
- [ ] Audio scripts extracted
- [ ] Visual notes created
- [ ] Recording checklists generated
- [ ] Assets identified and prepared

## Next Steps
1. Review and approve all scripts
2. Prepare visual assets
3. Set up recording environment
4. Record all lessons
5. Post-production editing
6. Quality assurance review
7. Publish and distribute

## Metadata
- **Created**: $(date -Iseconds)
- **Last Updated**: $(date -Iseconds)
- **Status**: Script preparation complete
- **Priority**: High
- **Dependencies**: Asset preparation, recording setup
EOF
}

# Function to validate content
validate_content() {
    local input_file="$1"
    
    echo "🔍 Validating content: $(basename "$input_file")"
    
    local issues=0
    
    # Check for required sections
    if ! grep -q "## Video Script" "$input_file"; then
        echo "❌ Missing 'Video Script' section"
        issues=$((issues + 1))
    fi
    
    if ! grep -q "## Visual Assets Needed" "$input_file"; then
        echo "❌ Missing 'Visual Assets Needed' section"
        issues=$((issues + 1))
    fi
    
    # Check for timing information
    if ! grep -q "Duration" "$input_file"; then
        echo "⚠️  Missing duration information"
        issues=$((issues + 1))
    fi
    
    # Check for narrator text
    narrator_count=$(grep -c "^\*\*NARRATOR\*\*:" "$input_file")
    if [ "$narrator_count" -lt 5 ]; then
        echo "⚠️  Limited narrator content ($narrator_count sections)"
        issues=$((issues + 1))
    fi
    
    # Check word count
    word_count=$(wc -w < "$input_file")
    if [ "$word_count" -lt 500 ]; then
        echo "⚠️  Short content ($word_count words)"
        issues=$((issues + 1))
    fi
    
    if [ "$issues" -eq 0 ]; then
        echo "✅ Content validation passed"
        return 0
    else
        echo "⚠️  Found $issues issues - review recommended"
        return 1
    fi
}

# Main function
main() {
    echo "🚀 Starting ShareConnect video content creation pipeline"
    
    # Create necessary directories
    mkdir -p "$CONTENT_DIR"/{teleprompter,audio-scripts,visual-notes,lessons,course-summaries}
    
    # Process the installation course
    if [ -d "$COURSES_DIR/installation-setup" ]; then
        echo "📚 Processing Installation & Setup course"
        process_course_content "installation-setup"
    fi
    
    # Validate all scripts
    echo "🔍 Validating all course scripts..."
    for script in "$COURSES_DIR"/*/scripts/*.md; do
        if [ -f "$script" ]; then
            validate_content "$script"
        fi
    done
    
    echo ""
    echo "✅ Content creation pipeline completed!"
    echo ""
    echo "📁 Content files created in: $CONTENT_DIR"
    echo ""
    echo "Next steps:"
    echo "1. Review teleprompter scripts in: $CONTENT_DIR/teleprompter/"
    echo "2. Prepare visual assets based on: $CONTENT_DIR/visual-notes/"
    echo "3. Set up recording environment"
    echo "4. Begin recording process"
    echo ""
    echo "For professional recording:"
    echo "- Set up OBS with: $PROJECT_ROOT/VideoProduction/setup/setup-obs.sh"
    echo "- Configure audio with: $PROJECT_ROOT/VideoProduction/setup/setup-audio.sh"
    echo "- Test recording workflow: $PROJECT_ROOT/VideoProduction/pipeline/recording-workflow.sh"
}

# Run main function if script is executed directly
if [[ "${BASH_SOURCE[0]}" == "${0}" ]]; then
    main "$@"
fi