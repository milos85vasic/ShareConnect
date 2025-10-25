package com.shareconnect.seafileconnect.data.encryption

import android.util.Base64
import android.util.Log
import org.apache.commons.codec.binary.Hex
import java.security.MessageDigest
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.Mac
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * Seafile Encryption Manager
 * 
 * Handles encryption and decryption for Seafile encrypted libraries.
 * Seafile uses AES-256-CBC for library encryption with PBKDF2 key derivation.
 */
class SeafileEncryptionManager {

    companion object {
        private const val TAG = "SeafileEncryption"
        private const val CIPHER_ALGORITHM = "AES/CBC/PKCS5Padding"
        private const val KEY_ALGORITHM = "AES"
        private const val HMAC_ALGORITHM = "HmacSHA256"
        private const val HASH_ALGORITHM = "SHA-256"
        private const val KEY_SIZE = 32 // 256 bits
        private const val IV_SIZE = 16 // 128 bits
        private const val PBKDF2_ITERATIONS = 10000
    }

    /**
     * Derive encryption key from password using PBKDF2
     */
    fun deriveKey(password: String, salt: ByteArray, iterations: Int = PBKDF2_ITERATIONS): ByteArray {
        try {
            val keySpec = javax.crypto.spec.PBEKeySpec(
                password.toCharArray(),
                salt,
                iterations,
                KEY_SIZE * 8
            )
            val keyFactory = javax.crypto.SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1")
            return keyFactory.generateSecret(keySpec).encoded
        } catch (e: Exception) {
            Log.e(TAG, "Key derivation failed", e)
            throw e
        }
    }

    /**
     * Generate SHA-256 hash of data
     */
    fun sha256(data: ByteArray): ByteArray {
        val digest = MessageDigest.getInstance(HASH_ALGORITHM)
        return digest.digest(data)
    }

    /**
     * Generate SHA-256 hash of string
     */
    fun sha256(data: String): ByteArray = sha256(data.toByteArray())

    /**
     * Convert byte array to hex string
     */
    fun toHex(data: ByteArray): String = Hex.encodeHexString(data)

    /**
     * Convert hex string to byte array
     */
    fun fromHex(hex: String): ByteArray = Hex.decodeHex(hex.toCharArray())

    /**
     * Encrypt data with AES-256-CBC
     */
    fun encrypt(data: ByteArray, key: ByteArray, iv: ByteArray? = null): EncryptedData {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val keySpec = SecretKeySpec(key, KEY_ALGORITHM)
            
            val ivBytes = iv ?: ByteArray(IV_SIZE).apply {
                SecureRandom().nextBytes(this)
            }
            val ivSpec = IvParameterSpec(ivBytes)
            
            cipher.init(Cipher.ENCRYPT_MODE, keySpec, ivSpec)
            val encrypted = cipher.doFinal(data)
            
            return EncryptedData(encrypted, ivBytes)
        } catch (e: Exception) {
            Log.e(TAG, "Encryption failed", e)
            throw e
        }
    }

    /**
     * Decrypt data with AES-256-CBC
     */
    fun decrypt(encryptedData: ByteArray, key: ByteArray, iv: ByteArray): ByteArray {
        try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val keySpec = SecretKeySpec(key, KEY_ALGORITHM)
            val ivSpec = IvParameterSpec(iv)
            
            cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec)
            return cipher.doFinal(encryptedData)
        } catch (e: Exception) {
            Log.e(TAG, "Decryption failed", e)
            throw e
        }
    }

    /**
     * Calculate HMAC-SHA256
     */
    fun hmacSha256(data: ByteArray, key: ByteArray): ByteArray {
        try {
            val mac = Mac.getInstance(HMAC_ALGORITHM)
            val keySpec = SecretKeySpec(key, HMAC_ALGORITHM)
            mac.init(keySpec)
            return mac.doFinal(data)
        } catch (e: Exception) {
            Log.e(TAG, "HMAC calculation failed", e)
            throw e
        }
    }

    /**
     * Verify HMAC-SHA256
     */
    fun verifyHmac(data: ByteArray, key: ByteArray, expectedHmac: ByteArray): Boolean {
        val actualHmac = hmacSha256(data, key)
        return actualHmac.contentEquals(expectedHmac)
    }

    /**
     * Generate random salt
     */
    fun generateSalt(size: Int = KEY_SIZE): ByteArray {
        return ByteArray(size).apply {
            SecureRandom().nextBytes(this)
        }
    }

    /**
     * Generate random IV
     */
    fun generateIV(): ByteArray = ByteArray(IV_SIZE).apply {
        SecureRandom().nextBytes(this)
    }

    /**
     * Encode to Base64
     */
    fun toBase64(data: ByteArray): String {
        return Base64.encodeToString(data, Base64.NO_WRAP)
    }

    /**
     * Decode from Base64
     */
    fun fromBase64(base64: String): ByteArray {
        return Base64.decode(base64, Base64.NO_WRAP)
    }

    /**
     * Derive library encryption key from password
     * This matches Seafile's library encryption scheme
     */
    fun deriveLibraryKey(password: String, repoId: String): LibraryKey {
        // Seafile uses repo_id as part of salt derivation
        val salt = sha256("$repoId$password")
        val key = deriveKey(password, salt)
        
        return LibraryKey(
            key = key,
            salt = salt,
            repoId = repoId
        )
    }

    /**
     * Encrypt file name (for encrypted libraries)
     */
    fun encryptFilename(filename: String, key: ByteArray): String {
        val filenameBytes = filename.toByteArray()
        val encrypted = encrypt(filenameBytes, key)
        return toBase64(encrypted.iv + encrypted.data)
    }

    /**
     * Decrypt file name (for encrypted libraries)
     */
    fun decryptFilename(encryptedFilename: String, key: ByteArray): String {
        val combined = fromBase64(encryptedFilename)
        val iv = combined.sliceArray(0 until IV_SIZE)
        val data = combined.sliceArray(IV_SIZE until combined.size)
        val decrypted = decrypt(data, key, iv)
        return String(decrypted)
    }

    /**
     * Data class for encrypted data with IV
     */
    data class EncryptedData(
        val data: ByteArray,
        val iv: ByteArray
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as EncryptedData

            if (!data.contentEquals(other.data)) return false
            if (!iv.contentEquals(other.iv)) return false

            return true
        }

        override fun hashCode(): Int {
            var result = data.contentHashCode()
            result = 31 * result + iv.contentHashCode()
            return result
        }
    }

    /**
     * Data class for library encryption key
     */
    data class LibraryKey(
        val key: ByteArray,
        val salt: ByteArray,
        val repoId: String
    ) {
        override fun equals(other: Any?): Boolean {
            if (this === other) return true
            if (javaClass != other?.javaClass) return false

            other as LibraryKey

            if (!key.contentEquals(other.key)) return false
            if (!salt.contentEquals(other.salt)) return false
            if (repoId != other.repoId) return false

            return true
        }

        override fun hashCode(): Int {
            var result = key.contentHashCode()
            result = 31 * result + salt.contentHashCode()
            result = 31 * result + repoId.hashCode()
            return result
        }
    }
}
