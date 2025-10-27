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


package com.shareconnect.duplicaticonnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Duplicati Server State
 * Represents the current state and configuration of the Duplicati server
 */
data class DuplicatiServerState(
    @SerializedName("ProgramState")
    val programState: String,

    @SerializedName("LastEventID")
    val lastEventId: Long,

    @SerializedName("LastDataUpdateID")
    val lastDataUpdateId: Long,

    @SerializedName("ActiveTask")
    val activeTask: ActiveTask?,

    @SerializedName("PausedUntil")
    val pausedUntil: String?,

    @SerializedName("SuggestedStatusIcon")
    val suggestedStatusIcon: String,

    @SerializedName("EstimatedPauseEnd")
    val estimatedPauseEnd: String?,

    @SerializedName("UpdatedVersion")
    val updatedVersion: String?,

    @SerializedName("UpdateDownloadProgress")
    val updateDownloadProgress: Double?,

    @SerializedName("HasWarning")
    val hasWarning: Boolean,

    @SerializedName("HasError")
    val hasError: Boolean
)

/**
 * Active Task
 * Currently running backup or restore operation
 */
data class ActiveTask(
    @SerializedName("Item1")
    val taskId: Long,

    @SerializedName("Item2")
    val taskType: String
)

/**
 * Duplicati Backup Configuration
 * Complete backup job configuration including schedule and filters
 */
data class DuplicatiBackup(
    @SerializedName("ID")
    val id: String,

    @SerializedName("Name")
    val name: String,

    @SerializedName("Description")
    val description: String?,

    @SerializedName("Tags")
    val tags: List<String>?,

    @SerializedName("TargetURL")
    val targetUrl: String,

    @SerializedName("DBPath")
    val dbPath: String?,

    @SerializedName("Sources")
    val sources: List<String>,

    @SerializedName("Settings")
    val settings: List<BackupSetting>?,

    @SerializedName("Filters")
    val filters: List<BackupFilter>?,

    @SerializedName("Schedule")
    val schedule: BackupSchedule?,

    @SerializedName("Metadata")
    val metadata: BackupMetadata?,

    @SerializedName("IsTemporary")
    val isTemporary: Boolean?
)

/**
 * Backup Setting
 * Individual configuration setting for a backup job
 */
data class BackupSetting(
    @SerializedName("Name")
    val name: String,

    @SerializedName("Value")
    val value: String,

    @SerializedName("Filter")
    val filter: String?
)

/**
 * Backup Filter
 * Include/exclude filters for backup sources
 */
data class BackupFilter(
    @SerializedName("Order")
    val order: Int,

    @SerializedName("Include")
    val include: Boolean,

    @SerializedName("Expression")
    val expression: String
)

/**
 * Backup Schedule
 * Scheduling configuration for automated backups
 */
data class BackupSchedule(
    @SerializedName("ID")
    val id: Long,

    @SerializedName("Tags")
    val tags: List<String>?,

    @SerializedName("Time")
    val time: String,

    @SerializedName("Repeat")
    val repeat: String,

    @SerializedName("LastRun")
    val lastRun: String?,

    @SerializedName("Rule")
    val rule: String,

    @SerializedName("AllowedDays")
    val allowedDays: List<String>?
)

/**
 * Backup Metadata
 * Additional metadata about backup job execution
 */
data class BackupMetadata(
    @SerializedName("LastBackupDate")
    val lastBackupDate: String?,

    @SerializedName("LastBackupStarted")
    val lastBackupStarted: String?,

    @SerializedName("LastBackupFinished")
    val lastBackupFinished: String?,

    @SerializedName("LastBackupDuration")
    val lastBackupDuration: String?,

    @SerializedName("LastBackupFileCount")
    val lastBackupFileCount: Long?,

    @SerializedName("LastBackupFileSize")
    val lastBackupFileSize: Long?,

    @SerializedName("LastBackupAddedFiles")
    val lastBackupAddedFiles: Long?,

    @SerializedName("LastBackupModifiedFiles")
    val lastBackupModifiedFiles: Long?,

    @SerializedName("LastBackupDeletedFiles")
    val lastBackupDeletedFiles: Long?,

    @SerializedName("LastBackupVerificationFileCount")
    val lastBackupVerificationFileCount: Long?,

    @SerializedName("SourceFilesCount")
    val sourceFilesCount: Long?,

    @SerializedName("SourceFilesSize")
    val sourceFilesSize: Long?
)

/**
 * Duplicati Backup Run Result
 * Result of executing a backup operation
 */
