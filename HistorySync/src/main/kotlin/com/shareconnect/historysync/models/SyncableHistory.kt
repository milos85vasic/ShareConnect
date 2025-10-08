package com.shareconnect.historysync.models

import digital.vasic.asinka.models.SyncableObject

class SyncableHistory(
    private var historyData: HistoryData
) : SyncableObject {

    override val objectId: String
        get() = historyData.id

    override val objectType: String
        get() = HistoryData.OBJECT_TYPE

    override val version: Int
        get() = historyData.version

    fun getHistoryData(): HistoryData = historyData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to historyData.id,
            "url" to historyData.url,
            "title" to historyData.title,
            "description" to historyData.description,
            "thumbnailUrl" to historyData.thumbnailUrl,
            "serviceProvider" to historyData.serviceProvider,
            "type" to historyData.type,
            "timestamp" to historyData.timestamp,
            "profileId" to historyData.profileId,
            "profileName" to historyData.profileName,
            "isSentSuccessfully" to historyData.isSentSuccessfully,
            "serviceType" to historyData.serviceType,
            "torrentClientType" to historyData.torrentClientType,
            "sourceApp" to historyData.sourceApp,
            "version" to historyData.version,
            "lastModified" to historyData.lastModified,
            "fileSize" to historyData.fileSize,
            "duration" to historyData.duration,
            "quality" to historyData.quality,
            "downloadPath" to historyData.downloadPath,
            "torrentHash" to historyData.torrentHash,
            "magnetUri" to historyData.magnetUri,
            "category" to historyData.category,
            "tags" to historyData.tags
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        // Handle version field - could be Int or Long from field map
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> historyData.version
        }

        // Handle timestamp - could be Int or Long
        val timestampValue = when (val t = fields["timestamp"]) {
            is Int -> t.toLong()
            is Long -> t
            else -> historyData.timestamp
        }

        // Handle lastModified - could be Int or Long
        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Int -> lm.toLong()
            is Long -> lm
            else -> historyData.lastModified
        }

        // Handle fileSize - could be Int or Long
        val fileSizeValue = when (val fs = fields["fileSize"]) {
            is Int -> fs.toLong()
            is Long -> fs
            else -> historyData.fileSize
        }

        // Handle duration - could be Int or Long
        val durationValue = when (val d = fields["duration"]) {
            is Int -> d
            is Long -> d.toInt()
            else -> historyData.duration
        }

        historyData = HistoryData(
            id = fields["id"] as? String ?: historyData.id,
            url = fields["url"] as? String ?: historyData.url,
            title = fields["title"] as? String,
            description = fields["description"] as? String,
            thumbnailUrl = fields["thumbnailUrl"] as? String,
            serviceProvider = fields["serviceProvider"] as? String,
            type = fields["type"] as? String ?: historyData.type,
            timestamp = timestampValue,
            profileId = fields["profileId"] as? String,
            profileName = fields["profileName"] as? String,
            isSentSuccessfully = fields["isSentSuccessfully"] as? Boolean ?: historyData.isSentSuccessfully,
            serviceType = fields["serviceType"] as? String ?: historyData.serviceType,
            torrentClientType = fields["torrentClientType"] as? String,
            sourceApp = fields["sourceApp"] as? String ?: historyData.sourceApp,
            version = versionValue,
            lastModified = lastModifiedValue,
            fileSize = fileSizeValue,
            duration = durationValue,
            quality = fields["quality"] as? String,
            downloadPath = fields["downloadPath"] as? String,
            torrentHash = fields["torrentHash"] as? String,
            magnetUri = fields["magnetUri"] as? String,
            category = fields["category"] as? String,
            tags = fields["tags"] as? String
        )
    }

    companion object {
        fun fromHistoryData(historyData: HistoryData): SyncableHistory {
            return SyncableHistory(historyData)
        }
    }
}
