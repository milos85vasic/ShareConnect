package com.shareconnect.languagesync.models

import org.junit.Assert.*
import org.junit.Test

class LanguageDataTest {

    @Test
    fun testCreateDefault() {
        val defaultLanguage = LanguageData.createDefault()

        assertEquals("language_preference", defaultLanguage.id)
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, defaultLanguage.languageCode)
        assertEquals(LanguageData.NAME_SYSTEM_DEFAULT, defaultLanguage.displayName)
        assertTrue(defaultLanguage.isSystemDefault)
        assertEquals(1, defaultLanguage.version)
    }

    @Test
    fun testGetAvailableLanguages() {
        val languages = LanguageData.getAvailableLanguages()

        // Should have 16 languages (system default + 15 languages)
        assertEquals(16, languages.size)

        // First should be system default
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, languages[0].first)
        assertEquals(LanguageData.NAME_SYSTEM_DEFAULT, languages[0].second)

        // Verify some specific languages are present
        assertTrue(languages.any { it.first == LanguageData.CODE_ENGLISH })
        assertTrue(languages.any { it.first == LanguageData.CODE_SPANISH })
        assertTrue(languages.any { it.first == LanguageData.CODE_RUSSIAN })
        assertTrue(languages.any { it.first == LanguageData.CODE_CHINESE })
    }

    @Test
    fun testLanguageDataCopy() {
        val original = LanguageData.createDefault()
        val updated = original.copy(
            languageCode = LanguageData.CODE_ENGLISH,
            displayName = LanguageData.NAME_ENGLISH,
            isSystemDefault = false,
            version = 2
        )

        assertEquals("language_preference", updated.id)
        assertEquals(LanguageData.CODE_ENGLISH, updated.languageCode)
        assertEquals(LanguageData.NAME_ENGLISH, updated.displayName)
        assertFalse(updated.isSystemDefault)
        assertEquals(2, updated.version)
    }

    @Test
    fun testLanguageCodesMatch() {
        // Verify all codes have corresponding names
        assertEquals(LanguageData.CODE_ENGLISH, "en")
        assertEquals(LanguageData.CODE_ARABIC, "ar")
        assertEquals(LanguageData.CODE_BELARUSIAN, "be")
        assertEquals(LanguageData.CODE_GERMAN, "de")
        assertEquals(LanguageData.CODE_SPANISH, "es")
        assertEquals(LanguageData.CODE_FRENCH, "fr")
        assertEquals(LanguageData.CODE_HUNGARIAN, "hu")
        assertEquals(LanguageData.CODE_ITALIAN, "it")
        assertEquals(LanguageData.CODE_JAPANESE, "ja")
        assertEquals(LanguageData.CODE_KANNADA, "kn")
        assertEquals(LanguageData.CODE_KOREAN, "ko")
        assertEquals(LanguageData.CODE_PORTUGUESE, "pt")
        assertEquals(LanguageData.CODE_RUSSIAN, "ru")
        assertEquals(LanguageData.CODE_SERBIAN, "sr")
        assertEquals(LanguageData.CODE_CHINESE, "zh")
    }

    @Test
    fun testAllLanguagesHaveUniqueCodeAndName() {
        val languages = LanguageData.getAvailableLanguages()
        val codes = languages.map { it.first }
        val names = languages.map { it.second }

        // All codes should be unique
        assertEquals(codes.size, codes.toSet().size)

        // All names should be unique
        assertEquals(names.size, names.toSet().size)
    }

    @Test
    fun testVersionIncrement() {
        val original = LanguageData.createDefault()
        val updated = original.copy(version = original.version + 1)

        assertEquals(2, updated.version)
        assertTrue(updated.version > original.version)
    }

    @Test
    fun testLastModifiedTimestamp() {
        val before = System.currentTimeMillis()
        val language = LanguageData.createDefault()
        val after = System.currentTimeMillis()

        assertTrue(language.lastModified >= before)
        assertTrue(language.lastModified <= after)
    }

    @Test
    fun testSystemDefaultFlag() {
        val systemDefault = LanguageData.createDefault()
        assertTrue(systemDefault.isSystemDefault)
        assertEquals(LanguageData.CODE_SYSTEM_DEFAULT, systemDefault.languageCode)

        val englishLanguage = systemDefault.copy(
            languageCode = LanguageData.CODE_ENGLISH,
            isSystemDefault = false
        )
        assertFalse(englishLanguage.isSystemDefault)
    }
}
