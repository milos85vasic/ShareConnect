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


package com.shareconnect.plexconnect.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migration from version 1 to version 2
 * Adds semantic_embeddings table for AI-powered recommendations
 */
object Migration1To2 : Migration(1, 2) {
    
    override fun migrate(database: SupportSQLiteDatabase) {
        // Create semantic_embeddings table
        database.execSQL(
            """
            CREATE TABLE IF NOT EXISTS `semantic_embeddings` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT,
                `mediaRatingKey` TEXT NOT NULL,
                `embedding` BLOB NOT NULL,
                `language` TEXT NOT NULL,
                `embeddingSource` TEXT NOT NULL,
                `modelVersion` TEXT NOT NULL,
                `createdAt` INTEGER NOT NULL,
                `updatedAt` INTEGER NOT NULL,
                `contentHash` TEXT NOT NULL,
                `qualityScore` REAL NOT NULL,
                `needsRefresh` INTEGER NOT NULL,
                FOREIGN KEY(`mediaRatingKey`) REFERENCES `plex_media_items`(`ratingKey`) ON DELETE CASCADE,
                UNIQUE(`mediaRatingKey`)
            )
            """.trimIndent()
        )
        
        // Create indexes for performance
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_semantic_embeddings_mediaRatingKey` ON `semantic_embeddings` (`mediaRatingKey`)"
        )
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_semantic_embeddings_embeddingSource` ON `semantic_embeddings` (`embeddingSource`)"
        )
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_semantic_embeddings_language` ON `semantic_embeddings` (`language`)"
        )
        
        database.execSQL(
            "CREATE INDEX IF NOT EXISTS `index_semantic_embeddings_createdAt` ON `semantic_embeddings` (`createdAt`)"
        )
    }
}