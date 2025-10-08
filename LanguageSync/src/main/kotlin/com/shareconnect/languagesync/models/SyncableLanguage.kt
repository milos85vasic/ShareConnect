package com.shareconnect.languagesync.models

import digital.vasic.asinka.models.SyncableObject

class SyncableLanguage(
    private var languageData: LanguageData
) : SyncableObject {

    override val objectId: String
        get() = languageData.id

    override val objectType: String
        get() = LanguageData.OBJECT_TYPE

    override val version: Int
        get() = languageData.version

    fun getLanguageData(): LanguageData = languageData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to languageData.id,
            "languageCode" to languageData.languageCode,
            "displayName" to languageData.displayName,
            "isSystemDefault" to languageData.isSystemDefault,
            "version" to languageData.version,
            "lastModified" to languageData.lastModified
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> languageData.version
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Long -> lm
            is Int -> lm.toLong()
            else -> languageData.lastModified
        }

        languageData = LanguageData(
            id = fields["id"] as? String ?: languageData.id,
            languageCode = fields["languageCode"] as? String ?: languageData.languageCode,
            displayName = fields["displayName"] as? String ?: languageData.displayName,
            isSystemDefault = fields["isSystemDefault"] as? Boolean ?: languageData.isSystemDefault,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromLanguageData(languageData: LanguageData): SyncableLanguage {
            return SyncableLanguage(languageData)
        }
    }
}
