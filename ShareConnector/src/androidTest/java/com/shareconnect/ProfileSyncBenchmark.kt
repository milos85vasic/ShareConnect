package com.shareconnect

import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.utils.ProfileManager
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileSyncBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val profileManager = ProfileManager.getInstance(context)

    @Test
    fun benchmarkProfileSync() {
        benchmarkRule.measureRepeated {
            // Simulate profile sync operation
            runWithTimingDisabled {
                // Setup test data
                val testProfiles = createTestProfiles()
            }

            // Measure sync operation
            profileManager.syncProfiles()
        }
    }

    @Test
    fun benchmarkProfileCreation() {
        benchmarkRule.measureRepeated {
            val profile = createTestProfile()
            profileManager.addProfile(profile)
        }
    }

    @Test
    fun benchmarkProfileRetrieval() {
        benchmarkRule.measureRepeated {
            runWithTimingDisabled {
                // Setup
                val profile = createTestProfile()
                profileManager.addProfile(profile)
            }

            // Measure retrieval
            profileManager.getProfile(profile.id)
        }
    }

    private fun createTestProfiles(): List<Profile> {
        return (1..10).map { createTestProfile(it) }
    }

    private fun createTestProfile(id: Int = 1): Profile {
        return Profile(
            id = "test_profile_$id",
            name = "Test Profile $id",
            type = ProfileType.TORRENT_CLIENT,
            serverConfig = ServerConfig(
                host = "localhost",
                port = 9091 + id,
                username = "test",
                password = "test"
            )
        )
    }
}