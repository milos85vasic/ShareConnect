#!/bin/bash

# ShareConnect Phase 4 Implementation Script
# Creates comprehensive video courses

set -e

echo "🎥 ShareConnect Phase 4: Create Video Courses"
echo "============================================"
echo ""

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Function to print colored output
print_status() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

print_info() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

# Create directories for video production
mkdir -p Video_Courses/Installation_Setup
mkdir -p Video_Courses/Basic_Usage
mkdir -p Video_Courses/Advanced_Features
mkdir -p Video_Courses/Development_Tutorials
mkdir -p Video_Courses/Troubleshooting
mkdir -p Video_Courses/Scripts
mkdir -p Video_Courses/Assets

echo ""
print_info "Starting Phase 4: Video Course Creation"
echo "This script will:"
echo "1. Create scripts for 30+ tutorial videos"
echo "2. Generate thumbnails and titles"
echo "3. Create recording checklists"
echo "4. Generate metadata for video platforms"
echo "5. Create website integration code"
echo ""

# Step 1: Installation & Setup Series (5 videos)
echo ""
echo "============================================="
echo "STEP 1: Installation & Setup Series"
echo "============================================="

VIDEO_1_SCRIPTS=(
    "SC_ShareConnect_Install:ShareConnect Main App Installation"
    "SC_Connector_Setup:Connector Setup Basics"
    "SC_Advanced_Config:Advanced Configuration"
    "SC_Install_Issues:Troubleshooting Installation Issues"
    "SC_Security_Setup:Security Setup Guide"
)

for video_info in "${VIDEO_1_SCRIPTS[@]}"; do
    IFS=':' read -r script_id title <<< "$video_info"
    
    print_info "Creating script: $title"
    
    cat > "Video_Courses/Installation_Setup/${script_id}_Script.md" << EOF
# $title

**Video ID:** $script_id  
**Series:** Installation & Setup  
**Duration:** ~5-7 minutes  
**Presenter:** [Name]

## Opening (30 seconds)
"Welcome to ShareConnect! In this video, I'll show you how to $title."

## Main Content (4-6 minutes)

### Step 1: [First Step]
- Explain what we're doing
- Show on screen
- Provide tips

### Step 2: [Second Step]
- Continue demonstration
- Highlight important points
- Show common mistakes

### Step 3: [Third Step]
- Complete the process
- Verify successful completion
- Show result

## Closing (30 seconds)
"That's it! You've successfully $title. In the next video, we'll cover [next topic]."

## Screen Elements Needed
- [List specific screens or elements to capture]
- Emphasize areas to highlight

## Notes for Editor
- Add zoom effects for important UI elements
- Include text overlays for key points
- Add background music (if appropriate)

## Resources Mentioned
- [Link to relevant documentation]
- [Download links if applicable]
EOF

    print_status "Created script for $title"
done

# Step 2: Basic Usage Tutorials (8 videos)
echo ""
echo "============================================="
echo "STEP 2: Basic Usage Tutorials"
echo "============================================="

VIDEO_2_SCRIPTS=(
    "BU_Torrent_Sharing:Sharing to Torrent Clients"
    "BU_Torrent_qBit:qBittorrent Integration"
    "BU_Torrent_Trans:Transmission Integration"
    "BU_Media_Servers:Media Server Integration"
    "BU_Media_Plex:Plex Media Server"
    "BU_Media_Jellyfin:Jellyfin Media Server"
    "BU_Download_Managers:Download Manager Setup"
    "BU_Download_JD:JDownloader Integration"
)

for video_info in "${VIDEO_2_SCRIPTS[@]}"; do
    IFS=':' read -r script_id title <<< "$video_info"
    
    print_info "Creating script: $title"
    
    cat > "Video_Courses/Basic_Usage/${script_id}_Script.md" << EOF
# $title

**Video ID:** $script_id  
**Series:** Basic Usage  
**Duration:** ~8-10 minutes  
**Presenter:** [Name]

