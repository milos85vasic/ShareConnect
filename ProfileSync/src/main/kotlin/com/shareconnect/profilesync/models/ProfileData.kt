package com.shareconnect.profilesync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "synced_profiles")
data class ProfileData(
    @PrimaryKey
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val isDefault: Boolean,
    val serviceType: String,  // metube, ytdl, torrent, jdownloader
    val torrentClientType: String?,  // qbittorrent, transmission, utorrent (only for torrent type)
    val username: String?,
    val password: String?,
    val sourceApp: String,
    val version: Int = 1,
    val lastModified: Long = System.currentTimeMillis(),
    // Additional fields for advanced configurations
    val rpcUrl: String? = null,  // For Transmission
    val useHttps: Boolean = false,
    val trustSelfSignedCert: Boolean = false
) {
    companion object {
        const val OBJECT_TYPE = "profile"

        // Service types
        const val TYPE_METUBE = "metube"
        const val TYPE_YTDL = "ytdl"
        const val TYPE_TORRENT = "torrent"
        const val TYPE_JDOWNLOADER = "jdownloader"

        // Torrent client types
        const val TORRENT_CLIENT_QBITTORRENT = "qbittorrent"
        const val TORRENT_CLIENT_TRANSMISSION = "transmission"
        const val TORRENT_CLIENT_UTORRENT = "utorrent"

        // App identifiers
        const val APP_SHARE_CONNECT = "com.shareconnect"
        const val APP_QBIT_CONNECT = "com.shareconnect.qbitconnect"
        const val APP_TRANSMISSION_CONNECT = "com.shareconnect.transmissionconnect"
        const val APP_UTORRENT_CONNECT = "com.shareconnect.utorrentconnect"
    }

    fun isTorrentProfile(): Boolean = serviceType == TYPE_TORRENT

    fun isQBitTorrentProfile(): Boolean =
        isTorrentProfile() && torrentClientType == TORRENT_CLIENT_QBITTORRENT

    fun isTransmissionProfile(): Boolean =
        isTorrentProfile() && torrentClientType == TORRENT_CLIENT_TRANSMISSION

    fun isUTorrentProfile(): Boolean =
        isTorrentProfile() && torrentClientType == TORRENT_CLIENT_UTORRENT

    fun isMeTubeProfile(): Boolean = serviceType == TYPE_METUBE

    fun isYtDlProfile(): Boolean = serviceType == TYPE_YTDL

    fun isJDownloaderProfile(): Boolean = serviceType == TYPE_JDOWNLOADER

    /**
     * Check if this profile is relevant for qBitConnect (only qBittorrent profiles)
     */
    fun isRelevantForQBitConnect(): Boolean = isQBitTorrentProfile()

    /**
     * Check if this profile is relevant for TransmissionConnect (only Transmission profiles)
     */
    fun isRelevantForTransmissionConnect(): Boolean = isTransmissionProfile()

    /**
     * Check if this profile is relevant for uTorrentConnect (only uTorrent profiles)
     */
    fun isRelevantForUTorrentConnect(): Boolean = isUTorrentProfile()

    /**
     * Get the full URL for this profile
     */
    fun getFullUrl(): String {
        val protocol = if (useHttps) "https" else "http"
        return "$protocol://$host:$port${rpcUrl?.let { "/$it" } ?: ""}"
    }
}
