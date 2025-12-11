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

import androidx.room.*
import com.shareconnect.plexconnect.data.database.entity.SemanticEmbeddingEntity
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import kotlinx.coroutines.flow.Flow

@Dao
interface SemanticEmbeddingDao {

    /**
     * Get embedding for a specific media item
     */
    @Query("SELECT * FROM semantic_embeddings WHERE mediaRatingKey = :ratingKey LIMIT 1")
    suspend fun getEmbeddingForMedia(ratingKey: String): SemanticEmbeddingEntity?

    /**
     * Get embedding flow for a specific media item
     */
    @Query("SELECT * FROM semantic_embeddings WHERE mediaRatingKey = :ratingKey LIMIT 1")
    fun getEmbeddingFlowForMedia(ratingKey: String): Flow<SemanticEmbeddingEntity?>

    /**
     * Get all embeddings for a list of media items
     */
    @Query("SELECT * FROM semantic_embeddings WHERE mediaRatingKey IN (:ratingKeys)")
    suspend fun getEmbeddingsForMedia(ratingKeys: List<String>): List<SemanticEmbeddingEntity>

    /**
     * Get all embeddings in the database
     */
    @Query("SELECT * FROM semantic_embeddings ORDER BY createdAt DESC")
    fun getAllEmbeddings(): Flow<List<SemanticEmbeddingEntity>>

    /**
     * Get embeddings that need refresh
     */
    @Query("SELECT * FROM semantic_embeddings WHERE needsRefresh = 1 ORDER BY qualityScore ASC")
    suspend fun getEmbeddingsNeedingRefresh(): List<SemanticEmbeddingEntity>

    /**
     * Get embeddings by source
     */
    @Query("SELECT * FROM semantic_embeddings WHERE embeddingSource = :source ORDER BY qualityScore DESC")
    suspend fun getEmbeddingsBySource(source: AdvancedSemanticEmbedding.EmbeddingSource): List<SemanticEmbeddingEntity>

    /**
     * Get embeddings by language
     */
    @Query("SELECT * FROM semantic_embeddings WHERE language = :language ORDER BY qualityScore DESC")
    suspend fun getEmbeddingsByLanguage(language: String): List<SemanticEmbeddingEntity>

    /**
     * Get embeddings created after a specific timestamp
     */
    @Query("SELECT * FROM semantic_embeddings WHERE createdAt > :timestamp ORDER BY createdAt ASC")
    suspend fun getEmbeddingsCreatedAfter(timestamp: Long): List<SemanticEmbeddingEntity>

    /**
     * Get embeddings with quality score above threshold
     */
    @Query("SELECT * FROM semantic_embeddings WHERE qualityScore >= :minQuality ORDER BY qualityScore DESC")
    suspend fun getHighQualityEmbeddings(minQuality: Float = 0.8f): List<SemanticEmbeddingEntity>

    /**
     * Insert or update an embedding
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmbedding(embedding: SemanticEmbeddingEntity): Long

    /**
     * Insert multiple embeddings
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmbeddings(embeddings: List<SemanticEmbeddingEntity>): List<Long>

    /**
     * Update an existing embedding
     */
    @Update
    suspend fun updateEmbedding(embedding: SemanticEmbeddingEntity)

    /**
     * Mark embedding as needing refresh
     */
    @Query("UPDATE semantic_embeddings SET needsRefresh = 1, updatedAt = :timestamp WHERE mediaRatingKey = :ratingKey")
    suspend fun markForRefresh(ratingKey: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Mark multiple embeddings as needing refresh
     */
    @Query("UPDATE semantic_embeddings SET needsRefresh = 1, updatedAt = :timestamp WHERE mediaRatingKey IN (:ratingKeys)")
    suspend fun markMultipleForRefresh(ratingKeys: List<String>, timestamp: Long = System.currentTimeMillis())

    /**
     * Update quality score for an embedding
     */
    @Query("UPDATE semantic_embeddings SET qualityScore = :score, updatedAt = :timestamp WHERE mediaRatingKey = :ratingKey")
    suspend fun updateQualityScore(ratingKey: String, score: Float, timestamp: Long = System.currentTimeMillis())

    /**
     * Clear the refresh flag for an embedding
     */
    @Query("UPDATE semantic_embeddings SET needsRefresh = 0, updatedAt = :timestamp WHERE mediaRatingKey = :ratingKey")
    suspend fun clearRefreshFlag(ratingKey: String, timestamp: Long = System.currentTimeMillis())

    /**
     * Delete embedding for a media item
     */
    @Query("DELETE FROM semantic_embeddings WHERE mediaRatingKey = :ratingKey")
    suspend fun deleteEmbeddingForMedia(ratingKey: String)

    /**
     * Delete multiple embeddings
     */
    @Query("DELETE FROM semantic_embeddings WHERE mediaRatingKey IN (:ratingKeys)")
    suspend fun deleteEmbeddingsForMedia(ratingKeys: List<String>)

    /**
     * Delete old embeddings (created before timestamp)
     */
    @Query("DELETE FROM semantic_embeddings WHERE createdAt < :timestamp")
    suspend fun deleteOldEmbeddings(timestamp: Long)

    /**
     * Delete all embeddings
     */
    @Query("DELETE FROM semantic_embeddings")
    suspend fun deleteAllEmbeddings()

    /**
     * Count total embeddings
     */
    @Query("SELECT COUNT(*) FROM semantic_embeddings")
    suspend fun getEmbeddingCount(): Int

    /**
     * Count embeddings needing refresh
     */
    @Query("SELECT COUNT(*) FROM semantic_embeddings WHERE needsRefresh = 1")
    suspend fun getRefreshNeededCount(): Int

    /**
     * Get statistics for embeddings
     */
    @Query("""
        SELECT 
            COUNT(*) as total,
            AVG(qualityScore) as avgQuality,
            MIN(qualityScore) as minQuality,
            MAX(qualityScore) as maxQuality,
            COUNT(CASE WHEN needsRefresh = 1 THEN 1 END) as refreshNeeded
        FROM semantic_embeddings
    """)
    suspend fun getEmbeddingStatistics(): EmbeddingStatistics

    /**
     * Data class for embedding statistics
     */
    data class EmbeddingStatistics(
        val total: Int,
        val avgQuality: Float?,
        val minQuality: Float?,
        val maxQuality: Float?,
        val refreshNeeded: Int
    )
}