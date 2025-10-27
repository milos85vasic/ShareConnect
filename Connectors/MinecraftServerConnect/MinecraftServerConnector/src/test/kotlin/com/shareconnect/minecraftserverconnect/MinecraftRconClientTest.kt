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


package com.shareconnect.minecraftserverconnect

import com.shareconnect.minecraftserverconnect.data.models.*
import com.shareconnect.minecraftserverconnect.data.rcon.MinecraftRconClient
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*
import java.net.Socket

/**
 * Unit tests for MinecraftRconClient
 * Tests RCON protocol implementation and command execution
 */
class MinecraftRconClientTest {

    private lateinit var rconClient: MinecraftRconClient
    private val testHost = "localhost"
    private val testPort = 25575
    private val testPassword = "test_password"

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        rconClient = MinecraftRconClient(testHost, testPort, testPassword)
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `test RCON client initialization`() {
        assertNotNull("RCON client should be initialized", rconClient)
        assertEquals("Connection state should be DISCONNECTED initially",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())
    }

    @Test
    fun `test isConnected returns false when not connected`() {
        assertFalse("Should not be connected initially", rconClient.isConnected())
    }

    @Test
    fun `test connection state is DISCONNECTED initially`() {
        assertEquals("Initial state should be DISCONNECTED",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())
    }

    @Test
    fun `test parsePlayerListFromResponse with empty response`() {
        val response = "There are no players online"
        val client = MinecraftRconClient(testHost, testPort, testPassword)
        // Use reflection to test private method
        val method = MinecraftRconClient::class.java.getDeclaredMethod(
            "parsePlayerListFromResponse", String::class.java
        )
        method.isAccessible = true
        val result = method.invoke(client, response) as List<*>
        assertTrue("Player list should be empty", result.isEmpty())
    }

    @Test
    fun `test parsePlayerListFromResponse with valid response`() {
        val response = "There are 3 of a max of 20 players online: player1, player2, player3"
        val client = MinecraftRconClient(testHost, testPort, testPassword)
        val method = MinecraftRconClient::class.java.getDeclaredMethod(
            "parsePlayerListFromResponse", String::class.java
        )
        method.isAccessible = true
        val result = method.invoke(client, response) as List<*>
        assertEquals("Should have 3 players", 3, result.size)
        assertTrue("Should contain player1", result.contains("player1"))
        assertTrue("Should contain player2", result.contains("player2"))
        assertTrue("Should contain player3", result.contains("player3"))
    }

    @Test
    fun `test buildPacket creates proper packet structure`() {
        val client = MinecraftRconClient(testHost, testPort, testPassword)
        val method = MinecraftRconClient::class.java.getDeclaredMethod(
            "buildPacket", Int::class.java, RconPacketType::class.java, String::class.java
        )
        method.isAccessible = true
        val packet = method.invoke(client, 1, RconPacketType.AUTH, "password") as ByteArray

        assertTrue("Packet should have minimum size", packet.size >= 14)
        // Verify packet structure (size + id + type + payload + padding)
        assertTrue("Packet should contain payload", packet.isNotEmpty())
    }

    @Test
    fun `test RconPacketType enum values`() {
        assertEquals("AUTH type should be 3", 3, RconPacketType.AUTH.id)
        assertEquals("AUTH_RESPONSE type should be 2", 2, RconPacketType.AUTH_RESPONSE.id)
        assertEquals("EXECCOMMAND type should be 2", 2, RconPacketType.EXECCOMMAND.id)
        assertEquals("RESPONSE_VALUE type should be 0", 0, RconPacketType.RESPONSE_VALUE.id)
    }

    @Test
    fun `test RconPacketType fromId conversion`() {
        assertEquals("Should convert ID 3 to AUTH",
            RconPacketType.AUTH, RconPacketType.fromId(3))
        assertEquals("Should convert ID 2 to AUTH_RESPONSE",
            RconPacketType.AUTH_RESPONSE, RconPacketType.fromId(2))
        assertEquals("Should convert ID 0 to RESPONSE_VALUE",
            RconPacketType.RESPONSE_VALUE, RconPacketType.fromId(0))
        assertNull("Should return null for invalid ID", RconPacketType.fromId(99))
    }

