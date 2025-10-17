package com.shareconnect.utils

import com.shareconnect.ServerProfile
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
class UrlCompatibilityUtilsTest {

    @Test
    fun testDetectUrlType_StreamingUrls() {
        // YouTube URLs
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.youtube.com/watch?v=abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://youtu.be/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://music.youtube.com/watch?v=abc"))

        // Vimeo URLs
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://vimeo.com/123456"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://player.vimeo.com/video/123"))

        // Twitch URLs
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.twitch.tv/streamer"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://m.twitch.tv/streamer"))

        // Social Media Platforms
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.tiktok.com/@user/video/123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://vm.tiktok.com/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.instagram.com/p/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.facebook.com/watch?v=123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://fb.watch/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://twitter.com/user/status/123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://x.com/user/status/123"))

        // Music Streaming
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://soundcloud.com/artist/song"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.deezer.com/track/123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://open.spotify.com/track/abc"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.tidal.com/track/123"))

        // Video Sharing Platforms
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.dailymotion.com/video/xyz"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.bilibili.com/video/av123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.nicovideo.jp/watch/sm123"))

        // Mix/Cloud Platforms
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.mixcloud.com/artist/track/"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.bandcamp.com/track/song"))

        // International Platforms
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.youku.com/v_show/id_abc.html"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://v.qq.com/x/page/abc.html"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.iqiyi.com/v_abc.html"))

        // News and Media Sites
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.cnn.com/videos/politics/2024/01/01/abc"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.bbc.com/news/av/world-123"))
        assertEquals(UrlCompatibilityUtils.UrlType.STREAMING,
            UrlCompatibilityUtils.detectUrlType("https://www.australia.plus/video/123"))
    }

    @Test
    fun testDetectUrlType_TorrentUrls() {
        // Magnet links
        assertEquals(UrlCompatibilityUtils.UrlType.TORRENT,
            UrlCompatibilityUtils.detectUrlType("magnet:?xt=urn:btih:abc123"))

        // Torrent files
        assertEquals(UrlCompatibilityUtils.UrlType.TORRENT,
            UrlCompatibilityUtils.detectUrlType("https://example.com/file.torrent"))
        assertEquals(UrlCompatibilityUtils.UrlType.TORRENT,
            UrlCompatibilityUtils.detectUrlType("http://tracker.com/download.torrent"))
    }

    @Test
    fun testDetectUrlType_DirectDownloadUrls() {
        // Regular direct download URLs
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/file.zip"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("http://download.com/software.exe"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://cdn.example.com/image.jpg"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://files.example.com/document.pdf"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("ftp://ftp.example.com/file.txt"))

        // File hosting services (should be treated as direct downloads for JDownloader)
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://www.mediafire.com/file/abc123/file.zip"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://mega.nz/file/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://drive.google.com/file/d/abc123/view"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://1drv.ms/u/s!abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://www.dropbox.com/s/abc123/file.pdf"))

        // Premium link services
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://rapidgator.net/file/abc123/file.zip"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://uploaded.net/file/abc123"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://nitroflare.com/view/abc123/file.zip"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://filefactory.com/file/abc123"))

        // Container formats (DLC, RSDF, etc.)
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/container.dlc"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/links.rsdf"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/package.ccf"))

        // Archive files
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/archive.rar"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/files.7z"))
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("https://example.com/data.tar.gz"))
    }

    @Test
    fun testDetectUrlType_InvalidUrls() {
        assertNull(UrlCompatibilityUtils.detectUrlType(null))
        assertNull(UrlCompatibilityUtils.detectUrlType(""))
        assertNull(UrlCompatibilityUtils.detectUrlType("   "))
        assertNull(UrlCompatibilityUtils.detectUrlType("invalid-url"))
        // FTP URLs should be treated as direct downloads, not invalid
        assertEquals(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD,
            UrlCompatibilityUtils.detectUrlType("ftp://example.com/file.txt"))
    }

    @Test
    fun testIsProfileCompatible_MeTube() {
        val metubeProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
        }

        // MeTube supports streaming only
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(metubeProfile, UrlCompatibilityUtils.UrlType.STREAMING))
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(metubeProfile, UrlCompatibilityUtils.UrlType.TORRENT))
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(metubeProfile, UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD))
    }

    @Test
    fun testIsProfileCompatible_YTDL() {
        val ytdlProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_YTDL
        }

        // YT-DLP supports streaming and direct downloads
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(ytdlProfile, UrlCompatibilityUtils.UrlType.STREAMING))
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(ytdlProfile, UrlCompatibilityUtils.UrlType.TORRENT))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(ytdlProfile, UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD))
    }

    @Test
    fun testIsProfileCompatible_Torrent() {
        val torrentProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
        }

        // Torrent clients support torrents only
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(torrentProfile, UrlCompatibilityUtils.UrlType.STREAMING))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(torrentProfile, UrlCompatibilityUtils.UrlType.TORRENT))
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(torrentProfile, UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD))
    }

    @Test
    fun testIsProfileCompatible_JDownloader() {
        val jdownloaderProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_JDOWNLOADER
        }

        // jDownloader is a comprehensive download manager that supports everything except torrents
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile, UrlCompatibilityUtils.UrlType.STREAMING))
        assertFalse(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile, UrlCompatibilityUtils.UrlType.TORRENT))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile, UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD))

        // Test specific URL examples that JDownloader should support
        // Streaming URLs
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://www.youtube.com/watch?v=abc123")!!))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://www.tiktok.com/@user/video/123")!!))

        // File hosting services
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://www.mediafire.com/file/abc123/file.zip")!!))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://mega.nz/file/abc123")!!))

        // Premium link services
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://rapidgator.net/file/abc123/file.zip")!!))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://uploaded.net/file/abc123")!!))

        // Container formats
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://example.com/container.dlc")!!))
        assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
            UrlCompatibilityUtils.detectUrlType("https://example.com/links.rsdf")!!))

         // Archive files
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://example.com/archive.rar")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://example.com/files.7z")!!))

         // Additional JDownloader supported sites
         // File hosting services
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.mediafire.com/file/abc123/file.zip")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://mega.nz/file/abc123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://drive.google.com/file/d/abc123/view")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://1drv.ms/u/s!abc123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.dropbox.com/s/abc123/file.pdf")!!))

         // Premium link services
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://rapidgator.net/file/abc123/file.zip")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://uploaded.net/file/abc123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://nitroflare.com/view/abc123/file.zip")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://filefactory.com/file/abc123")!!))

         // Container formats
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://example.com/container.dlc")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://example.com/links.rsdf")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://example.com/package.ccf")!!))

         // Additional streaming platforms supported by JDownloader
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.tiktok.com/@user/video/123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.instagram.com/p/abc123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.facebook.com/watch?v=123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://twitter.com/user/status/123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://x.com/user/status/123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.bilibili.com/video/av123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.nicovideo.jp/watch/sm123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.deezer.com/track/123")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://open.spotify.com/track/abc")!!))
         assertTrue(UrlCompatibilityUtils.isProfileCompatible(jdownloaderProfile,
             UrlCompatibilityUtils.detectUrlType("https://www.tidal.com/track/123")!!))
     }

    @Test
    fun testFilterCompatibleProfiles() {
        val metubeProfile = ServerProfile().apply {
            name = "MeTube"
            serviceType = ServerProfile.TYPE_METUBE
        }
        val torrentProfile = ServerProfile().apply {
            name = "qBittorrent"
            serviceType = ServerProfile.TYPE_TORRENT
        }
        val ytdlProfile = ServerProfile().apply {
            name = "YT-DLP"
            serviceType = ServerProfile.TYPE_YTDL
        }
        val jdownloaderProfile = ServerProfile().apply {
            name = "JDownloader"
            serviceType = ServerProfile.TYPE_JDOWNLOADER
        }

        val allProfiles = listOf(metubeProfile, torrentProfile, ytdlProfile, jdownloaderProfile)

        // YouTube URL should show streaming profiles and JDownloader
        val youtubeCompatible = UrlCompatibilityUtils.filterCompatibleProfiles(
            allProfiles, "https://www.youtube.com/watch?v=test")
        assertEquals(3, youtubeCompatible.size)
        assertTrue(youtubeCompatible.contains(metubeProfile))
        assertTrue(youtubeCompatible.contains(ytdlProfile))
        assertTrue(youtubeCompatible.contains(jdownloaderProfile))
        assertFalse(youtubeCompatible.contains(torrentProfile))

        // Magnet URL should show torrent profiles only
        val magnetCompatible = UrlCompatibilityUtils.filterCompatibleProfiles(
            allProfiles, "magnet:?xt=urn:btih:test")
        assertEquals(1, magnetCompatible.size)
        assertTrue(magnetCompatible.contains(torrentProfile))
        assertFalse(magnetCompatible.contains(metubeProfile))
        assertFalse(magnetCompatible.contains(ytdlProfile))
        assertFalse(magnetCompatible.contains(jdownloaderProfile))

        // Direct download URL should show download-capable profiles
        val downloadCompatible = UrlCompatibilityUtils.filterCompatibleProfiles(
            allProfiles, "https://example.com/file.zip")
        assertEquals(2, downloadCompatible.size)
        assertTrue(downloadCompatible.contains(ytdlProfile))
        assertTrue(downloadCompatible.contains(jdownloaderProfile))
        assertFalse(downloadCompatible.contains(metubeProfile))
        assertFalse(downloadCompatible.contains(torrentProfile))
    }

    @Test
    fun testGetProfileSupportDescription() {
        val metubeProfile = ServerProfile().apply { serviceType = ServerProfile.TYPE_METUBE }
        val torrentProfile = ServerProfile().apply { serviceType = ServerProfile.TYPE_TORRENT }
        val ytdlProfile = ServerProfile().apply { serviceType = ServerProfile.TYPE_YTDL }
        val jdownloaderProfile = ServerProfile().apply { serviceType = ServerProfile.TYPE_JDOWNLOADER }

        assertEquals("Streaming videos from 1000+ sites (YouTube, Vimeo, TikTok, etc.)",
            UrlCompatibilityUtils.getProfileSupportDescription(metubeProfile))
        assertEquals("Torrent files and magnet links",
            UrlCompatibilityUtils.getProfileSupportDescription(torrentProfile))
        assertEquals("Streaming videos from 1000+ sites and direct downloads",
            UrlCompatibilityUtils.getProfileSupportDescription(ytdlProfile))
        assertEquals("Downloads from any website (streaming, direct, premium links, etc.)",
            UrlCompatibilityUtils.getProfileSupportDescription(jdownloaderProfile))
    }

    @Test
    fun testGetUrlTypeDescription() {
        assertEquals("streaming video",
            UrlCompatibilityUtils.getUrlTypeDescription(UrlCompatibilityUtils.UrlType.STREAMING))
        assertEquals("torrent",
            UrlCompatibilityUtils.getUrlTypeDescription(UrlCompatibilityUtils.UrlType.TORRENT))
        assertEquals("direct download",
            UrlCompatibilityUtils.getUrlTypeDescription(UrlCompatibilityUtils.UrlType.DIRECT_DOWNLOAD))
    }
}