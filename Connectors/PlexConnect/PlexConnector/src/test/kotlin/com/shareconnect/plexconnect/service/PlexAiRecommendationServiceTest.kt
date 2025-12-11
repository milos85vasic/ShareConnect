package com.shareconnect.plexconnect.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class PlexAiRecommendationServiceTest {

    private lateinit var aiService: PlexAiRecommendationService
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        aiService = PlexAiRecommendationService(context)
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `enhance media items with AI analysis`() = runTest {
        val testMediaItems = listOf(
            createTestMediaItem(
                title = "Inception",
                summary = "A mind-bending science fiction thriller about dream manipulation.",
                type = MediaType.MOVIE
            ),
            createTestMediaItem(
                title = "The Matrix",
                summary = "A computer hacker learns about the true nature of reality.",
                type = MediaType.MOVIE
            )
        )

        val enhancedItems = aiService.getEnhancedMediaItems(flowOf(testMediaItems))

        // Collect the flow results
        enhancedItems.collect { items ->
            assertEquals("Should enhance all media items", 2, items.size)
            
            items.forEach { enhancedItem ->
                assertNotNull("Original item should not be null", enhancedItem.originalItem)
                assertTrue(
                    "Semantic embedding should not be empty",
                    enhancedItem.semanticEmbedding.isNotEmpty()
                )
                assertTrue(
                    "Embedding should have 768 dimensions",
                    enhancedItem.semanticEmbedding.size == 768
                )
                assertEquals("Default language should be English", "en", enhancedItem.semanticLanguage)
            }
        }
    }

    @Test
    fun `find similar media using semantic similarity`() = runTest {
        val targetItem = createTestMediaItem(
            title = "Interstellar",
            summary = "A team of explorers travel through a wormhole in space.",
            type = MediaType.MOVIE
        )

        val candidateItems = listOf(
            createTestMediaItem(
                title = "Inception", 
                summary = "A mind-bending science fiction thriller about dream manipulation."
            ),
            createTestMediaItem(
                title = "The Shawshank Redemption", 
                summary = "Two imprisoned men bond over a number of years."
            ),
            createTestMediaItem(
                title = "2001: A Space Odyssey", 
                summary = "A journey through space with artificial intelligence."
            )
        )

        val similarItems = aiService.findSimilarMedia(targetItem, candidateItems, threshold = 0.5)

        // Validate similarity results
        assertTrue("Should find similar media items", similarItems.isNotEmpty())
        
        // Check that sci-fi movies have higher similarity
        val scifiItems = similarItems.filter { 
            it.mediaItem.summary.orEmpty().contains("science") || 
            it.mediaItem.summary.orEmpty().contains("space")
        }
        
        assertTrue(
            "Should identify science fiction movies as similar to Interstellar",
            scifiItems.isNotEmpty()
        )
        
        // Verify threshold filtering
        similarItems.forEach { result ->
            assertTrue(
                "All results should meet similarity threshold",
                result.similarity >= 0.5
            )
            assertTrue(
                "Threshold flag should be set correctly",
                result.isAboveThreshold
            )
        }
    }

    @Test
    fun `cross-lingual recommendations work correctly`() = runTest {
        val testMediaItems = listOf(
            createTestMediaItem(
                title = "The Dark Knight",
                summary = "Batman faces his greatest psychological challenge.",
                type = MediaType.MOVIE
            ),
            createTestMediaItem(
                title = "Pulp Fiction",
                summary = "Interweaving stories of crime and redemption.",
                type = MediaType.MOVIE
            )
        )

        val crossLingualItems = aiService.getCrossLingualRecommendations(testMediaItems, "en")

        // Validate cross-lingual results
        assertEquals("Should process all items", 2, crossLingualItems.size)
        
        crossLingualItems.forEach { enhancedItem ->
            assertEquals("Target language should be English", "en", enhancedItem.semanticLanguage)
            assertEquals(
                "Source should be TRANSFORMED for cross-lingual embeddings",
                AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED,
                enhancedItem.embeddingSource
            )
            assertTrue(
                "Cross-lingual embedding should not be empty",
                enhancedItem.semanticEmbedding.isNotEmpty()
            )
        }
    }

    @Test
    fun `error handling for invalid media items`() = runTest {
        val invalidItems = listOf(
            createTestMediaItem(title = "", summary = ""),
            createTestMediaItem(title = null, summary = null)
        )

        val enhancedItems = aiService.getEnhancedMediaItems(flowOf(invalidItems))

        enhancedItems.collect { items ->
            items.forEach { enhancedItem ->
                assertNotNull("Original item should be preserved", enhancedItem.originalItem)
                
                // Check error handling
                if (enhancedItem.originalItem.title.isNullOrEmpty()) {
                    assertEquals(
                        "Invalid items should have ERROR source",
                        AdvancedSemanticEmbedding.EmbeddingSource.ERROR,
                        enhancedItem.embeddingSource
                    )
                    assertNotNull("Should have analysis error message", enhancedItem.analysisError)
                }
            }
        }
    }

    @Test
    fun `media type context enhances embeddings correctly`() = runTest {
        val movieItem = createTestMediaItem(
            title = "Test Movie",
            summary = "A test movie for analysis.",
            type = MediaType.MOVIE
        )

        val tvShowItem = createTestMediaItem(
            title = "Test Show",
            summary = "A test TV show for analysis.",
            type = MediaType.SHOW
        )

        val enhancedMovie = aiService.enhanceMediaItem(movieItem)
        val enhancedShow = aiService.enhanceMediaItem(tvShowItem)

        // Verify that different media types result in different embeddings
        val movieEmbeddingSum = enhancedMovie.semanticEmbedding.sum()
        val showEmbeddingSum = enhancedShow.semanticEmbedding.sum()

        assertTrue(
            "Different media types should produce different embeddings",
            movieEmbeddingSum != showEmbeddingSum
        )
    }

    /**
     * Helper method to create test media items
     */
    private fun createTestMediaItem(
        title: String?, 
        summary: String?,
        type: MediaType? = MediaType.MOVIE
    ): PlexMediaItem {
        return PlexMediaItem(
            ratingKey = "test_${title.hashCode()}",
            key = "/library/metadata/${title.hashCode()}",
            title = title ?: "",
            summary = summary,
            type = type ?: MediaType.MOVIE,
            year = 2023,
            serverId = 1L
        )
    }
}