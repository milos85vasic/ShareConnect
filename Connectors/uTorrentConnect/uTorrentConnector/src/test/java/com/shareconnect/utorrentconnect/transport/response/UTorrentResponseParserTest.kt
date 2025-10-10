package com.shareconnect.utorrentconnect.transport.response

import com.google.common.truth.Truth.assertThat
import com.shareconnect.utorrentconnect.model.json.Torrent
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Test

/**
 * Unit tests for UTorrentResponseParser.
 * Tests parsing of uTorrent's array-based response format.
 */
class UTorrentResponseParserTest {

    @Test
    fun parseTorrentList_emptyResponse_returnsEmptyList() {
        val response = "{}"
        val result = UTorrentResponseParser.parseTorrentList(response)
        assertThat(result).isEmpty()
    }

    @Test
    fun parseTorrentList_noTorrentsArray_returnsEmptyList() {
        val response = """{"build": 25460, "torrentc": "12345"}"""
        val result = UTorrentResponseParser.parseTorrentList(response)
        assertThat(result).isEmpty()
    }

    @Test
    fun parseTorrentList_singleTorrent_parsesCorrectly() {
        val response = """{
            "torrents": [
                ["ABCD1234HASH", 201, "Test Torrent", 1073741824, 750, 102400, 204800, 2000, 102400, 51200, 3600, "Label", 5, 10, 3, 5, 1.990, 1, 268435456]
            ],
            "torrentc": "12345"
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(1)
        val torrent = result[0]
        assertThat(torrent.hash).isEqualTo("ABCD1234HASH")
        assertThat(torrent.name).isEqualTo("Test Torrent")
        assertThat(torrent.totalSize).isEqualTo(1073741824)
        assertThat(torrent.percentDone).isWithin(0.001).of(0.750) // 750 per mille = 0.750
        assertThat(torrent.uploadedSize).isEqualTo(204800)
        assertThat(torrent.uploadRatio).isWithin(0.001).of(2.0) // 2000 / 1000
        assertThat(torrent.uploadRate).isEqualTo(102400)
        assertThat(torrent.downloadRate).isEqualTo(51200)
        assertThat(torrent.eta).isEqualTo(3600)
        assertThat(torrent.peersGettingFromUs).isEqualTo(5)
        assertThat(torrent.peersSendingToUs).isEqualTo(10)
        assertThat(torrent.queuePosition).isEqualTo(1)
        assertThat(torrent.leftUntilDone).isEqualTo(268435456)
    }

    @Test
    fun parseTorrentList_multipleTorrents_parsesAll() {
        val response = """{
            "torrents": [
                ["HASH1", 1, "Torrent 1", 1000000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500000],
                ["HASH2", 32, "Torrent 2", 2000000, 1000, 2000000, 1000000, 500, 1024, 2048, 0, "", 2, 5, 1, 3, 1.5, 1, 0],
                ["HASH3", 64, "Torrent 3", 3000000, 250, 750000, 500000, 666, 512, 1024, 1800, "", 1, 10, 0, 5, 0.8, 2, 2250000]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(3)
        assertThat(result[0].hash).isEqualTo("HASH1")
        assertThat(result[1].hash).isEqualTo("HASH2")
        assertThat(result[2].hash).isEqualTo("HASH3")
    }

    @Test
    fun parseTorrentFromArray_completedTorrent_isFinishedTrue() {
        val response = """{
            "torrents": [
                ["COMPLETED", 201, "Completed Torrent", 1000000, 1000, 1000000, 2000000, 2000, 1024, 0, -1, "", 5, 0, 2, 0, 2.0, 0, 0]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(1)
        assertThat(result[0].isFinished).isTrue()
        assertThat(result[0].percentDone).isWithin(0.001).of(1.0)
    }

    @Test
    fun parseTorrentFromArray_inProgressTorrent_isFinishedFalse() {
        val response = """{
            "torrents": [
                ["INPROGRESS", 1, "In Progress", 1000000, 500, 500000, 100000, 200, 512, 1024, 500, "", 2, 5, 1, 3, 1.5, 0, 500000]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(1)
        assertThat(result[0].isFinished).isFalse()
        assertThat(result[0].percentDone).isWithin(0.001).of(0.5)
    }

    @Test
    fun mapUTorrentStatus_started_returnsDownloading() {
        val response = """{
            "torrents": [
                ["HASH", 1, "Started", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result[0].status).isEqualTo(Torrent.Status.DOWNLOAD)
    }

    @Test
    fun mapUTorrentStatus_checking_returnsCheck() {
        val response = """{
            "torrents": [
                ["HASH", 2, "Checking", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result[0].status).isEqualTo(Torrent.Status.CHECK)
    }

    @Test
    fun mapUTorrentStatus_paused_returnsStopped() {
        val response = """{
            "torrents": [
                ["HASH", 32, "Paused", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result[0].status).isEqualTo(Torrent.Status.STOPPED)
    }

    @Test
    fun mapUTorrentStatus_queued_returnsDownloadWait() {
        val response = """{
            "torrents": [
                ["HASH", 64, "Queued", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result[0].status).isEqualTo(Torrent.Status.DOWNLOAD_WAIT)
    }

    @Test
    fun mapUTorrentStatus_combinedFlags_prioritizesCorrectly() {
        // Status 201 = 1 (Started) + 8 (Checked) + 64 (Queued) + 128 (Loaded)
        // Should prioritize Queued (64) over Started (1)
        val response = """{
            "torrents": [
                ["HASH", 201, "Complex", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        // With current implementation, checking for flags in order: 2, 32, 64, 1
        // 201 has 64 (Queued) and 1 (Started)
        // Should return DOWNLOAD_WAIT for queued
        assertThat(result[0].status).isEqualTo(Torrent.Status.DOWNLOAD_WAIT)
    }

    @Test
    fun wrapInTransmissionFormat_createsValidTransmissionResponse() {
        val utorrentResponse = """{
            "torrents": [
                ["HASH123", 1, "Test", 1000000, 500, 500000, 100000, 200, 512, 1024, 500, "", 2, 5, 1, 3, 1.5, 0, 500000]
            ]
        }"""

        val wrapped = UTorrentResponseParser.wrapInTransmissionFormat(utorrentResponse)
        val json = JSONObject(wrapped)

        assertThat(json.has("result")).isTrue()
        assertThat(json.getString("result")).isEqualTo("success")
        assertThat(json.has("arguments")).isTrue()

        val arguments = json.getJSONObject("arguments")
        assertThat(arguments.has("torrents")).isTrue()

        val torrents = arguments.getJSONArray("torrents")
        assertThat(torrents.length()).isEqualTo(1)

        val torrent = torrents.getJSONObject(0)
        assertThat(torrent.has("hashString")).isTrue()
        assertThat(torrent.getString("hashString")).isEqualTo("HASH123")
        assertThat(torrent.has("name")).isTrue()
        assertThat(torrent.getString("name")).isEqualTo("Test")
    }

    @Test
    fun wrapInTransmissionFormat_emptyTorrentList_createsEmptyArray() {
        val utorrentResponse = """{"torrents": []}"""

        val wrapped = UTorrentResponseParser.wrapInTransmissionFormat(utorrentResponse)
        val json = JSONObject(wrapped)

        val arguments = json.getJSONObject("arguments")
        val torrents = arguments.getJSONArray("torrents")
        assertThat(torrents.length()).isEqualTo(0)
    }

    @Test
    fun wrapInTransmissionFormat_multipleTorrents_preservesAllData() {
        val utorrentResponse = """{
            "torrents": [
                ["HASH1", 1, "Torrent 1", 1000000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500000],
                ["HASH2", 32, "Torrent 2", 2000000, 1000, 2000000, 1000000, 500, 1024, 2048, 0, "", 2, 5, 1, 3, 1.5, 1, 0]
            ]
        }"""

        val wrapped = UTorrentResponseParser.wrapInTransmissionFormat(utorrentResponse)
        val json = JSONObject(wrapped)

        val torrents = json.getJSONObject("arguments").getJSONArray("torrents")
        assertThat(torrents.length()).isEqualTo(2)

        val torrent1 = torrents.getJSONObject(0)
        assertThat(torrent1.getString("hashString")).isEqualTo("HASH1")

        val torrent2 = torrents.getJSONObject(1)
        assertThat(torrent2.getString("hashString")).isEqualTo("HASH2")
    }

    // Note: parseTorrentList_malformedJson test skipped because error handling
    // uses Android's Log class which is not available in unit tests.
    // This would need to be tested in instrumented tests.

    @Test
    fun parseTorrentFromArray_zeroValues_handlesGracefully() {
        val response = """{
            "torrents": [
                ["ZERO", 0, "", 0, 0, 0, 0, 0, 0, 0, 0, "", 0, 0, 0, 0, 0, 0, 0]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(1)
        assertThat(result[0].hash).isEqualTo("ZERO")
        assertThat(result[0].totalSize).isEqualTo(0)
        assertThat(result[0].percentDone).isWithin(0.001).of(0.0)
    }

    @Test
    fun parseTorrentFromArray_negativeEta_handlesCorrectly() {
        val response = """{
            "torrents": [
                ["HASH", 1, "Unknown ETA", 1000000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500000]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result[0].eta).isEqualTo(-1)
    }

    @Test
    fun parseTorrentFromArray_largeValues_handlesCorrectly() {
        val response = """{
            "torrents": [
                ["LARGE", 1, "Large Torrent", 10737418240, 999, 10000000000, 5000000000, 5000, 104857600, 52428800, 100, "", 100, 500, 50, 250, 10.5, 5, 737418240]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        assertThat(result).hasSize(1)
        assertThat(result[0].totalSize).isEqualTo(10737418240L)
        assertThat(result[0].uploadedSize).isEqualTo(5000000000L)
        assertThat(result[0].uploadRate).isEqualTo(104857600)
        assertThat(result[0].downloadRate).isEqualTo(52428800)
    }

    @Test
    fun parseTorrentFromArray_idGeneratedFromHash_isConsistent() {
        val response = """{
            "torrents": [
                ["SAMEHASH", 1, "Test 1", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500],
                ["SAMEHASH", 1, "Test 2", 1000, 500, 0, 0, 0, 0, 0, -1, "", 0, 0, 0, 0, 0, 0, 500]
            ]
        }"""

        val result = UTorrentResponseParser.parseTorrentList(response)

        // Same hash should generate same ID
        assertThat(result[0].id).isEqualTo(result[1].id)
    }
}
