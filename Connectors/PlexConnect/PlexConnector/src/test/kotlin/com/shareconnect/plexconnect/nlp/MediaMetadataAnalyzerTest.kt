package com.shareconnect.plexconnect.nlp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class MediaMetadataAnalyzerTest {

    private lateinit var analyzer: MediaMetadataAnalyzer
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        analyzer = MediaMetadataAnalyzer(context)
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `metadata analysis handles various input scenarios`() = runBlocking {
        // Test scenarios with different media items
        val testScenarios = listOf(
            createTestMediaItem(
                title = "Inception", 
                summary = "A mind-bending science fiction thriller about dream manipulation."
            ),
            createTestMediaItem(
                title = "The Shawshank Redemption", 
                summary = "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency."
            ),
            createTestMediaItem(
                title = "", 
                summary = ""
            )
        )

        testScenarios.forEach { mediaItem ->
            val analysisResult = analyzer.analyzeMetadata(mediaItem)

            // Validate title analysis
            assertNotNull(analysisResult.title, "Title analysis should not be null")
            
            // Validate summary analysis
            assertNotNull(analysisResult.summary, "Summary analysis should not be null")
            
            // Check genre prediction
            assertTrue(analysisResult.genres.size <= 3, "Should return max 3 genres")
            
            // Sentiment score validation
            assertTrue(
                analysisResult.sentimentScore in 0.0..1.0, 
                "Sentiment score should be between 0 and 1"
            )
            
            // Semantic embedding validation
            assertTrue(
                analysisResult.semanticEmbedding.isNotEmpty(), 
                "Semantic embedding should not be empty"
            )
        }
    }

    @Test
    fun `title analysis captures key characteristics`() = runBlocking {
        val mediaItem = createTestMediaItem(
            title = "The Dark Knight Rises!", 
            summary = "Batman faces his greatest challenge yet."
        )

        val analysisResult = analyzer.analyzeMetadata(mediaItem)
        val titleAnalysis = analysisResult.title

        assertEquals("the dark knight rises!", titleAnalysis.normalizedTitle, "Title should be normalized")
        assertTrue(titleAnalysis.wordCount > 0, "Word count should be positive")
        assertTrue(titleAnalysis.containsSpecialChars, "Should detect special characters")
    }

    @Test
    fun `summary analysis provides comprehensive insights`() = runBlocking {
        val mediaItem = createTestMediaItem(
            title = "Pulp Fiction", 
            summary = "The lives of two mob hitmen, a boxer, a gangster and his wife, and a pair of diner bandits intertwine in four tales of violence and redemption."
        )

        val analysisResult = analyzer.analyzeMetadata(mediaItem)
        val summaryAnalysis = analysisResult.summary

        assertTrue(summaryAnalysis.wordCount > 0, "Summary word count should be positive")
        assertTrue(summaryAnalysis.sentenceCount > 0, "Should detect multiple sentences")
        assertTrue(summaryAnalysis.averageWordLength > 0, "Average word length should be positive")
    }

    @Test
    fun `genre prediction handles different content types`() = runBlocking {
        val testScenarios = listOf(
            createTestMediaItem(
                title = "Interstellar", 
                summary = "A team of explorers travel through a wormhole in space in an attempt to ensure humanity's survival."
            ),
            createTestMediaItem(
                title = "The Crown", 
                summary = "Follows the political rivalries and romance of Queen Elizabeth II's reign and the events that shaped the second half of the 20th century."
            )
        )

        testScenarios.forEach { mediaItem ->
            val analysisResult = analyzer.analyzeMetadata(mediaItem)
            
            // Verify genre predictions
            assertTrue(
                analysisResult.genres.isNotEmpty(), 
                "Should predict at least one genre"
            )
            assertTrue(
                analysisResult.genres.size <= 3, 
                "Should not exceed 3 genres"
            )
        }
    }

    /**
     * Helper method to create test media items
     */
    private fun createTestMediaItem(
        title: String, 
        summary: String
    ): PlexMediaItem {
        return PlexMediaItem(
            ratingKey = "test_${title.hashCode()}",
            title = title,
            summary = summary,
            type = "MOVIE",
            year = 2023
        )
    }
}