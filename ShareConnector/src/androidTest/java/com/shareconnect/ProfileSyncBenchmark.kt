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