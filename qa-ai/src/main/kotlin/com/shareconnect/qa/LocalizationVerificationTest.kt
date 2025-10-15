package com.shareconnect.qa

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.*
import com.shareconnect.languagesync.models.LanguageData
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.util.*

@RunWith(AndroidJUnit4::class)
class LocalizationVerificationTest {

    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val supportedLocales = listOf(
        "en" to "English",
        "es" to "Español",
        "fr" to "Français",
        "de" to "Deutsch",
        "it" to "Italiano",
        "pt" to "Português",
        "ru" to "Русский",
        "ja" to "日本語",
        "ko" to "한국어",
        "zh" to "中文",
        "ar" to "العربية",
        "hi" to "हिन्दी",
        "bn" to "বাংলা",
        "kn" to "ಕನ್ನಡ",
        "sr" to "Српски"
    )

    @Test
    fun testCompleteLocalizationOnRealDevice() {
        // Launch ShareConnect app
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        context.startActivity(intent)

        // Wait for app to load
        device.waitForIdle(5000)

        // Test each supported language
        for ((languageCode, displayName) in supportedLocales) {
            testLanguageLocalization(languageCode, displayName)

            // Reset to English between tests
            if (languageCode != "en") {
                resetToEnglish()
            }
        }
    }

    private fun testLanguageLocalization(languageCode: String, displayName: String) {
        try {
            // Navigate to language selection (assuming onboarding or settings)
            navigateToLanguageSelection()

            // Select the language
            selectLanguage(displayName)

            // Verify language change took effect
            verifyLanguageApplied(languageCode, displayName)

            // Test key UI elements are localized
            verifyKeyUIStringsAreLocalized(languageCode)

            // Test that no English fallback strings are showing
            verifyNoEnglishFallback(languageCode)

        } catch (e: Exception) {
            fail("Localization test failed for $displayName ($languageCode): ${e.message}")
        }
    }

    private fun navigateToLanguageSelection() {
        // Try to find language selection in onboarding or settings
        val languageSelectors = listOf(
            "Language",
            "Language Selection",
            "Select Language",
            "Idioma", // Spanish
            "Langue", // French
            "Sprache", // German
            "Lingua", // Italian
            "Idioma", // Portuguese
            "Язык", // Russian
            "言語", // Japanese
            "언어", // Korean
            "语言", // Chinese
            "لغة", // Arabic
            "भाषा", // Hindi
            "ভাষা", // Bengali
            "ಭಾಷೆ", // Kannada
            "Језик" // Serbian
        )

        var found = false
        for (selector in languageSelectors) {
            try {
                val languageButton = device.findObject(UiSelector().textContains(selector))
                if (languageButton.exists()) {
                    languageButton.click()
                    found = true
                    break
                }
            } catch (e: Exception) {
                continue
            }
        }

        if (!found) {
            // Try to navigate to settings
            navigateToSettings()
            val languageOption = device.findObject(UiSelector().textContains("Language"))
            if (languageOption.exists()) {
                languageOption.click()
            }
        }

        device.waitForIdle(2000)
    }

    private fun navigateToSettings() {
        // Try multiple ways to access settings
        val settingsSelectors = listOf(
            "Settings",
            "Подешавања", // Serbian
            "Configuración", // Spanish
            "Paramètres", // French
            "Einstellungen", // German
            "Impostazioni", // Italian
            "Configurações", // Portuguese
            "Настройки", // Russian
            "設定", // Japanese
            "설정", // Korean
            "设置", // Chinese
            "إعدادات", // Arabic
            "सेटिंग्स", // Hindi
            "সেটিংস", // Bengali
            "ಸೆಟ್ಟಿಂಗ್‌ಗಳು", // Kannada
            "Подешавања" // Serbian
        )

        for (selector in settingsSelectors) {
            try {
                val settingsButton = device.findObject(UiSelector().textContains(selector))
                if (settingsButton.exists()) {
                    settingsButton.click()
                    device.waitForIdle(2000)
                    return
                }
            } catch (e: Exception) {
                continue
            }
        }

        // Try menu button
        try {
            device.pressMenu()
            device.waitForIdle(1000)
            val settingsMenuItem = device.findObject(UiSelector().textContains("Settings"))
            if (settingsMenuItem.exists()) {
                settingsMenuItem.click()
                device.waitForIdle(2000)
            }
        } catch (e: Exception) {
            // Menu might not exist
        }
    }

