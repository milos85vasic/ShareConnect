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
