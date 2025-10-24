package com.shareconnect.plexconnect.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    private lateinit var securityAccessManager: SecurityAccessManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Initialize SecurityAccess
        securityAccessManager = SecurityAccessManager.getInstance(this)

        // Check if onboarding is needed BEFORE setting up UI
        if (isOnboardingNeeded()) {
            // Launch onboarding activity
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Check security access before showing UI
        checkSecurityAndShowUI()
    }

    override fun onResume() {
        super.onResume()
        // Check security access when app resumes from background
        if (::securityAccessManager.isInitialized) {
            checkSecurityAndShowUI()
        }
    }

    private fun checkSecurityAndShowUI() {
        lifecycleScope.launch {
            val isAuthenticated = securityAccessManager.authenticate(
                activity = this@MainActivity,
                securityLevel = SecurityLevel.PIN,
                title = "PlexConnect Security",
                message = "Authenticate to access your Plex servers"
            )

            if (isAuthenticated) {
                setContent {
                    App()
                }
            } else {
                // Authentication failed, finish activity
                finish()
            }
        }
    }

    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("plex_onboarding_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }
}