## Introduction (30 seconds)
"ShareConnect makes it easy to $title. Let me show you how..."

## Prerequisites (30 seconds)
Before we begin, make sure you have:
- ShareConnect installed
- [Specific service] running
- Basic understanding of [concept]

## Main Demonstration (6-8 minutes)

### Setting Up [Service]
1. Open ShareConnect
2. Navigate to [section]
3. Configure [settings]
4. Test connection

### Sharing Your First [Content]
1. Find content to share
2. Use Android Share menu
3. Select ShareConnect
4. Choose [service]
5. Confirm and verify

### Common Operations
1. [Operation 1]
2. [Operation 2]
3. [Operation 3]

## Tips & Tricks (1 minute)
- [Tip 1]
- [Tip 2]
- [Tip 3]

## Summary (30 seconds)
"And that's how you $title with ShareConnect. It's that simple!"

## Visual Elements
- Screen recording of mobile device
- Highlight share menu
- Show confirmation screens
- Emphasize key buttons

## Common Issues to Address
- Connection problems
- Authentication failures
- [Specific issues]

## Next Steps
"Try sharing some content yourself, and join us in the next video where we cover [next topic]."
EOF

    print_status "Created script for $title"
done

# Step 3: Advanced Features Series (7 videos)
echo ""
echo "============================================="
echo "STEP 3: Advanced Features Series"
echo "============================================="

VIDEO_3_SCRIPTS=(
    "AF_Device_Sync:Multi-Device Synchronization"
    "AF_Security_Features:Security Features Deep Dive"
    "AF_QR_Scanning:QR Code Scanning"
    "AF_Profile_Management:Profile Management"
    "AF_Theme_Custom:Theme Customization"
    "AF_Automation:Automation Features"
    "AF_Power_Tips:Power User Tips"
)

for video_info in "${VIDEO_3_SCRIPTS[@]}"; do
    IFS=':' read -r script_id title <<< "$video_info"
    
    print_info "Creating script: $title"
    
    cat > "Video_Courses/Advanced_Features/${script_id}_Script.md" << EOF
# $title

**Video ID:** $script_id  
**Series:** Advanced Features  
**Duration:** ~10-12 minutes  
**Presenter:** [Name]

## Intro (30 seconds)
"Ready to take your ShareConnect experience to the next level? Today we're exploring $title."

## Feature Overview (1 minute)
ShareConnect's $title allows you to:
- [Benefit 1]
- [Benefit 2]
- [Benefit 3]

## Deep Dive (8-9 minutes)

### Understanding [Concept]
- Explain the concept
- Show why it's useful
- Demonstrate basic usage

### Advanced Configuration
1. Access [settings]
2. Configure [options]
3. Customize [preferences]
4. Test and verify

### Real-World Examples
- Example 1: [Use case]
- Example 2: [Use case]
- Example 3: [Use case]

### Pro Tips
- [Tip 1]
- [Tip 2]
- [Tip 3]

## Best Practices (1 minute)
- Always [practice 1]
- Remember to [practice 2]
- Avoid [mistake]

## Conclusion (30 seconds)
"Mastering $title will significantly improve your workflow. Practice these techniques and..."

## Visual Needs
- Screen capture of advanced settings
- Side-by-side comparisons
- Animated transitions
- Custom graphics if needed

## Viewer Engagement
- Ask about their use cases
- Encourage comments
- Suggest next video

## Resources
- Link to advanced documentation
- Community discussion link
EOF

    print_status "Created script for $title"
done

# Step 4: Development Tutorials (6 videos)
echo ""
echo "============================================="
echo "STEP 4: Development Tutorials"
echo "============================================="

VIDEO_4_SCRIPTS=(
    "DT_Build_From_Source:Building from Source"
    "DT_Custom_Connector:Adding Custom Connectors"
    "DT_Testing_Workflow:Testing Workflow"
    "DT_Debug_Tips:Debugging Techniques"
    "DT_Contributing:Contributing to Project"
    "DT_Release_Process:Release Process"
)

