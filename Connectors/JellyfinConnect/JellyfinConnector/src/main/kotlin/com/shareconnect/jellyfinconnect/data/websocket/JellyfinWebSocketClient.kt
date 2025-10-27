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


package com.shareconnect.jellyfinconnect.data.websocket

import android.util.Log
import com.shareconnect.websocket.ConnectionState
import com.shareconnect.websocket.OkHttpWebSocketClient
import com.shareconnect.websocket.WebSocketConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Jellyfin WebSocket client
 * Handles real-time updates for sessions, playback, library changes, and more
 */
class JellyfinWebSocketClient(
    private val serverUrl: String,
    private val accessToken: String,
    private val deviceId: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "JellyfinWebSocket"
    }

    private val messageParser = JellyfinMessageParser()

    private val _sessions = MutableStateFlow<List<SessionsMessage.SessionInfo>>(emptyList())
    val sessions: StateFlow<List<SessionsMessage.SessionInfo>> = _sessions.asStateFlow()

    private val _nowPlaying = MutableStateFlow<SessionsMessage.NowPlayingItem?>(null)
    val nowPlaying: StateFlow<SessionsMessage.NowPlayingItem?> = _nowPlaying.asStateFlow()

    private var webSocketClient: OkHttpWebSocketClient? = null
    private var isReceivingSessions = false

    private val sessionChangeCallbacks = mutableListOf<(List<SessionsMessage.SessionInfo>) -> Unit>()
    private val libraryChangeCallbacks = mutableListOf<(LibraryChangedMessage) -> Unit>()
    private val userDataChangeCallbacks = mutableListOf<(UserDataChangedMessage) -> Unit>()
    private val playstateCallbacks = mutableListOf<(PlaystateMessage) -> Unit>()

    /**
     * Connect to Jellyfin WebSocket API
     */
    suspend fun connect() {
        val wsUrl = serverUrl.replace("http://", "ws://").replace("https://", "wss://")
        val fullUrl = "$wsUrl/socket?api_key=$accessToken&deviceId=$deviceId"

        val config = WebSocketConfig(
            url = fullUrl,
            reconnectEnabled = true,
            maxReconnectAttempts = 10,
            reconnectDelayMillis = 2000,
            reconnectMaxDelayMillis = 60000
        )

        webSocketClient = OkHttpWebSocketClient(
            config = config,
            messageParser = { json -> messageParser.parse(json) },
            scope = scope
        )

        // Subscribe to message types
        webSocketClient?.subscribe("Sessions") { message ->
            if (message is SessionsMessage) {
                handleSessions(message)
            }
        }

        webSocketClient?.subscribe("UserDataChanged") { message ->
            if (message is UserDataChangedMessage) {
                handleUserDataChanged(message)
            }
        }

        webSocketClient?.subscribe("LibraryChanged") { message ->
            if (message is LibraryChangedMessage) {
                handleLibraryChanged(message)
            }
        }

        webSocketClient?.subscribe("Playstate") { message ->
            if (message is PlaystateMessage) {
                handlePlaystate(message)
            }
        }

        webSocketClient?.subscribe("ForceKeepAlive") { message ->
            if (message is ForceKeepAliveMessage) {
                Log.d(TAG, "Received ForceKeepAlive: ${message.data}")
                // Respond with KeepAlive
                scope.launch {
                    sendKeepAlive()
                }
            }
        }

        webSocketClient?.connect(
            onConnected = {
                Log.d(TAG, "WebSocket connected")
                scope.launch {
                    // Send initial keep alive
                    sendKeepAlive()
                }
            },
            onDisconnected = { reason ->
                Log.d(TAG, "WebSocket disconnected: $reason")
                isReceivingSessions = false
            },
            onError = { error ->
                Log.e(TAG, "WebSocket error: ${error.message}", error)
            }
        )
    }

    /**
     * Disconnect from WebSocket
     */
    suspend fun disconnect() {
        if (isReceivingSessions) {
            stopReceivingSessions()
        }
        webSocketClient?.disconnect()
        webSocketClient = null
    }

    /**
     * Start receiving session updates
     */
    suspend fun startReceivingSessions(): Boolean {
        if (isReceivingSessions) return true

        val message = SessionsStartMessage()
        val success = webSocketClient?.send(message) ?: false
        if (success) {
            isReceivingSessions = true
            Log.d(TAG, "Started receiving sessions")
        }
        return success
    }

    /**
     * Stop receiving session updates
     */
    suspend fun stopReceivingSessions(): Boolean {
        if (!isReceivingSessions) return true

        val message = SessionsStopMessage()
        val success = webSocketClient?.send(message) ?: false
        if (success) {
            isReceivingSessions = false
            Log.d(TAG, "Stopped receiving sessions")
        }
        return success
    }

    /**
     * Send keep alive message
     */
    suspend fun sendKeepAlive(): Boolean {
        val message = KeepAliveMessage()
        return webSocketClient?.send(message) ?: false
    }

    /**
     * Subscribe to session changes
     */
    fun subscribeToSessions(callback: (List<SessionsMessage.SessionInfo>) -> Unit) {
        sessionChangeCallbacks.add(callback)
    }

    /**
     * Subscribe to library changes
     */
    fun subscribeToLibraryChanges(callback: (LibraryChangedMessage) -> Unit) {
        libraryChangeCallbacks.add(callback)
    }

    /**
     * Subscribe to user data changes
     */
    fun subscribeToUserDataChanges(callback: (UserDataChangedMessage) -> Unit) {
        userDataChangeCallbacks.add(callback)
    }

    /**
     * Subscribe to playstate changes
     */
    fun subscribeToPlaystate(callback: (PlaystateMessage) -> Unit) {
        playstateCallbacks.add(callback)
    }

    /**
     * Get connection state
     */
    fun getConnectionState(): StateFlow<ConnectionState> {
        return webSocketClient?.connectionState ?: MutableStateFlow(ConnectionState.Disconnected)
    }

    /**
     * Check if connected
     */
    fun isConnected(): Boolean {
        return webSocketClient?.isConnected() == true
    }

    // Private methods

    private fun handleSessions(message: SessionsMessage) {
        _sessions.value = message.sessions

        // Update now playing from current user's session
        val currentSession = message.sessions.firstOrNull { it.nowPlayingItem != null }
        _nowPlaying.value = currentSession?.nowPlayingItem

        // Notify callbacks
        sessionChangeCallbacks.forEach { callback ->
            scope.launch {
                callback(message.sessions)
            }
        }

        Log.d(TAG, "Sessions updated: ${message.sessions.size} active sessions")
    }

    private fun handleUserDataChanged(message: UserDataChangedMessage) {
        userDataChangeCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }

        Log.d(TAG, "User data changed: ${message.userDataList.size} changes")
    }

    private fun handleLibraryChanged(message: LibraryChangedMessage) {
        libraryChangeCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }

        Log.d(TAG, "Library changed - Added: ${message.itemsAdded.size}, Updated: ${message.itemsUpdated.size}, Removed: ${message.itemsRemoved.size}")
    }

    private fun handlePlaystate(message: PlaystateMessage) {
        playstateCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }

        Log.d(TAG, "Playstate command: ${message.command}")
    }
}
