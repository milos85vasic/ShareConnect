package com.shareconnect.activities

import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.EditProfileActivity
import com.shareconnect.R
import com.shareconnect.ServerProfile
import com.shareconnect.manager.ProfileManager
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditProfileActivityTest {

    private lateinit var profileManager: ProfileManager
    private lateinit var testProfile: ServerProfile

    @Before
    fun setUp() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        profileManager = ProfileManager(context)

        // Create a test profile
        testProfile = ServerProfile().apply {
            id = "test_profile_id"
            name = "Test Torrent Profile"
            url = "http://localhost"
            port = 8080
            serviceType = ServerProfile.TYPE_TORRENT
            torrentClientType = ServerProfile.TORRENT_CLIENT_QBITTORRENT
        }
    }

    @After
    fun tearDown() {
        // Clean up test profile if it exists
        try {
            profileManager.removeProfile(testProfile)
        } catch (e: Exception) {
            // Profile might not exist, ignore
        }
    }

    @Test
    fun testCreateTorrentProfile_showsTorrentClientField() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Select torrent service type
            onView(withId(R.id.autoCompleteServiceType))
                .perform(click())

            onView(withText(R.string.service_type_torrent))
                .perform(click())

            // Check that torrent client field is visible
            onView(withId(R.id.layoutTorrentClient))
                .check(matches(isDisplayed()))

            // Check that torrent client dropdown is visible
            onView(withId(R.id.autoCompleteTorrentClient))
                .check(matches(isDisplayed()))
        }
    }

    @Test
    fun testCreateNonTorrentProfile_hidesTorrentClientField() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Select MeTube service type (default)
            onView(withId(R.id.autoCompleteServiceType))
                .perform(click())

            onView(withText(R.string.service_type_metube))
                .perform(click())

            // Check that torrent client field is not visible
            onView(withId(R.id.layoutTorrentClient))
                .check(matches(withEffectiveVisibility(Visibility.GONE)))
        }
    }

    @Test
    fun testCreateQBitTorrentProfile_savesCorrectly() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Fill in profile details
            onView(withId(R.id.editTextProfileName))
                .perform(typeText("Test qBittorrent Profile"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerUrl))
                .perform(typeText("http://localhost"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerPort))
                .perform(typeText("8080"), closeSoftKeyboard())

            // Select torrent service type
            onView(withId(R.id.autoCompleteServiceType))
                .perform(click())

            onView(withText(R.string.service_type_torrent))
                .perform(click())

            // Select qBittorrent client
            onView(withId(R.id.autoCompleteTorrentClient))
                .perform(click())

            onView(withText(R.string.torrent_client_qbittorrent))
                .perform(click())

            // Save profile
            onView(withId(R.id.buttonSave))
                .perform(click())

            // Verify profile was saved
            val savedProfiles = profileManager.profiles
            val savedProfile = savedProfiles.find { it.name == "Test qBittorrent Profile" }
            assertTrue("Profile should be saved", savedProfile != null)
            assertTrue("Profile should be torrent type", savedProfile?.isTorrent() == true)
            assertTrue("Profile should be qBittorrent client",
                savedProfile?.torrentClientType == ServerProfile.TORRENT_CLIENT_QBITTORRENT)
        }
    }

    @Test
    fun testCreateTransmissionProfile_savesCorrectly() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Fill in profile details
            onView(withId(R.id.editTextProfileName))
                .perform(typeText("Test Transmission Profile"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerUrl))
                .perform(typeText("http://localhost"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerPort))
                .perform(typeText("9091"), closeSoftKeyboard())

            // Select torrent service type
            onView(withId(R.id.autoCompleteServiceType))
                .perform(click())

            onView(withText(R.string.service_type_torrent))
                .perform(click())

            // Select Transmission client
            onView(withId(R.id.autoCompleteTorrentClient))
                .perform(click())

            onView(withText(R.string.torrent_client_transmission))
                .perform(click())

            // Save profile
            onView(withId(R.id.buttonSave))
                .perform(click())

            // Verify profile was saved
            val savedProfiles = profileManager.profiles
            val savedProfile = savedProfiles.find { it.name == "Test Transmission Profile" }
            assertTrue("Profile should be saved", savedProfile != null)
            assertTrue("Profile should be torrent type", savedProfile?.isTorrent() == true)
            assertTrue("Profile should be Transmission client",
                savedProfile?.torrentClientType == ServerProfile.TORRENT_CLIENT_TRANSMISSION)
        }
    }

    @Test
    fun testEditExistingProfile_loadsCorrectly() {
        // First save a test profile
        profileManager.addProfile(testProfile)

        // Launch activity for editing existing profile
        val intent = android.content.Intent(
            InstrumentationRegistry.getInstrumentation().targetContext,
            EditProfileActivity::class.java
        ).apply {
            putExtra("profile_id", testProfile.id)
        }

        ActivityScenario.launch<EditProfileActivity>(intent).use { scenario ->
            // Check that fields are populated correctly
            onView(withId(R.id.editTextProfileName))
                .check(matches(withText(testProfile.name)))

            onView(withId(R.id.editTextServerUrl))
                .check(matches(withText(testProfile.url)))

            onView(withId(R.id.editTextServerPort))
                .check(matches(withText(testProfile.port.toString())))

            // Check that torrent client field is visible
            onView(withId(R.id.layoutTorrentClient))
                .check(matches(isDisplayed()))

            // Check that correct torrent client is selected
            onView(withId(R.id.autoCompleteTorrentClient))
                .check(matches(withText(getString(R.string.torrent_client_qbittorrent))))
        }
    }

    @Test
    fun testValidation_requiresProfileName() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Leave profile name empty, fill other fields
            onView(withId(R.id.editTextServerUrl))
                .perform(typeText("http://localhost"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerPort))
                .perform(typeText("8080"), closeSoftKeyboard())

            // Try to save
            onView(withId(R.id.buttonSave))
                .perform(click())

            // Check that error is shown
            onView(withId(R.id.editTextProfileName))
                .check(matches(hasErrorText(getString(R.string.profile_name_required))))
        }
    }

    @Test
    fun testValidation_requiresValidUrl() {
        // Launch activity for new profile
        ActivityScenario.launch(EditProfileActivity::class.java).use { scenario ->
            // Fill profile name
            onView(withId(R.id.editTextProfileName))
                .perform(typeText("Test Profile"), closeSoftKeyboard())

            // Enter invalid URL
            onView(withId(R.id.editTextServerUrl))
                .perform(typeText("invalid-url"), closeSoftKeyboard())

            onView(withId(R.id.editTextServerPort))
                .perform(typeText("8080"), closeSoftKeyboard())

            // Try to save
            onView(withId(R.id.buttonSave))
                .perform(click())

            // Check that error is shown
            onView(withId(R.id.editTextServerUrl))
                .check(matches(hasErrorText(getString(R.string.invalid_url))))
        }
    }

    private fun getString(resId: Int): String {
        return InstrumentationRegistry.getInstrumentation().targetContext.getString(resId)
    }
}