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


package com.shareconnect.minecraftserverconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Minecraft server status response from server ping
 */
data class MinecraftServerStatus(
    @SerializedName("version") val version: MinecraftVersion,
    @SerializedName("players") val players: MinecraftPlayers,
    @SerializedName("description") val description: MinecraftDescription,
    @SerializedName("favicon") val favicon: String? = null,
    @SerializedName("modinfo") val modInfo: MinecraftModInfo? = null
) {
    val isOnline: Boolean
        get() = true // If we get a response, server is online

    val playerCount: Int
        get() = players.online

    val maxPlayers: Int
        get() = players.max

    val serverVersion: String
        get() = version.name

    val motd: String
        get() = description.text
}

/**
 * Minecraft version information
 */
data class MinecraftVersion(
    @SerializedName("name") val name: String,
    @SerializedName("protocol") val protocol: Int
)

/**
 * Minecraft player information
 */
data class MinecraftPlayers(
    @SerializedName("max") val max: Int,
    @SerializedName("online") val online: Int,
    @SerializedName("sample") val sample: List<MinecraftPlayer>? = null
)

/**
 * Individual Minecraft player
 */
data class MinecraftPlayer(
    @SerializedName("name") val name: String,
    @SerializedName("id") val id: String
)

/**
 * Server description/MOTD
 */
data class MinecraftDescription(
    @SerializedName("text") val text: String = "",
    @SerializedName("extra") val extra: List<MinecraftDescriptionComponent>? = null
) {
    val plainText: String
        get() {
            if (text.isNotEmpty()) return text
            return extra?.joinToString("") { it.text } ?: ""
        }
}

/**
 * MOTD text component
 */
data class MinecraftDescriptionComponent(
    @SerializedName("text") val text: String = "",
    @SerializedName("color") val color: String? = null,
    @SerializedName("bold") val bold: Boolean? = null,
    @SerializedName("italic") val italic: Boolean? = null
)

/**
 * Minecraft mod information (for modded servers)
 */
data class MinecraftModInfo(
    @SerializedName("type") val type: String,
    @SerializedName("modList") val modList: List<MinecraftMod>? = null
)

/**
 * Individual mod information
 */
data class MinecraftMod(
    @SerializedName("modid") val modId: String,
    @SerializedName("version") val version: String
)

/**
 * RCON command response
 */
data class MinecraftRconResponse(
    val requestId: Int,
    val type: RconPacketType,
    val payload: String,
    val isSuccess: Boolean = true,
    val errorMessage: String? = null
) {
    companion object {
        fun success(requestId: Int, payload: String): MinecraftRconResponse {
            return MinecraftRconResponse(
                requestId = requestId,
                type = RconPacketType.RESPONSE_VALUE,
                payload = payload,
                isSuccess = true
            )
        }

        fun error(requestId: Int, errorMessage: String): MinecraftRconResponse {
            return MinecraftRconResponse(
                requestId = requestId,
                type = RconPacketType.RESPONSE_VALUE,
                payload = "",
                isSuccess = false,
                errorMessage = errorMessage
            )
        }
    }
}

/**
 * RCON packet types according to Valve RCON protocol
 */
enum class RconPacketType(val id: Int) {
    AUTH(3),
    AUTH_RESPONSE(2),
    EXECCOMMAND(2),
    RESPONSE_VALUE(0);

    companion object {
        fun fromId(id: Int): RconPacketType? {
            return entries.find { it.id == id }
        }
    }
}

/**
 * Minecraft command template
 */
data class MinecraftCommand(
    val name: String,
    val command: String,
    val description: String,
    val category: CommandCategory,
    val requiresOp: Boolean = true,
    val parameters: List<CommandParameter> = emptyList()
) {
    fun buildCommand(vararg args: String): String {
        var builtCommand = command
        parameters.forEachIndexed { index, param ->
            if (index < args.size) {
                builtCommand = builtCommand.replace("{${param.name}}", args[index])
            }
        }
        return builtCommand
    }
}

/**
 * Command parameter definition
 */
data class CommandParameter(
    val name: String,
    val description: String,
    val type: ParameterType,
    val required: Boolean = true,
    val defaultValue: String? = null
)

