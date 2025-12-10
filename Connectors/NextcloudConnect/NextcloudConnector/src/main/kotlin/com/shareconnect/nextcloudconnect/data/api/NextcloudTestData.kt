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

/**
 * Test data provider for Nextcloud API stub implementations.
 * Provides realistic sample data for all Nextcloud models to enable UI and integration testing
 * without requiring a live Nextcloud server.
 */
object NextcloudTestData {

    // Authentication Data
    const val TEST_SERVER_URL = "https://cloud.example.com"
    const val TEST_USERNAME = "testuser"
    const val TEST_PASSWORD = "testpassword123"
    const val TEST_USER_ID = "testuser"
    const val TEST_DISPLAY_NAME = "Test User"
    const val TEST_EMAIL = "testuser@example.com"

    // Server Status
    val testServerStatus = NextcloudStatus(
        installed = true,
        maintenance = false,
        needsDbUpgrade = false,
        version = "28.0.1.1",
        versionString = "28.0.1",
        edition = "Community",
        productName = "Nextcloud"
    )

    // User Information
    val testUserQuota = NextcloudUser.Quota(
        free = 50L * 1024 * 1024 * 1024, // 50 GB free
        used = 14L * 1024 * 1024 * 1024, // 14 GB used
        total = 64L * 1024 * 1024 * 1024, // 64 GB total
        relative = 21.875f // 21.875% used
    )

    val testUser = NextcloudUser(
        id = TEST_USER_ID,
        displayName = TEST_DISPLAY_NAME,
        email = TEST_EMAIL,
        quota = testUserQuota
    )

    // File/Folder Structure
    val testRootFolder = NextcloudFile(
        id = "1",
        name = "",
        path = "/",
        type = "directory",
        size = 0,
        modifiedTime = System.currentTimeMillis(),
        mimeType = "httpd/unix-directory",
        etag = "root-etag-001",
        permissions = "RDNVCK"
    )

    val testDocumentsFolder = NextcloudFile(
        id = "2",
        name = "Documents",
        path = "/Documents",
        type = "directory",
        size = 0,
        modifiedTime = System.currentTimeMillis() - 86400000, // 1 day ago
        mimeType = "httpd/unix-directory",
        etag = "documents-etag-002",
        permissions = "RDNVCK"
    )

    val testPhotosFolder = NextcloudFile(
        id = "3",
        name = "Photos",
        path = "/Photos",
        type = "directory",
        size = 0,
        modifiedTime = System.currentTimeMillis() - 172800000, // 2 days ago
        mimeType = "httpd/unix-directory",
        etag = "photos-etag-003",
        permissions = "RDNVCK"
    )

    val testMusicFolder = NextcloudFile(
        id = "4",
        name = "Music",
        path = "/Music",
        type = "directory",
        size = 0,
        modifiedTime = System.currentTimeMillis() - 259200000, // 3 days ago
        mimeType = "httpd/unix-directory",
        etag = "music-etag-004",
        permissions = "RDNVCK"
    )

    // Document Files
    val testReportPdf = NextcloudFile(
        id = "10",
        name = "Annual_Report_2024.pdf",
        path = "/Documents/Annual_Report_2024.pdf",
        type = "file",
        size = 2_457_600, // 2.4 MB
        modifiedTime = System.currentTimeMillis() - 3600000, // 1 hour ago
        mimeType = "application/pdf",
        etag = "pdf-etag-010",
        permissions = "RWNVD"
    )

    val testSpreadsheet = NextcloudFile(
        id = "11",
        name = "Budget_2024.xlsx",
        path = "/Documents/Budget_2024.xlsx",
        type = "file",
        size = 524_288, // 512 KB
        modifiedTime = System.currentTimeMillis() - 7200000, // 2 hours ago
        mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
        etag = "xlsx-etag-011",
        permissions = "RWNVD"
    )

    val testPresentation = NextcloudFile(
        id = "12",
        name = "Q4_Presentation.pptx",
        path = "/Documents/Q4_Presentation.pptx",
        type = "file",
        size = 5_242_880, // 5 MB
        modifiedTime = System.currentTimeMillis() - 10800000, // 3 hours ago
        mimeType = "application/vnd.openxmlformats-officedocument.presentationml.presentation",
        etag = "pptx-etag-012",
        permissions = "RWNVD"
    )

    val testTextFile = NextcloudFile(
        id = "13",
        name = "Notes.txt",
        path = "/Documents/Notes.txt",
        type = "file",
        size = 4_096, // 4 KB
        modifiedTime = System.currentTimeMillis() - 14400000, // 4 hours ago
        mimeType = "text/plain",
        etag = "txt-etag-013",
        permissions = "RWNVD"
    )

    // Photo Files
    val testPhoto1 = NextcloudFile(
        id = "20",
        name = "IMG_2024_001.jpg",
        path = "/Photos/IMG_2024_001.jpg",
        type = "file",
        size = 3_145_728, // 3 MB
        modifiedTime = System.currentTimeMillis() - 86400000, // 1 day ago
        mimeType = "image/jpeg",
        etag = "jpg-etag-020",
        permissions = "RWNVD"
    )

    val testPhoto2 = NextcloudFile(
        id = "21",
        name = "IMG_2024_002.jpg",
        path = "/Photos/IMG_2024_002.jpg",
        type = "file",
        size = 2_621_440, // 2.5 MB
        modifiedTime = System.currentTimeMillis() - 172800000, // 2 days ago
        mimeType = "image/jpeg",
        etag = "jpg-etag-021",
        permissions = "RWNVD"
    )

