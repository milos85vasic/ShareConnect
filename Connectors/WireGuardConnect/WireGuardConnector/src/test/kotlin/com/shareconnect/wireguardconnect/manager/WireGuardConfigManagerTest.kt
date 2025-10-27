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


package com.shareconnect.wireguardconnect.manager

import android.content.Context
import com.shareconnect.wireguardconnect.data.models.WireGuardConfig
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class WireGuardConfigManagerTest {

    private lateinit var context: Context
    private lateinit var configManager: WireGuardConfigManager

    private val sampleConfig = """
        [Interface]
        PrivateKey = QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
        Address = 10.0.0.2/24
        DNS = 1.1.1.1, 1.0.0.1
        MTU = 1420

        [Peer]
        PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
        AllowedIPs = 0.0.0.0/0, ::/0
        Endpoint = vpn.example.com:51820
        PersistentKeepalive = 25
    """.trimIndent()

    @Before
    fun setup() {
        context = RuntimeEnvironment.getApplication()
        configManager = WireGuardConfigManager.getInstance(context)
    }

    @Test
    fun `test parse valid config`() = runTest {
        val result = configManager.parseConfig("test", sampleConfig)

        assertTrue(result is WireGuardResult.Success)
        val config = (result as WireGuardResult.Success).data
        assertEquals("test", config.name)
        assertEquals("QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", config.interface.privateKey)
        assertEquals(listOf("10.0.0.2/24"), config.interface.address)
        assertEquals(1, config.peers.size)
    }

    @Test
    fun `test parse config with multiple peers`() = runTest {
        val multiPeerConfig = """
            [Interface]
            PrivateKey = QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
            Address = 10.0.0.2/24

            [Peer]
            PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
            AllowedIPs = 10.0.1.0/24
            Endpoint = peer1.example.com:51820

            [Peer]
            PublicKey = SIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC=
            AllowedIPs = 10.0.2.0/24
            Endpoint = peer2.example.com:51820
        """.trimIndent()

        val result = configManager.parseConfig("multi-peer", multiPeerConfig)

        assertTrue(result is WireGuardResult.Success)
        val config = (result as WireGuardResult.Success).data
        assertEquals(2, config.peers.size)
        assertEquals("RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=", config.peers[0].publicKey)
        assertEquals("SIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC=", config.peers[1].publicKey)
    }

    @Test
    fun `test parse config with comments`() = runTest {
        val configWithComments = """
            # This is a comment
            [Interface]
            # Private key for the interface
            PrivateKey = QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
            Address = 10.0.0.2/24
            # DNS servers
            DNS = 1.1.1.1

            # Peer configuration
            [Peer]
            PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
            AllowedIPs = 0.0.0.0/0
        """.trimIndent()

        val result = configManager.parseConfig("commented", configWithComments)

        assertTrue(result is WireGuardResult.Success)
        val config = (result as WireGuardResult.Success).data
        assertNotNull(config)
    }

    @Test
    fun `test parse invalid config missing interface`() = runTest {
        val invalidConfig = """
            [Peer]
            PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
        """.trimIndent()

        val result = configManager.parseConfig("invalid", invalidConfig)

        assertTrue(result is WireGuardResult.Error)
    }

    @Test
    fun `test create config`() {
        val config = configManager.createConfig(
            name = "test-config",
            address = listOf("10.0.0.2/24"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            dns = listOf("1.1.1.1", "1.0.0.1"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820",
            peerAllowedIps = listOf("0.0.0.0/0", "::/0")
        )

        assertEquals("test-config", config.name)
        assertEquals("QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=", config.interface.privateKey)
        assertEquals(listOf("10.0.0.2/24"), config.interface.address)
        assertEquals(1, config.peers.size)
    }

    @Test
    fun `test save and load config`() = runTest {
        val config = configManager.createConfig(
            name = "save-test",
            address = listOf("10.0.0.2/24"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val saveResult = configManager.saveConfig(config)
        assertTrue(saveResult is WireGuardResult.Success)

        val listResult = configManager.listConfigs()
        assertTrue(listResult is WireGuardResult.Success)
        val configs = (listResult as WireGuardResult.Success).data
        assertTrue(configs.any { it.name == "save-test" })
    }

    @Test
    fun `test generate key pair`() {
        val keyPair = configManager.generateKeyPair()

        assertNotNull(keyPair.privateKey)
        assertNotNull(keyPair.publicKey)
        assertTrue(keyPair.privateKey.isNotBlank())
        assertTrue(keyPair.publicKey.isNotBlank())
    }

    @Test
    fun `test export config`() {
        val config = configManager.createConfig(
            name = "export-test",
            address = listOf("10.0.0.2/24"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val exported = configManager.exportConfig(config)

        assertTrue(exported.contains("[Interface]"))
        assertTrue(exported.contains("[Peer]"))
        assertTrue(exported.contains("PrivateKey"))
        assertTrue(exported.contains("PublicKey"))
    }

    @Test
    fun `test import config`() = runTest {
        val result = configManager.importConfig("imported", sampleConfig)

        assertTrue(result is WireGuardResult.Success)
        val config = (result as WireGuardResult.Success).data
        assertEquals("imported", config.name)
    }

    @Test
    fun `test delete config`() = runTest {
        val config = configManager.createConfig(
            name = "delete-test",
            address = listOf("10.0.0.2/24"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        configManager.saveConfig(config)
        val deleteResult = configManager.deleteConfig(config.id)

        assertTrue(deleteResult is WireGuardResult.Success)
    }

    @Test
    fun `test parse config file`() = runTest {
        val tempFile = File.createTempFile("wireguard", ".conf")
        tempFile.writeText(sampleConfig)

        val result = configManager.parseConfigFile(tempFile)

        assertTrue(result is WireGuardResult.Success)
        tempFile.delete()
    }

    @Test
    fun `test config validation ipv4`() = runTest {
        val config = configManager.createConfig(
            name = "ipv4-test",
            address = listOf("192.168.1.2/24"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "192.168.1.1:51820"
        )

        val result = configManager.parseConfig("ipv4-test", config.toConfigString())
        assertTrue(result is WireGuardResult.Success)
    }

    @Test
    fun `test config validation ipv6`() = runTest {
        val config = configManager.createConfig(
            name = "ipv6-test",
            address = listOf("fd00::2/64"),
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "[fd00::1]:51820"
        )

        val result = configManager.parseConfig("ipv6-test", config.toConfigString())
        assertTrue(result is WireGuardResult.Success)
    }

    @Test
    fun `test list empty configs`() = runTest {
        val result = configManager.listConfigs()

        assertTrue(result is WireGuardResult.Success)
        val configs = (result as WireGuardResult.Success).data
        assertNotNull(configs)
    }
}
