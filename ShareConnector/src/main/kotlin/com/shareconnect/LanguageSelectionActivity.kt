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
