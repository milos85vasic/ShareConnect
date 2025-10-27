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

import digital.vasic.asinka.models.SyncableObject

class SyncableTheme(
    private var themeData: ThemeData
) : SyncableObject {

    override val objectId: String
        get() = themeData.id

    override val objectType: String
        get() = ThemeData.OBJECT_TYPE

    override val version: Int
        get() = themeData.version

    fun getThemeData(): ThemeData = themeData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to themeData.id,
            "name" to themeData.name,
            "colorScheme" to themeData.colorScheme,
            "isDarkMode" to themeData.isDarkMode,
            "isDefault" to themeData.isDefault,
            "sourceApp" to themeData.sourceApp,
            "version" to themeData.version,
            "lastModified" to themeData.lastModified,
            "isCustom" to themeData.isCustom,
            "customPrimary" to themeData.customPrimary,
            "customOnPrimary" to themeData.customOnPrimary,
            "customPrimaryContainer" to themeData.customPrimaryContainer,
            "customOnPrimaryContainer" to themeData.customOnPrimaryContainer,
            "customSecondary" to themeData.customSecondary,
            "customOnSecondary" to themeData.customOnSecondary,
            "customSecondaryContainer" to themeData.customSecondaryContainer,
            "customOnSecondaryContainer" to themeData.customOnSecondaryContainer,
            "customTertiary" to themeData.customTertiary,
            "customOnTertiary" to themeData.customOnTertiary,
            "customTertiaryContainer" to themeData.customTertiaryContainer,
            "customOnTertiaryContainer" to themeData.customOnTertiaryContainer,
            "customError" to themeData.customError,
            "customOnError" to themeData.customOnError,
            "customErrorContainer" to themeData.customErrorContainer,
            "customOnErrorContainer" to themeData.customOnErrorContainer,
            "customBackground" to themeData.customBackground,
            "customOnBackground" to themeData.customOnBackground,
            "customSurface" to themeData.customSurface,
            "customOnSurface" to themeData.customOnSurface,
            "customSurfaceVariant" to themeData.customSurfaceVariant,
            "customOnSurfaceVariant" to themeData.customOnSurfaceVariant,
            "customSurfaceTint" to themeData.customSurfaceTint,
            "customOutline" to themeData.customOutline,
            "customOutlineVariant" to themeData.customOutlineVariant,
            "customScrim" to themeData.customScrim,
            "customSurfaceBright" to themeData.customSurfaceBright,
            "customSurfaceDim" to themeData.customSurfaceDim,
            "customSurfaceContainer" to themeData.customSurfaceContainer,
            "customSurfaceContainerHigh" to themeData.customSurfaceContainerHigh,
            "customSurfaceContainerHighest" to themeData.customSurfaceContainerHighest,
            "customSurfaceContainerLow" to themeData.customSurfaceContainerLow,
            "customSurfaceContainerLowest" to themeData.customSurfaceContainerLowest
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        // Handle version field - could be Int or Long from field map
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> themeData.version
        }

        themeData = ThemeData(
            id = fields["id"] as? String ?: themeData.id,
            name = fields["name"] as? String ?: themeData.name,
            colorScheme = fields["colorScheme"] as? String ?: themeData.colorScheme,
            isDarkMode = fields["isDarkMode"] as? Boolean ?: themeData.isDarkMode,
            isDefault = fields["isDefault"] as? Boolean ?: themeData.isDefault,
            sourceApp = fields["sourceApp"] as? String ?: themeData.sourceApp,
            version = versionValue,
            lastModified = fields["lastModified"] as? Long ?: themeData.lastModified,
            isCustom = fields["isCustom"] as? Boolean ?: themeData.isCustom,
            customPrimary = fields["customPrimary"] as? Long,
            customOnPrimary = fields["customOnPrimary"] as? Long,
            customPrimaryContainer = fields["customPrimaryContainer"] as? Long,
            customOnPrimaryContainer = fields["customOnPrimaryContainer"] as? Long,
            customSecondary = fields["customSecondary"] as? Long,
            customOnSecondary = fields["customOnSecondary"] as? Long,
            customSecondaryContainer = fields["customSecondaryContainer"] as? Long,
            customOnSecondaryContainer = fields["customOnSecondaryContainer"] as? Long,
            customTertiary = fields["customTertiary"] as? Long,
            customOnTertiary = fields["customOnTertiary"] as? Long,
            customTertiaryContainer = fields["customTertiaryContainer"] as? Long,
            customOnTertiaryContainer = fields["customOnTertiaryContainer"] as? Long,
            customError = fields["customError"] as? Long,
            customOnError = fields["customOnError"] as? Long,
            customErrorContainer = fields["customErrorContainer"] as? Long,
            customOnErrorContainer = fields["customOnErrorContainer"] as? Long,
            customBackground = fields["customBackground"] as? Long,
            customOnBackground = fields["customOnBackground"] as? Long,
            customSurface = fields["customSurface"] as? Long,
            customOnSurface = fields["customOnSurface"] as? Long,
            customSurfaceVariant = fields["customSurfaceVariant"] as? Long,
            customOnSurfaceVariant = fields["customOnSurfaceVariant"] as? Long,
            customSurfaceTint = fields["customSurfaceTint"] as? Long,
            customOutline = fields["customOutline"] as? Long,
            customOutlineVariant = fields["customOutlineVariant"] as? Long,
            customScrim = fields["customScrim"] as? Long,
            customSurfaceBright = fields["customSurfaceBright"] as? Long,
            customSurfaceDim = fields["customSurfaceDim"] as? Long,
            customSurfaceContainer = fields["customSurfaceContainer"] as? Long,
            customSurfaceContainerHigh = fields["customSurfaceContainerHigh"] as? Long,
            customSurfaceContainerHighest = fields["customSurfaceContainerHighest"] as? Long,
            customSurfaceContainerLow = fields["customSurfaceContainerLow"] as? Long,
            customSurfaceContainerLowest = fields["customSurfaceContainerLowest"] as? Long
        )
    }

    companion object {
        fun fromThemeData(themeData: ThemeData): SyncableTheme {
            return SyncableTheme(themeData)
        }
    }
}
