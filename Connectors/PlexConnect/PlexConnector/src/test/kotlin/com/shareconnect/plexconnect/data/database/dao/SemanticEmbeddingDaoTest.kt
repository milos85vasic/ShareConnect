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


package com.shareconnect.plexconnect.data.database.dao

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.plexconnect.data.database.PlexDatabase
import com.shareconnect.plexconnect.data.database.entity.SemanticEmbeddingEntity
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.MediaType
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import kotlin.random.Random

@RunWith(AndroidJUnit4::class)
class SemanticEmbeddingDaoTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var database: PlexDatabase
    private lateinit var dao: SemanticEmbeddingDao

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PlexDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.semanticEmbeddingDao()
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertAndGetEmbedding() = runTest {
        // Given - Create parent media item first to satisfy foreign key constraint
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_1")

        // When
        val insertedId = dao.insertEmbedding(embedding)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertNotNull(insertedId)
        assertNotNull(retrieved)
        assertEquals("test_key_1", retrieved!!.mediaRatingKey)
        assertEquals(768, retrieved.getEmbeddingAsFloatArray().size)
        assertEquals("en", retrieved.language)
        assertEquals(AdvancedSemanticEmbedding.EmbeddingSource.UNIVERSAL_SENTENCE_ENCODER, retrieved.embeddingSource)
    }

    @Test
    fun getEmbeddingFlow() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_2")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_2")

        // When
        dao.insertEmbedding(embedding)
        val flow = dao.getEmbeddingFlowForMedia("test_key_2")
        val retrieved = flow.first()

        // Then
        assertNotNull(retrieved)
        assertEquals("test_key_2", retrieved!!.mediaRatingKey)
    }

    @Test
    fun getEmbeddingsForMediaList() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2"),
            createTestMediaItem("test_key_3")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2"),
            createTestEmbedding("test_key_3")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val retrieved = dao.getEmbeddingsForMedia(listOf("test_key_1", "test_key_3"))

        // Then
        assertEquals(2, retrieved.size)
        assertTrue(retrieved.map { it.mediaRatingKey }.containsAll(listOf("test_key_1", "test_key_3")))
    }

    @Test
    fun getAllEmbeddings() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val flow = dao.getAllEmbeddings()
        val retrieved = flow.first()

        // Then
        assertEquals(2, retrieved.size)
        assertTrue(retrieved.map { it.mediaRatingKey }.containsAll(listOf("test_key_1", "test_key_2")))
    }

    @Test
    fun getEmbeddingsBySource() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val transformerEmbedding = createTestEmbedding("test_key_1", AdvancedSemanticEmbedding.EmbeddingSource.UNIVERSAL_SENTENCE_ENCODER)
        val errorEmbedding = createTestEmbedding("test_key_2", AdvancedSemanticEmbedding.EmbeddingSource.ERROR)
        dao.insertEmbeddings(listOf(transformerEmbedding, errorEmbedding))

        // When
        val retrieved = dao.getEmbeddingsBySource(AdvancedSemanticEmbedding.EmbeddingSource.UNIVERSAL_SENTENCE_ENCODER)

        // Then
        assertEquals(1, retrieved.size)
        assertEquals("test_key_1", retrieved[0].mediaRatingKey)
        assertEquals(AdvancedSemanticEmbedding.EmbeddingSource.UNIVERSAL_SENTENCE_ENCODER, retrieved[0].embeddingSource)
    }

    @Test
    fun getEmbeddingsByLanguage() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val englishEmbedding = createTestEmbedding("test_key_1", language = "en")
        val spanishEmbedding = createTestEmbedding("test_key_2", language = "es")
        dao.insertEmbeddings(listOf(englishEmbedding, spanishEmbedding))

        // When
        val retrieved = dao.getEmbeddingsByLanguage("en")

        // Then
        assertEquals(1, retrieved.size)
        assertEquals("test_key_1", retrieved[0].mediaRatingKey)
        assertEquals("en", retrieved[0].language)
    }

    @Test
    fun getHighQualityEmbeddings() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_1", qualityScore = 0.9f)
        dao.insertEmbedding(embedding)

        // When
        val retrieved = dao.getHighQualityEmbeddings(0.8f)

        // Then
        assertEquals(1, retrieved.size)
        assertEquals("test_key_1", retrieved[0].mediaRatingKey)
        assertEquals(0.9f, retrieved[0].qualityScore)
    }

    @Test
    fun updateEmbedding() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val originalEmbedding = createTestEmbedding("test_key_1", qualityScore = 0.5f)
        val insertedId = dao.insertEmbedding(originalEmbedding)
        val updatedEmbedding = originalEmbedding.copy(id = insertedId, qualityScore = 0.9f, updatedAt = System.currentTimeMillis())

        // When
        dao.updateEmbedding(updatedEmbedding)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertNotNull(retrieved)
        assertEquals(0.9f, retrieved!!.qualityScore)
        assertTrue(retrieved.updatedAt != originalEmbedding.updatedAt)
    }

    @Test
    fun markForRefresh() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_1", needsRefresh = false)
        dao.insertEmbedding(embedding)
        val timestamp = System.currentTimeMillis()

        // When
        dao.markForRefresh("test_key_1", timestamp)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertNotNull(retrieved)
        assertEquals(true, retrieved!!.needsRefresh)
        assertEquals(timestamp, retrieved.updatedAt)
    }

    @Test
    fun updateQualityScore() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_1", qualityScore = 0.5f)
        dao.insertEmbedding(embedding)
        val timestamp = System.currentTimeMillis()

        // When
        dao.updateQualityScore("test_key_1", 0.8f, timestamp)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertNotNull(retrieved)
        assertEquals(0.8f, retrieved!!.qualityScore)
        assertEquals(timestamp, retrieved.updatedAt)
    }

    @Test
    fun deleteEmbeddingForMedia() = runTest {
        // Given - Create parent media item first
        val mediaItem = createTestMediaItem("test_key_1")
        database.plexMediaItemDao().insertMediaItem(mediaItem)
        
        val embedding = createTestEmbedding("test_key_1")
        dao.insertEmbedding(embedding)

        // When
        dao.deleteEmbeddingForMedia("test_key_1")
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertNull(retrieved)
    }

    @Test
    fun getEmbeddingStatistics() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2"),
            createTestMediaItem("test_key_3")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val embeddings = listOf(
            createTestEmbedding("test_key_1", qualityScore = 0.9f),
            createTestEmbedding("test_key_2", qualityScore = 0.7f),
            createTestEmbedding("test_key_3", qualityScore = 0.5f, needsRefresh = true)
        )
        dao.insertEmbeddings(embeddings)

        // When
        val stats = dao.getEmbeddingStatistics()

        // Then
        assertEquals(3, stats.total)
        assertEquals(0.7f, stats.avgQuality)
        assertEquals(0.5f, stats.minQuality)
        assertEquals(0.9f, stats.maxQuality)
        assertEquals(1, stats.refreshNeeded)
    }

    @Test
    fun getEmbeddingCount() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val count = dao.getEmbeddingCount()

        // Then
        assertEquals(2, count)
    }

    @Test
    fun getRefreshNeededCount() = runTest {
        // Given - Create parent media items first
        val mediaItems = listOf(
            createTestMediaItem("test_key_1"),
            createTestMediaItem("test_key_2"),
            createTestMediaItem("test_key_3")
        )
        mediaItems.forEach { database.plexMediaItemDao().insertMediaItem(it) }
        
        val embeddings = listOf(
            createTestEmbedding("test_key_1", needsRefresh = false),
            createTestEmbedding("test_key_2", needsRefresh = true),
            createTestEmbedding("test_key_3", needsRefresh = true)
        )
        dao.insertEmbeddings(embeddings)

        // When
        val count = dao.getRefreshNeededCount()

        // Then
        assertEquals(2, count)
    }

    private fun createTestEmbedding(
        ratingKey: String,
        source: AdvancedSemanticEmbedding.EmbeddingSource = AdvancedSemanticEmbedding.EmbeddingSource.UNIVERSAL_SENTENCE_ENCODER,
        language: String = "en",
        qualityScore: Float = 1.0f,
        needsRefresh: Boolean = false
    ): SemanticEmbeddingEntity {
        val floatArray = FloatArray(768) { Random.Default.nextFloat() }
        return SemanticEmbeddingEntity.fromFloatArray(
            mediaRatingKey = ratingKey,
            embedding = floatArray,
            language = language,
            embeddingSource = source,
            modelVersion = "1.0.0",
            contentHash = "hash_$ratingKey",
            qualityScore = qualityScore,
            needsRefresh = needsRefresh
        )
    }
    
    private fun createTestMediaItem(
        ratingKey: String,
        title: String = "Test Media $ratingKey",
        type: MediaType = MediaType.MOVIE
    ): PlexMediaItem {
        return PlexMediaItem(
            ratingKey = ratingKey,
            key = ratingKey,
            type = type,
            title = title,
            librarySectionTitle = "Test Library",
            librarySectionID = 1L,
            librarySectionKey = "1",
            summary = "Test summary for $title",
            addedAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            year = 2024,
            rating = 8.5f,
            duration = 120000L, // 2 minutes in milliseconds
            art = "https://example.com/art.jpg",
            thumb = "https://example.com/thumb.jpg",
            serverId = 1L
        )
    }
}