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


package com.shareconnect.paperlessngconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Paperless-ngx Document model
 * Represents a document in the Paperless-ngx system
 */
data class PaperlessDocument(
    @SerializedName("id")
    val id: Int,

    @SerializedName("correspondent")
    val correspondent: Int? = null,

    @SerializedName("document_type")
    val documentType: Int? = null,

    @SerializedName("storage_path")
    val storagePath: Int? = null,

    @SerializedName("title")
    val title: String,

    @SerializedName("content")
    val content: String? = null,

    @SerializedName("tags")
    val tags: List<Int> = emptyList(),

    @SerializedName("created")
    val created: String,

    @SerializedName("created_date")
    val createdDate: String? = null,

    @SerializedName("modified")
    val modified: String,

    @SerializedName("added")
    val added: String,

    @SerializedName("archive_serial_number")
    val archiveSerialNumber: String? = null,

    @SerializedName("original_file_name")
    val originalFileName: String,

    @SerializedName("archived_file_name")
    val archivedFileName: String? = null,

    @SerializedName("owner")
    val owner: Int? = null,

    @SerializedName("user_can_change")
    val userCanChange: Boolean = true,

    @SerializedName("is_shared_by_requester")
    val isSharedByRequester: Boolean = false,

    @SerializedName("notes")
    val notes: List<PaperlessNote> = emptyList(),

    @SerializedName("custom_fields")
    val customFields: List<PaperlessCustomField> = emptyList(),

    // Metadata
    @SerializedName("page_count")
    val pageCount: Int? = null,

    @SerializedName("file_type")
    val fileType: String? = null,

    @SerializedName("checksum")
    val checksum: String? = null,

    @SerializedName("__search_hit__")
    val searchHit: PaperlessSearchHit? = null
)

/**
 * Paperless-ngx Tag model
 */
data class PaperlessTag(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("text_color")
    val textColor: String? = null,

    @SerializedName("is_inbox_tag")
    val isInboxTag: Boolean = false,

    @SerializedName("match")
    val match: String? = null,

    @SerializedName("matching_algorithm")
    val matchingAlgorithm: Int? = null,

    @SerializedName("is_insensitive")
    val isInsensitive: Boolean = true,

    @SerializedName("owner")
    val owner: Int? = null,

    @SerializedName("user_can_change")
    val userCanChange: Boolean = true
)

/**
 * Paperless-ngx Correspondent model
 */
data class PaperlessCorrespondent(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("match")
    val match: String? = null,

    @SerializedName("matching_algorithm")
    val matchingAlgorithm: Int? = null,

    @SerializedName("is_insensitive")
    val isInsensitive: Boolean = true,

    @SerializedName("owner")
    val owner: Int? = null,

    @SerializedName("user_can_change")
    val userCanChange: Boolean = true
)

/**
 * Paperless-ngx Document Type model
 */
data class PaperlessDocumentType(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("match")
    val match: String? = null,

    @SerializedName("matching_algorithm")
    val matchingAlgorithm: Int? = null,

    @SerializedName("is_insensitive")
    val isInsensitive: Boolean = true,

    @SerializedName("owner")
    val owner: Int? = null,

    @SerializedName("user_can_change")
    val userCanChange: Boolean = true
)

/**
 * Paperless-ngx Storage Path model
 */
data class PaperlessStoragePath(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("slug")
    val slug: String,

    @SerializedName("path")
    val path: String,

    @SerializedName("match")
    val match: String? = null,

    @SerializedName("matching_algorithm")
    val matchingAlgorithm: Int? = null,

    @SerializedName("is_insensitive")
    val isInsensitive: Boolean = true,

    @SerializedName("owner")
    val owner: Int? = null,

    @SerializedName("user_can_change")
    val userCanChange: Boolean = true
)

/**
 * Paperless-ngx Note model
 */
data class PaperlessNote(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("note")
    val note: String,

    @SerializedName("created")
    val created: String? = null,

    @SerializedName("document")
    val document: Int? = null,

    @SerializedName("user")
    val user: PaperlessUser? = null
)

/**
 * Paperless-ngx Custom Field model
 */
data class PaperlessCustomField(
    @SerializedName("id")
    val id: Int,

    @SerializedName("field")
    val field: Int,

    @SerializedName("value")
    val value: String
)

/**
 * Paperless-ngx User model
 */
data class PaperlessUser(
    @SerializedName("id")
    val id: Int,

    @SerializedName("username")
    val username: String,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("first_name")
    val firstName: String? = null,

    @SerializedName("last_name")
    val lastName: String? = null
)

/**
 * Paperless-ngx Search Hit metadata
 */
data class PaperlessSearchHit(
    @SerializedName("score")
    val score: Double? = null,

    @SerializedName("highlights")
    val highlights: String? = null,

    @SerializedName("note_highlights")
    val noteHighlights: String? = null,

    @SerializedName("rank")
    val rank: Int? = null
)

/**
 * Paginated response for documents
 */
data class PaperlessDocumentListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("all")
    val all: List<Int> = emptyList(),

    @SerializedName("results")
    val results: List<PaperlessDocument>
)

/**
 * Paginated response for tags
 */
data class PaperlessTagListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<PaperlessTag>
)

/**
 * Paginated response for correspondents
 */
data class PaperlessCorrespondentListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<PaperlessCorrespondent>
)

/**
 * Paginated response for document types
 */
data class PaperlessDocumentTypeListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<PaperlessDocumentType>
)

/**
 * Paginated response for storage paths
 */
