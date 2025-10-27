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


package com.shareconnect.rsssync.database

import androidx.room.*
import com.shareconnect.rsssync.models.RSSFeedData
import kotlinx.coroutines.flow.Flow

@Dao
interface RSSDao {
    @Query("SELECT * FROM synced_rss_feeds ORDER BY name ASC")
    fun getAllFeeds(): Flow<List<RSSFeedData>>

    @Query("SELECT * FROM synced_rss_feeds ORDER BY name ASC")
    suspend fun getAllFeedsSync(): List<RSSFeedData>

    @Query("SELECT * FROM synced_rss_feeds WHERE id = :feedId")
    suspend fun getFeedById(feedId: String): RSSFeedData?

    @Query("SELECT * FROM synced_rss_feeds WHERE isEnabled = 1 ORDER BY name ASC")
    fun getEnabledFeeds(): Flow<List<RSSFeedData>>

    @Query("SELECT * FROM synced_rss_feeds WHERE torrentClientType = :clientType ORDER BY name ASC")
    fun getFeedsByClientType(clientType: String): Flow<List<RSSFeedData>>

    @Query("SELECT * FROM synced_rss_feeds WHERE category = :category ORDER BY name ASC")
    suspend fun getFeedsByCategory(category: String): List<RSSFeedData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(feed: RSSFeedData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(feeds: List<RSSFeedData>)

    @Update
    suspend fun update(feed: RSSFeedData)

    @Query("DELETE FROM synced_rss_feeds WHERE id = :feedId")
    suspend fun deleteFeed(feedId: String)

    @Query("DELETE FROM synced_rss_feeds")
    suspend fun deleteAllFeeds()

    @Query("SELECT COUNT(*) FROM synced_rss_feeds")
    suspend fun getFeedCount(): Int

    @Query("SELECT COUNT(*) FROM synced_rss_feeds WHERE isEnabled = 1")
    suspend fun getEnabledFeedCount(): Int
}
