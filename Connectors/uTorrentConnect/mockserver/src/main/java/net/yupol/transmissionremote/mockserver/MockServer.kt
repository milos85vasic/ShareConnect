package net.yupol.transmissionremote.mockserver

import com.dampcake.bencode.Bencode
import com.dampcake.bencode.Type
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToJsonElement
import net.yupol.transmissionremote.mockserver.bencode.BencodeTorrent
import net.yupol.transmissionremote.mockserver.model.Torrent
import net.yupol.transmissionremote.mockserver.rpc.RpcArguments
import net.yupol.transmissionremote.mockserver.rpc.RpcFile
import net.yupol.transmissionremote.mockserver.rpc.RpcFileStats
import net.yupol.transmissionremote.mockserver.rpc.RpcFreeSpace
import net.yupol.transmissionremote.mockserver.rpc.RpcRequest
import net.yupol.transmissionremote.mockserver.rpc.RpcResponse
import net.yupol.transmissionremote.mockserver.rpc.RpcSession
import net.yupol.transmissionremote.mockserver.rpc.RpcStatus
import net.yupol.transmissionremote.mockserver.rpc.RpcTorrent
import net.yupol.transmissionremote.mockserver.rpc.RpcTorrents
import net.yupol.transmissionremote.mockserver.utils.nowSeconds
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import java.util.Base64
import javax.inject.Inject

@OptIn(ExperimentalSerializationApi::class)
class MockServer @Inject constructor() {
    val hostName: String
        get() = mockWebServer.hostName
    val port: Int
        get() = mockWebServer.port

