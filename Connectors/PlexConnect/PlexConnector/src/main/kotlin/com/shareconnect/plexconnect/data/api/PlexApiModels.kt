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
enum class LibraryType {
    MOVIE,
    TV_SHOW,
    MUSIC,
    PHOTO,
    MIXED
}

/**
 * Represents a media item in a Plex library
 */
@Serializable
data class PlexMediaItem(
    val id: String,
    val libraryId: String,
    val title: String,
    val type: MediaType,
    val year: Int? = null,
    val summary: String? = null,
    val thumbnailUrl: String? = null
)

/**
 * Represents different types of media in Plex
 */
enum class MediaType {
    MOVIE,
    TV_SHOW,
    EPISODE,
    SEASON,
    MUSIC_TRACK,
    ALBUM,
    ARTIST,
    PHOTO
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