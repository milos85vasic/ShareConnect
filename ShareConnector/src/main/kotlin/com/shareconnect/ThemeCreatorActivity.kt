package com.shareconnect

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.redelf.commons.logging.Console
import com.shareconnect.themesync.ThemeSyncManager
import com.shareconnect.themesync.models.ThemeData
import kotlinx.coroutines.launch

class ThemeCreatorActivity : AppCompatActivity() {

    private lateinit var themeSyncManager: ThemeSyncManager
    private var editingThemeId: String? = null
    private var isDarkMode = false

    // UI Elements
    private lateinit var editTextThemeName: TextInputEditText
    private lateinit var toggleGroupThemeMode: MaterialButtonToggleGroup
    private lateinit var buttonLightMode: MaterialButton
    private lateinit var buttonDarkMode: MaterialButton
    private lateinit var editTextPrimary: TextInputEditText
    private lateinit var editTextOnPrimary: TextInputEditText
    private lateinit var editTextSecondary: TextInputEditText
    private lateinit var editTextOnSecondary: TextInputEditText
    private lateinit var editTextBackground: TextInputEditText
    private lateinit var editTextOnBackground: TextInputEditText
    private lateinit var editTextSurface: TextInputEditText
    private lateinit var editTextOnSurface: TextInputEditText
    private lateinit var buttonSave: MaterialButton
    private lateinit var buttonDelete: MaterialButton
    private lateinit var cardPreview: MaterialCardView
    private lateinit var textPreviewTitle: android.widget.TextView
    private lateinit var textPreviewSubtitle: android.widget.TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply current theme before setting content and calling super.onCreate()
        val themeManager = ThemeManager.getInstance(this)
        themeManager.applyTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme_creator)

        // Enable edge-to-edge display
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        initViews()
        setupToolbar()
        setupTextWatchers()

        themeSyncManager = (application as SCApplication).themeSyncManager

        // Check if we're editing an existing theme
        editingThemeId = intent.getStringExtra(EXTRA_THEME_ID)
        if (editingThemeId != null) {
            loadThemeForEditing(editingThemeId!!)
            buttonDelete.visibility = android.view.View.VISIBLE
        } else {
            buttonDelete.visibility = android.view.View.GONE
            // Set default values for new theme
            setDefaultColorValues()
        }

        updatePreview()
    }

    private fun initViews() {
        editTextThemeName = findViewById(R.id.editTextThemeName)
        toggleGroupThemeMode = findViewById(R.id.toggleGroupThemeMode)
        buttonLightMode = findViewById(R.id.buttonLightMode)
        buttonDarkMode = findViewById(R.id.buttonDarkMode)
        editTextPrimary = findViewById(R.id.editTextPrimary)
        editTextOnPrimary = findViewById(R.id.editTextOnPrimary)
        editTextSecondary = findViewById(R.id.editTextSecondary)
        editTextOnSecondary = findViewById(R.id.editTextOnSecondary)
        editTextBackground = findViewById(R.id.editTextBackground)
        editTextOnBackground = findViewById(R.id.editTextOnBackground)
        editTextSurface = findViewById(R.id.editTextSurface)
        editTextOnSurface = findViewById(R.id.editTextOnSurface)
        buttonSave = findViewById(R.id.buttonSave)
        buttonDelete = findViewById(R.id.buttonDelete)
        cardPreview = findViewById(R.id.cardPreview)
        textPreviewTitle = findViewById(R.id.textPreviewTitle)
        textPreviewSubtitle = findViewById(R.id.textPreviewSubtitle)

        buttonSave.setOnClickListener { saveTheme() }
        buttonDelete.setOnClickListener { deleteTheme() }

        toggleGroupThemeMode.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                isDarkMode = checkedId == R.id.buttonDarkMode
                updatePreview()
            }
        }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                updatePreview()
            }
        }

        editTextThemeName.addTextChangedListener(textWatcher)
        editTextPrimary.addTextChangedListener(textWatcher)
        editTextOnPrimary.addTextChangedListener(textWatcher)
        editTextSecondary.addTextChangedListener(textWatcher)
        editTextOnSecondary.addTextChangedListener(textWatcher)
        editTextBackground.addTextChangedListener(textWatcher)
        editTextOnBackground.addTextChangedListener(textWatcher)
        editTextSurface.addTextChangedListener(textWatcher)
        editTextOnSurface.addTextChangedListener(textWatcher)
    }

    private fun setDefaultColorValues() {
        // Light mode defaults
        editTextPrimary.setText("#FF6B35")
        editTextOnPrimary.setText("#FFFFFF")
        editTextSecondary.setText("#FF8A65")
        editTextOnSecondary.setText("#FFFFFF")
        editTextBackground.setText("#FFFBFE")
        editTextOnBackground.setText("#1C1B1F")
        editTextSurface.setText("#FFFBFE")
        editTextOnSurface.setText("#1C1B1F")
    }

    private fun loadThemeForEditing(themeId: String) {
        lifecycleScope.launch {
            try {
                val theme = themeSyncManager.getThemeById(themeId)
                if (theme != null) {
                    editTextThemeName.setText(theme.name)
                    isDarkMode = theme.isDarkMode
                    toggleGroupThemeMode.check(if (isDarkMode) R.id.buttonDarkMode else R.id.buttonLightMode)

                    // Load custom colors if available
                    theme.customPrimary?.let { editTextPrimary.setText(colorToHex(it)) }
                    theme.customOnPrimary?.let { editTextOnPrimary.setText(colorToHex(it)) }
                    theme.customSecondary?.let { editTextSecondary.setText(colorToHex(it)) }
                    theme.customOnSecondary?.let { editTextOnSecondary.setText(colorToHex(it)) }
                    theme.customBackground?.let { editTextBackground.setText(colorToHex(it)) }
                    theme.customOnBackground?.let { editTextOnBackground.setText(colorToHex(it)) }
                    theme.customSurface?.let { editTextSurface.setText(colorToHex(it)) }
                    theme.customOnSurface?.let { editTextOnSurface.setText(colorToHex(it)) }
                }
            } catch (e: Exception) {
                Console.error("Error loading theme for editing: ${e.message}")
                Toast.makeText(this@ThemeCreatorActivity, "Error loading theme", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun updatePreview() {
        try {
            val primaryColor = parseColorSafely(editTextPrimary.text.toString())
            val onPrimaryColor = parseColorSafely(editTextOnPrimary.text.toString())
            val backgroundColor = parseColorSafely(editTextBackground.text.toString())
            val onBackgroundColor = parseColorSafely(editTextOnBackground.text.toString())

            cardPreview.setCardBackgroundColor(backgroundColor)
            textPreviewTitle.setTextColor(onBackgroundColor)
            textPreviewSubtitle.setTextColor(onBackgroundColor)

            // Update theme name in preview if provided
            val themeName = editTextThemeName.text.toString().takeIf { it.isNotEmpty() } ?: "Theme Preview"
            textPreviewTitle.text = themeName
        } catch (e: Exception) {
            Console.debug("Error updating preview: ${e.message}")
        }
    }

    private fun parseColorSafely(colorString: String): Int {
        return try {
            if (colorString.startsWith("#")) {
                Color.parseColor(colorString)
            } else {
                Color.parseColor("#$colorString")
            }
        } catch (e: Exception) {
            Color.BLACK // Fallback color
        }
    }

    private fun colorToHex(color: Long): String {
        return String.format("#%06X", (0xFFFFFF and color.toInt()))
    }

    private fun saveTheme() {
        val themeName = editTextThemeName.text.toString().trim()

        if (themeName.isEmpty()) {
            Toast.makeText(this, "Please enter a theme name", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val primaryColor = parseColorSafely(editTextPrimary.text.toString())
            val onPrimaryColor = parseColorSafely(editTextOnPrimary.text.toString())
            val secondaryColor = parseColorSafely(editTextSecondary.text.toString())
            val onSecondaryColor = parseColorSafely(editTextOnSecondary.text.toString())
            val backgroundColor = parseColorSafely(editTextBackground.text.toString())
            val onBackgroundColor = parseColorSafely(editTextOnBackground.text.toString())
            val surfaceColor = parseColorSafely(editTextSurface.text.toString())
            val onSurfaceColor = parseColorSafely(editTextOnSurface.text.toString())

            lifecycleScope.launch {
                try {
                    val themeData = ThemeData.createCustomTheme(
                        name = themeName,
                        isDarkMode = isDarkMode,
                        sourceApp = applicationContext.packageName,
                        primary = primaryColor.toLong(),
                        onPrimary = onPrimaryColor.toLong(),
                        secondary = secondaryColor.toLong(),
                        onSecondary = onSecondaryColor.toLong(),
                        background = backgroundColor.toLong(),
                        onBackground = onBackgroundColor.toLong(),
                        surface = surfaceColor.toLong(),
                        onSurface = onSurfaceColor.toLong()
                    )

                    if (editingThemeId != null) {
                        // Update existing theme
                        val updatedTheme = themeData.copy(id = editingThemeId!!, version = themeData.version + 1)
                        themeSyncManager.updateTheme(updatedTheme)
                        Toast.makeText(this@ThemeCreatorActivity, "Theme updated successfully", Toast.LENGTH_SHORT).show()
                    } else {
                        // Create new theme
                        themeSyncManager.createTheme(themeData)
                        Toast.makeText(this@ThemeCreatorActivity, "Theme created successfully", Toast.LENGTH_SHORT).show()
                    }

                    setResult(RESULT_OK)
                    finish()
                } catch (e: Exception) {
                    Console.error("Error saving theme: ${e.message}")
                    Toast.makeText(this@ThemeCreatorActivity, "Error saving theme", Toast.LENGTH_SHORT).show()
                }
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Please check your color values", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteTheme() {
        if (editingThemeId == null) return

        lifecycleScope.launch {
            try {
                themeSyncManager.deleteTheme(editingThemeId!!)
                Toast.makeText(this@ThemeCreatorActivity, "Theme deleted successfully", Toast.LENGTH_SHORT).show()
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                Console.error("Error deleting theme: ${e.message}")
                Toast.makeText(this@ThemeCreatorActivity, "Error deleting theme", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        const val EXTRA_THEME_ID = "extra_theme_id"
        const val RESULT_THEME_CREATED = 1001
        const val RESULT_THEME_UPDATED = 1002
        const val RESULT_THEME_DELETED = 1003
    }
}