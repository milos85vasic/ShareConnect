package com.shareconnect

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redelf.commons.logging.Console
import com.shareconnect.database.Theme
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

class ThemeSelectionActivity : AppCompatActivity(), ThemeAdapter.OnThemeSelectListener {
    private var recyclerViewThemes: RecyclerView? = null
    private var themeAdapter: ThemeAdapter? = null
    private var themeRepository: com.shareconnect.database.ThemeRepository? = null
    private lateinit var themeSyncManager: ThemeSyncManager

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply current theme before setting content and calling super.onCreate()
        val themeManager = ThemeManager.getInstance(this)
        themeManager.applyTheme(this)

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_theme_selection)

        initViews()
        setupToolbar()
        setupRecyclerView()

        themeRepository = themeManager.themeRepositoryVal
        themeSyncManager = (application as SCApplication).themeSyncManager

        loadThemes()
        observeThemeChanges()
    }

    private fun observeThemeChanges() {
        lifecycleScope.launch {
            themeSyncManager.getAllThemes().collect { syncedThemes ->
                // Convert synced themes to legacy Theme objects for display
                val themes = syncedThemes.map { syncedTheme ->
                    Theme(
                        id = syncedTheme.id.hashCode(),
                        name = syncedTheme.name,
                        colorScheme = syncedTheme.colorScheme,
                        isDarkMode = syncedTheme.isDarkMode,
                        isDefault = syncedTheme.isDefault
                    )
                }
                themeAdapter?.updateThemes(themes)
            }
        }
    }

    private fun initViews() {
        recyclerViewThemes = findViewById(R.id.recyclerViewThemes)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupRecyclerView() {
        themeAdapter = ThemeAdapter(this)
        recyclerViewThemes!!.layoutManager = LinearLayoutManager(this)
        recyclerViewThemes!!.adapter = themeAdapter
    }

    private fun loadThemes() {
        lifecycleScope.launch {
            try {
                // Try to load from theme sync manager first
                val syncedThemes = themeSyncManager.getAllThemes().first()
                if (syncedThemes.isNotEmpty()) {
                    // Convert synced themes to legacy Theme objects for display
                    val themes = syncedThemes.map { syncedTheme ->
                        Theme(
                            id = syncedTheme.id.hashCode(),
                            name = syncedTheme.name,
                            colorScheme = syncedTheme.colorScheme,
                            isDarkMode = syncedTheme.isDarkMode,
                            isDefault = syncedTheme.isDefault
                        )
                    }
                    themeAdapter?.updateThemes(themes)
                } else {
                    // Fallback to legacy themes
                    val themes = themeRepository!!.allThemes
                    themeAdapter!!.updateThemes(themes)
                }
            } catch (e: Exception) {
                // Fallback to legacy themes if sync manager fails
                val themes = themeRepository!!.allThemes
                themeAdapter!!.updateThemes(themes)
            }
        }
    }

    override fun onThemeSelected(theme: Theme) {
        Console.debug(getString(R.string.log_on_theme_selected, theme.name, theme.id, theme.isDefault))

        // Set theme through ThemeSyncManager for cross-app sync
        lifecycleScope.launch {
            try {
                // Find the synced theme by matching properties
                val allSyncedThemes = themeSyncManager.getAllThemes().first()
                val selectedSyncedTheme = allSyncedThemes.find {
                    it.name == theme.name &&
                    it.colorScheme == theme.colorScheme &&
                    it.isDarkMode == theme.isDarkMode
                }

                if (selectedSyncedTheme != null) {
                    themeSyncManager.setDefaultTheme(selectedSyncedTheme.id)
                    Console.debug(getString(R.string.log_selected_theme, theme.name, theme.id))

                    // Also update legacy theme repository
                    themeRepository!!.setDefaultTheme(theme.id)
                } else {
                    // Fallback to legacy repository
                    themeRepository!!.setDefaultTheme(theme.id)
                }

                // Set result to indicate theme was changed
                setResult(RESULT_OK)

                // Finish the activity
                finish()
            } catch (e: Exception) {
                Console.error("Error selecting theme: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}