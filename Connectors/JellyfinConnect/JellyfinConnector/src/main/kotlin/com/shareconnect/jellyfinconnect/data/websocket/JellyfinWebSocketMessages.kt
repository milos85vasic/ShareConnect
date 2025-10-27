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

import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.shareconnect.websocket.WebSocketMessage

/**
 * Jellyfin WebSocket API message types
 * Reference: https://jellyfin.org/docs/general/networking/websocket.html
 */

/**
 * Base class for Jellyfin WebSocket messages
 */
sealed class JellyfinWebSocketMessage : WebSocketMessage() {
    abstract val messageId: String?
}

/**
 * ForceKeepAlive message - sent by server to maintain connection
 */
data class ForceKeepAliveMessage(
    val data: Int
) : JellyfinWebSocketMessage() {
    override val type = "ForceKeepAlive"
    override val messageId: String? = null

    override fun toJson(): String = ""
}

/**
 * KeepAlive message - heartbeat message
 */
data class KeepAliveMessage(
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "KeepAlive"

    override fun toJson(): String {
        return if (messageId != null) {
            """{"MessageType":"KeepAlive","MessageId":"$messageId","Data":{}}"""
        } else {
            """{"MessageType":"KeepAlive","Data":{}}"""
        }
    }
}

/**
 * Sessions message - contains session information
 */
data class SessionsMessage(
    val sessions: List<SessionInfo>,
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "Sessions"

    override fun toJson(): String = ""

    data class SessionInfo(
        val id: String,
        val userId: String,
        val userName: String,
        val client: String,
        val lastActivityDate: String,
        val deviceName: String,
        val deviceId: String,
        val applicationVersion: String,
        val nowPlayingItem: NowPlayingItem? = null,
        val playState: PlayState? = null
    )

    data class NowPlayingItem(
        val id: String,
        val name: String,
        val type: String,
        val runTimeTicks: Long? = null,
        val seriesName: String? = null,
        val indexNumber: Int? = null,
        val parentIndexNumber: Int? = null
    )

    data class PlayState(
        val positionTicks: Long? = null,
        val canSeek: Boolean = false,
        val isPaused: Boolean = false,
        val isMuted: Boolean = false,
        val volumeLevel: Int = 100,
        val playMethod: String? = null
    )
}

/**
 * SessionsStart message - request session updates
 */
data class SessionsStartMessage(
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "SessionsStart"

    override fun toJson(): String {
        return if (messageId != null) {
            """{"MessageType":"SessionsStart","MessageId":"$messageId","Data":"1000,1000"}"""
        } else {
            """{"MessageType":"SessionsStart","Data":"1000,1000"}"""
        }
    }
}

/**
 * SessionsStop message - stop receiving session updates
 */
data class SessionsStopMessage(
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "SessionsStop"

    override fun toJson(): String {
        return if (messageId != null) {
            """{"MessageType":"SessionsStop","MessageId":"$messageId"}"""
        } else {
            """{"MessageType":"SessionsStop"}"""
        }
    }
}

/**
 * UserDataChanged message - user data updates
 */
data class UserDataChangedMessage(
    val userDataList: List<UserDataChange>,
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "UserDataChanged"

    override fun toJson(): String = ""

    data class UserDataChange(
        val userId: String,
        val userDataList: List<UserItemData>
    )

    data class UserItemData(
        val itemId: String,
        val playbackPositionTicks: Long? = null,
        val playCount: Int? = null,
        val isFavorite: Boolean? = null,
        val played: Boolean? = null,
        val lastPlayedDate: String? = null
    )
}

/**
 * LibraryChanged message - library updates
 */
data class LibraryChangedMessage(
    val itemsAdded: List<String> = emptyList(),
    val itemsUpdated: List<String> = emptyList(),
    val itemsRemoved: List<String> = emptyList(),
    val foldersAddedTo: List<String> = emptyList(),
    val foldersRemovedFrom: List<String> = emptyList(),
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "LibraryChanged"

    override fun toJson(): String = ""
}

/**
 * Play message - playback command
 */
data class PlayMessage(
    val itemIds: List<String>,
    val startPositionTicks: Long? = null,
    val controllingUserId: String,
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "Play"

    override fun toJson(): String = ""
}

/**
 * Playstate message - playback control commands
 */
data class PlaystateMessage(
    val command: PlaystateCommand,
    val seekPositionTicks: Long? = null,
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "Playstate"

    override fun toJson(): String = ""

    enum class PlaystateCommand {
        Stop,
        Pause,
        Unpause,
        NextTrack,
        PreviousTrack,
        Seek,
        Rewind,
        FastForward
    }
}

/**
 * GeneralCommand message - general control commands
 */
data class GeneralCommandMessage(
    val name: String,
    val arguments: Map<String, String> = emptyMap(),
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "GeneralCommand"

    override fun toJson(): String = ""
}

/**
 * ServerShuttingDown message - server is shutting down
 */
data class ServerShuttingDownMessage(
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "ServerShuttingDown"

    override fun toJson(): String = ""
}

/**
 * ServerRestarting message - server is restarting
 */