    @Test
    fun `test MinecraftRconResponse success creation`() {
        val response = MinecraftRconResponse.success(1, "Success message")
        assertTrue("Response should be successful", response.isSuccess)
        assertEquals("Request ID should match", 1, response.requestId)
        assertEquals("Payload should match", "Success message", response.payload)
        assertNull("Error message should be null", response.errorMessage)
    }

    @Test
    fun `test MinecraftRconResponse error creation`() {
        val response = MinecraftRconResponse.error(1, "Error occurred")
        assertFalse("Response should not be successful", response.isSuccess)
        assertEquals("Request ID should match", 1, response.requestId)
        assertEquals("Payload should be empty", "", response.payload)
        assertEquals("Error message should match", "Error occurred", response.errorMessage)
    }

    @Test
    fun `test RconAuthResult success creation`() {
        val result = RconAuthResult.success()
        assertTrue("Auth should be successful", result.isAuthenticated)
        assertNull("Error message should be null", result.errorMessage)
    }

    @Test
    fun `test RconAuthResult failure creation`() {
        val result = RconAuthResult.failure("Invalid password")
        assertFalse("Auth should not be successful", result.isAuthenticated)
        assertEquals("Error message should match", "Invalid password", result.errorMessage)
    }

    @Test
    fun `test Coordinates toString formatting`() {
        val coords = Coordinates(100, 64, -200)
        assertEquals("Coordinates should format correctly", "100 64 -200", coords.toString())
    }

    @Test
    fun `test Coordinates parse valid input`() {
        val coords = Coordinates.parse("100 64 -200")
        assertNotNull("Should parse valid coordinates", coords)
        assertEquals("X coordinate should match", 100, coords?.x)
        assertEquals("Y coordinate should match", 64, coords?.y)
        assertEquals("Z coordinate should match", -200, coords?.z)
    }

    @Test
    fun `test Coordinates parse invalid input`() {
        assertNull("Should return null for invalid format", Coordinates.parse("invalid"))
        assertNull("Should return null for wrong number of parts", Coordinates.parse("100 64"))
        assertNull("Should return null for non-numeric values", Coordinates.parse("abc def ghi"))
    }

    @Test
    fun `test GameMode enum values`() {
        assertEquals("Survival mode string", "survival", GameMode.SURVIVAL.mode)
        assertEquals("Creative mode string", "creative", GameMode.CREATIVE.mode)
        assertEquals("Adventure mode string", "adventure", GameMode.ADVENTURE.mode)
        assertEquals("Spectator mode string", "spectator", GameMode.SPECTATOR.mode)
    }

    @Test
    fun `test GameMode fromString conversion`() {
        assertEquals("Should parse survival", GameMode.SURVIVAL, GameMode.fromString("survival"))
        assertEquals("Should parse creative", GameMode.CREATIVE, GameMode.fromString("creative"))
        assertEquals("Should parse case insensitive", GameMode.ADVENTURE, GameMode.fromString("ADVENTURE"))
        assertNull("Should return null for invalid mode", GameMode.fromString("invalid"))
    }

    @Test
    fun `test Difficulty enum levels`() {
        assertEquals("Peaceful level", 0, Difficulty.PEACEFUL.level)
        assertEquals("Easy level", 1, Difficulty.EASY.level)
        assertEquals("Normal level", 2, Difficulty.NORMAL.level)
        assertEquals("Hard level", 3, Difficulty.HARD.level)
    }

    @Test
    fun `test Difficulty fromLevel conversion`() {
        assertEquals("Should convert 0 to PEACEFUL", Difficulty.PEACEFUL, Difficulty.fromLevel(0))
        assertEquals("Should convert 1 to EASY", Difficulty.EASY, Difficulty.fromLevel(1))
        assertEquals("Should convert 2 to NORMAL", Difficulty.NORMAL, Difficulty.fromLevel(2))
        assertEquals("Should convert 3 to HARD", Difficulty.HARD, Difficulty.fromLevel(3))
        assertEquals("Should default to NORMAL for invalid level", Difficulty.NORMAL, Difficulty.fromLevel(99))
    }
}