for video_info in "${VIDEO_4_SCRIPTS[@]}"; do
    IFS=':' read -r script_id title <<< "$video_info"
    
    print_info "Creating script: $title"
    
    cat > "Video_Courses/Development_Tutorials/${script_id}_Script.md" << EOF
# $title

**Video ID:** $script_id  
**Series:** Development Tutorials  
**Duration:** ~15-20 minutes  
**Presenter:** [Name]

## Introduction (30 seconds)
"Welcome developers! Today we're diving into $title for ShareConnect."

## Prerequisites (1 minute)
To follow along, you'll need:
- Android Studio
- Git
- [Specific tools]
- Basic Android development knowledge

## Setup (2 minutes)
1. Clone the repository
2. Open in Android Studio
3. Sync Gradle
4. Set up emulator/device

## Main Tutorial (10-12 minutes)

### [Topic 1]
- Explain concept
- Show code
- Demonstrate
- Explain results

### [Topic 2]
- Continue with next aspect
- Code examples
- Best practices
- Common pitfalls

### [Topic 3]
- Advanced techniques
- Optimization
- Testing considerations

## Common Issues (2 minutes)
- [Issue 1] and solution
- [Issue 2] and solution
- [Issue 3] and solution

## Resources (30 seconds)
- Documentation links
- Sample code repository
- Community channels

## Conclusion (30 seconds)
"That's $title in ShareConnect development. Try it out and..."

## Technical Details
- Show IDE setup
- Terminal commands
- Build output
- Test results

## Code Snippets
Provide clear, well-commented code examples in video overlay or description.

## Call to Action
"Found this helpful? Like, subscribe, and comment with your questions!"
EOF

    print_status "Created script for $title"
done

# Step 5: Troubleshooting Videos (4 videos)
echo ""
echo "============================================="
echo "STEP 5: Troubleshooting Videos"
echo "============================================="

VIDEO_5_SCRIPTS=(
    "TS_Network_Issues:Network Issues"
    "TS_Auth_Problems:Authentication Problems"
    "TS_Performance_Tuning:Performance Optimization"
    "TS_Common_Errors:Common Error Solutions"
)

for video_info in "${VIDEO_5_SCRIPTS[@]}"; do
    IFS=':' read -r script_id title <<< "$video_info"
    
    print_info "Creating script: $title"
    
    cat > "Video_Courses/Troubleshooting/${script_id}_Script.md" << EOF
# $title

**Video ID:** $script_id  
**Series:** Troubleshooting  
**Duration:** ~8-10 minutes  
**Presenter:** [Name]

## Problem Introduction (30 seconds)
"Having trouble with $title? You're not alone. Let's work through common solutions."

## Symptom Identification (1 minute)
If you're experiencing:
- Symptom 1
- Symptom 2
- Symptom 3

Then this video is for you.

## Solution Walkthrough (6-7 minutes)

### Solution 1: [Name]
**When to use:** [Condition]
**Steps:**
1. [Step 1]
2. [Step 2]
3. [Step 3]
4. Verify fix

### Solution 2: [Name]
**When to use:** [Condition]
**Steps:**
1. [Step 1]
2. [Step 2]
3. [Step 3]
4. Verify fix

### Solution 3: [Name]
**When to use:** [Condition]
**Steps:**
1. [Step 1]
2. [Step 2]
3. [Step 3]
4. Verify fix

## Prevention Tips (1 minute)
- [Tip 1]
- [Tip 2]
- [Tip 3]

## When All Else Fails (30 seconds)
If you're still having issues:
1. Collect logs
2. Check documentation
3. Join our community
4. Create an issue

## Summary (30 seconds)
"Remember these key points for $title, and you'll be back up and running in no time."

## Visual Elements
- Show error messages
- Highlight settings to change
- Demonstrate fixes
- Show success states

