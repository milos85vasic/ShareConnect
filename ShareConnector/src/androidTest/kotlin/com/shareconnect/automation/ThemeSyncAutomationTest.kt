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


package com.shareconnect.automation

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import com.shareconnect.SCApplication
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeSyncAutomationTest {

    private lateinit var device: UiDevice
    private lateinit var context: Context
    private lateinit var application: SCApplication

    @Before
    fun setup() {
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())
        context = ApplicationProvider.getApplicationContext()
        application = context.applicationContext as SCApplication
    }

    @Test
    fun testThemeSyncManagerInitialization() = runBlocking {
        val themeSyncManager = application.themeSyncManager
        assertNotNull("ThemeSyncManager should be initialized", themeSyncManager)

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()
        assertTrue("Should have default themes loaded", themes.isNotEmpty())
    }

    @Test
    fun testDefaultThemeIsSet() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val defaultTheme = themeSyncManager.getDefaultTheme()
        assertNotNull("Default theme should be set", defaultTheme)
        assertTrue("Default theme flag should be true", defaultTheme?.isDefault == true)
    }

    @Test
    fun testAllColorSchemesAvailable() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()

        val colorSchemes = themes.map { it.colorScheme }.toSet()
        assertTrue("Should have WARM_ORANGE", colorSchemes.contains(ThemeData.COLOR_WARM_ORANGE))
        assertTrue("Should have CRIMSON", colorSchemes.contains(ThemeData.COLOR_CRIMSON))
        assertTrue("Should have LIGHT_BLUE", colorSchemes.contains(ThemeData.COLOR_LIGHT_BLUE))
        assertTrue("Should have PURPLE", colorSchemes.contains(ThemeData.COLOR_PURPLE))
        assertTrue("Should have GREEN", colorSchemes.contains(ThemeData.COLOR_GREEN))
        assertTrue("Should have MATERIAL", colorSchemes.contains(ThemeData.COLOR_MATERIAL))
    }

    @Test
    fun testLightAndDarkVariantsExist() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()
        val groupedByScheme = themes.groupBy { it.colorScheme }

        groupedByScheme.forEach { (scheme, themeList) ->
            val hasLight = themeList.any { !it.isDarkMode }
            val hasDark = themeList.any { it.isDarkMode }

            assertTrue("$scheme should have light variant", hasLight)
            assertTrue("$scheme should have dark variant", hasDark)
        }
    }

    @Test
    fun testThemeChangeNotification() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val initialTheme = themeSyncManager.getDefaultTheme()
        assertNotNull(initialTheme)

        // Get a different theme
        val themes = themeSyncManager.getAllThemes().first()
        val differentTheme = themes.find { it.id != initialTheme?.id }

        assertNotNull("Should have at least one other theme", differentTheme)

        if (differentTheme != null) {
            // Change the theme
            themeSyncManager.setDefaultTheme(differentTheme.id)

            // Wait for change to propagate
            delay(500)

            val newDefaultTheme = themeSyncManager.getDefaultTheme()
            assertEquals("Theme should have changed", differentTheme.id, newDefaultTheme?.id)
        }
    }

    @Test
    fun testThemeSourceAppIdentification() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()
        val shareConnectThemes = themes.filter {
            it.sourceApp == context.packageName
        }

        assertTrue("Should have ShareConnect themes", shareConnectThemes.isNotEmpty())
    }

    @Test
    fun testThemeVersioning() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val defaultTheme = themeSyncManager.getDefaultTheme()
        assertNotNull(defaultTheme)

        val initialVersion = defaultTheme?.version ?: 0

        // Update the theme (this increments version)
        if (defaultTheme != null) {
            themeSyncManager.setDefaultTheme(defaultTheme.id)

            delay(500)

            val updatedTheme = themeSyncManager.getDefaultTheme()
            assertTrue(
                "Version should increment after update",
                (updatedTheme?.version ?: 0) > initialVersion
            )
        }
    }

    @Test
    fun testThemeLastModifiedTimestamp() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val defaultTheme = themeSyncManager.getDefaultTheme()
        assertNotNull(defaultTheme)

        val initialTimestamp = defaultTheme?.lastModified ?: 0L

        // Wait a moment
        delay(100)

        // Update the theme
        if (defaultTheme != null) {
            themeSyncManager.setDefaultTheme(defaultTheme.id)

            delay(500)

            val updatedTheme = themeSyncManager.getDefaultTheme()
            assertTrue(
                "LastModified should update",
                (updatedTheme?.lastModified ?: 0L) > initialTimestamp
            )
        }
    }

    @Test
    fun testOnlyOneDefaultTheme() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()
        val defaultThemes = themes.filter { it.isDefault }

        assertEquals("Should have exactly one default theme", 1, defaultThemes.size)
    }

    @Test
    fun testThemeIdUniqueness() = runBlocking {
        val themeSyncManager = application.themeSyncManager

        // Wait for initialization
        delay(1000)

        val themes = themeSyncManager.getAllThemes().first()
        val ids = themes.map { it.id }.toSet()

        assertEquals("All theme IDs should be unique", themes.size, ids.size)
    }
}
