package com.shareconnect.minecraftserverconnect

import com.shareconnect.minecraftserverconnect.data.api.MinecraftServerPinger
import com.shareconnect.minecraftserverconnect.data.models.*
import io.mockk.*
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.Assert.*

/**
 * Unit tests for MinecraftServerPinger
 * Tests server ping protocol and status parsing
 */
class MinecraftServerPingerTest {

    private lateinit var pinger: MinecraftServerPinger

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxUnitFun = true)
        pinger = MinecraftServerPinger()
    }

    @After
    fun teardown() {
        unmockkAll()
    }

    @Test
    fun `test pinger initialization`() {
        assertNotNull("Pinger should be initialized", pinger)
    }

    @Test
    fun `test MinecraftServerStatus properties`() {
        val version = MinecraftVersion("1.20.1", 763)
        val players = MinecraftPlayers(max = 20, online = 5)
        val description = MinecraftDescription("Test Server")

        val status = MinecraftServerStatus(
            version = version,
            players = players,
            description = description
        )

        assertTrue("Server should be online", status.isOnline)
        assertEquals("Player count should match", 5, status.playerCount)
        assertEquals("Max players should match", 20, status.maxPlayers)
        assertEquals("Version should match", "1.20.1", status.serverVersion)
        assertEquals("MOTD should match", "Test Server", status.motd)
    }

    @Test
    fun `test MinecraftDescription with plain text`() {
        val description = MinecraftDescription(text = "Simple MOTD")
        assertEquals("Plain text should match", "Simple MOTD", description.plainText)
    }

    @Test
    fun `test MinecraftDescription with extra components`() {
        val extra = listOf(
            MinecraftDescriptionComponent(text = "Welcome ", color = "green"),
            MinecraftDescriptionComponent(text = "to server", color = "blue", bold = true)
        )
        val description = MinecraftDescription(text = "", extra = extra)
        assertEquals("Plain text should concatenate components", "Welcome to server", description.plainText)
    }

    @Test
    fun `test ServerPingResult success creation`() {
        val version = MinecraftVersion("1.20.1", 763)
        val players = MinecraftPlayers(max = 20, online = 5)
        val description = MinecraftDescription("Test Server")
        val status = MinecraftServerStatus(version, players, description)

        val result = ServerPingResult.success(status, 50L)

        assertTrue("Result should be successful", result.isSuccess)
        assertNotNull("Status should not be null", result.status)
        assertEquals("Latency should match", 50L, result.latency)
        assertNull("Error message should be null", result.errorMessage)
    }

    @Test
    fun `test ServerPingResult failure creation`() {
        val result = ServerPingResult.failure("Connection timeout")

        assertFalse("Result should not be successful", result.isSuccess)
        assertNull("Status should be null", result.status)
        assertEquals("Error message should match", "Connection timeout", result.errorMessage)
    }

    @Test
    fun `test MinecraftPlayer data structure`() {
        val player = MinecraftPlayer(name = "TestPlayer", id = "uuid-1234")
        assertEquals("Player name should match", "TestPlayer", player.name)
        assertEquals("Player ID should match", "uuid-1234", player.id)
    }

    @Test
    fun `test MinecraftPlayers with sample list`() {
        val samplePlayers = listOf(
            MinecraftPlayer("Player1", "uuid1"),
            MinecraftPlayer("Player2", "uuid2")
        )
        val players = MinecraftPlayers(max = 20, online = 2, sample = samplePlayers)

        assertEquals("Max players should be 20", 20, players.max)
        assertEquals("Online players should be 2", 2, players.online)
        assertNotNull("Sample should not be null", players.sample)
        assertEquals("Sample size should be 2", 2, players.sample?.size)
    }

    @Test
    fun `test MinecraftVersion data structure`() {
        val version = MinecraftVersion("1.20.1", 763)
        assertEquals("Version name should match", "1.20.1", version.name)
        assertEquals("Protocol version should match", 763, version.protocol)
    }

    @Test
    fun `test MinecraftModInfo structure`() {
        val mods = listOf(
            MinecraftMod("examplemod", "1.0.0"),
            MinecraftMod("anothermod", "2.5.3")
        )
        val modInfo = MinecraftModInfo("FML", mods)

        assertEquals("Mod type should be FML", "FML", modInfo.type)
        assertEquals("Should have 2 mods", 2, modInfo.modList?.size)
    }

    @Test
    fun `test MinecraftMod data structure`() {
        val mod = MinecraftMod("testmod", "1.2.3")
        assertEquals("Mod ID should match", "testmod", mod.modId)
        assertEquals("Mod version should match", "1.2.3", mod.version)
    }

    @Test
    fun `test MinecraftDescriptionComponent with styling`() {
        val component = MinecraftDescriptionComponent(
            text = "Styled Text",
            color = "red",
            bold = true,
            italic = false
        )

        assertEquals("Text should match", "Styled Text", component.text)
        assertEquals("Color should be red", "red", component.color)
        assertEquals("Should be bold", true, component.bold)
        assertEquals("Should not be italic", false, component.italic)
    }

    @Test
    fun `test MinecraftServerStatus with favicon`() {
        val version = MinecraftVersion("1.20.1", 763)
        val players = MinecraftPlayers(max = 20, online = 5)
        val description = MinecraftDescription("Test Server")
        val favicon = "data:image/png;base64,..."

        val status = MinecraftServerStatus(
            version = version,
            players = players,
            description = description,
            favicon = favicon
        )

        assertNotNull("Favicon should not be null", status.favicon)
        assertTrue("Favicon should start with data:", status.favicon?.startsWith("data:") == true)
    }

    @Test
    fun `test MinecraftServerStatus with mod info`() {
        val version = MinecraftVersion("1.20.1", 763)
        val players = MinecraftPlayers(max = 20, online = 5)
        val description = MinecraftDescription("Modded Server")
        val mods = listOf(MinecraftMod("forge", "47.1.0"))
        val modInfo = MinecraftModInfo("FML", mods)

        val status = MinecraftServerStatus(
            version = version,
            players = players,
            description = description,
            modInfo = modInfo
        )

        assertNotNull("Mod info should not be null", status.modInfo)
        assertEquals("Mod type should be FML", "FML", status.modInfo?.type)
    }

    @Test
    fun `test empty MinecraftDescription`() {
        val description = MinecraftDescription(text = "")
        assertEquals("Empty text should return empty string", "", description.plainText)
    }

    @Test
    fun `test MinecraftPlayers without sample`() {
        val players = MinecraftPlayers(max = 20, online = 0)
        assertNull("Sample should be null", players.sample)
        assertEquals("Online count should be 0", 0, players.online)
    }
}
