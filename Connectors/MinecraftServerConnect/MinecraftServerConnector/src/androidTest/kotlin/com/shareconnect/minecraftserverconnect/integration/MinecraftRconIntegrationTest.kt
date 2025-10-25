package com.shareconnect.minecraftserverconnect.integration

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.minecraftserverconnect.data.models.*
import com.shareconnect.minecraftserverconnect.data.rcon.MinecraftRconClient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.*

/**
 * Integration tests for MinecraftRconClient
 * Tests full RCON protocol flow and error handling
 */
@RunWith(AndroidJUnit4::class)
class MinecraftRconIntegrationTest {

    private lateinit var rconClient: MinecraftRconClient
    private val testHost = "127.0.0.1"
    private val testPort = 25575
    private val testPassword = "test_rcon_password"

    @Before
    fun setup() {
        rconClient = MinecraftRconClient(testHost, testPort, testPassword)
    }

    @After
    fun teardown() = runTest {
        rconClient.disconnect()
    }

    @Test
    fun testRconClientInitialState() {
        assertEquals("Initial state should be DISCONNECTED",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())
        assertFalse("Should not be connected initially", rconClient.isConnected())
    }

    @Test
    fun testDisconnectWhenNotConnected() = runTest {
        // Should not throw exception when disconnecting without being connected
        rconClient.disconnect()
        assertEquals("State should remain DISCONNECTED",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())
    }

    @Test
    fun testExecuteCommandWhenNotConnected() = runTest {
        val result = rconClient.executeCommand("list")
        assertTrue("Command should fail when not connected", result.isFailure)
    }

    @Test
    fun testMultipleDisconnectCalls() = runTest {
        rconClient.disconnect()
        rconClient.disconnect()
        rconClient.disconnect()
        assertEquals("State should be DISCONNECTED after multiple calls",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())
    }

    @Test
    fun testConnectionStateTransitions() {
        // Verify initial state
        assertEquals("Should start DISCONNECTED",
            RconConnectionState.DISCONNECTED, rconClient.getConnectionState())

        // State should only change after connect attempt
        assertFalse("Should not be connected", rconClient.isConnected())
    }

    @Test
    fun testRconResponseSuccessCreation() {
        val response = MinecraftRconResponse.success(123, "Command output")
        assertTrue("Response should be successful", response.isSuccess)
        assertEquals("Request ID should match", 123, response.requestId)
        assertEquals("Payload should match", "Command output", response.payload)
        assertEquals("Type should be RESPONSE_VALUE",
            RconPacketType.RESPONSE_VALUE, response.type)
    }

    @Test
    fun testRconResponseErrorCreation() {
        val response = MinecraftRconResponse.error(456, "Connection failed")
        assertFalse("Response should not be successful", response.isSuccess)
        assertEquals("Request ID should match", 456, response.requestId)
        assertEquals("Error message should match", "Connection failed", response.errorMessage)
        assertEquals("Payload should be empty", "", response.payload)
    }

    @Test
    fun testCoordinatesParsing() {
        val coords1 = Coordinates.parse("100 64 -200")
        assertNotNull("Should parse valid coordinates", coords1)
        assertEquals("X should be 100", 100, coords1?.x)
        assertEquals("Y should be 64", 64, coords1?.y)
        assertEquals("Z should be -200", -200, coords?.z)

        val coords2 = Coordinates.parse("invalid")
        assertNull("Should return null for invalid coordinates", coords2)

        val coords3 = Coordinates.parse("1 2")
        assertNull("Should return null for incomplete coordinates", coords3)
    }

    @Test
    fun testCoordinatesToString() {
        val coords = Coordinates(256, 128, -512)
        assertEquals("Coordinates string format", "256 128 -512", coords.toString())
    }

