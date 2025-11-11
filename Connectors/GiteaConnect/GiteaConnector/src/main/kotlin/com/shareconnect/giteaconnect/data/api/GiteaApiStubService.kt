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
import kotlinx.coroutines.delay
import okhttp3.ResponseBody.Companion.toResponseBody
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

/**
 * Stub implementation of GiteaApiService for testing without a live Gitea server.
 * Provides realistic responses with stateful behavior for repository, issue, PR, and star management.
 */
class GiteaApiStubService(
    private val requireAuth: Boolean = false
) : GiteaApiService {

    companion object {
        private const val NETWORK_DELAY_MS = 500L

        // Stateful storage
        private val repositories = mutableMapOf<String, GiteaRepository>()
        private val issues = mutableMapOf<String, MutableList<GiteaIssue>>()
        private val pullRequests = mutableMapOf<String, MutableList<GiteaPullRequest>>()
        private val starredRepos = mutableSetOf<String>()
        private var isAuthenticated = false
        private var nextRepoId = 100L
        private var nextIssueId = 100L
        private var nextPRId = 100L

        /**
         * Resets the stub service to its initial state with test data.
         */
        fun resetState() {
            repositories.clear()
            issues.clear()
            pullRequests.clear()
            starredRepos.clear()
            isAuthenticated = false
            nextRepoId = 100L
            nextIssueId = 100L
            nextPRId = 100L

            // Initialize with test data
            GiteaTestData.testAllRepos.forEach { repo ->
                repositories[repo.fullName] = repo
            }

            // Initialize issues for awesome-project
            val awesomeProjectKey = "admin/awesome-project"
            issues[awesomeProjectKey] = GiteaTestData.testAllIssues.toMutableList()

            // Initialize PRs for awesome-project
            pullRequests[awesomeProjectKey] = GiteaTestData.testAllPRs.toMutableList()
        }

        /**
         * Authenticate for testing (when requireAuth = true).
         */
        fun authenticate() {
            isAuthenticated = true
        }

        // Date formatter for timestamps
        private val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
            timeZone = TimeZone.getTimeZone("UTC")
        }

        private fun currentTimestamp(): String {
            return dateFormat.format(Date())
        }
    }

    init {
        if (repositories.isEmpty()) {
            resetState()
        }
    }

    private suspend fun checkAuth(authorization: String) {
        if (requireAuth && !isAuthenticated) {
            throw SecurityException("Unauthorized: Authentication required")
        }
        if (authorization != GiteaTestData.TEST_AUTH_HEADER) {
            throw SecurityException("Unauthorized: Invalid token")
        }
    }

    // User Endpoints

    override suspend fun getCurrentUser(authorization: String): Response<GiteaUser> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        return Response.success(GiteaTestData.testUserAdmin)
    }

    // Repository Endpoints

    override suspend fun getUserRepos(
        authorization: String,
        page: Int,
        limit: Int
    ): Response<List<GiteaRepository>> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        // Return all repos for authenticated user
        val userRepos = repositories.values.toList()
        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, userRepos.size)

        val paginatedRepos = if (startIndex < userRepos.size) {
            userRepos.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return Response.success(paginatedRepos)
    }

    override suspend fun getRepository(
        owner: String,
        repo: String,
        authorization: String
    ): Response<GiteaRepository> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        val repository = repositories[fullName]

        return if (repository != null) {
            Response.success(repository)
        } else {
            Response.error(404, "Repository not found".toResponseBody())
        }
    }

    override suspend fun createRepository(
        authorization: String,
        repository: Map<String, Any>
    ): Response<GiteaRepository> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        // Create new repository
        val owner = GiteaTestData.testUserAdmin
        val name = repository["name"] as String
        val description = repository["description"] as? String
        val private = repository["private"] as? Boolean ?: false
        val defaultBranch = repository["default_branch"] as? String ?: "main"

        val newRepo = GiteaRepository(
            id = nextRepoId++,
            owner = owner,
            name = name,
            fullName = "${owner.login}/$name",
            description = description,
            empty = true,
            private = private,
            fork = false,
            template = false,
            parent = null,
            mirror = false,
            size = 0L,
            htmlUrl = "${GiteaTestData.TEST_SERVER_URL}/${owner.login}/$name",
            sshUrl = "git@gitea.example.com:${owner.login}/$name.git",
            cloneUrl = "${GiteaTestData.TEST_SERVER_URL}/${owner.login}/$name.git",
            website = null,
            starsCount = 0,
            forksCount = 0,
            watchersCount = 0,
            openIssuesCount = 0,
            openPrCounter = 0,
            releaseCounter = 0,
            defaultBranch = defaultBranch,
            createdAt = currentTimestamp(),
            updatedAt = currentTimestamp()
        )

        repositories[newRepo.fullName] = newRepo
        issues[newRepo.fullName] = mutableListOf()
        pullRequests[newRepo.fullName] = mutableListOf()

        return Response.success(201, newRepo)
    }

    override suspend fun deleteRepository(
        owner: String,
        repo: String,
        authorization: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        return if (repositories.containsKey(fullName)) {
            repositories.remove(fullName)
            issues.remove(fullName)
            pullRequests.remove(fullName)
            starredRepos.remove(fullName)
            Response.success(204, Unit)
        } else {
            Response.error(404, "Repository not found".toResponseBody())
        }
    }

    // Issue Endpoints

    override suspend fun getIssues(
        owner: String,
        repo: String,
        authorization: String,
        state: String?,
        page: Int,
        limit: Int
    ): Response<List<GiteaIssue>> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val repoIssues = issues[fullName] ?: emptyList()
        val filteredIssues = when (state) {
            "open" -> repoIssues.filter { it.state == "open" }
            "closed" -> repoIssues.filter { it.state == "closed" }
            else -> repoIssues
        }

        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, filteredIssues.size)

        val paginatedIssues = if (startIndex < filteredIssues.size) {
            filteredIssues.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return Response.success(paginatedIssues)
    }

    override suspend fun getIssue(
        owner: String,
        repo: String,
        index: Long,
        authorization: String
    ): Response<GiteaIssue> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val issue = issues[fullName]?.find { it.number == index }
        return if (issue != null) {
            Response.success(issue)
        } else {
            Response.error(404, "Issue not found".toResponseBody())
        }
    }

    override suspend fun createIssue(
        owner: String,
        repo: String,
        authorization: String,
        issue: Map<String, Any>
    ): Response<GiteaIssue> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val issueList = issues.getOrPut(fullName) { mutableListOf() }
        val nextNumber = (issueList.maxOfOrNull { it.number } ?: 0L) + 1

        val title = issue["title"] as String
        val body = issue["body"] as? String ?: ""

        val newIssue = GiteaIssue(
            id = nextIssueId++,
            url = "${GiteaTestData.TEST_SERVER_URL}/api/v1/repos/$fullName/issues/$nextNumber",
            htmlUrl = "${GiteaTestData.TEST_SERVER_URL}/$fullName/issues/$nextNumber",
            number = nextNumber,
            user = GiteaTestData.testUserAdmin,
            title = title,
            body = body,
            state = "open",
            labels = emptyList(),
            milestone = null,
            assignees = emptyList(),
            comments = 0,
            createdAt = currentTimestamp(),
            updatedAt = currentTimestamp(),
            closedAt = null,
            pullRequest = null
        )

        issueList.add(newIssue)

        // Update repo issue count
        repositories[fullName]?.let { repository ->
            repositories[fullName] = repository.copy(
                openIssuesCount = repository.openIssuesCount + 1,
                updatedAt = currentTimestamp()
            )
        }

        return Response.success(201, newIssue)
    }

    override suspend fun editIssue(
        owner: String,
        repo: String,
        index: Long,
        authorization: String,
        updates: Map<String, Any>
    ): Response<GiteaIssue> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val issueList = issues[fullName] ?: return Response.error(404, "Issue not found".toResponseBody())
        val issueIndex = issueList.indexOfFirst { it.number == index }

        if (issueIndex == -1) {
            return Response.error(404, "Issue not found".toResponseBody())
        }

        val oldIssue = issueList[issueIndex]
        val newTitle = updates["title"] as? String ?: oldIssue.title
        val newBody = updates["body"] as? String ?: oldIssue.body
        val newState = updates["state"] as? String ?: oldIssue.state
        val newClosedAt = if (newState == "closed" && oldIssue.state != "closed") currentTimestamp() else oldIssue.closedAt

        val updatedIssue = oldIssue.copy(
            title = newTitle,
            body = newBody,
            state = newState,
            closedAt = newClosedAt,
            updatedAt = currentTimestamp()
        )

        issueList[issueIndex] = updatedIssue

        // Update repo issue counts if state changed
        if (newState != oldIssue.state) {
            repositories[fullName]?.let { repository ->
                val delta = if (newState == "closed") -1 else 1
                repositories[fullName] = repository.copy(
                    openIssuesCount = repository.openIssuesCount + delta,
                    updatedAt = currentTimestamp()
                )
            }
        }

        return Response.success(updatedIssue)
    }

    // Release Endpoints

    override suspend fun getReleases(
        owner: String,
        repo: String,
        authorization: String,
        page: Int,
        limit: Int
    ): Response<List<GiteaRelease>> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        // Return test releases for awesome-project
        val releases = if (fullName == "admin/awesome-project") {
            GiteaTestData.testAllReleases.filterNot { it.draft }
        } else {
            emptyList()
        }

        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, releases.size)

        val paginatedReleases = if (startIndex < releases.size) {
            releases.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return Response.success(paginatedReleases)
    }

    override suspend fun getRelease(
        owner: String,
        repo: String,
        id: Long,
        authorization: String
    ): Response<GiteaRelease> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val release = GiteaTestData.testAllReleases.find { it.id == id }
        return if (release != null) {
            Response.success(release)
        } else {
            Response.error(404, "Release not found".toResponseBody())
        }
    }

    // Commit Endpoints

    override suspend fun getCommits(
        owner: String,
        repo: String,
        authorization: String,
        sha: String?,
        page: Int,
        limit: Int
    ): Response<List<GiteaCommit>> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        // Return test commits for awesome-project
        val commits = if (fullName == "admin/awesome-project") {
            GiteaTestData.testAllCommits
        } else {
            emptyList()
        }

        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, commits.size)

        val paginatedCommits = if (startIndex < commits.size) {
            commits.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return Response.success(paginatedCommits)
    }

    // Pull Request Endpoints

    override suspend fun getPullRequests(
        owner: String,
        repo: String,
        authorization: String,
        state: String?,
        page: Int,
        limit: Int
    ): Response<List<GiteaPullRequest>> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val repoPRs = pullRequests[fullName] ?: emptyList()
        val filteredPRs = when (state) {
            "open" -> repoPRs.filter { it.state == "open" }
            "closed" -> repoPRs.filter { it.state == "closed" }
            else -> repoPRs
        }

        val startIndex = (page - 1) * limit
        val endIndex = minOf(startIndex + limit, filteredPRs.size)

        val paginatedPRs = if (startIndex < filteredPRs.size) {
            filteredPRs.subList(startIndex, endIndex)
        } else {
            emptyList()
        }

        return Response.success(paginatedPRs)
    }

    override suspend fun getPullRequest(
        owner: String,
        repo: String,
        index: Long,
        authorization: String
    ): Response<GiteaPullRequest> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        if (!repositories.containsKey(fullName)) {
            return Response.error(404, "Repository not found".toResponseBody())
        }

        val pr = pullRequests[fullName]?.find { it.number == index }
        return if (pr != null) {
            Response.success(pr)
        } else {
            Response.error(404, "Pull request not found".toResponseBody())
        }
    }

    override suspend fun createPullRequest(
        owner: String,
        repo: String,
        authorization: String,
        pullRequest: Map<String, Any>
    ): Response<GiteaPullRequest> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        val repository = repositories[fullName]
            ?: return Response.error(404, "Repository not found".toResponseBody())

        val prList = pullRequests.getOrPut(fullName) { mutableListOf() }
        val nextNumber = (prList.maxOfOrNull { it.number } ?: 0L) + 1

        val title = pullRequest["title"] as String
        val body = pullRequest["body"] as? String ?: ""
        val head = pullRequest["head"] as String
        val base = pullRequest["base"] as String

        val newPR = GiteaPullRequest(
            id = nextPRId++,
            url = "${GiteaTestData.TEST_SERVER_URL}/api/v1/repos/$fullName/pulls/$nextNumber",
            number = nextNumber,
            user = GiteaTestData.testUserAdmin,
            title = title,
            body = body,
            state = "open",
            merged = false,
            mergeable = true,
            mergedAt = null,
            createdAt = currentTimestamp(),
            updatedAt = currentTimestamp(),
            closedAt = null,
            head = GiteaPullRequest.PRBranch(
                label = "${repository.owner.login}:$head",
                ref = head,
                sha = "new_commit_sha_${System.currentTimeMillis()}",
                repo = repository
            ),
            base = GiteaPullRequest.PRBranch(
                label = "${repository.owner.login}:$base",
                ref = base,
                sha = "base_commit_sha_${System.currentTimeMillis()}",
                repo = repository
            )
        )

        prList.add(newPR)

        // Update repo PR count
        repositories[fullName] = repository.copy(
            openPrCounter = repository.openPrCounter + 1,
            updatedAt = currentTimestamp()
        )

        return Response.success(201, newPR)
    }

    // Star Endpoints

    override suspend fun starRepository(
        owner: String,
        repo: String,
        authorization: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        val repository = repositories[fullName]
            ?: return Response.error(404, "Repository not found".toResponseBody())

        if (!starredRepos.contains(fullName)) {
            starredRepos.add(fullName)
            repositories[fullName] = repository.copy(
                starsCount = repository.starsCount + 1,
                updatedAt = currentTimestamp()
            )
        }

        return Response.success(204, Unit)
    }

    override suspend fun unstarRepository(
        owner: String,
        repo: String,
        authorization: String
    ): Response<Unit> {
        delay(NETWORK_DELAY_MS)
        checkAuth(authorization)

        val fullName = "$owner/$repo"
        val repository = repositories[fullName]
            ?: return Response.error(404, "Repository not found".toResponseBody())

        if (starredRepos.contains(fullName)) {
            starredRepos.remove(fullName)
            repositories[fullName] = repository.copy(
                starsCount = maxOf(0, repository.starsCount - 1),
                updatedAt = currentTimestamp()
            )
        }

        return Response.success(204, Unit)
    }
}
