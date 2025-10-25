package com.shareconnect.giteaconnect.data.api

import com.shareconnect.giteaconnect.data.model.*
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import retrofit2.Response

/**
 * Unit tests for GiteaApiClient using MockK
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = com.shareconnect.giteaconnect.TestApplication::class)
class GiteaApiClientMockKTest {

    private lateinit var mockService: GiteaApiService
    private lateinit var apiClient: GiteaApiClient
    private val testServerUrl = "https://gitea.example.com"
    private val testToken = "test-token-123"

    @Before
    fun setUp() {
        mockService = mockk()
        apiClient = GiteaApiClient(testServerUrl, testToken, mockService)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `test API client initialization`() {
        assertNotNull(apiClient)
        assertEquals("token $testToken", apiClient.authHeader)
    }

    @Test
    fun `test get current user success`() = runBlocking {
        val mockUser = mockk<GiteaUser>(relaxed = true) {
            every { login } returns "testuser"
            every { fullName } returns "Test User"
        }

        coEvery { mockService.getCurrentUser(any()) } returns Response.success(mockUser)

        val result = apiClient.getCurrentUser()

        assertTrue(result.isSuccess)
        val user = result.getOrNull()!!
        assertEquals("testuser", user.login)
        coVerify { mockService.getCurrentUser(any()) }
    }

    @Test
    fun `test get user repos success`() = runBlocking {
        val mockRepos = listOf(
            mockk<GiteaRepository>(relaxed = true) {
                every { name } returns "test-repo"
                every { fullName } returns "testuser/test-repo"
            }
        )

        coEvery { mockService.getUserRepos(any(), any(), any()) } returns Response.success(mockRepos)

        val result = apiClient.getUserRepos()

        assertTrue(result.isSuccess)
        val repos = result.getOrNull()!!
        assertEquals(1, repos.size)
        assertEquals("test-repo", repos[0].name)
        coVerify { mockService.getUserRepos(any(), any(), any()) }
    }

    @Test
    fun `test get repository success`() = runBlocking {
        val mockRepo = mockk<GiteaRepository>(relaxed = true) {
            every { name } returns "test-repo"
            every { fullName } returns "testuser/test-repo"
        }

        coEvery { mockService.getRepository(any(), any(), any()) } returns Response.success(mockRepo)

        val result = apiClient.getRepository("testuser", "test-repo")

        assertTrue(result.isSuccess)
        assertEquals("test-repo", result.getOrNull()!!.name)
        coVerify { mockService.getRepository(any(), "testuser", "test-repo") }
    }

    @Test
    fun `test create repository success`() = runBlocking {
        val mockRepo = mockk<GiteaRepository>(relaxed = true) {
            every { name } returns "new-repo"
        }

        coEvery { mockService.createRepository(any(), any()) } returns Response.success(mockRepo)

        val result = apiClient.createRepository("new-repo", "Test description", true)

        assertTrue(result.isSuccess)
        assertEquals("new-repo", result.getOrNull()!!.name)
        coVerify { mockService.createRepository(any(), any()) }
    }

    @Test
    fun `test delete repository success`() = runBlocking {
        coEvery { mockService.deleteRepository(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.deleteRepository("testuser", "old-repo")

        assertTrue(result.isSuccess)
        coVerify { mockService.deleteRepository(any(), "testuser", "old-repo") }
    }

    @Test
    fun `test get issues success`() = runBlocking {
        val mockIssues = listOf(
            mockk<GiteaIssue>(relaxed = true) {
                every { title } returns "Test issue"
                every { state } returns "open"
            }
        )

        coEvery { mockService.getIssues(any(), any(), any(), any(), any(), any()) } returns Response.success(mockIssues)

        val result = apiClient.getIssues("testuser", "test-repo")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Test issue", result.getOrNull()!![0].title)
        coVerify { mockService.getIssues(any(), "testuser", "test-repo", any(), any(), any()) }
    }

    @Test
    fun `test create issue success`() = runBlocking {
        val mockIssue = mockk<GiteaIssue>(relaxed = true) {
            every { title } returns "New issue"
        }

        coEvery { mockService.createIssue(any(), any(), any(), any()) } returns Response.success(mockIssue)

        val result = apiClient.createIssue("testuser", "test-repo", "New issue", "Description")

        assertTrue(result.isSuccess)
        assertEquals("New issue", result.getOrNull()!!.title)
        coVerify { mockService.createIssue(any(), "testuser", "test-repo", any()) }
    }

    @Test
    fun `test get releases success`() = runBlocking {
        val mockReleases = listOf(
            mockk<GiteaRelease>(relaxed = true) {
                every { tagName } returns "v1.0.0"
            }
        )

        coEvery { mockService.getReleases(any(), any(), any(), any(), any()) } returns Response.success(mockReleases)

        val result = apiClient.getReleases("testuser", "test-repo")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("v1.0.0", result.getOrNull()!![0].tagName)
        coVerify { mockService.getReleases(any(), "testuser", "test-repo", any(), any()) }
    }

    @Test
    fun `test get commits success`() = runBlocking {
        val mockCommits = listOf(
            mockk<GiteaCommit>(relaxed = true) {
                every { sha } returns "abc123"
            }
        )

        coEvery { mockService.getCommits(any(), any(), any(), any(), any(), any()) } returns Response.success(mockCommits)

        val result = apiClient.getCommits("testuser", "test-repo")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("abc123", result.getOrNull()!![0].sha)
        coVerify { mockService.getCommits(any(), "testuser", "test-repo", any(), any(), any()) }
    }

    @Test
    fun `test get pull requests success`() = runBlocking {
        val mockPRs = listOf(
            mockk<GiteaPullRequest>(relaxed = true) {
                every { title } returns "Test PR"
            }
        )

        coEvery { mockService.getPullRequests(any(), any(), any(), any(), any(), any()) } returns Response.success(mockPRs)

        val result = apiClient.getPullRequests("testuser", "test-repo")

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()!!.size)
        assertEquals("Test PR", result.getOrNull()!![0].title)
        coVerify { mockService.getPullRequests(any(), "testuser", "test-repo", any(), any(), any()) }
    }

    @Test
    fun `test star repository success`() = runBlocking {
        coEvery { mockService.starRepository(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.starRepository("testuser", "test-repo")

        assertTrue(result.isSuccess)
        coVerify { mockService.starRepository(any(), "testuser", "test-repo") }
    }

    @Test
    fun `test unstar repository success`() = runBlocking {
        coEvery { mockService.unstarRepository(any(), any(), any()) } returns Response.success(Unit)

        val result = apiClient.unstarRepository("testuser", "test-repo")

        assertTrue(result.isSuccess)
        coVerify { mockService.unstarRepository(any(), "testuser", "test-repo") }
    }

    @Test
    fun `test HTTP error handling`() = runBlocking {
        coEvery { mockService.getCurrentUser(any()) } returns Response.error(500, mockk(relaxed = true))

        val result = apiClient.getCurrentUser()

        assertTrue(result.isFailure)
    }

    @Test
    fun `test exception handling`() = runBlocking {
        coEvery { mockService.getCurrentUser(any()) } throws RuntimeException("Network error")

        val result = apiClient.getCurrentUser()

        assertTrue(result.isFailure)
        assertTrue(result.exceptionOrNull() is RuntimeException)
    }
}
