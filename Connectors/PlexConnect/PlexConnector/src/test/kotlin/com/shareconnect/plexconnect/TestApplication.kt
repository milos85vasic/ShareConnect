package com.shareconnect.plexconnect

import android.app.Application

class TestApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        // Test application - skip all Asinka and Firebase initialization
    }
}
