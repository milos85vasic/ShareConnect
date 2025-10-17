package com.shareconnect.preferencessync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.preferencessync.models.PreferencesData
import com.shareconnect.preferencessync.repository.PreferencesRepository
import digital.vasic.asinka.AsinkaClient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.any
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class PreferencesSyncManagerTest {

    @Mock
    private lateinit var mockAsinkaClient: AsinkaClient

    @Mock
    private lateinit var mockRepository: PreferencesRepository

    private lateinit var context: Context
    private lateinit var preferencesSyncManager: PreferencesSyncManager

    @Before
    fun setUp() {
        MockitoAnnotations.openMocks(this)
        context = ApplicationProvider.getApplicationContext()

        // Create a test instance
        preferencesSyncManager = PreferencesSyncManager(
            context = context,
            appIdentifier = "test-app",
            appName = "TestApp",
            appVersion = "1.0.0",
            asinkaClient = mockAsinkaClient,
            repository = mockRepository
        )
    }

    @After
    fun tearDown() {
        PreferencesSyncManager.resetInstance()
    }

    @Test
    fun `test getInstance creates singleton instance`() = runTest {
        val instance1 = PreferencesSyncManager.getInstance(
            context, "test-app-1", "TestApp1", "1.0.0"
        )
        val instance2 = PreferencesSyncManager.getInstance(
            context, "test-app-2", "TestApp2", "1.0.0"
        )

        assertNotNull(instance1)
        assertNotNull(instance2)
        assertNotEquals(instance1, instance2)
    }

    @Test
    fun `test start initializes sync manager correctly`() = runTest {
        // Given
        whenever(mockAsinkaClient.start()).then { }

        // When
        preferencesSyncManager.start()

        // Then
        verify(mockAsinkaClient).start()
    }

    @Test
    fun `test handleReceivedPreferences saves new preferences`() = runTest {
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
        whenever(mockRepository.getPreferencesById("test-preferences-id")).thenReturn(null)
        whenever(mockRepository.insertPreferences(any())).then { }

        // When
        preferencesSyncManager.handleReceivedPreferences(preferencesData)

        // Then
        verify(mockRepository).insertPreferences(preferencesData)
    }

    @Test
    fun `test handleReceivedPreferences updates existing preferences when newer version`() = runTest {
        // Given
        val existingPreferences = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "light",
            type = "string",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )
        val newPreferences = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )

        whenever(mockRepository.getPreferencesById("test-preferences-id")).thenReturn(existingPreferences)
        whenever(mockRepository.updatePreferences(any())).then { }

        // When
        preferencesSyncManager.handleReceivedPreferences(newPreferences)

        // Then
        verify(mockRepository).updatePreferences(newPreferences)
    }

    @Test
    fun `test handleReceivedPreferences ignores older version of preferences`() = runTest {
        // Given
        val existingPreferences = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis(),
            version = 2,
            lastModified = System.currentTimeMillis()
        )
        val oldPreferences = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "light",
            type = "string",
            timestamp = System.currentTimeMillis() - 1000,
            version = 1,
            lastModified = System.currentTimeMillis() - 1000
        )

        whenever(mockRepository.getPreferencesById("test-preferences-id")).thenReturn(existingPreferences)

        // When
        preferencesSyncManager.handleReceivedPreferences(oldPreferences)

        // Then
        verify(mockRepository, never()).updatePreferences(any())
        verify(mockRepository, never()).insertPreferences(any())
    }

    @Test
    fun `test handleDeletedPreferences removes preferences`() = runTest {
        // Given
        whenever(mockRepository.deletePreferences("test-preferences-id")).then { }

        // When
        preferencesSyncManager.handleDeletedPreferences("test-preferences-id")

        // Then
        verify(mockRepository).deletePreferences("test-preferences-id")
    }

    @Test
    fun `test preferenceChangeFlow emits when preferences are saved`() = runTest {
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

        whenever(mockRepository.getPreferencesById("test-preferences-id")).thenReturn(null)
        whenever(mockRepository.insertPreferences(any())).then { }

        var emittedPreferences: PreferencesData? = null
        val job = kotlinx.coroutines.launch {
            preferencesSyncManager.preferenceChangeFlow.collect { emittedPreferences = it }
        }

        // When
        preferencesSyncManager.handleReceivedPreferences(preferencesData)

        // Small delay to allow flow emission
        kotlinx.coroutines.delay(100)

        // Then
        assertNotNull(emittedPreferences)
        assertEquals("test-preferences-id", emittedPreferences?.id)
        assertEquals("theme", emittedPreferences?.key)
        assertEquals("dark", emittedPreferences?.value)

        job.cancel()
    }
}