package com.shareconnect.utorrentconnect.logging

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics

class Logger constructor(
    private val crashlytics: FirebaseCrashlytics
) {

    fun log(message: String) {

        Log.d(TAG, message)
    }

    fun log(throwable: Throwable) {

        Log.e(TAG, null, throwable)
    }

    companion object {
        private const val TAG = "Logger"
    }
}
