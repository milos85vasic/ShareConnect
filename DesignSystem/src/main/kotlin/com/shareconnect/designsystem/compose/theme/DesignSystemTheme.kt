/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.designsystem.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.shareconnect.themesync.models.ThemeData

/**
 * Design System color palette for light theme
 */
private val LightColorScheme = lightColorScheme(
    primary = Color(0xFFFF6B35),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFFF8A65),
    onPrimaryContainer = Color(0xFF1A0000),
    secondary = Color(0xFFFF8A65),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFFFD7CC),
    onSecondaryContainer = Color(0xFF2A1400),
    tertiary = Color(0xFF7D5800),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDF9E),
    onTertiaryContainer = Color(0xFF271900),
    error = Color(0xFFBA1A1A),
    onError = Color(0xFFFFFFFF),
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    surfaceTint = Color(0xFFFF6B35),
    outline = Color(0xFF79747E),
    outlineVariant = Color(0xFFCAC4D0),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFFFFFBFE),
    surfaceDim = Color(0xFFDED8E1),
    surfaceContainer = Color(0xFFF3EDF7),
    surfaceContainerHigh = Color(0xFFECE6F0),
    surfaceContainerHighest = Color(0xFFE6E0E9),
    surfaceContainerLow = Color(0xFFF7F2FA),
    surfaceContainerLowest = Color(0xFFFFFFFF)
)

/**
 * Design System color palette for dark theme
 */
private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB4A3),
    onPrimary = Color(0xFF561F0F),
    primaryContainer = Color(0xFFFF8A65),
    onPrimaryContainer = Color(0xFF1A0000),
    secondary = Color(0xFFFFB4A3),
    onSecondary = Color(0xFF561F0F),
    secondaryContainer = Color(0xFF7D2C0C),
    onSecondaryContainer = Color(0xFFFFD7CC),
    tertiary = Color(0xFFE3C28C),
    onTertiary = Color(0xFF402D00),
    tertiaryContainer = Color(0xFF5B4200),
    onTertiaryContainer = Color(0xFFFFDF9E),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF141218),
    onBackground = Color(0xFFE6E1E5),
    surface = Color(0xFF141218),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceTint = Color(0xFFFFB4A3),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
    scrim = Color(0xFF000000),
    surfaceBright = Color(0xFF3B383E),
    surfaceDim = Color(0xFF141218),
    surfaceContainer = Color(0xFF211F26),
    surfaceContainerHigh = Color(0xFF2B2930),
    surfaceContainerHighest = Color(0xFF36343B),
    surfaceContainerLow = Color(0xFF1C1B1F),
    surfaceContainerLowest = Color(0xFF0F0D13)
)

/**
 * Create a color scheme from custom theme data
 */
