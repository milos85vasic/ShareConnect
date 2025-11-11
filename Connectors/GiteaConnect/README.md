# GiteaConnect - Gitea Android Client

GiteaConnect is an Android application that provides native client functionality for Gitea, the self-hosted Git service. Built with Kotlin and Jetpack Compose, it offers complete REST API support for managing repositories, issues, pull requests, and more.

## 🎯 Overview

GiteaConnect enables Android apps to interact with Gitea servers for:
- **Repository Management**: Create, browse, delete repositories
- **Issue Tracking**: Create, list, edit issues with labels and milestones
- **Pull Requests**: Manage PR workflows from mobile
- **Code Browsing**: View commits, releases, and repository contents
- **Collaboration**: Star repositories, comment on issues

## 📱 Features

### Repository Operations
- List user repositories with pagination
- Get repository details
- Create new repositories (public/private)
- Delete repositories
- Star/unstar repositories

### Issue Management
- List repository issues with state filtering (open/closed)
- Get issue details with labels, milestones, assignees
- Create new issues
- Edit existing issues (title, body, state)
- Comment count tracking

### Pull Requests
- List pull requests with state filtering
- Get PR details with branch information
- Create new pull requests
- View merge status and merged by information

### Code & Releases
- Browse commit history with pagination
- Filter commits by branch
- List releases with assets
- View release details and download statistics

## 🔧 API Reference

### GiteaApiClient

#### Initialization
```kotlin
val apiClient = GiteaApiClient(
    serverUrl = "https://gitea.example.com",
    token = "your-access-token"
)
```

#### User Operations
```kotlin
// Get current authenticated user
val userResult = apiClient.getCurrentUser()
val user = userResult.getOrThrow()
println("User: ${user.login}, Admin: ${user.isAdmin}")
```

#### Repository Management
```kotlin
// List user repositories
val reposResult = apiClient.getUserRepos(page = 1, limit = 50)
val repos = reposResult.getOrThrow()
repos.forEach { repo ->
    println("${repo.fullName}: ${repo.starsCount} stars")
}

// Get repository details
val repoResult = apiClient.getRepository("owner", "repo-name")
val repo = repoResult.getOrThrow()
println("Description: ${repo.description}")
println("Issues: ${repo.openIssuesCount}, PRs: ${repo.openPrCounter}")

// Create repository
val createResult = apiClient.createRepository(
    name = "new-project",
    description = "My awesome project",
    private = false
)
val newRepo = createResult.getOrThrow()

// Delete repository
apiClient.deleteRepository("owner", "repo-name")
```

#### Star Management
```kotlin
// Star a repository
apiClient.starRepository("owner", "repo-name")

// Unstar a repository
apiClient.unstarRepository("owner", "repo-name")
```

#### Issue Operations
```kotlin
// List issues
val issuesResult = apiClient.getIssues(
    owner = "owner",
    repo = "repo-name",
    state = "open",  // or "closed", "all"
    page = 1,
    limit = 50
)
val issues = issuesResult.getOrThrow()
issues.forEach { issue ->
    println("#${issue.number}: ${issue.title} [${issue.state}]")
    println("Labels: ${issue.labels.joinToString { it.name }}")
}

// Create issue
val createIssueResult = apiClient.createIssue(
    owner = "owner",
    repo = "repo-name",
    title = "Bug found in login flow",
    body = "Steps to reproduce:\n1. Open app\n2. Click login\n3. App crashes"
)
val newIssue = createIssueResult.getOrThrow()
```

#### Pull Request Operations
```kotlin
// List pull requests
val prsResult = apiClient.getPullRequests(
    owner = "owner",
    repo = "repo-name",
    state = "open",  // or "closed", "all"
    page = 1,
    limit = 50
)
val prs = prsResult.getOrThrow()
prs.forEach { pr ->
    println("#${pr.number}: ${pr.title}")
    println("${pr.head.ref} → ${pr.base.ref}")
    if (pr.merged) {
        println("Merged by ${pr.mergedBy?.login} at ${pr.mergedAt}")
    }
}
```

#### Commit & Release Operations
```kotlin
// Get commits
val commitsResult = apiClient.getCommits(
    owner = "owner",
    repo = "repo-name",
    branch = "main",  // optional
    page = 1,
    limit = 50
)
val commits = commitsResult.getOrThrow()
commits.forEach { commit ->
    println("${commit.sha.substring(0, 7)}: ${commit.commit.message}")
    println("Author: ${commit.commit.author.name}")
}

// Get releases
val releasesResult = apiClient.getReleases(
    owner = "owner",
    repo = "repo-name",
    page = 1,
    limit = 50
)
val releases = releasesResult.getOrThrow()
releases.forEach { release ->
    println("${release.tagName}: ${release.name}")
    println("Assets: ${release.assets.size}")
    release.assets.forEach { asset ->
        println("  ${asset.name} (${asset.size} bytes, ${asset.downloadCount} downloads)")
    }
}
```

