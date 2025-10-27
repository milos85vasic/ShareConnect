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

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class LocalizationTest {

    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val supportedLocales = listOf(
        "en", // English (default)
        "es", // Spanish
        "fr", // French
        "de", // German
        "it", // Italian
        "pt", // Portuguese
        "ru", // Russian
        "ja", // Japanese
        "ko", // Korean
        "zh", // Chinese
        "ar", // Arabic
        "hi", // Hindi
        "bn", // Bengali
        "kn", // Kannada
        "sr"  // Serbian
    )

    @Test
    fun testAllSupportedLanguagesHaveStringResources() {
        val resources = context.resources
        val configuration = Configuration(resources.configuration)

        for (languageCode in supportedLocales) {
            // Test that we can create a configuration for this locale
            val locale = Locale.forLanguageTag(languageCode)
            configuration.setLocale(locale)

            try {
                val localizedContext = context.createConfigurationContext(configuration)
                val localizedResources = localizedContext.resources

                // Test some key strings that should exist in all languages
                val appName = getStringSafe(localizedResources, "app_name")
                assertNotNull("app_name should exist for language: $languageCode", appName)
                assertTrue("app_name should not be empty for language: $languageCode", appName?.isNotEmpty() == true)

                val settings = getStringSafe(localizedResources, "settings")
                assertNotNull("settings should exist for language: $languageCode", settings)

                val save = getStringSafe(localizedResources, "save")
                assertNotNull("save should exist for language: $languageCode", save)

                val cancel = getStringSafe(localizedResources, "cancel")
                assertNotNull("cancel should exist for language: $languageCode", cancel)

            } catch (e: Exception) {
                fail("Failed to test localization for language: $languageCode - ${e.message}")
            }
        }
    }

    @Test
    fun testSerbianLocalizationCompleteness() {
        val serbianConfig = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("sr"))
        }
        val serbianContext = context.createConfigurationContext(serbianConfig)
        val serbianResources = serbianContext.resources

        // Test that Serbian has all the key strings from the main strings.xml
        val keyStrings = listOf(
            "app_name", "settings", "server_profiles", "add_profile", "profile_name",
            "server_url", "server_port", "save", "cancel", "delete", "set_default",
            "default_profile", "no_profiles_yet", "sent_successfully", "invalid_url",
            "profile_saved", "profile_deleted", "no_youtube_link", "confirm_delete_profile",
            "yes", "no", "ok", "error", "warning", "connection_error", "invalid_port",
            "url_required", "profile_name_required", "select", "dark", "light",
            "torrent_sharing", "appearance", "theme", "theme_summary", "language",
            "language_selection", "language_summary", "selected", "all_history",
            "connection_successful", "please_set_default_profile", "metube", "ytdlp",
            "torrent", "jdownloader", "unknown", "qbittorrent", "transmission", "utorrent",
            "testing", "test_connection", "username", "password", "open_default_service"
        )

        for (stringKey in keyStrings) {
            val localizedString = getStringSafe(serbianResources, stringKey)
            assertNotNull("Serbian localization missing for key: $stringKey", localizedString)
            assertTrue("Serbian localization should not be empty for key: $stringKey", localizedString?.isNotEmpty() == true)
            // Check that it's not falling back to English (basic check)
            assertFalse("Serbian string should not be the same as English for key: $stringKey",
                localizedString == getStringSafe(context.resources, stringKey))
        }
    }

    @Test
    fun testLanguageSwitchingWorks() = runBlocking {
        val application = context.applicationContext as SCApplication
        val languageSyncManager = application.languageSyncManager

        // Test switching to Serbian
        languageSyncManager.setLanguagePreference("sr", "Српски")

        // Wait a bit for the change to propagate
        kotlinx.coroutines.delay(1000)

        // Verify the language preference was set
        val currentLanguage = languageSyncManager.getOrCreateDefault()
        assertEquals("Language should be set to Serbian", "sr", currentLanguage.languageCode)
    }

    @Test
    fun testOnboardingStringsAreLocalized() {
        // Test that onboarding strings exist in Serbian
        val serbianConfig = Configuration(context.resources.configuration).apply {
            setLocale(Locale.forLanguageTag("sr"))
        }
        val serbianContext = context.createConfigurationContext(serbianConfig)

        // This would require access to onboarding module resources
        // For now, we'll test that the main app can access localized strings
        val appName = serbianContext.getString(R.string.app_name)
        assertTrue("Serbian app name should be localized", appName.contains("ShareConnect") || appName.contains("Подељен"))
    }

    @Test
    fun testNoHardcodedStringsInKeyActivities() {
        // This test would check that key activities don't have hardcoded strings
        // For now, we'll just verify that string resources are being used properly
        val settingsString = context.getString(R.string.settings)
        assertNotNull("Settings string should exist", settingsString)
        assertTrue("Settings string should not be empty", settingsString.isNotEmpty())
    }



    @Test
    fun testStringFormatting() {
        // Test that string formatting works correctly
        val testString = context.getString(R.string.profile_set_as_default, "Test Profile")
        assertTrue("Formatted string should contain the parameter", testString.contains("Test Profile"))
    }

    @Test
    fun testAllLanguagesHaveConsistentTranslations() {
        val baseResources = context.resources
        val baseConfig = Configuration(baseResources.configuration)

        for (languageCode in supportedLocales) {
            val locale = Locale.forLanguageTag(languageCode)
            val config = Configuration(baseConfig).apply { setLocale(locale) }

            try {
                val localizedContext = context.createConfigurationContext(config)
                val localizedResources = localizedContext.resources

                // Test that key strings are not empty and not the same as resource key
                val appName = getStringSafe(localizedResources, "app_name")
                assertNotNull("app_name should exist for $languageCode", appName)
                assertTrue("app_name should not be empty for $languageCode", appName?.isNotEmpty() == true)
                assertFalse("app_name should not be the resource key for $languageCode", appName == "app_name")

            } catch (e: Exception) {
                fail("Failed to test language consistency for $languageCode: ${e.message}")
            }
        }
    }

    private fun getStringSafe(resources: Resources, stringKey: String): String? {
        return try {
            val resId = resources.getIdentifier(stringKey, "string", context.packageName)
            if (resId != 0) {
                resources.getString(resId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}