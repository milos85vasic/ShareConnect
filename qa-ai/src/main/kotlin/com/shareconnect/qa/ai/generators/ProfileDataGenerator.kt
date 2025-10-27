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


package com.shareconnect.qa.ai.generators

import com.shareconnect.qa.ai.models.*
import java.util.UUID

/**
 * Generates test profile data for all possible combinations
 */
class ProfileDataGenerator {

    fun generateAllProfiles(): List<TestProfile> {
        val profiles = mutableListOf<TestProfile>()

        // Generate MeTube profiles
        profiles.addAll(generateMeTubeProfiles())

        // Generate YT-DLP profiles
        profiles.addAll(generateYtDlpProfiles())

        // Generate Torrent profiles (all client types)
        profiles.addAll(generateTorrentProfiles())

        // Generate jDownloader profiles
        profiles.addAll(generateJDownloaderProfiles())

        return profiles
    }

    fun generateAllScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        // Create profile scenarios
        scenarios.addAll(generateCreateScenarios())

        // Edit profile scenarios
        scenarios.addAll(generateEditScenarios())

        // Delete profile scenarios
        scenarios.addAll(generateDeleteScenarios())

        // Set default scenarios
        scenarios.addAll(generateSetDefaultScenarios())

        // Multi-profile scenarios
        scenarios.addAll(generateMultiProfileScenarios())

        // Validation scenarios
        scenarios.addAll(generateValidationScenarios())