    val testPhoto3 = NextcloudFile(
        id = "22",
        name = "vacation_2024.png",
        path = "/Photos/vacation_2024.png",
        type = "file",
        size = 4_194_304, // 4 MB
        modifiedTime = System.currentTimeMillis() - 259200000, // 3 days ago
        mimeType = "image/png",
        etag = "png-etag-022",
        permissions = "RWNVD"
    )

    // Music Files
    val testMusicFile1 = NextcloudFile(
        id = "30",
        name = "song_001.mp3",
        path = "/Music/song_001.mp3",
        type = "file",
        size = 4_718_592, // 4.5 MB
        modifiedTime = System.currentTimeMillis() - 345600000, // 4 days ago
        mimeType = "audio/mpeg",
        etag = "mp3-etag-030",
        permissions = "RWNVD"
    )

    val testMusicFile2 = NextcloudFile(
        id = "31",
        name = "song_002.mp3",
        path = "/Music/song_002.mp3",
        type = "file",
        size = 5_242_880, // 5 MB
        modifiedTime = System.currentTimeMillis() - 432000000, // 5 days ago
        mimeType = "audio/mpeg",
        etag = "mp3-etag-031",
        permissions = "RWNVD"
    )

    // Collections
    val testRootFiles = listOf(testDocumentsFolder, testPhotosFolder, testMusicFolder)
    val testDocumentFiles = listOf(testReportPdf, testSpreadsheet, testPresentation, testTextFile)
    val testPhotoFiles = listOf(testPhoto1, testPhoto2, testPhoto3)
    val testMusicFiles = listOf(testMusicFile1, testMusicFile2)
    val testAllFiles = testRootFiles + testDocumentFiles + testPhotoFiles + testMusicFiles

    // Shares
    val testShare1 = NextcloudShare(
        id = "1001",
        shareType = 3, // Public link
        owner = TEST_USER_ID,
        ownerDisplayName = TEST_DISPLAY_NAME,
        path = "/Documents/Annual_Report_2024.pdf",
        itemType = "file",
        mimeType = "application/pdf",
        fileTarget = "/Annual_Report_2024.pdf",
        url = "https://cloud.example.com/s/abcd1234"
    )

    val testShare2 = NextcloudShare(
        id = "1002",
        shareType = 3, // Public link
        owner = TEST_USER_ID,
        ownerDisplayName = TEST_DISPLAY_NAME,
        path = "/Photos",
        itemType = "folder",
        mimeType = null,
        fileTarget = "/Photos",
        url = "https://cloud.example.com/s/efgh5678"
    )

    val testShares = listOf(testShare1, testShare2)

    // Response Wrappers
    fun createUserResponse(user: NextcloudUser = testUser): NextcloudResponse<NextcloudUser> {
        return NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 100,
                    message = null
                ),
                data = user
            )
        )
    }

    fun createShareResponse(share: NextcloudShare): NextcloudResponse<NextcloudShare> {
        return NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 100,
                    message = null
                ),
                data = share
            )
        )
    }

    fun createSharesResponse(shares: List<NextcloudShare>): NextcloudResponse<List<NextcloudShare>> {
        return NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 100,
                    message = null
                ),
                data = shares
            )
        )
    }

    fun createEmptyResponse(): NextcloudResponse<Unit> {
        return NextcloudResponse(
            ocs = NextcloudResponse.OcsData(
                meta = NextcloudResponse.OcsData.Meta(
                    status = "ok",
                    statusCode = 100,
                    message = null
                ),
                data = null
            )
        )
    }

    // WebDAV XML Response
    fun createWebDavXmlResponse(files: List<NextcloudFile>): String {
        val responses = files.joinToString("\n") { file ->
            """
            <d:response>
                <d:href>/remote.php/dav/files/$TEST_USERNAME${file.path}</d:href>
                <d:propstat>
                    <d:prop>
                        <d:resourcetype>${if (file.type == "directory") "<d:collection/>" else ""}</d:resourcetype>
                        <d:getcontentlength>${file.size}</d:getcontentlength>
                        <d:getlastmodified>${file.modifiedTime}</d:getlastmodified>
                        <d:getetag>${file.etag}</d:getetag>
                        <d:getcontenttype>${file.mimeType ?: ""}</d:getcontenttype>
                        <oc:permissions>${file.permissions}</oc:permissions>
                    </d:prop>
                    <d:status>HTTP/1.1 200 OK</d:status>
                </d:propstat>
            </d:response>
            """.trimIndent()
        }

        return """
            <?xml version="1.0"?>
            <d:multistatus xmlns:d="DAV:" xmlns:oc="http://owncloud.org/ns" xmlns:nc="http://nextcloud.org/ns">
                $responses
            </d:multistatus>
        """.trimIndent()
    }

    // Helper Methods
    fun getFilesByPath(path: String): List<NextcloudFile> {
        val normalizedPath = path.removeSuffix("/")
        return when (normalizedPath) {
            "", "/" -> testRootFiles
            "/Documents" -> testDocumentFiles
            "/Photos" -> testPhotoFiles
            "/Music" -> testMusicFiles
            else -> emptyList()
        }
    }

    fun getFileByPath(path: String): NextcloudFile? {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return testAllFiles.find { it.path == normalizedPath }
    }

    fun getSharesByPath(path: String): List<NextcloudShare> {
        val normalizedPath = if (path.startsWith("/")) path else "/$path"
        return testShares.filter { it.path == normalizedPath }
    }

    fun getShareById(shareId: String): NextcloudShare? {
        return testShares.find { it.id == shareId }
    }
}
