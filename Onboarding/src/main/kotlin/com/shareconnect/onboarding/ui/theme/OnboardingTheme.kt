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


package com.shareconnect.onboarding.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.shareconnect.themesync.models.ThemeData

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0061A6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF535F70),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDEE3EA),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003257),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD6BEE4),
    onTertiary = Color(0xFF3B2946),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1419),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF0F1419),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF41484D),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF8B9198)
)

// Warm Orange Theme Colors
private val WarmOrangeLightColorScheme = lightColorScheme(
    primary = Color(0xFF8B4513),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFDBCC),
    onPrimaryContainer = Color(0xFF2E1500),
    secondary = Color(0xFF725A42),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFDBCC),
    onSecondaryContainer = Color(0xFF281805),
    tertiary = Color(0xFF5F6135),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFE4E7AF),
    onTertiaryContainer = Color(0xFF1B1D00),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF1F1B16),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF1F1B16),
    surfaceVariant = Color(0xFFF0E0D0),
    onSurfaceVariant = Color(0xFF4F4539),
    outline = Color(0xFF817567)
)

private val WarmOrangeDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB68B),
    onPrimary = Color(0xFF4C2700),
    primaryContainer = Color(0xFF6B3A00),
    onPrimaryContainer = Color(0xFFFFDBCC),
    secondary = Color(0xFFE0C1A0),
    onSecondary = Color(0xFF3F2D16),
    secondaryContainer = Color(0xFF58422B),
    onSecondaryContainer = Color(0xFFFFDBCC),
    tertiary = Color(0xFFC8CB95),
    onTertiary = Color(0xFF30330B),
    tertiaryContainer = Color(0xFF454920),
    onTertiaryContainer = Color(0xFFE4E7AF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1F1B16),
    onBackground = Color(0xFFEAE1D9),
    surface = Color(0xFF1F1B16),
    onSurface = Color(0xFFEAE1D9),
    surfaceVariant = Color(0xFF4F4539),
    onSurfaceVariant = Color(0xFFD4C4B4),
    outline = Color(0xFF9D8F80)
)

// Crimson Theme Colors
private val CrimsonLightColorScheme = lightColorScheme(
    primary = Color(0xFF8B1538),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFD9DD),
    onPrimaryContainer = Color(0xFF3B0014),
    secondary = Color(0xFF74565B),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFFFD9DD),
    onSecondaryContainer = Color(0xFF2A1519),
    tertiary = Color(0xFF7B5738),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFDCBE),
    onTertiaryContainer = Color(0xFF2C1600),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFF),
    onBackground = Color(0xFF201A1B),
    surface = Color(0xFFFFFBFF),
    onSurface = Color(0xFF201A1B),
    surfaceVariant = Color(0xFFF2DDE1),
    onSurfaceVariant = Color(0xFF514346),
    outline = Color(0xFF837377)
)

private val CrimsonDarkColorScheme = darkColorScheme(
    primary = Color(0xFFFFB2BE),
    onPrimary = Color(0xFF5E1133),
    primaryContainer = Color(0xFF7D2949),
    onPrimaryContainer = Color(0xFFFFD9DD),
    secondary = Color(0xFFE3BDC2),
    onSecondary = Color(0xFF41292E),
    secondaryContainer = Color(0xFF594043),
    onSecondaryContainer = Color(0xFFFFD9DD),
    tertiary = Color(0xFFE5C18C),
    onTertiary = Color(0xFF422B05),
    tertiaryContainer = Color(0xFF5B411A),
    onTertiaryContainer = Color(0xFFFFDCBE),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF201A1B),
    onBackground = Color(0xFFEBDFE1),
    surface = Color(0xFF201A1B),
    onSurface = Color(0xFFEBDFE1),
    surfaceVariant = Color(0xFF514346),
    onSurfaceVariant = Color(0xFFD5C2C6),
    outline = Color(0xFF9E8C90)
)

// Light Blue Theme Colors
private val LightBlueLightColorScheme = lightColorScheme(
    primary = Color(0xFF0061A6),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD1E4FF),
    onPrimaryContainer = Color(0xFF001D35),
    secondary = Color(0xFF535F70),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD7E3F7),
    onSecondaryContainer = Color(0xFF101C2B),
    tertiary = Color(0xFF6B5778),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFF2DAFF),
    onTertiaryContainer = Color(0xFF251431),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFEFBFF),
    onBackground = Color(0xFF191C1E),
    surface = Color(0xFFFEFBFF),
    onSurface = Color(0xFF191C1E),
    surfaceVariant = Color(0xFFDEE3EA),
    onSurfaceVariant = Color(0xFF41484D),
    outline = Color(0xFF71787E)
)

private val LightBlueDarkColorScheme = darkColorScheme(
    primary = Color(0xFF9ECAFF),
    onPrimary = Color(0xFF003257),
    primaryContainer = Color(0xFF00497D),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFFBBC7DB),
    onSecondary = Color(0xFF253140),
    secondaryContainer = Color(0xFF3B4858),
    onSecondaryContainer = Color(0xFFD7E3F7),
    tertiary = Color(0xFFD6BEE4),
    onTertiary = Color(0xFF3B2946),
    tertiaryContainer = Color(0xFF523F5F),
    onTertiaryContainer = Color(0xFFF2DAFF),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1419),
    onBackground = Color(0xFFE1E2E8),
    surface = Color(0xFF0F1419),
    onSurface = Color(0xFFE1E2E8),
    surfaceVariant = Color(0xFF41484D),
    onSurfaceVariant = Color(0xFFC1C7CE),
    outline = Color(0xFF8B9198)
)

