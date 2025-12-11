package com.shareconnect.plexconnect.data.api

import kotlinx.serialization.Serializable

/**
 * Represents a Plex media server
 */
@Serializable
data class PlexServer(
    val id: String,
    val name: String,
    val host: String,
    val port: Int,
    val secure: Boolean = true
)

/**
 * Represents a media library in a Plex server
 */
@Serializable
data class PlexLibrary(
    val id: String,
    val serverId: String,
    val title: String,
    val type: LibraryType
)

/**
 * Represents different types of Plex media libraries
 */
enum class LibraryType(val value: String) {
    MOVIE("movie"),
    TV_SHOW("show"),
    MUSIC("artist"),
    PHOTO("photo"),
    MIXED("mixed");

    companion object {
        fun fromString(value: String): LibraryType {
            return entries.find { it.value == value } ?: MOVIE
        }
        
        fun fromValue(value: String): LibraryType {
            return fromString(value)
        }
    }
}

/**
 * Represents a media item in a Plex library
 */
@Serializable
data class PlexMediaItem(
    val ratingKey: String? = null,
    val key: String? = null,
    val guid: String? = null,
    val studio: String? = null,
    val type: MediaType? = null,
    val title: String? = null,
    val titleSort: String? = null,
    val summary: String? = null,
    val year: Int? = null,
    val duration: Long? = null,
    val librarySectionTitle: String? = null,
    val librarySectionID: Long? = null,
    // Compatibility with existing API
    val id: String? = null,
    val libraryId: String? = null,
    val thumbnailUrl: String? = null,
    val index: Int? = null
)

/**
 * Represents different types of media in Plex
 */
enum class MediaType(val value: String) {
    MOVIE("movie"),
    EPISODE("episode"),
    SEASON("season"),
    SHOW("show"),
    MUSIC_TRACK("track"),
    ALBUM("album"),
    ARTIST("artist"),
    PHOTO("photo"),
    MUSIC("music");  // For library type mapping

    companion object {
        fun fromString(value: String): MediaType {
            return entries.find { it.value == value } ?: MOVIE
        }
    }
}

/**
 * Represents user profile in Plex
 */
@Serializable
data class PlexUser(
    val id: String,
    val username: String,
    val email: String? = null,
    val thumb: String? = null,
    val isHomeUser: Boolean = false
)