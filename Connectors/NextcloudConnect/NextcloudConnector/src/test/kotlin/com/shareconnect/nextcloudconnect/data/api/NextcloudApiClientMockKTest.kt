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
import io.mockk.*
import kotlinx.coroutines.runBlocking
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for NextcloudApiClient using MockK
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.nextcloudconnect.TestApplication::class)
class NextcloudApiClientMockKTest {

    private lateinit var mockService: NextcloudApiService
    private lateinit var apiClient: NextcloudApiClient
    private val testServerUrl = "https://nextcloud.example.com"
    private val testUsername = "testuser"
    private val testPassword = "testpass123"

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = NextcloudApiClient(testServerUrl, testUsername, testPassword, mockService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
        assertTrue(apiClient.authHeader.startsWith("Basic "))
    }

    @Test
    fun `test get server status success`() = runBlocking {
        val mockStatus = NextcloudStatus(
            installed = true,
            maintenance = false,
            needsDbUpgrade = false,
            version = "27.0.0",
            versionString = "27.0.0",
            edition = "Community",
            productName = "Nextcloud"
        )

        coEvery { mockService.getServerStatus() } returns Response.success(mockStatus)

        val result = apiClient.getServerStatus()

        assertTrue(result.isSuccess)
        val status = result.getOrNull()!!
        assertTrue(status.installed)
        assertFalse(status.maintenance)
        assertEquals("27.0.0", status.version)
    }

    @Test
    fun `test get user info success`() = runBlocking {
        val mockUser = NextcloudUser(
            id = "testuser",
            displayName = "Test User",
            email = "test@example.com",
            quota = NextcloudUser.Quota(
                free = 10000000000,
                used = 5000000000,
                total = 15000000000,
                relative = 33.33f
            )
        )
        val mockResponse = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = "OK"
                ),
                data = mockUser
            )
        )

        coEvery { mockService.getUserInfo(any()) } returns Response.success(mockResponse)

        val result = apiClient.getUserInfo()

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.id)
        assertEquals("Test User", user.displayName)
    }

    @Test
    fun `test list files success`() = runBlocking {
        val mockXmlResponse = """<?xml version="1.0"?>
            <d:multistatus xmlns:d="DAV:">
                <d:response>
                    <d:href>/remote.php/dav/files/testuser/Documents/</d:href>
                </d:response>
            </d:multistatus>"""

        coEvery { mockService.listFiles(any(), any(), any()) } returns Response.success(mockXmlResponse)

        val result = apiClient.listFiles("Documents")

        assertTrue(result.isSuccess)
        val xmlData = result.getOrNull()!!
        assertTrue(xmlData.contains("Documents"))
    }

    @Test
    fun `test download file success`() = runBlocking {
        val mockFileData = "Hello, World!".toByteArray()
        val mockResponseBody = mockFileData.toResponseBody()

        coEvery { mockService.downloadFile(any(), any(), any()) } returns Response.success(mockResponseBody)

        val result = apiClient.downloadFile("test.txt")

        assertTrue(result.isSuccess)
        assertArrayEquals(mockFileData, result.getOrNull()!!)
    }

    @Test
    fun `test upload file success`() = runBlocking {
        val testData = "Test content".toByteArray()

        coEvery { mockService.uploadFile(any(), any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.uploadFile("newfile.txt", testData)

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test create folder success`() = runBlocking {
        coEvery { mockService.createFolder(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.createFolder("NewFolder")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test delete file success`() = runBlocking {
        coEvery { mockService.delete(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.delete("test.txt")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test move file success`() = runBlocking {
        coEvery { mockService.move(any(), any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.move("old.txt", "new.txt")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test copy file success`() = runBlocking {
        coEvery { mockService.copy(any(), any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.copy("source.txt", "destination.txt")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test create share link success`() = runBlocking {
        val mockShare = NextcloudShare(
            id = "123",
            shareType = 3,
            owner = "testuser",
            ownerDisplayName = "Test User",
            path = "/Documents/shared.pdf",
            itemType = "file",
            mimeType = "application/pdf",
            fileTarget = "/shared.pdf",
            url = "https://nextcloud.example.com/s/abc123"
        )
        val mockResponse = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = "OK"
                ),
                data = mockShare
            )
        )

        coEvery { mockService.createShare(any(), any()) } returns Response.success(mockResponse)

        val result = apiClient.createShareLink("/Documents/shared.pdf")

        assertTrue(result.isSuccess)
        assertEquals("https://nextcloud.example.com/s/abc123", result.getOrNull()!!.url)
    }

    @Test
    fun `test get shares success`() = runBlocking {
        val mockShares = listOf(
            NextcloudShare(
                id = "123",
                shareType = 3,
                owner = "testuser",
                ownerDisplayName = "Test User",
                path = "/Documents/shared.pdf",
                itemType = "file",
                mimeType = "application/pdf",
                fileTarget = "/shared.pdf",
                url = "https://nextcloud.example.com/s/abc123"
            )
        )
        val mockResponse = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = "OK"
                ),
                data = mockShares
            )
        )

        coEvery { mockService.getShares(any(), any()) } returns Response.success(mockResponse)

        val result = apiClient.getShares("/Documents")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
    }

    @Test
    fun `test delete share success`() = runBlocking {
        val mockResponse = NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 200,
                    message = "OK"
                ),
                data = null as Unit?
            )
        )

        coEvery { mockService.deleteShare(any(), any()) } returns Response.success(mockResponse)

        val result = apiClient.deleteShare("123")

        assertTrue(result.isSuccess)
    }

    @Test
    fun `test HTTP error handling`() = runBlocking {
        coEvery { mockService.getServerStatus() } returns Response.error(500, mockk(relaxed = true))

        val result = apiClient.getServerStatus()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getServerStatus() } throws RuntimeException("Network error")

        val result = apiClient.getServerStatus()

        assertTrue(result.isFailure)
    }
}
