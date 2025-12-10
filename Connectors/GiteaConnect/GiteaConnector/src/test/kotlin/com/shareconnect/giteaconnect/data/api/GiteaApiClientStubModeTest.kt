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

import kotlinx.coroutines.test.runTest
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

/**
 * Tests for GiteaApiClient in stub mode to ensure stub data integration works correctly.
 */
@RunWith(RobolectricTestRunner::class)
class GiteaApiClientStubModeTest {

    private lateinit var apiClient: GiteaApiClient

    @Before
    fun setup() {
        apiClient = GiteaApiClient(
            serverUrl = GiteaTestData.TEST_SERVER_URL,
            token = GiteaTestData.TEST_TOKEN,
            isStubMode = true
        )
        GiteaApiStubService.resetState()
    }

    // User Tests

    @Test
    fun `test getCurrentUser succeeds in stub mode`() = runTest {
        val result = apiClient.getCurrentUser()

        assertTrue("Request should succeed", result.isSuccess)

        val user = result.getOrThrow()
        assertNotNull("User should not be null", user)
        assertEquals("Login should match", GiteaTestData.testUserAdmin.login, user.login)
        assertTrue("Should be admin", user.isAdmin)
    }

    // Repository Tests

    @Test
    fun `test getUserRepos succeeds in stub mode`() = runTest {
        val result = apiClient.getUserRepos()

        assertTrue("Request should succeed", result.isSuccess)

        val repos = result.getOrThrow()
        assertTrue("Should have repositories", repos.isNotEmpty())
    }

    @Test
    fun `test getUserRepos with pagination in stub mode`() = runTest {
        val page1 = apiClient.getUserRepos(page = 1, limit = 1)
        val page2 = apiClient.getUserRepos(page = 2, limit = 1)

        assertTrue("Page 1 should succeed", page1.isSuccess)
        assertTrue("Page 2 should succeed", page2.isSuccess)

        val repos1 = page1.getOrThrow()
        val repos2 = page2.getOrThrow()

        assertEquals("Page 1 should have 1 item", 1, repos1.size)
        if (repos2.isNotEmpty()) {
            assertNotEquals("Pages should have different repos",
                repos1.first().id, repos2.first().id)
        }
    }

