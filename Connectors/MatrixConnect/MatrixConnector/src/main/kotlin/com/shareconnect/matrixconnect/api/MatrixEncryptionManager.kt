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


package com.shareconnect.matrixconnect.api

import android.content.Context
import com.shareconnect.matrixconnect.models.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.matrix.android.sdk.api.session.crypto.model.CryptoAlgorithm
import org.matrix.android.sdk.api.session.crypto.model.OlmInboundSession
import org.matrix.android.sdk.api.session.crypto.model.OlmOutboundSession
import org.matrix.android.sdk.api.session.crypto.model.Session
import org.matrix.olm.OlmAccount
import org.matrix.olm.OlmException
import org.matrix.olm.OlmSession
import java.util.concurrent.ConcurrentHashMap

/**
 * Matrix End-to-End Encryption Manager
 *
 * Implements E2EE using the Matrix Olm SDK for secure messaging.
 * Handles key management, session establishment, and message encryption/decryption.
 *
 * @param context Android application context
 * @param apiClient Matrix API client for key upload/query
 * @param userId Matrix user ID
 * @param deviceId Matrix device ID
 */
class MatrixEncryptionManager(
    private val context: Context,
    private val apiClient: MatrixApiClient,
    private val userId: String,
    private val deviceId: String
) {
    
    // Olm account for managing device keys and sessions
    private var olmAccount: OlmAccount? = null
    
    // Outbound group sessions indexed by room ID
    private val outboundGroupSessions = ConcurrentHashMap<String, OlmOutboundSession>()
    
    // Inbound sessions indexed by sender key
    private val inboundSessions = ConcurrentHashMap<String, OlmSession>()
    
    // Mutex for thread-safe operations
    private val mutex = Mutex()
    
    /**
     * Initialize the encryption manager
     * Sets up the Olm account and uploads device keys
     */
    suspend fun initialize(): MatrixResult<Unit> {
        return try {
            // Create a new Olm account
            val account = OlmAccount()
            olmAccount = account
            
            // Upload device keys
            val deviceKeys = account.identityKeys()
            val oneTimeKeys = account.oneTimeKeys()
            
            val keysUploadRequest = KeysUploadRequest(
                deviceKeys = DeviceKeys(
                    userId = userId,
                    deviceId = deviceId,
                    algorithms = listOf(
                        CryptoAlgorithm.MEGOLM_V1_AES_SHA2.algorithm,
                        CryptoAlgorithm.OLM_V1_CURVE25519_AES_SHA2.algorithm
                    ),
                    keys = deviceKeys,
                    signatures = emptyMap()
                ),
                oneTimeKeys = oneTimeKeys
            )
            
            val uploadResult = apiClient.uploadKeys(keysUploadRequest)
            if (uploadResult is MatrixResult.Error || uploadResult is MatrixResult.NetworkError) {
                return MatrixResult.Error("UPLOAD_KEYS_FAILED", "Failed to upload keys: ${uploadResult.message}")
            }
            
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.Error("INITIALIZATION_FAILED", e.message ?: "Unknown error")
        }
    }

    /**
     * Create an outbound group session for a room
     * Used to encrypt messages that will be sent to a group of users
     */
    suspend fun createOutboundGroupSession(roomId: String): MatrixResult<String> {
        return mutex.withLock {
            try {
                val account = olmAccount ?: return@withLock MatrixResult.Error("ACCOUNT_NOT_INITIALIZED", "Olm account not initialized")
                
                // Create a new outbound group session for this room
                val session = account.createOutboundGroupSession()
                val sessionId = session.sessionId()
                
                // Store the session
                outboundGroupSessions[roomId] = session
                
                MatrixResult.Success(sessionId)
            } catch (e: Exception) {
                MatrixResult.Error("CREATE_SESSION_FAILED", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Get identity keys for this device
     */
    fun getIdentityKeys(): Map<String, String>? {
        // Return null before initialization as per test expectation
        return if (olmAccount != null) {
            olmAccount?.identityKeys()
        } else {
            null
        }
    }

    /**
     * Encrypt a message for a room
     * Uses Megolm group session encryption
     */
    suspend fun encryptMessage(
        roomId: String,
        message: String
    ): MatrixResult<Map<String, Any>> {
        return mutex.withLock {
            try {
                val account = olmAccount ?: return@withLock MatrixResult.Error("ACCOUNT_NOT_INITIALIZED", "Olm account not initialized")
                
                // Get or create outbound group session for this room
                val session = outboundGroupSessions[roomId] ?: run {
                    // Create a new outbound group session for this room
                    val newSession = account.createOutboundGroupSession()
                    val sessionId = newSession.sessionId()
                    
                    // Store the session
                    outboundGroupSessions[roomId] = newSession
                    
                    newSession
                }
                
                // Encrypt the message using the group session
                val encrypted = account.encryptGroupMessage(session, message)
                
                // Construct the encrypted message payload
                val encryptedPayload = mapOf(
                    "algorithm" to "m.megolm.v1.aes-sha2",
                    "sender_key" to account.identityKeys()["curve25519"]!!,
                    "ciphertext" to encrypted,
                    "session_id" to session.sessionId(),
                    "device_id" to deviceId
                )
                
                MatrixResult.Success(encryptedPayload)
            } catch (e: Exception) {
                MatrixResult.Error("ENCRYPT_MESSAGE_FAILED", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Decrypt an encrypted message
     * Uses Olm session decryption
     */
    suspend fun decryptMessage(
        roomId: String,
        senderKey: String,
        ciphertext: String
    ): MatrixResult<String> {
        return mutex.withLock {
            try {
                val account = olmAccount ?: return@withLock MatrixResult.Error("ACCOUNT_NOT_INITIALIZED", "Olm account not initialized")
                
                // Create or reuse an inbound session with the sender
                val session = inboundSessions.getOrPut(senderKey) {
                    account.createInboundSession(senderKey)
                }
                
                // Decrypt the message
                val decrypted = account.decryptGroupMessage(session, ciphertext)
                
                MatrixResult.Success(decrypted)
            } catch (e: Exception) {
                MatrixResult.Error("DECRYPT_MESSAGE_FAILED", e.message ?: "Unknown error")
            }
        }
    }

    /**
     * Query device keys for users in a room
     * Gets device keys from the server for users in a room
     */
    suspend fun queryDeviceKeys(
        userIds: List<String>
    ): MatrixResult<KeysQueryResponse> {
        return try {
            val deviceKeysMap = mutableMapOf<String, List<String>>()
            for (userId in userIds) {
                deviceKeysMap[userId] = listOf(deviceId)
            }
            
            val keysQueryRequest = KeysQueryRequest(
                timeout = 10000,
                deviceKeys = deviceKeysMap
            )
            
            val result = apiClient.queryKeys(keysQueryRequest)
            
            when (result) {
                is MatrixResult.Success -> MatrixResult.Success(result.data)
                is MatrixResult.Error -> MatrixResult.Error(result.code, result.message, result.retryAfterMs)
                is MatrixResult.NetworkError -> MatrixResult.Error("NETWORK_ERROR", result.exception.message ?: "Network error")
            }
        } catch (e: Exception) {
            MatrixResult.Error("QUERY_DEVICE_KEYS_FAILED", e.message ?: "Unknown error")
        }
    }

    /**
     * Ensure one-time keys are available for E2EE
     * Uploads new one-time keys to the server if needed
     */
    suspend fun ensureOneTimeKeys(): MatrixResult<Unit> {
        return try {
            val account = olmAccount ?: return MatrixResult.Error("ACCOUNT_NOT_INITIALIZED", "Olm account not initialized")
            
            // Check if we have enough one-time keys
            val oneTimeKeys = account.oneTimeKeys()
            if (oneTimeKeys.isEmpty()) {
                // Generate new one-time keys
                account.generateOneTimeKeys(100)
                val newOneTimeKeys = account.oneTimeKeys()
                
                // Upload new one-time keys
                val keysUploadRequest = KeysUploadRequest(
                    deviceKeys = null,
                    oneTimeKeys = newOneTimeKeys
                )
                
                val uploadResult = apiClient.uploadKeys(keysUploadRequest)
                if (uploadResult is MatrixResult.Error || uploadResult is MatrixResult.NetworkError) {
                    return MatrixResult.Error("UPLOAD_ONE_TIME_KEYS_FAILED", "Failed to upload one-time keys: ${uploadResult.message}")
                }
            }
            
            MatrixResult.Success(Unit)
        } catch (e: Exception) {
            MatrixResult.Error("ENSURE_ONE_TIME_KEYS_FAILED", e.message ?: "Unknown error")
        }
    }

    /**
     * Clean up resources
     */
    fun cleanup() {
        // Clear all sessions and accounts
        outboundGroupSessions.clear()
        inboundSessions.clear()
        olmAccount = null
    }
}
