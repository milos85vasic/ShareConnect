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

    @Test
    fun `test custom theme creation with basic colors`() {
        val customTheme = ThemeData.createCustomTheme(
            name = "My Custom Theme",
            isDarkMode = true,
            sourceApp = "test_app",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF,
            background = 0xFF121212,
            onBackground = 0xFFFFFFFF
        )

        assertEquals("My Custom Theme", customTheme.name)
        assertTrue(customTheme.isDarkMode)
        assertTrue(customTheme.isCustom)
        assertEquals(ThemeData.COLOR_CUSTOM, customTheme.colorScheme)
        assertEquals("test_app", customTheme.sourceApp)
        assertEquals(0xFF6200EE, customTheme.customPrimary)
        assertEquals(0xFFFFFFFF, customTheme.customOnPrimary)
        assertEquals(0xFF121212, customTheme.customBackground)
        assertEquals(0xFFFFFFFF, customTheme.customOnBackground)
        assertFalse(customTheme.isDefault)
    }

    @Test
    fun `test custom theme creation with all color parameters`() {
        val customTheme = ThemeData.createCustomTheme(
            name = "Full Custom Theme",
            isDarkMode = false,
            sourceApp = "test_app",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF,
            primaryContainer = 0xFFE1BEE7,
            onPrimaryContainer = 0xFF1C1B1F,
            secondary = 0xFF03DAC6,
            onSecondary = 0xFF000000,
            secondaryContainer = 0xFFCEF6F2,
            onSecondaryContainer = 0xFF00141A,
            tertiary = 0xFF7D5800,
            onTertiary = 0xFFFFFFFF,
            tertiaryContainer = 0xFFFFDF9E,
            onTertiaryContainer = 0xFF271900,
            error = 0xFFBA1A1A,
            onError = 0xFFFFFFFF,
            errorContainer = 0xFFFFDAD6,
            onErrorContainer = 0xFF410002,
            background = 0xFFFFFBFE,
            onBackground = 0xFF1C1B1F,
            surface = 0xFFFFFBFE,
            onSurface = 0xFF1C1B1F,
            surfaceVariant = 0xFFE7E0EC,
            onSurfaceVariant = 0xFF49454F,
            surfaceTint = 0xFF6200EE,
            outline = 0xFF79747E,
            outlineVariant = 0xFFCAC4D0,
            scrim = 0xFF000000,
            surfaceBright = 0xFFFFFBFE,
            surfaceDim = 0xFFDED8E1,
            surfaceContainer = 0xFFF3EDF7,
            surfaceContainerHigh = 0xFFECE6F0,
            surfaceContainerHighest = 0xFFE6E0E9,
            surfaceContainerLow = 0xFFF7F2FA,
            surfaceContainerLowest = 0xFFFFFFFF
        )

        assertEquals("Full Custom Theme", customTheme.name)
        assertFalse(customTheme.isDarkMode)
        assertTrue(customTheme.isCustom)
        assertEquals(ThemeData.COLOR_CUSTOM, customTheme.colorScheme)
        assertEquals("test_app", customTheme.sourceApp)

        // Verify all custom colors are set correctly
        assertEquals(0xFF6200EE, customTheme.customPrimary)
        assertEquals(0xFFFFFFFF, customTheme.customOnPrimary)
        assertEquals(0xFFE1BEE7, customTheme.customPrimaryContainer)
        assertEquals(0xFF1C1B1F, customTheme.customOnPrimaryContainer)
        assertEquals(0xFF03DAC6, customTheme.customSecondary)
        assertEquals(0xFF000000, customTheme.customOnSecondary)
        assertEquals(0xFFCEF6F2, customTheme.customSecondaryContainer)
        assertEquals(0xFF00141A, customTheme.customOnSecondaryContainer)
        assertEquals(0xFF7D5800, customTheme.customTertiary)
        assertEquals(0xFFFFFFFF, customTheme.customOnTertiary)
        assertEquals(0xFFFFDF9E, customTheme.customTertiaryContainer)
        assertEquals(0xFF271900, customTheme.customOnTertiaryContainer)
        assertEquals(0xFFBA1A1A, customTheme.customError)
        assertEquals(0xFFFFFFFF, customTheme.customOnError)
        assertEquals(0xFFFFDAD6, customTheme.customErrorContainer)
        assertEquals(0xFF410002, customTheme.customOnErrorContainer)
        assertEquals(0xFFFFFBFE, customTheme.customBackground)
        assertEquals(0xFF1C1B1F, customTheme.customOnBackground)
        assertEquals(0xFFFFFBFE, customTheme.customSurface)
        assertEquals(0xFF1C1B1F, customTheme.customOnSurface)
        assertEquals(0xFFE7E0EC, customTheme.customSurfaceVariant)
        assertEquals(0xFF49454F, customTheme.customOnSurfaceVariant)
        assertEquals(0xFF6200EE, customTheme.customSurfaceTint)
        assertEquals(0xFF79747E, customTheme.customOutline)
        assertEquals(0xFFCAC4D0, customTheme.customOutlineVariant)
        assertEquals(0xFF000000, customTheme.customScrim)
        assertEquals(0xFFFFFBFE, customTheme.customSurfaceBright)
        assertEquals(0xFFDED8E1, customTheme.customSurfaceDim)
        assertEquals(0xFFF3EDF7, customTheme.customSurfaceContainer)
        assertEquals(0xFFECE6F0, customTheme.customSurfaceContainerHigh)
        assertEquals(0xFFE6E0E9, customTheme.customSurfaceContainerHighest)
        assertEquals(0xFFF7F2FA, customTheme.customSurfaceContainerLow)
        assertEquals(0xFFFFFFFF, customTheme.customSurfaceContainerLowest)
    }

    @Test
    fun `test custom theme ID generation is unique`() {
        val theme1 = ThemeData.createCustomTheme(
            name = "Test Theme",
            isDarkMode = false,
            sourceApp = "test_app"
        )

        val theme2 = ThemeData.createCustomTheme(
            name = "Test Theme",
            isDarkMode = false,
            sourceApp = "test_app"
        )

        // IDs should be different due to timestamp
        assertNotEquals(theme1.id, theme2.id)
        assertTrue(theme1.id.startsWith("custom_test_app_"))
        assertTrue(theme2.id.startsWith("custom_test_app_"))
    }

    @Test
    fun `test custom theme defaults to non-default`() {
        val customTheme = ThemeData.createCustomTheme(
            name = "Custom Theme",
            isDarkMode = false,
            sourceApp = "test_app"
        )

        assertFalse(customTheme.isDefault)
    }

    @Test
    fun `test default themes are not custom`() {
        val themes = ThemeData.getDefaultThemes("test_app")

        themes.forEach { theme ->
            assertFalse(theme.isCustom)
            assertNull(theme.customPrimary)
            assertNull(theme.customOnPrimary)
        }
    }

    @Test
    fun `test custom theme color scheme constant exists`() {
        assertEquals("custom", ThemeData.COLOR_CUSTOM)
    }
}
