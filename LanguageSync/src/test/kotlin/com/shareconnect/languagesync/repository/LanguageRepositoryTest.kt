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


package com.shareconnect.languagesync.repository

import com.shareconnect.languagesync.database.LanguageDao
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.*

class LanguageRepositoryTest {

    private lateinit var mockDao: LanguageDao
    private lateinit var repository: LanguageRepository

    @Before
    fun setup() {
        mockDao = mock()
        repository = LanguageRepository(mockDao)
    }

    @Test
    fun testGetLanguagePreference(): Unit = runBlocking {
        val expectedLanguage = LanguageData.createDefault()
        whenever(mockDao.getLanguagePreference()).thenReturn(expectedLanguage)

        val result = repository.getLanguagePreference()

        assertEquals(expectedLanguage, result)
        verify(mockDao).getLanguagePreference()
    }

    @Test
    fun testGetLanguagePreferenceReturnsNull(): Unit = runBlocking {
        whenever(mockDao.getLanguagePreference()).thenReturn(null)

        val result = repository.getLanguagePreference()

        assertNull(result)
        verify(mockDao).getLanguagePreference()
    }

    @Test
    fun testGetLanguagePreferenceFlow() {
        val expectedLanguage = LanguageData.createDefault()
        val flow = flowOf(expectedLanguage)
        whenever(mockDao.getLanguagePreferenceFlow()).thenReturn(flow)

        val result = repository.getLanguagePreferenceFlow()

        assertEquals(flow, result)
        verify(mockDao).getLanguagePreferenceFlow()
    }

    @Test
    fun testSetLanguagePreference(): Unit = runBlocking {
        val language = LanguageData.createDefault()

        repository.setLanguagePreference(language)

        verify(mockDao).insertLanguagePreference(language)
    }

    @Test
    fun testGetOrCreateDefaultWhenExists(): Unit = runBlocking {
        val existingLanguage = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_ENGLISH,
            displayName = LanguageData.NAME_ENGLISH,
            isSystemDefault = false,
            version = 2,
            lastModified = 123456789L
        )
        whenever(mockDao.getLanguagePreference()).thenReturn(existingLanguage)

        val result = repository.getOrCreateDefault()

        assertEquals(existingLanguage, result)
        verify(mockDao).getLanguagePreference()
        verify(mockDao, never()).insertLanguagePreference(any())
    }

    @Test
    fun testGetOrCreateDefaultWhenNotExists(): Unit = runBlocking {
        whenever(mockDao.getLanguagePreference()).thenReturn(null)

        val result = repository.getOrCreateDefault()

        assertEquals("language_preference", result.id)
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, result.languageCode)
        assertTrue(result.isSystemDefault)
        verify(mockDao).getLanguagePreference()
        verify(mockDao).insertLanguagePreference(any())
    }

    @Test
    fun testMultipleCallsToGetOrCreateDefaultConsistent(): Unit = runBlocking {
        whenever(mockDao.getLanguagePreference()).thenReturn(null)

        val result1 = repository.getOrCreateDefault()
        val result2 = repository.getOrCreateDefault()

        // Should create default twice (since mock returns null each time)
        assertEquals(result1.id, result2.id)
        assertEquals(result1.languageCode, result2.languageCode)
        verify(mockDao, times(2)).getLanguagePreference()
        verify(mockDao, times(2)).insertLanguagePreference(any())
    }
}
