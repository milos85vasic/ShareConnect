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


package com.shareconnect.matrixconnect.models

import com.google.gson.annotations.SerializedName

/**
 * Matrix login response containing access token and user information
 */
data class MatrixLoginResponse(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("access_token")
    val accessToken: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("home_server")
    val homeServer: String? = null,
    @SerializedName("well_known")
    val wellKnown: WellKnown? = null
)

/**
 * Well-known server information for auto-discovery
 */
data class WellKnown(
    @SerializedName("m.homeserver")
    val homeserver: HomeserverInfo
)

data class HomeserverInfo(
    @SerializedName("base_url")
    val baseUrl: String
)

/**
 * Login request payload
 */
data class MatrixLoginRequest(
    @SerializedName("type")
    val type: String = "m.login.password",
    @SerializedName("identifier")
    val identifier: Identifier,
    @SerializedName("password")
    val password: String,
    @SerializedName("device_id")
    val deviceId: String? = null,
    @SerializedName("initial_device_display_name")
    val initialDeviceDisplayName: String? = null
)

data class Identifier(
    @SerializedName("type")
    val type: String = "m.id.user",
    @SerializedName("user")
    val user: String
)

/**
 * Matrix sync response containing rooms, events, and presence
 */
data class MatrixSyncResponse(
    @SerializedName("next_batch")
    val nextBatch: String,
    @SerializedName("rooms")
    val rooms: Rooms? = null,
    @SerializedName("presence")
    val presence: Presence? = null,
    @SerializedName("account_data")
    val accountData: AccountData? = null,
    @SerializedName("to_device")
    val toDevice: ToDevice? = null
)

data class Rooms(
    @SerializedName("join")
    val join: Map<String, JoinedRoom>? = null,
    @SerializedName("invite")
    val invite: Map<String, InvitedRoom>? = null,
    @SerializedName("leave")
    val leave: Map<String, LeftRoom>? = null
)

data class JoinedRoom(
    @SerializedName("timeline")
    val timeline: Timeline,
    @SerializedName("state")
    val state: State? = null,
    @SerializedName("ephemeral")
    val ephemeral: Ephemeral? = null,
    @SerializedName("account_data")
    val accountData: AccountData? = null,
    @SerializedName("unread_notifications")
    val unreadNotifications: UnreadNotifications? = null
)

data class InvitedRoom(
    @SerializedName("invite_state")
    val inviteState: InviteState
)

data class LeftRoom(
    @SerializedName("timeline")
    val timeline: Timeline,
    @SerializedName("state")
    val state: State? = null
)

data class Timeline(
    @SerializedName("events")
    val events: List<MatrixEvent>,
    @SerializedName("limited")
    val limited: Boolean = false,
    @SerializedName("prev_batch")
    val prevBatch: String? = null
)

