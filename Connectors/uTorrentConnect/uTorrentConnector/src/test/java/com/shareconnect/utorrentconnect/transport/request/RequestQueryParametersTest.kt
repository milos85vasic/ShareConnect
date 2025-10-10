package com.shareconnect.utorrentconnect.transport.request

import com.google.common.truth.Truth.assertThat
import com.shareconnect.utorrentconnect.model.json.Torrent
import org.json.JSONObject
import org.junit.Test
import java.io.File

/**
 * Unit tests for Request query parameter generation.
 * Tests that all request classes generate correct uTorrent API query parameters.
 */
class RequestQueryParametersTest {

    @Test
    fun torrentGetRequest_generatesListParameter() {
        val request = TorrentGetRequest()

        val params = request.queryParameters

        assertThat(params).containsKey("list")
        assertThat(params["list"]).isEqualTo("1")
    }

    @Test
    fun addTorrentByUrlRequest_generatesCorrectParameters() {
        val magnetLink = "magnet:?xt=urn:btih:abc123"
        val request = AddTorrentByUrlRequest(magnetLink, null, false)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("add-url")
        assertThat(params).containsKey("s")
        assertThat(params["s"]).isEqualTo(magnetLink)
    }

    // Note: AddTorrentByFileRequest test skipped because it uses Android's Base64 class
    // which is not available in unit tests. This would need to be tested in instrumented tests.

    @Test
    fun torrentRemoveRequest_withoutDeleteData_generatesRemoveAction() {
        val torrentIds = intArrayOf(12345)
        val request = TorrentRemoveRequest(torrentIds, false)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("remove")
        assertThat(params).containsKey("hash")
    }

    @Test
    fun torrentRemoveRequest_withDeleteData_generatesRemoveDataAction() {
        val torrentIds = intArrayOf(12345)
        val request = TorrentRemoveRequest(torrentIds, true)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("removedata")
        assertThat(params).containsKey("hash")
    }

    @Test
    fun startTorrentRequest_generatesStartAction() {
        val request = StartTorrentRequest(12345)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("start")
    }

    @Test
    fun stopTorrentRequest_generatesStopAction() {
        val request = StopTorrentRequest(12345)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("stop")
    }

    @Test
    fun verifyTorrentRequest_generatesRecheckAction() {
        val request = VerifyTorrentRequest(12345)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("recheck")
    }

    @Test
    fun sessionGetRequest_generatesGetSettingsAction() {
        val request = SessionGetRequest()

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("getsettings")
    }

    @Test
    fun sessionSetRequest_generatesSetSettingAction() {
        val arguments = JSONObject().apply {
            put("speed-limit-down", 1024)
        }
        val request = SessionSetRequest(arguments)

        val params = request.queryParameters

        assertThat(params).containsKey("action")
        assertThat(params["action"]).isEqualTo("setsetting")
    }

    @Test
    fun torrentActionRequest_toHashes_extractsHashesFromTorrents() {
        val torrents = listOf(
            Torrent.Builder().hash("hash1").build(),
            Torrent.Builder().hash("hash2").build(),
            Torrent.Builder().hash("hash3").build()
        )

        val hashes = TorrentActionRequest.toHashes(torrents)

        assertThat(hashes).hasLength(3)
        assertThat(hashes[0]).isEqualTo("hash1")
        assertThat(hashes[1]).isEqualTo("hash2")
        assertThat(hashes[2]).isEqualTo("hash3")
    }

    @Test
    fun torrentActionRequest_toHashes_fallsBackToIdWhenHashMissing() {
        val torrents = listOf(
            Torrent.Builder().id(123).hash(null).build(),
            Torrent.Builder().id(456).hash("").build()
        )

        val hashes = TorrentActionRequest.toHashes(torrents)

        assertThat(hashes).hasLength(2)
        assertThat(hashes[0]).isEqualTo("123")
        assertThat(hashes[1]).isEqualTo("456")
    }

    @Test
    fun torrentActionRequest_toIds_extractsIdsFromTorrents() {
        val torrents = listOf(
            Torrent.Builder().id(111).build(),
            Torrent.Builder().id(222).build(),
            Torrent.Builder().id(333).build()
        )

        val ids = TorrentActionRequest.toIds(torrents)

        assertThat(ids).hasLength(3)
        assertThat(ids[0]).isEqualTo(111)
        assertThat(ids[1]).isEqualTo(222)
        assertThat(ids[2]).isEqualTo(333)
    }

    @Test
    fun baseRequest_getQueryParameters_defaultImplementationReturnsEmptyMap() {
        // Create an anonymous request subclass to test base implementation
        val request = object : Request<Void>(Void::class.java) {
            override fun getMethod(): String = "test-method"
            override fun getArguments(): JSONObject? = null
            // Uses default getQueryParameters() from base class
        }

        val params = request.queryParameters

        assertThat(params).isEmpty()
    }
}