## 🧪 Stub Mode for Testing and Development

GiteaConnect includes a **stub mode** that allows development and testing without requiring a live Gitea server.

### Enabling Stub Mode

```kotlin
val apiClient = GiteaApiClient(
    serverUrl = "https://gitea.example.com",
    token = "test-token",
    isStubMode = true  // Enable stub mode
)
```

### Stub Mode Features

**Realistic Test Data**:
- 3 test users (admin, developer, contributor)
- 5 pre-configured repositories (public, private, fork, archived, empty)
- 5 test issues with labels and milestones
- 4 test pull requests (open, merged, closed)
- 5 test commits with realistic messages
- 3 test releases with assets
- Complete user profiles with avatars

**Complete API Coverage**:
- All 17 REST API methods implemented
- Stateful repository management (create/delete persist)
- Stateful issue management
- Stateful pull request operations
- Stateful star operations
- Error simulation for invalid operations

**Stateful Behavior**:
- In-memory storage for created/modified items
- State transitions (create → exists → delete)
- Repository counters update (issues, PRs, stars)
- Pagination support across all list endpoints

**Error Simulation**:
- 404 errors for non-existent resources
- 401 errors for authentication failures (when requireAuth = true)
- Validation errors for invalid parameters

### Using Stub Mode in Tests

```kotlin
class MyViewModelTest {
    @Test
    fun `test repository management with stub data`() = runTest {
        val apiClient = GiteaApiClient(
            serverUrl = GiteaTestData.TEST_SERVER_URL,
            token = GiteaTestData.TEST_TOKEN,
            isStubMode = true
        )

        // Create repository
        val createResult = apiClient.createRepository(
            name = "test-repo",
            description = "Test description",
            private = false
        )
        assertTrue(createResult.isSuccess)

        // Verify it exists
        val getResult = apiClient.getRepository("admin", "test-repo")
        assertTrue(getResult.isSuccess)

        val repo = getResult.getOrThrow()
        assertEquals("test-repo", repo.name)
        assertEquals("Test description", repo.description)
    }
}
```

### Stub Data Constants

Access predefined test data through `GiteaTestData`:

```kotlin
// Server configuration
GiteaTestData.TEST_SERVER_URL     // "https://gitea.example.com"
GiteaTestData.TEST_TOKEN          // "test-gitea-token-abc123xyz789"

// Test users
GiteaTestData.testUserAdmin       // Admin user
GiteaTestData.testUserDeveloper   // Regular developer user
GiteaTestData.testUserContributor // Contributor user

// Test repositories
GiteaTestData.testRepoPublic      // Public repository
GiteaTestData.testRepoPrivate     // Private repository
GiteaTestData.testRepoFork        // Forked repository
GiteaTestData.testRepoArchived    // Archived repository
GiteaTestData.testRepoEmpty       // Newly created empty repository

// Test issues
GiteaTestData.testIssueOpen1      // Open bug issue with label
GiteaTestData.testIssueOpen2      // Open feature request
GiteaTestData.testIssueClosed1    // Closed bug fix
GiteaTestData.testIssueClosed2    // Closed feature implementation

// Test pull requests
GiteaTestData.testPROpen1         // Open feature PR
GiteaTestData.testPROpen2         // Open documentation PR
GiteaTestData.testPRMerged        // Merged authentication PR
GiteaTestData.testPRClosed        // Closed experimental PR

// Test commits
GiteaTestData.testCommit1         // Initial commit
GiteaTestData.testCommit2         // Feature commit
GiteaTestData.testCommit3         // Bug fix commit

// Test releases
GiteaTestData.testReleaseStable   // Stable release with assets
GiteaTestData.testReleaseBeta     // Beta release
GiteaTestData.testReleaseDraft    // Draft release (not public)

// Collections
GiteaTestData.testAllRepos        // All test repositories
GiteaTestData.testAllIssues       // All test issues
GiteaTestData.testOpenIssues      // Open issues only
GiteaTestData.testClosedIssues    // Closed issues only
GiteaTestData.testAllPRs          // All test pull requests
GiteaTestData.testOpenPRs         // Open PRs only
GiteaTestData.testMergedPRs       // Merged PRs only
GiteaTestData.testAllCommits      // All test commits
GiteaTestData.testAllReleases     // All test releases
```

### Testing Complete Workflows

