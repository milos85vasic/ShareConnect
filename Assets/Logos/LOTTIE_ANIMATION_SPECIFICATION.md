# Lottie Animation Specification for ShareConnect Icons

## Overview
Lottie animations for splash screens that bring the ShareConnect icon family to life with engaging, consistent animations across all applications.

## Animation Design Principles

### Core Animation Patterns
1. **Entrance Animation**: Smooth reveal of the icon
2. **Idle Animation**: Subtle breathing/pulsing effect
3. **Exit Animation**: Clean transition out
4. **Duration**: 2-3 seconds total per animation

### Visual Consistency
- **Timing**: Consistent easing curves across all animations
- **Scale**: Uniform animation scales
- **Colors**: Maintain brand color palette
- **Performance**: Optimized for smooth 60fps playback

## Animation Specifications

### 1. ShareConnector Animation
**Concept**: Network connectivity coming to life
**Animation Sequence**:
1. Central node pulses into existence
2. Satellite nodes appear sequentially
3. Connection lines draw between nodes
4. Subtle glow effect on all nodes
5. Continuous gentle pulsing

### 2. TransmissionConnect Animation
**Concept**: Butterfly taking flight
**Animation Sequence**:
1. Butterfly silhouette fades in
2. Wings unfold with mechanical precision
3. Gear teeth rotate slowly
4. Download arrows pulse
5. Gentle wing flapping motion

### 3. uTorrentConnect Animation
**Concept**: µ symbol with dynamic download
**Animation Sequence**:
1. µ symbol draws from center
2. Download arrow fills with progress
3. Torrent pieces orbit around symbol
4. Speed indicators pulse
5. Continuous orbiting motion

### 4. qBitConnect Animation
**Concept**: Q letter assembling with torrent pieces
**Animation Sequence**:
1. Q outline draws
2. Torrent pieces fly into position
3. Pieces click into place
4. Speed indicators animate
5. Subtle rotation of inner pieces

### 5. JDownloaderConnect Animation
**Concept**: Download manager in action
**Animation Sequence**:
1. Download arrow draws
2. Packages stack below arrow
3. Progress bar fills
4. Container symbols appear
5. Continuous progress animation

### 6. PlexConnect Animation
**Concept**: Media waves flowing
**Animation Sequence**:
1. Play triangle appears
2. Media waves ripple outward
3. Film, music, photo symbols fade in
4. Continuous wave motion
5. Subtle color shifts

### 7. HomeAssistantConnect Animation
**Concept**: Smart home waking up
**Animation Sequence**:
1. House outline appears
2. Devices light up sequentially
3. Connection lines draw
4. Devices pulse with activity
5. Continuous gentle pulsing

### 8. PortainerConnect Animation
**Concept**: Container stack building
**Animation Sequence**:
1. Docker whale appears
2. Containers stack on whale
3. Networking waves animate
4. Containers pulse with activity
5. Continuous wave motion

### 9. JellyfinConnect Animation
**Concept**: Jellyfish swimming
**Animation Sequence**:
1. Jellyfish body appears
2. Tentacles extend with media symbols
3. Streaming waves flow
4. Gentle swimming motion
5. Continuous tentacle movement

### 10. NetdataConnect Animation
**Concept**: Live dashboard metrics
**Animation Sequence**:
1. Dashboard outline appears
2. Graphs animate with real data
3. Metrics update in sequence
4. Alert indicators pulse
5. Continuous graph animation

### 11. WireGuardConnect Animation
**Concept**: Secure connection establishing
**Animation Sequence**:
1. Shield outline appears
2. Lock symbol animates into place
3. Network waves radiate outward
4. Secure connection lines draw
5. Continuous wave motion

### 12. MatrixConnect Animation
**Concept**: Chat platform coming online
**Animation Sequence**:
1. Chat bubble matrix forms
2. Message flow lines animate
3. Encryption symbols appear
4. Continuous message flow
5. Subtle matrix effect

### 13. GiteaConnect Animation
**Concept**: Code repository syncing
**Animation Sequence**:
1. Git branch draws
2. Code symbols appear
3. Version indicators animate
4. Continuous sync motion
5. Subtle branch movement

