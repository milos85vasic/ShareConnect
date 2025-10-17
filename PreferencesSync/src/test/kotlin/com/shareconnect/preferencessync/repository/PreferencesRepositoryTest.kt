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
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.insertPreferences(preferencesData)

        // Then
        val savedPreferences = repository.getPreferencesById("test-preferences-id")
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
            key = "theme",
            value = "light",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreferences(originalPreferences)

        val updatedPreferences = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        // When
        repository.updatePreferences(updatedPreferences)

        // Then
        val savedPreferences = repository.getPreferencesById("test-preferences-id")
        assertNotNull(savedPreferences)
        assertEquals("dark", savedPreferences?.value)
        assertEquals(2, savedPreferences?.version)
    }

    @Test
    fun `test deletePreferences removes preferences`() = runTest {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreferences(preferencesData)

        // Verify it exists
        assertNotNull(repository.getPreferencesById("test-preferences-id"))

        // When
        repository.deletePreferences("test-preferences-id")

        // Then
        val deletedPreferences = repository.getPreferencesById("test-preferences-id")
        assertNull(deletedPreferences)
    }

    @Test
    fun `test getAllPreferences returns all saved preferences`() = runTest {
        // Given
        val preferences1 = PreferencesData(
            id = "test-preferences-id-1",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        val preferences2 = PreferencesData(
            id = "test-preferences-id-2",
            key = "language",
            value = "en",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )

        repository.insertPreferences(preferences1)
        repository.insertPreferences(preferences2)

        // When
        val allPreferences = repository.getAllPreferences()

        // Then
        assertEquals(2, allPreferences.size)
        assertTrue(allPreferences.any { it.id == "test-preferences-id-1" })
        assertTrue(allPreferences.any { it.id == "test-preferences-id-2" })
    }

    @Test
    fun `test getPreferencesById returns null for non-existent id`() = runTest {
        // When
        val preferences = repository.getPreferencesById("non-existent-id")

        // Then
        assertNull(preferences)
    }

    @Test
    fun `test getPreferencesByKey returns correct preferences`() = runTest {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 1,
            lastModified = System.currentTimeMillis()
        )
        repository.insertPreferences(preferencesData)

        // When
        val preferences = repository.getPreferencesByKey("theme")

        // Then
        assertNotNull(preferences)
        assertEquals("test-preferences-id", preferences?.id)
        assertEquals("theme", preferences?.key)
        assertEquals("dark", preferences?.value)
    }

    @Test
    fun `test getPreferencesByKey returns null for non-existent key`() = runTest {
        // When
        val preferences = repository.getPreferencesByKey("non-existent-key")

        // Then
        assertNull(preferences)
    }
}