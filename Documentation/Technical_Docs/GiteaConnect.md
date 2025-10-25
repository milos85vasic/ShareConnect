# GiteaConnect

## Overview and Purpose

GiteaConnect is a specialized Android application that provides comprehensive integration with Gitea, the lightweight self-hosted Git service. It offers a modern mobile interface for managing repositories, issues, pull requests, releases, and commits. The app connects to Gitea instances via the Gitea REST API v1.

GiteaConnect extends the ShareConnect ecosystem by enabling sharing from code repositories to other connected services (download managers for release downloads, torrent clients for repository distribution, cloud storage for backups).

## Architecture and Components

GiteaConnect is built using Kotlin with Jetpack Compose for UI, following the MVVM architecture pattern with Repository pattern and REST API integration.

### Core Components
- **GiteaConnectApplication**: Main application class managing lifecycle and sync managers
- **DependencyContainer**: Handles dependency injection and service initialization
- **GiteaApiClient**: HTTP client for Gitea REST API communication
- **GiteaApiService**: Retrofit service interface for Gitea endpoints

### Data Layer
- **GiteaApiClient**: Manages all API communication with Gitea servers
- **Authentication**: Token-based authentication (API tokens or OAuth)
- **Data Models**: Repository, issue, pull request, release, commit, and user models

### UI Layer (Jetpack Compose)
- **MainActivity**: Primary application entry point with navigation
- **OnboardingActivity**: First-time setup and server configuration
- **RepositoryListScreen**: Repository browsing interface
- **RepositoryDetailScreen**: Detailed repository view with actions
- **IssueListScreen**: Issue management interface
- **SettingsScreen**: Application preferences and server management

### Sync Integration
GiteaConnect integrates with ShareConnect's sync ecosystem:
- **ThemeSyncManager**: Theme synchronization (port 8890)
- **ProfileSyncManager**: Server profile management (port 8900)
- **HistorySyncManager**: Repository access history (port 8910)
- **RSSSyncManager**: RSS feed integration for releases (port 8920)
- **BookmarkSyncManager**: Repository bookmark management (port 8930)
- **PreferencesSyncManager**: User preference synchronization (port 8940)
- **LanguageSyncManager**: Language settings sync (port 8950)

## API Reference

### GiteaApiClient

#### User Operations
```kotlin
/**
 * Get current authenticated user information
 * @return Result containing user details
 */
suspend fun getCurrentUser(): Result<GiteaUser>

/**
 * Get user by username
 * @param username Username to lookup
 * @return Result containing user details
 */
suspend fun getUser(username: String): Result<GiteaUser>

/**
 * Get list of users (admin only)
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing user list
 */
suspend fun listUsers(page: Int = 1, limit: Int = 50): Result<List<GiteaUser>>
```

#### Repository Operations
```kotlin
/**
 * Get repositories for current user
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing repository list
 */
suspend fun getUserRepos(page: Int = 1, limit: Int = 50): Result<List<GiteaRepository>>

/**
 * Get repository details
 * @param owner Repository owner username
 * @param repo Repository name
 * @return Result containing repository details
 */
suspend fun getRepository(owner: String, repo: String): Result<GiteaRepository>

/**
 * Create a new repository
 * @param name Repository name
 * @param description Optional description
 * @param private Whether repository is private (default false)
 * @return Result containing created repository
 */
suspend fun createRepository(
    name: String,
    description: String? = null,
    private: Boolean = false
): Result<GiteaRepository>

/**
 * Delete a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @return Result indicating success or failure
 */
suspend fun deleteRepository(owner: String, repo: String): Result<Unit>

/**
 * Fork a repository
 * @param owner Original repository owner
 * @param repo Original repository name
 * @param organization Optional organization to fork to
 * @return Result containing forked repository
 */
suspend fun forkRepository(
    owner: String,
    repo: String,
    organization: String? = null
): Result<GiteaRepository>

/**
 * Star a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @return Result indicating success or failure
 */
suspend fun starRepository(owner: String, repo: String): Result<Unit>

/**
 * Unstar a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @return Result indicating success or failure
 */
suspend fun unstarRepository(owner: String, repo: String): Result<Unit>

/**
 * Check if repository is starred by current user
 * @param owner Repository owner username
 * @param repo Repository name
 * @return Result with true if starred, false otherwise
 */
suspend fun isRepositoryStarred(owner: String, repo: String): Result<Boolean>
```

