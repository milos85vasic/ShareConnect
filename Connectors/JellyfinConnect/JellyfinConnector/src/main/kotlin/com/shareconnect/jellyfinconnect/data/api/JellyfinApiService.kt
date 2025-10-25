package com.shareconnect.jellyfinconnect.data.api

import com.shareconnect.jellyfinconnect.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Jellyfin REST API service interface
 * Based on Jellyfin API v1
 */
interface JellyfinApiService {

    /**
     * Authenticate with username and password
     */
    @POST("Users/AuthenticateByName")
    suspend fun authenticateByName(
        @Header("X-Emby-Authorization") authHeader: String,
        @Body request: JellyfinAuthRequest
    ): Response<JellyfinAuthResponse>

    /**
     * Get server information
     */
    @GET("System/Info/Public")
    suspend fun getPublicServerInfo(@Url url: String): Response<JellyfinServerInfo>

    /**
     * Get server information (authenticated)
     */
    @GET("System/Info")
    suspend fun getServerInfo(
        @Url url: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinServerInfo>

    /**
     * Get current user information
     */
    @GET("Users/Me")
    suspend fun getCurrentUser(
        @Url url: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinUser>

    /**
     * Get user's views (libraries)
     */
    @GET("Users/{userId}/Views")
    suspend fun getUserViews(
        @Url url: String,
        @Path("userId") userId: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinItemsResult>

    /**
     * Get items from a library
     */
    @GET("Users/{userId}/Items")
    suspend fun getItems(
        @Url url: String,
        @Path("userId") userId: String,
        @Query("ParentId") parentId: String? = null,
        @Query("IncludeItemTypes") includeItemTypes: String? = null,
        @Query("Recursive") recursive: Boolean = false,
        @Query("SortBy") sortBy: String = "SortName",
        @Query("SortOrder") sortOrder: String = "Ascending",
        @Query("Limit") limit: Int = 50,
        @Query("StartIndex") startIndex: Int = 0,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinItemsResult>

    /**
     * Get a specific item by ID
     */
    @GET("Users/{userId}/Items/{itemId}")
    suspend fun getItem(
        @Url url: String,
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinItem>

    /**
     * Mark item as played
     */
    @POST("Users/{userId}/PlayedItems/{itemId}")
    suspend fun markPlayed(
        @Url url: String,
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinUserData>

    /**
     * Mark item as unplayed
     */
    @DELETE("Users/{userId}/PlayedItems/{itemId}")
    suspend fun markUnplayed(
        @Url url: String,
        @Path("userId") userId: String,
        @Path("itemId") itemId: String,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinUserData>

    /**
     * Update playback progress
     */
    @POST("Sessions/Playing/Progress")
    suspend fun updateProgress(
        @Url url: String,
        @Header("X-Emby-Authorization") authHeader: String,
        @Body progress: JellyfinPlaybackProgress
    ): Response<Unit>

    /**
     * Search for items
     */
    @GET("Search/Hints")
    suspend fun search(
        @Url url: String,
        @Query("SearchTerm") searchTerm: String,
        @Query("UserId") userId: String,
        @Query("Limit") limit: Int = 50,
        @Header("X-Emby-Authorization") authHeader: String
    ): Response<JellyfinSearchResult>
}
