package com.shareconnect.nextcloudconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * Nextcloud file/folder model
 */
data class NextcloudFile(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("type")
    val type: String, // "file" or "directory"

    @SerializedName("size")
    val size: Long,

    @SerializedName("mtime")
    val modifiedTime: Long,

    @SerializedName("mime")
    val mimeType: String?,

    @SerializedName("etag")
    val etag: String?,

    @SerializedName("permissions")
    val permissions: String?
)

/**
 * Nextcloud server status response
 */
data class NextcloudStatus(
    @SerializedName("installed")
    val installed: Boolean,

    @SerializedName("maintenance")
    val maintenance: Boolean,

    @SerializedName("needsDbUpgrade")
    val needsDbUpgrade: Boolean,

    @SerializedName("version")
    val version: String,

    @SerializedName("versionstring")
    val versionString: String,

    @SerializedName("edition")
    val edition: String,

    @SerializedName("productname")
    val productName: String
)

/**
 * Nextcloud user information
 */
data class NextcloudUser(
    @SerializedName("id")
    val id: String,

    @SerializedName("displayname")
    val displayName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("quota")
    val quota: Quota?
) {
    data class Quota(
        @SerializedName("free")
        val free: Long,

        @SerializedName("used")
        val used: Long,

        @SerializedName("total")
        val total: Long,

        @SerializedName("relative")
        val relative: Float
    )
}

/**
 * Nextcloud API response wrapper
 */
data class NextcloudResponse<T>(
    @SerializedName("ocs")
    val ocs: OcsData<T>
) {
    data class OcsData<T>(
        @SerializedName("meta")
        val meta: Meta,

        @SerializedName("data")
        val data: T?
    ) {
        data class Meta(
            @SerializedName("status")
            val status: String,

            @SerializedName("statuscode")
            val statusCode: Int,

            @SerializedName("message")
            val message: String?
        )
    }
}

/**
 * Nextcloud share information
 */
data class NextcloudShare(
    @SerializedName("id")
    val id: String,

    @SerializedName("share_type")
    val shareType: Int,

    @SerializedName("uid_owner")
    val owner: String,

    @SerializedName("displayname_owner")
    val ownerDisplayName: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("item_type")
    val itemType: String,

    @SerializedName("mimetype")
    val mimeType: String?,

    @SerializedName("file_target")
    val fileTarget: String,

    @SerializedName("url")
    val url: String?
)