fun createColorSchemeFromTheme(themeData: ThemeData, isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    return if (themeData.isCustom && themeData.customPrimary != null) {
        // Use custom colors if available
        if (isDarkMode) {
            darkColorScheme(
                primary = Color(themeData.customPrimary ?: 0xFFFF6B35L),
                onPrimary = Color(themeData.customOnPrimary ?: 0xFFFFFFFFL),
                primaryContainer = Color(themeData.customPrimaryContainer ?: (themeData.customPrimary ?: 0xFFFF6B35L)),
                onPrimaryContainer = Color(themeData.customOnPrimaryContainer ?: 0xFF1A0000L),
                secondary = Color((themeData.customSecondary ?: themeData.customPrimary) ?: 0xFFFF8A65L),
                onSecondary = Color(themeData.customOnSecondary ?: 0xFFFFFFFFL),
                secondaryContainer = Color(themeData.customSecondaryContainer ?: (themeData.customSecondary ?: (themeData.customPrimary ?: 0xFFFF8A65L))),
                onSecondaryContainer = Color(themeData.customOnSecondaryContainer ?: 0xFF2A1400),
                tertiary = Color(themeData.customTertiary ?: 0xFF7D5800),
                onTertiary = Color(themeData.customOnTertiary ?: 0xFFFFFFFF),
                tertiaryContainer = Color(themeData.customTertiaryContainer ?: 0xFFFFDF9E),
                onTertiaryContainer = Color(themeData.customOnTertiaryContainer ?: 0xFF271900),
                error = Color(themeData.customError ?: 0xFFBA1A1A),
                onError = Color(themeData.customOnError ?: 0xFFFFFFFF),
                errorContainer = Color(themeData.customErrorContainer ?: 0xFFFFDAD6L),
                onErrorContainer = Color(themeData.customOnErrorContainer ?: 0xFF410002L),
                background = Color(themeData.customBackground ?: if (isDarkMode) 0xFF141218L else 0xFFFFFBFEL),
                onBackground = Color(themeData.customOnBackground ?: if (isDarkMode) 0xFFE6E1E5L else 0xFF1C1B1FL),
                surface = Color(themeData.customSurface ?: if (isDarkMode) 0xFF141218L else 0xFFFFFBFEL),
                onSurface = Color(themeData.customOnSurface ?: if (isDarkMode) 0xFFE6E1E5L else 0xFF1C1B1FL),
                surfaceVariant = Color(themeData.customSurfaceVariant ?: if (isDarkMode) 0xFF49454FL else 0xFFE7E0ECL),
                onSurfaceVariant = Color(themeData.customOnSurfaceVariant ?: if (isDarkMode) 0xFFCAC4D0L else 0xFF49454FL),
                surfaceTint = Color(themeData.customSurfaceTint ?: (themeData.customPrimary ?: 0xFFFF6B35L)),
                outline = Color(themeData.customOutline ?: if (isDarkMode) 0xFF938F99L else 0xFF79747EL),
                outlineVariant = Color(themeData.customOutlineVariant ?: if (isDarkMode) 0xFF49454FL else 0xFFCAC4D0L),
                scrim = Color(themeData.customScrim ?: 0xFF000000L),
                surfaceBright = Color(themeData.customSurfaceBright ?: if (isDarkMode) 0xFF3B383EL else 0xFFFFFBFEL),
                surfaceDim = Color(themeData.customSurfaceDim ?: if (isDarkMode) 0xFF141218L else 0xFFDED8E1L),
                surfaceContainer = Color(themeData.customSurfaceContainer ?: if (isDarkMode) 0xFF211F26L else 0xFFF3EDF7L),
                surfaceContainerHigh = Color(themeData.customSurfaceContainerHigh ?: if (isDarkMode) 0xFF2B2930L else 0xFFECE6F0L),
                surfaceContainerHighest = Color(themeData.customSurfaceContainerHighest ?: if (isDarkMode) 0xFF36343BL else 0xFFE6E0E9L),
                surfaceContainerLow = Color(themeData.customSurfaceContainerLow ?: if (isDarkMode) 0xFF1C1B1FL else 0xFFF7F2FAL),
                surfaceContainerLowest = Color(themeData.customSurfaceContainerLowest ?: if (isDarkMode) 0xFF0F0D13L else 0xFFFFFFFFL)
            )
        } else {
            lightColorScheme(
                primary = Color(themeData.customPrimary ?: 0xFFFF6B35L),
                onPrimary = Color(themeData.customOnPrimary ?: 0xFFFFFFFFL),
                primaryContainer = Color(themeData.customPrimaryContainer ?: (themeData.customPrimary ?: 0xFFFF6B35L)),
                onPrimaryContainer = Color(themeData.customOnPrimaryContainer ?: 0xFF1A0000),
                secondary = Color((themeData.customSecondary ?: themeData.customPrimary) ?: 0xFFFF8A65L),
                onSecondary = Color(themeData.customOnSecondary ?: 0xFFFFFFFFL),
                secondaryContainer = Color(themeData.customSecondaryContainer ?: (themeData.customSecondary ?: (themeData.customPrimary ?: 0xFFFF8A65L))),
                onSecondaryContainer = Color(themeData.customOnSecondaryContainer ?: 0xFF2A1400),
                tertiary = Color(themeData.customTertiary ?: 0xFF7D5800),
                onTertiary = Color(themeData.customOnTertiary ?: 0xFFFFFFFF),
                tertiaryContainer = Color(themeData.customTertiaryContainer ?: 0xFFFFDF9E),
                onTertiaryContainer = Color(themeData.customOnTertiaryContainer ?: 0xFF271900L),
                error = Color(themeData.customError ?: 0xFFBA1A1AL),
                onError = Color(themeData.customOnError ?: 0xFFFFFFFFL),
                errorContainer = Color(themeData.customErrorContainer ?: 0xFFFFDAD6L),
                onErrorContainer = Color(themeData.customOnErrorContainer ?: 0xFF410002L),
                background = Color(themeData.customBackground ?: 0xFFFFFBFEL),
                onBackground = Color(themeData.customOnBackground ?: 0xFF1C1B1F),
                surface = Color(themeData.customSurface ?: 0xFFFFFBFEL),
                onSurface = Color(themeData.customOnSurface ?: 0xFF1C1B1FL),
                surfaceVariant = Color(themeData.customSurfaceVariant ?: 0xFFE7E0ECL),
                onSurfaceVariant = Color(themeData.customOnSurfaceVariant ?: 0xFF49454FL),
                surfaceTint = Color(themeData.customSurfaceTint ?: (themeData.customPrimary ?: 0xFFFF6B35L)),
                outline = Color(themeData.customOutline ?: 0xFF79747EL),
                outlineVariant = Color(themeData.customOutlineVariant ?: 0xFFCAC4D0L),
                scrim = Color(themeData.customScrim ?: 0xFF000000L),
                surfaceBright = Color(themeData.customSurfaceBright ?: 0xFFFFFBFEL),
                surfaceDim = Color(themeData.customSurfaceDim ?: 0xFFDED8E1L),
                surfaceContainer = Color(themeData.customSurfaceContainer ?: 0xFFF3EDF7L),
                surfaceContainerHigh = Color(themeData.customSurfaceContainerHigh ?: 0xFFECE6F0L),
                surfaceContainerHighest = Color(themeData.customSurfaceContainerHighest ?: 0xFFE6E0E9L),
                surfaceContainerLow = Color(themeData.customSurfaceContainerLow ?: 0xFFF7F2FAL),
                surfaceContainerLowest = Color(themeData.customSurfaceContainerLowest ?: 0xFFFFFFFFL)
            )
        }
    } else {
        // Use default color scheme based on colorScheme string
        when (themeData.colorScheme) {
            ThemeData.COLOR_WARM_ORANGE -> if (isDarkMode) DarkColorScheme else LightColorScheme
            ThemeData.COLOR_CRIMSON -> createCrimsonColorScheme(isDarkMode)
            ThemeData.COLOR_LIGHT_BLUE -> createLightBlueColorScheme(isDarkMode)
            ThemeData.COLOR_PURPLE -> createPurpleColorScheme(isDarkMode)
            ThemeData.COLOR_GREEN -> createGreenColorScheme(isDarkMode)
            ThemeData.COLOR_MATERIAL -> if (isDarkMode) androidx.compose.material3.darkColorScheme() else androidx.compose.material3.lightColorScheme()
            else -> if (isDarkMode) DarkColorScheme else LightColorScheme
        }
    }
}

