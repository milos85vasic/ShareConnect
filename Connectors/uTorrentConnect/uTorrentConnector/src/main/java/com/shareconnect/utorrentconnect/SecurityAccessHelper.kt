package com.shareconnect.utorrentconnect

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import digital.vasic.security.access.access.SecurityAccessManager
import digital.vasic.security.access.data.AccessMethod
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * Helper class to bridge Kotlin suspend functions to Java for SecurityAccessManager
 */
class SecurityAccessHelper(private val context: Context) {
    private val securityAccessManager: SecurityAccessManager by lazy {
        SecurityAccessManager.getInstance(context)
    }

    /**
     * Check if access is required (blocking call for Java interop)
     */
    fun isAccessRequiredBlocking(): Boolean {
        return runBlocking {
            securityAccessManager.isAccessRequired()
        }
    }

    /**
     * Authenticate with PIN using coroutine scope
     */
    fun authenticateWithPin(
        lifecycleOwner: LifecycleOwner,
        pin: String,
        onSuccess: Runnable,
        onFailure: Runnable,
        onError: java.util.function.Consumer<String>
    ) {
        lifecycleOwner.lifecycleScope.launch {
            try {
                when (val result = securityAccessManager.authenticate(AccessMethod.PIN, pin)) {
                    is SecurityAccessManager.AuthenticationResult.Success -> {
                        onSuccess.run()
                    }
                    else -> {
                        onFailure.run()
                    }
                }
            } catch (e: Exception) {
                onError.accept(e.message ?: "Authentication error")
            }
        }
    }
}
