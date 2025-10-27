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


package com.shareconnect.database

import android.content.Context
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.robolectric.RobolectricTestRunner
import org.robolectric.RuntimeEnvironment

@RunWith(RobolectricTestRunner::class)
@org.robolectric.annotation.Config(sdk = [33], application = android.app.Application::class)
class ThemeRepositoryUnitTest {

    private lateinit var mockThemeDao: ThemeDao
    private lateinit var context: Context
    private lateinit var themeRepository: ThemeRepository

    @Before
    fun setUp() {
        context = RuntimeEnvironment.getApplication()
        mockThemeDao = mock<ThemeDao>()

        // Create ThemeRepository with mocked DAO
        themeRepository = ThemeRepository(context, mockThemeDao)
    }

    @Test
    fun testGetAllThemes() {
        val expectedThemes = listOf(
            Theme().apply {
                id = 1
                name = "Light Theme"
                colorScheme = "blue"
                isDarkMode = false
                isDefault = true
            },
            Theme().apply {
                id = 2
                name = "Dark Theme"
                colorScheme = "blue"
                isDarkMode = true
                isDefault = false
            }
        )

        `when`(mockThemeDao.getAllThemes()).thenReturn(expectedThemes)

        val allThemes = themeRepository.allThemes

        assertEquals(2, allThemes.size)
        assertTrue(allThemes.any { it.name == "Light Theme" })
        assertTrue(allThemes.any { it.name == "Dark Theme" })
        verify(mockThemeDao).getAllThemes()
    }

    @Test
    fun testGetDefaultTheme() {
        val defaultTheme = Theme().apply {
            id = 1
            name = "Default Theme"
            colorScheme = "blue"
            isDarkMode = false
            isDefault = true
        }

        `when`(mockThemeDao.getDefaultTheme()).thenReturn(defaultTheme)

        val retrievedDefaultTheme = themeRepository.defaultTheme

        assertNotNull(retrievedDefaultTheme)
        assertEquals("Default Theme", retrievedDefaultTheme?.name)
        assertTrue(retrievedDefaultTheme?.isDefault == true)
        verify(mockThemeDao).getDefaultTheme()
    }

    @Test
    fun testGetThemeByColorSchemeAndMode() {
        val theme = Theme().apply {
            id = 1
            name = "Blue Light Theme"
            colorScheme = "blue"
            isDarkMode = false
            isDefault = false
        }

        `when`(mockThemeDao.getThemeByColorSchemeAndMode("blue", false)).thenReturn(theme)

        val retrievedTheme = themeRepository.getThemeByColorSchemeAndMode("blue", false)

        assertNotNull(retrievedTheme)
        assertEquals("Blue Light Theme", retrievedTheme?.name)
        assertEquals("blue", retrievedTheme?.colorScheme)
        assertFalse(retrievedTheme?.isDarkMode == true)
        verify(mockThemeDao).getThemeByColorSchemeAndMode("blue", false)
    }

    @Test
    fun testInsertTheme() {
        val theme = Theme().apply {
            id = 1
            name = "Test Theme"
            colorScheme = "blue"
            isDarkMode = false
            isDefault = true
        }

        themeRepository.insertTheme(theme)

        verify(mockThemeDao).insert(theme)
    }

    @Test
    fun testUpdateTheme() {
        val theme = Theme().apply {
            id = 1
            name = "Updated Theme"
            colorScheme = "green"
            isDarkMode = true
            isDefault = false
        }

        themeRepository.updateTheme(theme)

        verify(mockThemeDao).update(theme)
    }

    @Test
    fun testSetDefaultTheme() {
        themeRepository.setDefaultTheme(2)

        verify(mockThemeDao).clearDefaultThemes()
        verify(mockThemeDao).setDefaultTheme(2)
    }

    @Test
    fun testInitializeDefaultThemes() {
        `when`(mockThemeDao.getAllThemes()).thenReturn(emptyList())

        themeRepository.initializeDefaultThemes()

        // Verify that getAllThemes was called
        verify(mockThemeDao).getAllThemes()

        // Verify that insert was called 12 times (once for each default theme)
        verify(mockThemeDao, Mockito.times(12)).insert(any())
    }
}