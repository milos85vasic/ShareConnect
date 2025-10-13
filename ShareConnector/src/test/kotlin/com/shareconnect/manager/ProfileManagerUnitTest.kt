package com.shareconnect.manager

import android.content.Context
import android.content.SharedPreferences
import androidx.test.core.app.ApplicationProvider
import com.shareconnect.ProfileManager
import com.shareconnect.ServerProfile
import com.shareconnect.database.ServerProfileRepository
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33], application = android.app.Application::class)
class ProfileManagerUnitTest {

    private lateinit var mockRepository: ServerProfileRepository
    private lateinit var mockSharedPreferences: SharedPreferences
    private lateinit var mockEditor: SharedPreferences.Editor

    private lateinit var context: Context
    private lateinit var profileManager: ProfileManager

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()

        // Create mocks using Mockito.mock() for final classes
        mockRepository = Mockito.mock(ServerProfileRepository::class.java)
        mockSharedPreferences = Mockito.mock(SharedPreferences::class.java)
        mockEditor = Mockito.mock(SharedPreferences.Editor::class.java)

        // Mock SharedPreferences behavior
        `when`(mockSharedPreferences.edit()).thenReturn(mockEditor)
        `when`(mockEditor.putBoolean(any(), any())).thenReturn(mockEditor)
        `when`(mockEditor.remove(any())).thenReturn(mockEditor)
        `when`(mockEditor.apply()).then {}

