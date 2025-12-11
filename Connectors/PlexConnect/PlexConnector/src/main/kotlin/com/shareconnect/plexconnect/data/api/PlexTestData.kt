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

package com.shareconnect.plexconnect.data.api

import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.LibraryType
import com.shareconnect.plexconnect.data.model.*

/**
 * Test data provider for Plex API stub implementations.
 * Provides realistic sample data for all Plex models to enable UI and integration testing
 * without requiring a live Plex server.
 */
object PlexTestData {

    // Authentication Data
    const val TEST_CLIENT_IDENTIFIER = "test-client-12345"
    const val TEST_PIN_CODE = "ABCD1234"
    const val TEST_PIN_ID = 123456789L
    const val TEST_AUTH_TOKEN = "test-auth-token-xyz789"

    val testPinRequest = PlexPinRequest(
        strong = true,
        product = "PlexConnect",
        clientIdentifier = TEST_CLIENT_IDENTIFIER
    )

    val testPinResponseUnauthorized = PlexPinResponse(
        id = TEST_PIN_ID,
        code = TEST_PIN_CODE,
        product = "PlexConnect",
        trusted = false,
        clientIdentifier = TEST_CLIENT_IDENTIFIER,
        location = PlexLocation(
            code = "US",
            country = "United States",
            city = "San Francisco",
            subdivisions = "California",
            coordinates = "37.7749,-122.4194"
        ),
        expiresIn = 900,
        createdAt = "2025-01-01T12:00:00Z",
        expiresAt = "2025-01-01T12:15:00Z",
        authToken = null,
        newRegistration = null
    )

    val testPinResponseAuthorized = testPinResponseUnauthorized.copy(
        authToken = TEST_AUTH_TOKEN,
        trusted = true
    )

    // Server Data
    const val TEST_SERVER_URL = "http://192.168.1.100:32400"
    const val TEST_SERVER_NAME = "My Plex Server"
    const val TEST_MACHINE_IDENTIFIER = "test-machine-id-abc123"

    val testServerInfo = PlexServerInfo(
        machineIdentifier = TEST_MACHINE_IDENTIFIER,
        version = "1.32.5.7349",
        name = TEST_SERVER_NAME,
        host = "192.168.1.100",
        address = "192.168.1.100",
        port = 32400,
        scheme = "http",
        localAddresses = "192.168.1.100",
        owned = 1,
        synced = 0
    )

    // Library Data
    val testMovieLibrary = PlexLibrary(
        key = "1",
        title = "Movies",
        type = LibraryType.MOVIE,
        serverId = 1L,
        agent = "tv.plex.agents.movie",
        scanner = "Plex Movie",
        language = "en-US",
        uuid = "library-uuid-movies-001",
        updatedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis() - 86400000,
        scannedAt = System.currentTimeMillis(),
        content = true,
        directory = true,
        contentChangedAt = System.currentTimeMillis(),
        hidden = 0,
        art = "/library/sections/1/art",
        composite = "/library/sections/1/composite",
        thumb = "/library/sections/1/thumb"
    )

    val testTVShowLibrary = PlexLibrary(
        key = "2",
        title = "TV Shows",
        type = LibraryType.SHOW,
        serverId = 1L,
        agent = "tv.plex.agents.series",
        scanner = "Plex TV Series",
        language = "en-US",
        uuid = "library-uuid-shows-002",
        updatedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis() - 86400000,
        scannedAt = System.currentTimeMillis(),
        content = true,
        directory = true,
        contentChangedAt = System.currentTimeMillis(),
        hidden = 0,
        art = "/library/sections/2/art",
        composite = "/library/sections/2/composite",
        thumb = "/library/sections/2/thumb"
    )

    val testMusicLibrary = PlexLibrary(
        key = "3",
        title = "Music",
        type = LibraryType.MUSIC,
        serverId = 1L,
        agent = "tv.plex.agents.music",
        scanner = "Plex Music",
        language = "en-US",
        uuid = "library-uuid-music-003",
        updatedAt = System.currentTimeMillis(),
        createdAt = System.currentTimeMillis() - 86400000,
        scannedAt = System.currentTimeMillis(),
        content = true,
        directory = true,
        contentChangedAt = System.currentTimeMillis(),
        hidden = 0,
        art = "/library/sections/3/art",
        composite = "/library/sections/3/composite",
        thumb = "/library/sections/3/thumb"
    )