data class State(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

data class Ephemeral(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

data class AccountData(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

data class InviteState(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

data class UnreadNotifications(
    @SerializedName("highlight_count")
    val highlightCount: Int = 0,
    @SerializedName("notification_count")
    val notificationCount: Int = 0
)

data class Presence(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

data class ToDevice(
    @SerializedName("events")
    val events: List<MatrixEvent>
)

/**
 * Matrix event representing state changes, messages, etc.
 */
data class MatrixEvent(
    @SerializedName("type")
    val type: String,
    @SerializedName("event_id")
    val eventId: String? = null,
    @SerializedName("sender")
    val sender: String,
    @SerializedName("origin_server_ts")
    val originServerTs: Long,
    @SerializedName("content")
    val content: Map<String, Any>,
    @SerializedName("state_key")
    val stateKey: String? = null,
    @SerializedName("prev_content")
    val prevContent: Map<String, Any>? = null,
    @SerializedName("room_id")
    val roomId: String? = null,
    @SerializedName("unsigned")
    val unsigned: Map<String, Any>? = null
)

/**
 * Matrix room information
 */
data class MatrixRoom(
    @SerializedName("room_id")
    val roomId: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("topic")
    val topic: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null,
    @SerializedName("canonical_alias")
    val canonicalAlias: String? = null,
    @SerializedName("joined_members")
    val joinedMembers: Int = 0,
    @SerializedName("is_encrypted")
    val isEncrypted: Boolean = false,
    @SerializedName("is_direct")
    val isDirect: Boolean = false,
    @SerializedName("last_message")
    val lastMessage: MatrixMessage? = null,
    @SerializedName("unread_count")
    val unreadCount: Int = 0
)

/**
 * Matrix message content
 */
data class MatrixMessage(
    @SerializedName("msgtype")
    val msgtype: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("formatted_body")
    val formattedBody: String? = null,
    @SerializedName("format")
    val format: String? = null,
    @SerializedName("url")
    val url: String? = null,
    @SerializedName("info")
    val info: MessageInfo? = null,
    @SerializedName("relates_to")
    val relatesTo: Map<String, Any>? = null
)

data class MessageInfo(
    @SerializedName("mimetype")
    val mimetype: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("w")
    val width: Int? = null,
    @SerializedName("h")
    val height: Int? = null,
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String? = null,
    @SerializedName("thumbnail_info")
    val thumbnailInfo: ThumbnailInfo? = null
)

data class ThumbnailInfo(
    @SerializedName("mimetype")
    val mimetype: String? = null,
    @SerializedName("size")
    val size: Long? = null,
    @SerializedName("w")
    val width: Int? = null,
    @SerializedName("h")
    val height: Int? = null
)

/**
 * User profile information
 */
data class MatrixProfile(
    @SerializedName("displayname")
    val displayname: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

/**
 * User presence status
 */
data class MatrixPresence(
    @SerializedName("presence")
    val presence: String,
    @SerializedName("last_active_ago")
    val lastActiveAgo: Long? = null,
    @SerializedName("status_msg")
    val statusMsg: String? = null,
    @SerializedName("currently_active")
    val currentlyActive: Boolean? = null
)

/**
 * Room creation request
 */
data class CreateRoomRequest(
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("topic")
    val topic: String? = null,
    @SerializedName("is_direct")
    val isDirect: Boolean = false,
    @SerializedName("invite")
    val invite: List<String>? = null,
    @SerializedName("room_alias_name")
    val roomAliasName: String? = null,
    @SerializedName("visibility")
    val visibility: String = "private",
    @SerializedName("preset")
    val preset: String? = null,
    @SerializedName("initial_state")
    val initialState: List<StateEvent>? = null
)

data class StateEvent(
    @SerializedName("type")
    val type: String,
    @SerializedName("state_key")
    val stateKey: String = "",
    @SerializedName("content")
    val content: Map<String, Any>
)

data class CreateRoomResponse(
    @SerializedName("room_id")
    val roomId: String
)

/**
 * Joined rooms response
 */
data class JoinedRoomsResponse(
    @SerializedName("joined_rooms")
    val joinedRooms: List<String>
)

/**
 * Room messages response
 */
data class RoomMessagesResponse(
    @SerializedName("start")
    val start: String,
    @SerializedName("end")
    val end: String? = null,
    @SerializedName("chunk")
    val chunk: List<MatrixEvent>,
    @SerializedName("state")
    val state: List<MatrixEvent>? = null
)

/**
 * Send message request
 */
data class SendMessageRequest(
    @SerializedName("msgtype")
    val msgtype: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("formatted_body")
    val formattedBody: String? = null,
    @SerializedName("format")
    val format: String? = null
)

data class SendMessageResponse(
    @SerializedName("event_id")
    val eventId: String
)

/**
 * Send encrypted message request
 */
data class SendEncryptedMessageRequest(
    @SerializedName("algorithm")
    val algorithm: String = "m.megolm.v1.aes-sha2",
    @SerializedName("sender_key")
    val senderKey: String,
    @SerializedName("ciphertext")
    val ciphertext: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("device_id")
    val deviceId: String
)

/**
 * Room invite request
 */
data class InviteUserRequest(
    @SerializedName("user_id")
    val userId: String
)

/**
 * E2EE Keys upload request
 */
data class KeysUploadRequest(
    @SerializedName("device_keys")
    val deviceKeys: DeviceKeys? = null,
    @SerializedName("one_time_keys")
    val oneTimeKeys: Map<String, Any>? = null
)

data class DeviceKeys(
    @SerializedName("user_id")
    val userId: String,
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("algorithms")
    val algorithms: List<String>,
    @SerializedName("keys")
    val keys: Map<String, String>,
    @SerializedName("signatures")
    val signatures: Map<String, Map<String, String>>
)

data class KeysUploadResponse(
    @SerializedName("one_time_key_counts")
    val oneTimeKeyCounts: Map<String, Int>
)

/**
 * E2EE Keys query request
 */
data class KeysQueryRequest(
    @SerializedName("timeout")
    val timeout: Int = 10000,
    @SerializedName("device_keys")
    val deviceKeys: Map<String, List<String>>
)

data class KeysQueryResponse(
    @SerializedName("device_keys")
    val deviceKeys: Map<String, Map<String, DeviceKeys>>,
    @SerializedName("failures")
    val failures: Map<String, Any>? = null
)

/**
 * Matrix device information
 */
data class MatrixDevice(
    @SerializedName("device_id")
    val deviceId: String,
    @SerializedName("display_name")
    val displayName: String? = null,
    @SerializedName("last_seen_ip")
    val lastSeenIp: String? = null,
    @SerializedName("last_seen_ts")
    val lastSeenTs: Long? = null
)

data class DevicesResponse(
    @SerializedName("devices")
    val devices: List<MatrixDevice>
)

/**
 * Filter for sync requests
 */
data class SyncFilter(
    @SerializedName("room")
    val room: RoomFilter? = null,
    @SerializedName("presence")
    val presence: EventFilter? = null,
    @SerializedName("account_data")
    val accountData: EventFilter? = null
)

data class RoomFilter(
    @SerializedName("timeline")
    val timeline: RoomEventFilter? = null,
    @SerializedName("state")
    val state: StateFilter? = null,
    @SerializedName("ephemeral")
    val ephemeral: RoomEventFilter? = null,
    @SerializedName("account_data")
    val accountData: RoomEventFilter? = null
)

data class RoomEventFilter(
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("types")
    val types: List<String>? = null,
    @SerializedName("not_types")
    val notTypes: List<String>? = null,
    @SerializedName("senders")
    val senders: List<String>? = null,
    @SerializedName("not_senders")
    val notSenders: List<String>? = null
)

data class StateFilter(
    @SerializedName("types")
    val types: List<String>? = null,
    @SerializedName("not_types")
    val notTypes: List<String>? = null,
    @SerializedName("lazy_load_members")
    val lazyLoadMembers: Boolean = false
)

data class EventFilter(
    @SerializedName("limit")
    val limit: Int? = null,
    @SerializedName("types")
    val types: List<String>? = null,
    @SerializedName("not_types")
    val notTypes: List<String>? = null,
    @SerializedName("senders")
    val senders: List<String>? = null,
    @SerializedName("not_senders")
    val notSenders: List<String>? = null
)

/**
 * Generic Matrix API error response
 */
data class MatrixError(
    @SerializedName("errcode")
    val errcode: String,
    @SerializedName("error")
    val error: String,
    @SerializedName("retry_after_ms")
    val retryAfterMs: Long? = null
)

/**
 * Matrix API Result wrapper for error handling
 */
sealed class MatrixResult<out T> {
    data class Success<T>(val data: T) : MatrixResult<T>()
    data class Error(val code: String, val message: String, val retryAfterMs: Long? = null) : MatrixResult<Nothing>()
    data class NetworkError(val exception: Throwable) : MatrixResult<Nothing>()
}
