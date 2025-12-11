package com.shareconnect.plexconnect.data.repository

import com.shareconnect.plexconnect.data.model.PlexLibrarySection
import com.shareconnect.plexconnect.data.model.PlexMediaFilter
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServerInfo
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for Plex-related operations
 */
interface PlexRepository {
    /**
     * Retrieve server information
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     * @return Flow of server information
     */
    fun getServerInfo(serverUrl: String, token: String): Flow<PlexServerInfo>

    /**
     * Retrieve libraries from a Plex server
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     * @return Flow of library sections
     */
    fun getLibraries(serverUrl: String, token: String): Flow<List<PlexLibrarySection>>

    /**
     * Retrieve media items from a specific library
     * @param serverUrl URL of the Plex server
     * @param sectionKey Library section key
     * @param token Authentication token
     * @param limit Maximum number of items to retrieve
     * @param offset Offset for pagination
     * @return Flow of media items
     */
    fun getLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int = 50,
        offset: Int = 0,
        filter: PlexMediaFilter? = null
    ): Flow<List<PlexMediaItem>>

    /**
     * Search for media items across the Plex server
     * @param serverUrl URL of the Plex server
     * @param query Search query
     * @param token Authentication token
     * @param limit Maximum number of results
     * @return Flow of matching media items
     */
    fun searchMedia(
        serverUrl: String,
        query: String,
        token: String,
        limit: Int = 50
    ): Flow<List<PlexMediaItem>>

    /**
     * Mark a media item as played
     * @param serverUrl URL of the Plex server
     * @param key Unique identifier of the media item
     * @param token Authentication token
     */
    suspend fun markAsPlayed(serverUrl: String, key: String, token: String)

    /**
     * Mark a media item as unplayed
     * @param serverUrl URL of the Plex server
     * @param key Unique identifier of the media item
     * @param token Authentication token
     */
    suspend fun markAsUnplayed(serverUrl: String, key: String, token: String)

    /**
     * Update playback progress for a media item
     * @param serverUrl URL of the Plex server
     * @param key Unique identifier of the media item
     * @param time Current playback time
     * @param token Authentication token
     */
    suspend fun updateProgress(serverUrl: String, key: String, time: Long, token: String)

    /**
     * Synchronize local database with remote Plex server
     * @param serverUrl URL of the Plex server
     * @param token Authentication token
     */
    suspend fun sync(serverUrl: String, token: String)
}