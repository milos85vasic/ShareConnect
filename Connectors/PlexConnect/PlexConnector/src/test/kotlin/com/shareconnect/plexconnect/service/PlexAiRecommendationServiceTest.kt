package com.shareconnect.plexconnect.service

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

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
    fun `enhance media items with AI analysis`() = runBlocking {
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
            assertEquals(2, items.size, "Should enhance all media items")
            
            items.forEach { enhancedItem ->
                assertNotNull(enhancedItem.originalItem, "Original item should not be null")
                assertTrue(
                    enhancedItem.semanticEmbedding.isNotEmpty(), 
                    "Semantic embedding should not be empty"
                )
                assertTrue(
                    enhancedItem.semanticEmbedding.size == 768, 
                    "Embedding should have 768 dimensions"
                )
                assertEquals("en", enhancedItem.semanticLanguage, "Default language should be English")
            }
        }
    }

    @Test
    fun `find similar media using semantic similarity`() = runBlocking {
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
        assertTrue(similarItems.isNotEmpty(), "Should find similar media items")
        
        // Check that sci-fi movies have higher similarity
        val scifiItems = similarItems.filter { 
            it.mediaItem.summary.orEmpty().contains("science") || 
            it.mediaItem.summary.orEmpty().contains("space")
        }
        
        assertTrue(
            scifiItems.isNotEmpty(), 
            "Should identify science fiction movies as similar to Interstellar"
        )
        
        // Verify threshold filtering
        similarItems.forEach { result ->
            assertTrue(
                result.similarity >= 0.5, 
                "All results should meet similarity threshold"
            )
            assertTrue(
                result.isAboveThreshold, 
                "Threshold flag should be set correctly"
            )
        }
    }

    @Test
    fun `cross-lingual recommendations work correctly`() = runBlocking {
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
        assertEquals(2, crossLingualItems.size, "Should process all items")
        
        crossLingualItems.forEach { enhancedItem ->
            assertEquals("en", enhancedItem.semanticLanguage, "Target language should be English")
            assertEquals(
                AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED, 
                enhancedItem.embeddingSource, 
                "Source should be TRANSFORMED for cross-lingual embeddings"
            )
            assertTrue(
                enhancedItem.semanticEmbedding.isNotEmpty(), 
                "Cross-lingual embedding should not be empty"
            )
        }
    }

    @Test
    fun `error handling for invalid media items`() = runBlocking {
        val invalidItems = listOf(
            createTestMediaItem(title = "", summary = ""),
            createTestMediaItem(title = null, summary = null)
        )

        val enhancedItems = aiService.getEnhancedMediaItems(flowOf(invalidItems))

        enhancedItems.collect { items ->
            items.forEach { enhancedItem ->
                assertNotNull(enhancedItem.originalItem, "Original item should be preserved")
                
                // Check error handling
                if (enhancedItem.originalItem.title.isNullOrEmpty()) {
                    assertEquals(
                        AdvancedSemanticEmbedding.EmbeddingSource.ERROR, 
                        enhancedItem.embeddingSource, 
                        "Invalid items should have ERROR source"
                    )
                    assertNotNull(enhancedItem.analysisError, "Should have analysis error message")
                }
            }
        }
    }

    @Test
    fun `media type context enhances embeddings correctly`() = runBlocking {
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
            movieEmbeddingSum != showEmbeddingSum, 
            "Different media types should produce different embeddings"
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
            title = title,
            summary = summary,
            type = type?.value,
            year = 2023
        )
    }
}