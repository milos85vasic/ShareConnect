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

import com.shareconnect.plexconnect.data.model.*
import kotlinx.coroutines.delay
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * Stub implementation of PlexApiService for testing and UI development.
 * Provides realistic responses using PlexTestData without requiring a live Plex server.
 *
 * Features:
 * - Stateful PIN authentication simulation
 * - Realistic response delays
 * - Complete coverage of all API endpoints
 * - Uses PlexTestData for consistent test data
 */
class PlexApiStubService : PlexApiService {

    companion object {
        // Simulated network delay in milliseconds
        private const val NETWORK_DELAY_MS = 500L

        // In-memory state for PIN authentication
        private val activePins = mutableMapOf<Long, PlexPinResponse>()
        private var nextPinId = PlexTestData.TEST_PIN_ID
        private var pinAuthorizationEnabled = false
    }

    /**
     * Enable PIN authorization for testing.
     * When enabled, the next checkPin call will return an authorized PIN response.
     */
    fun enablePinAuthorization() {
        pinAuthorizationEnabled = true
    }

    /**
     * Reset all stub state (useful for testing).
     */
    fun resetState() {
        activePins.clear()
        nextPinId = PlexTestData.TEST_PIN_ID
        pinAuthorizationEnabled = false
    }

    // Authentication endpoints
    override suspend fun requestPin(request: PlexPinRequest): Response<PlexPinResponse> {
        delay(NETWORK_DELAY_MS)

        val pinId = nextPinId++
        val pinResponse = PlexTestData.testPinResponseUnauthorized.copy(
            id = pinId,
            clientIdentifier = request.clientIdentifier,
            product = request.product
        )

        activePins[pinId] = pinResponse

        return Response.success(pinResponse)
    }

    override suspend fun checkPin(pinId: Long): Response<PlexPinResponse> {
        delay(NETWORK_DELAY_MS)

        val pin = activePins[pinId]
            ?: return Response.error(404, "PIN not found".toResponseBody())

        // Simulate PIN authorization if enabled
        val response = if (pinAuthorizationEnabled) {
            pin.copy(
                authToken = PlexTestData.TEST_AUTH_TOKEN,
                trusted = true
            ).also {
                activePins[pinId] = it
                pinAuthorizationEnabled = false
            }
        } else {
            pin
        }

        return Response.success(response)
    }

    // Server discovery and information
    override suspend fun getServerInfo(url: String): Response<PlexServerInfo> {
        delay(NETWORK_DELAY_MS)
        return Response.success(PlexTestData.testServerInfo)
    }

    override suspend fun getLibraries(
        serverUrl: String,
        token: String
    ): Response<PlexLibraryResponse> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        val response = PlexTestData.createApiLibraryResponse()
        return Response.success(response)
    }

    override suspend fun getLibraryItems(
        serverUrl: String,
        sectionKey: String,
        token: String,
        limit: Int,
        offset: Int
    ): Response<PlexMediaResponse> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // Return items based on library section
        val items = when (sectionKey) {
            "1" -> PlexTestData.getMovieLibraryItems()
            "2" -> PlexTestData.getTVShowLibraryItems()
            "3" -> emptyList() // Music library (empty for now)
            else -> emptyList()
        }

        // Apply pagination
        val paginatedItems = items.drop(offset).take(limit)

        val response = PlexTestData.createApiMediaResponse(
            items = paginatedItems,
            librarySectionTitle = PlexTestData.testApiLibraries.find { it.id == sectionKey }?.title,
            librarySectionID = sectionKey.toLongOrNull()
        )

        return Response.success(response)
    }

    override suspend fun getMediaItem(
        serverUrl: String,
        ratingKey: String,
        token: String
    ): Response<PlexMediaResponse> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        val itemDto = PlexTestData.getMediaByRatingKey(ratingKey)
            ?: return Response.error(404, "Media item not found".toResponseBody())

        val response = PlexTestData.createApiMediaResponse(
            items = listOf(itemDto),
            librarySectionTitle = itemDto.librarySectionTitle,
            librarySectionID = itemDto.librarySectionID
        )

        return Response.success(response)
    }

    override suspend fun getMediaChildren(
        serverUrl: String,
        ratingKey: String,
        token: String
    ): Response<PlexMediaResponse> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // Get children (e.g., episodes for a TV show)
        val children = PlexTestData.getEpisodesForShow(ratingKey)
        val parent = PlexTestData.getMediaByRatingKey(ratingKey)
        val response = PlexTestData.createApiMediaResponse(
            items = children,
            librarySectionTitle = parent?.librarySectionTitle,
            librarySectionID = parent?.librarySectionID
        )

        return Response.success(response)
    }

    // Playback and status updates
    override suspend fun markAsPlayed(
        serverUrl: String,
        key: String,
        identifier: String,
        token: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // In stub mode, just return success
        return Response.success(Unit)
    }

    override suspend fun markAsUnplayed(
        serverUrl: String,
        key: String,
        identifier: String,
        token: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // In stub mode, just return success
        return Response.success(Unit)
    }

    override suspend fun updateProgress(
        serverUrl: String,
        key: String,
        identifier: String,
        time: Long,
        state: String,
        token: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // In stub mode, just return success
        return Response.success(Unit)
    }

    // Search
    override suspend fun search(
        serverUrl: String,
        query: String,
        limit: Int,
        token: String
    ): Response<com.shareconnect.plexconnect.data.api.PlexSearchResponse> {
        delay(NETWORK_DELAY_MS)

        // Validate token
        if (token.isBlank()) {
            return Response.error(401, "Unauthorized - invalid token".toResponseBody())
        }

        // Search through test data
        val results = PlexTestData.searchMediaDto(query).take(limit)
        val response = PlexTestData.createApiSearchResponse(results)

        return Response.success(response)
    }
}