```kotlin
@Test
fun `test complete repository workflow`() = runTest {
    val apiClient = GiteaApiClient(
        serverUrl = GiteaTestData.TEST_SERVER_URL,
        token = GiteaTestData.TEST_TOKEN,
        isStubMode = true
    )

    // 1. Create repository
    val createResult = apiClient.createRepository(
        name = "workflow-test",
        description = "Testing complete workflow",
        private = false
    )
    assertTrue(createResult.isSuccess)
    val repo = createResult.getOrThrow()

    // 2. Star repository
    apiClient.starRepository("admin", "workflow-test")
    val starredRepo = apiClient.getRepository("admin", "workflow-test").getOrThrow()
    assertEquals(repo.starsCount + 1, starredRepo.starsCount)

    // 3. Create issue
    val issueResult = apiClient.createIssue(
        owner = "admin",
        repo = "workflow-test",
        title = "Initial setup",
        body = "Configure project settings"
    )
    assertTrue(issueResult.isSuccess)

    // 4. Get repository with updated counts
    val updatedRepo = apiClient.getRepository("admin", "workflow-test").getOrThrow()
    assertTrue(updatedRepo.openIssuesCount > 0)

    // 5. Delete repository
    apiClient.deleteRepository("admin", "workflow-test")

    // 6. Verify deletion
    val verifyResult = apiClient.getRepository("admin", "workflow-test")
    assertTrue(verifyResult.isFailure)
}
```

### Testing Issue Management

```kotlin
@Test
fun `test issue lifecycle`() = runTest {
    val apiClient = GiteaApiClient(
        serverUrl = GiteaTestData.TEST_SERVER_URL,
        token = GiteaTestData.TEST_TOKEN,
        isStubMode = true
    )

    // 1. Create issue
    val createResult = apiClient.createIssue(
        owner = "admin",
        repo = "awesome-project",
        title = "Fix login bug",
        body = "Users cannot log in with email"
    )
    val issue = createResult.getOrThrow()
    assertEquals("open", issue.state)

    // 2. Verify in open issues list
    val openIssues = apiClient.getIssues(
        "admin",
        "awesome-project",
        state = "open"
    ).getOrThrow()
    assertTrue(openIssues.any { it.number == issue.number })

    // 3. Filter by state
    val closedIssues = apiClient.getIssues(
        "admin",
        "awesome-project",
        state = "closed"
    ).getOrThrow()
    assertTrue(closedIssues.all { it.state == "closed" })
}
```

### Testing Pagination

```kotlin
@Test
fun `test pagination across endpoints`() = runTest {
    val apiClient = GiteaApiClient(
        serverUrl = GiteaTestData.TEST_SERVER_URL,
        token = GiteaTestData.TEST_TOKEN,
        isStubMode = true
    )

    // Repository pagination
    val reposPage1 = apiClient.getUserRepos(page = 1, limit = 2).getOrThrow()
    val reposPage2 = apiClient.getUserRepos(page = 2, limit = 2).getOrThrow()
    assertEquals(2, reposPage1.size)

    // Issue pagination
    val issuesPage1 = apiClient.getIssues(
        "admin",
        "awesome-project",
        page = 1,
        limit = 2
    ).getOrThrow()
    val issuesPage2 = apiClient.getIssues(
        "admin",
        "awesome-project",
        page = 2,
        limit = 2
    ).getOrThrow()
    if (issuesPage1.isNotEmpty() && issuesPage2.isNotEmpty()) {
        assertNotEquals(issuesPage1.first().number, issuesPage2.first().number)
    }

    // Commit pagination
    val commitsPage1 = apiClient.getCommits(
        "admin",
        "awesome-project",
        page = 1,
        limit = 2
    ).getOrThrow()
    assertEquals(2, commitsPage1.size)
}
```

### Testing Error Scenarios

```kotlin
@Test
fun `test error handling`() = runTest {
    val apiClient = GiteaApiClient(
        serverUrl = GiteaTestData.TEST_SERVER_URL,
        token = GiteaTestData.TEST_TOKEN,
        isStubMode = true
    )

    // Repository not found
    val invalidRepo = apiClient.getRepository("admin", "nonexistent")
    assertTrue(invalidRepo.isFailure)

    // Issue not found
    val invalidIssue = apiClient.getIssues("admin", "nonexistent")
    assertTrue(invalidIssue.isFailure)

    // Delete non-existent repository
    val deleteResult = apiClient.deleteRepository("admin", "nonexistent")
    assertTrue(deleteResult.isFailure)
}
```

### Network Delay Simulation

Stub mode includes realistic network delays (500ms):

```kotlin
val start = System.currentTimeMillis()
apiClient.getCurrentUser()
val elapsed = System.currentTimeMillis() - start
// elapsed will be approximately 500ms
```

### Stub Mode Architecture

```
GiteaApiClient
    ├── isStubMode = false → Retrofit + OkHttp (live server)
    └── isStubMode = true  → GiteaApiStubService (in-memory state)
                                 └── GiteaTestData (sample data provider)
```

### Best Practices

