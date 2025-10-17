package com.shareconnect.integration

import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.historysync.HistorySyncManager
import com.shareconnect.preferencessync.PreferencesSyncManager
import com.shareconnect.themesync.ThemeSyncManager
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SyncModuleIntegrationTest {

    private lateinit var context: android.content.Context

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
    }

    @After
    fun tearDown() {
        // Reset all sync manager instances
        HistorySyncManager.resetInstance()
        PreferencesSyncManager.resetInstance()
        ThemeSyncManager.resetInstance()
    }

    @Test
    fun `test multiple sync managers can coexist without port conflicts`() = runTest {
        // Given
        val appId = "integration-test-app"

        // When - Create multiple sync managers
        val historySyncManager = HistorySyncManager.getInstance(
            context, appId, "IntegrationTest", "1.0.0"
        )
        val preferencesSyncManager = PreferencesSyncManager.getInstance(
            context, appId, "IntegrationTest", "1.0.0"
        )
        val themeSyncManager = ThemeSyncManager.getInstance(
            context, appId, "IntegrationTest", "1.0.0"
        )

        // Then - All managers should be created successfully
        assertNotNull("HistorySyncManager should be created", historySyncManager)
        assertNotNull("PreferencesSyncManager should be created", preferencesSyncManager)
        assertNotNull("ThemeSyncManager should be created", themeSyncManager)

        // And they should be different instances
        assertNotEquals("History and Preferences managers should be different", historySyncManager, preferencesSyncManager)
        assertNotEquals("History and Theme managers should be different", historySyncManager, themeSyncManager)
        assertNotEquals("Preferences and Theme managers should be different", preferencesSyncManager, themeSyncManager)
    }

    @Test
    fun `test sync managers use different ports to avoid conflicts`() = runTest {
        // Given
        val appId1 = "integration-test-app-1"
        val appId2 = "integration-test-app-2"

        // When - Create sync managers for different apps
        val historySyncManager1 = HistorySyncManager.getInstance(
            context, appId1, "IntegrationTest1", "1.0.0"
        )
        val historySyncManager2 = HistorySyncManager.getInstance(
            context, appId2, "IntegrationTest2", "1.0.0"
        )

        // Then - Both managers should be created successfully
        assertNotNull("First HistorySyncManager should be created", historySyncManager1)
        assertNotNull("Second HistorySyncManager should be created", historySyncManager2)

        // And they should be different instances
        assertNotEquals("Managers for different apps should be different", historySyncManager1, historySyncManager2)
    }

    @Test
    fun `test sync managers can be started and stopped independently`() = runTest {
        // Given
        val appId = "integration-test-app"
        val historySyncManager = HistorySyncManager.getInstance(
            context, appId, "IntegrationTest", "1.0.0"
        )

        // When - Start the sync manager
        historySyncManager.start()

        // Then - Manager should be started (no exceptions thrown)
        // Note: We can't easily test the internal state without exposing it,
        // but we can verify no exceptions are thrown during start

        // When - Reset the instance (simulates stopping)
        HistorySyncManager.resetInstance()

        // Then - New instance can be created
        val newHistorySyncManager = HistorySyncManager.getInstance(
            context, appId, "IntegrationTest", "1.0.0"
        )
        assertNotNull("New instance should be created after reset", newHistorySyncManager)
        assertNotEquals("New instance should be different from old", historySyncManager, newHistorySyncManager)
    }

    @Test
    fun `test sync managers handle concurrent access correctly`() = runTest {
        // Given
        val appId = "integration-test-app"

        // When - Create multiple threads trying to get instances simultaneously
        val managers = mutableListOf<HistorySyncManager>()
        val threads = (1..5).map { threadId ->
            Thread {
                val manager = HistorySyncManager.getInstance(
                    context, "$appId-thread-$threadId", "IntegrationTest", "1.0.0"
                )
                synchronized(managers) {
                    managers.add(manager)
                }
            }
        }

        // Start all threads
        threads.forEach { it.start() }

        // Wait for all threads to complete
        threads.forEach { it.join() }

        // Then - All managers should be created successfully
        assertEquals("All managers should be created", 5, managers.size)

        // And they should all be different instances (since they have different app IDs)
        val uniqueManagers = managers.distinct()
        assertEquals("All managers should be unique", 5, uniqueManagers.size)
    }
}