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

import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for NextcloudApiStubService to ensure stub data is returned correctly.
 */
class NextcloudApiStubServiceTest {

    private lateinit var stubService: NextcloudApiStubService
    private val testAuth = "Basic dGVzdHVzZXI6dGVzdHBhc3N3b3JkMTIz" // testuser:testpassword123

    @Before
    fun setup() {
        stubService = NextcloudApiStubService()
        NextcloudApiStubService.resetState()
    }

    @After
    fun tearDown() {
        NextcloudApiStubService.resetState()
    }

    // Server Status Tests

    @Test
    fun `test getServerStatus returns valid status`() = runTest {
        val response = stubService.getServerStatus()

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val status = response.body()!!
        assertTrue("Server should be installed", status.installed)
        assertFalse("Server should not be in maintenance", status.maintenance)
        assertEquals("Product name should be Nextcloud", "Nextcloud", status.productName)
        assertNotNull("Version should be set", status.version)
    }

    // User Info Tests

    @Test
    fun `test getUserInfo returns valid user with auth`() = runTest {
        val response = stubService.getUserInfo(testAuth)

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val userResponse = response.body()!!
        assertEquals("OCS status should be ok", "ok", userResponse.ocs.meta.status)

        val user = userResponse.ocs.data!!
        assertEquals("User ID should match", NextcloudTestData.TEST_USER_ID, user.id)
        assertEquals("Display name should match", NextcloudTestData.TEST_DISPLAY_NAME, user.displayName)
        assertEquals("Email should match", NextcloudTestData.TEST_EMAIL, user.email)
        assertNotNull("Quota should be set", user.quota)
    }

    @Test
    fun `test getUserInfo returns 401 without auth`() = runTest {
        val response = stubService.getUserInfo("")

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }

    // File Listing Tests

    @Test
    fun `test listFiles returns root files`() = runTest {
        val response = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "",
            authorization = testAuth
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val xmlResponse = response.body()!!
        assertTrue("Should contain Documents", xmlResponse.contains("Documents"))
        assertTrue("Should contain Photos", xmlResponse.contains("Photos"))
        assertTrue("Should contain Music", xmlResponse.contains("Music"))
    }

    @Test
    fun `test listFiles returns Documents folder contents`() = runTest {
        val response = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        assertTrue("Response should be successful", response.isSuccessful)
        val xmlResponse = response.body()!!

        assertTrue("Should contain Annual_Report", xmlResponse.contains("Annual_Report_2024.pdf"))
        assertTrue("Should contain Budget", xmlResponse.contains("Budget_2024.xlsx"))
        assertTrue("Should contain Presentation", xmlResponse.contains("Q4_Presentation.pptx"))
        assertTrue("Should contain Notes", xmlResponse.contains("Notes.txt"))
    }