        // Create ProfileManager with mocked dependencies
        profileManager = ProfileManager(context, mockRepository, mockSharedPreferences)
    }

    @Test
    fun testProfileManagerInitialization() {
        assertNotNull(profileManager)
    }

    @Test
    fun testHasProfilesWithEmptyProfiles() {
        `when`(mockRepository.hasProfiles()).thenReturn(false)

        assertFalse(profileManager.hasProfiles())
        verify(mockRepository).hasProfiles()
    }

    @Test
    fun testHasProfilesWithExistingProfiles() {
        `when`(mockRepository.hasProfiles()).thenReturn(true)

        assertTrue(profileManager.hasProfiles())
        verify(mockRepository).hasProfiles()
    }

    @Test
    fun testGetProfilesEmpty() {
        `when`(mockRepository.getAllProfiles()).thenReturn(emptyList())

        val profiles = profileManager.profiles

        assertTrue(profiles.isEmpty())
        verify(mockRepository).getAllProfiles()
    }

    @Test
    fun testGetProfiles() {
        val testProfile = ServerProfile().apply {
            id = "test-id"
            name = "Test Profile"
            url = "http://example.com"
            port = 8080
            serviceType = ServerProfile.TYPE_METUBE
            username = null
            password = null
        }

        `when`(mockRepository.getAllProfiles()).thenReturn(listOf(testProfile))

        val profiles = profileManager.profiles

        assertEquals(1, profiles.size)
        assertEquals("test-id", profiles[0].id)
        assertEquals("Test Profile", profiles[0].name)
        assertEquals(null, profiles[0].username)
        assertEquals(null, profiles[0].password)
        verify(mockRepository).getAllProfiles()
    }

    @Test
    fun testDefaultProfileReturnsRepositoryDefault() {
        val testProfile = ServerProfile().apply {
            id = "default-id"
            name = "Default Profile"
        }

        `when`(mockRepository.getDefaultProfile()).thenReturn(testProfile)

        val defaultProfile = profileManager.defaultProfile()

        assertEquals(testProfile, defaultProfile)
        verify(mockRepository).getDefaultProfile()
    }

    @Test
    fun testDefaultProfileFallsBackToSharedPreferences() {
        val testProfile = ServerProfile().apply {
            id = "fallback-id"
            name = "Fallback Profile"
        }

        `when`(mockRepository.getDefaultProfile()).thenReturn(null)
        `when`(mockSharedPreferences.getString("default_profile", null)).thenReturn("fallback-id")
        `when`(mockRepository.getProfileById("fallback-id")).thenReturn(testProfile)

        val defaultProfile = profileManager.defaultProfile()

        assertEquals(testProfile, defaultProfile)
        verify(mockRepository).getDefaultProfile()
        verify(mockSharedPreferences).getString("default_profile", null)
        verify(mockRepository).getProfileById("fallback-id")
        verify(mockRepository).setDefaultProfile("fallback-id")
        verify(mockEditor).remove("default_profile")
    }

    @Test
    fun testDefaultProfileFallsBackToFirstProfile() {
        val firstProfile = ServerProfile().apply {
            id = "first-id"
            name = "First Profile"
        }

        `when`(mockRepository.getDefaultProfile()).thenReturn(null)
        `when`(mockSharedPreferences.getString("default_profile", null)).thenReturn(null)
        `when`(mockRepository.getAllProfiles()).thenReturn(listOf(firstProfile))

        val defaultProfile = profileManager.defaultProfile()

        assertEquals(firstProfile, defaultProfile)
        verify(mockRepository).getDefaultProfile()
        verify(mockSharedPreferences).getString("default_profile", null)
        verify(mockRepository).getAllProfiles()
    }

    @Test
    fun testDefaultProfileReturnsNullWhenNoProfiles() {
        `when`(mockRepository.getDefaultProfile()).thenReturn(null)
        `when`(mockSharedPreferences.getString("default_profile", null)).thenReturn(null)
        `when`(mockRepository.getAllProfiles()).thenReturn(emptyList())

        val defaultProfile = profileManager.defaultProfile()

        assertNull(defaultProfile)
    }

    @Test
    fun testGetProfilesByServiceType() {
        val metubeProfile = ServerProfile().apply {
            id = "test-id-1"
            name = "Profile 1"
            url = "http://example1.com"
            port = 8080
            serviceType = ServerProfile.TYPE_METUBE
            username = "user1"
            password = "pass1"
        }

        val ytdlProfile = ServerProfile().apply {
            id = "test-id-2"
            name = "Profile 2"
            url = "http://example2.com"
            port = 9090
            serviceType = ServerProfile.TYPE_YTDL
            username = null
            password = null
        }

        `when`(mockRepository.getProfilesByServiceType(ServerProfile.TYPE_METUBE)).thenReturn(listOf(metubeProfile))
        `when`(mockRepository.getProfilesByServiceType(ServerProfile.TYPE_YTDL)).thenReturn(listOf(ytdlProfile))

        val metubeProfiles = profileManager.getProfilesByServiceType(ServerProfile.TYPE_METUBE)

        assertEquals(1, metubeProfiles.size)
        assertEquals("Profile 1", metubeProfiles[0].name)
        assertEquals("user1", metubeProfiles[0].username)
        assertEquals("pass1", metubeProfiles[0].password)

        val ytdlProfiles = profileManager.getProfilesByServiceType(ServerProfile.TYPE_YTDL)
        assertEquals(1, ytdlProfiles.size)
        assertEquals("Profile 2", ytdlProfiles[0].name)
        assertEquals(null, ytdlProfiles[0].username)
        assertEquals(null, ytdlProfiles[0].password)

        verify(mockRepository).getProfilesByServiceType(ServerProfile.TYPE_METUBE)
        verify(mockRepository).getProfilesByServiceType(ServerProfile.TYPE_YTDL)
    }

    @Test
    fun testAddProfile() {
        val testProfile = ServerProfile().apply {
            name = "Test Profile"
            url = "http://example.com"
            port = 8080
            serviceType = ServerProfile.TYPE_METUBE
        }

        profileManager.addProfile(testProfile)

        assertNotNull(testProfile.id) // Should generate an ID
        assertEquals(ServerProfile.TYPE_METUBE, testProfile.serviceType) // Should set default service type
        verify(mockRepository).addProfile(testProfile)
    }

    @Test
    fun testUpdateProfile() {
        val testProfile = ServerProfile().apply {
            id = "test-id"
            name = "Updated Profile"
        }

        profileManager.updateProfile(testProfile)

        verify(mockRepository).updateProfile(testProfile)
    }

    @Test
    fun testDeleteProfile() {
        val testProfile = ServerProfile().apply {
            id = "test-id"
            name = "Test Profile"
        }

        profileManager.deleteProfile(testProfile)

        verify(mockRepository).deleteProfile(testProfile)
    }

    @Test
    fun testSetDefaultProfile() {
        val testProfile = ServerProfile().apply {
            id = "test-id"
            name = "Test Profile"
        }

        profileManager.setDefaultProfile(testProfile)

        verify(mockRepository).setDefaultProfile("test-id")
    }

    @Test
    fun testClearDefaultProfile() {
        profileManager.clearDefaultProfile()

        verify(mockRepository).clearDefaultProfile()
    }

    @Test
    fun testAllServiceTypes() {
        val metubeProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
        }
        val ytdlProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_YTDL
        }
        val anotherMetubeProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
        }

        `when`(mockRepository.getAllProfiles()).thenReturn(listOf(metubeProfile, ytdlProfile, anotherMetubeProfile))

        val serviceTypes = profileManager.allServiceTypes()

        assertEquals(2, serviceTypes.size)
        assertTrue(serviceTypes.contains(ServerProfile.TYPE_METUBE))
        assertTrue(serviceTypes.contains(ServerProfile.TYPE_YTDL))
    }

    @Test
    fun testAllTorrentClientTypes() {
        val torrentProfile1 = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = "qbittorrent"
        }
        val torrentProfile2 = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = "transmission"
        }
        val nonTorrentProfile = ServerProfile().apply {
            serviceType = ServerProfile.TYPE_METUBE
            torrentClientType = "should-not-appear"
        }

        `when`(mockRepository.getAllProfiles()).thenReturn(listOf(torrentProfile1, torrentProfile2, nonTorrentProfile))

        val clientTypes = profileManager.allTorrentClientTypes()

        assertEquals(2, clientTypes.size)
        assertTrue(clientTypes.contains("qbittorrent"))
        assertTrue(clientTypes.contains("transmission"))
    }
}