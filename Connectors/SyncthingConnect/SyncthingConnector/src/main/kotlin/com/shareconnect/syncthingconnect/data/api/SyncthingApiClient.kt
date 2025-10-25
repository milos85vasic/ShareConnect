package com.shareconnect.syncthingconnect.data.api

import android.util.Log
import com.google.gson.Gson
import com.shareconnect.syncthingconnect.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.io.IOException
import java.util.concurrent.TimeUnit

interface SyncthingApiService {
    @GET("rest/system/config")
    suspend fun getConfig(): Response<SystemConfig>
    
    @POST("rest/system/config")
    suspend fun setConfig(@Body config: SystemConfig): Response<Unit>
    
    @GET("rest/system/status")
    suspend fun getStatus(): Response<SystemStatus>
    
    @GET("rest/system/version")
    suspend fun getVersion(): Response<SystemVersion>
    
    @GET("rest/system/connections")
    suspend fun getConnections(): Response<SystemConnections>
    
    @GET("rest/db/status")
    suspend fun getDatabaseStatus(@Query("folder") folder: String): Response<DatabaseStatus>
    
    @GET("rest/db/browse")
    suspend fun browseDirectory(
        @Query("folder") folder: String,
        @Query("prefix") prefix: String = ""
    ): Response<List<DirectoryEntry>>
    
    @GET("rest/db/completion")
    suspend fun getCompletion(
        @Query("device") device: String,
        @Query("folder") folder: String
    ): Response<FolderCompletion>
    
    @POST("rest/db/scan")
    suspend fun scan(
        @Query("folder") folder: String,
        @Query("sub") sub: String? = null
    ): Response<Unit>
    
    @POST("rest/system/restart")
    suspend fun restart(): Response<Unit>
    
    @POST("rest/system/shutdown")
    suspend fun shutdown(): Response<Unit>
}

class SyncthingApiClient(
    private val baseUrl: String,
    private val apiKey: String
) {
    companion object {
        private const val TAG = "SyncthingApiClient"
        private const val TIMEOUT_SECONDS = 30L
    }

    private val gson = Gson()
    
    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("X-API-Key", apiKey)
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl.trimEnd('/') + "/")
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .build()

    private val apiService = retrofit.create(SyncthingApiService::class.java)

    suspend fun getConfig(): Result<SystemConfig> = executeRequest {
        apiService.getConfig()
    }

    suspend fun setConfig(config: SystemConfig): Result<Unit> = executeRequest {
        apiService.setConfig(config)
    }

    suspend fun getStatus(): Result<SystemStatus> = executeRequest {
        apiService.getStatus()
    }

    suspend fun getVersion(): Result<SystemVersion> = executeRequest {
        apiService.getVersion()
    }

    suspend fun getConnections(): Result<SystemConnections> = executeRequest {
        apiService.getConnections()
    }

    suspend fun getDatabaseStatus(folder: String): Result<DatabaseStatus> = executeRequest {
        apiService.getDatabaseStatus(folder)
    }

    suspend fun browseDirectory(folder: String, prefix: String = ""): Result<List<DirectoryEntry>> = executeRequest {
        apiService.browseDirectory(folder, prefix)
    }

    suspend fun getCompletion(device: String, folder: String): Result<FolderCompletion> = executeRequest {
        apiService.getCompletion(device, folder)
    }

    suspend fun scan(folder: String, sub: String? = null): Result<Unit> = executeRequest {
        apiService.scan(folder, sub)
    }

    suspend fun restart(): Result<Unit> = executeRequest {
        apiService.restart()
    }

    suspend fun shutdown(): Result<Unit> = executeRequest {
        apiService.shutdown()
    }

    suspend fun testConnection(): Result<Boolean> = try {
        getStatus().map { true }
    } catch (e: Exception) {
        Log.e(TAG, "Connection test failed", e)
        Result.failure(e)
    }

    private suspend fun <T> executeRequest(block: suspend () -> Response<T>): Result<T> = try {
        val response = block()
        if (response.isSuccessful && response.body() != null) {
            Result.success(response.body()!!)
        } else {
            Result.failure(IOException("HTTP ${response.code()}: ${response.message()}"))
        }
    } catch (e: Exception) {
        Log.e(TAG, "Request error", e)
        Result.failure(e)
    }
}
