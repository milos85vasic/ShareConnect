package com.shareconnect.giteaconnect.data.api

import com.shareconnect.giteaconnect.data.model.*
import retrofit2.Response
import retrofit2.http.*

/**
 * Gitea REST API service interface
 */
interface GiteaApiService {

    /**
     * Get authenticated user information
     */
    @GET("api/v1/user")
    suspend fun getCurrentUser(
        @Header("Authorization") authorization: String
    ): Response<GiteaUser>

    /**
     * Get user repositories
     */
    @GET("api/v1/user/repos")
    suspend fun getUserRepos(
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<GiteaRepository>>

    /**
     * Get repository details
     */
    @GET("api/v1/repos/{owner}/{repo}")
    suspend fun getRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String
    ): Response<GiteaRepository>

    /**
     * Create a new repository
     */
    @POST("api/v1/user/repos")
    suspend fun createRepository(
        @Header("Authorization") authorization: String,
        @Body repository: Map<String, Any>
    ): Response<GiteaRepository>

    /**
     * Delete a repository
     */
    @DELETE("api/v1/repos/{owner}/{repo}")
    suspend fun deleteRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    /**
     * Get repository issues
     */
    @GET("api/v1/repos/{owner}/{repo}/issues")
    suspend fun getIssues(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Query("state") state: String? = null, // open, closed, all
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<GiteaIssue>>

    /**
     * Get specific issue
     */
    @GET("api/v1/repos/{owner}/{repo}/issues/{index}")
    suspend fun getIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("index") index: Long,
        @Header("Authorization") authorization: String
    ): Response<GiteaIssue>

    /**
     * Create a new issue
     */
    @POST("api/v1/repos/{owner}/{repo}/issues")
    suspend fun createIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Body issue: Map<String, Any>
    ): Response<GiteaIssue>

    /**
     * Edit an issue
     */
    @PATCH("api/v1/repos/{owner}/{repo}/issues/{index}")
    suspend fun editIssue(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("index") index: Long,
        @Header("Authorization") authorization: String,
        @Body updates: Map<String, Any>
    ): Response<GiteaIssue>

    /**
     * Get repository releases
     */
    @GET("api/v1/repos/{owner}/{repo}/releases")
    suspend fun getReleases(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<GiteaRelease>>

    /**
     * Get specific release
     */
    @GET("api/v1/repos/{owner}/{repo}/releases/{id}")
    suspend fun getRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("id") id: Long,
        @Header("Authorization") authorization: String
    ): Response<GiteaRelease>

    /**
     * Get repository commits
     */
    @GET("api/v1/repos/{owner}/{repo}/commits")
    suspend fun getCommits(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Query("sha") sha: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<GiteaCommit>>

    /**
     * Get pull requests
     */
    @GET("api/v1/repos/{owner}/{repo}/pulls")
    suspend fun getPullRequests(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Query("state") state: String? = null, // open, closed, all
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): Response<List<GiteaPullRequest>>

    /**
     * Get specific pull request
     */
    @GET("api/v1/repos/{owner}/{repo}/pulls/{index}")
    suspend fun getPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Path("index") index: Long,
        @Header("Authorization") authorization: String
    ): Response<GiteaPullRequest>

    /**
     * Create a pull request
     */
    @POST("api/v1/repos/{owner}/{repo}/pulls")
    suspend fun createPullRequest(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String,
        @Body pullRequest: Map<String, Any>
    ): Response<GiteaPullRequest>

    /**
     * Star a repository
     */
    @PUT("api/v1/user/starred/{owner}/{repo}")
    suspend fun starRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>

    /**
     * Unstar a repository
     */
    @DELETE("api/v1/user/starred/{owner}/{repo}")
    suspend fun unstarRepository(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("Authorization") authorization: String
    ): Response<Unit>
}
