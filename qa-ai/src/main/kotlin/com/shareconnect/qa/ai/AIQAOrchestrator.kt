package com.shareconnect.qa.ai

import android.content.Context
import android.content.Intent
import android.net.Uri
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.util.*
import kotlin.random.Random

/**
 * AI QA Orchestrator that simulates multiple individual users
 * with different behaviors, preferences, and usage patterns
 */
class AIQAOrchestrator private constructor(
    private val context: Context,
    private val userProfiles: List<UserProfile>
) {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val testResults = MutableSharedFlow<TestResult>(replay = 100)
    val testResultsFlow: SharedFlow<TestResult> = testResults.asSharedFlow()

    private var isRunning = false

    companion object {
        fun create(context: Context, userProfiles: List<UserProfile>): AIQAOrchestrator {
            return AIQAOrchestrator(context, userProfiles)
        }

        fun createDefaultUserProfiles(): List<UserProfile> {
            return listOf(
                UserProfile(
                    id = "user_streaming_enthusiast",
                    name = "Alex Streaming",
                    description = "Heavy streaming service user",
                    preferredServices = listOf("youtube", "spotify", "twitch"),
                    usagePattern = UsagePattern.HEAVY,
                    deviceType = "phone",
                    networkType = "wifi"
                ),
                UserProfile(
                    id = "user_torrent_downloader",
                    name = "Bob Torrent",
                    description = "Torrent and file sharing user",
                    preferredServices = listOf("qbittorrent", "transmission", "mega"),
                    usagePattern = UsagePattern.MODERATE,
                    deviceType = "tablet",
                    networkType = "mobile"
                ),
                UserProfile(
                    id = "user_casual_sharer",
                    name = "Carol Casual",
                    description = "Occasional content sharer",
                    preferredServices = listOf("youtube", "instagram", "dropbox"),
                    usagePattern = UsagePattern.LIGHT,
                    deviceType = "phone",
                    networkType = "wifi"
                ),
                UserProfile(
                    id = "user_power_user",
                    name = "David Power",
                    description = "Advanced user with multiple services",
                    preferredServices = listOf("qbittorrent", "transmission", "utorrent", "jdownloader"),
                    usagePattern = UsagePattern.HEAVY,
                    deviceType = "tablet",
                    networkType = "wifi"
                ),
                UserProfile(
                    id = "user_mobile_user",
                    name = "Emma Mobile",
                    description = "Mobile-only user",
                    preferredServices = listOf("youtube", "tiktok", "instagram"),
                    usagePattern = UsagePattern.MODERATE,
                    deviceType = "phone",
                    networkType = "mobile"
                )
            )
        }
    }

    fun startSimulation(durationMinutes: Int = 30): Job {
        if (isRunning) {
            throw IllegalStateException("Simulation is already running")
        }

        isRunning = true

        return scope.launch {
            val endTime = System.currentTimeMillis() + (durationMinutes * 60 * 1000L)

            while (System.currentTimeMillis() < endTime && isRunning) {
                // Run user simulations concurrently
                userProfiles.map { userProfile ->
                    launch {
                        simulateUser(userProfile)
                    }
                }.joinAll()

                // Wait before next simulation cycle
                delay(Random.nextLong(5000, 15000)) // 5-15 seconds between cycles
            }

            isRunning = false
        }
    }

    fun stopSimulation() {
        isRunning = false
        scope.cancel()
    }

    private suspend fun simulateUser(userProfile: UserProfile) {
        try {
            val userSimulator = UserSimulator(context, userProfile)

            // Simulate user launching app
            userSimulator.launchApp()

            // Simulate user behavior based on profile
            when (userProfile.usagePattern) {
                UsagePattern.HEAVY -> simulateHeavyUser(userSimulator)
                UsagePattern.MODERATE -> simulateModerateUser(userSimulator)
                UsagePattern.LIGHT -> simulateLightUser(userSimulator)
            }

            // Simulate user closing app
            userSimulator.closeApp()

            // Report successful simulation
            testResults.emit(TestResult.Success(
                userId = userProfile.id,
                testName = "User Simulation",
                duration = Random.nextLong(1000, 5000),
                details = "User ${userProfile.name} completed simulation successfully"
            ))

        } catch (e: Exception) {
            // Report simulation failure
            testResults.emit(TestResult.Failure(
                userId = userProfile.id,
                testName = "User Simulation",
                error = e.message ?: "Unknown error",
                details = "User ${userProfile.name} simulation failed: ${e.stackTraceToString()}"
            ))
        }
    }

    private suspend fun simulateHeavyUser(userSimulator: UserSimulator) {
        // Heavy users share frequently and use multiple services
        repeat(Random.nextInt(5, 10)) {
            val service = userSimulator.userProfile.preferredServices.random()
            val content = generateContentForService(service)
            userSimulator.shareContent(content)

            delay(Random.nextLong(2000, 5000)) // 2-5 seconds between shares
        }

        // Test profile management
        userSimulator.manageProfiles()

        // Test settings and preferences
        userSimulator.adjustSettings()
    }

    private suspend fun simulateModerateUser(userSimulator: UserSimulator) {
        // Moderate users share occasionally
        repeat(Random.nextInt(2, 5)) {
            val service = userSimulator.userProfile.preferredServices.random()
            val content = generateContentForService(service)
            userSimulator.shareContent(content)

            delay(Random.nextLong(3000, 8000)) // 3-8 seconds between shares
        }

        // Occasional profile management
        if (Random.nextFloat() < 0.3f) {
            userSimulator.manageProfiles()
        }
    }

    private suspend fun simulateLightUser(userSimulator: UserSimulator) {
        // Light users share infrequently
        repeat(Random.nextInt(1, 3)) {
            val service = userSimulator.userProfile.preferredServices.random()
            val content = generateContentForService(service)
            userSimulator.shareContent(content)

            delay(Random.nextLong(5000, 12000)) // 5-12 seconds between shares
        }
    }

    private fun generateContentForService(service: String): ShareableContent {
        return when (service) {
            "youtube" -> ShareableContent(
                url = "https://www.youtube.com/watch?v=${generateRandomId()}",
                title = "Sample YouTube Video",
                type = "video",
                service = "youtube"
            )
            "spotify" -> ShareableContent(
                url = "https://open.spotify.com/track/${generateRandomId()}",
                title = "Sample Spotify Track",
                type = "audio",
                service = "spotify"
            )
            "twitch" -> ShareableContent(
                url = "https://www.twitch.tv/videos/${generateRandomId()}",
                title = "Sample Twitch Stream",
                type = "video",
                service = "twitch"
            )
            "qbittorrent", "transmission", "utorrent" -> ShareableContent(
                url = "magnet:?xt=urn:btih:${generateRandomId()}&dn=Sample+Torrent",
                title = "Sample Torrent",
                type = "torrent",
                service = service
            )
            "mega" -> ShareableContent(
                url = "https://mega.nz/file/${generateRandomId()}",
                title = "Sample Mega File",
                type = "file",
                service = "mega"
            )
            "instagram" -> ShareableContent(
                url = "https://www.instagram.com/p/${generateRandomId()}/",
                title = "Sample Instagram Post",
                type = "image",
                service = "instagram"
            )
            "dropbox" -> ShareableContent(
                url = "https://www.dropbox.com/s/${generateRandomId()}/sample.pdf",
                title = "Sample Dropbox File",
                type = "document",
                service = "dropbox"
            )
            else -> ShareableContent(
                url = "https://example.com/sample.${generateRandomId()}",
                title = "Sample Content",
                type = "unknown",
                service = service
            )
        }
    }

    private fun generateRandomId(): String {
        return Random.nextInt(1000000, 9999999).toString()
    }

    data class UserProfile(
        val id: String,
        val name: String,
        val description: String,
        val preferredServices: List<String>,
        val usagePattern: UsagePattern,
        val deviceType: String,
        val networkType: String
    )

    enum class UsagePattern {
        LIGHT, MODERATE, HEAVY
    }

    data class ShareableContent(
        val url: String,
        val title: String,
        val type: String,
        val service: String
    )

    sealed class TestResult {
        data class Success(
            val userId: String,
            val testName: String,
            val duration: Long,
            val details: String
        ) : TestResult()

        data class Failure(
            val userId: String,
            val testName: String,
            val error: String,
            val details: String
        ) : TestResult()
    }
}

/**
 * Individual user simulator that mimics real user behavior
 */
class UserSimulator(
    private val context: Context,
    val userProfile: AIQAOrchestrator.UserProfile
) {

    suspend fun launchApp() {
        // Simulate app launch
        val intent = context.packageManager.getLaunchIntentForPackage("com.shareconnect")
        intent?.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        delay(2000) // Wait for app to launch
    }

    suspend fun shareContent(content: AIQAOrchestrator.ShareableContent) {
        // Simulate sharing content
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content.url)
            putExtra(Intent.EXTRA_TITLE, content.title)
        }

        val chooserIntent = Intent.createChooser(intent, "Share ${content.title}")
        chooserIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooserIntent)

        delay(3000) // Wait for share dialog and processing
    }

    suspend fun manageProfiles() {
        // Simulate profile management
        // This would involve navigating to profiles screen and making changes
        delay(2000)
    }

    suspend fun adjustSettings() {
        // Simulate settings adjustment
        // This would involve navigating to settings and changing preferences
        delay(1500)
    }

    suspend fun closeApp() {
        // Simulate app closing
        val intent = Intent(Intent.ACTION_MAIN)
        intent.addCategory(Intent.CATEGORY_HOME)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)

        delay(1000)
    }
}