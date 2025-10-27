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


package com.shareconnect.bookmarksync.repository

import com.shareconnect.bookmarksync.database.BookmarkDao
import com.shareconnect.bookmarksync.models.BookmarkData
import kotlinx.coroutines.flow.Flow

class BookmarkRepository(private val bookmarkDao: BookmarkDao) {

    fun getAllBookmarks(): Flow<List<BookmarkData>> = bookmarkDao.getAllBookmarks()
    suspend fun getAllBookmarksSync(): List<BookmarkData> = bookmarkDao.getAllBookmarksSync()
    suspend fun getBookmarkById(bookmarkId: String): BookmarkData? = bookmarkDao.getBookmarkById(bookmarkId)
    fun getFavoriteBookmarks(): Flow<List<BookmarkData>> = bookmarkDao.getFavoriteBookmarks()
    fun getBookmarksByType(type: String): Flow<List<BookmarkData>> = bookmarkDao.getBookmarksByType(type)
    suspend fun getBookmarksByCategory(category: String): List<BookmarkData> = bookmarkDao.getBookmarksByCategory(category)
    suspend fun searchBookmarks(query: String): List<BookmarkData> = bookmarkDao.searchBookmarks("%$query%")
    suspend fun getBookmarksByTag(tag: String): List<BookmarkData> = bookmarkDao.getBookmarksByTag("%$tag%")
    suspend fun getMostAccessedBookmarks(limit: Int = 10): List<BookmarkData> = bookmarkDao.getMostAccessedBookmarks(limit)
    suspend fun getRecentlyAccessedBookmarks(limit: Int = 10): List<BookmarkData> = bookmarkDao.getRecentlyAccessedBookmarks(limit)

    suspend fun insertBookmark(bookmark: BookmarkData): Long = bookmarkDao.insert(bookmark)
    suspend fun insertAllBookmarks(bookmarks: List<BookmarkData>) = bookmarkDao.insertAll(bookmarks)
    suspend fun updateBookmark(bookmark: BookmarkData) = bookmarkDao.update(bookmark)
    suspend fun deleteBookmark(bookmarkId: String) = bookmarkDao.deleteBookmark(bookmarkId)
    suspend fun deleteAllBookmarks() = bookmarkDao.deleteAllBookmarks()
    suspend fun getBookmarkCount(): Int = bookmarkDao.getBookmarkCount()
    suspend fun getFavoriteBookmarkCount(): Int = bookmarkDao.getFavoriteBookmarkCount()

    fun getTorrentBookmarks(bookmarks: List<BookmarkData>): List<BookmarkData> {
        return bookmarks.filter { it.isTorrentBookmark() }
    }

    fun getMediaBookmarks(bookmarks: List<BookmarkData>): List<BookmarkData> {
        return bookmarks.filter { it.isMediaBookmark() }
    }
}
