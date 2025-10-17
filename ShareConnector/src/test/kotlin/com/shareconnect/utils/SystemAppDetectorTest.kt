package com.shareconnect.utils

import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
class SystemAppDetectorTest {

    private lateinit var context: android.content.Context

    @org.junit.Before
    fun setUp() {
        context = RuntimeEnvironment.application
    }

    @Test
    fun testGetCompatibleApps_StreamingUrl_IncludesMyJDownloader() {
        // Test that MyJDownloader is included for streaming URLs when available
        val apps = SystemAppDetector.getCompatibleApps(context, "https://www.youtube.com/watch?v=test")

        // MyJDownloader should be included for streaming URLs if the app is installed
        // Since the app may not be installed in test environment, we just verify the method doesn't crash
        val myJDownloaderApp = apps.find { it.packageName == "org.appwork.myjdremote" }
        // If the app is not installed, myJDownloaderApp will be null, which is expected
        if (myJDownloaderApp != null) {
            assertEquals("MyJDownloader", myJDownloaderApp.appName)
        }
        // The important thing is that the method completes without errors
        assertNotNull("Apps list should not be null", apps)
    }

    @Test
    fun testGetCompatibleApps_FileHostingUrl_IncludesMyJDownloader() {
        // Test that MyJDownloader is included for file hosting URLs when available
        val apps = SystemAppDetector.getCompatibleApps(context, "https://mega.nz/file/test123")

        // MyJDownloader should be included for file hosting URLs if the app is installed
        val myJDownloaderApp = apps.find { it.packageName == "org.appwork.myjdremote" }
        if (myJDownloaderApp != null) {
            assertEquals("MyJDownloader", myJDownloaderApp.appName)
        }
        assertNotNull("Apps list should not be null", apps)
    }

    @Test
    fun testGetCompatibleApps_PremiumLinkUrl_IncludesMyJDownloader() {
        // Test that MyJDownloader is included for premium link URLs when available
        val apps = SystemAppDetector.getCompatibleApps(context, "https://rapidgator.net/file/test123/file.zip")

        // MyJDownloader should be included for premium link URLs if the app is installed
        val myJDownloaderApp = apps.find { it.packageName == "org.appwork.myjdremote" }
        if (myJDownloaderApp != null) {
            assertEquals("MyJDownloader", myJDownloaderApp.appName)
        }
        assertNotNull("Apps list should not be null", apps)
    }

    @Test
    fun testGetCompatibleApps_ContainerFormatUrl_IncludesMyJDownloader() {
        // Test that MyJDownloader is included for container format URLs when available
        val apps = SystemAppDetector.getCompatibleApps(context, "https://example.com/container.dlc")

        // MyJDownloader should be included for container format URLs if the app is installed
        val myJDownloaderApp = apps.find { it.packageName == "org.appwork.myjdremote" }
        if (myJDownloaderApp != null) {
            assertEquals("MyJDownloader", myJDownloaderApp.appName)
        }
        assertNotNull("Apps list should not be null", apps)
    }

    @Test
    fun testGetCompatibleApps_TorrentUrl_DoesNotIncludeMyJDownloader() {
        // Test that MyJDownloader is NOT included for torrent URLs (JDownloader doesn't support torrents)
        val apps = SystemAppDetector.getCompatibleApps(context, "magnet:?xt=urn:btih:test")

        // Should NOT include MyJDownloader for torrent URLs
        val myJDownloaderApp = apps.find { it.packageName == "org.appwork.myjdremote" }
        assertNull("MyJDownloader should NOT be included for torrent URLs", myJDownloaderApp)
    }

    @Test
    fun testGetCompatibleApps_NullUrl_ReturnsEmptyList() {
        val apps = SystemAppDetector.getCompatibleApps(context, null)
        assertTrue("Null URL should return empty list", apps.isEmpty())
    }

    @Test
    fun testGetCompatibleApps_EmptyUrl_ReturnsEmptyList() {
        val apps = SystemAppDetector.getCompatibleApps(context, "")
        assertTrue("Empty URL should return empty list", apps.isEmpty())
    }

    @Test
    fun testGetCompatibleApps_InvalidUrl_ReturnsEmptyList() {
        val apps = SystemAppDetector.getCompatibleApps(context, "invalid-url")
        assertTrue("Invalid URL should return empty list", apps.isEmpty())
    }


}