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
        @JvmStatic
        fun onAttach(baseContext: Context): Context {
            val language = getPersistedLanguage(baseContext)
            return setLocale(baseContext, language)
        }

        /**
         * Attach base context with specific language
         */
        @JvmStatic
        fun onAttach(baseContext: Context, languageCode: String): Context {
            return setLocale(baseContext, languageCode)
        }

        /**
         * Get persisted language from SharedPreferences
         */
        @JvmStatic
        fun getPersistedLanguage(context: Context): String {
            val preferences = context.getSharedPreferences("language_sync_prefs", Context.MODE_PRIVATE)
            return preferences.getString(SELECTED_LANGUAGE, LanguageData.CODE_SYSTEM_DEFAULT)
                ?: LanguageData.CODE_SYSTEM_DEFAULT
        }

        /**
         * Persist language to SharedPreferences
         */
        @JvmStatic
        fun persistLanguage(context: Context, languageCode: String) {
            val preferences = context.getSharedPreferences("language_sync_prefs", Context.MODE_PRIVATE)
            preferences.edit().putString(SELECTED_LANGUAGE, languageCode).apply()
        }

        /**
         * Set locale for context
         */
        @JvmStatic
        fun setLocale(context: Context, languageCode: String): Context {
            persistLanguage(context, languageCode)
            return LanguageUtils.applyLanguage(context, languageCode)
        }

        /**
         * Update activity locale and recreate
         */
        @JvmStatic
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
