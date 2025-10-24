package com.shareconnect.jdownloaderconnect.data.repository

import com.shareconnect.jdownloaderconnect.data.model.JDownloaderAccount
import com.shareconnect.jdownloaderconnect.domain.model.*
import com.shareconnect.jdownloaderconnect.network.api.MyJDownloaderApi
import com.shareconnect.jdownloaderconnect.network.api.InstanceStatus
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class MyJDownloaderRepository(
    private val api: MyJDownloaderApi,
    private val accountRepository: JDownloaderRepository
) {
    // Store session token for authenticated requests
    private var currentSessionToken: String? = null

    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Cache for instances and their data
    private val instancesCache = ConcurrentHashMap<String, JDownloaderInstance>()
    private val speedHistoryCache = ConcurrentHashMap<String, SpeedHistory>()

    // Real-time monitoring flows
    private val _instances = MutableStateFlow<List<JDownloaderInstance>>(emptyList())
    val instances: StateFlow<List<JDownloaderInstance>> = _instances.asStateFlow()

    private val _instanceUpdates = MutableSharedFlow<InstanceUpdate>()
    val instanceUpdates: SharedFlow<InstanceUpdate> = _instanceUpdates.asSharedFlow()

    init {
        startRealTimeMonitoring()
    }

    /**
     * Get all My JDownloader instances for the active account
     */
    suspend fun getInstances(): Result<List<JDownloaderInstance>> = withContext(Dispatchers.IO) {
        try {
            // For now, use mock authentication since actual auth isn't implemented
            val sessionToken = currentSessionToken ?: "mock_session_token"
            val response = api.listInstances("Bearer $sessionToken")
            if (response.isSuccessful) {
                val instances = response.body()?.instances?.map { apiInstance ->
                    com.shareconnect.jdownloaderconnect.domain.model.JDownloaderInstance(
                        id = apiInstance.id,
                        name = apiInstance.name,
                        status = apiInstance.status.toDomainStatus(),
                        version = apiInstance.version,
                        lastSeen = apiInstance.lastSeen,
                        isOnline = apiInstance.isOnline,
                        deviceId = apiInstance.deviceId,
                        accountId = apiInstance.accountId
                    )
                } ?: emptyList()
                instancesCache.clear()
                instances.forEach { instancesCache[it.id] = it }
                _instances.value = instances
                Result.success(instances)
            } else {
                Result.failure(Exception("Failed to fetch instances: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get detailed status for a specific instance
     */
    suspend fun getInstanceStatus(instanceId: String): Result<JDownloaderInstance> = withContext(Dispatchers.IO) {
        try {
            val sessionToken = currentSessionToken ?: "mock_session_token"
            val response = api.getInstanceStatus("Bearer $sessionToken", instanceId)
            if (response.isSuccessful) {
                val statusResponse = response.body()
                    ?: return@withContext Result.failure(Exception("Empty response"))

                val updatedInstance = instancesCache[instanceId]?.copy(
                    status = statusResponse.status.toDomainStatus(),
                    activeDownloads = statusResponse.activeDownloads,
                    uptime = statusResponse.uptime,
                    errorMessage = statusResponse.errorMessage
                ) ?: return@withContext Result.failure(Exception("Instance not found"))

                instancesCache[instanceId] = updatedInstance
                updateInstancesList()

                // Emit update
                _instanceUpdates.emit(InstanceUpdate(
                    instanceId = instanceId,
                    type = UpdateType.STATUS_CHANGED,
                    data = UpdateData(status = statusResponse.status.toDomainStatus())
                ))

                Result.success(updatedInstance)
            } else {
                Result.failure(Exception("Failed to fetch instance status: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get current download speed for an instance
     */
    suspend fun getDownloadSpeed(instanceId: String): Result<Long> = withContext(Dispatchers.IO) {
        try {
            val sessionToken = currentSessionToken ?: "mock_session_token"
            val response = api.getDownloadSpeed("Bearer $sessionToken", instanceId)
            if (response.isSuccessful) {
                val speedResponse = response.body()
                    ?: return@withContext Result.failure(Exception("Empty response"))

                // Update instance with current speed
                instancesCache[instanceId]?.let { instance ->
                    val updatedInstance = instance.copy(currentSpeed = speedResponse.currentSpeed)
                    instancesCache[instanceId] = updatedInstance
                    updateInstancesList()
                }

                // Emit speed update
                _instanceUpdates.emit(InstanceUpdate(
                    instanceId = instanceId,
                    type = UpdateType.SPEED_UPDATED,
                    data = UpdateData(speed = speedResponse.currentSpeed)
                ))

                Result.success(speedResponse.currentSpeed)
            } else {
                Result.failure(Exception("Failed to fetch download speed: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get speed history for an instance
     */
    suspend fun getSpeedHistory(instanceId: String, durationMinutes: Int = 60): Result<SpeedHistory> = withContext(Dispatchers.IO) {
        try {
            val sessionToken = currentSessionToken ?: "mock_session_token"
            val response = api.getSpeedHistory("Bearer $sessionToken", instanceId, durationMinutes)
            if (response.isSuccessful) {
                val historyResponse = response.body()
                    ?: return@withContext Result.failure(Exception("Empty response"))

                val speedHistory = SpeedHistory(
                    instanceId = instanceId,
                    points = historyResponse.speedPoints.map { apiPoint ->
                        com.shareconnect.jdownloaderconnect.domain.model.SpeedPoint(
                            timestamp = apiPoint.timestamp,
                            speed = apiPoint.speed,
                            activeConnections = apiPoint.activeConnections
                        )
                    },
                    averageSpeed = historyResponse.speedPoints.map { it.speed }.average().toLong(),
                    maxSpeed = historyResponse.speedPoints.maxOfOrNull { it.speed } ?: 0,
                    minSpeed = historyResponse.speedPoints.minOfOrNull { it.speed } ?: 0,
                    durationMinutes = durationMinutes
                )

                speedHistoryCache[instanceId] = speedHistory
                Result.success(speedHistory)
            } else {
                Result.failure(Exception("Failed to fetch speed history: ${response.message()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Get dashboard data for an instance
     */
    suspend fun getInstanceDashboard(instanceId: String): Result<InstanceDashboard> = withContext(Dispatchers.IO) {
        try {
            val instance = instancesCache[instanceId]
                ?: return@withContext Result.failure(Exception("Instance not found"))

            val speedHistory = getSpeedHistory(instanceId).getOrNull()
                ?: SpeedHistory(instanceId, emptyList(), 0, 0, 0, 60)

            // Mock recent activity for now - in real implementation this would come from API
            val recentActivity = listOf(
                DownloadActivity(
                    id = "1",
                    filename = "example_file.mp4",
                    status = DownloadStatus.DOWNLOADING,
                    progress = 0.75f,
                    speed = 2048000, // 2 MB/s
                    eta = 120, // 2 minutes
                    timestamp = System.currentTimeMillis()
                )
            )

            val dashboard = InstanceDashboard(
                instance = instance,
                speedHistory = speedHistory,
                recentActivity = recentActivity
            )

            Result.success(dashboard)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Start real-time monitoring of all instances
     */
    private fun startRealTimeMonitoring() {
        coroutineScope.launch {
            while (isActive) {
                try {
                    // Update all instances status and speed every 5 seconds
                    instances.value.forEach { instance ->
                        launch {
                            getInstanceStatus(instance.id)
                            delay(1.seconds)
                            getDownloadSpeed(instance.id)
                        }
                    }
                } catch (e: Exception) {
                    // Log error but continue monitoring
                    e.printStackTrace()
                }

                delay(5.seconds)
            }
        }
    }

    /**
     * Control instance (start/stop/pause)
     */
    suspend fun controlInstance(instanceId: String, action: InstanceAction): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // This would be implemented with actual API calls
            // For now, just update local state
            val instance = instancesCache[instanceId]
                ?: return@withContext Result.failure(Exception("Instance not found"))

            val newStatus = when (action) {
                InstanceAction.START -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.RUNNING
                InstanceAction.STOP -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.STOPPED
                InstanceAction.PAUSE -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.PAUSED
            }

            val updatedInstance = instance.copy(status = newStatus)
            instancesCache[instanceId] = updatedInstance
            updateInstancesList()

            _instanceUpdates.emit(InstanceUpdate(
                instanceId = instanceId,
                type = UpdateType.STATUS_CHANGED,
                data = UpdateData(status = newStatus)
            ))

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun updateInstancesList() {
        _instances.value = instancesCache.values.toList()
    }

    fun cleanup() {
        coroutineScope.cancel()
        instancesCache.clear()
        speedHistoryCache.clear()
    }
}



private fun InstanceStatus.toDomainStatus(): com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus {
    return when (this) {
        InstanceStatus.RUNNING -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.RUNNING
        InstanceStatus.PAUSED -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.PAUSED
        InstanceStatus.STOPPED -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.STOPPED
        InstanceStatus.ERROR -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.ERROR
        InstanceStatus.OFFLINE -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.OFFLINE
        InstanceStatus.CONNECTING -> com.shareconnect.jdownloaderconnect.domain.model.InstanceStatus.CONNECTING
    }
}

enum class InstanceAction {
    START, STOP, PAUSE
}