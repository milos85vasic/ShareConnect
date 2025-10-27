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

import digital.vasic.asinka.models.SyncableObject

class SyncableLanguage(
    private var languageData: LanguageData
) : SyncableObject {

    override val objectId: String
        get() = languageData.id

    override val objectType: String
        get() = LanguageData.OBJECT_TYPE

    override val version: Int
        get() = languageData.version

    fun getLanguageData(): LanguageData = languageData

    override fun toFieldMap(): Map<String, Any?> {
        return mapOf(
            "id" to languageData.id,
            "languageCode" to languageData.languageCode,
            "displayName" to languageData.displayName,
            "isSystemDefault" to languageData.isSystemDefault,
            "version" to languageData.version,
            "lastModified" to languageData.lastModified
        )
    }

    override fun fromFieldMap(fields: Map<String, Any?>) {
        val versionValue = when (val v = fields["version"]) {
            is Int -> v
            is Long -> v.toInt()
            else -> languageData.version
        }

        val lastModifiedValue = when (val lm = fields["lastModified"]) {
            is Long -> lm
            is Int -> lm.toLong()
            else -> languageData.lastModified
        }

        languageData = LanguageData(
            id = fields["id"] as? String ?: languageData.id,
            languageCode = fields["languageCode"] as? String ?: languageData.languageCode,
            displayName = fields["displayName"] as? String ?: languageData.displayName,
            isSystemDefault = fields["isSystemDefault"] as? Boolean ?: languageData.isSystemDefault,
            version = versionValue,
            lastModified = lastModifiedValue
        )
    }

    companion object {
        fun fromLanguageData(languageData: LanguageData): SyncableLanguage {
            return SyncableLanguage(languageData)
        }
    }
}