#### Issue Operations
```kotlin
/**
 * Get issues for a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @param state Filter by state (open, closed, all)
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing issue list
 */
suspend fun getIssues(
    owner: String,
    repo: String,
    state: String? = "open",
    page: Int = 1,
    limit: Int = 50
): Result<List<GiteaIssue>>

/**
 * Get issue details
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Issue number
 * @return Result containing issue details
 */
suspend fun getIssue(owner: String, repo: String, index: Int): Result<GiteaIssue>

/**
 * Create a new issue
 * @param owner Repository owner username
 * @param repo Repository name
 * @param title Issue title
 * @param body Issue description
 * @return Result containing created issue
 */
suspend fun createIssue(
    owner: String,
    repo: String,
    title: String,
    body: String
): Result<GiteaIssue>

/**
 * Close an issue
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Issue number
 * @return Result containing updated issue
 */
suspend fun closeIssue(owner: String, repo: String, index: Int): Result<GiteaIssue>

/**
 * Reopen a closed issue
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Issue number
 * @return Result containing updated issue
 */
suspend fun reopenIssue(owner: String, repo: String, index: Int): Result<GiteaIssue>

/**
 * Get issue comments
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Issue number
 * @return Result containing comment list
 */
suspend fun getIssueComments(
    owner: String,
    repo: String,
    index: Int
): Result<List<GiteaComment>>

/**
 * Add comment to issue
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Issue number
 * @param body Comment text
 * @return Result containing created comment
 */
suspend fun addIssueComment(
    owner: String,
    repo: String,
    index: Int,
    body: String
): Result<GiteaComment>
```

#### Pull Request Operations
```kotlin
/**
 * Get pull requests for a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @param state Filter by state (open, closed, all)
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing pull request list
 */
suspend fun getPullRequests(
    owner: String,
    repo: String,
    state: String? = "open",
    page: Int = 1,
    limit: Int = 50
): Result<List<GiteaPullRequest>>

/**
 * Get pull request details
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Pull request number
 * @return Result containing pull request details
 */
suspend fun getPullRequest(
    owner: String,
    repo: String,
    index: Int
): Result<GiteaPullRequest>

/**
 * Create a pull request
 * @param owner Repository owner username
 * @param repo Repository name
 * @param title Pull request title
 * @param head Source branch
 * @param base Target branch
 * @param body Pull request description
 * @return Result containing created pull request
 */
suspend fun createPullRequest(
    owner: String,
    repo: String,
    title: String,
    head: String,
    base: String,
    body: String? = null
): Result<GiteaPullRequest>

/**
 * Merge a pull request
 * @param owner Repository owner username
 * @param repo Repository name
 * @param index Pull request number
 * @param mergeMethod Merge method (merge, rebase, squash)
 * @return Result indicating success or failure
 */
suspend fun mergePullRequest(
    owner: String,
    repo: String,
    index: Int,
    mergeMethod: String = "merge"
): Result<Unit>
```

#### Release Operations
```kotlin
/**
 * Get releases for a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing release list
 */
suspend fun getReleases(
    owner: String,
    repo: String,
    page: Int = 1,
    limit: Int = 50
): Result<List<GiteaRelease>>

/**
 * Get release by tag
 * @param owner Repository owner username
 * @param repo Repository name
 * @param tag Release tag
 * @return Result containing release details
 */
suspend fun getReleaseByTag(
    owner: String,
    repo: String,
    tag: String
): Result<GiteaRelease>

/**
 * Create a new release
 * @param owner Repository owner username
 * @param repo Repository name
 * @param tagName Git tag for release
 * @param name Release name
 * @param body Release notes
 * @param draft Is draft release (default false)
 * @param prerelease Is pre-release (default false)
 * @return Result containing created release
 */
suspend fun createRelease(
    owner: String,
    repo: String,
    tagName: String,
    name: String,
    body: String? = null,
    draft: Boolean = false,
    prerelease: Boolean = false
): Result<GiteaRelease>

/**
 * Delete a release
 * @param owner Repository owner username
 * @param repo Repository name
 * @param releaseId Release ID
 * @return Result indicating success or failure
 */
suspend fun deleteRelease(owner: String, repo: String, releaseId: Long): Result<Unit>
```

