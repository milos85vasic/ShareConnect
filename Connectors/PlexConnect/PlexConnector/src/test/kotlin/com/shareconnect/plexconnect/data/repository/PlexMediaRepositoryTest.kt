/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of Software, and to permit persons to whom Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


package com.shareconnect.plexconnect.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.api.PlexApiClient
import io.mockk.coEvery
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class PlexMediaRepositoryTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @MockK
    private lateinit var context: Context
    
    @MockK
    private lateinit var mediaItemDao: PlexMediaItemDao
    
    @MockK
    private lateinit var apiClient: com.shareconnect.plexconnect.data.api.PlexApiClient
    
    private lateinit var repository: PlexMediaRepository
    
    private val testMediaItems = listOf(
        PlexMediaItem(
            ratingKey = "100",
            key = "/library/metadata/100",
            type = MediaType.MOVIE,
            title = "Test Movie",
            year = 2024,
            serverId = 1L
        ),
        PlexMediaItem(
            ratingKey = "200",
            key = "/library/metadata/200", 
            type = MediaType.SHOW,
            title = "Test Show",
            year = 2023,
            serverId = 1L
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        
        // Stub the API client since it's in stub mode
        every { apiClient.isStubMode } returns true
        
        repository = PlexMediaRepository(context, mediaItemDao, apiClient)
    }

    @Test
    fun `repository should initialize successfully`() = runTest {
        // Given - setup already done
        
        // When - repository is created
        
        // Then - repository should not be null
        assertNotNull(repository)
    }

    @Test
    fun `getAllMediaItems should return flow`() = runTest {
        // Given
        val flowOfItems = flowOf(testMediaItems)
        every { mediaItemDao.getAllMediaItems() } returns flowOfItems
        
        // When
        val result = repository.getAllMediaItems().first()
        
        // Then
        assertEquals(2, result.size)
        assertTrue("Result should contain items", result.isNotEmpty())
        val firstItem = result.first()
        assertEquals("Test Movie", firstItem.title)
    }

    @Test
    fun `getMediaItemsByType should filter by type`() = runTest {
        // Given
        val movieItems = testMediaItems.filter { it.type == MediaType.MOVIE }
        every { mediaItemDao.getMediaItemsByType(MediaType.MOVIE.value) } returns flowOf(movieItems)
        
        // When
        val result = repository.getMediaItemsByType(MediaType.MOVIE).first()
        
        // Then
        assertEquals(1, result.size)
        assertTrue("Result should contain items", result.isNotEmpty())
        val firstItem = result.first()
        assertEquals("Test Movie", firstItem.title)
    }

    @Test
    fun `suspend function should work`() = runTest {
        // Given
        val mediaItem = testMediaItems[0]
        coEvery { mediaItemDao.getMediaItemByKey("100") } returns mediaItem
        
        // When
        val result = repository.getMediaItemByKey("100")
        
        // Then
        assertEquals("Test Movie", result?.title)
    }
}