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

package com.shareconnect.giteaconnect.data.api

import com.shareconnect.giteaconnect.data.model.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * Test data provider for Gitea API stub implementations.
 * Provides realistic sample data for all Gitea models to enable UI and integration testing
 * without requiring a live Gitea server.
 */
object GiteaTestData {

    // Server Configuration
    const val TEST_SERVER_URL = "https://gitea.example.com"
    const val TEST_TOKEN = "test-gitea-token-abc123xyz789"
    const val TEST_AUTH_HEADER = "token $TEST_TOKEN"

    // Date formatter for consistent timestamps
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }

    // Helper to create ISO timestamps
    private fun createTimestamp(daysAgo: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        return dateFormat.format(cal.time)
    }

    // Test Users
    val testUserAdmin = GiteaUser(
        id = 1L,
        login = "admin",
        fullName = "Admin User",
        email = "admin@example.com",
        avatarUrl = "$TEST_SERVER_URL/avatars/1",
        isAdmin = true,
        created = createTimestamp(730),
        restricted = false
    )

    val testUserDeveloper = GiteaUser(
        id = 2L,
        login = "developer",
        fullName = "Jane Developer",
        email = "jane@example.com",
        avatarUrl = "$TEST_SERVER_URL/avatars/2",
        isAdmin = false,
        created = createTimestamp(365),
        restricted = false
    )

    val testUserContributor = GiteaUser(
        id = 3L,
        login = "contributor",
        fullName = "Bob Contributor",
        email = "bob@example.com",
        avatarUrl = "$TEST_SERVER_URL/avatars/3",
        isAdmin = false,
        created = createTimestamp(180),
        restricted = false
    )

    // Test Labels
    val testLabelBug = GiteaLabel(
        id = 1L,
        name = "bug",
        color = "#d73a4a",
        description = "Something isn't working"
    )

    val testLabelFeature = GiteaLabel(
        id = 2L,
        name = "enhancement",
        color = "#a2eeef",
        description = "New feature or request"
    )

    val testLabelDocs = GiteaLabel(
        id = 3L,
        name = "documentation",
        color = "#0075ca",
        description = "Improvements or additions to documentation"
    )

    val testAllLabels = listOf(testLabelBug, testLabelFeature, testLabelDocs)

    // Test Milestones
    val testMilestoneV1 = GiteaMilestone(
        id = 1L,
        title = "v1.0.0",
        description = "First stable release",
        state = "open",
        openIssues = 5,
        closedIssues = 10,
        createdAt = createTimestamp(90),
        updatedAt = createTimestamp(1),
        closedAt = null,
        dueOn = createTimestamp(-30)
    )

    val testMilestoneV2 = GiteaMilestone(
        id = 2L,
        title = "v2.0.0",
        description = "Major version update",
        state = "open",
        openIssues = 8,
        closedIssues = 2,
        createdAt = createTimestamp(60),
        updatedAt = createTimestamp(2),
        closedAt = null,
        dueOn = createTimestamp(-90)
    )

    // Test Repositories
    val testRepoPublic = GiteaRepository(
        id = 1L,
        owner = testUserAdmin,
        name = "awesome-project",
        fullName = "admin/awesome-project",
        description = "An awesome open source project",
        empty = false,
        private = false,
        fork = false,
        template = false,
        parent = null,
        mirror = false,
        size = 15728640L, // 15 MB
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project",
        sshUrl = "git@gitea.example.com:admin/awesome-project.git",
        cloneUrl = "$TEST_SERVER_URL/admin/awesome-project.git",
        website = "https://awesome-project.example.com",
        starsCount = 42,
        forksCount = 8,
        watchersCount = 15,
        openIssuesCount = 5,
        openPrCounter = 2,
        releaseCounter = 3,
        defaultBranch = "main",
        createdAt = createTimestamp(365),
        updatedAt = createTimestamp(1)
    )

    val testRepoPrivate = GiteaRepository(
        id = 2L,
        owner = testUserDeveloper,
        name = "secret-project",
        fullName = "developer/secret-project",
        description = "Private project with sensitive code",
        empty = false,
        private = true,
        fork = false,
        template = false,
        parent = null,
        mirror = false,
        size = 5242880L, // 5 MB
        htmlUrl = "$TEST_SERVER_URL/developer/secret-project",
        sshUrl = "git@gitea.example.com:developer/secret-project.git",
        cloneUrl = "$TEST_SERVER_URL/developer/secret-project.git",
        website = null,
        starsCount = 3,
        forksCount = 0,
        watchersCount = 2,
        openIssuesCount = 12,
        openPrCounter = 4,
        releaseCounter = 0,
        defaultBranch = "main",
        createdAt = createTimestamp(180),
        updatedAt = createTimestamp(2)
    )

    val testRepoFork = GiteaRepository(
        id = 3L,
        owner = testUserContributor,
        name = "awesome-project",
        fullName = "contributor/awesome-project",
        description = "Forked from admin/awesome-project",
        empty = false,
        private = false,
        fork = true,
        template = false,
        parent = testRepoPublic,
        mirror = false,
        size = 15728640L,
        htmlUrl = "$TEST_SERVER_URL/contributor/awesome-project",
        sshUrl = "git@gitea.example.com:contributor/awesome-project.git",
        cloneUrl = "$TEST_SERVER_URL/contributor/awesome-project.git",
        website = null,
        starsCount = 0,
        forksCount = 0,
        watchersCount = 1,
        openIssuesCount = 0,
        openPrCounter = 1,
        releaseCounter = 0,
        defaultBranch = "main",
        createdAt = createTimestamp(30),
        updatedAt = createTimestamp(5)
    )

    val testRepoEmpty = GiteaRepository(
        id = 4L,
        owner = testUserDeveloper,
        name = "new-project",
        fullName = "developer/new-project",
        description = null,
        empty = true,
        private = false,
        fork = false,
        template = false,
        parent = null,
        mirror = false,
        size = 0L,
        htmlUrl = "$TEST_SERVER_URL/developer/new-project",
        sshUrl = "git@gitea.example.com:developer/new-project.git",
        cloneUrl = "$TEST_SERVER_URL/developer/new-project.git",
        website = null,
        starsCount = 0,
        forksCount = 0,
        watchersCount = 0,
        openIssuesCount = 0,
        openPrCounter = 0,
        releaseCounter = 0,
        defaultBranch = "main",
        createdAt = createTimestamp(1),
        updatedAt = createTimestamp(1)
    )

    val testAllRepos = listOf(
        testRepoPublic,
        testRepoPrivate,
        testRepoFork,
        testRepoEmpty
    )

    // Test Issues
    val testIssueOpen1 = GiteaIssue(
        id = 1L,
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/issues/1",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/issues/1",
        number = 1L,
        user = testUserContributor,
        title = "Application crashes on startup",
        body = "The app crashes immediately when launched on Android 12. Stack trace attached.",
        state = "open",
        labels = listOf(testLabelBug),
        milestone = testMilestoneV1,
        assignees = listOf(testUserDeveloper),
        comments = 5,
        createdAt = createTimestamp(7),
        updatedAt = createTimestamp(2),
        closedAt = null,
        pullRequest = null
    )

    val testIssueOpen2 = GiteaIssue(
        id = 2L,
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/issues/2",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/issues/2",
        number = 2L,
        user = testUserDeveloper,
        title = "Add dark mode support",
        body = "Users are requesting dark mode for better viewing at night.",
        state = "open",
        labels = listOf(testLabelFeature),
        milestone = testMilestoneV2,
        assignees = emptyList(),
        comments = 12,
        createdAt = createTimestamp(14),
        updatedAt = createTimestamp(1),
        closedAt = null,
        pullRequest = null
    )

    val testIssueClosed = GiteaIssue(
        id = 3L,
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/issues/3",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/issues/3",
        number = 3L,
        user = testUserDeveloper,
        title = "Fix memory leak in background service",
        body = "Background service was causing memory leaks. Fixed by properly cleaning up resources.",
        state = "closed",
        labels = listOf(testLabelBug),
        milestone = testMilestoneV1,
        assignees = listOf(testUserDeveloper),
        comments = 8,
        createdAt = createTimestamp(30),
        updatedAt = createTimestamp(10),
        closedAt = createTimestamp(10),
        pullRequest = null
    )

    val testAllIssues = listOf(testIssueOpen1, testIssueOpen2, testIssueClosed)
    val testOpenIssues = testAllIssues.filter { it.state == "open" }
    val testClosedIssues = testAllIssues.filter { it.state == "closed" }

    // Test Commits
    val testCommit1 = GiteaCommit(
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/git/commits/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
        sha = "a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/commit/a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0",
        commit = GiteaCommit.CommitDetails(
            message = "Initial commit",
            author = GiteaCommit.GitSignature(
                name = "Admin User",
                email = "admin@example.com",
                date = createTimestamp(365)
            ),
            committer = GiteaCommit.GitSignature(
                name = "Admin User",
                email = "admin@example.com",
                date = createTimestamp(365)
            )
        ),
        author = testUserAdmin,
        committer = testUserAdmin
    )

    val testCommit2 = GiteaCommit(
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/git/commits/b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1",
        sha = "b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/commit/b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1",
        commit = GiteaCommit.CommitDetails(
            message = "Add user authentication feature",
            author = GiteaCommit.GitSignature(
                name = "Jane Developer",
                email = "jane@example.com",
                date = createTimestamp(45)
            ),
            committer = GiteaCommit.GitSignature(
                name = "Admin User",
                email = "admin@example.com",
                date = createTimestamp(45)
            )
        ),
        author = testUserDeveloper,
        committer = testUserAdmin
    )

    val testCommit3 = GiteaCommit(
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/git/commits/c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2",
        sha = "c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/commit/c3d4e5f6g7h8i9j0k1l2m3n4o5p6q7r8s9t0u1v2",
        commit = GiteaCommit.CommitDetails(
            message = "Fix memory leak in background service",
            author = GiteaCommit.GitSignature(
                name = "Jane Developer",
                email = "jane@example.com",
                date = createTimestamp(10)
            ),
            committer = GiteaCommit.GitSignature(
                name = "Jane Developer",
                email = "jane@example.com",
                date = createTimestamp(10)
            )
        ),
        author = testUserDeveloper,
        committer = testUserDeveloper
    )

    val testAllCommits = listOf(testCommit1, testCommit2, testCommit3)

    // Test Release Assets
    val testAsset1 = GiteaRelease.ReleaseAsset(
        id = 1L,
        name = "app-release.apk",
        size = 15728640L, // 15 MB
        downloadCount = 156,
        createdAt = createTimestamp(30),
        browserDownloadUrl = "$TEST_SERVER_URL/admin/awesome-project/releases/download/v1.0.0/app-release.apk"
    )

    val testAsset2 = GiteaRelease.ReleaseAsset(
        id = 2L,
        name = "app-debug.apk",
        size = 18874368L, // 18 MB
        downloadCount = 42,
        createdAt = createTimestamp(30),
        browserDownloadUrl = "$TEST_SERVER_URL/admin/awesome-project/releases/download/v1.0.0/app-debug.apk"
    )

    // Test Releases
    val testReleaseStable = GiteaRelease(
        id = 1L,
        tagName = "v1.0.0",
        targetCommitish = "main",
        name = "Version 1.0.0 - First Stable Release",
        body = "## Features\n- User authentication\n- Dark mode support\n- Offline sync\n\n## Bug Fixes\n- Fixed memory leaks\n- Improved performance",
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/releases/1",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/releases/tag/v1.0.0",
        tarballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v1.0.0.tar.gz",
        zipballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v1.0.0.zip",
        draft = false,
        prerelease = false,
        createdAt = createTimestamp(30),
        publishedAt = createTimestamp(30),
        author = testUserAdmin,
        assets = listOf(testAsset1, testAsset2)
    )

    val testReleaseBeta = GiteaRelease(
        id = 2L,
        tagName = "v0.9.0-beta",
        targetCommitish = "develop",
        name = "Version 0.9.0 Beta",
        body = "Beta release for testing. Please report any issues.",
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/releases/2",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/releases/tag/v0.9.0-beta",
        tarballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v0.9.0-beta.tar.gz",
        zipballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v0.9.0-beta.zip",
        draft = false,
        prerelease = true,
        createdAt = createTimestamp(60),
        publishedAt = createTimestamp(60),
        author = testUserDeveloper,
        assets = emptyList()
    )

    val testReleaseDraft = GiteaRelease(
        id = 3L,
        tagName = "v2.0.0",
        targetCommitish = "main",
        name = "Version 2.0.0 - Major Update (Draft)",
        body = "## Upcoming Changes\n- Complete UI redesign\n- New API endpoints\n- Performance improvements",
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/releases/3",
        htmlUrl = "$TEST_SERVER_URL/admin/awesome-project/releases/tag/v2.0.0",
        tarballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v2.0.0.tar.gz",
        zipballUrl = "$TEST_SERVER_URL/admin/awesome-project/archive/v2.0.0.zip",
        draft = true,
        prerelease = false,
        createdAt = createTimestamp(1),
        publishedAt = null,
        author = testUserAdmin,
        assets = emptyList()
    )

    val testAllReleases = listOf(testReleaseStable, testReleaseBeta, testReleaseDraft)

    // Test Pull Requests
    val testPROpen1 = GiteaPullRequest(
        id = 1L,
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/pulls/1",
        number = 1L,
        user = testUserDeveloper,
        title = "Add dark mode feature",
        body = "Implements dark mode support with automatic theme switching.",
        state = "open",
        merged = false,
        mergeable = true,
        mergedAt = null,
        createdAt = createTimestamp(7),
        updatedAt = createTimestamp(1),
        closedAt = null,
        head = GiteaPullRequest.PRBranch(
            label = "developer:feature/dark-mode",
            ref = "feature/dark-mode",
            sha = testCommit3.sha,
            repo = testRepoPublic
        ),
        base = GiteaPullRequest.PRBranch(
            label = "admin:main",
            ref = "main",
            sha = testCommit1.sha,
            repo = testRepoPublic
        )
    )

    val testPRMerged = GiteaPullRequest(
        id = 2L,
        url = "$TEST_SERVER_URL/api/v1/repos/admin/awesome-project/pulls/2",
        number = 2L,
        user = testUserDeveloper,
        title = "Implement user authentication",
        body = "Adds JWT-based authentication system",
        state = "closed",
        merged = true,
        mergeable = true,
        mergedAt = createTimestamp(45),
        createdAt = createTimestamp(50),
        updatedAt = createTimestamp(45),
        closedAt = createTimestamp(45),
        head = GiteaPullRequest.PRBranch(
            label = "developer:feature/auth",
            ref = "feature/auth",
            sha = testCommit2.sha,
            repo = testRepoPublic
        ),
        base = GiteaPullRequest.PRBranch(
            label = "admin:main",
            ref = "main",
            sha = testCommit1.sha,
            repo = testRepoPublic
        )
    )

    val testAllPRs = listOf(testPROpen1, testPRMerged)
    val testOpenPRs = testAllPRs.filter { it.state == "open" }
    val testClosedPRs = testAllPRs.filter { it.state == "closed" }
    val testMergedPRs = testAllPRs.filter { it.merged == true }

    // Helper Methods

    fun getRepoByFullName(fullName: String): GiteaRepository? {
        return testAllRepos.find { it.fullName == fullName }
    }

    fun getReposByOwner(owner: String): List<GiteaRepository> {
        return testAllRepos.filter { it.owner.login == owner }
    }

    fun getIssueByNumber(number: Long): GiteaIssue? {
        return testAllIssues.find { it.number == number }
    }

    fun getIssuesByState(state: String): List<GiteaIssue> {
        return testAllIssues.filter { it.state == state }
    }

    fun getPRByNumber(number: Long): GiteaPullRequest? {
        return testAllPRs.find { it.number == number }
    }

    fun getPRsByState(state: String): List<GiteaPullRequest> {
        return testAllPRs.filter { it.state == state }
    }

    fun getReleaseByTagName(tagName: String): GiteaRelease? {
        return testAllReleases.find { it.tagName == tagName }
    }

    fun getCommitBySha(sha: String): GiteaCommit? {
        return testAllCommits.find { it.sha == sha }
    }
}
