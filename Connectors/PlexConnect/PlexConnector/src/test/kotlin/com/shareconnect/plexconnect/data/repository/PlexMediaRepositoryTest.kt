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


package com.shareconnect.plexconnect.data.repository

import android.content.Context
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.service.PlexAiRecommendationService
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

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

    @MockK
    private lateinit var aiRecommendationService: PlexAiRecommendationService

    private lateinit var repository: PlexMediaRepository

    // Test data
    private val testMediaItems = listOf(
        PlexMediaItem(
            ratingKey = "1",
            title = "Test Movie",
            type = MediaType.MOVIE.value,
            summary = "A test movie summary",
            serverId = 1L,
            librarySectionID = 1L,
            addedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        ),
        PlexMediaItem(
            ratingKey = "2",
            title = "Test Show",
            type = MediaType.SHOW.value,
            summary = "A test show summary",
            serverId = 1L,
            librarySectionID = 1L,
            addedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )
    )

    @Before
    fun setup() {
        MockKAnnotations.init(this, relaxed = true)
        
        // Mock the AI service lazy initialization by creating a spy
        mockkConstructor(PlexAiRecommendationService::class)
        every { PlexAiRecommendationService(any()) } returns aiRecommendationService
        
        repository = PlexMediaRepository(context, mediaItemDao, apiClient)
    }

    @Test
    fun `getEnhancedMediaItems should return enhanced items`() = runTest {
        // Given
        val flowOfItems = flowOf(testMediaItems)
        every { mediaItemDao.getAllMediaItems() } returns flowOfItems
        
        val enhancedItems = testMediaItems.map { item ->
            PlexAiRecommendationService.EnhancedMediaItem(
                originalItem = item,
                metadataAnalysis = null,
                semanticEmbedding = FloatArray(768) { 0f },
                semanticLanguage = "en",
                embeddingSource = com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER
            )
        }
        
        every { aiRecommendationService.getEnhancedMediaItems(any()) } returns flowOf(enhancedItems)
        
        // When
        val result = repository.getEnhancedMediaItems().toList()
        
        // Then
        assert(result[0].size == 2)
        assert(result[0][0].originalItem.title == "Test Movie")
        assert(result[0][1].originalItem.title == "Test Show")
        
        verify { mediaItemDao.getAllMediaItems() }
        verify { aiRecommendationService.getEnhancedMediaItems(any()) }
    }

    @Test
    fun `getEnhancedMediaItemsForServer should filter by serverId`() = runTest {
        // Given
        val serverId = 1L
        val flowOfItems = flowOf(testMediaItems)
        every { mediaItemDao.getMediaItemsForServer(serverId) } returns flowOfItems
        
        val enhancedItems = testMediaItems.map { item ->
            PlexAiRecommendationService.EnhancedMediaItem(
                originalItem = item,
                metadataAnalysis = null,
                semanticEmbedding = FloatArray(768) { 0f },
                semanticLanguage = "en",
                embeddingSource = com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER
            )
        }
        
        every { aiRecommendationService.getEnhancedMediaItems(any()) } returns flowOf(enhancedItems)
        
        // When
        val result = repository.getEnhancedMediaItemsForServer(serverId).toList()
        
        // Then
        assert(result[0].size == 2)
        assert(result[0].all { it.originalItem.serverId == serverId })
        
        verify { mediaItemDao.getMediaItemsForServer(serverId) }
        verify { aiRecommendationService.getEnhancedMediaItems(any()) }
    }

    @Test
    fun `getEnhancedMediaItemsForLibrary should filter by libraryId`() = runTest {
        // Given
        val libraryId = 1L
        val flowOfItems = flowOf(testMediaItems)
        every { mediaItemDao.getMediaItemsForLibrary(libraryId) } returns flowOfItems
        
        val enhancedItems = testMediaItems.map { item ->
            PlexAiRecommendationService.EnhancedMediaItem(
                originalItem = item,
                metadataAnalysis = null,
                semanticEmbedding = FloatArray(768) { 0f },
                semanticLanguage = "en",
                embeddingSource = com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER
            )
        }
        
        every { aiRecommendationService.getEnhancedMediaItems(any()) } returns flowOf(enhancedItems)
        
        // When
        val result = repository.getEnhancedMediaItemsForLibrary(libraryId).toList()
        
        // Then
        assert(result[0].size == 2)
        assert(result[0].all { it.originalItem.librarySectionID == libraryId })
        
        verify { mediaItemDao.getMediaItemsForLibrary(libraryId) }
        verify { aiRecommendationService.getEnhancedMediaItems(any()) }
    }

    @Test
    fun `findSimilarMedia should return similar items`() = runTest {
        // Given
        val targetItem = testMediaItems[0]
        val similarityResults = listOf(
            PlexAiRecommendationService.SimilarMediaResult(
                mediaItem = testMediaItems[1],
                similarity = 0.85,
                isAboveThreshold = true
            )
        )
        
        coEvery { aiRecommendationService.findSimilarMedia(any(), any(), any()) } returns similarityResults
        
        // When
        val result = repository.findSimilarMedia(targetItem, 0.7)
        
        // Then
        assert(result.size == 1)
        assert(result[0].mediaItem.ratingKey == "2")
        assert(result[0].similarity == 0.85)
        assert(result[0].isAboveThreshold)
        
        coVerify { aiRecommendationService.findSimilarMedia(targetItem, emptyList(), 0.7) }
    }

    @Test
    fun `getCrossLingualRecommendations should return recommendations`() = runTest {
        // Given
        val targetLanguage = "es"
        val recommendations = testMediaItems.map { item ->
            PlexAiRecommendationService.EnhancedMediaItem(
                originalItem = item,
                metadataAnalysis = null,
                semanticEmbedding = FloatArray(768) { 0f },
                semanticLanguage = targetLanguage,
                embeddingSource = com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED
            )
        }
        
        coEvery { aiRecommendationService.getCrossLingualRecommendations(any(), targetLanguage) } returns recommendations
        
        // When
        val result = repository.getCrossLingualRecommendations(targetLanguage, 20)
        
        // Then
        assert(result.size == 2)
        assert(result.all { it.semanticLanguage == targetLanguage })
        
        coVerify { aiRecommendationService.getCrossLingualRecommendations(emptyList(), targetLanguage) }
    }

    @Test
    fun `getRecommendationsBasedOnHistory should return history-based recommendations`() = runTest {
        // Given
        val watchedItems = testMediaItems.filter { it.viewCount > 0 }
        val similarityResults = listOf(
            PlexAiRecommendationService.SimilarMediaResult(
                mediaItem = testMediaItems[1],
                similarity = 0.9,
                isAboveThreshold = true
            )
        )
        
        coEvery { 
            aiRecommendationService.findSimilarMedia(any(), any(), any()) 
        } returns similarityResults andThen emptyList()
        
        // When
        val result = repository.getRecommendationsBasedOnHistory(20)
        
        // Then
        assert(result.isNotEmpty())
        
        coVerify { 
            // Should call findSimilarMedia for each watched item
            aiRecommendationService.findSimilarMedia(any(), any(), any())
        }
    }
}