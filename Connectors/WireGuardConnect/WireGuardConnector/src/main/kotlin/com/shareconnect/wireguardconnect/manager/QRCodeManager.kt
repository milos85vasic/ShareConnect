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

import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.shareconnect.wireguardconnect.data.models.WireGuardConfig
import com.shareconnect.wireguardconnect.data.models.WireGuardResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Manages QR code generation and scanning for WireGuard configurations.
 */
class QRCodeManager {

    companion object {
        private const val TAG = "QRCodeManager"
        private const val DEFAULT_QR_SIZE = 512
        private const val QR_CODE_MARGIN = 1

        @Volatile
        private var instance: QRCodeManager? = null

        fun getInstance(): QRCodeManager {
            return instance ?: synchronized(this) {
                instance ?: QRCodeManager().also {
                    instance = it
                }
            }
        }
    }

    /**
     * Generates a QR code bitmap from a WireGuard configuration.
     */
    suspend fun generateQRCode(
        config: WireGuardConfig,
        size: Int = DEFAULT_QR_SIZE
    ): WireGuardResult<Bitmap> = withContext(Dispatchers.Default) {
        try {
            val configContent = config.toConfigString()

            // Validate size
            if (configContent.length > 2953) {
                return@withContext WireGuardResult.Error(
                    "Configuration is too large for QR code (max 2953 bytes)"
                )
            }

            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN to QR_CODE_MARGIN,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            val bitMatrix = MultiFormatWriter().encode(
                configContent,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val bitmap = createBitmapFromBitMatrix(bitMatrix)
            Log.d(TAG, "QR code generated successfully for config: ${config.name}")
            WireGuardResult.Success(bitmap)

        } catch (e: Exception) {
            Log.e(TAG, "Error generating QR code", e)
            WireGuardResult.Error("Failed to generate QR code: ${e.message}", e)
        }
    }

    /**
     * Generates a QR code bitmap from a config string.
     */
    suspend fun generateQRCodeFromString(
        configContent: String,
        size: Int = DEFAULT_QR_SIZE
    ): WireGuardResult<Bitmap> = withContext(Dispatchers.Default) {
        try {
            // Validate size
            if (configContent.length > 2953) {
                return@withContext WireGuardResult.Error(
                    "Configuration is too large for QR code (max 2953 bytes)"
                )
            }

            val hints = mapOf(
                EncodeHintType.ERROR_CORRECTION to ErrorCorrectionLevel.M,
                EncodeHintType.MARGIN to QR_CODE_MARGIN,
                EncodeHintType.CHARACTER_SET to "UTF-8"
            )

            val bitMatrix = MultiFormatWriter().encode(
                configContent,
                BarcodeFormat.QR_CODE,
                size,
                size,
                hints
            )

            val bitmap = createBitmapFromBitMatrix(bitMatrix)
            Log.d(TAG, "QR code generated successfully from string")
            WireGuardResult.Success(bitmap)

        } catch (e: Exception) {
            Log.e(TAG, "Error generating QR code", e)
            WireGuardResult.Error("Failed to generate QR code: ${e.message}", e)
        }
    }

    /**
     * Parses a WireGuard configuration from a QR code scan result.
     */
    suspend fun parseQRCodeContent(
        name: String,
        content: String
    ): WireGuardResult<WireGuardConfig> = withContext(Dispatchers.IO) {
        try {
            // Validate that content looks like a WireGuard config
            if (!content.contains("[Interface]")) {
                return@withContext WireGuardResult.Error(
                    "QR code does not contain a valid WireGuard configuration"
                )
            }

            val config = WireGuardConfig.fromConfigString(name, content)
            Log.d(TAG, "QR code parsed successfully: ${config.name}")
            WireGuardResult.Success(config)

        } catch (e: Exception) {
            Log.e(TAG, "Error parsing QR code content", e)
            WireGuardResult.Error("Failed to parse QR code: ${e.message}", e)
        }
    }

    /**
     * Creates a bitmap from a BitMatrix.
     */
    private fun createBitmapFromBitMatrix(bitMatrix: BitMatrix): Bitmap {
        val width = bitMatrix.width
        val height = bitMatrix.height
        val pixels = IntArray(width * height)

        for (y in 0 until height) {
            val offset = y * width
            for (x in 0 until width) {
                pixels[offset + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
            }
        }

        return Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565).apply {
            setPixels(pixels, 0, width, 0, 0, width, height)
        }
    }

    /**
     * Estimates the size of a QR code for a given configuration.
     */
    fun estimateQRCodeSize(config: WireGuardConfig): Int {
        val configContent = config.toConfigString()
        return configContent.length
    }

    /**
     * Checks if a configuration can be encoded as a QR code.
     */
    fun canEncodeAsQRCode(config: WireGuardConfig): Boolean {
        val size = estimateQRCodeSize(config)
        return size <= 2953 // Maximum size for QR code with error correction level M
    }

    /**
     * Gets the maximum supported configuration size for QR codes.
     */
    fun getMaxQRCodeSize(): Int = 2953

    /**
     * Creates a shareable QR code image with branding.
     */
    suspend fun createShareableQRCode(
        config: WireGuardConfig,
        size: Int = DEFAULT_QR_SIZE,
        includeLabel: Boolean = true
    ): WireGuardResult<Bitmap> = withContext(Dispatchers.Default) {
        try {
            val qrResult = generateQRCode(config, size)

            when (qrResult) {
                is WireGuardResult.Success -> {
                    val qrBitmap = qrResult.data

                    if (includeLabel) {
                        // In a full implementation, you would add a label with the config name
                        // For now, return the QR code as is
                        WireGuardResult.Success(qrBitmap)
                    } else {
                        WireGuardResult.Success(qrBitmap)
                    }
                }
                is WireGuardResult.Error -> qrResult
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error creating shareable QR code", e)
            WireGuardResult.Error("Failed to create shareable QR code: ${e.message}", e)
        }
    }
}
