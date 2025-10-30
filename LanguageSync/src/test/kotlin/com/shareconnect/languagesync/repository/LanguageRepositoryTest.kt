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
import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import io.mockk.*
import io.mockk.Runs

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = com.shareconnect.languagesync.TestApplication::class)
class LanguageRepositoryTest {

    private lateinit var mockDao: LanguageDao
    private lateinit var repository: LanguageRepository

    @Before
    fun setup() {
        mockDao = mockk()
        repository = LanguageRepository(mockDao)
    }

    @Test
    fun testGetLanguagePreference() = runTest {
        val expectedLanguage = LanguageData.createDefault()
        coEvery { mockDao.getLanguagePreference() } returns expectedLanguage

        val result = repository.getLanguagePreference()

        assertEquals(expectedLanguage, result)
        coVerify { mockDao.getLanguagePreference() }
    }

    @Test
    fun testGetLanguagePreferenceReturnsNull() = runTest {
        coEvery { mockDao.getLanguagePreference() } returns null

        val result = repository.getLanguagePreference()

        assertNull(result)
        coVerify { mockDao.getLanguagePreference() }
    }

    @Test
    fun testGetLanguagePreferenceFlow() {
        val expectedLanguage = LanguageData.createDefault()
        val flow = flowOf(expectedLanguage)
        every { mockDao.getLanguagePreferenceFlow() } returns flow

        val result = repository.getLanguagePreferenceFlow()

        assertEquals(flow, result)
        verify { mockDao.getLanguagePreferenceFlow() }
    }

    @Test
    fun testSetLanguagePreference() = runTest {
        val language = LanguageData.createDefault()
        coEvery { mockDao.insertLanguagePreference(any()) } just Runs

        repository.setLanguagePreference(language)

        coVerify { mockDao.insertLanguagePreference(language) }
    }

    @Test
    fun testGetOrCreateDefaultWhenExists() = runTest {
        val existingLanguage = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_ENGLISH,
            displayName = LanguageData.NAME_ENGLISH,
            isSystemDefault = false,
            version = 2,
            lastModified = 123456789L
        )
        coEvery { mockDao.getLanguagePreference() } returns existingLanguage

        val result = repository.getOrCreateDefault()

        assertEquals(existingLanguage, result)
        coVerify { mockDao.getLanguagePreference() }
        coVerify(exactly = 0) { mockDao.insertLanguagePreference(any()) }
    }

    @Test
    fun testGetOrCreateDefaultWhenNotExists() = runTest {
        coEvery { mockDao.getLanguagePreference() } returns null
        coEvery { mockDao.insertLanguagePreference(any()) } just Runs

        val result = repository.getOrCreateDefault()

        assertEquals("language_preference", result.id)
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, result.languageCode)
        assertTrue(result.isSystemDefault)
        coVerify { mockDao.getLanguagePreference() }
        coVerify { mockDao.insertLanguagePreference(any()) }
    }

    @Test
    fun testMultipleCallsToGetOrCreateDefaultConsistent() = runTest {
        coEvery { mockDao.getLanguagePreference() } returns null
        coEvery { mockDao.insertLanguagePreference(any()) } just Runs

        val result1 = repository.getOrCreateDefault()
        val result2 = repository.getOrCreateDefault()

        // Should create default twice (since mock returns null each time)
        assertEquals(result1.id, result2.id)
        assertEquals(result1.languageCode, result2.languageCode)
        coVerify(exactly = 2) { mockDao.getLanguagePreference() }
        coVerify(exactly = 2) { mockDao.insertLanguagePreference(any()) }
    }
}
