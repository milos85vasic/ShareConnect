package com.shareconnect.seafileconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * Seafile authentication response containing auth token
 */
data class SeafileAuthResponse(
    @SerializedName("token")
    val token: String
)

/**
 * Seafile account information
 */
data class SeafileAccount(
    @SerializedName("email")
    val email: String,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("usage")
    val usage: Long = 0,

    @SerializedName("total")
    val total: Long = 0,

    @SerializedName("department")
    val department: String? = null,

    @SerializedName("contact_email")
    val contactEmail: String? = null,

    @SerializedName("institution")
    val institution: String? = null,

    @SerializedName("create_time")
    val createTime: Long = 0
)

/**
 * Seafile library (repository) information
 */
data class SeafileLibrary(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("desc")
    val description: String? = null,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    @SerializedName("mtime")
    val modifiedTime: Long = 0,

    @SerializedName("size")
    val size: Long = 0,

    @SerializedName("encrypted")
    val encrypted: Boolean = false,

    @SerializedName("permission")
    val permission: String = "r",

    @SerializedName("type")
    val type: String = "repo",

    @SerializedName("virtual")
    val virtual: Boolean = false,

    @SerializedName("version")
    val version: Int = 0
)

/**
 * Detailed library information
 */
data class SeafileLibraryDetail(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("desc")
    val description: String? = null,

    @SerializedName("owner")
    val owner: String,

    @SerializedName("owner_name")
    val ownerName: String? = null,

    @SerializedName("owner_nickname")
    val ownerNickname: String? = null,

    @SerializedName("mtime")
    val modifiedTime: Long = 0,

    @SerializedName("size")
    val size: Long = 0,

    @SerializedName("encrypted")
    val encrypted: Boolean = false,

    @SerializedName("permission")
    val permission: String = "r",

    @SerializedName("version")
    val version: Int = 0,

    @SerializedName("file_count")
    val fileCount: Int = 0,

    @SerializedName("root")
    val root: String? = null
)

/**
 * Directory entry (file or folder)
 */
data class SeafileDirectoryEntry(
    @SerializedName("id")
    val id: String,

    @SerializedName("type")
    val type: String, // "file" or "dir"

    @SerializedName("name")
    val name: String,

    @SerializedName("size")
    val size: Long = 0,

    @SerializedName("mtime")
    val modifiedTime: Long = 0,

    @SerializedName("permission")
    val permission: String = "r",

    @SerializedName("modifier_email")
    val modifierEmail: String? = null,

    @SerializedName("modifier_name")
    val modifierName: String? = null,

    @SerializedName("modifier_contact_email")
    val modifierContactEmail: String? = null
) {
    val isDirectory: Boolean
        get() = type == "dir"

    val isFile: Boolean
        get() = type == "file"
}

/**
 * Directory listing response
 */
data class SeafileDirectoryListing(
    @SerializedName("dirent_list")
    val entries: List<SeafileDirectoryEntry> = emptyList()
)

/**
 * File detail information
 */
data class SeafileFile(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("size")
    val size: Long,

    @SerializedName("type")
    val type: String = "file",

    @SerializedName("mtime")
    val modifiedTime: Long = 0,

    @SerializedName("permission")
    val permission: String = "r"
)

/**
 * Upload link response
 */
data class SeafileUploadLink(
    @SerializedName("upload_link")
    val uploadLink: String? = null,

    // Alternative field name used by some API endpoints
    @SerializedName("url")
    val url: String? = null
) {
    val link: String
        get() = uploadLink ?: url ?: ""
}

/**
 * Download link response
 */
data class SeafileDownloadLink(
    @SerializedName("download_link")
    val downloadLink: String? = null,

    // Direct URL response for some endpoints
    @SerializedName("url")
    val url: String? = null
) {
    val link: String
        get() = downloadLink ?: url ?: ""
}

/**
 * Search result item
 */
data class SeafileSearchResult(
    @SerializedName("name")
    val name: String,

    @SerializedName("repo_id")
    val repoId: String,

    @SerializedName("repo_name")
    val repoName: String,

    @SerializedName("is_dir")
    val isDirectory: Boolean = false,

    @SerializedName("size")
    val size: Long = 0,

    @SerializedName("mtime")
    val modifiedTime: Long = 0,

    @SerializedName("fullpath")
    val fullPath: String,

    @SerializedName("permission")
    val permission: String = "r"
)

/**
 * Search response wrapper
 */
data class SeafileSearchResponse(
    @SerializedName("total")
    val total: Int = 0,

    @SerializedName("results")
    val results: List<SeafileSearchResult> = emptyList(),

    @SerializedName("has_more")
    val hasMore: Boolean = false
)

/**
 * Share link information
 */
data class SeafileShareLink(
    @SerializedName("token")
    val token: String,

    @SerializedName("link")
    val link: String,

    @SerializedName("repo_id")
    val repoId: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("ctime")
    val createTime: Long = 0,

    @SerializedName("expire_date")
    val expireDate: String? = null,

    @SerializedName("view_cnt")
    val viewCount: Int = 0,

    @SerializedName("is_expired")
    val isExpired: Boolean = false,

    @SerializedName("is_dir")
    val isDirectory: Boolean = false,

    @SerializedName("permissions")
    val permissions: SharePermissions? = null
)

/**
 * Share link permissions
 */
data class SharePermissions(
    @SerializedName("can_edit")
    val canEdit: Boolean = false,

    @SerializedName("can_download")
    val canDownload: Boolean = true
)

/**
 * File history/version information
 */
data class SeafileFileHistory(
    @SerializedName("commit_id")
    val commitId: String,

    @SerializedName("rev_file_id")
    val revFileId: String,

    @SerializedName("ctime")
    val createTime: Long,

    @SerializedName("creator_name")
    val creatorName: String,

    @SerializedName("creator")
    val creator: String,

    @SerializedName("size")
    val size: Long,

    @SerializedName("rev_renamed_old_path")
    val renamedOldPath: String? = null
)

/**
 * Encrypted library password verification response
 */
data class SeafileLibraryPasswordResponse(
    @SerializedName("success")
    val success: Boolean = false,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Generic Seafile error response
 */
data class SeafileErrorResponse(
    @SerializedName("error_msg")
    val errorMessage: String? = null,

    @SerializedName("detail")
    val detail: String? = null
) {
    val message: String
        get() = errorMessage ?: detail ?: "Unknown error"
}

/**
 * Thumbnail information
 */
data class SeafileThumbnail(
    @SerializedName("thumbnail_url")
    val thumbnailUrl: String,

    @SerializedName("size")
    val size: Int = 48
)

/**
 * Directory creation request
 */
data class CreateDirectoryRequest(
    @SerializedName("operation")
    val operation: String = "mkdir"
)

/**
 * Move/Copy operation request
 */
data class MoveOperationRequest(
    @SerializedName("operation")
    val operation: String, // "move" or "copy"

    @SerializedName("dst_repo")
    val dstRepo: String,

    @SerializedName("dst_dir")
    val dstDir: String
)