### 14. OnlyOfficeConnect Animation
**Concept**: Document creation process
**Animation Sequence**:
1. Document outline appears
2. Editing tools animate in
3. Content symbols appear
4. Collaboration indicators pulse
5. Continuous tool animation

### 15. MinecraftServerConnect Animation
**Concept**: Game server starting up
**Animation Sequence**:
1. Minecraft block appears
2. Server gear rotates
3. Player indicators animate
4. Block texture animates
5. Continuous gear rotation

### 16. SeafileConnect Animation
**Concept**: File synchronization process
**Animation Sequence**:
1. Cloud outline appears
2. Sync arrows animate
3. File symbols appear
4. Version history indicators
5. Continuous sync motion

### 17. SyncthingConnect Animation
**Concept**: Real-time file sync
**Animation Sequence**:
1. Two folders appear
2. Bidirectional arrows animate
3. Sync indicators pulse
4. Real-time update effects
5. Continuous sync motion

### 18. DuplicatiConnect Animation
**Concept**: Backup process
**Animation Sequence**:
1. Backup vault appears
2. Data streams flow in
3. Recovery symbols animate
4. Progress indicators
5. Continuous data flow

### 19. PaperlessNGConnect Animation
**Concept**: Document scanning process
**Animation Sequence**:
1. Scanner outline appears
2. OCR text recognition effect
3. Filing system animates
4. Document processing
5. Continuous scan effect

### 20. MotrixConnect Animation
**Concept**: Download manager interface
**Animation Sequence**:
1. Manager interface appears
2. Speed indicators animate
3. Queue management symbols
4. Progress animations
5. Continuous speed display

## Technical Specifications

### Animation Properties
- **Format**: Lottie JSON (.json)
- **Duration**: 2000-3000ms
- **Framerate**: 60fps
- **Resolution**: 512x512 (optimized for mobile)
- **File Size**: <100KB per animation

### Performance Optimization
- **Layer Count**: Keep under 20 layers
- **Shapes**: Use simple vector shapes
- **Gradients**: Limit complex gradients
- **Masks**: Use sparingly
- **Expressions**: Avoid complex expressions

### Color Palette
- **Background**: Transparent
- **Primary**: #4FC3F7 (Blue)
- **Secondary**: #FFFFFF (White)
- **Accents**: Application-specific colors

## Implementation Guidelines

### Splash Screen Integration
```kotlin
// Android implementation
val animationView = findViewById<LottieAnimationView>(R.id.animation_view)
animationView.setAnimation("splash_animation.json")
animationView.playAnimation()

animationView.addAnimatorListener(object : Animator.AnimatorListener {
    override fun onAnimationStart(animation: Animator) {}
    override fun onAnimationEnd(animation: Animator) {
        // Start main activity
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }
    override fun onAnimationCancel(animation: Animator) {}
    override fun onAnimationRepeat(animation: Animator) {}
})
```

### File Organization
```
Assets/Logos/Lottie/
├── ShareConnector/
│   ├── splash_animation.json
│   └── preview.gif
├── TransmissionConnect/
│   ├── splash_animation.json
│   └── preview.gif
└── [Other Applications...]
```

### Naming Convention
- `splash_animation.json` - Main Lottie file
- `icon_animation.json` - Alternative icon animation
- `loading_animation.json` - Loading state animation

## Creation Workflow

### 1. Design in After Effects
- Create compositions for each animation
- Use shape layers and minimal effects
- Export using Bodymovin extension

### 2. Optimize for Lottie
- Simplify complex shapes
- Reduce layer count
- Test on target devices

### 3. Integration Testing
- Test on Android devices
- Verify performance
- Check memory usage
- Ensure smooth playback

## Quality Assurance

### Animation Quality
- [ ] Smooth 60fps playback
- [ ] Consistent timing across apps
- [ ] Clear visual hierarchy
- [ ] Appropriate duration

### Technical Quality
- [ ] File size under 100KB
- [ ] No performance issues
- [ ] Proper layer organization
- [ ] Cross-platform compatibility

### Brand Consistency
- [ ] Matches icon design
- [ ] Uses correct colors
- [ ] Consistent animation style
- [ ] Professional appearance

This specification ensures all Lottie animations maintain visual consistency while providing engaging, application-specific animations for splash screens across the ShareConnect ecosystem.