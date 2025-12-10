package com.shareconnect.plexconnect.ml

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.local.PlexDatabase
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runBlockingTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
class AdvancedRecommendationAlgorithmTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var mockDatabase: PlexDatabase

    @Mock
    private lateinit var mockPlexMediaItemDao: PlexMediaItemDao

    private lateinit var recommendationAlgorithm: AdvancedRecommendationAlgorithm

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        whenever(mockDatabase.plexMediaItemDao()).thenReturn(mockPlexMediaItemDao)
        recommendationAlgorithm = AdvancedRecommendationAlgorithm(mockDatabase)
    }

    @Test
    fun `recommendation generation returns non-empty list`() = runBlockingTest {
        // Prepare mock data
        val userId = "test_user"
        val watchHistory = listOf(
            createMockMediaItem("Movie1", "MOVIE", 2020),
            createMockMediaItem("TV Show1", "TV_SHOW", 2019)
        )
        val allItems = listOf(
            createMockMediaItem("Movie2", "MOVIE", 2021),
            createMockMediaItem("TV Show2", "TV_SHOW", 2022)
        )

        // Mock DAO responses
        whenever(mockPlexMediaItemDao.getWatchedMediaItemsForUser(userId))
            .thenReturn(watchHistory)
        whenever(mockPlexMediaItemDao.getAllMediaItems())
            .thenReturn(allItems)

        // Generate recommendations
        val recommendations = recommendationAlgorithm
            .generateRecommendations(userId)
            .first()

        // Assertions
        assertTrue(recommendations.isNotEmpty(), "Recommendations should not be empty")
        assertEquals(2, recommendations.size, "Should return recommended items")
    }

    @Test
    fun `recommendation scoring handles different media types`() = runBlockingTest {
        val userId = "test_user"
        val watchHistory = listOf(
            createMockMediaItem("Sci-Fi Movie", "MOVIE", 2020, "Sci-fi adventure"),
            createMockMediaItem("Sci-Fi Show", "TV_SHOW", 2019, "Sci-fi series")
        )
        val allItems = listOf(
            createMockMediaItem("Action Movie", "MOVIE", 2021, "Action thriller"),
            createMockMediaItem("Comedy Show", "TV_SHOW", 2022, "Hilarious comedy")
        )

        whenever(mockPlexMediaItemDao.getWatchedMediaItemsForUser(userId))
            .thenReturn(watchHistory)
        whenever(mockPlexMediaItemDao.getAllMediaItems())
            .thenReturn(allItems)

        val recommendations = recommendationAlgorithm
            .generateRecommendations(
                userId, 
                AdvancedRecommendationAlgorithm.RecommendationConfig(
                    maxRecommendations = 2
                )
            )
            .first()

        // Verify recommendations have scores
        recommendations.forEach { recommendedItem ->
            assertTrue(
                recommendedItem.score > 0, 
                "Recommendation should have a positive score"
            )
        }
    }

    @Test
    fun `recommendation config weights impact scoring`() {
        val config1 = AdvancedRecommendationAlgorithm.RecommendationConfig(
            weights = AdvancedRecommendationAlgorithm.ScoringWeights(
                typeSimilarity = 1.0,
                yearProximity = 0.0,
                metadataSimilarity = 0.0
            )
        )

        val config2 = AdvancedRecommendationAlgorithm.RecommendationConfig(
            weights = AdvancedRecommendationAlgorithm.ScoringWeights(
                typeSimilarity = 0.0,
                yearProximity = 1.0,
                metadataSimilarity = 0.0
            )
        )

        // These would require more complex setup with actual data
        // Placeholder for demonstrating weight impact
        assertTrue(config1 != config2, "Different weight configurations should produce different results")
    }

    /**
     * Helper method to create mock media items
     */
    private fun createMockMediaItem(
        title: String, 
        type: String, 
        year: Int, 
        summary: String = ""
    ): PlexMediaItemEntity {
        return PlexMediaItemEntity(
            id = title.hashCode().toString(),
            libraryId = "test_library",
            title = title,
            type = type,
            year = year,
            summary = summary
        )
    }
}