package com.shareconnect.motrixconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * Motrix download status
 */
data class MotrixDownload(
    @SerializedName("gid")
    val gid: String,

    @SerializedName("status")
    val status: String, // active, waiting, paused, error, complete, removed

    @SerializedName("totalLength")
    val totalLength: String,

    @SerializedName("completedLength")
    val completedLength: String,

    @SerializedName("uploadLength")
    val uploadLength: String,

    @SerializedName("downloadSpeed")
    val downloadSpeed: String,

    @SerializedName("uploadSpeed")
    val uploadSpeed: String,

    @SerializedName("files")
    val files: List<DownloadFile>?,

    @SerializedName("dir")
    val directory: String?,

    @SerializedName("errorCode")
    val errorCode: String?,

    @SerializedName("errorMessage")
    val errorMessage: String?,

    @SerializedName("connections")
    val connections: String?
) {
    data class DownloadFile(
        @SerializedName("index")
        val index: String,

        @SerializedName("path")
        val path: String,

        @SerializedName("length")
        val length: String,

        @SerializedName("completedLength")
        val completedLength: String,

        @SerializedName("selected")
        val selected: String,

        @SerializedName("uris")
        val uris: List<Uri>?
    ) {
        data class Uri(
            @SerializedName("uri")
            val uri: String,

            @SerializedName("status")
            val status: String
        )
    }
}

/**
 * Motrix global statistics
 */
data class MotrixGlobalStat(
    @SerializedName("downloadSpeed")
    val downloadSpeed: String,

    @SerializedName("uploadSpeed")
    val uploadSpeed: String,

    @SerializedName("numActive")
    val numActive: String,

    @SerializedName("numWaiting")
    val numWaiting: String,

    @SerializedName("numStopped")
    val numStopped: String,

    @SerializedName("numStoppedTotal")
    val numStoppedTotal: String
)

/**
 * Motrix version information
 */
data class MotrixVersion(
    @SerializedName("version")
    val version: String,

    @SerializedName("enabledFeatures")
    val enabledFeatures: List<String>?
)

/**
 * Motrix download options
 */
data class MotrixDownloadOptions(
    @SerializedName("dir")
    val directory: String? = null,

    @SerializedName("out")
    val outputFileName: String? = null,

    @SerializedName("split")
    val connections: Int? = null,

    @SerializedName("max-download-limit")
    val maxDownloadSpeed: String? = null,

    @SerializedName("max-upload-limit")
    val maxUploadSpeed: String? = null,

    @SerializedName("header")
    val headers: List<String>? = null,

    @SerializedName("referer")
    val referer: String? = null,

    @SerializedName("user-agent")
    val userAgent: String? = null
)

/**
 * JSON-RPC request
 */
data class MotrixRpcRequest(
    @SerializedName("jsonrpc")
    val jsonrpc: String = "2.0",

    @SerializedName("id")
    val id: String,

    @SerializedName("method")
    val method: String,

    @SerializedName("params")
    val params: List<Any>? = null
)

/**
 * JSON-RPC response
 */
data class MotrixRpcResponse<T>(
    @SerializedName("id")
    val id: String,

    @SerializedName("jsonrpc")
    val jsonrpc: String,

    @SerializedName("result")
    val result: T?,

    @SerializedName("error")
    val error: RpcError?
) {
    data class RpcError(
        @SerializedName("code")
        val code: Int,

        @SerializedName("message")
        val message: String
    )
}
