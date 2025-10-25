package com.shareconnect.wireguardconnect.automation

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.Until
import com.shareconnect.wireguardconnect.manager.QRCodeManager
import com.shareconnect.wireguardconnect.manager.WireGuardConfigManager
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class WireGuardConnectAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var configManager: WireGuardConfigManager
    private lateinit var qrCodeManager: QRCodeManager

    companion object {
        private const val LAUNCH_TIMEOUT = 5000L
        private const val PACKAGE_NAME = "com.shareconnect.wireguardconnect.debug"
    }

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        configManager = WireGuardConfigManager.getInstance(context)
        qrCodeManager = QRCodeManager.getInstance()

        // Start from home screen
        device.pressHome()

        // Wait for launcher
        val launcherPackage = device.launcherPackageName
        assertNotNull(launcherPackage)
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT)
    }

    @Test
    fun testLaunchApp() {
        // Launch app
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        assertNotNull(intent)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for app to launch
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)

        // Verify app is running
        val appElement = device.findObject(By.pkg(PACKAGE_NAME))
        assertNotNull(appElement)
    }

    @Test
    fun testConfigManagerInitialization() = runTest {
        // Test that config manager initializes correctly
        val listResult = configManager.listConfigs()
        assertNotNull(listResult)
    }

    @Test
    fun testQRCodeManagerInitialization() {
        // Test that QR code manager initializes correctly
        val maxSize = qrCodeManager.getMaxQRCodeSize()
        assertTrue(maxSize > 0)
    }

    @Test
    fun testCreateConfigFlow() = runTest {
        val config = configManager.createConfig(
            name = "automation-test",
            address = listOf("10.0.0.2/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        assertNotNull(config)
        assertTrue(config.name == "automation-test")
    }

    @Test
    fun testSaveConfigFlow() = runTest {
        val config = configManager.createConfig(
            name = "save-automation-test",
            address = listOf("10.0.0.2/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val saveResult = configManager.saveConfig(config)
        assertTrue(saveResult is com.shareconnect.wireguardconnect.data.models.WireGuardResult.Success)

        // Clean up
        configManager.deleteConfig(config.id)
    }

    @Test
    fun testQRCodeGenerationFlow() = runTest {
        val config = configManager.createConfig(
            name = "qr-automation-test",
            address = listOf("10.0.0.2/24"),
            peerPublicKey = "RIAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAB=",
            peerEndpoint = "vpn.example.com:51820"
        )

        val qrResult = qrCodeManager.generateQRCode(config)
        assertTrue(qrResult is com.shareconnect.wireguardconnect.data.models.WireGuardResult.Success)
    }

    @Test
    fun testKeyPairGenerationFlow() {
        val keyPair = configManager.generateKeyPair()
        assertNotNull(keyPair)
        assertTrue(keyPair.privateKey.isNotBlank())
        assertTrue(keyPair.publicKey.isNotBlank())
    }

    @Test
    fun testAppResumeFromBackground() {
        // Launch app
        val intent = context.packageManager.getLaunchIntentForPackage(PACKAGE_NAME)
        assertNotNull(intent)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)

        // Send app to background
        device.pressHome()
        Thread.sleep(1000)

        // Resume app
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(PACKAGE_NAME).depth(0)), LAUNCH_TIMEOUT)

        // Verify app is running
        val appElement = device.findObject(By.pkg(PACKAGE_NAME))
        assertNotNull(appElement)
    }
}
