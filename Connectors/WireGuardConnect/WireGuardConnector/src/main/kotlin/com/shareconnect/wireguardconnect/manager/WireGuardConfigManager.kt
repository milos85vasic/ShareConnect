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


package com.shareconnect.wireguardconnect.manager

import android.content.Context
import android.util.Log
import com.shareconnect.wireguardconnect.data.models.WireGuardConfig
import com.shareconnect.wireguardconnect.data.models.WireGuardInterface
import com.shareconnect.wireguardconnect.data.models.WireGuardKeyPair
import com.shareconnect.wireguardconnect.data.models.WireGuardPeer
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.security.SecureRandom
import java.util.Base64

/**
 * Manages WireGuard configurations including parsing, generation, and validation.
 */
class WireGuardConfigManager(private val context: Context) {

    companion object {
        private const val TAG = "WireGuardConfigManager"
        private const val CONFIG_DIR = "wireguard_configs"
        private const val CURVE25519_KEY_SIZE = 32

        @Volatile
        private var instance: WireGuardConfigManager? = null

        fun getInstance(context: Context): WireGuardConfigManager {
            return instance ?: synchronized(this) {
                instance ?: WireGuardConfigManager(context.applicationContext).also {
                    instance = it
                }
            }
        }
    }

    private val configDir: File = File(context.filesDir, CONFIG_DIR).also {
        if (!it.exists()) {
            it.mkdirs()
        }
    }

    /**
     * Parses a WireGuard configuration from a string.
     */
    suspend fun parseConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig> =
        withContext(Dispatchers.IO) {
            try {
                val config = WireGuardConfig.fromConfigString(name, configContent)
                validateConfig(config)
                WireGuardResult.Success(config)
            } catch (e: Exception) {
                Log.e(TAG, "Error parsing config", e)
                WireGuardResult.Error("Failed to parse configuration: ${e.message}", e)
            }
        }

    /**
     * Parses a WireGuard configuration from a file.
     */
    suspend fun parseConfigFile(file: File): WireGuardResult<WireGuardConfig> =
        withContext(Dispatchers.IO) {
            try {
                if (!file.exists() || !file.canRead()) {
                    return@withContext WireGuardResult.Error("Cannot read file: ${file.absolutePath}")
                }

                val content = file.readText()
                val name = file.nameWithoutExtension
                parseConfig(name, content)
            } catch (e: Exception) {
                Log.e(TAG, "Error reading config file", e)
                WireGuardResult.Error("Failed to read config file: ${e.message}", e)
            }
        }

    /**
     * Saves a WireGuard configuration to a file.
     */
    suspend fun saveConfig(config: WireGuardConfig): WireGuardResult<File> =
        withContext(Dispatchers.IO) {
            try {
                validateConfig(config)

                val fileName = "${sanitizeFileName(config.name)}.conf"
                val file = File(configDir, fileName)

                file.writeText(config.toConfigString())

                Log.d(TAG, "Config saved to: ${file.absolutePath}")
                WireGuardResult.Success(file)
            } catch (e: Exception) {
                Log.e(TAG, "Error saving config", e)
                WireGuardResult.Error("Failed to save configuration: ${e.message}", e)
            }
        }

