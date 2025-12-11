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


package com.shareconnect.plexconnect.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding
import com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource

/**
 * Entity for storing semantic embeddings of media items
 * Used for AI-powered recommendations and similarity analysis
 */
@Entity(
    tableName = "semantic_embeddings",
    foreignKeys = [
        ForeignKey(
            entity = com.shareconnect.plexconnect.data.model.PlexMediaItem::class,
            parentColumns = ["ratingKey"],
            childColumns = ["mediaRatingKey"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["mediaRatingKey"], unique = true),
        Index(value = ["embeddingSource"]),
        Index(value = ["language"]),
        Index(value = ["createdAt"])
    ]
)
data class SemanticEmbeddingEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    /**
     * Reference to the media item this embedding belongs to
     */
    val mediaRatingKey: String,
    
    /**
     * The embedding vector (768 dimensions for transformer models)
     * Stored as BLOB for Room compatibility
     */
    @ColumnInfo(typeAffinity = ColumnInfo.BLOB)
    val embedding: ByteArray,
    
    /**
     * Language of the processed text
     */
    val language: String,
    
    /**
     * Source of the embedding generation
     */
    val embeddingSource: AdvancedSemanticEmbedding.EmbeddingSource,
    
    /**
     * Model version used for generation
     */
    val modelVersion: String,
    
    /**
     * When this embedding was created
     */
    val createdAt: Long = System.currentTimeMillis(),
    
    /**
     * When this embedding was last updated
     */
    val updatedAt: Long = System.currentTimeMillis(),
    
    /**
     * Hash of the source content for change detection
     */
    val contentHash: String,
    
    /**
     * Quality score of the embedding (0.0 to 1.0)
     */
    val qualityScore: Float = 1.0f,
    
    /**
     * Whether this embedding should be refreshed
     */
    val needsRefresh: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SemanticEmbeddingEntity

        if (id != other.id) return false
        if (mediaRatingKey != other.mediaRatingKey) return false
        if (!embedding.contentEquals(other.embedding)) return false
        if (language != other.language) return false
        if (embeddingSource != other.embeddingSource) return false
        if (modelVersion != other.modelVersion) return false
        if (createdAt != other.createdAt) return false
        if (updatedAt != other.updatedAt) return false
        if (contentHash != other.contentHash) return false
        if (qualityScore != other.qualityScore) return false
        if (needsRefresh != other.needsRefresh) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + mediaRatingKey.hashCode()
        result = 31 * result + embedding.contentHashCode()
        result = 31 * result + language.hashCode()
        result = 31 * result + embeddingSource.hashCode()
        result = 31 * result + modelVersion.hashCode()
        result = 31 * result + createdAt.hashCode()
        result = 31 * result + updatedAt.hashCode()
        result = 31 * result + contentHash.hashCode()
        result = 31 * result + qualityScore.hashCode()
        result = 31 * result + needsRefresh.hashCode()
        return result
    }
    
    /**
     * Convert ByteArray embedding to FloatArray for use with NLP models
     */
    fun getEmbeddingAsFloatArray(): FloatArray {
        val byteBuffer = java.nio.ByteBuffer.wrap(embedding)
        byteBuffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
        val floatArray = FloatArray(embedding.size / 4)
        byteBuffer.asFloatBuffer().get(floatArray)
        return floatArray
    }
    
    companion object {
        /**
         * Create entity from FloatArray embedding
         */
        fun fromFloatArray(
            mediaRatingKey: String,
            embedding: FloatArray,
            language: String,
            embeddingSource: com.shareconnect.plexconnect.nlp.AdvancedSemanticEmbedding.EmbeddingSource,
            modelVersion: String,
            contentHash: String,
            qualityScore: Float = 1.0f,
            needsRefresh: Boolean = false
        ): SemanticEmbeddingEntity {
            val byteBuffer = java.nio.ByteBuffer.allocate(embedding.size * 4)
            byteBuffer.order(java.nio.ByteOrder.LITTLE_ENDIAN)
            byteBuffer.asFloatBuffer().put(embedding)
            
            return SemanticEmbeddingEntity(
                mediaRatingKey = mediaRatingKey,
                embedding = byteBuffer.array(),
                language = language,
                embeddingSource = embeddingSource,
                modelVersion = modelVersion,
                contentHash = contentHash,
                qualityScore = qualityScore,
                needsRefresh = needsRefresh
            )
        }
    }
}