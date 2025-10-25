package com.shareconnect.seafileconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Authentication response
 */
data class AuthToken(
    @SerializedName("token")
    val token: String
)

/**
 * Account information
 */
data class AccountInfo(
    @SerializedName("email")
    val email: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("usage")
    val usage: Long,
    @SerializedName("total")
    val total: Long,
    @SerializedName("institution")
    val institution: String? = null,
    @SerializedName("avatar_url")
    val avatarUrl: String? = null
)

/**
 * Library (repository) representation
 */
data class Library(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("desc")
    val description: String?,
    @SerializedName("owner")
    val owner: String,
    @SerializedName("encrypted")
    val encrypted: Boolean = false,
    @SerializedName("size")
    val size: Long = 0,
    @SerializedName("mtime")
    val modifiedTime: Long = 0,
    @SerializedName("permission")
    val permission: String = "rw",
    @SerializedName("type")
    val type: String = "repo"
)

/**
 * Directory entry (file or directory)
 */
data class DirectoryEntry(
    @SerializedName("id")
    val id: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("type")
    val type: String, // "file" or "dir"
    @SerializedName("size")
    val size: Long = 0,
    @SerializedName("mtime")
    val modifiedTime: Long = 0,
    @SerializedName("permission")
    val permission: String? = null
) {
    val isDirectory: Boolean
        get() = type == "dir"
    
    val isFile: Boolean
        get() = type == "file"
}

/**
 * Directory listing response
 */
data class DirectoryListing(
    @SerializedName("dirent_list")
    val entries: List<DirectoryEntry>
)

/**
 * Upload link response
 */
data class UploadLink(
    @SerializedName("upload_link")
    val uploadLink: String
)

/**
 * Download link response
 */
data class DownloadLink(
    @SerializedName("download_link")
    val downloadLink: String
)

/**
 * File operation result
 */
data class FileOperationResult(
    @SerializedName("success")
    val success: Boolean = true,
    @SerializedName("task_id")
    val taskId: String? = null
)

/**
 * Search result item
 */
data class SearchResult(
    @SerializedName("name")
    val name: String,
    @SerializedName("oid")
    val oid: String,
    @SerializedName("repo_id")
    val repoId: String,
    @SerializedName("repo_name")
    val repoName: String,
    @SerializedName("is_dir")
    val isDir: Boolean = false,
    @SerializedName("size")
    val size: Long = 0,
    @SerializedName("mtime")
    val modifiedTime: Long = 0,
    @SerializedName("fullpath")
    val fullPath: String
)

/**
 * Search response
 */
data class SearchResponse(
    @SerializedName("total")
    val total: Int,
    @SerializedName("results")
    val results: List<SearchResult>
)

/**
 * Library decryption request
 */
data class DecryptLibraryRequest(
    @SerializedName("password")
    val password: String
)

/**
 * Library decryption result
 */
data class DecryptLibraryResult(
    @SerializedName("success")
    val success: Boolean = true
)

/**
 * Error response from Seafile API
 */
data class SeafileErrorResponse(
    @SerializedName("error_msg")
    val errorMessage: String? = null,
    @SerializedName("error")
    val error: String? = null
) {
    fun getMessage(): String = errorMessage ?: error ?: "Unknown error"
}

/**
 * File share link
 */
data class ShareLink(
    @SerializedName("link")
    val link: String,
    @SerializedName("token")
    val token: String,
    @SerializedName("ctime")
    val createTime: Long
)