/**
 * Parameter types for commands
 */
enum class ParameterType {
    STRING,
    INTEGER,
    BOOLEAN,
    PLAYER_NAME,
    COORDINATES,
    ITEM_ID,
    GAMEMODE
}

/**
 * Command categories
 */
enum class CommandCategory {
    PLAYER_MANAGEMENT,
    SERVER_CONTROL,
    WORLD_MANAGEMENT,
    GAME_RULES,
    WHITELIST,
    OPERATOR,
    BAN_MANAGEMENT,
    TELEPORT,
    TIME_WEATHER,
    GIVE_ITEMS,
    EXPERIENCE,
    DIFFICULTY,
    SCOREBOARD,
    OTHER
}

/**
 * Server information
 */
data class MinecraftServerInfo(
    val host: String,
    val gamePort: Int = 25565,
    val rconPort: Int = 25575,
    val rconPassword: String = "",
    val name: String = "",
    val description: String = ""
) {
    val hasRconConfigured: Boolean
        get() = rconPassword.isNotEmpty()
}

/**
 * Player list entry with additional information
 */
data class MinecraftPlayerInfo(
    val name: String,
    val uuid: String,
    val isOnline: Boolean = true,
    val ping: Long = 0,
    val gameMode: String? = null,
    val dimension: String? = null
)

/**
 * World information
 */
data class MinecraftWorldInfo(
    val name: String,
    val seed: String? = null,
    val time: Long = 0,
    val weather: Weather = Weather.CLEAR,
    val difficulty: Difficulty = Difficulty.NORMAL,
    val spawnPoint: Coordinates? = null
)

/**
 * Weather types
 */
enum class Weather {
    CLEAR,
    RAIN,
    THUNDER
}

/**
 * Difficulty levels
 */
enum class Difficulty(val level: Int) {
    PEACEFUL(0),
    EASY(1),
    NORMAL(2),
    HARD(3);

    companion object {
        fun fromLevel(level: Int): Difficulty {
            return entries.find { it.level == level } ?: NORMAL
        }
    }
}

/**
 * Game modes
 */
enum class GameMode(val mode: String) {
    SURVIVAL("survival"),
    CREATIVE("creative"),
    ADVENTURE("adventure"),
    SPECTATOR("spectator");

    companion object {
        fun fromString(mode: String): GameMode? {
            return entries.find { it.mode.equals(mode, ignoreCase = true) }
        }
    }
}

/**
 * Coordinates in the Minecraft world
 */
data class Coordinates(
    val x: Int,
    val y: Int,
    val z: Int
) {
    override fun toString(): String = "$x $y $z"

    companion object {
        fun parse(coords: String): Coordinates? {
            val parts = coords.trim().split(" ")
            if (parts.size != 3) return null
            return try {
                Coordinates(
                    x = parts[0].toInt(),
                    y = parts[1].toInt(),
                    z = parts[2].toInt()
                )
            } catch (e: NumberFormatException) {
                null
            }
        }
    }
}

/**
 * RCON authentication result
 */
data class RconAuthResult(
    val isAuthenticated: Boolean,
    val errorMessage: String? = null
) {
    companion object {
        fun success(): RconAuthResult = RconAuthResult(isAuthenticated = true)
        fun failure(message: String): RconAuthResult = RconAuthResult(isAuthenticated = false, errorMessage = message)
    }
}

/**
 * RCON connection state
 */
enum class RconConnectionState {
    DISCONNECTED,
    CONNECTING,
    AUTHENTICATING,
    AUTHENTICATED,
    ERROR
}

/**
 * Server ping result
 */
data class ServerPingResult(
    val isSuccess: Boolean,
    val status: MinecraftServerStatus? = null,
    val latency: Long = 0,
    val errorMessage: String? = null
) {
    companion object {
        fun success(status: MinecraftServerStatus, latency: Long): ServerPingResult {
            return ServerPingResult(isSuccess = true, status = status, latency = latency)
        }

        fun failure(errorMessage: String): ServerPingResult {
            return ServerPingResult(isSuccess = false, errorMessage = errorMessage)
        }
    }
}
