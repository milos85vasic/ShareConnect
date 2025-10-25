package com.shareconnect.minecraftserverconnect.data.rcon

import android.util.Log
import com.shareconnect.minecraftserverconnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.Socket
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.atomic.AtomicInteger
import kotlin.random.Random

/**
 * Minecraft RCON client implementing the Source RCON protocol
 * https://wiki.vg/RCON
 */
class MinecraftRconClient(
    private val host: String,
    private val port: Int,
    private val password: String
) {
    private var socket: Socket? = null
    private var outputStream: DataOutputStream? = null
    private var inputStream: DataInputStream? = null
    private var connectionState: RconConnectionState = RconConnectionState.DISCONNECTED
    private val requestIdCounter = AtomicInteger(Random.nextInt(1, 1000))

    companion object {
        private const val TAG = "MinecraftRconClient"
        private const val DEFAULT_TIMEOUT = 10000L // 10 seconds
        private const val PACKET_PADDING_LENGTH = 2 // Two null bytes
        private const val MAX_PACKET_SIZE = 4096
        private const val MIN_PACKET_SIZE = 14 // 4 (length) + 4 (id) + 4 (type) + 2 (padding)
    }

    /**
     * Connect to the Minecraft server RCON
     */
    suspend fun connect(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            if (isConnected()) {
                return@withContext Result.success(Unit)
            }

            connectionState = RconConnectionState.CONNECTING

            withTimeout(DEFAULT_TIMEOUT) {
                socket = Socket(host, port).apply {
                    soTimeout = DEFAULT_TIMEOUT.toInt()
                    tcpNoDelay = true
                }

                outputStream = DataOutputStream(socket!!.getOutputStream())
                inputStream = DataInputStream(socket!!.getInputStream())
            }

            Log.d(TAG, "Connected to RCON server at $host:$port")

            // Authenticate
            val authResult = authenticate()
            if (!authResult.isSuccess) {
                disconnect()
                return@withContext Result.failure(Exception(authResult.exceptionOrNull()?.message ?: "Authentication failed"))
            }

            connectionState = RconConnectionState.AUTHENTICATED
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to connect to RCON server", e)
            connectionState = RconConnectionState.ERROR
            disconnect()
            Result.failure(e)
        }
    }

    /**
     * Authenticate with the RCON server
     */
    private suspend fun authenticate(): Result<RconAuthResult> = withContext(Dispatchers.IO) {
        try {
            connectionState = RconConnectionState.AUTHENTICATING

            val requestId = getNextRequestId()
            val authPacket = buildPacket(requestId, RconPacketType.AUTH, password)

            outputStream?.write(authPacket)
            outputStream?.flush()

            // Read authentication response
            val response = readPacket()

            if (response.requestId == -1) {
                // Authentication failed
                Log.e(TAG, "RCON authentication failed")
                Result.success(RconAuthResult.failure("Invalid RCON password"))
            } else if (response.requestId == requestId) {
                // Authentication successful
                Log.d(TAG, "RCON authentication successful")
                Result.success(RconAuthResult.success())
            } else {
                Log.e(TAG, "Unexpected response ID: ${response.requestId}, expected: $requestId")
                Result.success(RconAuthResult.failure("Unexpected response from server"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Authentication error", e)
            Result.failure(e)
        }
    }

    /**
     * Execute a command on the Minecraft server
     */
    suspend fun executeCommand(command: String): Result<MinecraftRconResponse> = withContext(Dispatchers.IO) {
        try {
            if (!isConnected()) {
                return@withContext Result.failure(Exception("Not connected to RCON server"))
            }

            if (connectionState != RconConnectionState.AUTHENTICATED) {
                return@withContext Result.failure(Exception("Not authenticated"))
            }

            val requestId = getNextRequestId()
            val commandPacket = buildPacket(requestId, RconPacketType.EXECCOMMAND, command)

            outputStream?.write(commandPacket)
            outputStream?.flush()

            // Read response
            val response = readPacket()

            if (response.requestId != requestId) {
                Log.w(TAG, "Response ID mismatch: expected $requestId, got ${response.requestId}")
            }

            Log.d(TAG, "Command executed: $command, response: ${response.payload}")
            Result.success(MinecraftRconResponse.success(response.requestId, response.payload))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute command: $command", e)
            Result.failure(e)
        }
    }

    /**
     * Execute multiple commands sequentially
     */
    suspend fun executeCommands(commands: List<String>): Result<List<MinecraftRconResponse>> = withContext(Dispatchers.IO) {
        try {
            val responses = mutableListOf<MinecraftRconResponse>()

            for (command in commands) {
                val result = executeCommand(command)
                if (result.isSuccess) {
                    responses.add(result.getOrThrow())
                } else {
                    return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Command execution failed"))
                }
            }

            Result.success(responses)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to execute multiple commands", e)
            Result.failure(e)
        }
    }

    /**
     * Disconnect from the RCON server
     */
    suspend fun disconnect() = withContext(Dispatchers.IO) {
        try {
            outputStream?.close()
            inputStream?.close()
            socket?.close()
        } catch (e: Exception) {
            Log.e(TAG, "Error during disconnect", e)
        } finally {
            outputStream = null
            inputStream = null
            socket = null
            connectionState = RconConnectionState.DISCONNECTED
            Log.d(TAG, "Disconnected from RCON server")
        }
    }

    /**
     * Check if connected to the server
     */
    fun isConnected(): Boolean {
        return socket?.isConnected == true &&
               socket?.isClosed == false &&
               connectionState == RconConnectionState.AUTHENTICATED
    }

    /**
     * Get current connection state
     */
    fun getConnectionState(): RconConnectionState = connectionState

    /**
     * Build an RCON packet according to the protocol
     */
    private fun buildPacket(requestId: Int, type: RconPacketType, payload: String): ByteArray {
        val payloadBytes = payload.toByteArray(Charsets.UTF_8)
        val packetSize = 4 + 4 + payloadBytes.size + PACKET_PADDING_LENGTH // id + type + payload + padding

        val buffer = ByteBuffer.allocate(4 + packetSize).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putInt(packetSize)
        buffer.putInt(requestId)
        buffer.putInt(type.id)
        buffer.put(payloadBytes)
        buffer.put(0) // Null terminator
        buffer.put(0) // Null terminator

        return buffer.array()
    }

    /**
     * Read an RCON packet from the input stream
     */
    private fun readPacket(): MinecraftRconResponse {
        val input = inputStream ?: throw Exception("Input stream is null")

        // Read packet size
        val sizeBytes = ByteArray(4)
        input.readFully(sizeBytes)
        val size = ByteBuffer.wrap(sizeBytes).order(ByteOrder.LITTLE_ENDIAN).int

        if (size < MIN_PACKET_SIZE - 4 || size > MAX_PACKET_SIZE) {
            throw Exception("Invalid packet size: $size")
        }

        // Read packet data
        val packetData = ByteArray(size)
        input.readFully(packetData)

        val buffer = ByteBuffer.wrap(packetData).order(ByteOrder.LITTLE_ENDIAN)
        val requestId = buffer.int
        val type = buffer.int

        // Read payload (everything except the last 2 null bytes)
        val payloadSize = size - 10 // Subtract id (4) + type (4) + padding (2)
        val payloadBytes = ByteArray(payloadSize)
        buffer.get(payloadBytes)

        val payload = String(payloadBytes, Charsets.UTF_8)
        val packetType = RconPacketType.fromId(type) ?: RconPacketType.RESPONSE_VALUE

        return MinecraftRconResponse(
            requestId = requestId,
            type = packetType,
            payload = payload
        )
    }

    /**
     * Get next request ID
     */
    private fun getNextRequestId(): Int {
        return requestIdCounter.incrementAndGet()
    }

    /**
     * Test connection to the server
     */
    suspend fun testConnection(): Result<Boolean> = withContext(Dispatchers.IO) {
        try {
            val connectResult = connect()
            if (connectResult.isFailure) {
                return@withContext Result.failure(connectResult.exceptionOrNull() ?: Exception("Connection failed"))
            }

            // Try a simple command
            val testResult = executeCommand("list")
            disconnect()

            if (testResult.isSuccess) {
                Result.success(true)
            } else {
                Result.failure(testResult.exceptionOrNull() ?: Exception("Test command failed"))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Connection test failed", e)
            Result.failure(e)
        }
    }

    /**
     * Get list of online players
     */
    suspend fun getOnlinePlayers(): Result<List<String>> = withContext(Dispatchers.IO) {
        try {
            val result = executeCommand("list")
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Failed to get player list"))
            }

            val response = result.getOrThrow()
            val playerList = parsePlayerListFromResponse(response.payload)
            Result.success(playerList)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get online players", e)
            Result.failure(e)
        }
    }

    /**
     * Parse player list from RCON response
     * Format: "There are X of a max of Y players online: player1, player2, player3"
     */
    private fun parsePlayerListFromResponse(response: String): List<String> {
        if (response.contains("no players", ignoreCase = true)) {
            return emptyList()
        }

        val parts = response.split(":")
        if (parts.size < 2) {
            return emptyList()
        }

        val playerNames = parts[1].trim().split(",").map { it.trim() }.filter { it.isNotEmpty() }
        return playerNames
    }

    /**
     * Kick a player from the server
     */
    suspend fun kickPlayer(playerName: String, reason: String = "Kicked by admin"): Result<MinecraftRconResponse> {
        return executeCommand("kick $playerName $reason")
    }

    /**
     * Ban a player
     */
    suspend fun banPlayer(playerName: String, reason: String = "Banned by admin"): Result<MinecraftRconResponse> {
        return executeCommand("ban $playerName $reason")
    }

    /**
     * Pardon a player (remove ban)
     */
    suspend fun pardonPlayer(playerName: String): Result<MinecraftRconResponse> {
        return executeCommand("pardon $playerName")
    }

    /**
     * Set game mode for a player
     */
    suspend fun setGameMode(playerName: String, gameMode: GameMode): Result<MinecraftRconResponse> {
        return executeCommand("gamemode ${gameMode.mode} $playerName")
    }

    /**
     * Teleport a player
     */
    suspend fun teleportPlayer(playerName: String, coordinates: Coordinates): Result<MinecraftRconResponse> {
        return executeCommand("tp $playerName ${coordinates.x} ${coordinates.y} ${coordinates.z}")
    }

    /**
     * Give item to a player
     */
    suspend fun giveItem(playerName: String, itemId: String, amount: Int = 1): Result<MinecraftRconResponse> {
        return executeCommand("give $playerName $itemId $amount")
    }

    /**
     * Set server difficulty
     */
    suspend fun setDifficulty(difficulty: Difficulty): Result<MinecraftRconResponse> {
        return executeCommand("difficulty ${difficulty.name.lowercase()}")
    }

    /**
     * Set world time
     */
    suspend fun setTime(time: Long): Result<MinecraftRconResponse> {
        return executeCommand("time set $time")
    }

    /**
     * Set weather
     */
    suspend fun setWeather(weather: Weather, duration: Int = 300): Result<MinecraftRconResponse> {
        val weatherCommand = when (weather) {
            Weather.CLEAR -> "weather clear $duration"
            Weather.RAIN -> "weather rain $duration"
            Weather.THUNDER -> "weather thunder $duration"
        }
        return executeCommand(weatherCommand)
    }

    /**
     * Stop the server
     */
    suspend fun stopServer(): Result<MinecraftRconResponse> {
        return executeCommand("stop")
    }

    /**
     * Save the world
     */
    suspend fun saveWorld(): Result<MinecraftRconResponse> {
        return executeCommand("save-all")
    }

    /**
     * Broadcast a message to all players
     */
    suspend fun broadcastMessage(message: String): Result<MinecraftRconResponse> {
        return executeCommand("say $message")
    }

    /**
     * Op a player
     */
    suspend fun opPlayer(playerName: String): Result<MinecraftRconResponse> {
        return executeCommand("op $playerName")
    }

    /**
     * Deop a player
     */
    suspend fun deopPlayer(playerName: String): Result<MinecraftRconResponse> {
        return executeCommand("deop $playerName")
    }

    /**
     * Add player to whitelist
     */
    suspend fun whitelistAdd(playerName: String): Result<MinecraftRconResponse> {
        return executeCommand("whitelist add $playerName")
    }

    /**
     * Remove player from whitelist
     */
    suspend fun whitelistRemove(playerName: String): Result<MinecraftRconResponse> {
        return executeCommand("whitelist remove $playerName")
    }
}
