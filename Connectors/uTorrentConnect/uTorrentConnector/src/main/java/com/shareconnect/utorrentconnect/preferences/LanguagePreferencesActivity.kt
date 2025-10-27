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


package com.shareconnect.utorrentconnect.preferences

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.shareconnect.utorrentconnect.R
import com.shareconnect.utorrentconnect.uTorrentRemote
import com.shareconnect.languagesync.LanguageSyncManager
import com.shareconnect.languagesync.models.LanguageData
import kotlinx.coroutines.launch

class LanguagePreferencesActivity : AppCompatActivity(), LanguageAdapter.OnLanguageSelectListener {
    private lateinit var recyclerView: RecyclerView
    private lateinit var languageAdapter: LanguageAdapter
    private lateinit var languageSyncManager: LanguageSyncManager
    private var currentLanguageCode: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language_preferences)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Language Selection"

        recyclerView = findViewById(R.id.recyclerViewLanguages)
        recyclerView.layoutManager = LinearLayoutManager(this)

        languageSyncManager = (application as uTorrentRemote).languageSyncManager

        languageAdapter = LanguageAdapter(this)
        recyclerView.adapter = languageAdapter

        loadLanguages()
        observeLanguageChanges()
    }

    private fun loadLanguages() {
        lifecycleScope.launch {
            val currentLanguage = languageSyncManager.getOrCreateDefault()
            currentLanguageCode = currentLanguage.languageCode

            val availableLanguages = LanguageData.getAvailableLanguages()
            languageAdapter.updateLanguages(availableLanguages, currentLanguageCode!!)
        }
    }

    private fun observeLanguageChanges() {
        lifecycleScope.launch {
            languageSyncManager.languageChangeFlow.collect { languageData ->
                currentLanguageCode = languageData.languageCode
                languageAdapter.setSelectedLanguage(currentLanguageCode!!)
            }
        }
    }

    override fun onLanguageSelected(languageCode: String, displayName: String) {
        lifecycleScope.launch {
            try {
                languageSyncManager.setLanguagePreference(languageCode, displayName)
                setResult(RESULT_OK)
                finish()
            } catch (e: Exception) {
                e.printStackTrace()
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
}
