package com.shareconnect.utils

import android.net.Uri
import com.shareconnect.ServerProfile

/**
 * Utility class for determining URL type and profile compatibility
 */
object UrlCompatibilityUtils {

    enum class UrlType {
        STREAMING,      // YouTube, Vimeo, Twitch, etc.
        TORRENT,        // Magnet links and .torrent files
        DIRECT_DOWNLOAD // Direct HTTP/HTTPS download links
    }

    /**
     * Detect the type of URL being shared
     */
    fun detectUrlType(url: String?): UrlType? {
        if (url.isNullOrBlank()) return null

        return try {
            // Handle magnet links first (they don't parse well as URLs)
            if (url.startsWith("magnet:", ignoreCase = true)) {
                return UrlType.TORRENT
            }

            // Handle torrent files
            if (url.endsWith(".torrent", ignoreCase = true)) {
                return UrlType.TORRENT
            }

            val uri = Uri.parse(url)
            val scheme = uri.scheme?.lowercase()
            val host = uri.host?.lowercase()

            when {
                // Streaming platforms - check this before generic HTTP/HTTPS
                isStreamingUrl(host) && scheme in listOf("http", "https") -> UrlType.STREAMING

                // Direct download URLs (HTTP/HTTPS/FTP) - fallback for non-streaming URLs
                scheme in listOf("http", "https", "ftp") -> UrlType.DIRECT_DOWNLOAD

                else -> null
            }
        } catch (e: Exception) {
            // Fallback parsing for edge cases
            return when {
                url.startsWith("magnet:", ignoreCase = true) -> UrlType.TORRENT
                url.endsWith(".torrent", ignoreCase = true) -> UrlType.TORRENT
                url.startsWith("http://", ignoreCase = true) || url.startsWith("https://", ignoreCase = true) || url.startsWith("ftp://", ignoreCase = true) -> {
                    // Simple string-based host detection as fallback
                    val isStreaming = listOf(
                        "youtube.com", "www.youtube.com", "youtu.be", "m.youtube.com",
                        "vimeo.com", "www.vimeo.com",
                        "twitch.tv", "www.twitch.tv",
                        "reddit.com", "www.reddit.com",
                        "twitter.com", "www.twitter.com", "x.com", "www.x.com",
                        "instagram.com", "www.instagram.com",
                        "facebook.com", "www.facebook.com",
                        "soundcloud.com", "www.soundcloud.com",
                        "dailymotion.com", "www.dailymotion.com",
                        "bandcamp.com", "www.bandcamp.com"
                    ).any { host -> url.contains(host, ignoreCase = true) }

                    if (isStreaming) UrlType.STREAMING else UrlType.DIRECT_DOWNLOAD
                }
                else -> null
            }
        }
    }

    /**
     * Check if a host is a streaming platform
     */
    private fun isStreamingUrl(host: String?): Boolean {
        if (host == null) return false

        val streamingHosts = setOf(
            // Major platforms
            "youtube.com", "www.youtube.com", "m.youtube.com", "youtu.be",
            "vimeo.com", "www.vimeo.com", "player.vimeo.com",
            "twitch.tv", "www.twitch.tv", "m.twitch.tv",
            "reddit.com", "www.reddit.com",
            "twitter.com", "www.twitter.com", "x.com", "www.x.com",
            "instagram.com", "www.instagram.com",
            "facebook.com", "www.facebook.com", "fb.watch",
            "soundcloud.com", "www.soundcloud.com",
            "dailymotion.com", "www.dailymotion.com",
            "bandcamp.com", "www.bandcamp.com",
            "tiktok.com", "www.tiktok.com", "vm.tiktok.com",

            // Additional streaming services supported by YTDLP/MeTube
            "bilibili.com", "www.bilibili.com",
            "nicovideo.jp", "www.nicovideo.jp",
            "mixcloud.com", "www.mixcloud.com",
            "spotify.com", "www.spotify.com", "open.spotify.com",
            "deezer.com", "www.deezer.com",
            "tidal.com", "www.tidal.com",
            "music.youtube.com",

            // International platforms
            "youku.com", "www.youku.com",
            "qq.com", "v.qq.com",
            "iqiyi.com", "www.iqiyi.com",

            // News and media sites
            "cnn.com", "www.cnn.com",
            "bbc.com", "www.bbc.com",
            "australia.plus", "www.australia.plus"
        )

        return streamingHosts.contains(host)
    }

    /**
     * Check if a profile supports a specific URL type
     */
    fun isProfileCompatible(profile: ServerProfile, urlType: UrlType): Boolean {
        return when (profile.serviceType) {
            ServerProfile.TYPE_METUBE -> {
                // MeTube supports streaming URLs only
                urlType == UrlType.STREAMING
            }

            ServerProfile.TYPE_YTDL -> {
                // YT-DLP supports streaming and direct download URLs
                urlType in listOf(UrlType.STREAMING, UrlType.DIRECT_DOWNLOAD)
            }

            ServerProfile.TYPE_TORRENT -> {
                // Torrent clients support torrent URLs only
                urlType == UrlType.TORRENT
            }

            ServerProfile.TYPE_JDOWNLOADER -> {
                // jDownloader is a comprehensive download manager that supports:
                // - Direct downloads (HTTP/HTTPS/FTP)
                // - Premium link downloaders
                // - Container formats (DLC, RSDF, CCF, etc.)
                // - Streaming sites
                // - File hosting services
                // - And many other specialized download types
                // Basically everything except torrents
                urlType != UrlType.TORRENT
            }

            else -> false
        }
    }

    /**
     * Filter profiles to show only compatible ones for the given URL
     */
    fun filterCompatibleProfiles(profiles: List<ServerProfile>, url: String?): List<ServerProfile> {
        val urlType = detectUrlType(url) ?: return profiles

        return profiles.filter { profile ->
            isProfileCompatible(profile, urlType)
        }
    }

    /**
     * Get user-friendly description of what URL types a profile supports
     */
    fun getProfileSupportDescription(profile: ServerProfile): String {
        return when (profile.serviceType) {
            ServerProfile.TYPE_METUBE -> "Streaming videos from 1000+ sites (YouTube, Vimeo, TikTok, etc.)"
            ServerProfile.TYPE_YTDL -> "Streaming videos from 1000+ sites and direct downloads"
            ServerProfile.TYPE_TORRENT -> "Torrent files and magnet links"
            ServerProfile.TYPE_JDOWNLOADER -> "Downloads from any website (streaming, direct, premium links, etc.)"
            else -> "Unknown content types"
        }
    }

    /**
     * Get user-friendly description of the URL type
     */
    fun getUrlTypeDescription(urlType: UrlType): String {
        return when (urlType) {
            UrlType.STREAMING -> "streaming video"
            UrlType.TORRENT -> "torrent"
            UrlType.DIRECT_DOWNLOAD -> "direct download"
        }
    }
}