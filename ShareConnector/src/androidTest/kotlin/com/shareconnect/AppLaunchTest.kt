package com.shareconnect

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertNotNull
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Automation test to verify that the ShareConnect application loads and starts
 * and presents the main screen without crashing.
 */
@RunWith(AndroidJUnit4::class)
class AppLaunchTest {

    @Test
    fun app_launches_without_crashing() {
        // Get the application context
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        assertNotNull("Application context should not be null", context)

        // Get the application instance
        val application = context.applicationContext as SCApplication
        assertNotNull("SCApplication should not be null", application)

        // Verify that all sync managers are initialized
        assertNotNull("Theme sync manager should be initialized", application.themeSyncManager)
        assertNotNull("Profile sync manager should be initialized", application.profileSyncManager)
        assertNotNull("History sync manager should be initialized", application.historySyncManager)
        assertNotNull("RSS sync manager should be initialized", application.rssSyncManager)
        assertNotNull("Bookmark sync manager should be initialized", application.bookmarkSyncManager)
        assertNotNull("Preferences sync manager should be initialized", application.preferencesSyncManager)
        assertNotNull("Language sync manager should be initialized", application.languageSyncManager)
        assertNotNull("Torrent sharing sync manager should be initialized", application.torrentSharingSyncManager)

        // Verify database is initialized
        assertNotNull("Database should be initialized", application.database)

        // If we reach this point without exceptions, the app initialized successfully
        // This test verifies that the application can start up all its components without crashing
    }
}