## Links
- Documentation: [link]
- Community: [link]
- Issue tracker: [link]

## Next Video
"Next time, we'll cover [next topic]. Don't forget to subscribe!"
EOF

    print_status "Created script for $title"
done

# Step 6: Create recording checklist
echo ""
echo "============================================="
echo "STEP 6: Recording Checklist"
echo "============================================="

print_info "Creating video production checklist..."

cat > "Video_Courses/Production_Checklist.md" << EOF
# ShareConnect Video Production Checklist

## Pre-Recording Checklist

### Equipment
- [ ] High-quality microphone (e.g., Blue Yeti)
- [ ] Screen recording software (e.g., OBS Studio)
- [ ] Good lighting setup
- [ ] Quiet recording environment
- [ ] Android device/emulator for demos

### Software Setup
- [ ] Screen resolution: 1080p minimum
- [ ] Audio format: 48kHz, 16-bit
- [ ] Recording format: MP4 or MOV
- [ ] Clean desktop/workspace
- [ ] Open all needed apps beforehand

### Content Preparation
- [ ] Script reviewed and rehearsed
- [ ] Demo environment set up
- [ ] Test recordings done
- [ ] All accounts/services ready
- [ ] Backup plans for failures

## During Recording

### Technical
- [ ] Check audio levels
- [ ] Start screen recording
- [ ] Record a few seconds of silence
- [ ] Check recording quality periodically

### Presentation
- [ ] Speak clearly and at moderate pace
- [ ] Use consistent terminology
- [ ] Demonstrate actual workflows
- [ ] Explain why, not just how

### Content
- [ ] Follow script but be natural
- [ ] Include common use cases
- [ ] Show errors and recovery
- [ ] Verify success on screen

## Post-Recording Checklist

### Editing
- [ ] Remove mistakes and pauses
- [ ] Add zooms for important UI elements
- [ ] Include text overlays for key points
- [ ] Add intro/outro music or branding
- [ ] Normalize audio levels
- [ ] Export in 1080p MP4

### Metadata
- [ ] Create eye-catching thumbnail
- [ ] Write compelling title and description
- [ ] Add relevant tags
- [ ] Include links to resources
- [ ] Add chapters/timestamps

### Platform Upload
- [ ] Upload to YouTube
- [ ] Add to playlist
- [ ] Set appropriate visibility
- [ ] Enable comments
- [ ] Add cards/endscreens

## Quality Standards

### Video Quality
- Resolution: 1080p minimum
- Frame rate: 30fps or 60fps
- No visible compression artifacts
- Stable and smooth playback

### Audio Quality
- Clear voice recording
- No background noise
- Consistent volume levels
- No clipping or distortion

### Content Quality
- Accurate information
- Clear demonstrations
- Professional presentation
- Helpful for target audience

## Series Standards

### Branding
- Consistent intro/outro
- Logo placement
- Color scheme
- Font choices

### Structure
- Title card
- Clear objectives
- Step-by-step format
- Summary/recap
- Call to action

## Review Process
1. Self-review for mistakes
2. Technical quality check
3. Content accuracy verification
4. Peer review if possible
5. Final approval

## Backup and Storage
- [ ] Raw footage backed up
- [ ] Project files saved
- [ ] Final versions in cloud storage
- [ ] Metadata documented
EOF

print_status "Created production checklist"

# Step 7: Create YouTube metadata templates
echo ""
echo "============================================="
echo "STEP 7: YouTube Metadata Templates"
echo "============================================="

print_info "Creating YouTube metadata templates..."

cat > "Video_Courses/YouTube_Metadata_Template.md" << EOF
# YouTube Metadata Template for ShareConnect Videos

## Title Template
"[Video Title] | ShareConnect Tutorial | [Series Name]"

