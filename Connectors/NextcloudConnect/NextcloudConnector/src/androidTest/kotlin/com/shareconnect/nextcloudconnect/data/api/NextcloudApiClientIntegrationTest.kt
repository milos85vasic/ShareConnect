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

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.nextcloudconnect.data.model.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class NextcloudApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: NextcloudApiClient
    private lateinit var apiService: NextcloudApiService
    private val testUsername = "testuser"
    private val testPassword = "testpass"

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(NextcloudApiService::class.java)
        apiClient = NextcloudApiClient(mockWebServer.url("/").toString(), testUsername, testPassword, apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getServerStatus_returnsStatus_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "installed": true,
                    "maintenance": false,
                    "needsDbUpgrade": false,
                    "version": "27.0.0",
                    "versionstring": "27.0.0",
                    "edition": "Community",
                    "productname": "Nextcloud"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getServerStatus()

        // Then
        assertTrue(result.isSuccess)
        val status = result.getOrNull()!!
        assertTrue(status.installed)
        assertFalse(status.maintenance)
        assertEquals("27.0.0", status.version)
        assertEquals("Nextcloud", status.productName)
    }

    @Test
    fun getServerStatus_returnsFailure_onNetworkError() = runTest {
        // Given
        mockWebServer.shutdown()

        // When
        val result = apiClient.getServerStatus()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getUserInfo_returnsUser_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "ocs": {
                        "meta": {
                            "status": "ok",
                            "statuscode": 200,
                            "message": "OK"
                        },
                        "data": {
                            "id": "testuser",
                            "displayname": "Test User",
                            "email": "test@example.com",
                            "quota": {
                                "free": 10000000000,
                                "used": 5000000000,
                                "total": 15000000000,
                                "relative": 33.33
                            }
                        }
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getUserInfo()

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.id)
        assertEquals("Test User", user.displayName)
        assertNotNull(user.quota)
        assertEquals(15000000000L, user.quota?.total)
    }

    @Test
    fun listFiles_returnsXml_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""<?xml version="1.0"?>
                <d:multistatus xmlns:d="DAV:">
                    <d:response>
                        <d:href>/remote.php/dav/files/testuser/Documents/</d:href>
                        <d:propstat>
                            <d:prop>
                                <d:getcontenttype/>
                                <d:resourcetype><d:collection/></d:resourcetype>
                            </d:prop>
                            <d:status>HTTP/1.1 200 OK</d:status>
                        </d:propstat>
                    </d:response>
                </d:multistatus>""")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.listFiles("Documents")

        // Then
        assertTrue(result.isSuccess)
        val xmlData = result.getOrNull()!!
        assertTrue(xmlData.contains("Documents"))
    }

    @Test
    fun downloadFile_returnsBytes_onSuccessfulResponse() = runTest {
        // Given
        val fileContent = "Hello, Nextcloud!".toByteArray()
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody(okhttp3.internal.toHexString(fileContent))
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.downloadFile("test.txt")

        // Then
        assertTrue(result.isSuccess)
        assertNotNull(result.getOrNull())
    }

    @Test
    fun uploadFile_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val fileContent = "Test content".toByteArray()
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.uploadFile("newfile.txt", fileContent)

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun createFolder_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(201)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createFolder("NewFolder")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun delete_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.delete("test.txt")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun createShareLink_returnsShare_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "ocs": {
                        "meta": {
                            "status": "ok",
                            "statuscode": 200,
                            "message": "OK"
                        },
                        "data": {
                            "id": "123",
                            "share_type": 3,
                            "uid_owner": "testuser",
                            "displayname_owner": "Test User",
                            "path": "/Documents/shared.pdf",
                            "item_type": "file",
                            "mimetype": "application/pdf",
                            "file_target": "/shared.pdf",
                            "url": "https://nextcloud.example.com/s/abc123"
                        }
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createShareLink("/Documents/shared.pdf")

        // Then
        assertTrue(result.isSuccess)
        val share = result.getOrNull()!!
        assertEquals("https://nextcloud.example.com/s/abc123", share.url)
        assertEquals("file", share.itemType)
    }

    @Test
    fun getShares_returnsSharesList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "ocs": {
                        "meta": {
                            "status": "ok",
                            "statuscode": 200,
                            "message": "OK"
                        },
                        "data": [
                            {
                                "id": "123",
                                "share_type": 3,
                                "uid_owner": "testuser",
                                "displayname_owner": "Test User",
                                "path": "/Documents/shared.pdf",
                                "item_type": "file",
                                "mimetype": "application/pdf",
                                "file_target": "/shared.pdf",
                                "url": "https://nextcloud.example.com/s/abc123"
                            }
                        ]
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getShares("/Documents")

        // Then
        assertTrue(result.isSuccess)
        val shares = result.getOrNull()!!
        assertEquals(1, shares.size)
        assertEquals("123", shares[0].id)
    }

    @Test
    fun deleteShare_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "ocs": {
                        "meta": {
                            "status": "ok",
                            "statuscode": 200,
                            "message": "OK"
                        },
                        "data": null
                    }
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.deleteShare("123")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun httpError_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getServerStatus()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun authentication_includesBasicAuthHeader() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"installed": true}""")
        mockWebServer.enqueue(mockResponse)

        // When
        apiClient.getServerStatus()

        // Then
        val request = mockWebServer.takeRequest()
        assertNotNull(request.getHeader("Authorization"))
        assertTrue(request.getHeader("Authorization")!!.startsWith("Basic "))
    }
}
