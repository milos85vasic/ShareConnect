package com.shareconnect.languagesync.utils

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import com.shareconnect.languagesync.models.LanguageData

/**
 * Helper class for applying locale changes to Activities and Application
 */
class LocaleHelper {

    companion object {
        private const val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

        /**
         * Attach base context with language applied
         * Call this in Activity.attachBaseContext()
         */
        fun onAttach(baseContext: Context): Context {
            val language = getPersistedLanguage(baseContext)
            return setLocale(baseContext, language)
        }

        /**
         * Attach base context with specific language
         */
        fun onAttach(baseContext: Context, languageCode: String): Context {
            return setLocale(baseContext, languageCode)
        }

        /**
         * Get persisted language from SharedPreferences
         */
        fun getPersistedLanguage(context: Context): String {
            val preferences = context.getSharedPreferences("language_sync_prefs", Context.MODE_PRIVATE)
            return preferences.getString(SELECTED_LANGUAGE, LanguageData.CODE_SYSTEM_DEFAULT)
                ?: LanguageData.CODE_SYSTEM_DEFAULT
        }

        /**
         * Persist language to SharedPreferences
         */
        fun persistLanguage(context: Context, languageCode: String) {
            val preferences = context.getSharedPreferences("language_sync_prefs", Context.MODE_PRIVATE)
            preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
        }

        /**
         * Set locale for context
         */
        fun setLocale(context: Context, languageCode: String): Context {
            persistLanguage(context, languageCode)
            return LanguageUtils.applyLanguage(context, languageCode)
        }

        /**
         * Update activity locale and recreate
         */
        fun updateLocale(activity: Activity, languageCode: String) {
            persistLanguage(activity, languageCode)
            activity.recreate()
        }
    }
}

/**
 * Context wrapper that applies locale
 */
class LocaleContextWrapper(base: Context) : ContextWrapper(base) {
    companion object {
        fun wrap(context: Context, languageCode: String): ContextWrapper {
            var newContext = context
            val currentLocale = LanguageUtils.getCurrentLocale(context)
            val currentLanguage = LanguageUtils.getLanguageCode(currentLocale)

            // Only update if language is different
            if (languageCode != currentLanguage || languageCode == LanguageData.CODE_SYSTEM_DEFAULT) {
                newContext = LanguageUtils.applyLanguage(context, languageCode)
            }

            return LocaleContextWrapper(newContext)
        }
    }
}
