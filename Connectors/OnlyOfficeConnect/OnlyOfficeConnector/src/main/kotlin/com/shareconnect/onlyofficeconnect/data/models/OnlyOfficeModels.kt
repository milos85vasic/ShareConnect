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


package com.shareconnect.onlyofficeconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * ONLYOFFICE Authentication Models
 */
data class OnlyOfficeAuthRequest(
    @SerializedName("userName") val userName: String,
    @SerializedName("password") val password: String
)

data class OnlyOfficeAuthResponse(
    @SerializedName("token") val token: String,
    @SerializedName("expires") val expires: String? = null,
    @SerializedName("response") val response: OnlyOfficeAuthUser? = null
)

data class OnlyOfficeAuthUser(
    @SerializedName("id") val id: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("userName") val userName: String
)

/**
 * ONLYOFFICE File Models
 */
data class OnlyOfficeFile(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("folderId") val folderId: String,
    @SerializedName("version") val version: Int = 1,
    @SerializedName("versionGroup") val versionGroup: Int = 1,
    @SerializedName("contentLength") val contentLength: Long = 0,
    @SerializedName("pureContentLength") val pureContentLength: Long = 0,
    @SerializedName("fileStatus") val fileStatus: Int = 0,
    @SerializedName("viewUrl") val viewUrl: String? = null,
    @SerializedName("webUrl") val webUrl: String? = null,
    @SerializedName("fileType") val fileType: Int = 0,
    @SerializedName("fileExst") val fileExst: String? = null,
    @SerializedName("comment") val comment: String? = null,
    @SerializedName("encrypted") val encrypted: Boolean = false,
    @SerializedName("thumbnailUrl") val thumbnailUrl: String? = null,
    @SerializedName("created") val created: String,
    @SerializedName("createdBy") val createdBy: OnlyOfficeUser? = null,
    @SerializedName("updated") val updated: String,
    @SerializedName("updatedBy") val updatedBy: OnlyOfficeUser? = null,
    @SerializedName("rootFolderType") val rootFolderType: Int = 0,
    @SerializedName("providerKey") val providerKey: String? = null,
    @SerializedName("canShare") val canShare: Boolean = false,
    @SerializedName("canEdit") val canEdit: Boolean = false
)

data class OnlyOfficeFilesResponse(
    @SerializedName("response") val response: OnlyOfficeFilesData,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("statusCode") val statusCode: Int = 200
)

data class OnlyOfficeFilesData(
    @SerializedName("files") val files: List<OnlyOfficeFile> = emptyList(),
    @SerializedName("folders") val folders: List<OnlyOfficeFolder> = emptyList(),
    @SerializedName("current") val current: OnlyOfficeFolder? = null,
    @SerializedName("pathParts") val pathParts: List<OnlyOfficeFolder> = emptyList(),
    @SerializedName("total") val total: Int = 0,
    @SerializedName("count") val count: Int = 0
)

/**
 * ONLYOFFICE Folder Models
 */
data class OnlyOfficeFolder(
    @SerializedName("id") val id: String,
    @SerializedName("title") val title: String,
    @SerializedName("parentId") val parentId: String? = null,
    @SerializedName("filesCount") val filesCount: Int = 0,
    @SerializedName("foldersCount") val foldersCount: Int = 0,
    @SerializedName("new") val new: Int = 0,
    @SerializedName("rootFolderType") val rootFolderType: Int = 0,
    @SerializedName("created") val created: String,
    @SerializedName("createdBy") val createdBy: OnlyOfficeUser? = null,
    @SerializedName("updated") val updated: String,
    @SerializedName("updatedBy") val updatedBy: OnlyOfficeUser? = null,
    @SerializedName("providerKey") val providerKey: String? = null,
    @SerializedName("canShare") val canShare: Boolean = false,
    @SerializedName("canEdit") val canEdit: Boolean = false
)

data class OnlyOfficeFolderResponse(
    @SerializedName("response") val response: OnlyOfficeFolder,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("statusCode") val statusCode: Int = 200
)

/**
 * ONLYOFFICE User Models
 */
data class OnlyOfficeUser(
    @SerializedName("id") val id: String,
    @SerializedName("displayName") val displayName: String,
    @SerializedName("email") val email: String? = null,
    @SerializedName("userName") val userName: String? = null,
    @SerializedName("isOwner") val isOwner: Boolean = false,
    @SerializedName("isAdmin") val isAdmin: Boolean = false,
    @SerializedName("isVisitor") val isVisitor: Boolean = false,
    @SerializedName("avatarSmall") val avatarSmall: String? = null,
    @SerializedName("profileUrl") val profileUrl: String? = null
)

/**
 * ONLYOFFICE Capabilities Models
 */
