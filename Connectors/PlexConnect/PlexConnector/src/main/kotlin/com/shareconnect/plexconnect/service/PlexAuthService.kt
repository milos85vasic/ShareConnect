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


package com.shareconnect.plexconnect.service

import android.util.Log
import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.model.PlexPinResponse
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.*
class PlexAuthService(
    private val apiClient: PlexApiClient
) {

    private val tag = "PlexAuthService"

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private var authJob: Job? = null
    private var currentPinId: Long? = null

    sealed class AuthState {
        object Idle : AuthState()
        data class RequestingPin(val clientId: String) : AuthState()
        data class PinReceived(val pin: PlexPinResponse) : AuthState()
        object CheckingPin : AuthState()
        data class Authenticated(val token: String) : AuthState()
        data class Error(val message: String) : AuthState()
    }

    fun startAuthentication(clientIdentifier: String = UUID.randomUUID().toString()) {
        if (authJob?.isActive == true) {
            Log.w(tag, "Authentication already in progress")
            return
        }

        authJob = CoroutineScope(Dispatchers.IO).launch {
            try {
                _authState.value = AuthState.RequestingPin(clientIdentifier)

                // Request PIN from Plex
                val pinResult = apiClient.requestPin(clientIdentifier)
                pinResult.fold(
                    onSuccess = { pinResponse ->
                        currentPinId = pinResponse.id
                        _authState.value = AuthState.PinReceived(pinResponse)

                        // Start polling for authentication
                        pollForAuth(pinResponse.id)
                    },
                    onFailure = { error ->
                        Log.e(tag, "Failed to request PIN", error)
                        _authState.value = AuthState.Error("Failed to request PIN: ${error.message}")
                    }
                )
            } catch (e: Exception) {
                Log.e(tag, "Authentication error", e)
                _authState.value = AuthState.Error("Authentication failed: ${e.message}")
            }
        }
    }

    private suspend fun pollForAuth(pinId: Long) {
        var attempts = 0
        val maxAttempts = 120 // 2 minutes at 1 second intervals

        while (attempts < maxAttempts && authJob?.isActive == true) {
            delay(1000) // Wait 1 second between checks
            attempts++

            _authState.value = AuthState.CheckingPin

            try {
                val checkResult = apiClient.checkPin(pinId)
                checkResult.fold(
                    onSuccess = { pinResponse ->
                        pinResponse.authToken?.let { token ->
                            // Authentication successful
                            _authState.value = AuthState.Authenticated(token)
                            Log.i(tag, "Authentication successful")
                            return
                        }
                        // Continue polling if no token yet
                    },
                    onFailure = { error ->
                        Log.w(tag, "PIN check failed (attempt $attempts)", error)
                        // Continue polling on failure (might be temporary network issue)
                    }
                )
            } catch (e: Exception) {
                Log.w(tag, "PIN check error (attempt $attempts)", e)
                // Continue polling on exception
            }
        }

        // Timeout reached
        if (authJob?.isActive == true) {
            _authState.value = AuthState.Error("Authentication timeout - PIN may have expired")
            Log.w(tag, "Authentication timeout after $maxAttempts attempts")
        }
    }

    fun cancelAuthentication() {
        authJob?.cancel()
        authJob = null
        currentPinId = null
        _authState.value = AuthState.Idle
        Log.i(tag, "Authentication cancelled")
    }

    fun reset() {
        cancelAuthentication()
    }

    fun isAuthenticating(): Boolean = authJob?.isActive == true

    fun getCurrentPinId(): Long? = currentPinId
}