data class ServerRestartingMessage(
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "ServerRestarting"

    override fun toJson(): String = ""
}

/**
 * ScheduledTasksInfo message - scheduled task information
 */
data class ScheduledTasksInfoMessage(
    val tasks: List<TaskInfo>,
    override val messageId: String? = null
) : JellyfinWebSocketMessage() {
    override val type = "ScheduledTasksInfo"

    override fun toJson(): String = ""

    data class TaskInfo(
        val id: String,
        val name: String,
        val state: String,
        val currentProgressPercentage: Double? = null,
        val lastExecutionResult: TaskResult? = null
    )

    data class TaskResult(
        val startTimeUtc: String,
        val endTimeUtc: String,
        val status: String,
        val name: String,
        val key: String
    )
}

/**
 * Parse Jellyfin WebSocket messages from JSON
 */
class JellyfinMessageParser {
    private val gson = Gson()

    fun parse(json: String): JellyfinWebSocketMessage? {
        return try {
            val jsonObject = gson.fromJson(json, JsonObject::class.java)
            val messageType = jsonObject.get("MessageType")?.asString ?: return null
            val messageId = jsonObject.get("MessageId")?.asString
            val data = jsonObject.get("Data")

            when (messageType) {
                "ForceKeepAlive" -> {
                    val keepAliveData = data?.asInt ?: 60
                    ForceKeepAliveMessage(keepAliveData)
                }

                "KeepAlive" -> {
                    KeepAliveMessage(messageId)
                }

                "Sessions" -> {
                    val sessions = parseSessionsData(data)
                    SessionsMessage(sessions, messageId)
                }

                "UserDataChanged" -> {
                    val userDataList = parseUserDataChanged(data)
                    UserDataChangedMessage(userDataList, messageId)
                }

                "LibraryChanged" -> {
                    parseLibraryChanged(data, messageId)
                }

                "Play" -> {
                    parsePlay(data, messageId)
                }

                "Playstate" -> {
                    parsePlaystate(data, messageId)
                }

                "GeneralCommand" -> {
                    parseGeneralCommand(data, messageId)
                }

                "ServerShuttingDown" -> {
                    ServerShuttingDownMessage(messageId)
                }

                "ServerRestarting" -> {
                    ServerRestartingMessage(messageId)
                }

                "ScheduledTasksInfo" -> {
                    val tasks = parseScheduledTasksInfo(data)
                    ScheduledTasksInfoMessage(tasks, messageId)
                }

                else -> null
            }
        } catch (e: Exception) {
            null
        }
    }

