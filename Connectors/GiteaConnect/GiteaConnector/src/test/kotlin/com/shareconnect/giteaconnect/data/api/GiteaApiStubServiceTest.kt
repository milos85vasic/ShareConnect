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
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

/**
 * Tests for GiteaApiStubService to ensure stub data is returned correctly.
 */
class GiteaApiStubServiceTest {

    private lateinit var stubService: GiteaApiStubService

    @Before
    fun setup() {
        stubService = GiteaApiStubService(requireAuth = false)
        GiteaApiStubService.resetState()
    }

    @After
    fun tearDown() {
        GiteaApiStubService.resetState()
    }

    // User Tests

    @Test
    fun `test getCurrentUser returns valid user`() = runTest {
        val response = stubService.getCurrentUser(GiteaTestData.TEST_AUTH_HEADER)

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val user = response.body()!!
        assertEquals("User should be admin", GiteaTestData.testUserAdmin.login, user.login)
        assertTrue("Should be admin", user.isAdmin)
    }

    @Test
    fun `test getCurrentUser with invalid auth fails`() = runTest {
        try {
            stubService.getCurrentUser("invalid")
            fail("Should throw SecurityException")
        } catch (e: SecurityException) {
            assertTrue("Error should mention auth", e.message!!.contains("Unauthorized"))
        }
    }

    // Repository Tests

