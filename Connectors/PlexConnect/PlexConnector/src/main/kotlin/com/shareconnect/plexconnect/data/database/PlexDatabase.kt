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

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.shareconnect.plexconnect.data.database.dao.PlexLibraryDao
import com.shareconnect.plexconnect.data.database.dao.PlexMediaItemDao
import com.shareconnect.plexconnect.data.database.dao.PlexServerDao
import com.shareconnect.plexconnect.data.database.dao.SemanticEmbeddingDao
import com.shareconnect.plexconnect.data.database.entity.SemanticEmbeddingEntity
import com.shareconnect.plexconnect.data.model.PlexLibrary
import com.shareconnect.plexconnect.data.model.PlexMediaItem
import com.shareconnect.plexconnect.data.model.PlexServer

@Database(
    entities = [
        PlexServer::class,
        PlexLibrary::class,
        PlexMediaItem::class,
        SemanticEmbeddingEntity::class
    ],
    version = 2, // NLP integration
    exportSchema = false, // Disabled to avoid KSP warning
    autoMigrations = [
        // Migration will be handled manually for now
    ]
)
@TypeConverters(PlexTypeConverters::class)
abstract class PlexDatabase : RoomDatabase() {

    abstract fun plexServerDao(): PlexServerDao
    abstract fun plexLibraryDao(): PlexLibraryDao
    abstract fun plexMediaItemDao(): PlexMediaItemDao
    abstract fun semanticEmbeddingDao(): SemanticEmbeddingDao

    companion object {
        const val DATABASE_NAME = "plex_database"
    }
}