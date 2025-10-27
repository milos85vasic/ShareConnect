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
