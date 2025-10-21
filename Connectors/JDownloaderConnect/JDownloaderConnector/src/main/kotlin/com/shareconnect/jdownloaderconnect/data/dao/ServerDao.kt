package com.shareconnect.jdownloaderconnect.data.dao

import androidx.room.*
import com.shareconnect.jdownloaderconnect.data.model.ServerProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface ServerDao {

    @Query("SELECT * FROM server_profiles")
    fun getAllServers(): Flow<List<ServerProfile>>

    @Query("SELECT * FROM server_profiles WHERE id = :id")
    suspend fun getServerById(id: String): ServerProfile?

    @Query("SELECT * FROM server_profiles WHERE isDefault = 1")
    fun getDefaultServer(): Flow<ServerProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertServer(server: ServerProfile)

    @Update
    suspend fun updateServer(server: ServerProfile)

    @Delete
    suspend fun deleteServer(server: ServerProfile)

    @Query("UPDATE server_profiles SET isDefault = 0")
    suspend fun clearDefaultServers()

    @Query("UPDATE server_profiles SET isDefault = 1 WHERE id = :id")
    suspend fun setDefaultServer(id: String)
}