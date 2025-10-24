package com.shareconnect.utorrentconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * uTorrent Web UI torrent model
 * Array format: [hash, status, name, size, progress, downloaded, uploaded, ratio, upload_speed, download_speed, eta, label, peers_connected, peers_in_swarm, seeds_connected, seeds_in_swarm, availability, torrent_queue_order, remaining]
 */
data class UTorrentTorrent(
    val hash: String,
    val status: Int,
    val name: String,
    val size: Long,
    val progress: Long, // Progress in per mille (0-1000)
    val downloaded: Long,
    val uploaded: Long,
    val ratio: Long, // Ratio in per mille
    val uploadSpeed: Long,
    val downloadSpeed: Long,
    val eta: Long, // ETA in seconds
    val label: String,
    val peersConnected: Int,
    val peersInSwarm: Int,
    val seedsConnected: Int,
    val seedsInSwarm: Int,
    val availability: Long, // Availability in 1/65536ths
    val queueOrder: Int,
    val remaining: Long
) {
    companion object {
        /**
         * Parse torrent from JSON array
         */
        fun fromArray(array: List<Any>): UTorrentTorrent {
            return UTorrentTorrent(
                hash = array[0] as String,
                status = (array[1] as Number).toInt(),
                name = array[2] as String,
                size = (array[3] as Number).toLong(),
                progress = (array[4] as Number).toLong(),
                downloaded = (array[5] as Number).toLong(),
                uploaded = (array[6] as Number).toLong(),
                ratio = (array[7] as Number).toLong(),
                uploadSpeed = (array[8] as Number).toLong(),
                downloadSpeed = (array[9] as Number).toLong(),
                eta = (array[10] as Number).toLong(),
                label = array[11] as String,
                peersConnected = (array[12] as Number).toInt(),
                peersInSwarm = (array[13] as Number).toInt(),
                seedsConnected = (array[14] as Number).toInt(),
                seedsInSwarm = (array[15] as Number).toInt(),
                availability = (array[16] as Number).toLong(),
                queueOrder = (array[17] as Number).toInt(),
                remaining = (array[18] as Number).toLong()
            )
        }
    }

    /**
     * Get progress as percentage (0-100)
     */
    fun getProgressPercent(): Double = progress / 10.0

    /**
     * Get ratio as decimal
     */
    fun getRatioDecimal(): Double = ratio / 1000.0

    /**
     * Check if torrent is started
     */
    fun isStarted(): Boolean = (status and 1) != 0

    /**
     * Check if torrent is checking
     */
    fun isChecking(): Boolean = (status and 2) != 0

    /**
     * Check if torrent has error
     */
    fun hasError(): Boolean = (status and 16) != 0

    /**
     * Check if torrent is paused
     */
    fun isPaused(): Boolean = (status and 32) != 0

    /**
     * Check if torrent is queued
     */
    fun isQueued(): Boolean = (status and 64) != 0

    /**
     * Check if torrent is loaded
     */
    fun isLoaded(): Boolean = (status and 128) != 0
}

/**
 * uTorrent file model
 * Array format: [name, size, downloaded, priority]
 */
data class UTorrentFile(
    val name: String,
    val size: Long,
    val downloaded: Long,
    val priority: Int
) {
    companion object {
        /**
         * Parse file from JSON array
         */
        fun fromArray(array: List<Any>): UTorrentFile {
            return UTorrentFile(
                name = array[0] as String,
                size = (array[1] as Number).toLong(),
                downloaded = (array[2] as Number).toLong(),
                priority = (array[3] as Number).toInt()
            )
        }

        // Priority constants
        const val PRIORITY_DONT_DOWNLOAD = 0
        const val PRIORITY_LOW = 1
        const val PRIORITY_NORMAL = 2
        const val PRIORITY_HIGH = 3
    }

    /**
     * Get download progress as percentage
     */
    fun getProgressPercent(): Double = if (size > 0) (downloaded.toDouble() / size * 100) else 0.0
}

/**
 * uTorrent label model
 */
data class UTorrentLabel(
    val name: String,
    val torrentCount: Int
) {
    companion object {
        /**
         * Parse label from JSON array
         */
        fun fromArray(array: List<Any>): UTorrentLabel {
            return UTorrentLabel(
                name = array[0] as String,
                torrentCount = (array[1] as Number).toInt()
            )
        }
    }
}

