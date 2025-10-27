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

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class HistoryItemTest {

    private lateinit var historyItem: HistoryItem

    @Before
    fun setUp() {
        historyItem = HistoryItem()
    }

    @Test
    fun testHistoryItemInitialization() {
        assertNotNull(historyItem)
        assertEquals(0, historyItem.id)
        assertNull(historyItem.url)
        assertNull(historyItem.title)
        assertNull(historyItem.serviceProvider)
        assertNull(historyItem.type)
        assertEquals(0L, historyItem.timestamp)
        assertNull(historyItem.profileId)
        assertNull(historyItem.profileName)
        assertFalse(historyItem.isSentSuccessfully)
        assertNull(historyItem.serviceType)
    }

    @Test
    fun testHistoryItemSettersAndGetters() {
        val id = 1
        val url = "https://www.youtube.com/watch?v=test"
        val title = "Test Video"
        val serviceProvider = "YouTube"
        val type = "video"
        val timestamp = System.currentTimeMillis()
        val profileId = "profile-123"
        val profileName = "Test Profile"
        val isSentSuccessfully = true
        val serviceType = "metube"

        historyItem.id = id
        historyItem.url = url
        historyItem.title = title
        historyItem.serviceProvider = serviceProvider
        historyItem.type = type
        historyItem.timestamp = timestamp
        historyItem.profileId = profileId
        historyItem.profileName = profileName
        historyItem.isSentSuccessfully = isSentSuccessfully
        historyItem.serviceType = serviceType

        assertEquals(id, historyItem.id)
        assertEquals(url, historyItem.url)
        assertEquals(title, historyItem.title)
        assertEquals(serviceProvider, historyItem.serviceProvider)
        assertEquals(type, historyItem.type)
        assertEquals(timestamp, historyItem.timestamp)
        assertEquals(profileId, historyItem.profileId)
        assertEquals(profileName, historyItem.profileName)
        assertEquals(isSentSuccessfully, historyItem.isSentSuccessfully)
        assertEquals(serviceType, historyItem.serviceType)
    }

    @Test
    fun testHistoryItemEquality() {
        val item1 = HistoryItem().apply {
            id = 1
            url = "https://www.youtube.com/watch?v=test"
            title = "Test Video"
            serviceProvider = "YouTube"
            type = "video"
            timestamp = 1234567890L
            profileId = "profile-123"
            profileName = "Test Profile"
            isSentSuccessfully = true
            serviceType = "metube"
        }

        val item2 = HistoryItem().apply {
            id = 1
            url = "https://www.youtube.com/watch?v=test"
            title = "Test Video"
            serviceProvider = "YouTube"
            type = "video"
            timestamp = 1234567890L
            profileId = "profile-123"
            profileName = "Test Profile"
            isSentSuccessfully = true
            serviceType = "metube"
        }

        assertEquals(item1.id, item2.id)
        assertEquals(item1.url, item2.url)
        assertEquals(item1.title, item2.title)
        assertEquals(item1.serviceProvider, item2.serviceProvider)
        assertEquals(item1.type, item2.type)
        assertEquals(item1.timestamp, item2.timestamp)
        assertEquals(item1.profileId, item2.profileId)
        assertEquals(item1.profileName, item2.profileName)
        assertEquals(item1.isSentSuccessfully, item2.isSentSuccessfully)
        assertEquals(item1.serviceType, item2.serviceType)
    }

    @Test
    fun testHistoryItemWithFailedSend() {
        historyItem.apply {
            url = "https://www.vimeo.com/test"
            title = "Failed Video"
            serviceProvider = "Vimeo"
            isSentSuccessfully = false
            timestamp = System.currentTimeMillis()
        }

        assertEquals("https://www.vimeo.com/test", historyItem.url)
        assertEquals("Failed Video", historyItem.title)
        assertEquals("Vimeo", historyItem.serviceProvider)
        assertFalse(historyItem.isSentSuccessfully)
        assertTrue(historyItem.timestamp > 0)
    }

    @Test
    fun testHistoryItemWithNullValues() {
        historyItem.apply {
            url = null
            title = null
            serviceProvider = null
            type = null
            profileId = null
            profileName = null
            serviceType = null
        }

        assertNull(historyItem.url)
        assertNull(historyItem.title)
        assertNull(historyItem.serviceProvider)
        assertNull(historyItem.type)
        assertNull(historyItem.profileId)
        assertNull(historyItem.profileName)
        assertNull(historyItem.serviceType)
    }
}