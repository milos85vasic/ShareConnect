package com.shareconnect.plexconnect.data.repository

import android.util.Log
import com.shareconnect.plexconnect.data.api.*
import com.shareconnect.plexconnect.data.database.dao.PlexLibraryDao
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.database.dao.PlexServerDao
import com.shareconnect.plexconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

/**
 * Implementation of PlexRepository with offline-first caching strategy
 */
class PlexRepositoryImpl(
    private val apiClient: PlexApiClient,
    private val serverDao: PlexServerDao,
    private val libraryDao: PlexLibraryDao,
    private val mediaItemDao: PlexMediaItemDao
) : PlexRepository {

    private val tag = "PlexRepositoryImpl"

    override fun getServerInfo(serverUrl: String, token: String): Flow<com.shareconnect.plexconnect.data.model.PlexServerInfo> = flow {
        emit(getServerInfoFromCache(serverUrl))
        
        try {
            val result = apiClient.getServerInfo(serverUrl)
            if (result.isSuccess) {
                val apiServerInfo = result.getOrThrow()
                val modelServerInfo = mapApiServerInfoToModel(apiServerInfo)
                updateServerInfoInCache(serverUrl, modelServerInfo)
                emit(modelServerInfo)
            } else {
                Log.e(tag, "Failed to fetch server info: ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching server info", e)
        }
    }

    override fun getLibraries(serverUrl: String, token: String): Flow<List<PlexLibrarySection>> = flow {
        // Emit cached libraries first
        emit(getLibrariesFromCache(serverUrl))
        
        try {
            val result = apiClient.getLibraries(serverUrl, token)
            if (result.isSuccess) {
                val apiLibraries = result.getOrThrow()
                val modelLibraries = apiLibraries.map { apiLibrary ->
                    mapApiLibraryToModel(apiLibrary)
                }
                updateLibrariesInCache(serverUrl, modelLibraries)
                val librarySections = modelLibraries.map { library ->
                    com.shareconnect.plexconnect.data.model.PlexLibrarySection(
                        key = library.key,
                        title = library.title,
                        type = when (library.type.value) {
                            "movie" -> com.shareconnect.plexconnect.data.model.LibraryType.MOVIE
                            "show" -> com.shareconnect.plexconnect.data.model.LibraryType.SHOW
                            "artist" -> com.shareconnect.plexconnect.data.model.LibraryType.MUSIC
                            "photo" -> com.shareconnect.plexconnect.data.model.LibraryType.PHOTO
                            else -> com.shareconnect.plexconnect.data.model.LibraryType.UNKNOWN
                        }
                    )
                }
                emit(librarySections)
            } else {
                Log.e(tag, "Failed to fetch libraries: ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching libraries", e)
        }
    }

    override fun getLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int,
        offset: Int,
        filter: PlexMediaFilter?
    ): Flow<List<com.shareconnect.plexconnect.data.model.PlexMediaItem>> = flow {
        // Emit cached items first
        emit(getLibraryItemsFromCache(sectionKey, limit, offset, filter))
        
        try {
            val result = apiClient.getLibraryItems(serverUrl, sectionKey, token, limit, offset)
            if (result.isSuccess) {
                val apiMediaItems = result.getOrThrow()
                val modelMediaItems = apiMediaItems.map { apiItem ->
                    mapApiMediaItemToModel(apiItem)
                }
                updateMediaItemsInCache(modelMediaItems)
                emit(applyFilter(modelMediaItems, filter))
            } else {
                Log.e(tag, "Failed to fetch library items: ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error fetching library items", e)
        }
    }

    override fun searchMedia(
        serverUrl: String,
        query: String,
        token: String,
        limit: Int
    ): Flow<List<com.shareconnect.plexconnect.data.model.PlexMediaItem>> = flow {
        // Emit cached search results first
        emit(getSearchResultsFromCache(query))
        
        try {
            val result = apiClient.search(serverUrl, query, token, limit)
            if (result.isSuccess) {
                val apiMediaItems = result.getOrThrow()
                val modelMediaItems = apiMediaItems.map { apiItem ->
                    mapApiMediaItemToModel(apiItem)
                }
                updateMediaItemsInCache(modelMediaItems)
                emit(modelMediaItems)
            } else {
                Log.e(tag, "Failed to search media: ${result.exceptionOrNull()}")
            }
        } catch (e: Exception) {
            Log.e(tag, "Error searching media", e)
        }
    }

    override suspend fun markAsPlayed(serverUrl: String, key: String, token: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = apiClient.markAsPlayed(serverUrl, key, token)
                if (result.isSuccess) {
                    updateMediaItemPlayState(key, true, 0)
                } else {
                    Log.e(tag, "Failed to mark as played: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error marking as played", e)
            }
        }
    }

    override suspend fun markAsUnplayed(serverUrl: String, key: String, token: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = apiClient.markAsUnplayed(serverUrl, key, token)
                if (result.isSuccess) {
                    updateMediaItemPlayState(key, false, 0)
                } else {
                    Log.e(tag, "Failed to mark as unplayed: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error marking as unplayed", e)
            }
        }
    }

    override suspend fun updateProgress(serverUrl: String, key: String, time: Long, token: String) {
        withContext(Dispatchers.IO) {
            try {
                val result = apiClient.updateProgress(serverUrl, key, time, token)
                if (result.isSuccess) {
                    updateMediaItemProgress(key, time)
                } else {
                    Log.e(tag, "Failed to update progress: ${result.exceptionOrNull()}")
                }
            } catch (e: Exception) {
                Log.e(tag, "Error updating progress", e)
            }
        }
    }

    override suspend fun sync(serverUrl: String, token: String) {
        withContext(Dispatchers.IO) {
            try {
                Log.d(tag, "Starting sync for server: $serverUrl")
                
                // Get server info
                val serverResult = apiClient.getServerInfo(serverUrl)
                if (serverResult.isSuccess) {
                    val apiServerInfo = serverResult.getOrThrow()
                    val modelServerInfo = mapApiServerInfoToModel(apiServerInfo)
                    updateServerInfoInCache(serverUrl, modelServerInfo)
                }
                
                // Get and sync libraries
                val librariesResult = apiClient.getLibraries(serverUrl, token)
                if (librariesResult.isSuccess) {
                    val apiLibraries = librariesResult.getOrThrow()
                    val modelLibraries = apiLibraries.map { apiLibrary ->
                        mapApiLibraryToModel(apiLibrary)
                    }
                    updateLibrariesInCache(serverUrl, modelLibraries)
                    
                    // Sync each library
                    modelLibraries.forEach { library ->
                        try {
                            val itemsResult = apiClient.getLibraryItems(serverUrl, library.key, token, 1000, 0)
                            if (itemsResult.isSuccess) {
                                val apiMediaItems = itemsResult.getOrThrow()
                                val modelMediaItems = apiMediaItems.map { apiItem ->
                                    mapApiMediaItemToModel(apiItem)
                                }
                                updateMediaItemsInCache(modelMediaItems)
                            }
                        } catch (e: Exception) {
                            Log.e(tag, "Error syncing library ${library.title}", e)
                        }
                    }
                }
                
                Log.d(tag, "Sync completed for server: $serverUrl")
            } catch (e: Exception) {
                Log.e(tag, "Error during sync", e)
            }
        }
    }

    // Private helper methods for caching and mapping
    
    private fun mapApiServerInfoToModel(apiServerInfo: com.shareconnect.plexconnect.data.api.PlexServerInfo): com.shareconnect.plexconnect.data.model.PlexServerInfo {
        return com.shareconnect.plexconnect.data.model.PlexServerInfo(
            machineIdentifier = apiServerInfo.machineIdentifier,
            version = apiServerInfo.version,
            myPlexUsername = apiServerInfo.name,
            platform = apiServerInfo.host,
            platformVersion = null
        )
    }
    
    private fun mapApiLibraryToModel(apiLibrary: com.shareconnect.plexconnect.data.api.PlexLibrary): com.shareconnect.plexconnect.data.model.PlexLibrary {
        return com.shareconnect.plexconnect.data.model.PlexLibrary(
            key = apiLibrary.id,
            title = apiLibrary.title,
            type = when (apiLibrary.type.value) {
                "movie" -> com.shareconnect.plexconnect.data.model.LibraryType.MOVIE
                "show" -> com.shareconnect.plexconnect.data.model.LibraryType.SHOW
                "artist" -> com.shareconnect.plexconnect.data.model.LibraryType.MUSIC
                "photo" -> com.shareconnect.plexconnect.data.model.LibraryType.PHOTO
                else -> com.shareconnect.plexconnect.data.model.LibraryType.UNKNOWN
            },
            serverId = apiLibrary.serverId.toLongOrNull() ?: 0L
        )
    }
    
    private fun mapApiMediaItemToModel(apiMediaItem: com.shareconnect.plexconnect.data.api.PlexMediaItem): com.shareconnect.plexconnect.data.model.PlexMediaItem {
        return com.shareconnect.plexconnect.data.model.PlexMediaItem(
            ratingKey = apiMediaItem.ratingKey ?: "",
            key = apiMediaItem.key ?: "",
            guid = apiMediaItem.guid,
            librarySectionTitle = apiMediaItem.librarySectionTitle,
            librarySectionID = apiMediaItem.librarySectionID,
            librarySectionKey = apiMediaItem.libraryId,
            type = when (apiMediaItem.type?.value) {
                "movie" -> com.shareconnect.plexconnect.data.model.MediaType.MOVIE
                "episode" -> com.shareconnect.plexconnect.data.model.MediaType.EPISODE
                "season" -> com.shareconnect.plexconnect.data.model.MediaType.SEASON
                "show" -> com.shareconnect.plexconnect.data.model.MediaType.SHOW
                "artist" -> com.shareconnect.plexconnect.data.model.MediaType.ARTIST
                "album" -> com.shareconnect.plexconnect.data.model.MediaType.ALBUM
                "track" -> com.shareconnect.plexconnect.data.model.MediaType.TRACK
                "photo" -> com.shareconnect.plexconnect.data.model.MediaType.PHOTO
                else -> com.shareconnect.plexconnect.data.model.MediaType.UNKNOWN
            },
            title = apiMediaItem.title ?: "",
            titleSort = apiMediaItem.titleSort,
            summary = apiMediaItem.summary,
            rating = null, // API model doesn't have rating field, set to null for now
            year = apiMediaItem.year,
            duration = apiMediaItem.duration,
            addedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            studio = apiMediaItem.studio,
            serverId = 0L // This should be set based on context
        )
    }
    
    private suspend fun getServerInfoFromCache(serverUrl: String): com.shareconnect.plexconnect.data.model.PlexServerInfo {
        return try {
            val server = serverDao.getServerByMachineIdentifier(serverUrl.hashCode().toString())
            if (server != null) {
                com.shareconnect.plexconnect.data.model.PlexServerInfo(
                    machineIdentifier = server.machineIdentifier,
                    version = server.version,
                    myPlexUsername = server.name,
                    platform = server.address,
                    platformVersion = null
                )
            } else {
                com.shareconnect.plexconnect.data.model.PlexServerInfo()
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting server info from cache", e)
            com.shareconnect.plexconnect.data.model.PlexServerInfo()
        }
    }

    private suspend fun updateServerInfoInCache(serverUrl: String, serverInfo: com.shareconnect.plexconnect.data.model.PlexServerInfo) {
        try {
            val server = com.shareconnect.plexconnect.data.model.PlexServer(
                id = 0, // Room will handle ID
                name = serverInfo.myPlexUsername ?: "Plex Server",
                address = serverUrl,
                port = 32400,
                token = null, // This should be managed separately
                isLocal = true,
                isOwned = false,
                ownerId = null,
                machineIdentifier = serverInfo.machineIdentifier ?: "",
                version = serverInfo.version,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis()
            )
            serverDao.insertServer(server)
        } catch (e: Exception) {
            Log.e(tag, "Error updating server info in cache", e)
        }
    }

    private suspend fun getLibrariesFromCache(serverUrl: String): List<com.shareconnect.plexconnect.data.model.PlexLibrarySection> {
        return try {
            val server = serverDao.getServerByMachineIdentifier(serverUrl.hashCode().toString())
            if (server != null) {
                val libraries = libraryDao.getLibrariesForServer(server.id).first()
                libraries.map { library ->
                    com.shareconnect.plexconnect.data.model.PlexLibrarySection(
                        key = library.key,
                        title = library.title,
                        type = when (library.type.value) {
                            "movie" -> com.shareconnect.plexconnect.data.model.LibraryType.MOVIE
                            "show" -> com.shareconnect.plexconnect.data.model.LibraryType.SHOW
                            "artist" -> com.shareconnect.plexconnect.data.model.LibraryType.MUSIC
                            "photo" -> com.shareconnect.plexconnect.data.model.LibraryType.PHOTO
                            else -> com.shareconnect.plexconnect.data.model.LibraryType.UNKNOWN
                        }
                    )
                }
            } else emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Error getting libraries from cache", e)
            emptyList()
        }
    }

    private suspend fun updateLibrariesInCache(serverUrl: String, libraries: List<com.shareconnect.plexconnect.data.model.PlexLibrary>) {
        try {
            val server = serverDao.getServerByMachineIdentifier(serverUrl.hashCode().toString()) ?: return
            libraries.forEach { library ->
                libraryDao.insertLibrary(library)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating libraries in cache", e)
        }
    }

    private suspend fun getLibraryItemsFromCache(
        sectionKey: String,
        limit: Int,
        offset: Int,
        filter: PlexMediaFilter?
    ): List<com.shareconnect.plexconnect.data.model.PlexMediaItem> {
        return try {
            val library = libraryDao.getLibraryByKey(sectionKey)
            if (library != null) {
                val items = mediaItemDao.getMediaItemsForLibrary(library.key.toLongOrNull() ?: 0L).first()
                val filteredItems = applyFilter(items, filter)
                // Apply pagination
                filteredItems.drop(offset).take(limit)
            } else emptyList()
        } catch (e: Exception) {
            Log.e(tag, "Error getting library items from cache", e)
            emptyList()
        }
    }

    private suspend fun updateMediaItemsInCache(mediaItems: List<com.shareconnect.plexconnect.data.model.PlexMediaItem>) {
        try {
            mediaItems.forEach { item ->
                mediaItemDao.insertMediaItem(item)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating media items in cache", e)
        }
    }

    private suspend fun getSearchResultsFromCache(query: String): List<com.shareconnect.plexconnect.data.model.PlexMediaItem> {
        return try {
            // Use first() operator to get the first value from the Flow
            mediaItemDao.searchMediaItems(query).first()
        } catch (e: Exception) {
            Log.e(tag, "Error getting search results from cache", e)
            emptyList()
        }
    }

    private suspend fun updateMediaItemPlayState(key: String, isPlayed: Boolean, viewOffset: Long) {
        try {
            val mediaItem = mediaItemDao.getMediaItemByKey(key)
            mediaItem?.let { item ->
                val updatedItem = item.copy(
                    viewCount = if (isPlayed) (item.viewCount + 1) else 0,
                    lastViewedAt = if (isPlayed) System.currentTimeMillis() else null,
                    viewOffset = viewOffset,
                    updatedAt = System.currentTimeMillis()
                )
                mediaItemDao.updateMediaItem(updatedItem)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating media item play state", e)
        }
    }

    private suspend fun updateMediaItemProgress(key: String, time: Long) {
        try {
            val mediaItem = mediaItemDao.getMediaItemByKey(key)
            mediaItem?.let { item ->
                val updatedItem = item.copy(
                    viewOffset = time,
                    updatedAt = System.currentTimeMillis()
                )
                mediaItemDao.updateMediaItem(updatedItem)
            }
        } catch (e: Exception) {
            Log.e(tag, "Error updating media item progress", e)
        }
    }

    private fun applyFilter(items: List<com.shareconnect.plexconnect.data.model.PlexMediaItem>, filter: PlexMediaFilter?): List<com.shareconnect.plexconnect.data.model.PlexMediaItem> {
        if (filter == null) return items
        
        return items.filter { item ->
            // Filter by media type
            if (filter.type != null && item.type != filter.type) {
                return@filter false
            }
            
            // Filter by year range
            if (filter.year != null && item.year != null) {
                if (item.year !in filter.year.start..filter.year.endInclusive) {
                    return@filter false
                }
            }
            
            // Filter by rating
            if (filter.rating != null && item.rating != null) {
                if (filter.rating.minRating != null && item.rating < filter.rating.minRating.toFloat()) {
                    return@filter false
                }
                if (filter.rating.maxRating != null && item.rating > filter.rating.maxRating.toFloat()) {
                    return@filter false
                }
            }
            
            // Filter by genres
            if (!filter.genre.isNullOrEmpty()) {
                val hasMatchingGenre = filter.genre.any { genre ->
                    item.genres.contains(genre)
                }
                if (!hasMatchingGenre) {
                    return@filter false
                }
            }
            
            // Filter by watched status
            if (filter.watchStatus != null) {
                when (filter.watchStatus) {
                    PlexMediaFilter.WatchStatus.WATCHED -> if (!item.isWatched) return@filter false
                    PlexMediaFilter.WatchStatus.UNWATCHED -> if (item.isWatched) return@filter false
                    PlexMediaFilter.WatchStatus.IN_PROGRESS -> if (!item.isPartiallyWatched) return@filter false
                }
            }
            
            true
        }
    }
}