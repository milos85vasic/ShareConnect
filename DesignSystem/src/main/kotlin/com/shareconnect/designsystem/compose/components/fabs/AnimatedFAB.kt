package com.shareconnect.designsystem.compose.components.fabs

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * FAB size variants
 */
enum class FABSize {
    SMALL,
    MEDIUM,
    LARGE
}

/**
 * Animated Floating Action Button with press animations and modern design.
 */
@Composable
fun AnimatedFAB(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: FABSize = FABSize.MEDIUM,
    elevation: Dp = 6.dp,
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.9f else 1f,
        animationSpec = tween(durationMillis = 150),
        label = "fab_scale"
    )

    val fabSize = when (size) {
        FABSize.SMALL -> 40.dp
        FABSize.MEDIUM -> 56.dp
        FABSize.LARGE -> 72.dp
    }

    Box(
        modifier = modifier
            .size(fabSize)
            .scale(scale)
            .shadow(
                elevation = elevation,
                shape = CircleShape,
                clip = false
            )
            .clip(CircleShape)
            .background(
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = CircleShape
            )
            .clickable(
                interactionSource = interactionSource,
                indication = null, // We'll handle our own animation
                enabled = true,
                role = Role.Button,
                onClick = onClick
            ),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}