/**
 * Design System theme that provides Material 3 theming with custom color schemes
 */
@Composable
fun DesignSystemTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DesignSystemTypography,
        shapes = DesignSystemShapes,
        content = content
    )
}

/**
 * Design System theme that uses a specific theme data
 */
@Composable
fun DesignSystemTheme(
    themeData: ThemeData,
    content: @Composable () -> Unit
) {
    val colorScheme = createColorSchemeFromTheme(themeData, themeData.isDarkMode)

    MaterialTheme(
        colorScheme = colorScheme,
        typography = DesignSystemTypography,
        shapes = DesignSystemShapes,
        content = content
    )
}

// Helper functions for creating color schemes for different theme types
private fun createCrimsonColorScheme(isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    return if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFFFFB4AB),
            onPrimary = Color(0xFF690005),
            primaryContainer = Color(0xFF93000A),
            onPrimaryContainer = Color(0xFFFFDAD6),
            secondary = Color(0xFFE7BDB8),
            onSecondary = Color(0xFF442A25),
            secondaryContainer = Color(0xFF5D3F3A),
            onSecondaryContainer = Color(0xFFFFDAD6),
            tertiary = Color(0xFFE3C28C),
            onTertiary = Color(0xFF402D00),
            tertiaryContainer = Color(0xFF5B4200),
            onTertiaryContainer = Color(0xFFFFDF9E),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF53433F),
            onSurfaceVariant = Color(0xFFD8C2BC),
            surfaceTint = Color(0xFFFFB4AB),
            outline = Color(0xFFA08D87),
            outlineVariant = Color(0xFF53433F),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFF3B383E),
            surfaceDim = Color(0xFF1C1B1F),
            surfaceContainer = Color(0xFF211F26),
            surfaceContainerHigh = Color(0xFF2B2930),
            surfaceContainerHighest = Color(0xFF36343B),
            surfaceContainerLow = Color(0xFF1C1B1F),
            surfaceContainerLowest = Color(0xFF0F0D13)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF93000A),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDAD6),
            onPrimaryContainer = Color(0xFF410002),
            secondary = Color(0xFF775652),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFDAD6),
            onSecondaryContainer = Color(0xFF2C1512),
            tertiary = Color(0xFF7D5800),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFDF9E),
            onTertiaryContainer = Color(0xFF271900),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFE),
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFE),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFF5DED8),
            onSurfaceVariant = Color(0xFF53433F),
            surfaceTint = Color(0xFF93000A),
            outline = Color(0xFF85736E),
            outlineVariant = Color(0xFFD8C2BC),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFFFFFBFE),
            surfaceDim = Color(0xFFE7D8E2),
            surfaceContainer = Color(0xFFF3EDF7),
            surfaceContainerHigh = Color(0xFFECE6F0),
            surfaceContainerHighest = Color(0xFFE6E0E9),
            surfaceContainerLow = Color(0xFFF7F2FA),
            surfaceContainerLowest = Color(0xFFFFFFFF)
        )
    }
}

