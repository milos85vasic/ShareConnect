package com.shareconnect.motrixconnect.data.model

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Motrix data models
 */
class MotrixModelsTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun `MotrixDownload serialization and deserialization`() {
        val download = MotrixDownload(
            gid = "2089b05ecca3d829",
            status = "active",
            totalLength = "1073741824",
            completedLength = "536870912",
            uploadLength = "0",
            downloadSpeed = "1048576",
            uploadSpeed = "0",
            files = emptyList(),
            directory = "/downloads",
            errorCode = null,
            errorMessage = null,
            connections = "16"
        )

        val json = gson.toJson(download)
        val deserialized = gson.fromJson(json, MotrixDownload::class.java)

        assertEquals(download.gid, deserialized.gid)
        assertEquals(download.status, deserialized.status)
        assertEquals(download.totalLength, deserialized.totalLength)
        assertEquals(download.completedLength, deserialized.completedLength)
        assertEquals(download.downloadSpeed, deserialized.downloadSpeed)
    }

    @Test
    fun `MotrixDownload with error status`() {
        val download = MotrixDownload(
            gid = "error-gid",
            status = "error",
            totalLength = "1000000",
            completedLength = "500000",
            uploadLength = "0",
            downloadSpeed = "0",
            uploadSpeed = "0",
            files = null,
            directory = "/downloads",
            errorCode = "13",
            errorMessage = "Resource not found",
            connections = null
        )

        assertEquals("error", download.status)
        assertEquals("13", download.errorCode)
        assertNotNull(download.errorMessage)
        assertTrue(download.errorMessage!!.contains("not found"))
    }

    @Test
    fun `MotrixDownload DownloadFile with URIs`() {
        val uri = MotrixDownload.DownloadFile.Uri(
            uri = "https://example.com/file.zip",
            status = "used"
        )

        val file = MotrixDownload.DownloadFile(
            index = "1",
            path = "/downloads/file.zip",
            length = "1073741824",
            completedLength = "536870912",
            selected = "true",
            uris = listOf(uri)
        )

        val json = gson.toJson(file)
        val deserialized = gson.fromJson(json, MotrixDownload.DownloadFile::class.java)

        assertEquals(file.index, deserialized.index)
        assertEquals(file.path, deserialized.path)
        assertEquals(1, deserialized.uris?.size)
        assertEquals("https://example.com/file.zip", deserialized.uris?.get(0)?.uri)
    }

    @Test
    fun `MotrixGlobalStat serialization and deserialization`() {
        val stat = MotrixGlobalStat(
            downloadSpeed = "2097152",
            uploadSpeed = "1048576",
            numActive = "5",
            numWaiting = "3",
            numStopped = "15",
            numStoppedTotal = "200"
        )

        val json = gson.toJson(stat)
        val deserialized = gson.fromJson(json, MotrixGlobalStat::class.java)

        assertEquals(stat.downloadSpeed, deserialized.downloadSpeed)
        assertEquals(stat.uploadSpeed, deserialized.uploadSpeed)
        assertEquals(stat.numActive, deserialized.numActive)
        assertEquals(stat.numWaiting, deserialized.numWaiting)
        assertEquals(stat.numStopped, deserialized.numStopped)
    }

    @Test
    fun `MotrixGlobalStat with zero values`() {
        val stat = MotrixGlobalStat(
            downloadSpeed = "0",
            uploadSpeed = "0",
            numActive = "0",
            numWaiting = "0",
            numStopped = "0",
            numStoppedTotal = "0"
        )

        assertEquals("0", stat.downloadSpeed)
        assertEquals("0", stat.uploadSpeed)
        assertEquals("0", stat.numActive)
    }

    @Test
    fun `MotrixVersion serialization and deserialization`() {
        val version = MotrixVersion(
            version = "1.43.0",
            enabledFeatures = listOf("Async DNS", "BitTorrent", "Firefox3 Cookie", "GZip", "HTTPS", "Message Digest", "Metalink", "XML-RPC")
        )

        val json = gson.toJson(version)
        val deserialized = gson.fromJson(json, MotrixVersion::class.java)

        assertEquals(version.version, deserialized.version)
        assertNotNull(deserialized.enabledFeatures)
        assertEquals(8, deserialized.enabledFeatures?.size)
        assertTrue(deserialized.enabledFeatures!!.contains("BitTorrent"))
        assertTrue(deserialized.enabledFeatures.contains("HTTPS"))
    }

    @Test
    fun `MotrixVersion with null features`() {
        val version = MotrixVersion(
            version = "1.40.0",
            enabledFeatures = null
        )

        val json = gson.toJson(version)
        val deserialized = gson.fromJson(json, MotrixVersion::class.java)

        assertEquals("1.40.0", deserialized.version)
        assertNull(deserialized.enabledFeatures)
    }

    @Test
    fun `MotrixDownloadOptions with all fields`() {
        val options = MotrixDownloadOptions(
            directory = "/custom/downloads",
            outputFileName = "renamed-file.zip",
            connections = 32,
            maxDownloadSpeed = "5M",
            maxUploadSpeed = "1M",
            headers = listOf("Authorization: Bearer token", "User-Agent: Custom"),
            referer = "https://example.com/page",
            userAgent = "Custom User Agent"
        )

        val json = gson.toJson(options)
        val deserialized = gson.fromJson(json, MotrixDownloadOptions::class.java)

        assertEquals(options.directory, deserialized.directory)
        assertEquals(options.outputFileName, deserialized.outputFileName)
        assertEquals(options.connections, deserialized.connections)
        assertEquals(options.maxDownloadSpeed, deserialized.maxDownloadSpeed)
        assertEquals(2, deserialized.headers?.size)
    }

    @Test
    fun `MotrixDownloadOptions with minimal fields`() {
        val options = MotrixDownloadOptions(
            directory = "/downloads"
        )

        assertEquals("/downloads", options.directory)
        assertNull(options.outputFileName)
        assertNull(options.connections)
        assertNull(options.maxDownloadSpeed)
    }

    @Test
    fun `MotrixRpcRequest serialization`() {
        val request = MotrixRpcRequest(
            id = "request-123",
            method = "aria2.getVersion",
            params = listOf("token:secret")
        )

        val json = gson.toJson(request)
        val parsed = gson.fromJson(json, MotrixRpcRequest::class.java)

        assertEquals("2.0", parsed.jsonrpc)
        assertEquals("request-123", parsed.id)
        assertEquals("aria2.getVersion", parsed.method)
        assertEquals(1, parsed.params?.size)
    }

    @Test
    fun `MotrixRpcRequest with null params`() {
        val request = MotrixRpcRequest(
            id = "request-456",
            method = "aria2.getGlobalStat",
            params = null
        )

        val json = gson.toJson(request)
        val parsed = gson.fromJson(json, MotrixRpcRequest::class.java)

        assertEquals("aria2.getGlobalStat", parsed.method)
        assertNull(parsed.params)
    }

    @Test
    fun `MotrixRpcResponse with successful result`() {
        val response = MotrixRpcResponse(
            id = "response-123",
            jsonrpc = "2.0",
            result = "2089b05ecca3d829",
            error = null
        )

        val json = gson.toJson(response)
        assertTrue(json.contains("2089b05ecca3d829"))

        val typeToken = object : com.google.gson.reflect.TypeToken<MotrixRpcResponse<String>>() {}
        val parsed: MotrixRpcResponse<String> = gson.fromJson(json, typeToken.type)
        assertEquals("response-123", parsed.id)
        assertNotNull(parsed.result)
        assertNull(parsed.error)
    }

    @Test
    fun `MotrixRpcResponse with error`() {
        val error = MotrixRpcResponse.RpcError(
            code = 1,
            message = "Unauthorized"
        )

        val response = MotrixRpcResponse<String>(
            id = "error-response",
            jsonrpc = "2.0",
            result = null,
            error = error
        )

        assertNull(response.result)
        assertNotNull(response.error)
        assertEquals(1, response.error!!.code)
        assertEquals("Unauthorized", response.error.message)
    }

    @Test
    fun `MotrixRpcResponse RpcError serialization`() {
        val error = MotrixRpcResponse.RpcError(
            code = 404,
            message = "Resource not found"
        )

        val json = gson.toJson(error)
        val deserialized = gson.fromJson(json, MotrixRpcResponse.RpcError::class.java)

        assertEquals(404, deserialized.code)
        assertEquals("Resource not found", deserialized.message)
    }

    @Test
    fun `Data class equality and hashCode`() {
        val version1 = MotrixVersion(
            version = "1.43.0",
            enabledFeatures = listOf("BitTorrent")
        )

        val version2 = MotrixVersion(
            version = "1.43.0",
            enabledFeatures = listOf("BitTorrent")
        )

        assertEquals(version1, version2)
        assertEquals(version1.hashCode(), version2.hashCode())
    }

    @Test
    fun `Data class copy functionality`() {
        val original = MotrixDownloadOptions(
            directory = "/downloads",
            outputFileName = "original.zip",
            connections = 8
        )

        val modified = original.copy(outputFileName = "modified.zip", connections = 16)

        assertEquals("modified.zip", modified.outputFileName)
        assertEquals(16, modified.connections)
        assertEquals(original.directory, modified.directory) // Unchanged field
    }

    @Test
    fun `Download status types`() {
        val statuses = listOf("active", "waiting", "paused", "error", "complete", "removed")

        statuses.forEach { status ->
            val download = MotrixDownload(
                gid = "test-gid",
                status = status,
                totalLength = "1000000",
                completedLength = "500000",
                uploadLength = "0",
                downloadSpeed = "100000",
                uploadSpeed = "0",
                files = null,
                directory = null,
                errorCode = null,
                errorMessage = null,
                connections = null
            )

            assertEquals(status, download.status)
        }
    }

    @Test
    fun `Complex nested download structure`() {
        val uri = MotrixDownload.DownloadFile.Uri(
            uri = "https://example.com/file.zip",
            status = "used"
        )

        val file = MotrixDownload.DownloadFile(
            index = "1",
            path = "/downloads/file.zip",
            length = "1073741824",
            completedLength = "536870912",
            selected = "true",
            uris = listOf(uri)
        )

        val download = MotrixDownload(
            gid = "test-gid",
            status = "active",
            totalLength = "1073741824",
            completedLength = "536870912",
            uploadLength = "0",
            downloadSpeed = "1048576",
            uploadSpeed = "0",
            files = listOf(file),
            directory = "/downloads",
            errorCode = null,
            errorMessage = null,
            connections = "16"
        )

        val json = gson.toJson(download)
        val deserialized = gson.fromJson(json, MotrixDownload::class.java)

        assertNotNull(deserialized.files)
        assertEquals(1, deserialized.files?.size)
        assertNotNull(deserialized.files?.get(0)?.uris)
        assertEquals("https://example.com/file.zip", deserialized.files?.get(0)?.uris?.get(0)?.uri)
    }
}