data class OnlyOfficeCapabilities(
    @SerializedName("ldapEnabled") val ldapEnabled: Boolean = false,
    @SerializedName("ssoUrl") val ssoUrl: String? = null,
    @SerializedName("ssoLabel") val ssoLabel: String? = null,
    @SerializedName("providers") val providers: List<String> = emptyList(),
    @SerializedName("recaptchaPublicKey") val recaptchaPublicKey: String? = null
)

data class OnlyOfficeCapabilitiesResponse(
    @SerializedName("response") val response: OnlyOfficeCapabilities,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("statusCode") val statusCode: Int = 200
)

/**
 * ONLYOFFICE Document Editor Models
 */
data class OnlyOfficeDocument(
    @SerializedName("fileType") val fileType: String,
    @SerializedName("key") val key: String,
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("permissions") val permissions: OnlyOfficePermissions? = null,
    @SerializedName("info") val info: OnlyOfficeDocumentInfo? = null
)

data class OnlyOfficePermissions(
    @SerializedName("comment") val comment: Boolean = true,
    @SerializedName("copy") val copy: Boolean = true,
    @SerializedName("download") val download: Boolean = true,
    @SerializedName("edit") val edit: Boolean = true,
    @SerializedName("fillForms") val fillForms: Boolean = true,
    @SerializedName("modifyFilter") val modifyFilter: Boolean = true,
    @SerializedName("modifyContentControl") val modifyContentControl: Boolean = true,
    @SerializedName("print") val print: Boolean = true,
    @SerializedName("review") val review: Boolean = true,
    @SerializedName("chat") val chat: Boolean = true,
    @SerializedName("commentGroups") val commentGroups: Map<String, List<String>>? = null
)

data class OnlyOfficeDocumentInfo(
    @SerializedName("owner") val owner: String? = null,
    @SerializedName("uploaded") val uploaded: String? = null,
    @SerializedName("favorite") val favorite: Boolean = false
)

data class OnlyOfficeEditorConfig(
    @SerializedName("callbackUrl") val callbackUrl: String? = null,
    @SerializedName("createUrl") val createUrl: String? = null,
    @SerializedName("lang") val lang: String = "en",
    @SerializedName("mode") val mode: String = "edit", // "edit" or "view"
    @SerializedName("user") val user: OnlyOfficeEditorUser? = null,
    @SerializedName("customization") val customization: OnlyOfficeCustomization? = null,
    @SerializedName("embedded") val embedded: OnlyOfficeEmbedded? = null,
    @SerializedName("plugins") val plugins: OnlyOfficePlugins? = null,
    @SerializedName("recent") val recent: List<OnlyOfficeRecentFile>? = null
)

data class OnlyOfficeEditorUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String,
    @SerializedName("group") val group: String? = null
)

data class OnlyOfficeCustomization(
    @SerializedName("anonymous") val anonymous: OnlyOfficeAnonymous? = null,
    @SerializedName("autosave") val autosave: Boolean = true,
    @SerializedName("comments") val comments: Boolean = true,
    @SerializedName("compactHeader") val compactHeader: Boolean = false,
    @SerializedName("compactToolbar") val compactToolbar: Boolean = false,
    @SerializedName("compatibleFeatures") val compatibleFeatures: Boolean = false,
    @SerializedName("forcesave") val forcesave: Boolean = false,
    @SerializedName("help") val help: Boolean = true,
    @SerializedName("hideRightMenu") val hideRightMenu: Boolean = false,
    @SerializedName("logo") val logo: OnlyOfficeLogo? = null,
    @SerializedName("mentionShare") val mentionShare: Boolean = true,
    @SerializedName("reviewDisplay") val reviewDisplay: String? = null,
    @SerializedName("showReviewChanges") val showReviewChanges: Boolean = false,
    @SerializedName("spellcheck") val spellcheck: Boolean = true,
    @SerializedName("toolbarNoTabs") val toolbarNoTabs: Boolean = false,
    @SerializedName("unit") val unit: String = "cm",
    @SerializedName("zoom") val zoom: Int = 100
)

data class OnlyOfficeAnonymous(
    @SerializedName("request") val request: Boolean = true,
    @SerializedName("label") val label: String? = null
)

data class OnlyOfficeLogo(
    @SerializedName("image") val image: String? = null,
    @SerializedName("imageEmbedded") val imageEmbedded: String? = null,
    @SerializedName("url") val url: String? = null
)

data class OnlyOfficeEmbedded(
    @SerializedName("embedUrl") val embedUrl: String? = null,
    @SerializedName("fullscreenUrl") val fullscreenUrl: String? = null,
    @SerializedName("saveUrl") val saveUrl: String? = null,
    @SerializedName("shareUrl") val shareUrl: String? = null,
    @SerializedName("toolbarDocked") val toolbarDocked: String = "top"
)