// Purple Theme Colors
private val PurpleLightColorScheme = lightColorScheme(
    primary = Color(0xFF6B46C1),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFEADDFF),
    onPrimaryContainer = Color(0xFF21005D),
    secondary = Color(0xFF605B71),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE7DEF8),
    onSecondaryContainer = Color(0xFF1D192B),
    tertiary = Color(0xFF7C5261),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD8E4),
    onTertiaryContainer = Color(0xFF31101D),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFFFFBFE),
    onBackground = Color(0xFF1C1B1F),
    surface = Color(0xFFFFFBFE),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7E0EC),
    onSurfaceVariant = Color(0xFF49454F),
    outline = Color(0xFF79747E)
)

private val PurpleDarkColorScheme = darkColorScheme(
    primary = Color(0xFFCFBCFF),
    onPrimary = Color(0xFF381E72),
    primaryContainer = Color(0xFF4F378B),
    onPrimaryContainer = Color(0xFFEADDFF),
    secondary = Color(0xFFCCC2DC),
    onSecondary = Color(0xFF332D41),
    secondaryContainer = Color(0xFF4A4458),
    onSecondaryContainer = Color(0xFFE7DEF8),
    tertiary = Color(0xFFEFB8C8),
    onTertiary = Color(0xFF492532),
    tertiaryContainer = Color(0xFF633B48),
    onTertiaryContainer = Color(0xFFFFD8E4),
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
    outline = Color(0xFF938F99)
)

// Green Theme Colors
private val GreenLightColorScheme = lightColorScheme(
    primary = Color(0xFF146C2B),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFA6F5B8),
    onPrimaryContainer = Color(0xFF002108),
    secondary = Color(0xFF52634F),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD5E8CF),
    onSecondaryContainer = Color(0xFF101F0F),
    tertiary = Color(0xFF386666),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFBCEBEB),
    onTertiaryContainer = Color(0xFF002021),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),
    background = Color(0xFFF7FBF1),
    onBackground = Color(0xFF181D17),
    surface = Color(0xFFF7FBF1),
    onSurface = Color(0xFF181D17),
    surfaceVariant = Color(0xFFDEE5D8),
    onSurfaceVariant = Color(0xFF414941),
    outline = Color(0xFF717970)
)

private val GreenDarkColorScheme = darkColorScheme(
    primary = Color(0xFF8DD99F),
    onPrimary = Color(0xFF003912),
    primaryContainer = Color(0xFF00531F),
    onPrimaryContainer = Color(0xFFA6F5B8),
    secondary = Color(0xFFB9CCB4),
    onSecondary = Color(0xFF253423),
    secondaryContainer = Color(0xFF3B4B39),
    onSecondaryContainer = Color(0xFFD5E8CF),
    tertiary = Color(0xFFA0CFD0),
    onTertiary = Color(0xFF003738),
    tertiaryContainer = Color(0xFF1E4E4F),
    onTertiaryContainer = Color(0xFFBCEBEB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF0F1510),
    onBackground = Color(0xFFDEE5D8),
    surface = Color(0xFF0F1510),
    onSurface = Color(0xFFDEE5D8),
    surfaceVariant = Color(0xFF414941),
    onSurfaceVariant = Color(0xFFBECAB3),
    outline = Color(0xFF899380)
)

val OnboardingTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = 0.sp
    ),
    displayMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 45.sp,
        lineHeight = 52.sp,
        letterSpacing = 0.sp
    ),
    displaySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 36.sp,
        lineHeight = 44.sp,
        letterSpacing = 0.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp,
        letterSpacing = 0.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 28.sp,
        lineHeight = 36.sp,
        letterSpacing = 0.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 24.sp,
        lineHeight = 32.sp,
        letterSpacing = 0.sp
    ),
    titleLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    titleSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.15.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.25.sp
    ),
    bodySmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.4.sp
    ),
    labelLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp,
        lineHeight = 20.sp,
        letterSpacing = 0.1.sp
    ),
    labelMedium = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.SemiBold,
        fontSize = 12.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    ),
    labelSmall = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Bold,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)

fun getColorSchemeForTheme(theme: ThemeData?): androidx.compose.material3.ColorScheme {
    if (theme == null) {
        return LightColorScheme // Default fallback
    }

    return when (theme.colorScheme.lowercase()) {
        "warm_orange" -> if (theme.isDarkMode) WarmOrangeDarkColorScheme else WarmOrangeLightColorScheme
        "crimson" -> if (theme.isDarkMode) CrimsonDarkColorScheme else CrimsonLightColorScheme
        "light_blue" -> if (theme.isDarkMode) LightBlueDarkColorScheme else LightBlueLightColorScheme
        "purple" -> if (theme.isDarkMode) PurpleDarkColorScheme else PurpleLightColorScheme
        "green" -> if (theme.isDarkMode) GreenDarkColorScheme else GreenLightColorScheme
        "material" -> if (theme.isDarkMode) DarkColorScheme else LightColorScheme
        else -> LightColorScheme // Default fallback
    }
}

@Composable
fun OnboardingTheme(
    selectedTheme: ThemeData? = null,
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (selectedTheme != null) {
        getColorSchemeForTheme(selectedTheme)
    } else {
        if (darkTheme) DarkColorScheme else LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = OnboardingTypography,
        content = content
    )
}