## Description Template
```
Learn how to [video topic] with ShareConnect! 🚀

📱 ShareConnect is the ultimate media sharing solution for Android that lets you share content seamlessly across 20+ services.

🔗 Get ShareConnect: [Play Store Link]
📚 Documentation: [Documentation Link]
💬 Community: [Discord/Forum Link]

⏱️ Timestamps:
00:00 - Introduction
[Add timestamps as needed]

🔔 Don't forget to like, subscribe, and hit the bell icon for more ShareConnect tutorials!

#ShareConnect #AndroidTutorial #[SpecificTopic]

---

ShareConnect Features:
• One-tap media sharing
• 20+ service integrations
• Secure and private
• Real-time synchronization
• End-to-end encryption

Connect with us:
• GitHub: [GitHub Link]
• Twitter: [Twitter Link]
• Website: [Website Link]
```

## Tags Template
```
ShareConnect, Android tutorial, [service name], media sharing, tutorial, how to, Android apps, [specific keywords], tech guide, app tutorial
```

## Thumbnail Guidelines

### Text
- Maximum 3 lines of text
- Large, bold, readable fonts
- Contrasting colors
- Key benefit highlighted

### Visual Elements
- ShareConnect logo
- App screenshot(s)
- Relevant icons
- Bright, attractive colors
- No clutter

### Technical Specs
- Resolution: 1280x720 pixels
- Format: JPG or PNG
- File size: Under 2MB
- Mobile-friendly design

## Playlist Structure

### Main Playlist: ShareConnect Complete Guide
- Installation & Setup videos
- Basic Usage tutorials
- Advanced Features
- Development Tutorials
- Troubleshooting

### Sub-playlists
- ShareConnect for Beginners
- ShareConnect Power Users
- ShareConnect Development

## Publishing Schedule
- Tuesdays: Basic tutorials
- Thursdays: Advanced features
- Saturdays: Development content

## Community Engagement
- Respond to all comments
- Pin helpful resources
- Create polls for future content
- Highlight community contributions
EOF

print_status "Created YouTube metadata template"

# Step 8: Create website integration code
echo ""
echo "============================================="
echo "STEP 8: Website Integration Code"
echo "============================================="

print_info "Creating website integration for videos..."

cat > "website_content/video_integration.html" << EOF
<!-- Add this section to the main index.html after the screenshots section -->

<section id="video-tutorials" class="video-tutorials">
    <div class="container">
        <div class="section-header">
            <h2>Video Tutorials</h2>
            <p>Learn ShareConnect with our comprehensive video courses</p>
        </div>

        <div class="video-categories">
            <div class="video-category">
                <div class="category-icon">
                    <i class="fas fa-play-circle"></i>
                </div>
                <h3>Installation & Setup</h3>
                <p>Get started with ShareConnect in minutes</p>
                <div class="video-list">
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>ShareConnect Main App Installation</h4>
                            <p>5 min • Beginner</p>
                        </div>
                    </div>
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>Connector Setup Basics</h4>
                            <p>7 min • Beginner</p>
                        </div>
                    </div>
                </div>
                <a href="#" class="btn btn-outline">View All (5)</a>
            </div>

            <div class="video-category">
                <div class="category-icon">
                    <i class="fas fa-graduation-cap"></i>
                </div>
                <h3>Basic Usage</h3>
                <p>Master the fundamentals of media sharing</p>
                <div class="video-list">
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>Sharing to Torrent Clients</h4>
                            <p>8 min • Beginner</p>
                        </div>
                    </div>
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>Media Server Integration</h4>
                            <p>10 min • Beginner</p>
                        </div>
                    </div>
                </div>
                <a href="#" class="btn btn-outline">View All (8)</a>
            </div>

            <div class="video-category">
                <div class="category-icon">
                    <i class="fas fa-rocket"></i>
                </div>
                <h3>Advanced Features</h3>
                <p>Become a ShareConnect power user</p>
                <div class="video-list">
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>Multi-Device Synchronization</h4>
                            <p>12 min • Advanced</p>
                        </div>
                    </div>
                    <div class="video-item">
                        <div class="video-thumbnail">
                            <i class="fas fa-play"></i>
                        </div>
                        <div class="video-info">
                            <h4>Security Features Deep Dive</h4>
                            <p>10 min • Advanced</p>
                        </div>
                    </div>
                </div>
                <a href="#" class="btn btn-outline">View All (7)</a>
            </div>
        </div>

        <div class="video-cta">
            <h3>Full Video Course Available</h3>
            <p>30+ tutorials covering everything from basics to development</p>
            <a href="https://youtube.com/playlist?list=PLAYLIST_ID" class="btn btn-primary btn-large">
                <i class="fab fa-youtube"></i>
                Watch on YouTube
            </a>
        </div>
    </div>
