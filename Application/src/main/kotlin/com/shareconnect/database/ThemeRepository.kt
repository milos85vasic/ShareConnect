package com.shareconnect.database

import android.content.Context
import com.redelf.commons.logging.Console

class ThemeRepository(context: Context, testDao: ThemeDao? = null) {
    private val appContext = context.applicationContext
    private lateinit var database: HistoryDatabase
    private lateinit var themeDao: ThemeDao

    init {
        if (testDao != null) {
            // Use test DAO
            themeDao = testDao
        } else {
            // Initialize the database
            database = androidx.room.Room.databaseBuilder(
                appContext,
                HistoryDatabase::class.java, appContext.getString(R.string.db_history_database)
            )
                .allowMainThreadQueries()
                .fallbackToDestructiveMigration()
                .build()
            themeDao = database.themeDao()
        }
    }

    // Get all themes
    val allThemes: List<Theme>
        get() = themeDao.getAllThemes()

    // Get default theme
    val defaultTheme: Theme?
        get() {
            val theme = themeDao.getDefaultTheme()
            Console.debug(
                appContext.getString(R.string.log_get_default_theme, if (theme != null) theme.name + " (ID: " + theme.id + ", isDefault: " + theme.isDefault + ")" else "null")
            )
            return theme
        }

    // Get theme by color scheme and mode
    fun getThemeByColorSchemeAndMode(colorScheme: String, isDarkMode: Boolean): Theme? {
        return themeDao.getThemeByColorSchemeAndMode(colorScheme, isDarkMode)
    }

    // Insert a new theme
    fun insertTheme(theme: Theme) {
        themeDao.insert(theme)
    }

    // Update a theme
    fun updateTheme(theme: Theme) {
        themeDao.update(theme)
    }

    // Set default theme
    fun setDefaultTheme(themeId: Int) {
        Console.debug(appContext.getString(R.string.log_set_default_theme_called, themeId))
        themeDao.clearDefaultThemes()
        themeDao.setDefaultTheme(themeId)
        Console.debug(appContext.getString(R.string.log_set_default_theme_completed))

        // Verify the theme was set correctly
        val newDefaultTheme = defaultTheme
        if (newDefaultTheme != null) {
            Console.debug(
                "Verified new default theme: " + newDefaultTheme.name + " (ID: " + newDefaultTheme.id + ", isDefault: " + newDefaultTheme.isDefault + ")"
            )
        } else {
            Console.debug(appContext.getString(R.string.log_failed_verify_default_theme))
        }
    }

    // Initialize default themes if none exist
    fun initializeDefaultThemes() {
        Console.debug(appContext.getString(R.string.log_initialize_default_themes_called))
        if (allThemes.isEmpty()) {
            Console.debug(appContext.getString(R.string.log_no_existing_themes))
            // Warm Orange theme
            themeDao.insert(Theme(1, appContext.getString(R.string.theme_warm_orange_light), "warm_orange", false, true))
            themeDao.insert(Theme(2, appContext.getString(R.string.theme_warm_orange_dark), "warm_orange", true, false))

            // Crimson theme
            themeDao.insert(Theme(3, appContext.getString(R.string.theme_crimson_light), "crimson", false, false))
            themeDao.insert(Theme(4, appContext.getString(R.string.theme_crimson_dark), "crimson", true, false))

            // Light Blue theme
            themeDao.insert(Theme(5, appContext.getString(R.string.theme_light_blue_light), "light_blue", false, false))
            themeDao.insert(Theme(6, appContext.getString(R.string.theme_light_blue_dark), "light_blue", true, false))

            // Purple theme
            themeDao.insert(Theme(7, appContext.getString(R.string.theme_purple_light), "purple", false, false))
            themeDao.insert(Theme(8, appContext.getString(R.string.theme_purple_dark), "purple", true, false))

            // Green theme
            themeDao.insert(Theme(9, appContext.getString(R.string.theme_green_light), "green", false, false))
            themeDao.insert(Theme(10, appContext.getString(R.string.theme_green_dark), "green", true, false))

            // Default Material theme
            themeDao.insert(Theme(11, appContext.getString(R.string.theme_material_light), "material", false, false))
            themeDao.insert(Theme(12, appContext.getString(R.string.theme_material_dark), "material", true, false))
            Console.debug(appContext.getString(R.string.log_default_themes_created))
        } else {
            Console.debug(appContext.getString(R.string.log_themes_already_exist))
            val themes = allThemes
            for (theme in themes) {
                Console.debug(
                    appContext.getString(R.string.log_existing_theme, theme.name, theme.id, theme.isDefault)
                )
            }
        }
    }
}