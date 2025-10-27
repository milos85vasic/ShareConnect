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

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class ProfilesActivity : AppCompatActivity(), ProfileAdapter.OnProfileClickListener {
    private var recyclerViewProfiles: RecyclerView? = null
    private var profileAdapter: ProfileAdapter? = null
    private val profiles: MutableList<ServerProfile> = ArrayList()
    private var fabAddProfile: FloatingActionButton? = null
    private var textViewNoProfiles: TextView? = null
    private var profileManager: ProfileManager? = null
    private var themeManager: ThemeManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before setting content
        themeManager = ThemeManager.getInstance(this)
        themeManager!!.applyTheme(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profiles)

        profileManager = ProfileManager(this)

        initViews()
        setupRecyclerView()
        loadProfiles()

        // Handle window insets for proper positioning
        handleWindowInsets()
    }

    private fun handleWindowInsets() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            // For Android 11 and above, use WindowInsetsController
            window.setDecorFitsSystemWindows(false)
        } else {
            // For older versions, we can use fitsSystemWindows in the layout
            // which is already set in the XML
        }
    }

    private fun initViews() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        }

        recyclerViewProfiles = findViewById(R.id.recyclerViewProfiles)
        fabAddProfile = findViewById(R.id.fabAddProfile)
        textViewNoProfiles = findViewById(R.id.textViewNoProfiles)

        fabAddProfile!!.setOnClickListener {
            val intent = Intent(this@ProfilesActivity, EditProfileActivity::class.java)
            startActivity(intent)
        }
    }

    private fun setupRecyclerView() {
        profileAdapter = ProfileAdapter(profiles, this, profileManager!!)
        recyclerViewProfiles!!.layoutManager = LinearLayoutManager(this)
        recyclerViewProfiles!!.adapter = profileAdapter
    }

    private fun loadProfiles() {
        profiles.clear()
        profiles.addAll(profileManager!!.profiles)
        profileAdapter!!.notifyDataSetChanged()
        updateUI()
    }

    private fun updateUI() {
        if (profiles.isEmpty()) {
            recyclerViewProfiles!!.visibility = View.GONE
            textViewNoProfiles!!.visibility = View.VISIBLE
        } else {
            recyclerViewProfiles!!.visibility = View.VISIBLE
            textViewNoProfiles!!.visibility = View.GONE
        }
    }

    override fun onProfileClick(profile: ServerProfile) {
        val intent = Intent(this@ProfilesActivity, EditProfileActivity::class.java)
        intent.putExtra("profile_id", profile.id)
        startActivity(intent)
    }

    override fun onSetDefaultClick(profile: ServerProfile) {
        // Show confirmation dialog before setting default profile
        DialogUtils.showConfirmDialog(
            this,
            R.string.confirm_set_default,
            R.string.confirm_set_default_message,
            { dialog, _ ->
                profileManager!!.setDefaultProfile(profile)
                loadProfiles() // Refresh to show which is default
                dialog.dismiss()
            },
            null
        )
    }

    override fun onDeleteClick(profile: ServerProfile) {
        // Show confirmation dialog before deleting profile
        DialogUtils.showConfirmDialog(
            this,
            R.string.confirm_delete_profile,
            R.string.confirm_delete_profile_message,
            { dialog, _ ->
                profileManager!!.deleteProfile(profile)
                loadProfiles()
                dialog.dismiss()
            },
            null
        )
    }

    override fun onResume() {
        super.onResume()
        // Check if theme has changed and recreate activity if needed
        themeManager = ThemeManager.getInstance(this)
        if (themeManager!!.hasThemeChanged()) {
            themeManager!!.resetThemeChangedFlag()
            recreate()
        }
        // Refresh the list when returning from EditProfileActivity
        loadProfiles()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}