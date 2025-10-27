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


package com.shareconnect.themesync.models

import org.junit.Assert.*
import org.junit.Test

class SyncableThemeTest {

    @Test
    fun `test syncable theme creation from theme data`() {
        val themeData = ThemeData(
            id = "test_1",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        val syncableTheme = SyncableTheme.fromThemeData(themeData)

        assertEquals("test_1", syncableTheme.objectId)
        assertEquals(ThemeData.OBJECT_TYPE, syncableTheme.objectType)
        assertEquals(1, syncableTheme.version)
        assertEquals(themeData, syncableTheme.getThemeData())
    }

    @Test
    fun `test to field map conversion`() {
        val themeData = ThemeData(
            id = "test_1",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = true,
            isDefault = false,
            sourceApp = ThemeData.APP_QBIT_CONNECT
        )

        val syncableTheme = SyncableTheme.fromThemeData(themeData)
        val fieldMap = syncableTheme.toFieldMap()

        assertEquals("test_1", fieldMap["id"])
        assertEquals("Test Theme", fieldMap["name"])
        assertEquals(ThemeData.COLOR_CRIMSON, fieldMap["colorScheme"])
        assertEquals(true, fieldMap["isDarkMode"])
        assertEquals(false, fieldMap["isDefault"])
        assertEquals(ThemeData.APP_QBIT_CONNECT, fieldMap["sourceApp"])
        assertEquals(1, fieldMap["version"])
        assertNotNull(fieldMap["lastModified"])
    }

    @Test
    fun `test from field map conversion`() {
        val fields = mapOf(
            "id" to "test_2",
            "name" to "Updated Theme",
            "colorScheme" to ThemeData.COLOR_PURPLE,
            "isDarkMode" to false,
            "isDefault" to true,
            "sourceApp" to ThemeData.APP_TRANSMISSION_CONNECT,
            "version" to 2L,
            "lastModified" to 123456789L
        )

        val initialData = ThemeData(
            id = "test_1",
            name = "Original",
            colorScheme = ThemeData.COLOR_GREEN,
            isDarkMode = true,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        val syncableTheme = SyncableTheme.fromThemeData(initialData)
        syncableTheme.fromFieldMap(fields)

        val updatedData = syncableTheme.getThemeData()
        assertEquals("test_2", updatedData.id)
        assertEquals("Updated Theme", updatedData.name)
        assertEquals(ThemeData.COLOR_PURPLE, updatedData.colorScheme)
        assertFalse(updatedData.isDarkMode)
        assertTrue(updatedData.isDefault)
        assertEquals(ThemeData.APP_TRANSMISSION_CONNECT, updatedData.sourceApp)
        assertEquals(2, updatedData.version)
        assertEquals(123456789L, updatedData.lastModified)
    }

    @Test
    fun `test field map round trip`() {
        val originalData = ThemeData(
            id = "test_3",
            name = "Round Trip Theme",
            colorScheme = ThemeData.COLOR_LIGHT_BLUE,
            isDarkMode = true,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT,
            version = 5,
            lastModified = 987654321L
        )

        val syncableTheme1 = SyncableTheme.fromThemeData(originalData)
        val fieldMap = syncableTheme1.toFieldMap()

        val syncableTheme2 = SyncableTheme.fromThemeData(
            ThemeData(
                id = "dummy",
                name = "Dummy",
                colorScheme = ThemeData.COLOR_MATERIAL,
                isDarkMode = false,
                isDefault = true,
                sourceApp = ThemeData.APP_QBIT_CONNECT
            )
        )
        syncableTheme2.fromFieldMap(fieldMap)

        val resultData = syncableTheme2.getThemeData()
        assertEquals(originalData.id, resultData.id)
        assertEquals(originalData.name, resultData.name)
        assertEquals(originalData.colorScheme, resultData.colorScheme)
        assertEquals(originalData.isDarkMode, resultData.isDarkMode)
        assertEquals(originalData.isDefault, resultData.isDefault)
        assertEquals(originalData.sourceApp, resultData.sourceApp)
        // Version is Int in fieldMap, so it might be converted
        assertTrue("Version should be 5", resultData.version == 5)
        assertEquals(originalData.lastModified, resultData.lastModified)
    }
}
