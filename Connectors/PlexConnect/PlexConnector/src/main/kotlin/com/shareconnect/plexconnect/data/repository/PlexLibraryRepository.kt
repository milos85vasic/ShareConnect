/*
 * Copyright (c) 2025 MeTube Share
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */


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

    suspend fun refreshLibrariesForServer(server: PlexServer): Result<List<com.shareconnect.plexconnect.data.model.PlexLibrary>> {
        return try {
            server.token?.let { token ->
                val librariesResult = apiClient.getLibraries(server.baseUrl, token)
                librariesResult.fold(
                    onSuccess = { apiLibraries ->
                        val modelLibraries = apiLibraries.map { apiLib ->
                            com.shareconnect.plexconnect.data.model.PlexLibrary(
                                key = apiLib.id ?: "",
                                title = apiLib.title ?: "",
                                type = com.shareconnect.plexconnect.data.model.LibraryType.fromValue(apiLib.type?.value ?: "movie"),
                                serverId = server.id
                            )
                        }
                        libraryDao.insertLibraries(modelLibraries)
                        Result.success(modelLibraries)
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

    suspend fun addLibrary(library: com.shareconnect.plexconnect.data.model.PlexLibrary): Long = libraryDao.insertLibrary(library)

    suspend fun updateLibrary(library: com.shareconnect.plexconnect.data.model.PlexLibrary) = libraryDao.updateLibrary(library)

    suspend fun deleteLibrary(library: com.shareconnect.plexconnect.data.model.PlexLibrary) = libraryDao.deleteLibrary(library)

    suspend fun deleteLibraryByKey(libraryKey: String) = libraryDao.deleteLibraryByKey(libraryKey)

    suspend fun deleteLibrariesForServer(serverId: Long) = libraryDao.deleteLibrariesForServer(serverId)

    suspend fun getLibraryCount(): Int = libraryDao.getLibraryCount()

    suspend fun getLibraryCountForServer(serverId: Long): Int = libraryDao.getLibraryCountForServer(serverId)
}