#### Commit Operations
```kotlin
/**
 * Get commits for a repository
 * @param owner Repository owner username
 * @param repo Repository name
 * @param branch Optional branch name
 * @param page Page number (default 1)
 * @param limit Items per page (default 50)
 * @return Result containing commit list
 */
suspend fun getCommits(
    owner: String,
    repo: String,
    branch: String? = null,
    page: Int = 1,
    limit: Int = 50
): Result<List<GiteaCommit>>

/**
 * Get commit details
 * @param owner Repository owner username
 * @param repo Repository name
 * @param sha Commit SHA
 * @return Result containing commit details
 */
suspend fun getCommit(owner: String, repo: String, sha: String): Result<GiteaCommit>
```

## Key Classes and Their Responsibilities

### GiteaConnectApplication
- **Responsibilities**:
  - Application lifecycle management
  - Dependency injection container initialization
  - Sync manager initialization and coordination
  - Global coroutine scope management
  - Application-wide error handling

### GiteaApiClient
- **Responsibilities**:
  - HTTP client management for Gitea API communication
  - Token-based authentication header generation
  - API request/response serialization
  - Error handling and retry logic
  - Connection pooling and timeout management
  - Pagination support

### GiteaApiService
- **Responsibilities**:
  - Retrofit service interface definitions
  - HTTP method annotations for REST endpoints
  - Request/response type declarations
  - Path and query parameter handling

## Data Models

### User Models

#### GiteaUser
```kotlin
data class GiteaUser(
    val id: Long,                            // User ID
    val login: String,                       // Username
    val fullName: String?,                   // Full display name
    val email: String?,                      // Email address
    val avatarUrl: String?,                  // Avatar image URL
    val isAdmin: Boolean,                    // Admin status
    val created: String?,                    // Account creation date
    val restricted: Boolean?                 // Restricted account flag
)
```

### Repository Models

#### GiteaRepository
```kotlin
data class GiteaRepository(
    val id: Long,                            // Repository ID
    val owner: GiteaUser,                    // Repository owner
    val name: String,                        // Repository name
    val fullName: String,                    // Full name (owner/repo)
    val description: String?,                // Repository description
    val empty: Boolean,                      // Is repository empty
    val private: Boolean,                    // Is private repository
    val fork: Boolean,                       // Is forked repository
    val template: Boolean,                   // Is template repository
    val parent: GiteaRepository?,            // Parent if forked
    val mirror: Boolean,                     // Is mirror repository
    val size: Long,                          // Repository size in KB
    val htmlUrl: String,                     // Web URL
    val sshUrl: String,                      // SSH clone URL
    val cloneUrl: String,                    // HTTPS clone URL
    val website: String?,                    // Website URL
    val starsCount: Int,                     // Number of stars
    val forksCount: Int,                     // Number of forks
    val watchersCount: Int,                  // Number of watchers
    val openIssuesCount: Int,                // Number of open issues
    val openPrCounter: Int,                  // Number of open pull requests
    val releaseCounter: Int,                 // Number of releases
    val defaultBranch: String?,              // Default branch name
    val createdAt: String,                   // Creation timestamp
    val updatedAt: String                    // Last update timestamp
)
```

### Issue Models

#### GiteaIssue
```kotlin
data class GiteaIssue(
    val id: Long,                            // Issue ID
    val url: String,                         // API URL
    val htmlUrl: String,                     // Web URL
    val number: Long,                        // Issue number
    val user: GiteaUser,                     // Issue creator
    val title: String,                       // Issue title
    val body: String?,                       // Issue description
    val labels: List<GiteaLabel>?,           // Issue labels
    val milestone: GiteaMilestone?,          // Associated milestone
    val assignees: List<GiteaUser>?,         // Assigned users
    val state: String,                       // State: open, closed
    val comments: Int,                       // Number of comments
    val createdAt: String,                   // Creation timestamp
    val updatedAt: String,                   // Last update timestamp
    val closedAt: String?                    // Closed timestamp
)
```

#### GiteaLabel
```kotlin
data class GiteaLabel(
    val id: Long,                            // Label ID
    val name: String,                        // Label name
    val color: String,                       // Hex color code
    val description: String?                 // Label description
)
```

#### GiteaMilestone
```kotlin
data class GiteaMilestone(
    val id: Long,                            // Milestone ID
    val title: String,                       // Milestone title
    val description: String?,                // Milestone description
    val state: String,                       // State: open, closed
    val dueDate: String?,                    // Due date
    val closedAt: String?                    // Closed timestamp
)
```

