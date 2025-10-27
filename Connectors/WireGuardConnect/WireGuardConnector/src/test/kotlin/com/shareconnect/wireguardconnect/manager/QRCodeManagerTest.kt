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

import com.shareconnect.wireguardconnect.data.models.WireGuardConfig
import com.shareconnect.wireguardconnect.data.models.WireGuardInterface
import com.shareconnect.wireguardconnect.data.models.WireGuardPeer
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class QRCodeManagerTest {

    private lateinit var qrCodeManager: QRCodeManager

    private val testConfig = WireGuardConfig(
        name = "test",
        interface = WireGuardInterface(
            privateKey = "QIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA=",
            address = listOf("10.0.0.2/24"),
            dns = listOf("1.1.1.1")
        ),
        peers = listOf(
            WireGuardPeer(
                publicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
                endpoint = "vpn.example.com:51820",
                allowedIps = listOf("0.0.0.0/0")
            )
        )
    )

    @Before
    fun setup() {
        qrCodeManager = QRCodeManager.getInstance()
    }

    @Test
    fun `test generate QR code from config`() = runTest {
        val result = qrCodeManager.generateQRCode(testConfig)

        assertTrue(result is WireGuardResult.Success)
        val bitmap = (result as WireGuardResult.Success).data
        assertNotNull(bitmap)
        assertTrue(bitmap.width > 0)
        assertTrue(bitmap.height > 0)
    }

    @Test
    fun `test generate QR code with custom size`() = runTest {
        val customSize = 1024
        val result = qrCodeManager.generateQRCode(testConfig, customSize)

        assertTrue(result is WireGuardResult.Success)
        val bitmap = (result as WireGuardResult.Success).data
        assertNotNull(bitmap)
        assertTrue(bitmap.width == customSize)
        assertTrue(bitmap.height == customSize)
    }

    @Test
    fun `test generate QR code from string`() = runTest {
        val configString = testConfig.toConfigString()
        val result = qrCodeManager.generateQRCodeFromString(configString)

        assertTrue(result is WireGuardResult.Success)
        val bitmap = (result as WireGuardResult.Success).data
        assertNotNull(bitmap)
    }

    @Test
    fun `test parse QR code content`() = runTest {
        val configString = testConfig.toConfigString()
        val result = qrCodeManager.parseQRCodeContent("scanned", configString)

        assertTrue(result is WireGuardResult.Success)
        val config = (result as WireGuardResult.Success).data
        assertNotNull(config)
        assertTrue(config.name == "scanned")
    }

    @Test
    fun `test parse invalid QR code content`() = runTest {
        val invalidContent = "This is not a WireGuard config"
        val result = qrCodeManager.parseQRCodeContent("invalid", invalidContent)

        assertTrue(result is WireGuardResult.Error)
    }

    @Test
    fun `test estimate QR code size`() {
        val size = qrCodeManager.estimateQRCodeSize(testConfig)
        assertTrue(size > 0)
        assertTrue(size < qrCodeManager.getMaxQRCodeSize())
    }

    @Test
    fun `test can encode as QR code`() {
        val canEncode = qrCodeManager.canEncodeAsQRCode(testConfig)
        assertTrue(canEncode)
    }

    @Test
    fun `test cannot encode large config as QR code`() {
        val largeConfig = WireGuardConfig(
            name = "large-config",
            interface = WireGuardInterface(
                privateKey = "Q".repeat(1000),
                address = listOf("10.0.0.2/24")
            ),
            peers = List(100) { index ->
                WireGuardPeer(
                    publicKey = "R".repeat(100),
                    endpoint = "peer$index.example.com:51820",
                    allowedIps = listOf("10.0.$index.0/24")
                )
            }
        )

        val canEncode = qrCodeManager.canEncodeAsQRCode(largeConfig)
        assertTrue(!canEncode)
    }

    @Test
    fun `test get max QR code size`() {
        val maxSize = qrCodeManager.getMaxQRCodeSize()
        assertTrue(maxSize == 2953)
    }

    @Test
    fun `test create shareable QR code`() = runTest {
        val result = qrCodeManager.createShareableQRCode(testConfig)

        assertTrue(result is WireGuardResult.Success)
        val bitmap = (result as WireGuardResult.Success).data
        assertNotNull(bitmap)
    }

    @Test
    fun `test create shareable QR code without label`() = runTest {
        val result = qrCodeManager.createShareableQRCode(testConfig, includeLabel = false)

        assertTrue(result is WireGuardResult.Success)
        val bitmap = (result as WireGuardResult.Success).data
        assertNotNull(bitmap)
    }

    @Test
    fun `test generate QR code from minimal config`() = runTest {
        val minimalConfig = WireGuardConfig(
            name = "minimal",
            interface = WireGuardInterface(
                privateKey = "Q",
                address = listOf("10.0.0.2/24")
            ),
            peers = listOf(
                WireGuardPeer(
                    publicKey = "R",
                    allowedIps = listOf("0.0.0.0/0")
                )
            )
        )

        val result = qrCodeManager.generateQRCode(minimalConfig)
        assertTrue(result is WireGuardResult.Success)
    }

    @Test
    fun `test round trip QR code generation and parsing`() = runTest {
        // Generate QR code
        val qrResult = qrCodeManager.generateQRCode(testConfig)
        assertTrue(qrResult is WireGuardResult.Success)

        // Parse the config string
        val configString = testConfig.toConfigString()
        val parseResult = qrCodeManager.parseQRCodeContent("roundtrip", configString)
        assertTrue(parseResult is WireGuardResult.Success)

        val parsedConfig = (parseResult as WireGuardResult.Success).data
        assertTrue(parsedConfig.interface.privateKey == testConfig.interface.privateKey)
    }
}
