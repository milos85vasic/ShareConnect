package com.shareconnect.plexconnect.data.repository

import android.util.Log
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.local.*
import com.shareconnect.plexconnect.data.model.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.time.Instant

/**
 * Implementation of PlexRepository with local database caching
 * @param apiClient Plex API client for network operations
 * @param serverDao DAO for server-related database operations
 * @param libraryDao DAO for library-related database operations
 * @param mediaItemDao DAO for media item-related database operations
 */
class PlexRepositoryImpl(
    private val apiClient: PlexApiClient,
    private val serverDao: PlexServerDao,
    private val libraryDao: PlexLibraryDao,
    private val mediaItemDao: PlexMediaItemDao
) : PlexRepository {
    
    private val mutex = Mutex()
    private val tag = "PlexRepositoryImpl"

    override fun getServerInfo(serverUrl: String, token: String): Flow<PlexServerInfo> = flow {
        // First, try to get from API
        val apiResult = apiClient.getServerInfo(serverUrl)
        
        apiResult.onSuccess { serverInfo ->
            emit(serverInfo)
            
            // Cache the server info
            val serverEntity = PlexServerEntity(
                id = serverInfo.machineIdentifier ?: "unknown",
                name = serverInfo.myPlexUsername ?: "Unnamed Server",
                host = serverUrl.removePrefix("http://").removePrefix("https://").split(":").first(),
                port = serverUrl.split(":").lastOrNull()?.toIntOrNull() ?: 32400,
                lastSyncTime = Instant.now()
            )
            serverDao.insertServer(serverEntity)
        }.onFailure { exception ->
            // If API fails, try to get from local database
            serverDao.getServerById(serverUrl.hashCode().toString())
                .collect { cachedServer ->
                    if (cachedServer != null) {
                        emit(PlexServerInfo(
                            machineIdentifier = cachedServer.id,
                            myPlexUsername = cachedServer.name,
                            version = null,
                            platform = null,
                            platformVersion = null
                        ))
                    } else {
                        // If both API and cache fail, throw the original exception
                        throw exception
                    }
                }
        }
    }.catch { e ->
        Log.e(tag, "Error in getServerInfo", e)
        throw e
    }

    override fun getLibraries(serverUrl: String, token: String): Flow<List<PlexLibrarySection>> = flow {
        // First, try to get from API
        val apiResult = apiClient.getLibraries(serverUrl, token)
        
        apiResult.onSuccess { libraries ->
            emit(libraries)
            
            // Cache the libraries
            val libraryEntities = libraries.map { library ->
                PlexLibraryEntity(
                    id = library.key,
                    serverId = serverUrl.hashCode().toString(),
                    title = library.title,
                    type = library.type,
                    lastSyncTime = Instant.now()
                )
            }
            libraryDao.insertLibraries(libraryEntities)
        }.onFailure { exception ->
            // If API fails, try to get from local database
            libraryDao.getLibrariesForServer(serverUrl.hashCode().toString())
                .collect { cachedLibraries ->
                    if (cachedLibraries.isNotEmpty()) {
                        val libraries = cachedLibraries.map { library ->
                            PlexLibrarySection(
                                key = library.id,
                                title = library.title,
                                type = library.type
                            )
                        }
                        emit(libraries)
                    } else {
                        // If both API and cache fail, throw the original exception
                        throw exception
                    }
                }
        }
    }.catch { e ->
        Log.e(tag, "Error in getLibraries", e)
        throw e
    }

    override fun getLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int,
        offset: Int
    ): Flow<List<PlexMediaItem>> = flow {
        // First, try to get from API
        val apiResult = apiClient.getLibraryItems(serverUrl, sectionKey, token, limit, offset)
        
        apiResult.onSuccess { items ->
            emit(items)
            
            // Cache the media items
            val mediaItemEntities = items.map { item ->
                PlexMediaItemEntity(
                    id = item.ratingKey ?: item.guid ?: "unknown",
                    libraryId = sectionKey,
                    title = item.title ?: "Unknown Title",
                    type = item.type ?: "unknown",
                    year = item.year,
                    summary = item.summary,
                    thumbnailUrl = null, // TODO: Add thumbnail URL extraction
                    lastSyncTime = Instant.now()
                )
            }
            mediaItemDao.insertMediaItems(mediaItemEntities)
        }.onFailure { exception ->
            // If API fails, try to get from local database
            mediaItemDao.getMediaItemsForLibrary(sectionKey)
                .collect { cachedItems ->
                    if (cachedItems.isNotEmpty()) {
                        val mediaItems = cachedItems.map { item ->
                            PlexMediaItem(
                                ratingKey = item.id,
                                key = item.id,
                                guid = item.id,
                                title = item.title,
                                type = item.type,
                                year = item.year,
                                summary = item.summary
                            )
                        }
                        emit(mediaItems)
                    } else {
                        // If both API and cache fail, throw the original exception
                        throw exception
                    }
                }
        }
    }.catch { e ->
        Log.e(tag, "Error in getLibraryItems", e)
        throw e
    }

    override fun searchMedia(
        serverUrl: String,
        query: String,
        token: String,
        limit: Int
    ): Flow<List<PlexMediaItem>> = flow {
        // Search via API
        val apiResult = apiClient.search(serverUrl, query, token, limit)
        
        apiResult.onSuccess { items ->
            emit(items)
            
            // Cache search results
            val mediaItemEntities = items.map { item ->
                PlexMediaItemEntity(
                    id = item.ratingKey ?: item.guid ?: "unknown",
                    libraryId = "search_results", // Special library for search results
                    title = item.title ?: "Unknown Title",
                    type = item.type ?: "unknown",
                    year = item.year,
                    summary = item.summary,
                    thumbnailUrl = null, // TODO: Add thumbnail URL extraction
                    lastSyncTime = Instant.now()
                )
            }
            mediaItemDao.insertMediaItems(mediaItemEntities)
        }.onFailure { exception ->
            // If API fails, try local search (basic implementation)
            mediaItemDao.getMediaItemsForLibrary("search_results")
                .collect { cachedItems ->
                    if (cachedItems.isNotEmpty()) {
                        val mediaItems = cachedItems
                            .filter { it.title.contains(query, ignoreCase = true) }
                            .map { item ->
                                PlexMediaItem(
                                    ratingKey = item.id,
                                    key = item.id,
                                    guid = item.id,
                                    title = item.title,
                                    type = item.type,
                                    year = item.year,
                                    summary = item.summary
                                )
                            }
                        emit(mediaItems)
                    } else {
                        // If both API and cache fail, throw the original exception
                        throw exception
                    }
                }
        }
    }.catch { e ->
        Log.e(tag, "Error in searchMedia", e)
        throw e
    }

    override suspend fun markAsPlayed(serverUrl: String, key: String, token: String) {
        mutex.withLock {
            // Mark as played in Plex
            val apiResult = apiClient.markAsPlayed(serverUrl, key, token)
            
            apiResult.onSuccess {
                // Update local database
                mediaItemDao.markAsWatched(key, Instant.now().toEpochMilli())
            }.onFailure { exception ->
                Log.e(tag, "Error marking as played", exception)
                throw exception
            }
        }
    }

    override suspend fun markAsUnplayed(serverUrl: String, key: String, token: String) {
        mutex.withLock {
            // Mark as unplayed in Plex
            val apiResult = apiClient.markAsUnplayed(serverUrl, key, token)
            
            apiResult.onSuccess {
                // Update local database
                mediaItemDao.markAsUnwatched(key)
            }.onFailure { exception ->
                Log.e(tag, "Error marking as unplayed", exception)
                throw exception
            }
        }
    }

    override suspend fun updateProgress(
        serverUrl: String,
        key: String,
        time: Long,
        token: String
    ) {
        mutex.withLock {
            // Update progress in Plex
            val apiResult = apiClient.updateProgress(serverUrl, key, time, token)
            
            apiResult.onSuccess {
                // Here you might want to update local database with progress
                // This is a placeholder - you'd need to add a progress column to PlexMediaItemEntity
                Log.d(tag, "Progress updated for $key at $time")
            }.onFailure { exception ->
                Log.e(tag, "Error updating progress", exception)
                throw exception
            }
        }
    }

    override suspend fun sync(serverUrl: String, token: String) {
        mutex.withLock {
            try {
                // Sync servers
                val serversResult = apiClient.getServerInfo(serverUrl)
                serversResult.onSuccess { serverInfo ->
                    val serverEntity = PlexServerEntity(
                        id = serverInfo.machineIdentifier ?: "unknown",
                        name = serverInfo.myPlexUsername ?: "Unnamed Server",
                        host = serverUrl.removePrefix("http://").removePrefix("https://").split(":").first(),
                        port = serverUrl.split(":").lastOrNull()?.toIntOrNull() ?: 32400,
                        lastSyncTime = Instant.now()
                    )
                    serverDao.insertServer(serverEntity)
                }

                // Sync libraries
                val librariesResult = apiClient.getLibraries(serverUrl, token)
                librariesResult.onSuccess { libraries ->
                    val libraryEntities = libraries.map { library ->
                        PlexLibraryEntity(
                            id = library.key,
                            serverId = serverUrl.hashCode().toString(),
                            title = library.title,
                            type = library.type,
                            lastSyncTime = Instant.now()
                        )
                    }
                    libraryDao.insertLibraries(libraryEntities)

                    // Sync media items for each library
                    libraries.forEach { library ->
                        val itemsResult = apiClient.getLibraryItems(serverUrl, library.key, token)
                        itemsResult.onSuccess { items ->
                            val mediaItemEntities = items.map { item ->
                                PlexMediaItemEntity(
                                    id = item.ratingKey ?: item.guid ?: "unknown",
                                    libraryId = library.key,
                                    title = item.title ?: "Unknown Title",
                                    type = item.type ?: "unknown",
                                    year = item.year,
                                    summary = item.summary,
                                    thumbnailUrl = null, // TODO: Add thumbnail URL extraction
                                    lastSyncTime = Instant.now()
                                )
                            }
                            mediaItemDao.insertMediaItems(mediaItemEntities)
                        }
                    }
                }

                Log.d(tag, "Comprehensive sync completed successfully")
            } catch (e: Exception) {
                Log.e(tag, "Comprehensive sync failed", e)
                throw e
            }
        }
    }
}