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
        val key = "theme"
        val value = "dark"
        val type = "string"
        val description = "User interface theme preference"
        val category = "ui"
        val isSensitive = false
        val defaultValue = "light"
        val allowedValues = "light,dark,system"
        val validationPattern = "^(light|dark|system)$"
        val minValue = null
        val maxValue = null
        val timestamp = System.currentTimeMillis()
        val version = 1
        val lastModified = System.currentTimeMillis()

        // When
        val preferencesData = PreferencesData(
            id = id,
            key = key,
            value = value,
            type = type,
            description = description,
            category = category,
            isSensitive = isSensitive,
            defaultValue = defaultValue,
            allowedValues = allowedValues,
            validationPattern = validationPattern,
            minValue = minValue,
            maxValue = maxValue,
            timestamp = timestamp,
            version = version,
            lastModified = lastModified
        )

        // Then
        assertEquals(id, preferencesData.id)
        assertEquals(key, preferencesData.key)
        assertEquals(value, preferencesData.value)
        assertEquals(type, preferencesData.type)
        assertEquals(description, preferencesData.description)
        assertEquals(category, preferencesData.category)
        assertEquals(isSensitive, preferencesData.isSensitive)
        assertEquals(defaultValue, preferencesData.defaultValue)
        assertEquals(allowedValues, preferencesData.allowedValues)
        assertEquals(validationPattern, preferencesData.validationPattern)
        assertEquals(minValue, preferencesData.minValue)
        assertEquals(maxValue, preferencesData.maxValue)
        assertEquals(timestamp, preferencesData.timestamp)
        assertEquals(version, preferencesData.version)
        assertEquals(lastModified, preferencesData.lastModified)
    }

    @Test
    fun `test PreferencesData creation with minimal fields`() {
        // Given
        val id = "test-preferences-id"
        val key = "theme"
        val value = "dark"
        val type = "string"
        val timestamp = System.currentTimeMillis()

        // When
        val preferencesData = PreferencesData(
            id = id,
            key = key,
            value = value,
            type = type,
            timestamp = timestamp
        )

        // Then
        assertEquals(id, preferencesData.id)
        assertEquals(key, preferencesData.key)
        assertEquals(value, preferencesData.value)
        assertEquals(type, preferencesData.type)
        assertEquals(timestamp, preferencesData.timestamp)
        assertNull(preferencesData.description)
        assertNull(preferencesData.category)
        assertFalse(preferencesData.isSensitive)
        assertNull(preferencesData.defaultValue)
        assertNull(preferencesData.allowedValues)
        assertNull(preferencesData.validationPattern)
        assertNull(preferencesData.minValue)
        assertNull(preferencesData.maxValue)
        assertEquals(0, preferencesData.version)
        assertEquals(0L, preferencesData.lastModified)
    }

    @Test
    fun `test PreferencesData equals and hashCode work correctly`() {
        // Given
        val preferences1 = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis()
        )
        val preferences2 = PreferencesData(
            id = "test-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis()
        )
        val preferences3 = PreferencesData(
            id = "different-preferences-id",
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis()
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
            key = "theme",
            value = "dark",
            type = "string",
            timestamp = System.currentTimeMillis()
        )

        // When
        val toString = preferencesData.toString()

        // Then
        assertTrue(toString.contains("test-preferences-id"))
        assertTrue(toString.contains("theme"))
        assertTrue(toString.contains("dark"))
        assertTrue(toString.contains("string"))
    }
}