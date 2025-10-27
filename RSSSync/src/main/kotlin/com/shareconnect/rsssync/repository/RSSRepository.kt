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