    val testLibraries = listOf(testMovieLibrary, testTVShowLibrary, testMusicLibrary)

    // API versions of test libraries
    val testApiLibraries = listOf(
        PlexLibrary(
            id = "1",
            serverId = "1",
            title = "Movies",
            type = com.shareconnect.plexconnect.data.api.LibraryType.MOVIE
        ),
        PlexLibrary(
            id = "2", 
            serverId = "1",
            title = "TV Shows",
            type = com.shareconnect.plexconnect.data.api.LibraryType.TV_SHOW
        ),
        PlexLibrary(
            id = "3",
            serverId = "1", 
            title = "Music",
            type = com.shareconnect.plexconnect.data.api.LibraryType.MUSIC
        )
    )

    // Media Items Data - Movies
    val testMovie1 = PlexMediaItem(
        ratingKey = "1001",
        key = "/library/metadata/1001",
        guid = "plex://movie/5d776825880197001ec967c8",
        librarySectionTitle = "Movies",
        librarySectionID = 1L,
        librarySectionKey = "1",
        type = MediaType.MOVIE,
        title = "The Matrix",
        titleSort = "Matrix",
        originalTitle = "The Matrix",
        summary = "A computer hacker learns from mysterious rebels about the true nature of his reality and his role in the war against its controllers.",
        rating = 8.7f,
        audienceRating = 9.2f,
        year = 1999,
        tagline = "Welcome to the Real World",
        thumb = "/library/metadata/1001/thumb",
        art = "/library/metadata/1001/art",
        duration = 8160000L, // 136 minutes in milliseconds
        originallyAvailableAt = "1999-03-31",
        addedAt = System.currentTimeMillis() - 2592000000L, // 30 days ago
        updatedAt = System.currentTimeMillis(),
        audienceRatingImage = "rottentomatoes://image.rating.upright",
        ratingImage = "imdb://image.rating",
        viewCount = 3,
        lastViewedAt = System.currentTimeMillis() - 604800000L, // 7 days ago
        viewOffset = 0L,
        studio = "Warner Bros.",
        contentRating = "R",
        directors = listOf("Lana Wachowski", "Lilly Wachowski"),
        writers = listOf("Lana Wachowski", "Lilly Wachowski"),
        actors = listOf("Keanu Reeves", "Laurence Fishburne", "Carrie-Anne Moss"),
        genres = listOf("Science Fiction", "Action"),
        collections = listOf("The Matrix Collection"),
        serverId = 1L
    )

    val testMovie2 = PlexMediaItem(
        ratingKey = "1002",
        key = "/library/metadata/1002",
        guid = "plex://movie/5d7768269c91a0001f4d5bc7",
        librarySectionTitle = "Movies",
        librarySectionID = 1L,
        librarySectionKey = "1",
        type = MediaType.MOVIE,
        title = "Inception",
        titleSort = "Inception",
        originalTitle = "Inception",
        summary = "A thief who steals corporate secrets through the use of dream-sharing technology is given the inverse task of planting an idea into the mind of a C.E.O.",
        rating = 8.8f,
        audienceRating = 9.3f,
        year = 2010,
        tagline = "Your mind is the scene of the crime",
        thumb = "/library/metadata/1002/thumb",
        art = "/library/metadata/1002/art",
        duration = 8880000L, // 148 minutes in milliseconds
        originallyAvailableAt = "2010-07-16",
        addedAt = System.currentTimeMillis() - 1296000000L, // 15 days ago
        updatedAt = System.currentTimeMillis(),
        audienceRatingImage = "rottentomatoes://image.rating.upright",
        ratingImage = "imdb://image.rating",
        viewCount = 0,
        lastViewedAt = null,
        viewOffset = 3600000L, // 1 hour in, partially watched
        studio = "Warner Bros.",
        contentRating = "PG-13",
        directors = listOf("Christopher Nolan"),
        writers = listOf("Christopher Nolan"),
        actors = listOf("Leonardo DiCaprio", "Joseph Gordon-Levitt", "Elliot Page"),
        genres = listOf("Science Fiction", "Action", "Thriller"),
        serverId = 1L
    )

