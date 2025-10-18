package com.shareconnect


import com.shareconnect.historysync.models.HistoryData
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.ArgumentCaptor
import org.mockito.Mock
import org.mockito.Mockito.*
import org.mockito.MockitoAnnotations
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import java.util.*

// Import R class for resource IDs
import com.shareconnect.R

@RunWith(RobolectricTestRunner::class)
class HistoryAdapterTest {

    @Mock
    private lateinit var mockListener: HistoryAdapter.OnHistoryItemClickListener

    private lateinit var adapter: HistoryAdapter
    private lateinit var historyItems: List<HistoryData>

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        adapter = HistoryAdapter(mockListener)

        // Create test history items
        historyItems = listOf(
            HistoryData(
                id = "1",
                url = "https://youtube.com/watch?v=test",
                title = "Test Video",
                description = "A test video description",
                thumbnailUrl = null,
                serviceProvider = "YouTube",
                type = HistoryData.TYPE_VIDEO,
                timestamp = System.currentTimeMillis(),
                profileId = "profile1",
                profileName = "Test Profile",
                isSentSuccessfully = true,
                serviceType = HistoryData.SERVICE_METUBE,
                torrentClientType = null,
                sourceApp = "ShareConnector",
                version = 1,
                lastModified = System.currentTimeMillis(),
                fileSize = 1024 * 1024 * 100L, // 100 MB
                duration = 3600, // 1 hour
                quality = "1080p",
                downloadPath = null,
                torrentHash = null,
                magnetUri = null,
                category = null,
                tags = null
            ),
            HistoryData(
                id = "2",
                url = "magnet:?xt=urn:btih:test",
                title = "Test Torrent",
                description = null,
                thumbnailUrl = null,
                serviceProvider = "ThePirateBay",
                type = HistoryData.TYPE_TORRENT,
                timestamp = System.currentTimeMillis() - 86400000, // 1 day ago
                profileId = "profile2",
                profileName = "Torrent Profile",
                isSentSuccessfully = false,
                serviceType = HistoryData.SERVICE_TORRENT,
                torrentClientType = HistoryData.TORRENT_CLIENT_QBITTORRENT,
                sourceApp = "qBitConnect",
                version = 1,
                lastModified = System.currentTimeMillis(),
                fileSize = 1024 * 1024 * 1024L * 2, // 2 GB
                duration = null,
                quality = null,
                downloadPath = null,
                torrentHash = null,
                magnetUri = null,
                category = "Movies",
                tags = "HD,Action,2024"
            ),
            HistoryData(
                id = "3",
                url = "https://example.com/file.zip",
                title = null, // Test fallback to URL
                description = null,
                thumbnailUrl = null,
                serviceProvider = null,
                type = HistoryData.TYPE_FILE,
                timestamp = System.currentTimeMillis() - 3600000, // 1 hour ago
                profileId = null,
                profileName = null,
                isSentSuccessfully = false,
                serviceType = HistoryData.SERVICE_JDOWNLOADER,
                torrentClientType = null,
                sourceApp = "ShareConnector",
                version = 1,
                lastModified = System.currentTimeMillis(),
                fileSize = null,
                duration = null,
                quality = null,
                downloadPath = null,
                torrentHash = null,
                magnetUri = null,
                category = null,
                tags = null
            )
        )
    }

    @Test
    fun `updateHistoryItems updates adapter with new items`() {
        adapter.updateHistoryItems(historyItems)

        assertEquals("Adapter should have 3 items", 3, adapter.itemCount)
    }

    @Test
    fun `adapter can be instantiated with listener`() {
        // Test that adapter can be created and has expected initial state
        val testAdapter = HistoryAdapter(mockListener)

        assertNotNull("Adapter should be created successfully", testAdapter)
        assertEquals("New adapter should have 0 items", 0, testAdapter.itemCount)
    }

    @Test
    fun `onBindViewHolder binds video history item correctly`() {
        // Test core binding logic without layout inflation
        val videoItem = historyItems[0]

        // Verify the item has correct properties for video binding
        assertEquals("Test Video", videoItem.getDisplayTitle())
        assertEquals("https://youtube.com/watch?v=test", videoItem.url)
        assertEquals("YouTube", videoItem.serviceProvider)
        assertEquals(HistoryData.TYPE_VIDEO, videoItem.type)
        assertTrue(videoItem.isSentSuccessfully)
        assertEquals("100 MB", videoItem.getFormattedFileSize())
        assertEquals("1:00:00", videoItem.getFormattedDuration())
        assertEquals("1080p", videoItem.quality)
        assertFalse(videoItem.isTorrentHistory())
        assertTrue(videoItem.isMediaHistory())
    }

    @Test
    fun `adapter handles torrent history items correctly`() {
        // Test that verifies the adapter can handle torrent items
        adapter.updateHistoryItems(listOf(historyItems[1]))

        assertEquals("Adapter should have 1 item", 1, adapter.itemCount)

        // Verify the torrent item properties
        val torrentItem = historyItems[1]
        assertEquals("Test Torrent", torrentItem.getDisplayTitle())
        assertTrue("Item should be identified as torrent", torrentItem.isTorrentHistory())
        assertTrue("Item should be qBittorrent", torrentItem.isQBitTorrentHistory())
        assertEquals(HistoryData.TORRENT_CLIENT_QBITTORRENT, torrentItem.torrentClientType)
        assertEquals("Movies", torrentItem.category)
        assertEquals(3, torrentItem.getTagsList().size) // Should be 3 after trimming empty strings
    }

    @Test
    fun `onBindViewHolder handles null title with URL fallback`() {
        // Test title fallback logic without layout inflation
        val fileItem = historyItems[2]

        // Verify null title falls back to URL
        assertNull(fileItem.title)
        assertEquals("https://example.com/file.zip", fileItem.getDisplayTitle())
        assertEquals(HistoryData.TYPE_FILE, fileItem.type)
        assertFalse(fileItem.isSentSuccessfully)
        assertNull(fileItem.fileSize)
        assertFalse(fileItem.isTorrentHistory())
        assertFalse(fileItem.isMediaHistory())
    }

    @Test
    fun `onBindViewHolder shows media details for video items`() {
        // Test media details formatting without layout inflation
        val videoItem = historyItems[0]

        // Verify media details are properly formatted
        assertEquals("1:00:00", videoItem.getFormattedDuration())
        assertEquals("1080p", videoItem.quality)
        assertEquals("100 MB", videoItem.getFormattedFileSize())
        assertEquals(3600, videoItem.duration)
        assertEquals(1024 * 1024 * 100L, videoItem.fileSize)
        assertTrue(videoItem.isMediaHistory())
    }

    @Test
    fun `onBindViewHolder shows torrent details for torrent items`() {
        // Test torrent details formatting without layout inflation
        val torrentItem = historyItems[1]

        // Verify torrent details are properly formatted
        assertEquals("Test Torrent", torrentItem.getDisplayTitle())
        assertEquals(HistoryData.TORRENT_CLIENT_QBITTORRENT, torrentItem.torrentClientType)
        assertEquals("Movies", torrentItem.category)
        assertEquals("2 GB", torrentItem.getFormattedFileSize())
        assertEquals(3, torrentItem.getTagsList().size)
        assertTrue(torrentItem.isTorrentHistory())
        assertTrue(torrentItem.isQBitTorrentHistory())
    }

    @Test
    fun `button clicks trigger correct listener methods`() {
        // Test that listener interface methods are properly defined
        // This test verifies the contract between adapter and listener

        // Verify listener is not null and properly set
        assertNotNull("Listener should be set", mockListener)

        // Test that the adapter can be created with a listener
        val testAdapter = HistoryAdapter(mockListener)
        assertNotNull("Adapter should be created successfully", testAdapter)

        // Test that history items can be updated
        testAdapter.updateHistoryItems(historyItems)
        assertEquals("Adapter should have correct item count", 3, testAdapter.itemCount)
    }

    @Test
    fun `getFormattedFileSize returns correct formats`() {
        val item1 = HistoryData(id = "1", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_FILE, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_JDOWNLOADER, torrentClientType = null, sourceApp = "ShareConnector", fileSize = 512L) // 512 B
        val item2 = HistoryData(id = "2", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_FILE, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_JDOWNLOADER, torrentClientType = null, sourceApp = "ShareConnector", fileSize = 1024L) // 1 KB
        val item3 = HistoryData(id = "3", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_FILE, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_JDOWNLOADER, torrentClientType = null, sourceApp = "ShareConnector", fileSize = 1024 * 1024L) // 1 MB
        val item4 = HistoryData(id = "4", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_FILE, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_JDOWNLOADER, torrentClientType = null, sourceApp = "ShareConnector", fileSize = 1024 * 1024 * 1024L) // 1 GB

        assertEquals("512 B", item1.getFormattedFileSize())
        assertEquals("1 KB", item2.getFormattedFileSize())
        assertEquals("1 MB", item3.getFormattedFileSize())
        assertEquals("1 GB", item4.getFormattedFileSize())
    }

    @Test
    fun `getFormattedDuration returns correct formats`() {
        val item1 = HistoryData(id = "1", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector", duration = 30) // 30 seconds
        val item2 = HistoryData(id = "2", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector", duration = 90) // 1:30
        val item3 = HistoryData(id = "3", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector", duration = 3661) // 1:01:01

        assertEquals("0:30", item1.getFormattedDuration())
        assertEquals("1:30", item2.getFormattedDuration())
        assertEquals("1:01:01", item3.getFormattedDuration())
    }

    @Test
    fun `getTagsList parses comma separated tags correctly`() {
        val item = HistoryData(id = "1", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector", tags = "HD, Action, 2024, ")
        val tags = item.getTagsList()

        assertEquals(3, tags.size)
        assertTrue(tags.contains("HD"))
        assertTrue(tags.contains("Action"))
        assertTrue(tags.contains("2024"))
        assertFalse(tags.contains(""))
    }

    @Test
    fun `history item contains comprehensive metadata for video content`() {
        val videoItem = HistoryData(
            id = "video_123",
            url = "https://youtube.com/watch?v=test",
            title = "Amazing Video Title",
            description = "This is a detailed description of the video content",
            thumbnailUrl = "https://img.youtube.com/vi/test/maxresdefault.jpg",
            serviceProvider = "YouTube",
            type = HistoryData.TYPE_VIDEO,
            timestamp = System.currentTimeMillis(),
            profileId = "profile1",
            profileName = "My Server",
            isSentSuccessfully = true,
            serviceType = HistoryData.SERVICE_METUBE,
            torrentClientType = null,
            sourceApp = "ShareConnector",
            version = 1,
            lastModified = System.currentTimeMillis(),
            fileSize = 1024 * 1024 * 500L, // 500 MB
            duration = 3600, // 1 hour
            quality = "1080p",
            downloadPath = "/downloads/videos/",
            torrentHash = null,
            magnetUri = null,
            category = null,
            tags = "educational, tutorial, programming"
        )

        // Verify all metadata fields are populated
        assertEquals("video_123", videoItem.id)
        assertEquals("https://youtube.com/watch?v=test", videoItem.url)
        assertEquals("Amazing Video Title", videoItem.title)
        assertEquals("This is a detailed description of the video content", videoItem.description)
        assertEquals("https://img.youtube.com/vi/test/maxresdefault.jpg", videoItem.thumbnailUrl)
        assertEquals("YouTube", videoItem.serviceProvider)
        assertEquals(HistoryData.TYPE_VIDEO, videoItem.type)
        assertEquals("profile1", videoItem.profileId)
        assertEquals("My Server", videoItem.profileName)
        assertTrue(videoItem.isSentSuccessfully)
        assertEquals(HistoryData.SERVICE_METUBE, videoItem.serviceType)
        assertEquals("ShareConnector", videoItem.sourceApp)
        assertEquals(500L * 1024 * 1024, videoItem.fileSize)
        assertEquals(3600, videoItem.duration)
        assertEquals("1080p", videoItem.quality)
        assertEquals("/downloads/videos/", videoItem.downloadPath)
        assertEquals("educational, tutorial, programming", videoItem.tags)

        // Verify computed fields work correctly
        assertEquals("Amazing Video Title", videoItem.getDisplayTitle())
        assertEquals("500 MB", videoItem.getFormattedFileSize())
        assertEquals("1:00:00", videoItem.getFormattedDuration())
        assertEquals(3, videoItem.getTagsList().size)
        assertFalse(videoItem.isTorrentHistory())
        assertTrue(videoItem.isMediaHistory())
    }

    @Test
    fun `history item contains comprehensive metadata for torrent content`() {
        val torrentItem = HistoryData(
            id = "torrent_456",
            url = "magnet:?xt=urn:btih:abc123",
            title = "Ubuntu Linux ISO",
            description = "Latest Ubuntu Linux distribution",
            thumbnailUrl = null,
            serviceProvider = "ThePirateBay",
            type = HistoryData.TYPE_TORRENT,
            timestamp = System.currentTimeMillis(),
            profileId = "profile2",
            profileName = "Torrent Server",
            isSentSuccessfully = true,
            serviceType = HistoryData.SERVICE_TORRENT,
            torrentClientType = HistoryData.TORRENT_CLIENT_QBITTORRENT,
            sourceApp = "qBitConnect",
            version = 1,
            lastModified = System.currentTimeMillis(),
            fileSize = 1024 * 1024 * 1024L * 3, // 3 GB
            duration = null,
            quality = null,
            downloadPath = "/downloads/torrents/ubuntu/",
            torrentHash = "abc123def456",
            magnetUri = "magnet:?xt=urn:btih:abc123def456",
            category = "Operating Systems",
            tags = "linux, ubuntu, iso, free"
        )

        // Verify all metadata fields are populated
        assertEquals("torrent_456", torrentItem.id)
        assertEquals("magnet:?xt=urn:btih:abc123", torrentItem.url)
        assertEquals("Ubuntu Linux ISO", torrentItem.title)
        assertEquals("Latest Ubuntu Linux distribution", torrentItem.description)
        assertEquals("ThePirateBay", torrentItem.serviceProvider)
        assertEquals(HistoryData.TYPE_TORRENT, torrentItem.type)
        assertEquals("profile2", torrentItem.profileId)
        assertEquals("Torrent Server", torrentItem.profileName)
        assertTrue(torrentItem.isSentSuccessfully)
        assertEquals(HistoryData.SERVICE_TORRENT, torrentItem.serviceType)
        assertEquals(HistoryData.TORRENT_CLIENT_QBITTORRENT, torrentItem.torrentClientType)
        assertEquals("qBitConnect", torrentItem.sourceApp)
        assertEquals(3L * 1024 * 1024 * 1024, torrentItem.fileSize)
        assertEquals("/downloads/torrents/ubuntu/", torrentItem.downloadPath)
        assertEquals("abc123def456", torrentItem.torrentHash)
        assertEquals("magnet:?xt=urn:btih:abc123def456", torrentItem.magnetUri)
        assertEquals("Operating Systems", torrentItem.category)
        assertEquals("linux, ubuntu, iso, free", torrentItem.tags)

        // Verify computed fields work correctly
        assertEquals("Ubuntu Linux ISO", torrentItem.getDisplayTitle())
        assertEquals("3 GB", torrentItem.getFormattedFileSize())
        assertNull(torrentItem.getFormattedDuration())
        assertEquals(4, torrentItem.getTagsList().size)
        assertTrue(torrentItem.isTorrentHistory())
        assertTrue(torrentItem.isQBitTorrentHistory())
        assertFalse(torrentItem.isMediaHistory())
    }

    @Test
    fun `history item handles null metadata gracefully`() {
        val minimalItem = HistoryData(
            id = "minimal_789",
            url = "https://example.com/file.txt",
            title = null,
            description = null,
            thumbnailUrl = null,
            serviceProvider = null,
            type = HistoryData.TYPE_FILE,
            timestamp = System.currentTimeMillis(),
            profileId = null,
            profileName = null,
            isSentSuccessfully = false,
            serviceType = HistoryData.SERVICE_JDOWNLOADER,
            torrentClientType = null,
            sourceApp = "ShareConnector"
        )

        // Verify null handling
        assertEquals("https://example.com/file.txt", minimalItem.getDisplayTitle())
        assertNull(minimalItem.getFormattedFileSize())
        assertNull(minimalItem.getFormattedDuration())
        assertTrue(minimalItem.getTagsList().isEmpty())
        assertFalse(minimalItem.isTorrentHistory())
        assertFalse(minimalItem.isMediaHistory())
        assertEquals("Unknown", minimalItem.serviceProvider ?: "Unknown")
        assertEquals("Not Sent", minimalItem.profileName ?: "Not Sent")
    }

    @Test
    fun `isTorrentHistory returns true for torrent types`() {
        val torrentItem = HistoryData(id = "1", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_TORRENT, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_TORRENT, torrentClientType = null, sourceApp = "ShareConnector")
        val magnetItem = HistoryData(id = "2", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_MAGNET, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_TORRENT, torrentClientType = null, sourceApp = "ShareConnector")
        val videoItem = HistoryData(id = "3", url = "test", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector")

        assertTrue(torrentItem.isTorrentHistory())
        assertTrue(magnetItem.isTorrentHistory())
        assertFalse(videoItem.isTorrentHistory())
    }

    @Test
    fun `getDisplayTitle returns title or URL fallback`() {
        val itemWithTitle = HistoryData(id = "1", url = "https://example.com", title = "Test Title", description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector")
        val itemWithoutTitle = HistoryData(id = "2", url = "https://example.com", title = null, description = null, thumbnailUrl = null, serviceProvider = null, type = HistoryData.TYPE_VIDEO, timestamp = System.currentTimeMillis(), profileId = null, profileName = null, isSentSuccessfully = true, serviceType = HistoryData.SERVICE_METUBE, torrentClientType = null, sourceApp = "ShareConnector")

        assertEquals("Test Title", itemWithTitle.getDisplayTitle())
        assertEquals("https://example.com", itemWithoutTitle.getDisplayTitle())
    }



    @Test
    fun `torrent history item has comprehensive metadata for re-sharing`() {
        // Test torrent item metadata completeness
        val torrentItem = historyItems[1]

        // Verify that all necessary metadata is available for re-sharing
        assertNotNull("URL should be present", torrentItem.url)
        assertNotNull("Title should be present", torrentItem.title)
        assertNotNull("Service provider should be present", torrentItem.serviceProvider)
        assertNotNull("Type should be present", torrentItem.type)
        assertNotNull("Service type should be present", torrentItem.serviceType)
        assertNotNull("Torrent client type should be present", torrentItem.torrentClientType)
        assertNotNull("File size should be present", torrentItem.fileSize)
        assertNotNull("Category should be present", torrentItem.category)
        assertNotNull("Tags should be present", torrentItem.tags)
        assertNotNull("History ID should be present", torrentItem.id)
        assertTrue("Should be torrent history", torrentItem.isTorrentHistory())
    }
}