### Pull Request Models

#### GiteaPullRequest
```kotlin
data class GiteaPullRequest(
    val id: Long,                            // Pull request ID
    val url: String,                         // API URL
    val htmlUrl: String,                     // Web URL
    val number: Long,                        // Pull request number
    val user: GiteaUser,                     // Pull request creator
    val title: String,                       // Pull request title
    val body: String?,                       // Pull request description
    val labels: List<GiteaLabel>?,           // Pull request labels
    val milestone: GiteaMilestone?,          // Associated milestone
    val assignees: List<GiteaUser>?,         // Assigned reviewers
    val state: String,                       // State: open, closed
    val merged: Boolean,                     // Is merged
    val mergeable: Boolean,                  // Can be merged
    val mergedAt: String?,                   // Merge timestamp
    val mergedBy: GiteaUser?,                // User who merged
    val head: PullRequestBranch,             // Source branch
    val base: PullRequestBranch,             // Target branch
    val createdAt: String,                   // Creation timestamp
    val updatedAt: String                    // Last update timestamp
) {
    data class PullRequestBranch(
        val label: String,                   // Branch label
        val ref: String,                     // Branch ref
        val sha: String,                     // Commit SHA
        val repoId: Long?,                   // Repository ID
        val repo: GiteaRepository?           // Repository details
    )
}
```

### Release Models

#### GiteaRelease
```kotlin
data class GiteaRelease(
    val id: Long,                            // Release ID
    val tagName: String,                     // Git tag
    val targetCommitish: String,             // Target branch/commit
    val name: String,                        // Release name
    val body: String?,                       // Release notes
    val url: String,                         // API URL
    val htmlUrl: String,                     // Web URL
    val tarballUrl: String,                  // Tarball download URL
    val zipballUrl: String,                  // Zipball download URL
    val draft: Boolean,                      // Is draft
    val prerelease: Boolean,                 // Is pre-release
    val createdAt: String,                   // Creation timestamp
    val publishedAt: String?,                // Publish timestamp
    val author: GiteaUser,                   // Release author
    val assets: List<ReleaseAsset>?          // Release assets/files
) {
    data class ReleaseAsset(
        val id: Long,                        // Asset ID
        val name: String,                    // File name
        val size: Long,                      // File size in bytes
        val downloadCount: Int,              // Download count
        val createdAt: String,               // Upload timestamp
        val uuid: String,                    // Unique identifier
        val browserDownloadUrl: String       // Download URL
    )
}
```

### Commit Models

#### GiteaCommit
```kotlin
data class GiteaCommit(
    val url: String,                         // API URL
    val sha: String,                         // Commit SHA
    val htmlUrl: String,                     // Web URL
    val commit: CommitDetails,               // Commit details
    val author: GiteaUser?,                  // Commit author (if user)
    val committer: GiteaUser?,               // Committer (if user)
    val parents: List<CommitParent>?         // Parent commits
) {
    data class CommitDetails(
        val message: String,                 // Commit message
        val author: CommitSignature,         // Author signature
        val committer: CommitSignature       // Committer signature
    )

    data class CommitSignature(
        val name: String,                    // Name
        val email: String,                   // Email
        val date: String                     // Timestamp
    )

    data class CommitParent(
        val url: String,                     // Parent API URL
        val sha: String                      // Parent SHA
    )
}
```

### Comment Models

#### GiteaComment
```kotlin
data class GiteaComment(
    val id: Long,                            // Comment ID
    val htmlUrl: String,                     // Web URL
    val issueUrl: String,                    // Issue URL
    val user: GiteaUser,                     // Comment author
    val body: String,                        // Comment text
    val createdAt: String,                   // Creation timestamp
    val updatedAt: String                    // Last update timestamp
)
```

## Usage Examples

### Initializing GiteaConnect

```kotlin
class GiteaConnectApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // Initialize dependency container
        val container = DependencyContainer(this)

        // Initialize sync managers
        val themeSyncManager = ThemeSyncManager.getInstance(
            this,
            "com.shareconnect.giteaconnect",
            "GiteaConnect",
            "1.0.0"
        )
        themeSyncManager.startSync()

        val profileSyncManager = ProfileSyncManager.getInstance(
            this,
            "com.shareconnect.giteaconnect",
            "GiteaConnect",
            "1.0.0"
        )
        profileSyncManager.startSync()

        // Additional sync managers...
    }
}
```

