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


package com.shareconnect.preferencessync.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.preferencessync.database.PreferencesDatabase
import com.shareconnect.preferencessync.models.PreferencesData
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PreferencesRepositoryTest {

    private lateinit var database: PreferencesDatabase
    private lateinit var repository: PreferencesRepository

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            PreferencesDatabase::class.java
        ).build()

        repository = PreferencesRepository(database.preferencesDao())
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `test insertPreferences saves preferences correctly`() = runTest {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.insertPreference(preferencesData)

        // Then
        val savedPreferences = repository.getPreferenceById("test-preferences-id")
        assertNotNull(savedPreferences)
        assertEquals("test-preferences-id", savedPreferences?.id)
        assertEquals("theme", savedPreferences?.key)
        assertEquals("dark", savedPreferences?.value)
        assertEquals("string", savedPreferences?.type)
    }

    @Test
    fun `test updatePreferences modifies existing preferences`() = runTest {
        // Given
        val originalPreferences = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "light",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreference(originalPreferences)

        val updatedPreferences = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.updatePreference(updatedPreferences)

        // Then
        val savedPreferences = repository.getPreferenceById("test-preferences-id")
        assertNotNull(savedPreferences)
        assertEquals("dark", savedPreferences?.value)
        assertEquals(2, savedPreferences?.version)
    }

    @Test
    fun `test deletePreferences removes preferences`() = runTest {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreference(preferencesData)

        // Verify it exists
        assertNotNull(repository.getPreferenceById("test-preferences-id"))

        // When
        repository.deletePreference("test-preferences-id")

        // Then
        val deletedPreferences = repository.getPreferenceById("test-preferences-id")
        assertNull(deletedPreferences)
    }

    @Test
    fun `test getAllPreferences returns all saved preferences`() = runTest {
        // Given
        val preferences1 = PreferencesData(
            id = "test-preferences-id-1",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val preferences2 = PreferencesData(
            id = "test-preferences-id-2",
            category = "ui",
            key = "language",
            value = "en",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        repository.insertPreference(preferences1)
        repository.insertPreference(preferences2)

        // When
        val allPreferences = repository.getAllPreferencesSync()

        // Then
        assertEquals(2, allPreferences.size)
        assertTrue(allPreferences.any { it.id == "test-preferences-id-1" })
        assertTrue(allPreferences.any { it.id == "test-preferences-id-2" })
    }

    @Test
    fun `test getPreferenceById returns null for non-existent id`() = runTest {
        // When
        val preferences = repository.getPreferenceById("non-existent-id")

        // Then
        assertNull(preferences)
    }

    @Test
    fun `test getPreferencesByKey returns correct preferences`() = runTest {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreference(preferencesData)

        // When
        val preferences = repository.getPreferenceByKey("ui", "theme")

        // Then
        assertNotNull(preferences)
        assertEquals("test-preferences-id", preferences?.id)
        assertEquals("theme", preferences?.key)
        assertEquals("dark", preferences?.value)
    }

    @Test
    fun `test getPreferenceByKey returns null for non-existent key`() = runTest {
        // When
        val preferences = repository.getPreferenceByKey("ui", "non-existent-key")

        // Then
        assertNull(preferences)
    }
}