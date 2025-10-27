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


package com.shareconnect.utorrentconnect

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.remoteconfig.ConfigUpdate
import com.google.firebase.remoteconfig.ConfigUpdateListener
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.FirebaseRemoteConfigException

class FeatureManager constructor(
    private val remoteConfig: FirebaseRemoteConfig,
    private val crashlytics: FirebaseCrashlytics
) {
    init {
        remoteConfig.activate().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                crashlytics.log("Remote config activated")
            } else {
                crashlytics.log("Remote config not activated")
            }
            logConfig()
        }
        remoteConfig.addOnConfigUpdateListener(object : ConfigUpdateListener {
            override fun onUpdate(configUpdate: ConfigUpdate) {}

            override fun onError(error: FirebaseRemoteConfigException) {
                Log.w(TAG, "Config update error with code: ${error.code}", error)
            }
        })
    }

    fun useOkHttp(): Boolean {
        return remoteConfig.getBoolean("use_okhttp")
    }

    private fun logConfig() {
        crashlytics.log("Use OkHttp: ${useOkHttp()}")
    }

    companion object {
        private val TAG = FeatureManager::class.java.simpleName
    }
}
