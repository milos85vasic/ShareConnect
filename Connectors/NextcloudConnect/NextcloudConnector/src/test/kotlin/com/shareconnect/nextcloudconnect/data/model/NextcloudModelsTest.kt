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


package com.shareconnect.nextcloudconnect.data.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Nextcloud data models
 */
class NextcloudModelsTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun `NextcloudFile serialization and deserialization`() {
        val file = NextcloudFile(
            id = "123",
            name = "document.pdf",
            path = "/Documents/document.pdf",
            type = "file",
            size = 1024000,
            modifiedTime = 1729814400000,
            mimeType = "application/pdf",
            etag = "abc123",
            permissions = "RDNVW"
        )

        val json = gson.toJson(file)
        val deserialized = gson.fromJson(json, NextcloudFile::class.java)

        assertEquals(file.id, deserialized.id)
        assertEquals(file.name, deserialized.name)
        assertEquals(file.path, deserialized.path)
        assertEquals(file.type, deserialized.type)
        assertEquals(file.size, deserialized.size)
        assertEquals(file.mimeType, deserialized.mimeType)
    }

    @Test
    fun `NextcloudFile with null optional fields`() {
        val file = NextcloudFile(
            id = "456",
            name = "folder",
            path = "/folder",
            type = "directory",
            size = 0,
            modifiedTime = 1729814400000,
            mimeType = null,
            etag = null,
            permissions = null
        )

        val json = gson.toJson(file)
        val deserialized = gson.fromJson(json, NextcloudFile::class.java)

        assertEquals(file.id, deserialized.id)
        assertEquals(file.type, deserialized.type)
        assertNull(deserialized.mimeType)
        assertNull(deserialized.etag)
        assertNull(deserialized.permissions)
    }

    @Test
    fun `NextcloudStatus serialization and deserialization`() {
        val status = NextcloudStatus(
            installed = true,
            maintenance = false,
            needsDbUpgrade = false,
            version = "27.0.0",
            versionString = "27.0.0.5",
            edition = "Community",
            productName = "Nextcloud"
        )

        val json = gson.toJson(status)
        val deserialized = gson.fromJson(json, NextcloudStatus::class.java)

        assertEquals(status.installed, deserialized.installed)
        assertEquals(status.maintenance, deserialized.maintenance)
        assertEquals(status.needsDbUpgrade, deserialized.needsDbUpgrade)
        assertEquals(status.version, deserialized.version)
        assertEquals(status.edition, deserialized.edition)
        assertEquals(status.productName, deserialized.productName)
    }

    @Test
    fun `NextcloudStatus maintenance mode`() {
        val status = NextcloudStatus(
            installed = true,
            maintenance = true,
            needsDbUpgrade = true,
            version = "27.0.0",
            versionString = "27.0.0.5",
            edition = "Community",
            productName = "Nextcloud"
        )

        assertTrue(status.maintenance)
        assertTrue(status.needsDbUpgrade)
    }

    @Test
    fun `NextcloudUser with full quota information`() {
        val user = NextcloudUser(
            id = "testuser",
            displayName = "Test User",
            email = "test@example.com",
            quota = NextcloudUser.Quota(
                free = 5000000000,
                used = 3000000000,
                total = 8000000000,
                relative = 37.5f
            )
        )

        val json = gson.toJson(user)
        val deserialized = gson.fromJson(json, NextcloudUser::class.java)

        assertEquals(user.id, deserialized.id)
        assertEquals(user.displayName, deserialized.displayName)
        assertEquals(user.email, deserialized.email)
        assertNotNull(deserialized.quota)
        assertEquals(user.quota?.free, deserialized.quota?.free)
        assertEquals(user.quota?.used, deserialized.quota?.used)
        assertEquals(user.quota?.total, deserialized.quota?.total)
        assertEquals(user.quota?.relative, deserialized.quota?.relative)
    }

    @Test
    fun `NextcloudUser with null optional fields`() {
        val user = NextcloudUser(
            id = "user123",
            displayName = null,
            email = null,
            quota = null
        )

        val json = gson.toJson(user)
        val deserialized = gson.fromJson(json, NextcloudUser::class.java)

        assertEquals(user.id, deserialized.id)
        assertNull(deserialized.displayName)
        assertNull(deserialized.email)
        assertNull(deserialized.quota)
    }

    @Test
    fun `NextcloudUser quota calculations`() {
        val quota = NextcloudUser.Quota(
            free = 5000000000,
            used = 3000000000,
            total = 8000000000,
            relative = 37.5f
        )

        assertEquals(8000000000, quota.total)
        assertEquals(5000000000, quota.free)
        assertEquals(3000000000, quota.used)
        assertEquals(37.5f, quota.relative, 0.01f)

        // Verify that free + used = total
        assertEquals(quota.total, quota.free + quota.used)
    }

    @Test
    fun `NextcloudResponse with successful data`() {
        val response = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = "OK"
                ),
                data = "Test data"
            )
        )

        assertEquals("ok", response.ocs.meta.status)
        assertEquals(200, response.ocs.meta.statusCode)
        assertEquals("Test data", response.ocs.data)
    }

    @Test
    fun `NextcloudResponse with error`() {
        val response = NextcloudResponse<String>(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "error",
                    statusCode = 404,
                    message = "Not found"
                ),
                data = null
            )
        )

        assertEquals("error", response.ocs.meta.status)
        assertEquals(404, response.ocs.meta.statusCode)
        assertEquals("Not found", response.ocs.meta.message)
        assertNull(response.ocs.data)
    }

    @Test
    fun `NextcloudResponse serialization with nested data`() {
        val user = NextcloudUser(
            id = "testuser",
            displayName = "Test User",
            email = "test@example.com",
            quota = null
        )

        val response = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = null
                ),
                data = user
            )
        )

        val json = gson.toJson(response)
        assertTrue(json.contains("testuser"))
        assertTrue(json.contains("Test User"))
    }

    @Test
    fun `NextcloudShare public link`() {
        val share = NextcloudShare(
            id = "123",
            shareType = 3, // Public link
            owner = "testuser",
            ownerDisplayName = "Test User",
            path = "/Documents/shared.pdf",
            itemType = "file",
            mimeType = "application/pdf",
            fileTarget = "/shared.pdf",
            url = "https://nextcloud.example.com/s/abc123"
        )

        val json = gson.toJson(share)
        val deserialized = gson.fromJson(json, NextcloudShare::class.java)

        assertEquals(share.id, deserialized.id)
        assertEquals(3, deserialized.shareType)
        assertEquals(share.url, deserialized.url)
        assertEquals(share.path, deserialized.path)
    }

    @Test
    fun `NextcloudShare user share`() {
        val share = NextcloudShare(
            id = "456",
            shareType = 0, // User share
            owner = "user1",
            ownerDisplayName = "User One",
            path = "/Documents/contract.docx",
            itemType = "file",
            mimeType = "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
            fileTarget = "/contract.docx",
            url = null // User shares don't have public URLs
        )

        val json = gson.toJson(share)
        val deserialized = gson.fromJson(json, NextcloudShare::class.java)

        assertEquals(share.id, deserialized.id)
        assertEquals(0, deserialized.shareType)
        assertNull(deserialized.url)
    }

    @Test
    fun `NextcloudShare directory share`() {
        val share = NextcloudShare(
            id = "789",
            shareType = 3,
            owner = "admin",
            ownerDisplayName = "Administrator",
            path = "/Photos",
            itemType = "folder",
            mimeType = null,
            fileTarget = "/Photos",
            url = "https://nextcloud.example.com/s/xyz789"
        )

        assertEquals("folder", share.itemType)
        assertNull(share.mimeType)
        assertTrue(share.url!!.startsWith("https://"))
    }

    @Test
    fun `Data class equality and hashCode`() {
        val file1 = NextcloudFile(
            id = "123",
            name = "test.txt",
            path = "/test.txt",
            type = "file",
            size = 100,
            modifiedTime = 1000,
            mimeType = "text/plain",
            etag = "abc",
            permissions = "RW"
        )

        val file2 = NextcloudFile(
            id = "123",
            name = "test.txt",
            path = "/test.txt",
            type = "file",
            size = 100,
            modifiedTime = 1000,
            mimeType = "text/plain",
            etag = "abc",
            permissions = "RW"
        )

        assertEquals(file1, file2)
        assertEquals(file1.hashCode(), file2.hashCode())
    }

    @Test
    fun `Data class copy functionality`() {
        val original = NextcloudFile(
            id = "123",
            name = "original.txt",
            path = "/original.txt",
            type = "file",
            size = 100,
            modifiedTime = 1000,
            mimeType = "text/plain",
            etag = "abc",
            permissions = "RW"
        )

        val modified = original.copy(name = "modified.txt", path = "/modified.txt")

        assertEquals("modified.txt", modified.name)
        assertEquals("/modified.txt", modified.path)
        assertEquals(original.id, modified.id) // Unchanged fields
        assertEquals(original.size, modified.size)
    }
}
