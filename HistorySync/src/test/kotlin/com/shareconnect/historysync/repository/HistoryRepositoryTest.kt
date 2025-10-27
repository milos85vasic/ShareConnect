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


package com.shareconnect.historysync.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.historysync.database.HistoryDatabase
import com.shareconnect.historysync.models.HistoryData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HistoryRepositoryTest {

    private lateinit var database: HistoryDatabase
    private lateinit var repository: HistoryRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            HistoryDatabase::class.java
        ).build()

        repository = HistoryRepository(database.historyDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test insertHistory saves history item correctly`() = runTest {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            description = "Test Description",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.insertHistory(historyData)

        // Then
        val savedHistory = repository.getHistoryById("test-id")
        assertNotNull(savedHistory)
        assertEquals("test-id", savedHistory?.id)
        assertEquals("Test Title", savedHistory?.title)
        assertEquals("https://example.com", savedHistory?.url)
    }

    @Test
    fun `test updateHistory modifies existing history item`() = runTest {
        // Given
        val originalHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Original Title",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertHistory(originalHistory)

        val updatedHistory = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Updated Title",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.updateHistory(updatedHistory)

        // Then
        val savedHistory = repository.getHistoryById("test-id")
        assertNotNull(savedHistory)
        assertEquals("Updated Title", savedHistory?.title)
        assertEquals(2, savedHistory?.version)
    }

    @Test
    fun `test deleteHistory removes history item`() = runTest {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertHistory(historyData)

        // Verify it exists
        assertNotNull(repository.getHistoryById("test-id"))

        // When
        repository.deleteHistory("test-id")

        // Then
        val deletedHistory = repository.getHistoryById("test-id")
        assertNull(deletedHistory)
    }

    @Test
    fun `test getAllHistory returns all saved history items`() = runTest {
        // Given
        val history1 = HistoryData(
            id = "test-id-1",
            url = "https://example1.com",
            title = "Title 1",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val history2 = HistoryData(
            id = "test-id-2",
            url = "https://example2.com",
            title = "Title 2",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        repository.insertHistory(history1)
        repository.insertHistory(history2)

        // When
        val allHistory = repository.getAllHistory()

        // Then
        assertEquals(2, allHistory.size)
        assertTrue(allHistory.any { it.id == "test-id-1" })
        assertTrue(allHistory.any { it.id == "test-id-2" })
    }

    @Test
    fun `test getHistoryById returns null for non-existent id`() = runTest {
        // When
        val history = repository.getHistoryById("non-existent-id")

        // Then
        assertNull(history)
    }

    @Test
    fun `test getHistoryByUrl returns correct history item`() = runTest {
        // Given
        val historyData = HistoryData(
            id = "test-id",
            url = "https://example.com",
            title = "Test Title",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertHistory(historyData)

        // When
        val history = repository.getHistoryByUrl("https://example.com")

        // Then
        assertNotNull(history)
        assertEquals("test-id", history?.id)
        assertEquals("Test Title", history?.title)
    }

    @Test
    fun `test getHistoryByUrl returns null for non-existent url`() = runTest {
        // When
        val history = repository.getHistoryByUrl("https://non-existent.com")

        // Then
        assertNull(history)
    }
}