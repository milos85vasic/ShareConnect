package com.shareconnect.utorrentconnect.transport.response

import com.shareconnect.utorrentconnect.model.json.Torrent
import org.json.JSONArray
import org.json.JSONObject

/**
 * Parser for uTorrent Web API responses.
 *
 * uTorrent uses an array-based format for efficiency:
 * {
 *   "torrents": [
 *     ["HASH", status, "name", size, progress, downloaded, uploaded, ratio, uploadSpeed, downloadSpeed, eta, ...],
 *     ...
 *   ],
 *   "torrentc": "CACHE_ID"
 * }
 *
 * Array indices correspond to torrent properties as documented in API_IMPLEMENTATION.md
 */
object UTorrentResponseParser {

    /**
     * Parse uTorrent torrent list response and convert to Torrent objects.
     *
     * @param responseBody JSON response from uTorrent /gui/?list=1
     * @return List of Torrent objects
     */
    fun parseTorrentList(responseBody: String): List<Torrent> {
        val torrents = mutableListOf<Torrent>()

        try {
            val json = JSONObject(responseBody)
            val torrentsArray = json.optJSONArray("torrents") ?: return emptyList()

            for (i in 0 until torrentsArray.length()) {
                val torrentArray = torrentsArray.getJSONArray(i)
                val torrent = parseTorrentFromArray(torrentArray)
                torrents.add(torrent)
            }
        } catch (e: Exception) {
            android.util.Log.e("UTorrentParser", "Error parsing torrent list", e)
        }

        return torrents
    }

    /**
     * Parse a single torrent from uTorrent's array format.
     *
     * Array format (indices):
     * 0:  hash (String)
     * 1:  status (Int) - bitfield
     * 2:  name (String)
     * 3:  size (Long)
     * 4:  progress (Int) - per mille (0-1000)
     * 5:  downloaded (Long)
     * 6:  uploaded (Long)
     * 7:  ratio (Int) - ratio * 1000
     * 8:  uploadSpeed (Long) - bytes/sec
     * 9:  downloadSpeed (Long) - bytes/sec
     * 10: eta (Int) - seconds (-1 = unknown)
     * 11: label (String)
     * 12: peersConnected (Int)
     * 13: peersInSwarm (Int)
     * 14: seedsConnected (Int)
     * 15: seedsInSwarm (Int)
     * 16: availability (Float)
     * 17: queueOrder (Int)
     * 18: remaining (Long)
     */
    private fun parseTorrentFromArray(array: JSONArray): Torrent {
        return try {
            // Extract values from array with safe defaults
            val hash = array.optString(0, "")
            val status = array.optInt(1, 0)
            val name = array.optString(2, "")
            val size = array.optLong(3, 0)
            val progressPermille = array.optInt(4, 0)
            val downloaded = array.optLong(5, 0)
            val uploaded = array.optLong(6, 0)
            val ratioThousandths = array.optInt(7, 0)
            val uploadSpeed = array.optLong(8, 0).toInt()
            val downloadSpeed = array.optLong(9, 0).toInt()
            val eta = array.optInt(10, -1).toLong()
            // label at index 11 (not used in Torrent model)
            val peersGettingFromUs = array.optInt(12, 0)
            val peersSendingToUs = array.optInt(13, 0)
            // seeds at index 14-15 (can be added if needed)
            // availability at index 16
            val queueOrder = array.optInt(17, -1)
            val remaining = array.optLong(18, 0)

            // Build torrent using Builder pattern
            Torrent.Builder()
                .id(hash.hashCode()) // Use hash code as numeric ID
                .hash(hash)
                .name(name)
                .totalSize(size)
                .percentDone(progressPermille / 1000.0) // Convert per mille to fraction
                .status(mapUTorrentStatus(status))
                .uploadRate(uploadSpeed)
                .downloadRate(downloadSpeed)
                .eta(eta)
                .uploadedSize(uploaded)
                .uploadRatio(ratioThousandths / 1000.0)
                .peersGettingFromUs(peersGettingFromUs)
                .peersSendingToUs(peersSendingToUs)
                .queuePosition(queueOrder)
                .leftUntilDone(remaining)
                .sizeWhenDone(size)
                .isFinished(progressPermille >= 1000)
                .addedDate(System.currentTimeMillis() / 1000) // Not in response
                .build()

        } catch (e: Exception) {
            android.util.Log.e("UTorrentParser", "Error parsing torrent from array", e)
            Torrent() // Return empty torrent on error
        }
    }

    /**
     * Map uTorrent status bitfield to Transmission status enum value.
     *
     * uTorrent status flags:
     * 1    = Started
     * 2    = Checking
     * 4    = Start after check
     * 8    = Checked
     * 16   = Error
     * 32   = Paused
     * 64   = Queued
     * 128  = Loaded
     *
     * Transmission status values:
     * 0 = Stopped
     * 1 = Check wait
     * 2 = Checking
     * 3 = Download wait
     * 4 = Downloading
     * 5 = Seed wait
     * 6 = Seeding
     */
    private fun mapUTorrentStatus(utorrentStatus: Int): Int {
        return when {
            utorrentStatus and 2 != 0 -> 2 // Checking
            utorrentStatus and 32 != 0 -> 0 // Paused -> Stopped
            utorrentStatus and 64 != 0 -> 3 // Queued -> Download wait
            utorrentStatus and 1 != 0 -> {
                // Started - determine if downloading or seeding based on other info
                // For now, default to downloading (4)
                // This could be refined by checking progress
                4
            }
            else -> 0 // Stopped
        }
    }

    /**
     * Wrap uTorrent response in Transmission-compatible format.
     * This allows existing code to work without changes.
     */
    fun wrapInTransmissionFormat(responseBody: String): String {
        return try {
            val torrents = parseTorrentList(responseBody)

            // Create Transmission-style response
            val wrappedResponse = JSONObject()
            wrappedResponse.put("result", "success")

            val arguments = JSONObject()
            val torrentsArray = JSONArray()

            torrents.forEach { torrent ->
                val torrentJson = JSONObject().apply {
                    put("id", torrent.id)
                    put("hashString", torrent.hash)
                    put("name", torrent.name)
                    put("totalSize", torrent.totalSize)
                    put("percentDone", torrent.percentDone)
                    put("status", torrent.status.value)
                    put("rateDownload", torrent.downloadRate)
                    put("rateUpload", torrent.uploadRate)
                    put("eta", torrent.eta)
                    put("uploadedEver", torrent.uploadedSize)
                    put("uploadRatio", torrent.uploadRatio)
                    put("peersGettingFromUs", torrent.peersGettingFromUs)
                    put("peersSendingToUs", torrent.peersSendingToUs)
                    put("queuePosition", torrent.queuePosition)
                    put("leftUntilDone", torrent.leftUntilDone)
                    put("sizeWhenDone", torrent.sizeWhenDone)
                    put("isFinished", torrent.isFinished)
                    put("error", 0)
                    put("errorString", "")
                }
                torrentsArray.put(torrentJson)
            }

            arguments.put("torrents", torrentsArray)
            wrappedResponse.put("arguments", arguments)

            wrappedResponse.toString()
        } catch (e: Exception) {
            android.util.Log.e("UTorrentParser", "Error wrapping response", e)
            responseBody // Return original on error
        }
    }
}
