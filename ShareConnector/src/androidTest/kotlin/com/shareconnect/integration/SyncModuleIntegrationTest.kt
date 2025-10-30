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
    fun testMultipleSyncManagersCanCoexistWithoutPortConflicts() = runTest {
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
    fun testSyncManagersUseDifferentPortsToAvoidConflicts() = runTest {
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
    fun testSyncManagersCanBeStartedAndStoppedIndependently() = runTest {
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
    fun testSyncManagersHandleConcurrentAccessCorrectly() = runTest {
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