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


package com.shareconnect.minecraftserverconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.minecraftserverconnect.data.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import java.io.DataInputStream
import java.io.DataOutputStream
import java.net.InetSocketAddress
import java.net.Socket
import java.nio.ByteBuffer

/**
 * Minecraft server pinger using the Server List Ping protocol
 * https://wiki.vg/Server_List_Ping
 */
class MinecraftServerPinger {

    companion object {
        private const val TAG = "MinecraftServerPinger"
        private const val PROTOCOL_VERSION = 47 // Minecraft 1.8+
        private const val DEFAULT_TIMEOUT = 5000L // 5 seconds
        private const val PACKET_HANDSHAKE = 0x00
        private const val PACKET_STATUS_REQUEST = 0x00
        private const val PACKET_PING = 0x01
        private const val STATE_STATUS = 1
    }

    /**
     * Ping a Minecraft server and get its status
     */
    suspend fun ping(host: String, port: Int = 25565): Result<ServerPingResult> = withContext(Dispatchers.IO) {
        var socket: Socket? = null
        val startTime = System.currentTimeMillis()

        try {
            withTimeout(DEFAULT_TIMEOUT) {
                socket = Socket()
                socket.connect(InetSocketAddress(host, port), DEFAULT_TIMEOUT.toInt())
                socket.soTimeout = DEFAULT_TIMEOUT.toInt()

                val output = DataOutputStream(socket.getOutputStream())
                val input = DataInputStream(socket.getInputStream())

                // Send handshake packet
                sendHandshake(output, host, port)

                // Send status request
                sendStatusRequest(output)

                // Read status response
                val statusJson = readStatusResponse(input)

                // Calculate latency
                val latency = System.currentTimeMillis() - startTime

                // Parse JSON response
                val status = parseServerStatus(statusJson)

                // Optional: Send ping packet for more accurate latency
                // sendPing(output, input)

                Log.d(TAG, "Successfully pinged server $host:$port, latency: ${latency}ms")
                Result.success(ServerPingResult.success(status, latency))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to ping server $host:$port", e)
            Result.success(ServerPingResult.failure(e.message ?: "Failed to ping server"))
        } finally {
            try {
                socket?.close()
            } catch (e: Exception) {
                Log.w(TAG, "Error closing socket", e)
            }
        }
    }

    /**
     * Send handshake packet to server
     */
    private fun sendHandshake(output: DataOutputStream, host: String, port: Int) {
        val handshakeData = ByteBuffer.allocate(256)

        // Packet ID
        writeVarInt(handshakeData, PACKET_HANDSHAKE)

        // Protocol version
        writeVarInt(handshakeData, PROTOCOL_VERSION)

        // Server address
        val hostBytes = host.toByteArray(Charsets.UTF_8)
        writeVarInt(handshakeData, hostBytes.size)
        handshakeData.put(hostBytes)

        // Server port
        handshakeData.putShort(port.toShort())

        // Next state (1 = status)
        writeVarInt(handshakeData, STATE_STATUS)

        // Write packet length and data
        val packetData = ByteArray(handshakeData.position())
        handshakeData.flip()
        handshakeData.get(packetData)

        writeVarInt(output, packetData.size)
        output.write(packetData)
        output.flush()
    }

    /**
     * Send status request packet
     */
    private fun sendStatusRequest(output: DataOutputStream) {
        // Packet length (1 byte for packet ID)
        writeVarInt(output, 1)

        // Packet ID
        writeVarInt(output, PACKET_STATUS_REQUEST)

        output.flush()
    }

    /**
     * Read status response from server
     */
    private fun readStatusResponse(input: DataInputStream): String {
        // Read packet length
        val packetLength = readVarInt(input)

        // Read packet ID
        val packetId = readVarInt(input)

        if (packetId != PACKET_STATUS_REQUEST) {
            throw Exception("Invalid packet ID: expected $PACKET_STATUS_REQUEST, got $packetId")
        }

        // Read JSON response length
        val jsonLength = readVarInt(input)

        // Read JSON data
        val jsonBytes = ByteArray(jsonLength)
        input.readFully(jsonBytes)

        return String(jsonBytes, Charsets.UTF_8)
    }

    /**
     * Send ping packet and measure latency
     */
    private fun sendPing(output: DataOutputStream, input: DataInputStream): Long {
        val startTime = System.currentTimeMillis()
        val payload = startTime

        // Write ping packet
        writeVarInt(output, 9) // Packet length
        writeVarInt(output, PACKET_PING) // Packet ID
        output.writeLong(payload) // Payload
        output.flush()

        // Read pong response
        val pongLength = readVarInt(input)
        val pongId = readVarInt(input)
        val pongPayload = input.readLong()

        if (pongId != PACKET_PING) {
            throw Exception("Invalid pong packet ID")
        }

        if (pongPayload != payload) {
            throw Exception("Pong payload mismatch")
        }

        return System.currentTimeMillis() - startTime
    }

    /**
     * Parse server status JSON
     */
    private fun parseServerStatus(json: String): MinecraftServerStatus {
        try {
            val gson = Gson()
            return gson.fromJson(json, MinecraftServerStatus::class.java)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse server status JSON", e)
            throw Exception("Failed to parse server status: ${e.message}")
        }
    }

    /**
     * Write a VarInt to output stream
     */
    private fun writeVarInt(output: DataOutputStream, value: Int) {
        var v = value
        while (v and 0x7F.inv() != 0) {
            output.writeByte((v and 0x7F) or 0x80)
            v = v ushr 7
        }
        output.writeByte(v and 0x7F)
    }

    /**
     * Write a VarInt to ByteBuffer
     */
    private fun writeVarInt(buffer: ByteBuffer, value: Int) {
        var v = value
        while (v and 0x7F.inv() != 0) {
            buffer.put(((v and 0x7F) or 0x80).toByte())
            v = v ushr 7
        }
        buffer.put((v and 0x7F).toByte())
    }

    /**
     * Read a VarInt from input stream
     */
    private fun readVarInt(input: DataInputStream): Int {
        var value = 0
        var position = 0
        var currentByte: Byte

        while (true) {
            currentByte = input.readByte()
            value = value or ((currentByte.toInt() and 0x7F) shl position)

            if ((currentByte.toInt() and 0x80) == 0) {
                break
            }

            position += 7

            if (position >= 32) {
                throw Exception("VarInt is too big")
            }
        }

        return value
    }

    /**
     * Test if a server is online (quick check)
     */
    suspend fun isServerOnline(host: String, port: Int = 25565): Boolean = withContext(Dispatchers.IO) {
        val result = ping(host, port)
        result.isSuccess && result.getOrNull()?.isSuccess == true
    }

    /**
     * Get player count from server
     */
    suspend fun getPlayerCount(host: String, port: Int = 25565): Result<Pair<Int, Int>> = withContext(Dispatchers.IO) {
        try {
            val result = ping(host, port)
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Ping failed"))
            }

            val pingResult = result.getOrThrow()
            if (!pingResult.isSuccess || pingResult.status == null) {
                return@withContext Result.failure(Exception(pingResult.errorMessage ?: "No status available"))
            }

            val status = pingResult.status
            Result.success(Pair(status.players.online, status.players.max))
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get player count", e)
            Result.failure(e)
        }
    }

