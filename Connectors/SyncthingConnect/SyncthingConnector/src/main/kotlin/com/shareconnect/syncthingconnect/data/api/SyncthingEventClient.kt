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


package com.shareconnect.syncthingconnect.data.api

import com.google.gson.Gson
import com.shareconnect.syncthingconnect.data.models.SyncthingEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.sse.EventSource
import okhttp3.sse.EventSourceListener
import okhttp3.sse.EventSources
import java.util.concurrent.TimeUnit

/**
 * Syncthing Event Stream Client using Server-Sent Events (SSE)
 *
 * @param serverUrl Base URL of Syncthing server
 * @param apiKey API key for authentication
 */
class SyncthingEventClient(
    private val serverUrl: String,
    private val apiKey: String
) {
    private val gson = Gson()
    private val client = OkHttpClient.Builder()
        .connectTimeout(0, TimeUnit.MILLISECONDS)
        .readTimeout(0, TimeUnit.MILLISECONDS)
        .writeTimeout(0, TimeUnit.MILLISECONDS)
        .build()

    /**
     * Subscribe to event stream
     *
     * @param since Last event ID received (for resuming)
     * @param events List of event types to filter (empty = all events)
     * @param timeout Timeout in seconds for long polling (default 60)
     * @return Flow of Syncthing events
     */
    fun subscribeToEvents(
        since: Long = 0,
        events: List<String> = emptyList(),
        timeout: Int = 60
    ): Flow<SyncthingEvent> = flow {
        val urlBuilder = StringBuilder("$serverUrl/rest/events")
        urlBuilder.append("?since=$since")
        urlBuilder.append("&timeout=$timeout")
        
        if (events.isNotEmpty()) {
            urlBuilder.append("&events=${events.joinToString(",")}")
        }

        val request = Request.Builder()
            .url(urlBuilder.toString())
            .header("X-API-Key", apiKey)
            .build()

        val eventSourceListener = object : EventSourceListener() {
            override fun onOpen(eventSource: EventSource, response: Response) {
                // Connection opened
            }

            override fun onEvent(
                eventSource: EventSource,
                id: String?,
                type: String?,
                data: String
            ) {
                try {
                    // Parse array of events
                    val eventArray = gson.fromJson(data, Array<SyncthingEvent>::class.java)
                    // Emit each event
                    eventArray.forEach { event ->
                        // Note: This is a simplified version
                        // In production, you'd need a channel or callback mechanism
                    }
                } catch (e: Exception) {
                    // Handle parsing errors
                }
            }

            override fun onClosed(eventSource: EventSource) {
                // Connection closed
            }

            override fun onFailure(eventSource: EventSource, t: Throwable?, response: Response?) {
                // Handle failure
            }
        }

        val eventSource = EventSources.createFactory(client)
            .newEventSource(request, eventSourceListener)
    }.flowOn(Dispatchers.IO)

    /**
     * Simplified polling-based event retrieval
     * This is more suitable for Kotlin coroutines
     *
     * @param since Last event ID
     * @param events Event types to filter
     * @return List of events since last ID
     */
    suspend fun getEvents(
        since: Long = 0,
        events: List<String> = emptyList()
    ): Result<List<SyncthingEvent>> {
        return try {
            val urlBuilder = StringBuilder("$serverUrl/rest/events")
            urlBuilder.append("?since=$since")
            
            if (events.isNotEmpty()) {
                urlBuilder.append("&events=${events.joinToString(",")}")
            }

            val request = Request.Builder()
                .url(urlBuilder.toString())
                .header("X-API-Key", apiKey)
                .build()

            val response = client.newCall(request).execute()
            
            if (response.isSuccessful && response.body != null) {
                val eventArray = gson.fromJson(
                    response.body!!.string(),
                    Array<SyncthingEvent>::class.java
                )
                Result.success(eventArray.toList())
            } else {
                Result.failure(Exception("Failed to get events: ${response.code}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get disk events
     */
    suspend fun getDiskEvents(since: Long = 0): Result<List<SyncthingEvent>> {
        return getEvents(since, listOf("LocalChangeDetected", "RemoteChangeDetected"))
    }

    /**
     * Get folder events
     */
    suspend fun getFolderEvents(since: Long = 0): Result<List<SyncthingEvent>> {
        return getEvents(
            since,
            listOf(
                "FolderCompletion",
                "FolderSummary",
                "FolderErrors",
                "FolderScanProgress",
                "FolderPaused",
                "FolderResumed"
            )
        )
    }

    /**
     * Get device events
     */
    suspend fun getDeviceEvents(since: Long = 0): Result<List<SyncthingEvent>> {
        return getEvents(
            since,
            listOf(
                "DeviceConnected",
                "DeviceDisconnected",
                "DeviceDiscovered",
                "DevicePaused",
                "DeviceResumed",
                "DeviceRejected"
            )
        )
    }

    /**
     * Get sync events
     */
    suspend fun getSyncEvents(since: Long = 0): Result<List<SyncthingEvent>> {
        return getEvents(
            since,
            listOf(
                "DownloadProgress",
                "ItemStarted",
                "ItemFinished",
                "StateChanged"
            )
        )
    }

    /**
     * Get system events
     */
    suspend fun getSystemEvents(since: Long = 0): Result<List<SyncthingEvent>> {
        return getEvents(
            since,
            listOf(
                "Starting",
                "StartupComplete",
                "ConfigSaved",
                "ListenAddressesChanged"
            )
        )
    }

    /**
     * Get all event types
     */
    fun getAllEventTypes(): List<String> {
        return listOf(
            // Config events
            "ConfigSaved",
            
            // Device events
            "DeviceConnected",
            "DeviceDisconnected",
            "DeviceDiscovered",
            "DevicePaused",
            "DeviceResumed",
            "DeviceRejected",
            
            // Folder events
            "FolderCompletion",
            "FolderSummary",
            "FolderErrors",
            "FolderScanProgress",
            "FolderPaused",
            "FolderResumed",
            "FolderRejected",
            
            // Item events
            "ItemStarted",
            "ItemFinished",
            "LocalChangeDetected",
            "RemoteChangeDetected",
            "LocalIndexUpdated",
            "RemoteIndexUpdated",
            
            // Download events
            "DownloadProgress",
            
            // State events
            "StateChanged",
            
            // System events
            "Starting",
            "StartupComplete",
            "ListenAddressesChanged",
            
            // Pending events
            "PendingDevicesChanged",
            "PendingFoldersChanged"
        )
    }
}
