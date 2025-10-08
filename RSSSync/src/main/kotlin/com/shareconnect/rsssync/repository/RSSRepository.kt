package com.shareconnect.rsssync.repository

import com.shareconnect.rsssync.database.RSSDao
import com.shareconnect.rsssync.models.RSSFeedData
import kotlinx.coroutines.flow.Flow

class RSSRepository(private val rssDao: RSSDao) {

    fun getAllFeeds(): Flow<List<RSSFeedData>> = rssDao.getAllFeeds()
    suspend fun getAllFeedsSync(): List<RSSFeedData> = rssDao.getAllFeedsSync()
    suspend fun getFeedById(feedId: String): RSSFeedData? = rssDao.getFeedById(feedId)
    fun getEnabledFeeds(): Flow<List<RSSFeedData>> = rssDao.getEnabledFeeds()
    fun getFeedsByClientType(clientType: String): Flow<List<RSSFeedData>> = rssDao.getFeedsByClientType(clientType)
    suspend fun getFeedsByCategory(category: String): List<RSSFeedData> = rssDao.getFeedsByCategory(category)

    suspend fun insertFeed(feed: RSSFeedData): Long = rssDao.insert(feed)
    suspend fun insertAllFeeds(feeds: List<RSSFeedData>) = rssDao.insertAll(feeds)
    suspend fun updateFeed(feed: RSSFeedData) = rssDao.update(feed)
    suspend fun deleteFeed(feedId: String) = rssDao.deleteFeed(feedId)
    suspend fun deleteAllFeeds() = rssDao.deleteAllFeeds()
    suspend fun getFeedCount(): Int = rssDao.getFeedCount()
    suspend fun getEnabledFeedCount(): Int = rssDao.getEnabledFeedCount()

    fun filterForQBittorrent(feeds: List<RSSFeedData>): List<RSSFeedData> {
        return feeds.filter { it.torrentClientType == null || it.isForQBittorrent() }
    }

    fun filterForTransmission(feeds: List<RSSFeedData>): List<RSSFeedData> {
        return feeds.filter { it.torrentClientType == null || it.isForTransmission() }
    }
}
