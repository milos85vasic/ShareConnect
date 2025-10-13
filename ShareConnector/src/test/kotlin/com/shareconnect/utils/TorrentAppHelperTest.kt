package com.shareconnect.utils

import android.content.Context
import android.content.pm.PackageManager
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.ServerProfile
import com.shareconnect.torrentsharingsync.TorrentSharingSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.TestApplication::class)
class TorrentAppHelperTest {

    private lateinit var context: Context
    private lateinit var mockPackageManager: PackageManager
    private lateinit var openMocks: AutoCloseable

    @Mock
    private lateinit var mockSyncManager: TorrentSharingSyncManager

    @Before
    fun setUp() {
        openMocks = MockitoAnnotations.openMocks(this)
        context = mock(Context::class.java)
        mockPackageManager = mock(PackageManager::class.java)

        // Mock the context to return our mock packageManager
        `when`(context.packageManager).thenReturn(mockPackageManager)

        // Skip initialization for tests that don't need sync manager
        // TorrentAppHelper.initialize(context, "test_app", "Test App", "1.0.0")
    }

    @After
    fun tearDown() {
        openMocks.close()
        TorrentSharingSyncManager.resetInstance()
    }

    @Test
    fun testIsQBitConnectInstalled_returnsTrueWhenInstalled() {
        // Given
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.isQBitConnectInstalled(context)

        // Then
        assertTrue(result)
    }

