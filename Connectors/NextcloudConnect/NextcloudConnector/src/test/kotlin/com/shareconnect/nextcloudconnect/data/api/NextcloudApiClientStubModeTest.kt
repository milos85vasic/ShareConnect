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
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for NextcloudApiClient in stub mode to ensure stub data integration works correctly.
 */
@RunWith(RobolectricTestRunner::class)
class NextcloudApiClientStubModeTest {

    private lateinit var apiClient: NextcloudApiClient

    @Before
    fun setup() {
        apiClient = NextcloudApiClient(
            serverUrl = NextcloudTestData.TEST_SERVER_URL,
            username = NextcloudTestData.TEST_USERNAME,
            password = NextcloudTestData.TEST_PASSWORD,
            isStubMode = true
        )
        NextcloudApiStubService.resetState()
    }

    @Test
    fun `test getServerStatus succeeds in stub mode`() = runTest {
        val result = apiClient.getServerStatus()

        assertTrue("Request should succeed", result.isSuccess)

        val status = result.getOrThrow()
        assertNotNull("Status should not be null", status)
        assertTrue("Server should be installed", status.installed)
        assertEquals("Product name should be Nextcloud", "Nextcloud", status.productName)
    }

    @Test
    fun `test getUserInfo succeeds in stub mode`() = runTest {
        val result = apiClient.getUserInfo()

        assertTrue("Request should succeed", result.isSuccess)

        val user = result.getOrThrow()
        assertNotNull("User should not be null", user)
        assertEquals("User ID should match", NextcloudTestData.TEST_USER_ID, user.id)
        assertEquals("Display name should match", NextcloudTestData.TEST_DISPLAY_NAME, user.displayName)
        assertNotNull("Quota should be set", user.quota)
    }

    @Test
    fun `test listFiles returns root directory contents`() = runTest {
        val result = apiClient.listFiles("")

        assertTrue("Request should succeed", result.isSuccess)

        val xmlResponse = result.getOrThrow()
        assertNotNull("Response should not be null", xmlResponse)
        assertTrue("Should contain Documents folder", xmlResponse.contains("Documents"))
        assertTrue("Should contain Photos folder", xmlResponse.contains("Photos"))
        assertTrue("Should contain Music folder", xmlResponse.contains("Music"))
    }

    @Test
    fun `test listFiles returns subdirectory contents`() = runTest {
        val result = apiClient.listFiles("Documents")

        assertTrue("Request should succeed", result.isSuccess)

        val xmlResponse = result.getOrThrow()
        assertTrue("Should contain PDF", xmlResponse.contains("Annual_Report_2024.pdf"))
        assertTrue("Should contain spreadsheet", xmlResponse.contains("Budget_2024.xlsx"))
    }

    @Test
    fun `test downloadFile succeeds in stub mode`() = runTest {
        val result = apiClient.downloadFile("Documents/Annual_Report_2024.pdf")

        assertTrue("Request should succeed", result.isSuccess)

        val content = result.getOrThrow()
        assertNotNull("Content should not be null", content)
        assertTrue("Content should not be empty", content.isNotEmpty())
    }

    @Test
    fun `test uploadFile succeeds in stub mode`() = runTest {
        val testData = "Test file content".toByteArray()

        val result = apiClient.uploadFile("Documents/TestFile.txt", testData, "text/plain")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify file was created
        val listResult = apiClient.listFiles("Documents")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertTrue("Should contain uploaded file", xmlResponse.contains("TestFile.txt"))
    }

    @Test
    fun `test createFolder succeeds in stub mode`() = runTest {
        val result = apiClient.createFolder("TestFolder")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify folder was created
        val listResult = apiClient.listFiles("")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertTrue("Should contain new folder", xmlResponse.contains("TestFolder"))
    }

    @Test
    fun `test delete succeeds in stub mode`() = runTest {
        val result = apiClient.delete("Documents/Notes.txt")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify file was deleted
        val listResult = apiClient.listFiles("Documents")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertFalse("Should not contain deleted file", xmlResponse.contains("Notes.txt"))
    }

