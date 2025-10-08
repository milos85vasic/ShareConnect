package com.shareconnect.bookmarksync.database

import androidx.room.*
import com.shareconnect.bookmarksync.models.BookmarkData
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkDao {
    @Query("SELECT * FROM synced_bookmarks ORDER BY createdAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkData>>

    @Query("SELECT * FROM synced_bookmarks ORDER BY createdAt DESC")
    suspend fun getAllBookmarksSync(): List<BookmarkData>

    @Query("SELECT * FROM synced_bookmarks WHERE id = :bookmarkId")
    suspend fun getBookmarkById(bookmarkId: String): BookmarkData?

    @Query("SELECT * FROM synced_bookmarks WHERE isFavorite = 1 ORDER BY createdAt DESC")
    fun getFavoriteBookmarks(): Flow<List<BookmarkData>>

    @Query("SELECT * FROM synced_bookmarks WHERE type = :type ORDER BY createdAt DESC")
    fun getBookmarksByType(type: String): Flow<List<BookmarkData>>

    @Query("SELECT * FROM synced_bookmarks WHERE category = :category ORDER BY createdAt DESC")
    suspend fun getBookmarksByCategory(category: String): List<BookmarkData>

    @Query("SELECT * FROM synced_bookmarks WHERE url LIKE :searchQuery OR title LIKE :searchQuery OR description LIKE :searchQuery ORDER BY createdAt DESC")
    suspend fun searchBookmarks(searchQuery: String): List<BookmarkData>

    @Query("SELECT * FROM synced_bookmarks WHERE tags LIKE :tag ORDER BY createdAt DESC")
    suspend fun getBookmarksByTag(tag: String): List<BookmarkData>

    @Query("SELECT * FROM synced_bookmarks ORDER BY accessCount DESC LIMIT :limit")
    suspend fun getMostAccessedBookmarks(limit: Int): List<BookmarkData>

    @Query("SELECT * FROM synced_bookmarks ORDER BY lastAccessedAt DESC LIMIT :limit")
    suspend fun getRecentlyAccessedBookmarks(limit: Int): List<BookmarkData>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(bookmark: BookmarkData): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(bookmarks: List<BookmarkData>)

    @Update
    suspend fun update(bookmark: BookmarkData)

    @Query("DELETE FROM synced_bookmarks WHERE id = :bookmarkId")
    suspend fun deleteBookmark(bookmarkId: String)

    @Query("DELETE FROM synced_bookmarks")
    suspend fun deleteAllBookmarks()

    @Query("SELECT COUNT(*) FROM synced_bookmarks")
    suspend fun getBookmarkCount(): Int

    @Query("SELECT COUNT(*) FROM synced_bookmarks WHERE isFavorite = 1")
    suspend fun getFavoriteBookmarkCount(): Int
}