data class DuplicatiBackupRun(
    @SerializedName("ID")
    val id: Long,

    @SerializedName("Status")
    val status: String,

    @SerializedName("Message")
    val message: String?
)

/**
 * Duplicati Restore Options
 * Configuration for file/folder restore operations
 */
data class DuplicatiRestoreOptions(
    @SerializedName("time")
    val time: String? = null,

    @SerializedName("restore-path")
    val restorePath: String? = null,

    @SerializedName("overwrite")
    val overwrite: Boolean = false,

    @SerializedName("permissions")
    val permissions: Boolean = true,

    @SerializedName("skip-metadata")
    val skipMetadata: Boolean = false,

    val paths: List<String> = emptyList()
)

/**
 * Duplicati Log Entry
 * Individual log message from backup operations
 */
data class DuplicatiLogEntry(
    @SerializedName("ID")
    val id: Long,

    @SerializedName("BackupID")
    val backupId: String?,

    @SerializedName("Type")
    val type: String,

    @SerializedName("Timestamp")
    val timestamp: String,

    @SerializedName("Message")
    val message: String,

    @SerializedName("Exception")
    val exception: String?
)

/**
 * Duplicati Progress Information
 * Real-time progress data for backup/restore operations
 */
data class DuplicatiProgress(
    @SerializedName("Phase")
    val phase: String,

    @SerializedName("Overall")
    val overall: ProgressMetrics,

    @SerializedName("Current")
    val current: ProgressMetrics,

    @SerializedName("ProcessedFileCount")
    val processedFileCount: Long,

    @SerializedName("ProcessedFileSize")
    val processedFileSize: Long,

    @SerializedName("TotalFileCount")
    val totalFileCount: Long,

    @SerializedName("TotalFileSize")
    val totalFileSize: Long,

    @SerializedName("StillCounting")
    val stillCounting: Boolean
)

/**
 * Progress Metrics
 * Detailed metrics for progress tracking
 */
data class ProgressMetrics(
    @SerializedName("Speed")
    val speed: Double,

    @SerializedName("Count")
    val count: Long,

    @SerializedName("Size")
    val size: Long
)

/**
 * Duplicati Notification
 * System notification or alert message
 */
data class DuplicatiNotification(
    @SerializedName("ID")
    val id: Long,

    @SerializedName("Type")
    val type: String,

    @SerializedName("Title")
    val title: String,

    @SerializedName("Message")
    val message: String,

    @SerializedName("Exception")
    val exception: String?,

    @SerializedName("BackupID")
    val backupId: String?,

    @SerializedName("Action")
    val action: String?,

    @SerializedName("Timestamp")
    val timestamp: String
)

/**
 * Duplicati File System Entry
 * File or directory entry for filesystem browsing
 */
data class DuplicatiFileSystemEntry(
    @SerializedName("Path")
    val path: String,

    @SerializedName("IconCls")
    val iconClass: String,

    @SerializedName("IsFolder")
    val isFolder: Boolean,

    @SerializedName("Resolves")
    val resolves: Boolean?,

    @SerializedName("Hidden")
    val hidden: Boolean?
)

/**
 * Duplicati Login Request
 * Authentication request payload
 */
data class DuplicatiLoginRequest(
    @SerializedName("Password")
    val password: String
)

/**
 * Duplicati Login Response
 * Authentication response with status
 */
data class DuplicatiLoginResponse(
    @SerializedName("Status")
    val status: String,

    @SerializedName("Salt")
    val salt: String?,

    @SerializedName("Nonce")
    val nonce: String?
)

/**
 * Backup Creation Request
 * Payload for creating a new backup job
 */
data class BackupCreationRequest(
    @SerializedName("Backup")
    val backup: DuplicatiBackup
)

/**
 * Backup Creation Response
 * Response after creating a backup job
 */
data class BackupCreationResponse(
    @SerializedName("ID")
    val id: String,

    @SerializedName("Status")
    val status: String
)

/**
 * Duplicati System Info
 * System and version information
 */
data class DuplicatiSystemInfo(
    @SerializedName("APIVersion")
    val apiVersion: Int,

    @SerializedName("ServerVersion")
    val serverVersion: String,

    @SerializedName("ServerVersionName")
    val serverVersionName: String,

    @SerializedName("OSType")
    val osType: String,

    @SerializedName("OSVersion")
    val osVersion: String,

    @SerializedName("MachineName")
    val machineName: String,

    @SerializedName("UserName")
    val userName: String,

    @SerializedName("NewLine")
    val newLine: String,

    @SerializedName("DirectorySeparator")
    val directorySeparator: String
)
