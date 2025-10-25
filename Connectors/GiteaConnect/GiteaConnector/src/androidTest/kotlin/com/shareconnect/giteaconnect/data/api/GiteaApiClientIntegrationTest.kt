package com.shareconnect.giteaconnect.data.api

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.giteaconnect.data.model.*
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(AndroidJUnit4::class)
class GiteaApiClientIntegrationTest {

    private lateinit var mockWebServer: MockWebServer
    private lateinit var apiClient: GiteaApiClient
    private lateinit var apiService: GiteaApiService
    private val testToken = "test-token-123"

    @Before
    fun setup() {
        mockWebServer = MockWebServer()
        mockWebServer.start()

        val retrofit = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(GiteaApiService::class.java)
        apiClient = GiteaApiClient(mockWebServer.url("/").toString(), testToken, apiService)
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun getCurrentUser_returnsUser_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": 1,
                    "login": "testuser",
                    "full_name": "Test User",
                    "email": "test@example.com",
                    "avatar_url": "https://gitea.example.com/avatars/1",
                    "is_admin": false
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getCurrentUser()

        // Then
        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.login)
        assertEquals("Test User", user.fullName)
        assertEquals("test@example.com", user.email)
    }

    @Test
    fun getCurrentUser_returnsFailure_onNetworkError() = runTest {
        // Given
        mockWebServer.shutdown()

        // When
        val result = apiClient.getCurrentUser()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun getUserRepos_returnsRepositories_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id": 100,
                        "owner": {
                            "id": 1,
                            "login": "testuser",
                            "is_admin": false
                        },
                        "name": "test-repo",
                        "full_name": "testuser/test-repo",
                        "empty": false,
                        "private": false,
                        "fork": false,
                        "template": false,
                        "mirror": false,
                        "size": 1024000,
                        "html_url": "https://gitea.example.com/testuser/test-repo",
                        "ssh_url": "git@gitea.example.com:testuser/test-repo.git",
                        "clone_url": "https://gitea.example.com/testuser/test-repo.git",
                        "stars_count": 10,
                        "forks_count": 2,
                        "watchers_count": 5,
                        "open_issues_count": 3,
                        "open_pr_counter": 1,
                        "release_counter": 5,
                        "default_branch": "main",
                        "created_at": "2025-01-01T00:00:00Z",
                        "updated_at": "2025-01-10T00:00:00Z"
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getUserRepos()

        // Then
        assertTrue(result.isSuccess)
        val repos = result.getOrNull()!!
        assertEquals(1, repos.size)
        assertEquals("test-repo", repos[0].name)
        assertEquals(10, repos[0].starsCount)
    }

    @Test
    fun getRepository_returnsRepository_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                {
                    "id": 100,
                    "owner": {
                        "id": 1,
                        "login": "testuser",
                        "is_admin": false
                    },
                    "name": "test-repo",
                    "full_name": "testuser/test-repo",
                    "description": "A test repository",
                    "empty": false,
                    "private": false,
                    "fork": false,
                    "template": false,
                    "mirror": false,
                    "size": 1024000,
                    "html_url": "https://gitea.example.com/testuser/test-repo",
                    "ssh_url": "git@gitea.example.com:testuser/test-repo.git",
                    "clone_url": "https://gitea.example.com/testuser/test-repo.git",
                    "stars_count": 10,
                    "forks_count": 2,
                    "watchers_count": 5,
                    "open_issues_count": 3,
                    "open_pr_counter": 1,
                    "release_counter": 5,
                    "default_branch": "main",
                    "created_at": "2025-01-01T00:00:00Z",
                    "updated_at": "2025-01-10T00:00:00Z"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getRepository("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
        val repo = result.getOrNull()!!
        assertEquals("test-repo", repo.name)
        assertEquals("A test repository", repo.description)
    }

    @Test
    fun createRepository_returnsNewRepository_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("""
                {
                    "id": 101,
                    "owner": {
                        "id": 1,
                        "login": "testuser",
                        "is_admin": false
                    },
                    "name": "new-repo",
                    "full_name": "testuser/new-repo",
                    "empty": true,
                    "private": true,
                    "fork": false,
                    "template": false,
                    "mirror": false,
                    "size": 0,
                    "html_url": "https://gitea.example.com/testuser/new-repo",
                    "ssh_url": "git@gitea.example.com:testuser/new-repo.git",
                    "clone_url": "https://gitea.example.com/testuser/new-repo.git",
                    "stars_count": 0,
                    "forks_count": 0,
                    "watchers_count": 0,
                    "open_issues_count": 0,
                    "open_pr_counter": 0,
                    "release_counter": 0,
                    "default_branch": "main",
                    "created_at": "2025-01-10T00:00:00Z",
                    "updated_at": "2025-01-10T00:00:00Z"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createRepository("new-repo", "Test description", true)

        // Then
        assertTrue(result.isSuccess)
        val repo = result.getOrNull()!!
        assertEquals("new-repo", repo.name)
        assertTrue(repo.private)
    }

    @Test
    fun deleteRepository_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.deleteRepository("testuser", "old-repo")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun getIssues_returnsIssuesList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id": 1,
                        "url": "https://gitea.example.com/api/v1/repos/testuser/test-repo/issues/1",
                        "html_url": "https://gitea.example.com/testuser/test-repo/issues/1",
                        "number": 1,
                        "user": {
                            "id": 1,
                            "login": "testuser",
                            "is_admin": false
                        },
                        "title": "Bug: Application crashes",
                        "body": "Description of the bug",
                        "state": "open",
                        "comments": 5,
                        "created_at": "2025-01-01T00:00:00Z",
                        "updated_at": "2025-01-10T00:00:00Z"
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getIssues("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
        val issues = result.getOrNull()!!
        assertEquals(1, issues.size)
        assertEquals("Bug: Application crashes", issues[0].title)
        assertEquals("open", issues[0].state)
    }

    @Test
    fun createIssue_returnsNewIssue_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(201)
            .setBody("""
                {
                    "id": 2,
                    "url": "https://gitea.example.com/api/v1/repos/testuser/test-repo/issues/2",
                    "html_url": "https://gitea.example.com/testuser/test-repo/issues/2",
                    "number": 2,
                    "user": {
                        "id": 1,
                        "login": "testuser",
                        "is_admin": false
                    },
                    "title": "New issue",
                    "body": "Description",
                    "state": "open",
                    "comments": 0,
                    "created_at": "2025-01-10T00:00:00Z",
                    "updated_at": "2025-01-10T00:00:00Z"
                }
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.createIssue("testuser", "test-repo", "New issue", "Description")

        // Then
        assertTrue(result.isSuccess)
        val issue = result.getOrNull()!!
        assertEquals("New issue", issue.title)
    }

    @Test
    fun getReleases_returnsReleasesList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id": 1,
                        "tag_name": "v1.0.0",
                        "target_commitish": "main",
                        "name": "Version 1.0.0",
                        "url": "https://gitea.example.com/api/v1/repos/testuser/test-repo/releases/1",
                        "html_url": "https://gitea.example.com/testuser/test-repo/releases/tag/v1.0.0",
                        "tarball_url": "https://gitea.example.com/testuser/test-repo/archive/v1.0.0.tar.gz",
                        "zipball_url": "https://gitea.example.com/testuser/test-repo/archive/v1.0.0.zip",
                        "draft": false,
                        "prerelease": false,
                        "created_at": "2025-01-01T00:00:00Z",
                        "published_at": "2025-01-01T12:00:00Z",
                        "author": {
                            "id": 1,
                            "login": "testuser",
                            "is_admin": false
                        }
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getReleases("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
        val releases = result.getOrNull()!!
        assertEquals(1, releases.size)
        assertEquals("v1.0.0", releases[0].tagName)
    }

    @Test
    fun getCommits_returnsCommitsList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "url": "https://gitea.example.com/api/v1/repos/testuser/test-repo/git/commits/abc123",
                        "sha": "abc123def456",
                        "html_url": "https://gitea.example.com/testuser/test-repo/commit/abc123def456",
                        "commit": {
                            "message": "Fix critical bug",
                            "author": {
                                "name": "Test User",
                                "email": "test@example.com",
                                "date": "2025-01-01T00:00:00Z"
                            },
                            "committer": {
                                "name": "Test User",
                                "email": "test@example.com",
                                "date": "2025-01-01T00:00:00Z"
                            }
                        }
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getCommits("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
        val commits = result.getOrNull()!!
        assertEquals(1, commits.size)
        assertEquals("abc123def456", commits[0].sha)
        assertEquals("Fix critical bug", commits[0].commit.message)
    }

    @Test
    fun getPullRequests_returnsPRsList_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""
                [
                    {
                        "id": 1,
                        "url": "https://gitea.example.com/api/v1/repos/testuser/test-repo/pulls/1",
                        "number": 1,
                        "user": {
                            "id": 1,
                            "login": "contributor",
                            "is_admin": false
                        },
                        "title": "Add new feature",
                        "body": "This PR adds a new feature",
                        "state": "open",
                        "merged": false,
                        "mergeable": true,
                        "created_at": "2025-01-01T00:00:00Z",
                        "updated_at": "2025-01-05T00:00:00Z",
                        "head": {
                            "label": "contributor:feature-branch",
                            "ref": "feature-branch",
                            "sha": "abc123"
                        },
                        "base": {
                            "label": "testuser:main",
                            "ref": "main",
                            "sha": "def456"
                        }
                    }
                ]
            """.trimIndent())
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getPullRequests("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
        val prs = result.getOrNull()!!
        assertEquals(1, prs.size)
        assertEquals("Add new feature", prs[0].title)
        assertEquals("open", prs[0].state)
    }

    @Test
    fun starRepository_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.starRepository("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun unstarRepository_succeeds_onSuccessfulResponse() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(204)
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.unstarRepository("testuser", "test-repo")

        // Then
        assertTrue(result.isSuccess)
    }

    @Test
    fun httpError_returnsFailure() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(500)
            .setBody("Internal Server Error")
        mockWebServer.enqueue(mockResponse)

        // When
        val result = apiClient.getCurrentUser()

        // Then
        assertTrue(result.isFailure)
    }

    @Test
    fun authentication_includesTokenHeader() = runTest {
        // Given
        val mockResponse = MockResponse()
            .setResponseCode(200)
            .setBody("""{"id": 1, "login": "testuser", "is_admin": false}""")
        mockWebServer.enqueue(mockResponse)

        // When
        apiClient.getCurrentUser()

        // Then
        val request = mockWebServer.takeRequest()
        assertEquals("token $testToken", request.getHeader("Authorization"))
    }
}
