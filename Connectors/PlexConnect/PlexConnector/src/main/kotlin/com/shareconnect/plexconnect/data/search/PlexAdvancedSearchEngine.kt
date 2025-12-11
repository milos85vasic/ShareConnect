package com.shareconnect.plexconnect.data.search

import android.util.Log
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.api.PlexMediaItem as ApiPlexMediaItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.first
import java.time.Instant

/**
 * Advanced search engine for Plex media library
 */
class PlexAdvancedSearchEngine(
    private val apiClient: PlexApiClient,
    private val database: PlexDatabase
) {
    /**
     * Comprehensive search with multiple filtering and ranking strategies
     */
    fun search(
        serverUrl: String,
        token: String,
        query: String,
        searchOptions: SearchOptions = SearchOptions()
    ): Flow<SearchResult> = flow {
        try {
            // Perform local database search first
            val localResults = searchLocalDatabase(query, searchOptions)
            emit(SearchResult(localResults, SearchSource.LOCAL))

            // Perform API search
            val apiResults = searchViaApi(serverUrl, token, query, searchOptions)
            emit(SearchResult(apiResults, SearchSource.REMOTE))

            // Merge and rank results
            val rankedResults = mergeAndRankResults(localResults, apiResults, searchOptions)
            emit(SearchResult(rankedResults, SearchSource.MERGED))
        } catch (e: Exception) {
            Log.e("PlexAdvancedSearchEngine", "Search failed", e)
            throw e
        }
    }

    /**
     * Search local database
     */
    private suspend fun searchLocalDatabase(
        query: String, 
        options: SearchOptions
    ): List<PlexMediaItem> {
        return database.plexMediaItemDao()
            .searchMediaItems("%$query%")
            .first()
            .filter { item -> 
                matchesSearchOptions(item, options)
            }
    }

    /**
     * Search via Plex API
     */
    private suspend fun searchViaApi(
        serverUrl: String, 
        token: String, 
        query: String, 
        options: SearchOptions
    ): List<PlexMediaItem> {
        val apiResult = apiClient.search(serverUrl, query, token, options.limit)
        
        return apiResult.getOrNull()
            ?.filter { item -> matchesApiSearchOptions(item, options) }
            ?.map { apiItem ->
                // Convert API item to model item
                PlexMediaItem(
                    ratingKey = apiItem.ratingKey ?: "",
                    key = apiItem.key ?: "",
                    type = apiItem.type?.let { com.shareconnect.plexconnect.data.model.MediaType.fromString(it.value) } 
                        ?: com.shareconnect.plexconnect.data.model.MediaType.UNKNOWN,
                    title = apiItem.title ?: "",
                    year = apiItem.year,
                    serverId = ""
                )
            }
            ?: emptyList()
    }

    /**
     * Merge and rank search results
     */
    private fun mergeAndRankResults(
        localResults: List<PlexMediaItem>,
        apiResults: List<PlexMediaItem>,
        options: SearchOptions
    ): List<PlexMediaItem> {
        // Combine results, removing duplicates
        val combinedResults = (localResults + apiResults)
            .distinctBy { it.ratingKey }
            .sortedWith(compareByDescending { item ->
                calculateRelevanceScore(item, options)
            })
            .take(options.limit)

        return combinedResults
    }

    /**
     * Calculate relevance score for search ranking
     */
    private fun calculateRelevanceScore(
        item: PlexMediaItem, 
        options: SearchOptions
    ): Double {
        var score = 0.0

        // Title match score
        item.title?.let { title ->
            score += calculateTitleMatchScore(title, options.query)
        }

        // Type matching
        options.mediaTypes?.let { allowedTypes ->
            item.type?.let { itemType ->
                if (allowedTypes.any { it.name.lowercase() == itemType.value.lowercase() }) {
                    score += 10.0
                }
            }
        }

        // Year matching
        options.yearRange?.let { range ->
            item.year?.let { year ->
                if (year in range) {
                    score += 5.0
                }
            }
        }

        // Recency bonus
        item.year?.let { year ->
            val currentYear = Instant.now().toString().substring(0, 4).toInt()
            if (currentYear - year <= 2) {
                score += 3.0
            }
        }

        return score
    }

    /**
     * Calculate title match score
     */
    private fun calculateTitleMatchScore(title: String, query: String): Double {
        // Fuzzy matching and scoring
        val normalizedTitle = title.lowercase()
        val normalizedQuery = query.lowercase()

        return when {
            normalizedTitle == normalizedQuery -> 20.0  // Exact match
            normalizedTitle.contains(normalizedQuery) -> 15.0  // Partial match
            normalizedQuery.split(" ").all { normalizedTitle.contains(it) } -> 10.0  // All words present
            else -> 0.0
        }
    }

    /**
     * Check if item matches search options
     */
    private fun matchesSearchOptions(
        item: PlexMediaItem, 
        options: SearchOptions
    ): Boolean {
        // Type filtering
        options.mediaTypes?.let { allowedTypes ->
            item.type?.let { itemType ->
                if (!allowedTypes.any { it.name.lowercase() == itemType.value.lowercase() }) {
                    return false
                }
            }
        }

        // Year range filtering
        options.yearRange?.let { range ->
            item.year?.let { year ->
                if (year !in range) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Check if API item matches search options
     */
    private fun matchesApiSearchOptions(
        item: ApiPlexMediaItem, 
        options: SearchOptions
    ): Boolean {
        // Type filtering
        options.mediaTypes?.let { allowedTypes ->
            item.type?.let { itemType ->
                if (!allowedTypes.any { it.name.lowercase() == itemType.value.lowercase() }) {
                    return false
                }
            }
        }

        // Year range filtering
        options.yearRange?.let { range ->
            item.year?.let { year ->
                if (year !in range) {
                    return false
                }
            }
        }

        return true
    }

    /**
     * Search configuration options
     */
    data class SearchOptions(
        val query: String = "",
        val mediaTypes: List<MediaType>? = null,
        val yearRange: IntRange? = null,
        val limit: Int = 50
    )

    /**
     * Search result with source tracking
     */
    data class SearchResult(
        val items: List<PlexMediaItem>,
        val source: SearchSource
    )

    /**
     * Search result source
     */
    enum class SearchSource {
        LOCAL,
        REMOTE,
        MERGED
    }


}