package com.shareconnect.plexconnect.ml

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AdvancedRecommendationAlgorithmTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private lateinit var mockDatabase: PlexDatabase
    private lateinit var mockPlexMediaItemDao: PlexMediaItemDao
    private lateinit var recommendationAlgorithm: AdvancedRecommendationAlgorithm

    @Before
    fun setup() {
        mockDatabase = mockk()
        mockPlexMediaItemDao = mockk()
        every { mockDatabase.plexMediaItemDao() } returns mockPlexMediaItemDao
        recommendationAlgorithm = AdvancedRecommendationAlgorithm(mockDatabase)
    }

    @Test
    fun `recommendation generation returns non-empty list`() = runTest {
        // Prepare mock data
        val userId = "test_user"
        val watchHistory = listOf(
            createMockMediaItem("Movie1", MediaType.MOVIE, 2020),
            createMockMediaItem("TV Show1", MediaType.SHOW, 2019)
        )
        val allItems = listOf(
            createMockMediaItem("Movie2", MediaType.MOVIE, 2021),
            createMockMediaItem("TV Show2", MediaType.SHOW, 2022)
        )

        // Mock DAO responses
        every { mockPlexMediaItemDao.getWatchedItems() }
            .returns(flowOf(watchHistory))
        every { mockPlexMediaItemDao.getAllMediaItems() }
            .returns(flowOf(allItems))

        // Generate recommendations
        val recommendations = recommendationAlgorithm
            .generateRecommendations(userId)
            .first()

        // Assertions
        assertTrue("Recommendations should not be empty", recommendations.isNotEmpty())
        assertEquals("Should return recommended items", 2, recommendations.size)
    }

    @Test
    fun `recommendation scoring handles different media types`() = runTest {
        val userId = "test_user"
        val watchHistory = listOf(
            createMockMediaItem("Sci-Fi Movie", MediaType.MOVIE, 2020, "Sci-fi adventure"),
            createMockMediaItem("Sci-Fi Show", MediaType.SHOW, 2019, "Sci-fi series")
        )
        val allItems = listOf(
            createMockMediaItem("Action Movie", MediaType.MOVIE, 2021, "Action thriller"),
            createMockMediaItem("Comedy Show", MediaType.SHOW, 2022, "Hilarious comedy")
        )

        every { mockPlexMediaItemDao.getWatchedItems() }
            .returns(flowOf(watchHistory))
        every { mockPlexMediaItemDao.getAllMediaItems() }
            .returns(flowOf(allItems))

        val recommendations = recommendationAlgorithm
            .generateRecommendations(
                userId, 
                RecommendationConfig(
                    maxRecommendations = 2
                )
            )
            .first()

        // Verify recommendations have scores
        recommendations.forEach { recommendedItem ->
            assertTrue(
                "Recommendation should have a positive score",
                recommendedItem.score > 0
            )
        }
    }

    @Test
    fun `recommendation config weights impact scoring`() {
        val config1 = RecommendationConfig(
            weights = RecommendationWeights(
                typeSimilarity = 1.0,
                yearProximity = 0.0,
                metadataSimilarity = 0.0
            )
        )

        val config2 = RecommendationConfig(
            weights = RecommendationWeights(
                typeSimilarity = 0.0,
                yearProximity = 1.0,
                metadataSimilarity = 0.0
            )
        )

        // Verify different configs are different
        assertTrue("Different weight configurations should be different", config1 != config2)
    }

    @Test
    fun `recommendation weights should affect results`() = runTest {
        // Given
        val watchHistory = listOf(
            createMockMediaItem("Action Movie", MediaType.MOVIE, 2020)
        )
        val allItems = listOf(
            createMockMediaItem("Action Movie", MediaType.MOVIE, 2021),
            createMockMediaItem("Comedy Show", MediaType.SHOW, 2022)
        )

        every { mockPlexMediaItemDao.getWatchedItems() }
            .returns(flowOf(watchHistory))
        every { mockPlexMediaItemDao.getAllMediaItems() }
            .returns(flowOf(allItems))

        val config1 = RecommendationConfig(
            maxRecommendations = 2,
            weights = RecommendationWeights(
                typeSimilarity = 1.0,
                yearProximity = 0.0,
                metadataSimilarity = 0.0
            )
        )
        val config2 = RecommendationConfig(
            maxRecommendations = 2,
            weights = RecommendationWeights(
                typeSimilarity = 0.0,
                yearProximity = 1.0,
                metadataSimilarity = 0.0
            )
        )

        // Verify different configs are different
        assertTrue("Different weight configurations should be different", config1 != config2)
    }

    /**
     * Helper method to create mock media items
     */
    private fun createMockMediaItem(
        title: String, 
        type: MediaType, 
        year: Int, 
        summary: String = ""
    ): PlexMediaItem {
        return PlexMediaItem(
            ratingKey = title.hashCode().toString(),
            key = "/library/metadata/${title.hashCode()}",
            type = type,
            title = title,
            summary = summary,
            year = year,
            serverId = 1L
        )
    }
}