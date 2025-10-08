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
    val lastModified: Long = System.currentTimeMillis()
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
    }
}