    @Test
    fun `test listFiles returns 404 for non-existent path`() = runTest {
        val response = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NonExistentFolder",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    @Test
    fun `test listFiles returns 401 without auth`() = runTest {
        val response = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "",
            authorization = ""
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 401", 401, response.code())
    }

    // File Download Tests

    @Test
    fun `test downloadFile returns file content`() = runTest {
        val response = stubService.downloadFile(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents/Annual_Report_2024.pdf",
            authorization = testAuth
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val content = response.body()!!.string()
        assertTrue("Content should contain file name", content.contains("Annual_Report_2024.pdf"))
    }

    @Test
    fun `test downloadFile returns 404 for non-existent file`() = runTest {
        val response = stubService.downloadFile(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NonExistent.pdf",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    @Test
    fun `test downloadFile returns 404 for directory`() = runTest {
        val response = stubService.downloadFile(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // File Upload Tests

    @Test
    fun `test uploadFile creates new file`() = runTest {
        val fileContent = "Test file content".toByteArray()
        val requestBody = fileContent.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = stubService.uploadFile(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents/NewFile.txt",
            authorization = testAuth,
            file = requestBody
        )

        assertTrue("Response should be successful", response.isSuccessful)

        // Verify file was created by listing
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        assertTrue("List should be successful", listResponse.isSuccessful)
        val xmlResponse = listResponse.body()!!
        assertTrue("Should contain new file", xmlResponse.contains("NewFile.txt"))
    }

    @Test
    fun `test uploadFile returns 409 if parent directory does not exist`() = runTest {
        val fileContent = "Test file content".toByteArray()
        val requestBody = fileContent.toRequestBody("text/plain".toMediaTypeOrNull())

        val response = stubService.uploadFile(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NonExistentFolder/NewFile.txt",
            authorization = testAuth,
            file = requestBody
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 409", 409, response.code())
    }

    // Folder Creation Tests

    @Test
    fun `test createFolder creates new folder`() = runTest {
        val response = stubService.createFolder(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NewFolder",
            authorization = testAuth
        )

        assertTrue("Response should be successful", response.isSuccessful)

        // Verify folder was created by listing
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "",
            authorization = testAuth
        )

        assertTrue("List should be successful", listResponse.isSuccessful)
        val xmlResponse = listResponse.body()!!
        assertTrue("Should contain new folder", xmlResponse.contains("NewFolder"))
    }

    @Test
    fun `test createFolder returns 405 if path already exists`() = runTest {
        val response = stubService.createFolder(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 405", 405, response.code())
    }

    @Test
    fun `test createFolder returns 409 if parent does not exist`() = runTest {
        val response = stubService.createFolder(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NonExistent/NewFolder",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 409", 409, response.code())
    }

    // Delete Tests

    @Test
    fun `test delete removes file`() = runTest {
        val response = stubService.delete(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents/Notes.txt",
            authorization = testAuth
        )

        assertTrue("Response should be successful", response.isSuccessful)

        // Verify file was deleted
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        val xmlResponse = listResponse.body()!!
        assertFalse("Should not contain deleted file", xmlResponse.contains("Notes.txt"))
    }

    @Test
    fun `test delete returns 404 for non-existent path`() = runTest {
        val response = stubService.delete(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "NonExistent.txt",
            authorization = testAuth
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // Move Tests

    @Test
    fun `test move renames file`() = runTest {
        val destUrl = "${NextcloudTestData.TEST_SERVER_URL}/remote.php/dav/files/${NextcloudTestData.TEST_USER_ID}/Documents/Renamed.txt"

        val response = stubService.move(
            userId = NextcloudTestData.TEST_USER_ID,
            sourcePath = "Documents/Notes.txt",
            authorization = testAuth,
            destination = destUrl
        )

        assertTrue("Response should be successful", response.isSuccessful)

        // Verify file was moved
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        val xmlResponse = listResponse.body()!!
        assertFalse("Should not contain old name", xmlResponse.contains("Notes.txt"))
        assertTrue("Should contain new name", xmlResponse.contains("Renamed.txt"))
    }

    @Test
    fun `test move returns 404 for non-existent source`() = runTest {
        val destUrl = "${NextcloudTestData.TEST_SERVER_URL}/remote.php/dav/files/${NextcloudTestData.TEST_USER_ID}/NewFile.txt"

        val response = stubService.move(
            userId = NextcloudTestData.TEST_USER_ID,
            sourcePath = "NonExistent.txt",
            authorization = testAuth,
            destination = destUrl
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // Copy Tests

    @Test
    fun `test copy duplicates file`() = runTest {
        val destUrl = "${NextcloudTestData.TEST_SERVER_URL}/remote.php/dav/files/${NextcloudTestData.TEST_USER_ID}/Documents/Notes_Copy.txt"

        val response = stubService.copy(
            userId = NextcloudTestData.TEST_USER_ID,
            sourcePath = "Documents/Notes.txt",
            authorization = testAuth,
            destination = destUrl
        )

        assertTrue("Response should be successful", response.isSuccessful)

        // Verify both files exist
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "Documents",
            authorization = testAuth
        )

        val xmlResponse = listResponse.body()!!
        assertTrue("Should contain original", xmlResponse.contains("Notes.txt"))
        assertTrue("Should contain copy", xmlResponse.contains("Notes_Copy.txt"))
    }

    // Share Tests

    @Test
    fun `test createShare creates public link`() = runTest {
        val response = stubService.createShare(
            authorization = testAuth,
            path = "/Documents/Budget_2024.xlsx"
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val shareResponse = response.body()!!
        val share = shareResponse.ocs.data!!

        assertNotNull("Share ID should be set", share.id)
        assertEquals("Share type should be 3 (public link)", 3, share.shareType)
        assertEquals("Path should match", "/Documents/Budget_2024.xlsx", share.path)
        assertNotNull("URL should be set", share.url)
        assertTrue("URL should be valid", share.url!!.contains("/s/"))
    }

    @Test
    fun `test createShare returns 404 for non-existent path`() = runTest {
        val response = stubService.createShare(
            authorization = testAuth,
            path = "/NonExistent.pdf"
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    @Test
    fun `test getShares returns shares for path`() = runTest {
        val response = stubService.getShares(
            authorization = testAuth,
            path = "/Documents/Annual_Report_2024.pdf"
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Response body should not be null", response.body())

        val sharesResponse = response.body()!!
        val shares = sharesResponse.ocs.data ?: emptyList()

        assertTrue("Should return at least 1 share", shares.size >= 1)
        assertTrue("Should contain share for the file",
            shares.any { it.path == "/Documents/Annual_Report_2024.pdf" })
    }

    @Test
    fun `test getShares returns empty for path without shares`() = runTest {
        val response = stubService.getShares(
            authorization = testAuth,
            path = "/Documents/Notes.txt"
        )

        assertTrue("Response should be successful", response.isSuccessful)
        val sharesResponse = response.body()!!
        val shares = sharesResponse.ocs.data ?: emptyList()

        assertEquals("Should return no shares", 0, shares.size)
    }

    @Test
    fun `test deleteShare removes share`() = runTest {
        // First create a share
        val createResponse = stubService.createShare(
            authorization = testAuth,
            path = "/Documents/Notes.txt"
        )

        assertTrue("Create should be successful", createResponse.isSuccessful)
        val shareId = createResponse.body()!!.ocs.data!!.id

        // Delete the share
        val deleteResponse = stubService.deleteShare(
            authorization = testAuth,
            shareId = shareId
        )

        assertTrue("Delete should be successful", deleteResponse.isSuccessful)

        // Verify share was deleted
        val getResponse = stubService.getShares(
            authorization = testAuth,
            path = "/Documents/Notes.txt"
        )

        val shares = getResponse.body()!!.ocs.data ?: emptyList()
        assertFalse("Share should be removed", shares.any { it.id == shareId })
    }

    @Test
    fun `test deleteShare returns 404 for non-existent share`() = runTest {
        val response = stubService.deleteShare(
            authorization = testAuth,
            shareId = "99999"
        )

        assertFalse("Response should not be successful", response.isSuccessful)
        assertEquals("Response code should be 404", 404, response.code())
    }

    // State Management Tests

    @Test
    fun `test resetState restores initial state`() = runTest {
        // Modify state
        stubService.createFolder(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "TempFolder",
            authorization = testAuth
        )

        // Reset state
        NextcloudApiStubService.resetState()

        // Verify temp folder is gone
        val listResponse = stubService.listFiles(
            userId = NextcloudTestData.TEST_USER_ID,
            path = "",
            authorization = testAuth
        )

        val xmlResponse = listResponse.body()!!
        assertFalse("Temp folder should be removed", xmlResponse.contains("TempFolder"))
        assertTrue("Original folders should be present", xmlResponse.contains("Documents"))
    }
}