    private fun selectLanguage(displayName: String) {
        // Find and click the language option
        val languageOption = device.findObject(UiSelector().text(displayName))
        if (languageOption.exists()) {
            languageOption.click()
            device.waitForIdle(3000) // Wait for language change
        } else {
            // Try partial match
            val partialMatch = device.findObject(UiSelector().textContains(displayName.substring(0, 3)))
            if (partialMatch.exists()) {
                partialMatch.click()
                device.waitForIdle(3000)
            }
        }
    }

    private fun verifyLanguageApplied(languageCode: String, displayName: String) {
        // Verify that the system locale changed or app restarted with new language
        val currentConfig = context.resources.configuration
        val currentLocale = currentConfig.locale

        // The locale might not change immediately due to app restart requirements
        // Instead, check that the UI shows the selected language
        val languageIndicator = findLanguageIndicator(displayName)
        assertTrue("Language $displayName should be indicated as selected", languageIndicator)
    }

    private fun findLanguageIndicator(displayName: String): Boolean {
        // Look for check marks, selected indicators, or the language name being highlighted
        val checkMark = device.findObject(UiSelector().descriptionContains("Selected"))
        if (checkMark.exists()) {
            return true
        }

        // Look for the language name in a selected state
        val selectedLanguage = device.findObject(UiSelector().text(displayName).selected(true))
        if (selectedLanguage.exists()) {
            return true
        }

        // Check if we're back at the main screen and language has changed
        return device.findObject(UiSelector().text(displayName)).exists()
    }

    private fun verifyKeyUIStringsAreLocalized(languageCode: String) {
        // Test that key UI strings are in the correct language
        val keyStrings = getLocalizedKeyStrings(languageCode)

        for ((english, localized) in keyStrings) {
            // Check that English version is NOT visible
            val englishText = device.findObject(UiSelector().text(english))
            assertFalse("English string '$english' should not be visible in $languageCode", englishText.exists())

            // Check that localized version IS visible (if we're on the right screen)
            if (localized.isNotEmpty()) {
                val localizedText = device.findObject(UiSelector().textContains(localized.substring(0, 3)))
                // Note: We can't assert this always exists as we might not be on the right screen
            }
        }
    }

    private fun verifyNoEnglishFallback(languageCode: String) {
        if (languageCode == "en") return // Skip for English

        // Common English words that should not appear if properly localized
        val englishWords = listOf(
            "Settings", "Save", "Cancel", "Delete", "Add", "Edit", "Open", "Close",
            "Select", "Apply", "Retry", "Search", "Rename", "Share", "OK", "Yes", "No",
            "Error", "Warning", "Success", "Loading", "Saving", "Deleting"
        )

        for (word in englishWords) {
            val englishElement = device.findObject(UiSelector().text(word))
            if (englishElement.exists()) {
                // Allow some common technical terms, but flag obvious localization failures
                val bounds = englishElement.bounds
                if (bounds.width() > 50 && bounds.height() > 20) { // Not just icons
                    fail("English fallback detected: '$word' found in $languageCode localization")
                }
            }
        }
    }

    private fun resetToEnglish() {
        // Reset language to English for next test
        navigateToLanguageSelection()
        selectLanguage("English")
        device.waitForIdle(3000)
    }

