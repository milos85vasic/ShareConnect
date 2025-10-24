package com.shareconnect.plexconnect.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if onboarding is needed BEFORE setting up UI
        if (isOnboardingNeeded()) {
            // Launch onboarding activity
            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
            return
        }

        // Show UI
        setContent {
            App()
        }
    }

    private fun isOnboardingNeeded(): Boolean {
        val prefs = getSharedPreferences("plex_onboarding_prefs", MODE_PRIVATE)
        return !prefs.getBoolean("onboarding_completed", false)
    }
}