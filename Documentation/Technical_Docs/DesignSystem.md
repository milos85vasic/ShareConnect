# DesignSystem

## Overview and Purpose

DesignSystem is a unified design system library that provides consistent UI components, theming, and animations across all ShareConnect applications. It implements Material Design 3 principles with custom extensions for enhanced user experience. The system ensures visual consistency and provides reusable components for rapid development.

## Architecture and Components

DesignSystem follows a modular architecture with both View-based and Compose-based implementations:

### Core Components
- **DesignSystemTheme**: Unified theming system
- **DesignSystemComponent**: Base component interface
- **Animated Components**: Enhanced components with animations
- **Shape System**: Consistent shape definitions
- **Typography System**: Unified text styling

### Component Categories
- **Buttons**: AnimatedButton, FAB components
- **Cards**: AnimatedCard for content display
- **Dialogs**: AnimatedDialog for modal interactions
- **Inputs**: AnimatedInputField for data entry
- **Animations**: LottieAnimationView for complex animations

## API Reference

### DesignSystemTheme
```kotlin
object DesignSystemTheme {
    val colors: DesignSystemColors
    val typography: DesignSystemTypography
    val shapes: DesignSystemShapes

    @Composable
    fun ProvideDesignSystemTheme(
        theme: ThemeData,
        content: @Composable () -> Unit
    )
}
```

### AnimatedFAB
```kotlin
@Composable
fun AnimatedFAB(
    onClick: () -> Unit,
    icon: ImageVector,
    text: String? = null,
    extended: Boolean = false,
    modifier: Modifier = Modifier
)
```

### AnimatedCard
```kotlin
@Composable
fun AnimatedCard(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
)
```

## Key Classes and Their Responsibilities

### DesignSystemTheme
- **Responsibilities**:
  - Theme data application to Compose UI
  - Color scheme management
  - Typography scale definition
  - Shape system coordination
  - Dynamic theme switching

### AnimatedFAB
- **Responsibilities**:
  - Floating action button with expand/collapse animations
  - Icon and text state management
  - Accessibility support
  - Material Design 3 compliance

### AnimatedCard
- **Responsibilities**:
  - Card container with elevation animations
  - Click state handling
  - Content layout management
  - Shadow and border animations

### AnimatedDialog
- **Responsibilities**:
  - Modal dialog with enter/exit animations
  - Backdrop blur effects
  - Content scaling and positioning
  - Dismiss handling

## Data Models

### DesignSystemColors
```kotlin
data class DesignSystemColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val error: Color,
    val onError: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color
)
```

### DesignSystemTypography
```kotlin
data class DesignSystemTypography(
    val displayLarge: TextStyle,
    val displayMedium: TextStyle,
    val displaySmall: TextStyle,
    val headlineLarge: TextStyle,
    val headlineMedium: TextStyle,
    val headlineSmall: TextStyle,
    val titleLarge: TextStyle,
    val titleMedium: TextStyle,
    val titleSmall: TextStyle,
    val bodyLarge: TextStyle,
    val bodyMedium: TextStyle,
    val bodySmall: TextStyle,
    val labelLarge: TextStyle,
    val labelMedium: TextStyle,
    val labelSmall: TextStyle
)
```

### DesignSystemShapes
```kotlin
data class DesignSystemShapes(
    val extraSmall: CornerBasedShape,
    val small: CornerBasedShape,
    val medium: CornerBasedShape,
    val large: CornerBasedShape,
    val extraLarge: CornerBasedShape
)
```

## Usage Examples

### Applying Design System Theme
```kotlin
@Composable
fun MyApp() {
    val themeData = remember { getCurrentTheme() }

    DesignSystemTheme.ProvideDesignSystemTheme(theme = themeData) {
        // Your app content here
        MainScreen()
    }
}
```

### Using Animated Components
```kotlin
@Composable
fun ProfileScreen() {
    Column {
        AnimatedCard(onClick = { /* handle click */ }) {
            Text("Profile Card")
        }

        Spacer(modifier = Modifier.height(16.dp))

        AnimatedFAB(
            onClick = { /* add new profile */ },
            icon = Icons.Default.Add,
            text = "Add Profile"
        )
    }
}
```

### Custom Component Implementation
```kotlin
@Composable
fun CustomProfileCard(
    profile: ServerProfile,
    onClick: () -> Unit
) {
    AnimatedCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile icon
            Icon(
                imageVector = getServiceIcon(profile.serviceType),
                contentDescription = null,
                modifier = Modifier.size(48.dp)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = profile.name,
                    style = DesignSystemTheme.typography.titleMedium
                )
                Text(
                    text = "${profile.host}:${profile.port}",
                    style = DesignSystemTheme.typography.bodyMedium,
                    color = DesignSystemTheme.colors.onSurfaceVariant
                )
            }
        }
    }
}
```

## Dependencies

### Compose Dependencies
- `androidx.compose.material3:material3:1.2.1` - Material Design 3
- `androidx.compose.ui:ui:1.6.8` - Compose UI core
- `androidx.compose.ui:ui-tooling:1.6.8` - UI tooling
- `androidx.compose.runtime:runtime:1.6.8` - Compose runtime

### Animation Dependencies
- `com.airbnb.android:lottie-compose:6.5.0` - Lottie animations

### Android Framework
- `androidx.core:core-ktx:1.12.0` - Core extensions
- `androidx.activity:activity-compose:1.9.3` - Activity integration

### Testing Dependencies
- `junit:junit:4.13.2` - Unit testing
- `androidx.compose.ui:ui-test-junit4:1.6.8` - Compose testing

---

*For more information, visit [https://shareconnect.org/docs/designsystem](https://shareconnect.org/docs/designsystem)*