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
import retrofit2.Response
import retrofit2.http.*

interface PlexApiService {

    // Authentication endpoints
    @POST("https://plex.tv/api/v2/pins")
    suspend fun requestPin(@Body request: PlexPinRequest): Response<PlexPinResponse>

    @GET("https://plex.tv/api/v2/pins/{pinId}")
    suspend fun checkPin(@Path("pinId") pinId: Long): Response<PlexPinResponse>

    // Server discovery and information
    @GET
    suspend fun getServerInfo(@Url url: String = "{serverUrl}"): Response<PlexServerInfo>

    @GET("{serverUrl}/library/sections")
    suspend fun getLibraries(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Query("X-Plex-Token") token: String
    ): Response<PlexLibraryResponse>

    @GET("{serverUrl}/library/sections/{sectionKey}/all")
    suspend fun getLibraryItems(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Path("sectionKey") sectionKey: String,
        @Query("X-Plex-Token") token: String,
        @Query("limit") limit: Int = 50,
        @Query("offset") offset: Int = 0
    ): Response<PlexMediaResponse>

    @GET("{serverUrl}/library/metadata/{ratingKey}")
    suspend fun getMediaItem(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Path("ratingKey") ratingKey: String,
        @Query("X-Plex-Token") token: String
    ): Response<PlexMediaResponse>

    @GET("{serverUrl}/library/metadata/{ratingKey}/children")
    suspend fun getMediaChildren(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Path("ratingKey") ratingKey: String,
        @Query("X-Plex-Token") token: String
    ): Response<PlexMediaResponse>

    // Playback and status updates
    @PUT("{serverUrl}/:/scrobble")
    suspend fun markAsPlayed(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Query("key") key: String,
        @Query("identifier") identifier: String = "com.plexapp.plugins.library",
        @Query("X-Plex-Token") token: String
    ): Response<Unit>

    @PUT("{serverUrl}/:/unscrobble")
    suspend fun markAsUnplayed(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Query("key") key: String,
        @Query("identifier") identifier: String = "com.plexapp.plugins.library",
        @Query("X-Plex-Token") token: String
    ): Response<Unit>

    @PUT("{serverUrl}/:/progress")
    suspend fun updateProgress(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Query("key") key: String,
        @Query("identifier") identifier: String = "com.plexapp.plugins.library",
        @Query("time") time: Long,
        @Query("state") state: String = "playing",
        @Query("X-Plex-Token") token: String
    ): Response<Unit>

    // Search
    @GET("{serverUrl}/search")
    suspend fun search(
        @Path("serverUrl", encoded = true) serverUrl: String,
        @Query("query") query: String,
        @Query("limit") limit: Int = 50,
        @Query("X-Plex-Token") token: String
    ): Response<PlexSearchResponse>
}