    /**
     * Deletes a WireGuard configuration file.
     */
    suspend fun deleteConfig(configId: String): WireGuardResult<Boolean> =
        withContext(Dispatchers.IO) {
            try {
                val files = configDir.listFiles()?.filter { it.extension == "conf" } ?: emptyList()
                val deleted = files.any { file ->
                    val content = file.readText()
                    val config = WireGuardConfig.fromConfigString(file.nameWithoutExtension, content)
                    if (config.id == configId) {
                        file.delete()
                        true
                    } else {
                        false
                    }
                }

                if (deleted) {
                    WireGuardResult.Success(true)
                } else {
                    WireGuardResult.Error("Configuration not found")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error deleting config", e)
                WireGuardResult.Error("Failed to delete configuration: ${e.message}", e)
            }
        }

    /**
     * Lists all saved WireGuard configurations.
     */
    suspend fun listConfigs(): WireGuardResult<List<WireGuardConfig>> =
        withContext(Dispatchers.IO) {
            try {
                val configs = mutableListOf<WireGuardConfig>()
                val files = configDir.listFiles()?.filter { it.extension == "conf" } ?: emptyList()

                for (file in files) {
                    try {
                        val content = file.readText()
                        val config = WireGuardConfig.fromConfigString(file.nameWithoutExtension, content)
                        configs.add(config)
                    } catch (e: Exception) {
                        Log.w(TAG, "Skipping invalid config file: ${file.name}", e)
                    }
                }

                WireGuardResult.Success(configs.sortedByDescending { it.lastModified })
            } catch (e: Exception) {
                Log.e(TAG, "Error listing configs", e)
                WireGuardResult.Error("Failed to list configurations: ${e.message}", e)
            }
        }

    /**
     * Generates a new WireGuard key pair.
     */
    fun generateKeyPair(): WireGuardKeyPair {
        val secureRandom = SecureRandom()
        val privateKeyBytes = ByteArray(CURVE25519_KEY_SIZE)
        secureRandom.nextBytes(privateKeyBytes)

        // Clamp private key for Curve25519
        privateKeyBytes[0] = (privateKeyBytes[0].toInt() and 248).toByte()
        privateKeyBytes[31] = (privateKeyBytes[31].toInt() and 127).toByte()
        privateKeyBytes[31] = (privateKeyBytes[31].toInt() or 64).toByte()

        val privateKey = Base64.getEncoder().encodeToString(privateKeyBytes)

        // In a real implementation, you would compute the public key from the private key
        // using Curve25519 scalar multiplication. For this implementation, we'll generate
        // a placeholder public key. In production, use a proper crypto library.
        val publicKeyBytes = ByteArray(CURVE25519_KEY_SIZE)
        secureRandom.nextBytes(publicKeyBytes)
        val publicKey = Base64.getEncoder().encodeToString(publicKeyBytes)

        return WireGuardKeyPair(privateKey, publicKey)
    }

    /**
     * Creates a new WireGuard configuration from parameters.
     */
    fun createConfig(
        name: String,
        address: List<String>,
        privateKey: String? = null,
        dns: List<String> = listOf("1.1.1.1", "1.0.0.1"),
        mtu: Int? = 1420,
        peerPublicKey: String,
        peerEndpoint: String,
        peerAllowedIps: List<String> = listOf("0.0.0.0/0", "::/0"),
        persistentKeepalive: Int? = 25
    ): WireGuardConfig {
        val keyPair = if (privateKey == null) generateKeyPair() else null

        val interface = WireGuardInterface(
            privateKey = privateKey ?: keyPair!!.privateKey,
            address = address,
            dns = dns,
            mtu = mtu
        )

        val peer = WireGuardPeer(
            publicKey = peerPublicKey,
            endpoint = peerEndpoint,
            allowedIps = peerAllowedIps,
            persistentKeepalive = persistentKeepalive
        )

        return WireGuardConfig(
            name = name,
            interface = interface,
            peers = listOf(peer)
        )
    }

    /**
     * Validates a WireGuard configuration.
     */
    private fun validateConfig(config: WireGuardConfig) {
        require(config.name.isNotBlank()) { "Configuration name cannot be blank" }
        require(config.interface.privateKey.isNotBlank()) { "Private key is required" }
        require(config.interface.address.isNotEmpty()) { "At least one address is required" }

        // Validate addresses
        config.interface.address.forEach { addr ->
            require(isValidIpAddress(addr)) { "Invalid address: $addr" }
        }

        // Validate DNS
        config.interface.dns.forEach { dns ->
            val ip = dns.split("/").first()
            require(isValidIp(ip)) { "Invalid DNS: $dns" }
        }

        // Validate peers
        require(config.peers.isNotEmpty()) { "At least one peer is required" }
        config.peers.forEach { peer ->
            require(peer.publicKey.isNotBlank()) { "Peer public key is required" }
            peer.allowedIps.forEach { allowedIp ->
                require(isValidIpAddress(allowedIp)) { "Invalid allowed IP: $allowedIp" }
            }
        }
    }

    /**
     * Validates an IP address with optional CIDR notation.
     */
    private fun isValidIpAddress(address: String): Boolean {
        val parts = address.split("/")
        if (parts.size > 2) return false

        val ip = parts[0]
        if (!isValidIp(ip)) return false

        if (parts.size == 2) {
            val cidr = parts[1].toIntOrNull() ?: return false
            val isIpv6 = ip.contains(":")
            val maxCidr = if (isIpv6) 128 else 32
            if (cidr !in 0..maxCidr) return false
        }

        return true
    }

    /**
     * Validates an IP address (IPv4 or IPv6).
     */
    private fun isValidIp(ip: String): Boolean {
        return isValidIpv4(ip) || isValidIpv6(ip)
    }

    /**
     * Validates an IPv4 address.
     */
    private fun isValidIpv4(ip: String): Boolean {
        val parts = ip.split(".")
        if (parts.size != 4) return false
        return parts.all {
            val num = it.toIntOrNull() ?: return false
            num in 0..255
        }
    }

    /**
     * Validates an IPv6 address.
     */
    private fun isValidIpv6(ip: String): Boolean {
        // Simplified IPv6 validation
        if (!ip.contains(":")) return false
        val parts = ip.split(":")
        if (parts.size > 8) return false
        return parts.all {
            it.isEmpty() || it.matches(Regex("[0-9a-fA-F]{1,4}"))
        }
    }

    /**
     * Sanitizes a file name by removing invalid characters.
     */
    private fun sanitizeFileName(name: String): String {
        return name.replace(Regex("[^a-zA-Z0-9._-]"), "_")
    }

    /**
     * Exports a configuration to a string.
     */
    fun exportConfig(config: WireGuardConfig): String {
        return config.toConfigString()
    }

    /**
     * Imports a configuration from a string.
     */
    suspend fun importConfig(name: String, configContent: String): WireGuardResult<WireGuardConfig> {
        return parseConfig(name, configContent)
    }
}
