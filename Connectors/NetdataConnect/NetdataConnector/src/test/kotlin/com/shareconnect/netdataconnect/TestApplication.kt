package com.shareconnect.netdataconnect

import android.app.Application

/**
 * Test application for unit tests
 * Skips all Asinka and sync manager initialization to avoid AndroidKeyStore issues in Robolectric
 */
class TestApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Test application - skip all sync manager initialization
    }
}
