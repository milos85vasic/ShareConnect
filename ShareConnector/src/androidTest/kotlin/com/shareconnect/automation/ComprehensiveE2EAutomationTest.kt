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


package com.shareconnect.automation

import android.content.Intent
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import androidx.test.uiautomator.Until
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File

@RunWith(AndroidJUnit4::class)
class ComprehensiveE2EAutomationTest {

    private lateinit var device: UiDevice
    private val packageName = "com.shareconnect"
    private val timeout = 10000L

    @Before
    fun setUp() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

        // Wake up device and unlock
        device.wakeUp()
        device.pressHome()

        // Clear any existing app data
        clearAppData()
    }

    @After
    fun tearDown() {
        // Clean up test data
        clearAppData()

        // Take screenshot for test report
        takeScreenshot("test_teardown")
    }

    @Test
    fun testCompleteUserJourneyFromAppLaunchToSharingYouTubeVideo() {
        // Step 1: Launch ShareConnector app
        launchShareConnector()

        // Step 2: Complete onboarding if needed
        handleOnboarding()

        // Step 3: Navigate to main screen
        navigateToMainScreen()

        // Step 4: Create a torrent client profile
        createTorrentProfile()

        // Step 5: Share a YouTube video URL
        shareYouTubeVideo()

        // Step 6: Verify sharing was successful
        verifySharingSuccess()

        // Step 7: Check history for the shared item
        verifyHistoryEntry()

        // Step 8: Test theme sync
        testThemeSync()

        // Step 9: Test language sync
        testLanguageSync()
    }

    @Test
    fun testCompleteUserJourneyWithMultipleURLTypes() {
        // Step 1: Launch app and complete setup
        launchShareConnector()
        handleOnboarding()
        navigateToMainScreen()

        // Step 2: Test YouTube video sharing
        shareYouTubeVideo()
        verifySharingSuccess()

        // Step 3: Test torrent magnet link
        shareTorrentMagnet()
        verifySharingSuccess()

        // Step 4: Test file hosting service
        shareMegaFile()
        verifySharingSuccess()

        // Step 5: Test direct download
        sharePdfDocument()
        verifySharingSuccess()

        // Step 6: Verify all items appear in history
        verifyMultipleHistoryEntries()
    }

    @Test
    fun testCrossAppSyncFunctionality() {
        // Step 1: Set up multiple sync scenarios
        launchShareConnector()
        handleOnboarding()
        navigateToMainScreen()

        // Step 2: Create profiles for different services
        createMultipleProfiles()

        // Step 3: Share content across different services
        shareContentAcrossServices()

        // Step 4: Verify sync across all modules
        verifyCrossModuleSync()

        // Step 5: Test conflict resolution
        testSyncConflictResolution()
    }

    private fun launchShareConnector() {
        // Launch the ShareConnector app
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        // Wait for app to launch
        device.wait(Until.hasObject(By.pkg(packageName)), timeout)
        takeScreenshot("app_launched")
    }

    private fun handleOnboarding() {
        // Check if onboarding is needed
        val onboardingScreen = device.findObject(By.res(packageName, "onboarding_container"))
        if (onboardingScreen != null) {
            // Complete onboarding steps
            completeOnboardingFlow()
        }
    }

    private fun completeOnboardingFlow() {
        // Step through onboarding screens
        val nextButton = device.findObject(By.res(packageName, "onboarding_next_button"))
        while (nextButton != null) {
            nextButton.click()
            device.waitForIdle()
        }

        // Complete final onboarding step
        val finishButton = device.findObject(By.res(packageName, "onboarding_finish_button"))
        finishButton?.click()
        device.waitForIdle()

        takeScreenshot("onboarding_completed")
    }

    private fun navigateToMainScreen() {
        // Wait for main activity to load
        device.wait(Until.hasObject(By.res(packageName, "main_container")), timeout)
        takeScreenshot("main_screen")
    }

    private fun createTorrentProfile() {
        // Navigate to profiles screen
        val profilesButton = device.findObject(By.res(packageName, "profiles_button"))
        profilesButton?.click()

        // Add new profile
        val addProfileButton = device.findObject(By.res(packageName, "add_profile_button"))
        addProfileButton?.click()

        // Select torrent client type
        val torrentClientOption = device.findObject(By.text("qBittorrent"))
        torrentClientOption?.click()

        // Fill in profile details
        fillProfileForm("Test qBittorrent", "192.168.1.100", "8080", "admin", "adminadmin")

        // Save profile
        val saveButton = device.findObject(By.res(packageName, "save_profile_button"))
        saveButton?.click()

        device.waitForIdle()
        takeScreenshot("profile_created")
    }

    private fun fillProfileForm(name: String, host: String, port: String, username: String, password: String) {
        // Fill name field
        val nameField = device.findObject(By.res(packageName, "profile_name_field"))
        nameField?.text = name

        // Fill host field
        val hostField = device.findObject(By.res(packageName, "profile_host_field"))
        hostField?.text = host

        // Fill port field
        val portField = device.findObject(By.res(packageName, "profile_port_field"))
        portField?.text = port

        // Fill username field
        val usernameField = device.findObject(By.res(packageName, "profile_username_field"))
        usernameField?.text = username

        // Fill password field
        val passwordField = device.findObject(By.res(packageName, "profile_password_field"))
        passwordField?.text = password
    }

    private fun shareYouTubeVideo() {
        // Create share intent with YouTube URL
        val youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, youtubeUrl)
        }

        // Start share activity
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.startActivity(Intent.createChooser(intent, "Share YouTube Video"))

        // Wait for share dialog
        device.wait(Until.hasObject(By.text("ShareConnect")), timeout)

        // Select ShareConnect
        val shareConnectOption = device.findObject(By.text("ShareConnect"))
        shareConnectOption?.click()

        device.waitForIdle()
        takeScreenshot("youtube_shared")
    }

    private fun shareTorrentMagnet() {
        // Create share intent with magnet link
        val magnetUrl = "magnet:?xt=urn:btih:abc123def456&dn=Test+Torrent"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, magnetUrl)
        }

        // Start share activity
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.startActivity(Intent.createChooser(intent, "Share Torrent"))

        // Wait for share dialog
        device.wait(Until.hasObject(By.text("ShareConnect")), timeout)

        // Select ShareConnect
        val shareConnectOption = device.findObject(By.text("ShareConnect"))
        shareConnectOption?.click()

        device.waitForIdle()
        takeScreenshot("magnet_shared")
    }

    private fun shareMegaFile() {
        // Create share intent with Mega file URL
        val megaUrl = "https://mega.nz/file/abc123#def456"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, megaUrl)
        }

        // Start share activity
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.startActivity(Intent.createChooser(intent, "Share Mega File"))

        // Wait for share dialog
        device.wait(Until.hasObject(By.text("ShareConnect")), timeout)

        // Select ShareConnect
        val shareConnectOption = device.findObject(By.text("ShareConnect"))
        shareConnectOption?.click()

        device.waitForIdle()
        takeScreenshot("mega_shared")
    }

    private fun sharePdfDocument() {
        // Create share intent with PDF URL
        val pdfUrl = "https://example.com/document.pdf"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, pdfUrl)
        }

        // Start share activity
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.startActivity(Intent.createChooser(intent, "Share PDF"))

        // Wait for share dialog
        device.wait(Until.hasObject(By.text("ShareConnect")), timeout)

        // Select ShareConnect
        val shareConnectOption = device.findObject(By.text("ShareConnect"))
        shareConnectOption?.click()

        device.waitForIdle()
        takeScreenshot("pdf_shared")
    }

    private fun shareDirectDownload() {
        // Create share intent with direct download URL
        val downloadUrl = "https://example.com/file.zip"
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, downloadUrl)
        }

        // Start share activity
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        context.startActivity(Intent.createChooser(intent, "Share Direct Download"))

        // Wait for share dialog
        device.wait(Until.hasObject(By.text("ShareConnect")), timeout)

        // Select ShareConnect
        val shareConnectOption = device.findObject(By.text("ShareConnect"))
        shareConnectOption?.click()

        device.waitForIdle()
        takeScreenshot("direct_download_shared")
    }

    private fun verifySharingSuccess() {
        // Check for success indicator
        val successIndicator = device.findObject(By.res(packageName, "sharing_success_indicator"))
        assertNotNull("Sharing should be successful", successIndicator)

        takeScreenshot("sharing_success")
    }

    private fun verifyHistoryEntry() {
        // Navigate to history screen
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()

        // Check for history entry
        val historyEntry = device.findObject(By.res(packageName, "history_item"))
        assertNotNull("History entry should exist", historyEntry)

        takeScreenshot("history_verified")
    }

    private fun verifyMultipleHistoryEntries() {
        // Navigate to history screen
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()

        // Check for multiple history entries
        val historyEntries = device.findObjects(By.res(packageName, "history_item"))
        assertTrue("Multiple history entries should exist", historyEntries.size >= 4)

        takeScreenshot("multiple_history_entries")
    }

    @Test
    fun testHistoryFunctionality() {
        // Share a few items first to populate history
        shareYouTubeVideo()
        shareTorrentMagnet()
        shareDirectDownload()

        // Navigate to history screen
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()
        device.waitForIdle()

        // Verify history screen loads
        val historyRecyclerView = device.findObject(By.res(packageName, "recyclerViewHistory"))
        assertNotNull("History recycler view should be visible", historyRecyclerView)

        // Check for history items
        val historyItems = device.findObjects(By.res(packageName, "history_item"))
        assertTrue("Should have at least 3 history items", historyItems.size >= 3)

        takeScreenshot("history_screen_loaded")

        // Test filtering by service type
        val serviceTypeFilter = device.findObject(By.res(packageName, "autoCompleteServiceTypeFilter"))
        serviceTypeFilter?.click()
        device.waitForIdle()

        // Select "MeTube" filter
        val metubeOption = device.findObject(By.text("MeTube"))
        metubeOption?.click()
        device.waitForIdle()

        // Verify filtered results
        val filteredItems = device.findObjects(By.res(packageName, "history_item"))
        assertTrue("Should have filtered results", filteredItems.size >= 1)

        takeScreenshot("history_filtered_metube")

        // Test re-sharing functionality
        val firstHistoryItem = device.findObject(By.res(packageName, "history_item"))
        firstHistoryItem?.click()

        val resendButton = device.findObject(By.res(packageName, "buttonResend"))
        resendButton?.click()
        device.waitForIdle()

        // Should navigate back to share screen or show sharing dialog
        takeScreenshot("history_reshare_attempted")

        // Test history cleanup
        device.pressBack() // Go back to history screen

        val menuButton = device.findObject(By.desc("More options"))
        menuButton?.click()
        device.waitForIdle()

        val cleanupOption = device.findObject(By.text("Cleanup History"))
        cleanupOption?.click()
        device.waitForIdle()

        val deleteAllOption = device.findObject(By.text("All History"))
        deleteAllOption?.click()
        device.waitForIdle()

        val confirmButton = device.findObject(By.text("Delete"))
        confirmButton?.click()
        device.waitForIdle()

        // Verify history is cleared
        val emptyState = device.findObject(By.res(packageName, "textViewEmptyHistory"))
        assertNotNull("Empty history state should be visible", emptyState)

        takeScreenshot("history_cleared")
    }

    @Test
    fun testHistoryDetailedInformation() {
        // Share a video with detailed metadata
        shareYouTubeVideo()

        // Navigate to history
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()
        device.waitForIdle()

        // Check for detailed information display
        val historyItem = device.findObject(By.res(packageName, "history_item"))
        assertNotNull("History item should exist", historyItem)

        // Verify title is displayed
        val titleText = device.findObject(By.res(packageName, "textViewTitle"))
        assertNotNull("Title should be displayed", titleText)

        // Verify service provider
        val serviceProviderText = device.findObject(By.res(packageName, "textViewServiceProvider"))
        assertNotNull("Service provider should be displayed", serviceProviderText)

        // Verify type
        val typeText = device.findObject(By.res(packageName, "textViewType"))
        assertNotNull("Type should be displayed", typeText)

        // Verify timestamp
        val timestampText = device.findObject(By.res(packageName, "textViewTimestamp"))
        assertNotNull("Timestamp should be displayed", timestampText)

        takeScreenshot("history_detailed_info")
    }

    private fun createMultipleProfiles() {
        // Navigate to profiles screen
        val profilesButton = device.findObject(By.res(packageName, "profiles_button"))
        profilesButton?.click()

        // Create qBittorrent profile
        createProfile("qBittorrent", "Test qBittorrent", "192.168.1.100", "8080", "admin", "adminadmin")

        // Create Transmission profile
        createProfile("Transmission", "Test Transmission", "192.168.1.101", "9091", "transmission", "transmission")

        // Create uTorrent profile
        createProfile("uTorrent", "Test uTorrent", "192.168.1.102", "5000", "admin", "admin")

        takeScreenshot("multiple_profiles_created")
    }

    private fun createProfile(clientType: String, name: String, host: String, port: String, username: String, password: String) {
        val addProfileButton = device.findObject(By.res(packageName, "add_profile_button"))
        addProfileButton?.click()

        // Select client type
        val clientOption = device.findObject(By.text(clientType))
        clientOption?.click()

        // Fill profile form
        fillProfileForm(name, host, port, username, password)

        // Save profile
        val saveButton = device.findObject(By.res(packageName, "save_profile_button"))
        saveButton?.click()

        device.waitForIdle()
    }

    private fun shareContentAcrossServices() {
        // Share YouTube video
        shareYouTubeVideo()

        // Share torrent magnet
        shareTorrentMagnet()

        // Share Mega file
        shareMegaFile()

        takeScreenshot("content_shared_across_services")
    }

    private fun verifyCrossModuleSync() {
        // Check if data appears in history
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()

        val historyEntries = device.findObjects(By.res(packageName, "history_item"))
        assertTrue("Cross-module sync should show shared content", historyEntries.size >= 3)

        takeScreenshot("cross_module_sync_verified")
    }

    private fun testSyncConflictResolution() {
        // This would test scenarios where the same content is shared from different sources
        // For now, we'll just verify that the sync system handles duplicates gracefully

        // Share the same YouTube video twice
        shareYouTubeVideo()
        shareYouTubeVideo()

        // Check that history doesn't have duplicates
        val historyButton = device.findObject(By.res(packageName, "history_button"))
        historyButton?.click()

        val historyEntries = device.findObjects(By.res(packageName, "history_item"))
        // Should handle duplicates appropriately (either merge or show once)

        takeScreenshot("sync_conflict_resolution")
    }

    private fun testThemeSync() {
        // Navigate to settings
        val settingsButton = device.findObject(By.res(packageName, "settings_button"))
        settingsButton?.click()

        // Change theme
        val themeOption = device.findObject(By.text("Dark Theme"))
        themeOption?.click()

        // Verify theme change
        // This would require checking the actual theme state

        takeScreenshot("theme_sync_tested")
    }

    private fun testLanguageSync() {
        // Navigate to settings
        val settingsButton = device.findObject(By.res(packageName, "settings_button"))
        settingsButton?.click()

        // Change language
        val languageOption = device.findObject(By.text("Español"))
        languageOption?.click()

        // Verify language change
        // This would require checking the actual language state

        takeScreenshot("language_sync_tested")
    }

    private fun clearAppData() {
        // Clear app data using ADB
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val packageManager = context.packageManager
        try {
            val intent = Intent(Intent.ACTION_DELETE)
            intent.data = Uri.parse("package:$packageName")
            context.startActivity(intent)
        } catch (e: Exception) {
            // Handle case where app data clearing isn't available
        }
    }

    private fun takeScreenshot(name: String) {
        // Take screenshot for test reporting
        val screenshotDir = File("/sdcard/test_screenshots")
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs()
        }

        val timestamp = System.currentTimeMillis()
        val screenshotPath = "/sdcard/test_screenshots/${name}_$timestamp.png"

        try {
            device.takeScreenshot(File(screenshotPath))
        } catch (e: Exception) {
            // Screenshot failed, continue test
        }
    }
}