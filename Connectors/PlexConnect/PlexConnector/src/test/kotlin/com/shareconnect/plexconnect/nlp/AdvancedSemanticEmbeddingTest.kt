/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in Software without restriction, including without limitation the rights
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

package com.shareconnect.plexconnect.nlp

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue

@RunWith(AndroidJUnit4::class)
class AdvancedSemanticEmbeddingTest {

    private lateinit var embeddingGenerator: AdvancedSemanticEmbedding
    private lateinit var context: Context

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        embeddingGenerator = AdvancedSemanticEmbedding(context)
    }

    @Test
    fun `embedding generation should return valid result`() = runTest {
        // Given
        val text = "Test movie content"
        
        // When
        val result = embeddingGenerator.generateEmbedding(text)
        
        // Then - stub should return predictable result
        assertNotNull(result)
        assertEquals(768, result.vector.size) // Standard embedding size from stub
        assertEquals(AdvancedSemanticEmbedding.EmbeddingSource.GENERATED, result.source)
        assertEquals(0.5f, result.confidence)
    }

    @Test
    fun `similarity calculation should work`() = runTest {
        // Given
        val embedding1 = FloatArray(768) { 0.1f }
        val embedding2 = FloatArray(768) { 0.2f }
        
        // When
        val similarity = embeddingGenerator.calculateSemanticSimilarity(embedding1, embedding2)
        
        // Then
        assertTrue("Similarity should be non-negative", similarity >= 0.0)
        assertTrue("Similarity should be at most 1.0", similarity <= 1.0)
        assertEquals("Stub should return predictable similarity", 0.5, similarity, 0.001)
    }

    @Test
    fun `cross-lingual embedding generation should work`() = runTest {
        // Given
        val text = "Test content"
        val sourceLang = "en"
        val targetLang = "es"
        
        // When
        val result = embeddingGenerator.generateCrossLingualEmbedding(text, sourceLang, targetLang)
        
        // Then
        assertNotNull(result)
        assertEquals("Embedding should have standard size", 768, result.vector.size)
        assertEquals(AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMED, result.source)
        assertEquals(0.3f, result.confidence, 0.001f)
        assertEquals(sourceLang, result.metadata["source_lang"])
        assertEquals(targetLang, result.metadata["target_lang"])
    }

    @Test
    fun `stub analyzer should return null`() = runTest {
        // Given
        val analyzer = AdvancedSemanticEmbedding.StubAnalyzer()
        val text = "Test text"
        
        // When
        val result = analyzer.analyze(text)
        
        // Then
        assertEquals(null, result, "Stub analyzer should return null")
    }

    @Test
    fun `stub analyzer should generate embedding`() = runTest {
        // Given
        val analyzer = AdvancedSemanticEmbedding.StubAnalyzer()
        val text = "Test text"
        
        // When
        val result = analyzer.generateEmbedding(text)
        
        // Then
        assertNotNull(result)
        assertEquals(768, result.vector.size)
        assertEquals(AdvancedSemanticEmbedding.EmbeddingSource.GENERATED, result.source)
        assertEquals(0.5f, result.confidence)
    }

    @Test
    fun `embedding entity conversion should work`() {
        // Given
        val mediaKey = "test_123"
        val embeddingData = AdvancedSemanticEmbedding.EmbeddingData(
            vector = FloatArray(768) { 0.5f },
            source = AdvancedSemanticEmbedding.EmbeddingSource.TITLE,
            confidence = 0.8f,
            metadata = mapOf("test" to "value")
        )
        val mediaType = "movie"
        
        // When
        val entity = embeddingGenerator.toEmbeddingEntity(mediaKey, embeddingData, mediaType)
        
        // Then
        assertNotNull(entity)
        assertEquals(mediaKey, entity.mediaRatingKey)
        assertEquals("en", entity.language)
        assertEquals(embeddingData.source, entity.embeddingSource)
        assertEquals(embeddingData.confidence, entity.qualityScore)
    }
}