    // Media Items Data - TV Shows
    val testTVShow1 = PlexMediaItem(
        ratingKey = "2001",
        key = "/library/metadata/2001",
        guid = "plex://show/5d9c086e46115600208900d4",
        librarySectionTitle = "TV Shows",
        librarySectionID = 2L,
        librarySectionKey = "2",
        type = MediaType.SHOW,
        title = "Breaking Bad",
        titleSort = "Breaking Bad",
        originalTitle = "Breaking Bad",
        summary = "A high school chemistry teacher diagnosed with inoperable lung cancer turns to manufacturing and selling methamphetamine in order to secure his family's future.",
        rating = 9.5f,
        audienceRating = 9.8f,
        year = 2008,
        tagline = "All Hail the King",
        thumb = "/library/metadata/2001/thumb",
        art = "/library/metadata/2001/art",
        duration = null,
        originallyAvailableAt = "2008-01-20",
        addedAt = System.currentTimeMillis() - 5184000000L, // 60 days ago
        updatedAt = System.currentTimeMillis(),
        audienceRatingImage = "rottentomatoes://image.rating.upright",
        ratingImage = "themoviedb://image.rating",
        viewCount = 62, // 62 episodes watched
        lastViewedAt = System.currentTimeMillis() - 259200000L, // 3 days ago
        viewOffset = 0L,
        studio = "AMC",
        contentRating = "TV-MA",
        directors = emptyList(),
        writers = listOf("Vince Gilligan"),
        actors = listOf("Bryan Cranston", "Aaron Paul", "Anna Gunn"),
        genres = listOf("Drama", "Crime", "Thriller"),
        serverId = 1L
    )

    val testEpisode1 = PlexMediaItem(
        ratingKey = "2101",
        key = "/library/metadata/2101",
        guid = "plex://episode/5d9c086f6ab6e40020b5f2eb",
        librarySectionTitle = "TV Shows",
        librarySectionID = 2L,
        librarySectionKey = "2",
        type = MediaType.EPISODE,
        title = "Pilot",
        titleSort = "Pilot",
        originalTitle = "Breaking Bad - S01E01",
        summary = "Walter White, a struggling chemistry teacher, is diagnosed with lung cancer. In order to pay for his treatment and secure his family's financial future, he begins producing and selling methamphetamine with one of his former students.",
        rating = 8.9f,
        audienceRating = 9.1f,
        year = 2008,
        thumb = "/library/metadata/2101/thumb",
        art = "/library/metadata/2101/art",
        duration = 3480000L, // 58 minutes in milliseconds
        originallyAvailableAt = "2008-01-20",
        addedAt = System.currentTimeMillis() - 5184000000L,
        updatedAt = System.currentTimeMillis(),
        audienceRatingImage = "rottentomatoes://image.rating.upright",
        ratingImage = "themoviedb://image.rating",
        viewCount = 1,
        lastViewedAt = System.currentTimeMillis() - 5184000000L,
        viewOffset = 0L,
        studio = "AMC",
        contentRating = "TV-MA",
        directors = listOf("Vince Gilligan"),
        writers = listOf("Vince Gilligan"),
        actors = listOf("Bryan Cranston", "Aaron Paul", "Anna Gunn"),
        genres = listOf("Drama", "Crime"),
        serverId = 1L
    )

    val testMovies = listOf(testMovie1, testMovie2)
    val testTVShows = listOf(testTVShow1)
    val testEpisodes = listOf(testEpisode1)
    val testAllMedia = testMovies + testTVShows + testEpisodes

