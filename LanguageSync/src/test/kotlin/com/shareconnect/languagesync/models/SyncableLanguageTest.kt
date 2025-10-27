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

import org.junit.Assert.*
import org.junit.Test

class SyncableLanguageTest {

    @Test
    fun testFromLanguageData() {
        val languageData = LanguageData.createDefault()
        val syncable = SyncableLanguage.fromLanguageData(languageData)

        assertEquals(languageData.id, syncable.objectId)
        assertEquals(LanguageData.OBJECT_TYPE, syncable.objectType)
        assertEquals(languageData.version, syncable.version)
        assertEquals(languageData, syncable.getLanguageData())
    }

    @Test
    fun testToFieldMap() {
        val languageData = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_ENGLISH,
            displayName = LanguageData.NAME_ENGLISH,
            isSystemDefault = false,
            version = 2,
            lastModified = 123456789L
        )
        val syncable = SyncableLanguage.fromLanguageData(languageData)
        val fieldMap = syncable.toFieldMap()

        assertEquals("language_preference", fieldMap["id"])
        assertEquals(LanguageData.CODE_ENGLISH, fieldMap["languageCode"])
        assertEquals(LanguageData.NAME_ENGLISH, fieldMap["displayName"])
        assertEquals(false, fieldMap["isSystemDefault"])
        assertEquals(2, fieldMap["version"])
        assertEquals(123456789L, fieldMap["lastModified"])
    }

    @Test
    fun testFromFieldMap() {
        val original = LanguageData.createDefault()
        val syncable = SyncableLanguage.fromLanguageData(original)

        val fields = mapOf(
            "id" to "language_preference",
            "languageCode" to LanguageData.CODE_SPANISH,
            "displayName" to LanguageData.NAME_SPANISH,
            "isSystemDefault" to false,
            "version" to 3,
            "lastModified" to 987654321L
        )

        syncable.fromFieldMap(fields)
        val updated = syncable.getLanguageData()

        assertEquals("language_preference", updated.id)
        assertEquals(LanguageData.CODE_SPANISH, updated.languageCode)
        assertEquals(LanguageData.NAME_SPANISH, updated.displayName)
        assertFalse(updated.isSystemDefault)
        assertEquals(3, updated.version)
        assertEquals(987654321L, updated.lastModified)
    }

    @Test
    fun testFromFieldMapWithIntVersion() {
        val syncable = SyncableLanguage.fromLanguageData(LanguageData.createDefault())

        val fields = mapOf(
            "id" to "language_preference",
            "languageCode" to LanguageData.CODE_FRENCH,
            "displayName" to LanguageData.NAME_FRENCH,
            "isSystemDefault" to false,
            "version" to 5, // Int version
            "lastModified" to 111111111L
        )

        syncable.fromFieldMap(fields)
        val updated = syncable.getLanguageData()

        assertEquals(5, updated.version)
    }

    @Test
    fun testFromFieldMapWithLongVersion() {
        val syncable = SyncableLanguage.fromLanguageData(LanguageData.createDefault())

        val fields = mapOf(
            "id" to "language_preference",
            "languageCode" to LanguageData.CODE_GERMAN,
            "displayName" to LanguageData.NAME_GERMAN,
            "isSystemDefault" to false,
            "version" to 7L, // Long version
            "lastModified" to 222222222L
        )

        syncable.fromFieldMap(fields)
        val updated = syncable.getLanguageData()

        assertEquals(7, updated.version)
    }

    @Test
    fun testFromFieldMapWithIntLastModified() {
        val syncable = SyncableLanguage.fromLanguageData(LanguageData.createDefault())

        val fields = mapOf(
            "id" to "language_preference",
            "languageCode" to LanguageData.CODE_RUSSIAN,
            "displayName" to LanguageData.NAME_RUSSIAN,
            "isSystemDefault" to false,
            "version" to 2,
            "lastModified" to 333333 // Int lastModified
        )

        syncable.fromFieldMap(fields)
        val updated = syncable.getLanguageData()

        assertEquals(333333L, updated.lastModified)
    }

    @Test
    fun testFromFieldMapPreservesDefaultsOnMissingFields() {
        val original = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_ITALIAN,
            displayName = LanguageData.NAME_ITALIAN,
            isSystemDefault = false,
            version = 10,
            lastModified = 999999999L
        )
        val syncable = SyncableLanguage.fromLanguageData(original)

        // Only provide some fields
        val fields = mapOf(
            "languageCode" to LanguageData.CODE_JAPANESE
        )

        syncable.fromFieldMap(fields)
        val updated = syncable.getLanguageData()

        // Should preserve original values for missing fields
        assertEquals("language_preference", updated.id)
        assertEquals(LanguageData.CODE_JAPANESE, updated.languageCode) // Updated
        assertEquals(LanguageData.NAME_ITALIAN, updated.displayName) // Preserved
        assertFalse(updated.isSystemDefault) // Preserved
        assertEquals(10, updated.version) // Preserved
        assertEquals(999999999L, updated.lastModified) // Preserved
    }

    @Test
    fun testRoundTripConversion() {
        val original = LanguageData(
            id = "language_preference",
            languageCode = LanguageData.CODE_CHINESE,
            displayName = LanguageData.NAME_CHINESE,
            isSystemDefault = false,
            version = 42,
            lastModified = 555555555L
        )

        val syncable = SyncableLanguage.fromLanguageData(original)
        val fieldMap = syncable.toFieldMap()

        val newSyncable = SyncableLanguage.fromLanguageData(LanguageData.createDefault())
        newSyncable.fromFieldMap(fieldMap)
        val result = newSyncable.getLanguageData()

        assertEquals(original.id, result.id)
        assertEquals(original.languageCode, result.languageCode)
        assertEquals(original.displayName, result.displayName)
        assertEquals(original.isSystemDefault, result.isSystemDefault)
        assertEquals(original.version, result.version)
        assertEquals(original.lastModified, result.lastModified)
    }
}
