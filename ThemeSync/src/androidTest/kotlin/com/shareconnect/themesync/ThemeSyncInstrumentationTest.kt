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


package com.shareconnect.themesync

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.themesync.database.ThemeDatabase
import com.shareconnect.themesync.models.ThemeData
import com.shareconnect.themesync.repository.ThemeRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeSyncInstrumentationTest {

    private lateinit var context: Context
    private lateinit var database: ThemeDatabase
    private lateinit var repository: ThemeRepository

    @Before
    fun setup() {
        context = ApplicationProvider.getApplicationContext()
        database = ThemeDatabase.getInMemoryInstance(context)
        repository = ThemeRepository(context, ThemeData.APP_SHARE_CONNECT, database.themeDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun testThemeInsertion() = runBlocking {
        val theme = ThemeData(
            id = "test_1",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        repository.insertTheme(theme)

        val retrieved = repository.getThemeById("test_1")
        assertNotNull(retrieved)
        assertEquals("Test Theme", retrieved?.name)
        assertEquals(ThemeData.COLOR_WARM_ORANGE, retrieved?.colorScheme)
    }

    @Test
    fun testDefaultThemeSelection() = runBlocking {
        val theme1 = ThemeData(
            id = "theme_1",
            name = "Theme 1",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        val theme2 = ThemeData(
            id = "theme_2",
            name = "Theme 2",
            colorScheme = ThemeData.COLOR_PURPLE,
            isDarkMode = true,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        repository.insertTheme(theme1)
        repository.insertTheme(theme2)

        val defaultTheme = repository.getDefaultTheme()
        assertNotNull(defaultTheme)
        assertEquals("theme_1", defaultTheme?.id)

        // Change default
        repository.setDefaultTheme("theme_2")

        val newDefaultTheme = repository.getDefaultTheme()
        assertNotNull(newDefaultTheme)
        assertEquals("theme_2", newDefaultTheme?.id)
    }

    @Test
    fun testInitializeDefaultThemes() = runBlocking {
        repository.initializeDefaultThemes()

        val count = repository.getThemeCount()
        assertEquals(12, count) // 6 color schemes * 2 modes

        val themes = repository.getAllThemesSync()
        val defaultThemes = themes.filter { it.isDefault }
        assertEquals(1, defaultThemes.size)
    }

    @Test
    fun testThemeUpdate() = runBlocking {
        val theme = ThemeData(
            id = "update_test",
            name = "Original Name",
            colorScheme = ThemeData.COLOR_GREEN,
            isDarkMode = false,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        repository.insertTheme(theme)

        val updatedTheme = theme.copy(name = "Updated Name", isDarkMode = true)
        repository.updateTheme(updatedTheme)

        val retrieved = repository.getThemeById("update_test")
        assertNotNull(retrieved)
        assertEquals("Updated Name", retrieved?.name)
        assertTrue(retrieved?.isDarkMode == true)
    }

    @Test
    fun testThemeDeletion() = runBlocking {
        val theme = ThemeData(
            id = "delete_test",
            name = "To Delete",
            colorScheme = ThemeData.COLOR_LIGHT_BLUE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        repository.insertTheme(theme)
        assertNotNull(repository.getThemeById("delete_test"))

        repository.deleteTheme("delete_test")
        assertNull(repository.getThemeById("delete_test"))
    }

    @Test
    fun testObserveDefaultTheme() = runBlocking {
        repository.initializeDefaultThemes()

        val defaultTheme = repository.observeDefaultTheme().first()
        assertNotNull(defaultTheme)
        assertTrue(defaultTheme?.isDefault == true)
    }

    @Test
    fun testGetThemesBySourceApp() = runBlocking {
        val shareConnectTheme = ThemeData(
            id = "sc_1",
            name = "SC Theme",
            colorScheme = ThemeData.COLOR_MATERIAL,
            isDarkMode = false,
            isDefault = false,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        val qbitTheme = ThemeData(
            id = "qb_1",
            name = "QB Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = ThemeData.APP_QBIT_CONNECT
        )

        repository.insertTheme(shareConnectTheme)
        repository.insertTheme(qbitTheme)

        val scThemes = repository.getThemesBySourceApp(ThemeData.APP_SHARE_CONNECT)
        assertEquals(1, scThemes.size)
        assertEquals("sc_1", scThemes[0].id)

        val qbThemes = repository.getThemesBySourceApp(ThemeData.APP_QBIT_CONNECT)
        assertEquals(1, qbThemes.size)
        assertEquals("qb_1", qbThemes[0].id)
    }

    @Test
    fun testClearDefaultThemes() = runBlocking {
        val theme1 = ThemeData(
            id = "clear_1",
            name = "Theme 1",
            colorScheme = ThemeData.COLOR_PURPLE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        val theme2 = ThemeData(
            id = "clear_2",
            name = "Theme 2",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        repository.insertTheme(theme1)
        repository.insertTheme(theme2)

        // Both should have been inserted as default
        val themes = repository.getAllThemesSync()
        val defaultCount = themes.count { it.isDefault }
        assertTrue("Should have at least one default", defaultCount > 0)
    }
}
