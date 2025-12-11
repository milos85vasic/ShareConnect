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
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import java.util.*

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
        // Given
        val embedding = createTestEmbedding("test_key_1")

        // When
        val insertedId = dao.insertEmbedding(embedding)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertThat(insertedId, not(nullValue))
        assertThat(retrieved, not(nullValue))
        assertThat(retrieved!!.mediaRatingKey, `is`("test_key_1"))
        assertThat(retrieved.embedding.contentLength, `is`(768))
        assertThat(retrieved.language, `is`("en"))
        assertThat(retrieved.embeddingSource, `is`(AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER))
    }

    @Test
    fun getEmbeddingFlow() = runTest {
        // Given
        val embedding = createTestEmbedding("test_key_2")

        // When
        dao.insertEmbedding(embedding)
        val flow = dao.getEmbeddingFlowForMedia("test_key_2")
        val retrieved = flow.first()

        // Then
        assertThat(retrieved, not(nullValue))
        assertThat(retrieved!!.mediaRatingKey, `is`("test_key_2"))
    }

    @Test
    fun getEmbeddingsForMediaList() = runTest {
        // Given
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2"),
            createTestEmbedding("test_key_3")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val retrieved = dao.getEmbeddingsForMedia(listOf("test_key_1", "test_key_3"))

        // Then
        assertThat(retrieved.size, `is`(2))
        assertThat(retrieved.map { it.mediaRatingKey }, hasItems("test_key_1", "test_key_3"))
    }

    @Test
    fun getAllEmbeddings() = runTest {
        // Given
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val flow = dao.getAllEmbeddings()
        val retrieved = flow.first()

        // Then
        assertThat(retrieved.size, `is`(2))
        assertThat(retrieved.map { it.mediaRatingKey }, hasItems("test_key_1", "test_key_2"))
    }

    @Test
    fun getEmbeddingsBySource() = runTest {
        // Given
        val transformerEmbedding = createTestEmbedding("test_key_1", AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER)
        val errorEmbedding = createTestEmbedding("test_key_2", AdvancedSemanticEmbedding.EmbeddingSource.ERROR)
        dao.insertEmbeddings(listOf(transformerEmbedding, errorEmbedding))

        // When
        val retrieved = dao.getEmbeddingsBySource(AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER)

        // Then
        assertThat(retrieved.size, `is`(1))
        assertThat(retrieved[0].mediaRatingKey, `is`("test_key_1"))
        assertThat(retrieved[0].embeddingSource, `is`(AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER))
    }

    @Test
    fun getEmbeddingsByLanguage() = runTest {
        // Given
        val englishEmbedding = createTestEmbedding("test_key_1", language = "en")
        val spanishEmbedding = createTestEmbedding("test_key_2", language = "es")
        dao.insertEmbeddings(listOf(englishEmbedding, spanishEmbedding))

        // When
        val retrieved = dao.getEmbeddingsByLanguage("en")

        // Then
        assertThat(retrieved.size, `is`(1))
        assertThat(retrieved[0].mediaRatingKey, `is`("test_key_1"))
        assertThat(retrieved[0].language, `is`("en"))
    }

    @Test
    fun getHighQualityEmbeddings() = runTest {
        // Given
        val highQualityEmbedding = createTestEmbedding("test_key_1", qualityScore = 0.9f)
        val lowQualityEmbedding = createTestEmbedding("test_key_2", qualityScore = 0.4f)
        dao.insertEmbeddings(listOf(highQualityEmbedding, lowQualityEmbedding))

        // When
        val retrieved = dao.getHighQualityEmbeddings(0.8f)

        // Then
        assertThat(retrieved.size, `is`(1))
        assertThat(retrieved[0].mediaRatingKey, `is`("test_key_1"))
        assertThat(retrieved[0].qualityScore, `is`(0.9f))
    }

    @Test
    fun updateEmbedding() = runTest {
        // Given
        val originalEmbedding = createTestEmbedding("test_key_1", qualityScore = 0.5f)
        val insertedId = dao.insertEmbedding(originalEmbedding)
        val updatedEmbedding = originalEmbedding.copy(id = insertedId, qualityScore = 0.9f, updatedAt = System.currentTimeMillis())

        // When
        dao.updateEmbedding(updatedEmbedding)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertThat(retrieved, not(nullValue))
        assertThat(retrieved!!.qualityScore, `is`(0.9f))
        assertThat(retrieved.updatedAt, not(`is`(originalEmbedding.updatedAt)))
    }

    @Test
    fun markForRefresh() = runTest {
        // Given
        val embedding = createTestEmbedding("test_key_1", needsRefresh = false)
        dao.insertEmbedding(embedding)
        val timestamp = System.currentTimeMillis()

        // When
        dao.markForRefresh("test_key_1", timestamp)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertThat(retrieved, not(nullValue))
        assertThat(retrieved!!.needsRefresh, `is`(true))
        assertThat(retrieved.updatedAt, `is`(timestamp))
    }

    @Test
    fun updateQualityScore() = runTest {
        // Given
        val embedding = createTestEmbedding("test_key_1", qualityScore = 0.5f)
        dao.insertEmbedding(embedding)
        val timestamp = System.currentTimeMillis()

        // When
        dao.updateQualityScore("test_key_1", 0.8f, timestamp)
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertThat(retrieved, not(nullValue))
        assertThat(retrieved!!.qualityScore, `is`(0.8f))
        assertThat(retrieved.updatedAt, `is`(timestamp))
    }

    @Test
    fun deleteEmbeddingForMedia() = runTest {
        // Given
        val embedding = createTestEmbedding("test_key_1")
        dao.insertEmbedding(embedding)

        // When
        dao.deleteEmbeddingForMedia("test_key_1")
        val retrieved = dao.getEmbeddingForMedia("test_key_1")

        // Then
        assertThat(retrieved, nullValue())
    }

    @Test
    fun getEmbeddingStatistics() = runTest {
        // Given
        val embeddings = listOf(
            createTestEmbedding("test_key_1", qualityScore = 0.9f),
            createTestEmbedding("test_key_2", qualityScore = 0.7f),
            createTestEmbedding("test_key_3", qualityScore = 0.5f, needsRefresh = true)
        )
        dao.insertEmbeddings(embeddings)

        // When
        val stats = dao.getEmbeddingStatistics()

        // Then
        assertThat(stats.total, `is`(3))
        assertThat(stats.avgQuality, `is`(0.7f))
        assertThat(stats.minQuality, `is`(0.5f))
        assertThat(stats.maxQuality, `is`(0.9f))
        assertThat(stats.refreshNeeded, `is`(1))
    }

    @Test
    fun getEmbeddingCount() = runTest {
        // Given
        val embeddings = listOf(
            createTestEmbedding("test_key_1"),
            createTestEmbedding("test_key_2")
        )
        dao.insertEmbeddings(embeddings)

        // When
        val count = dao.getEmbeddingCount()

        // Then
        assertThat(count, `is`(2))
    }

    @Test
    fun getRefreshNeededCount() = runTest {
        // Given
        val embeddings = listOf(
            createTestEmbedding("test_key_1", needsRefresh = false),
            createTestEmbedding("test_key_2", needsRefresh = true),
            createTestEmbedding("test_key_3", needsRefresh = true)
        )
        dao.insertEmbeddings(embeddings)

        // When
        val count = dao.getRefreshNeededCount()

        // Then
        assertThat(count, `is`(2))
    }

    private fun createTestEmbedding(
        ratingKey: String,
        source: AdvancedSemanticEmbedding.EmbeddingSource = AdvancedSemanticEmbedding.EmbeddingSource.TRANSFORMER,
        language: String = "en",
        qualityScore: Float = 1.0f,
        needsRefresh: Boolean = false
    ): SemanticEmbeddingEntity {
        val floatArray = FloatArray(768) { Random().nextFloat() }
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
}