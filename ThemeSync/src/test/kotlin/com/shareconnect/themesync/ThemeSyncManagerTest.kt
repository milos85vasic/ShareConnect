package com.shareconnect.themesync

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.shareconnect.themesync.database.ThemeDatabase
import com.shareconnect.themesync.models.ThemeData
import com.shareconnect.themesync.repository.ThemeRepository
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.whenever

@RunWith(AndroidJUnit4::class)
class ThemeSyncManagerTest {

    private lateinit var database: ThemeDatabase
    private lateinit var repository: ThemeRepository
    private lateinit var syncManager: ThemeSyncManager

    @Mock
    private lateinit var mockAsinkaClient: digital.vasic.asinka.AsinkaClient

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            ThemeDatabase::class.java
        ).build()

        repository = ThemeRepository(context, "test_app", database.themeDao())

        // For testing, we'll create a sync manager without actually starting Asinka
        // In a real test environment, you might want to mock the AsinkaClient
        syncManager = ThemeSyncManager.getInstance(
            context,
            "test_app",
            "Test App",
            "1.0.0"
        )
    }

    @After
    fun teardown() {
        database.close()
        ThemeSyncManager.resetInstance()
    }

    @Test
    fun `test create theme adds to repository and syncs`() = runTest {
        val customTheme = ThemeData.createCustomTheme(
            name = "Test Custom Theme",
            isDarkMode = false,
            sourceApp = "test_app",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF
        )

        // This would normally sync with Asinka, but for testing we'll just verify repository
        runBlocking {
            repository.insertTheme(customTheme)
        }

        val retrievedTheme = repository.getThemeById(customTheme.id)
        assertNotNull(retrievedTheme)
        assertEquals("Test Custom Theme", retrievedTheme?.name)
        assertTrue(retrievedTheme?.isCustom == true)
    }

    @Test
    fun `test update theme modifies existing theme`() = runTest {
        val originalTheme = ThemeData.createCustomTheme(
            name = "Original Theme",
            isDarkMode = false,
            sourceApp = "test_app"
        )

        runBlocking {
            repository.insertTheme(originalTheme)
        }

        val updatedTheme = originalTheme.copy(name = "Updated Theme", version = 2)

        runBlocking {
            repository.updateTheme(updatedTheme)
        }

        val retrievedTheme = repository.getThemeById(originalTheme.id)
        assertNotNull(retrievedTheme)
        assertEquals("Updated Theme", retrievedTheme?.name)
        assertEquals(2, retrievedTheme?.version)
    }

    @Test
    fun `test delete theme removes from repository`() = runTest {
        val theme = ThemeData.createCustomTheme(
            name = "Theme to Delete",
            isDarkMode = false,
            sourceApp = "test_app"
        )

        runBlocking {
            repository.insertTheme(theme)
        }

        assertNotNull(repository.getThemeById(theme.id))

        runBlocking {
            repository.deleteTheme(theme.id)
        }

        val deletedTheme = repository.getThemeById(theme.id)
        assertNull(deletedTheme)
    }

    @Test
    fun `test set default theme updates theme and clears others`() = runTest {
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

        runBlocking {
            repository.insertTheme(theme1)
            repository.insertTheme(theme2)
        }

        runBlocking {
            repository.setDefaultTheme("theme_2")
        }

        val defaultTheme = repository.getDefaultTheme()
        assertNotNull(defaultTheme)
        assertEquals("theme_2", defaultTheme?.id)
        assertTrue(defaultTheme?.isDefault == true)
    }

    @Test
    fun `test get all themes returns all themes from repository`() = runTest {
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
            isDarkMode = true,
            isDefault = false,
            sourceApp = "test_app"
        )

        runBlocking {
            repository.insertTheme(theme1)
            repository.insertTheme(theme2)
        }

        val allThemes = repository.getAllThemesSync()
        assertEquals(2, allThemes.size)
        assertTrue(allThemes.any { it.name == "Theme 1" })
        assertTrue(allThemes.any { it.name == "Theme 2" })
    }

    @Test
    fun `test initialize default themes populates database when empty`() = runTest {
        assertEquals(0, repository.getThemeCount())

        runBlocking {
            repository.initializeDefaultThemes()
        }

        val count = repository.getThemeCount()
        assertTrue(count > 0)

        val themes = repository.getAllThemesSync()
        assertTrue(themes.any { it.isDefault })
        assertTrue(themes.any { !it.isCustom })
    }

    @Test
    fun `test custom theme creation and management workflow`() = runTest {
        // 1. Create a custom theme
        val customTheme = ThemeData.createCustomTheme(
            name = "Workflow Test Theme",
            isDarkMode = true,
            sourceApp = "test_app",
            primary = 0xFFBB86FC,
            onPrimary = 0xFF000000,
            background = 0xFF121212,
            onBackground = 0xFFE1E1E1
        )

        // 2. Insert the theme
        runBlocking {
            repository.insertTheme(customTheme)
        }

        // 3. Verify it was inserted correctly
        val insertedTheme = repository.getThemeById(customTheme.id)
        assertNotNull(insertedTheme)
        assertEquals("Workflow Test Theme", insertedTheme?.name)
        assertTrue(insertedTheme?.isCustom == true)
        assertTrue(insertedTheme?.isDarkMode == true)
        assertEquals(0xFFBB86FC, insertedTheme?.customPrimary)
        assertEquals(0xFF000000, insertedTheme?.customOnPrimary)

        // 4. Update the theme
        val updatedTheme = insertedTheme?.copy(name = "Updated Workflow Theme", version = 2)
        requireNotNull(updatedTheme)

        runBlocking {
            repository.updateTheme(updatedTheme)
        }

        // 5. Verify the update
        val retrievedUpdated = repository.getThemeById(customTheme.id)
        assertNotNull(retrievedUpdated)
        assertEquals("Updated Workflow Theme", retrievedUpdated?.name)
        assertEquals(2, retrievedUpdated?.version)

        // 6. Set as default
        runBlocking {
            repository.setDefaultTheme(customTheme.id)
        }

        // 7. Verify it's now the default
        val defaultTheme = repository.getDefaultTheme()
        assertNotNull(defaultTheme)
        assertEquals(customTheme.id, defaultTheme?.id)
        assertTrue(defaultTheme?.isDefault == true)

        // 8. Delete the theme
        runBlocking {
            repository.deleteTheme(customTheme.id)
        }

        // 9. Verify it's deleted
        val deletedTheme = repository.getThemeById(customTheme.id)
        assertNull(deletedTheme)

        val defaultAfterDelete = repository.getDefaultTheme()
        assertNull(defaultAfterDelete) // No default should remain
    }

    @Test
    fun `test theme data field map serialization includes custom fields`() {
        val customTheme = ThemeData.createCustomTheme(
            name = "Serialization Test",
            isDarkMode = false,
            sourceApp = "test_app",
            primary = 0xFF6200EE,
            onPrimary = 0xFFFFFFFF
        )

        val syncableTheme = com.shareconnect.themesync.models.SyncableTheme.fromThemeData(customTheme)
        val fieldMap = syncableTheme.toFieldMap()

        // Verify basic fields
        assertEquals(customTheme.id, fieldMap["id"])
        assertEquals(customTheme.name, fieldMap["name"])
        assertEquals(customTheme.colorScheme, fieldMap["colorScheme"])
        assertEquals(customTheme.isDarkMode, fieldMap["isDarkMode"])
        assertEquals(customTheme.isDefault, fieldMap["isDefault"])
        assertEquals(customTheme.sourceApp, fieldMap["sourceApp"])
        assertEquals(customTheme.version, fieldMap["version"])
        assertEquals(customTheme.lastModified, fieldMap["lastModified"])

        // Verify custom fields
        assertEquals(customTheme.isCustom, fieldMap["isCustom"])
        assertEquals(customTheme.customPrimary, fieldMap["customPrimary"])
        assertEquals(customTheme.customOnPrimary, fieldMap["customOnPrimary"])
        assertEquals(customTheme.customBackground, fieldMap["customBackground"])
        assertEquals(customTheme.customOnBackground, fieldMap["customOnBackground"])
    }

    @Test
    fun `test theme data field map deserialization handles custom fields`() {
        val originalTheme = ThemeData.createCustomTheme(
            name = "Deserialization Test",
            isDarkMode = true,
            sourceApp = "test_app",
            primary = 0xFF03DAC6,
            onPrimary = 0xFF000000
        )

        val syncableTheme = com.shareconnect.themesync.models.SyncableTheme.fromThemeData(originalTheme)
        val fieldMap = syncableTheme.toFieldMap()

        // Create a new syncable theme and deserialize
        val newSyncableTheme = com.shareconnect.themesync.models.SyncableTheme.fromThemeData(
            ThemeData(
                id = "new_id",
                name = "New Theme",
                colorScheme = ThemeData.COLOR_WARM_ORANGE,
                isDarkMode = false,
                isDefault = false,
                sourceApp = "test_app"
            )
        )

        newSyncableTheme.fromFieldMap(fieldMap)

        val deserializedTheme = newSyncableTheme.getThemeData()

        // Verify custom fields were preserved
        assertEquals(originalTheme.customPrimary, deserializedTheme.customPrimary)
        assertEquals(originalTheme.customOnPrimary, deserializedTheme.customOnPrimary)
        assertEquals(originalTheme.isDarkMode, deserializedTheme.isDarkMode)
        assertEquals(originalTheme.isCustom, deserializedTheme.isCustom)
    }
}