    // Response Wrappers
    fun createLibraryResponse(libraries: List<com.shareconnect.plexconnect.data.api.PlexLibrary> = testApiLibraries): com.shareconnect.plexconnect.data.model.PlexLibraryResponse {
        return com.shareconnect.plexconnect.data.model.PlexLibraryResponse(
            size = libraries.size,
            sections = libraries.map { lib ->
                com.shareconnect.plexconnect.data.model.PlexLibrarySection(
                    key = lib.id,
                    title = lib.title,
                    type = when(lib.type) {
                        com.shareconnect.plexconnect.data.api.LibraryType.MOVIE -> com.shareconnect.plexconnect.data.model.LibraryType.MOVIE
                        com.shareconnect.plexconnect.data.api.LibraryType.TV_SHOW -> com.shareconnect.plexconnect.data.model.LibraryType.SHOW
                        com.shareconnect.plexconnect.data.api.LibraryType.MUSIC -> com.shareconnect.plexconnect.data.model.LibraryType.MUSIC
                        com.shareconnect.plexconnect.data.api.LibraryType.PHOTO -> com.shareconnect.plexconnect.data.model.LibraryType.PHOTO
                        com.shareconnect.plexconnect.data.api.LibraryType.MIXED -> com.shareconnect.plexconnect.data.model.LibraryType.UNKNOWN
                    }
                )
            }
        )
    }

    fun createMediaResponse(
        items: List<PlexMediaItemDto>,
        librarySectionTitle: String? = null,
        librarySectionID: Long? = null
    ): com.shareconnect.plexconnect.data.model.PlexMediaResponse {
        return com.shareconnect.plexconnect.data.model.PlexMediaResponse(
            items = items
        )
    }

    fun createSearchResponse(items: List<PlexMediaItemDto>): com.shareconnect.plexconnect.data.model.PlexSearchResponse {
        return com.shareconnect.plexconnect.data.model.PlexSearchResponse(
            items = items
        )
    }
    
    // API version creators
    fun createApiLibraryResponse(): com.shareconnect.plexconnect.data.api.PlexLibraryResponse {
        val modelResponse = createLibraryResponse()
        return com.shareconnect.plexconnect.data.api.PlexLibraryResponse(
            size = modelResponse.size,
            allowSync = modelResponse.allowSync,
            mediaContainer = com.shareconnect.plexconnect.data.api.PlexMediaContainer<PlexLibrary>(
                size = modelResponse.sections?.size ?: 0,
                Directory = modelResponse.sections?.map { section ->
                    com.shareconnect.plexconnect.data.api.PlexLibrary(
                        id = section.key,
                        serverId = section.key,
                        title = section.title,
                        type = when(section.type) {
                            com.shareconnect.plexconnect.data.model.LibraryType.MOVIE -> com.shareconnect.plexconnect.data.api.LibraryType.MOVIE
                            com.shareconnect.plexconnect.data.model.LibraryType.SHOW -> com.shareconnect.plexconnect.data.api.LibraryType.TV_SHOW
                            com.shareconnect.plexconnect.data.model.LibraryType.MUSIC -> com.shareconnect.plexconnect.data.api.LibraryType.MUSIC
                            com.shareconnect.plexconnect.data.model.LibraryType.PHOTO -> com.shareconnect.plexconnect.data.api.LibraryType.PHOTO
                            com.shareconnect.plexconnect.data.model.LibraryType.UNKNOWN -> com.shareconnect.plexconnect.data.api.LibraryType.MIXED
                        }
                    )
                } ?: emptyList()
            )
        )
    }
    
    fun createApiMediaResponse(
        items: List<PlexMediaItemDto>,
        librarySectionTitle: String? = null,
        librarySectionID: Long? = null
    ): com.shareconnect.plexconnect.data.api.PlexMediaResponse {
        return com.shareconnect.plexconnect.data.api.PlexMediaResponse(
            mediaContainer = com.shareconnect.plexconnect.data.api.PlexMediaContainer(
                size = items.size,
                Metadata = items.map { item ->
                    com.shareconnect.plexconnect.data.api.PlexMediaItem(
                        ratingKey = item.ratingKey,
                        key = item.key,
                        guid = item.guid,
                        studio = item.studio,
                        type = com.shareconnect.plexconnect.data.api.MediaType.fromString(item.type ?: "movie"),
                        title = item.title ?: "",
                        titleSort = item.titleSort,
                        summary = item.summary,
                        id = item.ratingKey,
                        libraryId = item.librarySectionID?.toString(),
                        year = item.year,
                        duration = item.duration,
                        librarySectionTitle = item.librarySectionTitle,
                        librarySectionID = item.librarySectionID?.toLong()
                    )
                }
            )
        )
    }
    