### Connecting to Gitea Server

```kotlin
// Create API client with token
val apiClient = GiteaApiClient(
    serverUrl = "https://gitea.example.com",
    token = "your-api-token-here"
)

// Verify authentication and get user info
val userResult = apiClient.getCurrentUser()
if (userResult.isSuccess) {
    val user = userResult.getOrNull()!!
    println("Logged in as: ${user.fullName} (@${user.login})")
    println("Email: ${user.email}")
    println("Admin: ${user.isAdmin}")
} else {
    println("Authentication failed: ${userResult.exceptionOrNull()?.message}")
}
```

### Browsing Repositories

```kotlin
// Get user's repositories
val reposResult = apiClient.getUserRepos(page = 1, limit = 50)

if (reposResult.isSuccess) {
    val repos = reposResult.getOrNull()!!

    repos.forEach { repo ->
        println("📁 ${repo.fullName}")
        println("   ${repo.description ?: "No description"}")
        println("   ⭐ ${repo.starsCount} | 🍴 ${repo.forksCount} | 👁 ${repo.watchersCount}")
        println("   Issues: ${repo.openIssuesCount} | PRs: ${repo.openPrCounter}")
        println("   ${repo.htmlUrl}")
        println()
    }
}

// Get specific repository details
val repoResult = apiClient.getRepository("username", "repo-name")
if (repoResult.isSuccess) {
    val repo = repoResult.getOrNull()!!
    println("Clone URL: ${repo.cloneUrl}")
    println("SSH URL: ${repo.sshUrl}")
    println("Default branch: ${repo.defaultBranch}")
}
```

### Managing Repositories

```kotlin
// Create a new repository
val createResult = apiClient.createRepository(
    name = "my-awesome-project",
    description = "An awesome project built with Kotlin",
    private = false
)

if (createResult.isSuccess) {
    val newRepo = createResult.getOrNull()!!
    println("Repository created: ${newRepo.htmlUrl}")
    println("Clone it: git clone ${newRepo.cloneUrl}")
}

// Star a repository
val starResult = apiClient.starRepository("owner", "repo-name")

// Check if starred
val isStarredResult = apiClient.isRepositoryStarred("owner", "repo-name")
if (isStarredResult.isSuccess) {
    val isStarred = isStarredResult.getOrNull()!!
    println("Starred: $isStarred")
}

// Fork a repository
val forkResult = apiClient.forkRepository("original-owner", "original-repo")
```

### Working with Issues

```kotlin
// Get open issues
val issuesResult = apiClient.getIssues(
    owner = "username",
    repo = "repo-name",
    state = "open"
)

if (issuesResult.isSuccess) {
    val issues = issuesResult.getOrNull()!!

    issues.forEach { issue ->
        println("#${issue.number}: ${issue.title}")
        println("   State: ${issue.state} | Comments: ${issue.comments}")
        println("   Created by @${issue.user.login}")
        issue.labels?.forEach { label ->
            println("   🏷 ${label.name}")
        }
        println()
    }
}

// Create a new issue
val newIssueResult = apiClient.createIssue(
    owner = "username",
    repo = "repo-name",
    title = "Bug: Application crashes on startup",
    body = """
        ## Description
        The application crashes immediately after launching.

        ## Steps to Reproduce
        1. Launch the app
        2. Observe crash

        ## Expected Behavior
        App should start normally

        ## Environment
        - Android 14
        - Device: Pixel 7
    """.trimIndent()
)

if (newIssueResult.isSuccess) {
    val issue = newIssueResult.getOrNull()!!
    println("Issue created: ${issue.htmlUrl}")
}

// Add comment to issue
val commentResult = apiClient.addIssueComment(
    owner = "username",
    repo = "repo-name",
    index = 42,
    body = "I can reproduce this on my device as well."
)

// Close an issue
val closeResult = apiClient.closeIssue(
    owner = "username",
    repo = "repo-name",
    index = 42
)
```

### Working with Pull Requests

