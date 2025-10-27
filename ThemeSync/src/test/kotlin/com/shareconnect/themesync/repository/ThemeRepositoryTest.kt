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


package com.shareconnect.themesync.repository

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.themesync.database.ThemeDatabase
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeRepositoryTest {

    private lateinit var database: ThemeDatabase
    private lateinit var repository: ThemeRepository

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ThemeDatabase::class.java
        ).build()

        repository = ThemeRepository(context, "test_app", database.themeDao())
    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun `test insert and retrieve theme`() = runTest {
        val theme = ThemeData(
            id = "test_theme_1",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = "test_app"
        )

        val insertedId = repository.insertTheme(theme)
        assertTrue(insertedId > 0)

        val retrievedTheme = repository.getThemeById("test_theme_1")
        assertNotNull(retrievedTheme)
        assertEquals("Test Theme", retrievedTheme?.name)
        assertEquals(ThemeData.COLOR_WARM_ORANGE, retrievedTheme?.colorScheme)
    }

    @Test
    fun `test get all themes flow`() = runTest {
        val theme1 = ThemeData(
            id = "test_theme_1",
            name = "Theme 1",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        val theme2 = ThemeData(
            id = "test_theme_2",
            name = "Theme 2",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = true,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme1)
        repository.insertTheme(theme2)

        val themes = repository.getAllThemes().first()
        assertEquals(2, themes.size)
        assertTrue(themes.any { it.name == "Theme 1" })
        assertTrue(themes.any { it.name == "Theme 2" })
    }

    @Test
    fun `test get default theme`() = runTest {
        val defaultTheme = ThemeData(
            id = "default_theme",
            name = "Default Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = "test_app"
        )

        val otherTheme = ThemeData(
            id = "other_theme",
            name = "Other Theme",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(defaultTheme)
        repository.insertTheme(otherTheme)

        val retrievedDefault = repository.getDefaultTheme()
        assertNotNull(retrievedDefault)
        assertEquals("Default Theme", retrievedDefault?.name)
        assertTrue(retrievedDefault?.isDefault == true)
    }

    @Test
    fun `test set default theme`() = runTest {
        val theme1 = ThemeData(
            id = "theme_1",
            name = "Theme 1",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        val theme2 = ThemeData(
            id = "theme_2",
            name = "Theme 2",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme1)
        repository.insertTheme(theme2)

        repository.setDefaultTheme("theme_2")

        val defaultTheme = repository.getDefaultTheme()
        assertNotNull(defaultTheme)
        assertEquals("theme_2", defaultTheme?.id)
        assertTrue(defaultTheme?.isDefault == true)
    }

    @Test
    fun `test delete theme`() = runTest {
        val theme = ThemeData(
            id = "theme_to_delete",
            name = "Theme to Delete",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme)
        assertNotNull(repository.getThemeById("theme_to_delete"))

        repository.deleteTheme("theme_to_delete")

        val deletedTheme = repository.getThemeById("theme_to_delete")
        assertNull(deletedTheme)
    }

    @Test
    fun `test update theme`() = runTest {
        val theme = ThemeData(
            id = "theme_to_update",
            name = "Original Name",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme)

        val updatedTheme = theme.copy(name = "Updated Name", version = 2)
        repository.updateTheme(updatedTheme)

        val retrievedTheme = repository.getThemeById("theme_to_update")
        assertNotNull(retrievedTheme)
        assertEquals("Updated Name", retrievedTheme?.name)
        assertEquals(2, retrievedTheme?.version)
    }

    @Test
    fun `test get themes by source app`() = runTest {
        val theme1 = ThemeData(
            id = "theme_1",
            name = "Theme 1",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "app1"
        )

        val theme2 = ThemeData(
            id = "theme_2",
            name = "Theme 2",
            colorScheme = ThemeData.COLOR_CRIMSON,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "app1"
        )

        val theme3 = ThemeData(
            id = "theme_3",
            name = "Theme 3",
            colorScheme = ThemeData.COLOR_PURPLE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "app2"
        )

        repository.insertTheme(theme1)
        repository.insertTheme(theme2)
        repository.insertTheme(theme3)

        val app1Themes = repository.getThemesBySourceApp("app1")
        assertEquals(2, app1Themes.size)
        assertTrue(app1Themes.any { it.name == "Theme 1" })
        assertTrue(app1Themes.any { it.name == "Theme 2" })

        val app2Themes = repository.getThemesBySourceApp("app2")
        assertEquals(1, app2Themes.size)
        assertEquals("Theme 3", app2Themes[0].name)
    }

    @Test
    fun `test get theme by color scheme and mode`() = runTest {
        val theme = ThemeData(
            id = "specific_theme",
            name = "Specific Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = true,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme)

        val retrievedTheme = repository.getThemeByColorSchemeAndMode(
            ThemeData.COLOR_WARM_ORANGE,
            true
        )

        assertNotNull(retrievedTheme)
        assertEquals("Specific Theme", retrievedTheme?.name)
        assertTrue(retrievedTheme?.isDarkMode == true)
    }

    @Test
    fun `test insert all themes`() = runTest {
        val themes = listOf(
            ThemeData(
                id = "theme_1",
                name = "Theme 1",
                colorScheme = ThemeData.COLOR_WARM_ORANGE,
                isDarkMode = false,
                isDefault = false,
                sourceApp = "test_app"
            ),
            ThemeData(
                id = "theme_2",
                name = "Theme 2",
                colorScheme = ThemeData.COLOR_CRIMSON,
                isDarkMode = false,
                isDefault = false,
                sourceApp = "test_app"
            )
        )

        repository.insertAll(themes)

        val allThemes = repository.getAllThemesSync()
        assertEquals(2, allThemes.size)
        assertTrue(allThemes.any { it.name == "Theme 1" })
        assertTrue(allThemes.any { it.name == "Theme 2" })
    }

    @Test
    fun `test get theme count`() = runTest {
        assertEquals(0, repository.getThemeCount())

        val theme = ThemeData(
            id = "test_theme",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(theme)
        assertEquals(1, repository.getThemeCount())
    }

    @Test
    fun `test initialize default themes when empty`() = runTest {
        assertEquals(0, repository.getThemeCount())

        repository.initializeDefaultThemes()

        val count = repository.getThemeCount()
        assertTrue(count > 0)

        val themes = repository.getAllThemesSync()
        assertTrue(themes.any { it.isDefault })
    }

    @Test
    fun `test initialize default themes when not empty`() = runTest {
        val existingTheme = ThemeData(
            id = "existing_theme",
            name = "Existing Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = false,
            sourceApp = "test_app"
        )

        repository.insertTheme(existingTheme)
        val initialCount = repository.getThemeCount()

        repository.initializeDefaultThemes()

        val finalCount = repository.getThemeCount()
        assertEquals(initialCount, finalCount) // Should not add more themes
    }

    @Test
    fun `test custom theme operations`() = runTest {
        // Test creating custom theme
        val customTheme = ThemeData.createCustomTheme(
            name = "My Custom Theme",
            isDarkMode = true,
            sourceApp = "test_app",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF
        )

        val insertedId = repository.createCustomTheme(customTheme)
        assertTrue(insertedId > 0)

        // Test retrieving custom themes
        val customThemes = repository.getCustomThemesSync()
        assertEquals(1, customThemes.size)
        assertEquals("My Custom Theme", customThemes[0].name)
        assertTrue(customThemes[0].isCustom)

        // Test updating custom theme
        val updatedTheme = customThemes[0].copy(name = "Updated Custom Theme", version = 2)
        repository.updateCustomTheme(updatedTheme)

        val retrievedUpdated = repository.getThemeById(updatedTheme.id)
        assertNotNull(retrievedUpdated)
        assertEquals("Updated Custom Theme", retrievedUpdated?.name)
        assertEquals(2, retrievedUpdated?.version)

        // Test deleting custom theme
        repository.deleteCustomTheme(updatedTheme.id)

        val deletedTheme = repository.getThemeById(updatedTheme.id)
        assertNull(deletedTheme)

        val customThemesAfterDelete = repository.getCustomThemesSync()
        assertEquals(0, customThemesAfterDelete.size)
    }

    @Test
    fun `test get default themes vs custom themes`() = runTest {
        // Insert some default themes
        val defaultThemes = ThemeData.getDefaultThemes("test_app")
        repository.insertAll(defaultThemes)

        // Insert a custom theme
        val customTheme = ThemeData.createCustomTheme(
            name = "Custom Theme",
            isDarkMode = false,
            sourceApp = "test_app"
        )
        repository.insertTheme(customTheme)

        // Test getting default themes
        val defaultThemesRetrieved = repository.getDefaultThemesSync()
        assertTrue(defaultThemesRetrieved.all { !it.isCustom })

        // Test getting custom themes
        val customThemesRetrieved = repository.getCustomThemesSync()
        assertEquals(1, customThemesRetrieved.size)
        assertTrue(customThemesRetrieved[0].isCustom)
    }
}