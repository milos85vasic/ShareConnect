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


package com.shareconnect.qa.ai.execution

import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Emulator Manager for AI QA Testing
 *
 * Handles emulator lifecycle, app installation, and environment management
 */
class EmulatorManager(
    private val emulatorName: String = "ShareConnect_QA_Emulator",
    private val adbPath: String = "adb"
) {

    private var emulatorProcess: Process? = null
    private var emulatorPid: Long? = null

    /**
     * Setup emulator for a specific app scenario
     */
    fun setupEmulatorForScenario(scenario: String) {
        println("Setting up emulator for scenario: $scenario")

        // Kill any existing emulator
        killExistingEmulator()

        // Start clean emulator
        startCleanEmulator(scenario)

        // Wait for boot
        waitForEmulatorBoot()

        // Configure emulator settings
        configureEmulator()

        // Install required apps for scenario
        installAppsForScenario(scenario)

        // Grant permissions
        grantAppPermissions(scenario)

        // Clear app data
        clearAppData(scenario)

        println("Emulator setup complete for scenario: $scenario")
    }

    /**
     * Cleanup emulator after test execution
     */
    fun cleanupEmulator() {
        println("Cleaning up emulator...")

        try {
            // Clear all app data
            clearAllAppData()

            // Clear device storage
            clearDeviceStorage()

            // Reset emulator settings
            resetEmulatorSettings()

        } catch (e: Exception) {
            println("Warning: Emulator cleanup failed: ${e.message}")
        }
    }

    /**
     * Kill any existing emulator processes
     */
    private fun killExistingEmulator() {
        println("Killing existing emulator processes...")

        // Kill by process
        emulatorProcess?.destroyForcibly()
        emulatorProcess = null

        // Kill by PID if known
        emulatorPid?.let { pid ->
            try {
                ProcessBuilder("kill", "-9", pid.toString()).start().waitFor()
            } catch (e: Exception) {
                // Ignore
            }
        }

        // Kill any emulator processes
        try {
            ProcessBuilder("pkill", "-f", emulatorName).start().waitFor()
        } catch (e: Exception) {
            // Ignore
        }

        // Kill ADB server
        try {
            executeAdb("kill-server")
        } catch (e: Exception) {
            // Ignore
        }

        Thread.sleep(2000) // Wait for processes to die
    }

    /**
     * Start clean emulator instance
     */
    private fun startCleanEmulator(scenario: String) {
        println("Starting clean emulator for scenario: $scenario")

        // Start ADB server
        executeAdb("start-server")

        // Start emulator
        val processBuilder = ProcessBuilder(
            "emulator",
            "-avd", emulatorName,
            "-no-window",
            "-no-audio",
            "-gpu", "swiftshader_indirect",
            "-memory", "4096",
            "-partition-size", "4096"
        )

        emulatorProcess = processBuilder.start()
        emulatorPid = try {
            // Use reflection to get PID for compatibility
            val pidMethod = Process::class.java.getMethod("pid")
            pidMethod.invoke(emulatorProcess) as? Long
        } catch (e: Exception) {
            null
        }

        println("Emulator started with PID: $emulatorPid")
    }

    /**
     * Wait for emulator to fully boot
     */
    private fun waitForEmulatorBoot(timeoutSeconds: Long = 300) {
        println("Waiting for emulator to boot...")

        val startTime = System.currentTimeMillis()
        val timeoutMs = TimeUnit.SECONDS.toMillis(timeoutSeconds)

        while (System.currentTimeMillis() - startTime < timeoutMs) {
            try {
                val result = executeAdb("shell getprop sys.boot_completed")
                if (result.trim() == "1") {
                    println("Emulator booted successfully")
                    return
                }
            } catch (e: Exception) {
                // ADB not ready yet
            }

            Thread.sleep(2000)
        }

        throw RuntimeException("Emulator failed to boot within $timeoutSeconds seconds")
    }

    /**
     * Configure emulator settings for testing
     */
    private fun configureEmulator() {
        println("Configuring emulator settings...")

        // Disable animations
        executeAdb("shell settings put global window_animation_scale 0.0")
        executeAdb("shell settings put global transition_animation_scale 0.0")
        executeAdb("shell settings put global animator_duration_scale 0.0")

        // Disable system notifications during tests
        executeAdb("shell settings put global heads_up_notifications_enabled 0")

        // Set screen timeout to never
        executeAdb("shell settings put system screen_off_timeout 2147483647")

        // Wake up screen and unlock
        executeAdb("shell input keyevent 26") // Power button
        executeAdb("shell input keyevent 82") // Unlock

        // Set timezone and locale for consistency
        executeAdb("shell setprop persist.sys.timezone UTC")
        executeAdb("shell setprop persist.sys.locale en-US")

        // Disable auto-rotate
        executeAdb("shell content insert --uri content://settings/system --bind name:s:accelerometer_rotation --bind value:i:0")
        executeAdb("shell content insert --uri content://settings/system --bind name:s:user_rotation --bind value:i:0")
    }

    /**
     * Install apps required for the scenario
     */
    private fun installAppsForScenario(scenario: String) {
        println("Installing apps for scenario: $scenario")

        val appsToInstall = getAppsForScenario(scenario)

        for (app in appsToInstall) {
            val apkPath = getApkPath(app)
            if (File(apkPath).exists()) {
                println("Installing $app...")
                executeAdb("install -r -g $apkPath")
            } else {
                throw RuntimeException("APK not found for $app: $apkPath")
            }
        }
    }

    /**
     * Grant necessary permissions to installed apps
     */
    private fun grantAppPermissions(scenario: String) {
        println("Granting app permissions...")

        val appsToInstall = getAppsForScenario(scenario)

        for (app in appsToInstall) {
            val packageName = getPackageName(app)

            // Common permissions
            val permissions = listOf(
                "android.permission.READ_EXTERNAL_STORAGE",
                "android.permission.WRITE_EXTERNAL_STORAGE",
                "android.permission.INTERNET",
                "android.permission.ACCESS_NETWORK_STATE"
            )

            for (permission in permissions) {
                try {
                    executeAdb("shell pm grant $packageName $permission")
                } catch (e: Exception) {
                    // Permission might not be needed or already granted
                }
            }
        }
    }

    /**
     * Clear app data for clean state
     */
    private fun clearAppData(scenario: String) {
        println("Clearing app data...")

        val appsToInstall = getAppsForScenario(scenario)

        for (app in appsToInstall) {
            val packageName = getPackageName(app)
            try {
                executeAdb("shell pm clear $packageName")
            } catch (e: Exception) {
                // App might not be installed
            }
        }
    }

    /**
     * Clear all app data
     */
    private fun clearAllAppData() {
        val allApps = listOf("ShareConnector", "TransmissionConnector", "uTorrentConnector", "qBitConnector")

        for (app in allApps) {
            val packageName = getPackageName(app)
            try {
                executeAdb("shell pm clear $packageName")
            } catch (e: Exception) {
                // Ignore
            }
        }
    }

    /**
     * Clear device storage
     */
    private fun clearDeviceStorage() {
        try {
            executeAdb("shell rm -rf /sdcard/Download/*")
            executeAdb("shell rm -rf /sdcard/DCIM/*")
            executeAdb("shell rm -rf /sdcard/Pictures/*")
            executeAdb("shell rm -rf /sdcard/Movies/*")
        } catch (e: Exception) {
            // Ignore storage cleanup errors
        }
    }

    /**
     * Reset emulator settings to defaults
     */
    private fun resetEmulatorSettings() {
        try {
            // Re-enable animations
            executeAdb("shell settings put global window_animation_scale 1.0")
            executeAdb("shell settings put global transition_animation_scale 1.0")
            executeAdb("shell settings put global animator_duration_scale 1.0")

            // Re-enable notifications
            executeAdb("shell settings put global heads_up_notifications_enabled 1")

            // Reset screen timeout
            executeAdb("shell settings put system screen_off_timeout 60000")
        } catch (e: Exception) {
            // Ignore reset errors
        }
    }

    /**
     * Get list of apps to install for a scenario
     */
    private fun getAppsForScenario(scenario: String): List<String> {
        return when (scenario) {
            "single_shareconnector" -> listOf("ShareConnector")
            "single_transmission" -> listOf("TransmissionConnector")
            "single_utorrent" -> listOf("uTorrentConnector")
            "single_qbit" -> listOf("qBitConnector")
            "dual_shareconnector_transmission" -> listOf("ShareConnector", "TransmissionConnector")
            "dual_shareconnector_utorrent" -> listOf("ShareConnector", "uTorrentConnector")
            "dual_shareconnector_qbit" -> listOf("ShareConnector", "qBitConnector")
            "triple_shareconnector_transmission_utorrent" -> listOf("ShareConnector", "TransmissionConnector", "uTorrentConnector")
            "all_apps" -> listOf("ShareConnector", "TransmissionConnector", "uTorrentConnector", "qBitConnector")
            else -> throw IllegalArgumentException("Unknown scenario: $scenario")
        }
    }

    /**
     * Get APK path for an app
     */
    private fun getApkPath(app: String): String {
        return when (app) {
            "ShareConnector" -> "ShareConnector/build/outputs/apk/debug/ShareConnector-debug.apk"
            "TransmissionConnector" -> "TransmissionConnect/build/outputs/apk/debug/TransmissionConnect-debug.apk"
            "uTorrentConnector" -> "uTorrentConnect/build/outputs/apk/debug/uTorrentConnect-debug.apk"
            "qBitConnector" -> "qBitConnect/build/outputs/apk/debug/qBitConnect-debug.apk"
            else -> throw IllegalArgumentException("Unknown app: $app")
        }
    }

    /**
     * Get package name for an app
     */
    private fun getPackageName(app: String): String {
        return when (app) {
            "ShareConnector" -> "com.shareconnect"
            "TransmissionConnector" -> "com.transmissionconnect"
            "uTorrentConnector" -> "com.utorrentconnect"
            "qBitConnector" -> "com.qbitconnect"
            else -> throw IllegalArgumentException("Unknown app: $app")
        }
    }

    /**
     * Execute ADB command and return output
     */
    private fun executeAdb(command: String): String {
        val process = ProcessBuilder("$adbPath $command".split(" "))
            .redirectErrorStream(true)
            .start()

        val output = process.inputStream.bufferedReader().readText()
        val exitCode = process.waitFor()

        if (exitCode != 0) {
            throw RuntimeException("ADB command failed: $command (exit code: $exitCode)\nOutput: $output")
        }

        return output
    }
}