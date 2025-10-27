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


package com.shareconnect.themesync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_themes")
data class ThemeData(
    @PrimaryKey
    val id: String,
    val name: String,
    val colorScheme: String,
    val isDarkMode: Boolean,
    val isDefault: Boolean,
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis(),
    val isCustom: Boolean = false,
    val customPrimary: Long? = null,
    val customOnPrimary: Long? = null,
    val customPrimaryContainer: Long? = null,
    val customOnPrimaryContainer: Long? = null,
    val customSecondary: Long? = null,
    val customOnSecondary: Long? = null,
    val customSecondaryContainer: Long? = null,
    val customOnSecondaryContainer: Long? = null,
    val customTertiary: Long? = null,
    val customOnTertiary: Long? = null,
    val customTertiaryContainer: Long? = null,
    val customOnTertiaryContainer: Long? = null,
    val customError: Long? = null,
    val customOnError: Long? = null,
    val customErrorContainer: Long? = null,
    val customOnErrorContainer: Long? = null,
    val customBackground: Long? = null,
    val customOnBackground: Long? = null,
    val customSurface: Long? = null,
    val customOnSurface: Long? = null,
    val customSurfaceVariant: Long? = null,
    val customOnSurfaceVariant: Long? = null,
    val customSurfaceTint: Long? = null,
    val customOutline: Long? = null,
    val customOutlineVariant: Long? = null,
    val customScrim: Long? = null,
    val customSurfaceBright: Long? = null,
    val customSurfaceDim: Long? = null,
    val customSurfaceContainer: Long? = null,
    val customSurfaceContainerHigh: Long? = null,
    val customSurfaceContainerHighest: Long? = null,
    val customSurfaceContainerLow: Long? = null,
    val customSurfaceContainerLowest: Long? = null
) {
    companion object {
        const val OBJECT_TYPE = "theme"

        // Color schemes
        const val COLOR_WARM_ORANGE = "warm_orange"
        const val COLOR_CRIMSON = "crimson"
        const val COLOR_LIGHT_BLUE = "light_blue"
        const val COLOR_PURPLE = "purple"
        const val COLOR_GREEN = "green"
        const val COLOR_MATERIAL = "material"
        const val COLOR_CUSTOM = "custom"

        // App identifiers
        const val APP_SHARE_CONNECT = "com.shareconnect"
        const val APP_QBIT_CONNECT = "com.shareconnect.qbitconnect"
        const val APP_TRANSMISSION_CONNECT = "com.shareconnect.transmissionconnect"

        fun getDefaultThemes(sourceApp: String): List<ThemeData> {
            val themes = mutableListOf<ThemeData>()
            var id = 1

            // Warm Orange
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Warm Orange Light",
                    colorScheme = COLOR_WARM_ORANGE,
                    isDarkMode = false,
                    isDefault = true,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Warm Orange Dark",
                    colorScheme = COLOR_WARM_ORANGE,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            // Crimson
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Crimson Light",
                    colorScheme = COLOR_CRIMSON,
                    isDarkMode = false,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Crimson Dark",
                    colorScheme = COLOR_CRIMSON,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            // Light Blue
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Light Blue Light",
                    colorScheme = COLOR_LIGHT_BLUE,
                    isDarkMode = false,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Light Blue Dark",
                    colorScheme = COLOR_LIGHT_BLUE,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            // Purple
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Purple Light",
                    colorScheme = COLOR_PURPLE,
                    isDarkMode = false,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Purple Dark",
                    colorScheme = COLOR_PURPLE,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            // Green
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Green Light",
                    colorScheme = COLOR_GREEN,
                    isDarkMode = false,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Green Dark",
                    colorScheme = COLOR_GREEN,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            // Material
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Material Light",
                    colorScheme = COLOR_MATERIAL,
                    isDarkMode = false,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )
            themes.add(
                ThemeData(
                    id = "${sourceApp}_theme_${id++}",
                    name = "Material Dark",
                    colorScheme = COLOR_MATERIAL,
                    isDarkMode = true,
                    isDefault = false,
                    sourceApp = sourceApp
                )
            )

            return themes
        }

        fun createCustomTheme(
            name: String,
            isDarkMode: Boolean,
            sourceApp: String,
            primary: Long? = null,
            onPrimary: Long? = null,
            primaryContainer: Long? = null,
            onPrimaryContainer: Long? = null,
            secondary: Long? = null,
            onSecondary: Long? = null,
            secondaryContainer: Long? = null,
            onSecondaryContainer: Long? = null,
            tertiary: Long? = null,
            onTertiary: Long? = null,
            tertiaryContainer: Long? = null,
            onTertiaryContainer: Long? = null,
            error: Long? = null,
            onError: Long? = null,
            errorContainer: Long? = null,
            onErrorContainer: Long? = null,
            background: Long? = null,
            onBackground: Long? = null,
            surface: Long? = null,
            onSurface: Long? = null,
            surfaceVariant: Long? = null,
            onSurfaceVariant: Long? = null,
            surfaceTint: Long? = null,
            outline: Long? = null,
            outlineVariant: Long? = null,
            scrim: Long? = null,
            surfaceBright: Long? = null,
            surfaceDim: Long? = null,
            surfaceContainer: Long? = null,
            surfaceContainerHigh: Long? = null,
            surfaceContainerHighest: Long? = null,
            surfaceContainerLow: Long? = null,
            surfaceContainerLowest: Long? = null
        ): ThemeData {
            val id = "custom_${sourceApp}_${System.currentTimeMillis()}_${name.hashCode()}"
            return ThemeData(
                id = id,
                name = name,
                colorScheme = COLOR_CUSTOM,
                isDarkMode = isDarkMode,
                isDefault = false,
                sourceApp = sourceApp,
                isCustom = true,
                customPrimary = primary,
                customOnPrimary = onPrimary,
                customPrimaryContainer = primaryContainer,
                customOnPrimaryContainer = onPrimaryContainer,
                customSecondary = secondary,
                customOnSecondary = onSecondary,
                customSecondaryContainer = secondaryContainer,
                customOnSecondaryContainer = onSecondaryContainer,
                customTertiary = tertiary,
                customOnTertiary = onTertiary,
                customTertiaryContainer = tertiaryContainer,
                customOnTertiaryContainer = onTertiaryContainer,
                customError = error,
                customOnError = onError,
                customErrorContainer = errorContainer,
                customOnErrorContainer = onErrorContainer,
                customBackground = background,
                customOnBackground = onBackground,
                customSurface = surface,
                customOnSurface = onSurface,
                customSurfaceVariant = surfaceVariant,
                customOnSurfaceVariant = onSurfaceVariant,
                customSurfaceTint = surfaceTint,
                customOutline = outline,
                customOutlineVariant = outlineVariant,
                customScrim = scrim,
                customSurfaceBright = surfaceBright,
                customSurfaceDim = surfaceDim,
                customSurfaceContainer = surfaceContainer,
                customSurfaceContainerHigh = surfaceContainerHigh,
                customSurfaceContainerHighest = surfaceContainerHighest,
                customSurfaceContainerLow = surfaceContainerLow,
                customSurfaceContainerLowest = surfaceContainerLowest
            )
        }
    }
}