    fun createApiSearchResponse(
        items: List<PlexMediaItemDto>
    ): com.shareconnect.plexconnect.data.api.PlexSearchResponse {
        return com.shareconnect.plexconnect.data.api.PlexSearchResponse(
            mediaContainer = com.shareconnect.plexconnect.data.api.PlexMediaContainer(
                size = items.size,
                media = items.map { item ->
                    com.shareconnect.plexconnect.data.api.PlexMediaItem(
                        ratingKey = item.ratingKey,
                        key = item.key,
                        guid = item.guid,
                        studio = item.studio,
                        type = com.shareconnect.plexconnect.data.api.MediaType.fromString(item.type ?: "movie"),
                        title = item.title ?: "",
                        titleSort = item.titleSort,
                        summary = item.summary,
                        id = item.ratingKey,
                        libraryId = item.librarySectionID?.toString(),
                        year = item.year,
                        duration = item.duration,
                        librarySectionTitle = item.librarySectionTitle,
                        librarySectionID = item.librarySectionID?.toLong()
                    )
                }
            )
        )
    }

    // Helper methods for filtering test data
    fun getMovieLibraryItems(): List<PlexMediaItemDto> = testMovies.map { 
        PlexMediaItemDto(
            ratingKey = it.ratingKey,
            key = it.key,
            guid = it.guid,
            studio = it.studio,
            type = it.type.value,
            title = it.title,
            titleSort = it.titleSort,
            summary = it.summary,
            index = null,
            year = it.year,
            duration = it.duration,
            librarySectionTitle = it.librarySectionTitle,
            librarySectionID = it.librarySectionID
        )
    }


    fun getTVShowLibraryItems(): List<PlexMediaItemDto> = testTVShows.map {
        PlexMediaItemDto(
            ratingKey = it.ratingKey,
            key = it.key,
            guid = it.guid,
            studio = it.studio,
            type = it.type.value,
            title = it.title,
            titleSort = it.titleSort,
            summary = it.summary,
            index = null,
            year = it.year,
            duration = it.duration,
            librarySectionTitle = it.librarySectionTitle,
            librarySectionID = it.librarySectionID
        )
    }


    fun getEpisodesForShow(showRatingKey: String): List<PlexMediaItemDto> {
        // Map shows to their episodes by comparing the first character of rating keys
        // Show: 2001 -> Episodes: 21xx
        return when (showRatingKey) {
            "2001" -> testEpisodes.map { toDto(it) }
            else -> emptyList()
        }
    }

    fun searchMedia(query: String): List<PlexMediaItemDto> {
        val lowerQuery = query.lowercase()
        return testAllMedia.map { toDto(it) }.filter {
            it.title?.lowercase()?.contains(lowerQuery) == true ||
            it.summary?.lowercase()?.contains(lowerQuery) == true
        }
    }
    
    fun searchMediaDto(query: String): List<PlexMediaItemDto> {
        return searchMedia(query)
    }

    fun getMediaByRatingKey(ratingKey: String): PlexMediaItemDto? {
        return testAllMedia.find { it.ratingKey == ratingKey }?.let { toDto(it) }
    }

    // Helper method to convert model to DTO
    private fun toDto(mediaItem: com.shareconnect.plexconnect.data.model.PlexMediaItem): PlexMediaItemDto {
        return PlexMediaItemDto(
            ratingKey = mediaItem.ratingKey,
            key = mediaItem.key,
            guid = mediaItem.guid,
            studio = mediaItem.studio,
            type = mediaItem.type.value,
            title = mediaItem.title,
            titleSort = mediaItem.titleSort,
            summary = mediaItem.summary,
            index = null, // index is not in model
            year = mediaItem.year,
            duration = mediaItem.duration,
            librarySectionTitle = mediaItem.librarySectionTitle,
            librarySectionID = mediaItem.librarySectionID
        )
    }
}
