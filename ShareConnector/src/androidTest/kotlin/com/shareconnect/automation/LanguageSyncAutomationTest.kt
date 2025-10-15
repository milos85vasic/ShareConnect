package com.shareconnect.automation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.shareconnect.SCApplication
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.languagesync.utils.LanguageUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class LanguageSyncAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var application: SCApplication

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        application = context.applicationContext as SCApplication
    }

    @Test
    fun testLanguageSyncManagerInitialization() = runBlocking {
        val languageSyncManager = application.languageSyncManager
        assertNotNull("LanguageSyncManager should be initialized", languageSyncManager)

        // Wait for initialization
        delay(1000)

        val language = languageSyncManager.getOrCreateDefault()
        assertNotNull("Should have default language loaded", language)
    }

    @Test
    fun testDefaultLanguageIsSet() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val defaultLanguage = languageSyncManager.getOrCreateDefault()
        assertNotNull("Default language should be set", defaultLanguage)
        assertTrue(
            "Default should be system default or explicit language",
            defaultLanguage.languageCode == LanguageData.CODE_SYSTEM_DEFAULT ||
            LanguageUtils.isSupportedLanguage(defaultLanguage.languageCode)
        )
    }

    @Test
    fun testAllSupportedLanguagesAvailable() {
        val availableLanguages = LanguageData.getAvailableLanguages()

        assertTrue("Should have at least 16 languages", availableLanguages.size >= 16)

        // Verify system default is included
        val systemDefault = availableLanguages.find { it.first == LanguageData.CODE_SYSTEM_DEFAULT }
        assertNotNull("Should have system default option", systemDefault)

        // Verify specific languages
        assertTrue("Should have English", availableLanguages.any { it.first == LanguageData.CODE_ENGLISH })
        assertTrue("Should have Spanish", availableLanguages.any { it.first == LanguageData.CODE_SPANISH })
        assertTrue("Should have French", availableLanguages.any { it.first == LanguageData.CODE_FRENCH })
        assertTrue("Should have German", availableLanguages.any { it.first == LanguageData.CODE_GERMAN })
        assertTrue("Should have Russian", availableLanguages.any { it.first == LanguageData.CODE_RUSSIAN })
        assertTrue("Should have Chinese", availableLanguages.any { it.first == LanguageData.CODE_CHINESE })
        assertTrue("Should have Japanese", availableLanguages.any { it.first == LanguageData.CODE_JAPANESE })
        assertTrue("Should have Korean", availableLanguages.any { it.first == LanguageData.CODE_KOREAN })
        assertTrue("Should have Arabic", availableLanguages.any { it.first == LanguageData.CODE_ARABIC })
        assertTrue("Should have Portuguese", availableLanguages.any { it.first == LanguageData.CODE_PORTUGUESE })
        assertTrue("Should have Italian", availableLanguages.any { it.first == LanguageData.CODE_ITALIAN })
        assertTrue("Should have Hungarian", availableLanguages.any { it.first == LanguageData.CODE_HUNGARIAN })
        assertTrue("Should have Serbian", availableLanguages.any { it.first == LanguageData.CODE_SERBIAN })
        assertTrue("Should have Belarusian", availableLanguages.any { it.first == LanguageData.CODE_BELARUSIAN })
        assertTrue("Should have Kannada", availableLanguages.any { it.first == LanguageData.CODE_KANNADA })
    }

    @Test
    fun testLanguageChangeNotification() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val initialLanguage = languageSyncManager.getOrCreateDefault()
        assertNotNull(initialLanguage)

        // Change to a different language
        val newLanguageCode = if (initialLanguage.languageCode == LanguageData.CODE_ENGLISH) {
            LanguageData.CODE_SPANISH
        } else {
            LanguageData.CODE_ENGLISH
        }

        languageSyncManager.setLanguagePreference(newLanguageCode, "Test Language")

        // Wait for change to propagate
        delay(500)

        val newLanguage = languageSyncManager.getOrCreateDefault()
        assertEquals("Language should have changed", newLanguageCode, newLanguage.languageCode)
    }

    @Test
    fun testLanguagePreferenceExists() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val language = languageSyncManager.getOrCreateDefault()
        assertNotNull("Should have language preference", language)
        assertNotNull("Language code should not be null", language.languageCode)
        assertNotNull("Display name should not be null", language.displayName)
    }

    @Test
    fun testLanguageVersioning() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val initialLanguage = languageSyncManager.getOrCreateDefault()
        assertNotNull(initialLanguage)

        val initialVersion = initialLanguage.version

        // Update the language (this increments version)
        languageSyncManager.setLanguagePreference(
            initialLanguage.languageCode,
            initialLanguage.displayName
        )

        delay(500)

        val updatedLanguage = languageSyncManager.getOrCreateDefault()
        assertTrue(
            "Version should increment after update",
            updatedLanguage.version > initialVersion
        )
    }

    @Test
    fun testLanguageLastModifiedTimestamp() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val initialLanguage = languageSyncManager.getOrCreateDefault()
        assertNotNull(initialLanguage)

        val initialTimestamp = initialLanguage.lastModified

        // Wait a moment
        delay(100)

        // Update the language
        languageSyncManager.setLanguagePreference(
            initialLanguage.languageCode,
            initialLanguage.displayName
        )

        delay(500)

        val updatedLanguage = languageSyncManager.getOrCreateDefault()
        assertTrue(
            "LastModified should update",
            updatedLanguage.lastModified > initialTimestamp
        )
    }

    @Test
    fun testLanguageIdConsistency() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        val language = languageSyncManager.getOrCreateDefault()
        assertEquals("Language preference ID should be constant", "language_preference", language.id)
    }

    @Test
    fun testLanguageCodeValidation() {
        // Test valid language codes
        assertTrue("English should be valid", LanguageUtils.isSupportedLanguage(LanguageData.CODE_ENGLISH))
        assertTrue("Spanish should be valid", LanguageUtils.isSupportedLanguage(LanguageData.CODE_SPANISH))
        assertTrue("French should be valid", LanguageUtils.isSupportedLanguage(LanguageData.CODE_FRENCH))

        // Test invalid language code
        assertFalse("Invalid code should return false", LanguageUtils.isSupportedLanguage("invalid"))
        assertFalse("Empty code should return false", LanguageUtils.isSupportedLanguage(""))
    }

    @Test
    fun testLanguageDisplayNames() {
        // Test display names for supported languages
        assertNotNull("English should have display name", LanguageUtils.getDisplayName(LanguageData.CODE_ENGLISH))
        assertNotNull("Spanish should have display name", LanguageUtils.getDisplayName(LanguageData.CODE_SPANISH))
        assertNotNull("French should have display name", LanguageUtils.getDisplayName(LanguageData.CODE_FRENCH))
        assertNotNull("Russian should have display name", LanguageUtils.getDisplayName(LanguageData.CODE_RUSSIAN))
        assertNotNull("Chinese should have display name", LanguageUtils.getDisplayName(LanguageData.CODE_CHINESE))

        // Test invalid language code
        assertNull("Invalid code should return null", LanguageUtils.getDisplayName("invalid"))
    }

    @Test
    fun testSystemDefaultLanguage() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        // Set to system default
        languageSyncManager.setLanguagePreference(
            LanguageData.CODE_SYSTEM_DEFAULT,
            "System Default"
        )

        delay(500)

        val language = languageSyncManager.getOrCreateDefault()
        assertEquals("Should be system default", LanguageData.CODE_SYSTEM_DEFAULT, language.languageCode)
        assertTrue("System default flag should be true", language.isSystemDefault)
    }

    @Test
    fun testExplicitLanguageSelection() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        // Set to explicit language
        languageSyncManager.setLanguagePreference(
            LanguageData.CODE_SPANISH,
            "Español"
        )

        delay(500)

        val language = languageSyncManager.getOrCreateDefault()
        assertEquals("Should be Spanish", LanguageData.CODE_SPANISH, language.languageCode)
        assertFalse("System default flag should be false", language.isSystemDefault)
    }

    @Test
    fun testLocaleApplicationForEnglish() {
        val locale = Locale.forLanguageTag("en")
        val context = LanguageUtils.applyLanguage(this.context, LanguageData.CODE_ENGLISH)

        assertNotNull("Context should not be null", context)
        assertEquals("Locale should be English", "en", context.resources.configuration.locales.get(0).language)
    }

    @Test
    fun testLocaleApplicationForSpanish() {
        val context = LanguageUtils.applyLanguage(this.context, LanguageData.CODE_SPANISH)

        assertNotNull("Context should not be null", context)
        assertEquals("Locale should be Spanish", "es", context.resources.configuration.locales.get(0).language)
    }

    @Test
    fun testLocaleApplicationForSystemDefault() {
        val context = LanguageUtils.applyLanguage(this.context, LanguageData.CODE_SYSTEM_DEFAULT)

        assertNotNull("Context should not be null", context)
        // Should use system locale (can't test exact value as it depends on system settings)
    }

    @Test
    fun testGetCurrentLocale() {
        val locale = LanguageUtils.getCurrentLocale(context)

        assertNotNull("Current locale should not be null", locale)
        assertNotNull("Locale language should not be null", locale.language)
        assertTrue("Locale language should not be empty", locale.language.isNotEmpty())
    }

    @Test
    fun testLanguageCodeFromLocale() {
        val englishLocale = Locale.forLanguageTag("en")
        val code = LanguageUtils.localeToLanguageCode(englishLocale)

        assertEquals("Should return en", "en", code)
    }

    @Test
    fun testMultipleLanguageSwitches() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        // Switch through multiple languages
        val languages = listOf(
            LanguageData.CODE_ENGLISH to "English",
            LanguageData.CODE_SPANISH to "Español",
            LanguageData.CODE_FRENCH to "Français",
            LanguageData.CODE_GERMAN to "Deutsch"
        )

        for ((code, name) in languages) {
            languageSyncManager.setLanguagePreference(code, name)
            delay(300)

            val currentLanguage = languageSyncManager.getOrCreateDefault()
            assertEquals("Language should match", code, currentLanguage.languageCode)
        }
    }

    @Test
    fun testLanguagePreferenceFlow() = runBlocking {
        val languageSyncManager = application.languageSyncManager

        // Wait for initialization
        delay(1000)

        var changeDetected = false
        val job = CoroutineScope(Dispatchers.Default).launch {
            languageSyncManager.languageChangeFlow.collect {
                changeDetected = true
            }
        }

        // Change language
        languageSyncManager.setLanguagePreference(
            LanguageData.CODE_FRENCH,
            "Français"
        )

        delay(500)

        assertTrue("Language change flow should emit", changeDetected)

        job.cancel()
    }

    @Test
    fun testUniquePortCalculationPerApp() {
        // Test that different app IDs generate different ports
        val appId1 = "com.shareconnect"
        val appId2 = "com.shareconnect.transmissionconnect"
        val appId3 = "com.shareconnect.utorrentconnect"

        val basePort = 8890

        val port1 = basePort + Math.abs(appId1.hashCode() % 100)
        val port2 = basePort + Math.abs(appId2.hashCode() % 100)
        val port3 = basePort + Math.abs(appId3.hashCode() % 100)

        // Verify ports are different
        assertNotEquals("Different app IDs should generate different ports", port1, port2)
        assertNotEquals("Different app IDs should generate different ports", port1, port3)
        assertNotEquals("Different app IDs should generate different ports", port2, port3)

        // Verify ports are within reasonable range
        assertTrue("Port should be >= base port", port1 >= basePort)
        assertTrue("Port should be >= base port", port2 >= basePort)
        assertTrue("Port should be >= base port", port3 >= basePort)
        assertTrue("Port should be <= base port + 99", port1 <= basePort + 99)
        assertTrue("Port should be <= base port + 99", port2 <= basePort + 99)
        assertTrue("Port should be <= base port + 99", port3 <= basePort + 99)
    }
}
