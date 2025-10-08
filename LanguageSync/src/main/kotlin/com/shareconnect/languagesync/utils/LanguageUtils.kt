package com.shareconnect.languagesync.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import com.shareconnect.languagesync.models.LanguageData
import java.util.Locale

object LanguageUtils {

    /**
     * Apply language to context and return updated context
     */
    fun applyLanguage(context: Context, languageCode: String): Context {
        return if (languageCode == LanguageData.CODE_SYSTEM_DEFAULT) {
            // Use system default - reset to default configuration
            applySystemDefault(context)
        } else {
            // Apply specific language
            applySpecificLanguage(context, languageCode)
        }
    }

    /**
     * Apply system default language
     */
    private fun applySystemDefault(context: Context): Context {
        val config = context.resources.configuration

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            // Use system default locales
            val systemLocales = LocaleList.getDefault()
            config.setLocales(systemLocales)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = Locale.getDefault()
            @Suppress("DEPRECATION")
            context.createConfigurationContext(config)
        }
    }

    /**
     * Apply specific language code
     */
    private fun applySpecificLanguage(context: Context, languageCode: String): Context {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context.createConfigurationContext(config)
        } else {
            @Suppress("DEPRECATION")
            config.locale = locale
            @Suppress("DEPRECATION")
            context.createConfigurationContext(config)
        }
    }

    /**
     * Get current locale from context
     */
    fun getCurrentLocale(context: Context): Locale {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            context.resources.configuration.locales[0]
        } else {
            @Suppress("DEPRECATION")
            context.resources.configuration.locale
        }
    }

    /**
     * Get language code from locale
     */
    fun getLanguageCode(locale: Locale): String {
        return locale.language
    }

    /**
     * Check if language code is supported
     */
    fun isSupportedLanguage(languageCode: String): Boolean {
        val availableCodes = LanguageData.getAvailableLanguages().map { it.first }
        return languageCode in availableCodes
    }

    /**
     * Get display name for language code
     */
    fun getDisplayName(languageCode: String): String? {
        return LanguageData.getAvailableLanguages()
            .find { it.first == languageCode }
            ?.second
    }

    /**
     * Convert locale to LanguageData
     */
    fun localeToLanguageCode(locale: Locale): String {
        val code = locale.language
        return if (isSupportedLanguage(code)) {
            code
        } else {
            LanguageData.CODE_ENGLISH // Fallback to English
        }
    }
}
