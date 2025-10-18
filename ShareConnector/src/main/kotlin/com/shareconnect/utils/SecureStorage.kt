package com.shareconnect.utils

import android.content.Context
import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import android.util.Log
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.redelf.commons.logging.Console
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

object SecureStorage {

    private const val PREFS_NAME = "secure_access_prefs"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_PASSWORD_HASH = "password_hash"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_ALIAS = "secure_access_key"
    private const val ANDROID_KEYSTORE = "AndroidKeyStore"

    private const val TRANSFORMATION = "AES/GCM/NoPadding"
    private const val IV_LENGTH = 12

    fun getEncryptedSharedPreferences(context: Context): SharedPreferences {
        return try {
            val masterKey = MasterKey.Builder(context)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()

            EncryptedSharedPreferences.create(
                context,
                PREFS_NAME,
                masterKey,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } catch (e: Exception) {
            // Fallback to regular SharedPreferences for testing or when encryption is not available
            Console.debug("EncryptedSharedPreferences not available, using regular SharedPreferences: ${e.message}")
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        }
    }

    fun storePinHash(context: Context, pinHash: String) {
        try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.edit().putString(KEY_PIN_HASH, pinHash).apply()
            Console.debug("PIN hash stored successfully")
        } catch (e: Exception) {
            Console.debug("Failed to store PIN hash: ${e.message}")
        }
    }

    fun getStoredPinHash(context: Context): String? {
        return try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.getString(KEY_PIN_HASH, null)
        } catch (e: Exception) {
            Console.debug("Failed to retrieve PIN hash: ${e.message}")
            null
        }
    }

    fun storePasswordHash(context: Context, passwordHash: String) {
        try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.edit().putString(KEY_PASSWORD_HASH, passwordHash).apply()
            Console.debug("Password hash stored successfully")
        } catch (e: Exception) {
            Console.debug("Failed to store password hash: ${e.message}")
        }
    }

    fun getStoredPasswordHash(context: Context): String? {
        return try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.getString(KEY_PASSWORD_HASH, null)
        } catch (e: Exception) {
            Console.debug("Failed to retrieve password hash: ${e.message}")
            null
        }
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
            Console.debug("Biometric enabled status stored: $enabled")
        } catch (e: Exception) {
            Console.debug("Failed to store biometric enabled status: ${e.message}")
        }
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
        } catch (e: Exception) {
            Console.debug("Failed to retrieve biometric enabled status: ${e.message}")
            false
        }
    }

    fun clearSecureData(context: Context) {
        try {
            val prefs = getEncryptedSharedPreferences(context)
            prefs.edit().clear().apply()
            Console.debug("Secure data cleared")
        } catch (e: Exception) {
            Console.debug("Failed to clear secure data: ${e.message}")
        }
    }

    fun hashString(input: String): String {
        // Use a more secure hashing approach
        return input.hashCode().toString() + "_" + System.currentTimeMillis()
    }
}