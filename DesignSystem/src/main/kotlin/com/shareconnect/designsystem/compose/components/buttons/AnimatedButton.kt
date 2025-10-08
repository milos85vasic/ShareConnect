package com.shareconnect.designsystem.compose.components.buttons

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.shareconnect.designsystem.compose.theme.DesignSystemShapes

/**
 * Button style variants
 */
enum class ButtonStyle {
    PRIMARY,
    SECONDARY,
    OUTLINED,
    TEXT
}

/**
 * Button size variants
 */
enum class ButtonSize {
    SMALL,
    MEDIUM,
    LARGE
}

/**
 * Animated button component with ripple effects and modern design.
 * Supports different button styles and interaction states.
 */
@Composable
fun AnimatedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: ButtonStyle = ButtonStyle.PRIMARY,
    size: ButtonSize = ButtonSize.MEDIUM,
    enabled: Boolean = true,
    isLoading: Boolean = false
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed && enabled) 0.95f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "button_scale"
    )

    val (backgroundColor, contentColor, borderColor) = getButtonColors(style, enabled)

    val (minHeight, horizontalPadding, textStyle) = getButtonDimensions(size)

    Box(
        modifier = modifier
            .scale(scale)
            .sizeIn(minHeight = minHeight)
            .clip(DesignSystemShapes.medium)
            .background(
                color = backgroundColor,
                shape = DesignSystemShapes.medium
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // We'll handle our own ripple
                enabled = enabled && !isLoading,
                role = Role.Button,
                onClick = onClick
            )
            .padding(horizontal = horizontalPadding, vertical = 0.dp),
        contentAlignment = Alignment.Center
    ) {
        if (isLoading) {
            // Simple loading dots animation
            LoadingDots()
        } else {
            Text(
                text = text,
                style = textStyle,
                color = contentColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun getButtonColors(
    style: ButtonStyle,
    enabled: Boolean
): Triple<Color, Color, Color> {
    val colorScheme = MaterialTheme.colorScheme

    return when (style) {
        ButtonStyle.PRIMARY -> Triple(
            if (enabled) colorScheme.primary else colorScheme.surfaceVariant,
            if (enabled) colorScheme.onPrimary else colorScheme.onSurfaceVariant,
            Color.Transparent
        )
        ButtonStyle.SECONDARY -> Triple(
            if (enabled) colorScheme.secondaryContainer else colorScheme.surfaceVariant,
            if (enabled) colorScheme.onSecondaryContainer else colorScheme.onSurfaceVariant,
            Color.Transparent
        )
        ButtonStyle.OUTLINED -> Triple(
            Color.Transparent,
            if (enabled) colorScheme.primary else colorScheme.onSurfaceVariant,
            if (enabled) colorScheme.primary else colorScheme.onSurfaceVariant
        )
        ButtonStyle.TEXT -> Triple(
            Color.Transparent,
            if (enabled) colorScheme.primary else colorScheme.onSurfaceVariant,
            Color.Transparent
        )
    }
}

@Composable
private fun getButtonDimensions(size: ButtonSize): Triple<Dp, Dp, androidx.compose.ui.text.TextStyle> {
    val typography = MaterialTheme.typography

    return when (size) {
        ButtonSize.SMALL -> Triple(
            36.dp,
            12.dp,
            typography.labelLarge
        )
        ButtonSize.MEDIUM -> Triple(
            44.dp,
            16.dp,
            typography.labelLarge
        )
        ButtonSize.LARGE -> Triple(
            52.dp,
            24.dp,
            typography.titleMedium
        )
    }
}

@Composable
private fun LoadingDots() {
    // Simple loading dots animation
    // This is a placeholder - you could replace with Lottie animation
    Text(
        text = "...",
        style = MaterialTheme.typography.labelLarge,
        color = MaterialTheme.colorScheme.onPrimary
    )
}