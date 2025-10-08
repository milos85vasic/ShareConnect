# ShareConnect Design System

## Overview

The ShareConnect Design System provides a unified set of design tokens, reusable components, and consistent patterns across all ShareConnect applications. Built on Material Design 3 principles, it ensures a cohesive user experience with modern aesthetics and smooth animations.

## Design Tokens

### Colors
- **Primary Brand**: Orange-based color palette (#FF6B35)
- **Semantic Colors**: Success, warning, error, and info states
- **Surface System**: Multi-layered surface colors for depth
- **Typography**: Consistent text colors and contrast ratios

### Spacing
- **4pt Grid System**: Consistent spacing scale (4dp, 8dp, 12dp, 16dp, 20dp, 24dp, 28dp, 32dp)
- **Component Spacing**: Standardized padding and margins

### Typography
- **Material Design 3 Scale**: Display, headline, title, body, and label text styles
- **Responsive Sizing**: Appropriate text sizes for different screen densities

### Elevation & Shadows
- **Material Design 3 Elevation**: 6 levels of elevation (0dp to 12dp)
- **Consistent Shadows**: Proper shadow implementation for depth

## Components

### Buttons
- **AnimatedButton**: Primary, secondary, outlined, and text button styles with ripple effects and state animations
- **FAB (AnimatedFAB)**: Extended and regular FABs with expand/collapse animations

### Cards
- **AnimatedCard**: Elevated, outlined, and filled card styles with hover effects and smooth animations

### Inputs
- **AnimatedInputField**: Floating label inputs with validation states and smooth transitions

### Dialogs
- **AnimatedDialog**: Scale, slide, and fade animations with Material Design 3 styling

### Animations
- **LottieAnimationView**: Wrapper for Lottie animations with consistent playback controls

## Motion & Animation

### Animation Principles
- **Purposeful Motion**: All animations serve a functional purpose
- **Consistent Timing**: Standardized duration curves (150ms, 250ms, 400ms)
- **Easing Curves**: Material Design standard, decelerate, and accelerate curves

### Interaction States
- **Press Feedback**: Immediate visual feedback on touch
- **State Transitions**: Smooth transitions between different component states
- **Loading States**: Consistent loading indicators and progress feedback

## Implementation

### Module Structure
```
DesignSystem/
├── src/main/res/values/
│   ├── colors.xml      # Design tokens and color system
│   ├── dimens.xml      # Spacing, typography, and component dimensions
│   ├── themes.xml      # Material Design 3 themes
│   └── attrs.xml       # Custom component attributes
├── src/main/kotlin/com/shareconnect/designsystem/
│   ├── components/     # Reusable UI components
│   │   ├── buttons/
│   │   ├── cards/
│   │   ├── dialogs/
│   │   ├── inputs/
│   │   └── fabs/
│   └── DesignSystemComponent.kt  # Base interface
└── build.gradle.kts   # Dependencies and configuration
```

### Usage
```kotlin
// In build.gradle.kts
implementation(project(":DesignSystem"))

// In XML layouts
<com.shareconnect.designsystem.components.buttons.AnimatedButton
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    app:dsButtonStyle="primary"
    app:dsText="Click Me" />

// In code
val button = findViewById<AnimatedButton>(R.id.my_button)
button.setOnClickListener { /* handle click */ }
```

## Accessibility

- **Color Contrast**: All text meets WCAG AA standards
- **Touch Targets**: Minimum 44dp touch targets
- **Animation Preferences**: Respects system animation settings
- **Screen Reader Support**: Proper content descriptions and labels

## Performance

- **Efficient Rendering**: Optimized drawing and animation performance
- **Memory Management**: Proper cleanup of animations and resources
- **Battery Conscious**: Animations respect power-saving modes

## Future Enhancements

- **Dark Mode Optimization**: Enhanced dark theme support
- **Dynamic Colors**: Android 12+ dynamic color theming
- **Extended Component Library**: Additional specialized components
- **Animation Presets**: Pre-built animation sequences for common interactions</content>
</xai:function_call">DesignSystem/src/main/res/raw/keep