    @Test
    fun `test getRepository succeeds in stub mode`() = runTest {
        val result = apiClient.getRepository("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        val repo = result.getOrThrow()
        assertEquals("Name should match", "awesome-project", repo.name)
        assertEquals("Owner should match", "admin", repo.owner.login)
    }

    @Test
    fun `test getRepository fails for invalid repo in stub mode`() = runTest {
        val result = apiClient.getRepository("admin", "nonexistent")

        assertTrue("Request should fail", result.isFailure)
        assertTrue("Error should mention failure",
            result.exceptionOrNull()!!.message!!.contains("failed"))
    }

    @Test
    fun `test createRepository succeeds in stub mode`() = runTest {
        val result = apiClient.createRepository(
            name = "new-test-repo",
            description = "Test description",
            private = false
        )

        assertTrue("Request should succeed", result.isSuccess)

        val repo = result.getOrThrow()
        assertEquals("Name should match", "new-test-repo", repo.name)
        assertEquals("Description should match", "Test description", repo.description)
        assertFalse("Should not be private", repo.private)

        // Verify repo exists
        val getResult = apiClient.getRepository("admin", "new-test-repo")
        assertTrue("Created repo should exist", getResult.isSuccess)
    }

    @Test
    fun `test deleteRepository succeeds in stub mode`() = runTest {
        val result = apiClient.deleteRepository("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        // Verify repo is deleted
        val getResult = apiClient.getRepository("admin", "awesome-project")
        assertTrue("Deleted repo should not exist", getResult.isFailure)
    }

    // Issue Tests

    @Test
    fun `test getIssues succeeds in stub mode`() = runTest {
        val result = apiClient.getIssues("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        val issues = result.getOrThrow()
        assertTrue("Should have issues", issues.isNotEmpty())
    }

    @Test
    fun `test getIssues filters by state in stub mode`() = runTest {
        val openResult = apiClient.getIssues("admin", "awesome-project", state = "open")
        val closedResult = apiClient.getIssues("admin", "awesome-project", state = "closed")

        assertTrue("Open request should succeed", openResult.isSuccess)
        assertTrue("Closed request should succeed", closedResult.isSuccess)

        val openIssues = openResult.getOrThrow()
        val closedIssues = closedResult.getOrThrow()

        assertTrue("All open issues should be open", openIssues.all { it.state == "open" })
        assertTrue("All closed issues should be closed", closedIssues.all { it.state == "closed" })
    }

    @Test
    fun `test getIssues with pagination in stub mode`() = runTest {
        val page1 = apiClient.getIssues("admin", "awesome-project", page = 1, limit = 2)
        val page2 = apiClient.getIssues("admin", "awesome-project", page = 2, limit = 2)

        assertTrue("Page 1 should succeed", page1.isSuccess)
        assertTrue("Page 2 should succeed", page2.isSuccess)
    }

    @Test
    fun `test createIssue succeeds in stub mode`() = runTest {
        val result = apiClient.createIssue(
            owner = "admin",
            repo = "awesome-project",
            title = "New bug report",
            body = "Found a bug in the app"
        )

        assertTrue("Request should succeed", result.isSuccess)

        val issue = result.getOrThrow()
        assertEquals("Title should match", "New bug report", issue.title)
        assertEquals("Body should match", "Found a bug in the app", issue.body)
        assertEquals("State should be open", "open", issue.state)
    }

    // Release Tests

    @Test
    fun `test getReleases succeeds in stub mode`() = runTest {
        val result = apiClient.getReleases("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        val releases = result.getOrThrow()
        assertTrue("Should have releases", releases.isNotEmpty())
        assertTrue("Should not include drafts", releases.none { it.draft })
    }

    @Test
    fun `test getReleases with pagination in stub mode`() = runTest {
        val page1 = apiClient.getReleases("admin", "awesome-project", page = 1, limit = 1)
        val page2 = apiClient.getReleases("admin", "awesome-project", page = 2, limit = 1)

        assertTrue("Page 1 should succeed", page1.isSuccess)
        assertTrue("Page 2 should succeed", page2.isSuccess)
    }

    // Commit Tests

    @Test
    fun `test getCommits succeeds in stub mode`() = runTest {
        val result = apiClient.getCommits("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        val commits = result.getOrThrow()
        assertTrue("Should have commits", commits.isNotEmpty())
        assertTrue("Commits should have messages",
            commits.all { it.commit.message.isNotEmpty() })
    }

    @Test
    fun `test getCommits with branch in stub mode`() = runTest {
        val result = apiClient.getCommits("admin", "awesome-project", branch = "main")

        assertTrue("Request should succeed", result.isSuccess)

        val commits = result.getOrThrow()
        assertTrue("Should have commits", commits.isNotEmpty())
    }

    @Test
    fun `test getCommits with pagination in stub mode`() = runTest {
        val page1 = apiClient.getCommits("admin", "awesome-project", page = 1, limit = 2)
        val page2 = apiClient.getCommits("admin", "awesome-project", page = 2, limit = 2)

        assertTrue("Page 1 should succeed", page1.isSuccess)
        assertTrue("Page 2 should succeed", page2.isSuccess)

        val commits1 = page1.getOrThrow()
        assertEquals("Page 1 should have 2 items", 2, commits1.size)
    }

    // Pull Request Tests

    @Test
    fun `test getPullRequests succeeds in stub mode`() = runTest {
        val result = apiClient.getPullRequests("admin", "awesome-project")

        assertTrue("Request should succeed", result.isSuccess)

        val prs = result.getOrThrow()
        assertTrue("Should have PRs", prs.isNotEmpty())
    }

    @Test
    fun `test getPullRequests filters by state in stub mode`() = runTest {
        val openResult = apiClient.getPullRequests("admin", "awesome-project", state = "open")
        val closedResult = apiClient.getPullRequests("admin", "awesome-project", state = "closed")

        assertTrue("Open request should succeed", openResult.isSuccess)
        assertTrue("Closed request should succeed", closedResult.isSuccess)

        val openPRs = openResult.getOrThrow()
        val closedPRs = closedResult.getOrThrow()

        assertTrue("All open PRs should be open", openPRs.all { it.state == "open" })
        assertTrue("All closed PRs should be closed", closedPRs.all { it.state == "closed" })
    }

    @Test
    fun `test getPullRequests with pagination in stub mode`() = runTest {
        val page1 = apiClient.getPullRequests("admin", "awesome-project", page = 1, limit = 2)
        val page2 = apiClient.getPullRequests("admin", "awesome-project", page = 2, limit = 2)

        assertTrue("Page 1 should succeed", page1.isSuccess)
        assertTrue("Page 2 should succeed", page2.isSuccess)
    }

    // Star Tests

    @Test
    fun `test starRepository increases star count in stub mode`() = runTest {
        val beforeResult = apiClient.getRepository("admin", "awesome-project")
        val beforeStars = beforeResult.getOrThrow().starsCount

        val starResult = apiClient.starRepository("admin", "awesome-project")
        assertTrue("Star should succeed", starResult.isSuccess)

        val afterResult = apiClient.getRepository("admin", "awesome-project")
        val afterStars = afterResult.getOrThrow().starsCount

        assertEquals("Star count should increase", beforeStars + 1, afterStars)
    }

    @Test
    fun `test unstarRepository decreases star count in stub mode`() = runTest {
        // First star the repo
        apiClient.starRepository("admin", "awesome-project")

        val beforeResult = apiClient.getRepository("admin", "awesome-project")
        val beforeStars = beforeResult.getOrThrow().starsCount

        val unstarResult = apiClient.unstarRepository("admin", "awesome-project")
        assertTrue("Unstar should succeed", unstarResult.isSuccess)

        val afterResult = apiClient.getRepository("admin", "awesome-project")
        val afterStars = afterResult.getOrThrow().starsCount

        assertEquals("Star count should decrease", beforeStars - 1, afterStars)
    }

    // Complete Workflow Tests

    @Test
    fun `test complete repository workflow in stub mode`() = runTest {
        // 1. Create repository
        val createResult = apiClient.createRepository(
            name = "workflow-test",
            description = "Test workflow",
            private = false
        )
        assertTrue("Create should succeed", createResult.isSuccess)
        val repo = createResult.getOrThrow()

        // 2. Get repository
        val getResult = apiClient.getRepository("admin", "workflow-test")
        assertTrue("Get should succeed", getResult.isSuccess)
        assertEquals("Name should match", "workflow-test", getResult.getOrThrow().name)

        // 3. Star repository
        apiClient.starRepository("admin", "workflow-test")
        val starredRepo = apiClient.getRepository("admin", "workflow-test").getOrThrow()
        assertEquals("Star count should increase", repo.starsCount + 1, starredRepo.starsCount)

        // 4. Unstar repository
        apiClient.unstarRepository("admin", "workflow-test")
        val unstarredRepo = apiClient.getRepository("admin", "workflow-test").getOrThrow()
        assertEquals("Star count should decrease", repo.starsCount, unstarredRepo.starsCount)

        // 5. Delete repository
        val deleteResult = apiClient.deleteRepository("admin", "workflow-test")
        assertTrue("Delete should succeed", deleteResult.isSuccess)

        // 6. Verify deletion
        val verifyResult = apiClient.getRepository("admin", "workflow-test")
        assertTrue("Repo should not exist", verifyResult.isFailure)
    }

    @Test
    fun `test complete issue workflow in stub mode`() = runTest {
        // 1. Create issue
        val createResult = apiClient.createIssue(
            owner = "admin",
            repo = "awesome-project",
            title = "Workflow test issue",
            body = "Testing the complete workflow"
        )
        assertTrue("Create should succeed", createResult.isSuccess)
        val issue = createResult.getOrThrow()
        assertEquals("State should be open", "open", issue.state)

        // 2. Verify issue appears in open issues list
        val openIssues = apiClient.getIssues("admin", "awesome-project", state = "open").getOrThrow()
        assertTrue("Should contain new issue", openIssues.any { it.number == issue.number })

        // Note: editIssue not exposed in client, but tested in stub service tests
    }

    @Test
    fun `test list all repositories workflow in stub mode`() = runTest {
        // 1. Get all admin repos
        val result = apiClient.getUserRepos()
        assertTrue("Request should succeed", result.isSuccess)

        val repos = result.getOrThrow()
        assertTrue("Should have repos", repos.isNotEmpty())

        // 2. Get details for first repo
        val firstRepo = repos.first()
        val detailResult = apiClient.getRepository(firstRepo.owner.login, firstRepo.name)
        assertTrue("Detail request should succeed", detailResult.isSuccess)

        val details = detailResult.getOrThrow()
        assertEquals("ID should match", firstRepo.id, details.id)
    }

    @Test
    fun `test repository with issues and PRs workflow in stub mode`() = runTest {
        // 1. Get repository
        val repo = apiClient.getRepository("admin", "awesome-project").getOrThrow()
        assertNotNull("Repo should exist", repo)

        // 2. Get issues
        val issues = apiClient.getIssues("admin", "awesome-project").getOrThrow()
        assertTrue("Should have issues", issues.isNotEmpty())

        // 3. Get pull requests
        val prs = apiClient.getPullRequests("admin", "awesome-project").getOrThrow()
        assertTrue("Should have PRs", prs.isNotEmpty())

        // 4. Get commits
        val commits = apiClient.getCommits("admin", "awesome-project").getOrThrow()
        assertTrue("Should have commits", commits.isNotEmpty())

        // 5. Get releases
        val releases = apiClient.getReleases("admin", "awesome-project").getOrThrow()
        assertTrue("Should have releases", releases.isNotEmpty())
    }

    @Test
    fun `test error handling for invalid operations in stub mode`() = runTest {
        // Test invalid repository
        assertTrue("Should fail for invalid repo",
            apiClient.getRepository("admin", "nonexistent").isFailure)

        // Test invalid issue number
        assertTrue("Should fail for invalid issue",
            apiClient.getIssues("admin", "nonexistent").isFailure)

        // Test invalid PR number
        assertTrue("Should fail for invalid PR",
            apiClient.getPullRequests("admin", "nonexistent").isFailure)

        // Test delete nonexistent repo
        assertTrue("Should fail to delete nonexistent repo",
            apiClient.deleteRepository("admin", "nonexistent").isFailure)
    }

    @Test
    fun `test pagination workflow across all list endpoints in stub mode`() = runTest {
        // Repositories pagination
        val repos1 = apiClient.getUserRepos(page = 1, limit = 1).getOrThrow()
        val repos2 = apiClient.getUserRepos(page = 2, limit = 1).getOrThrow()
        if (repos1.isNotEmpty() && repos2.isNotEmpty()) {
            assertNotEquals("Repo pages should differ", repos1.first().id, repos2.first().id)
        }

        // Issues pagination
        val issues1 = apiClient.getIssues("admin", "awesome-project", page = 1, limit = 1).getOrThrow()
        val issues2 = apiClient.getIssues("admin", "awesome-project", page = 2, limit = 1).getOrThrow()
        if (issues1.isNotEmpty() && issues2.isNotEmpty()) {
            assertNotEquals("Issue pages should differ", issues1.first().number, issues2.first().number)
        }

        // PRs pagination
        val prs1 = apiClient.getPullRequests("admin", "awesome-project", page = 1, limit = 1).getOrThrow()
        val prs2 = apiClient.getPullRequests("admin", "awesome-project", page = 2, limit = 1).getOrThrow()
        if (prs1.isNotEmpty() && prs2.isNotEmpty()) {
            assertNotEquals("PR pages should differ", prs1.first().number, prs2.first().number)
        }

        // Commits pagination
        val commits1 = apiClient.getCommits("admin", "awesome-project", page = 1, limit = 2).getOrThrow()
        val commits2 = apiClient.getCommits("admin", "awesome-project", page = 2, limit = 2).getOrThrow()
        if (commits1.isNotEmpty() && commits2.isNotEmpty()) {
            assertNotEquals("Commit pages should differ", commits1.first().sha, commits2.first().sha)
        }

        // Releases pagination
        val releases1 = apiClient.getReleases("admin", "awesome-project", page = 1, limit = 1).getOrThrow()
        val releases2 = apiClient.getReleases("admin", "awesome-project", page = 2, limit = 1).getOrThrow()
        if (releases1.isNotEmpty() && releases2.isNotEmpty()) {
            assertNotEquals("Release pages should differ", releases1.first().id, releases2.first().id)
        }
    }

    @Test
    fun `test state management workflow in stub mode`() = runTest {
        // Create a repository
        val createResult = apiClient.createRepository(
            name = "state-test",
            description = "State test",
            private = false
        )
        assertTrue("Create should succeed", createResult.isSuccess)

        // Verify it exists
        val getResult = apiClient.getRepository("admin", "state-test")
        assertTrue("Should exist", getResult.isSuccess)

        // Reset state
        GiteaApiStubService.resetState()

        // Verify it's gone after reset
        val afterResetResult = apiClient.getRepository("admin", "state-test")
        assertTrue("Should not exist after reset", afterResetResult.isFailure)

        // Verify test data is back
        val testRepoResult = apiClient.getRepository("admin", "awesome-project")
        assertTrue("Test repo should exist", testRepoResult.isSuccess)
    }
}
