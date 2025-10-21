package com.shareconnect.jdownloaderconnect.data.repository

import com.shareconnect.jdownloaderconnect.data.dao.ServerDao
import com.shareconnect.jdownloaderconnect.data.model.ServerProfile
import kotlinx.coroutines.flow.Flow
class ServerRepository constructor(
    private val serverDao: ServerDao
) {

    suspend fun insertServer(serverProfile: ServerProfile) = serverDao.insertServer(serverProfile)
    
    suspend fun updateServer(serverProfile: ServerProfile) = serverDao.updateServer(serverProfile)
    
    suspend fun deleteServer(serverProfile: ServerProfile) = serverDao.deleteServer(serverProfile)
    
    fun getAllServers(): Flow<List<ServerProfile>> = serverDao.getAllServers()
    
    suspend fun getServerById(id: String): ServerProfile? = serverDao.getServerById(id)
    
    suspend fun setActiveServer(serverProfile: ServerProfile) {
        serverDao.clearDefaultServers()
        serverDao.insertServer(serverProfile.copy(isDefault = true))
    }
    
    fun getActiveServer(): Flow<ServerProfile?> = serverDao.getDefaultServer()
    
    suspend fun clearAllServers() = serverDao.clearDefaultServers()
}