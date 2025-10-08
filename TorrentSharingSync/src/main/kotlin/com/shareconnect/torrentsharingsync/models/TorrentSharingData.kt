package com.shareconnect.torrentsharingsync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Torrent sharing preferences data model
 *
 * Stores torrent app sharing preferences to be synced across all apps
 * in the ShareConnect ecosystem.
 *
 * Fields:
 * - id: Unique identifier (always "torrent_sharing_prefs" for singleton pattern)
 * - directSharingEnabled: Whether direct sharing to torrent apps is enabled
 * - dontAskQBitConnect: "Don't ask again" preference for qBitConnect installation
 * - dontAskTransmissionConnect: "Don't ask again" preference for TransmissionConnect
 * - version: Sync version for conflict resolution
 * - lastModified: Last modification timestamp
 */
@Entity(tableName = "synced_torrent_sharing_prefs")
data class TorrentSharingData(
    @PrimaryKey
    val id: String = "torrent_sharing_prefs", // Singleton pattern

    val directSharingEnabled: Boolean = true, // Direct sharing enabled by default

    val dontAskQBitConnect: Boolean = false, // Don't ask again for qBitConnect

    val dontAskTransmissionConnect: Boolean = false, // Don't ask again for TransmissionConnect

    val version: Int = 1, // For sync conflict resolution

    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        const val OBJECT_TYPE = "torrent_sharing_prefs"
        const val PREFS_ID = "torrent_sharing_prefs"

        /**
         * Create default torrent sharing preferences
         */
        fun createDefault(): TorrentSharingData = TorrentSharingData(
            id = PREFS_ID,
            directSharingEnabled = true,
            dontAskQBitConnect = false,
            dontAskTransmissionConnect = false,
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        /**
         * Create updated preferences with new values
         */
        fun createUpdated(
            current: TorrentSharingData,
            directSharingEnabled: Boolean? = null,
            dontAskQBitConnect: Boolean? = null,
            dontAskTransmissionConnect: Boolean? = null
        ): TorrentSharingData = TorrentSharingData(
            id = PREFS_ID,
            directSharingEnabled = directSharingEnabled ?: current.directSharingEnabled,
            dontAskQBitConnect = dontAskQBitConnect ?: current.dontAskQBitConnect,
            dontAskTransmissionConnect = dontAskTransmissionConnect ?: current.dontAskTransmissionConnect,
            version = current.version + 1,
            lastModified = System.currentTimeMillis()
        )
    }

    /**
     * Check if a specific app should be prompted for installation
     */
    fun shouldAskForApp(appPackage: String): Boolean = when (appPackage) {
        "com.shareconnect.qbitconnect" -> !dontAskQBitConnect
        "com.shareconnect.transmissionconnect" -> !dontAskTransmissionConnect
        else -> true
    }

    /**
     * Copy with updated version (for conflict resolution)
     */
    fun withIncrementedVersion(): TorrentSharingData = copy(
        version = version + 1,
        lastModified = System.currentTimeMillis()
    )
}
