package com.shareconnect.automation

import android.content.Context
import android.content.Intent
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.shareconnect.ShareActivity
import com.shareconnect.utils.UrlCompatibilityUtils
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class ComprehensiveSharingAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        device.waitForIdle(5000)
    }

    @Test
    fun testStreamingUrlSharing_Youtube() {
        val youtubeUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ"
        testUrlSharing(youtubeUrl, "STREAMING", listOf("MeTube", "YT-DLP", "JDownloader"))
    }

    @Test
    fun testStreamingUrlSharing_TikTok() {
        val tiktokUrl = "https://www.tiktok.com/@user/video/1234567890123456789"
        testUrlSharing(tiktokUrl, "STREAMING", listOf("MeTube", "YT-DLP", "JDownloader"))
    }

    @Test
    fun testStreamingUrlSharing_Vimeo() {
        val vimeoUrl = "https://vimeo.com/123456789"
        testUrlSharing(vimeoUrl, "STREAMING", listOf("MeTube", "YT-DLP", "JDownloader"))
    }

    @Test
    fun testStreamingUrlSharing_SoundCloud() {
        val soundcloudUrl = "https://soundcloud.com/artist/track-name"
        testUrlSharing(soundcloudUrl, "STREAMING", listOf("MeTube", "YT-DLP", "JDownloader"))
    }

    @Test
    fun testStreamingUrlSharing_Spotify() {
        val spotifyUrl = "https://open.spotify.com/track/4iV5W9uYEdYUVa79Axb7Rh"
        testUrlSharing(spotifyUrl, "STREAMING", listOf("MeTube", "YT-DLP", "JDownloader"))
    }

    @Test
    fun testFileHostingUrlSharing_MediaFire() {
        val mediafireUrl = "https://www.mediafire.com/file/abc123def456/file.zip"
        testUrlSharing(mediafireUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testFileHostingUrlSharing_Mega() {
        val megaUrl = "https://mega.nz/file/abc123#def456"
        testUrlSharing(megaUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testFileHostingUrlSharing_GoogleDrive() {
        val driveUrl = "https://drive.google.com/file/d/1abc123def456/view"
        testUrlSharing(driveUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testFileHostingUrlSharing_Dropbox() {
        val dropboxUrl = "https://www.dropbox.com/s/abc123/file.pdf"
        testUrlSharing(dropboxUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testPremiumLinkSharing_Rapidgator() {
        val rapidgatorUrl = "https://rapidgator.net/file/abc123/file.zip.html"
        testUrlSharing(rapidgatorUrl, "DIRECT_DOWNLOAD", listOf("JDownloader"))
    }

    @Test
    fun testPremiumLinkSharing_Uploaded() {
        val uploadedUrl = "https://uploaded.net/file/abc123"
        testUrlSharing(uploadedUrl, "DIRECT_DOWNLOAD", listOf("JDownloader"))
    }

    @Test
    fun testPremiumLinkSharing_Nitroflare() {
        val nitroflareUrl = "https://nitroflare.com/view/ABC123/file.zip"
        testUrlSharing(nitroflareUrl, "DIRECT_DOWNLOAD", listOf("JDownloader"))
    }

    @Test
    fun testTorrentSharing_MagnetLink() {
        val magnetUrl = "magnet:?xt=urn:btih:abc123def456&dn=Example+File&tr=udp%3A%2F%2Ftracker.example.com%3A6969"
        testUrlSharing(magnetUrl, "TORRENT", listOf("qBittorrent", "Transmission", "uTorrent"))
    }

    @Test
    fun testTorrentSharing_TorrentFile() {
        val torrentUrl = "https://example.com/file.torrent"
        testUrlSharing(torrentUrl, "TORRENT", listOf("qBittorrent", "Transmission", "uTorrent"))
    }

    @Test
    fun testArchiveSharing_RAR() {
        val rarUrl = "https://example.com/archive.rar"
        testUrlSharing(rarUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testArchiveSharing_7Z() {
        val sevenZipUrl = "https://example.com/archive.7z"
        testUrlSharing(sevenZipUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testContainerSharing_DLC() {
        val dlcUrl = "https://example.com/links.dlc"
        testUrlSharing(dlcUrl, "DIRECT_DOWNLOAD", listOf("JDownloader"))
    }

    @Test
    fun testContainerSharing_RSDF() {
        val rsdfUrl = "https://example.com/links.rsdf"
        testUrlSharing(rsdfUrl, "DIRECT_DOWNLOAD", listOf("JDownloader"))
    }

    @Test
    fun testDirectDownloadSharing_ZIP() {
        val zipUrl = "https://cdn.example.com/software.zip"
        testUrlSharing(zipUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testDirectDownloadSharing_EXE() {
        val exeUrl = "https://download.example.com/installer.exe"
        testUrlSharing(exeUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testDirectDownloadSharing_PDF() {
        val pdfUrl = "https://example.com/document.pdf"
        testUrlSharing(pdfUrl, "DIRECT_DOWNLOAD", listOf("YT-DLP", "JDownloader"))
    }

    @Test
    fun testUrlTypeDetection_Comprehensive() {
        // Test that all URL types are correctly detected
        val testUrls = mapOf(
            "https://www.youtube.com/watch?v=abc123" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://youtu.be/abc123" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://www.tiktok.com/@user/video/123" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://vimeo.com/123456" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://soundcloud.com/artist/song" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://open.spotify.com/track/abc" to UrlCompatibilityUtils.UrlType.STREAMING,
            "https://www.mediafire.com/file/abc/file.zip" to UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            "https://mega.nz/file/abc" to UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            "https://rapidgator.net/file/abc/file.zip" to UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            "magnet:?xt=urn:btih:abc" to UrlCompatibilityUtils.UrlType.TORRENT,
            "https://example.com/file.torrent" to UrlCompatibilityUtils.UrlType.TORRENT,
            "https://example.com/archive.rar" to UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            "https://example.com/file.zip" to UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD
        )

        for ((url, expectedType) in testUrls) {
            val detectedType = UrlCompatibilityUtils.detectUrlType(url)
            assertEquals("URL $url should be detected as $expectedType", expectedType, detectedType)
        }
    }

    private fun testUrlSharing(url: String, expectedType: String, expectedProfiles: List<String>) {
        // Launch ShareActivity with the URL
        val intent = Intent(context, ShareActivity::class.java).apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(intent)

        // Wait for the activity to load
        device.waitForIdle(3000)

        // Verify the URL is displayed
        val urlText = device.findObject(UiSelector().resourceId("com.shareconnect:id/urlText"))
        assertTrue("URL should be displayed", urlText.waitForExists(5000))
        assertEquals("Displayed URL should match input", url, urlText.text)

        // Verify URL type is detected correctly
        val urlTypeText = device.findObject(UiSelector().resourceId("com.shareconnect:id/urlTypeText"))
        if (urlTypeText.exists()) {
            assertTrue("URL type should contain $expectedType", urlTypeText.text.contains(expectedType))
        }

        // Verify compatible profiles are shown
        for (profile in expectedProfiles) {
            val profileButton = device.findObject(UiSelector().textContains(profile))
            assertTrue("Profile $profile should be available", profileButton.waitForExists(3000))
        }

        // Test selecting first available profile and sharing
        if (expectedProfiles.isNotEmpty()) {
            val firstProfile = device.findObject(UiSelector().textContains(expectedProfiles[0]))
            if (firstProfile.exists()) {
                firstProfile.click()
                device.waitForIdle(1000)

                // Click share button
                val shareButton = device.findObject(UiSelector().resourceId("com.shareconnect:id/buttonShare"))
                if (shareButton.exists()) {
                    shareButton.click()

                    // Wait for success message or result
                    device.waitForIdle(3000)

                    // Check for success toast or navigation back
                    val successToast = device.findObject(UiSelector().textContains("success"))
                    val backNavigation = device.findObject(UiSelector().description("Navigate up"))

                    assertTrue("Should show success or navigate back",
                        successToast.exists() || backNavigation.exists())
                }
            }
        }

        // Clean up - press back to exit activity
        device.pressBack()
        device.waitForIdle(1000)
    }
}