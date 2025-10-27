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
