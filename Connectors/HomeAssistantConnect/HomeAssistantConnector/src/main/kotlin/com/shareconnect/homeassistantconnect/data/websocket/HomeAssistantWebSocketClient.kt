package com.shareconnect.homeassistantconnect.data.websocket

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
import java.util.concurrent.atomic.AtomicInteger

/**
 * Home Assistant WebSocket client
 * Handles authentication, subscriptions, and real-time state updates
 */
class HomeAssistantWebSocketClient(
    private val serverUrl: String,
    private val accessToken: String,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
) {
    companion object {
        private const val TAG = "HAWebSocketClient"
    }

    private val messageParser = HomeAssistantMessageParser()
    private val messageIdCounter = AtomicInteger(1)

    private val _authenticationState = MutableStateFlow<AuthenticationState>(AuthenticationState.NotAuthenticated)
    val authenticationState: StateFlow<AuthenticationState> = _authenticationState.asStateFlow()

    private val _entities = MutableStateFlow<Map<String, EventMessage.EntityState>>(emptyMap())
    val entities: StateFlow<Map<String, EventMessage.EntityState>> = _entities.asStateFlow()

    private var webSocketClient: OkHttpWebSocketClient? = null
    private var eventsSubscriptionId: Int? = null

    private val stateChangeCallbacks = mutableListOf<(EventMessage.StateChangeData) -> Unit>()
    private val entityCallbacks = mutableMapOf<String, MutableList<(EventMessage.EntityState) -> Unit>>()

    /**
     * Connect to Home Assistant WebSocket API
     */
    suspend fun connect() {
        val wsUrl = serverUrl.replace("http://", "ws://").replace("https://", "wss://")
        val fullUrl = "$wsUrl/api/websocket"

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

        webSocketClient?.subscribe("auth_ok") { message ->
            if (message is AuthOkMessage) {
                handleAuthOk(message)
            }
        }

        webSocketClient?.subscribe("auth_invalid") { message ->
            if (message is AuthInvalidMessage) {
                handleAuthInvalid(message)
            }
        }

        webSocketClient?.subscribe("result") { message ->
            if (message is ResultMessage) {
                handleResult(message)
            }
        }

        webSocketClient?.subscribe("event") { message ->
            if (message is EventMessage) {
                handleEvent(message)
            }
        }

        webSocketClient?.connect(
            onConnected = {
                Log.d(TAG, "WebSocket connected, sending auth...")
                scope.launch {
                    authenticate()
                }
            },
            onDisconnected = { reason ->
                Log.d(TAG, "WebSocket disconnected: $reason")
                _authenticationState.value = AuthenticationState.NotAuthenticated
            },
            onError = { error ->
                Log.e(TAG, "WebSocket error: ${error.message}", error)
                _authenticationState.value = AuthenticationState.Error(error.message ?: "Unknown error")
            }
        )
    }

    /**
     * Disconnect from WebSocket
     */
    suspend fun disconnect() {
        webSocketClient?.disconnect()
        webSocketClient = null
        _authenticationState.value = AuthenticationState.NotAuthenticated
    }

    /**
     * Subscribe to state changes for all entities
     */
    fun subscribeToStateChanges(callback: (EventMessage.StateChangeData) -> Unit) {
        stateChangeCallbacks.add(callback)
    }

    /**
     * Subscribe to state changes for a specific entity
     */
    fun subscribeToEntity(entityId: String, callback: (EventMessage.EntityState) -> Unit) {
        entityCallbacks.getOrPut(entityId) { mutableListOf() }.add(callback)
    }

    /**
     * Call a service (e.g., turn on a light)
     */
    suspend fun callService(
        domain: String,
        service: String,
        entityId: String? = null,
        serviceData: Map<String, Any>? = null
    ): Boolean {
        val id = messageIdCounter.getAndIncrement()
        val target = if (entityId != null) mapOf("entity_id" to entityId) else null

        val message = CallServiceMessage(
            id = id,
            domain = domain,
            service = service,
            serviceData = serviceData,
            target = target
        )

        return webSocketClient?.send(message) ?: false
    }

    /**
     * Get all current states
     */
    suspend fun getStates(): Boolean {
        val id = messageIdCounter.getAndIncrement()
        val message = GetStatesMessage(id)

        return webSocketClient?.send(message) ?: false
    }

    /**
     * Turn on an entity (light, switch, etc.)
     */
    suspend fun turnOn(entityId: String): Boolean {
        val domain = entityId.split(".").firstOrNull() ?: return false
        return callService(domain, "turn_on", entityId)
    }

    /**
     * Turn off an entity
     */
    suspend fun turnOff(entityId: String): Boolean {
        val domain = entityId.split(".").firstOrNull() ?: return false
        return callService(domain, "turn_off", entityId)
    }

    /**
     * Toggle an entity
     */
    suspend fun toggle(entityId: String): Boolean {
        val domain = entityId.split(".").firstOrNull() ?: return false
        return callService(domain, "toggle", entityId)
    }

    /**
     * Get connection state
     */
    fun getConnectionState(): StateFlow<ConnectionState> {
        return webSocketClient?.connectionState ?: MutableStateFlow(ConnectionState.Disconnected)
    }

    /**
     * Check if connected and authenticated
     */
    fun isReady(): Boolean {
        return webSocketClient?.isConnected() == true &&
               _authenticationState.value is AuthenticationState.Authenticated
    }

    // Private methods

    private suspend fun authenticate() {
        _authenticationState.value = AuthenticationState.Authenticating

        val authMessage = AuthMessage(accessToken)
        webSocketClient?.send(authMessage)
    }

    private fun handleAuthOk(message: AuthOkMessage) {
        Log.d(TAG, "Authentication successful, HA version: ${message.haVersion}")
        _authenticationState.value = AuthenticationState.Authenticated(message.haVersion)

        // Subscribe to state change events
        scope.launch {
            subscribeToEvents()
            // Request initial states
            getStates()
        }
    }

    private fun handleAuthInvalid(message: AuthInvalidMessage) {
        Log.e(TAG, "Authentication failed: ${message.message}")
        _authenticationState.value = AuthenticationState.Error(message.message)
    }

    private suspend fun subscribeToEvents() {
        val id = messageIdCounter.getAndIncrement()
        val message = SubscribeEventsMessage(id, "state_changed")

        webSocketClient?.send(message)
        eventsSubscriptionId = id
    }

    private fun handleResult(message: ResultMessage) {
        if (message.success) {
            when (message.result) {
                is List<*> -> {
                    // Handle get_states result
                    // This would require proper JSON deserialization
                    Log.d(TAG, "Received states result")
                }
            }
        } else {
            Log.e(TAG, "Command failed: ${message.error?.message}")
        }
    }

    private fun handleEvent(message: EventMessage) {
        val stateChangeData = message.eventData.data ?: return

        // Update entities map
        val newState = stateChangeData.newState
        if (newState != null) {
            val currentEntities = _entities.value.toMutableMap()
            currentEntities[newState.entityId] = newState
            _entities.value = currentEntities

            // Notify entity-specific callbacks
            entityCallbacks[newState.entityId]?.forEach { callback ->
                scope.launch {
                    callback(newState)
                }
            }
        }

        // Notify state change callbacks
        stateChangeCallbacks.forEach { callback ->
            scope.launch {
                callback(stateChangeData)
            }
        }

        Log.d(TAG, "State changed: ${stateChangeData.entityId} -> ${newState?.state}")
    }

    /**
     * Authentication state
     */
    sealed class AuthenticationState {
        object NotAuthenticated : AuthenticationState()
        object Authenticating : AuthenticationState()
        data class Authenticated(val haVersion: String) : AuthenticationState()
        data class Error(val message: String) : AuthenticationState()
    }
}
