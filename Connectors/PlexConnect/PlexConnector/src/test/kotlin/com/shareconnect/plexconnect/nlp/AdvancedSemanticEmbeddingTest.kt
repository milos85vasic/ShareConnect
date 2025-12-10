package com.shareconnect.plexconnect.nlp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

@RunWith(AndroidJUnit4::class)
class AdvancedSemanticEmbeddingTest {

    private lateinit var embeddingGenerator: AdvancedSemanticEmbedding
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        embeddingGenerator = AdvancedSemanticEmbedding(context)
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `embedding generation handles various input scenarios`() = runBlocking {
        val testScenarios = listOf(
            "A thrilling science fiction movie about space exploration",
            "A dramatic tale of human resilience and hope",
            "An action-packed adventure with unexpected twists",
            "" // Edge case: empty string
        )

        testScenarios.forEach { text ->
            val embeddingResult = embeddingGenerator.generateEmbedding(text)

            // Validate embedding generation
            assertTrue(embeddingResult.embedding.isNotEmpty(), "Embedding should not be empty")
            assertTrue(
                embeddingResult.embedding.size == 768, 
                "Embedding should have 768 dimensions"
            )
            
            // Check embedding source
            assertTrue(
                embeddingResult.source in listOf(
                    AdvancedSemanticEmbedding.EmbeddingSource.GENERATED,
                    AdvancedSemanticEmbedding.EmbeddingSource.CACHED
                ),
                "Embedding source should be valid"
            )
        }
    }

    @Test
    fun `semantic similarity calculation works correctly`() {
        // Prepare test embeddings
        val embedding1 = floatArrayOf(1f, 0f, 1f)
        val embedding2 = floatArrayOf(1f, 0f, 1f)
        val embedding3 = floatArrayOf(0f, 1f, 0f)

        // Calculate similarities
        val similarity12 = embeddingGenerator.calculateSemanticSimilarity(embedding1, embedding2)
        val similarity13 = embeddingGenerator.calculateSemanticSimilarity(embedding1, embedding3)

        // Validate similarity calculations
        assertTrue(similarity12 > 0.99, "Identical embeddings should have near-perfect similarity")
        assertTrue(similarity13 < 0.1, "Different embeddings should have low similarity")
    }

    @Test
    fun `caching mechanism works as expected`() = runBlocking {
        val text = "A complex narrative exploring human emotions"

        // First embedding generation
        val firstEmbedding = embeddingGenerator.generateEmbedding(text)

        // Second embedding generation (should be cached)
        val secondEmbedding = embeddingGenerator.generateEmbedding(text)

        // Validate caching
        assertEquals(
            firstEmbedding.embedding.contentToString(), 
            secondEmbedding.embedding.contentToString(), 
            "Cached embeddings should be identical"
        )
        assertEquals(
            secondEmbedding.source, 
            AdvancedSemanticEmbedding.EmbeddingSource.CACHED, 
            "Second embedding should come from cache"
        )
    }

    @Test
    fun `advanced context-based embedding enhancement`() = runBlocking {
        val text = "An epic journey of discovery"

        // Embedding with multiple context dimensions
        val movieSciFiContext = mapOf(
            "media_type" to "MOVIE", 
            "genre" to "SCIFI", 
            "semantic_intensity" to 1.2f
        )
        val movieSciFiEmbedding = embeddingGenerator.generateEmbedding(
            text, 
            movieSciFiContext
        )

        val dramaDocumentaryContext = mapOf(
            "media_type" to "DOCUMENTARY", 
            "genre" to "DRAMA", 
            "semantic_intensity" to 0.8f
        )
        val dramaDocumentaryEmbedding = embeddingGenerator.generateEmbedding(
            text, 
            dramaDocumentaryContext
        )

        // Validate multi-dimensional context-based differences
        assertNotEquals(
            movieSciFiEmbedding.embedding.contentToString(), 
            dramaDocumentaryEmbedding.embedding.contentToString(), 
            "Different multi-dimensional contexts should produce different embeddings"
        )

        // Validate semantic intensity impact
        assertTrue(
            movieSciFiEmbedding.embedding.average() > dramaDocumentaryEmbedding.embedding.average(),
            "Higher semantic intensity should amplify embedding values"
        )
    }

    @Test
    fun `tokenization handles complex input scenarios`() {
        val testCases = listOf(
            "machine learning",
            "artificial intelligence",
            "quantum computing",
            "natural language processing"
        )

        testCases.forEach { text ->
            val tokens = embeddingGenerator.tokenizer.tokenize(text)
            
            assertTrue(tokens.isNotEmpty(), "Tokenization should not produce empty list")
            assertTrue(tokens.all { it.isNotBlank() }, "All tokens should be non-blank")
            
            // Check subword tokenization
            assertTrue(
                tokens.any { it.length < text.length },
                "Complex words should be tokenized into subwords"
            )
        }
    }

    @Test
    fun `embedding normalization maintains vector properties`() = runBlocking {
        val text = "A profound exploration of human complexity"

        val embeddingResult = embeddingGenerator.generateEmbedding(text)
        val embedding = embeddingResult.embedding

        // Validate normalization
        val magnitude = Math.sqrt(embedding.map { it * it }.sum().toDouble())
        
        assertTrue(
            magnitude.toFloat() in 0.99f..1.01f, 
            "Normalized embedding should have unit magnitude"
        )
    }