    private val mockWebServer = MockWebServer()
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
        isLenient = true
        explicitNulls = false
    }
    private val session = RpcSession()
    private val torrents = RpcTorrents(torrents = emptyList())
    private var nextTorrentId = 1

    init {
        mockWebServer.dispatcher = object : Dispatcher() {
            override fun dispatch(request: RecordedRequest): MockResponse {
                val rpcRequest = json.decodeFromStream<RpcRequest>(request.body.inputStream())
                return when (rpcRequest.method) {
                    "torrent-start" -> {
                        startTorrents(ids = rpcRequest.getIntArrayArgument(key = "ids").orEmpty())
                    }
                    "torrent-start-now" -> TODO()
                    "torrent-stop" -> {
                        stopTorrents(ids = rpcRequest.getIntArrayArgument(key = "ids").orEmpty())
                    }
                    "torrent-verify" -> TODO()
                    "torrent-reannounce" -> TODO()
                    "torrent-set" -> TODO()
                    "torrent-get" -> {
                        getTorrents(ids = rpcRequest.getIntArrayArgument(key = "ids").orEmpty())
                    }
                    "torrent-add" -> {
                        addTorrent(
                            metaInfo = rpcRequest.getStringArgument(key = "metainfo"),
                            paused = rpcRequest.getBooleanArgument("paused") ?: false
                        )
                    }
                    "torrent-remove" -> {
                        removeTorrents(ids = rpcRequest.getIntArrayArgument(key = "ids").orEmpty())
                    }
                    "torrent-set-location" -> TODO()
                    "torrent-rename-path" -> TODO()
                    "session-set" -> TODO()
                    "session-get" -> getSession()
                    "session-stats" -> TODO()
                    "blocklist-update" -> TODO()
                    "port-test" -> TODO()
                    "session-close" -> TODO()
                    "queue-move-top" -> TODO()
                    "queue-move-up" -> TODO()
                    "queue-move-down" -> TODO()
                    "queue-move-bottom" -> TODO()
                    "free-space" -> {
                        getFreeSpace(path = rpcRequest.getStringArgument(key = "path"))
                    }
                    "group-set" -> TODO()
                    "group-get" -> TODO()
                    else -> {
                        throw IllegalStateException("Unknown RPC method: ${rpcRequest.method}")
                    }
                }
            }
        }
    }

    fun start(port: Int = 0) = mockWebServer.start(port)

    fun shutdown() = mockWebServer.shutdown()

    fun addTorrent(
        name: String,
        paused: Boolean = false,
        size: Torrent.Size,
        dates: Torrent.Dates = Torrent.Dates(
            added = System.currentTimeMillis() / 1000,
            created = System.currentTimeMillis() / 1000
        ),
        secondsDownloading: Int = 0,
        secondsSeeding: Int = 0,
        downloadDir: String = "/",
        creator: String = "",
        comment: String = "",
        magnetLink: String = ""
    ) {
        torrents.torrents += RpcTorrent(
            id = nextTorrentId++,
            name = name,
            status = if (paused) RpcStatus.STOPPED.status else RpcStatus.DOWNLOAD.status,
            totalSize = size.totalSize,
            sizeWhenDone = size.sizeWhenDone,
            leftUntilDone = size.leftUntilDone,
            desiredAvailable = size.desiredAvailable,
            haveUnchecked = size.haveUnchecked,
            haveValid = size.haveValid,
            downloadedEver = size.downloadedEver,
            uploadedEver = size.uploadedEver,
            secondsDownloading = secondsDownloading,
            secondsSeeding = secondsSeeding,
            pieceSize = size.pieceSize,
            pieceCount = size.pieceCount,
            addedDate = dates.added,
            dateCreated = dates.created,
            doneDate = dates.done,
            activityDate = dates.activity,
            queuePosition = torrents.torrents.size + 1,
            files = listOf(
                RpcFile(
                    name = name,
                    length = size.totalSize
                )
            ),
            fileStats = listOf(
                RpcFileStats(
                    wanted = true
                )
            ),
            downloadDir = downloadDir,
            creator = creator,
            comment = comment,
            magnetLink = magnetLink
        )
    }

    private fun getSession(): MockResponse {
        return successResponse(arguments = session)
    }

    private fun getTorrents(ids: List<Int>): MockResponse {
        val selectedTorrents = if (ids.isEmpty()) {
            torrents.torrents
        } else {
            torrents.torrents.filter { torrent ->
                ids.contains(torrent.id)
            }
        }
        return successResponse(arguments = torrents.copy(torrents = selectedTorrents))
    }

    private fun getFreeSpace(path: String?): MockResponse {
        return successResponse(arguments = RpcFreeSpace(path = path, size = 50L * 1024 * 1024 * 1024))
    }

    private fun addTorrent(metaInfo: String?, paused: Boolean): MockResponse {
        requireNotNull(metaInfo) { "MetaInfo must be non-null" }

        val bytes = Base64.getDecoder().decode(metaInfo.replace("\n", "", false))
        val data = Bencode().decode(bytes, Type.DICTIONARY)
        val bencodeTorrent = BencodeTorrent.fromMap(data)
        addTorrent(
            name = bencodeTorrent.info.name,
            paused = paused,
            size = Torrent.Size(
                totalSize = bencodeTorrent.totalSize
            ),
            dates = Torrent.Dates(
                added = nowSeconds(),
                created = bencodeTorrent.creationDate
            ),
            creator = bencodeTorrent.createdBy.orEmpty(),
            comment = bencodeTorrent.comment
        )
        return successResponse()
    }

    private fun removeTorrents(ids: List<Int>): MockResponse {
        torrents.torrents = torrents.torrents.filter { it.id !in ids }
        return successResponse()
    }

    private fun startTorrents(ids: List<Int>): MockResponse {
        torrents.torrents = torrents.torrents.map { torrent ->
            if (torrent.id in ids) {
                torrent.copy(status = RpcStatus.DOWNLOAD.status)
            } else {
                torrent
            }
        }
        return successResponse()
    }

    private fun stopTorrents(ids: List<Int>): MockResponse {
        torrents.torrents = torrents.torrents.map { torrent ->
            if (torrent.id in ids) {
                torrent.copy(status = RpcStatus.STOPPED.status)
            } else {
                torrent
            }
        }
        return successResponse()
    }

    private fun successResponse(arguments: RpcArguments? = null): MockResponse {
        return MockResponse().apply {
            setResponseCode(code = 200)
            val response = RpcResponse(
                result = "success",
                arguments = if (arguments != null) {
                    json.encodeToJsonElement(arguments)
                } else {
                    JsonObject(emptyMap())
                }
            )
            setBody(json.encodeToString(response))
        }
    }
}