/**
 * uTorrent RSS feed model
 */
data class UTorrentRssFeed(
    val id: Int,
    val enabled: Boolean,
    val useUrl: Boolean,
    val customAlias: String,
    val subscribeToDownloads: Boolean,
    val smartFilterEnabled: Boolean,
    val url: String,
    val alias: String
) {
    companion object {
        /**
         * Parse RSS feed from JSON array
         */
        fun fromArray(array: List<Any>): UTorrentRssFeed {
            return UTorrentRssFeed(
                id = (array[0] as Number).toInt(),
                enabled = (array[1] as Number).toInt() == 1,
                useUrl = (array[2] as Number).toInt() == 1,
                customAlias = array[3] as String,
                subscribeToDownloads = (array[4] as Number).toInt() == 1,
                smartFilterEnabled = (array[5] as Number).toInt() == 1,
                url = array[6] as String,
                alias = array[7] as String
            )
        }
    }
}

/**
 * uTorrent RSS filter model
 */
data class UTorrentRssFilter(
    val id: Int,
    val flags: Int,
    val name: String,
    val filter: String,
    val notFilter: String,
    val directory: String,
    val feed: Int,
    val quality: Int,
    val label: String,
    val postponeMode: Boolean,
    val lastMatch: Long,
    val smartEpFilter: Boolean,
    val repackEpFilter: Boolean,
    val episodeFilter: String
) {
    companion object {
        /**
         * Parse RSS filter from JSON array
         */
        fun fromArray(array: List<Any>): UTorrentRssFilter {
            return UTorrentRssFilter(
                id = (array[0] as Number).toInt(),
                flags = (array[1] as Number).toInt(),
                name = array[2] as String,
                filter = array[3] as String,
                notFilter = array[4] as String,
                directory = array[5] as String,
                feed = (array[6] as Number).toInt(),
                quality = (array[7] as Number).toInt(),
                label = array[8] as String,
                postponeMode = (array[9] as Number).toInt() == 1,
                lastMatch = (array[10] as Number).toLong(),
                smartEpFilter = (array[11] as Number).toInt() == 1,
                repackEpFilter = (array[12] as Number).toInt() == 1,
                episodeFilter = array[13] as String
            )
        }
    }
}

/**
 * uTorrent settings/preferences
 */
data class UTorrentSettings(
    val settings: Map<String, Setting>
) {
    data class Setting(
        val type: Int,
        val value: Any,
        val access: String
    )
}

/**
 * uTorrent Web UI response
 */
data class UTorrentResponse(
    @SerializedName("torrents")
    val torrents: List<List<Any>>? = null,

    @SerializedName("torrentp")
    val torrentProperties: List<List<Any>>? = null,

    @SerializedName("files")
    val files: List<List<Any>>? = null,

    @SerializedName("label")
    val labels: List<List<Any>>? = null,

    @SerializedName("rssfeeds")
    val rssFeeds: List<List<Any>>? = null,

    @SerializedName("rssfilters")
    val rssFilters: List<List<Any>>? = null,

    @SerializedName("settings")
    val settings: List<List<Any>>? = null,

    @SerializedName("build")
    val build: Int? = null,

    @SerializedName("torrentc")
    val torrentCacheId: String? = null
) {
    /**
     * Parse torrents from response
     */
    fun parseTorrents(): List<UTorrentTorrent> {
        return torrents?.map { UTorrentTorrent.fromArray(it) } ?: emptyList()
    }

    /**
     * Parse files from response
     */
    fun parseFiles(): List<UTorrentFile> {
        return files?.flatMap { it.drop(1) as List<List<Any>> }
            ?.map { UTorrentFile.fromArray(it) } ?: emptyList()
    }

    /**
     * Parse labels from response
     */
    fun parseLabels(): List<UTorrentLabel> {
        return labels?.map { UTorrentLabel.fromArray(it) } ?: emptyList()
    }

    /**
     * Parse RSS feeds from response
     */
    fun parseRssFeeds(): List<UTorrentRssFeed> {
        return rssFeeds?.map { UTorrentRssFeed.fromArray(it) } ?: emptyList()
    }

    /**
     * Parse RSS filters from response
     */
    fun parseRssFilters(): List<UTorrentRssFilter> {
        return rssFilters?.map { UTorrentRssFilter.fromArray(it) } ?: emptyList()
    }
}
