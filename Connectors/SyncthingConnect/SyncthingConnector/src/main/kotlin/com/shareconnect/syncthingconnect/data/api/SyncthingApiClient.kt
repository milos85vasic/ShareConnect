package com.shareconnect.syncthingconnect.data.api

import com.shareconnect.syncthingconnect.data.models.*
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Syncthing API client for P2P file synchronization
 *
 * @param serverUrl Base URL of Syncthing server (e.g., "http://192.168.1.100:8384")
 * @param apiKey API key for authentication (X-API-Key header)
 * @param syncthingApiService Optional service instance for dependency injection (testing)
 */
class SyncthingApiClient(
    private val serverUrl: String,
    private val apiKey: String,
    syncthingApiService: SyncthingApiService? = null
) {

    private val service: SyncthingApiService by lazy {
        syncthingApiService ?: createService()
    }

    private fun createService(): SyncthingApiService {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(serverUrl)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(SyncthingApiService::class.java)
    }

    suspend fun getConfig(): Result<SyncthingConfig> {
        return try {
            val response = service.getConfig(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get config: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateConfig(config: SyncthingConfig): Result<SyncthingApiResponse> {
        return try {
            val response = service.updateConfig(apiKey, config)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to update config: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getStatus(): Result<SyncthingStatus> {
        return try {
            val response = service.getStatus(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getVersion(): Result<SyncthingVersion> {
        return try {
            val response = service.getVersion(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get version: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getConnections(): Result<SyncthingConnections> {
        return try {
            val response = service.getConnections(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get connections: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun restart(): Result<SyncthingApiResponse> {
        return try {
            val response = service.restart(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Restarting"))
            } else {
                Result.failure(Exception("Failed to restart: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun shutdown(): Result<SyncthingApiResponse> {
        return try {
            val response = service.shutdown(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Shutting down"))
            } else {
                Result.failure(Exception("Failed to shutdown: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDBStatus(folder: String): Result<SyncthingDBStatus> {
        return try {
            val response = service.getDBStatus(apiKey, folder)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get DB status: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun browse(folder: String, prefix: String? = null, dirsonly: Boolean? = null, levels: Int? = null): Result<List<SyncthingBrowseEntry>> {
        return try {
            val response = service.browse(apiKey, folder, prefix, dirsonly, levels)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to browse: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCompletion(folder: String, device: String): Result<SyncthingCompletion> {
        return try {
            val response = service.getCompletion(apiKey, folder, device)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get completion: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun scan(folder: String, sub: String? = null, next: Int? = null): Result<SyncthingApiResponse> {
        return try {
            val response = service.scan(apiKey, folder, sub, next)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Scanning"))
            } else {
                Result.failure(Exception("Failed to scan: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFolderStats(): Result<Map<String, SyncthingFolderStats>> {
        return try {
            val response = service.getFolderStats(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get folder stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceStats(): Result<Map<String, SyncthingDeviceStats>> {
        return try {
            val response = service.getDeviceStats(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get device stats: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getErrors(): Result<SyncthingApiResponse> {
        return try {
            val response = service.getErrors(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get errors: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun clearErrors(): Result<SyncthingApiResponse> {
        return try {
            val response = service.clearErrors(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Errors cleared"))
            } else {
                Result.failure(Exception("Failed to clear errors: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLog(since: Long? = null): Result<Map<String, Any>> {
        return try {
            val response = service.getLog(apiKey, since)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get log: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun ping(): Result<Map<String, String>> {
        return try {
            val response = service.ping(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to ping: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pauseDevice(device: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.pauseDevice(apiKey, device)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Device paused"))
            } else {
                Result.failure(Exception("Failed to pause device: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resumeDevice(device: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.resumeDevice(apiKey, device)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Device resumed"))
            } else {
                Result.failure(Exception("Failed to resume device: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun pauseFolder(folder: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.pauseFolder(apiKey, folder)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Folder paused"))
            } else {
                Result.failure(Exception("Failed to pause folder: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resumeFolder(folder: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.resumeFolder(apiKey, folder)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Folder resumed"))
            } else {
                Result.failure(Exception("Failed to resume folder: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDiscovery(): Result<Map<String, Any>> {
        return try {
            val response = service.getDiscovery(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get discovery: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUpgrade(): Result<Map<String, Any>> {
        return try {
            val response = service.getUpgrade(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get upgrade: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun performUpgrade(): Result<SyncthingApiResponse> {
        return try {
            val response = service.performUpgrade(apiKey)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Upgrading"))
            } else {
                Result.failure(Exception("Failed to perform upgrade: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getIgnores(folder: String): Result<Map<String, Any>> {
        return try {
            val response = service.getIgnores(apiKey, folder)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get ignores: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateIgnores(folder: String, ignores: Map<String, Any>): Result<SyncthingApiResponse> {
        return try {
            val response = service.updateIgnores(apiKey, folder, ignores)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Ignores updated"))
            } else {
                Result.failure(Exception("Failed to update ignores: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun override(folder: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.override(apiKey, folder)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Override applied"))
            } else {
                Result.failure(Exception("Failed to override: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun revert(folder: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.revert(apiKey, folder)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Revert applied"))
            } else {
                Result.failure(Exception("Failed to revert: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getNeed(folder: String, page: Int? = null, perpage: Int? = null): Result<Map<String, Any>> {
        return try {
            val response = service.getNeed(apiKey, folder, page, perpage)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get need: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRemoteNeed(folder: String, device: String, page: Int? = null, perpage: Int? = null): Result<Map<String, Any>> {
        return try {
            val response = service.getRemoteNeed(apiKey, folder, device, page, perpage)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get remote need: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getFile(folder: String, file: String): Result<Map<String, Any>> {
        return try {
            val response = service.getFile(apiKey, folder, file)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get file: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getGlobalFile(folder: String, file: String): Result<Map<String, Any>> {
        return try {
            val response = service.getGlobalFile(apiKey, folder, file)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get global file: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setPriority(folder: String, file: String): Result<SyncthingApiResponse> {
        return try {
            val response = service.setPriority(apiKey, folder, file)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Priority set"))
            } else {
                Result.failure(Exception("Failed to set priority: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDebug(): Result<Map<String, Any>> {
        return try {
            val response = service.getDebug(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get debug: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun setDebug(facilities: Map<String, Any>): Result<SyncthingApiResponse> {
        return try {
            val response = service.setDebug(apiKey, facilities)
            if (response.isSuccessful) {
                Result.success(response.body() ?: SyncthingApiResponse(message = "Debug set"))
            } else {
                Result.failure(Exception("Failed to set debug: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingDevices(): Result<Map<String, Any>> {
        return try {
            val response = service.getPendingDevices(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get pending devices: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPendingFolders(): Result<Map<String, Any>> {
        return try {
            val response = service.getPendingFolders(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get pending folders: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getDeviceID(id: String): Result<Map<String, String>> {
        return try {
            val response = service.getDeviceID(apiKey, id)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get device ID: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getLanguages(): Result<List<Map<String, String>>> {
        return try {
            val response = service.getLanguages(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get languages: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomString(length: Int = 32): Result<Map<String, String>> {
        return try {
            val response = service.getRandomString(apiKey, length)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get random string: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getReport(): Result<Map<String, Any>> {
        return try {
            val response = service.getReport(apiKey)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Failed to get report: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
