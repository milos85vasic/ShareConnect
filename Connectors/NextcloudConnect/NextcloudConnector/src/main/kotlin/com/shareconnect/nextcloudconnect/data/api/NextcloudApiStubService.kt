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

package com.shareconnect.nextcloudconnect.data.api

import com.shareconnect.nextcloudconnect.data.model.*
import kotlinx.coroutines.delay
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response

/**
 * Stub implementation of NextcloudApiService for testing and UI development.
 * Provides realistic responses using NextcloudTestData without requiring a live Nextcloud server.
 *
 * Features:
 * - WebDAV file operations simulation
 * - OCS API v2 responses
 * - Realistic response delays
 * - Complete coverage of all API endpoints
 * - Uses NextcloudTestData for consistent test data
 */
class NextcloudApiStubService : NextcloudApiService {

    companion object {
        // Simulated network delay in milliseconds
        private const val NETWORK_DELAY_MS = 500L

        // In-memory file system state
        private val fileSystem = mutableMapOf<String, NextcloudFile>()
        private val shares = mutableMapOf<String, NextcloudShare>()
        private var nextFileId = 100
        private var nextShareId = 2000

        init {
            // Initialize with test data
            resetState()
        }

        /**
         * Reset all stub state (useful for testing).
         */
        fun resetState() {
            fileSystem.clear()
            shares.clear()
            nextFileId = 100
            nextShareId = 2000

            // Add test files to file system
            NextcloudTestData.testAllFiles.forEach { file ->
                fileSystem[file.path] = file
            }

            // Add test shares
            NextcloudTestData.testShares.forEach { share ->
                shares[share.id] = share
            }
        }
    }

    private fun validateAuth(authorization: String): Boolean {
        // In stub mode, accept any non-empty authorization header
        return authorization.isNotBlank()
    }

    // Server Status
    override suspend fun getServerStatus(): Response<NextcloudStatus> {
        delay(NETWORK_DELAY_MS)
        return Response.success(NextcloudTestData.testServerStatus)
    }