    /**
     * Get server version
     */
    suspend fun getServerVersion(host: String, port: Int = 25565): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = ping(host, port)
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Ping failed"))
            }

            val pingResult = result.getOrThrow()
            if (!pingResult.isSuccess || pingResult.status == null) {
                return@withContext Result.failure(Exception(pingResult.errorMessage ?: "No status available"))
            }

            Result.success(pingResult.status.version.name)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get server version", e)
            Result.failure(e)
        }
    }

    /**
     * Get server MOTD (Message of the Day)
     */
    suspend fun getServerMotd(host: String, port: Int = 25565): Result<String> = withContext(Dispatchers.IO) {
        try {
            val result = ping(host, port)
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Ping failed"))
            }

            val pingResult = result.getOrThrow()
            if (!pingResult.isSuccess || pingResult.status == null) {
                return@withContext Result.failure(Exception(pingResult.errorMessage ?: "No status available"))
            }

            Result.success(pingResult.status.description.plainText)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get server MOTD", e)
            Result.failure(e)
        }
    }

    /**
     * Get online players list (if available in server response)
     */
    suspend fun getOnlinePlayers(host: String, port: Int = 25565): Result<List<MinecraftPlayer>> = withContext(Dispatchers.IO) {
        try {
            val result = ping(host, port)
            if (result.isFailure) {
                return@withContext Result.failure(result.exceptionOrNull() ?: Exception("Ping failed"))
            }

            val pingResult = result.getOrThrow()
            if (!pingResult.isSuccess || pingResult.status == null) {
                return@withContext Result.failure(Exception(pingResult.errorMessage ?: "No status available"))
            }

            val players = pingResult.status.players.sample ?: emptyList()
            Result.success(players)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get online players", e)
            Result.failure(e)
        }
    }
}
