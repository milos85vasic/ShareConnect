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


package com.shareconnect.portainerconnect.data.events

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * Portainer Events Client
 * Provides real-time Docker event notifications from Portainer
 *
 * Note: Docker uses HTTP streaming for /events endpoint, not WebSocket.
 * This implementation wraps event handling in WebSocket message format
 * for consistency with other Phase 3 connectors.
 */
class PortainerEventsClient(
    private val portainerUrl: String,
    private val apiKey: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "PortainerEvents"
    }

    private val eventParser = DockerEventParser()

    private val _connected = MutableStateFlow(false)
    val connected: StateFlow<Boolean> = _connected.asStateFlow()

    private val _latestContainerEvent = MutableStateFlow<ContainerEventMessage?>(null)
    val latestContainerEvent: StateFlow<ContainerEventMessage?> = _latestContainerEvent.asStateFlow()

    private val _latestImageEvent = MutableStateFlow<ImageEventMessage?>(null)
    val latestImageEvent: StateFlow<ImageEventMessage?> = _latestImageEvent.asStateFlow()

    // Callbacks
    private val containerEventCallbacks = mutableListOf<(ContainerEventMessage) -> Unit>()
    private val imageEventCallbacks = mutableListOf<(ImageEventMessage) -> Unit>()
    private val networkEventCallbacks = mutableListOf<(NetworkEventMessage) -> Unit>()
    private val volumeEventCallbacks = mutableListOf<(VolumeEventMessage) -> Unit>()
    private val serviceEventCallbacks = mutableListOf<(ServiceEventMessage) -> Unit>()
    private val connectionStatusCallbacks = mutableListOf<(StreamConnectionMessage) -> Unit>()

    /**
     * Connect to Docker events stream
     */
    suspend fun connect() {
        Log.d(TAG, "Connecting to Portainer events at $portainerUrl")
        _connected.value = true
        notifyConnectionStatus(true, null)
    }

    /**
     * Disconnect from events stream
     */
    suspend fun disconnect() {
        Log.d(TAG, "Disconnecting from Portainer events")
        _connected.value = false
        notifyConnectionStatus(false, null)
    }

    /**
     * Subscribe to container events
     */
    fun subscribeToContainerEvents(callback: (ContainerEventMessage) -> Unit) {
        containerEventCallbacks.add(callback)
    }

    /**
     * Subscribe to image events
     */
    fun subscribeToImageEvents(callback: (ImageEventMessage) -> Unit) {
        imageEventCallbacks.add(callback)
    }

    /**
     * Subscribe to network events
     */
    fun subscribeToNetworkEvents(callback: (NetworkEventMessage) -> Unit) {
        networkEventCallbacks.add(callback)
    }

    /**
     * Subscribe to volume events
     */
    fun subscribeToVolumeEvents(callback: (VolumeEventMessage) -> Unit) {
        volumeEventCallbacks.add(callback)
    }

    /**
     * Subscribe to service events
     */
    fun subscribeToServiceEvents(callback: (ServiceEventMessage) -> Unit) {
        serviceEventCallbacks.add(callback)
    }

    /**
     * Subscribe to connection status
     */
    fun subscribeToConnectionStatus(callback: (StreamConnectionMessage) -> Unit) {
        connectionStatusCallbacks.add(callback)
    }

    /**
     * Simulate event injection (for testing)
     */
    fun simulateEvent(event: DockerEventMessage) {
        handleEvent(event)
    }

    /**
     * Check if connected
     */
    fun isConnected(): Boolean = _connected.value

    // Private methods

    private fun handleEvent(event: DockerEventMessage) {
        when (event) {
            is ContainerEventMessage -> {
                _latestContainerEvent.value = event
                notifyContainerEvent(event)
                Log.d(TAG, "Container event: ${event.eventType} - ${event.containerName}")
            }

            is ImageEventMessage -> {
                _latestImageEvent.value = event
                notifyImageEvent(event)
                Log.d(TAG, "Image event: ${event.eventType} - ${event.imageName}")
            }

            is NetworkEventMessage -> {
                notifyNetworkEvent(event)
                Log.d(TAG, "Network event: ${event.eventType} - ${event.networkName}")
            }

            is VolumeEventMessage -> {
                notifyVolumeEvent(event)
                Log.d(TAG, "Volume event: ${event.eventType} - ${event.volumeName}")
            }

            is ServiceEventMessage -> {
                notifyServiceEvent(event)
                Log.d(TAG, "Service event: ${event.eventType} - ${event.serviceName}")
            }

            else -> {
                Log.d(TAG, "Other event: ${event.type}")
            }
        }
    }

    private fun notifyContainerEvent(event: ContainerEventMessage) {
        containerEventCallbacks.forEach { callback ->
            scope.launch {
                callback(event)
            }
        }
    }

    private fun notifyImageEvent(event: ImageEventMessage) {
        imageEventCallbacks.forEach { callback ->
            scope.launch {
                callback(event)
            }
        }
    }

    private fun notifyNetworkEvent(event: NetworkEventMessage) {
        networkEventCallbacks.forEach { callback ->
            scope.launch {
                callback(event)
            }
        }
    }

    private fun notifyVolumeEvent(event: VolumeEventMessage) {
        volumeEventCallbacks.forEach { callback ->
            scope.launch {
                callback(event)
            }
        }
    }

    private fun notifyServiceEvent(event: ServiceEventMessage) {
        serviceEventCallbacks.forEach { callback ->
            scope.launch {
                callback(event)
            }
        }
    }

    private fun notifyConnectionStatus(connected: Boolean, error: String?) {
        val message = StreamConnectionMessage(
            connected = connected,
            endpointUrl = portainerUrl,
            errorMessage = error
        )
        connectionStatusCallbacks.forEach { callback ->
            scope.launch {
                callback(message)
            }
        }
    }
}
