package com.shareconnect.wireguardconnect.integration

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import com.shareconnect.wireguardconnect.manager.QRCodeManager
import com.shareconnect.wireguardconnect.manager.WireGuardConfigManager
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class WireGuardConfigIntegrationTest {

    private lateinit var context: Context
    private lateinit var configManager: WireGuardConfigManager
    private lateinit var qrCodeManager: QRCodeManager

    private val sampleConfig = """
        [Interface]
        PrivateKey = QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
        Address = 10.0.0.2/24, fd00::2/64
        DNS = 1.1.1.1, 1.0.0.1
        MTU = 1420

        [Peer]
        PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
        PresharedKey = SIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC=
        AllowedIPs = 0.0.0.0/0, ::/0
        Endpoint = vpn.example.com:51820
        PersistentKeepalive = 25
    """.trimIndent()

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        configManager = WireGuardConfigManager.getInstance(context)
        qrCodeManager = QRCodeManager.getInstance()
    }

    @Test
    fun testCreateSaveLoadConfig() = runTest {
        // Create config
        val config = configManager.createConfig(
            name = "integration-test",
            address = listOf("10.0.0.2/24"),
            dns = listOf("1.1.1.1"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        // Save config
        val saveResult = configManager.saveConfig(config)
        assertTrue(saveResult is WireGuardResult.Success)

        // Load configs
        val listResult = configManager.listConfigs()
        assertTrue(listResult is WireGuardResult.Success)
        val configs = (listResult as WireGuardResult.Success).data
        assertTrue(configs.any { it.name == "integration-test" })

        // Clean up
        configManager.deleteConfig(config.id)
    }

    @Test
    fun testConfigParsingAndExport() = runTest {
        // Parse config
        val parseResult = configManager.parseConfig("parsed", sampleConfig)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        // Export config
        val exported = configManager.exportConfig(config)
        assertTrue(exported.contains("[Interface]"))
        assertTrue(exported.contains("[Peer]"))

        // Re-parse exported config
        val reParseResult = configManager.parseConfig("reparsed", exported)
        assertTrue(reParseResult is WireGuardResult.Success)
    }

    @Test
    fun testQRCodeGenerationAndParsing() = runTest {
        // Parse config
        val parseResult = configManager.parseConfig("qr-test", sampleConfig)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        // Generate QR code
        val qrResult = qrCodeManager.generateQRCode(config)
        assertTrue(qrResult is WireGuardResult.Success)
        val bitmap = (qrResult as WireGuardResult.Success).data
        assertNotNull(bitmap)

        // Parse QR code content
        val configString = config.toConfigString()
        val parseQrResult = qrCodeManager.parseQRCodeContent("scanned", configString)
        assertTrue(parseQrResult is WireGuardResult.Success)
    }

    @Test
    fun testKeyPairGeneration() {
        val keyPair1 = configManager.generateKeyPair()
        val keyPair2 = configManager.generateKeyPair()

        // Keys should be different
        assertTrue(keyPair1.privateKey != keyPair2.privateKey)
        assertTrue(keyPair1.publicKey != keyPair2.publicKey)
    }

    @Test
    fun testConfigWithMultipleAddresses() = runTest {
        val config = configManager.createConfig(
            name = "multi-address",
            address = listOf("10.0.0.2/24", "fd00::2/64"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val exported = configManager.exportConfig(config)
        val parseResult = configManager.parseConfig("reparsed", exported)
        assertTrue(parseResult is WireGuardResult.Success)
    }

    @Test
    fun testConfigWithNoDNS() = runTest {
        val config = configManager.createConfig(
            name = "no-dns",
            address = listOf("10.0.0.2/24"),
            dns = emptyList(),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val saveResult = configManager.saveConfig(config)
        assertTrue(saveResult is WireGuardResult.Success)

        configManager.deleteConfig(config.id)
    }

    @Test
    fun testConfigUpdateFlow() = runTest {
        // Create and save initial config
        val config1 = configManager.createConfig(
            name = "update-test",
            address = listOf("10.0.0.2/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn1.example.com:51820"
        )
        configManager.saveConfig(config1)

        // Delete old config
        val deleteResult = configManager.deleteConfig(config1.id)
        assertTrue(deleteResult is WireGuardResult.Success)

        // Create updated config
        val config2 = configManager.createConfig(
            name = "update-test",
            address = listOf("10.0.0.3/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn2.example.com:51820"
        )
        configManager.saveConfig(config2)

        // Clean up
        configManager.deleteConfig(config2.id)
    }

    @Test
    fun testQRCodeSizeEstimation() = runTest {
        val parseResult = configManager.parseConfig("size-test", sampleConfig)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        val estimatedSize = qrCodeManager.estimateQRCodeSize(config)
        val canEncode = qrCodeManager.canEncodeAsQRCode(config)

        assertTrue(estimatedSize > 0)
        assertTrue(canEncode)
        assertTrue(estimatedSize <= qrCodeManager.getMaxQRCodeSize())
    }

    @Test
    fun testShareableQRCodeCreation() = runTest {
        val parseResult = configManager.parseConfig("shareable-test", sampleConfig)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        val qrResult = qrCodeManager.createShareableQRCode(config)
        assertTrue(qrResult is WireGuardResult.Success)
        val bitmap = (qrResult as WireGuardResult.Success).data
        assertNotNull(bitmap)
        assertTrue(bitmap.width > 0)
    }

    @Test
    fun testConfigImportExportRoundTrip() = runTest {
        // Import config
        val importResult = configManager.importConfig("imported", sampleConfig)
        assertTrue(importResult is WireGuardResult.Success)
        val config = (importResult as WireGuardResult.Success).data

        // Export config
        val exported = configManager.exportConfig(config)

        // Re-import
        val reImportResult = configManager.importConfig("reimported", exported)
        assertTrue(reImportResult is WireGuardResult.Success)
        val reImported = (reImportResult as WireGuardResult.Success).data

        // Verify data integrity
        assertEquals(config.interface.privateKey, reImported.interface.privateKey)
        assertEquals(config.peers.size, reImported.peers.size)
    }

    @Test
    fun testMultipleConfigsManagement() = runTest {
        val configs = listOf(
            configManager.createConfig(
                name = "config-1",
                address = listOf("10.0.1.2/24"),
                peerPublicKey = "R1AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                peerEndpoint = "vpn1.example.com:51820"
            ),
            configManager.createConfig(
                name = "config-2",
                address = listOf("10.0.2.2/24"),
                peerPublicKey = "R2AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                peerEndpoint = "vpn2.example.com:51820"
            ),
            configManager.createConfig(
                name = "config-3",
                address = listOf("10.0.3.2/24"),
                peerPublicKey = "R3AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
                peerEndpoint = "vpn3.example.com:51820"
            )
        )

        // Save all configs
        configs.forEach { configManager.saveConfig(it) }

        // List configs
        val listResult = configManager.listConfigs()
        assertTrue(listResult is WireGuardResult.Success)
        val savedConfigs = (listResult as WireGuardResult.Success).data
        assertTrue(savedConfigs.size >= 3)

        // Clean up
        configs.forEach { configManager.deleteConfig(it.id) }
    }

    @Test
    fun testConfigWithIPv6Only() = runTest {
        val config = configManager.createConfig(
            name = "ipv6-only",
            address = listOf("fd00::2/64"),
            dns = listOf("2606:4700:4700::1111"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "[2001:db8::1]:51820",
            peerAllowedIps = listOf("::/0")
        )

        val saveResult = configManager.saveConfig(config)
        assertTrue(saveResult is WireGuardResult.Success)

        configManager.deleteConfig(config.id)
    }

    @Test
    fun testConfigWithCustomMTU() = runTest {
        val config = configManager.createConfig(
            name = "custom-mtu",
            address = listOf("10.0.0.2/24"),
            mtu = 1280,
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val exported = configManager.exportConfig(config)
        assertTrue(exported.contains("MTU = 1280"))
    }

    @Test
    fun testConfigWithoutPersistentKeepalive() = runTest {
        val config = configManager.createConfig(
            name = "no-keepalive",
            address = listOf("10.0.0.2/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820",
            persistentKeepalive = null
        )

        val exported = configManager.exportConfig(config)
        assertTrue(!exported.contains("PersistentKeepalive"))
    }

    @Test
    fun testQRCodeDifferentSizes() = runTest {
        val parseResult = configManager.parseConfig("size-test", sampleConfig)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        val sizes = listOf(256, 512, 1024, 2048)
        sizes.forEach { size ->
            val qrResult = qrCodeManager.generateQRCode(config, size)
            assertTrue(qrResult is WireGuardResult.Success)
            val bitmap = (qrResult as WireGuardResult.Success).data
            assertEquals(size, bitmap.width)
            assertEquals(size, bitmap.height)
        }
    }

    @Test
    fun testConfigWithAllOptionalFields() = runTest {
        val configWithAllFields = """
            [Interface]
            PrivateKey = QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=
            Address = 10.0.0.2/24
            DNS = 1.1.1.1
            MTU = 1420
            ListenPort = 51820
            Table = auto
            PreUp = /bin/true
            PostUp = /bin/true
            PreDown = /bin/true
            PostDown = /bin/true

            [Peer]
            PublicKey = RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=
            PresharedKey = SIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAC=
            AllowedIPs = 0.0.0.0/0
            Endpoint = vpn.example.com:51820
            PersistentKeepalive = 25
        """.trimIndent()

        val parseResult = configManager.parseConfig("full-config", configWithAllFields)
        assertTrue(parseResult is WireGuardResult.Success)
        val config = (parseResult as WireGuardResult.Success).data

        assertNotNull(config.interface.mtu)
        assertNotNull(config.interface.listenPort)
        assertNotNull(config.interface.table)
        assertNotNull(config.interface.preUp)
        assertNotNull(config.interface.postUp)
        assertNotNull(config.interface.preDown)
        assertNotNull(config.interface.postDown)
    }
}