private fun createLightBlueColorScheme(isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    return if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFF9ECAFF),
            onPrimary = Color(0xFF003257),
            primaryContainer = Color(0xFF00497D),
            onPrimaryContainer = Color(0xFFD1E4FF),
            secondary = Color(0xFFB8C8DA),
            onSecondary = Color(0xFF253041),
            secondaryContainer = Color(0xFF3B4858),
            onSecondaryContainer = Color(0xFFD4E4F6),
            tertiary = Color(0xFFD4BFFF),
            onTertiary = Color(0xFF381E72),
            tertiaryContainer = Color(0xFF50359A),
            onTertiaryContainer = Color(0xFFEADDFF),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF44464F),
            onSurfaceVariant = Color(0xFFC4C6D0),
            surfaceTint = Color(0xFF9ECAFF),
            outline = Color(0xFF8E9099),
            outlineVariant = Color(0xFF44464F),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFF3B383E),
            surfaceDim = Color(0xFF1C1B1F),
            surfaceContainer = Color(0xFF211F26),
            surfaceContainerHigh = Color(0xFF2B2930),
            surfaceContainerHighest = Color(0xFF36343B),
            surfaceContainerLow = Color(0xFF1C1B1F),
            surfaceContainerLowest = Color(0xFF0F0D13)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF00497D),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFD1E4FF),
            onPrimaryContainer = Color(0xFF001D35),
            secondary = Color(0xFF535F70),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD7E3F8),
            onSecondaryContainer = Color(0xFF0F1C2B),
            tertiary = Color(0xFF6B5B9F),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFEADDFF),
            onTertiaryContainer = Color(0xFF251431),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFEFBFF),
            onBackground = Color(0xFF1A1C1E),
            surface = Color(0xFFFEFBFF),
            onSurface = Color(0xFF1A1C1E),
            surfaceVariant = Color(0xFFDDE3EA),
            onSurfaceVariant = Color(0xFF41484D),
            surfaceTint = Color(0xFF00497D),
            outline = Color(0xFF71787E),
            outlineVariant = Color(0xFFC1C7CE),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFFFEFBFF),
            surfaceDim = Color(0xFFDAD9E0),
            surfaceContainer = Color(0xFFF0F0F7),
            surfaceContainerHigh = Color(0xFFEAEAF0),
            surfaceContainerHighest = Color(0xFFE4E4EA),
            surfaceContainerLow = Color(0xFFF6F6FC),
            surfaceContainerLowest = Color(0xFFFFFFFF)
        )
    }
}

