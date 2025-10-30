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


package com.shareconnect.preferencessync.models

import org.junit.Assert.*
import org.junit.Test

class PreferencesDataTest {

    @Test
    fun `test PreferencesData creation with all fields`() {
        // Given
        val id = "test-preferences-id"
        val category = "ui"
        val key = "theme"
        val value = "dark"
        val type = "string"
        val description = "User interface theme preference"
        val sourceApp = "TestApp"
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val preferencesData = PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value,
            type = type,
            description = description,
            sourceApp = sourceApp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, preferencesData.id)
        assertEquals(category, preferencesData.category)
        assertEquals(key, preferencesData.key)
        assertEquals(value, preferencesData.value)
        assertEquals(type, preferencesData.type)
        assertEquals(description, preferencesData.description)
        assertEquals(sourceApp, preferencesData.sourceApp)
        assertEquals(version, preferencesData.version)
        assertEquals(lastModified, preferencesData.lastModified)
    }

    @Test
    fun `test PreferencesData creation with minimal fields`() {
        // Given
        val id = "test-preferences-id"
        val category = "ui"
        val key = "theme"
        val value = "dark"
        val type = "string"
        val sourceApp = "TestApp"

        // When
        val preferencesData = PreferencesData(
            id = id,
            category = category,
            key = key,
            value = value,
            type = type,
            sourceApp = sourceApp
        )

        // Then
        assertEquals(id, preferencesData.id)
        assertEquals(category, preferencesData.category)
        assertEquals(key, preferencesData.key)
        assertEquals(value, preferencesData.value)
        assertEquals(type, preferencesData.type)
        assertEquals(sourceApp, preferencesData.sourceApp)
        assertNull(preferencesData.description)
        assertEquals(1, preferencesData.version)
        assertTrue(Math.abs(System.currentTimeMillis() - preferencesData.lastModified) < 1000) // Allow 1 second tolerance
    }

    @Test
    fun `test PreferencesData equals and hashCode work correctly`() {
        // Given
        val timestamp = System.currentTimeMillis()
        val preferences1 = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            lastModified = timestamp
        )
        val preferences2 = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            lastModified = timestamp
        )
        val preferences3 = PreferencesData(
            id = "different-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp",
            lastModified = timestamp
        )

        // Then
        assertEquals(preferences1, preferences2)
        assertEquals(preferences1.hashCode(), preferences2.hashCode())
        assertNotEquals(preferences1, preferences3)
        assertNotEquals(preferences1.hashCode(), preferences3.hashCode())
    }

    @Test
    fun `test PreferencesData toString contains relevant information`() {
        // Given
        val preferencesData = PreferencesData(
            id = "test-preferences-id",
            category = "ui",
            key = "theme",
            value = "dark",
            type = "string",
            sourceApp = "TestApp"
        )

        // When
        val toString = preferencesData.toString()

        // Then
        assertTrue(toString.contains("test-preferences-id"))
        assertTrue(toString.contains("ui"))
        assertTrue(toString.contains("theme"))
        assertTrue(toString.contains("dark"))
        assertTrue(toString.contains("string"))
    }
}