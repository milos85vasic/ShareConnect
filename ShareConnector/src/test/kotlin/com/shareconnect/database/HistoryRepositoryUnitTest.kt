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


package com.shareconnect.database

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
class HistoryRepositoryUnitTest {

    private lateinit var mockHistoryDao: HistoryItemDao

    private lateinit var context: Context
    private lateinit var historyRepository: HistoryRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()

        mockHistoryDao = Mockito.mock(HistoryItemDao::class.java)

        // Create HistoryRepository with mocked DAO
        historyRepository = HistoryRepository(context, mockHistoryDao)
    }

    @Test
    fun testInsertHistoryItem() {
        val historyItem = HistoryItem().apply {
            url = "https://www.youtube.com/watch?v=test"
            title = "Test Video"
            serviceProvider = "YouTube"
            type = "video"
            timestamp = System.currentTimeMillis()
            profileId = "profile-123"
            profileName = "Test Profile"
            isSentSuccessfully = true
            serviceType = "metube"
        }

        `when`(mockHistoryDao.insert(historyItem)).thenReturn(1L)

        historyRepository.insertHistoryItem(historyItem)

        verify(mockHistoryDao).insert(historyItem)
    }

    @Test
    fun testRepositoryInitialization() {
        assertNotNull(historyRepository)
    }

    @Test
    fun testGetAllHistoryItems() {
        val expectedItems = listOf(
            HistoryItem().apply {
                url = "https://www.youtube.com/watch?v=test1"
                title = "Test Video 1"
                serviceProvider = "YouTube"
                timestamp = System.currentTimeMillis()
                isSentSuccessfully = true
            },
            HistoryItem().apply {
                url = "https://vimeo.com/test2"
                title = "Test Video 2"
                serviceProvider = "Vimeo"
                timestamp = System.currentTimeMillis()
                isSentSuccessfully = false
            }
        )

        `when`(mockHistoryDao.getAllHistoryItems()).thenReturn(expectedItems)

        val allItems = historyRepository.allHistoryItems

        assertEquals(2, allItems.size)
        assertTrue(allItems.any { it.serviceProvider == "YouTube" })
        assertTrue(allItems.any { it.serviceProvider == "Vimeo" })
        verify(mockHistoryDao).getAllHistoryItems()
    }

    @Test
    fun testGetHistoryItemsByServiceProvider() {
        val expectedItems = listOf(
            HistoryItem().apply {
                url = "https://www.youtube.com/watch?v=test1"
                title = "Test Video 1"
                serviceProvider = "YouTube"
                timestamp = System.currentTimeMillis()
            }
        )

        `when`(mockHistoryDao.getHistoryItemsByServiceProvider("YouTube")).thenReturn(expectedItems)

        val items = historyRepository.getHistoryItemsByServiceProvider("YouTube")

        assertEquals(1, items.size)
        assertEquals("YouTube", items[0].serviceProvider)
        verify(mockHistoryDao).getHistoryItemsByServiceProvider("YouTube")
    }

    @Test
    fun testGetHistoryItemsByType() {
        val expectedItems = listOf(
            HistoryItem().apply {
                url = "https://www.youtube.com/watch?v=test1"
                title = "Test Video 1"
                type = "video"
                timestamp = System.currentTimeMillis()
            }
        )

        `when`(mockHistoryDao.getHistoryItemsByType("video")).thenReturn(expectedItems)

        val items = historyRepository.getHistoryItemsByType("video")

        assertEquals(1, items.size)
        assertEquals("video", items[0].type)
        verify(mockHistoryDao).getHistoryItemsByType("video")
    }

    @Test
    fun testGetHistoryItemsByServiceType() {
        val expectedItems = listOf(
            HistoryItem().apply {
                url = "https://www.youtube.com/watch?v=test1"
                title = "Test Video 1"
                serviceType = "metube"
                timestamp = System.currentTimeMillis()
            }
        )

        `when`(mockHistoryDao.getHistoryItemsByServiceType("metube")).thenReturn(expectedItems)

        val items = historyRepository.getHistoryItemsByServiceType("metube")

        assertEquals(1, items.size)
        assertEquals("metube", items[0].serviceType)
        verify(mockHistoryDao).getHistoryItemsByServiceType("metube")
    }

    @Test
    fun testGetAllServiceProviders() {
        val expectedProviders = listOf("YouTube", "Vimeo")

        `when`(mockHistoryDao.getAllServiceProviders()).thenReturn(expectedProviders)

        val providers = historyRepository.allServiceProviders

        assertEquals(2, providers.size)
        assertTrue(providers.contains("YouTube"))
        assertTrue(providers.contains("Vimeo"))
        verify(mockHistoryDao).getAllServiceProviders()
    }

    @Test
    fun testGetAllTypes() {
        val expectedTypes = listOf("video", "audio")

        `when`(mockHistoryDao.getAllTypes()).thenReturn(expectedTypes)

        val types = historyRepository.allTypes

        assertEquals(2, types.size)
        assertTrue(types.contains("video"))
        assertTrue(types.contains("audio"))
        verify(mockHistoryDao).getAllTypes()
    }

    @Test
    fun testGetAllServiceTypes() {
        val expectedServiceTypes = listOf("metube", "ytdl")

        `when`(mockHistoryDao.getAllServiceTypes()).thenReturn(expectedServiceTypes)

        val serviceTypes = historyRepository.allServiceTypes

        assertEquals(2, serviceTypes.size)
        assertTrue(serviceTypes.contains("metube"))
        assertTrue(serviceTypes.contains("ytdl"))
        verify(mockHistoryDao).getAllServiceTypes()
    }

    @Test
    fun testDeleteHistoryItem() {
        val historyItem = HistoryItem().apply {
            url = "https://www.youtube.com/watch?v=test"
            title = "Item to Delete"
            timestamp = System.currentTimeMillis()
        }

        historyRepository.deleteHistoryItem(historyItem)

        verify(mockHistoryDao).delete(historyItem)
    }

    @Test
    fun testDeleteAllHistoryItems() {
        historyRepository.deleteAllHistoryItems()

        verify(mockHistoryDao).deleteAll()
    }

    @Test
    fun testDeleteHistoryItemsByServiceProvider() {
        historyRepository.deleteHistoryItemsByServiceProvider("YouTube")

        verify(mockHistoryDao).deleteByServiceProvider("YouTube")
    }

    @Test
    fun testDeleteHistoryItemsByType() {
        historyRepository.deleteHistoryItemsByType("video")

        verify(mockHistoryDao).deleteByType("video")
    }

    @Test
    fun testDeleteHistoryItemsByServiceType() {
        historyRepository.deleteHistoryItemsByServiceType("metube")

        verify(mockHistoryDao).deleteByServiceType("metube")
    }
}