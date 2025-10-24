package com.shareconnect.giteaconnect.data.api

import android.util.Log
import com.shareconnect.giteaconnect.data.model.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Gitea API client for repository management
 */
class GiteaApiClient(
    private val serverUrl: String,
    private val token: String
) {
    private val tag = "GiteaApiClient"

    private val authHeader: String
        get() = "token $token"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(serverUrl.removeSuffix("/") + "/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val apiService = retrofit.create(GiteaApiService::class.java)

    /**
     * Get current authenticated user
     */
    suspend fun getCurrentUser(): Result<GiteaUser> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCurrentUser(authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get user failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting current user", e)
            Result.failure(e)
        }
    }

    /**
     * Get user repositories
     */
    suspend fun getUserRepos(page: Int = 1, limit: Int = 50): Result<List<GiteaRepository>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getUserRepos(authHeader, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get repos failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting user repos", e)
            Result.failure(e)
        }
    }

    /**
     * Get repository details
     */
    suspend fun getRepository(owner: String, repo: String): Result<GiteaRepository> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getRepository(owner, repo, authHeader)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Get repo failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting repository", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new repository
     */
    suspend fun createRepository(name: String, description: String? = null, private: Boolean = false): Result<GiteaRepository> = withContext(Dispatchers.IO) {
        try {
            val body = mutableMapOf<String, Any>(
                "name" to name,
                "private" to private
            )
            description?.let { body["description"] = it }

            val response = apiService.createRepository(authHeader, body)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create repo failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating repository", e)
            Result.failure(e)
        }
    }

    /**
     * Delete a repository
     */
    suspend fun deleteRepository(owner: String, repo: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.deleteRepository(owner, repo, authHeader)
            if (response.isSuccessful || response.code() == 204) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Delete repo failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error deleting repository", e)
            Result.failure(e)
        }
    }

    /**
     * Get repository issues
     */
    suspend fun getIssues(owner: String, repo: String, state: String? = "open", page: Int = 1, limit: Int = 50): Result<List<GiteaIssue>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getIssues(owner, repo, authHeader, state, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get issues failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting issues", e)
            Result.failure(e)
        }
    }

    /**
     * Create a new issue
     */
    suspend fun createIssue(owner: String, repo: String, title: String, body: String? = null): Result<GiteaIssue> = withContext(Dispatchers.IO) {
        try {
            val issueBody = mutableMapOf<String, Any>("title" to title)
            body?.let { issueBody["body"] = it }

            val response = apiService.createIssue(owner, repo, authHeader, issueBody)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.failure(Exception("Empty response"))
            } else {
                Result.failure(Exception("Create issue failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error creating issue", e)
            Result.failure(e)
        }
    }

    /**
     * Get repository releases
     */
    suspend fun getReleases(owner: String, repo: String, page: Int = 1, limit: Int = 50): Result<List<GiteaRelease>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getReleases(owner, repo, authHeader, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get releases failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting releases", e)
            Result.failure(e)
        }
    }

    /**
     * Get repository commits
     */
    suspend fun getCommits(owner: String, repo: String, branch: String? = null, page: Int = 1, limit: Int = 50): Result<List<GiteaCommit>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getCommits(owner, repo, authHeader, branch, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get commits failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting commits", e)
            Result.failure(e)
        }
    }

    /**
     * Get pull requests
     */
    suspend fun getPullRequests(owner: String, repo: String, state: String? = "open", page: Int = 1, limit: Int = 50): Result<List<GiteaPullRequest>> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.getPullRequests(owner, repo, authHeader, state, page, limit)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) }
                    ?: Result.success(emptyList())
            } else {
                Result.failure(Exception("Get PRs failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error getting pull requests", e)
            Result.failure(e)
        }
    }

    /**
     * Star a repository
     */
    suspend fun starRepository(owner: String, repo: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.starRepository(owner, repo, authHeader)
            if (response.isSuccessful || response.code() == 204) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Star repo failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error starring repository", e)
            Result.failure(e)
        }
    }

    /**
     * Unstar a repository
     */
    suspend fun unstarRepository(owner: String, repo: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = apiService.unstarRepository(owner, repo, authHeader)
            if (response.isSuccessful || response.code() == 204) {
                Result.success(Unit)
            } else {
                Result.failure(Exception("Unstar repo failed: ${response.code()} ${response.message()}"))
            }
        } catch (e: Exception) {
            Log.e(tag, "Error unstarring repository", e)
            Result.failure(e)
        }
    }
}