    @Test
    fun `handles long and complex input texts`() = runBlocking {
        val longText = """
            In the vast expanse of human knowledge and technological innovation, 
            we find ourselves at the crossroads of unprecedented discovery. 
            The intricate tapestry of scientific understanding weaves together 
            complex narratives of exploration, pushing the boundaries of 
            what we once thought impossible.
        """.trimIndent()

        val embeddingResult = embeddingGenerator.generateEmbedding(longText)

        // Validate embedding for long text
        assertTrue(
            embeddingResult.embedding.isNotEmpty(), 
            "Long text should generate a valid embedding"
        )
        assertTrue(
            embeddingResult.embedding.size == 768, 
            "Embedding should maintain consistent dimensionality"
        )
    }

    @Test
    fun `multilingual language detection works correctly`() {
        val testCases = listOf(
            "Hello world" to "en",
            "你好世界" to "zh",
            "こんにちは世界" to "ja",
            "안녕하세요 세계" to "ko",
            "مرحبا بالعالم" to "ar",
            "नमस्ते दुनिया" to "hi"
        )

        testCases.forEach { (text, expectedLanguage) ->
            val detectedLanguage = embeddingGenerator.tokenizer.detectLanguage(text)
            assertEquals(
                expectedLanguage, 
                detectedLanguage, 
                "Language detection failed for text: $text"
            )
        }
    }

    @Test
    fun `cross-lingual embedding transformation works`() = runBlocking {
        val testTexts = listOf(
            "Hello world" to "en",
            "你好世界" to "zh",
            "こんにちは世界" to "ja"
        )

        testTexts.forEach { (text, sourceLanguage) ->
            // Generate original embedding
            val originalEmbedding = embeddingGenerator.generateEmbedding(text)
            
            // Transform to English
            val transformedEmbedding = embeddingGenerator.generateCrossLingualEmbedding(
                text, 
                "en"
            )

            // Validate transformation
            assertEquals(
                "en", 
                transformedEmbedding.language, 
                "Transformed embedding should be in English"
            )
            assertEquals(
                AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED, 
                transformedEmbedding.source, 
                "Embedding source should be TRANSFORMED"
            )
            assertTrue(
                transformedEmbedding.embedding.size == 768, 
                "Transformed embedding should maintain dimensionality"
            )
        }
    }

    @Test
    fun `multilingual tokenization handles different language scripts`() {
        val testCases = listOf(
            "Hello world" to "en",
            "你好世界" to "zh",
            "こんにちは世界" to "ja",
            "안녕하세요 세계" to "ko",
            "مرحبا بالعالم" to "ar",
            "नमस्ते दुनिया" to "hi"
        )

        testCases.forEach { (text, language) ->
            val tokens = embeddingGenerator.tokenizer.tokenize(text)
            
            assertTrue(
                tokens.isNotEmpty(), 
                "Tokenization should not produce empty list for $language text"
            )
            
            when (language) {
                "en" -> assertTrue(
                    tokens.any { it.length > 1 }, 
                    "English tokenization should preserve word structure"
                )
                "zh" -> assertTrue(
                    tokens.all { it.length == 1 }, 
                    "Chinese tokenization should be character-level"
                )
                "ja" -> assertTrue(
                    tokens.any { it.length > 1 }, 
                    "Japanese tokenization should handle multi-character tokens"
                )
                "ko" -> assertTrue(
                    tokens.any { it.length > 1 }, 
                    "Korean tokenization should handle syllable-level tokens"
                )
                "ar" -> assertTrue(
                    tokens.any { it.length > 1 }, 
                    "Arabic tokenization should preserve word roots"
                )
                "hi" -> assertTrue(
                    tokens.any { it.length > 1 }, 
                    "Hindi tokenization should handle Devanagari script"
                )
            }
        }
    }

    @Test
    fun `language similarity calculation works`() = runBlocking {
        val testCases = listOf(
            "Hello world" to "en",
            "Bonjour le monde" to "fr",
            "你好世界" to "zh"
        )

        val similarities = testCases.mapIndexed { index, (text1, lang1) ->
            testCases.mapIndexed { innerIndex, (text2, lang2) ->
                if (index == innerIndex) return@mapIndexed null
                
                val embedding1 = embeddingGenerator.generateEmbedding(text1)
                val embedding2 = embeddingGenerator.generateEmbedding(text2)
                
                embeddingGenerator.multilingualTransformer.calculateLanguageSimilarity(
                    embedding1.embedding, 
                    embedding2.embedding, 
                    lang1, 
                    lang2
                )
            }.filterNotNull()
        }.flatten()

        // Validate similarity distribution
        assertTrue(
            similarities.all { it in -1.0..1.0 }, 
            "Language similarities should be between -1 and 1"
        )
        
        // Similar languages should have higher similarity
        val similarityWithEnglish = testCases
            .filter { it.second != "en" }
            .map { (text, lang) ->
                val enEmbedding = embeddingGenerator.generateEmbedding("Hello world")
                val textEmbedding = embeddingGenerator.generateEmbedding(text)
                embeddingGenerator.multilingualTransformer.calculateLanguageSimilarity(
                    enEmbedding.embedding, 
                    textEmbedding.embedding, 
                    "en", 
                    lang
                )
            }

        assertTrue(
            similarityWithEnglish.all { it > 0.5 }, 
            "Similarities with English should be relatively high"
        )
    }
}