data class OnlyOfficePlugins(
    @SerializedName("autostart") val autostart: List<String>? = null,
    @SerializedName("pluginsData") val pluginsData: List<String>? = null
)

data class OnlyOfficeRecentFile(
    @SerializedName("title") val title: String,
    @SerializedName("url") val url: String,
    @SerializedName("folder") val folder: String? = null
)

/**
 * ONLYOFFICE Editor Configuration Container
 */
data class OnlyOfficeEditorConfiguration(
    @SerializedName("document") val document: OnlyOfficeDocument,
    @SerializedName("documentType") val documentType: String, // "word", "cell", "slide"
    @SerializedName("editorConfig") val editorConfig: OnlyOfficeEditorConfig,
    @SerializedName("height") val height: String = "100%",
    @SerializedName("width") val width: String = "100%",
    @SerializedName("token") val token: String? = null,
    @SerializedName("type") val type: String = "mobile" // "desktop", "mobile", "embedded"
)

/**
 * ONLYOFFICE File Operations Models
 */
data class OnlyOfficeUploadRequest(
    @SerializedName("createNewIfExist") val createNewIfExist: Boolean = false,
    @SerializedName("keepConvertStatus") val keepConvertStatus: Boolean = true
)

data class OnlyOfficeUploadResponse(
    @SerializedName("response") val response: OnlyOfficeFile,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("statusCode") val statusCode: Int = 201
)

data class OnlyOfficeMoveRequest(
    @SerializedName("destFolderId") val destFolderId: String,
    @SerializedName("conflictResolveType") val conflictResolveType: Int = 0, // 0=Skip, 1=Overwrite, 2=Duplicate
    @SerializedName("deleteAfter") val deleteAfter: Boolean = true
)

data class OnlyOfficeCopyRequest(
    @SerializedName("destFolderId") val destFolderId: String,
    @SerializedName("conflictResolveType") val conflictResolveType: Int = 0
)

data class OnlyOfficeOperationResponse(
    @SerializedName("response") val response: List<OnlyOfficeOperationProgress>,
    @SerializedName("status") val status: Int = 0,
    @SerializedName("statusCode") val statusCode: Int = 200
)

data class OnlyOfficeOperationProgress(
    @SerializedName("id") val id: String,
    @SerializedName("operation") val operation: Int, // 0=Copy, 1=Move, 2=Delete, 3=Download, etc.
    @SerializedName("progress") val progress: Int = 0,
    @SerializedName("error") val error: String? = null,
    @SerializedName("processed") val processed: Int = 0
)

/**
 * ONLYOFFICE Callback Models (for document save events)
 */
data class OnlyOfficeCallback(
    @SerializedName("key") val key: String,
    @SerializedName("status") val status: Int, // 0=NotFound, 1=Editing, 2=Ready, 3=SaveError, 4=Closed, 6=ForceSave, 7=CorruptedForce
    @SerializedName("url") val url: String? = null,
    @SerializedName("changesurl") val changesUrl: String? = null,
    @SerializedName("history") val history: OnlyOfficeHistory? = null,
    @SerializedName("users") val users: List<String>? = null,
    @SerializedName("actions") val actions: List<OnlyOfficeAction>? = null,
    @SerializedName("lastsave") val lastSave: String? = null,
    @SerializedName("notmodified") val notModified: Boolean = false
)

data class OnlyOfficeHistory(
    @SerializedName("serverVersion") val serverVersion: String? = null,
    @SerializedName("changes") val changes: List<OnlyOfficeHistoryChange>? = null
)

data class OnlyOfficeHistoryChange(
    @SerializedName("created") val created: String,
    @SerializedName("user") val user: OnlyOfficeHistoryUser
)

data class OnlyOfficeHistoryUser(
    @SerializedName("id") val id: String,
    @SerializedName("name") val name: String
)

data class OnlyOfficeAction(
    @SerializedName("type") val type: Int, // 0=User connected, 1=User disconnected
    @SerializedName("userid") val userId: String
)

data class OnlyOfficeCallbackResponse(
    @SerializedName("error") val error: Int = 0 // 0=No error
)

/**
 * ONLYOFFICE Error Models
 */
data class OnlyOfficeError(
    @SerializedName("error") val error: OnlyOfficeErrorDetail,
    @SerializedName("status") val status: Int = 1,
    @SerializedName("statusCode") val statusCode: Int
)

data class OnlyOfficeErrorDetail(
    @SerializedName("message") val message: String,
    @SerializedName("type") val type: String? = null
)
