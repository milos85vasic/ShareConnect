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
