package com.shareconnect.portainerconnect.data.models

import com.google.gson.annotations.SerializedName

/**
 * Portainer authentication request
 */
data class PortainerAuthRequest(
    @SerializedName("Username") val username: String,
    @SerializedName("Password") val password: String
)

/**
 * Portainer authentication response
 */
data class PortainerAuthResponse(
    @SerializedName("jwt") val jwt: String
)

/**
 * Portainer status information
 */
data class PortainerStatus(
    @SerializedName("Version") val version: String,
    @SerializedName("Edition") val edition: String? = null,
    @SerializedName("InstanceID") val instanceId: String? = null
)

/**
 * Portainer endpoint (Docker instance)
 */
data class PortainerEndpoint(
    @SerializedName("Id") val id: Int,
    @SerializedName("Name") val name: String,
    @SerializedName("Type") val type: Int,  // 1=Docker, 2=Agent, 3=Azure, 4=EdgeAgent, 5=KubernetesLocal, 6=KubernetesAgent
    @SerializedName("URL") val url: String,
    @SerializedName("Status") val status: Int,  // 1=Up, 2=Down
    @SerializedName("Snapshots") val snapshots: List<PortainerSnapshot>? = null
)

/**
 * Portainer endpoint snapshot
 */
data class PortainerSnapshot(
    @SerializedName("Time") val time: Long,
    @SerializedName("DockerSnapshotRaw") val dockerSnapshotRaw: PortainerDockerSnapshot? = null,
    @SerializedName("TotalCPU") val totalCPU: Int? = null,
    @SerializedName("TotalMemory") val totalMemory: Long? = null
)

/**
 * Docker snapshot data
 */
data class PortainerDockerSnapshot(
    @SerializedName("Containers") val containers: Int? = null,
    @SerializedName("Images") val images: Int? = null,
    @SerializedName("Volumes") val volumes: Int? = null,
    @SerializedName("Running") val running: Int? = null,
    @SerializedName("Stopped") val stopped: Int? = null
)

/**
 * Docker container
 */
data class PortainerContainer(
    @SerializedName("Id") val id: String,
    @SerializedName("Names") val names: List<String>,
    @SerializedName("Image") val image: String,
    @SerializedName("ImageID") val imageId: String,
    @SerializedName("Command") val command: String? = null,
    @SerializedName("Created") val created: Long,
    @SerializedName("State") val state: String,  // "running", "exited", "paused", etc.
    @SerializedName("Status") val status: String,
    @SerializedName("Ports") val ports: List<PortainerPort>? = null,
    @SerializedName("Labels") val labels: Map<String, String>? = null,
    @SerializedName("Mounts") val mounts: List<PortainerMount>? = null,
    @SerializedName("NetworkSettings") val networkSettings: PortainerNetworkSettings? = null
)

/**
 * Docker container port mapping
 */
data class PortainerPort(
    @SerializedName("PrivatePort") val privatePort: Int,
    @SerializedName("PublicPort") val publicPort: Int? = null,
    @SerializedName("Type") val type: String,  // "tcp" or "udp"
    @SerializedName("IP") val ip: String? = null
)

/**
 * Docker container mount
 */
data class PortainerMount(
    @SerializedName("Type") val type: String,  // "bind", "volume", "tmpfs"
    @SerializedName("Source") val source: String,
    @SerializedName("Destination") val destination: String,
    @SerializedName("Mode") val mode: String? = null,
    @SerializedName("RW") val rw: Boolean? = null,
    @SerializedName("Propagation") val propagation: String? = null
)

/**
 * Docker network settings
 */
data class PortainerNetworkSettings(
    @SerializedName("Networks") val networks: Map<String, PortainerNetworkConfig>? = null
)

/**
 * Docker network configuration
 */
data class PortainerNetworkConfig(
    @SerializedName("IPAMConfig") val ipamConfig: Any? = null,
    @SerializedName("Links") val links: List<String>? = null,
    @SerializedName("Aliases") val aliases: List<String>? = null,
    @SerializedName("NetworkID") val networkId: String? = null,
    @SerializedName("EndpointID") val endpointId: String? = null,
    @SerializedName("Gateway") val gateway: String? = null,
    @SerializedName("IPAddress") val ipAddress: String? = null,
    @SerializedName("IPPrefixLen") val ipPrefixLen: Int? = null,
    @SerializedName("IPv6Gateway") val ipv6Gateway: String? = null,
    @SerializedName("GlobalIPv6Address") val globalIPv6Address: String? = null,
    @SerializedName("GlobalIPv6PrefixLen") val globalIPv6PrefixLen: Int? = null,
    @SerializedName("MacAddress") val macAddress: String? = null
)

