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


package com.shareconnect.wireguardconnect.data.models

import kotlinx.serialization.Serializable
import java.util.UUID

/**
 * Represents a complete WireGuard configuration.
 */
@Serializable
data class WireGuardConfig(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val wgInterface: WireGuardInterface,
    val peers: List<WireGuardPeer>,
    val createdAt: Long = System.currentTimeMillis(),
    val lastModified: Long = System.currentTimeMillis(),
    val isActive: Boolean = false
) {
    /**
     * Converts this configuration to WireGuard INI format.
     */
    fun toConfigString(): String {
        val builder = StringBuilder()

        // Interface section
        builder.appendLine("[Interface]")
        builder.appendLine("PrivateKey = ${wgInterface.privateKey}")
        if (wgInterface.address.isNotEmpty()) {
            builder.appendLine("Address = ${wgInterface.address.joinToString(", ")}")
        }
        if (wgInterface.dns.isNotEmpty()) {
            builder.appendLine("DNS = ${wgInterface.dns.joinToString(", ")}")
        }
        if (wgInterface.mtu != null) {
            builder.appendLine("MTU = ${wgInterface.mtu}")
        }
        if (wgInterface.listenPort != null) {
            builder.appendLine("ListenPort = ${wgInterface.listenPort}")
        }
        if (wgInterface.table != null) {
            builder.appendLine("Table = ${wgInterface.table}")
        }
        if (wgInterface.preUp != null) {
            builder.appendLine("PreUp = ${wgInterface.preUp}")
        }
        if (wgInterface.postUp != null) {
            builder.appendLine("PostUp = ${wgInterface.postUp}")
        }
        if (wgInterface.preDown != null) {
            builder.appendLine("PreDown = ${wgInterface.preDown}")
        }
        if (wgInterface.postDown != null) {
            builder.appendLine("PostDown = ${wgInterface.postDown}")
        }

        // Peer sections
        peers.forEach { peer ->
            builder.appendLine()
            builder.appendLine("[Peer]")
            builder.appendLine("PublicKey = ${peer.publicKey}")
            if (peer.presharedKey != null) {
                builder.appendLine("PresharedKey = ${peer.presharedKey}")
            }
            if (peer.allowedIps.isNotEmpty()) {
                builder.appendLine("AllowedIPs = ${peer.allowedIps.joinToString(", ")}")
            }
            if (peer.endpoint != null) {
                builder.appendLine("Endpoint = ${peer.endpoint}")
            }
            if (peer.persistentKeepalive != null) {
                builder.appendLine("PersistentKeepalive = ${peer.persistentKeepalive}")
            }
        }

        return builder.toString()
    }

    companion object {
        /**
         * Parses a WireGuard config file into a WireGuardConfig object.
         */
        fun fromConfigString(name: String, configContent: String): WireGuardConfig {
            val lines = configContent.lines().map { it.trim() }
            var interfaceSection: WireGuardInterface? = null
            val peers = mutableListOf<WireGuardPeer>()

            var currentSection = ""
            val sectionData = mutableMapOf<String, String>()

            fun parseKeyValue(line: String): Pair<String, String>? {
                val parts = line.split("=", limit = 2)
                if (parts.size != 2) return null
                return parts[0].trim() to parts[1].trim()
            }

            fun parseSectionData() {
                when (currentSection.lowercase()) {
                    "interface" -> {
                        interfaceSection = WireGuardInterface(
                            privateKey = sectionData["privatekey"] ?: "",
                            address = sectionData["address"]?.split(",")?.map { it.trim() } ?: emptyList(),
                            dns = sectionData["dns"]?.split(",")?.map { it.trim() } ?: emptyList(),
                            mtu = sectionData["mtu"]?.toIntOrNull(),
                            listenPort = sectionData["listenport"]?.toIntOrNull(),
                            table = sectionData["table"],
                            preUp = sectionData["preup"],
                            postUp = sectionData["postup"],
                            preDown = sectionData["predown"],
                            postDown = sectionData["postdown"]
                        )
                    }
                    "peer" -> {
                        peers.add(
                            WireGuardPeer(
                                publicKey = sectionData["publickey"] ?: "",
                                presharedKey = sectionData["presharedkey"],
                                allowedIps = sectionData["allowedips"]?.split(",")?.map { it.trim() } ?: emptyList(),
                                endpoint = sectionData["endpoint"],
                                persistentKeepalive = sectionData["persistentkeepalive"]?.toIntOrNull()
                            )
                        )
                    }
                }
            }

            for (line in lines) {
                if (line.isEmpty() || line.startsWith("#")) continue

                if (line.startsWith("[") && line.endsWith("]")) {
                    // Parse previous section
                    if (currentSection.isNotEmpty()) {
                        parseSectionData()
                        sectionData.clear()
                    }
                    currentSection = line.substring(1, line.length - 1)
                } else {
                    parseKeyValue(line)?.let { (key, value) ->
                        sectionData[key.lowercase()] = value
                    }
                }
            }

            // Parse final section
            if (currentSection.isNotEmpty()) {
                parseSectionData()
            }

            return WireGuardConfig(
                name = name,
                wgInterface = interfaceSection ?: throw IllegalArgumentException("No [Interface] section found"),
                peers = peers
            )
        }
    }
}

/**
 * Represents the [Interface] section of a WireGuard config.
 */
@Serializable
data class WireGuardInterface(
    val privateKey: String,
    val address: List<String> = emptyList(),
    val dns: List<String> = emptyList(),
    val mtu: Int? = null,
    val listenPort: Int? = null,
    val table: String? = null,
    val preUp: String? = null,
    val postUp: String? = null,
    val preDown: String? = null,
    val postDown: String? = null
)

/**
 * Represents a [Peer] section of a WireGuard config.
 */
@Serializable
data class WireGuardPeer(
    val publicKey: String,
    val presharedKey: String? = null,
    val allowedIps: List<String> = emptyList(),
    val endpoint: String? = null,
    val persistentKeepalive: Int? = null
)

/**
 * Represents an active WireGuard tunnel.
 */
@Serializable
data class WireGuardTunnel(
    val configId: String,
    val name: String,
    val isActive: Boolean,
    val startTime: Long? = null,
    val statistics: WireGuardStatistics? = null
)

/**
 * Represents WireGuard tunnel statistics.
 */
@Serializable
data class WireGuardStatistics(
    val bytesReceived: Long = 0,
    val bytesSent: Long = 0,
    val lastHandshakeTime: Long? = null,
    val peersConnected: Int = 0
)

/**
 * Represents the result of a WireGuard operation.
 */
sealed class WireGuardResult<out T> {
    data class Success<T>(val data: T) : WireGuardResult<T>()
    data class Error(val message: String, val exception: Throwable? = null) : WireGuardResult<Nothing>()
}

/**
 * Key pair for WireGuard.
 */
@Serializable
data class WireGuardKeyPair(
    val privateKey: String,
    val publicKey: String
)