        return scenarios
    }

    private fun generateMeTubeProfiles(): List<TestProfile> {
        val profiles = mutableListOf<TestProfile>()

        // Standard MeTube profile
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "MeTube Server",
                url = "http://localhost",
                port = 8081,
                serviceType = ServiceType.METUBE,
                description = "Standard MeTube server configuration"
            )
        )

        // MeTube with custom port
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "MeTube Custom Port",
                url = "http://192.168.1.100",
                port = 9090,
                serviceType = ServiceType.METUBE,
                description = "MeTube with custom port"
            )
        )

        // MeTube as default
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "MeTube Default",
                url = "http://metube.local",
                port = 8081,
                isDefault = true,
                serviceType = ServiceType.METUBE,
                description = "MeTube set as default profile"
            )
        )

        return profiles
    }

    private fun generateYtDlpProfiles(): List<TestProfile> {
        val profiles = mutableListOf<TestProfile>()

        // Standard YT-DLP profile
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "YT-DLP Server",
                url = "http://localhost",
                port = 8082,
                serviceType = ServiceType.YTDL,
                description = "Standard YT-DLP server configuration"
            )
        )

        // YT-DLP with authentication
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "YT-DLP with Auth",
                url = "http://ytdl.server",
                port = 8082,
                serviceType = ServiceType.YTDL,
                authentication = Authentication("user", "pass123"),
                description = "YT-DLP with authentication"
            )
        )

        return profiles
    }

    private fun generateTorrentProfiles(): List<TestProfile> {
        val profiles = mutableListOf<TestProfile>()

        // qBittorrent profiles
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "qBittorrent",
                url = "http://localhost",
                port = 8083,
                serviceType = ServiceType.TORRENT,
                torrentClientType = TorrentClientType.QBITTORRENT,
                authentication = Authentication("admin", "adminpass"),
                description = "qBittorrent with default auth"
            )
        )

        // Transmission profiles
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "Transmission",
                url = "http://localhost",
                port = 9091,
                serviceType = ServiceType.TORRENT,
                torrentClientType = TorrentClientType.TRANSMISSION,
                authentication = Authentication("transmission", "transmission"),
                description = "Transmission with default auth"
            )
        )

        // uTorrent profiles
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "uTorrent",
                url = "http://localhost",
                port = 8080,
                serviceType = ServiceType.TORRENT,
                torrentClientType = TorrentClientType.UTORRENT,
                authentication = Authentication("admin", "admin"),
                description = "uTorrent with default auth"
            )
        )

        // Torrent without authentication (edge case)
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "Torrent No Auth",
                url = "http://localhost",
                port = 8083,
                serviceType = ServiceType.TORRENT,
                torrentClientType = TorrentClientType.QBITTORRENT,
                isEdgeCase = true,
                edgeCaseType = "missing_authentication",
                description = "Torrent client without authentication (should fail)"
            )
        )

        return profiles
    }

    private fun generateJDownloaderProfiles(): List<TestProfile> {
        val profiles = mutableListOf<TestProfile>()

        // Standard jDownloader
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "jDownloader",
                url = "http://localhost",
                port = 3129,
                serviceType = ServiceType.JDOWNLOADER,
                description = "Standard jDownloader configuration"
            )
        )

        // jDownloader with auth
        profiles.add(
            TestProfile(
                id = UUID.randomUUID().toString(),
                name = "jDownloader Auth",
                url = "http://jd.local",
                port = 3129,
                serviceType = ServiceType.JDOWNLOADER,
                authentication = Authentication("jd_user", "jd_pass"),
                description = "jDownloader with authentication"
            )
        )

        return profiles
    }

    private fun generateCreateScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        ServiceType.values().forEach { serviceType ->
            val profile = when (serviceType) {
                ServiceType.METUBE -> TestProfile(
                    UUID.randomUUID().toString(), "New MeTube", "http://localhost", 8081,
                    serviceType = ServiceType.METUBE
                )
                ServiceType.YTDL -> TestProfile(
                    UUID.randomUUID().toString(), "New YT-DLP", "http://localhost", 8082,
                    serviceType = ServiceType.YTDL
                )
                ServiceType.TORRENT -> TestProfile(
                    UUID.randomUUID().toString(), "New Torrent", "http://localhost", 8083,
                    serviceType = ServiceType.TORRENT, torrentClientType = TorrentClientType.QBITTORRENT,
                    authentication = Authentication("admin", "admin")
                )
                ServiceType.JDOWNLOADER -> TestProfile(
                    UUID.randomUUID().toString(), "New jDownloader", "http://localhost", 3129,
                    serviceType = ServiceType.JDOWNLOADER
                )
            }

            scenarios.add(
                ProfileTestScenario(
                    id = UUID.randomUUID().toString(),
                    name = "Create ${serviceType.name} Profile",
                    description = "Test creating a new ${serviceType.name} profile",
                    profiles = listOf(profile),
                    expectedBehavior = "Profile should be created successfully and appear in the list",
                    testType = ProfileTestType.CREATE
                )
            )
        }

        return scenarios
    }

    private fun generateEditScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        val originalProfile = TestProfile(
            id = UUID.randomUUID().toString(),
            name = "Original Profile",
            url = "http://localhost",
            port = 8081,
            serviceType = ServiceType.METUBE
        )

        val editedProfile = originalProfile.copy(
            name = "Edited Profile",
            port = 9090
        )

        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Edit Profile Name and Port",
                description = "Test editing profile name and port",
                profiles = listOf(originalProfile, editedProfile),
                expectedBehavior = "Profile should be updated with new values",
                testType = ProfileTestType.EDIT
            )
        )

        return scenarios
    }

    private fun generateDeleteScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        val profileToDelete = TestProfile(
            id = UUID.randomUUID().toString(),
            name = "Profile to Delete",
            url = "http://localhost",
            port = 8081,
            serviceType = ServiceType.METUBE
        )

        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Delete Profile",
                description = "Test deleting a profile",
                profiles = listOf(profileToDelete),
                expectedBehavior = "Profile should be removed from the list",
                testType = ProfileTestType.DELETE
            )
        )

        return scenarios
    }

    private fun generateSetDefaultScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        val profile1 = TestProfile(
            id = UUID.randomUUID().toString(),
            name = "Profile 1",
            url = "http://localhost",
            port = 8081,
            isDefault = true,
            serviceType = ServiceType.METUBE
        )

        val profile2 = TestProfile(
            id = UUID.randomUUID().toString(),
            name = "Profile 2",
            url = "http://localhost",
            port = 8082,
            serviceType = ServiceType.YTDL
        )

        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Set New Default Profile",
                description = "Test setting a different profile as default",
                profiles = listOf(profile1, profile2),
                expectedBehavior = "Only one profile should be marked as default",
                testType = ProfileTestType.SET_DEFAULT
            )
        )

        return scenarios
    }

    private fun generateMultiProfileScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        val multiProfiles = listOf(
            TestProfile(UUID.randomUUID().toString(), "MeTube", "http://localhost", 8081,
                serviceType = ServiceType.METUBE),
            TestProfile(UUID.randomUUID().toString(), "YT-DLP", "http://localhost", 8082,
                serviceType = ServiceType.YTDL),
            TestProfile(UUID.randomUUID().toString(), "qBittorrent", "http://localhost", 8083,
                serviceType = ServiceType.TORRENT, torrentClientType = TorrentClientType.QBITTORRENT,
                authentication = Authentication("admin", "admin")),
            TestProfile(UUID.randomUUID().toString(), "jDownloader", "http://localhost", 3129,
                serviceType = ServiceType.JDOWNLOADER)
        )

        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Multiple Profiles Management",
                description = "Test managing multiple profiles of different types",
                profiles = multiProfiles,
                expectedBehavior = "All profiles should be created and manageable independently",
                testType = ProfileTestType.MULTI_PROFILE
            )
        )

        return scenarios
    }

    private fun generateValidationScenarios(): List<ProfileTestScenario> {
        val scenarios = mutableListOf<ProfileTestScenario>()

        // Invalid URL
        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Invalid URL Validation",
                description = "Test profile creation with invalid URL",
                profiles = listOf(
                    TestProfile(
                        UUID.randomUUID().toString(), "Invalid URL", "not-a-url", 8081,
                        serviceType = ServiceType.METUBE, isEdgeCase = true
                    )
                ),
                expectedBehavior = "Should show validation error for invalid URL",
                testType = ProfileTestType.VALIDATION
            )
        )

        // Invalid port
        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Invalid Port Validation",
                description = "Test profile creation with invalid port",
                profiles = listOf(
                    TestProfile(
                        UUID.randomUUID().toString(), "Invalid Port", "http://localhost", 99999,
                        serviceType = ServiceType.METUBE, isEdgeCase = true
                    )
                ),
                expectedBehavior = "Should show validation error for invalid port",
                testType = ProfileTestType.VALIDATION
            )
        )

        // Empty name
        scenarios.add(
            ProfileTestScenario(
                id = UUID.randomUUID().toString(),
                name = "Empty Name Validation",
                description = "Test profile creation with empty name",
                profiles = listOf(
                    TestProfile(
                        UUID.randomUUID().toString(), "", "http://localhost", 8081,
                        serviceType = ServiceType.METUBE, isEdgeCase = true
                    )
                ),
                expectedBehavior = "Should show validation error for empty name",
                testType = ProfileTestType.VALIDATION
            )
        )

        return scenarios
    }
}
