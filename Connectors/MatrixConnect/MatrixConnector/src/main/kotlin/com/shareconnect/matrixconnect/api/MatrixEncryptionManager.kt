package com.shareconnect.matrixconnect.api

import android.content.Context
import android.util.Base64
import com.shareconnect.matrixconnect.models.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import org.matrix.olm.OlmAccount
import org.matrix.olm.OlmException
import org.matrix.olm.OlmOutboundGroupSession
import org.matrix.olm.OlmSession
import java.security.SecureRandom

/**
 * Matrix End-to-End Encryption Manager using Olm/Megolm
 *
 * Provides E2EE capabilities including:
 * - Olm account management (device identity keys)
 * - One-time key generation and management
 * - Megolm session creation for room encryption
 * - Message encryption and decryption
 * - Device verification support
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
    private var olmAccount: OlmAccount? = null
    private val outboundGroupSessions = mutableMapOf<String, OlmOutboundGroupSession>()
    private val olmSessions = mutableMapOf<String, MutableList<OlmSession>>()
    private val mutex = Mutex()
    private val secureRandom = SecureRandom()

    companion object {
        private const val MAX_ONE_TIME_KEYS = 100
        private const val ONE_TIME_KEY_ALGORITHM = "signed_curve25519"
        private const val MEGOLM_ALGORITHM = "m.megolm.v1.aes-sha2"
    }

    /**
     * Initialize E2EE by creating Olm account and uploading keys
     */
    suspend fun initialize(): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                // Create new Olm account
                olmAccount = OlmAccount()

                // Generate initial one-time keys
                val keysToGenerate = MAX_ONE_TIME_KEYS
                olmAccount?.generateOneTimeKeys(keysToGenerate)

                // Upload device and one-time keys
                val uploadResult = uploadDeviceKeys()
                if (uploadResult is MatrixResult.Error || uploadResult is MatrixResult.NetworkError) {
                    return@withContext uploadResult
                }

                MatrixResult.Success(Unit)
            } catch (e: OlmException) {
                MatrixResult.Error("OLM_ERROR", "Failed to initialize Olm: ${e.message}")
            } catch (e: Exception) {
                MatrixResult.NetworkError(e)
            }
        }
    }

    /**
     * Upload device identity keys and one-time keys to server
     */
    private suspend fun uploadDeviceKeys(): MatrixResult<KeysUploadResponse> {
        val account = olmAccount ?: return MatrixResult.Error(
            "OLM_NOT_INITIALIZED",
            "Olm account not initialized"
        )

        try {
            // Get identity keys
            val identityKeys = account.identityKeys()
            val ed25519Key = identityKeys.optString("ed25519")
            val curve25519Key = identityKeys.optString("curve25519")

            // Build device keys
            val deviceKeys = DeviceKeys(
                userId = userId,
                deviceId = deviceId,
                algorithms = listOf("m.olm.v1.curve25519-aes-sha2", MEGOLM_ALGORITHM),
                keys = mapOf(
                    "ed25519:$deviceId" to ed25519Key,
                    "curve25519:$deviceId" to curve25519Key
                ),
                signatures = mapOf(
                    userId to mapOf(
                        "ed25519:$deviceId" to signJson(
                            createCanonicalJson(
                                mapOf(
                                    "user_id" to userId,
                                    "device_id" to deviceId,
                                    "algorithms" to listOf("m.olm.v1.curve25519-aes-sha2", MEGOLM_ALGORITHM),
                                    "keys" to mapOf(
                                        "ed25519:$deviceId" to ed25519Key,
                                        "curve25519:$deviceId" to curve25519Key
                                    )
                                )
                            )
                        )
                    )
                )
            )

            // Get one-time keys
            val oneTimeKeys = account.oneTimeKeys()
            val oneTimeKeysMap = mutableMapOf<String, Any>()
            val curve25519Keys = oneTimeKeys.optJSONObject("curve25519")

            curve25519Keys?.let { keys ->
                keys.keys().forEach { keyId ->
                    val key = keys.getString(keyId)
                    oneTimeKeysMap["$ONE_TIME_KEY_ALGORITHM:$keyId"] = key
                }
            }

            // Upload keys
            val request = KeysUploadRequest(
                deviceKeys = deviceKeys,
                oneTimeKeys = oneTimeKeysMap.ifEmpty { null }
            )

            val result = apiClient.uploadKeys(request)

            // Mark keys as published
            if (result is MatrixResult.Success) {
                account.markKeysAsPublished()
            }

            return result
        } catch (e: OlmException) {
            return MatrixResult.Error("OLM_ERROR", "Failed to upload keys: ${e.message}")
        } catch (e: Exception) {
            return MatrixResult.NetworkError(e)
        }
    }

    /**
     * Generate additional one-time keys if needed
     */
    suspend fun ensureOneTimeKeys(): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            val account = olmAccount ?: return@withContext MatrixResult.Error(
                "OLM_NOT_INITIALIZED",
                "Olm account not initialized"
            )

            try {
                // Check how many keys we need
                val maxKeys = account.maxNumberOfOneTimeKeys()
                val currentKeys = account.oneTimeKeys().optJSONObject("curve25519")?.length() ?: 0

                if (currentKeys < maxKeys / 2) {
                    // Generate more keys
                    val keysToGenerate = maxKeys - currentKeys
                    account.generateOneTimeKeys(keysToGenerate)

                    // Upload new keys
                    return@withContext uploadDeviceKeys().let { result ->
                        when (result) {
                            is MatrixResult.Success -> MatrixResult.Success(Unit)
                            is MatrixResult.Error -> result
                            is MatrixResult.NetworkError -> result
                        }
                    }
                }

                MatrixResult.Success(Unit)
            } catch (e: OlmException) {
                MatrixResult.Error("OLM_ERROR", "Failed to generate keys: ${e.message}")
            } catch (e: Exception) {
                MatrixResult.NetworkError(e)
            }
        }
    }

    /**
     * Create outbound Megolm session for room encryption
     *
     * @param roomId Room identifier
     * @return Session ID or error
     */
    suspend fun createOutboundGroupSession(roomId: String): MatrixResult<String> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val session = OlmOutboundGroupSession()
                outboundGroupSessions[roomId] = session

                val sessionId = session.sessionIdentifier()
                MatrixResult.Success(sessionId)
            } catch (e: OlmException) {
                MatrixResult.Error("OLM_ERROR", "Failed to create group session: ${e.message}")
            } catch (e: Exception) {
                MatrixResult.NetworkError(e)
            }
        }
    }

    /**
     * Encrypt message for room using Megolm
     *
     * @param roomId Room identifier
     * @param plaintext Message plaintext
     * @return Encrypted message or error
     */
    suspend fun encryptMessage(roomId: String, plaintext: String): MatrixResult<SendEncryptedMessageRequest> =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    var session = outboundGroupSessions[roomId]

                    // Create session if it doesn't exist
                    if (session == null) {
                        val createResult = createOutboundGroupSession(roomId)
                        if (createResult is MatrixResult.Error || createResult is MatrixResult.NetworkError) {
                            return@withContext MatrixResult.Error(
                                "SESSION_ERROR",
                                "Failed to create encryption session"
                            )
                        }
                        session = outboundGroupSessions[roomId]!!
                    }

                    val account = olmAccount ?: return@withContext MatrixResult.Error(
                        "OLM_NOT_INITIALIZED",
                        "Olm account not initialized"
                    )

                    // Encrypt the message
                    val ciphertext = session.encryptMessage(plaintext)
                    val identityKeys = account.identityKeys()
                    val senderKey = identityKeys.optString("curve25519")

                    val encryptedMessage = SendEncryptedMessageRequest(
                        algorithm = MEGOLM_ALGORITHM,
                        senderKey = senderKey,
                        ciphertext = ciphertext,
                        sessionId = session.sessionIdentifier(),
                        deviceId = deviceId
                    )

                    MatrixResult.Success(encryptedMessage)
                } catch (e: OlmException) {
                    MatrixResult.Error("ENCRYPTION_ERROR", "Failed to encrypt message: ${e.message}")
                } catch (e: Exception) {
                    MatrixResult.NetworkError(e)
                }
            }
        }

    /**
     * Decrypt message using Megolm session
     *
     * @param encryptedMessage Encrypted message content
     * @return Decrypted plaintext or error
     */
    suspend fun decryptMessage(encryptedMessage: Map<String, Any>): MatrixResult<String> =
        withContext(Dispatchers.IO) {
            mutex.withLock {
                try {
                    // Extract encryption parameters
                    val algorithm = encryptedMessage["algorithm"] as? String
                    val ciphertext = encryptedMessage["ciphertext"] as? String
                    val sessionId = encryptedMessage["session_id"] as? String
                    val senderKey = encryptedMessage["sender_key"] as? String

                    if (algorithm != MEGOLM_ALGORITHM || ciphertext == null || sessionId == null || senderKey == null) {
                        return@withContext MatrixResult.Error(
                            "INVALID_ENCRYPTED_MESSAGE",
                            "Missing or invalid encryption parameters"
                        )
                    }

                    // TODO: Implement inbound group session management
                    // For now, return error indicating decryption not fully implemented
                    MatrixResult.Error(
                        "NOT_IMPLEMENTED",
                        "Message decryption requires inbound group session"
                    )
                } catch (e: OlmException) {
                    MatrixResult.Error("DECRYPTION_ERROR", "Failed to decrypt message: ${e.message}")
                } catch (e: Exception) {
                    MatrixResult.NetworkError(e)
                }
            }
        }

    /**
     * Query device keys for other users
     *
     * @param userIds List of user IDs to query
     * @return Device keys or error
     */
    suspend fun queryDeviceKeys(userIds: List<String>): MatrixResult<KeysQueryResponse> {
        val deviceKeys = userIds.associateWith { emptyList<String>() }
        val request = KeysQueryRequest(deviceKeys = deviceKeys)
        return apiClient.queryKeys(request)
    }

    /**
     * Create Olm session with another device
     *
     * @param theirIdentityKey Their curve25519 identity key
     * @param theirOneTimeKey Their one-time key
     * @return Success or error
     */
    suspend fun createOlmSession(
        theirIdentityKey: String,
        theirOneTimeKey: String
    ): MatrixResult<Unit> = withContext(Dispatchers.IO) {
        mutex.withLock {
            try {
                val account = olmAccount ?: return@withContext MatrixResult.Error(
                    "OLM_NOT_INITIALIZED",
                    "Olm account not initialized"
                )

                val session = OlmSession()
                session.initOutboundSession(account, theirIdentityKey, theirOneTimeKey)

                // Store session
                val sessions = olmSessions.getOrPut(theirIdentityKey) { mutableListOf() }
                sessions.add(session)

                MatrixResult.Success(Unit)
            } catch (e: OlmException) {
                MatrixResult.Error("OLM_ERROR", "Failed to create Olm session: ${e.message}")
            } catch (e: Exception) {
                MatrixResult.NetworkError(e)
            }
        }
    }

    /**
     * Get device identity keys
     */
    fun getIdentityKeys(): Map<String, String>? {
        return try {
            val account = olmAccount ?: return null
            val keys = account.identityKeys()
            mapOf(
                "ed25519" to keys.optString("ed25519"),
                "curve25519" to keys.optString("curve25519")
            )
        } catch (e: Exception) {
            null
        }
    }

    /**
     * Sign JSON using device signing key
     */
    private fun signJson(canonicalJson: String): String {
        val account = olmAccount ?: throw OlmException(OlmException.EXCEPTION_CODE_INIT_ACCOUNT_CREATION, "Account not initialized")
        return account.signMessage(canonicalJson)
    }

    /**
     * Create canonical JSON string for signing
     */
    private fun createCanonicalJson(data: Map<String, Any>): String {
        // Simple canonical JSON implementation
        // In production, use proper canonical JSON library
        val sorted = data.entries.sortedBy { it.key }
        return sorted.joinToString(",", "{", "}") { (key, value) ->
            "\"$key\":${jsonValue(value)}"
        }
    }

    private fun jsonValue(value: Any): String {
        return when (value) {
            is String -> "\"$value\""
            is Number -> value.toString()
            is Boolean -> value.toString()
            is List<*> -> value.joinToString(",", "[", "]") { jsonValue(it ?: "null") }
            is Map<*, *> -> {
                val entries = value.entries.sortedBy { it.key.toString() }
                entries.joinToString(",", "{", "}") { (k, v) ->
                    "\"$k\":${jsonValue(v ?: "null")}"
                }
            }
            else -> "\"$value\""
        }
    }

    /**
     * Clean up and release Olm resources
     */
    fun cleanup() {
        olmAccount?.releaseAccount()
        olmAccount = null

        outboundGroupSessions.values.forEach { it.releaseSession() }
        outboundGroupSessions.clear()

        olmSessions.values.flatten().forEach { it.releaseSession() }
        olmSessions.clear()
    }
}