    // User Information
    override suspend fun getUserInfo(authorization: String): Response<NextcloudResponse<NextcloudUser>> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val response = NextcloudTestData.createUserResponse()
        return Response.success(response)
    }

    // File Listing (WebDAV)
    override suspend fun listFiles(
        userId: String,
        path: String,
        authorization: String,
        requestBody: String
    ): Response<String> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.isEmpty() || path == "/") "" else "/$path".removeSuffix("/")
        val files = NextcloudTestData.getFilesByPath(normalizedPath)

        if (files.isEmpty() && normalizedPath.isNotEmpty()) {
            // Path doesn't exist
            return Response.error(404, "Path not found".toResponseBody())
        }

        val xmlResponse = NextcloudTestData.createWebDavXmlResponse(files)
        return Response.success(xmlResponse)
    }

    // File Download
    override suspend fun downloadFile(
        userId: String,
        path: String,
        authorization: String
    ): Response<ResponseBody> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val file = fileSystem[normalizedPath]

        if (file == null || file.type == "directory") {
            return Response.error(404, "File not found".toResponseBody())
        }

        // Return dummy file content
        val content = "Stub file content for ${file.name}".toByteArray()
        val responseBody = content.toResponseBody(file.mimeType?.toMediaTypeOrNull())

        return Response.success(responseBody)
    }

    // File Upload
    override suspend fun uploadFile(
        userId: String,
        path: String,
        authorization: String,
        file: RequestBody
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"

        // Check if parent directory exists
        val parentPath = normalizedPath.substringBeforeLast("/")
        if (parentPath.isNotEmpty() && !fileSystem.containsKey(parentPath)) {
            return Response.error(409, "Parent directory does not exist".toResponseBody())
        }

        // Add file to file system
        val fileName = normalizedPath.substringAfterLast("/")
        val newFile = NextcloudFile(
            id = (nextFileId++).toString(),
            name = fileName,
            path = normalizedPath,
            type = "file",
            size = 1024, // Dummy size
            modifiedTime = System.currentTimeMillis(),
            mimeType = "application/octet-stream",
            etag = "etag-${System.currentTimeMillis()}",
            permissions = "RWNVD"
        )

        fileSystem[normalizedPath] = newFile

        return Response.success(Unit)
    }

    // Create Folder
    override suspend fun createFolder(
        userId: String,
        path: String,
        authorization: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"

        // Check if already exists
        if (fileSystem.containsKey(normalizedPath)) {
            return Response.error(405, "Path already exists".toResponseBody())
        }

        // Check if parent directory exists
        val parentPath = normalizedPath.substringBeforeLast("/")
        if (parentPath.isNotEmpty() && !fileSystem.containsKey(parentPath)) {
            return Response.error(409, "Parent directory does not exist".toResponseBody())
        }

        // Create folder
        val folderName = normalizedPath.substringAfterLast("/")
        val newFolder = NextcloudFile(
            id = (nextFileId++).toString(),
            name = folderName,
            path = normalizedPath,
            type = "directory",
            size = 0,
            modifiedTime = System.currentTimeMillis(),
            mimeType = "httpd/unix-directory",
            etag = "etag-${System.currentTimeMillis()}",
            permissions = "RDNVCK"
        )

        fileSystem[normalizedPath] = newFolder

        return Response.success(Unit)
    }

    // Delete
    override suspend fun delete(
        userId: String,
        path: String,
        authorization: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"

        if (!fileSystem.containsKey(normalizedPath)) {
            return Response.error(404, "Path not found".toResponseBody())
        }

        // Remove from file system
        fileSystem.remove(normalizedPath)

        // Remove any shares for this path
        shares.values.removeIf { it.path == normalizedPath }

        return Response.success(Unit)
    }

    // Move/Rename
    override suspend fun move(
        userId: String,
        sourcePath: String,
        authorization: String,
        destination: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedSource = if (sourcePath.startsWith("/")) sourcePath else "/$sourcePath"

        // Extract destination path from full URL
        val destPath = destination.substringAfter("/files/$userId").let {
            if (it.startsWith("/")) it else "/$it"
        }

        val sourceFile = fileSystem[normalizedSource]
            ?: return Response.error(404, "Source not found".toResponseBody())

        if (fileSystem.containsKey(destPath)) {
            return Response.error(412, "Destination already exists".toResponseBody())
        }

        // Move file
        fileSystem.remove(normalizedSource)
        val movedFile = sourceFile.copy(
            path = destPath,
            name = destPath.substringAfterLast("/")
        )
        fileSystem[destPath] = movedFile

        return Response.success(Unit)
    }

    // Copy
    override suspend fun copy(
        userId: String,
        sourcePath: String,
        authorization: String,
        destination: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedSource = if (sourcePath.startsWith("/")) sourcePath else "/$sourcePath"

        // Extract destination path from full URL
        val destPath = destination.substringAfter("/files/$userId").let {
            if (it.startsWith("/")) it else "/$it"
        }

        val sourceFile = fileSystem[normalizedSource]
            ?: return Response.error(404, "Source not found".toResponseBody())

        if (fileSystem.containsKey(destPath)) {
            return Response.error(412, "Destination already exists".toResponseBody())
        }

        // Copy file
        val copiedFile = sourceFile.copy(
            id = (nextFileId++).toString(),
            path = destPath,
            name = destPath.substringAfterLast("/")
        )
        fileSystem[destPath] = copiedFile

        return Response.success(Unit)
    }

    // Create Share Link
    override suspend fun createShare(
        authorization: String,
        path: String,
        shareType: Int,
        permissions: Int
    ): Response<NextcloudResponse<NextcloudShare>> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"

        val file = fileSystem[normalizedPath]
            ?: return Response.error(404, "Path not found".toResponseBody())

        // Create new share
        val shareId = (nextShareId++).toString()
        val share = NextcloudShare(
            id = shareId,
            shareType = shareType,
            owner = NextcloudTestData.TEST_USER_ID,
            ownerDisplayName = NextcloudTestData.TEST_DISPLAY_NAME,
            path = normalizedPath,
            itemType = if (file.type == "directory") "folder" else "file",
            mimeType = file.mimeType,
            fileTarget = file.name,
            url = "${NextcloudTestData.TEST_SERVER_URL}/s/${shareId.takeLast(8)}"
        )

        shares[shareId] = share

        val response = NextcloudTestData.createShareResponse(share)
        return Response.success(response)
    }

    // Get Shares
    override suspend fun getShares(
        authorization: String,
        path: String
    ): Response<NextcloudResponse<List<NextcloudShare>>> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        val pathShares = shares.values.filter { it.path == normalizedPath }

        val response = NextcloudTestData.createSharesResponse(pathShares)
        return Response.success(response)
    }

    // Delete Share
    override suspend fun deleteShare(
        authorization: String,
        shareId: String
    ): Response<NextcloudResponse<Unit>> {
        delay(NETWORK_DELAY_MS)

        if (!validateAuth(authorization)) {
            return Response.error(401, "Unauthorized".toResponseBody())
        }

        if (!shares.containsKey(shareId)) {
            return Response.error(404, "Share not found".toResponseBody())
        }

        shares.remove(shareId)

        val response = NextcloudTestData.createEmptyResponse()
        return Response.success(response)
    }
}
