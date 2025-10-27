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


package com.shareconnect.giteaconnect.data.model

import com.google.gson.annotations.SerializedName

/**
 * Gitea user model
 */
data class GiteaUser(
    @SerializedName("id")
    val id: Long,

    @SerializedName("login")
    val login: String,

    @SerializedName("full_name")
    val fullName: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("avatar_url")
    val avatarUrl: String?,

    @SerializedName("is_admin")
    val isAdmin: Boolean,

    @SerializedName("created")
    val created: String?,

    @SerializedName("restricted")
    val restricted: Boolean?
)

/**
 * Gitea repository model
 */
data class GiteaRepository(
    @SerializedName("id")
    val id: Long,

    @SerializedName("owner")
    val owner: GiteaUser,

    @SerializedName("name")
    val name: String,

    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("empty")
    val empty: Boolean,

    @SerializedName("private")
    val private: Boolean,

    @SerializedName("fork")
    val fork: Boolean,

    @SerializedName("template")
    val template: Boolean,

    @SerializedName("parent")
    val parent: GiteaRepository?,

    @SerializedName("mirror")
    val mirror: Boolean,

    @SerializedName("size")
    val size: Long,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("ssh_url")
    val sshUrl: String,

    @SerializedName("clone_url")
    val cloneUrl: String,

    @SerializedName("website")
    val website: String?,

    @SerializedName("stars_count")
    val starsCount: Int,

    @SerializedName("forks_count")
    val forksCount: Int,

    @SerializedName("watchers_count")
    val watchersCount: Int,

    @SerializedName("open_issues_count")
    val openIssuesCount: Int,

    @SerializedName("open_pr_counter")
    val openPrCounter: Int,

    @SerializedName("release_counter")
    val releaseCounter: Int,

    @SerializedName("default_branch")
    val defaultBranch: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String
)

/**
 * Gitea issue model
 */
data class GiteaIssue(
    @SerializedName("id")
    val id: Long,

    @SerializedName("url")
    val url: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("number")
    val number: Long,

    @SerializedName("user")
    val user: GiteaUser,

    @SerializedName("title")
    val title: String,

    @SerializedName("body")
    val body: String?,

    @SerializedName("state")
    val state: String, // open, closed

    @SerializedName("labels")
    val labels: List<GiteaLabel>?,

    @SerializedName("milestone")
    val milestone: GiteaMilestone?,

    @SerializedName("assignees")
    val assignees: List<GiteaUser>?,

    @SerializedName("comments")
    val comments: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String,

    @SerializedName("closed_at")
    val closedAt: String?,

    @SerializedName("pull_request")
    val pullRequest: PullRequestMeta?
) {
    data class PullRequestMeta(
        @SerializedName("merged")
        val merged: Boolean?,

        @SerializedName("merged_at")
        val mergedAt: String?
    )
}

/**
 * Gitea label model
 */
data class GiteaLabel(
    @SerializedName("id")
    val id: Long,

    @SerializedName("name")
    val name: String,

    @SerializedName("color")
    val color: String,

    @SerializedName("description")
    val description: String?
)

/**
 * Gitea milestone model
 */
data class GiteaMilestone(
    @SerializedName("id")
    val id: Long,

    @SerializedName("title")
    val title: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("state")
    val state: String,

    @SerializedName("open_issues")
    val openIssues: Int,

    @SerializedName("closed_issues")
    val closedIssues: Int,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("closed_at")
    val closedAt: String?,

    @SerializedName("due_on")
    val dueOn: String?
)

/**
 * Gitea release model
 */
data class GiteaRelease(
    @SerializedName("id")
    val id: Long,

    @SerializedName("tag_name")
    val tagName: String,

    @SerializedName("target_commitish")
    val targetCommitish: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("body")
    val body: String?,

    @SerializedName("url")
    val url: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("tarball_url")
    val tarballUrl: String,

    @SerializedName("zipball_url")
    val zipballUrl: String,

    @SerializedName("draft")
    val draft: Boolean,

    @SerializedName("prerelease")
    val prerelease: Boolean,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("published_at")
    val publishedAt: String?,

    @SerializedName("author")
    val author: GiteaUser,

    @SerializedName("assets")
    val assets: List<ReleaseAsset>?
) {
    data class ReleaseAsset(
        @SerializedName("id")
        val id: Long,

        @SerializedName("name")
        val name: String,

        @SerializedName("size")
        val size: Long,

        @SerializedName("download_count")
        val downloadCount: Int,

        @SerializedName("created_at")
        val createdAt: String,

        @SerializedName("browser_download_url")
        val browserDownloadUrl: String
    )
}

/**
 * Gitea commit model
 */
data class GiteaCommit(
    @SerializedName("url")
    val url: String,

    @SerializedName("sha")
    val sha: String,

    @SerializedName("html_url")
    val htmlUrl: String,

    @SerializedName("commit")
    val commit: CommitDetails,

    @SerializedName("author")
    val author: GiteaUser?,

    @SerializedName("committer")
    val committer: GiteaUser?
) {
    data class CommitDetails(
        @SerializedName("message")
        val message: String,

        @SerializedName("author")
        val author: GitSignature,

        @SerializedName("committer")
        val committer: GitSignature
    )

    data class GitSignature(
        @SerializedName("name")
        val name: String,

        @SerializedName("email")
        val email: String,

        @SerializedName("date")
        val date: String
    )
}

/**
 * Gitea pull request model
 */
data class GiteaPullRequest(
    @SerializedName("id")
    val id: Long,

    @SerializedName("url")
    val url: String,

    @SerializedName("number")
    val number: Long,

    @SerializedName("user")
    val user: GiteaUser,

    @SerializedName("title")
    val title: String,

    @SerializedName("body")
    val body: String?,

    @SerializedName("state")
    val state: String,

    @SerializedName("merged")
    val merged: Boolean?,

    @SerializedName("mergeable")
    val mergeable: Boolean?,

    @SerializedName("merged_at")
    val mergedAt: String?,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("updated_at")
    val updatedAt: String?,

    @SerializedName("closed_at")
    val closedAt: String?,

    @SerializedName("head")
    val head: PRBranch,

    @SerializedName("base")
    val base: PRBranch
) {
    data class PRBranch(
        @SerializedName("label")
        val label: String,

        @SerializedName("ref")
        val ref: String,

        @SerializedName("sha")
        val sha: String,

        @SerializedName("repo")
        val repo: GiteaRepository?
    )
}
