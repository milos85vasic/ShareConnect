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


package com.shareconnect.jdownloaderconnect.config

import android.content.Context
import android.content.pm.PackageManager

/**
 * Configuration manager for JDownloaderConnect that determines
 * whether the app is running in standalone mode or integrated with ShareConnect
 */
object AppConfig {
    
    private var _isShareConnectAvailable: Boolean? = null
    
    /**
     * Check if ShareConnect is available on the device
     */
    fun isShareConnectAvailable(context: Context): Boolean {
        return _isShareConnectAvailable ?: run {
            val available = try {
                context.packageManager.getPackageInfo("com.shareconnect", 0)
                true
            } catch (e: PackageManager.NameNotFoundException) {
                false
            }
            _isShareConnectAvailable = available
            available
        }
    }
    
    /**
     * Check if sync features should be enabled
     */
    fun shouldEnableSync(context: Context): Boolean {
        return isShareConnectAvailable(context)
    }
    
    /**
     * Check if onboarding should use ShareConnect's onboarding
     */
    fun shouldUseShareConnectOnboarding(context: Context): Boolean {
        return isShareConnectAvailable(context)
    }
    
    /**
     * Check if themes should be synced with ShareConnect
     */
    fun shouldSyncThemes(context: Context): Boolean {
        return isShareConnectAvailable(context)
    }
    
    /**
     * Check if profiles should be synced with ShareConnect
     */
    fun shouldSyncProfiles(context: Context): Boolean {
        return isShareConnectAvailable(context)
    }
    
    /**
     * Check if history should be synced with ShareConnect
     */
    fun shouldSyncHistory(context: Context): Boolean {
        return isShareConnectAvailable(context)
    }
    
    /**
     * Get the app mode (Standalone or Integrated)
     */
    fun getAppMode(context: Context): AppMode {
        return if (isShareConnectAvailable(context)) {
            AppMode.INTEGRATED
        } else {
            AppMode.STANDALONE
        }
    }
}

/**
 * Application modes
 */
enum class AppMode {
    STANDALONE,    // Running independently
    INTEGRATED     // Integrated with ShareConnect ecosystem
}