private fun createPurpleColorScheme(isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    return if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378A),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF492532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            surfaceTint = Color(0xFFD0BCFF),
            outline = Color(0xFF938F99),
            outlineVariant = Color(0xFF49454F),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFF3B383E),
            surfaceDim = Color(0xFF1C1B1F),
            surfaceContainer = Color(0xFF211F26),
            surfaceContainerHigh = Color(0xFF2B2930),
            surfaceContainerHighest = Color(0xFF36343B),
            surfaceContainerLow = Color(0xFF1C1B1F),
            surfaceContainerLowest = Color(0xFF0F0D13)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF4F378A),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF13005A),
            secondary = Color(0xFF625B71),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1E192B),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31101D),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFFFBFE),
            onBackground = Color(0xFF1C1B1F),
            surface = Color(0xFFFFFBFE),
            onSurface = Color(0xFF1C1B1F),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            surfaceTint = Color(0xFF4F378A),
            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFFFFFBFE),
            surfaceDim = Color(0xFFDED8E1),
            surfaceContainer = Color(0xFFF3EDF7),
            surfaceContainerHigh = Color(0xFFECE6F0),
            surfaceContainerHighest = Color(0xFFE6E0E9),
            surfaceContainerLow = Color(0xFFF7F2FA),
            surfaceContainerLowest = Color(0xFFFFFFFF)
        )
    }
}

private fun createGreenColorScheme(isDarkMode: Boolean): androidx.compose.material3.ColorScheme {
    return if (isDarkMode) {
        darkColorScheme(
            primary = Color(0xFF8DD99A),
            onPrimary = Color(0xFF003912),
            primaryContainer = Color(0xFF005319),
            onPrimaryContainer = Color(0xFFA9F6B5),
            secondary = Color(0xFFB8CCB9),
            onSecondary = Color(0xFF253233),
            secondaryContainer = Color(0xFF3B4B3C),
            onSecondaryContainer = Color(0xFFD4E8D5),
            tertiary = Color(0xFFA3CED4),
            onTertiary = Color(0xFF00363D),
            tertiaryContainer = Color(0xFF204E54),
            onTertiaryContainer = Color(0xFFBFEAF1),
            error = Color(0xFFFFB4AB),
            onError = Color(0xFF690005),
            errorContainer = Color(0xFF93000A),
            onErrorContainer = Color(0xFFFFDAD6),
            background = Color(0xFF1C1B1F),
            onBackground = Color(0xFFE6E1E5),
            surface = Color(0xFF1C1B1F),
            onSurface = Color(0xFFE6E1E5),
            surfaceVariant = Color(0xFF414942),
            onSurfaceVariant = Color(0xFFC1C9C0),
            surfaceTint = Color(0xFF8DD99A),
            outline = Color(0xFF8B938A),
            outlineVariant = Color(0xFF414942),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFF3B383E),
            surfaceDim = Color(0xFF1C1B1F),
            surfaceContainer = Color(0xFF211F26),
            surfaceContainerHigh = Color(0xFF2B2930),
            surfaceContainerHighest = Color(0xFF36343B),
            surfaceContainerLow = Color(0xFF1C1B1F),
            surfaceContainerLowest = Color(0xFF0F0D13)
        )
    } else {
        lightColorScheme(
            primary = Color(0xFF005319),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFA9F6B5),
            onPrimaryContainer = Color(0xFF001B05),
            secondary = Color(0xFF526351),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD5E8D4),
            onSecondaryContainer = Color(0xFF101F10),
            tertiary = Color(0xFF39656C),
            onTertiary = Color(0xFFFFFFFF),
            tertiaryContainer = Color(0xFFBFEAF1),
            onTertiaryContainer = Color(0xFF001F24),
            error = Color(0xFFBA1A1A),
            onError = Color(0xFFFFFFFF),
            errorContainer = Color(0xFFFFDAD6),
            onErrorContainer = Color(0xFF410002),
            background = Color(0xFFFEFFFD),
            onBackground = Color(0xFF191C19),
            surface = Color(0xFFFEFFFD),
            onSurface = Color(0xFF191C19),
            surfaceVariant = Color(0xFFDDE5DB),
            onSurfaceVariant = Color(0xFF414942),
            surfaceTint = Color(0xFF005319),
            outline = Color(0xFF717971),
            outlineVariant = Color(0xFFC1C9C0),
            scrim = Color(0xFF000000),
            surfaceBright = Color(0xFFFEFFFD),
            surfaceDim = Color(0xFFDADBD9),
            surfaceContainer = Color(0xFFF0F1EE),
            surfaceContainerHigh = Color(0xFFEAEBE8),
            surfaceContainerHighest = Color(0xFFE4E6E2),
            surfaceContainerLow = Color(0xFFF6F7F4),
            surfaceContainerLowest = Color(0xFFFFFFFF)
        )
    }
}