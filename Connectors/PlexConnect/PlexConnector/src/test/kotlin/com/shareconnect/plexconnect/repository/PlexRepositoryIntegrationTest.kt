package com.shareconnect.plexconnect.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.local.PlexLibraryDao
import com.shareconnect.plexconnect.data.local.PlexMediaItemDao
import com.shareconnect.plexconnect.data.local.PlexServerDao
import com.shareconnect.plexconnect.data.repository.PlexRepositoryImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class PlexRepositoryIntegrationTest {

    private lateinit var database: PlexDatabase
    private lateinit var serverDao: PlexServerDao
    private lateinit var libraryDao: PlexLibraryDao
    private lateinit var mediaItemDao: PlexMediaItemDao
    private lateinit var repository: PlexRepositoryImpl

    @Mock
    private lateinit var apiClient: PlexApiClient

    @Before
    fun setup() {
        // Initialize Mockito mocks
        MockitoAnnotations.openMocks(this)

        // Create an in-memory database
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context, PlexDatabase::class.java
        ).build()

        // Get DAOs
        serverDao = database.plexServerDao()
        libraryDao = database.plexLibraryDao()
        mediaItemDao = database.plexMediaItemDao()

        // Create repository with mocked API client
        repository = PlexRepositoryImpl(
            apiClient, serverDao, libraryDao, mediaItemDao
        )
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        database.close()
    }

    @Test
    fun `getServerInfo caches server information when API call succeeds`() = runBlocking {
        // Prepare mock API response
        val mockServerInfo = com.shareconnect.plexconnect.data.model.PlexServerInfo(
            machineIdentifier = "test-server-1",
            myPlexUsername = "TestUser",
            version = "1.0.0",
            platform = "Test",
            platformVersion = "1.0"
        )
        whenever(apiClient.getServerInfo(any())).thenReturn(Result.success(mockServerInfo))

        // Call repository method
        val serverInfoFlow = repository.getServerInfo("http://test-server", "test-token")
        val serverInfo = serverInfoFlow.first()

        // Verify server info
        assert(serverInfo.machineIdentifier == "test-server-1")
        assert(serverInfo.myPlexUsername == "TestUser")

        // Verify database caching
        val cachedServer = serverDao.getServerById("test-server-1").first()
        assert(cachedServer != null)
        assert(cachedServer?.name == "TestUser")
    }

    @Test
    fun `getLibraries caches library information when API call succeeds`() = runBlocking {
        // Prepare mock API response
        val mockLibraries = listOf(
            com.shareconnect.plexconnect.data.model.PlexLibrarySection(
                key = "library-1",
                title = "Movies",
                type = "movie"
            ),
            com.shareconnect.plexconnect.data.model.PlexLibrarySection(
                key = "library-2",
                title = "TV Shows",
                type = "show"
            )
        )
        whenever(apiClient.getLibraries(any(), any())).thenReturn(Result.success(mockLibraries))

        // Call repository method
        val librariesFlow = repository.getLibraries("http://test-server", "test-token")
        val libraries = librariesFlow.first()

        // Verify library info
        assert(libraries.size == 2)
        assert(libraries[0].title == "Movies")
        assert(libraries[1].title == "TV Shows")

        // Verify database caching
        val cachedLibraries = libraryDao.getLibrariesForServer(any()).first()
        assert(cachedLibraries.size == 2)
        assert(cachedLibraries[0].title == "Movies")
    }

    @Test
    fun `markAsPlayed updates local database when API call succeeds`() = runBlocking {
        // Prepare mock API response
        whenever(apiClient.markAsPlayed(any(), any(), any())).thenReturn(Result.success(Unit))

        // Prepare test media item in database
        val testMediaItem = com.shareconnect.plexconnect.data.local.PlexMediaItemEntity(
            id = "test-media-1",
            libraryId = "test-library",
            title = "Test Media",
            type = "movie",
            isWatched = false
        )
        mediaItemDao.insertMediaItem(testMediaItem)

        // Call repository method
        repository.markAsPlayed("http://test-server", "test-media-1", "test-token")

        // Verify local database update
        val updatedMediaItem = mediaItemDao.getMediaItemById("test-media-1").first()
        assert(updatedMediaItem?.isWatched == true)
        assert(updatedMediaItem?.lastPlayedTime != null)
    }

    @Test(expected = Exception::class)
    fun `repository handles API failure gracefully`() = runBlocking {
        // Simulate API failure
        whenever(apiClient.getServerInfo(any())).thenReturn(Result.failure(Exception("API Error")))

        // This should throw an exception if no cached data is available
        val serverInfoFlow = repository.getServerInfo("http://test-server", "test-token")
        serverInfoFlow.first()
    }
}