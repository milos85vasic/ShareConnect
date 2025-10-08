package com.shareconnect.preferencessync.models

import digital.vasic.asinka.models.SyncableObject

class SyncablePreferences(
    private var preferencesData: PreferencesData
) : SyncableObject {

    override val objectId: String get() = preferencesData.id
    override val objectType: String get() = PreferencesData.OBJECT_TYPE
    override val version: Int get() = preferencesData.version

    fun getPreferencesData(): PreferencesData = preferencesData

    override fun toFieldMap(): Map<String, Any?> = mapOf(
        "id" to preferencesData.id,
        "category" to preferencesData.category,
        "key" to preferencesData.key,
        "value" to preferencesData.value,
        "type" to preferencesData.type,
        "description" to preferencesData.description,
        "sourceApp" to preferencesData.sourceApp,
        "version" to preferencesData.version,
        "lastModified" to preferencesData.lastModified
    )

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> preferencesData.version
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Int -> lm.toLong()
            is Long -> lm
            else -> preferencesData.lastModified
        }

        preferencesData = PreferencesData(
            id = fields["id"] as? String ?: preferencesData.id,
            category = fields["category"] as? String ?: preferencesData.category,
            key = fields["key"] as? String ?: preferencesData.key,
            value = fields["value"] as? String,
            type = fields["type"] as? String ?: preferencesData.type,
            description = fields["description"] as? String,
            sourceApp = fields["sourceApp"] as? String ?: preferencesData.sourceApp,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromPreferencesData(preferencesData: PreferencesData) = SyncablePreferences(preferencesData)
    }
}