    private fun parseSessionsData(data: JsonElement?): List<SessionsMessage.SessionInfo> {
        if (data == null || !data.isJsonArray) return emptyList()

        return try {
            data.asJsonArray.mapNotNull { sessionElement ->
                val sessionObj = sessionElement.asJsonObject

                val id = sessionObj.get("Id")?.asString ?: return@mapNotNull null
                val userId = sessionObj.get("UserId")?.asString ?: ""
                val userName = sessionObj.get("UserName")?.asString ?: ""
                val client = sessionObj.get("Client")?.asString ?: ""
                val lastActivityDate = sessionObj.get("LastActivityDate")?.asString ?: ""
                val deviceName = sessionObj.get("DeviceName")?.asString ?: ""
                val deviceId = sessionObj.get("DeviceId")?.asString ?: ""
                val applicationVersion = sessionObj.get("ApplicationVersion")?.asString ?: ""

                val nowPlayingItem = sessionObj.getAsJsonObject("NowPlayingItem")?.let { item ->
                    SessionsMessage.NowPlayingItem(
                        id = item.get("Id")?.asString ?: "",
                        name = item.get("Name")?.asString ?: "",
                        type = item.get("Type")?.asString ?: "",
                        runTimeTicks = item.get("RunTimeTicks")?.asLong,
                        seriesName = item.get("SeriesName")?.asString,
                        indexNumber = item.get("IndexNumber")?.asInt,
                        parentIndexNumber = item.get("ParentIndexNumber")?.asInt
                    )
                }

                val playState = sessionObj.getAsJsonObject("PlayState")?.let { state ->
                    SessionsMessage.PlayState(
                        positionTicks = state.get("PositionTicks")?.asLong,
                        canSeek = state.get("CanSeek")?.asBoolean ?: false,
                        isPaused = state.get("IsPaused")?.asBoolean ?: false,
                        isMuted = state.get("IsMuted")?.asBoolean ?: false,
                        volumeLevel = state.get("VolumeLevel")?.asInt ?: 100,
                        playMethod = state.get("PlayMethod")?.asString
                    )
                }

                SessionsMessage.SessionInfo(
                    id = id,
                    userId = userId,
                    userName = userName,
                    client = client,
                    lastActivityDate = lastActivityDate,
                    deviceName = deviceName,
                    deviceId = deviceId,
                    applicationVersion = applicationVersion,
                    nowPlayingItem = nowPlayingItem,
                    playState = playState
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseUserDataChanged(data: JsonElement?): List<UserDataChangedMessage.UserDataChange> {
        if (data == null || !data.isJsonObject) return emptyList()

        return try {
            val dataObj = data.asJsonObject
            val userDataArray = dataObj.getAsJsonArray("UserDataList") ?: return emptyList()

            userDataArray.mapNotNull { userDataElement ->
                val userDataObj = userDataElement.asJsonObject
                val userId = userDataObj.get("UserId")?.asString ?: return@mapNotNull null
                val userItemDataArray = userDataObj.getAsJsonArray("UserItemDataList") ?: return@mapNotNull null

                val userItemDataList = userItemDataArray.mapNotNull { itemElement ->
                    val itemObj = itemElement.asJsonObject
                    UserDataChangedMessage.UserItemData(
                        itemId = itemObj.get("ItemId")?.asString ?: return@mapNotNull null,
                        playbackPositionTicks = itemObj.get("PlaybackPositionTicks")?.asLong,
                        playCount = itemObj.get("PlayCount")?.asInt,
                        isFavorite = itemObj.get("IsFavorite")?.asBoolean,
                        played = itemObj.get("Played")?.asBoolean,
                        lastPlayedDate = itemObj.get("LastPlayedDate")?.asString
                    )
                }

                UserDataChangedMessage.UserDataChange(
                    userId = userId,
                    userDataList = userItemDataList
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun parseLibraryChanged(data: JsonElement?, messageId: String?): LibraryChangedMessage {
        if (data == null || !data.isJsonObject) return LibraryChangedMessage(messageId = messageId)

        return try {
            val dataObj = data.asJsonObject
            LibraryChangedMessage(
                itemsAdded = dataObj.getAsJsonArray("ItemsAdded")?.map { it.asString } ?: emptyList(),
                itemsUpdated = dataObj.getAsJsonArray("ItemsUpdated")?.map { it.asString } ?: emptyList(),
                itemsRemoved = dataObj.getAsJsonArray("ItemsRemoved")?.map { it.asString } ?: emptyList(),
                foldersAddedTo = dataObj.getAsJsonArray("FoldersAddedTo")?.map { it.asString } ?: emptyList(),
                foldersRemovedFrom = dataObj.getAsJsonArray("FoldersRemovedFrom")?.map { it.asString } ?: emptyList(),
                messageId = messageId
            )
        } catch (e: Exception) {
            LibraryChangedMessage(messageId = messageId)
        }
    }

    private fun parsePlay(data: JsonElement?, messageId: String?): PlayMessage? {
        if (data == null || !data.isJsonObject) return null

        return try {
            val dataObj = data.asJsonObject
            val itemIds = dataObj.getAsJsonArray("ItemIds")?.map { it.asString } ?: return null
            PlayMessage(
                itemIds = itemIds,
                startPositionTicks = dataObj.get("StartPositionTicks")?.asLong,
                controllingUserId = dataObj.get("ControllingUserId")?.asString ?: "",
                messageId = messageId
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parsePlaystate(data: JsonElement?, messageId: String?): PlaystateMessage? {
        if (data == null || !data.isJsonObject) return null

        return try {
            val dataObj = data.asJsonObject
            val commandStr = dataObj.get("Command")?.asString ?: return null
            val command = PlaystateMessage.PlaystateCommand.valueOf(commandStr)
            PlaystateMessage(
                command = command,
                seekPositionTicks = dataObj.get("SeekPositionTicks")?.asLong,
                messageId = messageId
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseGeneralCommand(data: JsonElement?, messageId: String?): GeneralCommandMessage? {
        if (data == null || !data.isJsonObject) return null

        return try {
            val dataObj = data.asJsonObject
            val name = dataObj.get("Name")?.asString ?: return null
            val arguments = dataObj.getAsJsonObject("Arguments")?.entrySet()?.associate {
                it.key to it.value.asString
            } ?: emptyMap()

            GeneralCommandMessage(
                name = name,
                arguments = arguments,
                messageId = messageId
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseScheduledTasksInfo(data: JsonElement?): List<ScheduledTasksInfoMessage.TaskInfo> {
        if (data == null || !data.isJsonArray) return emptyList()

        return try {
            data.asJsonArray.mapNotNull { taskElement ->
                val taskObj = taskElement.asJsonObject
                val id = taskObj.get("Id")?.asString ?: return@mapNotNull null
                val name = taskObj.get("Name")?.asString ?: ""
                val state = taskObj.get("State")?.asString ?: ""

                val lastExecutionResult = taskObj.getAsJsonObject("LastExecutionResult")?.let { result ->
                    ScheduledTasksInfoMessage.TaskResult(
                        startTimeUtc = result.get("StartTimeUtc")?.asString ?: "",
                        endTimeUtc = result.get("EndTimeUtc")?.asString ?: "",
                        status = result.get("Status")?.asString ?: "",
                        name = result.get("Name")?.asString ?: "",
                        key = result.get("Key")?.asString ?: ""
                    )
                }

                ScheduledTasksInfoMessage.TaskInfo(
                    id = id,
                    name = name,
                    state = state,
                    currentProgressPercentage = taskObj.get("CurrentProgressPercentage")?.asDouble,
                    lastExecutionResult = lastExecutionResult
                )
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
