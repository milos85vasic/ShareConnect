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

import com.google.gson.Gson
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Unit tests for Gitea data models
 */
class GiteaModelsTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = Gson()
    }

    @Test
    fun `GiteaUser serialization and deserialization`() {
        val user = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = "Test User",
            email = "test@example.com",
            avatarUrl = "https://gitea.example.com/avatars/1",
            isAdmin = false,
            created = "2025-01-01T00:00:00Z",
            restricted = false
        )

        val json = gson.toJson(user)
        val deserialized = gson.fromJson(json, GiteaUser::class.java)

        assertEquals(user.id, deserialized.id)
        assertEquals(user.login, deserialized.login)
        assertEquals(user.fullName, deserialized.fullName)
        assertEquals(user.email, deserialized.email)
        assertEquals(user.isAdmin, deserialized.isAdmin)
    }

    @Test
    fun `GiteaUser admin user`() {
        val admin = GiteaUser(
            id = 1,
            login = "admin",
            fullName = "Administrator",
            email = "admin@example.com",
            avatarUrl = null,
            isAdmin = true,
            created = null,
            restricted = null
        )

        assertTrue(admin.isAdmin)
        assertEquals("admin", admin.login)
    }

    @Test
    fun `GiteaRepository serialization and deserialization`() {
        val owner = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val repo = GiteaRepository(
            id = 100,
            owner = owner,
            name = "test-repo",
            fullName = "testuser/test-repo",
            description = "A test repository",
            empty = false,
            private = false,
            fork = false,
            template = false,
            parent = null,
            mirror = false,
            size = 1024000,
            htmlUrl = "https://gitea.example.com/testuser/test-repo",
            sshUrl = "git@gitea.example.com:testuser/test-repo.git",
            cloneUrl = "https://gitea.example.com/testuser/test-repo.git",
            website = "https://example.com",
            starsCount = 10,
            forksCount = 2,
            watchersCount = 5,
            openIssuesCount = 3,
            openPrCounter = 1,
            releaseCounter = 5,
            defaultBranch = "main",
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-10T00:00:00Z"
        )

        val json = gson.toJson(repo)
        val deserialized = gson.fromJson(json, GiteaRepository::class.java)

        assertEquals(repo.id, deserialized.id)
        assertEquals(repo.name, deserialized.name)
        assertEquals(repo.fullName, deserialized.fullName)
        assertEquals(repo.starsCount, deserialized.starsCount)
        assertFalse(deserialized.private)
        assertFalse(deserialized.fork)
    }

    @Test
    fun `GiteaRepository fork with parent`() {
        val parentOwner = GiteaUser(
            id = 1,
            login = "original",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val parent = GiteaRepository(
            id = 99,
            owner = parentOwner,
            name = "original-repo",
            fullName = "original/original-repo",
            description = "Original repository",
            empty = false,
            private = false,
            fork = false,
            template = false,
            parent = null,
            mirror = false,
            size = 2048000,
            htmlUrl = "https://gitea.example.com/original/original-repo",
            sshUrl = "git@gitea.example.com:original/original-repo.git",
            cloneUrl = "https://gitea.example.com/original/original-repo.git",
            website = null,
            starsCount = 100,
            forksCount = 20,
            watchersCount = 50,
            openIssuesCount = 5,
            openPrCounter = 2,
            releaseCounter = 10,
            defaultBranch = "main",
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2025-01-10T00:00:00Z"
        )

        val forkOwner = GiteaUser(
            id = 2,
            login = "forker",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val fork = parent.copy(
            id = 100,
            owner = forkOwner,
            name = "original-repo",
            fullName = "forker/original-repo",
            fork = true,
            parent = parent,
            starsCount = 0,
            forksCount = 0
        )

        assertTrue(fork.fork)
        assertNotNull(fork.parent)
        assertEquals("original/original-repo", fork.parent?.fullName)
    }

    @Test
    fun `GiteaIssue open issue`() {
        val user = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val issue = GiteaIssue(
            id = 1,
            url = "https://gitea.example.com/api/v1/repos/testuser/test-repo/issues/1",
            htmlUrl = "https://gitea.example.com/testuser/test-repo/issues/1",
            number = 1,
            user = user,
            title = "Bug: Application crashes",
            body = "The application crashes when clicking the button.",
            state = "open",
            labels = null,
            milestone = null,
            assignees = null,
            comments = 5,
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-10T00:00:00Z",
            closedAt = null,
            pullRequest = null
        )

        assertEquals("open", issue.state)
        assertNull(issue.closedAt)
        assertNull(issue.pullRequest)
        assertEquals(5, issue.comments)
    }

    @Test
    fun `GiteaIssue with labels and milestone`() {
        val user = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val label = GiteaLabel(
            id = 1,
            name = "bug",
            color = "#ee0701",
            description = "Something isn't working"
        )

        val milestone = GiteaMilestone(
            id = 1,
            title = "v1.0",
            description = "First major release",
            state = "open",
            openIssues = 10,
            closedIssues = 5,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = "2025-01-10T00:00:00Z",
            closedAt = null,
            dueOn = "2025-12-31T00:00:00Z"
        )

        val issue = GiteaIssue(
            id = 1,
            url = "https://gitea.example.com/api/v1/repos/testuser/test-repo/issues/1",
            htmlUrl = "https://gitea.example.com/testuser/test-repo/issues/1",
            number = 1,
            user = user,
            title = "Feature request",
            body = "Add new feature",
            state = "open",
            labels = listOf(label),
            milestone = milestone,
            assignees = null,
            comments = 0,
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-10T00:00:00Z",
            closedAt = null,
            pullRequest = null
        )

        assertNotNull(issue.labels)
        assertEquals(1, issue.labels?.size)
        assertEquals("bug", issue.labels?.get(0)?.name)
        assertNotNull(issue.milestone)
        assertEquals("v1.0", issue.milestone?.title)
    }

    @Test
    fun `GiteaRelease with assets`() {
        val author = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val asset = GiteaRelease.ReleaseAsset(
            id = 1,
            name = "app-v1.0.0.zip",
            size = 10485760,
            downloadCount = 100,
            createdAt = "2025-01-01T00:00:00Z",
            browserDownloadUrl = "https://gitea.example.com/testuser/test-repo/releases/download/v1.0.0/app-v1.0.0.zip"
        )

        val release = GiteaRelease(
            id = 1,
            tagName = "v1.0.0",
            targetCommitish = "main",
            name = "Version 1.0.0",
            body = "First stable release",
            url = "https://gitea.example.com/api/v1/repos/testuser/test-repo/releases/1",
            htmlUrl = "https://gitea.example.com/testuser/test-repo/releases/tag/v1.0.0",
            tarballUrl = "https://gitea.example.com/testuser/test-repo/archive/v1.0.0.tar.gz",
            zipballUrl = "https://gitea.example.com/testuser/test-repo/archive/v1.0.0.zip",
            draft = false,
            prerelease = false,
            createdAt = "2025-01-01T00:00:00Z",
            publishedAt = "2025-01-01T12:00:00Z",
            author = author,
            assets = listOf(asset)
        )

        assertFalse(release.draft)
        assertFalse(release.prerelease)
        assertNotNull(release.assets)
        assertEquals(1, release.assets?.size)
        assertEquals("app-v1.0.0.zip", release.assets?.get(0)?.name)
        assertEquals(100, release.assets?.get(0)?.downloadCount)
    }

    @Test
    fun `GiteaCommit with details`() {
        val author = GiteaUser(
            id = 1,
            login = "testuser",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val signature = GiteaCommit.GitSignature(
            name = "Test User",
            email = "test@example.com",
            date = "2025-01-01T00:00:00Z"
        )

        val details = GiteaCommit.CommitDetails(
            message = "Fix critical bug",
            author = signature,
            committer = signature
        )

        val commit = GiteaCommit(
            url = "https://gitea.example.com/api/v1/repos/testuser/test-repo/git/commits/abc123",
            sha = "abc123def456",
            htmlUrl = "https://gitea.example.com/testuser/test-repo/commit/abc123def456",
            commit = details,
            author = author,
            committer = author
        )

        assertEquals("abc123def456", commit.sha)
        assertEquals("Fix critical bug", commit.commit.message)
        assertEquals("Test User", commit.commit.author.name)
        assertEquals("test@example.com", commit.commit.author.email)
    }

    @Test
    fun `GiteaPullRequest open PR`() {
        val user = GiteaUser(
            id = 1,
            login = "contributor",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val owner = GiteaUser(
            id = 2,
            login = "maintainer",
            fullName = null,
            email = null,
            avatarUrl = null,
            isAdmin = false,
            created = null,
            restricted = null
        )

        val repo = GiteaRepository(
            id = 100,
            owner = owner,
            name = "test-repo",
            fullName = "maintainer/test-repo",
            description = null,
            empty = false,
            private = false,
            fork = false,
            template = false,
            parent = null,
            mirror = false,
            size = 1000,
            htmlUrl = "https://gitea.example.com/maintainer/test-repo",
            sshUrl = "git@gitea.example.com:maintainer/test-repo.git",
            cloneUrl = "https://gitea.example.com/maintainer/test-repo.git",
            website = null,
            starsCount = 0,
            forksCount = 0,
            watchersCount = 0,
            openIssuesCount = 0,
            openPrCounter = 1,
            releaseCounter = 0,
            defaultBranch = "main",
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-01T00:00:00Z"
        )

        val headBranch = GiteaPullRequest.PRBranch(
            label = "contributor:feature-branch",
            ref = "feature-branch",
            sha = "abc123",
            repo = null
        )

        val baseBranch = GiteaPullRequest.PRBranch(
            label = "maintainer:main",
            ref = "main",
            sha = "def456",
            repo = repo
        )

        val pr = GiteaPullRequest(
            id = 1,
            url = "https://gitea.example.com/api/v1/repos/maintainer/test-repo/pulls/1",
            number = 1,
            user = user,
            title = "Add new feature",
            body = "This PR adds a new feature",
            state = "open",
            merged = false,
            mergeable = true,
            mergedAt = null,
            createdAt = "2025-01-01T00:00:00Z",
            updatedAt = "2025-01-05T00:00:00Z",
            closedAt = null,
            head = headBranch,
            base = baseBranch
        )

        assertEquals("open", pr.state)
        assertFalse(pr.merged ?: true)
        assertTrue(pr.mergeable ?: false)
        assertNull(pr.mergedAt)
        assertEquals("feature-branch", pr.head.ref)
        assertEquals("main", pr.base.ref)
    }

    @Test
    fun `Data class equality and hashCode`() {
        val label1 = GiteaLabel(
            id = 1,
            name = "bug",
            color = "#ee0701",
            description = "Something isn't working"
        )

        val label2 = GiteaLabel(
            id = 1,
            name = "bug",
            color = "#ee0701",
            description = "Something isn't working"
        )

        assertEquals(label1, label2)
        assertEquals(label1.hashCode(), label2.hashCode())
    }

    @Test
    fun `Data class copy functionality`() {
        val original = GiteaMilestone(
            id = 1,
            title = "v1.0",
            description = "First release",
            state = "open",
            openIssues = 10,
            closedIssues = 5,
            createdAt = "2024-01-01T00:00:00Z",
            updatedAt = null,
            closedAt = null,
            dueOn = "2025-12-31T00:00:00Z"
        )

        val modified = original.copy(
            state = "closed",
            closedIssues = 15,
            closedAt = "2025-01-10T00:00:00Z"
        )

        assertEquals("closed", modified.state)
        assertEquals(15, modified.closedIssues)
        assertNotNull(modified.closedAt)
        assertEquals(original.id, modified.id) // Unchanged fields
        assertEquals(original.title, modified.title)
    }
}