    @Test
    fun `test getUserRepos returns repositories`() = runTest {
        val response = stubService.getUserRepos(
            GiteaTestData.TEST_AUTH_HEADER,
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val repos = response.body()!!
        assertTrue("Should have repositories", repos.isNotEmpty())
    }

    @Test
    fun `test getUserRepos with pagination`() = runTest {
        val page1 = stubService.getUserRepos(
            GiteaTestData.TEST_AUTH_HEADER,
            page = 1,
            limit = 1
        )

        assertTrue("Page 1 should be successful", page1.isSuccessful)
        assertEquals("Page 1 should have 1 item", 1, page1.body()!!.size)

        val page2 = stubService.getUserRepos(
            GiteaTestData.TEST_AUTH_HEADER,
            page = 2,
            limit = 1
        )

        assertTrue("Page 2 should be successful", page2.isSuccessful)
        if (page2.body()!!.isNotEmpty()) {
            assertNotEquals("Pages should have different repos",
                page1.body()!!.first().id, page2.body()!!.first().id)
        }
    }

    @Test
    fun `test getRepository returns repo details`() = runTest {
        val response = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val repo = response.body()!!
        assertEquals("Name should match", "awesome-project", repo.name)
        assertEquals("Owner should match", "admin", repo.owner.login)
        assertFalse("Should not be private", repo.private)
    }

    @Test
    fun `test getRepository with invalid repo returns 404`() = runTest {
        val response = stubService.getRepository(
            "admin",
            "nonexistent",
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertFalse("Response should fail", response.isSuccessful)
        assertEquals("Should be 404", 404, response.code())
    }

    @Test
    fun `test createRepository creates new repo`() = runTest {
        val request = mapOf(
            "name" to "new-test-repo",
            "description" to "A test repository",
            "private" to false
        )

        val response = stubService.createRepository(
            GiteaTestData.TEST_AUTH_HEADER,
            request
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertEquals("Should be 201", 201, response.code())
        assertNotNull("Body should not be null", response.body())

        val repo = response.body()!!
        assertEquals("Name should match", "new-test-repo", repo.name)
        assertEquals("Description should match", "A test repository", repo.description)

        // Verify repo exists
        val getResponse = stubService.getRepository(
            "admin",
            "new-test-repo",
            GiteaTestData.TEST_AUTH_HEADER
        )
        assertTrue("Created repo should exist", getResponse.isSuccessful)
    }

    @Test
    fun `test deleteRepository removes repo`() = runTest {
        val response = stubService.deleteRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertEquals("Should be 204", 204, response.code())

        // Verify repo is deleted
        val getResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        assertFalse("Deleted repo should not exist", getResponse.isSuccessful)
        assertEquals("Should be 404", 404, getResponse.code())
    }

    // Issue Tests

    @Test
    fun `test getIssues returns issues`() = runTest {
        val response = stubService.getIssues(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "all",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val issues = response.body()!!
        assertTrue("Should have issues", issues.isNotEmpty())
    }

    @Test
    fun `test getIssues filters by state`() = runTest {
        val openResponse = stubService.getIssues(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "open",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", openResponse.isSuccessful)
        val openIssues = openResponse.body()!!
        assertTrue("All issues should be open", openIssues.all { it.state == "open" })

        val closedResponse = stubService.getIssues(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "closed",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", closedResponse.isSuccessful)
        val closedIssues = closedResponse.body()!!
        assertTrue("All issues should be closed", closedIssues.all { it.state == "closed" })
    }

    @Test
    fun `test getIssue returns issue details`() = runTest {
        val response = stubService.getIssue(
            "admin",
            "awesome-project",
            1L,
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val issue = response.body()!!
        assertEquals("Number should match", 1L, issue.number)
        assertNotNull("Title should not be null", issue.title)
    }

    @Test
    fun `test getIssue with invalid number returns 404`() = runTest {
        val response = stubService.getIssue(
            "admin",
            "awesome-project",
            999L,
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertFalse("Response should fail", response.isSuccessful)
        assertEquals("Should be 404", 404, response.code())
    }

    @Test
    fun `test createIssue creates new issue`() = runTest {
        val request = mapOf(
            "title" to "New test issue",
            "body" to "This is a test issue"
        )

        val response = stubService.createIssue(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            request
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertEquals("Should be 201", 201, response.code())
        assertNotNull("Body should not be null", response.body())

        val issue = response.body()!!
        assertEquals("Title should match", "New test issue", issue.title)
        assertEquals("Body should match", "This is a test issue", issue.body)
        assertEquals("State should be open", "open", issue.state)
    }

    @Test
    fun `test editIssue updates issue`() = runTest {
        val request = mapOf(
            "title" to "Updated title",
            "state" to "closed"
        )

        val response = stubService.editIssue(
            "admin",
            "awesome-project",
            1L,
            GiteaTestData.TEST_AUTH_HEADER,
            request
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val issue = response.body()!!
        assertEquals("Title should be updated", "Updated title", issue.title)
        assertEquals("State should be closed", "closed", issue.state)

        // Verify change persists
        val getResponse = stubService.getIssue(
            "admin",
            "awesome-project",
            1L,
            GiteaTestData.TEST_AUTH_HEADER
        )
        assertEquals("State should persist", "closed", getResponse.body()!!.state)
    }

    // Release Tests

    @Test
    fun `test getReleases returns releases`() = runTest {
        val response = stubService.getReleases(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val releases = response.body()!!
        assertTrue("Should have releases", releases.isNotEmpty())
        assertTrue("Should not include drafts", releases.none { it.draft })
    }

    @Test
    fun `test getRelease returns release details`() = runTest {
        val response = stubService.getRelease(
            "admin",
            "awesome-project",
            1L, // testReleaseStable ID
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val release = response.body()!!
        assertEquals("Tag should match", "v1.0.0", release.tagName)
        assertNotNull("Name should not be null", release.name)
    }

    @Test
    fun `test getRelease with invalid id returns 404`() = runTest {
        val response = stubService.getRelease(
            "admin",
            "awesome-project",
            999L, // non-existent ID
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertFalse("Response should fail", response.isSuccessful)
        assertEquals("Should be 404", 404, response.code())
    }

    // Commit Tests

    @Test
    fun `test getCommits returns commits`() = runTest {
        val response = stubService.getCommits(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            sha = null,
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val commits = response.body()!!
        assertTrue("Should have commits", commits.isNotEmpty())
        assertTrue("Commits should have messages", commits.all { it.commit.message.isNotEmpty() })
    }

    @Test
    fun `test getCommits with pagination`() = runTest {
        val page1 = stubService.getCommits(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            sha = null,
            page = 1,
            limit = 2
        )

        assertTrue("Page 1 should be successful", page1.isSuccessful)
        assertEquals("Page 1 should have 2 items", 2, page1.body()!!.size)

        val page2 = stubService.getCommits(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            sha = null,
            page = 2,
            limit = 2
        )

        assertTrue("Page 2 should be successful", page2.isSuccessful)
        if (page2.body()!!.isNotEmpty()) {
            assertNotEquals("Pages should have different commits",
                page1.body()!!.first().sha, page2.body()!!.first().sha)
        }
    }

    // Pull Request Tests

    @Test
    fun `test getPullRequests returns PRs`() = runTest {
        val response = stubService.getPullRequests(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "all",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val prs = response.body()!!
        assertTrue("Should have PRs", prs.isNotEmpty())
    }

    @Test
    fun `test getPullRequests filters by state`() = runTest {
        val openResponse = stubService.getPullRequests(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "open",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", openResponse.isSuccessful)
        val openPRs = openResponse.body()!!
        assertTrue("All PRs should be open", openPRs.all { it.state == "open" })

        val closedResponse = stubService.getPullRequests(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            state = "closed",
            page = 1,
            limit = 50
        )

        assertTrue("Response should be successful", closedResponse.isSuccessful)
        val closedPRs = closedResponse.body()!!
        assertTrue("All PRs should be closed", closedPRs.all { it.state == "closed" })
    }

    @Test
    fun `test getPullRequest returns PR details`() = runTest {
        val response = stubService.getPullRequest(
            "admin",
            "awesome-project",
            1L,
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertNotNull("Body should not be null", response.body())

        val pr = response.body()!!
        assertEquals("Number should match", 1L, pr.number)
        assertNotNull("Title should not be null", pr.title)
        assertNotNull("Head branch should not be null", pr.head.ref)
        assertNotNull("Base branch should not be null", pr.base.ref)
    }

    @Test
    fun `test getPullRequest with invalid number returns 404`() = runTest {
        val response = stubService.getPullRequest(
            "admin",
            "awesome-project",
            999L,
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertFalse("Response should fail", response.isSuccessful)
        assertEquals("Should be 404", 404, response.code())
    }

    @Test
    fun `test createPullRequest creates new PR`() = runTest {
        val request = mapOf(
            "title" to "New feature PR",
            "body" to "This PR adds a new feature",
            "head" to "feature-branch",
            "base" to "main"
        )

        val response = stubService.createPullRequest(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER,
            request
        )

        assertTrue("Response should be successful", response.isSuccessful)
        assertEquals("Should be 201", 201, response.code())
        assertNotNull("Body should not be null", response.body())

        val pr = response.body()!!
        assertEquals("Title should match", "New feature PR", pr.title)
        assertEquals("Body should match", "This PR adds a new feature", pr.body)
        assertEquals("State should be open", "open", pr.state)
        assertEquals("Head should match", "feature-branch", pr.head.ref)
        assertEquals("Base should match", "main", pr.base.ref)
    }

    // Star Tests

    @Test
    fun `test starRepository increases star count`() = runTest {
        val beforeResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val beforeStars = beforeResponse.body()!!.starsCount

        val starResponse = stubService.starRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Star should be successful", starResponse.isSuccessful)
        assertEquals("Should be 204", 204, starResponse.code())

        val afterResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val afterStars = afterResponse.body()!!.starsCount

        assertEquals("Star count should increase", beforeStars + 1, afterStars)
    }

    @Test
    fun `test unstarRepository decreases star count`() = runTest {
        // First star the repo
        stubService.starRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )

        val beforeResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val beforeStars = beforeResponse.body()!!.starsCount

        val unstarResponse = stubService.unstarRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )

        assertTrue("Unstar should be successful", unstarResponse.isSuccessful)
        assertEquals("Should be 204", 204, unstarResponse.code())

        val afterResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val afterStars = afterResponse.body()!!.starsCount

        assertEquals("Star count should decrease", beforeStars - 1, afterStars)
    }

    @Test
    fun `test starRepository twice does not double count`() = runTest {
        val beforeResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val beforeStars = beforeResponse.body()!!.starsCount

        stubService.starRepository("admin", "awesome-project", GiteaTestData.TEST_AUTH_HEADER)
        stubService.starRepository("admin", "awesome-project", GiteaTestData.TEST_AUTH_HEADER)

        val afterResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        val afterStars = afterResponse.body()!!.starsCount

        assertEquals("Should only increase by 1", beforeStars + 1, afterStars)
    }

    // State Management Tests

    @Test
    fun `test resetState restores initial state`() = runTest {
        // Modify state
        val createRequest = mapOf(
            "name" to "temp-repo",
            "description" to "Temporary",
            "private" to false
        )
        stubService.createRepository(GiteaTestData.TEST_AUTH_HEADER, createRequest)

        // Reset state
        GiteaApiStubService.resetState()

        // Verify temp repo is gone
        val getResponse = stubService.getRepository(
            "admin",
            "temp-repo",
            GiteaTestData.TEST_AUTH_HEADER
        )
        assertFalse("Temp repo should not exist", getResponse.isSuccessful)

        // Verify test data is back
        val testRepoResponse = stubService.getRepository(
            "admin",
            "awesome-project",
            GiteaTestData.TEST_AUTH_HEADER
        )
        assertTrue("Test repo should exist", testRepoResponse.isSuccessful)
    }

    // Authentication Tests

    @Test
    fun `test authentication required when enabled`() = runTest {
        val authService = GiteaApiStubService(requireAuth = true)

        try {
            authService.getCurrentUser(GiteaTestData.TEST_AUTH_HEADER)
            fail("Should throw SecurityException")
        } catch (e: SecurityException) {
            assertTrue("Should be unauthorized", e.message!!.contains("Unauthorized"))
        }
    }

    @Test
    fun `test authentication succeeds after authenticate`() = runTest {
        val authService = GiteaApiStubService(requireAuth = true)
        GiteaApiStubService.authenticate()

        val response = authService.getCurrentUser(GiteaTestData.TEST_AUTH_HEADER)
        assertTrue("Request should succeed", response.isSuccessful)
    }
}
