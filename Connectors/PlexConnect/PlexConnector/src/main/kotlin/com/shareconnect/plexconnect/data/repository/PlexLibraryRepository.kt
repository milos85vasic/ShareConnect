package com.shareconnect.plexconnect.data.repository

import com.shareconnect.plexconnect.data.api.PlexApiClient
import com.shareconnect.plexconnect.data.database.dao.PlexLibraryDao
import com.shareconnect.plexconnect.data.model.LibraryType
import com.shareconnect.plexconnect.data.model.PlexLibrary
import com.shareconnect.plexconnect.data.model.PlexServer
import kotlinx.coroutines.flow.Flow
class PlexLibraryRepository(
    private val libraryDao: PlexLibraryDao,
    private val apiClient: PlexApiClient
) {

    fun getAllLibraries(): Flow<List<PlexLibrary>> = libraryDao.getAllLibraries()

    fun getLibrariesForServer(serverId: Long): Flow<List<PlexLibrary>> = libraryDao.getLibrariesForServer(serverId)

    fun getLibrariesByType(serverId: Long, type: LibraryType): Flow<List<PlexLibrary>> =
        libraryDao.getLibrariesByType(serverId, type.value)

    suspend fun getLibraryByKey(libraryKey: String): PlexLibrary? = libraryDao.getLibraryByKey(libraryKey)

    suspend fun refreshLibrariesForServer(server: PlexServer): Result<List<PlexLibrary>> {
        return try {
            server.token?.let { token ->
                val librariesResult = apiClient.getLibraries(server.baseUrl, token)
                librariesResult.fold(
                    onSuccess = { libraries ->
                        val librariesWithServerId = libraries.map { it.copy(serverId = server.id) }
                        libraryDao.insertLibraries(librariesWithServerId)
                        Result.success(librariesWithServerId)
                    },
                    onFailure = { error ->
                        Result.failure(error)
                    }
                )
            } ?: Result.failure(Exception("Server not authenticated"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addLibrary(library: PlexLibrary): Long = libraryDao.insertLibrary(library)

    suspend fun updateLibrary(library: PlexLibrary) = libraryDao.updateLibrary(library)

    suspend fun deleteLibrary(library: PlexLibrary) = libraryDao.deleteLibrary(library)

    suspend fun deleteLibraryByKey(libraryKey: String) = libraryDao.deleteLibraryByKey(libraryKey)

    suspend fun deleteLibrariesForServer(serverId: Long) = libraryDao.deleteLibrariesForServer(serverId)

    suspend fun getLibraryCount(): Int = libraryDao.getLibraryCount()

    suspend fun getLibraryCountForServer(serverId: Long): Int = libraryDao.getLibraryCountForServer(serverId)
}