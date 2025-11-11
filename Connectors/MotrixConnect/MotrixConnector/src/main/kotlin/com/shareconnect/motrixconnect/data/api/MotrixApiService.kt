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

package com.shareconnect.motrixconnect.data.api

import com.shareconnect.motrixconnect.data.model.*

/**
 * Motrix API service interface using Aria2 JSON-RPC protocol.
 *
 * This interface defines all available Aria2/Motrix JSON-RPC methods.
 * Implementations can be either live (making actual HTTP calls) or stub (returning test data).
 */
interface MotrixApiService {

    /**
     * Get Motrix/Aria2 version information
     */
    suspend fun getVersion(): MotrixRpcResponse<MotrixVersion>

    /**
     * Get global statistics
     */
    suspend fun getGlobalStat(): MotrixRpcResponse<MotrixGlobalStat>

    /**
     * Add a new download by URI(s)
     */
    suspend fun addUri(uris: List<String>, options: MotrixDownloadOptions? = null): MotrixRpcResponse<String>

    /**
     * Get download status by GID
     */
    suspend fun tellStatus(gid: String): MotrixRpcResponse<MotrixDownload>

    /**
     * Get list of active downloads
     */
    suspend fun tellActive(): MotrixRpcResponse<List<MotrixDownload>>

    /**
     * Get list of waiting downloads
     */
    suspend fun tellWaiting(offset: Int = 0, limit: Int = 100): MotrixRpcResponse<List<MotrixDownload>>

    /**
     * Get list of stopped downloads
     */
    suspend fun tellStopped(offset: Int = 0, limit: Int = 100): MotrixRpcResponse<List<MotrixDownload>>

    /**
     * Pause a download by GID
     */
    suspend fun pause(gid: String): MotrixRpcResponse<String>

    /**
     * Pause all active downloads
     */
    suspend fun pauseAll(): MotrixRpcResponse<String>

    /**
     * Resume a paused download by GID
     */
    suspend fun unpause(gid: String): MotrixRpcResponse<String>

    /**
     * Resume all paused downloads
     */
    suspend fun unpauseAll(): MotrixRpcResponse<String>

    /**
     * Remove a download by GID
     */
    suspend fun remove(gid: String): MotrixRpcResponse<String>

    /**
     * Force remove a download by GID
     */
    suspend fun forceRemove(gid: String): MotrixRpcResponse<String>

    /**
     * Remove download result by GID
     */
    suspend fun removeDownloadResult(gid: String): MotrixRpcResponse<String>

    /**
     * Get global options
     */
    suspend fun getGlobalOption(): MotrixRpcResponse<Map<String, String>>

    /**
     * Change global options
     */
    suspend fun changeGlobalOption(options: Map<String, String>): MotrixRpcResponse<String>

    /**
     * Get download options by GID
     */
    suspend fun getOption(gid: String): MotrixRpcResponse<Map<String, String>>

    /**
     * Change download options by GID
     */
    suspend fun changeOption(gid: String, options: Map<String, String>): MotrixRpcResponse<String>

    /**
     * Purge completed/error/removed downloads from memory
     */
    suspend fun purgeDownloadResult(): MotrixRpcResponse<String>

    /**
     * Save session (current downloads state)
     */
    suspend fun saveSession(): MotrixRpcResponse<String>

    /**
     * Shutdown Aria2
     */
    suspend fun shutdown(): MotrixRpcResponse<String>

    /**
     * Force shutdown Aria2
     */
    suspend fun forceShutdown(): MotrixRpcResponse<String>
}
