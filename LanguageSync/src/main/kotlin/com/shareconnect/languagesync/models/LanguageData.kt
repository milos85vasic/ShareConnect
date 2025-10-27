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


package com.shareconnect.languagesync.models

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Language preference data model
 *
 * Stores the currently selected language preference to be synced across all apps
 * in the ShareConnect ecosystem.
 *
 * Fields:
 * - id: Unique identifier (always "language_preference" for singleton pattern)
 * - languageCode: ISO 639-1 language code (e.g., "en", "es", "fr", "de", "ru")
 * - displayName: Human-readable language name (e.g., "English", "Español")
 * - isSystemDefault: Whether to use system default language
 * - version: Sync version for conflict resolution
 * - lastModified: Last modification timestamp
 */
@Entity(tableName = "synced_language_preference")
data class LanguageData(
    @PrimaryKey
    val id: String = "language_preference", // Singleton pattern

    val languageCode: String, // ISO 639-1 code: "en", "es", "fr", etc.

    val displayName: String, // Human-readable name

    val isSystemDefault: Boolean = false, // If true, follow system language

    val version: Int = 1, // For sync conflict resolution

    val lastModified: Long = System.currentTimeMillis()
) {
    companion object {
        const val OBJECT_TYPE = "language_preference"

        // Supported languages
        const val CODE_SYSTEM_DEFAULT = "system"
        const val CODE_ENGLISH = "en"
        const val CODE_ARABIC = "ar"
        const val CODE_BELARUSIAN = "be"
        const val CODE_GERMAN = "de"
        const val CODE_SPANISH = "es"
        const val CODE_FRENCH = "fr"
        const val CODE_HUNGARIAN = "hu"
        const val CODE_ITALIAN = "it"
        const val CODE_JAPANESE = "ja"
        const val CODE_KANNADA = "kn"
        const val CODE_KOREAN = "ko"
        const val CODE_PORTUGUESE = "pt"
        const val CODE_RUSSIAN = "ru"
        const val CODE_SERBIAN = "sr"
        const val CODE_CHINESE = "zh"

        // Display names
        const val NAME_SYSTEM_DEFAULT = "System Default"
        const val NAME_ENGLISH = "English"
        const val NAME_ARABIC = "العربية"
        const val NAME_BELARUSIAN = "Беларуская"
        const val NAME_GERMAN = "Deutsch"
        const val NAME_SPANISH = "Español"
        const val NAME_FRENCH = "Français"
        const val NAME_HUNGARIAN = "Magyar"
        const val NAME_ITALIAN = "Italiano"
        const val NAME_JAPANESE = "日本語"
        const val NAME_KANNADA = "ಕನ್ನಡ"
        const val NAME_KOREAN = "한국어"
        const val NAME_PORTUGUESE = "Português"
        const val NAME_RUSSIAN = "Русский"
        const val NAME_SERBIAN = "Српски"
        const val NAME_CHINESE = "中文"

        /**
         * Get all available languages
         */
        fun getAvailableLanguages(): List<Pair<String, String>> = listOf(
            CODE_SYSTEM_DEFAULT to NAME_SYSTEM_DEFAULT,
            CODE_ENGLISH to NAME_ENGLISH,
            CODE_ARABIC to NAME_ARABIC,
            CODE_BELARUSIAN to NAME_BELARUSIAN,
            CODE_GERMAN to NAME_GERMAN,
            CODE_SPANISH to NAME_SPANISH,
            CODE_FRENCH to NAME_FRENCH,
            CODE_HUNGARIAN to NAME_HUNGARIAN,
            CODE_ITALIAN to NAME_ITALIAN,
            CODE_JAPANESE to NAME_JAPANESE,
            CODE_KANNADA to NAME_KANNADA,
            CODE_KOREAN to NAME_KOREAN,
            CODE_PORTUGUESE to NAME_PORTUGUESE,
            CODE_RUSSIAN to NAME_RUSSIAN,
            CODE_SERBIAN to NAME_SERBIAN,
            CODE_CHINESE to NAME_CHINESE
        )

        /**
         * Create default language preference (system default)
         */
        fun createDefault(): LanguageData = LanguageData(
            id = "language_preference",
            languageCode = CODE_SYSTEM_DEFAULT,
            displayName = NAME_SYSTEM_DEFAULT,
            isSystemDefault = true,
            version = 1,
            lastModified = System.currentTimeMillis()
        )
    }
}