```kotlin
// Get open pull requests
val prsResult = apiClient.getPullRequests(
    owner = "username",
    repo = "repo-name",
    state = "open"
)

if (prsResult.isSuccess) {
    val prs = prsResult.getOrNull()!!

    prs.forEach { pr ->
        println("PR #${pr.number}: ${pr.title}")
        println("   ${pr.head.label} → ${pr.base.label}")
        println("   Mergeable: ${pr.mergeable} | Merged: ${pr.merged}")
        println("   ${pr.htmlUrl}")
        println()
    }
}

// Create a pull request
val prResult = apiClient.createPullRequest(
    owner = "username",
    repo = "repo-name",
    title = "feat: Add dark mode support",
    head = "feature/dark-mode",
    base = "main",
    body = """
        ## Changes
        - Added dark theme colors
        - Implemented theme switcher
        - Updated all screens to support dark mode

        ## Screenshots
        [Include screenshots here]
    """.trimIndent()
)

// Merge a pull request
val mergeResult = apiClient.mergePullRequest(
    owner = "username",
    repo = "repo-name",
    index = 123,
    mergeMethod = "squash"  // or "merge", "rebase"
)
```

### Working with Releases

```kotlin
// Get all releases
val releasesResult = apiClient.getReleases("username", "repo-name")

if (releasesResult.isSuccess) {
    val releases = releasesResult.getOrNull()!!

    releases.forEach { release ->
        println("${release.name} (${release.tagName})")
        println("   Draft: ${release.draft} | Pre-release: ${release.prerelease}")
        println("   📦 ${release.assets?.size ?: 0} assets")

        release.assets?.forEach { asset ->
            val sizeMB = asset.size / 1_000_000.0
            println("      - ${asset.name} (${String.format("%.2f", sizeMB)} MB)")
            println("        Downloads: ${asset.downloadCount}")
            println("        URL: ${asset.browserDownloadUrl}")
        }
        println()
    }
}

// Create a new release
val releaseResult = apiClient.createRelease(
    owner = "username",
    repo = "repo-name",
    tagName = "v1.0.0",
    name = "Version 1.0.0 - Initial Release",
    body = """
        # What's New
        - Initial release
        - Core functionality implemented
        - Android support

        # Downloads
        See assets below for APK downloads
    """.trimIndent(),
    draft = false,
    prerelease = false
)
```

### Viewing Commits

```kotlin
// Get recent commits
val commitsResult = apiClient.getCommits(
    owner = "username",
    repo = "repo-name",
    branch = "main",
    limit = 20
)

if (commitsResult.isSuccess) {
    val commits = commitsResult.getOrNull()!!

    commits.forEach { commit ->
        println("${commit.sha.substring(0, 7)}: ${commit.commit.message}")
        println("   Author: ${commit.commit.author.name} <${commit.commit.author.email}>")
        println("   Date: ${commit.commit.author.date}")
        println()
    }
}

// Get specific commit details
val commitResult = apiClient.getCommit(
    owner = "username",
    repo = "repo-name",
    sha = "abc123def456"
)
```

### Complete Workflow Example

```kotlin
suspend fun developmentWorkflow() {
    val apiClient = GiteaApiClient(
        serverUrl = "https://gitea.example.com",
        token = "api-token"
    )

    // 1. Create new repository
    val repo = apiClient.createRepository(
        name = "my-app",
        description = "My awesome application",
        private = false
    ).getOrThrow()

    println("Repository created: ${repo.cloneUrl}")

    // 2. Star your own repo (why not!)
    apiClient.starRepository(repo.owner.login, repo.name)

    // 3. Create first issue
    val issue = apiClient.createIssue(
        owner = repo.owner.login,
        repo = repo.name,
        title = "Setup initial project structure",
        body = "Initialize project with basic setup"
    ).getOrThrow()

    println("Issue #${issue.number} created")

    // 4. Simulate development and create PR
    // (In real app, this would happen after git push)
    val pr = apiClient.createPullRequest(
        owner = repo.owner.login,
        repo = repo.name,
        title = "Initial setup",
        head = "develop",
        base = "main",
        body = "Closes #${issue.number}"
    ).getOrThrow()

    println("Pull request #${pr.number} created")

    // 5. Merge PR
    apiClient.mergePullRequest(
        owner = repo.owner.login,
        repo = repo.name,
        index = pr.number.toInt(),
        mergeMethod = "squash"
    )

    println("PR merged!")

    // 6. Close the issue
    apiClient.closeIssue(
        owner = repo.owner.login,
        repo = repo.name,
        index = issue.number.toInt()
    )

    // 7. Create release
    val release = apiClient.createRelease(
        owner = repo.owner.login,
        repo = repo.name,
        tagName = "v0.1.0",
        name = "Initial Alpha Release",
        body = "First alpha version",
        prerelease = true
    ).getOrThrow()

    println("Release created: ${release.htmlUrl}")
}
```