data class PaperlessStoragePathListResponse(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<PaperlessStoragePath>
)

/**
 * Search result response
 */
data class PaperlessSearchResult(
    @SerializedName("count")
    val count: Int,

    @SerializedName("next")
    val next: String? = null,

    @SerializedName("previous")
    val previous: String? = null,

    @SerializedName("results")
    val results: List<PaperlessDocument>
)

/**
 * Document creation/update request
 */
data class PaperlessDocumentRequest(
    @SerializedName("title")
    val title: String,

    @SerializedName("correspondent")
    val correspondent: Int? = null,

    @SerializedName("document_type")
    val documentType: Int? = null,

    @SerializedName("storage_path")
    val storagePath: Int? = null,

    @SerializedName("tags")
    val tags: List<Int> = emptyList(),

    @SerializedName("archive_serial_number")
    val archiveSerialNumber: String? = null,

    @SerializedName("created_date")
    val createdDate: String? = null
)

/**
 * Document metadata update request
 */
data class PaperlessDocumentUpdateRequest(
    @SerializedName("title")
    val title: String? = null,

    @SerializedName("correspondent")
    val correspondent: Int? = null,

    @SerializedName("document_type")
    val documentType: Int? = null,

    @SerializedName("storage_path")
    val storagePath: Int? = null,

    @SerializedName("tags")
    val tags: List<Int>? = null,

    @SerializedName("archive_serial_number")
    val archiveSerialNumber: String? = null,

    @SerializedName("created_date")
    val createdDate: String? = null
)

/**
 * Authentication token response
 */
data class PaperlessTokenResponse(
    @SerializedName("token")
    val token: String
)

/**
 * Authentication request
 */
data class PaperlessAuthRequest(
    @SerializedName("username")
    val username: String,

    @SerializedName("password")
    val password: String
)

/**
 * Upload task response
 */
data class PaperlessUploadResponse(
    @SerializedName("task_id")
    val taskId: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null
)

/**
 * Task status response
 */
data class PaperlessTaskStatus(
    @SerializedName("task_id")
    val taskId: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("result")
    val result: String? = null,

    @SerializedName("date_created")
    val dateCreated: String? = null,

    @SerializedName("date_done")
    val dateDone: String? = null
)

/**
 * Bulk edit request
 */
data class PaperlessBulkEditRequest(
    @SerializedName("documents")
    val documents: List<Int>,

    @SerializedName("method")
    val method: String, // "set_correspondent", "set_document_type", "set_storage_path", "add_tag", "remove_tag", "delete"

    @SerializedName("parameters")
    val parameters: Map<String, Any>? = null
)

/**
 * Saved view model
 */
data class PaperlessSavedView(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("show_on_dashboard")
    val showOnDashboard: Boolean = false,

    @SerializedName("show_in_sidebar")
    val showInSidebar: Boolean = false,

    @SerializedName("sort_field")
    val sortField: String? = null,

    @SerializedName("sort_reverse")
    val sortReverse: Boolean = false,

    @SerializedName("filter_rules")
    val filterRules: List<PaperlessFilterRule> = emptyList()
)

/**
 * Filter rule for saved views
 */
data class PaperlessFilterRule(
    @SerializedName("rule_type")
    val ruleType: Int,

    @SerializedName("value")
    val value: String? = null
)

/**
 * Workflow model
 */
data class PaperlessWorkflow(
    @SerializedName("id")
    val id: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("order")
    val order: Int,

    @SerializedName("enabled")
    val enabled: Boolean = true,

    @SerializedName("triggers")
    val triggers: List<PaperlessWorkflowTrigger> = emptyList(),

    @SerializedName("actions")
    val actions: List<PaperlessWorkflowAction> = emptyList()
)

/**
 * Workflow trigger
 */
data class PaperlessWorkflowTrigger(
    @SerializedName("type")
    val type: String,

    @SerializedName("sources")
    val sources: List<String>? = null,

    @SerializedName("filter_path")
    val filterPath: String? = null,

    @SerializedName("filter_filename")
    val filterFilename: String? = null,

    @SerializedName("filter_mailrule")
    val filterMailrule: Int? = null,

    @SerializedName("matching_algorithm")
    val matchingAlgorithm: Int? = null,

    @SerializedName("match")
    val match: String? = null,

    @SerializedName("is_insensitive")
    val isInsensitive: Boolean = true
)

/**
 * Workflow action
 */
data class PaperlessWorkflowAction(
    @SerializedName("type")
    val type: String,

    @SerializedName("assign_title")
    val assignTitle: String? = null,

    @SerializedName("assign_tags")
    val assignTags: List<Int>? = null,

    @SerializedName("assign_document_type")
    val assignDocumentType: Int? = null,

    @SerializedName("assign_correspondent")
    val assignCorrespondent: Int? = null,

    @SerializedName("assign_storage_path")
    val assignStoragePath: Int? = null,

    @SerializedName("assign_owner")
    val assignOwner: Int? = null,

    @SerializedName("assign_view_users")
    val assignViewUsers: List<Int>? = null,

    @SerializedName("assign_view_groups")
    val assignViewGroups: List<Int>? = null,

    @SerializedName("assign_change_users")
    val assignChangeUsers: List<Int>? = null,

    @SerializedName("assign_change_groups")
    val assignChangeGroups: List<Int>? = null,

    @SerializedName("assign_custom_field_1")
    val assignCustomField1: String? = null,

    @SerializedName("assign_custom_field_2")
    val assignCustomField2: String? = null
)