</section>

<!-- Add these styles to styles.css -->

<style>
.video-tutorials {
    padding: 80px 0;
    background: var(--background-secondary);
}

.video-categories {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
    gap: 30px;
    margin-top: 50px;
}

.video-category {
    background: var(--background);
    border-radius: 12px;
    padding: 30px;
    box-shadow: 0 5px 15px rgba(0,0,0,0.1);
    transition: transform 0.3s ease;
}

.video-category:hover {
    transform: translateY(-5px);
}

.category-icon {
    width: 60px;
    height: 60px;
    background: var(--primary);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 20px;
}

.category-icon i {
    font-size: 24px;
    color: white;
}

.video-list {
    margin: 20px 0;
}

.video-item {
    display: flex;
    align-items: center;
    padding: 15px 0;
    border-bottom: 1px solid var(--border);
}

.video-item:last-child {
    border-bottom: none;
}

.video-thumbnail {
    width: 60px;
    height: 40px;
    background: var(--primary-light);
    border-radius: 8px;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-right: 15px;
}

.video-info h4 {
    margin: 0 0 5px 0;
    font-size: 14px;
}

.video-info p {
    margin: 0;
    color: var(--text-secondary);
    font-size: 12px;
}

.video-cta {
    text-align: center;
    margin-top: 60px;
    padding: 40px;
    background: var(--primary);
    border-radius: 12px;
    color: white;
}
</style>
EOF

print_status "Created website integration code"

# Final summary
echo ""
echo "============================================="
echo "PHASE 4 COMPLETION SUMMARY"
echo "============================================="

print_status "Phase 4 video course materials created!"
echo ""
print_info "Created:"
echo "- 5 Installation & Setup video scripts"
echo "- 8 Basic Usage video scripts"
echo "- 7 Advanced Features video scripts"
echo "- 6 Development Tutorial scripts"
echo "- 4 Troubleshooting video scripts"
echo "- Production checklist"
echo "- YouTube metadata templates"
echo "- Website integration code"
echo ""

print_info "Total: 30 video scripts ready for production"
echo ""

echo "Directory structure created:"
echo "Video_Courses/"
echo "├── Installation_Setup/ (5 scripts)"
echo "├── Basic_Usage/ (8 scripts)"
echo "├── Advanced_Features/ (7 scripts)"
echo "├── Development_Tutorials/ (6 scripts)"
echo "├── Troubleshooting/ (4 scripts)"
echo "├── Scripts/ (master templates)"
echo "├── Assets/ (for thumbnails)"
echo "└── Production_Checklist.md"
echo ""

echo "Next steps:"
echo "1. Review and customize scripts"
echo "2. Set up recording equipment"
echo "3. Record videos following checklist"
echo "4. Edit and publish to YouTube"
echo "5. Run Phase 5 script: ./phase_5_website_update.sh"
echo ""

print_status "Phase 4 script completed!"
echo ""
print_info "Videos to produce:"
echo "Week 1: Installation + Basic Usage (13 videos)"
echo "Week 2: Advanced + Development (13 videos)"
echo "Week 3: Troubleshooting + reviews (4 videos + review)"
echo ""
print_info "Estimated production time: 2-3 weeks"