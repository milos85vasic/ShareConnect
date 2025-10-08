package com.shareconnect

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.redelf.commons.logging.Console
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.models.LanguageData
import com.shareconnect.languagesync.utils.LocaleHelper
import kotlinx.coroutines.launch

class LanguageSelectionActivity : AppCompatActivity(), LanguageAdapter.OnLanguageSelectListener {
    private var recyclerViewLanguages: RecyclerView? = null
    private var languageAdapter: LanguageAdapter? = null
    private lateinit var languageSyncManager: LanguageSyncManager
    private var currentLanguageCode: String? = null

    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(LocaleHelper.onAttach(newBase))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply current theme before setting content and calling super.onCreate()
        val themeManager = ThemeManager.getInstance(this)
        themeManager.applyTheme(this)

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display
        window.statusBarColor = android.graphics.Color.TRANSPARENT
        window.navigationBarColor = android.graphics.Color.TRANSPARENT

        setContentView(R.layout.activity_language_selection)

        initViews()
        setupToolbar()
        setupRecyclerView()

        languageSyncManager = (application as SCApplication).languageSyncManager

        loadLanguages()
        observeLanguageChanges()
    }

    private fun observeLanguageChanges() {
        lifecycleScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                currentLanguageCode = languageData.languageCode
                languageAdapter?.setSelectedLanguage(currentLanguageCode ?: LanguageData.CODE_SYSTEM_DEFAULT)
            }
        }
    }

    private fun initViews() {
        recyclerViewLanguages = findViewById(R.id.recyclerViewLanguages)
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.title = getString(R.string.language_selection)
        }
    }

    private fun setupRecyclerView() {
        languageAdapter = LanguageAdapter(this)
        recyclerViewLanguages!!.layoutManager = LinearLayoutManager(this)
        recyclerViewLanguages!!.adapter = languageAdapter
    }

    private fun loadLanguages() {
        lifecycleScope.launch {
            val currentLanguage = languageSyncManager.getOrCreateDefault()
            currentLanguageCode = currentLanguage.languageCode

            val availableLanguages = LanguageData.getAvailableLanguages()
            languageAdapter!!.updateLanguages(availableLanguages, currentLanguageCode!!)
        }
    }

    override fun onLanguageSelected(languageCode: String, displayName: String) {
        Console.debug("Language selected: $displayName ($languageCode)")

        lifecycleScope.launch {
            try {
                languageSyncManager.setLanguagePreference(languageCode, displayName)
                Console.debug("Language preference set: $languageCode")

                // Set result to indicate language was changed
                setResult(RESULT_OK)

                // Finish the activity
                finish()
            } catch (e: Exception) {
                Console.error("Error selecting language: ${e.message}")
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