## Dependencies

### Core Android
```gradle
implementation("androidx.core:core-ktx:1.12.0")
implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.0")
implementation("androidx.activity:activity-compose:1.9.0")
```

### Jetpack Compose
```gradle
implementation(platform("androidx.compose:compose-bom:2025.09.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.ui:ui-graphics")
implementation("androidx.compose.material3:material3")
implementation("androidx.navigation:navigation-compose:2.8.0")
```

### Networking
```gradle
implementation("com.squareup.retrofit2:retrofit:2.11.0")
implementation("com.squareup.retrofit2:converter-gson:2.11.0")
implementation("com.squareup.okhttp3:okhttp:4.12.0")
implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
```

### Serialization
```gradle
implementation("com.google.code.gson:gson:2.10.1")
```

### ShareConnect Modules
```gradle
implementation(project(":Asinka:asinka"))
implementation(project(":ThemeSync"))
implementation(project(":ProfileSync"))
implementation(project(":HistorySync"))
implementation(project(":RSSSync"))
implementation(project(":BookmarkSync"))
implementation(project(":PreferencesSync"))
implementation(project(":LanguageSync"))
implementation(project(":DesignSystem"))
implementation(project(":Onboarding"))
implementation(project(":Toolkit:Main"))
```

### Testing
```gradle
testImplementation("junit:junit:4.13.2")
testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.7.3")
testImplementation("io.mockk:mockk:1.13.8")
testImplementation("org.robolectric:robolectric:4.11.1")
testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")

androidTestImplementation("androidx.test.ext:junit:1.2.1")
androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
androidTestImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
```

## Security Considerations

### Authentication
- **API Tokens**: Secure token-based authentication
- **OAuth Support**: Optional OAuth 2.0 authentication
- **Token Storage**: Tokens encrypted in local storage
- **Scope Management**: Proper API scope permissions

### Network Security
- **HTTPS Required**: Production instances must use HTTPS
- **Certificate Validation**: Proper SSL/TLS certificate checking
- **Request Signing**: All requests signed with auth token

## Performance Optimization

### API Efficiency
- **Pagination**: All list endpoints support pagination
- **Caching**: ETag support for conditional requests
- **Batch Operations**: Multiple related operations batched
- **Connection Pooling**: HTTP connection reuse

### Data Management
- **Lazy Loading**: Load data on demand
- **Background Sync**: Periodic repository updates
- **Local Caching**: Reduce API calls with local cache

## Testing

GiteaConnect includes comprehensive test coverage:

### Unit Tests (28 tests)
- **GiteaApiClientMockKTest**: API client tests with MockK
- **GiteaModelsTest**: Data model serialization tests

### Integration Tests (15 tests)
- **GiteaApiClientIntegrationTest**: Full API integration tests

### Automation Tests (6 tests)
- **GiteaConnectAutomationTest**: App launch and lifecycle tests

**Total Coverage**: 49 tests

## Gitea API Features

### Supported Operations
- ✅ User management
- ✅ Repository CRUD
- ✅ Issues and comments
- ✅ Pull requests
- ✅ Releases and assets
- ✅ Commits and branches
- ✅ Stars and forks

### Not Yet Implemented
- ⏳ Webhooks
- ⏳ Organization management
- ⏳ Teams
- ⏳ GPG keys
- ⏳ SSH keys
- ⏳ Notifications
- ⏳ Search API

## Known Limitations

### API Version
- **Gitea 1.18+**: Recommended minimum version
- **Breaking Changes**: Some endpoints changed in Gitea 1.20+
- **Feature Detection**: Check server version for feature support

### Pagination
- **Max Page Size**: 50 items (Gitea default)
- **No Cursor Pagination**: Uses offset-based pagination
- **Total Count**: Not always provided in response headers

## Future Enhancements

- **Code browser**: View repository files and code
- **Diff viewer**: Visual diff comparison
- **Git operations**: Clone, pull, push from app
- **Notifications**: Real-time push notifications
- **Organization support**: Full org management
- **Wiki editing**: Repository wiki management
- **Projects/Kanban**: Project board support

---

**Last Updated**: 2025-10-25
**Version**: 1.0.0
**Gitea Compatibility**: 1.18.0+
**Android API**: 28-36
