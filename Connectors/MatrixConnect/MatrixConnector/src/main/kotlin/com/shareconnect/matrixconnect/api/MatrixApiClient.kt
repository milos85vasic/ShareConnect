package com.shareconnect.matrixconnect.api

import android.content.Context
import com.google.gson.Gson
import com.shareconnect.matrixconnect.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import java.io.IOException
import java.util.concurrent.TimeUnit

/**
 * Matrix Client-Server API implementation
 *
 * Provides complete Matrix protocol support including:
 * - Authentication and session management
 * - Room operations (create, join, leave, invite)
 * - Message sending and retrieval
 * - Real-time sync with long polling
 * - User profile management
 * - Presence tracking
 * - E2EE key management
 *
 * @param context Android application context
 * @param homeserverUrl Base URL of Matrix homeserver
 */
class MatrixApiClient(
    private val context: Context,
    private var homeserverUrl: String
) {
    private val gson = Gson()
    private val mediaType = "application/json; charset=utf-8".toMediaType()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(90, TimeUnit.SECONDS)  // Long for sync
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private var accessToken: String? = null
    private var userId: String? = null
    private var deviceId: String? = null

    companion object {
        private const val CLIENT_SERVER_API_PATH = "/_matrix/client/r0"
        private const val MEDIA_API_PATH = "/_matrix/media/r0"
    }

    /**
     * Set authentication credentials
     */
    fun setCredentials(accessToken: String, userId: String, deviceId: String) {
        this.accessToken = accessToken
        this.userId = userId
        this.deviceId = deviceId
    }

    /**
     * Clear authentication credentials
     */
    fun clearCredentials() {
        accessToken = null
        userId = null
        deviceId = null
    }

    /**
     * Login to Matrix homeserver
     *
     * @param username Matrix username (without @homeserver)
     * @param password User password
     * @param deviceName Display name for this device
     * @return Result containing login response or error
     */
    suspend fun login(
        username: String,
        password: String,
        deviceName: String = "MatrixConnect"
    ): MatrixResult<MatrixLoginResponse> = withContext(Dispatchers.IO) {
        try {
            val request = MatrixLoginRequest(
                identifier = Identifier(user = username),
                password = password,
                initialDeviceDisplayName = deviceName
            )

            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/login"
            val response = executeRequest<MatrixLoginResponse>(url, "POST", request)

            if (response is MatrixResult.Success) {
                // Store credentials
                setCredentials(
                    response.data.accessToken,
                    response.data.userId,
                    response.data.deviceId
                )
            }

            response
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Logout from Matrix homeserver
     */
    suspend fun logout(): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/logout"
            val response = executeRequest<Map<String, Any>>(url, "POST", emptyMap<String, Any>())

            if (response is MatrixResult.Success) {
                clearCredentials()
            }

            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Sync with Matrix homeserver to get latest events
     *
     * @param since Sync token from previous sync (null for initial sync)
     * @param timeout Long polling timeout in milliseconds
     * @param filter Optional filter to reduce response size
     * @return Result containing sync response or error
     */
    suspend fun sync(
        since: String? = null,
        timeout: Long = 30000,
        filter: SyncFilter? = null
    ): MatrixResult<MatrixSyncResponse> = withContext(Dispatchers.IO) {
        try {
            val url = buildString {
                append("$homeserverUrl$CLIENT_SERVER_API_PATH/sync")
                append("?timeout=$timeout")
                if (since != null) {
                    append("&since=$since")
                }
                if (filter != null) {
                    val filterJson = gson.toJson(filter)
                    append("&filter=$filterJson")
                }
            }

            executeRequest<MatrixSyncResponse>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Create a new room
     *
     * @param request Room creation parameters
     * @return Result containing room ID or error
     */
    suspend fun createRoom(
        request: CreateRoomRequest
    ): MatrixResult<CreateRoomResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/createRoom"
            executeRequest<CreateRoomResponse>(url, "POST", request)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get list of joined rooms
     *
     * @return Result containing list of room IDs
     */
    suspend fun getJoinedRooms(): MatrixResult<JoinedRoomsResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/joined_rooms"
            executeRequest<JoinedRoomsResponse>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get messages from a room
     *
     * @param roomId Room identifier
     * @param from Pagination token to start from
     * @param dir Direction to paginate (b for backwards, f for forwards)
     * @param limit Maximum number of messages to return
     * @return Result containing room messages
     */
    suspend fun getRoomMessages(
        roomId: String,
        from: String,
        dir: String = "b",
        limit: Int = 50
    ): MatrixResult<RoomMessagesResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/rooms/$roomId/messages?from=$from&dir=$dir&limit=$limit"
            executeRequest<RoomMessagesResponse>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Send a message to a room
     *
     * @param roomId Room identifier
     * @param message Message content
     * @param txnId Transaction ID for idempotency
     * @return Result containing event ID
     */
    suspend fun sendMessage(
        roomId: String,
        message: SendMessageRequest,
        txnId: String = System.currentTimeMillis().toString()
    ): MatrixResult<SendMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/rooms/$roomId/send/m.room.message/$txnId"
            executeRequest<SendMessageResponse>(url, "PUT", message)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Send an encrypted message to a room
     *
     * @param roomId Room identifier
     * @param encryptedMessage Encrypted message content
     * @param txnId Transaction ID for idempotency
     * @return Result containing event ID
     */
    suspend fun sendEncryptedMessage(
        roomId: String,
        encryptedMessage: SendEncryptedMessageRequest,
        txnId: String = System.currentTimeMillis().toString()
    ): MatrixResult<SendMessageResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/rooms/$roomId/send/m.room.encrypted/$txnId"
            executeRequest<SendMessageResponse>(url, "PUT", encryptedMessage)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Join a room
     *
     * @param roomIdOrAlias Room ID or alias
     * @return Result containing room ID
     */
    suspend fun joinRoom(
        roomIdOrAlias: String
    ): MatrixResult<Map<String, String>> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/join/$roomIdOrAlias"
            executeRequest<Map<String, String>>(url, "POST", emptyMap<String, Any>())
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Leave a room
     *
     * @param roomId Room identifier
     * @return Result indicating success
     */
    suspend fun leaveRoom(
        roomId: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/rooms/$roomId/leave"
            executeRequest<Map<String, Any>>(url, "POST", emptyMap<String, Any>())
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Invite a user to a room
     *
     * @param roomId Room identifier
     * @param userId User ID to invite
     * @return Result indicating success
     */
    suspend fun inviteUser(
        roomId: String,
        userId: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/rooms/$roomId/invite"
            val request = InviteUserRequest(userId)
            executeRequest<Map<String, Any>>(url, "POST", request)
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get user profile
     *
     * @param userId User identifier
     * @return Result containing user profile
     */
    suspend fun getProfile(
        userId: String
    ): MatrixResult<MatrixProfile> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/profile/$userId"
            executeRequest<MatrixProfile>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Set user display name
     *
     * @param userId User identifier
     * @param displayName New display name
     * @return Result indicating success
     */
    suspend fun setDisplayName(
        userId: String,
        displayName: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/profile/$userId/displayname"
            val request = mapOf("displayname" to displayName)
            executeRequest<Map<String, Any>>(url, "PUT", request)
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Set user avatar URL
     *
     * @param userId User identifier
     * @param avatarUrl MXC URL of avatar image
     * @return Result indicating success
     */
    suspend fun setAvatarUrl(
        userId: String,
        avatarUrl: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/profile/$userId/avatar_url"
            val request = mapOf("avatar_url" to avatarUrl)
            executeRequest<Map<String, Any>>(url, "PUT", request)
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get user presence
     *
     * @param userId User identifier
     * @return Result containing presence information
     */
    suspend fun getPresence(
        userId: String
    ): MatrixResult<MatrixPresence> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/presence/$userId/status"
            executeRequest<MatrixPresence>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Set user presence
     *
     * @param userId User identifier
     * @param presence Presence state (online, offline, unavailable)
     * @param statusMsg Optional status message
     * @return Result indicating success
     */
    suspend fun setPresence(
        userId: String,
        presence: String,
        statusMsg: String? = null
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/presence/$userId/status"
            val request = mutableMapOf("presence" to presence)
            if (statusMsg != null) {
                request["status_msg"] = statusMsg
            }
            executeRequest<Map<String, Any>>(url, "PUT", request)
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Upload E2EE device keys
     *
     * @param request Keys upload request
     * @return Result containing key counts
     */
    suspend fun uploadKeys(
        request: KeysUploadRequest
    ): MatrixResult<KeysUploadResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/keys/upload"
            executeRequest<KeysUploadResponse>(url, "POST", request)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Query E2EE device keys for users
     *
     * @param request Keys query request
     * @return Result containing device keys
     */
    suspend fun queryKeys(
        request: KeysQueryRequest
    ): MatrixResult<KeysQueryResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/keys/query"
            executeRequest<KeysQueryResponse>(url, "POST", request)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get list of user devices
     *
     * @return Result containing device list
     */
    suspend fun getDevices(): MatrixResult<DevicesResponse> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/devices"
            executeRequest<DevicesResponse>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Get device information
     *
     * @param deviceId Device identifier
     * @return Result containing device information
     */
    suspend fun getDevice(
        deviceId: String
    ): MatrixResult<MatrixDevice> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/devices/$deviceId"
            executeRequest<MatrixDevice>(url, "GET")
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Update device display name
     *
     * @param deviceId Device identifier
     * @param displayName New display name
     * @return Result indicating success
     */
    suspend fun updateDevice(
        deviceId: String,
        displayName: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        try {
            val url = "$homeserverUrl$CLIENT_SERVER_API_PATH/devices/$deviceId"
            val request = mapOf("display_name" to displayName)
            executeRequest<Map<String, Any>>(url, "PUT", request)
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.NetworkError(e)
        }
    }

    /**
     * Execute HTTP request with error handling
     */
    private inline fun <reified T> executeRequest(
        url: String,
        method: String,
        body: Any? = null
    ): MatrixResult<T> {
        try {
            val requestBuilder = Request.Builder().url(url)

            // Add authorization header if we have a token
            accessToken?.let {
                requestBuilder.addHeader("Authorization", "Bearer $it")
            }

            // Add request body for POST/PUT methods
            when (method) {
                "POST", "PUT" -> {
                    val jsonBody = if (body != null) {
                        gson.toJson(body)
                    } else {
                        "{}"
                    }
                    requestBuilder.method(method, jsonBody.toRequestBody(mediaType))
                }
                "GET" -> requestBuilder.get()
                "DELETE" -> requestBuilder.delete()
            }

            val request = requestBuilder.build()
            val response = okHttpClient.newCall(request).execute()

            val responseBody = response.body?.string() ?: ""

            return if (response.isSuccessful) {
                val data = gson.fromJson(responseBody, T::class.java)
                MatrixResult.Success(data)
            } else {
                try {
                    val error = gson.fromJson(responseBody, MatrixError::class.java)
                    MatrixResult.Error(
                        code = error.errcode,
                        message = error.error,
                        retryAfterMs = error.retryAfterMs
                    )
                } catch (e: Exception) {
                    MatrixResult.Error(
                        code = "HTTP_${response.code}",
                        message = "HTTP error: ${response.code} ${response.message}"
                    )
                }
            }
        } catch (e: IOException) {
            return MatrixResult.NetworkError(e)
        } catch (e: Exception) {
            return MatrixResult.NetworkError(e)
        }
    }
}
