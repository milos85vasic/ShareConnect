package com.shareconnect.themesync.models

import org.junit.Assert.*
import org.junit.Test

class ThemeDataTest {

    @Test
    fun `test theme data creation`() {
        val theme = ThemeData(
            id = "test_1",
            name = "Test Theme",
            colorScheme = ThemeData.COLOR_WARM_ORANGE,
            isDarkMode = false,
            isDefault = true,
            sourceApp = ThemeData.APP_SHARE_CONNECT
        )

        assertEquals("test_1", theme.id)
        assertEquals("Test Theme", theme.name)
        assertEquals(ThemeData.COLOR_WARM_ORANGE, theme.colorScheme)
        assertFalse(theme.isDarkMode)
        assertTrue(theme.isDefault)
        assertEquals(ThemeData.APP_SHARE_CONNECT, theme.sourceApp)
        assertEquals(1, theme.version)
    }

    @Test
    fun `test get default themes returns all color schemes`() {
        val themes = ThemeData.getDefaultThemes(ThemeData.APP_SHARE_CONNECT)

        assertEquals(12, themes.size) // 6 color schemes * 2 modes (light/dark)

        // Verify we have all color schemes
        val colorSchemes = themes.map { it.colorScheme }.toSet()
        assertTrue(colorSchemes.contains(ThemeData.COLOR_WARM_ORANGE))
        assertTrue(colorSchemes.contains(ThemeData.COLOR_CRIMSON))
        assertTrue(colorSchemes.contains(ThemeData.COLOR_LIGHT_BLUE))
        assertTrue(colorSchemes.contains(ThemeData.COLOR_PURPLE))
        assertTrue(colorSchemes.contains(ThemeData.COLOR_GREEN))
        assertTrue(colorSchemes.contains(ThemeData.COLOR_MATERIAL))
    }

    @Test
    fun `test default themes have one default theme`() {
        val themes = ThemeData.getDefaultThemes(ThemeData.APP_SHARE_CONNECT)
        val defaultThemes = themes.filter { it.isDefault }

        assertEquals(1, defaultThemes.size)
        assertEquals(ThemeData.COLOR_WARM_ORANGE, defaultThemes[0].colorScheme)
        assertFalse(defaultThemes[0].isDarkMode)
    }

    @Test
    fun `test each color scheme has light and dark variant`() {
        val themes = ThemeData.getDefaultThemes(ThemeData.APP_SHARE_CONNECT)
        val groupedByScheme = themes.groupBy { it.colorScheme }

        groupedByScheme.forEach { (scheme, themeList) ->
            assertEquals("Each scheme should have 2 variants", 2, themeList.size)
            assertTrue("Should have light variant", themeList.any { !it.isDarkMode })
            assertTrue("Should have dark variant", themeList.any { it.isDarkMode })
        }
    }

    @Test
    fun `test theme IDs are unique`() {
        val themes = ThemeData.getDefaultThemes(ThemeData.APP_SHARE_CONNECT)
        val ids = themes.map { it.id }.toSet()

        assertEquals(themes.size, ids.size)
    }

    @Test
    fun `test theme source app is correctly set`() {
        val themes = ThemeData.getDefaultThemes(ThemeData.APP_QBIT_CONNECT)

        themes.forEach { theme ->
            assertEquals(ThemeData.APP_QBIT_CONNECT, theme.sourceApp)
        }
    }
}