    @Test
    fun `test move succeeds in stub mode`() = runTest {
        val result = apiClient.move("Documents/Notes.txt", "Documents/RenamedNotes.txt")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify file was moved
        val listResult = apiClient.listFiles("Documents")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertFalse("Should not contain old name", xmlResponse.contains("Notes.txt"))
        assertTrue("Should contain new name", xmlResponse.contains("RenamedNotes.txt"))
    }

    @Test
    fun `test copy succeeds in stub mode`() = runTest {
        val result = apiClient.copy("Documents/Notes.txt", "Documents/NotesCopy.txt")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify both files exist
        val listResult = apiClient.listFiles("Documents")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertTrue("Should contain original", xmlResponse.contains("Notes.txt"))
        assertTrue("Should contain copy", xmlResponse.contains("NotesCopy.txt"))
    }

    @Test
    fun `test createShareLink succeeds in stub mode`() = runTest {
        val result = apiClient.createShareLink("/Documents/Budget_2024.xlsx")

        assertTrue("Request should succeed", result.isSuccess)

        val share = result.getOrThrow()
        assertNotNull("Share should not be null", share)
        assertNotNull("Share URL should be set", share.url)
        assertTrue("Share URL should be valid", share.url!!.contains("/s/"))
        assertEquals("Share path should match", "/Documents/Budget_2024.xlsx", share.path)
    }

    @Test
    fun `test getShares succeeds in stub mode`() = runTest {
        val result = apiClient.getShares("/Documents/Annual_Report_2024.pdf")

        assertTrue("Request should succeed", result.isSuccess)

        val shares = result.getOrThrow()
        assertNotNull("Shares should not be null", shares)
        assertTrue("Should return at least 1 share", shares.size >= 1)
    }

    @Test
    fun `test deleteShare succeeds in stub mode`() = runTest {
        // Create a share first
        val createResult = apiClient.createShareLink("/Documents/Notes.txt")
        assertTrue("Create should succeed", createResult.isSuccess)

        val share = createResult.getOrThrow()
        val shareId = share.id

        // Delete the share
        val deleteResult = apiClient.deleteShare(shareId)
        assertTrue("Delete should succeed", deleteResult.isSuccess)

        // Verify share was deleted
        val getResult = apiClient.getShares("/Documents/Notes.txt")
        val shares = getResult.getOrThrow()
        assertFalse("Share should be removed", shares.any { it.id == shareId })
    }

    @Test
    fun `test complete workflow in stub mode`() = runTest {
        // 1. Create a folder
        val createFolderResult = apiClient.createFolder("TestWorkflow")
        assertTrue("Create folder should succeed", createFolderResult.isSuccess)

        // 2. Upload a file to the folder
        val uploadResult = apiClient.uploadFile(
            "TestWorkflow/TestFile.txt",
            "Test content".toByteArray(),
            "text/plain"
        )
        assertTrue("Upload should succeed", uploadResult.isSuccess)

        // 3. Create a share link for the file
        val shareResult = apiClient.createShareLink("/TestWorkflow/TestFile.txt")
        assertTrue("Create share should succeed", shareResult.isSuccess)

        val share = shareResult.getOrThrow()
        assertNotNull("Share URL should be set", share.url)

        // 4. Copy the file
        val copyResult = apiClient.copy("TestWorkflow/TestFile.txt", "TestWorkflow/TestFileCopy.txt")
        assertTrue("Copy should succeed", copyResult.isSuccess)

        // 5. Delete the original file
        val deleteResult = apiClient.delete("TestWorkflow/TestFile.txt")
        assertTrue("Delete should succeed", deleteResult.isSuccess)

        // 6. Verify final state
        val listResult = apiClient.listFiles("TestWorkflow")
        assertTrue("List should succeed", listResult.isSuccess)

        val xmlResponse = listResult.getOrThrow()
        assertFalse("Should not contain deleted file", xmlResponse.contains("TestFile.txt"))
        assertTrue("Should contain copy", xmlResponse.contains("TestFileCopy.txt"))
    }
}
