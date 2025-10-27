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


package com.shareconnect.portainerconnect.data.api

import com.shareconnect.portainerconnect.data.models.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Portainer REST API service interface
 * Based on Portainer API v2
 */
interface PortainerApiService {

    /**
     * Authenticate and get JWT token
     */
    @POST("api/auth")
    suspend fun authenticate(
        @Body request: PortainerAuthRequest
    ): Response<PortainerAuthResponse>

    /**
     * Get Portainer status
     */
    @GET("api/status")
    suspend fun getStatus(): Response<PortainerStatus>

    /**
     * Get all endpoints
     */
    @GET("api/endpoints")
    suspend fun getEndpoints(
        @Header("Authorization") authHeader: String
    ): Response<List<PortainerEndpoint>>

    /**
     * Get containers for an endpoint
     */
    @GET("api/endpoints/{endpointId}/docker/containers/json")
    suspend fun getContainers(
        @Path("endpointId") endpointId: Int,
        @Query("all") all: Boolean = true,
        @Header("Authorization") authHeader: String
    ): Response<List<PortainerContainer>>

    /**
     * Start a container
     */
    @POST("api/endpoints/{endpointId}/docker/containers/{id}/start")
    suspend fun startContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Stop a container
     */
    @POST("api/endpoints/{endpointId}/docker/containers/{id}/stop")
    suspend fun stopContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Restart a container
     */
    @POST("api/endpoints/{endpointId}/docker/containers/{id}/restart")
    suspend fun restartContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Pause a container
     */
    @POST("api/endpoints/{endpointId}/docker/containers/{id}/pause")
    suspend fun pauseContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Unpause a container
     */
    @POST("api/endpoints/{endpointId}/docker/containers/{id}/unpause")
    suspend fun unpauseContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Remove a container
     */
    @DELETE("api/endpoints/{endpointId}/docker/containers/{id}")
    suspend fun removeContainer(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Query("force") force: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<Unit>

    /**
     * Get images for an endpoint
     */
    @GET("api/endpoints/{endpointId}/docker/images/json")
    suspend fun getImages(
        @Path("endpointId") endpointId: Int,
        @Header("Authorization") authHeader: String
    ): Response<List<PortainerImage>>

    /**
     * Get volumes for an endpoint
     */
    @GET("api/endpoints/{endpointId}/docker/volumes")
    suspend fun getVolumes(
        @Path("endpointId") endpointId: Int,
        @Header("Authorization") authHeader: String
    ): Response<PortainerVolumesResponse>

    /**
     * Get networks for an endpoint
     */
    @GET("api/endpoints/{endpointId}/docker/networks")
    suspend fun getNetworks(
        @Path("endpointId") endpointId: Int,
        @Header("Authorization") authHeader: String
    ): Response<List<PortainerNetwork>>

    /**
     * Get container stats
     */
    @GET("api/endpoints/{endpointId}/docker/containers/{id}/stats")
    suspend fun getContainerStats(
        @Path("endpointId") endpointId: Int,
        @Path("id") containerId: String,
        @Query("stream") stream: Boolean = false,
        @Header("Authorization") authHeader: String
    ): Response<PortainerContainerStats>
}