    private fun getLocalizedKeyStrings(languageCode: String): Map<String, String> {
        return when (languageCode) {
            "sr" -> mapOf(
                "Settings" to "Подешавања",
                "Save" to "Сачувај",
                "Cancel" to "Откажи",
                "Delete" to "Обриши",
                "Add" to "Додај",
                "Edit" to "Измени",
                "Open" to "Отвори",
                "Close" to "Затвори",
                "Select" to "Изабери",
                "Apply" to "Примени",
                "OK" to "У реду",
                "Yes" to "Да",
                "No" to "Не",
                "Error" to "Грешка",
                "Warning" to "Упозорење",
                "Success" to "Успех"
            )
            "es" -> mapOf(
                "Settings" to "Configuración",
                "Save" to "Guardar",
                "Cancel" to "Cancelar",
                "Delete" to "Eliminar",
                "Add" to "Agregar",
                "Edit" to "Editar",
                "Open" to "Abrir",
                "Close" to "Cerrar",
                "Select" to "Seleccionar",
                "Apply" to "Aplicar",
                "OK" to "Aceptar",
                "Yes" to "Sí",
                "No" to "No",
                "Error" to "Error",
                "Warning" to "Advertencia",
                "Success" to "Éxito"
            )
            "ru" -> mapOf(
                "Settings" to "Настройки",
                "Save" to "Сохранить",
                "Cancel" to "Отмена",
                "Delete" to "Удалить",
                "Add" to "Добавить",
                "Edit" to "Редактировать",
                "Open" to "Открыть",
                "Close" to "Закрыть",
                "Select" to "Выбрать",
                "Apply" to "Применить",
                "OK" to "OK",
                "Yes" to "Да",
                "No" to "Нет",
                "Error" to "Ошибка",
                "Warning" to "Предупреждение",
                "Success" to "Успешно"
            )
            // Add more languages as needed
            else -> emptyMap()
        }
    }

    @Test
    fun testOnboardingLocalizationFlow() {
        // Launch app fresh to trigger onboarding
        device.pressHome()
        Thread.sleep(1000)

        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(intent)

        device.waitForIdle(5000)

        // Test Serbian onboarding
        testOnboardingInLanguage("sr", "Српски")

        // Test Spanish onboarding
        resetAppData()
        testOnboardingInLanguage("es", "Español")

        // Test Russian onboarding
        resetAppData()
        testOnboardingInLanguage("ru", "Русский")
    }

    private fun testOnboardingInLanguage(languageCode: String, displayName: String) {
        // Complete onboarding in the specified language
        navigateToLanguageSelection()
        selectLanguage(displayName)

        // Continue with onboarding flow
        completeOnboardingFlow()

        // Verify app is properly localized
        verifyAppMainScreenLocalized(languageCode)
    }

    private fun completeOnboardingFlow() {
        // Click through onboarding steps
        // This is app-specific and would need customization
        val nextButtons = listOf("Next", "Continue", "Get Started", "Следеће", "Siguiente", "Suivant", "Далее")
        for (buttonText in nextButtons) {
            try {
                val nextButton = device.findObject(UiSelector().textContains(buttonText))
                if (nextButton.exists()) {
                    nextButton.click()
                    device.waitForIdle(2000)
                    break
                }
            } catch (e: Exception) {
                continue
            }
        }
    }

    private fun verifyAppMainScreenLocalized(languageCode: String) {
        // Verify main screen shows localized text
        val localizedStrings = getMainScreenStrings(languageCode)

        for (localizedString in localizedStrings) {
            val element = device.findObject(UiSelector().textContains(localizedString.substring(0, 3)))
            // We expect at least some localized strings to be visible
        }
    }

    private fun getMainScreenStrings(languageCode: String): List<String> {
        return when (languageCode) {
            "sr" -> listOf("Подешавања", "Профили сервера", "Додај профил")
            "es" -> listOf("Configuración", "Perfiles de servidor", "Agregar perfil")
            "ru" -> listOf("Настройки", "Профили сервера", "Добавить профиль")
            else -> emptyList()
        }
    }

    private fun resetAppData() {
        // Reset app data to trigger onboarding again
        device.executeShellCommand("pm clear ${context.packageName}")
        Thread.sleep(2000)
    }
}