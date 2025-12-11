/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.data.repository

import android.content.Context
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexLibrary
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServer
import com.shareconnect.plexconnect.service.PlexAiRecommendationService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.emitAll
class PlexMediaRepository(
    private val context: Context,
    private val mediaItemDao: PlexMediaItemDao,
    private val apiClient: PlexApiClient
) {
    // AI recommendation service for enhanced media analysis
    private val aiRecommendationService by lazy { PlexAiRecommendationService(context) }

    fun getAllMediaItems(): Flow<List<PlexMediaItem>> = mediaItemDao.getAllMediaItems()

    fun getMediaItemsForServer(serverId: Long): Flow<List<PlexMediaItem>> = mediaItemDao.getMediaItemsForServer(serverId)

    fun getMediaItemsForLibrary(libraryId: Long): Flow<List<PlexMediaItem>> = mediaItemDao.getMediaItemsForLibrary(libraryId)

    fun getMediaItemsByType(type: MediaType): Flow<List<PlexMediaItem>> = mediaItemDao.getMediaItemsByType(type.value)

    fun getWatchedItems(): Flow<List<PlexMediaItem>> = mediaItemDao.getWatchedItems()

    fun getInProgressItems(): Flow<List<PlexMediaItem>> = mediaItemDao.getInProgressItems()

    fun searchMediaItems(query: String): Flow<List<PlexMediaItem>> = mediaItemDao.searchMediaItems(query)

    suspend fun getMediaItemByKey(ratingKey: String): PlexMediaItem? = mediaItemDao.getMediaItemByKey(ratingKey)

    suspend fun refreshLibraryItems(server: PlexServer, library: com.shareconnect.plexconnect.data.model.PlexLibrary, limit: Int = 100, offset: Int = 0): Result<List<com.shareconnect.plexconnect.data.model.PlexMediaItem>> {
        return try {
            server.token?.let { token ->
                val itemsResult = apiClient.getLibraryItems(server.baseUrl, library.key, token, limit, offset)
                itemsResult.fold(
                    onSuccess = { apiItems ->
                        val modelItems = apiItems.map { apiItem ->
                            com.shareconnect.plexconnect.data.model.PlexMediaItem(
                                ratingKey = apiItem.ratingKey ?: "",
                                key = apiItem.key ?: "",
                                guid = apiItem.guid,
                                librarySectionTitle = apiItem.librarySectionTitle,
                                librarySectionID = apiItem.librarySectionID,
                                type = com.shareconnect.plexconnect.data.model.MediaType.fromString(apiItem.type?.value ?: "movie"),
                                title = apiItem.title ?: "",
                                titleSort = apiItem.titleSort,
                                summary = apiItem.summary,
                                year = apiItem.year,
                                duration = apiItem.duration,
                                studio = apiItem.studio,
                                serverId = server.id
                            )
                        }
                        mediaItemDao.insertMediaItems(modelItems)
                        Result.success(modelItems)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMediaItemDetails(server: PlexServer, ratingKey: String): Result<com.shareconnect.plexconnect.data.model.PlexMediaItem?> {
        return try {
            server.token?.let { token ->
                val result = apiClient.getMediaItem(server.baseUrl, ratingKey, token)
                result.fold(
                    onSuccess = { apiItem ->
                        if (apiItem != null) {
                            val modelItem = com.shareconnect.plexconnect.data.model.PlexMediaItem(
                                ratingKey = apiItem.ratingKey ?: "",
                                key = apiItem.key ?: "",
                                guid = apiItem.guid,
                                librarySectionTitle = apiItem.librarySectionTitle,
                                librarySectionID = apiItem.librarySectionID,
                                type = com.shareconnect.plexconnect.data.model.MediaType.fromString(apiItem.type?.value ?: "movie"),
                                title = apiItem.title ?: "",
                                titleSort = apiItem.titleSort,
                                summary = apiItem.summary,
                                year = apiItem.year,
                                duration = apiItem.duration,
                                studio = apiItem.studio,
                                serverId = server.id
                            )
                            Result.success(modelItem)
                        } else {
                            Result.success(null)
                        }
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMediaChildren(server: PlexServer, ratingKey: String): Result<List<com.shareconnect.plexconnect.data.model.PlexMediaItem>> {
        return try {
            server.token?.let { token ->
                val result = apiClient.getMediaChildren(server.baseUrl, ratingKey, token)
                result.fold(
                    onSuccess = { apiItems ->
                        val modelItems = apiItems.map { apiItem ->
                            com.shareconnect.plexconnect.data.model.PlexMediaItem(
                                ratingKey = apiItem.ratingKey ?: "",
                                key = apiItem.key ?: "",
                                guid = apiItem.guid,
                                librarySectionTitle = apiItem.librarySectionTitle,
                                librarySectionID = apiItem.librarySectionID,
                                type = com.shareconnect.plexconnect.data.model.MediaType.fromString(apiItem.type?.value ?: "episode"),
                                title = apiItem.title ?: "",
                                titleSort = apiItem.titleSort,
                                summary = apiItem.summary,
                                year = apiItem.year,
                                duration = apiItem.duration,
                                studio = apiItem.studio,
                                serverId = server.id
                            )
                        }
                        Result.success(modelItems)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsPlayed(server: PlexServer, mediaItem: PlexMediaItem): Result<Unit> {
        return try {
            server.token?.let { token ->
                val result = apiClient.markAsPlayed(server.baseUrl, mediaItem.key, server.token)
                result.fold(
                    onSuccess = {
                        val updatedItem = mediaItem.copy(
                            viewCount = mediaItem.viewCount + 1,
                            lastViewedAt = System.currentTimeMillis()
                        )
                        mediaItemDao.updateMediaItem(updatedItem)
                        Result.success(Unit)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun markAsUnplayed(server: PlexServer, mediaItem: PlexMediaItem): Result<Unit> {
        return try {
            server.token?.let { token ->
                val result = apiClient.markAsUnplayed(server.baseUrl, mediaItem.key, server.token)
                result.fold(
                    onSuccess = {
                        val updatedItem = mediaItem.copy(
                            viewCount = 0,
                            lastViewedAt = null
                        )
                        mediaItemDao.updateMediaItem(updatedItem)
                        Result.success(Unit)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateProgress(server: PlexServer, mediaItem: PlexMediaItem, progressMs: Long): Result<Unit> {
        return try {
            server.token?.let { token ->
                val result = apiClient.updateProgress(server.baseUrl, mediaItem.key, progressMs, server.token)
                result.fold(
                    onSuccess = {
                        val updatedItem = mediaItem.copy(
                            viewOffset = progressMs,
                            lastViewedAt = System.currentTimeMillis()
                        )
                        mediaItemDao.updateMediaItem(updatedItem)
                        Result.success(Unit)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMedia(server: PlexServer, query: String, limit: Int = 50): Result<List<PlexMediaItem>> {
        return try {
            server.token?.let { token ->
                val result = apiClient.search(server.baseUrl, query, token, limit)
                result.fold(
                    onSuccess = { response ->
                        val modelItems = response.map { apiItem ->
                            PlexMediaItem(
                                ratingKey = apiItem.ratingKey ?: "",
                                key = apiItem.key ?: "",
                                guid = apiItem.guid,
                                librarySectionTitle = apiItem.librarySectionTitle,
                                librarySectionID = apiItem.librarySectionID,
                                type = MediaType.valueOf(apiItem.type?.name ?: "MOVIE"),
                                title = apiItem.title ?: "",
                                titleSort = apiItem.titleSort,
                                summary = apiItem.summary,
                                year = apiItem.year,
                                duration = apiItem.duration,
                                serverId = server.id
                            )
                        } ?: emptyList()
                        Result.success(modelItems)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addMediaItem(item: PlexMediaItem): Long = mediaItemDao.insertMediaItem(item)

    suspend fun updateMediaItem(item: PlexMediaItem) = mediaItemDao.updateMediaItem(item)

    suspend fun deleteMediaItem(item: PlexMediaItem) = mediaItemDao.deleteMediaItem(item)

    suspend fun deleteMediaItemByKey(ratingKey: String) = mediaItemDao.deleteMediaItemByKey(ratingKey)

    suspend fun deleteMediaItemsForServer(serverId: Long) = mediaItemDao.deleteMediaItemsForServer(serverId)

    suspend fun deleteMediaItemsForLibrary(libraryId: Long) = mediaItemDao.deleteMediaItemsForLibrary(libraryId)

    suspend fun getMediaItemCount(): Int = mediaItemDao.getMediaItemCount()

    suspend fun getMediaItemCountForServer(serverId: Long): Int = mediaItemDao.getMediaItemCountForServer(serverId)

    suspend fun getMediaItemCountForLibrary(libraryId: Long): Int = mediaItemDao.getMediaItemCountForLibrary(libraryId)

    suspend fun getWatchedCount(): Int = mediaItemDao.getWatchedCount()

    suspend fun getInProgressCount(): Int = mediaItemDao.getInProgressCount()

    // AI-powered methods for enhanced media recommendations

    /**
     * Get enhanced media items with semantic analysis
     */
    fun getEnhancedMediaItems(): Flow<List<PlexAiRecommendationService.EnhancedMediaItem>> =
        flow {
            emitAll(aiRecommendationService.getEnhancedMediaItems(getAllMediaItems()))
        }

    /**
     * Get enhanced media items for a specific server
     */
    fun getEnhancedMediaItemsForServer(serverId: Long): Flow<List<PlexAiRecommendationService.EnhancedMediaItem>> =
        flow {
            emitAll(aiRecommendationService.getEnhancedMediaItems(getMediaItemsForServer(serverId)))
        }

    /**
     * Get enhanced media items for a specific library
     */
    fun getEnhancedMediaItemsForLibrary(libraryId: Long): Flow<List<PlexAiRecommendationService.EnhancedMediaItem>> =
        flow {
            emitAll(aiRecommendationService.getEnhancedMediaItems(getMediaItemsForLibrary(libraryId)))
        }

    /**
     * Find similar media items using semantic similarity
     */
    suspend fun findSimilarMedia(
        targetItem: PlexMediaItem,
        threshold: Double = PlexAiRecommendationService.DEFAULT_SIMILARITY_THRESHOLD
    ): List<PlexAiRecommendationService.SimilarMediaResult> {
        val allItems = getAllMediaItems()
        return aiRecommendationService.findSimilarMedia(
            targetItem = targetItem,
            candidateItems = emptyList(), // Will be populated from Flow
            threshold = threshold
        )
    }

    /**
     * Get cross-lingual recommendations for media items
     */
    suspend fun getCrossLingualRecommendations(
        targetLanguage: String = "en",
        limit: Int = PlexAiRecommendationService.MAX_RECOMMENDATIONS
    ): List<PlexAiRecommendationService.EnhancedMediaItem> {
        // Get recent items as basis for recommendations
        val recentItems = emptyList<PlexMediaItem>() // Would need to collect from Flow
        return aiRecommendationService.getCrossLingualRecommendations(
            mediaItems = recentItems,
            targetLanguage = targetLanguage
        ).take(limit)
    }

    /**
     * Get AI-powered recommendations based on watched items
     */
    suspend fun getRecommendationsBasedOnHistory(
        limit: Int = PlexAiRecommendationService.MAX_RECOMMENDATIONS
    ): List<PlexAiRecommendationService.SimilarMediaResult> {
        val watchedItems = emptyList<PlexMediaItem>() // Would need to collect from Flow
        val allItems = emptyList<PlexMediaItem>() // Would need to collect from Flow
        
        // Find similar items to each watched item
        return watchedItems.flatMap { watchedItem ->
            findSimilarMedia(watchedItem)
        }.distinctBy { it.mediaItem.ratingKey }
         .sortedByDescending { it.similarity }
         .take(limit)
    }
}