/**
 * Docker image
 */
data class PortainerImage(
    @SerializedName("Id") val id: String,
    @SerializedName("ParentId") val parentId: String? = null,
    @SerializedName("RepoTags") val repoTags: List<String>? = null,
    @SerializedName("RepoDigests") val repoDigests: List<String>? = null,
    @SerializedName("Created") val created: Long,
    @SerializedName("Size") val size: Long,
    @SerializedName("VirtualSize") val virtualSize: Long? = null,
    @SerializedName("SharedSize") val sharedSize: Long? = null,
    @SerializedName("Labels") val labels: Map<String, String>? = null,
    @SerializedName("Containers") val containers: Int? = null
)

/**
 * Docker volume
 */
data class PortainerVolume(
    @SerializedName("Name") val name: String,
    @SerializedName("Driver") val driver: String,
    @SerializedName("Mountpoint") val mountpoint: String,
    @SerializedName("CreatedAt") val createdAt: String? = null,
    @SerializedName("Status") val status: Map<String, Any>? = null,
    @SerializedName("Labels") val labels: Map<String, String>? = null,
    @SerializedName("Scope") val scope: String,  // "local" or "global"
    @SerializedName("Options") val options: Map<String, String>? = null
)

/**
 * Docker volumes list response
 */
data class PortainerVolumesResponse(
    @SerializedName("Volumes") val volumes: List<PortainerVolume>? = null,
    @SerializedName("Warnings") val warnings: List<String>? = null
)

/**
 * Docker network
 */
data class PortainerNetwork(
    @SerializedName("Name") val name: String,
    @SerializedName("Id") val id: String,
    @SerializedName("Created") val created: String? = null,
    @SerializedName("Scope") val scope: String,
    @SerializedName("Driver") val driver: String,
    @SerializedName("EnableIPv6") val enableIPv6: Boolean? = null,
    @SerializedName("IPAM") val ipam: Any? = null,
    @SerializedName("Internal") val internal: Boolean? = null,
    @SerializedName("Attachable") val attachable: Boolean? = null,
    @SerializedName("Ingress") val ingress: Boolean? = null,
    @SerializedName("Containers") val containers: Map<String, Any>? = null,
    @SerializedName("Options") val options: Map<String, String>? = null,
    @SerializedName("Labels") val labels: Map<String, String>? = null
)

/**
 * Docker container stats
 */
data class PortainerContainerStats(
    @SerializedName("read") val read: String,
    @SerializedName("preread") val preread: String,
    @SerializedName("cpu_stats") val cpuStats: PortainerCPUStats? = null,
    @SerializedName("precpu_stats") val precpuStats: PortainerCPUStats? = null,
    @SerializedName("memory_stats") val memoryStats: PortainerMemoryStats? = null,
    @SerializedName("networks") val networks: Map<String, PortainerNetworkStats>? = null
)

/**
 * Docker CPU stats
 */
data class PortainerCPUStats(
    @SerializedName("cpu_usage") val cpuUsage: PortainerCPUUsage? = null,
    @SerializedName("system_cpu_usage") val systemCpuUsage: Long? = null,
    @SerializedName("online_cpus") val onlineCpus: Int? = null
)

/**
 * Docker CPU usage
 */
data class PortainerCPUUsage(
    @SerializedName("total_usage") val totalUsage: Long,
    @SerializedName("percpu_usage") val percpuUsage: List<Long>? = null,
    @SerializedName("usage_in_kernelmode") val usageInKernelmode: Long? = null,
    @SerializedName("usage_in_usermode") val usageInUsermode: Long? = null
)

/**
 * Docker memory stats
 */
data class PortainerMemoryStats(
    @SerializedName("usage") val usage: Long,
    @SerializedName("max_usage") val maxUsage: Long? = null,
    @SerializedName("limit") val limit: Long,
    @SerializedName("stats") val stats: Map<String, Long>? = null
)

/**
 * Docker network stats
 */
data class PortainerNetworkStats(
    @SerializedName("rx_bytes") val rxBytes: Long,
    @SerializedName("rx_packets") val rxPackets: Long,
    @SerializedName("rx_errors") val rxErrors: Long,
    @SerializedName("rx_dropped") val rxDropped: Long,
    @SerializedName("tx_bytes") val txBytes: Long,
    @SerializedName("tx_packets") val txPackets: Long,
    @SerializedName("tx_errors") val txErrors: Long,
    @SerializedName("tx_dropped") val txDropped: Long
)
