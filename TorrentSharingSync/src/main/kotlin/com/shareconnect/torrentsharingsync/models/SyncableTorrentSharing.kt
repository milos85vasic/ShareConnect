package com.shareconnect.torrentsharingsync.models

import digital.vasic.asinka.models.SyncableObject

class SyncableTorrentSharing(
    private var torrentSharingData: TorrentSharingData
) : SyncableObject {

    override val objectId: String
        get() = torrentSharingData.id

    override val objectType: String
        get() = TorrentSharingData.OBJECT_TYPE

    override val version: Int
        get() = torrentSharingData.version

    fun getTorrentSharingData(): TorrentSharingData = torrentSharingData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to torrentSharingData.id,
            "directSharingEnabled" to torrentSharingData.directSharingEnabled,
            "dontAskQBitConnect" to torrentSharingData.dontAskQBitConnect,
            "dontAskTransmissionConnect" to torrentSharingData.dontAskTransmissionConnect,
            "version" to torrentSharingData.version,
            "lastModified" to torrentSharingData.lastModified
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> torrentSharingData.version
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Long -> lm
            is Int -> lm.toLong()
            else -> torrentSharingData.lastModified
        }

        torrentSharingData = TorrentSharingData(
            id = fields["id"] as? String ?: torrentSharingData.id,
            directSharingEnabled = fields["directSharingEnabled"] as? Boolean ?: torrentSharingData.directSharingEnabled,
            dontAskQBitConnect = fields["dontAskQBitConnect"] as? Boolean ?: torrentSharingData.dontAskQBitConnect,
            dontAskTransmissionConnect = fields["dontAskTransmissionConnect"] as? Boolean ?: torrentSharingData.dontAskTransmissionConnect,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromTorrentSharingData(torrentSharingData: TorrentSharingData): SyncableTorrentSharing {
            return SyncableTorrentSharing(torrentSharingData)
        }
    }
}
