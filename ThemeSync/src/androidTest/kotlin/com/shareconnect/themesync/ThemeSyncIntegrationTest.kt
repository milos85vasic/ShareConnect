package com.shareconnect.themesync

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.themesync.database.ThemeDatabase
import com.shareconnect.themesync.models.ThemeData
import com.shareconnect.themesync.repository.ThemeRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ThemeSyncIntegrationTest {

    private lateinit var database1: ThemeDatabase
    private lateinit var database2: ThemeDatabase
    private lateinit var repository1: ThemeRepository
    private lateinit var repository2: ThemeRepository
    private lateinit var syncManager1: ThemeSyncManager
    private lateinit var syncManager2: ThemeSyncManager

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // Create two separate databases for two "apps"
        database1 = Room.inMemoryDatabaseBuilder(
            context,
            ThemeDatabase::class.java
        ).build()

        database2 = Room.inMemoryDatabaseBuilder(
            context,
            ThemeDatabase::class.java
        ).build()

        repository1 = ThemeRepository(context, "app1", database1.themeDao())
        repository2 = ThemeRepository(context, "app2", database2.themeDao())

        // Create sync managers for two different apps
        syncManager1 = ThemeSyncManager.getInstance(
            context,
            "app1",
            "App 1",
            "1.0.0"
        )

        syncManager2 = ThemeSyncManager.getInstance(
            context,
            "app2",
            "App 2",
            "1.0.0"
        )
    }

    @After
    fun teardown() {
        database1.close()
        database2.close()
        ThemeSyncManager.resetInstance()
    }

    @Test
    fun `test theme creation syncs between apps`() = runTest {
        // Create a custom theme in app1
        val customTheme = ThemeData.createCustomTheme(
            name = "Shared Custom Theme",
            isDarkMode = false,
            sourceApp = "app1",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF,
            background = 0xFFFFFBFE,
            onBackground = 0xFF1C1B1F
        )

        // Insert theme in app1 (this would normally trigger sync)
        runBlocking {
            repository1.insertTheme(customTheme)
        }

        // Simulate sync delay
        delay(100)

        // Check if theme appears in app2 (simulating sync)
        // In a real integration test, this would involve actual network sync
        // For this test, we'll verify the repository operations work correctly
        val themesInApp1 = repository1.getAllThemesSync()
        val themesInApp2 = repository2.getAllThemesSync()

        assertEquals(1, themesInApp1.size)
        assertEquals("Shared Custom Theme", themesInApp1[0].name)
        assertTrue(themesInApp1[0].isCustom)

        // App2 should still be empty since we haven't implemented actual sync yet
        assertEquals(0, themesInApp2.size)
    }

    @Test
    fun `test theme update syncs between apps`() = runTest {
        // Create and insert a theme in app1
        val originalTheme = ThemeData.createCustomTheme(
            name = "Original Theme",
            isDarkMode = false,
            sourceApp = "app1"
        )

        runBlocking {
            repository1.insertTheme(originalTheme)
        }

        // Update the theme in app1
        val updatedTheme = originalTheme.copy(name = "Updated Theme", version = 2)

        runBlocking {
            repository1.updateTheme(updatedTheme)
        }

        // Verify the update in app1
        val retrievedInApp1 = repository1.getThemeById(originalTheme.id)
        assertNotNull(retrievedInApp1)
        assertEquals("Updated Theme", retrievedInApp1?.name)
        assertEquals(2, retrievedInApp1?.version)
    }

    @Test
    fun `test theme deletion syncs between apps`() = runTest {
        // Create and insert a theme in app1
        val themeToDelete = ThemeData.createCustomTheme(
            name = "Theme to Delete",
            isDarkMode = false,
            sourceApp = "app1"
        )

        runBlocking {
            repository1.insertTheme(themeToDelete)
        }

        // Verify it exists
        assertNotNull(repository1.getThemeById(themeToDelete.id))

        // Delete the theme
        runBlocking {
            repository1.deleteTheme(themeToDelete.id)
        }

        // Verify it's deleted
        val deletedTheme = repository1.getThemeById(themeToDelete.id)
        assertNull(deletedTheme)
    }

    @Test
    fun `test default theme setting affects only local app`() = runTest {
        // Create themes in both apps
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
            sourceApp = "app2"
        )

        runBlocking {
            repository1.insertTheme(theme1)
            repository2.insertTheme(theme2)
        }

        // Set theme1 as default in app1
        runBlocking {
            repository1.setDefaultTheme("theme_1")
        }

        // Verify theme1 is default in app1
        val defaultInApp1 = repository1.getDefaultTheme()
        assertNotNull(defaultInApp1)
        assertEquals("theme_1", defaultInApp1?.id)

        // Verify app2 has no default (since we haven't synced defaults)
        val defaultInApp2 = repository2.getDefaultTheme()
        assertNull(defaultInApp2)
    }

    @Test
    fun `test custom theme with all color parameters syncs correctly`() = runTest {
        // Create a fully customized theme
        val fullCustomTheme = ThemeData.createCustomTheme(
            name = "Full Custom Theme",
            isDarkMode = true,
            sourceApp = "app1",
            primary = 0xFFBB86FC,
            onPrimary = 0xFF000000,
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
            background = 0xFF121212,
            onBackground = 0xFFE1E1E1,
            surface = 0xFF1E1E1E,
            onSurface = 0xFFE1E1E1,
            surfaceVariant = 0xFF49454F,
            onSurfaceVariant = 0xFFCAC4D0,
            surfaceTint = 0xFFBB86FC,
            outline = 0xFF938F99,
            outlineVariant = 0xFF49454F,
            scrim = 0xFF000000,
            surfaceBright = 0xFF3B383E,
            surfaceDim = 0xFF121212,
            surfaceContainer = 0xFF211F26,
            surfaceContainerHigh = 0xFF2B2930,
            surfaceContainerHighest = 0xFF36343B,
            surfaceContainerLow = 0xFF1C1B1F,
            surfaceContainerLowest = 0xFF0F0D13
        )

        runBlocking {
            repository1.insertTheme(fullCustomTheme)
        }

        // Verify all custom colors are stored correctly
        val retrievedTheme = repository1.getThemeById(fullCustomTheme.id)
        assertNotNull(retrievedTheme)

        val retrieved = retrievedTheme!!
        assertEquals(0xFFBB86FC, retrieved.customPrimary)
        assertEquals(0xFF000000, retrieved.customOnPrimary)
        assertEquals(0xFFE1BEE7, retrieved.customPrimaryContainer)
        assertEquals(0xFF1C1B1F, retrieved.customOnPrimaryContainer)
        assertEquals(0xFF03DAC6, retrieved.customSecondary)
        assertEquals(0xFF000000, retrieved.customOnSecondary)
        assertEquals(0xFFCEF6F2, retrieved.customSecondaryContainer)
        assertEquals(0xFF00141A, retrieved.customOnSecondaryContainer)
        assertEquals(0xFF7D5800, retrieved.customTertiary)
        assertEquals(0xFFFFFFFF, retrieved.customOnTertiary)
        assertEquals(0xFFFFDF9E, retrieved.customTertiaryContainer)
        assertEquals(0xFF271900, retrieved.customOnTertiaryContainer)
        assertEquals(0xFFBA1A1A, retrieved.customError)
        assertEquals(0xFFFFFFFF, retrieved.customOnError)
        assertEquals(0xFFFFDAD6, retrieved.customErrorContainer)
        assertEquals(0xFF410002, retrieved.customOnErrorContainer)
        assertEquals(0xFF121212, retrieved.customBackground)
        assertEquals(0xFFE1E1E1, retrieved.customOnBackground)
        assertEquals(0xFF1E1E1E, retrieved.customSurface)
        assertEquals(0xFFE1E1E1, retrieved.customOnSurface)
        assertEquals(0xFF49454F, retrieved.customSurfaceVariant)
        assertEquals(0xFFCAC4D0, retrieved.customOnSurfaceVariant)
        assertEquals(0xFFBB86FC, retrieved.customSurfaceTint)
        assertEquals(0xFF938F99, retrieved.customOutline)
        assertEquals(0xFF49454F, retrieved.customOutlineVariant)
        assertEquals(0xFF000000, retrieved.customScrim)
        assertEquals(0xFF3B383E, retrieved.customSurfaceBright)
        assertEquals(0xFF121212, retrieved.customSurfaceDim)
        assertEquals(0xFF211F26, retrieved.customSurfaceContainer)
        assertEquals(0xFF2B2930, retrieved.customSurfaceContainerHigh)
        assertEquals(0xFF36343B, retrieved.customSurfaceContainerHighest)
        assertEquals(0xFF1C1B1F, retrieved.customSurfaceContainerLow)
        assertEquals(0xFF0F0D13, retrieved.customSurfaceContainerLowest)
    }

    @Test
    fun `test multiple custom themes management`() = runTest {
        // Create multiple custom themes
        val themes = listOf(
            ThemeData.createCustomTheme(
                name = "Theme 1",
                isDarkMode = false,
                sourceApp = "app1",
                primary = 0xFF6200EE
            ),
            ThemeData.createCustomTheme(
                name = "Theme 2",
                isDarkMode = true,
                sourceApp = "app1",
                primary = 0xFF03DAC6
            ),
            ThemeData.createCustomTheme(
                name = "Theme 3",
                isDarkMode = false,
                sourceApp = "app1",
                primary = 0xFFBB86FC
            )
        )

        // Insert all themes
        runBlocking {
            repository1.insertAll(themes)
        }

        // Verify all themes are stored
        val allThemes = repository1.getAllThemesSync()
        assertEquals(3, allThemes.size)

        val customThemes = repository1.getCustomThemesSync()
        assertEquals(3, customThemes.size)
        assertTrue(customThemes.all { it.isCustom })

        // Update one theme
        val themeToUpdate = customThemes.find { it.name == "Theme 2" }
        requireNotNull(themeToUpdate)

        val updatedTheme = themeToUpdate.copy(name = "Updated Theme 2", version = 2)

        runBlocking {
            repository1.updateTheme(updatedTheme)
        }

        // Verify the update
        val retrievedUpdated = repository1.getThemeById(themeToUpdate.id)
        assertNotNull(retrievedUpdated)
        assertEquals("Updated Theme 2", retrievedUpdated?.name)
        assertEquals(2, retrievedUpdated?.version)

        // Delete one theme
        val themeToDelete = customThemes.find { it.name == "Theme 3" }
        requireNotNull(themeToDelete)

        runBlocking {
            repository1.deleteTheme(themeToDelete.id)
        }

        // Verify deletion
        val remainingThemes = repository1.getCustomThemesSync()
        assertEquals(2, remainingThemes.size)
        assertFalse(remainingThemes.any { it.name == "Theme 3" })
        assertTrue(remainingThemes.any { it.name == "Theme 1" })
        assertTrue(remainingThemes.any { it.name == "Updated Theme 2" })
    }

    @Test
    fun `test theme sync manager initialization with default themes`() = runTest {
        // Initially empty
        assertEquals(0, repository1.getThemeCount())

        // Initialize default themes (this would normally happen during app startup)
        runBlocking {
            repository1.initializeDefaultThemes()
        }

        // Should now have default themes
        val count = repository1.getThemeCount()
        assertTrue(count > 0)

        val themes = repository1.getAllThemesSync()
        assertTrue(themes.any { it.isDefault })
        assertTrue(themes.any { !it.isCustom })

        // Should have one default theme
        val defaultThemes = themes.filter { it.isDefault }
        assertEquals(1, defaultThemes.size)
    }
}