    @Test
    fun testGameModeConversion() {
        assertEquals("Should parse survival", GameMode.SURVIVAL, GameMode.fromString("survival"))
        assertEquals("Should parse creative", GameMode.CREATIVE, GameMode.fromString("creative"))
        assertEquals("Should parse adventure", GameMode.ADVENTURE, GameMode.fromString("adventure"))
        assertEquals("Should parse spectator", GameMode.SPECTATOR, GameMode.fromString("spectator"))
        assertEquals("Should be case insensitive",
            GameMode.CREATIVE, GameMode.fromString("CREATIVE"))
        assertNull("Should return null for invalid", GameMode.fromString("invalid"))
    }

    @Test
    fun testDifficultyConversion() {
        assertEquals("Level 0 is PEACEFUL", Difficulty.PEACEFUL, Difficulty.fromLevel(0))
        assertEquals("Level 1 is EASY", Difficulty.EASY, Difficulty.fromLevel(1))
        assertEquals("Level 2 is NORMAL", Difficulty.NORMAL, Difficulty.fromLevel(2))
        assertEquals("Level 3 is HARD", Difficulty.HARD, Difficulty.fromLevel(3))
        assertEquals("Invalid level defaults to NORMAL",
            Difficulty.NORMAL, Difficulty.fromLevel(99))
    }

    @Test
    fun testRconPacketTypes() {
        assertEquals("AUTH type is 3", 3, RconPacketType.AUTH.id)
        assertEquals("AUTH_RESPONSE type is 2", 2, RconPacketType.AUTH_RESPONSE.id)
        assertEquals("EXECCOMMAND type is 2", 2, RconPacketType.EXECCOMMAND.id)
        assertEquals("RESPONSE_VALUE type is 0", 0, RconPacketType.RESPONSE_VALUE.id)

        assertEquals("ID 3 converts to AUTH", RconPacketType.AUTH, RconPacketType.fromId(3))
        assertEquals("ID 0 converts to RESPONSE_VALUE",
            RconPacketType.RESPONSE_VALUE, RconPacketType.fromId(0))
        assertNull("Invalid ID returns null", RconPacketType.fromId(999))
    }

    @Test
    fun testRconAuthResultSuccess() {
        val result = RconAuthResult.success()
        assertTrue("Should be authenticated", result.isAuthenticated)
        assertNull("No error message", result.errorMessage)
    }

    @Test
    fun testRconAuthResultFailure() {
        val result = RconAuthResult.failure("Invalid password")
        assertFalse("Should not be authenticated", result.isAuthenticated)
        assertEquals("Error message should match", "Invalid password", result.errorMessage)
    }

    @Test
    fun testMinecraftServerInfoHasRcon() {
        val info1 = MinecraftServerInfo(
            host = "localhost",
            gamePort = 25565,
            rconPort = 25575,
            rconPassword = "password123"
        )
        assertTrue("Should have RCON configured", info1.hasRconConfigured)

        val info2 = MinecraftServerInfo(
            host = "localhost",
            gamePort = 25565,
            rconPort = 25575,
            rconPassword = ""
        )
        assertFalse("Should not have RCON configured", info2.hasRconConfigured)
    }

    @Test
    fun testWeatherEnumValues() {
        val weathers = Weather.entries
        assertEquals("Should have 3 weather types", 3, weathers.size)
        assertTrue("Should contain CLEAR", weathers.contains(Weather.CLEAR))
        assertTrue("Should contain RAIN", weathers.contains(Weather.RAIN))
        assertTrue("Should contain THUNDER", weathers.contains(Weather.THUNDER))
    }

    @Test
    fun testDifficultyLevels() {
        assertEquals("PEACEFUL is level 0", 0, Difficulty.PEACEFUL.level)
        assertEquals("EASY is level 1", 1, Difficulty.EASY.level)
        assertEquals("NORMAL is level 2", 2, Difficulty.NORMAL.level)
        assertEquals("HARD is level 3", 3, Difficulty.HARD.level)
    }
}
