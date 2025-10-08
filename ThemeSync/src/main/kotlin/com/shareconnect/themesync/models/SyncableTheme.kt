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
            "lastModified" to themeData.lastModified
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
            lastModified = fields["lastModified"] as? Long ?: themeData.lastModified
        )
    }

    companion object {
        fun fromThemeData(themeData: ThemeData): SyncableTheme {
            return SyncableTheme(themeData)
        }
    }
}
