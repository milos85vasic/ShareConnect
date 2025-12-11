package com.shareconnect.plexconnect.nlp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.Modality
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.MockitoAnnotations
import java.io.File
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue
import kotlin.test.assertFalse

/**
 * Extension function for FloatArray to calculate average value
 */
private fun FloatArray.average(): Float {
    return if (isEmpty()) 0f else sum() / size.toFloat()
}

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
    fun `domain-specific embedding adaptation works correctly`() = runBlocking {
        val testTexts = listOf(
            "A thrilling science fiction adventure" to "MOVIE",
            "Breaking news about technological innovation" to "NEWS",
            "A documentary exploring human potential" to "DOCUMENTARY"
        )

        testTexts.forEach { (text, domain) ->
            // Generate base embedding
            val baseEmbedding = embeddingGenerator.generateEmbedding(text)

            // Adapt embedding to specific domain
            val adaptedEmbedding = embeddingGenerator.adaptEmbeddingToDomain(
                baseEmbedding.embedding, 
                domain, 
                baseEmbedding.language
            )

            // Validate adaptation
            assertNotEquals(
                baseEmbedding.embedding.contentToString(), 
                adaptedEmbedding.contentToString(), 
                "Domain adaptation should modify embedding"
            )

            assertTrue(
                adaptedEmbedding.size == 768, 
                "Adapted embedding should maintain dimensionality"
            )
        }
    }

    @Test
    fun `semantic consistency verification works correctly`() = runBlocking {
        val testCases = listOf(
            "A thrilling science fiction movie" to 
            "An exciting adventure in space",
            "Breaking news about technological innovation" to 
            "Latest developments in technology",
            "A documentary exploring human potential" to 
            "Investigating human capabilities"
        )

        testCases.forEach { (text1, text2) ->
            // Generate embeddings
            val embedding1 = embeddingGenerator.generateEmbedding(text1)
            val embedding2 = embeddingGenerator.generateEmbedding(text2)

            // Verify semantic consistency
            val isConsistent = embeddingGenerator.verifySemanticConsistency(
                embedding1.embedding, 
                embedding2.embedding
            )

            assertTrue(
                isConsistent, 
                "Semantically similar texts should have high consistency"
            )
        }
    }

    @Test
    fun `advanced multilingual similarity refinement`() {
        val testCases = listOf(
            Triple("en", "fr", "Hello world" to "Bonjour le monde"),
            Triple("en", "es", "Technology is amazing" to "La tecnología es increíble"),
            Triple("en", "zh", "Innovation drives progress" to "创新推动进步")
        )

        testCases.forEach { (lang1, lang2, textPair) ->
            // Generate embeddings
            val embedding1 = embeddingGenerator.generateEmbedding(textPair.first)
            val embedding2 = embeddingGenerator.generateEmbedding(textPair.second)

            // Calculate language similarity
            val similarity = embeddingGenerator.multilingualTransformer
                .calculateLanguageSimilarity(
                    embedding1.embedding, 
                    embedding2.embedding, 
                    lang1, 
                    lang2
                )

            // Validate similarity
            assertTrue(
                similarity in -1.0..1.0, 
                "Language similarity should be between -1 and 1"
            )

            // Expect higher similarity for linguistically close languages
            when {
                lang1 == "en" && lang2 == "fr" -> {
                    assertTrue(
                        similarity > 0.6, 
                        "English and French should have relatively high similarity"
                    )
                }
                lang1 == "en" && lang2 == "zh" -> {
                    assertTrue(
                        similarity < 0.4, 
                        "English and Chinese should have low similarity"
                    )
                }
            }
        }
    }

    @Test
    fun `cross-modal embedding generation works correctly`() = runBlocking {
        // Prepare test files for different modalities
        val textInput = "A fascinating journey of discovery"
        val imageFile = File.createTempFile("test_image", ".jpg")
        val audioFile = File.createTempFile("test_audio", ".mp3")
        val videoFile = File.createTempFile("test_video", ".mp4")

        try {
            // Generate test files (in a real scenario, these would be actual media files)
            generateTestImage(imageFile)
            generateTestAudio(audioFile)
            generateTestVideo(videoFile)

            // Test embeddings for each modality
            val modalityTests = listOf(
                Pair(textInput, Modality.TEXT),
                Pair(imageFile, Modality.IMAGE),
                Pair(audioFile, Modality.AUDIO),
                Pair(videoFile, Modality.VIDEO)
            )

            modalityTests.forEach { (input, modality) ->
                val embeddingResult = when (modality) {
                    Modality.TEXT -> embeddingGenerator.generateCrossModalEmbedding(input, modality)
                    Modality.IMAGE -> embeddingGenerator.generateCrossModalEmbedding(input as File, modality)
                    Modality.AUDIO -> embeddingGenerator.generateCrossModalEmbedding(input as File, modality)
                    Modality.VIDEO -> embeddingGenerator.generateCrossModalEmbedding(input as File, modality)
                }

                // Validate embedding
                assertTrue(
                    embeddingResult.embedding.isNotEmpty(), 
                    "Embedding generation failed for $modality"
                )
                assertEquals(
                    768, 
                    embeddingResult.embedding.size, 
                    "Embedding should have 768 dimensions"
                )
                assertEquals(
                    AdvancedSemanticEmbedding.EmbeddingSource.CROSS_MODAL, 
                    embeddingResult.source, 
                    "Embedding source should be CROSS_MODAL"
                )
            }
        } finally {
            // Clean up temporary files
            imageFile.delete()
            audioFile.delete()
            videoFile.delete()
        }
    }

    @Test
    fun `cross-modal semantic similarity calculation works`() = runBlocking {
        // Prepare test files
        val textInput1 = "A fascinating journey of scientific discovery"
        val textInput2 = "Exploring the frontiers of human knowledge"
        val imageFile1 = File.createTempFile("test_image1", ".jpg")
        val imageFile2 = File.createTempFile("test_image2", ".jpg")

        try {
            // Generate test files
            generateTestImage(imageFile1)
            generateTestImage(imageFile2)

            // Generate cross-modal embeddings
            val textEmbedding1 = embeddingGenerator.generateCrossModalEmbedding(
                textInput1, 
                Modality.TEXT
            )
            val textEmbedding2 = embeddingGenerator.generateCrossModalEmbedding(
                textInput2, 
                Modality.TEXT
            )
            val imageEmbedding1 = embeddingGenerator.generateCrossModalEmbedding(
                imageFile1, 
                Modality.IMAGE
            )
            val imageEmbedding2 = embeddingGenerator.generateCrossModalEmbedding(
                imageFile2, 
                Modality.IMAGE
            )

            // Calculate cross-modal similarities
            val textSimilarity = embeddingGenerator.calculateCrossModalSimilarity(
                textEmbedding1.embedding, 
                textEmbedding2.embedding, 
                Modality.TEXT, 
                Modality.TEXT
            )
            val imageSimilarity = embeddingGenerator.calculateCrossModalSimilarity(
                imageEmbedding1.embedding, 
                imageEmbedding2.embedding, 
                Modality.IMAGE, 
                Modality.IMAGE
            )
            val crossModalSimilarity = embeddingGenerator.calculateCrossModalSimilarity(
                textEmbedding1.embedding, 
                imageEmbedding1.embedding, 
                Modality.TEXT, 
                Modality.IMAGE
            )

            // Validate similarities
            assertTrue(
                textSimilarity in -1.0..1.0, 
                "Text similarity should be between -1 and 1"
            )
            assertTrue(
                imageSimilarity in -1.0..1.0, 
                "Image similarity should be between -1 and 1"
            )
            assertTrue(
                crossModalSimilarity in -1.0..1.0, 
                "Cross-modal similarity should be between -1 and 1"
            )
        } finally {
            // Clean up temporary files
            imageFile1.delete()
            imageFile2.delete()
        }
    }

    @Test
    fun `error handling for invalid input generates fallback embedding`() = runBlocking {
        // Test scenarios for error handling
        val testCases = listOf(
            "",                 // Empty string
            "  \t\n",           // Whitespace-only input
            "x" * (AdvancedSemanticEmbedding.MAX_SEQUENCE_LENGTH * 3)  // Extremely long input
        )

        testCases.forEach { invalidInput ->
            val fallbackEmbedding = embeddingGenerator.generateEmbedding(invalidInput)

            // Validate fallback embedding
            assertEquals(
                768, 
                fallbackEmbedding.embedding.size, 
                "Fallback embedding should maintain dimensionality"
            )
            assertEquals(
                AdvancedSemanticEmbedding.EmbeddingSource.ERROR, 
                fallbackEmbedding.source, 
                "Invalid input should generate an error source embedding"
            )
            
            // Check that fallback embedding is not all zeros
            assertFalse(
                fallbackEmbedding.embedding.all { it == 0f }, 
                "Fallback embedding should not be all zeros"
            )
        }
    }

    @Test
    fun `model error tracking prevents repeated failures`() = runBlocking {
        // Simulate repeated generation failures
        val failingGenerator = object : AdvancedSemanticEmbedding(context) {
            override suspend fun generateEmbedding(
                text: String, 
                additionalContext: Map<String, Any>
            ): SemanticEmbeddingResult {
                throw RuntimeException("Simulated model failure")
            }
        }

        // Attempt multiple embeddings to trigger circuit breaker
        val embeddings = (1..10).map { 
            failingGenerator.generateEmbedding("Test input $it") 
        }

        // Verify error handling
        assertTrue(
            embeddings.all { it.source == EmbeddingSource.ERROR }, 
            "All embedding generations should result in error embeddings"
        )

        // Check that after MAX_ERROR_THRESHOLD, further attempts are blocked
        val finalEmbedding = failingGenerator.generateEmbedding("Final test")
        assertEquals(
            EmbeddingSource.ERROR, 
            finalEmbedding.source, 
            "System should enter cooldown after repeated failures"
        )
    }

    @Test
    fun `model recovery after cooldown period`() = runBlocking {
        // Simulate a generator that initially fails and then succeeds
        var failureCount = 0
        val recoveryGenerator = object : AdvancedSemanticEmbedding(context) {
            override suspend fun generateEmbedding(
                text: String, 
                additionalContext: Map<String, Any>
            ): SemanticEmbeddingResult {
                failureCount++
                
                // First 5 attempts fail
                if (failureCount <= 5) {
                    throw RuntimeException("Simulated model failure")
                }
                
                // Subsequent attempts succeed
                return super.generateEmbedding(text, additionalContext)
            }
        }

        // Attempt multiple embeddings
        val embeddings = (1..10).map { 
            recoveryGenerator.generateEmbedding("Test input $it") 
        }

        // Verify recovery
        assertTrue(
            embeddings.take(5).all { it.source == EmbeddingSource.ERROR },
            "First 5 attempts should fail"
        )
        assertTrue(
            embeddings.drop(5).all { it.source == EmbeddingSource.GENERATED },
            "Subsequent attempts should succeed after cooldown"
        )
    }

    @Test
    fun `combine embeddings from multiple modalities`() = runBlocking {
        // Prepare test files
        val textInput = "An incredible scientific discovery"
        val imageFile = File.createTempFile("test_image", ".jpg")
        val audioFile = File.createTempFile("test_audio", ".mp3")
        val videoFile = File.createTempFile("test_video", ".mp4")

        try {
            // Generate test files
            generateTestImage(imageFile)
            generateTestAudio(audioFile)
            generateTestVideo(videoFile)

            // Generate cross-modal embeddings
            val textEmbedding = embeddingGenerator.generateCrossModalEmbedding(
                textInput, 
                Modality.TEXT
            )
            val imageEmbedding = embeddingGenerator.generateCrossModalEmbedding(
                imageFile, 
                Modality.IMAGE
            )
            val audioEmbedding = embeddingGenerator.generateCrossModalEmbedding(
                audioFile, 
                Modality.AUDIO
            )
            val videoEmbedding = embeddingGenerator.generateCrossModalEmbedding(
                videoFile, 
                Modality.VIDEO
            )

            // Combine embeddings
            val combinedEmbedding = embeddingGenerator.combineModalEmbeddings(
                listOf(
                    Pair(textEmbedding.embedding, Modality.TEXT),
                    Pair(imageEmbedding.embedding, Modality.IMAGE),
                    Pair(audioEmbedding.embedding, Modality.AUDIO),
                    Pair(videoEmbedding.embedding, Modality.VIDEO)
                )
            )

            // Validate combined embedding
            assertEquals(
                768, 
                combinedEmbedding.size, 
                "Combined embedding should have 768 dimensions"
            )
            assertFalse(
                combinedEmbedding.all { it == 0f }, 
                "Combined embedding should not be all zeros"
            )
        } finally {
            // Clean up temporary files
            imageFile.delete()
            audioFile.delete()
            videoFile.delete()
        }
    }

    // Helper functions to generate test media files
    private fun generateTestImage(file: File) {
        // Create a simple bitmap
        val bitmap = Bitmap.createBitmap(224, 224, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        canvas.drawColor(android.graphics.Color.BLUE)
        
        // Save bitmap to file
        file.outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
        }
    }

    private fun generateTestAudio(file: File) {
        // Create a minimal WAV file header for testing
        file.writeBytes(byteArrayOf(
            'R'.toByte(), 'I'.toByte(), 'F'.toByte(), 'F'.toByte(),
            // File size (placeholder)
            0, 0, 0, 0,
            'W'.toByte(), 'A'.toByte(), 'V'.toByte(), 'E'.toByte()
        ))
    }

    private fun generateTestVideo(file: File) {
        // Create a minimal MP4 file structure for testing
        file.writeBytes(byteArrayOf(
            0, 0, 0, 24,  // Box size
            'f'.toByte(), 't'.toByte(), 'y'.toByte(), 'p'.toByte(),
            'i'.toByte(), 's'.toByte(), 'o'.toByte(), '6'.toByte()
        ))
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