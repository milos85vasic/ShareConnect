package com.shareconnect.seafileconnect.data.api

import com.shareconnect.seafileconnect.data.models.*
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*

/**
 * Seafile REST API Service interface
 */
interface SeafileApiService {

    /**
     * Authenticate and get auth token
     */
    @FormUrlEncoded
    @POST("api2/auth-token/")
    suspend fun authenticate(
        @Field("username") username: String,
        @Field("password") password: String
    ): Response<AuthToken>

    /**
     * Get account information
     */
    @GET("api2/account/info/")
    suspend fun getAccountInfo(): Response<AccountInfo>

    /**
     * List all accessible libraries
     */
    @GET("api2/repos/")
    suspend fun listLibraries(): Response<List<Library>>

    /**
     * Get library details
     */
    @GET("api/v2.1/repos/{repo_id}/")
    suspend fun getLibrary(@Path("repo_id") repoId: String): Response<Library>

    /**
     * List directory contents
     */
    @GET("api2/repos/{repo_id}/dir/")
    suspend fun listDirectory(
        @Path("repo_id") repoId: String,
        @Query("p") path: String = "/"
    ): Response<List<DirectoryEntry>>

    /**
     * Get upload link for a library
     */
    @GET("api2/repos/{repo_id}/upload-link/")
    suspend fun getUploadLink(
        @Path("repo_id") repoId: String,
        @Query("p") path: String = "/"
    ): Response<String>

    /**
     * Upload file
     */
    @Multipart
    @POST
    suspend fun uploadFile(
        @Url uploadUrl: String,
        @Part("parent_dir") parentDir: String,
        @Part file: MultipartBody.Part,
        @Part("replace") replace: Int = 1
    ): Response<String>

    /**
     * Get download link for a file
     */
    @GET("api2/repos/{repo_id}/file/")
    suspend fun getDownloadLink(
        @Path("repo_id") repoId: String,
        @Query("p") path: String,
        @Query("reuse") reuse: Int = 1
    ): Response<String>

    /**
     * Download file
     */
    @Streaming
    @GET
    suspend fun downloadFile(@Url downloadUrl: String): Response<ResponseBody>

    /**
     * Create directory
     */
    @FormUrlEncoded
    @POST("api2/repos/{repo_id}/dir/")
    suspend fun createDirectory(
        @Path("repo_id") repoId: String,
        @Field("p") path: String
    ): Response<FileOperationResult>

    /**
     * Delete file or directory
     */
    @DELETE("api2/repos/{repo_id}/dir/")
    suspend fun deleteItem(
        @Path("repo_id") repoId: String,
        @Query("p") path: String
    ): Response<FileOperationResult>

    /**
     * Move or copy file/directory
     */
    @FormUrlEncoded
    @POST("api/v2.1/repos/{repo_id}/file/move/")
    suspend fun moveFile(
        @Path("repo_id") repoId: String,
        @Field("src_repo_id") srcRepoId: String,
        @Field("src_path") srcPath: String,
        @Field("dst_repo_id") dstRepoId: String,
        @Field("dst_path") dstPath: String
    ): Response<FileOperationResult>

    /**
     * Search for files
     */
    @GET("api2/search/")
    suspend fun search(
        @Query("q") query: String,
        @Query("per_page") perPage: Int = 25
    ): Response<SearchResponse>

    /**
     * Decrypt encrypted library
     */
    @POST("api2/repos/{repo_id}/decrypt-lib/")
    suspend fun decryptLibrary(
        @Path("repo_id") repoId: String,
        @Body request: DecryptLibraryRequest
    ): Response<DecryptLibraryResult>
}
