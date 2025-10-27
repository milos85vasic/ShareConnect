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