    @Test
    fun testIsQBitConnectInstalled_returnsFalseWhenNotInstalled() {
        // Given
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT, 0)
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT), any<PackageManager.PackageInfoFlags>())
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT_DEBUG, 0)
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT_DEBUG), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.isQBitConnectInstalled(context)

        // Then
        assertFalse(result)
    }

    @Test
    fun testIsTransmissionConnectInstalled_returnsTrueWhenInstalled() {
        // Given
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.isTransmissionConnectInstalled(context)

        // Then
        assertTrue(result)
    }

    @Test
    fun testHasAnyTorrentAppInstalled_returnsTrueWhenQBitConnectInstalled() {
        // Given
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.hasAnyTorrentAppInstalled(context)

        // Then
        assertTrue(result)
    }

    @Test
    fun testHasAnyTorrentAppInstalled_returnsTrueWhenTransmissionConnectInstalled() {
        // Given
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.hasAnyTorrentAppInstalled(context)

        // Then
        assertTrue(result)
    }

    @Test
    fun testHasAnyTorrentAppInstalled_returnsFalseWhenNoneInstalled() {
        // Given
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT, 0)
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT), any<PackageManager.PackageInfoFlags>())
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT_DEBUG, 0)
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT_DEBUG), any<PackageManager.PackageInfoFlags>())
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT, 0)
        doThrow(PackageManager.NameNotFoundException()).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.hasAnyTorrentAppInstalled(context)

        // Then
        assertFalse(result)
    }

    @Test
    fun testGetInstalledAppForProfile_returnsQBitConnectForQBitTorrentProfile() {
        // Given
        val profile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENT_QBITTORRENT
        }
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_QBITCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_QBITCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.getInstalledAppForProfile(context, profile)

        // Then
        assertEquals(TorrentAppHelper.PACKAGE_QBITCONNECT, result)
    }

    @Test
    fun testGetInstalledAppForProfile_returnsTransmissionConnectForTransmissionProfile() {
        // Given
        val profile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENT_TRANSMISSION
        }
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT, 0)
        doReturn(mock(android.content.pm.PackageInfo::class.java)).`when`(mockPackageManager).getPackageInfo(eq(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT), any<PackageManager.PackageInfoFlags>())

        // When
        val result = TorrentAppHelper.getInstalledAppForProfile(context, profile)

        // Then
        assertEquals(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT, result)
    }

    @Test
    fun testGetInstalledAppForProfile_returnsNullForNonTorrentProfile() {
        // Given
        val profile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
        }

        // When
        val result = TorrentAppHelper.getInstalledAppForProfile(context, profile)

        // Then
        assertNull(result)
    }

    @Test
    fun testGetAppPackageForClientType_returnsCorrectPackage() {
        // When & Then
        assertEquals(TorrentAppHelper.PACKAGE_QBITCONNECT,
            TorrentAppHelper.getAppPackageForClientType(ServerProfile.TORRENT_CLIENT_QBITTORRENT))
        assertEquals(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT,
            TorrentAppHelper.getAppPackageForClientType(ServerProfile.TORRENT_CLIENT_TRANSMISSION))
        assertNull(TorrentAppHelper.getAppPackageForClientType(ServerProfile.TORRENT_CLIENTUTORRENT))
        assertNull(TorrentAppHelper.getAppPackageForClientType(null))
    }

    @Test
    fun testIsMagnetLink_detectsMagnetLinks() {
        // When & Then
        assertTrue(TorrentAppHelper.isMagnetLink("magnet:?xt=urn:btih:1234567890abcdef"))
        assertTrue(TorrentAppHelper.isMagnetLink("MAGNET:?xt=urn:btih:1234567890abcdef"))
        assertFalse(TorrentAppHelper.isMagnetLink("http://example.com/file.torrent"))
        assertFalse(TorrentAppHelper.isMagnetLink(null))
        assertFalse(TorrentAppHelper.isMagnetLink(""))
    }

    @Test
    fun testIsTorrentFile_detectsTorrentFiles() {
        // When & Then
        assertTrue(TorrentAppHelper.isTorrentFile("http://example.com/file.torrent"))
        assertTrue(TorrentAppHelper.isTorrentFile("file.torrent"))
        assertTrue(TorrentAppHelper.isTorrentFile("FILE.TORRENT"))
        assertFalse(TorrentAppHelper.isTorrentFile("magnet:?xt=urn:btih:123"))
        assertFalse(TorrentAppHelper.isTorrentFile(null))
        assertFalse(TorrentAppHelper.isTorrentFile(""))
    }

    @Test
    fun testIsTorrentContent_detectsTorrentContent() {
        // When & Then
        assertTrue(TorrentAppHelper.isTorrentContent("magnet:?xt=urn:btih:123"))
        assertTrue(TorrentAppHelper.isTorrentContent("file.torrent"))
        assertFalse(TorrentAppHelper.isTorrentContent("http://example.com/video.mp4"))
        assertFalse(TorrentAppHelper.isTorrentContent(null))
    }

    @Test
    fun testGetSuggestedAppForProfile_returnsCorrectApp() {
        // Given
        val qbitProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENT_QBITTORRENT
        }
        val transmissionProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENT_TRANSMISSION
        }
        val utorrentProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENTUTORRENT
        }
        val nonTorrentProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
        }

        // When & Then
        assertEquals(TorrentAppHelper.PACKAGE_QBITCONNECT,
            TorrentAppHelper.getSuggestedAppForProfile(qbitProfile))
        assertEquals(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT,
            TorrentAppHelper.getSuggestedAppForProfile(transmissionProfile))
        assertNull(TorrentAppHelper.getSuggestedAppForProfile(utorrentProfile))
        assertNull(TorrentAppHelper.getSuggestedAppForProfile(nonTorrentProfile))
    }

    @Test
    fun testGetAppName_returnsCorrectNames() {
        // When & Then
        assertEquals("qBitConnect", TorrentAppHelper.getAppName(TorrentAppHelper.PACKAGE_QBITCONNECT))
        assertEquals("qBitConnect", TorrentAppHelper.getAppName(TorrentAppHelper.PACKAGE_QBITCONNECT_DEBUG))
        assertEquals("TransmissionConnect", TorrentAppHelper.getAppName(TorrentAppHelper.PACKAGE_TRANSMISSIONCONNECT))
        assertEquals("Torrent App", TorrentAppHelper.getAppName("unknown.package"))
    }
}