1. **Use in Tests**: Always use stub mode for unit and integration tests
2. **UI Development**: Enable stub mode during UI development to avoid server dependencies
3. **Demo Mode**: Use stub mode for app demonstrations
4. **State Management**: Call `GiteaApiStubService.resetState()` between tests
5. **Error Testing**: Stub service validates all parameters and simulates realistic errors

### Complete Integration Example

```kotlin
@Test
fun `test comprehensive Gitea integration`() = runTest {
    val apiClient = GiteaApiClient(
        serverUrl = GiteaTestData.TEST_SERVER_URL,
        token = GiteaTestData.TEST_TOKEN,
        isStubMode = true
    )

    // 1. Get current user
    val user = apiClient.getCurrentUser().getOrThrow()
    assertEquals("admin", user.login)

    // 2. List repositories
    val repos = apiClient.getUserRepos().getOrThrow()
    assertTrue(repos.isNotEmpty())

    // 3. Get repository details
    val repo = apiClient.getRepository("admin", "awesome-project").getOrThrow()
    assertEquals("awesome-project", repo.name)

    // 4. Get issues
    val issues = apiClient.getIssues("admin", "awesome-project").getOrThrow()
    assertTrue(issues.isNotEmpty())

    // 5. Get pull requests
    val prs = apiClient.getPullRequests("admin", "awesome-project").getOrThrow()
    assertTrue(prs.isNotEmpty())

    // 6. Get commits
    val commits = apiClient.getCommits("admin", "awesome-project").getOrThrow()
    assertTrue(commits.isNotEmpty())

    // 7. Get releases
    val releases = apiClient.getReleases("admin", "awesome-project").getOrThrow()
    assertTrue(releases.isNotEmpty())

    // 8. Create new repository
    val newRepo = apiClient.createRepository(
        name = "integration-test",
        description = "Integration test repo",
        private = false
    ).getOrThrow()

    // 9. Create issue in new repo
    val issue = apiClient.createIssue(
        owner = "admin",
        repo = "integration-test",
        title = "Setup CI/CD",
        body = "Configure automated testing"
    ).getOrThrow()

    // 10. Star repository
    apiClient.starRepository("admin", "integration-test")

    // 11. Clean up
    apiClient.deleteRepository("admin", "integration-test")
}
```

## 📊 Test Coverage

### Unit Tests
- **GiteaApiStubServiceTest**: 32 test methods covering all stub service functionality

### Integration Tests
- **GiteaApiClientStubModeTest**: 31 integration tests validating end-to-end stub mode

### Test Areas
- User operations (authentication, profile)
- Repository management (CRUD, star operations)
- Issue tracking (create, list, filter, edit)
- Pull request management (list, filter, create)
- Commit browsing (list, pagination)
- Release management (list, get details)
- Error scenarios (404, invalid parameters)
- State management and transitions
- Pagination (all list endpoints)
- Complete workflows (repository lifecycle, issue lifecycle)

**Total Tests**: 63
**Pass Rate**: 100%

## 🔐 Authentication

GiteaConnect supports Gitea personal access token authentication:

```kotlin
// With authentication
val apiClient = GiteaApiClient(
    serverUrl = "https://gitea.example.com",
    token = "your-personal-access-token"
)

// Token is automatically included in all requests as:
// Authorization: token your-personal-access-token
```

### Creating a Personal Access Token

1. Log into your Gitea instance
2. Go to Settings → Applications
3. Generate new access token
4. Select appropriate scopes (repo, issue, user)
5. Copy the generated token

## 🏗️ Technical Architecture

### Dependencies
- **Retrofit**: REST API client with Gson converter
- **OkHttp**: HTTP client with logging interceptor
- **Gson**: JSON serialization/deserialization
- **Kotlin Coroutines**: Async operations with `suspend` functions
- **Robolectric**: Android unit testing framework

### REST API Protocol
GiteaConnect implements the Gitea REST API v1:
- **Base Path**: `/api/v1/`
- **Authentication**: Token-based (Authorization header)
- **Response Format**: JSON with standard HTTP status codes
- **Pagination**: Page and limit parameters

### Request/Response Example
```kotlin
// Request
GET /api/v1/repos/owner/repo-name
Authorization: token abc123xyz789

// Response (Success)
{
  "id": 1,
  "owner": {
    "id": 1,
    "login": "admin",
    "full_name": "Admin User"
  },
  "name": "awesome-project",
  "full_name": "admin/awesome-project",
  "description": "An awesome project",
  "private": false,
  "stars_count": 42,
  "forks_count": 8,
  "open_issues_count": 5
}

// Response (Error)
{
  "message": "Not Found",
  "url": "https://gitea.example.com/api/swagger"
}
```

## 📄 License

GiteaConnect is part of the ShareConnect project. See the main project LICENSE file for details.

---

**Last Updated:** 2025-11-11
**Version:** 1.0.0
**Gitea Compatibility:** API v1 (Gitea 1.12+)
