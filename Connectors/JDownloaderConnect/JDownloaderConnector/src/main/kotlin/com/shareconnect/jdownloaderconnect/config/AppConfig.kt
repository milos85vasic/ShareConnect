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